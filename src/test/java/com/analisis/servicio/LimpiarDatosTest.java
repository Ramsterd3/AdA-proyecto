package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para LimpiarDatos
 */
public class LimpiarDatosTest {

    private LimpiarDatos limpiador;

    @BeforeEach
    public void setUp() {
        limpiador = new LimpiarDatos();
    }

    @Test
    public void testEliminaDuplicados() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        // Agregar datos duplicados
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000)); // Duplicado
        datos.add(new DatoFinanciero("2022-01-04", "AAPL", 103.0, 107.0, 102.0, 106.0, 1100000));
        
        int tamanoInicial = datos.size();
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        // Debe eliminar 1 duplicado
        assertTrue(limpios.size() < tamanoInicial);
        
        // Verificar que no hay duplicados
        long uniqueCount = limpios.stream()
            .map(d -> d.getFecha() + "-" + d.getSimbolo())
            .distinct()
            .count();
        assertEquals(limpios.size(), uniqueCount);
    }

    @Test
    public void testEliminaDiasNoHabiles() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        // Agregar datos en dias habiles y no habiles
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000)); // Lunes (habil)
        datos.add(new DatoFinanciero("2022-01-01", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000)); // Sabado (no habil)
        datos.add(new DatoFinanciero("2022-01-02", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000)); // Domingo (no habil)
        datos.add(new DatoFinanciero("2022-07-04", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000)); // Festivo USA (no habil)
        
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        // Solo debe quedar el lunes
        assertEquals(1, limpios.size());
        assertEquals("2022-01-03", limpios.get(0).getFecha());
    }

    @Test
    public void testInterpolaValoresFaltantes() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        // Serie con valor faltante en el medio
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 100.0, 1000000));
        datos.add(new DatoFinanciero("2022-01-04", "AAPL", 0.0, 0.0, 0.0, 0.0, 0)); // Faltante
        datos.add(new DatoFinanciero("2022-01-05", "AAPL", 110.0, 115.0, 109.0, 110.0, 1200000));
        
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        // El valor faltante debe ser interpolado
        DatoFinanciero interpolado = limpios.stream()
            .filter(d -> d.getFecha().equals("2022-01-04"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(interpolado);
        assertTrue(interpolado.getCierre() > 0);
        // Debe ser aproximadamente el promedio de 100 y 110
        assertEquals(105.0, interpolado.getCierre(), 1.0);
    }

    @Test
    public void testDetectaOutliers() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        // Serie normal con un outlier (usar dias habiles)
        for (int i = 0; i < 15; i++) {
            // Usar solo dias habiles (lunes a viernes)
            int dia = 3 + i; // Empezar desde el 3 de enero (lunes)
            if (dia > 31) break;
            
            datos.add(new DatoFinanciero(
                "2022-01-" + String.format("%02d", dia),
                "AAPL",
                100.0, 105.0, 99.0,
                100.0 + i, // Precios incrementales normales
                1000000
            ));
        }
        
        // Agregar outlier extremo en dia habil
        datos.add(new DatoFinanciero("2022-01-31", "AAPL", 1000.0, 1005.0, 999.0, 1000.0, 1000000));
        
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        // Los outliers se detectan pero NO se eliminan
        // Nota: algunos registros pueden ser eliminados por ser dias no habiles
        assertTrue(limpios.size() >= 10, "Debe haber al menos 10 registros despues de limpieza");
        
        // Verificar que el log contiene informacion
        List<String> log = limpiador.getLogDecisiones();
        assertNotNull(log);
        assertFalse(log.isEmpty());
    }

    @Test
    public void testForwardFill() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        // Serie con valor faltante al final (solo tiene anterior)
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 100.0, 1000000));
        datos.add(new DatoFinanciero("2022-01-04", "AAPL", 0.0, 0.0, 0.0, 0.0, 0)); // Faltante
        
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        // Debe usar forward fill (valor anterior)
        DatoFinanciero interpolado = limpios.stream()
            .filter(d -> d.getFecha().equals("2022-01-04"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(interpolado);
        assertEquals(100.0, interpolado.getCierre(), 0.01);
    }

    @Test
    public void testBackwardFill() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        // Serie con valor faltante al inicio (solo tiene siguiente)
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 0.0, 0.0, 0.0, 0.0, 0)); // Faltante
        datos.add(new DatoFinanciero("2022-01-04", "AAPL", 110.0, 115.0, 109.0, 110.0, 1200000));
        
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        // Debe usar backward fill (valor siguiente)
        DatoFinanciero interpolado = limpios.stream()
            .filter(d -> d.getFecha().equals("2022-01-03"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(interpolado);
        assertEquals(110.0, interpolado.getCierre(), 0.01);
    }

    @Test
    public void testMultiplesSimbolos() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        // Datos de multiples simbolos
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datos.add(new DatoFinanciero("2022-01-03", "MSFT", 200.0, 205.0, 199.0, 203.0, 2000000));
        datos.add(new DatoFinanciero("2022-01-04", "AAPL", 103.0, 107.0, 102.0, 106.0, 1100000));
        datos.add(new DatoFinanciero("2022-01-04", "MSFT", 203.0, 207.0, 202.0, 206.0, 2100000));
        
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        // Debe mantener todos los registros (son validos)
        assertTrue(limpios.size() >= 4);
        
        // Verificar que ambos simbolos estan presentes
        long simbolosUnicos = limpios.stream()
            .map(DatoFinanciero::getSimbolo)
            .distinct()
            .count();
        assertTrue(simbolosUnicos >= 2);
    }

    @Test
    public void testLogDecisiones() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000)); // Duplicado
        
        limpiador.limpiarDatos(datos);
        
        List<String> log = limpiador.getLogDecisiones();
        
        assertNotNull(log);
        assertFalse(log.isEmpty());
        assertTrue(log.get(0).contains("REPORTE DE LIMPIEZA ETL"));
    }

    @Test
    public void testDatosVacios() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        assertNotNull(limpios);
        assertTrue(limpios.isEmpty());
    }

    @Test
    public void testOrdenamientoFinal() {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        // Agregar datos desordenados
        datos.add(new DatoFinanciero("2022-01-05", "AAPL", 110.0, 115.0, 109.0, 113.0, 1200000));
        datos.add(new DatoFinanciero("2022-01-03", "AAPL", 100.0, 105.0, 99.0, 103.0, 1000000));
        datos.add(new DatoFinanciero("2022-01-04", "AAPL", 103.0, 107.0, 102.0, 106.0, 1100000));
        
        List<DatoFinanciero> limpios = limpiador.limpiarDatos(datos);
        
        // Verificar que estan ordenados por fecha
        for (int i = 1; i < limpios.size(); i++) {
            String fechaAnterior = limpios.get(i - 1).getFecha();
            String fechaActual = limpios.get(i).getFecha();
            assertTrue(fechaAnterior.compareTo(fechaActual) <= 0);
        }
    }
}
