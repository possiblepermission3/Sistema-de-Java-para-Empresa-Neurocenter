package com.neurocenter.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.neurocenter.exception.DatosInvalidosException;

/**
 * Validaciones de entrada reutilizables para toda la aplicacion.
 * <p>Cada metodo lanza {@link DatosInvalidosException} con un mensaje claro y
 * orientado al usuario, de modo que la capa de interfaz pueda mostrarlo en un
 * {@code JOptionPane} sin exponer detalles tecnicos.</p>
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class Validadores {

    /** Constructor privado: clase utilitaria. */
    private Validadores() {
    }

    /**
     * Valida un texto obligatorio.
     *
     * @param valor    texto a validar
     * @param campo    nombre del campo para el mensaje
     * @param longitud longitud minima permitida
     * @return texto recortado
     * @throws DatosInvalidosException si esta vacio o es demasiado corto
     */
    public static String texto(String valor, String campo, int longitud) throws DatosInvalidosException {
        if (valor == null || valor.trim().length() < longitud) {
            throw new DatosInvalidosException(
                    String.format("El campo %s es obligatorio y debe tener al menos %d caracteres.", campo, longitud));
        }
        return valor.trim();
    }

    /**
     * Valida un DNI peruano de 8 digitos.
     *
     * @param dni valor a validar
     * @return DNI normalizado
     * @throws DatosInvalidosException si no tiene 8 digitos
     */
    public static String dni(String dni) throws DatosInvalidosException {
        String valor = dni == null ? "" : dni.trim();
        if (!valor.matches("\\d{8}")) {
            throw new DatosInvalidosException("El DNI debe tener exactamente 8 digitos numéricos.");
        }
        return valor;
    }

    /**
     * Valida un numero de telefono peruano (9 digitos, iniciando en 9).
     *
     * @param telefono valor a validar
     * @return telefono normalizado sin espacios
     * @throws DatosInvalidosException si no es un numero valido
     */
    public static String telefono(String telefono) throws DatosInvalidosException {
        String valor = telefono == null ? "" : telefono.trim().replaceAll("[\\s()+-]", "");
        if (!valor.matches("9\\d{8}")) {
            throw new DatosInvalidosException("El celular debe tener 9 digitos y comenzar con 9.");
        }
        return valor;
    }

    /**
     * Valida un rango de edad.
     *
     * @param edad  valor a validar
     * @param min   limite inferior inclusive
     * @param max   limite superior inclusive
     * @param campo nombre del campo
     * @return edad validada
     * @throws DatosInvalidosException si esta fuera de rango
     */
    public static int edad(int edad, int min, int max, String campo) throws DatosInvalidosException {
        if (edad < min || edad > max) {
            throw new DatosInvalidosException(String.format("La %s debe estar entre %d y %d años.", campo, min, max));
        }
        return edad;
    }

    /**
     * Valida un monto positivo.
     *
     * @param monto valor a validar
     * @param campo nombre del campo
     * @return monto validado
     * @throws DatosInvalidosException si es menor o igual a cero
     */
    public static double monto(double monto, String campo) throws DatosInvalidosException {
        if (!Double.isFinite(monto) || monto <= 0) {
            throw new DatosInvalidosException("El " + campo + " debe ser un numero mayor a cero.");
        }
        return monto;
    }

    /**
     * Valida un entero dentro de un rango.
     *
     * @param valor valor a validar
     * @param min   limite inferior
     * @param max   limite superior
     * @param campo nombre del campo
     * @return valor validado
     * @throws DatosInvalidosException si esta fuera de rango
     */
    public static int enteroEnRango(int valor, int min, int max, String campo) throws DatosInvalidosException {
        if (valor < min || valor > max) {
            throw new DatosInvalidosException(
                    String.format("El campo %s debe estar entre %d y %d.", campo, min, max));
        }
        return valor;
    }

    /**
     * Valida que una fecha/hora no este en el pasado.
     *
     * @param fechaHora fecha y hora a validar
     * @throws DatosInvalidosException si es anterior al momento actual
     */
    public static void fechaFutura(LocalDateTime fechaHora) throws DatosInvalidosException {
        if (fechaHora == null) {
            throw new DatosInvalidosException("Debe seleccionar la fecha y hora de la sesion.");
        }
        if (fechaHora.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new DatosInvalidosException("No se puede programar una sesion en una fecha u hora pasada.");
        }
    }

    /**
     * Valida que una fecha no sea futura.
     *
     * @param fecha fecha a validar
     * @param campo nombre del campo
     * @throws DatosInvalidosException si es futura
     */
    public static void fechaNoFutura(LocalDate fecha, String campo) throws DatosInvalidosException {
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("La " + campo + " no puede ser futura.");
        }
    }

    /**
     * Valida que un objeto no sea nulo.
     *
     * @param objeto objeto a validar
     * @param campo  nombre del campo
     * @param <T>    tipo del objeto
     * @return el mismo objeto
     * @throws DatosInvalidosException si es nulo
     */
    public static <T> T requerido(T objeto, String campo) throws DatosInvalidosException {
        if (objeto == null) {
            throw new DatosInvalidosException("Debe seleccionar un " + campo + " valido.");
        }
        return objeto;
    }
}
