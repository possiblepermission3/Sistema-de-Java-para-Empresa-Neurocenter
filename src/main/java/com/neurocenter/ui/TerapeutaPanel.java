package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.neurocenter.model.Especialidad;
import com.neurocenter.model.Horario;
import com.neurocenter.model.Terapeuta;
import com.neurocenter.model.Turno;
import com.neurocenter.service.TerapeutaService;
import com.neurocenter.ui.components.UiTheme;

/**
 * Modulo de gestion de terapeutas y consulta de horarios.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class TerapeutaPanel extends JPanel implements Actualizable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Servicio de terapeutas. */
    private final TerapeutaService service = new TerapeutaService();
    /** Callback global. */
    private final Runnable onGlobalChange;
    /** Modelo de la tabla. */
    private final DefaultTableModel modelo = UiTheme.modelo("ID", "Nombre", "Apellido", "DNI", "Celular",
            "Edad", "Especialidad", "Turno", "Tarifa/h", "Estado");
    /** Tabla. */
    private final javax.swing.JTable tabla = UiTheme.tabla(modelo);
    /** Campo nombre. */
    private final JTextField nombre = UiTheme.campo(12);
    /** Campo apellido. */
    private final JTextField apellido = UiTheme.campo(12);
    /** Campo DNI. */
    private final JTextField dni = UiTheme.campo(10);
    /** Campo telefono. */
    private final JTextField telefono = UiTheme.campo(10);
    /** Campo edad. */
    private final JSpinner edad = new JSpinner(new SpinnerNumberModel(30, 18, 100, 1));
    /** Combo de especialidad. */
    private final JComboBox<Especialidad> especialidad = new JComboBox<>(Especialidad.values());
    /** Combo de turno. */
    private final JComboBox<Turno> turno = new JComboBox<>(Turno.values());
    /** Campo tarifa. */
    private final JSpinner tarifa = new JSpinner(new SpinnerNumberModel(120.0, 1.0, 1000.0, 5.0));
    /** Campo foto. */
    private final JTextField foto = UiTheme.campo(16);
    /** Campo busqueda. */
    private final JTextField busqueda = UiTheme.campo(16);
    /** Id seleccionado. */
    private String idSeleccionado;

    /**
     * Crea el panel.
     *
     * @param onGlobalChange callback global
     */
    public TerapeutaPanel(Runnable onGlobalChange) {
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

        JPanel formulario = new JPanel(new GridLayout(5, 4, 10, 10));
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
        formulario.add(UiTheme.etiqueta("Celular"));
        formulario.add(telefono);
        formulario.add(UiTheme.etiqueta("Edad"));
        formulario.add(edad);
        formulario.add(UiTheme.etiqueta("Especialidad"));
        formulario.add(especialidad);
        formulario.add(UiTheme.etiqueta("Turno"));
        formulario.add(turno);
        formulario.add(UiTheme.etiqueta("Tarifa por hora"));
        formulario.add(tarifa);
        formulario.add(UiTheme.etiqueta("Foto (ruta/enlace)"));
        formulario.add(foto);

        JButton nuevo = UiTheme.boton("Nuevo", UiTheme.TEXTO_SUAVE);
        JButton guardar = UiTheme.boton("Guardar", UiTheme.PRIMARIO);
        JButton eliminar = UiTheme.boton("Dar de baja", new java.awt.Color(170, 60, 60));
        JButton horarios = UiTheme.boton("Ver horarios", UiTheme.ACENTO);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        acciones.setBackground(Color.WHITE);
        acciones.add(nuevo);
        acciones.add(guardar);
        acciones.add(eliminar);
        acciones.add(horarios);
        formulario.add(acciones);

        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        buscador.setBackground(Color.WHITE);
        buscador.add(new JLabel("Buscar:"));
        buscador.add(busqueda);
        formulario.add(buscador);

        add(formulario, BorderLayout.NORTH);
        JPanel tablaPanel = UiTheme.grupo("Listado de terapeutas");
        tablaPanel.add(UiTheme.scroll(tabla));
        add(tablaPanel, BorderLayout.CENTER);

        nuevo.addActionListener(e -> limpiar());
        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> darDeBaja());
        horarios.addActionListener(e -> verHorarios());
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
     * Carga la tabla.
     */
    private void cargarTabla() {
        try {
            modelo.setRowCount(0);
            for (Terapeuta terapeuta : service.buscarPorTexto(busqueda.getText())) {
                modelo.addRow(new Object[]{
                        terapeuta.getId(), terapeuta.getNombre(), terapeuta.getApellido(), terapeuta.getDni(),
                        terapeuta.getTelefono(), terapeuta.getEdad(),
                        terapeuta.getEspecialidad().getNombreMostrar(),
                        terapeuta.getTurno().getNombreMostrar(),
                        com.neurocenter.util.Formatos.moneda(terapeuta.getTarifaHora()),
                        terapeuta.isActivo() ? "Activo" : "Baja"
                });
            }
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Selecciona un terapeuta.
     */
    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        try {
            String id = String.valueOf(modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0));
            Terapeuta terapeuta = service.buscar(id);
            idSeleccionado = terapeuta.getId();
            nombre.setText(terapeuta.getNombre());
            apellido.setText(terapeuta.getApellido());
            dni.setText(terapeuta.getDni());
            telefono.setText(terapeuta.getTelefono());
            edad.setValue(terapeuta.getEdad());
            especialidad.setSelectedItem(terapeuta.getEspecialidad());
            turno.setSelectedItem(terapeuta.getTurno());
            tarifa.setValue(terapeuta.getTarifaHora());
            foto.setText(com.neurocenter.util.Formatos.seguro(terapeuta.getFoto()));
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Guarda el terapeuta del formulario.
     */
    private void guardar() {
        try {
            Terapeuta terapeuta = new Terapeuta();
            terapeuta.setId(idSeleccionado);
            terapeuta.setNombre(nombre.getText());
            terapeuta.setApellido(apellido.getText());
            terapeuta.setDni(dni.getText());
            terapeuta.setTelefono(telefono.getText());
            terapeuta.setEdad((Integer) edad.getValue());
            terapeuta.setEspecialidad((Especialidad) especialidad.getSelectedItem());
            terapeuta.setTurno((Turno) turno.getSelectedItem());
            terapeuta.setTarifaHora(((Number) tarifa.getValue()).doubleValue());
            terapeuta.setFoto(foto.getText());
            service.guardar(terapeuta);
            UiTheme.mostrarInfo(this, "Terapeuta guardado correctamente.");
            limpiar();
            onGlobalChange.run();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Da de baja logica al terapeuta seleccionado.
     */
    private void darDeBaja() {
        if (idSeleccionado == null) {
            UiTheme.mostrarInfo(this, "Seleccione un terapeuta de la lista.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea dar de baja al terapeuta seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                Terapeuta terapeuta = service.buscar(idSeleccionado);
                terapeuta.setActivo(false);
                service.guardar(terapeuta);
                UiTheme.mostrarInfo(this, "Terapeuta dado de baja.");
                limpiar();
                onGlobalChange.run();
            } catch (Exception error) {
                UiTheme.mostrarError(this, error);
            }
        }
    }

    /**
     * Muestra los horarios configurados del terapeuta seleccionado.
     */
    private void verHorarios() {
        if (idSeleccionado == null) {
            UiTheme.mostrarInfo(this, "Seleccione un terapeuta de la lista.");
            return;
        }
        try {
            StringBuilder texto = new StringBuilder("<html><body><h3>Horarios de atención</h3><ul>");
            for (Horario horario : service.horarios(idSeleccionado)) {
                texto.append("<li>").append(horario.getDiaSemana()).append(": ")
                        .append(horario.getHoraInicio()).append(" - ").append(horario.getHoraFin())
                        .append("</li>");
            }
            texto.append("</ul></body></html>");
            UiTheme.mostrarInfo(this, texto.toString());
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
        telefono.setText("");
        edad.setValue(30);
        tarifa.setValue(120.0);
        foto.setText("");
        tabla.clearSelection();
    }

    @Override
    public void actualizar() {
        cargarTabla();
    }
}
