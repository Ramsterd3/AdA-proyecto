package com.analisis.modelo;

public class Patron {
    private String nombre;
    private String simbolo;
    private int posicionInicial;
    private int ventana;
    private String descripcion;

    public Patron(String nombre, String simbolo, int posicionInicial, int ventana, String descripcion) {
        this.nombre = nombre;
        this.simbolo = simbolo;
        this.posicionInicial = posicionInicial;
        this.ventana = ventana;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getSimbolo() { return simbolo; }
    public int getPosicionInicial() { return posicionInicial; }
    public int getVentana() { return ventana; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return nombre + "," + simbolo + "," + posicionInicial + "," + ventana + "," + descripcion;
    }

    public String toCsvHeader() {
        return "nombre,simbolo,posicion_inicial,ventana,descripcion";
    }
}