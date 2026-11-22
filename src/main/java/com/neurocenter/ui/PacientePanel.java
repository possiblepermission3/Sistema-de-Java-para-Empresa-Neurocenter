package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.neurocenter.model.HistorialClinico;
import com.neurocenter.model.Paciente;
import com.neurocenter.service.PacienteService;
import com.neurocenter.ui.components.UiTheme;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.Formatos;

/**
 * Modulo de gestion de pacientes y su historia clinica.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PacientePanel extends JPanel implements Actualizable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Servicio de pacientes. */
    private final PacienteService service = new PacienteService();
    /** Callback global. */
    private final Runnable onGlobalChange;
    /** Modelo de la tabla. */
    private final DefaultTableModel modelo = UiTheme.modelo("ID", "Nombre", "Apellido", "DNI", "Edad",
            "Celular", "Domicilio", "Estado", "Diagnóstico referido");
    /** Tabla de pacientes. */
    private final javax.swing.JTable tabla = UiTheme.tabla(modelo);
    /** Campo nombre. */
    private final JTextField nombre = UiTheme.campo(14);
    /** Campo apellido. */
    private final JTextField apellido = UiTheme.campo(14);
    /** Campo DNI. */
    private final JTextField dni = UiTheme.campo(10);
    /** Campo edad. */
    private final JSpinner edad = new JSpinner(new SpinnerNumberModel(30, 0, 120, 1));
    /** Campo telefono. */
    private final JTextField telefono = UiTheme.campo(11);
    /** Campo domicilio. */
    private final JTextField domicilio = UiTheme.campo(20);
    /** Campo diagnostico. */
    private final JTextField diagnostico = UiTheme.campo(24);
    /** Campo de busqueda. */
    private final JTextField busqueda = UiTheme.campo(18);
    /** Paciente seleccionado. */
    private String idSeleccionado;

    /**
     * Crea el panel.
     *
     * @param onGlobalChange callback para refrescar el resto de vistas
     */
    public PacientePanel(Runnable onGlobalChange) {
        this.onGlobalChange = onGlobalChange;
        construirInterfaz();
    }

    /**
     * Construye la interfaz.
     */
    private void construirInterfaz() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UiTheme.FONDO);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel formulario = new JPanel(new GridLayout(4, 4, 10, 10));
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDE),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        formulario.add(UiTheme.etiqueta("Nombre"));
        formulario.add(nombre);
        formulario.add(UiTheme.etiqueta("Apellido"));
        formulario.add(apellido);
        formulario.add(UiTheme.etiqueta("DNI"));
        formulario.add(dni);
        formulario.add(UiTheme.etiqueta("Edad"));
        formulario.add(edad);
        formulario.add(UiTheme.etiqueta("Celular"));
        formulario.add(telefono);
        formulario.add(UiTheme.etiqueta("Domicilio"));
        formulario.add(domicilio);
        formulario.add(UiTheme.etiqueta("Diagnóstico referido"));
        formulario.add(diagnostico);

        JButton nuevo = UiTheme.boton("Nuevo", UiTheme.TEXTO_SUAVE);
        JButton guardar = UiTheme.boton("Guardar", UiTheme.PRIMARIO);
        JButton eliminar = UiTheme.boton("Eliminar", new java.awt.Color(170, 60, 60));
        JButton historia = UiTheme.boton("Historia clínica", UiTheme.ACENTO);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        acciones.setBackground(Color.WHITE);
        acciones.add(nuevo);
        acciones.add(guardar);
        acciones.add(eliminar);
        acciones.add(historia);
        formulario.add(acciones);

        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        buscador.setBackground(Color.WHITE);
        buscador.add(new JLabel("Buscar:"));
        buscador.add(busqueda);
        formulario.add(buscador);

        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setBackground(UiTheme.FONDO);
        norte.add(formulario, BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        JPanel tablaPanel = UiTheme.grupo("Listado de pacientes");
        tablaPanel.add(UiTheme.scroll(tabla));
        add(tablaPanel, BorderLayout.CENTER);

        nuevo.addActionListener(e -> limpiar());
        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> eliminar());
        historia.addActionListener(e -> verHistoria());
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
     * Carga la tabla segun el criterio de busqueda.
     */
    private void cargarTabla() {
        try {
            modelo.setRowCount(0);
            for (Paciente paciente : service.buscarPorTexto(busqueda.getText())) {
                modelo.addRow(new Object[]{
                        paciente.getId(), paciente.getNombre(), paciente.getApellido(), paciente.getDni(),
                        paciente.getEdad(), paciente.getTelefono(), paciente.getDomicilio(),
                        paciente.isActivo() ? "Activo" : "Baja",
                        Formatos.seguro(paciente.getDiagnosticoReferido())
                });
            }
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Carga los datos de la fila seleccionada en el formulario.
     */
    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        try {
            String id = String.valueOf(modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0));
            Paciente paciente = service.buscar(id);
            idSeleccionado = paciente.getId();
            nombre.setText(paciente.getNombre());
            apellido.setText(paciente.getApellido());
            dni.setText(paciente.getDni());
            edad.setValue(paciente.getEdad());
            telefono.setText(paciente.getTelefono());
            domicilio.setText(paciente.getDomicilio());
            diagnostico.setText(Formatos.seguro(paciente.getDiagnosticoReferido()));
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Guarda el paciente del formulario.
     */
    private void guardar() {
        try {
            Paciente paciente = new Paciente();
            paciente.setId(idSeleccionado);
            paciente.setNombre(nombre.getText());
            paciente.setApellido(apellido.getText());
            paciente.setDni(dni.getText());
            paciente.setEdad((Integer) edad.getValue());
            paciente.setTelefono(telefono.getText());
            paciente.setDomicilio(domicilio.getText());
            paciente.setDiagnosticoReferido(diagnostico.getText());
            paciente.setFechaRegistro(Fechas.leerFecha(Fechas.FECHA_ISO.format(java.time.LocalDate.now())));
            service.guardar(paciente);
            UiTheme.mostrarInfo(this, "Paciente guardado correctamente.");
            limpiar();
            onGlobalChange.run();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Elimina o da de baja al paciente seleccionado.
     */
    private void eliminar() {
        if (idSeleccionado == null) {
            UiTheme.mostrarInfo(this, "Seleccione un paciente de la lista.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar al paciente seleccionado? Si tiene sesiones se le dará de baja.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                service.eliminar(idSeleccionado);
                UiTheme.mostrarInfo(this, "Paciente eliminado o dado de baja correctamente.");
                limpiar();
                onGlobalChange.run();
            } catch (Exception error) {
                UiTheme.mostrarError(this, error);
            }
        }
    }

    /**
     * Abre el editor de historia clinica del paciente seleccionado.
     */
    private void verHistoria() {
        if (idSeleccionado == null) {
            UiTheme.mostrarInfo(this, "Seleccione un paciente de la lista.");
            return;
        }
        try {
            Paciente paciente = service.buscar(idSeleccionado);
            HistorialClinico existente = service.historiaClinica(idSeleccionado).orElse(new HistorialClinico());
            if (existente.getId() == null) {
                existente.setPacienteId(idSeleccionado);
                existente.setFechaRegistro(java.time.LocalDate.now());
            }
            HistoriaDialog dialogo = new HistoriaDialog(paciente, existente);
            dialogo.setVisible(true);
            if (dialogo.isAceptado()) {
                service.guardarHistoria(dialogo.getHistorial());
                UiTheme.mostrarInfo(this, "Historia clínica guardada.");
                onGlobalChange.run();
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
        nombre.setText("");
        apellido.setText("");
        dni.setText("");
        edad.setValue(30);
        telefono.setText("");
        domicilio.setText("");
        diagnostico.setText("");
        tabla.clearSelection();
    }

    @Override
    public void actualizar() {
        cargarTabla();
    }

    /**
     * Dialogo de edicion de historia clinica.
     */
    private static class HistoriaDialog extends JDialog {

        /** Serial version UID. */
        private static final long serialVersionUID = 1L;
        /** Historial editable. */
        private final HistorialClinico historial;
        /** Estado de aceptacion. */
        private boolean aceptado;
        /** Campo sesiones. */
        private final JSpinner sesiones = new JSpinner(new SpinnerNumberModel(12, 0, 500, 1));
        /** Campo diagnostico interno. */
        private final JTextField interno = UiTheme.campo(30);
        /** Campo diagnostico externo. */
        private final JTextField externo = UiTheme.campo(30);
        /** Campo adjunto. */
        private final JTextField adjunto = UiTheme.campo(30);

        /**
         * Crea el dialogo.
         *
         * @param paciente  paciente
         * @param historial historia a editar
         */
        HistoriaDialog(Paciente paciente, HistorialClinico historial) {
            super((Frame) null, "Historia clínica - " + paciente.getNombreCompleto(), true);
            this.historial = historial;
            sesiones.setValue(historial.getSesionesProgramadas());
            interno.setText(Formatos.seguro(historial.getDiagnosticoInterno()));
            externo.setText(Formatos.seguro(historial.getDiagnosticoExterno()));
            adjunto.setText(Formatos.seguro(historial.getAdjunto()));
            construir();
            pack();
            setResizable(false);
            setLocationRelativeTo(null);
        }

        /**
         * Construye el dialogo.
         */
        private void construir() {
            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            panel.add(UiTheme.etiqueta("Sesiones programadas"));
            panel.add(sesiones);
            panel.add(UiTheme.etiqueta("Diagnóstico externo"));
            panel.add(externo);
            panel.add(UiTheme.etiqueta("Diagnóstico interno"));
            panel.add(interno);
            panel.add(UiTheme.etiqueta("Adjunto (Drive)"));
            panel.add(adjunto);

            JButton guardar = UiTheme.boton("Guardar", UiTheme.PRIMARIO);
            JButton cancelar = UiTheme.boton("Cancelar", UiTheme.TEXTO_SUAVE);
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            botones.add(guardar);
            botones.add(cancelar);

            JPanel principal = new JPanel(new BorderLayout(8, 12));
            principal.add(panel, BorderLayout.CENTER);
            principal.add(botones, BorderLayout.SOUTH);
            setContentPane(principal);

            guardar.addActionListener(e -> {
                historial.setSesionesProgramadas((Integer) sesiones.getValue());
                historial.setDiagnosticoExterno(externo.getText());
                historial.setDiagnosticoInterno(interno.getText());
                historial.setAdjunto(adjunto.getText());
                aceptado = true;
                dispose();
            });
            cancelar.addActionListener(e -> dispose());
        }

        /** @return si el usuario acepto */
        boolean isAceptado() {
            return aceptado;
        }

        /** @return historial editable */
        HistorialClinico getHistorial() {
            return historial;
        }
    }
}
