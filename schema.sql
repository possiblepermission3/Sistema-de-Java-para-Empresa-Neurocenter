-- =============================================================================
-- NEUROCENTER - Esquema fisico de base de datos (SQLite embebido)
-- Centro especializado en rehabilitacion neurologica
-- Calle San Hernan 196, Los Olivos, Lima, Peru
-- =============================================================================
-- Ejecutar con: sqlite3 neurocenter.db < schema.sql
-- La aplicacion ejecuta este mismo script automaticamente al iniciar.
-- =============================================================================

PRAGMA foreign_keys = ON;

-- -----------------------------------------------------------------------------
-- Pacientes
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS paciente (
    paciente_id          TEXT PRIMARY KEY,
    nombre               TEXT NOT NULL CHECK (length(trim(nombre)) >= 2),
    apellido             TEXT NOT NULL CHECK (length(trim(apellido)) >= 2),
    dni                  TEXT NOT NULL UNIQUE CHECK (length(dni) = 8 AND dni NOT GLOB '*[^0-9]*'),
    edad                 INTEGER NOT NULL CHECK (edad BETWEEN 0 AND 120),
    telefono             TEXT NOT NULL CHECK (length(telefono) BETWEEN 7 AND 15),
    domicilio            TEXT NOT NULL CHECK (length(trim(domicilio)) >= 4),
    fecha_registro       TEXT NOT NULL,
    diagnostico_referido TEXT,
    activo               INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0, 1))
);

-- -----------------------------------------------------------------------------
-- Terapeutas / especialistas
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS terapeuta (
    terapeuta_id  TEXT PRIMARY KEY,
    nombre        TEXT NOT NULL CHECK (length(trim(nombre)) >= 2),
    apellido      TEXT NOT NULL CHECK (length(trim(apellido)) >= 2),
    dni           TEXT NOT NULL UNIQUE CHECK (length(dni) = 8 AND dni NOT GLOB '*[^0-9]*'),
    telefono      TEXT NOT NULL CHECK (length(telefono) BETWEEN 7 AND 15),
    edad          INTEGER NOT NULL CHECK (edad BETWEEN 18 AND 100),
    turno         TEXT NOT NULL CHECK (turno IN ('MANANA', 'TARDE')),
    foto          TEXT,
    especialidad  TEXT NOT NULL CHECK (especialidad IN ('FISICA', 'OCUPACIONAL', 'LENGUAJE')),
    tarifa_hora   REAL NOT NULL CHECK (tarifa_hora > 0),
    activo        INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0, 1))
);

-- -----------------------------------------------------------------------------
-- Horarios de atencion por terapeuta (tabla de referencia del negocio)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS horario (
    horario_id   TEXT PRIMARY KEY,
    terapeuta_id TEXT NOT NULL REFERENCES terapeuta(terapeuta_id) ON DELETE CASCADE,
    dia_semana   TEXT NOT NULL CHECK (dia_semana IN
                 ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO')),
    hora_inicio  TEXT NOT NULL CHECK (hora_inicio GLOB '[0-1][0-9]:[0-5][0-9]' OR hora_inicio GLOB '2[0-3]:[0-5][0-9]'),
    hora_fin     TEXT NOT NULL CHECK (hora_fin GLOB '[0-1][0-9]:[0-5][0-9]' OR hora_fin GLOB '2[0-3]:[0-5][0-9]'),
    CHECK (hora_inicio < hora_fin)
);

-- -----------------------------------------------------------------------------
-- Historia clinica (uno a uno con paciente)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS historial_clinico (
    historial_id         TEXT PRIMARY KEY,
    paciente_id          TEXT NOT NULL UNIQUE REFERENCES paciente(paciente_id) ON DELETE CASCADE,
    sesiones_programadas INTEGER NOT NULL DEFAULT 0 CHECK (sesiones_programadas >= 0),
    diagnostico_interno  TEXT,
    diagnostico_externo  TEXT,
    adjunto              TEXT,
    fecha_registro       TEXT NOT NULL
);

-- -----------------------------------------------------------------------------
-- Planes de rehabilitacion
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS plan_rehabilitacion (
    plan_id          TEXT PRIMARY KEY,
    paciente_id      TEXT NOT NULL REFERENCES paciente(paciente_id) ON DELETE CASCADE,
    terapeuta_id     TEXT NOT NULL REFERENCES terapeuta(terapeuta_id) ON DELETE RESTRICT,
    especialidad     TEXT NOT NULL CHECK (especialidad IN ('FISICA', 'OCUPACIONAL', 'LENGUAJE')),
    sesiones_totales INTEGER NOT NULL CHECK (sesiones_totales > 0),
    fecha_inicio     TEXT NOT NULL,
    fecha_fin        TEXT,
    objetivo         TEXT NOT NULL,
    activo           INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0, 1))
);

-- -----------------------------------------------------------------------------
-- Sesiones / citas de terapia (herencia single-table: columna "tipo")
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sesion (
    sesion_id          TEXT PRIMARY KEY,
    tipo               TEXT NOT NULL CHECK (tipo IN ('FISICA', 'OCUPACIONAL', 'LENGUAJE')),
    paciente_id        TEXT NOT NULL REFERENCES paciente(paciente_id) ON DELETE RESTRICT,
    terapeuta_id       TEXT NOT NULL REFERENCES terapeuta(terapeuta_id) ON DELETE RESTRICT,
    plan_id            TEXT REFERENCES plan_rehabilitacion(plan_id) ON DELETE SET NULL,
    fecha_hora         TEXT NOT NULL,
    duracion_minutos   INTEGER NOT NULL CHECK (duracion_minutos BETWEEN 15 AND 240),
    costo              REAL NOT NULL CHECK (costo >= 0),
    estado             TEXT NOT NULL CHECK (estado IN ('PROGRAMADA', 'COMPLETADA', 'CANCELADA', 'INASISTIDA')),
    notas              TEXT,
    area_tratada       TEXT,
    actividad_adaptada TEXT,
    objetivo_lenguaje  TEXT,
    created_at         TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sesion_terapeuta_fecha ON sesion(terapeuta_id, fecha_hora);
CREATE INDEX IF NOT EXISTS idx_sesion_paciente_fecha  ON sesion(paciente_id, fecha_hora);
CREATE INDEX IF NOT EXISTS idx_sesion_estado          ON sesion(estado);
CREATE INDEX IF NOT EXISTS idx_sesion_plan            ON sesion(plan_id);

-- -----------------------------------------------------------------------------
-- Asistencia y evolucion por sesion
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS asistencia (
    asistencia_id   TEXT PRIMARY KEY,
    sesion_id       TEXT NOT NULL UNIQUE REFERENCES sesion(sesion_id) ON DELETE CASCADE,
    estado          TEXT NOT NULL CHECK (estado IN ('ASISTIO', 'FALTO', 'JUSTIFICADO')),
    fecha_efectiva  TEXT NOT NULL,
    nota_evolucion  TEXT
);

-- -----------------------------------------------------------------------------
-- Evaluaciones de progreso
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS evaluacion_progreso (
    evaluacion_id     TEXT PRIMARY KEY,
    plan_id           TEXT NOT NULL REFERENCES plan_rehabilitacion(plan_id) ON DELETE CASCADE,
    terapeuta_id      TEXT NOT NULL REFERENCES terapeuta(terapeuta_id) ON DELETE RESTRICT,
    fecha             TEXT NOT NULL,
    escala_funcional  INTEGER NOT NULL CHECK (escala_funcional BETWEEN 0 AND 100),
    observaciones     TEXT
);

CREATE INDEX IF NOT EXISTS idx_evaluacion_plan_fecha ON evaluacion_progreso(plan_id, fecha);

-- -----------------------------------------------------------------------------
-- Pagos (anticipados individuales o por paquete - Interbank / BCP / efectivo)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pago (
    pago_id           TEXT PRIMARY KEY,
    paciente_id       TEXT NOT NULL REFERENCES paciente(paciente_id) ON DELETE RESTRICT,
    sesion_id         TEXT REFERENCES sesion(sesion_id) ON DELETE SET NULL,
    fecha_pago        TEXT NOT NULL,
    monto             REAL NOT NULL CHECK (monto > 0),
    tipo_pago         TEXT NOT NULL CHECK (tipo_pago IN ('INDIVIDUAL', 'PAQUETE')),
    metodo_pago       TEXT NOT NULL CHECK (metodo_pago IN ('EFECTIVO', 'INTERBANK', 'BCP', 'TARJETA', 'YAPE', 'PLIN')),
    numero_operacion  TEXT,
    sesiones_paquete  INTEGER NOT NULL DEFAULT 1 CHECK (sesiones_paquete >= 1)
);

CREATE INDEX IF NOT EXISTS idx_pago_paciente_fecha ON pago(paciente_id, fecha_pago);

-- -----------------------------------------------------------------------------
-- Usuarios del sistema
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    username        TEXT PRIMARY KEY,
    password_hash   TEXT NOT NULL,
    rol             TEXT NOT NULL CHECK (rol IN ('ADMIN', 'TERAPEUTA')),
    nombre_completo TEXT NOT NULL,
    activo          INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0, 1))
);

-- -----------------------------------------------------------------------------
-- Parametros configurables (tarifas base, descuentos, datos de la empresa)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS parametro (
    clave       TEXT PRIMARY KEY,
    valor       TEXT NOT NULL,
    descripcion TEXT
);
