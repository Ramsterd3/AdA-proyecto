package com.analisis.similitud;

/**
 * Correlacion de Pearson entre dos series de tiempo.
 * 
 * DESCRIPCION MATEMATICA:
 * ===================
 * Formula: r = sum((a_i - a_media)(b_i - b_media)) / (n * sigma_a * sigma_b)
 * 
 * Equivalentemente: r = sum((a_i - a_media)(b_i - b_media)) / sqrt(sum(a_i-a_media)^2 * sum(b_i-b_media)^2)
 * 
 * donde:
 * - a_media, b_media = promedio de cada serie
 * - sigma_a, sigma_b = desviacion estandar
 * - n = cantidad de elementos
 * 
 * INTERPRETACION:
 * - r = 1: correlacion perfecta positiva (series suben/jueben juntas)
 * - r = -1: correlacion perfecta negativa (una sube, otra baja)
 * - r = 0: sin correlacion lineal
 * 
 * DESCRIPCION ALGORITMICA:
 * =================
 * 1. Calcular promedio de A y B (O(n))
 * 2. Para cada i: numerador += (a_i-mediaA)(b_i-mediaB)
 * 3. Para cada i: denomA += (a_i-mediaA)^2, denomB += (b_i-mediaB)^2
 * 4. denominador = sqrt(denomA * denomB)
 * 5. Retornar numerador / denominador
 * 
 * COMPLEJIDAD COMPUTACIONAL:
 * =======================
 * Tiempo: O(n) - dos pasadas sobre los datos
 * Espacio: O(1) - variables acumuladoras
 * 
 * COMPARACION CON OTROS METODOS:
 * ======================
 * vs Euclidiana: Pearson mide RELACION lineal, Euclidiana mide DISTANCIA
 * vs DTW: Pearson es O(n), DTW es O(n*m) mas costoso
 * vs Coseno: Pearson centra datos (restamedia), Coseno no
 * 
 * CUANDO USAR:
 * ==========
 * - Detectar si dos activos se mueven juntos
 * - Analisis de riesgo (diversificacion)
 * - Series de igual longitud
 */
public class CorrelacionPearson implements InterfazSimilitud {

    @Override
    public double calcular(double[] serieA, double[] serieB) {
        int n = Math.min(serieA.length, serieB.length);
        if (n == 0) return 0.0;

        // Paso 1: Calcular promedios
        double mediaA = 0.0, mediaB = 0.0;
        for (int i = 0; i < n; i++) {
            mediaA += serieA[i];
            mediaB += serieB[i];
        }
        mediaA /= n;
        mediaB /= n;

        // Paso 2 y 3: Calcular numerador y denominadores
        double numerador = 0.0, denomA = 0.0, denomB = 0.0;
        for (int i = 0; i < n; i++) {
            double da = serieA[i] - mediaA;
            double db = serieB[i] - mediaB;
            numerador += da * db;
            denomA += da * da;
            denomB += db * db;
        }

        double denominador = Math.sqrt(denomA * denomB);
        if (denominador == 0.0) return 0.0;
        return numerador / denominador;
    }

    @Override
    public String getNombre() { return "Correlacion de Pearson"; }

    @Override
    public String getComplejidad() { return "O(n)"; }

    @Override
    public String getDescripcion() {
        return "Formula: r = sum((a_i-avg)(b_i-avg))/(n*desv). "
             + "Mide relacion lineal. "
             + "Rango [-1,1]: 1=positiva, -1=negativa.";
    }
}