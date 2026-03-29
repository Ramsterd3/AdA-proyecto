package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * GnomeSort - Ordenamiento gnomo
 */
public class GnomeSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        int index = 0;
        
        while (index < n) {
            if (index == 0) {
                index++;
            }
            
            if (array[index].compareTo(array[index - 1]) >= 0) {
                index++;
            } else {
                DatoFinanciero temp = array[index];
                array[index] = array[index - 1];
                array[index - 1] = temp;
                index--;
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "Gnome Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n²)";
    }
}