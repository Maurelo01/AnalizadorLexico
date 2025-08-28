package com.mycompany.analizadorlexico;

import com.mycompany.analizadorlexico.configuracion.ConfiguracionES;
import com.mycompany.analizadorlexico.ui.EditorPrincipal;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AnalizadorLexico 
{
    private AnalizadorLexico() {}
    
    public static void main(String[] args) 
    {
        Thread.setDefaultUncaughtExceptionHandler((hilo, ex) -> 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error no controlado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE
            );
        });
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (Exception ignore) {}

        // Asegura que exista config.json con valores por defecto
        try 
        {
            ConfiguracionES.asegurarArchivoConfiguracion();
        } 
        catch (Exception ex) 
        {
            System.err.println("No se pudo asegurar config.json: " + ex.getMessage());
        }

        // Lanza la UI en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> 
        {
            EditorPrincipal ventana = new EditorPrincipal();
            ventana.setVisible(true);
        });
    }
}
