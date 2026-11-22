package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.EvaluacionProgreso;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de {@link EvaluacionProgreso}.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class EvaluacionRepository extends AbstractJdbcRepository<EvaluacionProgreso, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO evaluacion_progreso (evaluacion_id, plan_id, terapeuta_id, fecha, "
                    + "escala_funcional, observaciones) VALUES (?,?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE evaluacion_progreso SET plan_id=?, terapeuta_id=?, fecha=?, escala_funcional=?, "
                    + "observaciones=? WHERE evaluacion_id=?";

    /** Crea el repositorio. */
    public EvaluacionRepository() {
        super("evaluacion_progreso", "evaluacion_id", INSERT, UPDATE, rs -> {
            EvaluacionProgreso evaluacion = new EvaluacionProgreso();
            evaluacion.setId(rs.getString("evaluacion_id"));
            evaluacion.setPlanId(rs.getString("plan_id"));
            evaluacion.setTerapeutaId(rs.getString("terapeuta_id"));
            evaluacion.setFecha(Fechas.leerFecha(rs.getString("fecha")));
            evaluacion.setEscalaFuncional(rs.getInt("escala_funcional"));
            evaluacion.setObservaciones(rs.getString("observaciones"));
            return evaluacion;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("EVA");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, EvaluacionProgreso entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, EvaluacionProgreso entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   evaluacion
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, EvaluacionProgreso entidad)
            throws SQLException {
        statement.setString(inicio, entidad.getPlanId());
        statement.setString(inicio + 1, entidad.getTerapeutaId());
        statement.setString(inicio + 2, Fechas.guardarFecha(entidad.getFecha()));
        statement.setInt(inicio + 3, entidad.getEscalaFuncional());
        statement.setString(inicio + 4, entidad.getObservaciones());
    }

    /**
     * Lista evaluaciones de un plan.
     *
     * @param planId id del plan
     * @return evaluaciones ordenadas por fecha
     * @throws PersistenciaException si falla la consulta
     */
    public List<EvaluacionProgreso> porPlan(String planId) throws PersistenciaException {
        return consultar("SELECT * FROM evaluacion_progreso WHERE plan_id = ? ORDER BY fecha",
                getMapper(), planId);
    }

    /**
     * Devuelve la ultima evaluacion de un plan.
     *
     * @param planId id del plan
     * @return ultima evaluacion, si existe
     * @throws PersistenciaException si falla la consulta
     */
    public java.util.Optional<EvaluacionProgreso> ultima(String planId) throws PersistenciaException {
        return consultarUno("SELECT * FROM evaluacion_progreso WHERE plan_id = ? ORDER BY fecha DESC LIMIT 1",
                getMapper(), planId);
    }
}
