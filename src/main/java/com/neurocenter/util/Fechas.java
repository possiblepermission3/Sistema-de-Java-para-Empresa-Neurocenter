package com.neurocenter.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Utilidades de conversion y formato de fechas para la aplicacion.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public final class Fechas {

    /** Formato ISO usado para persistir fecha y hora en SQLite (TEXT). */
    public static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    /** Formato de fecha ISO. */
    public static final DateTimeFormatter FECHA_ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    /** Formato legible de fecha y hora. */
    public static final DateTimeFormatter LEGIBLE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    /** Formato legible de fecha. */
    public static final DateTimeFormatter FECHA_LEGIBLE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /** Formato de solo hora. */
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    /** Locale español para nombres de dias. */
    private static final Locale ES = new Locale("es", "ES");

    /** Constructor privado: clase utilitaria. */
    private Fechas() {
    }

    /**
     * Serializa una fecha y hora a texto ISO para la base de datos.
     *
     * @param fechaHora fecha y hora
     * @return texto ISO, o cadena vacia si el argumento es nulo
     */
    public static String guardar(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(ISO);
    }

    /**
     * Reconstruye una fecha y hora desde texto ISO.
     *
     * @param texto texto ISO
     * @return fecha y hora, o {@code null} si el texto esta vacio
     */
    public static LocalDateTime leer(String texto) {
        return texto == null || texto.isBlank() ? null : LocalDateTime.parse(texto, ISO);
    }

    /**
     * Serializa una fecha a texto ISO.
     *
     * @param fecha fecha
     * @return texto ISO, o cadena vacia si es nulo
     */
    public static String guardarFecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA_ISO);
    }

    /**
     * Reconstruye una fecha desde texto ISO.
     *
     * @param texto texto ISO
     * @return fecha, o {@code null} si esta vacio
     */
    public static LocalDate leerFecha(String texto) {
        return texto == null || texto.isBlank() ? null : LocalDate.parse(texto, FECHA_ISO);
    }

    /**
     * Devuelve el nombre del dia de la semana en español, en mayusculas.
     *
     * @param fechaHora fecha de referencia
     * @return LUNES, MARTES, etc.
     */
    public static String diaSemana(LocalDateTime fechaHora) {
        DayOfWeek dia = fechaHora.getDayOfWeek();
        return dia.getDisplayName(TextStyle.FULL, ES).toUpperCase(Locale.ROOT);
    }

    /**
     * Combina una fecha y una hora en una fecha y hora.
     *
     * @param fecha fecha
     * @param hora  hora
     * @return fecha y hora resultante
     */
    public static LocalDateTime combinar(LocalDate fecha, LocalTime hora) {
        return LocalDateTime.of(fecha, hora);
    }
}
