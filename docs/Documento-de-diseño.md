# Documento de Diseño - Proyecto de Análisis de Algoritmos

## 1. Introducción

Este proyecto implementa un sistema de análisis algorítmico sobre datos financieros reales. Cubre el proceso ETL completo, comparación de 12 algoritmos de ordenamiento y análisis de similitud entre series de tiempo de activos financieros mediante 4 métricas distintas.

## 2. Arquitectura del Sistema

### 2.1 Estructura Monolítica

El proyecto sigue una arquitectura monolítica simple:

```
src/main/java/com/analisis/
├── Principal.java              # Punto de entrada
├── modelo/
│   ├── DatoFinanciero.java    # Entidad de datos OHLCV
│   ├── ResultadoOrdenamiento.java
│   ├── ResultadoVolumen.java
│   └── ResultadoSimilitud.java
├── servicio/
│   ├── ObtenerDatos.java      # Extracción de datos
│   ├── LimpiarDatos.java      # Transformación
│   ├── CalendarioBursatil.java # Calendario mercados Colombia/EE.UU
│   ├── AnalizadorVolumen.java # Análisis de volumen
│   ├── AnalizadorSimilitud.java # Análisis de similitud
│   └── GeneradorGrafica.java  # Visualización
├── algoritmo/
│   ├── InterfazOrdenamiento.java
│   ├── TimSort.java
│   ├── CombSort.java
│   ├── SelectionSort.java
│   ├── TreeSort.java
│   ├── PigeonholeSort.java
│   ├── BucketSort.java
│   ├── QuickSort.java
│   ├── HeapSort.java
│   ├── BitonicSort.java
│   ├── GnomeSort.java
│   ├── BinaryInsertionSort.java
│   └── RadixSort.java
└── similitud/
    ├── InterfazSimilitud.java
    ├── DistanciaEuclidiana.java
    ├── CorrelacionPearson.java
    ├── DTW.java
    └── SimilitudCoseno.java
```

### 2.2 Flujo de Ejecución

```
ETL (Obtener → Limpiar → Unificar)
         ↓
Ordenamiento (12 algoritmos)
         ↓
Análisis de Volumen
         ↓
Análisis de Similitud (4 algoritmos)
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
│                      Calendarizacion                             │
│                      Trazabilidad                               │
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

1. **ELIMINACION DE DUPLICADOS** (O(n))
   - HashSet con clave compuesta "fecha-simbolo"
   - Justificacion: Evita sobreponderacion de precios
   - Impacto: Inflacion artificial de volumen evitada

2. **DETECCION DE OUTLIERS** (O(n log n))
   - Metodo: Rango Intercuartil (IQR)
   - Formula: [Q1 - 1.5*IQR, Q3 + 1.5*IQR]
   - Justificacion:Valores extremos distorsionan estadisticas
   - Impacto: Solo se reporta, no se elimina (analisis de sensibilidad)

3. **INTERPOLACION DE VALORES FALTANTES** (O(n^2))
   - Estrategia por orden de prioridad:
     1. Interpolacion lineal: (anterior + siguiente) / 2 — mantiene continuidad
     2. Forward fill: usa valor anterior — conservador
     3. Backward fill: usa valor siguiente — ajuste rapido
   - Justificacion:Series temporales requieren continuidad
   - Impacto: Mantiene tendencia vs suaviza transiciones

**Salida**: `List<DatoFinanciero>` limpia y lista para análisis

##### Componente `CalendarioBursatil`

**Responsabilidad**: Manejar el calendario bursatilde EE.UU.

**Caracteristicas**:
- Dias festivos de EE.UU. (2021-2026)
- Exclusion automatica de sabados y domingos
- Metodos: `esDiaHabil()`, `siguienteDiaHabil()`, `anteriorDiaHabil()`

**Complejidad**: O(1) por consulta

#### Etapa 3 — Load (`Principal.guardarDatos()`)

**Proceso**:
1. Recibe la lista limpia de `DatoFinanciero`
2. Escribe el encabezado CSV usando `toCsvHeader()`
3. Serializa cada registro con `toString()` en formato CSV
4. Persiste en `datos_unificados.csv` en el directorio de ejecución

**Salida**: archivo `datos_unificados.csv` con todos los registros procesado

#### Trazabilidad del Proceso ETL

El sistema registra decisiones en log:
```
Duplicados eliminados: X
Outliers detectados: rango=[Q1-1.5*IQR, Q3+1.5*IQR]
Valores interpolados: lineal/forward/backward
```

**Importancia**:
- Permite auditar el proceso de limpieza
- Justifica el impacto de cada decision sobre analisis posteriores
- Cumple con requisitos de transparencia algoritmica

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

**Responsabilidad**: Eliminar duplicados, detectar outliers, interpolar valores faltantes, calendarizar series temporales

**Complejidad**: O(n log n) por el ordenamiento de las series

** Técnicas**:
 - Eliminación de duplicados mediante HashSet
 - Detección de outliers usando rango intercuartil (IQR)
 - Interpolación lineal, forward fill y backward fill para valores faltantes
 - Calendarización usando `CalendarioBursatil`

**Impacto algorítmico de las decisiones**:
| Técnica | Cuándo usar | Efecto en análisis |
|---------|-----------|------------------|
| Interpolación lineal | Datos faltantes en medio | Suaviza picos, mantiene tendencia |
| Forward fill | Primer dato faltante | Conservador, asume precio constantes |
| Backward fill | Último dato faltante | Utiliza info reciente |
| Calendarización | Fines de semana/festivos | Justifica gaps, mantiene continuidad |

### 3.2b CalendarioBursatil

**Responsabilidad**: Gestionar días hábiles en mercados financieros

**Complejidad**: O(1) por consulta

**enum Mercado**: `USA`, `COLOMBIA`, `AMBOS`

**Festivos incluidos (2020-2026)**:
 - EE.UU.: New Year's Day, MLK Day, Presidents Day, Good Friday, Memorial Day, Independence Day, Labor Day, Thanksgiving, Christmas
 - Colombia: Año Nuevo, Reyes Magos, San José, Semana Santa, Día del Trabajo, Ascensión, Corpus Christi, Sagrado Corazón, San Pedro y San Pablo, Independencia, Batalla de Boyacá, Asunción, Día de la Raza, Todos los Santos, Independencia de Cartagena, Inmaculada Concepción, Navidad

**Métodos Implementados**:
- `esDiaHabil(fecha, mercado)`: Verifica si una fecha es día hábil (O(1))
- `siguienteDiaHabil(fecha, mercado)`: Obtiene el siguiente día hábil (O(k) donde k = días hasta encontrar hábil)
- `anteriorDiaHabil(fecha, mercado)`: Obtiene el día hábil anterior (O(k))
- `contarDiasHabiles(inicio, fin, mercado)`: Cuenta días hábiles en un rango (O(n) donde n = días en el rango)
- `esFestivo(fecha, mercado)`: Verifica si es festivo (O(1))

**Integración con ETL**:
- Usado en `LimpiarDatos.filtrarDiasNoHabiles()` para eliminar registros en días no hábiles
- Justifica gaps en series temporales
- Alinea datos de diferentes fuentes al mismo calendario

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

### 3.5 Algoritmos de Similitud de Series de Tiempo

**Responsabilidad**: Comparar el comportamiento historico entre pares de activos usando retornos diarios.

**Preprocesamiento**: Los precios de cierre se convierten a retornos diarios:
```
retorno_i = (cierre_i - cierre_{i-1}) / cierre_{i-1}
```
Esto normaliza las series y permite comparar activos con precios en distintas escalas.

---

#### Distancia Euclidiana

| Aspecto | Detalle |
|---------|---------|
| Complejidad | O(n) |
| Espacio | O(1) |
| Formula | d(A,B) = sqrt(sum(a_i - b_i)^2) |
| Rango | [0, infinito) |

**Algoritmo**:
1. Obtener n = min(|A|, |B|)
2. Para i = 0 hasta n-1: suma += (A[i] - B[i])^2
3. Retornar sqrt(suma)

** Cuando usar**: Series igual longitud, impo magnitude diferencial, analisis rapido

** vs DTW**: Mas rapido O(n) vs O(n*m), no tolera desplazamientos
** vs Pearson**: Mide distancia, no relacion lineal
** vs Coseno**: Sensible a magnitud, no solo direccion

---

#### Correlacion de Pearson

| Aspecto | Detalle |
|---------|---------|
| Complejidad | O(n) |
| Espacio | O(1) |
| Formula | r = sum((a_i-avg)(b_i-avg)) / sqrt(sum(a_i-avg)^2 * sum(b_i-avg)^2) |
| Rango | [-1, 1] |

**Algoritmo**:
1. Calcular promedio de A y B
2. Numerador: sum((a_i-mediaA)(b_i-mediaB))
3. Denominador: sqrt(sum(a_i-mediaA)^2 * sum(b_i-mediaB)^2)
4. Retornar numerador/denominador

** Cuando usar**: Detectar si activos se mueven juntos, analisis de diversificacion

** vs Euclidiana**: Mide RELACIONlineal, no distancia
** vs DTW**: Mas rapido O(n), asume alineacion correcta
** vs Coseno**: Centra datos (restar media), Coseno no

---

#### Dynamic Time Warping (DTW)

| Aspecto | Detalle |
|---------|---------|
| Complejidad | O(n * m) |
| Espacio | O(n * m) |
| Formula | dtw[i][j] = |a_i-b_j| + min(dtw[i-1][j], dtw[i][j-1], dtw[i-1][j-1]) |
| Rango | [0, infinito) |

**Algoritmo**:
1. Crear matriz dtw[(n+1)][(m+1)] con infinito
2. dtw[0][0] = 0
3. Para i=1,n: para j=1,m: dtw[i][j] = |A[i-1]-B[j-1]| + minvecino
4. Retornar dtw[n][m]

** Cuando usar**: Series deferent longitud, delays ofase variable

** Advertencia**: Costoso para series largas (O(n*m))
** vs Euclidiana**: Tolera desplazamientos, mas costoso
** vs Pearson**: No assume alineacion temporal correcta
** vs Coseno**: Maneja series de diferente longitud

---

#### Similitud por Coseno

| Aspecto | Detalle |
|---------|---------|
| Complejidad | O(n) |
| Espacio | O(1) |
| Formula | cos = (A·B) / (||A|| * ||B||) |
| Rango | [-1, 1] |

**Algoritmo**:
1. Producto punto: sum(a_i * b_i)
2. Norma A: sqrt(sum(a_i^2))
3. Norma B: sqrt(sum(b_i^2))
4. Retornar punto / (normaA * normaB)

** Cuando usar**: Comparar direccion, no magnitud, datos de diferentes escalas

** Caracteristica especial**: INSENSIBLE a magnitud, solo direccion

---

#### Implementacion en `AnalizadorSimilitud`

- `analizar(datos, simboloA, simboloB)`: calcula los 4 algoritmos para un par especifico
- `analizarTodosPares(datos)`: itera sobre todos los pares posibles
- `mostrarResumen(datos, simboloA, simboloB)`: imprime tabla comparativa
- `guardarResultados(resultados, archivo)`: persiste en CSV

**Salida**: `similitud_activos.csv` con columnas `ActivoA, ActivoB, Algoritmo, Complejidad, Valor`

---

## 4. Activos Financieros

### 20 Activos de EE.UU.
AAPL, MSFT, GOOGL, AMZN, NVDA, META, TSLA, JPM, JNJ, V, PG, UNH, HD, MA, DIS, PYPL, ADBE, NFLX, INTC, CSCO

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

- `datos_unificados.csv` — todos los registros financieros procesados
- `datos_ordenados.csv` — tabla de 12 algoritmos (método, complejidad, tamaño, tiempo)
- `top_volumen.csv` — 15 días con mayor volumen de negociación
- `similitud_activos.csv` — similitud entre todos los pares de activos con 4 métricas
- `grafica_ordenamiento.png` — gráfica de barras comparando tiempos de ordenamiento
- Los resultados deben mostrar correlación entre complejidad teórica y tiempo real

## 7. Testing

### 7.1 Tests Unitarios

El proyecto incluye tests unitarios para verificar el correcto funcionamiento de los componentes críticos:

| Módulo | Clase Test | Tests |
|--------|-----------|-------|
| Servicio | CalendarioBursatilTest | 5 |
| Servicio | LimpiarDatosTest | 5 |
| Similitud | SimilitudTest | 10 |
| Algoritmo | AlgoritmoOrdenamientoTest | 8 |

**Total: 28 tests unitarios**

### 7.2 Comandos

```bash
# Compilar y ejecutar tests
mvn clean test
```

### 7.3 Cobertura

Los tests verifican:
- Calendario bursátil: días hábiles, festivos EE.UU. y Colombia
- Limpieza ETL: deduplicación, interpolación, forward fill, backward fill
- Algoritmos de similitud: casos límites, series vacías, datos idénticos
- Algoritmos de ordenamiento: orden correcto, casos especiales

## 8. Requisitos de Ejecución

- Java 17
- Maven 3.8+

Ejecución: `mvn compile exec:java`
