package com.neurocenter.model;

import java.util.Objects;

/**
 * Cuenta de acceso al sistema con rol diferenciado.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class Usuario implements Entidad<String> {

    /** Nombre de usuario (clave primaria natural). */
    private String id;
    /** Hash SHA-256 de la contraseña. */
    private String passwordHash;
    /** Rol del usuario. */
    private String rol;
    /** Nombre completo del usuario. */
    private String nombreCompleto;
    /** Indica si la cuenta esta habilitada. */
    private boolean activo;

    /** Constructor sin argumentos. */
    public Usuario() {
        this.activo = true;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** @return hash de la contraseña */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @param passwordHash hash de la contraseña */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** @return rol */
    public String getRol() {
        return rol;
    }

    /** @param rol rol */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /** @return nombre completo */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /** @param nombreCompleto nombre completo */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
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
        if (!(objeto instanceof Usuario otro)) {
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
        return nombreCompleto + " (" + rol + ")";
    }
}
