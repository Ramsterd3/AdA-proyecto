package com.analisis.web.controller;

import com.analisis.web.service.AnalisisService;
import com.analisis.web.service.PdfService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class DashboardController {

    @Autowired private AnalisisService analisis;
    @Autowired private PdfService pdfService;

    // -----------------------------------------------------------------------
    // Vista principal
    // -----------------------------------------------------------------------
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("simbolos", analisis.getSimbolos());
        model.addAttribute("totalActivos", analisis.getSimbolos().size());
        return "dashboard";
    }

    // -----------------------------------------------------------------------
    // API REST — datos para los gráficos (Highcharts / Chart.js via AJAX)
    // -----------------------------------------------------------------------

    /** Devuelve la matriz de correlación como JSON */
    @GetMapping("/api/correlacion")
    @ResponseBody
    public Map<String, Object> correlacion() {
        List<String> simbolos = analisis.getSimbolos();
        Map<String, Map<String, Double>> matriz = analisis.calcularMatrizCorrelacion();

        // Serializar como lista de [fila, col, valor] para Highcharts heatmap
        List<List<Object>> series = new ArrayList<>();
        for (int i = 0; i < simbolos.size(); i++) {
            for (int j = 0; j < simbolos.size(); j++) {
                double val = matriz.getOrDefault(simbolos.get(i), Collections.emptyMap())
                                   .getOrDefault(simbolos.get(j), 0.0);
                series.add(Arrays.asList(j, i, Math.round(val * 100.0) / 100.0));
            }
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("simbolos", simbolos);
        resp.put("datos", series);
        return resp;
    }

    /** Devuelve datos OHLCV + SMA para gráfico de velas */
    @GetMapping("/api/velas/{simbolo}")
    @ResponseBody
    public Map<String, Object> velas(@PathVariable String simbolo,
                                     @RequestParam(defaultValue = "180") int dias) {
        List<Map<String, Object>> ohlcv = analisis.getCandlestickData(simbolo, dias);
        List<Map<String, Object>> sma20 = analisis.calcularSMA(simbolo, 20, dias);
        List<Map<String, Object>> sma50 = analisis.calcularSMA(simbolo, 50, dias);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("simbolo", simbolo);
        resp.put("ohlcv", ohlcv);
        resp.put("sma20", sma20);
        resp.put("sma50", sma50);
        resp.put("resumen", analisis.getResumenActivo(simbolo));
        return resp;
    }

    /** Devuelve clasificación de riesgo de todos los activos */
    @GetMapping("/api/riesgo")
    @ResponseBody
    public List<Map<String, Object>> riesgo() {
        return analisis.getClasificacionRiesgo();
    }

    /** Devuelve lista de símbolos disponibles */
    @GetMapping("/api/simbolos")
    @ResponseBody
    public List<String> simbolos() {
        return analisis.getSimbolos();
    }

    // -----------------------------------------------------------------------
    // Exportar PDF
    // -----------------------------------------------------------------------
    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdf() {
        try {
            byte[] pdf = pdfService.generarReporte();
            String nombre = "reporte-bursatil-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".pdf";

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        } catch (DocumentException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
