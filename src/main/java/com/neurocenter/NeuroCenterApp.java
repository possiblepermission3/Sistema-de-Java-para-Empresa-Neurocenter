package com.neurocenter;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.neurocenter.config.AppConfig;
import com.neurocenter.db.DatabaseInitializer;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Usuario;
import com.neurocenter.ui.LoginDialog;
import com.neurocenter.ui.MainFrame;
import com.neurocenter.ui.components.UiTheme;

/**
 * Punto de entrada de la aplicacion NEUROCENTER.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class NeuroCenterApp {

    /** Constructor privado: clase de arranque. */
    private NeuroCenterApp() {
    }

    /**
     * Inicializa la base de datos, solicita las credenciales y lanza la interfaz principal en el EDT.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorado) {
                // Se conserva el Look and Feel por defecto del sistema.
            }
            try {
                DatabaseInitializer.inicializar();
            } catch (PersistenciaException error) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo inicializar la base de datos: " + error.getMessage(),
                        AppConfig.NOMBRE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            Usuario usuario = LoginDialog.mostrar(null);
            if (usuario == null) {
                return;
            }
            try {
                new MainFrame(usuario).setVisible(true);
            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, error.getMessage(), AppConfig.NOMBRE,
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
