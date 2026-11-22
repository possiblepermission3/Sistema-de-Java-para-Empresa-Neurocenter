package com.neurocenter.model;

/**
 * Tipo de pago registrado en NEUROCENTER.
 */
public enum TipoPago {
    /** Pago por una sesion individual. */
    INDIVIDUAL("Individual", 1),
    /** Pago anticipado por paquete de sesiones (con descuento). */
    PAQUETE("Paquete", 10);

    /** Etiqueta legible. */
    private final String nombreMostrar;
    /** Cantidad de sesiones incluidas por defecto. */
    private final int sesionesIncluidas;

    TipoPago(String nombreMostrar, int sesionesIncluidas) {
        this.nombreMostrar = nombreMostrar;
        this.sesionesIncluidas = sesionesIncluidas;
    }

    /** @return etiqueta legible */
    public String getNombreMostrar() {
        return nombreMostrar;
    }

    /** @return sesiones incluidas por defecto */
    public int getSesionesIncluidas() {
        return sesionesIncluidas;
    }

    @Override
    public String toString() {
        return nombreMostrar;
    }
}
