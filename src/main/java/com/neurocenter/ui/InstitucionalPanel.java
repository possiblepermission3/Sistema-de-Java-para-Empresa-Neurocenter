package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;

import com.neurocenter.config.AppConfig;
import com.neurocenter.ui.components.UiTheme;

/**
 * Panel informativo institucional de NEUROCENTER: servicios, contacto y
 * flujo de atencion del negocio.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class InstitucionalPanel extends JPanel {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Crea el panel institucional. */
    public InstitucionalPanel() {
        construirInterfaz();
    }

    /**
     * Construye la interfaz.
     */
    private void construirInterfaz() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UiTheme.FONDO);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setBackground(UiTheme.FONDO);
        titulos.add(UiTheme.titulo(AppConfig.NOMBRE + " · " + AppConfig.RUBRO));
        titulos.add(UiTheme.subtitulo("Rehabilitación física · ocupacional · de lenguaje"));

        JLabel logo = new JLabel(UiTheme.iconoEscalado(UiTheme.IMAGEN_LOGO, 80, 80));
        JLabel banner = new JLabel(UiTheme.iconoEscalado(UiTheme.IMAGEN_BANNER, 620, 230));
        banner.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JPanel imagenes = new JPanel(new BorderLayout(12, 0));
        imagenes.setBackground(UiTheme.FONDO);
        imagenes.add(logo, BorderLayout.WEST);
        imagenes.add(banner, BorderLayout.CENTER);

        JPanel cabecera = new JPanel(new BorderLayout(10, 8));
        cabecera.setBackground(UiTheme.FONDO);
        cabecera.add(titulos, BorderLayout.NORTH);
        cabecera.add(imagenes, BorderLayout.CENTER);
        add(cabecera, BorderLayout.NORTH);

        JEditorPane contenido = new JEditorPane("text/html", construirHtml());
        contenido.setEditable(false);
        contenido.setFont(UiTheme.FUENTE.deriveFont(Font.PLAIN, 13f));
        contenido.setBackground(java.awt.Color.WHITE);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        contenido.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ignorado) {
                    UiTheme.mostrarInfo(this, "No se pudo abrir el enlace: " + e.getURL());
                }
            }
        });
        add(UiTheme.scrollTexto(contenido), BorderLayout.CENTER);
    }

    /**
     * Genera el contenido HTML del panel.
     *
     * @return documento HTML
     */
    private String construirHtml() {
        return "<html><head><style>"
                + "body{font-family:'Segoe UI',Arial,sans-serif;color:#1e2d3a;}"
                + "h2{color:#0a446b;border-bottom:2px solid #009691;padding-bottom:4px;}"
                + "h3{color:#009691;}"
                + "li{margin:4px 0;}"
                + ".card{background:#eef6f8;padding:12px;border-radius:6px;margin:8px 0;}"
                + "</style></head><body>"
                + "<h2>Somos NEUROCENTER</h2>"
                + "<p>Centro especializado en <b>rehabilitación neurológica</b> que brinda atención integral de "
                + "<b>terapia física</b>, <b>terapia ocupacional</b> y <b>terapia de lenguaje</b>. "
                + "Acompañamos a cada paciente desde la evaluación inicial hasta la reinserción en sus "
                + "actividades cotidianas.</p>"
                + "<div class='card'><b>Dirección:</b> " + AppConfig.DIRECCION + "<br>"
                + "<b>Teléfono / WhatsApp:</b> " + AppConfig.TELEFONO + "<br>"
                + "<b>Web:</b> <a href='https://" + AppConfig.WEB + "'>" + AppConfig.WEB + "</a><br>"
                + "<b>Instagram:</b> @" + AppConfig.INSTAGRAM + " &nbsp; <b>TikTok:</b> @" + AppConfig.TIKTOK
                + "</div>"
                + "<h2>Nuestros servicios</h2>"
                + "<ul>"
                + "<li><b>Terapia Física:</b> recuperación de fuerza, marcha, equilibrio y control motor.</li>"
                + "<li><b>Terapia Ocupacional:</b> adaptación de actividades de la vida diaria y funciones ejecutivas.</li>"
                + "<li><b>Terapia de Lenguaje:</b> estimulación del lenguaje expresivo y comprensivo.</li>"
                + "</ul>"
                + "<h2>Flujo de atención (workflow)</h2>"
                + "<ol>"
                + "<li>El paciente llega por recomendación, redes sociales o conocimiento del centro.</li>"
                + "<li>Solicita información y el fisioterapeuta explica costos, duración, horarios y tratamiento.</li>"
                + "<li>Si acepta, escribe al número del jefe y se coordina la evaluación con pago anticipado "
                + "(Interbank / BCP).</li>"
                + "<li>Se comunica al grupo de trabajo por WhatsApp y se crea la historia clínica en Google Drive.</li>"
                + "<li>Se registra el diagnóstico externo antes de la evaluación y el interno después de ella.</li>"
                + "<li>Se asigna la cantidad de sesiones semanales y se valida la conformidad del horario.</li>"
                + "<li>Se asigna al especialista y se informa sobre el pago individual o por paquete.</li>"
                + "<li>Se registra en el cuaderno: pago, número de sesión, monto, tipo de pago, paciente, hora y "
                + "fisioterapeuta.</li>"
                + "<li>El especialista actualiza las planillas de pagos y asistencia en Google Drive.</li>"
                + "<li>Mensualmente se generan y envían boletas e informes de evolución al paciente por WhatsApp.</li>"
                + "</ol>"
                + "<h2>Compromiso</h2>"
                + "<p>Garantizamos la integridad y disponibilidad de la información clínica y administrativa, "
                + "con seguimiento del avance, la adherencia y la satisfacción de cada paciente.</p>"
                + "</body></html>";
    }
}
