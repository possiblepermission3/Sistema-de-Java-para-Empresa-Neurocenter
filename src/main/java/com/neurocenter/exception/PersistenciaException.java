package com.neurocenter.exception;

/**
 * Excepcion tecnica que envuelve errores de JDBC y de inicializacion de la
 * base de datos, evitando que detalles SQL lleguen a la interfaz de usuario.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PersistenciaException extends Exception {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepcion con un mensaje y la causa original.
     *
     * @param mensaje contexto de la operacion fallida
     * @param causa   excepcion SQL o de E/S original
     */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje + (causa == null ? "" : " | " + causa.getMessage()), causa);
    }

    /**
     * Crea la excepcion con un mensaje.
     *
     * @param mensaje contexto de la operacion fallida
     */
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
