package com.neurocenter.model;

/**
 * Contrato para objetos capaces de calcular su costo a partir de una tarifa.
 * <p>Ejemplo de abstraccion: la interfaz separa la responsabilidad de tarificar
 * de la entidad concreta, permitiendo polimorfismo entre especialidades.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public interface CalculableCosto {

    /**
     * Calcula el costo de la sesion.
     *
     * @param tarifaHora tarifa por hora del terapeuta asignado
     * @return costo total en soles
     */
    double calcularCosto(double tarifaHora);
}
