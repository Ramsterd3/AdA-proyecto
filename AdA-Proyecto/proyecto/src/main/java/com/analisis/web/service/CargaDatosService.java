package com.analisis.web.service;

import com.analisis.web.model.DatoFinanciero;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Carga y expone los datos del archivo datos_unificados.csv generado
 * por el proyecto principal (Requerimiento 1).
 *
 * El CSV tiene el formato: fecha,simbolo,apertura,maximo,minimo,cierre,volumen
 * PERO los doubles se guardan con punto decimal que en Java se convierte a coma
 * al hacer toString(), generando columnas extra. Se maneja el parseo manual.
 */
@Service
public class CargaDatosService {

    @Value("${datos.ruta:./datos}")
    private String rutaDatos;

    private List<DatoFinanciero> datos = new ArrayList<>();
    private List<String> simbolos = new ArrayList<>();

    @PostConstruct
    public void cargar() {
        File archivo = resolverArchivo("datos_unificados.csv");
        if (!archivo.exists()) {
            System.out.println("[CargaDatos] No se encontró datos_unificados.csv en: " + archivo.getAbsolutePath());
            System.out.println("[CargaDatos] Usando datos de ejemplo para demostración.");
            datos = generarDatosEjemplo();
        } else {
            datos = leerCsv(archivo);
        }
        simbolos = datos.stream()
                .map(DatoFinanciero::getSimbolo)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("[CargaDatos] Cargados " + datos.size() + " registros, " + simbolos.size() + " activos.");
    }

    public List<DatoFinanciero> getDatos() { return datos; }
    public List<String> getSimbolos()      { return simbolos; }

    public List<DatoFinanciero> getDatosPorSimbolo(String simbolo) {
        return datos.stream()
                .filter(d -> d.getSimbolo().equals(simbolo))
                .sorted(Comparator.comparing(DatoFinanciero::getFecha))
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------
    // Parseo del CSV con doubles que pueden haber quedado separados por coma
    // -----------------------------------------------------------------------
    private List<DatoFinanciero> leerCsv(File archivo) {
        List<DatoFinanciero> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {

            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (primera) { primera = false; continue; } // saltar cabecera

                String[] cols = linea.split(",");
                // Formato esperado: fecha(0), simbolo(1), apertura(2[.3]), maximo([3]4), minimo([4]5), cierre([5]6), volumen(ultimo)
                // Con decimales partidos: fecha,simbolo,int,dec,int,dec,int,dec,int,dec,volumen = 11 cols
                // Sin decimales partidos: fecha,simbolo,d,d,d,d,volumen = 7 cols
                try {
                    if (cols.length == 7) {
                        // Formato correcto (punto decimal)
                        lista.add(new DatoFinanciero(
                            cols[0].trim(), cols[1].trim(),
                            Double.parseDouble(cols[2].trim()),
                            Double.parseDouble(cols[3].trim()),
                            Double.parseDouble(cols[4].trim()),
                            Double.parseDouble(cols[5].trim()),
                            Long.parseLong(cols[6].trim())
                        ));
                    } else if (cols.length >= 11) {
                        // Formato con decimales partidos por coma
                        lista.add(new DatoFinanciero(
                            cols[0].trim(), cols[1].trim(),
                            parseDouble(cols[2], cols[3]),
                            parseDouble(cols[4], cols[5]),
                            parseDouble(cols[6], cols[7]),
                            parseDouble(cols[8], cols[9]),
                            Long.parseLong(cols[10].trim())
                        ));
                    }
                } catch (Exception e) {
                    // Ignorar líneas mal formadas
                }
            }
        } catch (IOException e) {
            System.err.println("[CargaDatos] Error leyendo CSV: " + e.getMessage());
        }
        return lista;
    }

    private double parseDouble(String entero, String decimal) {
        return Double.parseDouble(entero.trim() + "." + decimal.trim());
    }

    private File resolverArchivo(String nombre) {
        // 1. En la ruta configurada
        File f = new File(rutaDatos, nombre);
        if (f.exists()) return f;
        // 2. En el directorio actual
        f = new File(nombre);
        if (f.exists()) return f;
        // 3. En el directorio padre (si se ejecuta desde target/)
        f = new File("../" + nombre);
        if (f.exists()) return f;
        return new File(rutaDatos, nombre);
    }

    // -----------------------------------------------------------------------
    // Datos de ejemplo para poder levantar el dashboard sin el CSV
    // -----------------------------------------------------------------------
    private List<DatoFinanciero> generarDatosEjemplo() {
        List<DatoFinanciero> lista = new ArrayList<>();
        String[] syms = {"AAPL","MSFT","GOOGL","AMZN","TSLA","NVDA","META","JPM","JNJ","V",
                         "UNH","PG","HD","MA","NFLX","ADBE","PYPL","CSCO","INTC","DIS"};
        Random rnd = new Random(42);

        for (String sym : syms) {
            double precio = 100 + rnd.nextDouble() * 400;
            // 5 años de datos diarios ≈ 1260 días
            java.time.LocalDate fecha = java.time.LocalDate.of(2020, 1, 2);
            for (int i = 0; i < 1260; i++) {
                double cambio = (rnd.nextDouble() - 0.48) * precio * 0.03;
                precio = Math.max(10, precio + cambio);
                double open  = precio * (1 + (rnd.nextDouble()-0.5)*0.01);
                double high  = precio * (1 + rnd.nextDouble()*0.015);
                double low   = precio * (1 - rnd.nextDouble()*0.015);
                long vol = (long)(rnd.nextDouble() * 50_000_000) + 5_000_000;
                lista.add(new DatoFinanciero(fecha.toString(), sym,
                    round2(open), round2(high), round2(low), round2(precio), vol));
                fecha = fecha.plusDays(fecha.getDayOfWeek().getValue() >= 5 ? (8 - fecha.getDayOfWeek().getValue()) : 1);
            }
        }
        return lista;
    }

    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}
