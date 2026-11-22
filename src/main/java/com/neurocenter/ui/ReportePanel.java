package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;

import com.neurocenter.config.AppConfig;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.EstadoSesion;
import com.neurocenter.service.ReporteService;
import com.neurocenter.ui.components.BarChartPanel;
import com.neurocenter.ui.components.MetricCard;
import com.neurocenter.ui.components.UiTheme;
import com.neurocenter.util.Formatos;

/**
 * Modulo de reportes y metricas consolidadas.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class ReportePanel extends JPanel implements Actualizable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Servicio de reportes. */
    private final ReporteService service = new ReporteService();
    /** Tarjeta de sesiones. */
    private final MetricCard tarjetaSesiones = new MetricCard("Sesiones totales", "0", UiTheme.PRIMARIO);
    /** Tarjeta de asistencia. */
    private final MetricCard tarjetaAsistencia = new MetricCard("Asistencia", "0%", UiTheme.ACENTO);
    /** Tarjeta de avance. */
    private final MetricCard tarjetaAvance = new MetricCard("Avance planes", "0%", new Color(40, 140, 90));
    /** Tarjeta de ingresos. */
    private final MetricCard tarjetaIngresos = new MetricCard("Ingresos", "S/ 0", new Color(190, 120, 40));
    /** Grafico de sesiones por especialidad. */
    private final BarChartPanel graficoSesiones = new BarChartPanel("Sesiones por especialidad", UiTheme.PRIMARIO);
    /** Grafico de ingresos por mes. */
    private final BarChartPanel graficoIngresos = new BarChartPanel("Ingresos por mes (S/)", UiTheme.ACENTO);
    /** Modelo de estados. */
    private final DefaultTableModel modeloEstados = UiTheme.modelo("Estado", "Sesiones");
    /** Modelo de ranking. */
    private final DefaultTableModel modeloRanking = UiTheme.modelo("Terapeuta", "Sesiones atendidas");
    /** Modelo de costos. */
    private final DefaultTableModel modeloCostos = UiTheme.modelo("Especialidad", "Costo de sesiones");
    /** Modelo de progreso. */
    private final DefaultTableModel modeloProgreso = UiTheme.modelo("Especialidad", "Escala funcional promedio");

    /**
     * Crea el panel.
     */
    public ReportePanel() {
        construirInterfaz();
    }

    /**
     * Construye la interfaz.
     */
    private void construirInterfaz() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UiTheme.FONDO);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 12, 12));
        tarjetas.setBackground(UiTheme.FONDO);
        tarjetas.add(tarjetaSesiones);
        tarjetas.add(tarjetaAsistencia);
        tarjetas.add(tarjetaAvance);
        tarjetas.add(tarjetaIngresos);
        add(tarjetas, BorderLayout.NORTH);

        JPanel graficos = new JPanel(new GridLayout(1, 2, 12, 12));
        graficos.setBackground(UiTheme.FONDO);
        JPanel izquierdo = UiTheme.grupo("Distribución de terapias");
        izquierdo.add(graficoSesiones);
        JPanel derecho = UiTheme.grupo("Evolución de ingresos");
        derecho.add(graficoIngresos);
        graficos.add(izquierdo);
        graficos.add(derecho);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(UiTheme.FUENTE);
        pestanas.addTab("Estados", tablaScroll(modeloEstados));
        pestanas.addTab("Ranking de terapeutas", tablaScroll(modeloRanking));
        pestanas.addTab("Costos por terapia", tablaScroll(modeloCostos));
        pestanas.addTab("Progreso clínico", tablaScroll(modeloProgreso));

        JPanel centro = new JPanel(new GridLayout(2, 1, 12, 12));
        centro.setBackground(UiTheme.FONDO);
        centro.add(graficos);
        centro.add(pestanas);
        centro.setPreferredSize(new Dimension(1000, 600));
        add(centro, BorderLayout.CENTER);

        JButton actualizar = UiTheme.boton("Actualizar reportes", UiTheme.PRIMARIO);
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.setBackground(UiTheme.FONDO);
        JLabel nota = new JLabel("Fuente: datos persistentes en SQLite · " + AppConfig.NOMBRE);
        nota.setForeground(UiTheme.TEXTO_SUAVE);
        nota.setFont(UiTheme.FUENTE);
        pie.add(nota);
        pie.add(actualizar);
        add(pie, BorderLayout.SOUTH);
        actualizar.addActionListener(e -> actualizar());
    }

    /**
     * Crea un panel de scroll para una tabla.
     *
     * @param modelo modelo
     * @return scroll
     */
    private JScrollPane tablaScroll(DefaultTableModel modelo) {
        return UiTheme.scroll(UiTheme.tabla(modelo));
    }

    @Override
    public void actualizar() {
        try {
            Map<String, Object> indicadores = service.indicadores();
            tarjetaSesiones.setValue(String.valueOf(indicadores.get("Sesiones totales")));
            tarjetaAsistencia.setValue(Formatos.porcentaje((Double) indicadores.get("Tasa de asistencia")));
            tarjetaAvance.setValue(Formatos.porcentaje(service.avancePromedio()));
            tarjetaIngresos.setValue(Formatos.moneda((Double) indicadores.get("Ingresos registrados")));

            graficoSesiones.setDatos(convertir(service.sesionesPorEspecialidad()));
            Map<String, Double> ingresos = new LinkedHashMap<>(service.ingresosPorMes());
            graficoIngresos.setDatos(ingresos);

            modeloEstados.setRowCount(0);
            for (Map.Entry<EstadoSesion, Long> entrada : service.sesionesPorEstado().entrySet()) {
                modeloEstados.addRow(new Object[]{entrada.getKey().getNombreMostrar(), entrada.getValue()});
            }

            modeloRanking.setRowCount(0);
            for (Map.Entry<String, Long> entrada : service.topTerapeutas(8)) {
                modeloRanking.addRow(new Object[]{entrada.getKey(), entrada.getValue()});
            }

            modeloCostos.setRowCount(0);
            for (Map.Entry<Especialidad, Double> entrada : service.costoPorEspecialidad().entrySet()) {
                modeloCostos.addRow(new Object[]{
                        entrada.getKey().getNombreMostrar(), Formatos.moneda(entrada.getValue())});
            }

            modeloProgreso.setRowCount(0);
            for (Map.Entry<Especialidad, Double> entrada : service.progresoPorEspecialidad().entrySet()) {
                modeloProgreso.addRow(new Object[]{
                        entrada.getKey().getNombreMostrar(), Formatos.porcentaje(entrada.getValue())});
            }
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Convierte un mapa de especialidades a etiquetas legibles para el grafico.
     *
     * @param origen mapa origen
     * @return mapa convertido
     */
    private Map<String, Double> convertir(Map<Especialidad, Long> origen) {
        Map<String, Double> destino = new LinkedHashMap<>();
        for (Especialidad especialidad : Especialidad.values()) {
            Long valor = origen.get(especialidad);
            if (valor != null) {
                destino.put(especialidad.getNombreMostrar(), valor.doubleValue());
            }
        }
        return destino;
    }
}
