/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.analizadorlexico.ui;

import com.mycompany.analizadorlexico.analisis.TipoToken;
import com.mycompany.analizadorlexico.configuracion.ConfiguracionES;
import com.mycompany.analizadorlexico.configuracion.ConfiguracionLexica;
import com.mycompany.analizadorlexico.io.Archivos;
import com.mycompany.analizadorlexico.util.Colores;
import java.awt.Color;
import java.awt.Font;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class EditorPrincipal extends javax.swing.JFrame 
{
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(EditorPrincipal.class.getName());
    private ConfiguracionLexica configuracion;  
    private ConfiguracionES configES;  
    private SimpleAttributeSet estiloPorDefecto;
    private Map<TipoToken, AttributeSet> mapaEstilosPorTipo;
    
    public EditorPrincipal() 
    {
        initComponents();
        configurarVentana();
        cargarConfiguracionInicial();
        configurarEventos();
    }
    
    private void configurarVentana() 
    {
        setLocationRelativeTo(null); // centrar
        areaEditor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    }
    
    private void cargarConfiguracionInicial() 
    {
        try 
        {
            ConfiguracionES.asegurarArchivoConfiguracion(); // Crea carpeta y archivo si no existen y escribe el JSON por defecto
            configuracion = ConfiguracionES.cargar(); // Carga el JSON a objeto
            crearMapaEstilosDesdeConfiguracion(); // Activa la cargar de colores
        } 
        catch (Exception ex) 
        {
            logger.log(java.util.logging.Level.SEVERE, "Error cargando config.json", ex);
            JOptionPane.showMessageDialog(this, "Error cargando config.json: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            configuracion = new ConfiguracionLexica(); // vacio para no romper la aplicacion
        }
    }
    
    private void configurarEventos() 
    {
        areaEditor.addCaretListener(new CaretListener() // Actualiza fila y columna
        {
            @Override
            public void caretUpdate(CaretEvent e) 
            {
                actualizarEtiquetaEstado(e);
            }
        });
        itemGuardar.addActionListener(e -> guardarArchivo());
        itemBusqueda.addActionListener(e -> abrirDialogoBusqueda());
        itemEditarConfig.addActionListener(e -> abrirDialogoConfig());
    }
    
    private void actualizarEtiquetaEstado(CaretEvent e) {
        try 
        {
            int pos = e.getDot();
            int fila = 1, col = 1;
            String texto = areaEditor.getText();
            int hasta = Math.min(pos, texto.length());
            for (int i = 0; i < hasta; i++) 
            {
                if (texto.charAt(i) == '\n') { fila++; col = 1; }
                else col++;
            }
            lblEtiquetaEstado.setText("Fila: " + fila + ", Columna: " + col);
        } 
        catch (Exception ignore){}
    }
    
    private void guardarArchivo() 
    {
        try 
        {
            Archivos.guardarTexto(this, areaEditor.getText());
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error al guardar archivo", ex);
        }
    }
    
    private void abrirDialogoBusqueda() 
    {
        new DialogoBusqueda(this, areaEditor.getText()).setVisible(true);
    
    }
    
    private void abrirDialogoConfig() 
    {
        DialogoConfig dlg = new DialogoConfig(this, configuracion);
        dlg.setVisible(true);
        try 
        {
            ConfiguracionES.guardar(configuracion);
            configuracion = ConfiguracionES.cargar();
        } 
        catch (Exception ex) 
        {
            logger.log(java.util.logging.Level.WARNING, "No se pudo guardar/recargar config tras diálogo", ex);
        }
    }
    
    private void crearMapaEstilosDesdeConfiguracion()
    {
        // Estilo por defecto (todo negro xd)
        estiloPorDefecto = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloPorDefecto, Color.BLACK);
        StyleConstants.setBold(estiloPorDefecto, false);
        StyleConstants.setItalic(estiloPorDefecto, false);
        
        // Mapa por tipo de token
        mapaEstilosPorTipo = new EnumMap<>(TipoToken.class);
        
        // Colores desde el config
        Map<String, String> colores = (configuracion != null && configuracion.getColores() != null) ? configuracion.getColores() : java.util.Map.of();
        Color colorReservada = Colores.convertirHexAColor(colores.getOrDefault("RESERVADA", "#0000FF"));
        Color colorIdentificador = Colores.convertirHexAColor(colores.getOrDefault("IDENTIFICADOR", "#8B4513"));
        Color colorNumero = Colores.convertirHexAColor(colores.getOrDefault("NUMERO", "#FF00FF"));
        Color colorDecimal = Colores.convertirHexAColor(colores.getOrDefault("DECIMAL", "#000000"));
        Color colorCadena = Colores.convertirHexAColor(colores.getOrDefault("CADENA", "#008000"));
        Color colorOperador = Colores.convertirHexAColor(colores.getOrDefault("OPERADOR", "#DC143C"));
        Color colorAgrupacion = Colores.convertirHexAColor(colores.getOrDefault("AGRUPACION", "#FF8C00"));
        Color colorPuntuacion = Colores.convertirHexAColor(colores.getOrDefault("PUNTUACION", "#800080"));
        Color colorComentario = Colores.convertirHexAColor(colores.getOrDefault("COMENTARIO", "#006400"));
        Color colorError = Colores.convertirHexAColor(colores.getOrDefault("ERROR", "#FF0000"));
        
        mapaEstilosPorTipo.put(TipoToken.RESERVADA, crearEstilo(colorReservada));
        mapaEstilosPorTipo.put(TipoToken.IDENTIFICADOR, crearEstilo(colorIdentificador));
        mapaEstilosPorTipo.put(TipoToken.NUMERO, crearEstilo(colorNumero));
        mapaEstilosPorTipo.put(TipoToken.DECIMAL, crearEstilo(colorDecimal));
        mapaEstilosPorTipo.put(TipoToken.CADENA, crearEstilo(colorCadena));
        mapaEstilosPorTipo.put(TipoToken.OPERADOR, crearEstilo(colorOperador));
        mapaEstilosPorTipo.put(TipoToken.AGRUPACION, crearEstilo(colorAgrupacion));
        mapaEstilosPorTipo.put(TipoToken.PUNTUACION, crearEstilo(colorPuntuacion));
        mapaEstilosPorTipo.put(TipoToken.COMENTARIO, crearEstilo(colorComentario)); 
        mapaEstilosPorTipo.put(TipoToken.ERROR, crearEstilo(colorError));
    }
    
    private AttributeSet crearEstilo(Color color)
    {
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, color != null ? color : Color.BLACK);
        return estilo;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPaneEditor = new javax.swing.JScrollPane();
        areaEditor = new javax.swing.JTextPane();
        panelestado = new javax.swing.JPanel();
        lblEtiquetaEstado = new javax.swing.JLabel();
        barraMenu = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        itemAbrir = new javax.swing.JMenuItem();
        itemGuardar = new javax.swing.JMenuItem();
        menuHerramientas = new javax.swing.JMenu();
        itemBusqueda = new javax.swing.JMenuItem();
        itemAnalizadorLexico = new javax.swing.JMenuItem();
        menuConfig = new javax.swing.JMenu();
        itemEditarConfig = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Analizador Léxico");
        setPreferredSize(new java.awt.Dimension(450, 350));

        scrollPaneEditor.setViewportView(areaEditor);

        getContentPane().add(scrollPaneEditor, java.awt.BorderLayout.CENTER);

        lblEtiquetaEstado.setText("Fila: 1, Columna: 1");
        panelestado.add(lblEtiquetaEstado);

        getContentPane().add(panelestado, java.awt.BorderLayout.LINE_START);

        menuArchivo.setText("Archivo");

        itemAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemAbrir.setText("Abrir");
        itemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAbrirActionPerformed(evt);
            }
        });
        menuArchivo.add(itemAbrir);

        itemGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemGuardar.setText("Guardar como");
        menuArchivo.add(itemGuardar);

        barraMenu.add(menuArchivo);

        menuHerramientas.setText("Herramientas");

        itemBusqueda.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemBusqueda.setText("Busqueda de patrones");
        menuHerramientas.add(itemBusqueda);

        itemAnalizadorLexico.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemAnalizadorLexico.setText("Analizador léxico");
        itemAnalizadorLexico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAnalizadorLexicoActionPerformed(evt);
            }
        });
        menuHerramientas.add(itemAnalizadorLexico);

        barraMenu.add(menuHerramientas);

        menuConfig.setText("Configuracion");

        itemEditarConfig.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemEditarConfig.setText("Editar config.json");
        menuConfig.add(itemEditarConfig);

        barraMenu.add(menuConfig);

        setJMenuBar(barraMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAbrirActionPerformed
        try 
        {
            String texto = Archivos.abrirTexto(this);
            if (texto != null) 
            {
                areaEditor.setText(texto);
                areaEditor.setCaretPosition(0);
            }
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, "No se pudo abrir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error al abrir archivo", ex);
        }
    }//GEN-LAST:event_itemAbrirActionPerformed

    private void itemAnalizadorLexicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAnalizadorLexicoActionPerformed
        try 
        {
            if (configuracion == null) // Asegura que la configuración este cargada
            {
                com.mycompany.analizadorlexico.configuracion.ConfiguracionES.asegurarArchivoConfiguracion();
                configuracion = com.mycompany.analizadorlexico.configuracion.ConfiguracionES.cargar();
            }
            com.mycompany.analizadorlexico.analisis.AnalizadorLexico analizador =new com.mycompany.analizadorlexico.analisis.AnalizadorLexico(); // Ejecuta el analizador léxico
            com.mycompany.analizadorlexico.analisis.ResultadoAnalisis resultado = analizador.analizar(areaEditor.getText(), configuracion);
            com.mycompany.analizadorlexico.ui.DialogoResultadoAnalisis dialogo = new com.mycompany.analizadorlexico.ui.DialogoResultadoAnalisis(this, true);
            dialogo.cargarResultadoAnalisis(resultado); dialogo.setLocationRelativeTo(this); dialogo.setVisible(true);
        } 
        catch (Exception excepcion) 
        {
            excepcion.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "No se pudo ejecutar el análisis: " + excepcion.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_itemAnalizadorLexicoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane areaEditor;
    private javax.swing.JMenuBar barraMenu;
    private javax.swing.JMenuItem itemAbrir;
    private javax.swing.JMenuItem itemAnalizadorLexico;
    private javax.swing.JMenuItem itemBusqueda;
    private javax.swing.JMenuItem itemEditarConfig;
    private javax.swing.JMenuItem itemGuardar;
    private javax.swing.JLabel lblEtiquetaEstado;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenu menuConfig;
    private javax.swing.JMenu menuHerramientas;
    private javax.swing.JPanel panelestado;
    private javax.swing.JScrollPane scrollPaneEditor;
    // End of variables declaration//GEN-END:variables
}
