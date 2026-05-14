package com.analisis.modelo;

public class ClasificacionRiesgo implements Comparable<ClasificacionRiesgo> {
    private String simbolo;
    private double desviacionEstandar;
    private double volatilidadHistorica;
    private CategoriaRiesgo categoria;
    private int cantidadDatos;

    public enum CategoriaRiesgo {
        CONSERVADOR("Conservador"),
        MODERADO("Moderado"),
        AGRESIVO("Agresivo");

        private final String nombre;

        CategoriaRiesgo(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() { return nombre; }
    }

    public ClasificacionRiesgo(String simbolo, double desviacionEstandar, 
                               double volatilidadHistorica, CategoriaRiesgo categoria,
                               int cantidadDatos) {
        this.simbolo = simbolo;
        this.desviacionEstandar = desviacionEstandar;
        this.volatilidadHistorica = volatilidadHistorica;
        this.categoria = categoria;
        this.cantidadDatos = cantidadDatos;
    }

    public String getSimbolo() { return simbolo; }
    public double getDesviacionEstandar() { return desviacionEstandar; }
    public double getVolatilidadHistorica() { return volatilidadHistorica; }
    public CategoriaRiesgo getCategoria() { return categoria; }
    public int getCantidadDatos() { return cantidadDatos; }

    @Override
    public int compareTo(ClasificacionRiesgo otro) {
        return Double.compare(this.volatilidadHistorica, otro.volatilidadHistorica);
    }

    @Override
    public String toString() {
        return String.format("%s,%.6f,%.4f,%s,%d",
            simbolo, desviacionEstandar, volatilidadHistorica, 
            categoria.getNombre(), cantidadDatos);
    }

    public String toCsvHeader() {
        return "simbolo,desviacion_estandar,volatilidad_historica,categoria,cantidad_datos";
    }
}