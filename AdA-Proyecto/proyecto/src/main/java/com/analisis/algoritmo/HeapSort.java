package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * HeapSort - Ordenamiento por monticulo
 */
public class HeapSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i);
        }
        
        for (int i = n - 1; i > 0; i--) {
            DatoFinanciero temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            heapify(array, i, 0);
        }
    }
    
    private void heapify(DatoFinanciero[] array, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (left < n && array[left].compareTo(array[largest]) > 0) {
            largest = left;
        }
        
        if (right < n && array[right].compareTo(array[largest]) > 0) {
            largest = right;
        }
        
        if (largest != i) {
            DatoFinanciero swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;
            heapify(array, n, largest);
        }
    }
    
    @Override
    public String getNombre() {
        return "HeapSort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n log n)";
    }
}