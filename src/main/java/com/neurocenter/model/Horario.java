package com.neurocenter.model;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Bloque horario de atencion de un terapeuta por dia de la semana.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class Horario implements Entidad<String> {

    /** Identificador unico (HOR-...). */
    private String id;
    /** Terapeuta propietario del horario. */
    private String terapeutaId;
    /** Dia de la semana (LUNES a SABADO). */
    private String diaSemana;
    /** Hora de inicio. */
    private LocalTime horaInicio;
    /** Hora de fin. */
    private LocalTime horaFin;

    /** Constructor sin argumentos. */
    public Horario() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** @return id del terapeuta */
    public String getTerapeutaId() {
        return terapeutaId;
    }

    /** @param terapeutaId id del terapeuta */
    public void setTerapeutaId(String terapeutaId) {
        this.terapeutaId = terapeutaId;
    }

    /** @return dia de la semana */
    public String getDiaSemana() {
        return diaSemana;
    }

    /** @param diaSemana dia de la semana */
    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    /** @return hora de inicio */
    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    /** @param horaInicio hora de inicio */
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    /** @return hora de fin */
    public LocalTime getHoraFin() {
        return horaFin;
    }

    /** @param horaFin hora de fin */
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * Comprueba si una hora se encuentra dentro del bloque.
     *
     * @param hora hora a evaluar
     * @return verdadero si esta dentro del rango [inicio, fin)
     */
    public boolean contiene(LocalTime hora) {
        return horaInicio != null && horaFin != null && !hora.isBefore(horaInicio) && hora.isBefore(horaFin);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Horario otro)) {
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
        return diaSemana + " " + horaInicio + " - " + horaFin;
    }
}
