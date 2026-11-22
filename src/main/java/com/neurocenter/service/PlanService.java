package com.neurocenter.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.neurocenter.exception.DatosInvalidosException;
import com.neurocenter.exception.EntidadNoEncontradaException;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.EvaluacionProgreso;
import com.neurocenter.model.EstadoSesion;
import com.neurocenter.model.Paciente;
import com.neurocenter.model.PlanRehabilitacion;
import com.neurocenter.model.ServicioTerapia;
import com.neurocenter.model.Terapeuta;
import com.neurocenter.repository.EvaluacionRepository;
import com.neurocenter.repository.PacienteRepository;
import com.neurocenter.repository.PlanRepository;
import com.neurocenter.repository.SesionRepository;
import com.neurocenter.repository.TerapeutaRepository;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.Validadores;

/**
 * Gestion de planes de rehabilitacion, calculo de avance, adherencia
 * terapeutica y registro de evaluaciones de progreso.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PlanService {

    /** Repositorio de planes. */
    private final PlanRepository planRepository;
    /** Repositorio de sesiones. */
    private final SesionRepository sesionRepository;
    /** Repositorio de evaluaciones. */
    private final EvaluacionRepository evaluacionRepository;
    /** Repositorio de pacientes. */
    private final PacienteRepository pacienteRepository;
    /** Repositorio de terapeutas. */
    private final TerapeutaRepository terapeutaRepository;

    /** Crea el servicio con dependencias por defecto. */
    public PlanService() {
        this(new PlanRepository(), new SesionRepository(), new EvaluacionRepository(),
                new PacienteRepository(), new TerapeutaRepository());
    }

    /**
     * Crea el servicio con dependencias inyectadas.
     *
     * @param planRepository       repositorio de planes
     * @param sesionRepository     repositorio de sesiones
     * @param evaluacionRepository repositorio de evaluaciones
     * @param pacienteRepository   repositorio de pacientes
     * @param terapeutaRepository  repositorio de terapeutas
     */
    public PlanService(PlanRepository planRepository, SesionRepository sesionRepository,
                       EvaluacionRepository evaluacionRepository, PacienteRepository pacienteRepository,
                       TerapeutaRepository terapeutaRepository) {
        this.planRepository = planRepository;
        this.sesionRepository = sesionRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.pacienteRepository = pacienteRepository;
        this.terapeutaRepository = terapeutaRepository;
    }

    /**
     * @return todos los planes
     * @throws PersistenciaException si falla la consulta
     */
    public List<PlanRehabilitacion> listar() throws PersistenciaException {
        return planRepository.listarTodos();
    }

    /**
     * @return planes activos
     * @throws PersistenciaException si falla la consulta
     */
    public List<PlanRehabilitacion> listarActivos() throws PersistenciaException {
        return planRepository.listarActivos();
    }

    /**
     * Busca un plan por id.
     *
     * @param id id del plan
     * @return plan
     * @throws EntidadNoEncontradaException si no existe
     * @throws PersistenciaException si falla la consulta
     */
    public PlanRehabilitacion buscar(String id) throws EntidadNoEncontradaException, PersistenciaException {
        return planRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Plan de rehabilitacion", id));
    }

    /**
     * Valida y guarda un plan.
     *
     * @param plan plan a guardar
     * @return plan persistido
     * @throws DatosInvalidosException si falla la validacion
     * @throws PersistenciaException si falla la persistencia
     */
    public PlanRehabilitacion guardar(PlanRehabilitacion plan)
            throws DatosInvalidosException, PersistenciaException {
        Validadores.requerido(plan.getPacienteId(), "paciente");
        Validadores.requerido(plan.getTerapeutaId(), "terapeuta");
        Validadores.requerido(plan.getEspecialidad(), "especialidad");
        Validadores.enteroEnRango(plan.getSesionesTotales(), 1, 500, "numero de sesiones");
        Validadores.texto(plan.getObjetivo(), "objetivo", 5);
        Validadores.fechaNoFutura(plan.getFechaInicio(), "fecha de inicio");
        Paciente paciente = pacienteRepository.buscarPorId(plan.getPacienteId())
                .orElseThrow(() -> new DatosInvalidosException("El paciente seleccionado no existe."));
        Terapeuta terapeuta = terapeutaRepository.buscarPorId(plan.getTerapeutaId())
                .orElseThrow(() -> new DatosInvalidosException("El terapeuta seleccionado no existe."));
        if (!paciente.isActivo()) {
            throw new DatosInvalidosException("El paciente seleccionado esta dado de baja.");
        }
        if (terapeuta.getEspecialidad() != plan.getEspecialidad()) {
            throw new DatosInvalidosException("La especialidad del plan no coincide con la del terapeuta.");
        }
        if (plan.getFechaFin() != null && plan.getFechaFin().isBefore(plan.getFechaInicio())) {
            throw new DatosInvalidosException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        return planRepository.guardar(plan);
    }

    /**
     * Calcula el porcentaje de avance: sesiones completadas / sesiones totales.
     *
     * @param planId id del plan
     * @return porcentaje 0-100
     * @throws PersistenciaException si falla la consulta
     */
    public double avance(String planId) throws PersistenciaException {
        PlanRehabilitacion plan = planRepository.buscarPorId(planId).orElse(null);
        if (plan == null || plan.getSesionesTotales() <= 0) {
            return 0.0;
        }
        long completadas = sesionRepository.contarCompletadas(planId);
        return Math.min(100.0, completadas * 100.0 / plan.getSesionesTotales());
    }

    /**
     * Calcula la adherencia terapéutica comparando las sesiones realizadas
     * (completadas o inasistidas) con las esperadas segun el tiempo transcurrido.
     *
     * @param planId id del plan
     * @return porcentaje 0-100
     * @throws PersistenciaException si falla la consulta
     */
    public double adherencia(String planId) throws PersistenciaException {
        PlanRehabilitacion plan = planRepository.buscarPorId(planId).orElse(null);
        if (plan == null || plan.getFechaInicio() == null) {
            return 0.0;
        }
        LocalDate hoy = LocalDate.now();
        LocalDate fin = plan.getFechaFin() == null ? hoy : plan.getFechaFin();
        long duracionTotal = Math.max(1, ChronoUnit.DAYS.between(plan.getFechaInicio(), fin) + 1);
        long transcurridos = Math.max(0, ChronoUnit.DAYS.between(plan.getFechaInicio(), hoy) + 1);
        double esperadas = plan.getSesionesTotales() * Math.min(1.0, transcurridos / (double) duracionTotal);

        long realizadas = sesionRepository.porPlan(planId).stream()
                .filter(s -> s.getEstado() == EstadoSesion.COMPLETADA
                        || s.getEstado() == EstadoSesion.INASISTIDA)
                .count();
        if (esperadas <= 0) {
            return 0.0;
        }
        return Math.min(100.0, realizadas * 100.0 / esperadas);
    }

    /**
     * @param planId id del plan
     * @return cantidad de sesiones completadas
     * @throws PersistenciaException si falla la consulta
     */
    public long completadas(String planId) throws PersistenciaException {
        return sesionRepository.contarCompletadas(planId);
    }

    /**
     * Registra una evaluacion de progreso.
     *
     * @param evaluacion evaluacion a registrar
     * @return evaluacion persistida
     * @throws DatosInvalidosException si falla la validacion
     * @throws PersistenciaException si falla la persistencia
     */
    public EvaluacionProgreso registrarEvaluacion(EvaluacionProgreso evaluacion)
            throws DatosInvalidosException, PersistenciaException {
        Validadores.requerido(evaluacion.getPlanId(), "plan");
        Validadores.requerido(evaluacion.getTerapeutaId(), "terapeuta");
        Validadores.enteroEnRango(evaluacion.getEscalaFuncional(), 0, 100, "escala funcional");
        Validadores.fechaNoFutura(evaluacion.getFecha(), "fecha de evaluación");
        if (!planRepository.buscarPorId(evaluacion.getPlanId()).isPresent()) {
            throw new DatosInvalidosException("El plan seleccionado no existe.");
        }
        return evaluacionRepository.guardar(evaluacion);
    }

    /**
     * Lista las evaluaciones de un plan.
     *
     * @param planId id del plan
     * @return evaluaciones
     * @throws PersistenciaException si falla la consulta
     */
    public List<EvaluacionProgreso> evaluaciones(String planId) throws PersistenciaException {
        return evaluacionRepository.porPlan(planId);
    }

    /**
     * @param planId id del plan
     * @return ultima evaluacion, si existe
     * @throws PersistenciaException si falla la consulta
     */
    public Optional<EvaluacionProgreso> ultimaEvaluacion(String planId) throws PersistenciaException {
        return evaluacionRepository.ultima(planId);
    }

    /**
     * Calcula la mejora entre la primera y la ultima evaluacion de un plan.
     *
     * @param planId id del plan
     * @return variacion de la escala funcional (puede ser negativa)
     * @throws PersistenciaException si falla la consulta
     */
    public int mejora(String planId) throws PersistenciaException {
        List<EvaluacionProgreso> evaluaciones = evaluaciones(planId);
        if (evaluaciones.size() < 2) {
            return 0;
        }
        return evaluaciones.get(evaluaciones.size() - 1).getEscalaFuncional()
                - evaluaciones.get(0).getEscalaFuncional();
    }

    /**
     * Devuelve el nombre del paciente de un plan (util para tablas).
     *
     * @param plan plan
     * @return nombre completo o id
     * @throws PersistenciaException si falla la consulta
     */
    public String nombrePaciente(PlanRehabilitacion plan) throws PersistenciaException {
        return pacienteRepository.buscarPorId(plan.getPacienteId())
                .map(Paciente::getNombreCompleto)
                .orElse(plan.getPacienteId());
    }

    /**
     * Devuelve el nombre del terapeuta de un plan.
     *
     * @param plan plan
     * @return nombre completo o id
     * @throws PersistenciaException si falla la consulta
     */
    public String nombreTerapeuta(PlanRehabilitacion plan) throws PersistenciaException {
        return terapeutaRepository.buscarPorId(plan.getTerapeutaId())
                .map(Terapeuta::getNombreCompleto)
                .orElse(plan.getTerapeutaId());
    }

    /**
     * Devuelve las sesiones de un plan (para calculos de reporting).
     *
     * @param planId id del plan
     * @return sesiones
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> sesiones(String planId) throws PersistenciaException {
        return sesionRepository.porPlan(planId);
    }

    /**
     * Promedio simple de la ultima evaluacion de cada plan segun especialidad.
     *
     * @return mapa especialidad -&gt; promedio
     * @throws PersistenciaException si falla la consulta
     */
    public java.util.Map<Especialidad, Double> progresoPorEspecialidad() throws PersistenciaException {
        java.util.Map<String, Especialidad> especialidades = planRepository.listarTodos().stream()
                .collect(Collectors.toMap(PlanRehabilitacion::getId, PlanRehabilitacion::getEspecialidad));
        return evaluacionRepository.listarTodos().stream()
                .filter(e -> especialidades.containsKey(e.getPlanId()))
                .collect(Collectors.groupingBy(
                        e -> especialidades.get(e.getPlanId()),
                        Collectors.averagingInt(EvaluacionProgreso::getEscalaFuncional)));
    }

    /**
     * Formatea una fecha para mostrar en la interfaz.
     *
     * @param fecha fecha
     * @return texto legible
     */
    public String formatearFecha(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(Fechas.FECHA_LEGIBLE);
    }
}
