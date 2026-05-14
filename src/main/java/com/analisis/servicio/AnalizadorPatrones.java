package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import com.analisis.modelo.Patron;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AnalizadorPatrones {

    private static final int VENTANA_DEFAULT = 5;

    /**
     * Analiza patrones en series temporales usando ventanas deslizantes (sliding window)
     * 
     * COMPLEJIDAD ALGORITMICA:
     * ======================
     * Tiempo: O(n * v * s) donde:
     *   - n = numero de dias en la serie
     *   - v = tamano de la ventana (sliding window)
     *   - s = numero de simbolos
     * Espacio: O(n) por serie temporal
     * 
     * PATRONES DEFINIDOS:
     * ==================
     * 1. DIAS_CONSECUTIVOS_ALZA: Secuencia de dias con cierre mayor al dia anterior
     *    - Formalizacion: Para toda i en [inicio, fin-1]: cierre[i+1] > cierre[i]
     *    - Longitud minima: ventana especificada
     * 
     * 2. FORMACION_VALLE: Minimo local seguido de tendencia al alza
     *    - Formalizacion: min(ventana) en posicion p, y promedio(posiciones > p) > promedio(posiciones < p)
     *    - Interpretacion: Precio minimo que luego se recupera
     */
    public Map<String, Map<String, Integer>> analizarPatrones(List<DatoFinanciero> datos, int ventana) {
        System.out.println("\n=== ETAPA 6: ANALISIS DE PATRONES (SLIDING WINDOW) ===");
        System.out.println("Ventana: " + ventana + " dias");
        
        Map<String, List<DatoFinanciero>> porSimbolo = agruparPorSimbolo(datos);
        Map<String, Map<String, Integer>> frecuencias = new LinkedHashMap<>();
        
        for (Map.Entry<String, List<DatoFinanciero>> entry : porSimbolo.entrySet()) {
            String simbolo = entry.getKey();
            List<DatoFinanciero> serie = entry.getValue();
            
            Map<String, Integer> freqSimbolo = new LinkedHashMap<>();
            freqSimbolo.put("DIAS_ALZA", detectarPatronAlza(serie, ventana));
            freqSimbolo.put("FORMACION_VALLE", detectarPatronValle(serie, ventana));
            freqSimbolo.put("DOBLE_MAXIMO", detectarPatronDobleMaximo(serie, ventana));
            
            frecuencias.put(simbolo, freqSimbolo);
            
            System.out.println(simbolo + ": Alza=" + freqSimbolo.get("DIAS_ALZA") + 
                             ", Valle=" + freqSimbolo.get("FORMACION_VALLE") + 
                             ", DobleMax=" + freqSimbolo.get("DOBLE_MAXIMO"));
        }
        
        return frecuencias;
    }

    /**
     * Detecta secuencia de dias consecutivos al alza
     * 
     * Patrón: DIAS_CONSECUTIVOS_ALZA
     * Formalizacion: Para una ventana de tamano v, se detecta el patron
     *                cuando todos los dias en la ventana tienen cierre mayor
     *                que su dia anterior (dentro de la ventana).
     * 
     * Ejemplo: Si ventana=3, se detecta si:
     *   cierre[i+1] > cierre[i] AND cierre[i+2] > cierre[i+1]
     * 
     * @param serie Lista de datos financieros ordenada por fecha
     * @param ventana Tamano de la ventana deslizante
     * @return Numero de veces que se detecto el patron
     */
    public int detectarPatronAlza(List<DatoFinanciero> serie, int ventana) {
        int contador = 0;
        
        for (int i = 0; i <= serie.size() - ventana; i++) {
            boolean patronDetectado = true;
            
            for (int j = i; j < i + ventana - 1; j++) {
                if (serie.get(j + 1).getCierre() <= serie.get(j).getCierre()) {
                    patronDetectado = false;
                    break;
                }
            }
            
            if (patronDetectado) {
                contador++;
            }
        }
        
        return contador;
    }

    /**
     * Detecta formacion de valle (minimo local seguido de recuperacion)
     * 
     * Patrón: FORMACION_VALLE
     * Formalizacion: Se detecta cuando en una ventana de tamano v:
     *   1. El precio de cierre tiene un minimo en una posicion p (dentro de la ventana)
     *   2. El promedio de precios de las posiciones posteriores a p es mayor
     *      que el promedio de las posiciones anteriores a p
     * 
     * Interpretacion: El activo toco fondo y comenzo a recuperarse,
     *                 senal potencial de inversion interesante
     * 
     * @param serie Lista de datos financieros
     * @param ventana Tamano de la ventana
     * @return Numero de valles detectados
     */
    public int detectarPatronValle(List<DatoFinanciero> serie, int ventana) {
        int contador = 0;
        
        for (int i = 0; i <= serie.size() - ventana; i++) {
            double minimo = Double.MAX_VALUE;
            int posMinimo = -1;
            
            for (int j = i; j < i + ventana; j++) {
                if (serie.get(j).getCierre() < minimo) {
                    minimo = serie.get(j).getCierre();
                    posMinimo = j;
                }
            }
            
            if (posMinimo > i && posMinimo < i + ventana - 1) {
                double sumaAnterior = 0;
                double sumaPosterior = 0;
                int countAnterior = posMinimo - i;
                int countPosterior = (i + ventana - 1) - posMinimo;
                
                for (int j = i; j < posMinimo; j++) {
                    sumaAnterior += serie.get(j).getCierre();
                }
                for (int j = posMinimo + 1; j < i + ventana; j++) {
                    sumaPosterior += serie.get(j).getCierre();
                }
                
                double promedioAnterior = countAnterior > 0 ? sumaAnterior / countAnterior : 0;
                double promedioPosterior = countPosterior > 0 ? sumaPosterior / countPosterior : 0;
                
                if (promedioPosterior > promedioAnterior) {
                    contador++;
                }
            }
        }
        
        return contador;
    }

    /**
     * Detecta doble maximo (patron adicional formalizado)
     * 
     * Patrón: DOBLE_MAXIMO
     * Formalizacion: Se detecta cuando en una ventana de tamano v:
     *   - Hay dos maximos relativos dentro de la ventana
     *   - El segundo maximo es menor o igual al primero pero mayor que los valores circundantes
     * 
     * Interpretacion: Patron de resistencia o formacion de patron M,
     *                 puede indicar tendencia a la baja o consolidacion
     * 
     * @param serie Lista de datos financieros
     * @param ventana Tamano de la ventana
     * @return Numero de doble maximos detectados
     */
    public int detectarPatronDobleMaximo(List<DatoFinanciero> serie, int ventana) {
        int contador = 0;
        
        for (int i = 0; i <= serie.size() - ventana; i++) {
            int maximosRelativos = 0;
            boolean primerMaximoEncontrado = false;
            
            for (int j = i + 1; j < i + ventana - 1; j++) {
                double actual = serie.get(j).getCierre();
                double anterior = serie.get(j - 1).getCierre();
                double siguiente = serie.get(j + 1).getCierre();
                
                if (actual > anterior && actual > siguiente) {
                    if (!primerMaximoEncontrado) {
                        primerMaximoEncontrado = true;
                    } else {
                        maximosRelativos++;
                        break;
                    }
                }
            }
            
            if (maximosRelativos >= 1) {
                contador++;
            }
        }
        
        return contador;
    }

    /**
     * Agrupa datos por simbolo
     * 
     * @param datos Lista de datos financieros
     * @return Mapa simbolo -> lista de datos ordenada por fecha
     */
    private Map<String, List<DatoFinanciero>> agruparPorSimbolo(List<DatoFinanciero> datos) {
        Map<String, List<DatoFinanciero>> porSimbolo = new LinkedHashMap<>();
        
        for (DatoFinanciero d : datos) {
            porSimbolo.computeIfAbsent(d.getSimbolo(), k -> new ArrayList<>()).add(d);
        }
        
        for (List<DatoFinanciero> lista : porSimbolo.values()) {
            lista.sort(Comparator.comparing(DatoFinanciero::getFecha));
        }
        
        return porSimbolo;
    }

    /**
     * Genera reporte de patrones por simbolo
     * 
     * @param frecuencias Mapa de frecuencias por simbolo
     * @param archivo Nombre del archivo de salida
     */
    public void guardarReportePatrones(Map<String, Map<String, Integer>> frecuencias, String archivo) {
        try (FileWriter fw = new FileWriter(archivo)) {
            fw.write("simbolo,patron,veces\n");
            
            for (Map.Entry<String, Map<String, Integer>> entry : frecuencias.entrySet()) {
                String simbolo = entry.getKey();
                Map<String, Integer> patrones = entry.getValue();
                
                for (Map.Entry<String, Integer> patron : patrones.entrySet()) {
                    fw.write(simbolo + "," + patron.getKey() + "," + patron.getValue() + "\n");
                }
            }
            
            System.out.println("\nReporte de patrones guardado en " + archivo);
        } catch (IOException e) {
            System.err.println("Error al guardar patrones: " + e.getMessage());
        }
    }

    public int getVentanaDefault() {
        return VENTANA_DEFAULT;
    }
}