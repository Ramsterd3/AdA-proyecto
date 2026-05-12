package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import com.analisis.modelo.ResultadoSimilitud;
import com.analisis.similitud.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AnalizadorSimilitud {

    private final List<InterfazSimilitud> algoritmos = Arrays.asList(
        new DistanciaEuclidiana(),
        new CorrelacionPearson(),
        new DTW(),
        new SimilitudCoseno()
    );

    public List<ResultadoSimilitud> analizar(List<DatoFinanciero> datos, String simboloA, String simboloB) {
        double[] serieA = extraerRetornos(datos, simboloA);
        double[] serieB = extraerRetornos(datos, simboloB);

        List<ResultadoSimilitud> resultados = new ArrayList<>();
        for (InterfazSimilitud alg : algoritmos) {
            double valor = alg.calcular(serieA, serieB);
            resultados.add(new ResultadoSimilitud(simboloA, simboloB,
                alg.getNombre(), alg.getComplejidad(), valor));
        }
        return resultados;
    }

    public List<ResultadoSimilitud> analizarTodosPares(List<DatoFinanciero> datos) {
        System.out.println("\n=== ETAPA: ANALISIS DE SIMILITUD ===");

        Set<String> simbolos = new LinkedHashSet<>();
        for (DatoFinanciero d : datos) simbolos.add(d.getSimbolo());
        List<String> lista = new ArrayList<>(simbolos);

        List<ResultadoSimilitud> todos = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                todos.addAll(analizar(datos, lista.get(i), lista.get(j)));
            }
        }

        System.out.println("Pares analizados: " + (todos.size() / algoritmos.size()));
        System.out.println("Total resultados: " + todos.size());
        return todos;
    }

    public void guardarResultados(List<ResultadoSimilitud> resultados, String archivo) {
        try (FileWriter fw = new FileWriter(archivo)) {
            fw.write(resultados.get(0).toCsvHeader() + "\n");
            for (ResultadoSimilitud r : resultados) {
                fw.write(r.toString() + "\n");
            }
            System.out.println("Resultados de similitud guardados en " + archivo);
        } catch (IOException e) {
            System.err.println("Error al guardar similitud: " + e.getMessage());
        }
    }

    public void mostrarResumen(List<DatoFinanciero> datos, String simboloA, String simboloB) {
        double[] serieA = extraerRetornos(datos, simboloA);
        double[] serieB = extraerRetornos(datos, simboloB);

        System.out.println("\nSimilitud entre " + simboloA + " y " + simboloB + ":");
        System.out.println(String.format("%-30s %-12s %-15s", "Algoritmo", "Complejidad", "Valor"));
        System.out.println("-".repeat(60));

        for (InterfazSimilitud alg : algoritmos) {
            double valor = alg.calcular(serieA, serieB);
            System.out.println(String.format("%-30s %-12s %-15.6f",
                alg.getNombre(), alg.getComplejidad(), valor));
        }

        System.out.println("\n--- Descripcion de Metodos ---");
        for (InterfazSimilitud alg : algoritmos) {
            System.out.println("\n" + alg.getNombre() + ":");
            System.out.println("  Complejidad: " + alg.getComplejidad());
            System.out.println("  " + alg.getDescripcion());
        }
    }

    private double[] extraerRetornos(List<DatoFinanciero> datos, String simbolo) {
        List<DatoFinanciero> serie = new ArrayList<>();
        for (DatoFinanciero d : datos) {
            if (d.getSimbolo().equals(simbolo)) serie.add(d);
        }
        serie.sort(Comparator.comparing(DatoFinanciero::getFecha));

        double[] retornos = new double[Math.max(0, serie.size() - 1)];
        for (int i = 1; i < serie.size(); i++) {
            double prev = serie.get(i - 1).getCierre();
            double curr = serie.get(i).getCierre();
            retornos[i - 1] = prev != 0 ? (curr - prev) / prev : 0.0;
        }
        return retornos;
    }
}
