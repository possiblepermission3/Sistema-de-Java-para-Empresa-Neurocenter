package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Horario;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de horarios de atencion.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class HorarioRepository extends AbstractJdbcRepository<Horario, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO horario (horario_id, terapeuta_id, dia_semana, hora_inicio, hora_fin) "
                    + "VALUES (?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE horario SET terapeuta_id=?, dia_semana=?, hora_inicio=?, hora_fin=? WHERE horario_id=?";

    /** Crea el repositorio. */
    public HorarioRepository() {
        super("horario", "horario_id", INSERT, UPDATE, rs -> {
            Horario horario = new Horario();
            horario.setId(rs.getString("horario_id"));
            horario.setTerapeutaId(rs.getString("terapeuta_id"));
            horario.setDiaSemana(rs.getString("dia_semana"));
            horario.setHoraInicio(java.time.LocalTime.parse(rs.getString("hora_inicio")));
            horario.setHoraFin(java.time.LocalTime.parse(rs.getString("hora_fin")));
            return horario;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("HOR");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, Horario entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, Horario entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   horario
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, Horario entidad) throws SQLException {
        statement.setString(inicio, entidad.getTerapeutaId());
        statement.setString(inicio + 1, entidad.getDiaSemana());
        statement.setString(inicio + 2, Fechas.HORA.format(entidad.getHoraInicio()));
        statement.setString(inicio + 3, Fechas.HORA.format(entidad.getHoraFin()));
    }

    /**
     * Lista los horarios de un terapeuta.
     *
     * @param terapeutaId id del terapeuta
     * @return horarios
     * @throws PersistenciaException si falla la consulta
     */
    public List<Horario> porTerapeuta(String terapeutaId) throws PersistenciaException {
        return consultar("SELECT * FROM horario WHERE terapeuta_id = ? ORDER BY dia_semana, hora_inicio",
                getMapper(), terapeutaId);
    }
}
