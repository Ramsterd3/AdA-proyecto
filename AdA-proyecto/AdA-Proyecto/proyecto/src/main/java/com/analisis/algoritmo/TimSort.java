package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * TimSort - Utiliza Collections.sort de Java (implementa TimSort)
 */
public class TimSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        java.util.Arrays.sort(array);
    }
    
    @Override
    public String getNombre() {
        return "TimSort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n log n)";
    }
}