package com.neurocenter.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formateadores de salida para montos, porcentajes y texto de la interfaz.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class Formatos {

    /** Simbolo monetario peruano. */
    private static final DecimalFormatSymbols SIMBOLOS = new DecimalFormatSymbols(Locale.forLanguageTag("es-PE"));

    static {
        SIMBOLOS.setDecimalSeparator('.');
        SIMBOLOS.setGroupingSeparator(',');
        SIMBOLOS.setCurrencySymbol("S/");
    }

    /** Formato de moneda. */
    private static final DecimalFormat MONEDA = new DecimalFormat("¤#,##0.00", SIMBOLOS);
    /** Formato de porcentaje. */
    private static final DecimalFormat PORCENTAJE = new DecimalFormat("0.0'%'", SIMBOLOS);

    /** Constructor privado: clase utilitaria. */
    private Formatos() {
    }

    /**
     * Formatea un monto en soles.
     *
     * @param monto monto a formatear
     * @return cadena como "S/ 1,280.00"
     */
    public static String moneda(double monto) {
        return MONEDA.format(monto);
    }

    /**
     * Formatea un valor 0-100 como porcentaje.
     *
     * @param valor valor porcentual
     * @return cadena como "75.0%"
     */
    public static String porcentaje(double valor) {
        return PORCENTAJE.format(valor);
    }

    /**
     * Convierte un texto cualquiera en una representacion segura para la tabla.
     *
     * @param texto texto original
     * @return texto vacio si es nulo
     */
    public static String seguro(String texto) {
        return texto == null ? "" : texto;
    }
}
