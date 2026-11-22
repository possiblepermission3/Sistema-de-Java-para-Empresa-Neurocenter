package com.neurocenter.util;

import java.util.Set;

/**
 * Reglas de acceso por rol de NEUROCENTER.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class Permisos {

    /** Rol administrador. */
    public static final String ROL_ADMIN = "ADMIN";
    /** Rol terapeuta. */
    public static final String ROL_TERAPEUTA = "TERAPEUTA";

    /** Modulos visibles para administrador. */
    private static final Set<String> MODULOS_ADMIN = Set.of(
            "Inicio", "Pacientes", "Terapeutas", "Sesiones", "Planes", "Pagos", "Reportes", "Institucional");

    /** Modulos visibles para terapeuta. */
    private static final Set<String> MODULOS_TERAPEUTA = Set.of(
            "Inicio", "Pacientes", "Sesiones", "Planes", "Reportes", "Institucional");

    /** Constructor privado: clase utilitaria. */
    private Permisos() {
    }

    /**
     * Devuelve los modulos permitidos para un rol.
     *
     * @param rol rol del usuario
     * @return conjunto inmutable de modulos
     */
    public static Set<String> modulosPermitidos(String rol) {
        return ROL_ADMIN.equalsIgnoreCase(rol) ? MODULOS_ADMIN : MODULOS_TERAPEUTA;
    }

    /**
     * Indica si un rol puede acceder a un modulo.
     *
     * @param rol    rol del usuario
     * @param modulo clave del modulo
     * @return {@code true} si el acceso esta permitido
     */
    public static boolean puedeAcceder(String rol, String modulo) {
        return modulo != null && modulosPermitidos(rol).contains(modulo);
    }

    /**
     * Indica si un rol puede eliminar pacientes.
     *
     * @param rol rol del usuario
     * @return {@code true} solo para administrador
     */
    public static boolean puedeEliminarPaciente(String rol) {
        return ROL_ADMIN.equalsIgnoreCase(rol);
    }
}
