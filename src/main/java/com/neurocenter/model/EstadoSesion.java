package com.neurocenter.model;

/**
 * Estados posibles de una sesion de rehabilitacion.
 */
public enum EstadoSesion {
    /** Sesion agendada, aun no ejecutada. */
    PROGRAMADA("Programada"),
    /** Sesion ejecutada correctamente. */
    COMPLETADA("Completada"),
    /** Sesion cancelada por el paciente o el centro. */
    CANCELADA("Cancelada"),
    /** Paciente no asistio. */
    INASISTIDA("Inasistida");

    /** Etiqueta legible. */
    private final String nombreMostrar;

    EstadoSesion(String nombreMostrar) {
        this.nombreMostrar = nombreMostrar;
    }

    /** @return etiqueta legible */
    public String getNombreMostrar() {
        return nombreMostrar;
    }

    @Override
    public String toString() {
        return nombreMostrar;
    }
}
