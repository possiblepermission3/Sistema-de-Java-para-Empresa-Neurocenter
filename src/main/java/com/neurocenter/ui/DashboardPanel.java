package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import com.neurocenter.model.Paciente;
import com.neurocenter.model.ServicioTerapia;
import com.neurocenter.service.PacienteService;
import com.neurocenter.service.ReporteService;
import com.neurocenter.service.SesionService;
import com.neurocenter.ui.components.MetricCard;
import com.neurocenter.ui.components.UiTheme;
import com.neurocenter.util.Formatos;

/**
 * Panel de inicio con indicadores y agenda del dia.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class DashboardPanel extends JPanel implements Actualizable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Servicio de sesiones. */
    private final SesionService sesionService = new SesionService();
    /** Servicio de reportes. */
    private final ReporteService reporteService = new ReporteService();
    /** Servicio de pacientes. */
    private final PacienteService pacienteService = new PacienteService();
    /** Tarjeta de pacientes. */
    private final MetricCard tarjetaPacientes = new MetricCard("Pacientes", "0", UiTheme.PRIMARIO);
    /** Tarjeta de sesiones programadas. */
    private final MetricCard tarjetaProgramadas = new MetricCard("Programadas", "0", UiTheme.ACENTO);
    /** Tarjeta de sesiones completadas. */
    private final MetricCard tarjetaCompletadas = new MetricCard("Completadas", "0", new Color(40, 140, 90));
    /** Tarjeta de ingresos. */
    private final MetricCard tarjetaIngresos = new MetricCard("Ingresos", "S/ 0", new Color(190, 120, 40));
    /** Modelo de proximas sesiones. */
    private final DefaultTableModel modeloProximas = UiTheme.modelo("Fecha", "Paciente", "Terapia", "Terapeuta");
    /** Modelo de sesiones recientes. */
    private final DefaultTableModel modeloRecientes = UiTheme.modelo("Fecha", "Paciente", "Terapia", "Estado");
    /** Etiqueta de proxima sesion. */
    private final JLabel etiquetaProxima = new JLabel("Próxima sesión: --");
    /** Callback de refresco global. */
    private final Runnable onGlobalChange;

    /**
     * Crea el panel.
     *
     * @param onGlobalChange callback para refrescar el resto de vistas
     */
    public DashboardPanel(Runnable onGlobalChange) {
        this.onGlobalChange = onGlobalChange;
        construirInterfaz();
    }

    /**
     * Construye la interfaz del panel.
     */
    private void construirInterfaz() {
        setLayout(new BorderLayout(14, 14));
        setBackground(UiTheme.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 14, 14));
        tarjetas.setBackground(UiTheme.FONDO);
        tarjetas.add(tarjetaPacientes);
        tarjetas.add(tarjetaProgramadas);
        tarjetas.add(tarjetaCompletadas);
        tarjetas.add(tarjetaIngresos);

        JPanel cabecera = new JPanel(new BorderLayout(12, 10));
        cabecera.setBackground(UiTheme.FONDO);
        JLabel banner = new JLabel(UiTheme.iconoEscalado(UiTheme.IMAGEN_BANNER, 1000, 230));
        banner.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cabecera.add(banner, BorderLayout.CENTER);
        cabecera.add(tarjetas, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        JPanel columnas = new JPanel(new GridLayout(1, 2, 14, 14));
        columnas.setBackground(UiTheme.FONDO);

        JPanel panelProximas = UiTheme.grupo("Próximas sesiones");
        panelProximas.add(UiTheme.scroll(UiTheme.tabla(modeloProximas)));
        JPanel panelRecientes = UiTheme.grupo("Actividad reciente");
        panelRecientes.add(UiTheme.scroll(UiTheme.tabla(modeloRecientes)));
        columnas.add(panelProximas);
        columnas.add(panelRecientes);
        add(columnas, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setBackground(Color.WHITE);
        inferior.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        etiquetaProxima.setFont(UiTheme.FUENTE.deriveFont(java.awt.Font.BOLD, 14f));
        etiquetaProxima.setForeground(UiTheme.PRIMARIO);
        JLabel logo = new JLabel(UiTheme.iconoEscalado(UiTheme.IMAGEN_LOGO, 44, 44));
        inferior.add(logo, BorderLayout.WEST);
        JLabel ayuda = new JLabel("Use el menú lateral para gestionar pacientes, terapeutas y sesiones.");
        ayuda.setFont(UiTheme.FUENTE);
        ayuda.setForeground(UiTheme.TEXTO_SUAVE);
        JPanel mensajes = new JPanel(new GridLayout(2, 1, 0, 2));
        mensajes.setBackground(Color.WHITE);
        mensajes.add(etiquetaProxima);
        mensajes.add(ayuda);
        inferior.add(mensajes, BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(1000, 620));
    }

    @Override
    public void actualizar() {
        try {
            Map<String, Object> indicadores = reporteService.indicadores();
            tarjetaPacientes.setValue(String.valueOf(indicadores.get("Pacientes registrados")));
            tarjetaProgramadas.setValue(String.valueOf(indicadores.get("Sesiones programadas")));
            tarjetaCompletadas.setValue(String.valueOf(indicadores.get("Sesiones completadas")));
            tarjetaIngresos.setValue(Formatos.moneda((Double) indicadores.get("Ingresos registrados")));

            List<Paciente> pacientes = pacienteService.listar();
            Map<String, String> nombresPaciente = pacientes.stream()
                    .collect(Collectors.toMap(Paciente::getId, Paciente::getNombreCompleto));
            Map<String, String> nombresTerapeuta = new java.util.HashMap<>();
            for (com.neurocenter.model.Terapeuta terapeuta
                    : new com.neurocenter.service.TerapeutaService().listar()) {
                nombresTerapeuta.put(terapeuta.getId(), terapeuta.getNombreCompleto());
            }

            modeloProximas.setRowCount(0);
            List<ServicioTerapia> proximas = sesionService.proximas();
            for (ServicioTerapia sesion : proximas) {
                modeloProximas.addRow(new Object[]{
                        sesion.getFechaHora().format(com.neurocenter.util.Fechas.LEGIBLE),
                        nombresPaciente.getOrDefault(sesion.getPacienteId(), sesion.getPacienteId()),
                        sesion.getEspecialidad().getNombreMostrar(),
                        nombresTerapeuta.getOrDefault(sesion.getTerapeutaId(), sesion.getTerapeutaId())
                });
            }
            if (!proximas.isEmpty()) {
                ServicioTerapia siguiente = proximas.get(0);
                etiquetaProxima.setText("Próxima sesión: " + siguiente.getFechaHora()
                        .format(com.neurocenter.util.Fechas.LEGIBLE) + " · "
                        + nombresPaciente.getOrDefault(siguiente.getPacienteId(), "Paciente"));
            } else {
                etiquetaProxima.setText("Próxima sesión: no hay sesiones programadas");
            }

            modeloRecientes.setRowCount(0);
            for (ServicioTerapia sesion : sesionService.recientes(8)) {
                modeloRecientes.addRow(new Object[]{
                        sesion.getFechaHora().format(com.neurocenter.util.Fechas.LEGIBLE),
                        nombresPaciente.getOrDefault(sesion.getPacienteId(), sesion.getPacienteId()),
                        sesion.getEspecialidad().getNombreMostrar(),
                        sesion.getEstado().getNombreMostrar()
                });
            }
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }
}
