/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlexico.util;

import java.awt.Color;

public class Colores 
{
    public static boolean esHexColorValido(String codigoHex) // Verifica el formato de colores #RRGGBB
    {
        if (codigoHex == null) return false;
        String limpio = codigoHex.trim();
        return limpio.matches("#[0-9A-Fa-f]{6}");
    }
    
    public static Color convertirHexAColor(String codigoHex)  // Convierte el formato de color #RRGGBB directo a color
    {
        if (!esHexColorValido(codigoHex)) return Color.BLACK;  // Sino es valiro retorna negro
        String limpio = codigoHex.trim();
        int r = Integer.parseInt(limpio.substring(1, 3), 16);
        int g = Integer.parseInt(limpio.substring(3, 5), 16);
        int b = Integer.parseInt(limpio.substring(5, 7), 16);
        return new Color(r, g, b);
    }
    
    public static String colorAHex(Color color)  // El inverso del metodo anterior, color a #RRGGBB
    {
        if (color == null) return "#000000";
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}
