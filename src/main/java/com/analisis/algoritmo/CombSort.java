package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * CombSort - Ordenamiento por peines
 */
public class CombSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        double factor = 1.2473309;
        int gap = n;
        
        while (gap > 1) {
            gap = (int) (gap / factor);
            if (gap < 1) gap = 1;
            
            for (int i = 0; i + gap < n; i++) {
                if (array[i].compareTo(array[i + gap]) > 0) {
                    DatoFinanciero temp = array[i];
                    array[i] = array[i + gap];
                    array[i + gap] = temp;
                }
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "Comb Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n²)";
    }
}