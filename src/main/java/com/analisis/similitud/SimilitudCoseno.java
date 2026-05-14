package com.analisis.similitud;

/**
 * Similitud por Coseno entre dos series de tiempo.
 * 
 * DESCRIPCION MATEMATICA:
 * ===================
 * Formula: cos(theta) = (A · B) / (||A|| * ||B||)
 * 
 * Equivalentemente:
 * cos(theta) = sum(a_i * b_i) / (sqrt(sum(a_i^2)) * sqrt(sum(b_i^2)))
 * 
 * donde:
 * - A · B = producto punto (dot product)
 * - ||A|| = norma euclidiana de A
 * - theta = angulo entre los vectores
 * 
 * INTERPRETACION:
 * - cos(theta) = 1: vectores en misma direccion (paralelos)
 * - cos(theta) = 0: vectores ortogonales (perpendiculares)
 * - cos(theta) = -1: vectores en direccion opuesta
 * - Rango: [-1, 1]
 * 
 * DESCRIPCION ALGORITMICA:
 * =================
 * 1. Calcular producto punto: sum(a_i * b_i)
 * 2. Calcular norma A: sqrt(sum(a_i^2))
 * 3. Calcular norma B: sqrt(sum(b_i^2))
 * 4. Retornar producto_punto / (normaA * normaB)
 * 
 * COMPLEJIDAD COMPUTACIONAL:
 * =======================
 * Tiempo: O(n) - una sola pasada
 * Espacio: O(1) - variables acumuladoras
 * 
 * COMPARACION CON OTROS METODOS:
 * ======================
 * vs Euclidiana: Coseno es INSENSIBLE a magnitud, solo direccion
 * vs Pearson: Coseno no centra datos (no restamedia)
 * vs DTW: Coseno es O(n), DTW es O(n*m)
 * 
 * CUANDO USAR:
 * ==========
 * - Comparar direccion de movimientos (no magnitud)
 * - Datos con diferentes escalas
 * - Analisis rapido
 * - NO usar si importan valores absolutos
 */
public class SimilitudCoseno implements InterfazSimilitud {

    @Override
    public double calcular(double[] serieA, double[] serieB) {
        int n = Math.min(serieA.length, serieB.length);
        if (n == 0) return 0.0;

        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < n; i++) {
            dotProduct += serieA[i] * serieB[i];
            normA += serieA[i] * serieA[i];
            normB += serieB[i] * serieB[i];
        }

        double denominador = Math.sqrt(normA) * Math.sqrt(normB);
        if (denominador == 0.0) return 0.0;
        return dotProduct / denominador;
    }

    @Override
    public String getNombre() { return "Similitud por Coseno"; }

    @Override
    public String getComplejidad() { return "O(n)"; }

    @Override
    public String getDescripcion() {
        return "Formula: cos = (A·B)/(|A||B|). "
             + "Mide angulo entre vectores. "
             + "Rango [-1,1]: 1=misma direccion, -1=opuesta.";
    }
}