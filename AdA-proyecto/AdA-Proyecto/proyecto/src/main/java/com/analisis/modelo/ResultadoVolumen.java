package com.analisis.modelo;

/**
 * Resultado del analisis de volumen por dia
 */
public class ResultadoVolumen {
    private String fecha;
    private long volumenTotal;

    public ResultadoVolumen(String fecha, long volumenTotal) {
        this.fecha = fecha;
        this.volumenTotal = volumenTotal;
    }

    public String getFecha() { return fecha; }
    public long getVolumenTotal() { return volumenTotal; }

    @Override
    public String toString() {
        return String.format("%s,%d", fecha, volumenTotal);
    }

    public String toCsvHeader() {
        return "Fecha,Volumen Total";
    }
}