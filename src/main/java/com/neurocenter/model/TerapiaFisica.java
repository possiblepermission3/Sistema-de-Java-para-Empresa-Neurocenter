package com.neurocenter.model;

/**
 * Sesion de terapia fisica. Aplica un recargo del 5% cuando la sesion supera
 * los 60 minutos por uso prolongado de equipos de electroterapia.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class TerapiaFisica extends ServicioTerapia {

    /** Area corporal o segmento tratado. */
    private String areaTratada;

    /** Constructor sin argumentos. */
    public TerapiaFisica() {
        super();
    }

    /**
     * Crea una sesion de terapia fisica para un area determinada.
     *
     * @param areaTratada area corporal
     */
    public TerapiaFisica(String areaTratada) {
        this();
        this.areaTratada = areaTratada;
    }

    @Override
    public Especialidad getEspecialidad() {
        return Especialidad.FISICA;
    }

    @Override
    public String getDetalleEspecifico() {
        return areaTratada == null ? "Area no especificada" : areaTratada;
    }

    @Override
    public double calcularCosto(double tarifaHora) {
        double base = costoBase(tarifaHora);
        return duracionMinutos > 60 ? base * 1.05 : base;
    }

    /** @return area tratada */
    public String getAreaTratada() {
        return areaTratada;
    }

    /** @param areaTratada area tratada */
    public void setAreaTratada(String areaTratada) {
        this.areaTratada = areaTratada;
    }
}
