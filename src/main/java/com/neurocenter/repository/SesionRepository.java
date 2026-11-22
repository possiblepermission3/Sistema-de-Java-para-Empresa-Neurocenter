package com.neurocenter.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.neurocenter.config.DatabaseConfig;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.EstadoAsistencia;
import com.neurocenter.model.EstadoSesion;
import com.neurocenter.model.ServicioTerapia;
import com.neurocenter.model.SesionFactory;
import com.neurocenter.model.TerapiaFisica;
import com.neurocenter.model.TerapiaLenguaje;
import com.neurocenter.model.TerapiaOcupacional;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de sesiones. Implementa herencia single-table mediante la
 * columna discriminadora {@code tipo} y fabrica la subclase adecuada al leer.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class SesionRepository extends AbstractJdbcRepository<ServicioTerapia, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO sesion (sesion_id, tipo, paciente_id, terapeuta_id, plan_id, fecha_hora, "
                    + "duracion_minutos, costo, estado, notas, area_tratada, actividad_adaptada, "
                    + "objetivo_lenguaje, created_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE sesion SET tipo=?, paciente_id=?, terapeuta_id=?, plan_id=?, fecha_hora=?, "
                    + "duracion_minutos=?, costo=?, estado=?, notas=?, area_tratada=?, "
                    + "actividad_adaptada=?, objetivo_lenguaje=?, created_at=? WHERE sesion_id=?";

    /** Crea el repositorio. */
    public SesionRepository() {
        super("sesion", "sesion_id", INSERT, UPDATE, rs -> {
            Especialidad tipo = Especialidad.valueOf(rs.getString("tipo"));
            ServicioTerapia sesion = SesionFactory.crear(tipo);
            sesion.setId(rs.getString("sesion_id"));
            sesion.setPacienteId(rs.getString("paciente_id"));
            sesion.setTerapeutaId(rs.getString("terapeuta_id"));
            sesion.setPlanId(rs.getString("plan_id"));
            sesion.setFechaHora(Fechas.leer(rs.getString("fecha_hora")));
            sesion.setDuracionMinutos(rs.getInt("duracion_minutos"));
            sesion.setCosto(rs.getDouble("costo"));
            sesion.setEstado(EstadoSesion.valueOf(rs.getString("estado")));
            sesion.setNotas(rs.getString("notas"));
            sesion.setCreatedAt(Fechas.leer(rs.getString("created_at")));
            if (sesion instanceof TerapiaFisica fisica) {
                fisica.setAreaTratada(rs.getString("area_tratada"));
            } else if (sesion instanceof TerapiaOcupacional ocupacional) {
                ocupacional.setActividadAdaptada(rs.getString("actividad_adaptada"));
            } else if (sesion instanceof TerapiaLenguaje lenguaje) {
                lenguaje.setObjetivoLenguaje(rs.getString("objetivo_lenguaje"));
            }
            return sesion;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("SES");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, ServicioTerapia entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, ServicioTerapia entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los trece campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   sesion
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, ServicioTerapia entidad)
            throws SQLException {
        statement.setString(inicio, entidad.getEspecialidad().name());
        statement.setString(inicio + 1, entidad.getPacienteId());
        statement.setString(inicio + 2, entidad.getTerapeutaId());
        statement.setString(inicio + 3, entidad.getPlanId());
        statement.setString(inicio + 4, Fechas.guardar(entidad.getFechaHora()));
        statement.setInt(inicio + 5, entidad.getDuracionMinutos());
        statement.setDouble(inicio + 6, entidad.getCosto());
        statement.setString(inicio + 7, entidad.getEstado().name());
        statement.setString(inicio + 8, entidad.getNotas());
        statement.setString(inicio + 9, detalle(entidad, Especialidad.FISICA));
        statement.setString(inicio + 10, detalle(entidad, Especialidad.OCUPACIONAL));
        statement.setString(inicio + 11, detalle(entidad, Especialidad.LENGUAJE));
        statement.setString(inicio + 12, Fechas.guardar(entidad.getCreatedAt()));
    }

    /**
     * Devuelve el detalle especifico solo si coincide con la especialidad.
     *
     * @param sesion       sesion
     * @param especialidad especialidad del detalle
     * @return detalle o {@code null}
     */
    private String detalle(ServicioTerapia sesion, Especialidad especialidad) {
        return sesion.getEspecialidad() == especialidad ? sesion.getDetalleEspecifico() : null;
    }

    /**
     * Lista sesiones de un terapeuta en un rango de fechas.
     *
     * @param terapeutaId id del terapeuta
     * @param inicio      inicio del rango
     * @param fin         fin del rango
     * @return sesiones ordenadas por fecha
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> porTerapeutaYRango(String terapeutaId, LocalDateTime inicio, LocalDateTime fin)
            throws PersistenciaException {
        String sql = "SELECT * FROM sesion WHERE terapeuta_id = ? AND estado IN ('PROGRAMADA','COMPLETADA') "
                + "AND fecha_hora >= ? AND fecha_hora < ? ORDER BY fecha_hora";
        return consultar(sql, getMapper(), terapeutaId, Fechas.guardar(inicio), Fechas.guardar(fin));
    }

    /**
     * Lista sesiones de un paciente en un rango de fechas.
     *
     * @param pacienteId id del paciente
     * @param inicio     inicio del rango
     * @param fin        fin del rango
     * @return sesiones
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> porPacienteYRango(String pacienteId, LocalDateTime inicio, LocalDateTime fin)
            throws PersistenciaException {
        String sql = "SELECT * FROM sesion WHERE paciente_id = ? AND estado IN ('PROGRAMADA','COMPLETADA') "
                + "AND fecha_hora >= ? AND fecha_hora < ? ORDER BY fecha_hora";
        return consultar(sql, getMapper(), pacienteId, Fechas.guardar(inicio), Fechas.guardar(fin));
    }

    /**
     * Lista las sesiones mas recientes.
     *
     * @param limite cantidad maxima
     * @return sesiones recientes
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> recientes(int limite) throws PersistenciaException {
        return consultar("SELECT * FROM sesion ORDER BY fecha_hora DESC LIMIT ?", getMapper(), limite);
    }

    /**
     * Lista las proximas sesiones programadas.
     *
     * @param desde fecha de referencia
     * @return sesiones futuras
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> proximas(LocalDateTime desde) throws PersistenciaException {
        String sql = "SELECT * FROM sesion WHERE estado = 'PROGRAMADA' AND fecha_hora >= ? ORDER BY fecha_hora";
        return consultar(sql, getMapper(), Fechas.guardar(desde));
    }

    /**
     * Lista todas las sesiones de un plan.
     *
     * @param planId id del plan
     * @return sesiones del plan
     * @throws PersistenciaException si falla la consulta
     */
    public List<ServicioTerapia> porPlan(String planId) throws PersistenciaException {
        return consultar("SELECT * FROM sesion WHERE plan_id = ? ORDER BY fecha_hora", getMapper(), planId);
    }

    /**
     * Cuenta las sesiones completadas de un plan.
     *
     * @param planId id del plan
     * @return cantidad de sesiones completadas
     * @throws PersistenciaException si falla la consulta
     */
    public long contarCompletadas(String planId) throws PersistenciaException {
        return consultarLong("SELECT COUNT(*) FROM sesion WHERE plan_id = ? AND estado = 'COMPLETADA'", planId);
    }

    /**
     * Cambia el estado de una sesion.
     *
     * @param id     id de la sesion
     * @param estado nuevo estado
     * @return filas afectadas
     * @throws PersistenciaException si falla la actualizacion
     */
    public int cambiarEstado(String id, EstadoSesion estado) throws PersistenciaException {
        return ejecutar("UPDATE sesion SET estado = ? WHERE sesion_id = ?",
                statement -> {
                    statement.setString(1, estado.name());
                    statement.setString(2, id);
                });
    }

    /**
     * Registra la finalizacion de una sesion y su asistencia en una sola
     * transaccion, garantizando integridad entre ambas tablas.
     *
     * @param sesionId       id de la sesion
     * @param notaEvolucion  nota clinica
     * @param estadoAsistencia estado de asistencia
     * @throws PersistenciaException si falla la transaccion
     */
    public void completarConAsistencia(String sesionId, String notaEvolucion,
                                       EstadoAsistencia estadoAsistencia) throws PersistenciaException {
        Connection conexion = null;
        try {
            conexion = DatabaseConfig.getConnection();
            conexion.setAutoCommit(false);
            try (PreparedStatement actualizar = conexion.prepareStatement(
                    "UPDATE sesion SET estado = ?, notas = ? WHERE sesion_id = ?")) {
                actualizar.setString(1, EstadoSesion.COMPLETADA.name());
                actualizar.setString(2, notaEvolucion);
                actualizar.setString(3, sesionId);
                actualizar.executeUpdate();
            }
            try (PreparedStatement borrar = conexion.prepareStatement(
                    "DELETE FROM asistencia WHERE sesion_id = ?")) {
                borrar.setString(1, sesionId);
                borrar.executeUpdate();
            }
            try (PreparedStatement insertar = conexion.prepareStatement(
                    "INSERT INTO asistencia (asistencia_id, sesion_id, estado, fecha_efectiva, nota_evolucion) "
                            + "VALUES (?,?,?,?,?)")) {
                insertar.setString(1, GeneradorIds.generar("ASI"));
                insertar.setString(2, sesionId);
                insertar.setString(3, estadoAsistencia.name());
                insertar.setString(4, Fechas.guardarFecha(LocalDate.now()));
                insertar.setString(5, notaEvolucion);
                insertar.executeUpdate();
            }
            conexion.commit();
        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ignorado) {
                    // El cierre de la conexion tambien deshace la transaccion pendiente.
                }
            }
            throw new PersistenciaException("No se pudo completar la sesion " + sesionId, e);
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException ignorado) {
                    // No hay accion adicional: los recursos ya fueron liberados.
                }
            }
        }
    }
}
