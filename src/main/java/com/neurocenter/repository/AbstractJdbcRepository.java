package com.neurocenter.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.neurocenter.config.DatabaseConfig;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Entidad;

/**
 * Repositorio JDBC generico que implementa las operaciones CRUD comunes
 * mediante el patron Template Method. Las subclases solo definen sus
 * sentencias SQL, el mapeo de filas y la forma de enlazar parametros.
 *
 * <p>Convencion de parametros: en INSERT el identificador es el marcador 1 y
 * las subclases enlazan a partir del 2; en UPDATE las subclases enlazan desde
 * el 1 y el identificador del WHERE se enlaza automaticamente al final.</p>
 *
 * @param <T>  entidad persistente
 * @param <ID> tipo del identificador
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public abstract class AbstractJdbcRepository<T extends Entidad<ID>, ID> implements CrudRepository<T, ID> {

    /** Nombre de la tabla. */
    private final String tabla;
    /** Columna de clave primaria. */
    private final String columnaId;
    /** Sentencia de insercion. */
    private final String sqlInsert;
    /** Sentencia de actualizacion. */
    private final String sqlUpdate;
    /** Mapeador de filas. */
    private final RowMapper<T> mapper;

    /**
     * Constructor del repositorio generico.
     *
     * @param tabla      nombre de la tabla
     * @param columnaId  columna de clave primaria
     * @param sqlInsert  SQL de insercion (primer parametro = id)
     * @param sqlUpdate  SQL de actualizacion (ultimo parametro = id)
     * @param mapper     mapeador de ResultSet a entidad
     */
    protected AbstractJdbcRepository(String tabla, String columnaId, String sqlInsert,
                                     String sqlUpdate, RowMapper<T> mapper) {
        this.tabla = tabla;
        this.columnaId = columnaId;
        this.sqlInsert = sqlInsert;
        this.sqlUpdate = sqlUpdate;
        this.mapper = mapper;
    }

    /**
     * Genera el identificador de una entidad nueva.
     *
     * @return identificador con el prefijo del negocio
     */
    protected abstract ID generarId();

    /**
     * Enlaza los parametros del INSERT (desde el indice 2).
     *
     * @param statement sentencia preparada
     * @param entidad   entidad a persistir
     * @throws SQLException si falla el enlazado
     */
    protected abstract void enlazarInsert(PreparedStatement statement, T entidad) throws SQLException;

    /**
     * Enlaza los parametros del UPDATE (desde el indice 1).
     *
     * @param statement sentencia preparada
     * @param entidad   entidad a actualizar
     * @throws SQLException si falla el enlazado
     */
    protected abstract void enlazarUpdate(PreparedStatement statement, T entidad) throws SQLException;

    @Override
    public T guardar(T entidad) throws PersistenciaException {
        if (entidad.getId() == null) {
            ID nuevoId = generarId();
            entidad.setId(nuevoId);
            ejecutar(sqlInsert, statement -> {
                statement.setObject(1, nuevoId);
                enlazarInsert(statement, entidad);
            });
        } else {
            actualizar(entidad);
        }
        return entidad;
    }

    @Override
    public boolean actualizar(T entidad) throws PersistenciaException {
        if (entidad.getId() == null) {
            return false;
        }
        int indiceId = contarMarcadores(sqlUpdate);
        int afectadas = ejecutar(sqlUpdate, statement -> {
            enlazarUpdate(statement, entidad);
            statement.setObject(indiceId, entidad.getId());
        });
        return afectadas > 0;
    }

    @Override
    public Optional<T> buscarPorId(ID id) throws PersistenciaException {
        String sql = "SELECT * FROM " + tabla + " WHERE " + columnaId + " = ?";
        return consultarUno(sql, mapper, id);
    }

    @Override
    public List<T> listarTodos() throws PersistenciaException {
        return consultar("SELECT * FROM " + tabla + " ORDER BY " + columnaId, mapper);
    }

    @Override
    public boolean eliminar(ID id) throws PersistenciaException {
        String sql = "DELETE FROM " + tabla + " WHERE " + columnaId + " = ?";
        return ejecutar(sql, statement -> statement.setObject(1, id)) > 0;
    }

    @Override
    public long contar() throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM " + tabla;
        try (Connection conexion = DatabaseConfig.getConnection();
             Statement statement = conexion.createStatement();
             ResultSet resultado = statement.executeQuery(sql)) {
            return resultado.next() ? resultado.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new PersistenciaException("Error contando registros de " + tabla, e);
        }
    }

    /**
     * Ejecuta una sentencia de insercion, actualizacion o borrado.
     *
     * @param sql          sentencia SQL
     * @param preparador   callback que enlaza parametros
     * @return numero de filas afectadas
     * @throws PersistenciaException si falla la ejecucion
     */
    protected int ejecutar(String sql, Preparador preparador) throws PersistenciaException {
        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            preparador.preparar(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error ejecutando SQL sobre " + tabla, e);
        }
    }

    /**
     * Ejecuta una consulta y mapea todas las filas.
     *
     * @param sql     consulta SQL
     * @param mapeador mapeador de filas
     * @param params  parametros posicionales
     * @param <R>     tipo resultante
     * @return lista de resultados (nunca nula)
     * @throws PersistenciaException si falla la consulta
     */
    protected <R> List<R> consultar(String sql, RowMapper<R> mapeador, Object... params)
            throws PersistenciaException {
        List<R> resultados = new ArrayList<>();
        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            enlazarParametros(statement, params);
            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    resultados.add(mapeador.map(resultado));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error consultando " + tabla, e);
        }
        return resultados;
    }

    /**
     * Ejecuta una consulta y devuelve como maximo un resultado.
     *
     * @param sql      consulta SQL
     * @param mapeador mapeador de filas
     * @param params   parametros posicionales
     * @param <R>      tipo resultante
     * @return primer resultado o vacio
     * @throws PersistenciaException si falla la consulta
     */
    protected <R> Optional<R> consultarUno(String sql, RowMapper<R> mapeador, Object... params)
            throws PersistenciaException {
        return consultar(sql, mapeador, params).stream().findFirst();
    }

    /**
     * Ejecuta una consulta escalar de tipo long.
     *
     * @param sql    consulta SQL
     * @param params parametros posicionales
     * @return valor escalar
     * @throws PersistenciaException si falla la consulta
     */
    protected long consultarLong(String sql, Object... params) throws PersistenciaException {
        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            enlazarParametros(statement, params);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? resultado.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error en consulta escalar sobre " + tabla, e);
        }
    }

    /** @return nombre de la tabla */
    protected String getTabla() {
        return tabla;
    }

    /** @return mapeador de filas */
    protected RowMapper<T> getMapper() {
        return mapper;
    }

    /**
     * Enlaza parametros posicionales a una sentencia preparada.
     *
     * @param statement sentencia
     * @param params    valores
     * @throws SQLException si falla el enlazado
     */
    private void enlazarParametros(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    /**
     * Cuenta los marcadores '?' de una sentencia.
     *
     * @param sql sentencia SQL
     * @return numero de marcadores
     */
    private int contarMarcadores(String sql) {
        return (int) sql.chars().filter(caracter -> caracter == '?').count();
    }

    /**
     * Callback para preparar sentencias JDBC.
     */
    @FunctionalInterface
    protected interface Preparador {
        /**
         * Enlaza parametros en la sentencia.
         *
         * @param statement sentencia preparada
         * @throws SQLException si falla el enlazado
         */
        void preparar(PreparedStatement statement) throws SQLException;
    }
}
