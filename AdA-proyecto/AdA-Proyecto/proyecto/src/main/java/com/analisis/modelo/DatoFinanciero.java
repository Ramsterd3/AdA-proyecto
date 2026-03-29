package com.analisis.modelo;

/**
 * Representa un dato financiero con precios OHLCV
 */
public class DatoFinanciero implements Comparable<DatoFinanciero> {
    private String fecha;
    private String simbolo;
    private double apertura;
    private double maximo;
    private double minimo;
    private double cierre;
    private long volumen;

    public DatoFinanciero(String fecha, String simbolo, double apertura, 
                          double maximo, double minimo, double cierre, long volumen) {
        this.fecha = fecha;
        this.simbolo = simbolo;
        this.apertura = apertura;
        this.maximo = maximo;
        this.minimo = minimo;
        this.cierre = cierre;
        this.volumen = volumen;
    }

    public String getFecha() { return fecha; }
    public String getSimbolo() { return simbolo; }
    public double getApertura() { return apertura; }
    public double getMaximo() { return maximo; }
    public double getMinimo() { return minimo; }
    public double getCierre() { return cierre; }
    public long getVolumen() { return volumen; }

    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setCierre(double cierre) { this.cierre = cierre; }

    @Override
    public int compareTo(DatoFinanciero otro) {
        int cmp = this.fecha.compareTo(otro.fecha);
        if (cmp != 0) return cmp;
        return Double.compare(this.cierre, otro.cierre);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%d",
            fecha, simbolo, apertura, maximo, minimo, cierre, volumen);
    }

    public String toCsvHeader() {
        return "fecha,simbolo,apertura,maximo,minimo,cierre,volumen";
    }
}