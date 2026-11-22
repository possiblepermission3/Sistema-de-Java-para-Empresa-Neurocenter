package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Paciente;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de {@link Paciente}.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PacienteRepository extends AbstractJdbcRepository<Paciente, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO paciente (paciente_id, nombre, apellido, dni, edad, telefono, domicilio, "
                    + "fecha_registro, diagnostico_referido, activo) VALUES (?,?,?,?,?,?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE paciente SET nombre=?, apellido=?, dni=?, edad=?, telefono=?, domicilio=?, "
                    + "fecha_registro=?, diagnostico_referido=?, activo=? WHERE paciente_id=?";

    /** Crea el repositorio con su mapeador de filas. */
    public PacienteRepository() {
        super("paciente", "paciente_id", INSERT, UPDATE, rs -> {
            Paciente paciente = new Paciente();
            paciente.setId(rs.getString("paciente_id"));
            paciente.setNombre(rs.getString("nombre"));
            paciente.setApellido(rs.getString("apellido"));
            paciente.setDni(rs.getString("dni"));
            paciente.setEdad(rs.getInt("edad"));
            paciente.setTelefono(rs.getString("telefono"));
            paciente.setDomicilio(rs.getString("domicilio"));
            paciente.setFechaRegistro(Fechas.leerFecha(rs.getString("fecha_registro")));
            paciente.setDiagnosticoReferido(rs.getString("diagnostico_referido"));
            paciente.setActivo(rs.getBoolean("activo"));
            return paciente;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("PAC");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, Paciente entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, Paciente entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los nueve primeros campos del paciente.
     *
     * @param statement sentencia
     * @param inicio    indice del primer parametro
     * @param entidad   paciente
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, Paciente entidad) throws SQLException {
        statement.setString(inicio, entidad.getNombre());
        statement.setString(inicio + 1, entidad.getApellido());
        statement.setString(inicio + 2, entidad.getDni());
        statement.setInt(inicio + 3, entidad.getEdad());
        statement.setString(inicio + 4, entidad.getTelefono());
        statement.setString(inicio + 5, entidad.getDomicilio());
        statement.setString(inicio + 6, Fechas.guardarFecha(entidad.getFechaRegistro()));
        statement.setString(inicio + 7, entidad.getDiagnosticoReferido());
        statement.setBoolean(inicio + 8, entidad.isActivo());
    }

    /**
     * Busca un paciente por DNI, excluyendo opcionalmente un identificador.
     *
     * @param dni           DNI buscado
     * @param idExcluido    identificador a ignorar (para actualizaciones)
     * @return paciente encontrado
     * @throws PersistenciaException si falla la consulta
     */
    public java.util.Optional<Paciente> buscarPorDni(String dni, String idExcluido)
            throws PersistenciaException {
        String sql = "SELECT * FROM paciente WHERE dni = ? AND (? IS NULL OR paciente_id <> ?)";
        return consultarUno(sql, getMapper(), dni, idExcluido, idExcluido);
    }

    /**
     * Comprueba si el paciente tiene sesiones registradas.
     *
     * @param id identificador del paciente
     * @return verdadero si tiene al menos una sesion
     * @throws PersistenciaException si falla la consulta
     */
    public boolean tieneSesiones(String id) throws PersistenciaException {
        return consultarLong("SELECT COUNT(*) FROM sesion WHERE paciente_id = ?", id) > 0;
    }

    /**
     * Da de baja logica a un paciente.
     *
     * @param id identificador
     * @return filas afectadas
     * @throws PersistenciaException si falla la actualizacion
     */
    public int desactivar(String id) throws PersistenciaException {
        return ejecutar("UPDATE paciente SET activo = 0 WHERE paciente_id = ?",
                statement -> statement.setString(1, id));
    }
}
