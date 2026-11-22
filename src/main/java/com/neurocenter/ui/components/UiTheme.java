package com.neurocenter.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 * Tema visual y fabricas de componentes Swing de NEUROCENTER.
 * <p>Centraliza colores, fuentes, tablas y botones para lograr una interfaz
 * coherente en todos los modulos.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class UiTheme {

    /** Color de fondo general. */
    public static final Color FONDO = new Color(244, 248, 250);
    /** Color primario (azul profundo). */
    public static final Color PRIMARIO = new Color(10, 68, 107);
    /** Color primario oscuro. */
    public static final Color PRIMARIO_OSCURO = new Color(6, 44, 71);
    /** Color de acento (turquesa). */
    public static final Color ACENTO = new Color(0, 150, 145);
    /** Color de texto principal. */
    public static final Color TEXTO = new Color(30, 45, 58);
    /** Color de texto secundario. */
    public static final Color TEXTO_SUAVE = new Color(105, 122, 136);
    /** Color de bordes. */
    public static final Color BORDE = new Color(213, 224, 231);
    /** Color de fila alterna. */
    public static final Color FILA_ALTERNA = new Color(236, 243, 247);
    /** Fuente base. */
    public static final Font FUENTE = new Font("Segoe UI", Font.PLAIN, 13);
    /** Fuente de titulo. */
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 20);
    /** Fuente de subtitulo. */
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    /** Logotipo institucional. */
    public static final String IMAGEN_LOGO = "/assets/neurocenter1.jpg";
    /** Banner institucional. */
    public static final String IMAGEN_BANNER = "/assets/neurocenter2.png";

    /** Constructor privado: clase utilitaria. */
    private UiTheme() {
    }

    /**
     * Crea un boton con estilo corporativo.
     *
     * @param texto texto del boton
     * @param color color de fondo
     * @return boton configurado
     */
    public static JButton boton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE.deriveFont(Font.BOLD));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setMargin(new Insets(8, 14, 8, 14));
        boton.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Restaura el color original de un boton despues de la accion.
     *
     * @param boton boton
     * @param color color original
     */
    public static void restaurarBoton(JButton boton, Color color) {
        boton.setBackground(color);
    }

    /**
     * Crea una etiqueta de titulo.
     *
     * @param texto texto
     * @return etiqueta
     */
    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_TITULO);
        etiqueta.setForeground(PRIMARIO_OSCURO);
        return etiqueta;
    }

    /**
     * Crea una etiqueta de subtitulo.
     *
     * @param texto texto
     * @return etiqueta
     */
    public static JLabel subtitulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_SUBTITULO);
        etiqueta.setForeground(PRIMARIO);
        return etiqueta;
    }

    /**
     * Crea una etiqueta de campo.
     *
     * @param texto texto
     * @return etiqueta
     */
    public static JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE.deriveFont(Font.BOLD));
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    /**
     * Crea un modelo de tabla no editable.
     *
     * @param columnas nombres de columnas
     * @return modelo
     */
    public static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    /**
     * Crea una tabla con estilo corporativo, ordenamiento y filas alternas.
     *
     * @param modelo modelo de datos
     * @return tabla configurada
     */
    public static JTable tabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo) {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int fila, int columna) {
                Component componente = super.prepareRenderer(renderer, fila, columna);
                if (!isRowSelected(fila)) {
                    componente.setBackground(fila % 2 == 0 ? Color.WHITE : FILA_ALTERNA);
                }
                return componente;
            }
        };
        tabla.setFont(FUENTE);
        tabla.setRowHeight(26);
        tabla.setGridColor(BORDE);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(190, 224, 226));
        tabla.setSelectionForeground(PRIMARIO_OSCURO);
        tabla.setAutoCreateRowSorter(true);
        JTableHeader cabecera = tabla.getTableHeader();
        cabecera.setFont(FUENTE.deriveFont(Font.BOLD));
        cabecera.setBackground(PRIMARIO);
        cabecera.setForeground(Color.WHITE);
        cabecera.setReorderingAllowed(false);
        return tabla;
    }

    /**
     * Crea un panel con borde y titulo para agrupar controles.
     *
     * @param titulo texto del borde
     * @return panel
     */
    public static JPanel grupo(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createTitledBorder(titulo)));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    /**
     * Crea un panel de scroll para una tabla.
     *
     * @param tabla tabla
     * @return panel de scroll
     */
    public static JScrollPane scroll(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        return scroll;
    }

    /**
     * Crea un panel de scroll para componentes de texto enriquecido.
     *
     * @param componente componente
     * @return panel de scroll
     */
    public static JScrollPane scrollTexto(Component componente) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        return scroll;
    }

    /**
     * Muestra un mensaje de error comprensible para el usuario.
     *
     * @param padre componente padre
     * @param error excepcion producida
     */
    public static void mostrarError(Component padre, Exception error) {
        JOptionPane.showMessageDialog(padre, error.getMessage(), "NEUROCENTER - No se pudo completar la operación",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje informativo.
     *
     * @param padre   componente padre
     * @param mensaje texto
     */
    public static void mostrarInfo(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "NEUROCENTER", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Crea un campo de texto con ancho preferido.
     *
     * @param columnas columnas
     * @return campo
     */
    public static JTextField campo(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(FUENTE);
        return campo;
    }

    /**
     * Carga una imagen del classpath y la escala conservando su proporcion.
     *
     * @param recurso     ruta del recurso, por ejemplo /assets/neurocenter1.jpg
     * @param anchoMaximo ancho maximo deseado
     * @param altoMaximo  alto maximo deseado
     * @return icono escalado, o {@code null} si el recurso no existe
     */
    public static ImageIcon iconoEscalado(String recurso, int anchoMaximo, int altoMaximo) {
        URL url = UiTheme.class.getResource(recurso);
        if (url == null) {
            return null;
        }
        ImageIcon original = new ImageIcon(url);
        int ancho = original.getIconWidth();
        int alto = original.getIconHeight();
        if (ancho <= 0 || alto <= 0) {
            return original;
        }
        double escala = Math.min((double) anchoMaximo / ancho, (double) altoMaximo / alto);
        int nuevoAncho = Math.max(1, (int) Math.round(ancho * escala));
        int nuevoAlto = Math.max(1, (int) Math.round(alto * escala));
        return new ImageIcon(original.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH));
    }

    /**
     * Devuelve el ancho preferido estandar de un panel lateral.
     *
     * @return dimension
     */
    public static Dimension tamannoBoton() {
        return new Dimension(180, 42);
    }
}
