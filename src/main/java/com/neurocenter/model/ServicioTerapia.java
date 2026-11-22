package com.neurocenter.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase abstracta base de todas las sesiones de rehabilitacion de NEUROCENTER.
 * <p>Representa el concepto comun de "cita/sesion": paciente, terapeuta,
 * fecha, duracion, estado y costo. Las particularidades de cada terapia se
 * delegan a las subclases {@link TerapiaFisica}, {@link TerapiaOcupacional} y
 * {@link TerapiaLenguaje} mediante polimorfismo.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public abstract class ServicioTerapia implements Entidad<String>, CalculableCosto {

    /** Identificador unico (SES-...). */
    protected String id;
    /** Identificador del paciente atendido. */
    protected String pacienteId;
    /** Identificador del terapeuta asignado. */
    protected String terapeutaId;
    /** Identificador del plan de rehabilitacion asociado (opcional). */
    protected String planId;
    /** Fecha y hora de inicio. */
    protected LocalDateTime fechaHora;
    /** Duracion en minutos. */
    protected int duracionMinutos;
    /** Costo calculado en soles. */
    protected double costo;
    /** Estado de la sesion. */
    protected EstadoSesion estado;
    /** Notas clinicas u operativas. */
    protected String notas;
    /** Fecha de creacion del registro. */
    protected LocalDateTime createdAt;

    /** Constructor base: estado por defecto PROGRAMADA. */
    protected ServicioTerapia() {
        this.estado = EstadoSesion.PROGRAMADA;
        this.createdAt = LocalDateTime.now();
        this.duracionMinutos = 60;
    }

    /**
     * @return especialidad de la terapia (implementado por cada subclase)
     */
    public abstract Especialidad getEspecialidad();

    /**
     * @return detalle especifico de la terapia para mostrar en la interfaz
     */
    public abstract String getDetalleEspecifico();

    /**
     * Calcula el costo proporcional al tiempo usando la tarifa por hora.
     *
     * @param tarifaHora tarifa del terapeuta
     * @return costo base en soles
     */
    protected double costoBase(double tarifaHora) {
        return tarifaHora * duracionMinutos / 60.0;
    }

    @Override
    public double calcularCosto(double tarifaHora) {
        return costoBase(tarifaHora);
    }

    /**
     * @return fecha y hora de finalizacion estimada
     */
    public LocalDateTime getFin() {
        return fechaHora == null ? null : fechaHora.plusMinutes(duracionMinutos);
    }

    /**
     * Determina si esta sesion se superpone en el tiempo con otra.
     * <p>Intervalo semiabierto: [inicio, fin). Dos sesiones consecutivas
     * (fin1 == inicio2) NO se consideran cruce.</p>
     *
     * @param otra otra sesion
     * @return verdadero si hay superposicion temporal
     */
    public boolean seSuperponeCon(ServicioTerapia otra) {
        if (otra == null || this.fechaHora == null || otra.fechaHora == null) {
            return false;
        }
        return this.fechaHora.isBefore(otra.getFin()) && otra.fechaHora.isBefore(this.getFin());
    }

    /**
     * @return si la sesion esta activa (programada o completada)
     */
    public boolean esActiva() {
        return estado == EstadoSesion.PROGRAMADA || estado == EstadoSesion.COMPLETADA;
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

    /** @return id del plan */
    public String getPlanId() {
        return planId;
    }

    /** @param planId id del plan */
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    /** @return fecha y hora */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /** @param fechaHora fecha y hora */
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    /** @return duracion en minutos */
    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    /** @param duracionMinutos duracion en minutos */
    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    /** @return costo */
    public double getCosto() {
        return costo;
    }

    /** @param costo costo */
    public void setCosto(double costo) {
        this.costo = costo;
    }

    /** @return estado */
    public EstadoSesion getEstado() {
        return estado;
    }

    /** @param estado estado */
    public void setEstado(EstadoSesion estado) {
        this.estado = estado;
    }

    /** @return notas */
    public String getNotas() {
        return notas;
    }

    /** @param notas notas */
    public void setNotas(String notas) {
        this.notas = notas;
    }

    /** @return fecha de creacion */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** @param createdAt fecha de creacion */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof ServicioTerapia otro)) {
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
        return getEspecialidad().getNombreMostrar() + " - "
                + (fechaHora == null ? "sin fecha" : fechaHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")));
    }
}
