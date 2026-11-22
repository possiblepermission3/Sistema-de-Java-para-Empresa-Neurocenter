package com.neurocenter.exception;

/**
 * Excepcion de negocio lanzada cuando una entrada no cumple las reglas de
 * validacion (DNI, telefono, fechas, montos, campos obligatorios, etc.).
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class DatosInvalidosException extends Exception {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepcion con un mensaje descriptivo.
     *
     * @param mensaje detalle del error de validacion
     */
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepcion con un mensaje y su causa.
     *
     * @param mensaje detalle del error de validacion
     * @param causa   excepcion original
     */
    public DatosInvalidosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
