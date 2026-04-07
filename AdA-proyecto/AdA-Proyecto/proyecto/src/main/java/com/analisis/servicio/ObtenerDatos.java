package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ObtenerDatos {
    
    private static final String API_KEY = "a8e75ce6a8bf4bf7b8744106d9cdfe71";
    private static final String BASE_URL = "https://api.twelvedata.com/time_series?interval=1day";
    
    private static final String[] SIMBOLOS = {
        "AAPL", "MSFT", "GOOGL", "AMZN", "NVDA", "META", "TSLA", "JPM", "JNJ", "V",
        "PG", "UNH", "HD", "MA", "DIS", "PYPL", "ADBE", "NFLX", "INTC", "CSCO"
    };

    public List<DatoFinanciero> obtenerTodosLosDatos(int anios) {
        List<DatoFinanciero> todos = new ArrayList<>();
        
        System.out.println("Descargando " + SIMBOLOS.length + " activos...");
        
        for (int i = 0; i < SIMBOLOS.length; i++) {
            String simbolo = SIMBOLOS[i];
            System.out.print("Descargando " + simbolo + " (" + (i+1) + "/" + SIMBOLOS.length + ")... ");
            
            List<DatoFinanciero> datos = obtenerDatosSimbolo(simbolo, anios);
            
            if (datos.isEmpty()) {
                System.out.println("ERROR: Sin datos");
            } else {
                System.out.println(datos.size() + " registros");
            }
            
            todos.addAll(datos);
            
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {}
        }
        
        return todos;
    }
    
    public List<DatoFinanciero> obtenerDatosSimbolo(String simbolo, int anios) {
        List<DatoFinanciero> datos = new ArrayList<>();
        
        try {
            LocalDate fechaFin = LocalDate.now();
            LocalDate fechaInicio = fechaFin.minusYears(anios);
            
            String urlStr = BASE_URL + "&symbol=" + simbolo + 
                           "&start_date=" + fechaInicio + 
                           "&end_date=" + fechaFin +
                           "&outputsize=5000" +
                           "&apikey=" + API_KEY;
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("HTTP " + responseCode);
                return datos;
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
            );
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String json = response.toString();
            
            if (json.contains("\"status\":\"error\"") || json.contains("\"code\":")) {
                System.err.println("API Error");
                return datos;
            }
            
            if (json.contains("\"status\":\"error\"") || (json.contains("\"code\":") && json.contains("429"))) {
                System.err.println("API Error o rate limit");
                return datos;
            }
            
            int valuesStart = json.indexOf("\"values\":[");
            if (valuesStart == -1) {
                System.err.println("No se encontró values");
                return datos;
            }
            
            int arrayStart = json.indexOf("[", valuesStart + 9);
            if (arrayStart == -1) {
                System.err.println("No se encontró array");
                return datos;
            }
            
            String valuesArray = json.substring(arrayStart + 1);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            String[] entries = valuesArray.split("\\}");
            for (String entry : entries) {
                if (!entry.contains("datetime")) continue;
                entry = entry + "}";
                if (entry.length() < 10) continue;
                
                int dtStart = entry.indexOf("datetime");
                if (dtStart == -1) continue;
                
                int dtEnd = entry.indexOf(",", dtStart);
                if (dtEnd == -1) dtEnd = entry.indexOf("}", dtStart);
                String fechaStr = entry.substring(dtStart + 11, dtEnd);
                fechaStr = fechaStr.replace("\"", "").trim();
                
                if (!fechaStr.matches("\\d{4}-\\d{2}-\\d{2}")) continue;
                
                LocalDate fecha = LocalDate.parse(fechaStr, formatter);
                
                double open = extractValueTV(entry, "open");
                double high = extractValueTV(entry, "high");
                double low = extractValueTV(entry, "low");
                double close = extractValueTV(entry, "close");
                long volume = (long) extractValueTV(entry, "volume");
                
                if (close > 0) {
                    DatoFinanciero dato = new DatoFinanciero(
                        fecha.toString(), simbolo,
                        open, high, low, close, volume
                    );
                    datos.add(dato);
                }
            }
            
            System.out.println("DEBUG: datos parseados: " + datos.size());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        return datos;
    }
    
    private double extractValue(String dataBlock, String key) {
        int keyPos = dataBlock.indexOf(key);
        if (keyPos == -1) return 0.0;
        
        int valueStart = keyPos + key.length();
        while (valueStart < dataBlock.length() && 
               (dataBlock.charAt(valueStart) == ' ' || dataBlock.charAt(valueStart) == '\"')) {
            valueStart++;
        }
        
        int valueEnd = valueStart;
        while (valueEnd < dataBlock.length() && 
               dataBlock.charAt(valueEnd) != ',' && 
               dataBlock.charAt(valueEnd) != '}' && 
               dataBlock.charAt(valueEnd) != '"') {
            valueEnd++;
        }
        
        try {
            return Double.parseDouble(dataBlock.substring(valueStart, valueEnd).trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private double extractValueTV(String dataBlock, String key) {
        int keyPos = dataBlock.indexOf("\"" + key + "\":");
        if (keyPos == -1) return 0.0;
        
        int valueStart = keyPos + key.length() + 4;
        while (valueStart < dataBlock.length() && 
               (dataBlock.charAt(valueStart) == ' ' || dataBlock.charAt(valueStart) == '\"')) {
            valueStart++;
        }
        
        int valueEnd = valueStart;
        while (valueEnd < dataBlock.length() && 
               dataBlock.charAt(valueEnd) != ',' && 
               dataBlock.charAt(valueEnd) != '}' && 
               dataBlock.charAt(valueEnd) != '"') {
            valueEnd++;
        }
        
        try {
            return Double.parseDouble(dataBlock.substring(valueStart, valueEnd).trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private static List<Double> extractArray(String json, int start, String keyOpen, String keyClose) {
        List<Double> result = new ArrayList<>();
        int pos = json.indexOf(keyOpen, start);
        if (pos == -1) return result;
        
        int arrStart = json.indexOf("[", pos);
        int arrEnd = json.indexOf(keyClose, arrStart);
        if (arrStart == -1 || arrEnd == -1) return result;
        
        String arrayContent = json.substring(arrStart + 1, arrEnd);
        String[] values = arrayContent.split(",");
        
        for (String v : values) {
            v = v.trim();
            if (v.equals("null") || v.isEmpty()) {
                result.add(null);
            } else {
                try { result.add(Double.parseDouble(v)); } catch (Exception e) { result.add(null); }
            }
        }
        return result;
    }
    
    private static List<Long> extractLongArray(String json, int start, String keyOpen, String keyClose) {
        List<Long> result = new ArrayList<>();
        int pos = json.indexOf(keyOpen, start);
        if (pos == -1) return result;
        
        int arrStart = json.indexOf("[", pos);
        int arrEnd = json.indexOf(keyClose, arrStart);
        if (arrStart == -1 || arrEnd == -1) return result;
        
        String arrayContent = json.substring(arrStart + 1, arrEnd);
        String[] values = arrayContent.split(",");
        
        for (String v : values) {
            v = v.trim();
            if (v.equals("null") || v.isEmpty()) {
                result.add(0L);
            } else {
                try { result.add(Long.parseLong(v)); } catch (Exception e) { result.add(0L); }
            }
        }
        return result;
    }
}