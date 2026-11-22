package com.neurocenter.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.neurocenter.config.DatabaseConfig;
import com.neurocenter.exception.PersistenciaException;

/**
 * Inicializador automatico de la base de datos.
 * <p>Ejecuta {@code schema.sql} (creacion de tablas e indices) y, solo si la
 * tabla de pacientes esta vacia, {@code seed.sql} (datos de prueba). Ambos
 * scripts se leen como recursos del classpath, por lo que la aplicacion funciona
 * desde Maven, desde un JAR o desde una compilacion manual con javac.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class DatabaseInitializer {

    /** Script de estructura. */
    private static final String SCHEMA = "/db/schema.sql";
    /** Script de datos iniciales. */
    private static final String SEED = "/db/seed.sql";

    /** Constructor privado: clase utilitaria. */
    private DatabaseInitializer() {
    }

    /**
     * Prepara la base de datos para su uso. Metodo idempotente.
     *
     * @throws PersistenciaException si falta un script o falla su ejecucion
     */
    public static void inicializar() throws PersistenciaException {
        ejecutarScript(SCHEMA);
        try {
            if (DatabaseConfig.contarFilas("paciente") == 0) {
                ejecutarScript(SEED);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo verificar el estado de la base de datos", e);
        }
    }

    /**
     * Ejecuta un script SQL dentro de una transaccion unica.
     *
     * @param recurso ruta del recurso en el classpath
     * @throws PersistenciaException si el recurso no existe o falla la ejecucion
     */
    private static void ejecutarScript(String recurso) throws PersistenciaException {
        String contenido = leerRecurso(recurso);
        try (Connection conexion = DatabaseConfig.getConnection()) {
            boolean anterior = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try (Statement statement = conexion.createStatement()) {
                for (String sentencia : contenido.split(";")) {
                    String sql = quitarComentarios(sentencia);
                    if (!sql.isEmpty()) {
                        statement.execute(sql);
                    }
                }
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw new PersistenciaException("Error ejecutando el script " + recurso, e);
            } finally {
                conexion.setAutoCommit(anterior);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo abrir la conexion para " + recurso, e);
        }
    }

    /**
     * Lee un recurso de texto del classpath.
     *
     * @param recurso ruta absoluta del recurso
     * @return contenido del archivo
     * @throws PersistenciaException si no existe o no se puede leer
     */
    private static String leerRecurso(String recurso) throws PersistenciaException {
        try (InputStream flujo = DatabaseInitializer.class.getResourceAsStream(recurso)) {
            if (flujo == null) {
                throw new PersistenciaException("No se encontro el recurso SQL: " + recurso);
            }
            StringBuilder contenido = new StringBuilder();
            try (BufferedReader lector = new BufferedReader(new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    contenido.append(linea).append('\n');
                }
            }
            return contenido.toString();
        } catch (IOException e) {
            throw new PersistenciaException("Error leyendo el recurso SQL: " + recurso, e);
        }
    }

    /**
     * Elimina lineas de comentario SQL y espacios sobrantes.
     *
     * @param bloque fragmento de script
     * @return sentencia limpia, o cadena vacia si solo habia comentarios
     */
    private static String quitarComentarios(String bloque) {
        StringBuilder limpio = new StringBuilder();
        for (String linea : bloque.split("\\R")) {
            String texto = linea.trim();
            if (!texto.isEmpty() && !texto.startsWith("--") && !texto.startsWith("/*")) {
                limpio.append(texto).append(' ');
            }
        }
        return limpio.toString().trim();
    }
}
