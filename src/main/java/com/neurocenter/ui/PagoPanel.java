package com.neurocenter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.neurocenter.model.Especialidad;
import com.neurocenter.model.MetodoPago;
import com.neurocenter.model.Paciente;
import com.neurocenter.model.Pago;
import com.neurocenter.model.TipoPago;
import com.neurocenter.service.PacienteService;
import com.neurocenter.service.PagoService;
import com.neurocenter.service.TerapeutaService;
import com.neurocenter.ui.components.UiTheme;
import com.neurocenter.util.Formatos;

/**
 * Modulo de registro y consulta de pagos (individuales y paquetes).
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PagoPanel extends JPanel implements Actualizable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Servicio de pagos. */
    private final PagoService service = new PagoService();
    /** Servicio de pacientes. */
    private final PacienteService pacienteService = new PacienteService();
    /** Servicio de terapeutas. */
    private final TerapeutaService terapeutaService = new TerapeutaService();
    /** Callback global. */
    private final Runnable onGlobalChange;
    /** Combo de pacientes. */
    private final JComboBox<Paciente> comboPaciente = new JComboBox<>();
    /** Combo de tipo de pago. */
    private final JComboBox<TipoPago> comboTipo = new JComboBox<>(TipoPago.values());
    /** Combo de metodo. */
    private final JComboBox<MetodoPago> comboMetodo = new JComboBox<>(MetodoPago.values());
    /** Combo de especialidad. */
    private final JComboBox<Especialidad> comboEspecialidad = new JComboBox<>(Especialidad.values());
    /** Cantidad de sesiones. */
    private final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));
    /** Monto. */
    private final JSpinner monto = new JSpinner(new SpinnerNumberModel(1080.0, 1.0, 100000.0, 10.0));
    /** Fecha. */
    private final JSpinner fecha = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    /** Numero de operacion. */
    private final JTextField operacion = UiTheme.campo(18);
    /** Etiqueta de total. */
    private final JLabel total = new JLabel("Total recaudado: S/ 0.00");
    /** Modelo de pagos. */
    private final DefaultTableModel modelo = UiTheme.modelo("ID", "Fecha", "Paciente", "Tipo", "Método",
            "Sesiones", "Monto", "Operación");
    /** Tabla. */
    private final javax.swing.JTable tabla = UiTheme.tabla(modelo);

    /**
     * Crea el panel.
     *
     * @param onGlobalChange callback global
     */
    public PagoPanel(Runnable onGlobalChange) {
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

        JPanel formulario = new JPanel(new GridLayout(5, 6, 8, 8));
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        formulario.add(UiTheme.etiqueta("Paciente"));
        formulario.add(comboPaciente);
        formulario.add(UiTheme.etiqueta("Tipo de pago"));
        formulario.add(comboTipo);
        formulario.add(UiTheme.etiqueta("Método"));
        formulario.add(comboMetodo);
        formulario.add(UiTheme.etiqueta("Especialidad (tarifa)"));
        formulario.add(comboEspecialidad);
        formulario.add(UiTheme.etiqueta("Cantidad de sesiones"));
        formulario.add(cantidad);
        formulario.add(UiTheme.etiqueta("Monto"));
        formulario.add(monto);
        formulario.add(UiTheme.etiqueta("Fecha"));
        formulario.add(fecha);
        formulario.add(UiTheme.etiqueta("N° de operación"));
        formulario.add(operacion);

        JButton calcular = UiTheme.boton("Calcular", UiTheme.ACENTO);
        JButton registrar = UiTheme.boton("Registrar pago", UiTheme.PRIMARIO);
        JButton limpiar = UiTheme.boton("Limpiar", UiTheme.TEXTO_SUAVE);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        acciones.setBackground(Color.WHITE);
        acciones.add(calcular);
        acciones.add(registrar);
        acciones.add(limpiar);
        formulario.add(acciones);
        add(formulario, BorderLayout.NORTH);

        JPanel tablaPanel = UiTheme.grupo("Comprobantes de pago");
        tablaPanel.add(UiTheme.scroll(tabla));
        add(tablaPanel, BorderLayout.CENTER);

        total.setFont(UiTheme.FUENTE.deriveFont(java.awt.Font.BOLD, 15f));
        total.setForeground(UiTheme.PRIMARIO);
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.setBackground(UiTheme.FONDO);
        pie.add(total);
        add(pie, BorderLayout.SOUTH);

        calcular.addActionListener(e -> calcularMonto());
        registrar.addActionListener(e -> registrar());
        limpiar.addActionListener(e -> limpiar());
    }

    /**
     * Carga los combos.
     */
    private void cargarCombos() {
        try {
            comboPaciente.removeAllItems();
            pacienteService.listar().stream().filter(Paciente::isActivo).forEach(comboPaciente::addItem);
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Calcula el monto del paquete con descuento.
     */
    private void calcularMonto() {
        try {
            Especialidad especialidad = (Especialidad) comboEspecialidad.getSelectedItem();
            double tarifa = especialidad == null ? 120.0
                    : terapeutaService.porEspecialidad(especialidad).stream()
                    .findFirst().map(t -> t.getTarifaHora()).orElse(com.neurocenter.config.AppConfig.tarifaBase(especialidad));
            double calculado = service.calcularMontoPaquete(especialidad, tarifa, (Integer) cantidad.getValue());
            monto.setValue(calculado);
            UiTheme.mostrarInfo(this, "Monto calculado: " + Formatos.moneda(calculado));
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Registra el pago.
     */
    private void registrar() {
        if (comboPaciente.getSelectedItem() == null) {
            UiTheme.mostrarInfo(this, "Seleccione un paciente.");
            return;
        }
        try {
            Pago pago = new Pago();
            pago.setPacienteId(((Paciente) comboPaciente.getSelectedItem()).getId());
            pago.setTipoPago((TipoPago) comboTipo.getSelectedItem());
            pago.setMetodoPago((MetodoPago) comboMetodo.getSelectedItem());
            pago.setSesionesPaquete((Integer) cantidad.getValue());
            pago.setMonto(((Number) monto.getValue()).doubleValue());
            pago.setFechaPago(((Date) fecha.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            pago.setNumeroOperacion(operacion.getText());
            service.registrar(pago);
            UiTheme.mostrarInfo(this, "Pago registrado correctamente.");
            limpiar();
            onGlobalChange.run();
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }

    /**
     * Limpia el formulario.
     */
    private void limpiar() {
        operacion.setText("");
        monto.setValue(1080.0);
        cantidad.setValue(10);
    }

    @Override
    public void actualizar() {
        try {
            cargarCombos();
            modelo.setRowCount(0);
            for (Pago pago : service.listar()) {
                modelo.addRow(new Object[]{
                        pago.getId(),
                        pago.getFechaPago().format(com.neurocenter.util.Fechas.FECHA_LEGIBLE),
                        pacienteService.buscar(pago.getPacienteId()).getNombreCompleto(),
                        pago.getTipoPago().getNombreMostrar(),
                        pago.getMetodoPago().getNombreMostrar(),
                        pago.getSesionesPaquete(),
                        Formatos.moneda(pago.getMonto()),
                        Formatos.seguro(pago.getNumeroOperacion())
                });
            }
            total.setText("Total recaudado: " + Formatos.moneda(service.totalRecaudado()));
        } catch (Exception error) {
            UiTheme.mostrarError(this, error);
        }
    }
}
