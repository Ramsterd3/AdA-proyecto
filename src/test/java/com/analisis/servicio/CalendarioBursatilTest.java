package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarioBursatilTest {

    @Test
    public void testEsDiaHabilSemana() {
        LocalDate sabado = LocalDate.of(2024, 1, 6);
        LocalDate domingo = LocalDate.of(2024, 1, 7);
        LocalDate lunes = LocalDate.of(2024, 1, 8);

        assertFalse(CalendarioBursatil.esDiaHabil(sabado));
        assertFalse(CalendarioBursatil.esDiaHabil(domingo));
        assertTrue(CalendarioBursatil.esDiaHabil(lunes));
    }

    @Test
    public void testFestivosEEUU() {
        LocalDate diaAccionGracias = LocalDate.of(2024, 11, 28);
        LocalDate navidad = LocalDate.of(2024, 12, 25);
        LocalDate aoNuevo = LocalDate.of(2024, 1, 1);

        assertTrue(CalendarioBursatil.esFestivo(diaAccionGracias));
        assertTrue(CalendarioBursatil.esFestivo(navidad));
        assertTrue(CalendarioBursatil.esFestivo(aoNuevo));
    }

    @Test
    public void testSiguienteDiaHabil() {
        LocalDate viernes = LocalDate.of(2024, 1, 5);
        LocalDate siguiente = CalendarioBursatil.siguienteDiaHabil(viernes);

        assertEquals(LocalDate.of(2024, 1, 8), siguiente);
    }

    @Test
    public void testAnteriorDiaHabil() {
        LocalDate lunes = LocalDate.of(2024, 1, 8);
        LocalDate anterior = CalendarioBursatil.anteriorDiaHabil(lunes);

        assertEquals(LocalDate.of(2024, 1, 5), anterior);
    }

    @Test
    public void testDiaHabilNormal() {
        LocalDate miercoles = LocalDate.of(2024, 1, 10);
        
        assertTrue(CalendarioBursatil.esDiaHabil(miercoles));
        assertFalse(CalendarioBursatil.esFestivo(miercoles));
    }
}