package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Usuario;

/**
 * Repositorio JDBC de usuarios del sistema.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class UsuarioRepository extends AbstractJdbcRepository<Usuario, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO usuario (username, password_hash, rol, nombre_completo, activo) VALUES (?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE usuario SET password_hash=?, rol=?, nombre_completo=?, activo=? WHERE username=?";

    /** Crea el repositorio. */
    public UsuarioRepository() {
        super("usuario", "username", INSERT, UPDATE, rs -> {
            Usuario usuario = new Usuario();
            usuario.setId(rs.getString("username"));
            usuario.setPasswordHash(rs.getString("password_hash"));
            usuario.setRol(rs.getString("rol"));
            usuario.setNombreCompleto(rs.getString("nombre_completo"));
            usuario.setActivo(rs.getBoolean("activo"));
            return usuario;
        });
    }

    @Override
    protected String generarId() {
        throw new UnsupportedOperationException("El username del usuario debe indicarse explicitamente");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, Usuario entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, Usuario entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   usuario
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, Usuario entidad) throws SQLException {
        statement.setString(inicio, entidad.getPasswordHash());
        statement.setString(inicio + 1, entidad.getRol());
        statement.setString(inicio + 2, entidad.getNombreCompleto());
        statement.setBoolean(inicio + 3, entidad.isActivo());
    }

    /**
     * Busca un usuario por nombre de cuenta.
     *
     * @param username nombre de usuario
     * @return usuario encontrado
     * @throws PersistenciaException si falla la consulta
     */
    public Optional<Usuario> buscarPorUsername(String username) throws PersistenciaException {
        return buscarPorId(username);
    }
}
