package com.neurocenter.model;

/**
 * Turno de atencion de un terapeuta.
 */
public enum Turno {
    /** Atencion de 8:00 a 13:00. */
    MANANA("Mañana", "08:00", "13:00"),
    /** Atencion de 14:00 a 19:00. */
    TARDE("Tarde", "14:00", "19:00");

    /** Etiqueta legible. */
    private final String nombreMostrar;
    /** Hora de inicio del turno. */
    private final String inicio;
    /** Hora de fin del turno. */
    private final String fin;

    Turno(String nombreMostrar, String inicio, String fin) {
        this.nombreMostrar = nombreMostrar;
        this.inicio = inicio;
        this.fin = fin;
    }

    /** @return etiqueta legible */
    public String getNombreMostrar() {
        return nombreMostrar;
    }

    /** @return hora de inicio */
    public String getInicio() {
        return inicio;
    }

    /** @return hora de fin */
    public String getFin() {
        return fin;
    }

    @Override
    public String toString() {
        return nombreMostrar;
    }
}
