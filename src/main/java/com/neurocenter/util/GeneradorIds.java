package com.neurocenter.util;

import java.util.UUID;

/**
 * Generador de identificadores unicos con prefijo de negocio.
 * <p>Ejemplo: {@code generar("PAC")} produce {@code PAC-3F9A2C71B840}.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class GeneradorIds {

    /** Constructor privado: clase utilitaria. */
    private GeneradorIds() {
    }

    /**
     * Genera un identificador unico.
     *
     * @param prefijo prefijo descriptivo (PAC, TER, SES, ...)
     * @return identificador con formato PREFIJO-XXXXXXXXXXXX
     */
    public static String generar(String prefijo) {
        String aleatorio = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return (prefijo == null || prefijo.isBlank() ? "ID" : prefijo) + "-" + aleatorio;
    }
}
