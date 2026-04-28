package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import com.analisis.modelo.ResultadoVolumen;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analiza el volumen de negociacion
 */
public class AnalizadorVolumen {

    public List<ResultadoVolumen> analizarTopVolumen(List<DatoFinanciero> datos, int topN) {
        System.out.println("\n=== ETAPA 5: ANALISIS DE VOLUMEN ===");
        
        Map<String, Long> volumenPorFecha = new HashMap<>();
        
        for (DatoFinanciero d : datos) {
            String fecha = d.getFecha();
            long volumen = d.getVolumen();
            volumenPorFecha.merge(fecha, volumen, Long::sum);
        }
        
        List<ResultadoVolumen> resultados = volumenPorFecha.entrySet().stream()
            .map(e -> new ResultadoVolumen(e.getKey(), e.getValue()))
            .sorted(Comparator.comparingLong(ResultadoVolumen::getVolumenTotal))
            .limit(topN)
            .collect(Collectors.toList());
        
        System.out.println("\nLos " + topN + " dias con mayor volumen (orden ascendente):");
        System.out.println(String.format("%-15s %20s", "Fecha", "Volumen Total"));
        System.out.println("----------------------------------------");
        
        for (ResultadoVolumen r : resultados) {
            System.out.println(String.format("%-15s %20d", r.getFecha(), r.getVolumenTotal()));
        }
        
        return resultados;
    }

    public void guardarResultados(List<ResultadoVolumen> resultados, String archivo) {
        try (FileWriter fw = new FileWriter(archivo)) {
            fw.write(resultados.get(0).toCsvHeader() + "\n");
            for (ResultadoVolumen r : resultados) {
                fw.write(r.toString() + "\n");
            }
            System.out.println("\nResultados guardados en " + archivo);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }
}