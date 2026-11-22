package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.neurocenter.config.AppConfig;
import com.neurocenter.model.Usuario;
import com.neurocenter.service.AuthService;
import com.neurocenter.ui.components.UiTheme;

/**
 * Dialogo modal de autenticacion de NEUROCENTER.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class LoginDialog extends JDialog {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Servicio de autenticacion. */
    private final AuthService authService = new AuthService();
    /** Campo de usuario. */
    private final JTextField campoUsuario = UiTheme.campo(18);
    /** Campo de contraseña. */
    private final JPasswordField campoClave = new JPasswordField(18);
    /** Usuario autenticado. */
    private Usuario usuario;

    /**
     * Crea el dialogo.
     *
     * @param padre componente padre
     */
    private LoginDialog(Component padre) {
        super((Frame) null, "NEUROCENTER - Acceso al sistema", true);
        construirInterfaz();
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    /**
     * Muestra el dialogo y devuelve el usuario autenticado.
     *
     * @param padre componente padre
     * @return usuario, o {@code null} si se cancelo
     */
    public static Usuario mostrar(Component padre) {
        LoginDialog dialogo = new LoginDialog(padre);
        dialogo.setVisible(true);
        return dialogo.usuario;
    }

    /**
     * Construye los componentes del dialogo.
     */
    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(16, 16));
        principal.setBackground(Color.WHITE);
        principal.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel logo = new JLabel(AppConfig.NOMBRE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logo.setForeground(UiTheme.PRIMARIO);
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lema = new JLabel("Sistema de Gestión de Rehabilitación Neurológica");
        lema.setFont(UiTheme.FUENTE);
        lema.setForeground(UiTheme.TEXTO_SUAVE);
        lema.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel cabecera = new JPanel(new GridLayout(2, 1, 0, 4));
        cabecera.setBackground(Color.WHITE);
        cabecera.add(logo);
        cabecera.add(lema);

        JPanel formulario = new JPanel(new GridLayout(2, 2, 10, 12));
        formulario.setBackground(Color.WHITE);
        formulario.add(UiTheme.etiqueta("Usuario"));
        formulario.add(campoUsuario);
        formulario.add(UiTheme.etiqueta("Contraseña"));
        formulario.add(campoClave);
        campoClave.setFont(UiTheme.FUENTE);

        JButton aceptar = UiTheme.boton("Ingresar", UiTheme.PRIMARIO);
        JButton cancelar = UiTheme.boton("Cancelar", UiTheme.TEXTO_SUAVE);
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 0));
        botones.setBackground(Color.WHITE);
        botones.add(aceptar);
        botones.add(cancelar);

        JLabel ayuda = new JLabel("<html><center>Usuarios de prueba:<br><b>admin</b> / admin123 &nbsp;|&nbsp; "
                + "<b>apanaredes</b> / neuro2026</center></html>");
        ayuda.setFont(UiTheme.FUENTE.deriveFont(Font.PLAIN, 11f));
        ayuda.setForeground(UiTheme.TEXTO_SUAVE);
        ayuda.setHorizontalAlignment(SwingConstants.CENTER);

        principal.add(cabecera, BorderLayout.NORTH);
        principal.add(formulario, BorderLayout.CENTER);
        principal.add(ayuda, BorderLayout.SOUTH);
        principal.add(botones, BorderLayout.PAGE_END);
        setContentPane(principal);
        getRootPane().setDefaultButton(aceptar);

        aceptar.addActionListener(this::autenticar);
        cancelar.addActionListener(e -> dispose());
        campoClave.addActionListener(this::autenticar);
    }

    /**
     * Intenta autenticar al usuario.
     *
     * @param evento evento de accion
     */
    private void autenticar(ActionEvent evento) {
        try {
            usuario = authService.login(campoUsuario.getText(),
                    new String(campoClave.getPassword()));
            dispose();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
            campoClave.setText("");
            campoClave.requestFocus();
        }
    }

    /** @return tamanno preferido del dialogo */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(460, 330);
    }
}
