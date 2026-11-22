package com.neurocenter.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidades de seguridad para el modulo de autenticacion.
 * <p>Las contraseñas se almacenan como hash SHA-256 nunca en texto plano.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class Seguridad {

    /** Constructor privado: clase utilitaria. */
    private Seguridad() {
    }

    /**
     * Calcula el hash SHA-256 de una cadena.
     *
     * @param texto cadena de entrada
     * @return hash en hexadecimal de 64 caracteres
     */
    public static String sha256(String texto) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hexadecimal.append(String.format("%02x", b & 0xff));
            }
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("El algoritmo SHA-256 no esta disponible", e);
        }
    }

    /**
     * Compara una contraseña con un hash almacenado.
     *
     * @param contrasena texto plano
     * @param hash       hash guardado
     * @return verdadero si coinciden
     */
    public static boolean verificar(String contrasena, String hash) {
        return contrasena != null && hash != null && sha256(contrasena).equalsIgnoreCase(hash);
    }
}
