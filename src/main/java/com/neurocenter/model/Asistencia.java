package com.neurocenter.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Registro de asistencia y nota de evolucion de una sesion concreta.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class Asistencia implements Entidad<String> {

    /** Identificador unico (ASI-...). */
    private String id;
    /** Sesion a la que corresponde. */
    private String sesionId;
    /** Estado de asistencia. */
    private EstadoAsistencia estado;
    /** Fecha efectiva de la sesion. */
    private LocalDate fechaEfectiva;
    /** Nota de evolucion del paciente. */
    private String notaEvolucion;

    /** Constructor sin argumentos. */
    public Asistencia() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** @return id de la sesion */
    public String getSesionId() {
        return sesionId;
    }

    /** @param sesionId id de la sesion */
    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    /** @return estado */
    public EstadoAsistencia getEstado() {
        return estado;
    }

    /** @param estado estado */
    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    /** @return fecha efectiva */
    public LocalDate getFechaEfectiva() {
        return fechaEfectiva;
    }

    /** @param fechaEfectiva fecha efectiva */
    public void setFechaEfectiva(LocalDate fechaEfectiva) {
        this.fechaEfectiva = fechaEfectiva;
    }

    /** @return nota de evolucion */
    public String getNotaEvolucion() {
        return notaEvolucion;
    }

    /** @param notaEvolucion nota de evolucion */
    public void setNotaEvolucion(String notaEvolucion) {
        this.notaEvolucion = notaEvolucion;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Asistencia otro)) {
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
        return estado + " - " + fechaEfectiva;
    }
}
