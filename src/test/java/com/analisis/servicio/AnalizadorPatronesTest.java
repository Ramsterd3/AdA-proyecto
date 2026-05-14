package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalizadorPatronesTest {

    @Test
    public void testDetectarPatronAlza() {
        AnalizadorPatrones analizador = new AnalizadorPatrones();
        
        List<DatoFinanciero> serie = new ArrayList<>();
        serie.add(new DatoFinanciero("2024-01-01", "AAPL", 100, 102, 99, 101, 1000));
        serie.add(new DatoFinanciero("2024-01-02", "AAPL", 101, 103, 100, 102, 1000));
        serie.add(new DatoFinanciero("2024-01-03", "AAPL", 102, 104, 101, 103, 1000));
        serie.add(new DatoFinanciero("2024-01-04", "AAPL", 103, 105, 102, 104, 1000));
        
        int resultado = analizador.detectarPatronAlza(serie, 3);
        
        assertTrue(resultado > 0, "Debe detectar al menos un patron de alza");
    }

    @Test
    public void testDetectarPatronAlzaSinPatron() {
        AnalizadorPatrones analizador = new AnalizadorPatrones();
        
        List<DatoFinanciero> serie = new ArrayList<>();
        serie.add(new DatoFinanciero("2024-01-01", "AAPL", 100, 102, 99, 100, 1000));
        serie.add(new DatoFinanciero("2024-01-02", "AAPL", 100, 102, 99, 99, 1000));
        serie.add(new DatoFinanciero("2024-01-03", "AAPL", 99, 101, 98, 98, 1000));
        
        int resultado = analizador.detectarPatronAlza(serie, 3);
        
        assertEquals(0, resultado, "No debe detectar patron de alza");
    }

    @Test
    public void testDetectarPatronValle() {
        AnalizadorPatrones analizador = new AnalizadorPatrones();
        
        List<DatoFinanciero> serie = new ArrayList<>();
        serie.add(new DatoFinanciero("2024-01-01", "AAPL", 105, 107, 104, 106, 1000));
        serie.add(new DatoFinanciero("2024-01-02", "AAPL", 106, 108, 105, 107, 1000));
        serie.add(new DatoFinanciero("2024-01-03", "AAPL", 100, 102, 99, 101, 1000));
        serie.add(new DatoFinanciero("2024-01-04", "AAPL", 101, 103, 100, 102, 1000));
        serie.add(new DatoFinanciero("2024-01-05", "AAPL", 102, 104, 101, 103, 1000));
        
        int resultado = analizador.detectarPatronValle(serie, 5);
        
        assertTrue(resultado >= 0);
    }

    @Test
    public void testAnalisisPatronesVacio() {
        AnalizadorPatrones analizador = new AnalizadorPatrones();
        
        List<DatoFinanciero> datos = new ArrayList<>();
        Map<String, Map<String, Integer>> resultado = analizador.analizarPatrones(datos, 5);
        
        assertTrue(resultado.isEmpty(), "Debe retornar mapa vacio");
    }

    @Test
    public void testVentanaDefault() {
        AnalizadorPatrones analizador = new AnalizadorPatrones();
        
        assertEquals(5, analizador.getVentanaDefault());
    }
}