# Prompts de IA utilizados en el desarrollo

Este documento declara el uso de herramientas de inteligencia artificial generativa como apoyo al desarrollo del proyecto, conforme a los lineamientos del curso.

---

## 1. Configuración inicial del proyecto

**Prompt 1**
> "Crea un proyecto Maven en Java 17 para analizar algoritmos de ordenamiento con datos financieros. Necesito la estructura de paquetes: modelo, servicio, algoritmo y una clase Principal."

**Prompt 2**
> "Crea la clase DatoFinanciero con los campos fecha, símbolo, open, high, low, close y volume, con sus getters, setters y método toCsvHeader()."

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

---

## 4. Algoritmos de ordenamiento

**Prompt 7**
> "Implementa los siguientes 12 algoritmos de ordenamiento en Java de forma explícita (sin usar Collections.sort ni Arrays.sort): TimSort, CombSort, SelectionSort, TreeSort, PigeonholeSort, BucketSort, QuickSort, HeapSort, BitonicSort, GnomeSort, BinaryInsertionSort y RadixSort."

**Prompt 8**
> "Crea una interfaz InterfazOrdenamiento con los métodos ordenar(DatoFinanciero[]), getNombre() y getComplejidad() que todos los algoritmos deben implementar."

**Prompt 9**
> "El criterio de ordenamiento debe ser: primero por fecha, y cuando la fecha sea igual, por precio de cierre de forma ascendente."

---

## 5. Medición y resultados

**Prompt 10**
> "Mide el tiempo de ejecución de cada algoritmo con System.nanoTime() y guarda los resultados en un CSV con columnas: algoritmo, complejidad, tamaño y tiempo."

**Prompt 11**
> "Crea la clase AnalizadorVolumen que identifique los 15 días con mayor volumen total sumando todos los activos por fecha, y guarde el resultado en top_volumen.csv."

---

## 6. Visualización

**Prompt 12**
> "Crea la clase GeneradorGrafica que use JFreeChart para generar un diagrama de barras verticales con los tiempos de los 12 algoritmos ordenados de forma ascendente y lo exporte como PNG."

---

## 7. Documentación

**Prompt 13**
> "Crea un documento de diseño en Markdown que explique la arquitectura del sistema, los módulos, las decisiones técnicas y la complejidad de cada algoritmo."

**Prompt 14**
> "Agrega al documento de diseño la sección de arquitectura ETL con diagrama de bloques, descripción de cada etapa y diagrama de secuencia."

---

## Declaración de uso

Las herramientas de IA generativa fueron utilizadas como soporte en la generación de código base y documentación. El diseño algorítmico, el análisis de complejidad y la verificación de resultados fueron realizados por los estudiantes. Ningún algoritmo solicitado fue reemplazado por funciones de alto nivel provistas por librerías externas.
