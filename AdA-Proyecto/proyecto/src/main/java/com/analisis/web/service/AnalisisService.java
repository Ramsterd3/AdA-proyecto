package com.analisis.web.service;

import com.analisis.web.model.DatoFinanciero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementa los algoritmos del Requerimiento 4:
 *  - Matriz de correlación de Pearson (O(n) por par)
 *  - Media Móvil Simple (SMA) algoritmo de ventana deslizante
 *  - Clasificación de riesgo por volatilidad histórica
 *
 * NOTA: Todos los algoritmos están implementados de forma explícita
 * sin usar librerías de alto nivel que los encapsulen.
 */
@Service
public class AnalisisService {

    @Autowired
    private CargaDatosService cargaDatos;

    // -----------------------------------------------------------------------
    // 1. MATRIZ DE CORRELACIÓN DE PEARSON
    //    Complejidad: O(s² * n) donde s=símbolos, n=días
    // -----------------------------------------------------------------------

    /**
     * Calcula la matriz de correlación de Pearson entre todos los activos.
     * Retorna un mapa: simbolo -> (simbolo -> correlacion)
     *
     * Fórmula:
     *   r = Σ((xi - x̄)(yi - ȳ)) / sqrt(Σ(xi-x̄)² * Σ(yi-ȳ)²)
     */
    public Map<String, Map<String, Double>> calcularMatrizCorrelacion() {
        List<String> simbolos = cargaDatos.getSimbolos();
        Map<String, double[]> retornos = new LinkedHashMap<>();

        // Calcular retornos logarítmicos diarios por símbolo
        for (String s : simbolos) {
            List<DatoFinanciero> serie = cargaDatos.getDatosPorSimbolo(s);
            retornos.put(s, calcularRetornos(serie));
        }

        // Construir matriz
        Map<String, Map<String, Double>> matriz = new LinkedHashMap<>();
        for (String sA : simbolos) {
            Map<String, Double> fila = new LinkedHashMap<>();
            for (String sB : simbolos) {
                double corr = pearson(retornos.get(sA), retornos.get(sB));
                fila.put(sB, round4(corr));
            }
            matriz.put(sA, fila);
        }
        return matriz;
    }

    /**
     * Correlación de Pearson entre dos vectores.
     * Complejidad: O(n)
     */
    private double pearson(double[] x, double[] y) {
        int n = Math.min(x.length, y.length);
        if (n < 2) return 0.0;

        double sumX = 0, sumY = 0;
        for (int i = 0; i < n; i++) { sumX += x[i]; sumY += y[i]; }
        double mediaX = sumX / n;
        double mediaY = sumY / n;

        double numerador = 0, denX = 0, denY = 0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - mediaX;
            double dy = y[i] - mediaY;
            numerador += dx * dy;
            denX += dx * dx;
            denY += dy * dy;
        }
        double denominador = Math.sqrt(denX * denY);
        return denominador == 0 ? 0.0 : numerador / denominador;
    }

    /**
     * Retornos logarítmicos: ln(P_t / P_{t-1})
     * Complejidad: O(n)
     */
    private double[] calcularRetornos(List<DatoFinanciero> serie) {
        if (serie.size() < 2) return new double[0];
        double[] ret = new double[serie.size() - 1];
        for (int i = 1; i < serie.size(); i++) {
            double anterior = serie.get(i - 1).getCierre();
            double actual   = serie.get(i).getCierre();
            ret[i - 1] = anterior > 0 ? Math.log(actual / anterior) : 0.0;
        }
        return ret;
    }

    // -----------------------------------------------------------------------
    // 2. CANDLESTICK + MEDIA MÓVIL SIMPLE (SMA)
    //    Algoritmo de ventana deslizante — Complejidad: O(n)
    // -----------------------------------------------------------------------

    /**
     * Datos OHLCV para gráfico de velas.
     */
    public List<Map<String, Object>> getCandlestickData(String simbolo, int limite) {
        List<DatoFinanciero> serie = cargaDatos.getDatosPorSimbolo(simbolo);
        int desde = Math.max(0, serie.size() - limite);
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (int i = desde; i < serie.size(); i++) {
            DatoFinanciero d = serie.get(i);
            Map<String, Object> punto = new LinkedHashMap<>();
            punto.put("fecha",    d.getFecha());
            punto.put("apertura", d.getApertura());
            punto.put("maximo",   d.getMaximo());
            punto.put("minimo",   d.getMinimo());
            punto.put("cierre",   d.getCierre());
            punto.put("volumen",  d.getVolumen());
            resultado.add(punto);
        }
        return resultado;
    }

    /**
     * Media Móvil Simple usando ventana deslizante.
     *
     * Algoritmo:
     *   - Mantiene una suma acumulada de la ventana
     *   - Al avanzar: suma += precio_nuevo - precio_saliente
     *   - Promedio = suma / ventana
     *
     * Complejidad: O(n) — sin recalcular la suma completa en cada paso
     *
     * @param simbolo  Símbolo del activo
     * @param ventana  Tamaño de la ventana (días)
     * @param limite   Últimos N días a devolver
     */
    public List<Map<String, Object>> calcularSMA(String simbolo, int ventana, int limite) {
        List<DatoFinanciero> serie = cargaDatos.getDatosPorSimbolo(simbolo);
        List<Map<String, Object>> resultado = new ArrayList<>();

        if (serie.size() < ventana) return resultado;

        // Inicializar suma de la primera ventana
        double suma = 0;
        for (int i = 0; i < ventana; i++) {
            suma += serie.get(i).getCierre();
        }

        // Desde donde empezar a guardar (para respetar el límite de días)
        int desde = Math.max(ventana - 1, serie.size() - limite);

        // Avanzar la ventana deslizante
        for (int i = ventana - 1; i < serie.size(); i++) {
            double sma = suma / ventana;

            if (i >= desde) {
                Map<String, Object> punto = new LinkedHashMap<>();
                punto.put("fecha", serie.get(i).getFecha());
                punto.put("sma",   round4(sma));
                resultado.add(punto);
            }

            // Deslizar: quitar el más antiguo, agregar el siguiente
            if (i + 1 < serie.size()) {
                suma -= serie.get(i - ventana + 1).getCierre();
                suma += serie.get(i + 1).getCierre();
            }
        }
        return resultado;
    }

    // -----------------------------------------------------------------------
    // 3. CLASIFICACIÓN POR VOLATILIDAD HISTÓRICA
    //    Complejidad: O(s * n)
    // -----------------------------------------------------------------------

    /**
     * Calcula volatilidad histórica anualizada y clasifica activos.
     *
     * Fórmula:
     *   volatilidad = desv_estándar(retornos) * sqrt(252)
     *
     * Umbrales:
     *   < 15%  → CONSERVADOR
     *   15-30% → MODERADO
     *   > 30%  → AGRESIVO
     */
    public List<Map<String, Object>> getClasificacionRiesgo() {
        List<String> simbolos = cargaDatos.getSimbolos();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (String s : simbolos) {
            List<DatoFinanciero> serie = cargaDatos.getDatosPorSimbolo(s);
            double[] retornos = calcularRetornos(serie);
            if (retornos.length < 2) continue;

            double desv = desviacionEstandar(retornos);
            double volatilidad = desv * Math.sqrt(252);

            String categoria;
            if (volatilidad < 0.15)      categoria = "CONSERVADOR";
            else if (volatilidad < 0.30) categoria = "MODERADO";
            else                          categoria = "AGRESIVO";

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("simbolo",     s);
            item.put("volatilidad", round4(volatilidad * 100)); // en %
            item.put("categoria",   categoria);
            resultado.add(item);
        }

        // Ordenar de mayor a menor volatilidad
        resultado.sort((a, b) -> Double.compare(
            (Double) b.get("volatilidad"), (Double) a.get("volatilidad")));

        return resultado;
    }

    /**
     * Desviación estándar muestral. Complejidad: O(n)
     */
    private double desviacionEstandar(double[] datos) {
        int n = datos.length;
        double suma = 0;
        for (double v : datos) suma += v;
        double media = suma / n;

        double varianza = 0;
        for (double v : datos) {
            double diff = v - media;
            varianza += diff * diff;
        }
        return Math.sqrt(varianza / (n - 1));
    }

    // -----------------------------------------------------------------------
    // Utilidades
    // -----------------------------------------------------------------------
    private double round4(double v) { return Math.round(v * 10000.0) / 10000.0; }

    public List<String> getSimbolos() { return cargaDatos.getSimbolos(); }

    public Map<String, Object> getResumenActivo(String simbolo) {
        List<DatoFinanciero> serie = cargaDatos.getDatosPorSimbolo(simbolo);
        if (serie.isEmpty()) return Collections.emptyMap();

        DatoFinanciero ultimo = serie.get(serie.size() - 1);
        DatoFinanciero primero = serie.get(0);
        double[] retornos = calcularRetornos(serie);

        double volatilidad = retornos.length > 1
            ? desviacionEstandar(retornos) * Math.sqrt(252) * 100 : 0;

        double retornoTotal = primero.getCierre() > 0
            ? (ultimo.getCierre() - primero.getCierre()) / primero.getCierre() * 100 : 0;

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("simbolo",       simbolo);
        res.put("ultimaFecha",   ultimo.getFecha());
        res.put("ultimoCierre",  round4(ultimo.getCierre()));
        res.put("volatilidad",   round4(volatilidad));
        res.put("retornoTotal",  round4(retornoTotal));
        res.put("diasDatos",     serie.size());
        return res;
    }
}
