package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para SelectorActivos
 */
public class SelectorActivosTest {

    private SelectorActivos selector;
    private List<DatoFinanciero> datos;

    @BeforeEach
    public void setUp() {
        selector = new SelectorActivos();
        datos = new ArrayList<>();
        
        // Crear datos de prueba con multiples simbolos
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datos.add(new DatoFinanciero("2022-01-03", "MSFT", 200.0, 205.0, 199.0, 203.0, 2000000));
        datos.add(new DatoFinanciero("2022-01-03", "GOOGL", 150.0, 155.0, 149.0, 153.0, 1500000));
        datos.add(new DatoFinanciero("2022-01-04", "AAPL", 103.0, 107.0, 102.0, 106.0, 1100000));
        datos.add(new DatoFinanciero("2022-01-04", "MSFT", 203.0, 207.0, 202.0, 206.0, 2100000));
        datos.add(new DatoFinanciero("2022-01-04", "GOOGL", 153.0, 157.0, 152.0, 156.0, 1600000));
    }

    @Test
    public void testObtenerSimbolosDisponibles() {
        List<String> simbolos = selector.obtenerSimbolosDisponibles(datos);
        
        assertNotNull(simbolos);
        assertEquals(3, simbolos.size());
        assertTrue(simbolos.contains("AAPL"));
        assertTrue(simbolos.contains("MSFT"));
        assertTrue(simbolos.contains("GOOGL"));
    }

    @Test
    public void testSimbolosOrdenados() {
        List<String> simbolos = selector.obtenerSimbolosDisponibles(datos);
        
        // Verificar que estan ordenados alfabeticamente
        for (int i = 1; i < simbolos.size(); i++) {
            assertTrue(simbolos.get(i - 1).compareTo(simbolos.get(i)) < 0);
        }
    }

    @Test
    public void testSimbolosUnicos() {
        // Agregar duplicados
        datos.add(new DatoFinanciero("2022-01-05", "AAPL", 106.0, 110.0, 105.0, 109.0, 1200000));
        datos.add(new DatoFinanciero("2022-01-05", "AAPL", 106.0, 110.0, 105.0, 109.0, 1200000));
        
        List<String> simbolos = selector.obtenerSimbolosDisponibles(datos);
        
        // Debe seguir siendo 3 simbolos unicos
        assertEquals(3, simbolos.size());
    }

    @Test
    public void testDatosVacios() {
        List<DatoFinanciero> datosVacios = new ArrayList<>();
        List<String> simbolos = selector.obtenerSimbolosDisponibles(datosVacios);
        
        assertNotNull(simbolos);
        assertTrue(simbolos.isEmpty());
    }

    @Test
    public void testUnSoloSimbolo() {
        List<DatoFinanciero> datosUnico = new ArrayList<>();
        datosUnico.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datosUnico.add(new DatoFinanciero("2022-01-04", "AAPL", 103.0, 107.0, 102.0, 106.0, 1100000));
        
        List<String> simbolos = selector.obtenerSimbolosDisponibles(datosUnico);
        
        assertEquals(1, simbolos.size());
        assertEquals("AAPL", simbolos.get(0));
    }

    @Test
    public void testMuchosSimbolos() {
        List<DatoFinanciero> datosMuchos = new ArrayList<>();
        
        // Agregar 20 simbolos diferentes
        String[] simbolos20 = {"AAPL", "MSFT", "GOOGL", "AMZN", "NVDA", "META", "TSLA", "JPM", "JNJ", "V",
                               "PG", "UNH", "HD", "MA", "DIS", "PYPL", "ADBE", "NFLX", "INTC", "CSCO"};
        
        for (String simbolo : simbolos20) {
            datosMuchos.add(new DatoFinanciero("2022-01-03", simbolo, 100.0, 105.0, 99.0, 103.0, 1000000));
        }
        
        List<String> simbolosObtenidos = selector.obtenerSimbolosDisponibles(datosMuchos);
        
        assertEquals(20, simbolosObtenidos.size());
        
        // Verificar que todos estan presentes
        for (String simbolo : simbolos20) {
            assertTrue(simbolosObtenidos.contains(simbolo));
        }
    }

    @Test
    public void testSimbolosConCaracteresEspeciales() {
        List<DatoFinanciero> datosEspeciales = new ArrayList<>();
        datosEspeciales.add(new DatoFinanciero("2022-01-03", "BRK.B", 100.0, 105.0, 99.0, 103.0, 1000000));
        datosEspeciales.add(new DatoFinanciero("2022-01-03", "BF.B", 200.0, 205.0, 199.0, 203.0, 2000000));
        
        List<String> simbolos = selector.obtenerSimbolosDisponibles(datosEspeciales);
        
        assertEquals(2, simbolos.size());
        assertTrue(simbolos.contains("BRK.B"));
        assertTrue(simbolos.contains("BF.B"));
    }
}
