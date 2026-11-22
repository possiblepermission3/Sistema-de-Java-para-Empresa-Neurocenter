package com.neurocenter.model;

/**
 * Registro de asistencia de una sesion.
 */
public enum EstadoAsistencia {
    /** Paciente asistio a la sesion. */
    ASISTIO("Asistió"),
    /** Paciente falto sin justificacion. */
    FALTO("Faltó"),
    /** Paciente falto con justificacion. */
    JUSTIFICADO("Justificado");

    /** Etiqueta legible. */
    private final String nombreMostrar;

    EstadoAsistencia(String nombreMostrar) {
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
