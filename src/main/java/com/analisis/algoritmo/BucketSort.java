package com.analisis.algoritmo;

import com.analisis.modelo.DatoFinanciero;
import java.util.ArrayList;
import java.util.Collections;

/**
 * BucketSort - Ordenamiento por cubetas
 */
public class BucketSort implements InterfazOrdenamiento {
    
    @Override
    public void ordenar(DatoFinanciero[] array) {
        int n = array.length;
        if (n == 0) return;
        
        double min = array[0].getCierre();
        double max = array[0].getCierre();
        for (int i = 1; i < n; i++) {
            double val = array[i].getCierre();
            if (val < min) min = val;
            if (val > max) max = val;
        }
        
        int bucketCount = n;
        @SuppressWarnings("unchecked")
        ArrayList<DatoFinanciero>[] buckets = new ArrayList[bucketCount];
        
        for (int i = 0; i < bucketCount; i++) {
            buckets[i] = new ArrayList<>();
        }
        
        double range = max - min;
        if (range == 0) range = 1;
        
        for (DatoFinanciero d : array) {
            int bucketIdx = (int) ((d.getCierre() - min) / range * (bucketCount - 1));
            buckets[bucketIdx].add(d);
        }
        
        for (ArrayList<DatoFinanciero> bucket : buckets) {
            Collections.sort(bucket);
        }
        
        int index = 0;
        for (ArrayList<DatoFinanciero> bucket : buckets) {
            for (DatoFinanciero d : bucket) {
                array[index++] = d;
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "Bucket Sort";
    }
    
    @Override
    public String getComplejidad() {
        return "O(n + k)";
    }
}