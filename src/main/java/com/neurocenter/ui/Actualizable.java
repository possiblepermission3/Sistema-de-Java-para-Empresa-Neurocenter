package com.neurocenter.ui;

/**
 * Contrato para los paneles que pueden refrescar sus datos desde la base de datos.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public interface Actualizable {

    /**
     * Recarga los datos del panel desde la capa de servicios.
     */
    void actualizar();
}
