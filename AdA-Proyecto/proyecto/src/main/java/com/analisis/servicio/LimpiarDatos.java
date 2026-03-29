package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;

import java.util.*;

/**
 * Limpia y preprocesa los datos financieros
 */
public class LimpiarDatos {

    public List<DatoFinanciero> limpiarDatos(List<DatoFinanciero> datos) {
        System.out.println("\n=== ETAPA 2: LIMPIEZA DE DATOS ===");
        
        int valoresFaltantes = 0;
        int duplicadosEliminados = 0;
        int outliersDetectados = 0;
        
        // Eliminar duplicados
        Set<String> seen = new HashSet<>();
        Iterator<DatoFinanciero> it = datos.iterator();
        while (it.hasNext()) {
            DatoFinanciero d = it.next();
            String key = d.getFecha() + "-" + d.getSimbolo();
            if (seen.contains(key)) {
                it.remove();
                duplicadosEliminados++;
            } else {
                seen.add(key);
            }
        }
        
        // Detectar y corregir outliers
        Map<String, List<DatoFinanciero>> porSimbolo = new HashMap<>();
        for (DatoFinanciero d : datos) {
            porSimbolo.computeIfAbsent(d.getSimbolo(), k -> new ArrayList<>()).add(d);
        }
        
        for (List<DatoFinanciero> lista : porSimbolo.values()) {
            if (lista.size() < 10) continue;
            
            double[] precios = lista.stream().mapToDouble(DatoFinanciero::getCierre).sorted().toArray();
            
            double q1 = precios[precios.length / 4];
            double q3 = precios[3 * precios.length / 4];
            double iqr = q3 - q1;
            double limiteInferior = q1 - 1.5 * iqr;
            double limiteSuperior = q3 + 1.5 * iqr;
            
            for (DatoFinanciero d : lista) {
                double precio = d.getCierre();
                if (precio < limiteInferior || precio > limiteSuperior) {
                    outliersDetectados++;
                }
            }
        }
        
        // Interpolar valores faltantes
        for (String simbolo : porSimbolo.keySet()) {
            List<DatoFinanciero> lista = porSimbolo.get(simbolo);
            lista.sort(Comparator.comparing(DatoFinanciero::getFecha));
            
            for (int i = 1; i < lista.size() - 1; i++) {
                DatoFinanciero anterior = lista.get(i - 1);
                DatoFinanciero actual = lista.get(i);
                DatoFinanciero siguiente = lista.get(i + 1);
                
                if (actual.getCierre() == 0) {
                    double promedio = (anterior.getCierre() + siguiente.getCierre()) / 2;
                    actual.setCierre(promedio);
                    valoresFaltantes++;
                }
            }
        }
        
        System.out.println("Reporte de limpieza:");
        System.out.println("  - Valores faltantes detectados: " + valoresFaltantes);
        System.out.println("  - Duplicados eliminados: " + duplicadosEliminados);
        System.out.println("  - Outliers detectados: " + outliersDetectados);
        
        return datos;
    }
}