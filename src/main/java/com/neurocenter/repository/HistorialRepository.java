package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.HistorialClinico;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de historias clinicas.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class HistorialRepository extends AbstractJdbcRepository<HistorialClinico, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO historial_clinico (historial_id, paciente_id, sesiones_programadas, "
                    + "diagnostico_interno, diagnostico_externo, adjunto, fecha_registro) VALUES (?,?,?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE historial_clinico SET paciente_id=?, sesiones_programadas=?, diagnostico_interno=?, "
                    + "diagnostico_externo=?, adjunto=?, fecha_registro=? WHERE historial_id=?";

    /** Crea el repositorio. */
    public HistorialRepository() {
        super("historial_clinico", "historial_id", INSERT, UPDATE, rs -> {
            HistorialClinico historial = new HistorialClinico();
            historial.setId(rs.getString("historial_id"));
            historial.setPacienteId(rs.getString("paciente_id"));
            historial.setSesionesProgramadas(rs.getInt("sesiones_programadas"));
            historial.setDiagnosticoInterno(rs.getString("diagnostico_interno"));
            historial.setDiagnosticoExterno(rs.getString("diagnostico_externo"));
            historial.setAdjunto(rs.getString("adjunto"));
            historial.setFechaRegistro(Fechas.leerFecha(rs.getString("fecha_registro")));
            return historial;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("HIS");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, HistorialClinico entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, HistorialClinico entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   historial
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, HistorialClinico entidad)
            throws SQLException {
        statement.setString(inicio, entidad.getPacienteId());
        statement.setInt(inicio + 1, entidad.getSesionesProgramadas());
        statement.setString(inicio + 2, entidad.getDiagnosticoInterno());
        statement.setString(inicio + 3, entidad.getDiagnosticoExterno());
        statement.setString(inicio + 4, entidad.getAdjunto());
        statement.setString(inicio + 5, Fechas.guardarFecha(entidad.getFechaRegistro()));
    }

    /**
     * Busca la historia clinica de un paciente.
     *
     * @param pacienteId id del paciente
     * @return historia clinica, si existe
     * @throws PersistenciaException si falla la consulta
     */
    public Optional<HistorialClinico> porPaciente(String pacienteId) throws PersistenciaException {
        return consultarUno("SELECT * FROM historial_clinico WHERE paciente_id = ?", getMapper(), pacienteId);
    }
}
