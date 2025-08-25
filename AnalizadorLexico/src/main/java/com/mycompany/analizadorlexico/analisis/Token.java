/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlexico.analisis;

public class Token 
{
    private final TipoToken tipo;
    private final String lexema;
    private final int fila;
    private final int columnaInicio;
    private final int columnaFin;

    public Token(TipoToken tipo, String lexema, int fila, int columnaInicio, int columnaFin)
    {
        this.tipo = tipo;
        this.lexema = lexema;
        this.fila = fila;
        this.columnaInicio = columnaInicio;
        this.columnaFin = columnaFin;
    }
    
    public TipoToken getTipo()
    {
        return tipo;
    }
    
    public String getLexema()
    {
        return lexema;
    }
    
    public int getFila()
    {
        return fila;
    }
    
    public int getColumnaInicio()
    {
        return columnaInicio;
    }
    
    public int getColumnaFin()
    {
        return columnaFin;
    }
}
