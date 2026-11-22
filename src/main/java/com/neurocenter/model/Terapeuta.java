package com.neurocenter.model;

import java.util.Objects;

/**
 * Entidad que representa a un terapeuta o especialista de NEUROCENTER.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class Terapeuta implements Entidad<String> {

    /** Identificador unico (TER-...). */
    private String id;
    /** Nombre del terapeuta. */
    private String nombre;
    /** Apellidos del terapeuta. */
    private String apellido;
    /** DNI peruano. */
    private String dni;
    /** Numero de celular. */
    private String telefono;
    /** Edad en años. */
    private int edad;
    /** Turno de atencion. */
    private Turno turno;
    /** Ruta o enlace de la foto. */
    private String foto;
    /** Especialidad principal. */
    private Especialidad especialidad;
    /** Tarifa por hora en soles. */
    private double tarifaHora;
    /** Indica si continua activo. */
    private boolean activo;

    /** Constructor sin argumentos requerido por el mapeo JDBC. */
    public Terapeuta() {
        this.activo = true;
    }

    /** @return nombre completo */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** @return nombre */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre nombre */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return apellidos */
    public String getApellido() {
        return apellido;
    }

    /** @param apellido apellidos */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /** @return DNI */
    public String getDni() {
        return dni;
    }

    /** @param dni DNI */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /** @return celular */
    public String getTelefono() {
        return telefono;
    }

    /** @param telefono celular */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** @return edad */
    public int getEdad() {
        return edad;
    }

    /** @param edad edad */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /** @return turno */
    public Turno getTurno() {
        return turno;
    }

    /** @param turno turno */
    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    /** @return foto */
    public String getFoto() {
        return foto;
    }

    /** @param foto foto */
    public void setFoto(String foto) {
        this.foto = foto;
    }

    /** @return especialidad */
    public Especialidad getEspecialidad() {
        return especialidad;
    }

    /** @param especialidad especialidad */
    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    /** @return tarifa por hora */
    public double getTarifaHora() {
        return tarifaHora;
    }

    /** @param tarifaHora tarifa por hora */
    public void setTarifaHora(double tarifaHora) {
        this.tarifaHora = tarifaHora;
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
        if (!(objeto instanceof Terapeuta otro)) {
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
        return getNombreCompleto() + " - " + (especialidad == null ? "Sin especialidad" : especialidad.getNombreMostrar());
    }
}
