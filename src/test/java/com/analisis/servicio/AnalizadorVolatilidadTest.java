package com.analisis.servicio;

import com.analisis.modelo.ClasificacionRiesgo;
import com.analisis.modelo.DatoFinanciero;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class AnalizadorVolatilidadTest {

    @Test
    public void testCalcularRetornos() {
        AnalizadorVolatilidad analizador = new AnalizadorVolatilidad();
        
        List<DatoFinanciero> serie = new ArrayList<>();
        serie.add(new DatoFinanciero("2024-01-01", "AAPL", 100, 100, 100, 100, 1000));
        serie.add(new DatoFinanciero("2024-01-02", "AAPL", 100, 100, 100, 110, 1000));
        serie.add(new DatoFinanciero("2024-01-03", "AAPL", 110, 110, 110, 120, 1000));
        
        double[] retornos = analizador.calcularRetornos(serie);
        
        assertEquals(2, retornos.length);
        assertEquals(0.1, retornos[0], 0.001);
        assertEquals(0.0909, retornos[1], 0.001);
    }

    @Test
    public void testCalcularDesviacionEstandar() {
        AnalizadorVolatilidad analizador = new AnalizadorVolatilidad();
        
        double[] valores = {0.01, -0.02, 0.03, -0.01, 0.02};
        
        double desviacion = analizador.calcularDesviacionEstandar(valores);
        
        assertTrue(desviacion > 0);
    }

    @Test
    public void testCalcularVolatilidadHistorica() {
        AnalizadorVolatilidad analizador = new AnalizadorVolatilidad();
        
        double desviacion = 0.01;
        double volatilidad = analizador.calcularVolatilidadHistorica(desviacion);
        
        double expected = desviacion * Math.sqrt(252);
        assertEquals(expected, volatilidad, 0.0001);
    }

    @Test
    public void testClasificarRiesgoConservador() {
        AnalizadorVolatilidad analizador = new AnalizadorVolatilidad();
        
        ClasificacionRiesgo.CategoriaRiesgo categoria = analizador.clasificarRiesgo(0.10);
        
        assertEquals(ClasificacionRiesgo.CategoriaRiesgo.CONSERVADOR, categoria);
    }

    @Test
    public void testClasificarRiesgoModerado() {
        AnalizadorVolatilidad analizador = new AnalizadorVolatilidad();
        
        ClasificacionRiesgo.CategoriaRiesgo categoria = analizador.clasificarRiesgo(0.20);
        
        assertEquals(ClasificacionRiesgo.CategoriaRiesgo.MODERADO, categoria);
    }

    @Test
    public void testClasificarRiesgoAgresivo() {
        AnalizadorVolatilidad analizador = new AnalizadorVolatilidad();
        
        ClasificacionRiesgo.CategoriaRiesgo categoria = analizador.clasificarRiesgo(0.35);
        
        assertEquals(ClasificacionRiesgo.CategoriaRiesgo.AGRESIVO, categoria);
    }

    @Test
    public void testClasificarPorRiesgoVacio() {
        AnalizadorVolatilidad analizador = new AnalizadorVolatilidad();
        
        List<DatoFinanciero> datos = new ArrayList<>();
        List<ClasificacionRiesgo> clasificaciones = analizador.clasificarPorRiesgo(datos);
        
        assertTrue(clasificaciones.isEmpty());
    }

    @Test
    public void testClasificarPorRiesgoUnSoloDato() {
        AnalizadorVolatilidad analizador = new AnalizadorVolatilidad();
        
        List<DatoFinanciero> datos = new ArrayList<>();
        datos.add(new DatoFinanciero("2024-01-01", "AAPL", 100, 100, 100, 100, 1000));
        
        List<ClasificacionRiesgo> clasificaciones = analizador.clasificarPorRiesgo(datos);
        
        assertTrue(clasificaciones.isEmpty(), "No debe clasificar con menos de 2 datos");
    }

    @Test
    public void testComparacionClasificacionRiesgo() {
        ClasificacionRiesgo c1 = new ClasificacionRiesgo("AAPL", 0.10, 0.15, 
            ClasificacionRiesgo.CategoriaRiesgo.CONSERVADOR, 100);
        ClasificacionRiesgo c2 = new ClasificacionRiesgo("GOOGL", 0.20, 0.30, 
            ClasificacionRiesgo.CategoriaRiesgo.MODERADO, 100);
        
        assertTrue(c1.compareTo(c2) < 0);
    }
}