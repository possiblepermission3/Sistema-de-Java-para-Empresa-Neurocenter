package com.neurocenter.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.neurocenter.config.AppConfig;
import com.neurocenter.exception.CruceHorarioException;
import com.neurocenter.exception.DatosInvalidosException;
import com.neurocenter.exception.EntidadNoEncontradaException;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.EstadoAsistencia;
import com.neurocenter.model.EstadoSesion;
import com.neurocenter.model.Horario;
import com.neurocenter.model.Paciente;
import com.neurocenter.model.PlanRehabilitacion;
import com.neurocenter.model.ServicioTerapia;
import com.neurocenter.model.Terapeuta;
import com.neurocenter.repository.HorarioRepository;
import com.neurocenter.repository.PacienteRepository;
import com.neurocenter.repository.PlanRepository;
import com.neurocenter.repository.SesionRepository;
import com.neurocenter.repository.TerapeutaRepository;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.Validadores;

/**
 * Servicio principal de agenda: programacion, reprogramacion, finalizacion,
 * prevencion de cruce de horarios y calculo polimorfico de tarifas.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class SesionService {

    /** Repositorio de sesiones. */
    private final SesionRepository sesionRepository;
    /** Repositorio de terapeutas. */
    private final TerapeutaRepository terapeutaRepository;
    /** Repositorio de pacientes. */
    private final PacienteRepository pacienteRepository;
    /** Repositorio de horarios. */
    private final HorarioRepository horarioRepository;
    /** Repositorio de planes. */
    private final PlanRepository planRepository;

    /** Crea el servicio con sus dependencias por defecto. */
    public SesionService() {
        this(new SesionRepository(), new TerapeutaRepository(), new PacienteRepository(),
                new HorarioRepository(), new PlanRepository());
    }

    /**
     * Crea el servicio con dependencias inyectadas.
     *
     * @param sesionRepository    repositorio de sesiones
     * @param terapeutaRepository repositorio de terapeutas
     * @param pacienteRepository  repositorio de pacientes
     * @param horarioRepository   repositorio de horarios
     * @param planRepository      repositorio de planes
     */
    public SesionService(SesionRepository sesionRepository, TerapeutaRepository terapeutaRepository,
                         PacienteRepository pacienteRepository, HorarioRepository horarioRepository,
                         PlanRepository planRepository) {
        this.sesionRepository = sesionRepository;
        this.terapeutaRepository = terapeutaRepository;
        this.pacienteRepository = pacienteRepository;
        this.horarioRepository = horarioRepository;
        this.planRepository = planRepository;
    }

    /**
     * @return todas las sesiones ordenadas por fecha
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> listar() throws PersistenciaException {
        return sesionRepository.listarTodos();
    }

    /**
     * @param limite cantidad maxima
     * @return sesiones mas recientes
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> recientes(int limite) throws PersistenciaException {
        return sesionRepository.recientes(limite);
    }

    /**
     * @return proximas sesiones programadas
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> proximas() throws PersistenciaException {
        return sesionRepository.proximas(LocalDateTime.now());
    }

    /**
     * Valida y programa una nueva sesion.
     *
     * @param sesion sesion con paciente, terapeuta, fecha y especialidad
     * @return sesion persistida con su costo calculado
     * @throws DatosInvalidosException si hay errores de entrada o de horario
     * @throws CruceHorarioException   si existe superposicion de agenda
     * @throws PersistenciaException   si falla la base de datos
     */
    public ServicioTerapia programar(ServicioTerapia sesion)
            throws DatosInvalidosException, CruceHorarioException, PersistenciaException {
        validarDatosBasicos(sesion);
        Terapeuta terapeuta = terapeutaRepository.buscarPorId(sesion.getTerapeutaId())
                .orElseThrow(() -> new DatosInvalidosException("El terapeuta seleccionado no existe."));
        Paciente paciente = pacienteRepository.buscarPorId(sesion.getPacienteId())
                .orElseThrow(() -> new DatosInvalidosException("El paciente seleccionado no existe."));
        if (!paciente.isActivo()) {
            throw new DatosInvalidosException("El paciente seleccionado esta dado de baja.");
        }
        if (terapeuta.getEspecialidad() != sesion.getEspecialidad()) {
            throw new DatosInvalidosException("La especialidad del terapeuta no coincide con el tipo de terapia.");
        }
        validarHorarioLaboral(sesion, terapeuta);
        verificarCruces(sesion, null, terapeuta, paciente);

        double costo = sesion.calcularCosto(terapeuta.getTarifaHora());
        if (sesion.getPlanId() != null && !sesion.getPlanId().isBlank()) {
            Optional<PlanRehabilitacion> plan = planRepository.buscarPorId(sesion.getPlanId());
            if (plan.isPresent() && plan.get().getSesionesTotales() >= AppConfig.SESIONES_MINIMAS_DESCUENTO) {
                costo *= (1 - AppConfig.DESCUENTO_PAQUETE);
            }
        }
        sesion.setCosto(Math.round(costo * 100.0) / 100.0);
        sesion.setEstado(EstadoSesion.PROGRAMADA);
        return sesionRepository.guardar(sesion);
    }

    /**
     * Valida campos obligatorios de la sesion.
     *
     * @param sesion sesion a validar
     * @throws DatosInvalidosException si hay errores
     */
    private void validarDatosBasicos(ServicioTerapia sesion) throws DatosInvalidosException {
        Validadores.requerido(sesion.getPacienteId(), "paciente");
        Validadores.requerido(sesion.getTerapeutaId(), "terapeuta");
        Validadores.requerido(sesion.getEspecialidad(), "especialidad");
        Validadores.fechaFutura(sesion.getFechaHora());
        Validadores.enteroEnRango(sesion.getDuracionMinutos(), 15, 240, "duración");
    }

    /**
     * Comprueba que la sesion caiga dentro de un bloque horario configurado
     * para el terapeuta y el dia de la semana correspondiente.
     *
     * @param sesion    sesion
     * @param terapeuta terapeuta asignado
     * @throws DatosInvalidosException si no hay horario disponible
     * @throws PersistenciaException   si falla la consulta
     */
    private void validarHorarioLaboral(ServicioTerapia sesion, Terapeuta terapeuta)
            throws DatosInvalidosException, PersistenciaException {
        String dia = Fechas.diaSemana(sesion.getFechaHora());
        LocalTime inicio = sesion.getFechaHora().toLocalTime();
        LocalTime fin = sesion.getFin().toLocalTime();
        boolean dentroDeHorario = horarioRepository.porTerapeuta(terapeuta.getId()).stream()
                .filter(h -> h.getDiaSemana().equals(dia))
                .anyMatch(h -> h.contiene(inicio) && !fin.isAfter(h.getHoraFin()));
        if (!dentroDeHorario) {
            throw new DatosInvalidosException(String.format(
                    "El terapeuta %s no tiene horario configurado para %s de %s a %s.",
                    terapeuta.getNombreCompleto(), dia, inicio.format(Fechas.HORA), fin.format(Fechas.HORA)));
        }
    }

    /**
     * Detecta superposiciones con la agenda del terapeuta y del paciente.
     *
     * @param sesion     sesion a programar
     * @param idExcluido id a ignorar (util en reprogramaciones)
     * @param terapeuta  terapeuta asignado
     * @param paciente   paciente asignado
     * @throws CruceHorarioException si hay cruce
     * @throws PersistenciaException si falla la consulta
     */
    private void verificarCruces(ServicioTerapia sesion, String idExcluido,
                                 Terapeuta terapeuta, Paciente paciente)
            throws CruceHorarioException, PersistenciaException {
        LocalDateTime inicioDia = sesion.getFechaHora().toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        Optional<ServicioTerapia> cruceTerapeuta = sesionRepository
                .porTerapeutaYRango(terapeuta.getId(), inicioDia, finDia).stream()
                .filter(s -> !s.getId().equals(idExcluido))
                .filter(s -> s.seSuperponeCon(sesion))
                .findFirst();
        if (cruceTerapeuta.isPresent()) {
            ServicioTerapia ocupada = cruceTerapeuta.get();
            throw new CruceHorarioException("terapeuta " + terapeuta.getNombreCompleto(),
                    "", ocupada.getFechaHora(), ocupada.getFin());
        }

        Optional<ServicioTerapia> crucePaciente = sesionRepository
                .porPacienteYRango(paciente.getId(), inicioDia, finDia).stream()
                .filter(s -> !s.getId().equals(idExcluido))
                .filter(s -> s.seSuperponeCon(sesion))
                .findFirst();
        if (crucePaciente.isPresent()) {
            ServicioTerapia ocupada = crucePaciente.get();
            throw new CruceHorarioException("paciente " + paciente.getNombreCompleto(),
                    "", ocupada.getFechaHora(), ocupada.getFin());
        }
    }

    /**
     * Da por finalizada una sesion y registra asistencia en una transaccion.
     *
     * @param id           id de la sesion
     * @param asistencia   estado de asistencia
     * @param notaEvolucion nota clinica
     * @throws DatosInvalidosException si la sesion no esta programada
     * @throws EntidadNoEncontradaException si no existe
     * @throws PersistenciaException si falla la transaccion
     */
    public void completar(String id, EstadoAsistencia asistencia, String notaEvolucion)
            throws DatosInvalidosException, EntidadNoEncontradaException, PersistenciaException {
        ServicioTerapia sesion = buscar(id);
        if (sesion.getEstado() != EstadoSesion.PROGRAMADA) {
            throw new DatosInvalidosException("Solo se pueden completar sesiones programadas.");
        }
        Validadores.requerido(asistencia, "estado de asistencia");
        sesionRepository.completarConAsistencia(id, notaEvolucion, asistencia);
    }

    /**
     * Cancela una sesion programada.
     *
     * @param id id de la sesion
     * @throws DatosInvalidosException si no se puede cancelar
     * @throws EntidadNoEncontradaException si no existe
     * @throws PersistenciaException si falla la operacion
     */
    public void cancelar(String id) throws DatosInvalidosException, EntidadNoEncontradaException,
            PersistenciaException {
        ServicioTerapia sesion = buscar(id);
        if (sesion.getEstado() != EstadoSesion.PROGRAMADA) {
            throw new DatosInvalidosException("Solo se pueden cancelar sesiones programadas.");
        }
        sesionRepository.cambiarEstado(id, EstadoSesion.CANCELADA);
    }

    /**
     * Traslada una sesion a otra fecha/hora validando disponibilidad.
     *
     * @param id           id de la sesion
     * @param nuevaFechaHora nueva fecha y hora
     * @return sesion actualizada
     * @throws DatosInvalidosException si la fecha no es valida
     * @throws CruceHorarioException si hay superposicion
     * @throws EntidadNoEncontradaException si no existe
     * @throws PersistenciaException si falla la operacion
     */
    public ServicioTerapia reprogramar(String id, LocalDateTime nuevaFechaHora)
            throws DatosInvalidosException, CruceHorarioException, EntidadNoEncontradaException,
            PersistenciaException {
        ServicioTerapia sesion = buscar(id);
        if (sesion.getEstado() != EstadoSesion.PROGRAMADA) {
            throw new DatosInvalidosException("Solo se pueden reprogramar sesiones programadas.");
        }
        sesion.setFechaHora(nuevaFechaHora);
        validarDatosBasicos(sesion);
        Terapeuta terapeuta = terapeutaRepository.buscarPorId(sesion.getTerapeutaId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Terapeuta", sesion.getTerapeutaId()));
        Paciente paciente = pacienteRepository.buscarPorId(sesion.getPacienteId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Paciente", sesion.getPacienteId()));
        validarHorarioLaboral(sesion, terapeuta);
        verificarCruces(sesion, id, terapeuta, paciente);
        sesionRepository.actualizar(sesion);
        return sesion;
    }

    /**
     * Busca una sesion por id.
     *
     * @param id id
     * @return sesion
     * @throws EntidadNoEncontradaException si no existe
     * @throws PersistenciaException si falla la consulta
     */
    public ServicioTerapia buscar(String id) throws EntidadNoEncontradaException, PersistenciaException {
        return sesionRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Sesion", id));
    }

    /**
     * Filtra y ordena sesiones usando Stream API.
     *
     * @param sesiones      lista origen
     * @param especialidad  filtro de especialidad (puede ser null)
     * @param estado        filtro de estado (puede ser null)
     * @param texto         texto libre (paciente, detalle o id)
     * @return lista filtrada
     */
    public List<ServicioTerapia> filtrar(List<ServicioTerapia> sesiones, Especialidad especialidad,
                                         EstadoSesion estado, String texto) {
        String criterio = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        return sesiones.stream()
                .filter(s -> especialidad == null || s.getEspecialidad() == especialidad)
                .filter(s -> estado == null || s.getEstado() == estado)
                .filter(s -> criterio.isEmpty()
                        || s.getId().toLowerCase(Locale.ROOT).contains(criterio)
                        || s.getDetalleEspecifico().toLowerCase(Locale.ROOT).contains(criterio))
                .sorted(Comparator.comparing(ServicioTerapia::getFechaHora))
                .collect(Collectors.toList());
    }

    /**
     * Suma el costo de las sesiones completadas.
     *
     * @return total facturado por sesiones
     * @throws PersistenciaException si falla la consulta
     */
    public double totalFacturadoSesiones() throws PersistenciaException {
        return listar().stream()
                .filter(s -> s.getEstado() == EstadoSesion.COMPLETADA)
                .mapToDouble(ServicioTerapia::getCosto)
                .sum();
    }

    /**
     * Devuelve las sesiones asociadas a un plan.
     *
     * @param planId id del plan
     * @return sesiones del plan
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> porPlan(String planId) throws PersistenciaException {
        return sesionRepository.porPlan(planId);
    }

    /**
     * Devuelve los horarios configurados de un terapeuta.
     *
     * @param terapeutaId id del terapeuta
     * @return horarios
     * @throws PersistenciaException si falla la consulta
     */
    public List<Horario> horarios(String terapeutaId) throws PersistenciaException {
        return horarioRepository.porTerapeuta(terapeutaId);
    }
}
