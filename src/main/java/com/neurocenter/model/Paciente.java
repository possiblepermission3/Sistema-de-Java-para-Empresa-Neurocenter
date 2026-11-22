package com.neurocenter.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa a un paciente de NEUROCENTER.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class Paciente implements Entidad<String> {

    /** Identificador unico (PAC-...). */
    private String id;
    /** Nombre del paciente. */
    private String nombre;
    /** Apellidos del paciente. */
    private String apellido;
    /** DNI peruano de 8 digitos. */
    private String dni;
    /** Edad en años. */
    private int edad;
    /** Numero de celular. */
    private String telefono;
    /** Domicilio declarado. */
    private String domicilio;
    /** Fecha de registro en el centro. */
    private LocalDate fechaRegistro;
    /** Diagnostico referido por el medico externo. */
    private String diagnosticoReferido;
    /** Indica si el paciente continua activo. */
    private boolean activo;

    /** Constructor sin argumentos requerido por el mapeo JDBC. */
    public Paciente() {
        this.activo = true;
        this.fechaRegistro = LocalDate.now();
    }

    /**
     * Crea un paciente con sus datos principales.
     *
     * @param nombre              nombre
     * @param apellido            apellidos
     * @param dni                 DNI
     * @param edad                edad
     * @param telefono            celular
     * @param domicilio           domicilio
     * @param diagnosticoReferido diagnostico externo
     */
    public Paciente(String nombre, String apellido, String dni, int edad, String telefono,
                    String domicilio, String diagnosticoReferido) {
        this();
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.edad = edad;
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.diagnosticoReferido = diagnosticoReferido;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** @return nombre completo */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
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

    /** @return edad */
    public int getEdad() {
        return edad;
    }

    /** @param edad edad */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /** @return celular */
    public String getTelefono() {
        return telefono;
    }

    /** @param telefono celular */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** @return domicilio */
    public String getDomicilio() {
        return domicilio;
    }

    /** @param domicilio domicilio */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /** @return fecha de registro */
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    /** @param fechaRegistro fecha de registro */
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /** @return diagnostico referido */
    public String getDiagnosticoReferido() {
        return diagnosticoReferido;
    }

    /** @param diagnosticoReferido diagnostico referido */
    public void setDiagnosticoReferido(String diagnosticoReferido) {
        this.diagnosticoReferido = diagnosticoReferido;
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
        if (!(objeto instanceof Paciente otro)) {
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
        return getNombreCompleto() + " (" + dni + ")";
    }
}
