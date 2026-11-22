package com.neurocenter.model;

/**
 * Fabrica de sesiones: centraliza la creacion polimorfica de objetos
 * {@link ServicioTerapia} segun la especialidad solicitada.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class SesionFactory {

    /** Constructor privado: clase utilitaria. */
    private SesionFactory() {
    }

    /**
     * Crea una sesion vacia del tipo correspondiente.
     *
     * @param tipo especialidad de la sesion
     * @return instancia concreta de la subclase adecuada
     */
    public static ServicioTerapia crear(Especialidad tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("La especialidad no puede ser nula");
        }
        return switch (tipo) {
            case FISICA -> new TerapiaFisica();
            case OCUPACIONAL -> new TerapiaOcupacional();
            case LENGUAJE -> new TerapiaLenguaje();
        };
    }
}
