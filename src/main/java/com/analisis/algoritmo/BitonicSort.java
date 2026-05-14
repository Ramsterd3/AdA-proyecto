package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * BitonicSort - Ordenamiento bitonico (requiere potencia de 2)
 */
public class BitonicSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        
        if (n == 0) return;
        
        // Encontrar la siguiente potencia de 2
        int size = 1;
        while (size < n) size *= 2;
        
        // Crear array temporal con potencia de 2
        DatoFinanciero[] temp = new DatoFinanciero[size];
        System.arraycopy(array, 0, temp, 0, n);
        
        // Llenar con valores maximos al final
        DatoFinanciero maxVal = array[n - 1];
        for (int i = n; i < size; i++) {
            temp[i] = maxVal;
        }
        
        bitonicSort(temp, size, 1, true);
        
        // Copiar de vuelta
        System.arraycopy(temp, 0, array, 0, n);
    }
    
    private void bitonicSort(DatoFinanciero[] array, int size, int j, boolean asc) {
        if (j >= size) return;
        
        for (int k = j; k < size; k *= 2) {
            for (int i = 0; i < size; i += k * 2) {
                boolean dir = asc;
                for (int l = i; l < i + k; l++) {
                    if (dir) {
                        if (array[l].compareTo(array[l + k]) > 0) {
                            swap(array, l, l + k);
                        }
                    } else {
                        if (array[l].compareTo(array[l + k]) < 0) {
                            swap(array, l, l + k);
                        }
                    }
                }
            }
            j *= 2;
        }
    }
    
    private void swap(DatoFinanciero[] array, int i, int j) {
        DatoFinanciero temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    @Override
    public String getNombre() {
        return "Bitonic Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(log² n)";
    }
}