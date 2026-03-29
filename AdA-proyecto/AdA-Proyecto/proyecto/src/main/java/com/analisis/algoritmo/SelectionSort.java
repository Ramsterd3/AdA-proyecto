package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * SelectionSort - Ordenamiento por seleccion
 */
public class SelectionSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j].compareTo(array[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            DatoFinanciero temp = array[minIdx];
            array[minIdx] = array[i];
            array[i] = temp;
        }
    }
    
    @Override
    public String getNombre() {
        return "Selection Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n²)";
    }
}