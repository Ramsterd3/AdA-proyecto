package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * RadixSort - Ordenamiento por radicacion
 */
public class RadixSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        if (n == 0) return;
        
        // Convertir precios a longs para ordenamiento
        long[] valores = new long[n];
        for (int i = 0; i < n; i++) {
            valores[i] = (long) (array[i].getCierre() * 100);
        }
        
        long max = valores[0];
        for (int i = 1; i < n; i++) {
            if (valores[i] > max) max = valores[i];
        }
        
        for (long exp = 1; max / exp > 0; exp *= 10) {
            countingSort(valores, array, exp);
        }
    }
    
    private void countingSort(long[] valores, DatoFinanciero[] array, long exp) {
        int n = valores.length;
        long[] output = new long[n];
        int[] count = new int[10];
        
        for (int i = 0; i < n; i++) {
            count[(int) ((valores[i] / exp) % 10)]++;
        }
        
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }
        
        for (int i = n - 1; i >= 0; i--) {
            output[count[(int) ((valores[i] / exp) % 10)] - 1] = valores[i];
            count[(int) ((valores[i] / exp) % 10)]--;
        }
        
        for (int i = 0; i < n; i++) {
            valores[i] = output[i];
        }
    }
    
    @Override
    public String getNombre() {
        return "Radix Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(nk)";
    }
}