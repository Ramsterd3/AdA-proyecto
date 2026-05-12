package com.analisis.modelo;

public class ResultadoSimilitud {
    private String simboloA;
    private String simboloB;
    private String algoritmo;
    private String complejidad;
    private double valor;

    public ResultadoSimilitud(String simboloA, String simboloB,
                               String algoritmo, String complejidad, double valor) {
        this.simboloA = simboloA;
        this.simboloB = simboloB;
        this.algoritmo = algoritmo;
        this.complejidad = complejidad;
        this.valor = valor;
    }

    public String getSimboloA() { return simboloA; }
    public String getSimboloB() { return simboloB; }
    public String getAlgoritmo() { return algoritmo; }
    public String getComplejidad() { return complejidad; }
    public double getValor() { return valor; }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%.6f", simboloA, simboloB, algoritmo, complejidad, valor);
    }

    public String toCsvHeader() {
        return "ActivoA,ActivoB,Algoritmo,Complejidad,Valor";
    }
}
