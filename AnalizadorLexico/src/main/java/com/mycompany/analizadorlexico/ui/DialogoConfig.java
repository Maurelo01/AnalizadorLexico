/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.analizadorlexico.ui;

import com.mycompany.analizadorlexico.configuracion.ConfiguracionES;
import com.mycompany.analizadorlexico.configuracion.ConfiguracionLexica;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.stream.Collectors;

public class DialogoConfig extends javax.swing.JDialog 
{
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DialogoConfig.class.getName());
    private ConfiguracionLexica config;
    public DialogoConfig(java.awt.Frame parent, ConfiguracionLexica config) 
    {
        super(parent, true);
        initComponents();
        this.config = config;
        tablaColores.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        cargarEnUI();
        configurarHandlers();
    }
    
    private void cargarEnUI() 
    {   
        // Listas
        setListModel(listaReservadas, config.getPalabrasReservadas());
        setListModel(listaOperadores, config.getOperadores());
        setListModel(listaAgrupacion, config.getAgrupacion());
        setListModel(listaPuntuacion, config.getPuntuacion());

        // Comentarios
        txtComentarioLinea.setText(orEmpty(config.getComentarioLinea()));
        txtComentarioBloqueInicio.setText(orEmpty(config.getComentarioBloqueInicio()));
        txtComentarioBloqueFin.setText(orEmpty(config.getComentarioBloqueFin()));

        // Colores en tabla
        DefaultTableModel m = (DefaultTableModel) tablaColores.getModel();
        m.setRowCount(0);
        Map<String,String> colores = config.getColores() != null ? config.getColores() : new HashMap<>();
        String[] tipos = {"RESERVADA","IDENTIFICADOR","NUMERO","DECIMAL","CADENA","OPERADOR","AGRUPACION","PUNTUACION","COMENTARIO","ERROR"};
        for (String t : tipos) {
            m.addRow(new Object[]{ t, colores.getOrDefault(t, "#000000") });
        }
    }
    
    private static void setListModel(JList<String> list, List<String> datos) 
    {
        DefaultListModel<String> model = new DefaultListModel<>();
        if (datos != null) datos.forEach(model::addElement);
        list.setModel(model);
    }
    
    private static String orEmpty(String s) 
    { 
        return s == null ? "" : s; 
    }

    private void configurarHandlers()
    {
        btnCerrar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            if (!volcarDesdeUI()) return; // valida
            try
            {
                ConfiguracionES.guardar(config);
                JOptionPane.showMessageDialog(this, "Configuración guardada.");
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnRestablecer.addActionListener(e -> {
            // Restablece a valores por defecto
            if (!commitEdicionTabla()) return;
            int r = JOptionPane.showConfirmDialog(this, "¿Restablecer a valores por defecto?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) 
            {
                try 
                {
                    ConfiguracionLexica base = ConfiguracionES.cargarPorDefecto();// Cargar la copia original
                    copiar(base, config);
                    cargarEnUI();
                    JOptionPane.showMessageDialog(this, "Valores de fábrica cargados en la interfaz.");
                }
                catch (Exception ex) 
                {
                    JOptionPane.showMessageDialog(this, "No se pudo restablecer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnAgregarReservada.addActionListener(e -> agregarA(listaReservadas));
        btnEditarReservada.addActionListener(e -> editarSeleccion(listaReservadas));
        btnEliminarReservada.addActionListener(e -> eliminarSeleccion(listaReservadas));
        btnSubirReservada.addActionListener(e -> moverSeleccion(listaReservadas, -1));
        btnBajarReservada.addActionListener(e -> moverSeleccion(listaReservadas, +1));
        
        btnAgregarOperadores.addActionListener(e -> agregarA(listaOperadores));
        btnEditarOperadores.addActionListener(e -> editarSeleccion(listaOperadores));
        btnEliminarOperadores.addActionListener(e -> eliminarSeleccion(listaOperadores));
        btnSubirOperadores.addActionListener(e -> moverSeleccion(listaOperadores, -1));
        btnBajarOperadores.addActionListener(e -> moverSeleccion(listaOperadores, +1));
        
        btnAgregarAgrupacion.addActionListener(e -> agregarA(listaAgrupacion));
        btnEditarAgrupacion.addActionListener(e -> editarSeleccion(listaAgrupacion));
        btnEliminarAgrupacion.addActionListener(e -> eliminarSeleccion(listaAgrupacion));
        btnSubirAgrupacion.addActionListener(e -> moverSeleccion(listaAgrupacion, -1));
        btnBajarAgrupacion.addActionListener(e -> moverSeleccion(listaAgrupacion, +1));
        
        btnAgregarPuntuacion.addActionListener(e -> agregarA(listaPuntuacion));
        btnEditarPuntuacion.addActionListener(e -> editarSeleccion(listaPuntuacion));
        btnEliminarPuntuacion.addActionListener(e -> eliminarSeleccion(listaPuntuacion));
        btnSubirPuntuacion.addActionListener(e -> moverSeleccion(listaPuntuacion, -1));
        btnBajarPuntuacion.addActionListener(e -> moverSeleccion(listaPuntuacion, +1));
    }
    
    private void agregarA(JList<String> lista) 
    {
        String valor = JOptionPane.showInputDialog(this, "Nuevo valor:");
        if (valor != null && !valor.isBlank()) 
        {
            DefaultListModel<String> m = (DefaultListModel<String>) lista.getModel();
            m.addElement(valor);
        }
    }
    
    private void editarSeleccion(JList<String> lista) 
    {
        int indice = lista.getSelectedIndex();
        if (indice < 0) return;
        DefaultListModel<String> modelo = (DefaultListModel<String>) lista.getModel();
        String actual = modelo.get(indice);
        String valor = JOptionPane.showInputDialog(this, "Editar:", actual);
        if (valor != null && !valor.isBlank()) modelo.set(indice, valor);
    }
    private void eliminarSeleccion(JList<String> lista) 
    {
        int indice = lista.getSelectedIndex();
        if (indice >= 0) ((DefaultListModel<String>) lista.getModel()).remove(indice);
    }
    private void moverSeleccion(JList<String> lista, int aux) 
    {
        int indice = lista.getSelectedIndex();
        if (indice < 0) return;
        int nuevo = indice + aux;
        DefaultListModel<String> modelo = (DefaultListModel<String>) lista.getModel();
        if (nuevo < 0 || nuevo >= modelo.size()) return;
        String valor = modelo.remove(indice);
        modelo.add(nuevo, valor);
        lista.setSelectedIndex(nuevo);
    }
    
    private boolean volcarDesdeUI() 
    {
        if (!commitEdicionTabla()) return false; 
        if (!validarColoresTabla()) return false;// Validacion simple de colores

        config.setPalabrasReservadas(listToArray(listaReservadas));
        config.setOperadores(listToArray(listaOperadores));
        config.setAgrupacion(listToArray(listaAgrupacion));
        config.setPuntuacion(listToArray(listaPuntuacion));

        config.setComentarioLinea(txtComentarioLinea.getText());
        config.setComentarioBloqueInicio(txtComentarioBloqueInicio.getText());
        config.setComentarioBloqueFin(txtComentarioBloqueFin.getText());

        Map<String,String> colores = new LinkedHashMap<>();
        DefaultTableModel m = (DefaultTableModel) tablaColores.getModel();
        for (int i = 0; i < m.getRowCount(); i++) 
        {
            String tipo = String.valueOf(m.getValueAt(i, 0));
            String hex  = String.valueOf(m.getValueAt(i, 1));
            if (hex != null) hex = hex.toUpperCase();
            colores.put(tipo, hex);
        }
        config.setColores(colores);
        return true;
    }
    
    private static List<String> listToArray(JList<String> lista) 
    {
        DefaultListModel<String> modelo = (DefaultListModel<String>) lista.getModel();
        java.util.List<String> resultado = new java.util.ArrayList<>();
        for (int i = 0; i < modelo.size(); i++) {
            String valor = modelo.getElementAt(i);
            boolean yaExiste = false;// solo agrega si aun no esta
            for (int j = 0; j < resultado.size(); j++) 
            {
                if (valor == null ? resultado.get(j) == null : valor.equals(resultado.get(j))) 
                {
                    yaExiste = true;
                    break;
                }
            }
            if (!yaExiste) resultado.add(valor);
        }
        return resultado;
    }
    
    private boolean validarColoresTabla() 
    {
        DefaultTableModel modelo = (DefaultTableModel) tablaColores.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) 
        {
            String codigo = String.valueOf(modelo.getValueAt(i, 1));
            codigo = (codigo == null) ? "" : codigo.trim();
            if (!codigo.matches("#[0-9A-Fa-f]{6}")) 
            {
                JOptionPane.showMessageDialog(this, "Color inválido en fila " + (i+1) + ": " + codigo + " (use #RRGGBB)", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }
    
    private static void copiar(ConfiguracionLexica src, ConfiguracionLexica config) 
    {
        config.setPalabrasReservadas(src.getPalabrasReservadas());
        config.setOperadores(src.getOperadores());
        config.setAgrupacion(src.getAgrupacion());
        config.setPuntuacion(src.getPuntuacion());
        config.setComentarioLinea(src.getComentarioLinea());
        config.setComentarioBloqueInicio(src.getComentarioBloqueInicio());
        config.setComentarioBloqueFin(src.getComentarioBloqueFin());
        config.setColores(src.getColores());
    }
    
    private boolean commitEdicionTabla() 
    {
    if (tablaColores.isEditing()) 
    {
        javax.swing.table.TableCellEditor ed = tablaColores.getCellEditor();
        if (ed != null) return ed.stopCellEditing(); // commit
    }
    return true;
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabsConfig = new javax.swing.JTabbedPane();
        tabReservadas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaReservadas = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        btnAgregarReservada = new javax.swing.JButton();
        btnEditarReservada = new javax.swing.JButton();
        btnEliminarReservada = new javax.swing.JButton();
        btnSubirReservada = new javax.swing.JButton();
        btnBajarReservada = new javax.swing.JButton();
        tabOperadores = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listaOperadores = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        btnAgregarOperadores = new javax.swing.JButton();
        btnEditarOperadores = new javax.swing.JButton();
        btnEliminarOperadores = new javax.swing.JButton();
        btnSubirOperadores = new javax.swing.JButton();
        btnBajarOperadores = new javax.swing.JButton();
        tabAgrupacion = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listaAgrupacion = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        btnAgregarAgrupacion = new javax.swing.JButton();
        btnEditarAgrupacion = new javax.swing.JButton();
        btnEliminarAgrupacion = new javax.swing.JButton();
        btnSubirAgrupacion = new javax.swing.JButton();
        btnBajarAgrupacion = new javax.swing.JButton();
        tabPuntuacion = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listaPuntuacion = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        btnAgregarPuntuacion = new javax.swing.JButton();
        btnEditarPuntuacion = new javax.swing.JButton();
        btnEliminarPuntuacion = new javax.swing.JButton();
        btnSubirPuntuacion = new javax.swing.JButton();
        btnBajarPuntuacion = new javax.swing.JButton();
        tabComentarios = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        lblComentarioLinea = new javax.swing.JLabel();
        txtComentarioLinea = new javax.swing.JTextField();
        lblBloqueInicio = new javax.swing.JLabel();
        txtComentarioBloqueInicio = new javax.swing.JTextField();
        lblBloqueFin = new javax.swing.JLabel();
        txtComentarioBloqueFin = new javax.swing.JTextField();
        tabColores = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tablaColores = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnRestablecer = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Editar config.json");
        setModal(true);
        setPreferredSize(new java.awt.Dimension(700, 620));

        tabReservadas.setToolTipText("");
        tabReservadas.setName(""); // NOI18N
        tabReservadas.setLayout(new java.awt.BorderLayout());

        listaReservadas.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listaReservadas);

        tabReservadas.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnAgregarReservada.setText("Agregar");
        jPanel1.add(btnAgregarReservada);

        btnEditarReservada.setText("Editar");
        jPanel1.add(btnEditarReservada);

        btnEliminarReservada.setText("Eliminar");
        jPanel1.add(btnEliminarReservada);

        btnSubirReservada.setText("Subir");
        jPanel1.add(btnSubirReservada);

        btnBajarReservada.setText("Bajar");
        jPanel1.add(btnBajarReservada);

        tabReservadas.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        tabsConfig.addTab("Palabras Reservadas", tabReservadas);

        tabOperadores.setLayout(new java.awt.BorderLayout());

        listaOperadores.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(listaOperadores);

        tabOperadores.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        btnAgregarOperadores.setText("Agregar");
        jPanel2.add(btnAgregarOperadores);

        btnEditarOperadores.setText("Editar");
        jPanel2.add(btnEditarOperadores);

        btnEliminarOperadores.setText("Eliminar");
        jPanel2.add(btnEliminarOperadores);

        btnSubirOperadores.setText("Subir");
        jPanel2.add(btnSubirOperadores);

        btnBajarOperadores.setText("Bajar");
        jPanel2.add(btnBajarOperadores);

        tabOperadores.add(jPanel2, java.awt.BorderLayout.PAGE_END);

        tabsConfig.addTab("Operadores", tabOperadores);

        tabAgrupacion.setLayout(new java.awt.BorderLayout());

        listaAgrupacion.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(listaAgrupacion);

        tabAgrupacion.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        btnAgregarAgrupacion.setText("Agregar");
        jPanel3.add(btnAgregarAgrupacion);

        btnEditarAgrupacion.setText("Editar");
        jPanel3.add(btnEditarAgrupacion);

        btnEliminarAgrupacion.setText("Eliminar");
        jPanel3.add(btnEliminarAgrupacion);

        btnSubirAgrupacion.setText("Subir");
        jPanel3.add(btnSubirAgrupacion);

        btnBajarAgrupacion.setText("Bajar");
        jPanel3.add(btnBajarAgrupacion);

        tabAgrupacion.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        tabsConfig.addTab("Agrupación", tabAgrupacion);

        tabPuntuacion.setLayout(new java.awt.BorderLayout());

        listaPuntuacion.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(listaPuntuacion);

        tabPuntuacion.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        btnAgregarPuntuacion.setText("Agregar");
        jPanel4.add(btnAgregarPuntuacion);

        btnEditarPuntuacion.setText("Editar");
        jPanel4.add(btnEditarPuntuacion);

        btnEliminarPuntuacion.setText("Eliminar");
        jPanel4.add(btnEliminarPuntuacion);

        btnSubirPuntuacion.setText("Subir");
        jPanel4.add(btnSubirPuntuacion);

        btnBajarPuntuacion.setText("Bajar");
        jPanel4.add(btnBajarPuntuacion);

        tabPuntuacion.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        tabsConfig.addTab("Puntuacion", tabPuntuacion);

        tabComentarios.setToolTipText("");
        tabComentarios.setName(""); // NOI18N
        tabComentarios.setLayout(new java.awt.BorderLayout());

        jPanel7.setLayout(new java.awt.GridBagLayout());

        lblComentarioLinea.setLabelFor(txtComentarioLinea);
        lblComentarioLinea.setText("Comentario de linea ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel7.add(lblComentarioLinea, gridBagConstraints);

        txtComentarioLinea.setColumns(20);
        txtComentarioLinea.setToolTipText("//");
        txtComentarioLinea.setNextFocusableComponent(txtComentarioBloqueInicio);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(txtComentarioLinea, gridBagConstraints);

        lblBloqueInicio.setLabelFor(txtComentarioBloqueInicio);
        lblBloqueInicio.setText("Bloque Inicio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel7.add(lblBloqueInicio, gridBagConstraints);

        txtComentarioBloqueInicio.setColumns(20);
        txtComentarioBloqueInicio.setToolTipText("/*");
        txtComentarioBloqueInicio.setNextFocusableComponent(txtComentarioBloqueFin);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(txtComentarioBloqueInicio, gridBagConstraints);

        lblBloqueFin.setLabelFor(txtComentarioBloqueFin);
        lblBloqueFin.setText("Bloque fin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel7.add(lblBloqueFin, gridBagConstraints);

        txtComentarioBloqueFin.setColumns(20);
        txtComentarioBloqueFin.setToolTipText("/");
        txtComentarioBloqueFin.setNextFocusableComponent(txtComentarioLinea);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(txtComentarioBloqueFin, gridBagConstraints);

        tabComentarios.add(jPanel7, java.awt.BorderLayout.CENTER);

        tabsConfig.addTab("Comentarios", tabComentarios);

        tabColores.setLayout(new java.awt.BorderLayout());

        tablaColores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Reservada", ""},
                {"Identificador", ""},
                {"Número", null},
                {"Decimal", null},
                {"Cadena", null},
                {"Operador", null},
                {"Agrupación", null},
                {"Puntuación", null},
                {"Comentario", null},
                {"Error", null}
            },
            new String [] {
                "Tipo", "Codigo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(tablaColores);

        tabColores.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        btnGuardar.setText("Guardar");
        jPanel5.add(btnGuardar);

        btnRestablecer.setText("Restablecer");
        jPanel5.add(btnRestablecer);

        btnCerrar.setText("Cerrar");
        jPanel5.add(btnCerrar);

        tabColores.add(jPanel5, java.awt.BorderLayout.PAGE_END);

        tabsConfig.addTab("Colores", tabColores);

        getContentPane().add(tabsConfig, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarAgrupacion;
    private javax.swing.JButton btnAgregarOperadores;
    private javax.swing.JButton btnAgregarPuntuacion;
    private javax.swing.JButton btnAgregarReservada;
    private javax.swing.JButton btnBajarAgrupacion;
    private javax.swing.JButton btnBajarOperadores;
    private javax.swing.JButton btnBajarPuntuacion;
    private javax.swing.JButton btnBajarReservada;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEditarAgrupacion;
    private javax.swing.JButton btnEditarOperadores;
    private javax.swing.JButton btnEditarPuntuacion;
    private javax.swing.JButton btnEditarReservada;
    private javax.swing.JButton btnEliminarAgrupacion;
    private javax.swing.JButton btnEliminarOperadores;
    private javax.swing.JButton btnEliminarPuntuacion;
    private javax.swing.JButton btnEliminarReservada;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnRestablecer;
    private javax.swing.JButton btnSubirAgrupacion;
    private javax.swing.JButton btnSubirOperadores;
    private javax.swing.JButton btnSubirPuntuacion;
    private javax.swing.JButton btnSubirReservada;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblBloqueFin;
    private javax.swing.JLabel lblBloqueInicio;
    private javax.swing.JLabel lblComentarioLinea;
    private javax.swing.JList<String> listaAgrupacion;
    private javax.swing.JList<String> listaOperadores;
    private javax.swing.JList<String> listaPuntuacion;
    private javax.swing.JList<String> listaReservadas;
    private javax.swing.JPanel tabAgrupacion;
    private javax.swing.JPanel tabColores;
    private javax.swing.JPanel tabComentarios;
    private javax.swing.JPanel tabOperadores;
    private javax.swing.JPanel tabPuntuacion;
    private javax.swing.JPanel tabReservadas;
    private javax.swing.JTable tablaColores;
    private javax.swing.JTabbedPane tabsConfig;
    private javax.swing.JTextField txtComentarioBloqueFin;
    private javax.swing.JTextField txtComentarioBloqueInicio;
    private javax.swing.JTextField txtComentarioLinea;
    // End of variables declaration//GEN-END:variables
}
