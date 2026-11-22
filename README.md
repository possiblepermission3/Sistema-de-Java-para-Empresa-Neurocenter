# NEUROCENTER — Sistema de Gestión de Rehabilitación Neurológica

Aplicación de escritorio Java Swing + JDBC para administrar pacientes, terapeutas, horarios, sesiones, planes de rehabilitación, asistencias, pagos y reportes del centro NEUROCENTER.

## 1. Requisitos

- JDK 17 o superior.
- Maven 3.9+ (opcional; también se puede compilar con `javac`).
- Dependencias runtime:
  - `org.xerial:sqlite-jdbc:3.45.3.0`
  - `org.slf4j:slf4j-simple:2.0.13`

## 2. Ejecución con Maven

Desde la raíz del proyecto (`TF_EquipoNN`):

```bash
mvn compile
mvn exec:java
mvn package
java -jar target/neurocenter.jar
```

Para usar otra ruta de base de datos:

```bash
MAVEN_OPTS="-Dneurocenter.db=/ruta/neurocenter.db" mvn exec:java
java -Dneurocenter.db=/ruta/neurocenter.db -jar target/neurocenter.jar
```

## 3. Ejecución sin Maven (compilación manual)

```bash
mkdir -p out lib

curl -fsSL -o lib/sqlite-jdbc-3.45.3.0.jar \
  https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar
curl -fsSL -o lib/slf4j-api-2.0.13.jar \
  https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar
curl -fsSL -o lib/slf4j-simple-2.0.13.jar \
  https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.13/slf4j-simple-2.0.13.jar

javac -encoding UTF-8 -cp "lib/*" -d out $(find src/main/java -name '*.java')

java -cp "out:lib/*:src/main/resources" com.neurocenter.NeuroCenterApp
```

En este entorno el JDK quedó instalado en:

```bash
/home/linuxbrew/.linuxbrew/opt/openjdk@17/bin/javac
/home/linuxbrew/.linuxbrew/opt/openjdk@17/bin/java
```

## 4. Base de datos

La aplicación usa SQLite embebido; no requiere instalar MySQL ni PostgreSQL.

- Archivo por defecto: `neurocenter.db` en el directorio donde se ejecuta el programa.
- Propiedad de configuración: `-Dneurocenter.db=<ruta>`.
- Scripts incluidos como recursos:
  - `src/main/resources/db/schema.sql`
  - `src/main/resources/db/seed.sql`
- `DatabaseInitializer.inicializar()` crea el esquema y carga datos de ejemplo solo si la tabla `paciente` está vacía. Es un proceso idempotente.

Tablas principales: `paciente`, `terapeuta`, `horario`, `historial_clinico`, `plan_rehabilitacion`, `sesion`, `asistencia`, `evaluacion_progreso`, `pago`, `usuario`, `parametro`.

### Acceso

La aplicación muestra `LoginDialog` al iniciar. Las credenciales de la base de datos son:

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `apanaredes` | `neuro2026` | Terapeuta |

Las contraseñas se almacenan como hash SHA-256.

Los permisos se centralizan en `com.neurocenter.util.Permisos`:

| Módulo / acción | Administrador | Terapeuta |
|---|---|---|
| Inicio, Pacientes, Citas y Sesiones, Planes y Progreso, Reportes, NEUROCENTER | Sí | Sí |
| Eliminar pacientes | Sí | No |
| Terapeutas, Pagos | Sí | No |

## 5. Recursos visuales

Las imágenes institucionales se incluyen en el classpath para que funcionen tanto con Maven como con el JAR:

```text
src/main/resources/assets/neurocenter1.jpg   Logotipo cuadrado
src/main/resources/assets/neurocenter2.png   Banner panorámico
```

`UiTheme.iconoEscalado(...)` las carga y escala conservando la proporción. Se muestran en la cabecera principal, el panel Inicio y el panel NEUROCENTER.

## 6. Módulos de la interfaz

- **Inicio**: panel de indicadores y accesos rápidos.
- **Pacientes**: alta, edición, baja lógica, búsqueda e historia clínica.
- **Terapeutas**: alta, edición, especialidades, horarios y tarifas.
- **Citas y Sesiones**: programación, reprogramación, cancelación, finalización y asistencia.
- **Planes y Progreso**: planes de rehabilitación y evaluaciones funcionales.
- **Pagos**: pagos individuales y paquetes con descuento.
- **Reportes**: métricas, gráficos y rankings.
- **NEUROCENTER**: información institucional, servicios, contacto y workflow.

La aplicación se lanza en el EDT de Swing mediante `SwingUtilities.invokeLater` desde `com.neurocenter.NeuroCenterApp`.

## 7. Arquitectura

```text
com.neurocenter
├── config     Configuración institucional, tarifas y JDBC
├── db         Inicialización automática de schema.sql y seed.sql
├── exception  Excepciones de dominio y persistencia
├── model      Entidades, enums, herencia de terapias y fábrica
├── repository Acceso JDBC genérico y repositorios por entidad
├── service    Reglas de negocio y validaciones
├── util       Fechas, formatos, validadores, seguridad e IDs
└── ui         Login, MainFrame, paneles y componentes visuales
```

### Programación orientada a objetos

- **Encapsulamiento**: entidades con atributos privados y acceso controlado.
- **Abstracción**: `ServicioTerapia`, `CalculableCosto`, `Entidad<ID>`, `CrudRepository<T, ID>`.
- **Herencia**: `TerapiaFisica`, `TerapiaOcupacional` y `TerapiaLenguaje` extienden `ServicioTerapia`.
- **Polimorfismo**: `SesionFactory.crear(Especialidad)` y cálculo de costos por especialidad.
- **Colecciones y genéricos**: `AbstractJdbcRepository<T extends Entidad<ID>, ID>`, `RowMapper<T>`, listas y mapas.
- **Lambdas y Stream API**: filtros, agrupaciones y métricas en servicios y reportes.

## 8. Reglas de negocio implementadas

- Validación de DNI, teléfono, edad, montos y campos obligatorios.
- Horario laboral por turno y prevención de cruces para terapeuta y paciente.
- Costo de sesión según especialidad y tarifa del terapeuta.
- Paquetes de 10 o más sesiones con 10% de descuento.
- Baja lógica de pacientes con historial; borrado físico solo cuando no tienen sesiones.
- Cancelación de sesiones programadas y reprogramación con validación de horario.
- Finalización de sesión con registro de asistencia en transacción JDBC.
- Evaluaciones de progreso, avance y adherencia por plan.
- Reportes de asistencia, ingresos, estados, costos, progreso y ranking de terapeutas.

## 9. Resumen técnico para sustentación

El sistema modela el flujo real de NEUROCENTER: evaluación inicial, historia clínica, asignación de especialista, programación de sesiones, control de asistencia, pagos y seguimiento mensual. La capa de persistencia usa JDBC contra SQLite y centraliza el mapeo de filas en `RowMapper` y operaciones CRUD en un repositorio genérico. La capa de servicios concentra las validaciones y reglas del negocio, mientras que la capa UI ofrece formularios, tablas ordenables, gráficos y actualización global vía la interfaz `Actualizable`. Los datos iniciales permiten demostrar el sistema sin configuración adicional.
