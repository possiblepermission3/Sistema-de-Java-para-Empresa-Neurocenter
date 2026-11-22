package com.neurocenter.model;

/**
 * Especialidades terapeuticas ofrecidas por NEUROCENTER.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public enum Especialidad {

    /** Terapia fisica / fisioterapia. */
    FISICA("Terapia Física", 60),
    /** Terapia ocupacional. */
    OCUPACIONAL("Terapia Ocupacional", 60),
    /** Terapia de lenguaje. */
    LENGUAJE("Terapia de Lenguaje", 45);

    /** Nombre para la interfaz. */
    private final String nombreMostrar;
    /** Duracion sugerida en minutos. */
    private final int duracionDefecto;

    /**
     * Constructor del enum.
     *
     * @param nombreMostrar     etiqueta legible
     * @param duracionDefecto   duracion sugerida en minutos
     */
    Especialidad(String nombreMostrar, int duracionDefecto) {
        this.nombreMostrar = nombreMostrar;
        this.duracionDefecto = duracionDefecto;
    }

    /**
     * @return etiqueta legible para la interfaz
     */
    public String getNombreMostrar() {
        return nombreMostrar;
    }

    /**
     * @return duracion sugerida en minutos
     */
    public int getDuracionDefecto() {
        return duracionDefecto;
    }

    @Override
    public String toString() {
        return nombreMostrar;
    }
}
