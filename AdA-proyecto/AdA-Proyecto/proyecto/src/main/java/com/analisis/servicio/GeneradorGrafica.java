package com.analisis.servicio;

import com.analisis.modelo.ResultadoOrdenamiento;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
}