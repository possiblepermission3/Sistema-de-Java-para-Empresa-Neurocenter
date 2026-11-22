package com.neurocenter.config;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.neurocenter.model.Especialidad;

/**
 * Configuracion centralizada de la aplicacion NEUROCENTER.
 * <p>Contiene los datos institucionales, los parametros economicos del negocio
 * y la ubicacion del archivo de base de datos embebida. Todos los miembros son
 * constantes o colecciones inmutables para garantizar su integridad.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class AppConfig {

    /** Nombre comercial del centro. */
    public static final String NOMBRE = "NEUROCENTER";
    /** Rubro segun la consigna. */
    public static final String RUBRO = "Medical & Health - Rehabilitacion neurologica";
    /** Direccion del centro. */
    public static final String DIRECCION = "Calle San Hernan 196, Los Olivos, Lima, Peru";
    /** Telefono y WhatsApp. */
    public static final String TELEFONO = "+51 980 664 453";
    /** Sitio web. */
    public static final String WEB = "neurocenter-peru.com";
    /** Cuenta de Instagram. */
    public static final String INSTAGRAM = "neurocenter_peru";
    /** Cuenta de TikTok. */
    public static final String TIKTOK = "neurocenterperu";

    /** Ruta de la base de datos; se puede sobreescribir con -Dneurocenter.db=&lt;ruta&gt;. */
    public static final String RUTA_BASE_DATOS =
            System.getProperty("neurocenter.db", "neurocenter.db");
    /** URL JDBC de SQLite. */
    public static final String URL_JDBC = "jdbc:sqlite:" + RUTA_BASE_DATOS;

    /** Cantidad minima de sesiones en un paquete para obtener descuento. */
    public static final int SESIONES_MINIMAS_DESCUENTO = 10;
    /** Porcentaje de descuento para paquetes (0.10 = 10%). */
    public static final double DESCUENTO_PAQUETE = 0.10;
    /** Duracion por defecto de una sesion, en minutos. */
    public static final int DURACION_DEFECTO = 60;

    /** Tarifa base por especialidad (tarifa por hora en soles). */
    private static final Map<Especialidad, Double> TARIFAS;

    static {
        Map<Especialidad, Double> tarifas = new EnumMap<>(Especialidad.class);
        tarifas.put(Especialidad.FISICA, 120.0);
        tarifas.put(Especialidad.OCUPACIONAL, 110.0);
        tarifas.put(Especialidad.LENGUAJE, 140.0);
        TARIFAS = Collections.unmodifiableMap(tarifas);
    }

    /** Constructor privado: clase utilitaria. */
    private AppConfig() {
    }

    /**
     * Devuelve la tarifa base configurada para una especialidad.
     *
     * @param especialidad especialidad consultada
     * @return tarifa por hora en soles
     */
    public static double tarifaBase(Especialidad especialidad) {
        return TARIFAS.getOrDefault(especialidad, 120.0);
    }

    /**
     * Devuelve una vista no modificable de las tarifas.
     *
     * @return mapa inmutable especialidad -&gt; tarifa
     */
    public static Map<Especialidad, Double> tarifas() {
        return TARIFAS;
    }
}
