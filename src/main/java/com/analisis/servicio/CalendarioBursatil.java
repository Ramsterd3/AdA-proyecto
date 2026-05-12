package com.analisis.servicio;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.TreeSet;

/**
 * Maneja el calendario bursátil para el mercado de EE.UU.
 */
public class CalendarioBursatil {

    private static final Set<LocalDate> FESTIVOS = new TreeSet<>();
    static {
        FESTIVOS.add(LocalDate.of(2021, 1, 1));
        FESTIVOS.add(LocalDate.of(2021, 1, 18));
        FESTIVOS.add(LocalDate.of(2021, 2, 15));
        FESTIVOS.add(LocalDate.of(2021, 4, 2));
        FESTIVOS.add(LocalDate.of(2021, 5, 31));
        FESTIVOS.add(LocalDate.of(2021, 7, 5));
        FESTIVOS.add(LocalDate.of(2021, 9, 6));
        FESTIVOS.add(LocalDate.of(2021, 11, 25));
        FESTIVOS.add(LocalDate.of(2021, 12, 24));
        
        FESTIVOS.add(LocalDate.of(2022, 1, 17));
        FESTIVOS.add(LocalDate.of(2022, 2, 21));
        FESTIVOS.add(LocalDate.of(2022, 4, 15));
        FESTIVOS.add(LocalDate.of(2022, 5, 30));
        FESTIVOS.add(LocalDate.of(2022, 7, 4));
        FESTIVOS.add(LocalDate.of(2022, 9, 5));
        FESTIVOS.add(LocalDate.of(2022, 11, 24));
        FESTIVOS.add(LocalDate.of(2022, 12, 26));
        
        FESTIVOS.add(LocalDate.of(2023, 1, 2));
        FESTIVOS.add(LocalDate.of(2023, 1, 16));
        FESTIVOS.add(LocalDate.of(2023, 2, 20));
        FESTIVOS.add(LocalDate.of(2023, 4, 7));
        FESTIVOS.add(LocalDate.of(2023, 5, 29));
        FESTIVOS.add(LocalDate.of(2023, 6, 19));
        FESTIVOS.add(LocalDate.of(2023, 7, 4));
        FESTIVOS.add(LocalDate.of(2023, 9, 4));
        FESTIVOS.add(LocalDate.of(2023, 11, 23));
        FESTIVOS.add(LocalDate.of(2023, 12, 25));
        
        FESTIVOS.add(LocalDate.of(2024, 1, 1));
        FESTIVOS.add(LocalDate.of(2024, 1, 15));
        FESTIVOS.add(LocalDate.of(2024, 2, 19));
        FESTIVOS.add(LocalDate.of(2024, 3, 29));
        FESTIVOS.add(LocalDate.of(2024, 5, 27));
        FESTIVOS.add(LocalDate.of(2024, 6, 19));
        FESTIVOS.add(LocalDate.of(2024, 7, 4));
        FESTIVOS.add(LocalDate.of(2024, 9, 2));
        FESTIVOS.add(LocalDate.of(2024, 11, 28));
        FESTIVOS.add(LocalDate.of(2024, 12, 25));
        
        FESTIVOS.add(LocalDate.of(2025, 1, 1));
        FESTIVOS.add(LocalDate.of(2025, 1, 20));
        FESTIVOS.add(LocalDate.of(2025, 2, 17));
        FESTIVOS.add(LocalDate.of(2025, 4, 18));
        FESTIVOS.add(LocalDate.of(2025, 5, 26));
        FESTIVOS.add(LocalDate.of(2025, 6, 19));
        FESTIVOS.add(LocalDate.of(2025, 7, 4));
        FESTIVOS.add(LocalDate.of(2025, 9, 1));
        FESTIVOS.add(LocalDate.of(2025, 11, 27));
        FESTIVOS.add(LocalDate.of(2025, 12, 25));
        
        FESTIVOS.add(LocalDate.of(2026, 1, 1));
        FESTIVOS.add(LocalDate.of(2026, 1, 20));
        FESTIVOS.add(LocalDate.of(2026, 2, 16));
        FESTIVOS.add(LocalDate.of(2026, 4, 3));
        FESTIVOS.add(LocalDate.of(2026, 5, 25));
        FESTIVOS.add(LocalDate.of(2026, 6, 19));
        FESTIVOS.add(LocalDate.of(2026, 7, 3));
        FESTIVOS.add(LocalDate.of(2026, 9, 7));
        FESTIVOS.add(LocalDate.of(2026, 11, 26));
        FESTIVOS.add(LocalDate.of(2026, 12, 25));
    }

    public static boolean esDiaHabil(LocalDate fecha) {
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || 
            fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        return !FESTIVOS.contains(fecha);
    }

    public static boolean esFestivo(LocalDate fecha) {
        return FESTIVOS.contains(fecha);
    }

    public static LocalDate siguienteDiaHabil(LocalDate fecha) {
        LocalDate siguiente = fecha.plusDays(1);
        while (!esDiaHabil(siguiente)) {
            siguiente = siguiente.plusDays(1);
        }
        return siguiente;
    }

    public static LocalDate anteriorDiaHabil(LocalDate fecha) {
        LocalDate anterior = fecha.minusDays(1);
        while (!esDiaHabil(anterior)) {
            anterior = anterior.minusDays(1);
        }
        return anterior;
    }

    public static Set<LocalDate> getFestivos() {
        return new TreeSet<>(FESTIVOS);
    }
}