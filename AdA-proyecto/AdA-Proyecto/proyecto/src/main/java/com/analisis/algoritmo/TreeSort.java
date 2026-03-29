package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;
import java.util.TreeSet;

/**
 * TreeSort - Ordenamiento usando TreeSet
 */
public class TreeSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        TreeSet<DatoFinanciero> tree = new TreeSet<>();
        for (DatoFinanciero d : array) {
            tree.add(d);
        }
        int i = 0;
        for (DatoFinanciero d : tree) {
            array[i++] = d;
        }
    }
    
    @Override
    public String getNombre() {
        return "Tree Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n log n)";
    }
}