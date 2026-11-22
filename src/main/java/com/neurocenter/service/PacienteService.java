package com.neurocenter.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.neurocenter.exception.DatosInvalidosException;
import com.neurocenter.exception.EntidadNoEncontradaException;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.HistorialClinico;
import com.neurocenter.model.Paciente;
import com.neurocenter.repository.HistorialRepository;
import com.neurocenter.repository.PacienteRepository;
import com.neurocenter.util.Validadores;

/**
 * Logica de negocio para la gestion de pacientes y su historia clinica.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PacienteService {

    /** Repositorio de pacientes. */
    private final PacienteRepository pacienteRepository;
    /** Repositorio de historias clinicas. */
    private final HistorialRepository historialRepository;

    /** Crea el servicio con sus repositorios. */
    public PacienteService() {
        this(new PacienteRepository(), new HistorialRepository());
    }

    /**
     * Crea el servicio con dependencias inyectadas (facilita pruebas).
     *
     * @param pacienteRepository  repositorio de pacientes
     * @param historialRepository repositorio de historias
     */
    public PacienteService(PacienteRepository pacienteRepository, HistorialRepository historialRepository) {
        this.pacienteRepository = pacienteRepository;
        this.historialRepository = historialRepository;
    }

    /**
     * Lista todos los pacientes.
     *
     * @return pacientes
     * @throws PersistenciaException si falla la consulta
     */
    public List<Paciente> listar() throws PersistenciaException {
        return pacienteRepository.listarTodos();
    }

    /**
     * Busca un paciente por identificador.
     *
     * @param id identificador
     * @return paciente encontrado
     * @throws EntidadNoEncontradaException si no existe
     * @throws PersistenciaException        si falla la consulta
     */
    public Paciente buscar(String id) throws EntidadNoEncontradaException, PersistenciaException {
        return pacienteRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Paciente", id));
    }

    /**
     * Valida y persiste un paciente (alta o modificacion).
     *
     * @param paciente paciente a guardar
     * @return paciente persistido
     * @throws DatosInvalidosException si falla la validacion
     * @throws PersistenciaException   si falla la persistencia
     */
    public Paciente guardar(Paciente paciente) throws DatosInvalidosException, PersistenciaException {
        paciente.setNombre(Validadores.texto(paciente.getNombre(), "nombre", 2));
        paciente.setApellido(Validadores.texto(paciente.getApellido(), "apellido", 2));
        paciente.setDni(Validadores.dni(paciente.getDni()));
        paciente.setTelefono(Validadores.telefono(paciente.getTelefono()));
        paciente.setEdad(Validadores.edad(paciente.getEdad(), 0, 120, "edad"));
        paciente.setDomicilio(Validadores.texto(paciente.getDomicilio(), "domicilio", 4));
        Optional<Paciente> duplicado = pacienteRepository.buscarPorDni(paciente.getDni(), paciente.getId());
        if (duplicado.isPresent()) {
            throw new DatosInvalidosException("Ya existe un paciente con el DNI " + paciente.getDni() + ".");
        }
        return pacienteRepository.guardar(paciente);
    }

    /**
     * Elimina un paciente. Si tiene sesiones registradas se da de baja
     * logicamente para no perder el historial clinico (integridad referencial).
     *
     * @param id identificador
     * @throws EntidadNoEncontradaException si no existe
     * @throws PersistenciaException        si falla la operacion
     */
    public void eliminar(String id) throws EntidadNoEncontradaException, PersistenciaException {
        buscar(id);
        if (pacienteRepository.tieneSesiones(id)) {
            pacienteRepository.desactivar(id);
        } else {
            pacienteRepository.eliminar(id);
            historialRepository.porPaciente(id).ifPresent(h -> {
                try {
                    historialRepository.eliminar(h.getId());
                } catch (PersistenciaException e) {
                    throw new IllegalStateException(e);
                }
            });
        }
    }

    /**
     * Filtra pacientes por nombre, apellido o DNI usando Stream API.
     *
     * @param texto criterio de busqueda
     * @return pacientes que coinciden
     * @throws PersistenciaException si falla la consulta
     */
    public List<Paciente> buscarPorTexto(String texto) throws PersistenciaException {
        String criterio = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        return listar().stream()
                .filter(p -> criterio.isEmpty()
                        || p.getNombreCompleto().toLowerCase(Locale.ROOT).contains(criterio)
                        || p.getDni().contains(criterio))
                .sorted((a, b) -> a.getApellido().compareToIgnoreCase(b.getApellido()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve la historia clinica de un paciente.
     *
     * @param pacienteId id del paciente
     * @return historia clinica, si existe
     * @throws PersistenciaException si falla la consulta
     */
    public Optional<HistorialClinico> historiaClinica(String pacienteId) throws PersistenciaException {
        return historialRepository.porPaciente(pacienteId);
    }

    /**
     * Valida y guarda una historia clinica.
     *
     * @param historial historia a guardar
     * @return historia persistida
     * @throws DatosInvalidosException si faltan datos
     * @throws PersistenciaException   si falla la persistencia
     */
    public HistorialClinico guardarHistoria(HistorialClinico historial)
            throws DatosInvalidosException, PersistenciaException {
        Validadores.requerido(historial.getPacienteId(), "paciente");
        Validadores.enteroEnRango(historial.getSesionesProgramadas(), 0, 500, "numero de sesiones");
        Validadores.texto(historial.getDiagnosticoExterno(), "diagnóstico externo", 3);
        return historialRepository.guardar(historial);
    }
}
