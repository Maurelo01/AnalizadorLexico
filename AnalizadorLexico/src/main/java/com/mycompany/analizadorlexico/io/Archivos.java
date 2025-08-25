/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlexico.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

public class Archivos 
{
    public static String abrirTexto(JFrame parent) throws IOException 
    {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) 
        {   
            File f = fc.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) 
            {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
                return sb.toString();
            }
        }
        return null;
    }
    
    public static void guardarTexto(JFrame parent, String contenido) throws IOException 
    {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) 
        {
            File f = fc.getSelectedFile();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) 
            {
                bw.write(contenido);
            }
        }
    }
}
