package com.neurocenter.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Evaluacion periodica del progreso funcional del paciente dentro de un plan.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class EvaluacionProgreso implements Entidad<String> {

    /** Identificador unico (EVA-...). */
    private String id;
    /** Plan evaluado. */
    private String planId;
    /** Terapeuta que aplico la evaluacion. */
    private String terapeutaId;
    /** Fecha de la evaluacion. */
    private LocalDate fecha;
    /** Escala funcional de 0 a 100. */
    private int escalaFuncional;
    /** Observaciones clinicas. */
    private String observaciones;

    /** Constructor sin argumentos. */
    public EvaluacionProgreso() {
        this.fecha = LocalDate.now();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** @return id del plan */
    public String getPlanId() {
        return planId;
    }

    /** @param planId id del plan */
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    /** @return id del terapeuta */
    public String getTerapeutaId() {
        return terapeutaId;
    }

    /** @param terapeutaId id del terapeuta */
    public void setTerapeutaId(String terapeutaId) {
        this.terapeutaId = terapeutaId;
    }

    /** @return fecha */
    public LocalDate getFecha() {
        return fecha;
    }

    /** @param fecha fecha */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /** @return escala funcional */
    public int getEscalaFuncional() {
        return escalaFuncional;
    }

    /** @param escalaFuncional escala funcional */
    public void setEscalaFuncional(int escalaFuncional) {
        this.escalaFuncional = escalaFuncional;
    }

    /** @return observaciones */
    public String getObservaciones() {
        return observaciones;
    }

    /** @param observaciones observaciones */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof EvaluacionProgreso otro)) {
            return false;
        }
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return fecha + " - " + escalaFuncional + "/100";
    }
}
