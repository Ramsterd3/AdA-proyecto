package com.analisis;

import com.analisis.algoritmo.*;
import com.analisis.modelo.ClasificacionRiesgo;
import com.analisis.modelo.DatoFinanciero;
import com.analisis.modelo.ResultadoOrdenamiento;
import com.analisis.modelo.ResultadoVolumen;
import com.analisis.modelo.ResultadoSimilitud;
import com.analisis.servicio.AnalizadorPatrones;
import com.analisis.servicio.AnalizadorSimilitud;
import com.analisis.servicio.AnalizadorVolumen;
import com.analisis.servicio.GeneradorGrafica;
import com.analisis.servicio.LimpiarDatos;
import com.analisis.servicio.ObtenerDatos;
import com.analisis.servicio.SelectorActivos;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Clase principal - Punto de entrada del programa
 */
public class Principal {

    private static final int ANIOS = 5;
    private static final int VENTANA_PATRONES = 5;
    private static final String ARCHIVO_ORDENAMIENTO = "datos_ordenados.csv";
    private static final String ARCHIVO_VOLUMEN = "top_volumen.csv";
    private static final String ARCHIVO_SIMILITUD = "similitud_activos.csv";
    private static final String ARCHIVO_PATRONES = "patrones_detectados.csv";
    private static final String ARCHIVO_RIESGO = "clasificacion_riesgo.csv";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("ANALISIS DE ALGORITMOS DE ORDENAMIENTO");
        System.out.println("========================================\n");

        // Obtener datos
        ObtenerDatos obtentor = new ObtenerDatos();
        List<DatoFinanciero> datos = obtentor.obtenerTodosLosDatos(ANIOS);
        
        System.out.println("\n=== ESTADISTICAS ===");
        System.out.println("Total registros: " + datos.size());
        
        // Limpiar datos
        LimpiarDatos limpiador = new LimpiarDatos();
        datos = limpiador.limpiarDatos(datos);
        
        // Unificar y guardar datos
        System.out.println("\n=== ETAPA 3: UNIFICACION ===");
        guardarDatos(datos, "datos_unificados.csv");
        
        // Ordenar y analizar
        DatoFinanciero[] arrayOrdenar = datos.toArray(new DatoFinanciero[0]);
        
        System.out.println("\n=== ETAPA 4: ANALISIS DE ORDENAMIENTO ===");
        List<ResultadoOrdenamiento> resultados = ejecutarAlgoritmos(arrayOrdenar);
        
        // Guardar resultados
        guardarResultadosOrdenamiento(resultados, ARCHIVO_ORDENAMIENTO);
        
        // Generar grafica
        GeneradorGrafica generadorGrafica = new GeneradorGrafica();
        generadorGrafica.generarGrafica(resultados, "grafica_ordenamiento.png");
        
        // Analisis de similitud con selector interactivo
        System.out.println("\n=== ANALISIS DE SIMILITUD ===");
        
        SelectorActivos selector = new SelectorActivos();
        AnalizadorSimilitud analizadorSimilitud = new AnalizadorSimilitud();
        GeneradorGrafica generadorSeries = new GeneradorGrafica();
        
        // Verificar si hay argumentos de linea de comandos
        boolean modoInteractivo = args.length == 0;
        
        if (modoInteractivo) {
            // Modo interactivo
            boolean continuar = true;
            while (continuar) {
                int opcion = selector.mostrarMenuAnalisis();
                
                switch (opcion) {
                    case 1: // Analizar par especifico
                        String[] activos = selector.seleccionarActivosInteractivo(datos);
                        analizarParActivos(datos, activos[0], activos[1], 
                                         analizadorSimilitud, generadorSeries, generadorGrafica);
                        continuar = selector.deseaContinuar();
                        break;
                        
                    case 2: // Analizar todos los pares
                        System.out.println("\nAnalizando todos los pares de activos...");
                        List<ResultadoSimilitud> similitudes = analizadorSimilitud.analizarTodosPares(datos);
                        analizadorSimilitud.guardarResultados(similitudes, ARCHIVO_SIMILITUD);
                        continuar = selector.deseaContinuar();
                        break;
                        
                    case 3: // Buscar similares a uno dado
                        List<String> simbolos = selector.obtenerSimbolosDisponibles(datos);
                        String simboloRef = selector.seleccionarActivoUnico(simbolos);
                        buscarActivosSimilares(datos, simboloRef, analizadorSimilitud);
                        continuar = selector.deseaContinuar();
                        break;
                        
                    case 4: // Salir
                        continuar = false;
                        break;
                }
            }
            selector.cerrar();
        } else {
            // Modo linea de comandos (compatibilidad)
            String simboloA = args.length > 0 ? args[0] : "AAPL";
            String simboloB = args.length > 1 ? args[1] : "MSFT";
            
            System.out.println("Modo linea de comandos: " + simboloA + " vs " + simboloB);
            analizarParActivos(datos, simboloA, simboloB, 
                             analizadorSimilitud, generadorSeries, generadorGrafica);
            
            List<ResultadoSimilitud> similitudes = analizadorSimilitud.analizarTodosPares(datos);
            analizadorSimilitud.guardarResultados(similitudes, ARCHIVO_SIMILITUD);
        }
        
        // REQUERIMIENTO 3: Analisis de Patrones y Volatilidad
        System.out.println("\n=== REQUERIMIENTO 3: PATRONES Y VOLATILIDAD ===");
        
        AnalizadorPatrones analizadorPatrones = new AnalizadorPatrones();
        Map<String, Map<String, Integer>> frecuenciasPatrones = 
            analizadorPatrones.analizarPatrones(datos, VENTANA_PATRONES);
        analizadorPatrones.guardarReportePatrones(frecuenciasPatrones, ARCHIVO_PATRONES);
        
        AnalizadorVolumen analizadorVolumen = new AnalizadorVolumen();
        List<ResultadoVolumen> topVolumen = analizadorVolumen.analizarTopVolumen(datos, 15);
        analizadorVolumen.guardarResultados(topVolumen, ARCHIVO_VOLUMEN);
        
        com.analisis.servicio.AnalizadorVolatilidad analizadorVolatilidad = 
            new com.analisis.servicio.AnalizadorVolatilidad();
        List<ClasificacionRiesgo> clasificacionesRiesgo = 
            analizadorVolatilidad.clasificarPorRiesgo(datos);
        analizadorVolatilidad.guardarReporteRiesgo(clasificacionesRiesgo, ARCHIVO_RIESGO);
        analizadorVolatilidad.mostrarResumenCategorias(clasificacionesRiesgo);
        
        System.out.println("\n========================================");
        System.out.println("ANALISIS COMPLETADO");
        System.out.println("========================================");
    }

    /**
     * Analiza la similitud entre dos activos especificos
     */
    private static void analizarParActivos(List<DatoFinanciero> datos, 
                                           String simboloA, String simboloB,
                                           AnalizadorSimilitud analizador,
                                           GeneradorGrafica generadorSeries,
                                           GeneradorGrafica generadorGrafica) {
        System.out.println("\nComparando: " + simboloA + " vs " + simboloB);
        
        // Mostrar resumen de similitud
        analizador.mostrarResumen(datos, simboloA, simboloB);
        
        // Generar grafica de series temporales
        generadorSeries.generarGraficaSeries(datos, simboloA, simboloB, 
                                            "series_" + simboloA + "_" + simboloB + ".png");
        
        // Obtener valores de similitud para grafica
        List<ResultadoSimilitud> resultados = analizador.analizar(datos, simboloA, simboloB);
        
        if (resultados.size() >= 4) {
            generadorGrafica.generarGraficaSimilitud(
                simboloA, simboloB,
                resultados.get(0).getValor(), // Euclidiana
                resultados.get(1).getValor(), // Pearson
                resultados.get(2).getValor(), // DTW
                resultados.get(3).getValor(), // Coseno
                "similitud_" + simboloA + "_" + simboloB + ".png"
            );
        }
    }

    /**
     * Busca los activos mas similares a uno dado
     */
    private static void buscarActivosSimilares(List<DatoFinanciero> datos,
                                               String simboloRef,
                                               AnalizadorSimilitud analizador) {
        System.out.println("\nBuscando activos similares a " + simboloRef + "...");
        
        List<String> simbolos = datos.stream()
            .map(DatoFinanciero::getSimbolo)
            .distinct()
            .filter(s -> !s.equals(simboloRef))
            .collect(java.util.stream.Collectors.toList());
        
        // Calcular similitud con todos los demas
        Map<String, Double> similitudes = new java.util.HashMap<>();
        
        for (String simbolo : simbolos) {
            List<ResultadoSimilitud> resultados = analizador.analizar(datos, simboloRef, simbolo);
            // Usar correlacion de Pearson como metrica principal
            double pearson = resultados.stream()
                .filter(r -> r.getAlgoritmo().contains("Pearson"))
                .findFirst()
                .map(ResultadoSimilitud::getValor)
                .orElse(0.0);
            similitudes.put(simbolo, pearson);
        }
        
        // Ordenar por similitud (mayor a menor)
        List<Map.Entry<String, Double>> ordenados = new java.util.ArrayList<>(similitudes.entrySet());
        ordenados.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        // Mostrar top 5
        System.out.println("\nTop 5 activos mas similares a " + simboloRef + ":");
        System.out.println(String.format("%-10s %15s", "Simbolo", "Correlacion"));
        System.out.println("-".repeat(30));
        
        for (int i = 0; i < Math.min(5, ordenados.size()); i++) {
            Map.Entry<String, Double> entry = ordenados.get(i);
            System.out.println(String.format("%-10s %15.6f", entry.getKey(), entry.getValue()));
        }
    }

    private static List<ResultadoOrdenamiento> ejecutarAlgoritmos(DatoFinanciero[] datosOriginal) {
        List<InterfazOrdenamiento> algoritmos = Arrays.asList(
            new TimSort(),
            new CombSort(),
            new SelectionSort(),
            new TreeSort(),
            new PigeonholeSort(),
            new BucketSort(),
            new QuickSort(),
            new HeapSort(),
            new BitonicSort(),
            new GnomeSort(),
            new BinaryInsertionSort(),
            new RadixSort()
        );
        
        List<ResultadoOrdenamiento> resultados = new ArrayList<>();
        
        for (InterfazOrdenamiento algoritmo : algoritmos) {
            System.out.print("Evaluando " + algoritmo.getNombre() + "... ");
            
            DatoFinanciero[] copia = datosOriginal.clone();
            
            long inicio = System.nanoTime();
            algoritmo.ordenar(copia);
            long fin = System.nanoTime();
            
            double tiempo = fin - inicio;
            resultados.add(new ResultadoOrdenamiento(
                algoritmo.getNombre(),
                algoritmo.getComplejidad(),
                copia.length,
                tiempo
            ));
            
            System.out.printf("%.6f s%n", tiempo / 1_000_000_000.0);
        }
        
        // Ordenar por tiempo
        resultados.sort(Comparator.comparingDouble(ResultadoOrdenamiento::getTiempoNanosegundos));
        
        // Mostrar tabla
        System.out.println("\n" + String.format("%-25s %-15s %-10s %-15s", 
            "Algoritmo", "Complejidad", "Tamano", "Tiempo (s)"));
        System.out.println("-".repeat(70));
        for (ResultadoOrdenamiento r : resultados) {
            System.out.println(String.format("%-25s %-15s %-10d %-15.6f",
                r.getAlgoritmo(), r.getComplejidad(), r.getTamano(), r.getTiempoSegundos()));
        }
        
        return resultados;
    }

    private static void guardarDatos(List<DatoFinanciero> datos, String archivo) {
        try (FileWriter fw = new FileWriter(archivo)) {
            if (!datos.isEmpty()) {
                fw.write(datos.get(0).toCsvHeader() + "\n");
                for (DatoFinanciero d : datos) {
                    fw.write(d.toString() + "\n");
                }
            }
            System.out.println("Datos guardados en " + archivo + " (" + datos.size() + " registros)");
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    private static void guardarResultadosOrdenamiento(List<ResultadoOrdenamiento> resultados, String archivo) {
        try (FileWriter fw = new FileWriter(archivo)) {
            fw.write(resultados.get(0).toCsvHeader() + "\n");
            for (ResultadoOrdenamiento r : resultados) {
                fw.write(r.toString() + "\n");
            }
            System.out.println("\nResultados guardados en " + archivo);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }
}