# Documento de Diseño - Proyecto de Análisis de Algoritmos

## 1. Introducción

Este proyecto implementa un sistema de análisis de algoritmos de ordenamiento utilizando datos financieros reales. El objetivo es comparar el rendimiento de 12 algoritmos de ordenamiento mediante la medición de tiempo de ejecución y verificar la complejidad algorítmica teórica.

## 2. Arquitectura del Sistema

### 2.1 Estructura Monolítica

El proyecto sigue una arquitectura monolítica simple:

```
src/main/java/com/analisis/
├── Principal.java              # Punto de entrada
├── modelo/
│   ├── DatoFinanciero.java    # Entidad de datos OHLCV
│   ├── ResultadoOrdenamiento.java
│   └── ResultadoVolumen.java
├── servicio/
│   ├── ObtenerDatos.java      # Extracción de datos
│   ├── LimpiarDatos.java      # Transformación
│   ├── AnalizadorVolumen.java # Análisis de volumen
│   └── GeneradorGrafica.java # Visualización
└── algoritmo/
    ├── InterfazOrdenamiento.java
    ├── TimSort.java
    ├── CombSort.java
    ├── SelectionSort.java
    ├── TreeSort.java
    ├── PigeonholeSort.java
    ├── BucketSort.java
    ├── QuickSort.java
    ├── HeapSort.java
    ├── BitonicSort.java
    ├── GnomeSort.java
    ├── BinaryInsertionSort.java
    └── RadixSort.java
```

### 2.2 Flujo de Ejecución

```
ETL (Obtener → Limpiar → Unificar)
         ↓
Ordenamiento (12 algoritmos)
         ↓
Análisis de Volumen
         ↓
Generación de Gráficos y CSV
```

### 2.3 Diseño Arquitectónico del Proceso ETL

El proceso ETL es automatizado y se ejecuta secuencialmente al iniciar el programa. Está compuesto por tres etapas bien definidas:

```
┌─────────────────────────────────────────────────────────────────┐
│                     PROCESO ETL AUTOMATIZADO                    │
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────────┐  │
│  │   EXTRACT    │───▶│  TRANSFORM   │───▶│      LOAD        │  │
│  │ ObtenerDatos │    │ LimpiarDatos │    │ guardarDatos()   │  │
│  └──────────────┘    └──────────────┘    └──────────────────┘  │
│         │                   │                    │              │
│  API Twelve Data      Deduplicación        datos_unificados     │
│  20 símbolos          IQR Outliers              .csv            │
│  5 años historial     Interpolación                             │
└─────────────────────────────────────────────────────────────────┘
```

#### Etapa 1 — Extract (`ObtenerDatos`)

**Fuente**: API REST de Twelve Data (`https://api.twelvedata.com/time_series`)

**Proceso**:
1. Itera sobre los 20 símbolos definidos en `SIMBOLOS[]`
2. Por cada símbolo construye la URL con parámetros: `interval=1day`, `start_date`, `end_date`, `outputsize=5000`
3. Realiza una petición HTTP GET con timeout de 30 segundos
4. Parsea manualmente el JSON de respuesta extrayendo el array `values[]`
5. Mapea cada entrada a un objeto `DatoFinanciero` (fecha, símbolo, open, high, low, close, volume)
6. Aplica un delay de 8 segundos entre peticiones para respetar el rate limit de la API

**Control de errores**:
- Verifica código HTTP 200 antes de procesar
- Detecta respuestas de error de la API (`status: error`, código 429)
- Omite registros con `close <= 0`

**Salida**: `List<DatoFinanciero>` con todos los registros crudos

#### Etapa 2 — Transform (`LimpiarDatos`)

**Proceso** (aplicado en orden):

1. **Deduplicación**: usa un `HashSet<String>` con clave compuesta `fecha-símbolo` para eliminar registros duplicados en O(n)
2. **Detección de outliers**: agrupa datos por símbolo, calcula Q1, Q3 e IQR sobre los precios de cierre ordenados, marca como outlier todo valor fuera del rango `[Q1 - 1.5·IQR, Q3 + 1.5·IQR]`
3. **Interpolación lineal**: para registros con `close == 0`, reemplaza el valor con el promedio entre el registro anterior y el siguiente del mismo símbolo

**Salida**: `List<DatoFinanciero>` limpia y lista para análisis

#### Etapa 3 — Load (`Principal.guardarDatos()`)

**Proceso**:
1. Recibe la lista limpia de `DatoFinanciero`
2. Escribe el encabezado CSV usando `toCsvHeader()`
3. Serializa cada registro con `toString()` en formato CSV
4. Persiste en `datos_unificados.csv` en el directorio de ejecución

**Salida**: archivo `datos_unificados.csv` con todos los registros procesados

#### Diagrama de secuencia ETL

```
Principal        ObtenerDatos         API Twelve Data      LimpiarDatos       FileWriter
    │                  │                      │                  │                 │
    │─obtenerTodos()──▶│                      │                  │                 │
    │                  │──GET /time_series───▶│                  │                 │
    │                  │◀──JSON response──────│                  │                 │
    │                  │  (parseo manual)     │                  │                 │
    │                  │  [repite x20 símbolos + delay 8s]       │                 │
    │◀─List<Dato>──────│                      │                  │                 │
    │                  │                      │                  │                 │
    │─limpiarDatos()──────────────────────────────────────────▶ │                 │
    │                  │                      │   deduplicar     │                 │
    │                  │                      │   outliers IQR   │                 │
    │                  │                      │   interpolar     │                 │
    │◀─List<Dato> limpia──────────────────────────────────────── │                 │
    │                  │                      │                  │                 │
    │─guardarDatos()──────────────────────────────────────────────────────────────▶│
    │                  │                      │                  │  datos_unificados.csv
    │◀────────────────────────────────────────────────────────────────────────────│
```

## 3. Módulos y Funcionalidades

### 3.1 ObtenerDatos

**Responsabilidad**: Extraer datos financieros desde Alpha Vantage API

**Complejidad**: O(n * d) donde n = número de activos, d = días de historial

**Decisiones técnicas**:
- Uso de HTTP directo (no librerías de alto nivel como yfinance)
- Peticiones explícitas a la API pública
- Manejo de errores con reintentos

### 3.2 LimpiarDatos

**Responsabilidad**: Eliminar duplicados, detectar outliers, interpolar valores faltantes

**Complejidad**: O(n) para遍历 de datos

**Técnicas**:
- Eliminación de duplicados mediante HashSet
- Detección de outliers usando rango intercuartil (IQR)
- Interpolación lineal para valores faltantes

### 3.3 Algoritmos de Ordenamiento

Los 12 algoritmos implementados con su complejidad teórica:

| Algoritmo | Complejidad |
|-----------|-------------|
| TimSort | O(n log n) |
| Comb Sort | O(n²) |
| Selection Sort | O(n²) |
| Tree Sort | O(n log n) |
| Pigeonhole Sort | O(n + k) |
| Bucket Sort | O(n + k) |
| QuickSort | O(n log n) |
| HeapSort | O(n log n) |
| Bitonic Sort | O(log² n) |
| Gnome Sort | O(n²) |
| Binary Insertion Sort | O(n²) |
| Radix Sort | O(nk) |

**Criterio de ordenamiento**: 
- Primary: Fecha
- Secondary: Precio de cierre

### 3.4 AnalizadorVolumen

**Responsabilidad**: Identificar los 15 días con mayor volumen total

**Complejidad**: O(n) para agregación + O(n log n) para ordenamiento

## 4. Activos Financieros

### 10 Acciones Colombianas
ECOPETROL, ISA, GEB, NUTRESA, GRUPOSURA, BANCOLOMBIA, COLTEJERA, ENKA, PREFERENCIAL, CANACOL

### 10 ETFs Internacionales
VOO, VTI, QQQ, SPY, VEA, VWO, BND, EFA, EEM, TLT

## 5. Justificación de Decisiones

### 5.1 API de Alpha Vantage
- Cumple con el requisito de "peticiones explícitas a APIs públicas"
- Provee datos financieros históricos de calidad
- Requiere API key gratuita

### 5.2 Implementación Manual de Algoritmos
- Cada algoritmo está implementado explícitamente
- Permite análisis transparente de comportamiento
- Evita uso de librerías de "alto nivel" que encapsulen algoritmos

### 5.3 Datos Reales
- El sistema usa siempre datos reales de la API
- No se permiten datasets estáticos o manuales
- La reproducibilidad está garantizada

## 6. Resultados Esperados

- CSV con tabla de 12 algoritmos (método+complejidad, tamaño, tiempo)
- CSV con 15 días de mayor volumen
- Gráfico de barras comparando tiempos
- Los resultados deben mostrar correlación entre complejidad teórica y tiempo real

## 7. Requisitos de Ejecución

- Java 17
- Maven 3.8+

Ejecución: `mvn compile exec:java`
