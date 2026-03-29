package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * QuickSort - Ordenamiento rapido
 */
public class QuickSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        quickSort(array, 0, array.length - 1);
    }
    
    private void quickSort(DatoFinanciero[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }
    
    private int partition(DatoFinanciero[] array, int low, int high) {
        DatoFinanciero pivot = array[high];
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) < 0) {
                i++;
                DatoFinanciero temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        
        DatoFinanciero temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        
        return i + 1;
    }
    
    @Override
    public String getNombre() {
        return "QuickSort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n log n)";
    }
}