package com.analisis.servicio;

import com.analisis.servicio.CalendarioBursatil.Mercado;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para CalendarioBursatil
 */
public class CalendarioBursatilTest {

    @Test
    public void testFinDeSemanaNoEsHabil() {
        // Sabado 1 de enero de 2022
        LocalDate sabado = LocalDate.of(2022, 1, 1);
        assertFalse(CalendarioBursatil.esDiaHabil(sabado, Mercado.USA));
        
        // Domingo 2 de enero de 2022
        LocalDate domingo = LocalDate.of(2022, 1, 2);
        assertFalse(CalendarioBursatil.esDiaHabil(domingo, Mercado.USA));
    }

    @Test
    public void testDiaLaboralNormalEsHabil() {
        // Lunes 3 de enero de 2022 (no festivo)
        LocalDate lunes = LocalDate.of(2022, 1, 3);
        assertTrue(CalendarioBursatil.esDiaHabil(lunes, Mercado.USA));
        
        // Miercoles 5 de enero de 2022
        LocalDate miercoles = LocalDate.of(2022, 1, 5);
        assertTrue(CalendarioBursatil.esDiaHabil(miercoles, Mercado.USA));
    }

    @Test
    public void testFestivoUSANoEsHabil() {
        // 4 de julio de 2022 (Independence Day)
        LocalDate independenceDay = LocalDate.of(2022, 7, 4);
        assertFalse(CalendarioBursatil.esDiaHabil(independenceDay, Mercado.USA));
        assertTrue(CalendarioBursatil.esFestivo(independenceDay, Mercado.USA));
        
        // 25 de diciembre de 2023 (Christmas)
        LocalDate christmas = LocalDate.of(2023, 12, 25);
        assertFalse(CalendarioBursatil.esDiaHabil(christmas, Mercado.USA));
        assertTrue(CalendarioBursatil.esFestivo(christmas, Mercado.USA));
    }

    @Test
    public void testSiguienteDiaHabil() {
        // Viernes 30 de diciembre de 2022 -> siguiente habil es martes 3 de enero de 2023
        // (31 dic sabado, 1 ene domingo, 2 ene festivo)
        LocalDate viernes = LocalDate.of(2022, 12, 30);
        LocalDate siguienteHabil = CalendarioBursatil.siguienteDiaHabil(viernes, Mercado.USA);
        
        // Debe saltar fin de semana
        assertTrue(siguienteHabil.isAfter(viernes));
        assertTrue(CalendarioBursatil.esDiaHabil(siguienteHabil, Mercado.USA));
    }

    @Test
    public void testAnteriorDiaHabil() {
        // Lunes 3 de enero de 2022 -> anterior habil es viernes 31 de diciembre de 2021
        LocalDate lunes = LocalDate.of(2022, 1, 3);
        LocalDate anteriorHabil = CalendarioBursatil.anteriorDiaHabil(lunes, Mercado.USA);
        
        assertTrue(anteriorHabil.isBefore(lunes));
        assertTrue(CalendarioBursatil.esDiaHabil(anteriorHabil, Mercado.USA));
    }

    @Test
    public void testContarDiasHabiles() {
        // Semana del 3 al 7 de enero de 2022 (lunes a viernes)
        LocalDate inicio = LocalDate.of(2022, 1, 3);
        LocalDate fin = LocalDate.of(2022, 1, 7);
        
        int diasHabiles = CalendarioBursatil.contarDiasHabiles(inicio, fin, Mercado.USA);
        assertEquals(5, diasHabiles); // 5 dias laborales
    }

    @Test
    public void testContarDiasHabilesConFestivo() {
        // Del 1 al 5 de julio de 2022 (incluye festivo el 4)
        LocalDate inicio = LocalDate.of(2022, 7, 1);
        LocalDate fin = LocalDate.of(2022, 7, 5);
        
        int diasHabiles = CalendarioBursatil.contarDiasHabiles(inicio, fin, Mercado.USA);
        // 1 jul viernes (habil), 2-3 jul fin de semana (no habil), 4 jul festivo (no habil), 5 jul martes (habil)
        assertEquals(2, diasHabiles);
    }

    @Test
    public void testMercadoColombia() {
        // 20 de julio (Independencia de Colombia)
        LocalDate independenciaColombia = LocalDate.of(2022, 7, 20);
        
        // No es habil en Colombia
        assertFalse(CalendarioBursatil.esDiaHabil(independenciaColombia, Mercado.COLOMBIA));
        assertTrue(CalendarioBursatil.esFestivo(independenciaColombia, Mercado.COLOMBIA));
        
        // Pero SI es habil en USA (no es festivo alli)
        assertTrue(CalendarioBursatil.esDiaHabil(independenciaColombia, Mercado.USA));
    }

    @Test
    public void testMercadoAmbos() {
        // 4 de julio (festivo USA, no Colombia)
        LocalDate july4 = LocalDate.of(2022, 7, 4);
        
        // No es habil en AMBOS porque es festivo en USA
        assertFalse(CalendarioBursatil.esDiaHabil(july4, Mercado.AMBOS));
        
        // 20 de julio (festivo Colombia, no USA)
        LocalDate july20 = LocalDate.of(2022, 7, 20);
        
        // No es habil en AMBOS porque es festivo en Colombia
        assertFalse(CalendarioBursatil.esDiaHabil(july20, Mercado.AMBOS));
    }

    @Test
    public void testDiaHabilNormalEnAmbos() {
        // 15 de marzo de 2022 (martes normal, no festivo en ninguno)
        LocalDate martes = LocalDate.of(2022, 3, 15);
        
        assertTrue(CalendarioBursatil.esDiaHabil(martes, Mercado.USA));
        assertTrue(CalendarioBursatil.esDiaHabil(martes, Mercado.COLOMBIA));
        assertTrue(CalendarioBursatil.esDiaHabil(martes, Mercado.AMBOS));
    }

    @Test
    public void testGoodFriday2024() {
        // Good Friday 2024 (29 de marzo)
        LocalDate goodFriday = LocalDate.of(2024, 3, 29);
        
        assertFalse(CalendarioBursatil.esDiaHabil(goodFriday, Mercado.USA));
        assertTrue(CalendarioBursatil.esFestivo(goodFriday, Mercado.USA));
    }

    @Test
    public void testThanksgiving2025() {
        // Thanksgiving 2025 (27 de noviembre)
        LocalDate thanksgiving = LocalDate.of(2025, 11, 27);
        
        assertFalse(CalendarioBursatil.esDiaHabil(thanksgiving, Mercado.USA));
        assertTrue(CalendarioBursatil.esFestivo(thanksgiving, Mercado.USA));
    }
}
