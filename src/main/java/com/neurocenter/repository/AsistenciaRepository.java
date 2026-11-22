package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Asistencia;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de registros de asistencia.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class AsistenciaRepository extends AbstractJdbcRepository<Asistencia, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO asistencia (asistencia_id, sesion_id, estado, fecha_efectiva, nota_evolucion) "
                    + "VALUES (?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE asistencia SET sesion_id=?, estado=?, fecha_efectiva=?, nota_evolucion=? "
                    + "WHERE asistencia_id=?";

    /** Crea el repositorio. */
    public AsistenciaRepository() {
        super("asistencia", "asistencia_id", INSERT, UPDATE, rs -> {
            Asistencia asistencia = new Asistencia();
            asistencia.setId(rs.getString("asistencia_id"));
            asistencia.setSesionId(rs.getString("sesion_id"));
            asistencia.setEstado(com.neurocenter.model.EstadoAsistencia.valueOf(rs.getString("estado")));
            asistencia.setFechaEfectiva(Fechas.leerFecha(rs.getString("fecha_efectiva")));
            asistencia.setNotaEvolucion(rs.getString("nota_evolucion"));
            return asistencia;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("ASI");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, Asistencia entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, Asistencia entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   asistencia
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, Asistencia entidad) throws SQLException {
        statement.setString(inicio, entidad.getSesionId());
        statement.setString(inicio + 1, entidad.getEstado().name());
        statement.setString(inicio + 2, Fechas.guardarFecha(entidad.getFechaEfectiva()));
        statement.setString(inicio + 3, entidad.getNotaEvolucion());
    }

    /**
     * Busca la asistencia de una sesion.
     *
     * @param sesionId id de la sesion
     * @return asistencia encontrada
     * @throws PersistenciaException si falla la consulta
     */
    public java.util.Optional<Asistencia> porSesion(String sesionId) throws PersistenciaException {
        return consultarUno("SELECT * FROM asistencia WHERE sesion_id = ?", getMapper(), sesionId);
    }
}
