package com.neurocenter.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Plan de rehabilitacion acordado con el paciente: numero de sesiones,
 * especialista responsable, objetivo y vigencia.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PlanRehabilitacion implements Entidad<String> {

    /** Identificador unico (PLAN-...). */
    private String id;
    /** Paciente del plan. */
    private String pacienteId;
    /** Terapeuta responsable. */
    private String terapeutaId;
    /** Especialidad del plan. */
    private Especialidad especialidad;
    /** Total de sesiones contratadas. */
    private int sesionesTotales;
    /** Fecha de inicio. */
    private LocalDate fechaInicio;
    /** Fecha estimada de fin. */
    private LocalDate fechaFin;
    /** Objetivo funcional. */
    private String objetivo;
    /** Indica si el plan esta vigente. */
    private boolean activo;

    /** Constructor sin argumentos. */
    public PlanRehabilitacion() {
        this.activo = true;
        this.fechaInicio = LocalDate.now();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** @return id del paciente */
    public String getPacienteId() {
        return pacienteId;
    }

    /** @param pacienteId id del paciente */
    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    /** @return id del terapeuta */
    public String getTerapeutaId() {
        return terapeutaId;
    }

    /** @param terapeutaId id del terapeuta */
    public void setTerapeutaId(String terapeutaId) {
        this.terapeutaId = terapeutaId;
    }

    /** @return especialidad */
    public Especialidad getEspecialidad() {
        return especialidad;
    }

    /** @param especialidad especialidad */
    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    /** @return total de sesiones */
    public int getSesionesTotales() {
        return sesionesTotales;
    }

    /** @param sesionesTotales total de sesiones */
    public void setSesionesTotales(int sesionesTotales) {
        this.sesionesTotales = sesionesTotales;
    }

    /** @return fecha de inicio */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /** @param fechaInicio fecha de inicio */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /** @return fecha de fin */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /** @param fechaFin fecha de fin */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /** @return objetivo */
    public String getObjetivo() {
        return objetivo;
    }

    /** @param objetivo objetivo */
    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    /** @return si esta activo */
    public boolean isActivo() {
        return activo;
    }

    /** @param activo si esta activo */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof PlanRehabilitacion otro)) {
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
        return (id == null ? "Nuevo plan" : id) + " - "
                + (especialidad == null ? "" : especialidad.getNombreMostrar());
    }
}
