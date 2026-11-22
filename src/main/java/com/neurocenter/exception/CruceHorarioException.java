package com.neurocenter.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Excepcion de regla de negocio lanzada cuando una nueva sesion se superpone
 * con otra ya programada para el mismo terapeuta o el mismo paciente.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class CruceHorarioException extends Exception {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Formato legible de fecha y hora. */
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Crea la excepcion con los datos del conflicto detectado.
     *
     * @param recurso  "terapeuta" o "paciente"
     * @param nombre   nombre del recurso ocupado
     * @param inicio   inicio de la sesion existente
     * @param fin      fin de la sesion existente
     */
    public CruceHorarioException(String recurso, String nombre, LocalDateTime inicio, LocalDateTime fin) {
        super(String.format("Cruce de horario: el %s %s ya tiene una sesion de %s a %s.",
                recurso, nombre, inicio.format(FORMATO), fin.format(FORMATO)));
    }

    /**
     * Crea la excepcion con un mensaje personalizado.
     *
     * @param mensaje detalle del conflicto
     */
    public CruceHorarioException(String mensaje) {
        super(mensaje);
    }
}
