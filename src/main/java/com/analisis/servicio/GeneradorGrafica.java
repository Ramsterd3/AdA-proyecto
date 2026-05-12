package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import com.analisis.modelo.ResultadoOrdenamiento;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Genera grafica de barras verticales con JFreeChart
 */
public class GeneradorGrafica {

    private static final Map<String, Integer> ORDEN_COMPLEJIDAD = new TreeMap<>();
    
    static {
        ORDEN_COMPLEJIDAD.put("O(n)", 0);
        ORDEN_COMPLEJIDAD.put("O(n log n)", 1);
        ORDEN_COMPLEJIDAD.put("O(log² n)", 2);
        ORDEN_COMPLEJIDAD.put("O(n + k)", 3);
        ORDEN_COMPLEJIDAD.put("O(nk)", 4);
        ORDEN_COMPLEJIDAD.put("O(n²)", 5);
    }

    public void generarGrafica(List<ResultadoOrdenamiento> resultados, String archivo) {
        // Ordenar por complejidad teorica y luego por tiempo
        resultados.sort((a, b) -> {
            int cmp = Integer.compare(
                ORDEN_COMPLEJIDAD.getOrDefault(a.getComplejidad(), 99),
                ORDEN_COMPLEJIDAD.getOrDefault(b.getComplejidad(), 99)
            );
            if (cmp != 0) return cmp;
            return Double.compare(a.getTiempoSegundos(), b.getTiempoSegundos());
        });

        // Crear dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] etiquetas = new String[resultados.size()];
        
        for (int i = 0; i < resultados.size(); i++) {
            String etiqueta = resultados.get(i).getAlgoritmo() + " " + resultados.get(i).getComplejidad();
            etiquetas[i] = etiqueta;
            dataset.addValue(resultados.get(i).getTiempoSegundos(), "Tiempo", etiqueta);
        }

        // Crear grafica
        JFreeChart chart = ChartFactory.createBarChart(
            "Tiempo de Ejecucion por Algoritmo de Ordenamiento",
            "Algoritmo",
            "Tiempo (segundos)",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );

        // Personalizar
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Rotar etiquetas del eje X
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 10));

        // Colores por tiempo
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.8);
        
        for (int i = 0; i < resultados.size(); i++) {
            double tiempo = resultados.get(i).getTiempoSegundos();
            if (tiempo < 0.01) {
                renderer.setSeriesPaint(i, new Color(46, 204, 113)); // Verde
            } else if (tiempo < 0.1) {
                renderer.setSeriesPaint(i, new Color(52, 152, 219)); // Azul
            } else if (tiempo < 0.5) {
                renderer.setSeriesPaint(i, new Color(241, 196, 15)); // Amarillo/naranja
            } else {
                renderer.setSeriesPaint(i, new Color(231, 76, 60)); // Rojo
            }
        }

        // Guardar usando ImageIO
        try {
            BufferedImage image = chart.createBufferedImage(1200, 700);
            ImageIO.write(image, "png", new File(archivo));
            System.out.println("Grafica guardada en " + archivo);
        } catch (IOException e) {
            System.err.println("Error al guardar grafica: " + e.getMessage());
        }
    }

    public void generarGraficaSeries(List<DatoFinanciero> datos, String simboloA, String simboloB, String archivo) {
        TimeSeries seriesA = new TimeSeries(simboloA);
        TimeSeries seriesB = new TimeSeries(simboloB);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Double> preciosA = new java.util.TreeMap<>();
        Map<String, Double> preciosB = new java.util.TreeMap<>();

        for (DatoFinanciero d : datos) {
            if (d.getSimbolo().equals(simboloA)) {
                preciosA.put(d.getFecha(), d.getCierre());
            } else if (d.getSimbolo().equals(simboloB)) {
                preciosB.put(d.getFecha(), d.getCierre());
            }
        }

        for (Map.Entry<String, Double> entry : preciosA.entrySet()) {
            try {
                Day day = new Day(sdf.parse(entry.getKey()));
                seriesA.add(day, entry.getValue());
            } catch (Exception e) {}
        }
        for (Map.Entry<String, Double> entry : preciosB.entrySet()) {
            try {
                Day day = new Day(sdf.parse(entry.getKey()));
                seriesB.add(day, entry.getValue());
            } catch (Exception e) {}
        }

        org.jfree.data.time.TimeSeriesCollection dataset = new org.jfree.data.time.TimeSeriesCollection();
        dataset.addSeries(seriesA);
        dataset.addSeries(seriesB);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Comparacion de Precios: " + simboloA + " vs " + simboloB,
            "Fecha",
            "Precio de Cierre",
            dataset,
            true,
            true,
            false
        );

        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));

        try {
            BufferedImage image = chart.createBufferedImage(1200, 700);
            ImageIO.write(image, "png", new File(archivo));
            System.out.println("Grafica de series guardada en " + archivo);
        } catch (IOException e) {
            System.err.println("Error al guardar grafica de series: " + e.getMessage());
        }
    }
}