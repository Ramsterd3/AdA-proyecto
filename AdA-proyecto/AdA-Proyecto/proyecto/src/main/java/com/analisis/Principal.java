package com.analisis;

import com.analisis.algoritmo.*;
import com.analisis.modelo.DatoFinanciero;
import com.analisis.modelo.ResultadoOrdenamiento;
import com.analisis.modelo.ResultadoVolumen;
import com.analisis.servicio.AnalizadorVolumen;
import com.analisis.servicio.GeneradorGrafica;
import com.analisis.servicio.LimpiarDatos;
import com.analisis.servicio.ObtenerDatos;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Clase principal - Punto de entrada del programa
 */
public class Principal {

    private static final int ANIOS = 5;
    private static final String ARCHIVO_ORDENAMIENTO = "datos_ordenados.csv";
    private static final String ARCHIVO_VOLUMEN = "top_volumen.csv";

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
        
        // Analizar volumen
        AnalizadorVolumen analizadorVolumen = new AnalizadorVolumen();
        List<ResultadoVolumen> volumenes = analizadorVolumen.analizarTopVolumen(datos, 15);
        analizadorVolumen.guardarResultados(volumenes, ARCHIVO_VOLUMEN);
        
        System.out.println("\n========================================");
        System.out.println("ANALISIS COMPLETADO");
        System.out.println("========================================");
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