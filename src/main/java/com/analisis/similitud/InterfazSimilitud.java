package com.analisis.similitud;

public interface InterfazSimilitud {
    double calcular(double[] serieA, double[] serieB);
    String getNombre();
    String getComplejidad();
    String getDescripcion();
}
