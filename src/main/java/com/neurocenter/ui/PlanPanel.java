package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.neurocenter.model.Especialidad;
import com.neurocenter.model.EvaluacionProgreso;
import com.neurocenter.model.Paciente;
import com.neurocenter.model.PlanRehabilitacion;
import com.neurocenter.model.Terapeuta;
import com.neurocenter.service.PacienteService;
import com.neurocenter.service.PlanService;
import com.neurocenter.service.TerapeutaService;
import com.neurocenter.ui.components.ProgressBarRenderer;
import com.neurocenter.ui.components.UiTheme;

/**
 * Modulo de planes de rehabilitacion, avance y evaluaciones de progreso.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PlanPanel extends JPanel implements Actualizable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Servicio de planes. */
    private final PlanService service = new PlanService();
    /** Servicio de pacientes. */
    private final PacienteService pacienteService = new PacienteService();
    /** Servicio de terapeutas. */
    private final TerapeutaService terapeutaService = new TerapeutaService();
    /** Callback global. */
    private final Runnable onGlobalChange;
    /** Combo de pacientes. */
    private final JComboBox<Paciente> comboPaciente = new JComboBox<>();
    /** Combo de especialidad. */
    private final JComboBox<Especialidad> comboEspecialidad = new JComboBox<>(Especialidad.values());
    /** Combo de terapeutas. */
    private final JComboBox<Terapeuta> comboTerapeuta = new JComboBox<>();
    /** Total de sesiones. */
    private final JSpinner sesiones = new JSpinner(new SpinnerNumberModel(12, 1, 500, 1));
    /** Fecha de inicio. */
    private final JSpinner fechaInicio = new JSpinner(
            new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    /** Fecha de fin. */
    private final JSpinner fechaFin = new JSpinner(
            new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    /** Objetivo. */
    private final JTextField objetivo = UiTheme.campo(30);
    /** Modelo de planes. */
    private final DefaultTableModel modelo = UiTheme.modelo("ID", "Paciente", "Terapeuta", "Especialidad",
            "Sesiones", "Avance", "Adherencia", "Objetivo");
    /** Tabla de planes. */
    private final javax.swing.JTable tabla = UiTheme.tabla(modelo);
    /** Etiqueta de detalle del plan. */
    private final JLabel detallePlan = new JLabel("Seleccione un plan para ver sus evaluaciones.");
    /** Modelo de evaluaciones. */
    private final DefaultTableModel modeloEvaluaciones = UiTheme.modelo("ID", "Fecha", "Escala", "Observaciones");
    /** Fecha de evaluacion. */
    private final JSpinner fechaEvaluacion = new JSpinner(
            new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    /** Escala funcional. */
    private final JSpinner escala = new JSpinner(new SpinnerNumberModel(50, 0, 100, 5));
    /** Observaciones. */
    private final JTextField observaciones = UiTheme.campo(30);
    /** Plan seleccionado. */
    private String idSeleccionado;

    /**
     * Crea el panel.
     *
     * @param onGlobalChange callback global
     */
    public PlanPanel(Runnable onGlobalChange) {
        this.onGlobalChange = onGlobalChange;
        construirInterfaz();
        cargarCombos();
    }

    /**
     * Construye la interfaz.
     */
    private void construirInterfaz() {
        setLayout(new BorderLayout(12, 10));
        setBackground(UiTheme.FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        fechaInicio.setEditor(new JSpinner.DateEditor(fechaInicio, "dd/MM/yyyy"));
        fechaFin.setEditor(new JSpinner.DateEditor(fechaFin, "dd/MM/yyyy"));
        fechaEvaluacion.setEditor(new JSpinner.DateEditor(fechaEvaluacion, "dd/MM/yyyy"));

        JPanel formulario = new JPanel(new GridLayout(4, 6, 8, 8));
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        formulario.add(UiTheme.etiqueta("Paciente"));
        formulario.add(comboPaciente);
        formulario.add(UiTheme.etiqueta("Terapia"));
        formulario.add(comboEspecialidad);
        formulario.add(UiTheme.etiqueta("Terapeuta"));
        formulario.add(comboTerapeuta);
        formulario.add(UiTheme.etiqueta("Sesiones totales"));
        formulario.add(sesiones);
        formulario.add(UiTheme.etiqueta("Fecha inicio"));
        formulario.add(fechaInicio);
        formulario.add(UiTheme.etiqueta("Fecha fin"));
        formulario.add(fechaFin);
        formulario.add(UiTheme.etiqueta("Objetivo"));
        formulario.add(objetivo);

        JButton guardar = UiTheme.boton("Guardar plan", UiTheme.PRIMARIO);
        JButton nuevo = UiTheme.boton("Nuevo", UiTheme.TEXTO_SUAVE);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        acciones.setBackground(Color.WHITE);
        acciones.add(guardar);
        acciones.add(nuevo);
        formulario.add(acciones);
        add(formulario, BorderLayout.NORTH);

        tabla.getColumnModel().getColumn(5).setCellRenderer(new ProgressBarRenderer());
        tabla.getColumnModel().getColumn(6).setCellRenderer(new ProgressBarRenderer());
        JPanel tablaPanel = UiTheme.grupo("Planes de rehabilitación");
        tablaPanel.add(UiTheme.scroll(tabla));
        add(tablaPanel, BorderLayout.CENTER);

        JPanel evaluacion = new JPanel(new BorderLayout(8, 8));
        evaluacion.setBackground(Color.WHITE);
        evaluacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDE),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        detallePlan.setFont(UiTheme.FUENTE.deriveFont(java.awt.Font.BOLD));
        detallePlan.setForeground(UiTheme.PRIMARIO);
        evaluacion.add(detallePlan, BorderLayout.NORTH);

        JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        campos.setBackground(Color.WHITE);
        campos.add(new JLabel("Fecha:"));
        campos.add(fechaEvaluacion);
        campos.add(new JLabel("Escala:"));
        campos.add(escala);
        campos.add(new JLabel("Observaciones:"));
        campos.add(observaciones);
        JButton registrar = UiTheme.boton("Registrar evaluación", UiTheme.ACENTO);
        campos.add(registrar);
        evaluacion.add(campos, BorderLayout.CENTER);

        javax.swing.JTable tablaEvaluaciones = UiTheme.tabla(modeloEvaluaciones);
        tablaEvaluaciones.setPreferredScrollableViewportSize(new Dimension(800, 90));
        evaluacion.add(UiTheme.scroll(tablaEvaluaciones), BorderLayout.SOUTH);
        add(evaluacion, BorderLayout.SOUTH);

        comboEspecialidad.addActionListener(e -> cargarTerapeutas());
        guardar.addActionListener(e -> guardar());
        nuevo.addActionListener(e -> limpiar());
        registrar.addActionListener(e -> registrarEvaluacion());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarPlan();
            }
        });
    }

    /**
     * Carga los combos.
     */
    private void cargarCombos() {
        try {
            comboPaciente.removeAllItems();
            pacienteService.listar().stream().filter(Paciente::isActivo).forEach(comboPaciente::addItem);
            cargarTerapeutas();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Carga terapeutas segun especialidad.
     */
    private void cargarTerapeutas() {
        try {
            Especialidad especialidad = (Especialidad) comboEspecialidad.getSelectedItem();
            comboTerapeuta.removeAllItems();
            if (especialidad != null) {
                terapeutaService.porEspecialidad(especialidad).forEach(comboTerapeuta::addItem);
            }
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Guarda el plan del formulario.
     */
    private void guardar() {
        if (comboPaciente.getSelectedItem() == null || comboTerapeuta.getSelectedItem() == null) {
            UiTheme.mostrarInfo(this, "Seleccione paciente y terapeuta.");
            return;
        }
        try {
            PlanRehabilitacion plan = new PlanRehabilitacion();
            plan.setId(idSeleccionado);
            plan.setPacienteId(((Paciente) comboPaciente.getSelectedItem()).getId());
            plan.setTerapeutaId(((Terapeuta) comboTerapeuta.getSelectedItem()).getId());
            plan.setEspecialidad((Especialidad) comboEspecialidad.getSelectedItem());
            plan.setSesionesTotales((Integer) sesiones.getValue());
            plan.setFechaInicio(fechaSpinner(fechaInicio));
            plan.setFechaFin(fechaSpinner(fechaFin));
            plan.setObjetivo(objetivo.getText());
            service.guardar(plan);
            UiTheme.mostrarInfo(this, "Plan guardado correctamente.");
            limpiar();
            onGlobalChange.run();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Registra una evaluacion para el plan seleccionado.
     */
    private void registrarEvaluacion() {
        if (idSeleccionado == null) {
            UiTheme.mostrarInfo(this, "Seleccione un plan primero.");
            return;
        }
        try {
            EvaluacionProgreso evaluacion = new EvaluacionProgreso();
            evaluacion.setPlanId(idSeleccionado);
            evaluacion.setTerapeutaId(service.buscar(idSeleccionado).getTerapeutaId());
            evaluacion.setFecha(fechaSpinner(fechaEvaluacion));
            evaluacion.setEscalaFuncional((Integer) escala.getValue());
            evaluacion.setObservaciones(observaciones.getText());
            service.registrarEvaluacion(evaluacion);
            UiTheme.mostrarInfo(this, "Evaluación registrada.");
            cargarEvaluaciones();
            onGlobalChange.run();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Convierte el valor de un spinner de fecha.
     *
     * @param spinner spinner
     * @return fecha
     */
    private LocalDate fechaSpinner(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Selecciona un plan de la tabla.
     */
    private void seleccionarPlan() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        try {
            idSeleccionado = String.valueOf(modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0));
            PlanRehabilitacion plan = service.buscar(idSeleccionado);
            comboPaciente.setSelectedItem(pacienteService.buscar(plan.getPacienteId()));
            comboEspecialidad.setSelectedItem(plan.getEspecialidad());
            comboTerapeuta.setSelectedItem(terapeutaService.buscar(plan.getTerapeutaId()));
            sesiones.setValue(plan.getSesionesTotales());
            objetivo.setText(plan.getObjetivo());
            detallePlan.setText(String.format("Plan %s · Avance: %.1f%% · Adherencia: %.1f%% · Mejora: %+d pts",
                    idSeleccionado, service.avance(idSeleccionado), service.adherencia(idSeleccionado),
                    service.mejora(idSeleccionado)));
            cargarEvaluaciones();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Carga las evaluaciones del plan seleccionado.
     */
    private void cargarEvaluaciones() {
        try {
            modeloEvaluaciones.setRowCount(0);
            if (idSeleccionado == null) {
                return;
            }
            for (EvaluacionProgreso evaluacion : service.evaluaciones(idSeleccionado)) {
                modeloEvaluaciones.addRow(new Object[]{
                        evaluacion.getId(), evaluacion.getFecha().format(com.neurocenter.util.Fechas.FECHA_LEGIBLE),
                        evaluacion.getEscalaFuncional() + "/100",
                        com.neurocenter.util.Formatos.seguro(evaluacion.getObservaciones())
                });
            }
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Carga la tabla de planes.
     */
    private void cargarTabla() {
        try {
            modelo.setRowCount(0);
            for (PlanRehabilitacion plan : service.listar()) {
                modelo.addRow(new Object[]{
                        plan.getId(),
                        service.nombrePaciente(plan),
                        service.nombreTerapeuta(plan),
                        plan.getEspecialidad().getNombreMostrar(),
                        plan.getSesionesTotales(),
                        service.avance(plan.getId()),
                        service.adherencia(plan.getId()),
                        com.neurocenter.util.Formatos.seguro(plan.getObjetivo())
                });
            }
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Limpia el formulario.
     */
    private void limpiar() {
        idSeleccionado = null;
        sesiones.setValue(12);
        objetivo.setText("");
        detallePlan.setText("Seleccione un plan para ver sus evaluaciones.");
        modeloEvaluaciones.setRowCount(0);
        tabla.clearSelection();
    }

    @Override
    public void actualizar() {
        cargarCombos();
        cargarTabla();
    }
}
