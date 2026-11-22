package com.neurocenter.model;

/**
 * Contrato generico que deben cumplir todas las entidades persistentes.
 * <p>Permite que los repositorios genericos trabajen con cualquier entidad
 * siempre que puedan leer y asignar su identificador.</p>
 *
 * @param <ID> tipo del identificador de la entidad
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public interface Entidad<ID> {

    /**
     * @return identificador unico de la entidad
     */
    ID getId();

    /**
     * Asigna el identificador de la entidad.
     *
     * @param id nuevo identificador
     */
    void setId(ID id);
}
