package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * PigeonholeSort - Ordenamiento por casillas
 */
public class PigeonholeSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        if (n == 0) return;
        
        // Convertir a array de indices para ordenar por cierre
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        
        // Encontrar min y max
        double min = array[0].getCierre();
        double max = array[0].getCierre();
        for (int i = 1; i < n; i++) {
            double val = array[i].getCierre();
            if (val < min) min = val;
            if (val > max) max = val;
        }
        
        int range = (int)(max - min) + 1;
        if (range <= 0) return;
        
        // Crear pigeonholes
        @SuppressWarnings("unchecked")
        java.util.List<Integer>[] holes = new java.util.ArrayList[range];
        for (int i = 0; i < range; i++) {
            holes[i] = new java.util.ArrayList<>();
        }
        
        // Distribuir
        for (int i = 0; i < n; i++) {
            int idx = (int)(array[i].getCierre() - min);
            holes[idx].add(i);
        }
        
        // Recolectar
        int pos = 0;
        for (int i = 0; i < range; i++) {
            for (int idx : holes[i]) {
                array[pos++] = array[idx];
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "Pigeonhole Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n + k)";
    }
}