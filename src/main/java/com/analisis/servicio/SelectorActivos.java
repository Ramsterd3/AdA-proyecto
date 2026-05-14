package com.analisis.servicio;

import com.analisis.modelo.DatoFinanciero;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Permite la seleccion interactiva de activos para analisis de similitud
 */
public class SelectorActivos {

    private final BufferedReader reader;

    public SelectorActivos() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Obtiene la lista de simbolos unicos disponibles en los datos
     * 
     * @param datos Lista de datos financieros
     * @return Lista ordenada de simbolos unicos
     */
    public List<String> obtenerSimbolosDisponibles(List<DatoFinanciero> datos) {
        return datos.stream()
            .map(DatoFinanciero::getSimbolo)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * Muestra el menu de seleccion de activos y retorna el par seleccionado
     * 
     * @param datos Lista de datos financieros
     * @return Array con dos simbolos [simboloA, simboloB]
     */
    public String[] seleccionarActivosInteractivo(List<DatoFinanciero> datos) {
        List<String> simbolos = obtenerSimbolosDisponibles(datos);
        
        if (simbolos.isEmpty()) {
            System.err.println("No hay datos disponibles para analizar");
            return new String[]{"AAPL", "MSFT"}; // Default
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("SELECCION DE ACTIVOS PARA ANALISIS DE SIMILITUD");
        System.out.println("=".repeat(60));
        
        mostrarSimbolosDisponibles(simbolos);
        
        String simboloA = seleccionarSimbolo(simbolos, "primer");
        String simboloB = seleccionarSimbolo(simbolos, "segundo", simboloA);
        
        System.out.println("\n✓ Activos seleccionados: " + simboloA + " y " + simboloB);
        
        return new String[]{simboloA, simboloB};
    }

    /**
     * Muestra la lista de simbolos disponibles en formato de tabla
     * 
     * @param simbolos Lista de simbolos
     */
    private void mostrarSimbolosDisponibles(List<String> simbolos) {
        System.out.println("\nActivos disponibles (" + simbolos.size() + " total):");
        System.out.println("-".repeat(60));
        
        int columnas = 5;
        for (int i = 0; i < simbolos.size(); i++) {
            System.out.printf("%-3d. %-8s", i + 1, simbolos.get(i));
            if ((i + 1) % columnas == 0 || i == simbolos.size() - 1) {
                System.out.println();
            }
        }
        System.out.println("-".repeat(60));
    }

    /**
     * Solicita al usuario que seleccione un simbolo
     * 
     * @param simbolos Lista de simbolos disponibles
     * @param orden "primer" o "segundo"
     * @return Simbolo seleccionado
     */
    private String seleccionarSimbolo(List<String> simbolos, String orden) {
        return seleccionarSimbolo(simbolos, orden, null);
    }

    /**
     * Solicita al usuario que seleccione un simbolo
     * 
     * @param simbolos Lista de simbolos disponibles
     * @param orden "primer" o "segundo"
     * @param excluir Simbolo a excluir (para evitar seleccionar el mismo dos veces)
     * @return Simbolo seleccionado
     */
    private String seleccionarSimbolo(List<String> simbolos, String orden, String excluir) {
        while (true) {
            try {
                System.out.print("\nSeleccione el " + orden + " activo (numero o simbolo): ");
                String input = reader.readLine().trim().toUpperCase();
                
                if (input.isEmpty()) {
                    System.out.println("⚠ Entrada vacia. Intente nuevamente.");
                    continue;
                }
                
                // Intentar parsear como numero
                try {
                    int indice = Integer.parseInt(input);
                    if (indice < 1 || indice > simbolos.size()) {
                        System.out.println("⚠ Numero fuera de rango. Debe estar entre 1 y " + simbolos.size());
                        continue;
                    }
                    String simbolo = simbolos.get(indice - 1);
                    if (simbolo.equals(excluir)) {
                        System.out.println("⚠ No puede seleccionar el mismo activo dos veces. Elija otro.");
                        continue;
                    }
                    return simbolo;
                } catch (NumberFormatException e) {
                    // No es un numero, intentar como simbolo
                    if (simbolos.contains(input)) {
                        if (input.equals(excluir)) {
                            System.out.println("⚠ No puede seleccionar el mismo activo dos veces. Elija otro.");
                            continue;
                        }
                        return input;
                    } else {
                        System.out.println("⚠ Simbolo '" + input + "' no encontrado. Intente nuevamente.");
                        System.out.println("   Simbolos validos: " + String.join(", ", simbolos));
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al leer entrada: " + e.getMessage());
                return simbolos.get(0); // Default al primero
            }
        }
    }

    /**
     * Pregunta al usuario si desea analizar otro par de activos
     * 
     * @return true si desea continuar, false si no
     */
    public boolean deseaContinuar() {
        try {
            System.out.print("\n¿Desea analizar otro par de activos? (s/n): ");
            String respuesta = reader.readLine().trim().toLowerCase();
            return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("y") || respuesta.equals("yes");
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Muestra un menu de opciones de analisis
     * 
     * @return Opcion seleccionada (1-4)
     */
    public int mostrarMenuAnalisis() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("MENU DE ANALISIS");
        System.out.println("=".repeat(60));
        System.out.println("1. Analizar similitud entre dos activos");
        System.out.println("2. Analizar todos los pares de activos");
        System.out.println("3. Buscar activos mas similares a uno dado");
        System.out.println("4. Salir");
        System.out.println("=".repeat(60));
        
        while (true) {
            try {
                System.out.print("Seleccione una opcion (1-4): ");
                String input = reader.readLine().trim();
                int opcion = Integer.parseInt(input);
                if (opcion >= 1 && opcion <= 4) {
                    return opcion;
                } else {
                    System.out.println("⚠ Opcion invalida. Debe estar entre 1 y 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Entrada invalida. Ingrese un numero entre 1 y 4.");
            } catch (IOException e) {
                System.err.println("Error al leer entrada: " + e.getMessage());
                return 4; // Salir por defecto
            }
        }
    }

    /**
     * Selecciona un unico activo para buscar similares
     * 
     * @param simbolos Lista de simbolos disponibles
     * @return Simbolo seleccionado
     */
    public String seleccionarActivoUnico(List<String> simbolos) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("BUSCAR ACTIVOS SIMILARES");
        System.out.println("=".repeat(60));
        
        mostrarSimbolosDisponibles(simbolos);
        
        return seleccionarSimbolo(simbolos, "activo de referencia");
    }

    /**
     * Cierra el lector de entrada
     */
    public void cerrar() {
        try {
            reader.close();
        } catch (IOException e) {
            // Ignorar error al cerrar
        }
    }
}
