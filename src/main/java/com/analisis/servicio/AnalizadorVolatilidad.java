package com.analisis.servicio;

import com.analisis.modelo.ClasificacionRiesgo;
import com.analisis.modelo.DatoFinanciero;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AnalizadorVolatilidad {

    private static final double UMBRAL_CONSERVADOR = 0.15;
    private static final double UMBRAL_MODERADO = 0.30;
    private static final int DIAS_TRADING_ANUAL = 252;

    /**
     * Analiza la volatilidad y clasifica activos por riesgo
     * 
     * COMPLEJIDAD ALGORITMICA:
     * ======================
     * Tiempo: O(s * n) donde:
     *   - s = numero de simbolos
     *   - n = promedio de dias por simbolo
     * Espacio: O(n) por simbolo
     * 
     * METODOS IMPLEMENTADOS:
     * ====================
     * 1. DESVIACION ESTANDAR de precios de cierre
     *    Formula: sqrt(sum(x_i - media)^2 / (n-1))
     * 
     * 2. VOLATILIDAD HISTORICA (anualizada)
     *    Formula: desviacion_estandar * sqrt(dias_trading_anual)
     *    Esta es la metrica clasica de volatilidad en finanzas
     *    Expresa la variabilidad porcentual anualizada de los retornos
     * 
     * CLASIFICACION DE RIESGO:
     * ======================
     * - CONSERVADOR: volatilidad < 15% (menor variacion)
     * - MODERADO: volatilidad entre 15% y 30%
     * - AGRESIVO: volatilidad > 30% (mayor variacion)
     * 
     * Justificacion: La clasificacion se basa en la dispersion de los retornos.
     * Un activo con alta volatilidad tiene mayor riesgo porque sus precios
     * fluctuan mas, ofreciendo tanto mayores ganancias potenciales como
     * mayores perdidas potenciales.
     */
    public List<ClasificacionRiesgo> clasificarPorRiesgo(List<DatoFinanciero> datos) {
        System.out.println("\n=== ETAPA 7: ANALISIS DE VOLATILIDAD Y CLASIFICACION DE RIESGO ===");
        
        Map<String, List<DatoFinanciero>> porSimbolo = agruparPorSimbolo(datos);
        List<ClasificacionRiesgo> clasificaciones = new ArrayList<>();
        
        for (Map.Entry<String, List<DatoFinanciero>> entry : porSimbolo.entrySet()) {
            String simbolo = entry.getKey();
            List<DatoFinanciero> serie = entry.getValue();
            
            double[] retornos = calcularRetornos(serie);
            
            if (retornos.length < 2) {
                continue;
            }
            
            double desviacionEstandar = calcularDesviacionEstandar(retornos);
            double volatilidadHistorica = calcularVolatilidadHistorica(desviacionEstandar);
            ClasificacionRiesgo.CategoriaRiesgo categoria = clasificarRiesgo(volatilidadHistorica);
            
            clasificaciones.add(new ClasificacionRiesgo(
                simbolo, desviacionEstandar, volatilidadHistorica, 
                categoria, retornos.length
            ));
            
            System.out.printf("%s: Desv=%.6f, Volat=%.4f (%s)%n", 
                simbolo, desviacionEstandar, volatilidadHistorica, categoria.getNombre());
        }
        
        Collections.sort(clasificaciones);
        
        System.out.println("\n--- Clasificacion por Riesgo (orden ascendente) ---");
        System.out.println(String.format("%-10s %-20s %-15s %s", 
            "Simbolo", "Desv. Estandar", "Volatilidad", "Categoria"));
        System.out.println("-".repeat(60));
        
        for (ClasificacionRiesgo c : clasificaciones) {
            System.out.printf("%-10s %-20.6f %-15.4f %s%n",
                c.getSimbolo(), c.getDesviacionEstandar(), 
                c.getVolatilidadHistorica(), c.getCategoria().getNombre());
        }
        
        return clasificaciones;
    }

    /**
     * Calcula los retornos diarios
     * 
     * Formula: retorno_i = (cierre_i - cierre_{i-1}) / cierre_{i-1}
     * 
     * @param serie Lista de datos financieros ordenada por fecha
     * @return Array de retornos diarios
     */
    public double[] calcularRetornos(List<DatoFinanciero> serie) {
        if (serie.size() < 2) {
            return new double[0];
        }
        
        double[] retornos = new double[serie.size() - 1];
        
        for (int i = 1; i < serie.size(); i++) {
            double cierreAnterior = serie.get(i - 1).getCierre();
            double cierreActual = serie.get(i).getCierre();
            
            if (cierreAnterior != 0) {
                retornos[i - 1] = (cierreActual - cierreAnterior) / cierreAnterior;
            } else {
                retornos[i - 1] = 0.0;
            }
        }
        
        return retornos;
    }

    /**
     * Calcula la desviacion estandar de los retornos
     * 
     * Formula: sqrt(sum(x_i - media)^2 / (n-1))
     * 
     * Se usa n-1 (estimador muestral) en lugar de n para mayor precision
     * con muestras pequenas.
     * 
     * @param valores Array de retornos
     * @return Desviacion estandar
     */
    public double calcularDesviacionEstandar(double[] valores) {
        if (valores.length < 2) {
            return 0.0;
        }
        
        double media = 0.0;
        for (double v : valores) {
            media += v;
        }
        media /= valores.length;
        
        double sumaCuadrados = 0.0;
        for (double v : valores) {
            double diferencia = v - media;
            sumaCuadrados += diferencia * diferencia;
        }
        
        double varianza = sumaCuadrados / (valores.length - 1);
        return Math.sqrt(varianza);
    }

    /**
     * Calcula la volatilidad historica anualizada
     * 
     * Formula: desviacion_estandar * sqrt(dias_trading_anual)
     * 
     * La volatilidad anualizada es una medida estandar en finanzas que
     * permite comparar activos con diferentes horizontes temporales.
     * Se asume 252 dias de trading por ano (promedio mercado NYSE).
     * 
     * @param desviacionEstandar Desviacion estandar de retornos
     * @return Volatilidad historica anualizada
     */
    public double calcularVolatilidadHistorica(double desviacionEstandar) {
        return desviacionEstandar * Math.sqrt(DIAS_TRADING_ANUAL);
    }

    /**
     * Clasifica el riesgo segun la volatilidad historica
     * 
     * Criterios (ajustables segun el mercado):
     * - CONSERVADOR: volatilidad < 15%
     *   Justificacion: Baja variacion en precios, menor incertidumbre
     * 
     * - MODERADO: volatilidad entre 15% y 30%
     *   Justificacion: Balance entre potencial de retorno y riesgo
     * 
     * - AGRESIVO: volatilidad > 30%
     *   Justificacion: Alta variacion, mayor incertidumbre y riesgo
     * 
     * @param volatilidad Volatilidad historica anualizada
     * @return Categoria de riesgo
     */
    public ClasificacionRiesgo.CategoriaRiesgo clasificarRiesgo(double volatilidad) {
        if (volatilidad < UMBRAL_CONSERVADOR) {
            return ClasificacionRiesgo.CategoriaRiesgo.CONSERVADOR;
        } else if (volatilidad < UMBRAL_MODERADO) {
            return ClasificacionRiesgo.CategoriaRiesgo.MODERADO;
        } else {
            return ClasificacionRiesgo.CategoriaRiesgo.AGRESIVO;
        }
    }

    /**
     * Agrupa datos por simbolo
     */
    private Map<String, List<DatoFinanciero>> agruparPorSimbolo(List<DatoFinanciero> datos) {
        Map<String, List<DatoFinanciero>> porSimbolo = new LinkedHashMap<>();
        
        for (DatoFinanciero d : datos) {
            porSimbolo.computeIfAbsent(d.getSimbolo(), k -> new ArrayList<>()).add(d);
        }
        
        for (List<DatoFinanciero> lista : porSimbolo.values()) {
            lista.sort(Comparator.comparing(DatoFinanciero::getFecha));
        }
        
        return porSimbolo;
    }

    /**
     * Genera reporte de clasificacion por riesgo
     */
    public void guardarReporteRiesgo(List<ClasificacionRiesgo> clasificaciones, String archivo) {
        try (FileWriter fw = new FileWriter(archivo)) {
            if (!clasificaciones.isEmpty()) {
                fw.write(clasificaciones.get(0).toCsvHeader() + "\n");
            }
            
            for (ClasificacionRiesgo c : clasificaciones) {
                fw.write(c.toString() + "\n");
            }
            
            System.out.println("\nClasificacion de riesgo guardada en " + archivo);
        } catch (IOException e) {
            System.err.println("Error al guardar riesgo: " + e.getMessage());
        }
    }

    /**
     * Obtiene resumen de categorias
     */
    public void mostrarResumenCategorias(List<ClasificacionRiesgo> clasificaciones) {
        int conservadores = 0;
        int moderados = 0;
        int agresivos = 0;
        
        for (ClasificacionRiesgo c : clasificaciones) {
            switch (c.getCategoria()) {
                case CONSERVADOR: conservadores++; break;
                case MODERADO: moderados++; break;
                case AGRESIVO: agresivos++; break;
            }
        }
        
        System.out.println("\n--- Resumen de Categorias ---");
        System.out.println("Conservadores: " + conservadores);
        System.out.println("Moderados: " + moderados);
        System.out.println("Agresivos: " + agresivos);
        System.out.println("Total: " + clasificaciones.size());
    }
}