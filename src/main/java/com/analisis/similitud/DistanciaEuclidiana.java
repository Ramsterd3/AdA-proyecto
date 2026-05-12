package com.analisis.similitud;

/**
 * Distancia Euclidiana entre dos series de tiempo.
 * 
 * DESCRIPCION MATEMATICA:
 * ===================
 * Formula: d(A,B) = sqrt( sum( (a_i - b_i)^2 ) )
 * 
 * donde:
 * - a_i, b_i = elementos de las series A y B en posicion i
 * - n = longitud minima de las series
 * 
 * INTERPRETACION:
 * - Valor 0 = series identicas en todas las posiciones
 * - Mayor valor = mayor distancia geometricaentre series
 * - Sensible a diferencias absolutas punto a punto
 * 
 * DESCRIPCION ALGORITMICA:
 * =================
 * 1. Obtener longitud n = min(|A|, |B|)
 * 2. Inicializar suma = 0
 * 3. Para i = 0 hasta n-1:
 *    diferencia = A[i] - B[i]
 *    suma += diferencia^2
 * 4. Retornar sqrt(suma)
 * 
 * COMPLEJIDAD COMPUTACIONAL:
 * =======================
 * Tiempo: O(n) donde n = longitud de las series
 * Espacio: O(1) solo variable acumuladora
 * 
 * COMPARACION CON OTROS METODOS:
 * ======================
 * vs Pearson: Euclidiana mide distancia absoluta, Pearson mide correlacion lineal
 * vs DTW: Euclidiana es mas rapida pero no tolera desplazamientos temporales
 * vs Coseno: Euclidiana es sensible a magnitud, Coseno solo a direccion
 * 
 * CUANDO USAR:
 * ==========
 * - Series de igual longitud
 * - Cuandoimporta la magnitud de diferencias
 * - Analisis rapido (O(n) vs O(n*m) de DTW)
 */
public class DistanciaEuclidiana implements InterfazSimilitud {

    @Override
    public double calcular(double[] serieA, double[] serieB) {
        int n = Math.min(serieA.length, serieB.length);
        if (n == 0) return 0.0;
        
        double suma = 0.0;
        for (int i = 0; i < n; i++) {
            double diff = serieA[i] - serieB[i];
            suma += diff * diff;
        }
        return Math.sqrt(suma);
    }

    @Override
    public String getNombre() { return "Distancia Euclidiana"; }

    @Override
    public String getComplejidad() { return "O(n)"; }

    @Override
    public String getDescripcion() {
        return "Formula: sqrt(sum(a_i - b_i)^2). "
             + "Mide distancia geometrica. "
             + "0=identicas, mayor=mas diferentes.";
    }
}