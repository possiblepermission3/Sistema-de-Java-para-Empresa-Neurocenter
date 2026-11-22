package com.neurocenter.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Tarjeta visual para mostrar un indicador numerico destacado.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class MetricCard extends JPanel {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Etiqueta del valor. */
    private final JLabel valor;

    /**
     * Crea una tarjeta.
     *
     * @param titulo titulo del indicador
     * @param texto  valor inicial
     * @param color  color de acento
     */
    public MetricCard(String titulo, String texto, Color color) {
        setLayout(new BorderLayout(6, 4));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        setPreferredSize(new Dimension(210, 96));

        JLabel tituloLabel = new JLabel(titulo.toUpperCase());
        tituloLabel.setFont(UiTheme.FUENTE.deriveFont(Font.BOLD, 10f));
        tituloLabel.setForeground(UiTheme.TEXTO_SUAVE);

        valor = new JLabel(texto);
        valor.setFont(UiTheme.FUENTE_TITULO);
        valor.setForeground(color);
        valor.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel franja = new JPanel();
        franja.setBackground(color);
        franja.setPreferredSize(new Dimension(4, 1));

        add(franja, BorderLayout.WEST);
        add(tituloLabel, BorderLayout.NORTH);
        add(valor, BorderLayout.CENTER);
    }

    /**
     * Actualiza el valor mostrado.
     *
     * @param texto nuevo valor
     */
    public void setValue(String texto) {
        valor.setText(texto);
    }
}
