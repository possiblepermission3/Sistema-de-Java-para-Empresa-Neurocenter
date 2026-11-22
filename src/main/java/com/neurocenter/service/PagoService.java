package com.neurocenter.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.neurocenter.config.AppConfig;
import com.neurocenter.exception.DatosInvalidosException;
import com.neurocenter.exception.EntidadNoEncontradaException;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.MetodoPago;
import com.neurocenter.model.Pago;
import com.neurocenter.model.Paciente;
import com.neurocenter.model.TipoPago;
import com.neurocenter.repository.PacienteRepository;
import com.neurocenter.repository.PagoRepository;
import com.neurocenter.util.Validadores;

/**
 * Logica de negocio de pagos: calculo de tarifas, descuentos por paquete y
 * validacion de comprobantes.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PagoService {

    /** Repositorio de pagos. */
    private final PagoRepository pagoRepository;
    /** Repositorio de pacientes. */
    private final PacienteRepository pacienteRepository;

    /** Crea el servicio. */
    public PagoService() {
        this(new PagoRepository(), new PacienteRepository());
    }

    /**
     * Crea el servicio con dependencias inyectadas.
     *
     * @param pagoRepository     repositorio de pagos
     * @param pacienteRepository repositorio de pacientes
     */
    public PagoService(PagoRepository pagoRepository, PacienteRepository pacienteRepository) {
        this.pagoRepository = pagoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * @return todos los pagos
     * @throws PersistenciaException si falla la consulta
     */
    public List<Pago> listar() throws PersistenciaException {
        return pagoRepository.listarTodos();
    }

    /**
     * Valida y registra un pago.
     *
     * @param pago pago a registrar
     * @return pago persistido
     * @throws DatosInvalidosException si falla la validacion
     * @throws PersistenciaException si falla la persistencia
     */
    public Pago registrar(Pago pago)
            throws DatosInvalidosException, EntidadNoEncontradaException, PersistenciaException {
        Validadores.requerido(pago.getPacienteId(), "paciente");
        Validadores.requerido(pago.getTipoPago(), "tipo de pago");
        Validadores.requerido(pago.getMetodoPago(), "método de pago");
        Validadores.monto(pago.getMonto(), "monto");
        Validadores.enteroEnRango(pago.getSesionesPaquete(), 1, 500, "sesiones del paquete");
        Validadores.fechaNoFutura(pago.getFechaPago(), "fecha de pago");
        Paciente paciente = pacienteRepository.buscarPorId(pago.getPacienteId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Paciente", pago.getPacienteId()));
        if (!paciente.isActivo()) {
            throw new DatosInvalidosException("El paciente seleccionado esta dado de baja.");
        }
        if (pago.getTipoPago() == TipoPago.PAQUETE
                && pago.getSesionesPaquete() < AppConfig.SESIONES_MINIMAS_DESCUENTO) {
            throw new DatosInvalidosException("Un paquete debe incluir al menos "
                    + AppConfig.SESIONES_MINIMAS_DESCUENTO + " sesiones para aplicar descuento.");
        }
        return pagoRepository.guardar(pago);
    }

    /**
     * Calcula el monto de un paquete aplicando descuento cuando corresponde.
     *
     * @param especialidad     especialidad contratada
     * @param tarifaHora       tarifa del especialista
     * @param cantidadSesiones sesiones del paquete
     * @return monto total con descuento aplicado
     */
    public double calcularMontoPaquete(Especialidad especialidad, double tarifaHora, int cantidadSesiones)
            throws DatosInvalidosException {
        Validadores.enteroEnRango(cantidadSesiones, 1, 500, "sesiones del paquete");
        double base = (tarifaHora > 0 ? tarifaHora : AppConfig.tarifaBase(especialidad)) * cantidadSesiones;
        double descuento = cantidadSesiones >= AppConfig.SESIONES_MINIMAS_DESCUENTO
                ? AppConfig.DESCUENTO_PAQUETE : 0.0;
        return Math.round(base * (1 - descuento) * 100.0) / 100.0;
    }

    /**
     * @return total recaudado
     * @throws PersistenciaException si falla la consulta
     */
    public double totalRecaudado() throws PersistenciaException {
        return listar().stream().mapToDouble(Pago::getMonto).sum();
    }

    /**
     * Filtra pagos por paciente usando Stream API.
     *
     * @param pacienteId id del paciente
     * @return pagos del paciente
     * @throws PersistenciaException si falla la consulta
     */
    public List<Pago> porPaciente(String pacienteId) throws PersistenciaException {
        return pagoRepository.porPaciente(pacienteId);
    }

    /**
     * Suma los pagos de un paciente.
     *
     * @param pacienteId id del paciente
     * @return total pagado
     * @throws PersistenciaException si falla la consulta
     */
    public double totalPorPaciente(String pacienteId) throws PersistenciaException {
        return porPaciente(pacienteId).stream().mapToDouble(Pago::getMonto).sum();
    }

    /**
     * Agrupa los pagos por metodo usando Collectors.
     *
     * @return mapa metodo -&gt; monto total
     * @throws PersistenciaException si falla la consulta
     */
    public java.util.Map<MetodoPago, Double> porMetodo() throws PersistenciaException {
        return listar().stream()
                .collect(Collectors.groupingBy(Pago::getMetodoPago, Collectors.summingDouble(Pago::getMonto)));
    }

    /**
     * Lista los pagos realizados en una fecha concreta.
     *
     * @param fecha fecha
     * @return pagos del dia
     * @throws PersistenciaException si falla la consulta
     */
    public List<Pago> porFecha(LocalDate fecha) throws PersistenciaException {
        return listar().stream().filter(p -> fecha.equals(p.getFechaPago())).collect(Collectors.toList());
    }
}
