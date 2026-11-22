package com.neurocenter.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapeador funcional de filas JDBC a objetos de dominio.
 *
 * @param <T> tipo de entidad resultante
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * Convierte la fila actual del ResultSet en una entidad.
     *
     * @param rs ResultSet posicionado en la fila
     * @return entidad mapeada
     * @throws SQLException si falla la lectura de columnas
     */
    T map(ResultSet rs) throws SQLException;
}
