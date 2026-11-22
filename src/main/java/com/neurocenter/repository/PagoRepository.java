package com.neurocenter.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.MetodoPago;
import com.neurocenter.model.Pago;
import com.neurocenter.model.TipoPago;
import com.neurocenter.util.Fechas;
import com.neurocenter.util.GeneradorIds;

/**
 * Repositorio JDBC de {@link Pago}.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class PagoRepository extends AbstractJdbcRepository<Pago, String> {

    /** SQL de insercion. */
    private static final String INSERT =
            "INSERT INTO pago (pago_id, paciente_id, sesion_id, fecha_pago, monto, tipo_pago, "
                    + "metodo_pago, numero_operacion, sesiones_paquete) VALUES (?,?,?,?,?,?,?,?,?)";
    /** SQL de actualizacion. */
    private static final String UPDATE =
            "UPDATE pago SET paciente_id=?, sesion_id=?, fecha_pago=?, monto=?, tipo_pago=?, "
                    + "metodo_pago=?, numero_operacion=?, sesiones_paquete=? WHERE pago_id=?";

    /** Crea el repositorio. */
    public PagoRepository() {
        super("pago", "pago_id", INSERT, UPDATE, rs -> {
            Pago pago = new Pago();
            pago.setId(rs.getString("pago_id"));
            pago.setPacienteId(rs.getString("paciente_id"));
            pago.setSesionId(rs.getString("sesion_id"));
            pago.setFechaPago(Fechas.leerFecha(rs.getString("fecha_pago")));
            pago.setMonto(rs.getDouble("monto"));
            pago.setTipoPago(TipoPago.valueOf(rs.getString("tipo_pago")));
            pago.setMetodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")));
            pago.setNumeroOperacion(rs.getString("numero_operacion"));
            pago.setSesionesPaquete(rs.getInt("sesiones_paquete"));
            return pago;
        });
    }

    @Override
    protected String generarId() {
        return GeneradorIds.generar("PAG");
    }

    @Override
    protected void enlazarInsert(PreparedStatement statement, Pago entidad) throws SQLException {
        enlazarDatos(statement, 2, entidad);
    }

    @Override
    protected void enlazarUpdate(PreparedStatement statement, Pago entidad) throws SQLException {
        enlazarDatos(statement, 1, entidad);
    }

    /**
     * Enlaza los campos no identificador.
     *
     * @param statement sentencia
     * @param inicio    primer indice
     * @param entidad   pago
     * @throws SQLException si falla el enlazado
     */
    private void enlazarDatos(PreparedStatement statement, int inicio, Pago entidad) throws SQLException {
        statement.setString(inicio, entidad.getPacienteId());
        statement.setString(inicio + 1, entidad.getSesionId());
        statement.setString(inicio + 2, Fechas.guardarFecha(entidad.getFechaPago()));
        statement.setDouble(inicio + 3, entidad.getMonto());
        statement.setString(inicio + 4, entidad.getTipoPago().name());
        statement.setString(inicio + 5, entidad.getMetodoPago().name());
        statement.setString(inicio + 6, entidad.getNumeroOperacion());
        statement.setInt(inicio + 7, entidad.getSesionesPaquete());
    }

    /**
     * Lista los pagos de un paciente.
     *
     * @param pacienteId id del paciente
     * @return pagos
     * @throws PersistenciaException si falla la consulta
     */
    public List<Pago> porPaciente(String pacienteId) throws PersistenciaException {
        return consultar("SELECT * FROM pago WHERE paciente_id = ? ORDER BY fecha_pago DESC",
                getMapper(), pacienteId);
    }
}
