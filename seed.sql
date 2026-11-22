-- =============================================================================
-- NEUROCENTER - Datos iniciales de prueba
-- Ejecutar despues de schema.sql: sqlite3 neurocenter.db < seed.sql
-- La aplicacion los inserta automaticamente solo si la base esta vacia.
-- =============================================================================

PRAGMA foreign_keys = ON;

INSERT INTO paciente (paciente_id, nombre, apellido, dni, edad, telefono, domicilio, fecha_registro, diagnostico_referido, activo) VALUES
('PAC-001', 'Rosa',     'Vargas Quispe',  '12345678', 45, '987654321', 'Av. Lima 123, Los Olivos',        '2026-10-02', 'ACV ischemic derecho', 1),
('PAC-002', 'Manuel',   'Salas Rojas',    '23456789', 62, '987111222', 'Jr. Callao 456, Lima',            '2026-10-08', 'Hemiparesia izquierda', 1),
('PAC-003', 'Lucia',    'Mendoza Torres', '34567890',  8, '987333444', 'Calle Las Palmeras 789, Comas',   '2026-10-14', 'Trastorno del lenguaje', 1),
('PAC-004', 'Jorge',    'Huerta Camera',  '45678901', 37, '987555666', 'Av. Industrial 321, Lima',        '2026-10-18', 'Traumatismo craneoencefalico', 1),
('PAC-005', 'Carmen',   'Rios Paredes',   '56789012', 54, '987777888', 'Psje. Los Olivos 45, Los Olivos', '2026-11-02', 'Lesion de medulla espinal', 1),
('PAC-006', 'Diego',    'Ferrer Nuñez',   '67890123', 29, '987999000', 'Av. Angamos 1111, Miraflores',    '2026-11-10', 'Paralisis facial periferica', 1);

INSERT INTO terapeuta (terapeuta_id, nombre, apellido, dni, telefono, edad, turno, foto, especialidad, tarifa_hora, activo) VALUES
('TER-001', 'Ana',     'Paredes Flores', '70111222', '999111222', 33, 'MANANA', 'fotos/ter-001.png', 'FISICA',      120.0, 1),
('TER-002', 'Carlos',  'Salazar Vega',   '70222333', '999333444', 41, 'TARDE',  'fotos/ter-002.png', 'FISICA',      130.0, 1),
('TER-003', 'Maria',   'Gomez Reyes',    '70333444', '999555666', 36, 'MANANA', 'fotos/ter-003.png', 'OCUPACIONAL', 110.0, 1),
('TER-004', 'Luis',    'Tapia Mendoza',  '70444555', '999777888', 39, 'TARDE',  'fotos/ter-004.png', 'LENGUAJE',    140.0, 1);

INSERT INTO horario (horario_id, terapeuta_id, dia_semana, hora_inicio, hora_fin) VALUES
('HOR-001', 'TER-001', 'LUNES',     '09:00', '13:00'),
('HOR-002', 'TER-001', 'MIERCOLES', '09:00', '13:00'),
('HOR-003', 'TER-001', 'VIERNES',   '09:00', '13:00'),
('HOR-004', 'TER-002', 'MARTES',    '15:00', '19:00'),
('HOR-005', 'TER-002', 'JUEVES',    '15:00', '19:00'),
('HOR-006', 'TER-002', 'MIERCOLES', '15:00', '19:00'),
('HOR-007', 'TER-003', 'LUNES',     '10:00', '14:00'),
('HOR-008', 'TER-003', 'JUEVES',    '10:00', '14:00'),
('HOR-009', 'TER-004', 'MARTES',    '16:00', '20:00'),
('HOR-010', 'TER-004', 'VIERNES',   '16:00', '20:00');

INSERT INTO historial_clinico (historial_id, paciente_id, sesiones_programadas, diagnostico_interno, diagnostico_externo, adjunto, fecha_registro) VALUES
('HIS-001', 'PAC-001', 24, 'Hemiparesia derecha grado 3, buen pronóstico funcional',       'ACV ischemic',        'drive://hc/PAC-001.docx', '2026-10-03'),
('HIS-002', 'PAC-002', 20, 'Hemiparesia izquierda, requiere apoyo para marcha',             'ACV hemorragico',     'drive://hc/PAC-002.docx', '2026-10-09'),
('HIS-003', 'PAC-003', 16, 'Retraso en lenguaje expresivo, comprensión conservada',         'Dislalia mixta',      'drive://hc/PAC-003.docx', '2026-10-15'),
('HIS-004', 'PAC-004', 18, 'Alteración de coordinación ojo-mano y funciones ejecutivas',   'TCE moderado',        'drive://hc/PAC-004.docx', '2026-10-19'),
('HIS-005', 'PAC-005', 12, 'Paraparesia incompleta, entrenamiento de transferencias',       'Lesión medular T10',  'drive://hc/PAC-005.docx', '2026-11-03'),
('HIS-006', 'PAC-006', 20, 'Paralisis facial periférica derecha, estimulación muscular',    'Parálisis de Bell',   'drive://hc/PAC-006.docx', '2026-11-11');

INSERT INTO plan_rehabilitacion (plan_id, paciente_id, terapeuta_id, especialidad, sesiones_totales, fecha_inicio, fecha_fin, objetivo, activo) VALUES
('PLAN-001', 'PAC-001', 'TER-001', 'FISICA',       24, '2026-10-05', '2027-01-05', 'Recuperar fuerza y marcha funcional autónoma', 1),
('PLAN-002', 'PAC-002', 'TER-002', 'FISICA',       20, '2026-10-10', '2026-12-30', 'Mejorar equilibrio y deambulación con bastón', 1),
('PLAN-003', 'PAC-003', 'TER-004', 'LENGUAJE',     16, '2026-10-15', '2026-12-20', 'Ampliar vocabulario y estructuras gramaticales', 1),
('PLAN-004', 'PAC-004', 'TER-003', 'OCUPACIONAL',  18, '2026-10-18', '2027-01-10', 'Reintegración a actividades de la vida diaria', 1),
('PLAN-005', 'PAC-005', 'TER-001', 'FISICA',       12, '2026-11-02', '2026-12-20', 'Transferencias seguras y fortalecimiento', 1),
('PLAN-006', 'PAC-006', 'TER-002', 'FISICA',       20, '2026-11-10', '2027-02-10', 'Reeducación muscular facial', 1);

INSERT INTO sesion (sesion_id, tipo, paciente_id, terapeuta_id, plan_id, fecha_hora, duracion_minutos, costo, estado, notas, area_tratada, actividad_adaptada, objetivo_lenguaje, created_at) VALUES
('SES-001', 'FISICA',       'PAC-001', 'TER-001', 'PLAN-001', '2026-11-16T09:00', 60, 120.0, 'COMPLETADA', 'Buena respuesta a ejercicios pasivos',        'Miembro superior derecho', NULL, NULL, '2026-11-01T08:30'),
('SES-002', 'FISICA',       'PAC-001', 'TER-001', 'PLAN-001', '2026-11-18T09:00', 60, 120.0, 'COMPLETADA', 'Mejora de tono muscular',                     'Miembro superior derecho', NULL, NULL, '2026-11-01T08:30'),
('SES-003', 'OCUPACIONAL',  'PAC-004', 'TER-003', 'PLAN-004', '2026-11-16T10:00', 60, 110.0, 'COMPLETADA', 'Adaptación de cubiertos exitosa',             NULL, 'Adaptación de utensilios de cocina', NULL, '2026-11-01T08:30'),
('SES-004', 'LENGUAJE',     'PAC-003', 'TER-004', 'PLAN-003', '2026-11-17T16:00', 45, 140.0, 'COMPLETADA', 'Nombra 12 imágenes correctamente',            NULL, NULL, 'Estimulación de lenguaje expresivo', '2026-11-01T08:30'),
('SES-005', 'FISICA',       'PAC-002', 'TER-002', 'PLAN-002', '2026-11-17T15:00', 60, 130.0, 'COMPLETADA', 'Marcha con apoyo mínimo',                     'Miembro inferior izquierdo', NULL, NULL, '2026-11-01T08:30'),
('SES-006', 'FISICA',       'PAC-002', 'TER-002', 'PLAN-002', '2026-11-19T15:00', 60, 130.0, 'COMPLETADA', 'Ejercicios de equilibrio en barra',            'Miembro inferior izquierdo', NULL, NULL, '2026-11-01T08:30'),
('SES-007', 'OCUPACIONAL',  'PAC-004', 'TER-003', 'PLAN-004', '2026-11-19T10:00', 60, 110.0, 'COMPLETADA', 'Mejora en coordinación ojo-mano',             NULL, 'Ensamblaje de piezas y manualidades', NULL, '2026-11-01T08:30'),
('SES-008', 'LENGUAJE',     'PAC-003', 'TER-004', 'PLAN-003', '2026-11-20T16:00', 45, 140.0, 'INASISTIDA', 'Falta justificada por madre',              NULL, NULL, 'Estructuración de frases simples', '2026-11-01T08:30'),
('SES-009', 'FISICA',       'PAC-005', 'TER-001', 'PLAN-005', '2026-11-20T11:00', 60, 120.0, 'COMPLETADA', 'Transferencia cama-silla con supervisión',     'Tronco y miembros inferiores', NULL, NULL, '2026-11-01T08:30'),
('SES-010', 'FISICA',       'PAC-001', 'TER-001', 'PLAN-001', '2026-11-23T09:00', 60, 120.0, 'PROGRAMADA', 'Sesión programada - control de fuerza',       'Miembro superior derecho', NULL, NULL, '2026-11-01T08:30'),
('SES-011', 'FISICA',       'PAC-002', 'TER-002', 'PLAN-002', '2026-11-23T15:00', 60, 130.0, 'PROGRAMADA', 'Sesión programada - marcha',                  'Miembro inferior izquierdo', NULL, NULL, '2026-11-01T08:30'),
('SES-012', 'LENGUAJE',     'PAC-003', 'TER-004', 'PLAN-003', '2026-11-24T16:00', 45, 140.0, 'PROGRAMADA', 'Sesión programada - lenguaje',               NULL, NULL, 'Comprensión de órdenes complejas', '2026-11-01T08:30'),
('SES-013', 'OCUPACIONAL',  'PAC-004', 'TER-003', 'PLAN-004', '2026-11-25T10:00', 60, 110.0, 'PROGRAMADA', 'Sesión programada - AVD',                    NULL, 'Vestido y cuidado personal', NULL, '2026-11-01T08:30'),
('SES-014', 'FISICA',       'PAC-006', 'TER-002', 'PLAN-006', '2026-11-25T16:30', 60, 130.0, 'PROGRAMADA', 'Sesión programada - masaje facial',          'Musculatura facial', NULL, NULL, '2026-11-10T08:30'),
('SES-015', 'FISICA',       'PAC-005', 'TER-001', 'PLAN-005', '2026-11-26T11:00', 60, 120.0, 'PROGRAMADA', 'Sesión programada - fortalecimiento',       'Tronco y miembros inferiores', NULL, NULL, '2026-11-01T08:30'),
('SES-016', 'FISICA',       'PAC-001', 'TER-001', 'PLAN-001', '2026-11-27T09:00', 60, 120.0, 'PROGRAMADA', 'Sesión programada - control de fuerza',       'Miembro superior derecho', NULL, NULL, '2026-11-01T08:30');

INSERT INTO asistencia (asistencia_id, sesion_id, estado, fecha_efectiva, nota_evolucion) VALUES
('ASI-001', 'SES-001', 'ASISTIO',      '2026-11-16', 'Incremento de 10° en extensión de codo'),
('ASI-002', 'SES-002', 'ASISTIO',      '2026-11-18', 'Mayor Control Motor, menos espasticidad'),
('ASI-003', 'SES-003', 'ASISTIO',      '2026-11-16', 'Logra tomar cubiertos con adaptación'),
('ASI-004', 'SES-004', 'ASISTIO',      '2026-11-17', 'Vocabulario activo en aumento'),
('ASI-005', 'SES-005', 'ASISTIO',      '2026-11-17', 'Marcha 20 metros con bastón'),
('ASI-006', 'SES-006', 'ASISTIO',      '2026-11-19', 'Mejor equilibrio unilateral'),
('ASI-007', 'SES-007', 'JUSTIFICADO',  '2026-11-19', 'Asistió 40 min por malestar'),
('ASI-008', 'SES-008', 'FALTO',        '2026-11-20', 'Falta justificada por familiar'),
('ASI-009', 'SES-009', 'ASISTIO',      '2026-11-20', 'Transferencia con mínima asistencia');

INSERT INTO evaluacion_progreso (evaluacion_id, plan_id, terapeuta_id, fecha, escala_funcional, observaciones) VALUES
('EVA-001', 'PLAN-001', 'TER-001', '2026-10-05', 30, 'Evaluación inicial: dependencia para marcha'),
('EVA-002', 'PLAN-001', 'TER-001', '2026-11-18', 58, 'Mejoría significativa en fuerza de miembro superior'),
('EVA-003', 'PLAN-002', 'TER-002', '2026-11-19', 52, 'Deambulación con apoyo parcial'),
('EVA-004', 'PLAN-003', 'TER-004', '2026-11-17', 48, 'Comprensión adecuada, expresión limitada'),
('EVA-005', 'PLAN-004', 'TER-003', '2026-11-19', 45, 'AVD con supervisión moderada'),
('EVA-006', 'PLAN-005', 'TER-001', '2026-11-20', 40, 'Transferencias con asistencia mínima');

INSERT INTO pago (pago_id, paciente_id, sesion_id, fecha_pago, monto, tipo_pago, metodo_pago, numero_operacion, sesiones_paquete) VALUES
('PAG-001', 'PAC-001', NULL,    '2026-10-04', 1080.00, 'PAQUETE',    'INTERBANK', 'IB-20261004-001', 10),
('PAG-002', 'PAC-002', NULL,    '2026-10-09',  520.00, 'INDIVIDUAL', 'BCP',       'BCP-20261009-01', 4),
('PAG-003', 'PAC-003', NULL,    '2026-10-14',  630.00, 'PAQUETE',    'YAPE',      'YP-20261014-001', 5),
('PAG-004', 'PAC-004', NULL,    '2026-10-18',  990.00, 'PAQUETE',    'INTERBANK', 'IB-20261018-002', 10),
('PAG-005', 'PAC-005', NULL,    '2026-11-02',  540.00, 'PAQUETE',    'EFECTIVO',  NULL,              5),
('PAG-006', 'PAC-006', NULL,    '2026-11-10',  260.00, 'INDIVIDUAL', 'PLIN',      'PL-20261110-001', 2),
('PAG-007', 'PAC-001', 'SES-001', '2026-11-16', 120.00, 'INDIVIDUAL', 'EFECTIVO', NULL, 1),
('PAG-008', 'PAC-003', 'SES-004', '2026-11-17', 140.00, 'INDIVIDUAL', 'YAPE',     'YP-20261117-004', 1);

INSERT INTO usuario (username, password_hash, rol, nombre_completo, activo) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'Administrador NEUROCENTER', 1),
('apanaredes', '0e5a170ff0867a879d950b746ab6c1b741cbb413769c35e9647f1e5726a137a4', 'TERAPEUTA', 'Ana Paredes Flores', 1);

INSERT INTO parametro (clave, valor, descripcion) VALUES
('TARIFA_BASE_FISICA',       '120.0',  'Tarifa base por sesión de terapia física'),
('TARIFA_BASE_OCUPACIONAL',  '110.0',  'Tarifa base por sesión de terapia ocupacional'),
('TARIFA_BASE_LENGUAJE',     '140.0',  'Tarifa base por sesión de terapia de lenguaje'),
('DESCUENTO_PAQUETE',        '0.10',   'Descuento aplicado a paquetes de 10 o más sesiones'),
('DURACION_DEFECTO',         '60',     'Duración por defecto de una sesión en minutos'),
('NOMBRE_EMPRESA',           'NEUROCENTER', 'Razón social'),
('DIRECCION',                'Calle San Hernan 196, Los Olivos, Lima, Peru', 'Dirección del centro'),
('TELEFONO',                 '+51 980 664 453', 'Teléfono y WhatsApp'),
('WEB',                      'neurocenter-peru.com', 'Sitio web'),
('INSTAGRAM',                'neurocenter_peru', 'Instagram'),
('TIKTOK',                   'neurocenterperu', 'TikTok');
