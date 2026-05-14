# Prompts de IA utilizados en el desarrollo

Este documento declara el uso de herramientas de inteligencia artificial generativa como apoyo al desarrollo del proyecto, conforme a los lineamientos del curso.

---

## 1. Configuración inicial del proyecto

**Prompt 1**
> "Crea un proyecto Maven en Java 17 para analizar algoritmos de ordenamiento con datos financieros. Necesito la estructura de paquetes: modelo, servicio, algoritmo y una clase Principal."

**Prompt 2**
> "Crea la clase DatoFinanciero con los campos fecha, símbolo, open, high, low, close y volume, con sus getters, setters y método toCsvHeader(). Implementa Comparable para ordenar por fecha y luego por precio de cierre."

---

## 2. ETL — Extracción

**Prompt 3**
> "Implementa una clase ObtenerDatos que consuma la API de Twelve Data para descargar series de tiempo diarias de 20 símbolos bursátiles para los últimos 5 años, usando solo HttpURLConnection sin librerías de alto nivel."

**Prompt 4**
> "El parseo del JSON de Twelve Data debe hacerse manualmente con indexOf y substring, sin usar Gson ni Jackson. Extrae los campos datetime, open, high, low, close y volume del array values[]."

**Prompt 5**
> "Agrega un delay de 8 segundos entre peticiones para respetar el rate limit del plan gratuito de Twelve Data."

---

## 3. ETL — Transformación

**Prompt 6**
> "Crea la clase LimpiarDatos que elimine duplicados usando HashSet con clave fecha-símbolo, detecte outliers con el método IQR (Q1 - 1.5·IQR, Q3 + 1.5·IQR) e interpole valores faltantes con el promedio entre el registro anterior y siguiente."

**Prompt 7**
> "Crea la clase CalendarioBursatil que gestione el calendario bursátil de EE.UU. y Colombia. Debe incluir un enum Mercado con valores USA, COLOMBIA y AMBOS. Implementa métodos: esDiaHabil(LocalDate fecha, Mercado mercado), esFestivo(LocalDate fecha, Mercado mercado), siguienteDiaHabil() y anteriorDiaHabil(). Incluye los principales festivos de ambos países para los años 2020-2026."

---

## 4. Algoritmos de ordenamiento

**Prompt 8**
> "Implementa los siguientes 12 algoritmos de ordenamiento en Java de forma explícita (sin usar Collections.sort ni Arrays.sort): TimSort, CombSort, SelectionSort, TreeSort, PigeonholeSort, BucketSort, QuickSort, HeapSort, BitonicSort, GnomeSort, BinaryInsertionSort y RadixSort."

**Prompt 9**
> "Crea una interfaz InterfazOrdenamiento con los métodos ordenar(DatoFinanciero[]), getNombre() y getComplejidad() que todos los algoritmos deben implementar."

**Prompt 10**
> "El criterio de ordenamiento debe ser: primero por fecha, y cuando la fecha sea igual, por precio de cierre de forma ascendente."

---

## 5. Algoritmos de similitud

**Prompt 11**
> "Implementa 4 algoritmos de similitud entre series de tiempo financieras: DistanciaEuclidiana, CorrelacionPearson, DynamicTimeWarping (DTW) y SimilitudCoseno. Cada uno debe implementar la interfaz InterfazSimilitud con método calcular(double[] serieA, double[] serieB)."

**Prompt 12**
> "Los algoritmos de similitud deben usar retornos diarios calculados como: retorno_i = (cierre_i - cierre_{i-1}) / cierre_{i-1}. Incluye documentación con la fórmula matemática, complejidad algorítmica y casos de uso."

**Prompt 13**
> "Crea la clase AnalizadorSimilitud que extraiga los retornos de cada símbolo, calcule las 4 métricas para un par de activos y muestre los resultados con su descripción algorítmica."

---

## 6. Análisis de patrones (Sliding Window) — Requerimiento 3

**Prompt 14**
> "Implementa la clase AnalizadorPatrones que use ventanas deslizantes (sliding window) para detectar patrones en series temporales financieras. La complejidad debe ser O(n * v * s) donde n=días, v=ventana, s=símbolos."

**Prompt 15**
> "Implementa el patrón DIAS_CONSECUTIVOS_ALZA: detecta secuencias donde todos los días en la ventana tienen cierre mayor al día anterior. Formalización: para toda i en [inicio, fin-1]: cierre[i+1] > cierre[i]."

**Prompt 16**
> "Implementa el patrón adicional FORMACION_VALLE: detecta mínimos locales seguidos de recuperación. Formalización: el mínimo está en posición p, y el promedio de precios posteriores a p es mayor al promedio de precios anteriores a p."

**Prompt 17**
> "Implementa el patrón adicional DOBLE_MAXIMO: detecta dos máximos relativos dentro de una ventana, indicando potencial patrón de resistencia o formación de M."

---

## 7. Análisis de volatilidad y riesgo — Requerimiento 3

**Prompt 18**
> "Implementa la clase AnalizadorVolatilidad que calcule la desviación estándar de los retornos diarios usando la fórmula: sqrt(sum(x_i - media)^2 / (n-1))."

**Prompt 19**
> "Calcula la volatilidad histórica anualizada con la fórmula: volatilidad = desviacionEstandar * sqrt(252), donde 252 es el número de días de trading promedio por año."

**Prompt 20**
> "Clasifica los activos en tres categorías de riesgo según la volatilidad histórica anualizada: CONSERVADOR (volatilidad < 15%), MODERADO (15% <= volatilidad < 30%), AGRESIVO (volatilidad >= 30%). Genera un CSV ordenado ascendentemente por volatilidad."

---

## 8. Interfaz interactiva

**Prompt 21**
> "Crea la clase SelectorActivos con métodos para mostrar un menú interactivo de análisis, seleccionar activos por número o símbolo, y permitir análisis múltiples sin reiniciar el programa."

**Prompt 22**
> "Modifica la clase Principal para ejecutar automáticamente el análisis de similitud al iniciar, permitiendo modo interactivo (sin argumentos) y modo línea de comandos (con argumentos simboloA simboloB)."

---

## 9. Medición y resultados

**Prompt 23**
> "Mide el tiempo de ejecución de cada algoritmo de ordenamiento con System.nanoTime() y guarda los resultados en un CSV con columnas: algoritmo, complejidad, tamaño y tiempo."

**Prompt 24**
> "Crea la clase AnalizadorVolumen que identifique los 15 días con mayor volumen total sumando todos los activos por fecha. Importante: primero selecciona los de mayor volumen (orden descendente) y luego ordena ascendente para presentación."

---

## 10. Visualización

**Prompt 25**
> "Crea la clase GeneradorGrafica que use JFreeChart para generar: 1) diagrama de barras con tiempos de algoritmos ordenados por complejidad teórica, 2) series temporales comparativas de dos activos, 3) gráfica comparativa de las 4 métricas de similitud."

---

## 11. Documentación

**Prompt 26**
> "Crea un documento de diseño en Markdown que explique la arquitectura del sistema, los módulos, las decisiones técnicas y la complejidad de cada algoritmo."

**Prompt 27**
> "Agrega al documento de diseño la sección de arquitectura ETL con diagrama de bloques, descripción de cada etapa y diagrama de secuencia. Incluye también las secciones de algoritmos de ordenamiento y similitud con tablas comparativas."

---

## 12. Testing

**Prompt 28**
> "Crea tests unitarios con JUnit 5 para: CalendarioBursatil (días hábiles, festivos), LimpiarDatos (duplicados, outliers, interpolación), algoritmos de similitud (series vacías, datos idénticos, casos límites), y algoritmos de ordenamiento (orden correcto, casos especiales)."

**Prompt 29**
> "Crea tests unitarios para AnalizadorPatrones (detección de patrones de alza, valle, doble máximo) y AnalizadorVolatilidad (cálculo de desviación estándar, volatilidad histórica, clasificación de riesgo)."

---

## Declaración de uso

Las herramientas de IA generativa fueron utilizadas como soporte en la generación de código base y documentación. El diseño algorítmico, el análisis de complejidad y la verificación de resultados fueron realizados por los estudiantes. Ningún algoritmo solicitado fue reemplazado por funciones de alto nivel provistas por librerías externas.

Se declara el uso de IA para el desarrollo de los siguientes componentes:
- Estructura del proyecto y configuración Maven
- Implementación de algoritmos de ordenamiento (12)
- Implementación de algoritmos de similitud (4)
- Implementación de análisis de patrones con sliding window (3 patrones)
- Implementación de análisis de volatilidad y clasificación de riesgo
- Generación de visualizaciones con JFreeChart
- Tests unitarios (61 tests en total)
- Documentación técnica