/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.analizadorlexico.ui;

import com.mycompany.analizadorlexico.analisis.ResultadoAnalisis;
import com.mycompany.analizadorlexico.analisis.TipoToken;
import com.mycompany.analizadorlexico.analisis.Token;
import com.mycompany.analizadorlexico.configuracion.ConfiguracionLexica;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DialogoResultadoAnalisis extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DialogoResultadoAnalisis.class.getName());
    private ResultadoAnalisis resultadoActual;
    private ConfiguracionLexica configuracionActual;
    
    public DialogoResultadoAnalisis(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        configurarTablas();
        actualizarResumen(0, 0);
    }
    
    public DialogoResultadoAnalisis(java.awt.Frame parent, ResultadoAnalisis resultado)
    {
        super(parent, true);
        initComponents();
        configurarTablas();
        cargarResultadoAnalisis(resultado);
    }
    
    private void configurarTablas() 
    {
        DefaultTableModel modeloTokens = new DefaultTableModel(new Object[]{"#", "Tipo", "Lexema", "Fila", "Columna inicio", "Columna fin"}, 0)  // Modelo vacio inicial para tokens
        {
            @Override
            public boolean isCellEditable(int fila, int columna) 
            { 
                return false; 
            }        
        };
        tablaTokens.setModel(modeloTokens);
        tablaTokens.setAutoCreateRowSorter(true);
        tablaTokens.setRowSelectionAllowed(true);
        tablaTokens.setColumnSelectionAllowed(false);
        tablaTokens.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        DefaultTableModel modeloErrores = new DefaultTableModel(new Object[]{"#", "Tipo", "Lexema", "Fila", "Columna inicio", "Columna fin"}, 0) // Modelo vacio inicial para errores
        {
            @Override public boolean isCellEditable(int fila, int columna) 
            { 
                return false; 
            }
        };
        tablaErrores.setModel(modeloErrores);
        tablaErrores.setAutoCreateRowSorter(true);
        tablaErrores.setRowSelectionAllowed(true);
        tablaErrores.setColumnSelectionAllowed(false);
        tablaErrores.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    }
    
    public void cargarResultadoAnalisis(ResultadoAnalisis resultado) 
    {
        if (resultado == null) 
        {
            limpiarTablas();
            actualizarResumen(0, 0);
            return;
        }
        this.resultadoActual = resultado;
        DefaultTableModel modeloTokens = (DefaultTableModel) tablaTokens.getModel();
        DefaultTableModel modeloErrores = (DefaultTableModel) tablaErrores.getModel();
        modeloTokens.setRowCount(0);
        modeloErrores.setRowCount(0);
        
        java.util.List<Token> listaTokens = resultado.getTokens(); // Llenar tokens
        for (int indice = 0; indice < listaTokens.size(); indice++) 
        {
            Token token = listaTokens.get(indice);
            modeloTokens.addRow(new Object[]
            {
                    indice + 1,
                    token.getTipo(),
                    token.getLexema(),
                    token.getFila(),
                    token.getColumnaInicio(),
                    token.getColumnaFin()
            });
        }
        java.util.List<Token> listaErrores = resultado.getErrores(); // Llenar errores
        for (int indice = 0; indice < listaErrores.size(); indice++) 
        {
            Token tokenError = listaErrores.get(indice);
            modeloErrores.addRow(new Object[]
            {
                indice + 1,
                tokenError.getTipo(),
                tokenError.getLexema(),
                tokenError.getFila(),
                tokenError.getColumnaInicio(),
                tokenError.getColumnaFin()
            });
        }
        actualizarResumen(listaTokens.size(), listaErrores.size());// Ir por defecto a la pestaña de Errores si hay alguno
        if (!listaErrores.isEmpty()) 
        {
            tabResultado.setSelectedIndex(1); // 0=Tokens, 1=Errores
        } 
        else 
        {
            tabResultado.setSelectedIndex(0);
        }
    }
    
    private void limpiarTablas() 
    {
        ((DefaultTableModel) tablaTokens.getModel()).setRowCount(0);
        ((DefaultTableModel) tablaErrores.getModel()).setRowCount(0);
    }

    private void actualizarResumen(int cantidadTokens, int cantidadErrores) 
    {
        lblResumen.setText("Tokens: " + cantidadTokens + "   |   Errores: " + cantidadErrores);
    }
    
    public static class FilaRecuento // Hace recuento de filas
    {
        public final String lexema;
        public final TipoToken tipo;
        public int cantidad;
        
        public FilaRecuento(String lexema, TipoToken tipo, int cantidad)
        {
            this.lexema = lexema; this.tipo = tipo; this.cantidad = cantidad;
        }
    }
    
    public void cargarReportes(ResultadoAnalisis resultado, ConfiguracionLexica configuracion)
    {
        this.resultadoActual = resultado;
        this.configuracionActual = configuracion;
        cargarTablaErrores(resultado.getErrores()); // Siempre llena los errores
        if (!resultado.getErrores().isEmpty()) // si hay errores solo muestra errores
        {
            if (tabResultado.getTabCount() >= 3) // Desactiva las demas pestañas
            {
                tabResultado.setEnabledAt(0, false); // Tokens
                tabResultado.setEnabledAt(2, false); // Recuentos
            }
            areaReporteGeneral.setText(construirReporteGeneral(resultado.getTokens(), resultado.getErrores(), configuracion));
            tabResultado.setSelectedIndex(1); // Errores
            return;
        }
        if (tabResultado.getTabCount() >= 3) // Muestra todo sino hay errores
        {
            tabResultado.setEnabledAt(0, true); // Tokens
            tabResultado.setEnabledAt(2, true); // Recuento
            tabResultado.setEnabledAt(3, true); // Recuento
        }
        cargarTablaTokens(resultado.getTokens());
        List<FilaRecuento> recuento = calcularRecuentoDeLexemas(resultado.getTokens());
        cargarTablaRecuento(recuento);
        areaReporteGeneral.setText(construirReporteGeneral(resultado.getTokens(), resultado.getErrores(), configuracion));
        tabResultado.setSelectedIndex(1); // Tokens
    }
    
    private void cargarTablaErrores(List<Token> errores)
    {
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"Símbolo o cadena", "Fila", "Columna inicio", "Columna fin"}, 0)
        {
            @Override public boolean isCellEditable(int fila, int columna) 
            { 
                return false; 
            }
        };
        for (Token e : errores)
        {
            modelo.addRow(new Object[]{ e.getLexema(), e.getFila(), e.getColumnaInicio(), e.getColumnaFin() });
        }
        tablaErrores.setModel(modelo);
    }
    
    private void cargarTablaTokens(List<Token> tokens) 
    {
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"Nombre de token", "Lexema", "Fila", "Columna inicio", "Columna fin"}, 0) 
        {
            @Override public boolean isCellEditable(int fila, int columna) 
            { 
                return false; 
            }
        };
        for (Token t : tokens) 
        {
            modelo.addRow(new Object[]{ t.getTipo().name(), t.getLexema(), t.getFila(), t.getColumnaInicio(), t.getColumnaFin() });
        }
        tablaTokens.setModel(modelo);
    }
    
    private void cargarTablaRecuento(List<FilaRecuento> recuento) 
    {
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"Lexema", "Tipo", "Cantidad"}, 0)
        {
            @Override public boolean isCellEditable(int fila, int columna) 
            { 
                return false; 
            }
        };
        for (FilaRecuento r : recuento) 
        {
            modelo.addRow(new Object[]{ r.lexema, r.tipo.name(), r.cantidad });
        }
        tablaRecuento.setModel(modelo);
    }
    
    private List<FilaRecuento> calcularRecuentoDeLexemas(List<Token> tokens) 
    {
        Map<String, FilaRecuento> mapa = new LinkedHashMap<>();
        for (Token t : tokens) 
        {
            String clave = t.getLexema() + "␟" + t.getTipo().name();
            FilaRecuento fila = mapa.get(clave);
            if (fila == null) 
            {
                fila = new FilaRecuento(t.getLexema(), t.getTipo(), 1);
                mapa.put(clave, fila);
            } 
            else 
            {
                fila.cantidad++;
            }
        }
        return new ArrayList<>(mapa.values());
    }
    
    private String construirReporteGeneral(List<Token> tokens, List<Token> errores, ConfiguracionLexica configuracion) 
    {
        int cantidadValidos = tokens.size();
        int cantidadErrores = errores.size();
        int totalReconocidos = cantidadValidos + cantidadErrores;
        double porcentajeValidos = (totalReconocidos == 0) ? 100.0 : (cantidadValidos * 100.0 / totalReconocidos);
        // Detecta los no usados de listas cerradas del config
        Set<String> reservadasUsadas = new HashSet<>();
        Set<String> operadoresUsados = new HashSet<>();
        Set<String> puntuacionUsada = new HashSet<>();
        Set<String> agrupacionUsada = new HashSet<>();

        for (Token token : tokens)
        {
            switch (token.getTipo()) 
            {
                case RESERVADA -> reservadasUsadas.add(token.getLexema());
                case OPERADOR  -> operadoresUsados.add(token.getLexema());
                case PUNTUACION-> puntuacionUsada.add(token.getLexema());
                case AGRUPACION-> agrupacionUsada.add(token.getLexema());
                default -> {}
            }
        }

        List<String> reservadasNoUsadas = faltantesDe(configuracion.getPalabrasReservadas(), reservadasUsadas);
        List<String> operadoresNoUsados = faltantesDe(configuracion.getOperadores(), operadoresUsados);
        List<String> puntuacionNoUsada  = faltantesDe(configuracion.getPuntuacion(), puntuacionUsada);
        List<String> agrupacionNoUsada  = faltantesDe(configuracion.getAgrupacion(), agrupacionUsada);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cantidade del texto\n");
        stringBuilder.append("------------------\n");
        stringBuilder.append("Cantidad de errores: ").append(cantidadErrores).append("\n");
        stringBuilder.append(String.format("Porcentaje de tokens validos: %.2f%%\n", porcentajeValidos));
        stringBuilder.append("Tokens no utilizados:\n");
        stringBuilder.append(" - Palabras reservadas no usadas: ").append(reservadasNoUsadas).append("\n");
        stringBuilder.append(" - Operadores no usados: ").append(operadoresNoUsados).append("\n");
        stringBuilder.append(" - Puntuacion no usada: ").append(puntuacionNoUsada).append("\n");
        stringBuilder.append(" - Agrupación no usada: ").append(agrupacionNoUsada).append("\n");
        return stringBuilder.toString();
    }
    
    private List<String> faltantesDe(List<String> definidos, Set<String> usados) 
    {
        List<String> faltantes = new ArrayList<>();
        if (definidos != null) 
        {
            for (String d : definidos) if (!usados.contains(d)) faltantes.add(d);
        }
        return faltantes;
    }
    
    
    private void guardarComoCsv(String nombreSugerido, String contenido) 
    {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File(nombreSugerido));
        int opcion = selector.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) return;

        try 
        {
            Path destino = selector.getSelectedFile().toPath();
            Files.writeString(destino, contenido, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE); 
            JOptionPane.showMessageDialog(this, "Exportado en:\n" + destino.toAbsolutePath());
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, "No se pudo exportar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String csv(String valor) 
    {
        if (valor == null) return "\"\"";
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
    
    private String construirCsvDeErrores(List<Token> errores) 
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("simbolo_o_cadena,fila,columna_inicio,columna_fin\n");
        for (Token e : errores) 
        {
            stringBuilder.append(csv(e.getLexema())).append(',').append(e.getFila()).append(',')
                    .append(e.getColumnaInicio()).append(',').append(e.getColumnaFin()).append('\n');
        }
        return stringBuilder.toString();
    }
    
    private String construirCsvDeTokens(List<Token> tokens) 
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("nombre_token,lexema,fila,columna_inicio,columna_fin\n");
        for (Token t : tokens) 
        {
            stringBuilder.append(csv(t.getTipo().name())).append(',').append(csv(t.getLexema())).append(',').append(t.getFila()).append(',')
              .append(t.getColumnaInicio()).append(',').append(t.getColumnaFin()).append('\n');
        }
        return stringBuilder.toString();
    }

    private String construirCsvDeRecuento(List<FilaRecuento> recuento) 
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("lexema,tipo,cantidad\n");
        for (FilaRecuento r : recuento) 
        {
            stringBuilder.append(csv(r.lexema)).append(',').append(csv(r.tipo.name())).append(',').append(r.cantidad).append('\n');
        }
        return stringBuilder.toString();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabResultado = new javax.swing.JTabbedPane();
        panelTokens = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTokens = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnExportarTokens = new javax.swing.JButton();
        panelErrores = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaErrores = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnExportarErrores = new javax.swing.JButton();
        panelRecuento = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaRecuento = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnExportarRecuento = new javax.swing.JButton();
        panelReporteGeneral = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        areaReporteGeneral = new javax.swing.JTextArea();
        panelCuentas = new javax.swing.JPanel();
        lblResumen = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Resultado del Análisis Léxico");
        setModal(true);

        panelTokens.setLayout(new java.awt.BorderLayout());

        tablaTokens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaTokens);

        panelTokens.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnExportarTokens.setText("Exportar Tokens");
        btnExportarTokens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarTokensActionPerformed(evt);
            }
        });
        jPanel3.add(btnExportarTokens);

        panelTokens.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        tabResultado.addTab("Tokens", panelTokens);

        panelErrores.setLayout(new java.awt.BorderLayout());

        tablaErrores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tablaErrores);

        panelErrores.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        btnExportarErrores.setText("Exportar Errores");
        btnExportarErrores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarErroresActionPerformed(evt);
            }
        });
        jPanel2.add(btnExportarErrores);

        panelErrores.add(jPanel2, java.awt.BorderLayout.PAGE_END);

        tabResultado.addTab("Errores", panelErrores);

        panelRecuento.setLayout(new java.awt.BorderLayout());

        tablaRecuento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tablaRecuento);

        panelRecuento.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        btnExportarRecuento.setText("Exportar Recuento");
        btnExportarRecuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarRecuentoActionPerformed(evt);
            }
        });
        jPanel1.add(btnExportarRecuento);

        panelRecuento.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        tabResultado.addTab("Recuentos", panelRecuento);

        panelReporteGeneral.setLayout(new java.awt.BorderLayout());

        areaReporteGeneral.setEditable(false);
        areaReporteGeneral.setColumns(20);
        areaReporteGeneral.setLineWrap(true);
        areaReporteGeneral.setRows(5);
        areaReporteGeneral.setWrapStyleWord(true);
        jScrollPane4.setViewportView(areaReporteGeneral);

        panelReporteGeneral.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        tabResultado.addTab("Reporte General", panelReporteGeneral);

        getContentPane().add(tabResultado, java.awt.BorderLayout.CENTER);

        panelCuentas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblResumen.setText("Tokens: 0 | Errores: 0 ");
        panelCuentas.add(lblResumen);

        getContentPane().add(panelCuentas, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExportarErroresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarErroresActionPerformed
        List<Token> errores = resultadoActual.getErrores();
        if (errores.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "No hay errores para exportar.");
            return;
        }
        guardarComoCsv("Errores.csv", construirCsvDeErrores(errores));
    }//GEN-LAST:event_btnExportarErroresActionPerformed

    private void btnExportarRecuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarRecuentoActionPerformed
        if (!resultadoActual.getErrores().isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Existen errores. El reporte de recuento solo se puede si no hay errores.");
            return;
        }
        List<FilaRecuento> recuento = calcularRecuentoDeLexemas(resultadoActual.getTokens());
        guardarComoCsv("Recuento.csv", construirCsvDeRecuento(recuento));
    }//GEN-LAST:event_btnExportarRecuentoActionPerformed

    private void btnExportarTokensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarTokensActionPerformed
        if (!resultadoActual.getErrores().isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Existen errores. El reporte de tokens solo se puede si no hay errores.");
            return;
        }
        guardarComoCsv("Tokens.csv", construirCsvDeTokens(resultadoActual.getTokens()));
    }//GEN-LAST:event_btnExportarTokensActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaReporteGeneral;
    private javax.swing.JButton btnExportarErrores;
    private javax.swing.JButton btnExportarRecuento;
    private javax.swing.JButton btnExportarTokens;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JPanel panelCuentas;
    private javax.swing.JPanel panelErrores;
    private javax.swing.JPanel panelRecuento;
    private javax.swing.JPanel panelReporteGeneral;
    private javax.swing.JPanel panelTokens;
    private javax.swing.JTabbedPane tabResultado;
    private javax.swing.JTable tablaErrores;
    private javax.swing.JTable tablaRecuento;
    private javax.swing.JTable tablaTokens;
    // End of variables declaration//GEN-END:variables
}
