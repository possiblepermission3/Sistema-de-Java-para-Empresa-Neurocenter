package com.neurocenter.service;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Asistencia;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.EstadoAsistencia;
import com.neurocenter.model.EstadoSesion;
import com.neurocenter.model.EvaluacionProgreso;
import com.neurocenter.model.Pago;
import com.neurocenter.model.PlanRehabilitacion;
import com.neurocenter.model.ServicioTerapia;
import com.neurocenter.model.Terapeuta;
import com.neurocenter.repository.AsistenciaRepository;
import com.neurocenter.repository.EvaluacionRepository;
import com.neurocenter.repository.PacienteRepository;
import com.neurocenter.repository.PagoRepository;
import com.neurocenter.repository.PlanRepository;
import com.neurocenter.repository.SesionRepository;
import com.neurocenter.repository.TerapeutaRepository;

/**
 * Servicio de reportes y metricas consolidadas. Toda la agregacion se realiza
 * con Stream API y Collectors (groupingBy, summingDouble, counting, averaging).
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class ReporteService {

    /** Repositorio de sesiones. */
    private final SesionRepository sesionRepository;
    /** Repositorio de pacientes. */
    private final PacienteRepository pacienteRepository;
    /** Repositorio de terapeutas. */
    private final TerapeutaRepository terapeutaRepository;
    /** Repositorio de pagos. */
    private final PagoRepository pagoRepository;
    /** Repositorio de asistencia. */
    private final AsistenciaRepository asistenciaRepository;
    /** Repositorio de planes. */
    private final PlanRepository planRepository;
    /** Repositorio de evaluaciones. */
    private final EvaluacionRepository evaluacionRepository;

    /** Crea el servicio con dependencias por defecto. */
    public ReporteService() {
        this(new SesionRepository(), new PacienteRepository(), new TerapeutaRepository(),
                new PagoRepository(), new AsistenciaRepository(), new PlanRepository(),
                new EvaluacionRepository());
    }

    /**
     * Crea el servicio con dependencias inyectadas.
     *
     * @param sesionRepository     repositorio de sesiones
     * @param pacienteRepository   repositorio de pacientes
     * @param terapeutaRepository  repositorio de terapeutas
     * @param pagoRepository       repositorio de pagos
     * @param asistenciaRepository repositorio de asistencia
     * @param planRepository       repositorio de planes
     * @param evaluacionRepository repositorio de evaluaciones
     */
    public ReporteService(SesionRepository sesionRepository, PacienteRepository pacienteRepository,
                          TerapeutaRepository terapeutaRepository, PagoRepository pagoRepository,
                          AsistenciaRepository asistenciaRepository, PlanRepository planRepository,
                          EvaluacionRepository evaluacionRepository) {
        this.sesionRepository = sesionRepository;
        this.pacienteRepository = pacienteRepository;
        this.terapeutaRepository = terapeutaRepository;
        this.pagoRepository = pagoRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.planRepository = planRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    /**
     * Construye el panel de indicadores principales.
     *
     * @return mapa ordenado con los indicadores
     * @throws PersistenciaException si falla alguna consulta
     */
    public Map<String, Object> indicadores() throws PersistenciaException {
        List<ServicioTerapia> sesiones = sesionRepository.listarTodos();
        List<Pago> pagos = pagoRepository.listarTodos();
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("Pacientes registrados", pacienteRepository.contar());
        datos.put("Terapeutas activos", terapeutaRepository.listarTodos().stream().filter(Terapeuta::isActivo).count());
        datos.put("Sesiones totales", sesiones.size());
        datos.put("Sesiones programadas", sesiones.stream()
                .filter(s -> s.getEstado() == EstadoSesion.PROGRAMADA).count());
        datos.put("Sesiones completadas", sesiones.stream()
                .filter(s -> s.getEstado() == EstadoSesion.COMPLETADA).count());
        datos.put("Ingresos registrados", pagos.stream().mapToDouble(Pago::getMonto).sum());
        datos.put("Tasa de asistencia", tasaAsistencia());
        datos.put("Planes activos", planRepository.listarActivos().size());
        return datos;
    }

    /**
     * @return cantidad de sesiones por especialidad
     * @throws PersistenciaException si falla la consulta
     */
    public Map<Especialidad, Long> sesionesPorEspecialidad() throws PersistenciaException {
        return sesionRepository.listarTodos().stream()
                .collect(Collectors.groupingBy(ServicioTerapia::getEspecialidad, Collectors.counting()));
    }

    /**
     * @return cantidad de sesiones por estado
     * @throws PersistenciaException si falla la consulta
     */
    public Map<EstadoSesion, Long> sesionesPorEstado() throws PersistenciaException {
        return sesionRepository.listarTodos().stream()
                .collect(Collectors.groupingBy(ServicioTerapia::getEstado, Collectors.counting()));
    }

    /**
     * @return costo de sesiones completadas por especialidad
     * @throws PersistenciaException si falla la consulta
     */
    public Map<Especialidad, Double> costoPorEspecialidad() throws PersistenciaException {
        return sesionRepository.listarTodos().stream()
                .filter(s -> s.getEstado() == EstadoSesion.COMPLETADA)
                .collect(Collectors.groupingBy(ServicioTerapia::getEspecialidad,
                        Collectors.summingDouble(ServicioTerapia::getCosto)));
    }

    /**
     * @return ingresos agrupados por mes (anio-mes)
     * @throws PersistenciaException si falla la consulta
     */
    public Map<String, Double> ingresosPorMes() throws PersistenciaException {
        return pagoRepository.listarTodos().stream()
                .collect(Collectors.groupingBy(
                        p -> YearMonth.from(p.getFechaPago()).toString(),
                        TreeMap::new,
                        Collectors.summingDouble(Pago::getMonto)));
    }

    /**
     * @return porcentaje de asistencia global
     * @throws PersistenciaException si falla la consulta
     */
    public double tasaAsistencia() throws PersistenciaException {
        List<Asistencia> registros = asistenciaRepository.listarTodos();
        if (registros.isEmpty()) {
            return 0.0;
        }
        long asistidas = registros.stream()
                .filter(a -> a.getEstado() == EstadoAsistencia.ASISTIO).count();
        return asistidas * 100.0 / registros.size();
    }

    /**
     * Calcula el promedio de la ultima evaluacion por especialidad.
     *
     * @return mapa especialidad -&gt; promedio
     * @throws PersistenciaException si falla la consulta
     */
    public Map<Especialidad, Double> progresoPorEspecialidad() throws PersistenciaException {
        Map<String, Especialidad> especialidades = planRepository.listarTodos().stream()
                .collect(Collectors.toMap(PlanRehabilitacion::getId, PlanRehabilitacion::getEspecialidad));
        return evaluacionRepository.listarTodos().stream()
                .filter(e -> especialidades.containsKey(e.getPlanId()))
                .collect(Collectors.groupingBy(
                        e -> especialidades.get(e.getPlanId()),
                        Collectors.averagingInt(EvaluacionProgreso::getEscalaFuncional)));
    }

    /**
     * Devuelve el ranking de terapeutas por cantidad de sesiones.
     *
     * @param limite cantidad maxima de resultados
     * @return lista ordenada de entradas nombre -&gt; sesiones
     * @throws PersistenciaException si falla la consulta
     */
    public List<Map.Entry<String, Long>> topTerapeutas(int limite) throws PersistenciaException {
        Map<String, String> nombres = terapeutaRepository.listarTodos().stream()
                .collect(Collectors.toMap(Terapeuta::getId, Terapeuta::getNombreCompleto));
        return sesionRepository.listarTodos().stream()
                .collect(Collectors.groupingBy(s -> nombres.getOrDefault(s.getTerapeutaId(), s.getTerapeutaId()),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve el avance promedio de los planes activos.
     *
     * @return porcentaje promedio
     * @throws PersistenciaException si falla la consulta
     */
    public double avancePromedio() throws PersistenciaException {
        Map<String, Long> completadas = sesionRepository.listarTodos().stream()
                .filter(s -> s.getEstado() == EstadoSesion.COMPLETADA && s.getPlanId() != null)
                .collect(Collectors.groupingBy(ServicioTerapia::getPlanId, Collectors.counting()));
        return planRepository.listarActivos().stream()
                .mapToDouble(p -> Math.min(100.0,
                        completadas.getOrDefault(p.getId(), 0L) * 100.0 / p.getSesionesTotales()))
                .average().orElse(0.0);
    }

    /**
     * Devuelve los pagos ordenados por fecha descendente.
     *
     * @return pagos
     * @throws PersistenciaException si falla la consulta
     */
    public List<Pago> pagos() throws PersistenciaException {
        return pagoRepository.listarTodos().stream()
                .sorted(Comparator.comparing(Pago::getFechaPago).reversed())
                .collect(Collectors.toList());
    }
}
