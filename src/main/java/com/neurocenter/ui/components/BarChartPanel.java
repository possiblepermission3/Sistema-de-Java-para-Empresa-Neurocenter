package com.neurocenter.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

import javax.swing.JPanel;

/**
 * Grafico de barras horizontal dibujado con Graphics2D, sin dependencias
 * externas de librerias graficas.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class BarChartPanel extends JPanel {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Titulo del grafico. */
    private final String titulo;
    /** Color de las barras. */
    private final Color color;
    /** Datos a graficar. */
    private Map<String, Double> datos = Map.of();

    /**
     * Crea el grafico.
     *
     * @param titulo titulo
     * @param color  color de barras
     */
    public BarChartPanel(String titulo, Color color) {
        this.titulo = titulo;
        this.color = color;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(420, 240));
    }

    /**
     * Reemplaza los datos del grafico.
     *
     * @param datos mapa etiqueta -&gt; valor
     */
    public void setDatos(Map<String, Double> datos) {
        this.datos = datos == null ? Map.of() : datos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int ancho = getWidth();
            int alto = getHeight();
            g2.setColor(UiTheme.TEXTO);
            g2.setFont(UiTheme.FUENTE.deriveFont(Font.BOLD, 14f));
            g2.drawString(titulo, 16, 24);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(UiTheme.BORDE);
            g2.drawLine(16, 34, ancho - 16, 34);

            if (datos.isEmpty()) {
                g2.setColor(UiTheme.TEXTO_SUAVE);
                g2.setFont(UiTheme.FUENTE);
                g2.drawString("No hay datos disponibles", 16, 60);
                return;
            }

            double maximo = datos.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            if (maximo <= 0) {
                maximo = 1.0;
            }
            FontMetrics metricas = g2.getFontMetrics(UiTheme.FUENTE);
            int numBarras = Math.min(datos.size(), 6);
            int areaAlto = alto - 60;
            int altoBarra = Math.max(12, Math.min(26, areaAlto / (numBarras * 2)));
            int inicioY = 50;
            int etiquetaAncho = 110;
            int areaBarras = ancho - etiquetaAncho - 80;
            int fila = 0;
            for (Map.Entry<String, Double> entrada : datos.entrySet()) {
                if (fila >= numBarras) {
                    break;
                }
                int y = inicioY + fila * (altoBarra + 12);
                String etiqueta = entrada.getKey();
                while (metricas.stringWidth(etiqueta) > etiquetaAncho - 8 && etiqueta.length() > 3) {
                    etiqueta = etiqueta.substring(0, etiqueta.length() - 2) + ".";
                }
                g2.setFont(UiTheme.FUENTE);
                g2.setColor(UiTheme.TEXTO);
                g2.drawString(etiqueta, 16, y + altoBarra - 5);

                int largo = (int) Math.round(entrada.getValue() / maximo * areaBarras);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 45));
                g2.fillRoundRect(etiquetaAncho + 8, y, areaBarras, altoBarra, 6, 6);
                g2.setColor(color);
                if (largo > 0) {
                    g2.fillRoundRect(etiquetaAncho + 8, y, largo, altoBarra, 6, 6);
                }
                g2.setColor(UiTheme.TEXTO);
                g2.drawString(String.format("%.1f", entrada.getValue()), etiquetaAncho + 16 + areaBarras, y + altoBarra - 5);
                fila++;
            }
        } finally {
            g2.dispose();
        }
    }
}
