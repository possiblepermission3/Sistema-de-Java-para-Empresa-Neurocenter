package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.Terapeuta;
import com.neurocenter.model.Turno;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de {@link Terapeuta}.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class TerapeutaRepository extends AbstractJdbcRepository<Terapeuta, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO terapeuta (terapeuta_id, nombre, apellido, dni, telefono, edad, turno, "
                    + "foto, especialidad, tarifa_hora, activo) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE terapeuta SET nombre=?, apellido=?, dni=?, telefono=?, edad=?, turno=?, "
                    + "foto=?, especialidad=?, tarifa_hora=?, activo=? WHERE terapeuta_id=?";

    /** Crea el repositorio. */
    public TerapeutaRepository() {
        super("terapeuta", "terapeuta_id", INSERT, UPDATE, rs -> {
            Terapeuta terapeuta = new Terapeuta();
            terapeuta.setId(rs.getString("terapeuta_id"));
            terapeuta.setNombre(rs.getString("nombre"));
            terapeuta.setApellido(rs.getString("apellido"));
            terapeuta.setDni(rs.getString("dni"));
            terapeuta.setTelefono(rs.getString("telefono"));
            terapeuta.setEdad(rs.getInt("edad"));
            terapeuta.setTurno(Turno.valueOf(rs.getString("turno")));
            terapeuta.setFoto(rs.getString("foto"));
            terapeuta.setEspecialidad(Especialidad.valueOf(rs.getString("especialidad")));
            terapeuta.setTarifaHora(rs.getDouble("tarifa_hora"));
            terapeuta.setActivo(rs.getBoolean("activo"));
            return terapeuta;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("TER");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, Terapeuta entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, Terapeuta entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los diez campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   terapeuta
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, Terapeuta entidad) throws SQLException {
        statement.setString(inicio, entidad.getNombre());
        statement.setString(inicio + 1, entidad.getApellido());
        statement.setString(inicio + 2, entidad.getDni());
        statement.setString(inicio + 3, entidad.getTelefono());
        statement.setInt(inicio + 4, entidad.getEdad());
        statement.setString(inicio + 5, entidad.getTurno() == null ? "MANANA" : entidad.getTurno().name());
        statement.setString(inicio + 6, entidad.getFoto());
        statement.setString(inicio + 7, entidad.getEspecialidad() == null ? "FISICA" : entidad.getEspecialidad().name());
        statement.setDouble(inicio + 8, entidad.getTarifaHora());
        statement.setBoolean(inicio + 9, entidad.isActivo());
    }

    /**
     * Lista terapeutas activos de una especialidad.
     *
     * @param especialidad especialidad filtrada
     * @return lista de terapeutas
     * @throws PersistenciaException si falla la consulta
     */
    public List<Terapeuta> buscarPorEspecialidad(Especialidad especialidad) throws PersistenciaException {
        String sql = "SELECT * FROM terapeuta WHERE activo = 1 AND especialidad = ? ORDER BY nombre";
        return consultar(sql, getMapper(), especialidad.name());
    }

    /**
     * Busca un terapeuta por DNI.
     *
     * @param dni        DNI
     * @param idExcluido id a excluir
     * @return terapeuta encontrado
     * @throws PersistenciaException si falla la consulta
     */
    public java.util.Optional<Terapeuta> buscarPorDni(String dni, String idExcluido)
            throws PersistenciaException {
        return consultarUno("SELECT * FROM terapeuta WHERE dni = ? AND (? IS NULL OR terapeuta_id <> ?)",
                getMapper(), dni, idExcluido, idExcluido);
    }
}
