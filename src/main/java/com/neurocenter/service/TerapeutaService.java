package com.neurocenter.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.neurocenter.exception.DatosInvalidosException;
import com.neurocenter.exception.EntidadNoEncontradaException;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.Terapeuta;
import com.neurocenter.repository.HorarioRepository;
import com.neurocenter.repository.TerapeutaRepository;
import com.neurocenter.util.Validadores;

/**
 * Logica de negocio para terapeutas y sus horarios de atencion.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class TerapeutaService {

    /** Repositorio de terapeutas. */
    private final TerapeutaRepository terapeutaRepository;
    /** Repositorio de horarios. */
    private final HorarioRepository horarioRepository;

    /** Crea el servicio. */
    public TerapeutaService() {
        this(new TerapeutaRepository(), new HorarioRepository());
    }

    /**
     * Crea el servicio con dependencias inyectadas.
     *
     * @param terapeutaRepository repositorio de terapeutas
     * @param horarioRepository   repositorio de horarios
     */
    public TerapeutaService(TerapeutaRepository terapeutaRepository, HorarioRepository horarioRepository) {
        this.terapeutaRepository = terapeutaRepository;
        this.horarioRepository = horarioRepository;
    }

    /**
     * @return todos los terapeutas
     * @throws PersistenciaException si falla la consulta
     */
    public List<Terapeuta> listar() throws PersistenciaException {
        return terapeutaRepository.listarTodos();
    }

    /**
     * Busca un terapeuta por id.
     *
     * @param id identificador
     * @return terapeuta
     * @throws EntidadNoEncontradaException si no existe
     * @throws PersistenciaException        si falla la consulta
     */
    public Terapeuta buscar(String id) throws EntidadNoEncontradaException, PersistenciaException {
        return terapeutaRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Terapeuta", id));
    }

    /**
     * Valida y guarda un terapeuta.
     *
     * @param terapeuta terapeuta a guardar
     * @return terapeuta persistido
     * @throws DatosInvalidosException si falla la validacion
     * @throws PersistenciaException   si falla la persistencia
     */
    public Terapeuta guardar(Terapeuta terapeuta) throws DatosInvalidosException, PersistenciaException {
        terapeuta.setNombre(Validadores.texto(terapeuta.getNombre(), "nombre", 2));
        terapeuta.setApellido(Validadores.texto(terapeuta.getApellido(), "apellido", 2));
        terapeuta.setDni(Validadores.dni(terapeuta.getDni()));
        terapeuta.setTelefono(Validadores.telefono(terapeuta.getTelefono()));
        terapeuta.setEdad(Validadores.edad(terapeuta.getEdad(), 18, 100, "edad"));
        Validadores.requerido(terapeuta.getEspecialidad(), "especialidad");
        Validadores.requerido(terapeuta.getTurno(), "turno");
        Validadores.monto(terapeuta.getTarifaHora(), "tarifa por hora");
        Optional<Terapeuta> duplicado = terapeutaRepository.buscarPorDni(terapeuta.getDni(), terapeuta.getId());
        if (duplicado.isPresent()) {
            throw new DatosInvalidosException("Ya existe un terapeuta con el DNI " + terapeuta.getDni() + ".");
        }
        return terapeutaRepository.guardar(terapeuta);
    }

    /**
     * Lista los terapeutas activos de una especialidad.
     *
     * @param especialidad especialidad
     * @return terapeutas disponibles
     * @throws PersistenciaException si falla la consulta
     */
    public List<Terapeuta> porEspecialidad(Especialidad especialidad) throws PersistenciaException {
        return terapeutaRepository.buscarPorEspecialidad(especialidad);
    }

    /**
     * Filtra terapeutas por texto usando Stream API.
     *
     * @param texto criterio
     * @return terapeutas filtrados
     * @throws PersistenciaException si falla la consulta
     */
    public List<Terapeuta> buscarPorTexto(String texto) throws PersistenciaException {
        String criterio = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        return listar().stream()
                .filter(t -> criterio.isEmpty()
                        || t.getNombreCompleto().toLowerCase(Locale.ROOT).contains(criterio)
                        || (t.getEspecialidad() != null && t.getEspecialidad().name().contains(criterio.toUpperCase(Locale.ROOT))))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve los horarios de un terapeuta.
     *
     * @param terapeutaId id del terapeuta
     * @return horarios
     * @throws PersistenciaException si falla la consulta
     */
    public List<com.neurocenter.model.Horario> horarios(String terapeutaId) throws PersistenciaException {
        return horarioRepository.porTerapeuta(terapeutaId);
    }
}
