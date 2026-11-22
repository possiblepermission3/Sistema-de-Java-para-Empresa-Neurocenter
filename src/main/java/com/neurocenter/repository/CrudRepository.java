package com.neurocenter.repository;

import java.util.List;
import java.util.Optional;

import com.neurocenter.exception.PersistenciaException;

/**
 * Contrato generico de persistencia para entidades con identificador de tipo
 * {@code ID}. Define las operaciones CRUD y el conteo de registros.
 *
 * @param <T>  tipo de entidad
 * @param <ID> tipo del identificador
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public interface CrudRepository<T, ID> {

    /**
     * Inserta o actualiza una entidad segun tenga o no identificador.
     *
     * @param entidad entidad a persistir
     * @return entidad persistida
     * @throws PersistenciaException si falla la operacion
     */
    T guardar(T entidad) throws PersistenciaException;

    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador
     * @return entidad encontrada, o vacio
     * @throws PersistenciaException si falla la consulta
     */
    Optional<T> buscarPorId(ID id) throws PersistenciaException;

    /**
     * Lista todas las entidades.
     *
     * @return lista de entidades
     * @throws PersistenciaException si falla la consulta
     */
    List<T> listarTodos() throws PersistenciaException;

    /**
     * Actualiza una entidad existente.
     *
     * @param entidad entidad con datos actualizados
     * @return verdadero si se actualizo al menos una fila
     * @throws PersistenciaException si falla la operacion
     */
    boolean actualizar(T entidad) throws PersistenciaException;

    /**
     * Elimina una entidad por identificador.
     *
     * @param id identificador
     * @return verdadero si se elimino al menos una fila
     * @throws PersistenciaException si falla la operacion
     */
    boolean eliminar(ID id) throws PersistenciaException;

    /**
     * @return cantidad total de registros
     * @throws PersistenciaException si falla la consulta
     */
    long contar() throws PersistenciaException;
}
