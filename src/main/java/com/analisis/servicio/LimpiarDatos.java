package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Limpia y preprocesa los datos financieros.
 * 
 * PROCESO ETL - ETAPA 2: TRANSFORM
 * ========================================
 * Este modulo implementa las siguientes transformaciones:
 * 
 * 1. ELIMINACION DE DUPLICADOS
 *    - Complejidad: O(n) usando HashSet
 *    - Justificacion:Registros duplicados sesgan analisis posteriores
 *    - Impacto: Evita sobreponderacion de precios en mismos instantes
 * 
 * 2. DETECCION DE OUTLIERS
 *    - Metodo: Rango Intercuartil (IQR)
 *    - Formula: [Q1 - 1.5*IQR, Q3 + 1.5*IQR]
 *    - Justificacion: Valores extremos distorsionan estadisticas
 *    - Impacto: Solo se detection, no se eliminan (analisis de sensibilidad)
 * 
 * 3. INTERPOLACION DE VALORES FALTANTES
 *    - Metodo: Forward/Backward fill o interpolacion lineal
 *    - Justificacion:Series temporales requieren continuidad
 *    - Impacto: Interpolacion lineal mantiene tendencia original,
 *             Forward fill es conservador, Backward fill usa valor reciente
 */
public class LimpiarDatos {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final List<String> logDecisiones = new ArrayList<>();

    public List<DatoFinanciero> limpiarDatos(List<DatoFinanciero> datos) {
        System.out.println("\n=== ETAPA 2: LIMPIEZA DE DATOS ===");
        
        logDecisiones.clear();
        logDecisiones.add("=== REPORTE DE LIMPIEZA ETL ===");
        logDecisiones.add("Registros iniciales: " + datos.size());
        
        int duplicadosEliminados = eliminarDuplicados(datos);
        Map<String, List<DatoFinanciero>> porSimbolo = agruparPorSimbolo(datos);
        int outliersDetectados = detectarOutliers(porSimbolo);
        int valoresFaltantes = interpolarValoresFaltantes(porSimbolo);
        
        datos.clear();
        for (List<DatoFinanciero> lista : porSimbolo.values()) {
            datos.addAll(lista);
        }
        
        datos.sort(Comparator.comparing(DatoFinanciero::getFecha));
        
        System.out.println("\nReporte de limpieza:");
        System.out.println("  - Duplicados eliminados: " + duplicadosEliminados);
        System.out.println("  - Outliers detectados: " + outliersDetectados);
        System.out.println("  - Valores interpolados: " + valoresFaltantes);
        System.out.println("  - Registros finales: " + datos.size());
        
        logDecisiones.add("Registros finales: " + datos.size());
        
        return datos;
    }
    
    public List<String> getLogDecisiones() {
        return new ArrayList<>(logDecisiones);
    }

    /**
     * ELIMINACION DE DUPLICADOS
     * =====================
     * Complejidad: O(n)
     * Estructura: HashSet con clave compuesta "fecha-simbolo"
     * 
     * IMPACTO ALGORITMICO:
     * - Sin deduplicacion: misma instancia cotizada cuenta multiples veces
     * - Impacto en analisis: Infl artificial de volumen, promedio sesgado
     * - Impacto en ordenamiento: Multiples registros identicos causan inefciencia
     * 
     * @param datos Lista de datos a limpiar
     * @return cantidad de duplicados eliminados
     */
    private int eliminarDuplicados(List<DatoFinanciero> datos) {
        int eliminados = 0;
        Set<String> seen = new HashSet<>();
        Iterator<DatoFinanciero> it = datos.iterator();
        while (it.hasNext()) {
            DatoFinanciero d = it.next();
            String key = d.getFecha() + "-" + d.getSimbolo();
            if (seen.contains(key)) {
                it.remove();
                eliminados++;
            } else {
                seen.add(key);
            }
        }
        return eliminados;
    }

    private Map<String, List<DatoFinanciero>> agruparPorSimbolo(List<DatoFinanciero> datos) {
        Map<String, List<DatoFinanciero>> porSimbolo = new HashMap<>();
        for (DatoFinanciero d : datos) {
            porSimbolo.computeIfAbsent(d.getSimbolo(), k -> new ArrayList<>()).add(d);
        }
        return porSimbolo;
    }

    /**
     * DETECCION DE OUTLIERS
     * ====================
     * Metodo: Rango Intercuartil (IQR) - метodo estadistico cl?sico
     * Complejidad: O(n log n) por el ordenamiento
     * 
     * IMPACTO ALGORITMICO:
     * - Valor muy alto: Error de captura o evento exceptional
     * - Valor muy bajo: Dato faltante (0) o error
     * - Por que no eliminar: El outlier puede serinformacion real
     * - Decision: Solo reportar, no modificar (analisis de sensibilidad)
     * 
     * Formula: outlier si precio < Q1 - 1.5*IQR o > Q3 + 1.5*IQR
     * Donde IQR = Q3 - Q1 (rango intercuartil)
     * 
     * @param porSimbolo Mapa de datos agrupados por simbolo
     * @return cantidad de outliers detectados
     */
    private int detectarOutliers(Map<String, List<DatoFinanciero>> porSimbolo) {
        int outliersDetectados = 0;
        
        for (List<DatoFinanciero> lista : porSimbolo.values()) {
            if (lista.size() < 10) continue;
            
            double[] precios = lista.stream()
                .mapToDouble(DatoFinanciero::getCierre)
                .sorted()
                .toArray();
            
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
        return outliersDetectados;
    }

    /**
     * INTERPOLACION DE VALORES FALTANTES
     * ============================
     * Complejidad: O(n) recorrida + O(n) busqueda = O(n^2) worst case
     * 
     * ESTRATEGIA DE INTERPOLACION:
     * 1. Interpolacion lineal: (anterior + siguiente) / 2
     *    - Justificacion: Mantiene continuidad de la serie temporal
     *    - Impacto: Suaviza transiciones abruptas
     * 
     * 2. Forward Fill: usar valor anterior
     *    - Justificacion:Conservador, asume precio constante
     *    - Impacto: No infla variaciones
     * 
     * 3. Backward Fill: usar valor siguiente
     *    - Justificacion: Usa informacion recente
     *    - Impacto: Ajusta rapidamente a nuevos niveles
     * 
     * @param porSimbolo Mapa de datos agrupados por simbolo
     * @return cantidad de valores interpolados
     */
    private int interpolarValoresFaltantes(Map<String, List<DatoFinanciero>> porSimbolo) {
        int interpolados = 0;
        
        for (List<DatoFinanciero> lista : porSimbolo.values()) {
            lista.sort(Comparator.comparing(DatoFinanciero::getFecha));
            
            for (int i = 0; i < lista.size(); i++) {
                DatoFinanciero actual = lista.get(i);
                
                if (actual.getCierre() == 0) {
                    DatoFinanciero anterior = encontrarAnterior(i, lista);
                    DatoFinanciero siguiente = encontrarSiguiente(i, lista);
                    
                    if (anterior != null && siguiente != null) {
                        // Interpolacion lineal: promedio entre anterior y siguiente
                        // Impacto algortmico: Mantiene tendencia de la serie
                        double valorInterpolado = (anterior.getCierre() + siguiente.getCierre()) / 2.0;
                        actual.setCierre(valorInterpolado);
                        interpolados++;
                    } else if (anterior != null) {
                        // Forward fill: usar valor anterior
                        // Impacto algortmico: Conservador, asume precio cte
                        actual.setCierre(anterior.getCierre());
                        interpolados++;
                    } else if (siguiente != null) {
                        // Backward fill: usar valor siguiente
                        // Impacto algortmico: Ajusta rapidamente
                        actual.setCierre(siguiente.getCierre());
                        interpolados++;
                    }
                }
            }
        }
        return interpolados;
    }

    private DatoFinanciero encontrarAnterior(int idx, List<DatoFinanciero> lista) {
        for (int i = idx - 1; i >= 0; i--) {
            if (lista.get(i).getCierre() > 0) {
                return lista.get(i);
            }
        }
        return null;
    }

    private DatoFinanciero encontrarSiguiente(int idx, List<DatoFinanciero> lista) {
        for (int i = idx + 1; i < lista.size(); i++) {
            if (lista.get(i).getCierre() > 0) {
                return lista.get(i);
            }
        }
        return null;
    }
}