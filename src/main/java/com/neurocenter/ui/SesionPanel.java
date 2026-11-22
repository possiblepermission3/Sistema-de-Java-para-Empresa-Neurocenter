package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.neurocenter.model.Especialidad;
import com.neurocenter.model.EstadoAsistencia;
import com.neurocenter.model.EstadoSesion;
import com.neurocenter.model.Paciente;
import com.neurocenter.model.PlanRehabilitacion;
import com.neurocenter.model.ServicioTerapia;
import com.neurocenter.model.SesionFactory;
import com.neurocenter.model.TerapiaFisica;
import com.neurocenter.model.TerapiaLenguaje;
import com.neurocenter.model.TerapiaOcupacional;
import com.neurocenter.model.Terapeuta;
import com.neurocenter.service.PacienteService;
import com.neurocenter.service.PlanService;
import com.neurocenter.service.SesionService;
import com.neurocenter.service.TerapeutaService;
import com.neurocenter.ui.components.UiTheme;
import com.neurocenter.util.Formatos;

/**
 * Modulo de programacion y seguimiento de citas/sesiones de terapia.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class SesionPanel extends JPanel implements Actualizable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Servicio de sesiones. */
    private final SesionService service = new SesionService();
    /** Servicio de pacientes. */
    private final PacienteService pacienteService = new PacienteService();
    /** Servicio de terapeutas. */
    private final TerapeutaService terapeutaService = new TerapeutaService();
    /** Servicio de planes. */
    private final PlanService planService = new PlanService();
    /** Callback global. */
    private final Runnable onGlobalChange;
    /** Combo de pacientes. */
    private final JComboBox<Paciente> comboPaciente = new JComboBox<>();
    /** Combo de especialidad. */
    private final JComboBox<Especialidad> comboEspecialidad = new JComboBox<>(Especialidad.values());
    /** Combo de terapeutas. */
    private final JComboBox<Terapeuta> comboTerapeuta = new JComboBox<>();
    /** Combo de planes. */
    private final JComboBox<PlanRehabilitacion> comboPlan = new JComboBox<>();
    /** Selector de fecha. */
    private final JSpinner fecha = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    /** Selector de hora. */
    private final JSpinner hora = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
    /** Selector de duracion. */
    private final JSpinner duracion = new JSpinner(new SpinnerNumberModel(60, 15, 240, 15));
    /** Campo de detalle especifico. */
    private final JTextField detalle = UiTheme.campo(22);
    /** Campo de notas. */
    private final JTextField notas = UiTheme.campo(22);
    /** Etiqueta de detalle. */
    private final JLabel etiquetaDetalle = UiTheme.etiqueta("Área tratada");
    /** Etiqueta de costo estimado. */
    private final JLabel costoEstimado = new JLabel("Costo estimado: S/ 0.00");
    /** Modelo de sesiones. */
    private final DefaultTableModel modelo = UiTheme.modelo("ID", "Fecha y hora", "Paciente", "Terapeuta",
            "Terapia", "Detalle", "Duración", "Costo", "Estado");
    /** Tabla. */
    private final javax.swing.JTable tabla = UiTheme.tabla(modelo);
    /** Filtro de especialidad. */
    private final JComboBox<Object> filtroEspecialidad = new JComboBox<>();
    /** Filtro de estado. */
    private final JComboBox<Object> filtroEstado = new JComboBox<>();
    /** Campo de busqueda. */
    private final JTextField busqueda = UiTheme.campo(14);

    /**
     * Crea el panel.
     *
     * @param onGlobalChange callback global
     */
    public SesionPanel(Runnable onGlobalChange) {
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

        fecha.setEditor(new JSpinner.DateEditor(fecha, "dd/MM/yyyy"));
        hora.setEditor(new JSpinner.DateEditor(hora, "HH:mm"));

        JPanel formulario = new JPanel(new GridLayout(5, 6, 8, 8));
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
        formulario.add(UiTheme.etiqueta("Fecha"));
        formulario.add(fecha);
        formulario.add(UiTheme.etiqueta("Hora"));
        formulario.add(hora);
        formulario.add(UiTheme.etiqueta("Duración (min)"));
        formulario.add(duracion);
        formulario.add(etiquetaDetalle);
        formulario.add(detalle);
        formulario.add(UiTheme.etiqueta("Plan (opcional)"));
        formulario.add(comboPlan);
        formulario.add(UiTheme.etiqueta("Notas"));
        formulario.add(notas);
        formulario.add(costoEstimado);

        JButton programar = UiTheme.boton("Programar sesión", UiTheme.PRIMARIO);
        JButton completar = UiTheme.boton("Completar", new Color(40, 140, 90));
        JButton reprogramar = UiTheme.boton("Reprogramar", UiTheme.ACENTO);
        JButton cancelar = UiTheme.boton("Cancelar sesión", new java.awt.Color(170, 60, 60));
        JButton limpiar = UiTheme.boton("Limpiar", UiTheme.TEXTO_SUAVE);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        acciones.setBackground(Color.WHITE);
        acciones.add(programar);
        acciones.add(completar);
        acciones.add(reprogramar);
        acciones.add(cancelar);
        acciones.add(limpiar);
        formulario.add(acciones);

        add(formulario, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtros.setBackground(UiTheme.FONDO);
        filtroEspecialidad.addItem("Todas");
        for (Especialidad especialidad : Especialidad.values()) {
            filtroEspecialidad.addItem(especialidad);
        }
        filtroEstado.addItem("Todos");
        for (EstadoSesion estado : EstadoSesion.values()) {
            filtroEstado.addItem(estado);
        }
        filtros.add(new JLabel("Terapia:"));
        filtros.add(filtroEspecialidad);
        filtros.add(new JLabel("Estado:"));
        filtros.add(filtroEstado);
        filtros.add(new JLabel("Buscar:"));
        filtros.add(busqueda);
        add(filtros, BorderLayout.CENTER);

        JPanel tablaPanel = UiTheme.grupo("Agenda de sesiones");
        tablaPanel.add(UiTheme.scroll(tabla));
        add(tablaPanel, BorderLayout.SOUTH);

        comboEspecialidad.addActionListener(e -> {
            cargarTerapeutas();
            actualizarEtiquetaDetalle();
            actualizarCosto();
        });
        comboTerapeuta.addActionListener(e -> actualizarCosto());
        duracion.addChangeListener(e -> actualizarCosto());
        programar.addActionListener(e -> programar());
        completar.addActionListener(e -> completar());
        reprogramar.addActionListener(e -> reprogramar());
        cancelar.addActionListener(e -> cancelarSesion());
        limpiar.addActionListener(e -> limpiar());
        filtroEspecialidad.addActionListener(e -> cargarTabla());
        filtroEstado.addActionListener(e -> cargarTabla());
        busqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                cargarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cargarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cargarTabla();
            }
        });
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });
    }

    /**
     * Carga los combos de pacientes, terapeutas y planes.
     */
    private void cargarCombos() {
        try {
            comboPaciente.removeAllItems();
            pacienteService.listar().stream()
                    .filter(Paciente::isActivo)
                    .forEach(comboPaciente::addItem);
            comboPlan.removeAllItems();
            comboPlan.addItem(null);
            planService.listarActivos().forEach(comboPlan::addItem);
            cargarTerapeutas();
            actualizarEtiquetaDetalle();
            actualizarCosto();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Carga los terapeutas segun la especialidad seleccionada.
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
     * Actualiza la etiqueta del campo especifico segun la terapia.
     */
    private void actualizarEtiquetaDetalle() {
        Especialidad especialidad = (Especialidad) comboEspecialidad.getSelectedItem();
        if (especialidad == Especialidad.FISICA) {
            etiquetaDetalle.setText("Área tratada");
        } else if (especialidad == Especialidad.OCUPACIONAL) {
            etiquetaDetalle.setText("Actividad adaptada");
        } else {
            etiquetaDetalle.setText("Objetivo de lenguaje");
        }
    }

    /**
     * Calcula el costo estimado usando polimorfismo.
     */
    private void actualizarCosto() {
        try {
            Especialidad especialidad = (Especialidad) comboEspecialidad.getSelectedItem();
            Terapeuta terapeuta = (Terapeuta) comboTerapeuta.getSelectedItem();
            if (especialidad == null) {
                return;
            }
            ServicioTerapia sesion = SesionFactory.crear(especialidad);
            sesion.setDuracionMinutos((Integer) duracion.getValue());
            double tarifa = terapeuta == null ? com.neurocenter.config.AppConfig.tarifaBase(especialidad)
                    : terapeuta.getTarifaHora();
            double costo = sesion.calcularCosto(tarifa);
            PlanRehabilitacion plan = (PlanRehabilitacion) comboPlan.getSelectedItem();
            if (plan != null && plan.getSesionesTotales() >= com.neurocenter.config.AppConfig.SESIONES_MINIMAS_DESCUENTO) {
                costo *= (1 - com.neurocenter.config.AppConfig.DESCUENTO_PAQUETE);
            }
            costoEstimado.setText("Costo estimado: " + Formatos.moneda(costo));
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Obtiene la fecha y hora seleccionadas.
     *
     * @return fecha y hora
     */
    private LocalDateTime fechaHoraSeleccionada() {
        LocalDate dia = ((Date) fecha.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime horaDia = ((Date) hora.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        return LocalDateTime.of(dia, horaDia);
    }

    /**
     * Construye una sesion a partir del formulario.
     *
     * @return sesion
     */
    private ServicioTerapia construirSesion() {
        Especialidad especialidad = (Especialidad) comboEspecialidad.getSelectedItem();
        ServicioTerapia sesion = SesionFactory.crear(especialidad);
        sesion.setPacienteId(((Paciente) comboPaciente.getSelectedItem()).getId());
        sesion.setTerapeutaId(((Terapeuta) comboTerapeuta.getSelectedItem()).getId());
        PlanRehabilitacion plan = (PlanRehabilitacion) comboPlan.getSelectedItem();
        sesion.setPlanId(plan == null ? null : plan.getId());
        sesion.setFechaHora(fechaHoraSeleccionada());
        sesion.setDuracionMinutos((Integer) duracion.getValue());
        sesion.setNotas(notas.getText());
        if (sesion instanceof TerapiaFisica fisica) {
            fisica.setAreaTratada(detalle.getText());
        } else if (sesion instanceof TerapiaOcupacional ocupacional) {
            ocupacional.setActividadAdaptada(detalle.getText());
        } else if (sesion instanceof TerapiaLenguaje lenguaje) {
            lenguaje.setObjetivoLenguaje(detalle.getText());
        }
        return sesion;
    }

    /**
     * Programa la sesion del formulario.
     */
    private void programar() {
        if (comboPaciente.getSelectedItem() == null || comboTerapeuta.getSelectedItem() == null) {
            UiTheme.mostrarInfo(this, "Seleccione un paciente y un terapeuta.");
            return;
        }
        try {
            ServicioTerapia sesion = service.programar(construirSesion());
            UiTheme.mostrarInfo(this, "Sesión " + sesion.getId() + " programada. Costo: "
                    + Formatos.moneda(sesion.getCosto()));
            limpiar();
            onGlobalChange.run();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Completa la sesion seleccionada registrando asistencia.
     */
    private void completar() {
        String id = idSeleccionado();
        if (id == null) {
            UiTheme.mostrarInfo(this, "Seleccione una sesión de la lista.");
            return;
        }
        Object estado = JOptionPane.showInputDialog(this, "Estado de asistencia:",
                "Completar sesión", JOptionPane.QUESTION_MESSAGE, null,
                EstadoAsistencia.values(), EstadoAsistencia.ASISTIO);
        if (estado == null) {
            return;
        }
        String nota = JOptionPane.showInputDialog(this, "Nota de evolución:", "Completar sesión",
                JOptionPane.QUESTION_MESSAGE);
        try {
            service.completar(id, (EstadoAsistencia) estado, nota == null ? "" : nota);
            UiTheme.mostrarInfo(this, "Sesión completada y asistencia registrada.");
            onGlobalChange.run();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Reprograma la sesion seleccionada.
     */
    private void reprogramar() {
        String id = idSeleccionado();
        if (id == null) {
            UiTheme.mostrarInfo(this, "Seleccione una sesión de la lista.");
            return;
        }
        String textoFecha = JOptionPane.showInputDialog(this, "Nueva fecha (dd/MM/yyyy):");
        if (textoFecha == null) {
            return;
        }
        String textoHora = JOptionPane.showInputDialog(this, "Nueva hora (HH:mm):");
        if (textoHora == null) {
            return;
        }
        try {
            LocalDate dia = LocalDate.parse(textoFecha, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalTime horaDia = LocalTime.parse(textoHora, DateTimeFormatter.ofPattern("HH:mm"));
            service.reprogramar(id, LocalDateTime.of(dia, horaDia));
            UiTheme.mostrarInfo(this, "Sesión reprogramada correctamente.");
            onGlobalChange.run();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Cancela la sesion seleccionada.
     */
    private void cancelarSesion() {
        String id = idSeleccionado();
        if (id == null) {
            UiTheme.mostrarInfo(this, "Seleccione una sesión de la lista.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea cancelar la sesión seleccionada?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                service.cancelar(id);
                UiTheme.mostrarInfo(this, "Sesión cancelada.");
                onGlobalChange.run();
            } catch (Exception error) {
                UiTheme.mostrarError(this, error);
            }
        }
    }

    /**
     * @return id de la sesion seleccionada
     */
    private String idSeleccionado() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : String.valueOf(modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0));
    }

    /**
     * Carga los datos de la sesion seleccionada.
     */
    private void seleccionarFila() {
        String id = idSeleccionado();
        if (id == null) {
            return;
        }
        try {
            ServicioTerapia sesion = service.buscar(id);
            Paciente paciente = pacienteService.buscar(sesion.getPacienteId());
            comboPaciente.setSelectedItem(paciente);
            comboEspecialidad.setSelectedItem(sesion.getEspecialidad());
            Terapeuta terapeuta = terapeutaService.buscar(sesion.getTerapeutaId());
            comboTerapeuta.setSelectedItem(terapeuta);
            fecha.setValue(java.util.Date.from(sesion.getFechaHora()
                    .atZone(ZoneId.systemDefault()).toInstant()));
            hora.setValue(java.util.Date.from(sesion.getFechaHora()
                    .atZone(ZoneId.systemDefault()).toInstant()));
            duracion.setValue(sesion.getDuracionMinutos());
            detalle.setText(sesion.getDetalleEspecifico());
            notas.setText(Formatos.seguro(sesion.getNotas()));
            actualizarCosto();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Carga la tabla con filtros.
     */
    private void cargarTabla() {
        try {
            modelo.setRowCount(0);
            Map<String, String> nombresPaciente = pacienteService.listar().stream()
                    .collect(Collectors.toMap(Paciente::getId, Paciente::getNombreCompleto));
            Map<String, String> nombresTerapeuta = terapeutaService.listar().stream()
                    .collect(Collectors.toMap(Terapeuta::getId, Terapeuta::getNombreCompleto));
            Especialidad esp = filtroEspecialidad.getSelectedItem() instanceof Especialidad e ? e : null;
            EstadoSesion estado = filtroEstado.getSelectedItem() instanceof EstadoSesion es ? es : null;
            List<ServicioTerapia> sesiones = service.filtrar(service.listar(), esp, estado, busqueda.getText());
            for (ServicioTerapia sesion : sesiones) {
                modelo.addRow(new Object[]{
                        sesion.getId(),
                        sesion.getFechaHora().format(com.neurocenter.util.Fechas.LEGIBLE),
                        nombresPaciente.getOrDefault(sesion.getPacienteId(), sesion.getPacienteId()),
                        nombresTerapeuta.getOrDefault(sesion.getTerapeutaId(), sesion.getTerapeutaId()),
                        sesion.getEspecialidad().getNombreMostrar(),
                        sesion.getDetalleEspecifico(),
                        sesion.getDuracionMinutos() + " min",
                        Formatos.moneda(sesion.getCosto()),
                        sesion.getEstado().getNombreMostrar()
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
        comboPaciente.setSelectedIndex(-1);
        comboTerapeuta.setSelectedIndex(-1);
        detalle.setText("");
        notas.setText("");
        tabla.clearSelection();
        actualizarCosto();
    }

    @Override
    public void actualizar() {
        cargarCombos();
        cargarTabla();
    }
}
