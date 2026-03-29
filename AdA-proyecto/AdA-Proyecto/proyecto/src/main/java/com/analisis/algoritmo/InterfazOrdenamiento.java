package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;

/**
 * Interfaz para algoritmos de ordenamiento
 */
public interface InterfazOrdenamiento {
    void ordenar(DatoFinanciero[] array);
    String getNombre();
    String getComplejidad();
}