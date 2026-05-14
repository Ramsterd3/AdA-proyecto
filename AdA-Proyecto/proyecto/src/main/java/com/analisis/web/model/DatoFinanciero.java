package com.analisis.web.model;

/**
 * Representa un dato financiero OHLCV para el dashboard.
 */
public class DatoFinanciero {
    private String fecha;
    private String simbolo;
    private double apertura;
    private double maximo;
    private double minimo;
    private double cierre;
    private long volumen;

    public DatoFinanciero() {}

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

    public String getFecha()    { return fecha;    }
    public String getSimbolo()  { return simbolo;  }
    public double getApertura() { return apertura; }
    public double getMaximo()   { return maximo;   }
    public double getMinimo()   { return minimo;   }
    public double getCierre()   { return cierre;   }
    public long   getVolumen()  { return volumen;  }
}
