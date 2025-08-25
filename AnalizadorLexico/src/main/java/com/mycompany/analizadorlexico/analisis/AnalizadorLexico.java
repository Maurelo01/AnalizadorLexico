/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlexico.analisis;

import com.mycompany.analizadorlexico.configuracion.ConfiguracionLexica;
import java.util.List;

public class AnalizadorLexico 
{
    public ResultadoAnalisis analizar(String textoFuente, ConfiguracionLexica configuracion)
    {
        ResultadoAnalisis resultado = new ResultadoAnalisis();
        if (textoFuente == null) textoFuente = "";
        char[] arregloCaracteres = textoFuente.toCharArray();
    
        int indiceActual = 0;
        int filaActual = 1;
        int columnaActual = 1;
        
        while(indiceActual < arregloCaracteres.length)
        {
            char caracterActual = arregloCaracteres[indiceActual];
            if (caracterActual == '\n') // Saltos de linea soporta \n y \r\n
            {
                indiceActual++;
                filaActual++;
                columnaActual = 1;
                continue;
            }
            if (caracterActual == '\r') // si hay \r\n las toma juntas
            {
                if (indiceActual + 1 < arregloCaracteres.length && arregloCaracteres[indiceActual + 1] == '\n')
                {
                    indiceActual += 2;
                }
                else
                {
                    indiceActual++;
                }
                filaActual++;
                columnaActual = 1;
                continue;
            }
            if(esEspacioEnBlanco(caracterActual)) // Espacios en blanco
            {
                indiceActual++;
                columnaActual++;
                continue;
            }
            String inicioLinea = configuracion.getComentarioLinea(); // Comentario de linea
            if (inicioLinea != null && !inicioLinea.isEmpty() && comienzaCon(arregloCaracteres, indiceActual, inicioLinea))
            {
                int indiceInicio = indiceActual;
                int columnaInicio = columnaActual;
                while (indiceActual < arregloCaracteres.length && arregloCaracteres[indiceActual] != '\n' && arregloCaracteres[indiceActual] != '\r')
                {
                    indiceActual++;
                    columnaActual++;
                }
                String lexema = extraer(arregloCaracteres, indiceInicio, indiceActual);
                resultado.agregarToken(new Token(TipoToken.COMENTARIO, lexema, filaActual, columnaInicio, columnaActual - 1));
                continue;
            }
            String inicioBloque = configuracion.getComentarioBloqueInicio(); // Comentario de bloque 
            String finBloque = configuracion.getComentarioBloqueFin();
            if (inicioBloque != null && finBloque != null && !inicioBloque.isEmpty() && !finBloque.isEmpty() && comienzaCon(arregloCaracteres, indiceActual, inicioBloque))
            {
                int indiceInicio = indiceActual;
                int columnaInicio = columnaActual;
                indiceActual += inicioBloque.length();
                columnaActual += inicioBloque.length();
                boolean encontroFin = false;
                while (indiceActual < arregloCaracteres.length)
                {
                    if (arregloCaracteres[indiceActual] == '\n') 
                    {
                        indiceActual++;
                        filaActual++;
                        columnaActual = 1;
                        continue;
                    }
                    if (arregloCaracteres[indiceActual] == '\r') 
                    {
                        if (indiceActual + 1 < arregloCaracteres.length && arregloCaracteres[indiceActual + 1] == '\n') 
                        {
                            indiceActual += 2;
                        } 
                        else 
                        {
                            indiceActual++;
                        }
                        filaActual++;
                        columnaActual = 1;
                        continue;
                    }
                    if (comienzaCon(arregloCaracteres, indiceActual, finBloque)) 
                    {
                        indiceActual += finBloque.length();
                        columnaActual += finBloque.length();
                        encontroFin = true;
                        break;
                    }
                    indiceActual++;
                    columnaActual++;
                }
                String lexema = extraer(arregloCaracteres, indiceInicio, indiceActual);
                if (encontroFin) 
                {
                    resultado.agregarToken(new Token(TipoToken.COMENTARIO, lexema, filaActual, columnaInicio, Math.max(columnaActual - 1, columnaInicio)));
                } 
                else // Comentario de bloque sin cerrar
                {
    
                    resultado.agregarError(new Token(TipoToken.ERROR, lexema, filaActual, columnaInicio, Math.max(columnaActual - 1, columnaInicio)));
                }
                continue;
            }
            if(caracterActual == '"') // cadenas entre comilla dobles
            {
                int indiceInicio = indiceActual;
                int columnaInicio = columnaActual;
                indiceActual++; // Tomar comilla de apertura
                columnaActual++;
                boolean cadenaCerrada = false;
                
                while (indiceActual < arregloCaracteres.length) 
                {
                    char caracterDeCadena = arregloCaracteres[indiceActual];
                    if (caracterDeCadena == '\\') { // escape
                        if (indiceActual + 1 < arregloCaracteres.length) 
                        {
                            indiceActual += 2;
                            columnaActual += 2;
                            continue;
                        } 
                        else 
                        {
                            indiceActual++;// fin de texto después de barra invertida 
                            columnaActual++;
                            break;
                        }
                    }
                    if (caracterDeCadena == '"') 
                    {
                        indiceActual++; // Tomar comilla de cierre
                        columnaActual++;
                        cadenaCerrada = true;
                        break;
                    }
                    if (caracterDeCadena == '\n' || caracterDeCadena == '\r') // no permite salto de linea dentro de cadena
                    {
                        break;
                    }
                    indiceActual++;
                    columnaActual++;
                }
                String lexema = extraer(arregloCaracteres, indiceInicio, Math.min(indiceActual, arregloCaracteres.length));
                if (cadenaCerrada) 
                {
                    resultado.agregarToken(new Token(TipoToken.CADENA, lexema, filaActual, columnaInicio, Math.max(columnaActual - 1, columnaInicio)));
                } 
                else 
                {
                    resultado.agregarError(new Token(TipoToken.ERROR, lexema, filaActual, columnaInicio, Math.max(columnaActual - 1, columnaInicio)));
                }
                continue;
            }
            if (esDigito(caracterActual)) // Número entero o decimal
            {
                int indiceInicio = indiceActual;
                int columnaInicio = columnaActual;

                while (indiceActual < arregloCaracteres.length && esDigito(arregloCaracteres[indiceActual])) // Enteros 
                {
                    indiceActual++;
                    columnaActual++;
                }

                boolean tienePuntoDecimal = false; // Decimales
                int cantidadDigitosDecimales = 0;
                if (indiceActual < arregloCaracteres.length && arregloCaracteres[indiceActual] == '.') 
                {
                    tienePuntoDecimal = true;
                    indiceActual++;
                    columnaActual++;
                    while (indiceActual < arregloCaracteres.length && esDigito(arregloCaracteres[indiceActual])) 
                    {
                        indiceActual++;
                        columnaActual++;
                        cantidadDigitosDecimales++;
                    }
                }
                
                String lexema = extraer(arregloCaracteres, indiceInicio, indiceActual);
                if (tienePuntoDecimal && cantidadDigitosDecimales == 0) 
                {
                    resultado.agregarError(new Token(TipoToken.ERROR, lexema, filaActual, columnaInicio, Math.max(columnaActual - 1, columnaInicio)));
                } 
                else if (tienePuntoDecimal) 
                {
                    resultado.agregarToken(new Token(TipoToken.DECIMAL, lexema, filaActual, columnaInicio, Math.max(columnaActual - 1, columnaInicio)));
                } 
                else 
                {
                    resultado.agregarToken(new Token(TipoToken.NUMERO, lexema, filaActual, columnaInicio, Math.max(columnaActual - 1, columnaInicio)));
                }
                continue;
            }
            
            if (esLetra(caracterActual) || caracterActual == '_') // identificador/reservada
            {
                int indiceInicio = indiceActual;
                int columnaInicio = columnaActual;

                while (indiceActual < arregloCaracteres.length) 
                {
                    char caracterIdentificador = arregloCaracteres[indiceActual];
                    if (esLetra(caracterIdentificador) || esDigito(caracterIdentificador) || caracterIdentificador == '_') 
                    {
                        indiceActual++;
                        columnaActual++;
                    } 
                    else 
                    {
                        break;
                    }
                }

                String lexema = extraer(arregloCaracteres, indiceInicio, indiceActual);
                boolean esReservada = estaEnLista(lexema, configuracion.getPalabrasReservadas());
                TipoToken tipo = esReservada ? TipoToken.RESERVADA : TipoToken.IDENTIFICADOR;
                resultado.agregarToken(new Token(tipo, lexema, filaActual, columnaInicio, Math.max(columnaActual - 1, columnaInicio)));
                continue;
            }
            
            Token tokenSimbolo = consumirSimboloConfigurado(arregloCaracteres, indiceActual, filaActual, columnaActual, configuracion); // Operadores / Agrupacinn / Puntuacion
            if (tokenSimbolo != null) 
            {
                resultado.agregarToken(tokenSimbolo);
                int longitudLexema = tokenSimbolo.getLexema().length();
                indiceActual += longitudLexema;
                columnaActual += longitudLexema;
                continue;
            }
            String lexemaError = String.valueOf(caracterActual); // error de un caracter y continuar si nada aplica
            resultado.agregarError(new Token(TipoToken.ERROR, lexemaError, filaActual, columnaActual, columnaActual));
            indiceActual++;
            columnaActual++;
        }
        return resultado;
    }
    
    private static boolean esEspacioEnBlanco(char caracter)
    {
        return caracter == ' ' || caracter == '\t' || caracter == '\f';
    }
    
    private static boolean esDigito(char caracter) 
    {
        return caracter >= '0' && caracter <= '9';
    }
    
    private static boolean esLetra(char caracter) 
    {
        return (caracter >= 'a' && caracter <= 'z') || (caracter >= 'A' && caracter <= 'Z') || (caracter >= 'á' && caracter <= 'ú') || (caracter >= 'Á' && caracter <= 'Ú');
    }
    
    private static boolean comienzaCon(char[] origen, int indiceDesde, String patron)
    {
        if (patron == null) return false;
        if (indiceDesde + patron.length() > origen.length) return false;
        for (int indicePatron = 0; indicePatron < patron.length(); indicePatron++)
        {
            if (origen[indiceDesde + indicePatron] != patron.charAt(indicePatron)) return false;
        }
        return true;
    }
    
    private static String extraer(char[] origen, int indiceDesde, int indiceHastaExclusivo) 
    {
        return new String(origen, indiceDesde, indiceHastaExclusivo - indiceDesde);
    }
    
    private static boolean estaEnLista(String valor, List<String> lista)
    {
        if (lista == null) return false;
        for (String elemento : lista)
        {
            if (valor.equals(elemento)) return true;
        }
        return false;
    }
    
    private static Token consumirSimboloConfigurado(char[] origen, int indiceDesde, int fila, int columna, com.mycompany.analizadorlexico.configuracion.ConfiguracionLexica config)
    {
        String mejorOperador = mejorCoincidencia(origen, indiceDesde, config.getOperadores());
        String mejorAgrupacion = mejorCoincidencia(origen, indiceDesde, config.getAgrupacion());
        String mejorPuntuacion = mejorCoincidencia(origen, indiceDesde, config.getPuntuacion());

        String lexemaElegido = null;
        TipoToken tipoElegido = null;
        
        if (mejorOperador != null)
        {
            lexemaElegido = mejorOperador;
            tipoElegido = TipoToken.OPERADOR;
        }
        
        if (mejorAgrupacion != null && (lexemaElegido == null || mejorAgrupacion.length() > lexemaElegido.length()))
        {
            lexemaElegido = mejorAgrupacion;
            tipoElegido = TipoToken.AGRUPACION;
        }
       
        if (mejorPuntuacion != null && (lexemaElegido == null || mejorPuntuacion.length() > lexemaElegido.length()))
        {
            lexemaElegido = mejorPuntuacion;
            tipoElegido = TipoToken.PUNTUACION;
        }
        
        if (lexemaElegido == null) return null;
        return new Token(tipoElegido, lexemaElegido, fila, columna, columna + lexemaElegido.length() - 1);
    }
    
    private static String mejorCoincidencia(char[] origen, int indiceDesde, List<String> candidatos) 
    {
        if (candidatos == null || candidatos.isEmpty()) return null;
        String mejor = null;
        for (String candidato : candidatos) 
        {
            if (candidato == null || candidato.isEmpty()) continue;
            if (comienzaCon(origen, indiceDesde, candidato)) 
            {
                if (mejor == null || candidato.length() > mejor.length()) 
                {
                    mejor = candidato;
                }
            }
        }
        return mejor;
    }
}
