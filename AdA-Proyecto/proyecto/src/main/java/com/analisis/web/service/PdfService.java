package com.analisis.web.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Genera el reporte técnico en PDF (Requerimiento 4 — exportación).
 * Usa iText 5 directamente sin librerías de alto nivel.
 */
@Service
public class PdfService {

    @Autowired
    private AnalisisService analisis;

    // Paleta de colores del dashboard
    private static final BaseColor COLOR_HEADER   = new BaseColor(15, 23, 42);
    private static final BaseColor COLOR_SUBHEADER = new BaseColor(30, 64, 175);
    private static final BaseColor COLOR_CELDA_PAR  = new BaseColor(241, 245, 249);
    private static final BaseColor COLOR_CELDA_IMPAR = BaseColor.WHITE;
    private static final BaseColor COLOR_AGRESIVO   = new BaseColor(220, 38, 38);
    private static final BaseColor COLOR_MODERADO   = new BaseColor(234, 179, 8);
    private static final BaseColor COLOR_CONSERV    = new BaseColor(22, 163, 74);

    public byte[] generarReporte() throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        // Encabezado y pie de página en cada hoja
        writer.setPageEvent(new HeaderFooter());

        doc.open();

        agregarPortada(doc);
        doc.newPage();

        agregarSeccion1Correlacion(doc);
        doc.newPage();

        agregarSeccion2Volatilidad(doc);
        doc.newPage();

        agregarSeccion3Explicacion(doc);

        doc.close();
        return baos.toByteArray();
    }

    // -----------------------------------------------------------------------
    private void agregarPortada(Document doc) throws DocumentException {
        // Título
        Font fTitulo = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, BaseColor.WHITE);
        Font fSub    = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(148, 163, 184));
        Font fFecha  = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, new BaseColor(100, 116, 139));

        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_HEADER);
        cell.setPadding(40);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph titulo = new Paragraph("Dashboard Bursátil\nReporte Técnico de Análisis", fTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setLeading(0, 1.4f);
        cell.addElement(titulo);

        Paragraph sub = new Paragraph("\nAnálisis de Algoritmos — Universidad del Quindío", fSub);
        sub.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(sub);

        String ahora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph fecha = new Paragraph("\nGenerado: " + ahora, fFecha);
        fecha.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(fecha);

        banner.addCell(cell);
        doc.add(banner);

        doc.add(Chunk.NEWLINE);

        // Activos analizados
        agregarTituloSeccion(doc, "Activos Analizados");
        List<String> simbolos = analisis.getSimbolos();
        Font fSim = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_SUBHEADER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < simbolos.size(); i++) {
            sb.append(simbolos.get(i));
            if (i < simbolos.size() - 1) sb.append("  ·  ");
        }
        Paragraph p = new Paragraph(sb.toString(), fSim);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);

        doc.add(Chunk.NEWLINE);
        agregarParrafo(doc,
            "Este reporte consolida los resultados del Requerimiento 4 del proyecto de " +
            "Análisis de Algoritmos. Incluye la matriz de correlación entre activos, " +
            "la clasificación por riesgo basada en volatilidad histórica y la " +
            "fundamentación matemática de los algoritmos implementados.");
    }

    // -----------------------------------------------------------------------
    private void agregarSeccion1Correlacion(Document doc) throws DocumentException {
        agregarTituloSeccion(doc, "1. Matriz de Correlación de Pearson");

        agregarParrafo(doc,
            "La matriz muestra el coeficiente de correlación de Pearson calculado sobre los " +
            "retornos logarítmicos diarios de cada par de activos. Valores cercanos a 1 indican " +
            "alta correlación positiva; cercanos a -1 indican correlación negativa; " +
            "cercanos a 0 indican independencia estadística.");

        java.util.List<String> simbolos = analisis.getSimbolos();
        Map<String, Map<String, Double>> matriz = analisis.calcularMatrizCorrelacion();

        // Tabla: columna de etiqueta + una columna por símbolo
        int cols = simbolos.size() + 1;
        PdfPTable tabla = new PdfPTable(cols);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(8);

        // Cabecera
        Font fCab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
        agregarCeldaTabla(tabla, "", fCab, COLOR_HEADER, Element.ALIGN_CENTER);
        for (String s : simbolos) {
            agregarCeldaTabla(tabla, s, fCab, COLOR_HEADER, Element.ALIGN_CENTER);
        }

        // Filas
        Font fVal = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, COLOR_HEADER);
        Font fEtq = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);

        for (int i = 0; i < simbolos.size(); i++) {
            String sA = simbolos.get(i);
            agregarCeldaTabla(tabla, sA, fEtq, COLOR_SUBHEADER, Element.ALIGN_CENTER);
            for (String sB : simbolos) {
                double val = matriz.getOrDefault(sA, Collections.emptyMap()).getOrDefault(sB, 0.0);
                BaseColor bg = colorCorrelacion(val, sA.equals(sB));
                PdfPCell cell = new PdfPCell(new Phrase(String.format("%.2f", val), fVal));
                cell.setBackgroundColor(bg);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(3);
                cell.setBorderColor(new BaseColor(226, 232, 240));
                tabla.addCell(cell);
            }
        }
        doc.add(tabla);
    }

    private BaseColor colorCorrelacion(double val, boolean diagonal) {
        if (diagonal) return new BaseColor(30, 64, 175);
        if (val > 0.7)  return new BaseColor(220, 252, 231);
        if (val > 0.4)  return new BaseColor(240, 253, 244);
        if (val < -0.4) return new BaseColor(254, 226, 226);
        return BaseColor.WHITE;
    }

    // -----------------------------------------------------------------------
    private void agregarSeccion2Volatilidad(Document doc) throws DocumentException {
        agregarTituloSeccion(doc, "2. Clasificación por Volatilidad e Indicadores de Riesgo");

        agregarParrafo(doc,
            "La volatilidad histórica anualizada se calcula como la desviación estándar " +
            "muestral de los retornos logarítmicos diarios multiplicada por √252 " +
            "(días de trading en un año). Los activos se clasifican en: " +
            "CONSERVADOR (< 15%), MODERADO (15–30%) y AGRESIVO (> 30%).");

        List<Map<String, Object>> clasificacion = analisis.getClasificacionRiesgo();

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(90);
        tabla.setSpacingBefore(8);
        tabla.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.setWidths(new float[]{2f, 2.5f, 2.5f, 3f});

        Font fCab = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
        Font fVal = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, COLOR_HEADER);
        Font fCat = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);

        for (String cab : new String[]{"Símbolo", "Volatilidad (%)", "Precio Cierre", "Categoría"}) {
            agregarCeldaTabla(tabla, cab, fCab, COLOR_HEADER, Element.ALIGN_CENTER);
        }

        for (int i = 0; i < clasificacion.size(); i++) {
            Map<String, Object> item = clasificacion.get(i);
            String sim  = (String) item.get("simbolo");
            double vol  = (Double) item.get("volatilidad");
            String cat  = (String) item.get("categoria");
            Map<String, Object> resumen = analisis.getResumenActivo(sim);
            double precio = resumen.containsKey("ultimoCierre") ? (Double) resumen.get("ultimoCierre") : 0;

            BaseColor bgFila = i % 2 == 0 ? COLOR_CELDA_PAR : COLOR_CELDA_IMPAR;
            BaseColor bgCat  = cat.equals("AGRESIVO") ? COLOR_AGRESIVO
                             : cat.equals("MODERADO") ? COLOR_MODERADO : COLOR_CONSERV;

            agregarCeldaTabla(tabla, sim,                    fVal, bgFila, Element.ALIGN_CENTER);
            agregarCeldaTabla(tabla, String.format("%.2f%%", vol), fVal, bgFila, Element.ALIGN_CENTER);
            agregarCeldaTabla(tabla, String.format("$%.2f", precio), fVal, bgFila, Element.ALIGN_CENTER);
            agregarCeldaTabla(tabla, cat, fCat, bgCat, Element.ALIGN_CENTER);
        }

        doc.add(tabla);
    }

    // -----------------------------------------------------------------------
    private void agregarSeccion3Explicacion(Document doc) throws DocumentException {
        agregarTituloSeccion(doc, "3. Fundamentos Matemáticos y Algorítmicos");

        agregarSubtitulo(doc, "3.1 Correlación de Pearson");
        agregarParrafo(doc,
            "Fórmula: r = Σ((xᵢ − x̄)(yᵢ − ȳ)) / √(Σ(xᵢ−x̄)² · Σ(yᵢ−ȳ)²)\n" +
            "Complejidad: O(n) por par de activos, O(s²·n) para la matriz completa.\n" +
            "Aplicada sobre retornos logarítmicos diarios: rₜ = ln(Pₜ / Pₜ₋₁)");

        agregarSubtitulo(doc, "3.2 Media Móvil Simple — Ventana Deslizante");
        agregarParrafo(doc,
            "Algoritmo de ventana deslizante:\n" +
            "  • Inicialización: suma = Σ Pᵢ para i ∈ [0, w-1]\n" +
            "  • Avance: suma = suma − P[i−w] + P[i+1]\n" +
            "  • SMA[i] = suma / w\n" +
            "Complejidad: O(n) — sin recalcular la suma completa en cada paso.\n" +
            "Ventanas implementadas: SMA-20 (corto plazo) y SMA-50 (mediano plazo).");

        agregarSubtitulo(doc, "3.3 Volatilidad Histórica Anualizada");
        agregarParrafo(doc,
            "Fórmula:\n" +
            "  σ = √(Σ(rᵢ − r̄)² / (n−1)) × √252\n" +
            "donde r̄ = media de retornos, 252 = días de trading anuales.\n" +
            "Complejidad: O(n) por activo, O(s·n) para todos los activos.\n\n" +
            "Umbrales de clasificación:\n" +
            "  • σ < 15%  → CONSERVADOR\n" +
            "  • 15% ≤ σ < 30% → MODERADO\n" +
            "  • σ ≥ 30%  → AGRESIVO");

        agregarSubtitulo(doc, "3.4 Gráfico de Velas (Candlestick)");
        agregarParrafo(doc,
            "Cada vela representa un día de trading con cuatro valores:\n" +
            "  • Apertura (Open): precio al inicio de la sesión\n" +
            "  • Máximo (High): precio más alto durante la sesión\n" +
            "  • Mínimo (Low): precio más bajo durante la sesión\n" +
            "  • Cierre (Close): precio al final de la sesión\n\n" +
            "Vela alcista (verde): Cierre > Apertura\n" +
            "Vela bajista (roja):  Cierre ≤ Apertura");
    }

    // -----------------------------------------------------------------------
    // Helpers de formato
    // -----------------------------------------------------------------------
    private void agregarTituloSeccion(Document doc, String texto) throws DocumentException {
        Font f = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(12);
        t.setSpacingAfter(8);
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setBackgroundColor(COLOR_HEADER);
        c.setPadding(8);
        c.setBorder(Rectangle.NO_BORDER);
        t.addCell(c);
        doc.add(t);
    }

    private void agregarSubtitulo(Document doc, String texto) throws DocumentException {
        Font f = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, COLOR_SUBHEADER);
        Paragraph p = new Paragraph(texto, f);
        p.setSpacingBefore(10);
        p.setSpacingAfter(4);
        doc.add(p);
    }

    private void agregarParrafo(Document doc, String texto) throws DocumentException {
        Font f = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, COLOR_HEADER);
        Paragraph p = new Paragraph(texto, f);
        p.setLeading(0, 1.5f);
        p.setSpacingAfter(6);
        doc.add(p);
    }

    private void agregarCeldaTabla(PdfPTable tabla, String texto, Font fuente,
                                    BaseColor fondo, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBackgroundColor(fondo);
        cell.setHorizontalAlignment(alineacion);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderColor(new BaseColor(226, 232, 240));
        tabla.addCell(cell);
    }

    // -----------------------------------------------------------------------
    // Header y footer en cada página
    // -----------------------------------------------------------------------
    private static class HeaderFooter extends PdfPageEventHelper {
        private Font fPie = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL,
                                      new BaseColor(100, 116, 139));

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            // Línea superior
            cb.setColorStroke(new BaseColor(15, 23, 42));
            cb.setLineWidth(2f);
            cb.moveTo(document.left(), document.top() + 10);
            cb.lineTo(document.right(), document.top() + 10);
            cb.stroke();

            // Pie: título + número de página
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                new Phrase("Dashboard Financiero — Análisis de Algoritmos", fPie),
                document.left(), document.bottom() - 15, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                new Phrase("Página " + writer.getPageNumber(), fPie),
                document.right(), document.bottom() - 15, 0);
        }
    }
}
