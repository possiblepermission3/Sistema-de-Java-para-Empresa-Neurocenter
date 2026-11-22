package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.neurocenter.config.AppConfig;
import com.neurocenter.model.Usuario;
import com.neurocenter.ui.components.UiTheme;
import com.neurocenter.util.Permisos;

/**
 * Ventana principal de NEUROCENTER. Organiza la navegacion entre modulos
 * mediante CardLayout y mantiene sincronizadas las vistas con la base de datos.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class MainFrame extends JFrame {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Layout de tarjetas. */
    private final java.awt.CardLayout tarjetas = new java.awt.CardLayout();
    /** Contenedor central. */
    private final JPanel areaContenido = new JPanel(tarjetas);
    /** Botones de navegacion. */
    private final Map<String, JButton> botones = new LinkedHashMap<>();
    /** Paneles actualizables. */
    private final List<Actualizable> actualizables = new ArrayList<>();
    /** Etiqueta del modulo activo. */
    private final JLabel etiquetaModulo = new JLabel("Inicio");
    /** Usuario autenticado. */
    private final Usuario usuario;
    /** Rol del usuario autenticado. */
    private final String rol;

    /**
     * Crea la ventana principal.
     *
     * @param usuario usuario autenticado
     */
    public MainFrame(Usuario usuario) {
        super(AppConfig.NOMBRE + " - Sistema de Gestión");
        this.usuario = usuario;
        this.rol = usuario.getRol();
        construirInterfaz();
        setSize(1240, 780);
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        actualizarTodos();
        mostrar("Inicio");
    }

    /**
     * Construye la estructura de la ventana.
     */
    private void construirInterfaz() {
        setLayout(new BorderLayout());
        areaContenido.setBackground(UiTheme.FONDO);
        add(construirCabecera(), BorderLayout.NORTH);
        add(construirMenu(), BorderLayout.WEST);
        add(areaContenido, BorderLayout.CENTER);
        add(construirPie(), BorderLayout.SOUTH);
    }

    /**
     * Construye la cabecera institucional.
     *
     * @return panel
     */
    private JPanel construirCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout(12, 4));
        cabecera.setBackground(UiTheme.PRIMARIO_OSCURO);
        cabecera.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JPanel identidad = new JPanel(new GridLayout(2, 1, 0, 2));
        identidad.setBackground(UiTheme.PRIMARIO_OSCURO);
        JLabel nombre = new JLabel(AppConfig.NOMBRE);
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nombre.setForeground(Color.WHITE);
        JLabel lema = new JLabel("Rehabilitación física · ocupacional · de lenguaje");
        lema.setFont(UiTheme.FUENTE);
        lema.setForeground(new Color(180, 215, 225));
        identidad.add(nombre);
        identidad.add(lema);

        JPanel contacto = new JPanel(new GridLayout(3, 1, 0, 2));
        contacto.setBackground(UiTheme.PRIMARIO_OSCURO);
        contacto.add(etiquetaClara(AppConfig.DIRECCION));
        contacto.add(etiquetaClara(AppConfig.TELEFONO + "  |  " + AppConfig.WEB));
        contacto.add(etiquetaClara("IG: @" + AppConfig.INSTAGRAM + "   TikTok: @" + AppConfig.TIKTOK));
        etiquetaModulo.setFont(UiTheme.FUENTE.deriveFont(Font.BOLD, 15f));
        etiquetaModulo.setForeground(new Color(120, 230, 220));
        etiquetaModulo.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel bloqueIdentidad = new JPanel(new BorderLayout(10, 0));
        bloqueIdentidad.setBackground(UiTheme.PRIMARIO_OSCURO);
        JLabel logo = new JLabel(UiTheme.iconoEscalado(UiTheme.IMAGEN_LOGO, 56, 56));
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        bloqueIdentidad.add(logo, BorderLayout.WEST);
        bloqueIdentidad.add(identidad, BorderLayout.CENTER);

        cabecera.add(bloqueIdentidad, BorderLayout.WEST);
        cabecera.add(contacto, BorderLayout.CENTER);
        cabecera.add(etiquetaModulo, BorderLayout.EAST);
        return cabecera;
    }

    /**
     * Crea una etiqueta de texto claro.
     *
     * @param texto texto
     * @return etiqueta
     */
    private JLabel etiquetaClara(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(UiTheme.FUENTE.deriveFont(Font.PLAIN, 11f));
        etiqueta.setForeground(new Color(200, 220, 230));
        etiqueta.setHorizontalAlignment(SwingConstants.RIGHT);
        return etiqueta;
    }

    /**
     * Construye el menu lateral.
     *
     * @return panel
     */
    private JPanel construirMenu() {
        JPanel menu = new JPanel();
        menu.setBackground(UiTheme.PRIMARIO);
        menu.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));
        menu.setLayout(new GridLayout(0, 1, 0, 8));

        Runnable actualizarGlobal = this::actualizarTodos;
        JLabel titulo = new JLabel("MÓDULOS");
        titulo.setFont(UiTheme.FUENTE.deriveFont(Font.BOLD, 11f));
        titulo.setForeground(new Color(160, 200, 215));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 0));
        menu.add(titulo);

        if (Permisos.puedeAcceder(rol, "Inicio")) {
            agregarModulo("Inicio", "Inicio", new DashboardPanel(actualizarGlobal));
        }
        if (Permisos.puedeAcceder(rol, "Pacientes")) {
            agregarModulo("Pacientes", "Pacientes",
                    new PacientePanel(actualizarGlobal, Permisos.puedeEliminarPaciente(rol)));
        }
        if (Permisos.puedeAcceder(rol, "Terapeutas")) {
            agregarModulo("Terapeutas", "Terapeutas", new TerapeutaPanel(actualizarGlobal));
        }
        if (Permisos.puedeAcceder(rol, "Sesiones")) {
            agregarModulo("Sesiones", "Citas y Sesiones", new SesionPanel(actualizarGlobal));
        }
        if (Permisos.puedeAcceder(rol, "Planes")) {
            agregarModulo("Planes", "Planes y Progreso", new PlanPanel(actualizarGlobal));
        }
        if (Permisos.puedeAcceder(rol, "Pagos")) {
            agregarModulo("Pagos", "Pagos", new PagoPanel(actualizarGlobal));
        }
        if (Permisos.puedeAcceder(rol, "Reportes")) {
            agregarModulo("Reportes", "Reportes", new ReportePanel());
        }
        if (Permisos.puedeAcceder(rol, "Institucional")) {
            agregarModulo("Institucional", "NEUROCENTER", new InstitucionalPanel());
        }

        for (Map.Entry<String, JButton> entrada : botones.entrySet()) {
            JButton boton = entrada.getValue();
            boton.setPreferredSize(new Dimension(210, 42));
            boton.addActionListener(e -> mostrar(entrada.getKey()));
            menu.add(boton);
        }
        menu.add(new JPanel());
        return menu;
    }

    /**
     * Registra un modulo en el menu y en el area de contenido.
     *
     * @param clave  clave del card
     * @param texto  texto del boton
     * @param panel  panel del modulo
     */
    private void agregarModulo(String clave, String texto, JPanel panel) {
        areaContenido.add(panel, clave);
        if (panel instanceof Actualizable actualizable) {
            actualizables.add(actualizable);
        }
        JButton boton = new JButton(texto);
        boton.setFont(UiTheme.FUENTE.deriveFont(Font.BOLD));
        boton.setForeground(Color.WHITE);
        boton.setBackground(UiTheme.PRIMARIO);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 10));
        boton.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        botones.put(clave, boton);
    }

    /**
     * Muestra un modulo y resalta su boton.
     *
     * @param clave clave del modulo
     */
    private void mostrar(String clave) {
        tarjetas.show(areaContenido, clave);
        botones.forEach((nombre, boton) -> boton.setBackground(
                nombre.equals(clave) ? UiTheme.ACENTO : UiTheme.PRIMARIO));
        JButton seleccionado = botones.get(clave);
        etiquetaModulo.setText(seleccionado == null ? clave : seleccionado.getText());
    }

    /**
     * Construye la barra inferior.
     *
     * @return panel
     */
    private JPanel construirPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(UiTheme.PRIMARIO_OSCURO);
        pie.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        JLabel usuarioLabel = new JLabel("Sesión: " + usuario.getNombreCompleto() + " (" + rol + ")");
        usuarioLabel.setFont(UiTheme.FUENTE.deriveFont(Font.PLAIN, 11f));
        usuarioLabel.setForeground(new Color(200, 220, 230));
        JLabel derechos = new JLabel("© 2026 NEUROCENTER · " + AppConfig.DIRECCION);
        derechos.setFont(UiTheme.FUENTE.deriveFont(Font.PLAIN, 11f));
        derechos.setForeground(new Color(200, 220, 230));
        pie.add(usuarioLabel, BorderLayout.WEST);
        pie.add(derechos, BorderLayout.EAST);
        return pie;
    }

    /**
     * Refresca todos los paneles que muestran datos persistentes.
     */
    public void actualizarTodos() {
        actualizables.forEach(Actualizable::actualizar);
    }

    /**
     * Muestra un mensaje general en la ventana principal.
     *
     * @param mensaje texto
     */
    public void informar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, AppConfig.NOMBRE, JOptionPane.INFORMATION_MESSAGE);
    }
}
