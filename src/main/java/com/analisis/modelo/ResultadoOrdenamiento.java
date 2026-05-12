package com.analisis.modelo;

/**
 * Resultado del analisis de un algoritmo de ordenamiento
 */
public class ResultadoOrdenamiento {
    private String algoritmo;
    private String complejidad;
    private int tamano;
    private double tiempoNanosegundos;

    public ResultadoOrdenamiento(String algoritmo, String complejidad, 
                                  int tamano, double tiempoNanosegundos) {
        this.algoritmo = algoritmo;
        this.complejidad = complejidad;
        this.tamano = tamano;
        this.tiempoNanosegundos = tiempoNanosegundos;
    }

    public String getAlgoritmo() { return algoritmo; }
    public String getComplejidad() { return complejidad; }
    public int getTamano() { return tamano; }
    public double getTiempoNanosegundos() { return tiempoNanosegundos; }

    public double getTiempoSegundos() {
        return tiempoNanosegundos / 1_000_000_000.0;
    }

    @Override
    public String toString() {
        return String.format("%s %s,%d,%.6f", algoritmo, complejidad, tamano, getTiempoSegundos());
    }

    public String toCsvHeader() {
        return "Metodo de ordenamiento,Tamano,Tiempo (s)";
    }
}