package com.neurocenter.exception;

/**
 * Excepcion lanzada cuando no se encuentra una entidad solicitada por su
 * identificador o por un criterio de busqueda.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class EntidadNoEncontradaException extends Exception {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepcion indicando la entidad y el criterio.
     *
     * @param entidad tipo de entidad (por ejemplo "Paciente")
     * @param criterio identificador o criterio de busqueda
     */
    public EntidadNoEncontradaException(String entidad, String criterio) {
        super(entidad + " no encontrado(a) para: " + criterio);
    }

    /**
     * Crea la excepcion con un mensaje personalizado.
     *
     * @param mensaje detalle del error
     */
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
