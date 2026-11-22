package com.neurocenter.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fabrica de conexiones JDBC hacia la base de datos SQLite embebida.
 * <p>Cada llamada devuelve una conexion nueva. Se activa explicitamente el
 * soporte de claves foraneas, que SQLite deshabilita por defecto, para
 * garantizar la integridad referencial de la informacion.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class DatabaseConfig {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("No se encontro el driver JDBC de SQLite: " + e.getMessage());
        }
    }

    /** Constructor privado: clase utilitaria. */
    private DatabaseConfig() {
    }

    /**
     * Obtiene una conexion nueva con foreign keys activadas.
     *
     * @return conexion JDBC abierta (debe ser cerrada por el llamador)
     * @throws SQLException si no se puede abrir la base de datos
     */
    public static Connection getConnection() throws SQLException {
        Connection conexion = DriverManager.getConnection(AppConfig.URL_JDBC);
        try (Statement statement = conexion.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("PRAGMA busy_timeout = 5000");
        }
        return conexion;
    }

    /**
     * Comprueba que una tabla exista y devuelve su cantidad de registros.
     *
     * @param tabla nombre de la tabla (identificador controlado internamente)
     * @return numero de filas
     * @throws SQLException si falla la consulta
     */
    public static long contarFilas(String tabla) throws SQLException {
        try (Connection conexion = getConnection();
             Statement statement = conexion.createStatement();
             var resultado = statement.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            return resultado.next() ? resultado.getLong(1) : 0L;
        }
    }
}
