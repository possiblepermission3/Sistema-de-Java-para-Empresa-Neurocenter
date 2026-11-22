package com.neurocenter.ui.components;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer de celdas que muestra un porcentaje como barra de progreso.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class ProgressBarRenderer extends DefaultTableCellRenderer {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Barra reutilizada para el pintado. */
    private final JProgressBar barra = new JProgressBar(0, 100);

    /** Crea el renderer. */
    public ProgressBarRenderer() {
        barra.setForeground(UiTheme.ACENTO);
        barra.setStringPainted(true);
        barra.setFont(UiTheme.FUENTE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                                                   boolean foco, int fila, int columna) {
        double porcentaje = 0;
        if (valor instanceof Number numero) {
            porcentaje = numero.doubleValue();
        } else if (valor instanceof String texto) {
            try {
                porcentaje = Double.parseDouble(texto);
            } catch (NumberFormatException ignorado) {
                porcentaje = 0;
            }
        }
        int entero = (int) Math.max(0, Math.min(100, Math.round(porcentaje)));
        barra.setValue(entero);
        barra.setString(entero + "%");
        barra.setBackground(seleccionado ? new java.awt.Color(190, 224, 226) : java.awt.Color.WHITE);
        return barra;
    }
}
