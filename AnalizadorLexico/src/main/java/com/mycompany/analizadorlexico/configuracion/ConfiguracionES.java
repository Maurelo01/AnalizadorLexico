/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlexico.configuracion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfiguracionES 
{
    private static final Path RUTA_POR_DEFECTO = Path.of(System.getProperty("user.home"), ".analizador", "config.json"); //
    private static final String JSON_POR_DEFECTO = """
    {
      "palabrasReservadas": ["if", "else", "while", "for", "return"],
      "operadores": ["+", "-", "*", "/", "=", "==", "!=", "<", ">", "<=", ">="],
      "agrupacion": ["(", ")", "{", "}", "[", "]"],
      "puntuacion": [",", ";", ":", "."],
      "comentarioLinea": "//",
      "comentarioBloqueInicio": "/*",
      "comentarioBloqueFin": "*/",
      "colores": {
        "RESERVADA": "#325FA9",
        "IDENTIFICADOR": "#5A3E2D",
        "NUMERO": "#8AC850",
        "DECIMAL": "#000000",
        "CADENA": "#FFA800",
        "OPERADOR": "#FFDE21",
        "AGRUPACION": "#6E3D6F",
        "PUNTUACION": "#DC143C",
        "COMENTARIO": "#006400",
        "ERROR": "#FF0000"
      }
    }
    """;   
    
    public static void asegurarArchivoConfiguracion() throws IOException 
    {
        Path dir = RUTA_POR_DEFECTO.getParent();
        if (dir != null && !Files.exists(dir)) 
        {
            Files.createDirectories(dir);
        }
        if (!Files.exists(RUTA_POR_DEFECTO)) 
        {
            Files.writeString(RUTA_POR_DEFECTO, JSON_POR_DEFECTO, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        }
    }
    
    public static ConfiguracionLexica cargar() throws Exception
    {
        asegurarArchivoConfiguracion();
        String json = Files.readString(RUTA_POR_DEFECTO, StandardCharsets.UTF_8);
        return parsear(json);
    }
    
    public static ConfiguracionLexica cargarPorDefecto() throws Exception 
    {
        return parsear(JSON_POR_DEFECTO);
    }
    
    public static void guardar(ConfiguracionLexica configuracionLexica) throws IOException
    {
        String json = aJson(configuracionLexica);
        if (!Files.exists(RUTA_POR_DEFECTO.getParent()))  // Asegura que exista el directorio antes de guardar
        {
            Files.createDirectories(RUTA_POR_DEFECTO.getParent());
        }
        Files.writeString(RUTA_POR_DEFECTO, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }
    
    public static String rutaPorDefecto() 
    {
        return RUTA_POR_DEFECTO.toString();
    }
    
    private static ConfiguracionLexica parsear(String json) throws Exception 
    {
        JsonReader lector = new JsonReader(json);
        ConfiguracionLexica configuracionLexica = new ConfiguracionLexica();
        lector.espaciosEnBlanco(); 
        lector.espera('{');
        boolean primero = true;
        while (true) 
        {
            lector.espaciosEnBlanco();
            if (lector.consumido('}')) break;
            if (!primero) 
            {
                lector.espera(',');
            }
            primero = false;
            String clave = lector.parseString(); 
            lector.espaciosEnBlanco(); 
            lector.espera(':'); 
            lector.espaciosEnBlanco();
            switch (clave) 
            {
                case "palabrasReservadas" -> configuracionLexica.setPalabrasReservadas(lector.parseArrayOfStrings());
                case "operadores" -> configuracionLexica.setOperadores(lector.parseArrayOfStrings());
                case "agrupacion" -> configuracionLexica.setAgrupacion(lector.parseArrayOfStrings());
                case "puntuacion" -> configuracionLexica.setPuntuacion(lector.parseArrayOfStrings());
                case "comentarioLinea" -> configuracionLexica.setComentarioLinea(lector.parseString());
                case "comentarioBloqueInicio" -> configuracionLexica.setComentarioBloqueInicio(lector.parseString());
                case "comentarioBloqueFin" -> configuracionLexica.setComentarioBloqueFin(lector.parseString());
                case "colores" -> configuracionLexica.setColores(lector.parseObjectStringToString());
                default -> lector.saltarValorGenerico();
            }
        }
        return configuracionLexica;
    }
    
    private static String aJson(ConfiguracionLexica configuracionLexica)
    {
        if (configuracionLexica.getPalabrasReservadas() == null) configuracionLexica.setPalabrasReservadas(List.of());
        if (configuracionLexica.getOperadores() == null) configuracionLexica.setOperadores(List.of());
        if (configuracionLexica.getAgrupacion() == null) configuracionLexica.setAgrupacion(List.of());
        if (configuracionLexica.getPuntuacion() == null) configuracionLexica.setPuntuacion(List.of());
        if (configuracionLexica.getComentarioLinea() == null) configuracionLexica.setComentarioLinea("//");
        if (configuracionLexica.getComentarioBloqueInicio() == null) configuracionLexica.setComentarioBloqueInicio("/*");
        if (configuracionLexica.getComentarioBloqueFin() == null) configuracionLexica.setComentarioBloqueFin("*/");
        if (configuracionLexica.getColores() == null) configuracionLexica.setColores(new LinkedHashMap<>());
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        // Escribe cada campo con funciones auxiliares
        escribirCampoArray(stringBuilder, "palabrasReservadas", configuracionLexica.getPalabrasReservadas(), 2);
        escribirCampoArray(stringBuilder, "operadores", configuracionLexica.getOperadores(), 2);
        escribirCampoArray(stringBuilder, "agrupacion", configuracionLexica.getAgrupacion(), 2);
        escribirCampoArray(stringBuilder, "puntuacion", configuracionLexica.getPuntuacion(), 2);
        escribirCampoString(stringBuilder, "comentarioLinea", configuracionLexica.getComentarioLinea(), 2);
        escribirCampoString(stringBuilder, "comentarioBloqueInicio", configuracionLexica.getComentarioBloqueInicio(), 2);
        escribirCampoString(stringBuilder, "comentarioBloqueFin", configuracionLexica.getComentarioBloqueFin(), 2);
        escribirCampoMapa(stringBuilder, "colores", configuracionLexica.getColores(), 2);
        stringBuilder.append("\n}\n");
        return stringBuilder.toString();
    }
    
    private static class JsonReader // Lector caracter a caracter
    {
        private final char[] palabra;
        private int i = 0;
        JsonReader(String json) 
        { 
            this.palabra = json.toCharArray();
        }
        
        void espaciosEnBlanco() // Consume espacios en blanco
        {
            while (i < palabra.length && (palabra[i] == ' ' || palabra[i] == '\n' || palabra[i] == '\r' || palabra[i] == '\t')) 
            i++; 
            
        }
        
        void espera(char caracter) throws Exception // Exige que despues del espacio sea un caracter para avanzar
        { 
            espaciosEnBlanco(); 
            if (i >= palabra.length || palabra[i] != caracter) throw new Exception("Se esperaba '" + caracter + "' en pos " + i);  
            i++; 
        }
        
        boolean consumido(char caracter) // Retorna verdadero si es consumido el espacio en blanco
        {
            espaciosEnBlanco();
            if (i < palabra.length && palabra[i] == caracter)
            {
                i++;
                return true;
            }
            return false;
        }
        
        String parseString() throws Exception // covierte un string JSON
        {
            espaciosEnBlanco();
            espera('"');
            StringBuilder stringBuilder = new StringBuilder();
            while (i < palabra.length)
            {
                char caracter = palabra[i++];
                if (caracter == '"') break;
                if (caracter == '\\')
                {
                    if (i >= palabra.length) throw new Exception("Escape incompleto");
                    char escape = palabra[i++];
                    switch (escape)
                    {
                        case '"' -> stringBuilder.append('"');
                        case '\\'-> stringBuilder.append('\\');
                        case 'n' -> stringBuilder.append('\n');
                        case 'r' -> stringBuilder.append('\r');
                        case 't' -> stringBuilder.append('\t');
                        default  -> stringBuilder.append(escape);
                    }
                }
                else stringBuilder.append(caracter); 
            }
            return stringBuilder.toString();
        }
        
        List<String> parseArrayOfStrings() throws Exception // Convierte un arreglo de strings
        {
            espaciosEnBlanco(); 
            espera('[');
            List<String> lista = new ArrayList<>();
            espaciosEnBlanco();
            if (consumido(']')) return lista;
            boolean primero = true;
            while (true) 
            {
                if (!primero) espera(',');
                primero = false;
                String v = parseString();
                lista.add(v);
                espaciosEnBlanco();
                if (consumido(']')) break;
            }
            return lista;
        }
        
        Map<String,String> parseObjectStringToString() throws Exception // Convierte un objeto de claves y valores strings
        {
            espaciosEnBlanco(); 
            espera('{');
            Map<String, String> mapa = new LinkedHashMap<>();
            espaciosEnBlanco();
            if (consumido('}')) return mapa;
            boolean primero = true;
            while (true) 
            {
                if (!primero) espera(',');
                primero = false;
                String clave = parseString(); 
                espaciosEnBlanco(); 
                espera(':');
                espaciosEnBlanco();
                String valor = parseString();
                mapa.put(clave, valor);
                espaciosEnBlanco();
                if (consumido('}')) break;
            }
            return mapa;
        }
        
        void saltarValorGenerico() throws Exception // Salta valores desconocidos
        {
            espaciosEnBlanco();
            if (consumido('"')) // Si es string,avanza hasta la " de cierre
            { 
                while (i < palabra.length) 
                {
                    char c = palabra[i++];
                    if (c == '\\') 
                    { 
                        if (i < palabra.length) i++; 
                        continue; 
                    }
                    if (c == '"') break;
                }
                return;
            }
            if (consumido('[')) // si es array, cuenta niveles para saltarlo completamente
            { 
                int nivel = 1;
                while (i < palabra.length && nivel > 0) 
                {
                    char caracter = palabra[i++];
                    if (caracter == '"') 
                    { 
                        i--; 
                        parseString(); 
                    }
                    else if (caracter == '[') nivel++;
                    else if (caracter == ']') nivel--;
                }
                return;
            }
            if (consumido('{')) // Si es objeto, cuenta niveles para saltarlo completamente
            { 
                int nivel = 1;
                while (i < palabra.length && nivel > 0) 
                {
                    char caracter = palabra[i++];
                    if (caracter == '"') 
                    { 
                        i--; parseString(); 
                    }
                    else if (caracter == '{') nivel++;
                    else if (caracter == '}') nivel--;
                }
                return;
            }
            while (i < palabra.length) 
            {
                char caracter = palabra[i];
                if (caracter==',' || caracter=='}' || caracter==']') break;
                i++;
            }
        }
    }
    
    private static void escribirCampoArray(StringBuilder stringBuilder, String nombre, List<String> arreglo, int espacios)
    {
        if (arreglo == null) arreglo = List.of();
        sangria(stringBuilder, espacios).append("\"").append(nombre).append("\": [");
        if (arreglo.isEmpty()) 
        {
            stringBuilder.append("],\n");
            return;
        }
        stringBuilder.append("\n");
        for (int i = 0; i < arreglo.size(); i++) 
        {
            sangria(stringBuilder, espacios + 2).append("\"").append(escapeCaracteres(arreglo.get(i))).append("\"");
            if (i < arreglo.size() - 1) stringBuilder.append(",");
            stringBuilder.append("\n");
        }
        sangria(stringBuilder, espacios).append("],\n");
    }

    private static StringBuilder sangria(StringBuilder stringBuilder, int espacios) 
    { 
        stringBuilder.append(" ".repeat(espacios)); 
        return stringBuilder; 
    }
    
    private static String escapeCaracteres(String escape) 
    {
        StringBuilder out = new StringBuilder();
        for (int j = 0; j < escape.length(); j++) 
        {
            char c = escape.charAt(j);
            if (c == '\\') out.append("\\\\");
            else if (c == '"') out.append("\\\"");
            else if (c == '\n') out.append("\\n");
            else if (c == '\r') out.append("\\r");
            else if (c == '\t') out.append("\\t");
            else out.append(c);
        }
        return out.toString();
    }
    
    private static void escribirCampoString(StringBuilder stringBuilder, String nombre, String valor, int espacios)
    {
        sangria(stringBuilder, espacios).append("\"").append(nombre).append("\": ");
        stringBuilder.append("\"").append(escapeCaracteres(valor == null ? "" : valor)).append("\",\n");
    }
    
    private static void escribirCampoMapa(StringBuilder stringBuilder, String nombre, Map<String, String> mapa, int espacios)
    {
        if (mapa == null) mapa = Map.of(); // Si no hay mapa usa uno vacio
        sangria(stringBuilder, espacios).append("\"").append(nombre).append("\": {");
        if (!mapa.isEmpty()) stringBuilder.append("\n");
        int k = 0;
        for (var e: mapa.entrySet()) 
        {
            sangria(stringBuilder, espacios + 2).append("\"").append(escapeCaracteres(e.getKey())).append("\": ");
            stringBuilder.append("\"").append(escapeCaracteres(e.getValue())).append("\"");
            k++;
            stringBuilder.append(k < mapa.size() ? ",\n" : "\n");
        }
        sangria(stringBuilder, espacios).append("}");
    }
}
