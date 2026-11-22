package com.neurocenter.model;

/**
 * Sesion de terapia ocupacional. Incluye un cargo fijo por materiales de
 * adaptacion y manualidades utilizados durante la sesion.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class TerapiaOcupacional extends ServicioTerapia {

    /** Actividad adaptada trabajada en la sesion. */
    private String actividadAdaptada;

    /** Constructor sin argumentos. */
    public TerapiaOcupacional() {
        super();
    }

    /**
     * Crea una sesion de terapia ocupacional.
     *
     * @param actividadAdaptada actividad trabajada
     */
    public TerapiaOcupacional(String actividadAdaptada) {
        this();
        this.actividadAdaptada = actividadAdaptada;
    }

    @Override
    public Especialidad getEspecialidad() {
        return Especialidad.OCUPACIONAL;
    }

    @Override
    public String getDetalleEspecifico() {
        return actividadAdaptada == null ? "Actividad no especificada" : actividadAdaptada;
    }

    @Override
    public double calcularCosto(double tarifaHora) {
        return costoBase(tarifaHora) + 5.0;
    }

    /** @return actividad adaptada */
    public String getActividadAdaptada() {
        return actividadAdaptada;
    }

    /** @param actividadAdaptada actividad adaptada */
    public void setActividadAdaptada(String actividadAdaptada) {
        this.actividadAdaptada = actividadAdaptada;
    }
}
