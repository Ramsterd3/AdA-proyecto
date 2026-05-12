package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LimpiarDatosTest {

    private List<DatoFinanciero> datosPrueba;

    @BeforeEach
    public void setUp() {
        datosPrueba = new ArrayList<>();
    }

    @Test
    public void testEliminarDuplicados() {
        datosPrueba.add(new DatoFinanciero("2024-01-01", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datosPrueba.add(new DatoFinanciero("2024-01-01", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datosPrueba.add(new DatoFinanciero("2024-01-02", "AAPL", 101.0, 106.0, 100.0, 104.0, 1100000));

        LimpiarDatos limpiador = new LimpiarDatos();
        List<DatoFinanciero> resultado = limpiador.limpiarDatos(datosPrueba);

        assertEquals(2, resultado.size());
    }

    @Test
    public void testInterpolarLineal() {
        datosPrueba.add(new DatoFinanciero("2024-01-01", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datosPrueba.add(new DatoFinanciero("2024-01-02", "AAPL", 0.0, 0.0, 0.0, 0.0, 0));
        datosPrueba.add(new DatoFinanciero("2024-01-03", "AAPL", 102.0, 107.0, 101.0, 105.0, 1200000));

        LimpiarDatos limpiador = new LimpiarDatos();
        List<DatoFinanciero> resultado = limpiador.limpiarDatos(datosPrueba);

        boolean tieneCierre = resultado.stream().anyMatch(d -> d.getSimbolo().equals("AAPL") && 
            d.getFecha().equals("2024-01-02") && d.getCierre() > 0);
        assertTrue(tieneCierre);
    }

    @Test
    public void testForwardFill() {
        datosPrueba.add(new DatoFinanciero("2024-01-01", "MSFT", 100.0, 105.0, 99.0, 103.0, 1000000));
        datosPrueba.add(new DatoFinanciero("2024-01-02", "MSFT", 0.0, 0.0, 0.0, 0.0, 0));

        LimpiarDatos limpiador = new LimpiarDatos();
        List<DatoFinanciero> resultado = limpiador.limpiarDatos(datosPrueba);

        boolean tieneForwardFill = resultado.stream().anyMatch(d -> d.getSimbolo().equals("MSFT") && 
            d.getFecha().equals("2024-01-02") && d.getCierre() == 103.0);
        assertTrue(tieneForwardFill);
    }

    @Test
    public void testOrdenamientoPorFecha() {
        datosPrueba.add(new DatoFinanciero("2024-01-03", "AAPL", 102.0, 107.0, 101.0, 105.0, 1200000));
        datosPrueba.add(new DatoFinanciero("2024-01-01", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datosPrueba.add(new DatoFinanciero("2024-01-02", "AAPL", 101.0, 106.0, 100.0, 104.0, 1100000));

        LimpiarDatos limpiador = new LimpiarDatos();
        List<DatoFinanciero> resultado = limpiador.limpiarDatos(datosPrueba);

        assertEquals("2024-01-01", resultado.get(0).getFecha());
        assertEquals("2024-01-03", resultado.get(2).getFecha());
    }

    @Test
    public void testLogDecisiones() {
        datosPrueba.add(new DatoFinanciero("2024-01-01", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datosPrueba.add(new DatoFinanciero("2024-01-01", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));

        LimpiarDatos limpiador = new LimpiarDatos();
        List<DatoFinanciero> resultado = limpiador.limpiarDatos(datosPrueba);

        List<String> log = limpiador.getLogDecisiones();
        assertFalse(log.isEmpty());
    }
}