package com.neurocenter.model;

/**
 * Metodo de pago utilizado por el paciente (transferencias Interbank/BCP segun el workflow).
 */
public enum MetodoPago {
    /** Efectivo. */
    EFECTIVO,
    /** Transferencia Interbank. */
    INTERBANK,
    /** Transferencia BCP. */
    BCP,
    /** Tarjeta de credito o debito. */
    TARJETA,
    /** Yape. */
    YAPE,
    /** Plin. */
    PLIN;

    /** @return etiqueta legible */
    public String getNombreMostrar() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
