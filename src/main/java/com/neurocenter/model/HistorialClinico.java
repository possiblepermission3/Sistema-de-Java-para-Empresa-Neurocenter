package com.neurocenter.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Historia clinica de un paciente, creada por el jefe del centro segun el
 * workflow (diagnostico externo antes de la evaluacion e interno despues).
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class HistorialClinico implements Entidad<String> {

    /** Identificador unico (HIS-...). */
    private String id;
    /** Paciente titular de la historia. */
    private String pacienteId;
    /** Sesiones programadas segun el plan. */
    private int sesionesProgramadas;
    /** Diagnostico interno del centro. */
    private String diagnosticoInterno;
    /** Diagnostico externo referido. */
    private String diagnosticoExterno;
    /** Enlace al documento adjunto (Google Drive). */
    private String adjunto;
    /** Fecha de registro. */
    private LocalDate fechaRegistro;

    /** Constructor sin argumentos. */
    public HistorialClinico() {
        this.fechaRegistro = LocalDate.now();
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

    /** @return sesiones programadas */
    public int getSesionesProgramadas() {
        return sesionesProgramadas;
    }

    /** @param sesionesProgramadas sesiones programadas */
    public void setSesionesProgramadas(int sesionesProgramadas) {
        this.sesionesProgramadas = sesionesProgramadas;
    }

    /** @return diagnostico interno */
    public String getDiagnosticoInterno() {
        return diagnosticoInterno;
    }

    /** @param diagnosticoInterno diagnostico interno */
    public void setDiagnosticoInterno(String diagnosticoInterno) {
        this.diagnosticoInterno = diagnosticoInterno;
    }

    /** @return diagnostico externo */
    public String getDiagnosticoExterno() {
        return diagnosticoExterno;
    }

    /** @param diagnosticoExterno diagnostico externo */
    public void setDiagnosticoExterno(String diagnosticoExterno) {
        this.diagnosticoExterno = diagnosticoExterno;
    }

    /** @return adjunto */
    public String getAdjunto() {
        return adjunto;
    }

    /** @param adjunto adjunto */
    public void setAdjunto(String adjunto) {
        this.adjunto = adjunto;
    }

    /** @return fecha de registro */
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    /** @param fechaRegistro fecha de registro */
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof HistorialClinico otro)) {
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
        return (id == null ? "Nueva historia" : id) + " - " + pacienteId;
    }
}
