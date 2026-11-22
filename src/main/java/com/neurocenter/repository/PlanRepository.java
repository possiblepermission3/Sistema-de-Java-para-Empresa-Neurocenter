package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Especialidad;
import com.neurocenter.model.PlanRehabilitacion;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de {@link PlanRehabilitacion}.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PlanRepository extends AbstractJdbcRepository<PlanRehabilitacion, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO plan_rehabilitacion (plan_id, paciente_id, terapeuta_id, especialidad, "
                    + "sesiones_totales, fecha_inicio, fecha_fin, objetivo, activo) VALUES (?,?,?,?,?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE plan_rehabilitacion SET paciente_id=?, terapeuta_id=?, especialidad=?, "
                    + "sesiones_totales=?, fecha_inicio=?, fecha_fin=?, objetivo=?, activo=? WHERE plan_id=?";

    /** Crea el repositorio. */
    public PlanRepository() {
        super("plan_rehabilitacion", "plan_id", INSERT, UPDATE, rs -> {
            PlanRehabilitacion plan = new PlanRehabilitacion();
            plan.setId(rs.getString("plan_id"));
            plan.setPacienteId(rs.getString("paciente_id"));
            plan.setTerapeutaId(rs.getString("terapeuta_id"));
            plan.setEspecialidad(Especialidad.valueOf(rs.getString("especialidad")));
            plan.setSesionesTotales(rs.getInt("sesiones_totales"));
            plan.setFechaInicio(Fechas.leerFecha(rs.getString("fecha_inicio")));
            plan.setFechaFin(Fechas.leerFecha(rs.getString("fecha_fin")));
            plan.setObjetivo(rs.getString("objetivo"));
            plan.setActivo(rs.getBoolean("activo"));
            return plan;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("PLAN");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, PlanRehabilitacion entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, PlanRehabilitacion entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   plan
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, PlanRehabilitacion entidad)
            throws SQLException {
        statement.setString(inicio, entidad.getPacienteId());
        statement.setString(inicio + 1, entidad.getTerapeutaId());
        statement.setString(inicio + 2, entidad.getEspecialidad().name());
        statement.setInt(inicio + 3, entidad.getSesionesTotales());
        statement.setString(inicio + 4, Fechas.guardarFecha(entidad.getFechaInicio()));
        statement.setString(inicio + 5, Fechas.guardarFecha(entidad.getFechaFin()));
        statement.setString(inicio + 6, entidad.getObjetivo());
        statement.setBoolean(inicio + 7, entidad.isActivo());
    }

    /**
     * Lista los planes activos ordenados por inicio.
     *
     * @return planes activos
     * @throws PersistenciaException si falla la consulta
     */
    public List<PlanRehabilitacion> listarActivos() throws PersistenciaException {
        return consultar("SELECT * FROM plan_rehabilitacion WHERE activo = 1 ORDER BY fecha_inicio", getMapper());
    }

    /**
     * Lista los planes de un paciente.
     *
     * @param pacienteId id del paciente
     * @return planes
     * @throws PersistenciaException si falla la consulta
     */
    public List<PlanRehabilitacion> porPaciente(String pacienteId) throws PersistenciaException {
        return consultar("SELECT * FROM plan_rehabilitacion WHERE paciente_id = ? ORDER BY fecha_inicio DESC",
                getMapper(), pacienteId);
    }
}
