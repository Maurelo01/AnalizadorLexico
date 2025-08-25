/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlexico.analisis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoAnalisis 
{
    private final List<Token> tokens = new ArrayList<>();
    private final List<Token> errores = new ArrayList<>();

    public void agregarToken(Token token)
    {
        tokens.add(token);
    }
    
    public void agregarError(Token tokenError)
    {
        errores.add(tokenError);
    }
    
    public List<Token> getTokens()
    {
        return Collections.unmodifiableList(tokens);
    }
    
    public List<Token> getErrores()
    {
        return Collections.unmodifiableList(errores);
    }
}
