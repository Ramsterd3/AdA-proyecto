package com.analisis.similitud;

/**
 * Dynamic Time Warping (DTW) entre dos series de tiempo.
 * 
 * DESCRIPCION MATEMATICA:
 * ===================
 * DTW encuentra elalineacion optima entre dos series de tiempo
 * que pueden diferir en velocidad o fase.
 * 
 * Formula recursiva:
 * dtw[i][j] = |a_i - b_j| + min(dtw[i-1][j], dtw[i][j-1], dtw[i-1][j-1])
 * 
 * donde:
 * - a_i, b_j = elementos en posicion i y j
 * - Matriz dtw de tamano (n+1) x (m+1)
 * 
 * INTERPRETACION:
 * - Valor 0 = series identicas
 * - Mayor valor = mas diferentes
 * - Permite series de diferente longitud
 * 
 * DESCRIPCION ALGORITMICA:
 * =================
 * 1. Crear matriz dtw[(n+1)][(m+1)] inicializada con infinito
 * 2. dtw[0][0] = 0
 * 3. Para i = 1 hasta n:
 *    Para j = 1 hasta m:
 *       costo = |A[i-1] - B[j-1]|
 *       dtw[i][j] = costo + min(dtw[i-1][j], dtw[i][j-1], dtw[i-1][j-1])
 * 4. Retornar dtw[n][m]
 * 
 * COMPLEJIDAD COMPUTACIONAL:
 * =======================
 * Tiempo: O(n * m) donde n,m = longitud de las series
 * Espacio: O(n * m) para la matriz
 * 
 * COMPARACION CON OTROS METODOS:
 * ======================
 * vs Euclidiana: DTW es O(n*m) mas costoso pero toleradesplazamientos
 * vs Pearson: DTW no asume alineacion temporal correcta
 * vs Coseno: DTW puede manejar series de diferente longitud
 * 
 * CUANDO USAR:
 * ==========
 * - Series de diferente longitud
 * - Cuando hay delays o desplazamientos entre series
 * - Analisis de patrones con fase variable
 * - Advertencia: Costoso computacionalmente para series largas
 */
public class DTW implements InterfazSimilitud {

    @Override
    public double calcular(double[] serieA, double[] serieB) {
        int n = serieA.length;
        int m = serieB.length;

        if (n == 0 || m == 0) return 0.0;

        double[][] dtw = new double[n + 1][m + 1];

        for (int i = 0; i <= n; i++)
            for (int j = 0; j <= m; j++)
                dtw[i][j] = Double.MAX_VALUE;

        dtw[0][0] = 0.0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                double costo = Math.abs(serieA[i - 1] - serieB[j - 1]);
                double minPrev = Math.min(dtw[i - 1][j],
                                 Math.min(dtw[i][j - 1], dtw[i - 1][j - 1]));
                dtw[i][j] = costo + minPrev;
            }
        }

        return dtw[n][m];
    }

    @Override
    public String getNombre() { return "Dynamic Time Warping (DTW)"; }

    @Override
    public String getComplejidad() { return "O(n*m)"; }

    @Override
    public String getDescripcion() {
        return "Algoritmo de programaciondinamica. "
             + "Alinea series de diferente longitud. "
             + "0=identicas, mayor=mas diferentes. "
             + "Caro para series largas.";
    }
}