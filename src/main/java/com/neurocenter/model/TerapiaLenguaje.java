package com.neurocenter.model;

/**
 * Sesion de terapia de lenguaje. Aplica un recargo del 5% por materiales
 * didacticos especializados (laminas, juegos foneticos, etc.).
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class TerapiaLenguaje extends ServicioTerapia {

    /** Objetivo fonetico o linguístico de la sesion. */
    private String objetivoLenguaje;

    /** Constructor sin argumentos. */
    public TerapiaLenguaje() {
        super();
    }

    /**
     * Crea una sesion de terapia de lenguaje.
     *
     * @param objetivoLenguaje objetivo trabajado
     */
    public TerapiaLenguaje(String objetivoLenguaje) {
        this();
        this.objetivoLenguaje = objetivoLenguaje;
    }

    @Override
    public Especialidad getEspecialidad() {
        return Especialidad.LENGUAJE;
    }

    @Override
    public String getDetalleEspecifico() {
        return objetivoLenguaje == null ? "Objetivo no especificado" : objetivoLenguaje;
    }

    @Override
    public double calcularCosto(double tarifaHora) {
        return costoBase(tarifaHora) * 1.05;
    }

    /** @return objetivo de lenguaje */
    public String getObjetivoLenguaje() {
        return objetivoLenguaje;
    }

    /** @param objetivoLenguaje objetivo de lenguaje */
    public void setObjetivoLenguaje(String objetivoLenguaje) {
        this.objetivoLenguaje = objetivoLenguaje;
    }
}
