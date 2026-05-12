package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * BinaryInsertionSort - Ordenamiento por insercion binaria
 */
public class BinaryInsertionSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        
        for (int i = 1; i < n; i++) {
            DatoFinanciero key = array[i];
            int pos = busquedaBinaria(array, key, 0, i);
            
            for (int j = i; j > pos; j--) {
                array[j] = array[j - 1];
            }
            array[pos] = key;
        }
    }
    
    private int busquedaBinaria(DatoFinanciero[] array, DatoFinanciero key, int low, int high) {
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (key.compareTo(array[mid]) >= 0) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }
    
    @Override
    public String getNombre() {
        return "Binary Insertion Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n²)";
    }
}