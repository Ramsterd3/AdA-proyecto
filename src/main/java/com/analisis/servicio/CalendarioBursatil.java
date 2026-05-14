package com.analisis.servicio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Gestiona el calendario bursátil de mercados financieros.
 * Identifica días hábiles y festivos para EE.UU. y Colombia.
 */
public class CalendarioBursatil {

    public enum Mercado {
        USA,
        COLOMBIA,
        AMBOS
    }

    private static final Set<LocalDate> FESTIVOS_USA = new HashSet<>();
    private static final Set<LocalDate> FESTIVOS_COLOMBIA = new HashSet<>();

    static {
        inicializarFestivosUSA();
        inicializarFestivosColombia();
    }

    /**
     * Inicializa festivos de EE.UU. (NYSE) para 2020-2026
     */
    private static void inicializarFestivosUSA() {
        // 2020
        FESTIVOS_USA.add(LocalDate.of(2020, 1, 1));   // New Year's Day
        FESTIVOS_USA.add(LocalDate.of(2020, 1, 20));  // MLK Day
        FESTIVOS_USA.add(LocalDate.of(2020, 2, 17));  // Presidents Day
        FESTIVOS_USA.add(LocalDate.of(2020, 4, 10));  // Good Friday
        FESTIVOS_USA.add(LocalDate.of(2020, 5, 25));  // Memorial Day
        FESTIVOS_USA.add(LocalDate.of(2020, 7, 3));   // Independence Day (observed)
        FESTIVOS_USA.add(LocalDate.of(2020, 9, 7));   // Labor Day
        FESTIVOS_USA.add(LocalDate.of(2020, 11, 26)); // Thanksgiving
        FESTIVOS_USA.add(LocalDate.of(2020, 12, 25)); // Christmas

        // 2021
        FESTIVOS_USA.add(LocalDate.of(2021, 1, 1));   // New Year's Day
        FESTIVOS_USA.add(LocalDate.of(2021, 1, 18));  // MLK Day
        FESTIVOS_USA.add(LocalDate.of(2021, 2, 15));  // Presidents Day
        FESTIVOS_USA.add(LocalDate.of(2021, 4, 2));   // Good Friday
        FESTIVOS_USA.add(LocalDate.of(2021, 5, 31));  // Memorial Day
        FESTIVOS_USA.add(LocalDate.of(2021, 7, 5));   // Independence Day (observed)
        FESTIVOS_USA.add(LocalDate.of(2021, 9, 6));   // Labor Day
        FESTIVOS_USA.add(LocalDate.of(2021, 11, 25)); // Thanksgiving
        FESTIVOS_USA.add(LocalDate.of(2021, 12, 24)); // Christmas (observed)

        // 2022
        FESTIVOS_USA.add(LocalDate.of(2022, 1, 17));  // MLK Day
        FESTIVOS_USA.add(LocalDate.of(2022, 2, 21));  // Presidents Day
        FESTIVOS_USA.add(LocalDate.of(2022, 4, 15));  // Good Friday
        FESTIVOS_USA.add(LocalDate.of(2022, 5, 30));  // Memorial Day
        FESTIVOS_USA.add(LocalDate.of(2022, 7, 4));   // Independence Day
        FESTIVOS_USA.add(LocalDate.of(2022, 9, 5));   // Labor Day
        FESTIVOS_USA.add(LocalDate.of(2022, 11, 24)); // Thanksgiving
        FESTIVOS_USA.add(LocalDate.of(2022, 12, 26)); // Christmas (observed)

        // 2023
        FESTIVOS_USA.add(LocalDate.of(2023, 1, 2));   // New Year's Day (observed)
        FESTIVOS_USA.add(LocalDate.of(2023, 1, 16));  // MLK Day
        FESTIVOS_USA.add(LocalDate.of(2023, 2, 20));  // Presidents Day
        FESTIVOS_USA.add(LocalDate.of(2023, 4, 7));   // Good Friday
        FESTIVOS_USA.add(LocalDate.of(2023, 5, 29));  // Memorial Day
        FESTIVOS_USA.add(LocalDate.of(2023, 7, 4));   // Independence Day
        FESTIVOS_USA.add(LocalDate.of(2023, 9, 4));   // Labor Day
        FESTIVOS_USA.add(LocalDate.of(2023, 11, 23)); // Thanksgiving
        FESTIVOS_USA.add(LocalDate.of(2023, 12, 25)); // Christmas

        // 2024
        FESTIVOS_USA.add(LocalDate.of(2024, 1, 1));   // New Year's Day
        FESTIVOS_USA.add(LocalDate.of(2024, 1, 15));  // MLK Day
        FESTIVOS_USA.add(LocalDate.of(2024, 2, 19));  // Presidents Day
        FESTIVOS_USA.add(LocalDate.of(2024, 3, 29));  // Good Friday
        FESTIVOS_USA.add(LocalDate.of(2024, 5, 27));  // Memorial Day
        FESTIVOS_USA.add(LocalDate.of(2024, 7, 4));   // Independence Day
        FESTIVOS_USA.add(LocalDate.of(2024, 9, 2));   // Labor Day
        FESTIVOS_USA.add(LocalDate.of(2024, 11, 28)); // Thanksgiving
        FESTIVOS_USA.add(LocalDate.of(2024, 12, 25)); // Christmas

        // 2025
        FESTIVOS_USA.add(LocalDate.of(2025, 1, 1));   // New Year's Day
        FESTIVOS_USA.add(LocalDate.of(2025, 1, 20));  // MLK Day
        FESTIVOS_USA.add(LocalDate.of(2025, 2, 17));  // Presidents Day
        FESTIVOS_USA.add(LocalDate.of(2025, 4, 18));  // Good Friday
        FESTIVOS_USA.add(LocalDate.of(2025, 5, 26));  // Memorial Day
        FESTIVOS_USA.add(LocalDate.of(2025, 7, 4));   // Independence Day
        FESTIVOS_USA.add(LocalDate.of(2025, 9, 1));   // Labor Day
        FESTIVOS_USA.add(LocalDate.of(2025, 11, 27)); // Thanksgiving
        FESTIVOS_USA.add(LocalDate.of(2025, 12, 25)); // Christmas

        // 2026
        FESTIVOS_USA.add(LocalDate.of(2026, 1, 1));   // New Year's Day
        FESTIVOS_USA.add(LocalDate.of(2026, 1, 19));  // MLK Day
        FESTIVOS_USA.add(LocalDate.of(2026, 2, 16));  // Presidents Day
        FESTIVOS_USA.add(LocalDate.of(2026, 4, 3));   // Good Friday
        FESTIVOS_USA.add(LocalDate.of(2026, 5, 25));  // Memorial Day
        FESTIVOS_USA.add(LocalDate.of(2026, 7, 3));   // Independence Day (observed)
        FESTIVOS_USA.add(LocalDate.of(2026, 9, 7));   // Labor Day
        FESTIVOS_USA.add(LocalDate.of(2026, 11, 26)); // Thanksgiving
        FESTIVOS_USA.add(LocalDate.of(2026, 12, 25)); // Christmas
    }

    /**
     * Inicializa festivos de Colombia (BVC) para 2020-2026
     */
    private static void inicializarFestivosColombia() {
        // 2020
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 1, 1));   // Año Nuevo
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 1, 6));   // Reyes Magos
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 3, 23));  // San José
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 4, 9));   // Jueves Santo
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 4, 10));  // Viernes Santo
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 5, 1));   // Día del Trabajo
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 5, 25));  // Ascensión
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 6, 15));  // Corpus Christi
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 6, 22));  // Sagrado Corazón
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 6, 29));  // San Pedro y San Pablo
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 7, 20));  // Independencia
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 8, 7));   // Batalla de Boyacá
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 8, 17));  // Asunción
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 10, 12)); // Día de la Raza
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 11, 2));  // Todos los Santos
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 11, 16)); // Independencia de Cartagena
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 12, 8));  // Inmaculada Concepción
        FESTIVOS_COLOMBIA.add(LocalDate.of(2020, 12, 25)); // Navidad

        // 2021-2026 (principales festivos fijos)
        for (int year = 2021; year <= 2026; year++) {
            FESTIVOS_COLOMBIA.add(LocalDate.of(year, 1, 1));   // Año Nuevo
            FESTIVOS_COLOMBIA.add(LocalDate.of(year, 5, 1));   // Día del Trabajo
            FESTIVOS_COLOMBIA.add(LocalDate.of(year, 7, 20));  // Independencia
            FESTIVOS_COLOMBIA.add(LocalDate.of(year, 8, 7));   // Batalla de Boyacá
            FESTIVOS_COLOMBIA.add(LocalDate.of(year, 12, 8));  // Inmaculada Concepción
            FESTIVOS_COLOMBIA.add(LocalDate.of(year, 12, 25)); // Navidad
        }
    }

    /**
     * Verifica si una fecha es día hábil en el mercado especificado
     * 
     * @param fecha Fecha a verificar
     * @param mercado Mercado (USA, COLOMBIA, AMBOS)
     * @return true si es día hábil, false si es fin de semana o festivo
     */
    public static boolean esDiaHabil(LocalDate fecha, Mercado mercado) {
        // Verificar fin de semana
        DayOfWeek dia = fecha.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return false;
        }

        // Verificar festivos según mercado
        switch (mercado) {
            case USA:
                return !FESTIVOS_USA.contains(fecha);
            case COLOMBIA:
                return !FESTIVOS_COLOMBIA.contains(fecha);
            case AMBOS:
                return !FESTIVOS_USA.contains(fecha) && !FESTIVOS_COLOMBIA.contains(fecha);
            default:
                return true;
        }
    }

    /**
     * Obtiene el siguiente día hábil a partir de una fecha
     * 
     * @param fecha Fecha de inicio
     * @param mercado Mercado
     * @return Siguiente día hábil
     */
    public static LocalDate siguienteDiaHabil(LocalDate fecha, Mercado mercado) {
        LocalDate siguiente = fecha.plusDays(1);
        while (!esDiaHabil(siguiente, mercado)) {
            siguiente = siguiente.plusDays(1);
        }
        return siguiente;
    }

    /**
     * Obtiene el día hábil anterior a una fecha
     * 
     * @param fecha Fecha de inicio
     * @param mercado Mercado
     * @return Día hábil anterior
     */
    public static LocalDate anteriorDiaHabil(LocalDate fecha, Mercado mercado) {
        LocalDate anterior = fecha.minusDays(1);
        while (!esDiaHabil(anterior, mercado)) {
            anterior = anterior.minusDays(1);
        }
        return anterior;
    }

    /**
     * Cuenta los días hábiles entre dos fechas (inclusive)
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @param mercado Mercado
     * @return Número de días hábiles
     */
    public static int contarDiasHabiles(LocalDate inicio, LocalDate fin, Mercado mercado) {
        int count = 0;
        LocalDate actual = inicio;
        while (!actual.isAfter(fin)) {
            if (esDiaHabil(actual, mercado)) {
                count++;
            }
            actual = actual.plusDays(1);
        }
        return count;
    }

    /**
     * Verifica si una fecha es festivo en el mercado especificado
     * 
     * @param fecha Fecha a verificar
     * @param mercado Mercado
     * @return true si es festivo
     */
    public static boolean esFestivo(LocalDate fecha, Mercado mercado) {
        switch (mercado) {
            case USA:
                return FESTIVOS_USA.contains(fecha);
            case COLOMBIA:
                return FESTIVOS_COLOMBIA.contains(fecha);
            case AMBOS:
                return FESTIVOS_USA.contains(fecha) || FESTIVOS_COLOMBIA.contains(fecha);
            default:
                return false;
        }
    }
}
