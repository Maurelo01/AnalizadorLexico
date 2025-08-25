/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlexico.configuracion;

import java.util.List;
import java.util.Map;

public class ConfiguracionLexica 
{
    private List<String> palabrasReservadas;
    private List<String> operadores;
    private List<String> agrupacion;
    private List<String> puntuacion;
    private String comentarioLinea;
    private String comentarioBloqueInicio;
    private String comentarioBloqueFin;
    private Map<String, String> colores;
    
    public List<String> getPalabrasReservadas()
    {
        return palabrasReservadas;
    }
    
    public void setPalabrasReservadas(List<String> palabrasReservadas)
    {
        this.palabrasReservadas = palabrasReservadas;
    }
    
    public List<String> getOperadores()
    {
        return operadores;
    }
    
    public void setOperadores(List<String> operadores)
    {
        this.operadores = operadores;
    }
    
    public List<String> getAgrupacion()
    {
        return agrupacion;
    }
    
    public void setAgrupacion(List<String> agrupacion)
    {
        this.agrupacion = agrupacion;
    }
    
    public List<String> getPuntuacion()
    {
        return puntuacion;
    }
    
    public void setPuntuacion(List<String> puntuacion)
    {
        this.puntuacion = puntuacion;
    }
    
    public String getComentarioLinea()
    {
        return comentarioLinea;
    }
    
    public void setComentarioLinea(String comentarioLinea)
    {
        this.comentarioLinea = comentarioLinea;
    }
    
    public String getComentarioBloqueInicio()
    {
        return comentarioBloqueInicio;
    }
    
    public void setComentarioBloqueInicio(String comentarioBloqueInicio)
    {
        this.comentarioBloqueInicio = comentarioBloqueInicio;
    }
    
    public String getComentarioBloqueFin()
    {
        return comentarioBloqueInicio;
    }
    
    public void setComentarioBloqueFin(String comentarioBloqueFin)
    {
        this.comentarioBloqueFin = comentarioBloqueFin;
    }
    
    public Map<String, String> getColores()
    {
        return colores;
    }
    
    public void setColores(Map<String, String> colores)
    {
        this.colores = colores;
    }
}
