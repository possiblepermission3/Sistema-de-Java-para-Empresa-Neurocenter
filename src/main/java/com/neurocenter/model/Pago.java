package com.neurocenter.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Comprobante de pago registrado por el area administrativa.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class Pago implements Entidad<String> {

    /** Identificador unico (PAG-...). */
    private String id;
    /** Paciente que cancela. */
    private String pacienteId;
    /** Sesion vinculada (opcional en pagos de paquete). */
    private String sesionId;
    /** Fecha del pago. */
    private LocalDate fechaPago;
    /** Monto cancelado. */
    private double monto;
    /** Tipo de pago. */
    private TipoPago tipoPago;
    /** Metodo de pago. */
    private MetodoPago metodoPago;
    /** Numero de operacion bancaria. */
    private String numeroOperacion;
    /** Cantidad de sesiones del paquete. */
    private int sesionesPaquete;

    /** Constructor sin argumentos. */
    public Pago() {
        this.fechaPago = LocalDate.now();
        this.sesionesPaquete = 1;
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

    /** @return id de la sesion */
    public String getSesionId() {
        return sesionId;
    }

    /** @param sesionId id de la sesion */
    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    /** @return fecha de pago */
    public LocalDate getFechaPago() {
        return fechaPago;
    }

    /** @param fechaPago fecha de pago */
    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    /** @return monto */
    public double getMonto() {
        return monto;
    }

    /** @param monto monto */
    public void setMonto(double monto) {
        this.monto = monto;
    }

    /** @return tipo de pago */
    public TipoPago getTipoPago() {
        return tipoPago;
    }

    /** @param tipoPago tipo de pago */
    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }

    /** @return metodo de pago */
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    /** @param metodoPago metodo de pago */
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    /** @return numero de operacion */
    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    /** @param numeroOperacion numero de operacion */
    public void setNumeroOperacion(String numeroOperacion) {
        this.numeroOperacion = numeroOperacion;
    }

    /** @return sesiones del paquete */
    public int getSesionesPaquete() {
        return sesionesPaquete;
    }

    /** @param sesionesPaquete sesiones del paquete */
    public void setSesionesPaquete(int sesionesPaquete) {
        this.sesionesPaquete = sesionesPaquete;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Pago otro)) {
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
        return (id == null ? "Nuevo pago" : id) + " - " + monto;
    }
}
