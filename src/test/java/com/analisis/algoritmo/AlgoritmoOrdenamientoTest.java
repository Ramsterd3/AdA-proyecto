package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlgoritmoOrdenamientoTest {

    @Test
    public void testQuickSort() {
        DatoFinanciero[] datos = {
            new DatoFinanciero("2024-01-03", "AAPL", 103.0, 105.0, 101.0, 104.0, 1000),
            new DatoFinanciero("2024-01-01", "AAPL", 100.0, 102.0, 99.0, 101.0, 1000),
            new DatoFinanciero("2024-01-02", "AAPL", 101.0, 103.0, 100.0, 102.0, 1000)
        };
        
        QuickSort qs = new QuickSort();
        qs.ordenar(datos);
        
        assertEquals("2024-01-01", datos[0].getFecha());
        assertEquals("2024-01-03", datos[2].getFecha());
    }

    @Test
    public void testHeapSort() {
        DatoFinanciero[] datos = {
            new DatoFinanciero("2024-01-03", "AAPL", 103.0, 105.0, 101.0, 104.0, 1000),
            new DatoFinanciero("2024-01-01", "AAPL", 100.0, 102.0, 99.0, 101.0, 1000),
            new DatoFinanciero("2024-01-02", "AAPL", 101.0, 103.0, 100.0, 102.0, 1000)
        };
        
        HeapSort hs = new HeapSort();
        hs.ordenar(datos);
        
        assertEquals("2024-01-01", datos[0].getFecha());
    }

    @Test
    public void testMergeSort() {
        DatoFinanciero[] datos = {
            new DatoFinanciero("2024-01-03", "AAPL", 103.0, 105.0, 101.0, 104.0, 1000),
            new DatoFinanciero("2024-01-01", "AAPL", 100.0, 102.0, 99.0, 101.0, 1000)
        };
        
        TimSort ts = new TimSort();
        ts.ordenar(datos);
        
        assertEquals("2024-01-01", datos[0].getFecha());
    }

    @Test
    public void testSelectionSort() {
        DatoFinanciero[] datos = {
            new DatoFinanciero("2024-01-03", "AAPL", 103.0, 105.0, 101.0, 104.0, 1000),
            new DatoFinanciero("2024-01-01", "AAPL", 100.0, 102.0, 99.0, 101.0, 1000)
        };
        
        SelectionSort ss = new SelectionSort();
        ss.ordenar(datos);
        
        assertEquals("2024-01-01", datos[0].getFecha());
    }

    @Test
    public void testOrdenamientoVacio() {
        DatoFinanciero[] datos = {};
        
        QuickSort qs = new QuickSort();
        qs.ordenar(datos);
        
        assertEquals(0, datos.length);
    }

    @Test
    public void testOrdenamientoUnElemento() {
        DatoFinanciero[] datos = {
            new DatoFinanciero("2024-01-01", "AAPL", 100.0, 102.0, 99.0, 101.0, 1000)
        };
        
        QuickSort qs = new QuickSort();
        qs.ordenar(datos);
        
        assertEquals("2024-01-01", datos[0].getFecha());
    }

    @Test
    public void testYAOrdenado() {
        DatoFinanciero[] datos = {
            new DatoFinanciero("2024-01-01", "AAPL", 100.0, 102.0, 99.0, 101.0, 1000),
            new DatoFinanciero("2024-01-02", "AAPL", 101.0, 103.0, 100.0, 102.0, 1000)
        };
        
        QuickSort qs = new QuickSort();
        qs.ordenar(datos);
        
        assertEquals("2024-01-01", datos[0].getFecha());
        assertEquals("2024-01-02", datos[1].getFecha());
    }

    @Test
    public void testComplejidades() {
        QuickSort qs = new QuickSort();
        HeapSort hs = new HeapSort();
        SelectionSort ss = new SelectionSort();
        TimSort ts = new TimSort();

        assertTrue(qs.getComplejidad().contains("n log n"));
        assertTrue(hs.getComplejidad().contains("n log n"));
        assertTrue(ss.getComplejidad().contains("n"));
        assertTrue(ts.getComplejidad().contains("n log n"));
    }
}