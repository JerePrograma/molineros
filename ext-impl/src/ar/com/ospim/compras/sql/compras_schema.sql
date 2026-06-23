-- =====================================================================
-- MODULO: Compras - reconstruccion canonica unificada de desarrollo
-- PostgreSQL 9.6+
--
-- DESTRUCTIVO:
--   Elimina completamente el esquema compras y lo reconstruye desde cero.
--   No conserva datos ni compatibilidad con el modelo anterior.
--   Toda la instalacion y su smoke test se ejecutan en una unica transaccion.
--
-- Flujo funcional activo:
--   1  PENDIENTE
--   2  A_COTIZAR
--   3  COTIZADO
--
-- Estados reconocidos de solo lectura, sin transiciones activas:
--   4  RECLAMO_RP
--   5  ORDEN_COMPRA
--
-- Estado lateral:
--   99 ANULADO
--
-- Contratos incorporados:
--   - guardar_requerimiento con 21 argumentos de entrada.
--   - persistencia de afiliado_id_ospim como snapshot.
--   - PDF con afiliado_id_ospim, integrante y documento.
--   - destinatario de cotizacion persistido por prestador.
--
-- Dependencias externas de solo lectura:
--   public.prestador
--   trae_tipos_prestadores()
--
-- Ejecutar con psql -v ON_ERROR_STOP=1.
-- Si la sesion esta abortada, ejecutar ROLLBACK antes de este archivo.
-- =====================================================================

BEGIN;

DROP SCHEMA IF EXISTS compras CASCADE;
CREATE SCHEMA compras;

-- =====================================================================
-- TABLAS
-- =====================================================================

CREATE TABLE compras.sector_requerimiento (
                                              id_sector SERIAL PRIMARY KEY,
                                              descripcion VARCHAR(120) NOT NULL,
                                              requiere_afiliado BOOLEAN NOT NULL DEFAULT FALSE,
                                              activo BOOLEAN NOT NULL DEFAULT TRUE,

                                              alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                              alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
                                              modi_fecha TIMESTAMP WITHOUT TIME ZONE,
                                              modi_usr VARCHAR(100),
                                              baja_fecha TIMESTAMP WITHOUT TIME ZONE,
                                              baja_usr VARCHAR(100),

                                              CONSTRAINT ck_compras_sector_descripcion
                                                  CHECK (length(btrim(descripcion)) > 0)
);

CREATE UNIQUE INDEX uq_compras_sector_descripcion_activo
    ON compras.sector_requerimiento (lower(btrim(descripcion)))
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_sector_activo
    ON compras.sector_requerimiento (activo)
    WHERE baja_fecha IS NULL;


CREATE TABLE compras.articulo (
                                  id_articulo SERIAL PRIMARY KEY,

                                  id_sector INTEGER NOT NULL
                                      REFERENCES compras.sector_requerimiento (id_sector),

                                  descripcion VARCHAR(200) NOT NULL,
                                  activo BOOLEAN NOT NULL DEFAULT TRUE,

                                  alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                  alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
                                  modi_fecha TIMESTAMP WITHOUT TIME ZONE,
                                  modi_usr VARCHAR(100),
                                  baja_fecha TIMESTAMP WITHOUT TIME ZONE,
                                  baja_usr VARCHAR(100),

                                  CONSTRAINT ck_compras_articulo_descripcion
                                      CHECK (length(btrim(descripcion)) > 0)
);

CREATE UNIQUE INDEX uq_compras_articulo_sector_descripcion_activo
    ON compras.articulo (
                         id_sector,
                         lower(btrim(descripcion))
        )
    WHERE baja_fecha IS NULL
      AND activo = TRUE;

CREATE INDEX ix_compras_articulo_sector
    ON compras.articulo (id_sector)
    WHERE baja_fecha IS NULL
      AND activo = TRUE;


CREATE TABLE compras.sector_tipo_prestador (
                                               id_sector INTEGER NOT NULL
                                                   REFERENCES compras.sector_requerimiento (id_sector),

                                               id_tipo_prestador INTEGER NOT NULL,

                                               activo BOOLEAN NOT NULL DEFAULT TRUE,

                                               alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                               alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
                                               modi_fecha TIMESTAMP WITHOUT TIME ZONE,
                                               modi_usr VARCHAR(100),
                                               baja_fecha TIMESTAMP WITHOUT TIME ZONE,
                                               baja_usr VARCHAR(100),

                                               CONSTRAINT pk_compras_sector_tipo_prestador
                                                   PRIMARY KEY (id_sector, id_tipo_prestador)
);

CREATE INDEX ix_compras_sector_tipo_sector_activo
    ON compras.sector_tipo_prestador (id_sector, activo)
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_sector_tipo_tipo_activo
    ON compras.sector_tipo_prestador (id_tipo_prestador, activo)
    WHERE baja_fecha IS NULL;


CREATE TABLE compras.requerimiento (
                                       id_requerimiento SERIAL PRIMARY KEY,

                                       estado INTEGER NOT NULL DEFAULT 1,

                                       id_sector INTEGER NOT NULL
                                           REFERENCES compras.sector_requerimiento (id_sector),

                                       afiliado_cuil_titular VARCHAR(20),
                                       afiliado_int INTEGER,
                                       afiliado_id_ospim INTEGER,

    -- Snapshot para consulta e impresion.
                                       afiliado_nombre VARCHAR(120),
                                       afiliado_apellido VARCHAR(120),
                                       afiliado_documento_tipo VARCHAR(10),
                                       afiliado_documento_nro VARCHAR(30),
                                       afiliado_direccion VARCHAR(250),
                                       afiliado_localidad VARCHAR(120),
                                       afiliado_provincia VARCHAR(120),
                                       afiliado_celular VARCHAR(80),
                                       afiliado_telefono VARCHAR(80),
                                       afiliado_email VARCHAR(160),

                                       cargo_ospim INTEGER NOT NULL DEFAULT 100,
                                       cargo_tercerizadora INTEGER NOT NULL DEFAULT 0,
                                       id_tercerizadora VARCHAR(40),
                                       recupero BOOLEAN NOT NULL DEFAULT FALSE,

                                       observaciones TEXT,

                                       alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                       alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
                                       modi_fecha TIMESTAMP WITHOUT TIME ZONE,
                                       modi_usr VARCHAR(100),
                                       baja_fecha TIMESTAMP WITHOUT TIME ZONE,
                                       baja_usr VARCHAR(100),

                                       CONSTRAINT ck_compras_requerimiento_estado
                                           CHECK (estado IN (1, 2, 3, 4, 5, 99)),

                                       CONSTRAINT ck_compras_requerimiento_afiliado_int
                                           CHECK (afiliado_int IS NULL OR afiliado_int >= 0),

                                       CONSTRAINT ck_compras_requerimiento_cargo_ospim
                                           CHECK (cargo_ospim BETWEEN 0 AND 100),

                                       CONSTRAINT ck_compras_requerimiento_cargo_tercerizadora
                                           CHECK (cargo_tercerizadora BETWEEN 0 AND 100),

                                       CONSTRAINT ck_compras_requerimiento_cargos_total
                                           CHECK (cargo_ospim + cargo_tercerizadora = 100),

                                       CONSTRAINT ck_compras_requerimiento_tercerizadora
                                           CHECK (
                                               cargo_tercerizadora = 0
                                                   OR NULLIF(btrim(id_tercerizadora), '') IS NOT NULL
                                               ),

                                       CONSTRAINT ck_compras_requerimiento_recupero
                                           CHECK (recupero = (cargo_tercerizadora > 0))
);

CREATE INDEX ix_compras_requerimiento_estado
    ON compras.requerimiento (estado, id_requerimiento DESC);

CREATE INDEX ix_compras_requerimiento_sector
    ON compras.requerimiento (id_sector, id_requerimiento DESC)
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_requerimiento_afiliado
    ON compras.requerimiento (
                              afiliado_cuil_titular,
                              afiliado_int
        )
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_requerimiento_afiliado_id_ospim
    ON compras.requerimiento (afiliado_id_ospim)
    WHERE baja_fecha IS NULL
      AND afiliado_id_ospim IS NOT NULL;

CREATE INDEX ix_compras_requerimiento_tercerizadora
    ON compras.requerimiento (id_tercerizadora)
    WHERE baja_fecha IS NULL
      AND id_tercerizadora IS NOT NULL;

CREATE INDEX ix_compras_requerimiento_alta
    ON compras.requerimiento (alta_fecha DESC);


CREATE TABLE compras.requerimiento_cotizacion_prestador (
                                                            id_requerimiento INTEGER NOT NULL
                                                                REFERENCES compras.requerimiento (id_requerimiento),

    -- Identificador externo. Sin FK: esta migracion no administra otros esquemas.
                                                            id_prestador INTEGER NOT NULL,

                                                            estado_envio VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
                                                            intentos INTEGER NOT NULL DEFAULT 0,

                                                            email_destino VARCHAR(320),

                                                            fecha_creacion TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                                            fecha_ultimo_intento TIMESTAMP WITHOUT TIME ZONE,
                                                            fecha_envio TIMESTAMP WITHOUT TIME ZONE,
                                                            ultimo_error TEXT,

                                                            alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
                                                            modi_fecha TIMESTAMP WITHOUT TIME ZONE,
                                                            modi_usr VARCHAR(100),

                                                            CONSTRAINT pk_compras_requerimiento_cotizacion_prestador
                                                                PRIMARY KEY (id_requerimiento, id_prestador),

                                                            CONSTRAINT ck_compras_cotizacion_estado_envio
                                                                CHECK (
                                                                    estado_envio IN (
                                                                                     'PENDIENTE',
                                                                                     'PROCESANDO',
                                                                                     'ENVIADO',
                                                                                     'ERROR',
                                                                                     'EMAIL_INVALIDO'
                                                                        )
                                                                    ),

                                                            CONSTRAINT ck_compras_cotizacion_intentos
                                                                CHECK (intentos >= 0),

                                                            CONSTRAINT ck_compras_cotizacion_fecha_envio
                                                                CHECK (
                                                                    estado_envio <> 'ENVIADO'
                                                                        OR fecha_envio IS NOT NULL
                                                                    )
);

CREATE INDEX ix_compras_cotizacion_requerimiento_estado
    ON compras.requerimiento_cotizacion_prestador (
                                                   id_requerimiento,
                                                   estado_envio
        );

CREATE INDEX ix_compras_cotizacion_prestador
    ON compras.requerimiento_cotizacion_prestador (id_prestador);

CREATE INDEX ix_compras_cotizacion_fecha
    ON compras.requerimiento_cotizacion_prestador (fecha_creacion DESC);


CREATE TABLE compras.requerimiento_detalle (
                                               id_detalle SERIAL PRIMARY KEY,

                                               id_requerimiento INTEGER NOT NULL
                                                   REFERENCES compras.requerimiento (id_requerimiento),

                                               id_articulo INTEGER NOT NULL
                                                   REFERENCES compras.articulo (id_articulo),

                                               cantidad INTEGER NOT NULL,
                                               observaciones TEXT,

                                               precio_unitario_estimado NUMERIC(18, 2),
                                               precio_total_estimado NUMERIC(18, 2),

    -- Identificador externo. Sin FK: esta migracion no administra otros esquemas.
                                               id_prestador INTEGER,

                                               alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                               alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
                                               modi_fecha TIMESTAMP WITHOUT TIME ZONE,
                                               modi_usr VARCHAR(100),
                                               baja_fecha TIMESTAMP WITHOUT TIME ZONE,
                                               baja_usr VARCHAR(100),

                                               CONSTRAINT ck_compras_detalle_cantidad
                                                   CHECK (cantidad > 0),

                                               CONSTRAINT ck_compras_detalle_precio_unitario
                                                   CHECK (
                                                       precio_unitario_estimado IS NULL
                                                           OR precio_unitario_estimado >= 0
                                                       ),

                                               CONSTRAINT ck_compras_detalle_precio_total
                                                   CHECK (
                                                       precio_total_estimado IS NULL
                                                           OR precio_total_estimado >= 0
                                                       )
);

CREATE INDEX ix_compras_detalle_requerimiento
    ON compras.requerimiento_detalle (
                                      id_requerimiento,
                                      id_detalle
        )
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_detalle_articulo
    ON compras.requerimiento_detalle (id_articulo)
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_detalle_prestador
    ON compras.requerimiento_detalle (id_prestador)
    WHERE baja_fecha IS NULL
      AND id_prestador IS NOT NULL;

-- =====================================================================
-- DATOS INICIALES
-- =====================================================================

INSERT INTO compras.sector_requerimiento (
    id_sector,
    descripcion,
    requiere_afiliado,
    activo,
    alta_usr
)
VALUES
    (1, 'Farmacia', TRUE, TRUE, 'sistema'),
    (2, 'Prestaciones Medicas', TRUE, TRUE, 'sistema'),
    (3, 'Auditoria Medica', TRUE, TRUE, 'sistema'),
    (4, 'Monotributo', TRUE, TRUE, 'sistema'),
    (5, 'Sistemas', FALSE, TRUE, 'sistema'),
    (6, 'RRHH', FALSE, TRUE, 'sistema'),
    (7, 'Legales', FALSE, TRUE, 'sistema'),
    (8, 'Otros', FALSE, TRUE, 'sistema');

SELECT setval(
               pg_get_serial_sequence(
                       'compras.sector_requerimiento',
                       'id_sector'
               ),
               8,
               TRUE
       );

-- =====================================================================
-- FUNCIONES AUXILIARES
-- =====================================================================

CREATE FUNCTION compras.normalizar_usuario(
    p_usuario VARCHAR
)
    RETURNS VARCHAR
AS $func$
SELECT COALESCE(
               NULLIF(btrim($1), ''),
               current_user::VARCHAR
       );
$func$
LANGUAGE sql
STABLE;


CREATE FUNCTION compras.estado_requerimiento_descripcion(
    p_estado INTEGER
)
RETURNS VARCHAR
AS $func$
BEGIN
    RETURN CASE p_estado
        WHEN 1 THEN 'PENDIENTE'
        WHEN 2 THEN 'A COTIZAR'
        WHEN 3 THEN 'COTIZADO'
        WHEN 4 THEN 'RECLAMO (RP)'
        WHEN 5 THEN 'ORDEN DE COMPRA'
        WHEN 99 THEN 'ANULADO'
        ELSE 'DESCONOCIDO'
    END;
END;
$func$
LANGUAGE plpgsql
IMMUTABLE;


CREATE FUNCTION compras.listar_estados_requerimiento()
RETURNS TABLE (
    id INTEGER,
    descripcion VARCHAR
)
AS $func$
BEGIN
    RETURN QUERY
    SELECT *
      FROM (
        VALUES
            (1, 'PENDIENTE'::VARCHAR),
            (2, 'A COTIZAR'::VARCHAR),
            (3, 'COTIZADO'::VARCHAR),
            (4, 'RECLAMO (RP)'::VARCHAR),
            (5, 'ORDEN DE COMPRA'::VARCHAR),
            (99, 'ANULADO'::VARCHAR)
      ) estados(id, descripcion);
END;
$func$
LANGUAGE plpgsql
IMMUTABLE;

-- =====================================================================
-- REGLAS DE INTEGRIDAD
-- =====================================================================

CREATE FUNCTION compras.validar_requerimiento_fila()
    RETURNS TRIGGER
AS $func$
DECLARE
v_requiere_afiliado BOOLEAN;
    v_cambio_estructura BOOLEAN;
    v_usuario VARCHAR(100);
BEGIN
SELECT s.requiere_afiliado
INTO v_requiere_afiliado
FROM compras.sector_requerimiento s
WHERE s.id_sector = NEW.id_sector
  AND s.activo = TRUE
  AND s.baja_fecha IS NULL;

IF v_requiere_afiliado IS NULL THEN
        RAISE EXCEPTION
            'El sector informado no existe o no esta activo.';
END IF;

    IF TG_OP = 'INSERT' THEN
        IF NEW.estado <> 1 THEN
            RAISE EXCEPTION
                'Un requerimiento nuevo debe crearse en estado PENDIENTE.';
END IF;
ELSE
        IF OLD.estado IN (3, 4, 5, 99)
           AND NEW IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        v_cambio_estructura :=
               NEW.id_sector IS DISTINCT FROM OLD.id_sector
            OR NEW.afiliado_cuil_titular
                IS DISTINCT FROM OLD.afiliado_cuil_titular
            OR NEW.afiliado_int IS DISTINCT FROM OLD.afiliado_int
            OR NEW.afiliado_id_ospim
                IS DISTINCT FROM OLD.afiliado_id_ospim
            OR NEW.afiliado_nombre
                IS DISTINCT FROM OLD.afiliado_nombre
            OR NEW.afiliado_apellido
                IS DISTINCT FROM OLD.afiliado_apellido
            OR NEW.afiliado_documento_tipo
                IS DISTINCT FROM OLD.afiliado_documento_tipo
            OR NEW.afiliado_documento_nro
                IS DISTINCT FROM OLD.afiliado_documento_nro
            OR NEW.afiliado_direccion
                IS DISTINCT FROM OLD.afiliado_direccion
            OR NEW.afiliado_localidad
                IS DISTINCT FROM OLD.afiliado_localidad
            OR NEW.afiliado_provincia
                IS DISTINCT FROM OLD.afiliado_provincia
            OR NEW.afiliado_celular
                IS DISTINCT FROM OLD.afiliado_celular
            OR NEW.afiliado_telefono
                IS DISTINCT FROM OLD.afiliado_telefono
            OR NEW.afiliado_email
                IS DISTINCT FROM OLD.afiliado_email
            OR NEW.cargo_ospim IS DISTINCT FROM OLD.cargo_ospim
            OR NEW.cargo_tercerizadora
                IS DISTINCT FROM OLD.cargo_tercerizadora
            OR NEW.id_tercerizadora
                IS DISTINCT FROM OLD.id_tercerizadora
            OR NEW.recupero IS DISTINCT FROM OLD.recupero
            OR NEW.observaciones IS DISTINCT FROM OLD.observaciones;

        IF v_cambio_estructura AND OLD.estado <> 1 THEN
            RAISE EXCEPTION
                'La estructura solo puede modificarse en estado PENDIENTE.';
END IF;

        IF NEW.estado IS DISTINCT FROM OLD.estado THEN
            IF NOT (
                    (OLD.estado = 1 AND NEW.estado IN (2, 99))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
            ) THEN
                RAISE EXCEPTION
                    'Transicion de estado invalida: % -> %.',
                    OLD.estado,
                    NEW.estado;
END IF;

            IF NEW.estado IN (4, 5) THEN
                RAISE EXCEPTION
                    'RECLAMO (RP) y ORDEN DE COMPRA son estados de solo lectura.';
END IF;

            IF OLD.estado = 1 AND NEW.estado = 2 THEN
                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento = NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN
                    RAISE EXCEPTION
                        'Debe existir al menos un detalle antes de enviar a cotizar.';
END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_cotizacion_prestador rcp
                     WHERE rcp.id_requerimiento =
                           NEW.id_requerimiento
                       AND rcp.estado_envio = 'ENVIADO'
                ) THEN
                    RAISE EXCEPTION
                        'Debe existir al menos un prestador notificado como ENVIADO antes de pasar a A COTIZAR.';
END IF;
END IF;

            IF OLD.estado = 2 AND NEW.estado = 3 THEN
                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento = NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN
                    RAISE EXCEPTION
                        'No se puede cerrar una cotizacion sin detalles.';
END IF;

                IF EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento = NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                       AND (
                              d.cantidad <= 0
                           OR d.precio_unitario_estimado IS NULL
                           OR d.precio_unitario_estimado < 0
                           OR d.precio_total_estimado IS NULL
                           OR d.id_prestador IS NULL
                           OR d.precio_total_estimado
                              <> round(
                                     d.cantidad
                                     * d.precio_unitario_estimado,
                                     2
                                 )
                           OR NOT EXISTS (
                                SELECT 1
                                  FROM compras.requerimiento_cotizacion_prestador rcp
                                 WHERE rcp.id_requerimiento =
                                       NEW.id_requerimiento
                                   AND rcp.id_prestador =
                                       d.id_prestador
                                   AND rcp.estado_envio =
                                       'ENVIADO'
                           )
                       )
                ) THEN
                    RAISE EXCEPTION
                        'No se puede cerrar la cotizacion: existen detalles incompletos o invalidos.';
END IF;
END IF;

            IF NEW.estado = 99 THEN
                v_usuario := compras.normalizar_usuario(
                    COALESCE(NEW.modi_usr, NEW.baja_usr)
                );

                NEW.baja_fecha := COALESCE(
                    NEW.baja_fecha,
                    now()
                );
                NEW.baja_usr := COALESCE(
                    NULLIF(btrim(NEW.baja_usr), ''),
                    v_usuario
                );
END IF;
END IF;
END IF;

    IF v_requiere_afiliado THEN
        IF NULLIF(
            btrim(NEW.afiliado_cuil_titular),
            ''
        ) IS NULL THEN
            RAISE EXCEPTION
                'Debe informar el CUIL titular del afiliado.';
END IF;

        IF NEW.afiliado_int IS NULL
           OR NEW.afiliado_int < 0 THEN
            RAISE EXCEPTION
                'Debe informar el integrante del afiliado.';
END IF;
ELSE
        NEW.afiliado_cuil_titular := NULL;
        NEW.afiliado_int := NULL;
        NEW.afiliado_id_ospim := NULL;

        NEW.afiliado_nombre := NULL;
        NEW.afiliado_apellido := NULL;
        NEW.afiliado_documento_tipo := NULL;
        NEW.afiliado_documento_nro := NULL;
        NEW.afiliado_direccion := NULL;
        NEW.afiliado_localidad := NULL;
        NEW.afiliado_provincia := NULL;
        NEW.afiliado_celular := NULL;
        NEW.afiliado_telefono := NULL;
        NEW.afiliado_email := NULL;

        NEW.cargo_ospim := 100;
        NEW.cargo_tercerizadora := 0;
        NEW.id_tercerizadora := NULL;
        NEW.recupero := FALSE;
END IF;

RETURN NEW;
END;
$func$
LANGUAGE plpgsql;


CREATE TRIGGER trg_compras_requerimiento_validar
    BEFORE INSERT OR UPDATE
                         ON compras.requerimiento
                         FOR EACH ROW
                         EXECUTE PROCEDURE compras.validar_requerimiento_fila();


CREATE FUNCTION compras.validar_requerimiento_detalle_fila()
    RETURNS TRIGGER
AS $func$
DECLARE
v_estado INTEGER;
    v_id_sector_requerimiento INTEGER;
    v_id_sector_articulo INTEGER;
    v_articulo_activo BOOLEAN;
BEGIN
SELECT
    r.estado,
    r.id_sector
INTO
    v_estado,
    v_id_sector_requerimiento
FROM compras.requerimiento r
WHERE r.id_requerimiento = NEW.id_requerimiento
  AND r.baja_fecha IS NULL;

IF v_estado IS NULL THEN
        RAISE EXCEPTION
            'No existe un requerimiento activo para el detalle.';
END IF;

SELECT
    a.id_sector,
    a.activo
INTO
    v_id_sector_articulo,
    v_articulo_activo
FROM compras.articulo a
WHERE a.id_articulo = NEW.id_articulo
  AND a.baja_fecha IS NULL;

IF v_id_sector_articulo IS NULL
       OR NOT COALESCE(v_articulo_activo, FALSE) THEN
        RAISE EXCEPTION
            'El articulo informado no existe o no esta activo.';
END IF;

    IF v_id_sector_articulo <> v_id_sector_requerimiento THEN
        RAISE EXCEPTION
            'El articulo no pertenece al sector del requerimiento.';
END IF;

    IF TG_OP = 'INSERT' AND v_estado <> 1 THEN
        RAISE EXCEPTION
            'Los detalles solo pueden crearse en estado PENDIENTE.';
END IF;

    IF TG_OP = 'UPDATE' THEN
        IF v_estado = 1 THEN
            -- En PENDIENTE se permite editar o dar de baja la estructura.
            NULL;

        ELSIF v_estado = 2 THEN
            IF NEW.id_requerimiento
                    IS DISTINCT FROM OLD.id_requerimiento
               OR NEW.id_articulo
                    IS DISTINCT FROM OLD.id_articulo
               OR NEW.cantidad
                    IS DISTINCT FROM OLD.cantidad
               OR NEW.observaciones
                    IS DISTINCT FROM OLD.observaciones
               OR NEW.baja_fecha
                    IS DISTINCT FROM OLD.baja_fecha
               OR NEW.baja_usr
                    IS DISTINCT FROM OLD.baja_usr THEN

                RAISE EXCEPTION
                    'En estado A COTIZAR la estructura del detalle esta bloqueada.';
END IF;

ELSE
            RAISE EXCEPTION
                'El detalle no puede modificarse en el estado actual.';
END IF;
END IF;

    IF v_estado = 1 THEN
        IF NEW.precio_unitario_estimado IS NOT NULL
           OR NEW.precio_total_estimado IS NOT NULL
           OR NEW.id_prestador IS NOT NULL THEN

            RAISE EXCEPTION
                'Un requerimiento PENDIENTE no puede tener datos de cotizacion.';
END IF;

    ELSIF v_estado = 2 THEN
        IF NEW.precio_unitario_estimado < 0 THEN
            RAISE EXCEPTION
                'El precio unitario estimado no puede ser negativo.';
        END IF;

        IF NEW.precio_unitario_estimado IS NULL THEN
            NEW.precio_total_estimado := NULL;
ELSE
            NEW.precio_total_estimado :=
                round(
                    NEW.cantidad
                    * NEW.precio_unitario_estimado,
                    2
                );
END IF;

        IF NEW.id_prestador IS NOT NULL
           AND NOT EXISTS (
                SELECT 1
                  FROM compras.requerimiento_cotizacion_prestador rcp
                 WHERE rcp.id_requerimiento =
                       NEW.id_requerimiento
                   AND rcp.id_prestador =
                       NEW.id_prestador
                   AND rcp.estado_envio =
                       'ENVIADO'
           ) THEN

            RAISE EXCEPTION
                'El prestador seleccionado no fue notificado correctamente para este requerimiento.';
END IF;
END IF;

RETURN NEW;
END;
$func$
LANGUAGE plpgsql;


CREATE TRIGGER trg_compras_detalle_validar
    BEFORE INSERT OR UPDATE
                         ON compras.requerimiento_detalle
                         FOR EACH ROW
                         EXECUTE PROCEDURE compras.validar_requerimiento_detalle_fila();

-- =====================================================================
-- SECTORES
-- =====================================================================

CREATE FUNCTION compras.listar_sector_requerimiento()
    RETURNS TABLE (
                      id INTEGER,
                      descripcion VARCHAR,
                      requiere_afiliado BOOLEAN
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT
    s.id_sector,
    s.descripcion,
    s.requiere_afiliado
FROM compras.sector_requerimiento s
WHERE s.activo = TRUE
  AND s.baja_fecha IS NULL
ORDER BY s.descripcion;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.get_sector_requerimiento(
    p_id_sector INTEGER
)
    RETURNS TABLE (
                      id INTEGER,
                      descripcion VARCHAR,
                      requiere_afiliado BOOLEAN
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT
    s.id_sector,
    s.descripcion,
    s.requiere_afiliado
FROM compras.sector_requerimiento s
WHERE s.id_sector = p_id_sector
  AND s.activo = TRUE
  AND s.baja_fecha IS NULL;
END;
$func$
LANGUAGE plpgsql
STABLE;

-- =====================================================================
-- TIPO DE PRESTADOR POR SECTOR
-- =====================================================================

CREATE FUNCTION compras.listar_tipos_prestador_sector(
    p_id_sector INTEGER
)
    RETURNS TABLE (
                      id_tipo_prestador INTEGER,
                      descripcion VARCHAR,
                      activo BOOLEAN
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT
    tp.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR,
    COALESCE(stp.activo, FALSE)::BOOLEAN
FROM trae_tipos_prestadores() tp
         LEFT JOIN compras.sector_tipo_prestador stp
                   ON stp.id_tipo_prestador =
                      tp.id_tipo_prestador::INTEGER
       AND stp.id_sector =
           p_id_sector
       AND stp.baja_fecha IS NULL
ORDER BY tp.descripcion;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.desactivar_tipos_prestador_sector(
    p_id_sector INTEGER,
    p_usuario VARCHAR
)
    RETURNS VOID
AS $func$
BEGIN
UPDATE compras.sector_tipo_prestador
SET activo = FALSE,
    modi_fecha = now(),
    modi_usr = compras.normalizar_usuario(p_usuario)
WHERE id_sector = p_id_sector
  AND baja_fecha IS NULL
  AND activo = TRUE;
END;
$func$
LANGUAGE plpgsql;


CREATE FUNCTION compras.guardar_sector_tipo_prestador(
    p_id_sector INTEGER,
    p_id_tipo_prestador INTEGER,
    p_activo BOOLEAN,
    p_usuario VARCHAR
)
    RETURNS VOID
AS $func$
DECLARE
v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(p_usuario);

    IF NOT EXISTS (
        SELECT 1
          FROM compras.sector_requerimiento s
         WHERE s.id_sector = p_id_sector
           AND s.activo = TRUE
           AND s.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'El sector informado no existe o no esta activo.';
END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM trae_tipos_prestadores() tp
         WHERE tp.id_tipo_prestador =
               p_id_tipo_prestador
    ) THEN
        RAISE EXCEPTION
            'El tipo de prestador informado no existe.';
END IF;

INSERT INTO compras.sector_tipo_prestador (
    id_sector,
    id_tipo_prestador,
    activo,
    alta_usr
)
VALUES (
           p_id_sector,
           p_id_tipo_prestador,
           COALESCE(p_activo, TRUE),
           v_usuario
       )
    ON CONFLICT (
        id_sector,
        id_tipo_prestador
    )
    DO UPDATE
               SET activo = EXCLUDED.activo,
               modi_fecha = now(),
               modi_usr = v_usuario,
               baja_fecha = NULL,
               baja_usr = NULL;
END;
$func$
LANGUAGE plpgsql;

-- =====================================================================
-- ARTICULOS
-- =====================================================================

CREATE FUNCTION compras.listar_articulos(
    p_id_sector INTEGER,
    p_texto VARCHAR
)
    RETURNS TABLE (
                      id INTEGER,
                      id_sector INTEGER,
                      sector_descripcion VARCHAR,
                      descripcion VARCHAR
                  )
    AS $func$
DECLARE
v_texto VARCHAR;
BEGIN
    v_texto := NULLIF(btrim(p_texto), '');

RETURN QUERY
SELECT
    a.id_articulo,
    a.id_sector,
    s.descripcion,
    a.descripcion
FROM compras.articulo a
         JOIN compras.sector_requerimiento s
              ON s.id_sector = a.id_sector
WHERE a.activo = TRUE
  AND a.baja_fecha IS NULL
  AND s.activo = TRUE
  AND s.baja_fecha IS NULL
  AND (
    p_id_sector IS NULL
        OR a.id_sector = p_id_sector
    )
  AND (
    v_texto IS NULL
        OR upper(a.descripcion)
        LIKE '%' || upper(v_texto) || '%'
    )
ORDER BY
    s.descripcion,
    a.descripcion;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.get_articulo(
    p_id_articulo INTEGER
)
    RETURNS TABLE (
                      id INTEGER,
                      id_sector INTEGER,
                      sector_descripcion VARCHAR,
                      descripcion VARCHAR
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT
    a.id_articulo,
    a.id_sector,
    s.descripcion,
    a.descripcion
FROM compras.articulo a
         JOIN compras.sector_requerimiento s
              ON s.id_sector = a.id_sector
WHERE a.id_articulo = p_id_articulo
  AND a.activo = TRUE
  AND a.baja_fecha IS NULL
  AND s.activo = TRUE
  AND s.baja_fecha IS NULL;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.guardar_articulo(
    p_id INTEGER,
    p_id_sector INTEGER,
    p_descripcion VARCHAR
)
    RETURNS INTEGER
AS $func$
DECLARE
v_id INTEGER;
    v_descripcion VARCHAR(200);
BEGIN
    v_descripcion := NULLIF(btrim(p_descripcion), '');

    IF v_descripcion IS NULL THEN
        RAISE EXCEPTION
            'Debe informar la descripcion del articulo.';
END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.sector_requerimiento s
         WHERE s.id_sector = p_id_sector
           AND s.activo = TRUE
           AND s.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'El sector informado no existe o no esta activo.';
END IF;

    IF p_id IS NULL OR p_id <= 0 THEN
        INSERT INTO compras.articulo (
            id_sector,
            descripcion,
            activo,
            alta_usr
        )
        VALUES (
            p_id_sector,
            v_descripcion,
            TRUE,
            'sistema'
        )
        RETURNING id_articulo
             INTO v_id;

RETURN v_id;
END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_detalle d
          JOIN compras.requerimiento r
            ON r.id_requerimiento =
               d.id_requerimiento
         WHERE d.id_articulo = p_id
           AND d.baja_fecha IS NULL
           AND r.baja_fecha IS NULL
           AND EXISTS (
                SELECT 1
                  FROM compras.articulo a
                 WHERE a.id_articulo = p_id
                   AND a.id_sector <> p_id_sector
           )
    ) THEN
        RAISE EXCEPTION
            'No se puede cambiar el sector de un articulo utilizado en detalles activos.';
END IF;

UPDATE compras.articulo
SET id_sector = p_id_sector,
    descripcion = v_descripcion,
    activo = TRUE,
    modi_fecha = now(),
    modi_usr = 'sistema',
    baja_fecha = NULL,
    baja_usr = NULL
WHERE id_articulo = p_id
    RETURNING id_articulo
INTO v_id;

IF v_id IS NULL THEN
        RAISE EXCEPTION
            'No se encontro el articulo a modificar.';
END IF;

RETURN v_id;
END;
$func$
LANGUAGE plpgsql;


CREATE FUNCTION compras.borrar_articulo(
    p_id_articulo INTEGER
)
    RETURNS VOID
AS $func$
BEGIN
    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_detalle d
          JOIN compras.requerimiento r
            ON r.id_requerimiento =
               d.id_requerimiento
         WHERE d.id_articulo =
               p_id_articulo
           AND d.baja_fecha IS NULL
           AND r.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'No se puede borrar el articulo porque esta utilizado en detalles activos.';
END IF;

UPDATE compras.articulo
SET activo = FALSE,
    baja_fecha = now(),
    baja_usr = 'sistema'
WHERE id_articulo = p_id_articulo
  AND baja_fecha IS NULL;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontro el articulo a borrar.';
END IF;
END;
$func$
LANGUAGE plpgsql;

-- =====================================================================
-- REQUERIMIENTOS: MODELO DE LECTURA
-- =====================================================================

CREATE TYPE compras.requerimiento_base_row AS (
    id INTEGER,

    alta_fecha TIMESTAMP WITHOUT TIME ZONE,
    alta_usr VARCHAR,

    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR,

    baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    baja_usr VARCHAR,

    afiliado_cuil_titular VARCHAR,
    afiliado_int INTEGER,
    afiliado_id_ospim INTEGER,

    afiliado_nombre VARCHAR,
    afiliado_apellido VARCHAR,
    afiliado_nombre_apellido VARCHAR,

    afiliado_documento_tipo VARCHAR,
    afiliado_documento_nro VARCHAR,
    afiliado_documento VARCHAR,

    afiliado_direccion VARCHAR,
    afiliado_localidad VARCHAR,
    afiliado_provincia VARCHAR,
    afiliado_celular VARCHAR,
    afiliado_telefono VARCHAR,
    afiliado_email VARCHAR,

    id_sector INTEGER,
    sector_descripcion VARCHAR,
    requiere_afiliado BOOLEAN,

    cargo_ospim INTEGER,
    cargo_tercerizadora INTEGER,
    id_tercerizadora VARCHAR,

    recupero BOOLEAN,
    observaciones TEXT,

    id_estado INTEGER,
    estado_descripcion VARCHAR
    );


CREATE FUNCTION compras.requerimiento_base()
    RETURNS SETOF compras.requerimiento_base_row
AS $func$
BEGIN
RETURN QUERY
SELECT
    r.id_requerimiento,

    r.alta_fecha,
    r.alta_usr,

    r.modi_fecha,
    r.modi_usr,

    r.baja_fecha,
    r.baja_usr,

    r.afiliado_cuil_titular,
    r.afiliado_int,
    r.afiliado_id_ospim,

    r.afiliado_nombre,
    r.afiliado_apellido,

    NULLIF(
            concat_ws(
                    ', ',
                    NULLIF(btrim(r.afiliado_apellido), ''),
                    NULLIF(btrim(r.afiliado_nombre), '')
            ),
            ''
    )::VARCHAR AS afiliado_nombre_apellido,

    r.afiliado_documento_tipo,
    r.afiliado_documento_nro,

    NULLIF(
            concat_ws(
                    ' ',
                    NULLIF(
                            btrim(r.afiliado_documento_tipo),
                            ''
                    ),
                    NULLIF(
                            btrim(r.afiliado_documento_nro),
                            ''
                    )
            ),
            ''
    )::VARCHAR AS afiliado_documento,

    r.afiliado_direccion,
    r.afiliado_localidad,
    r.afiliado_provincia,
    r.afiliado_celular,
    r.afiliado_telefono,
    r.afiliado_email,

    r.id_sector,
    s.descripcion,
    s.requiere_afiliado,

    r.cargo_ospim,
    r.cargo_tercerizadora,
    r.id_tercerizadora,

    r.recupero,
    r.observaciones,

    r.estado,
    compras.estado_requerimiento_descripcion(
            r.estado
    )
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento s
              ON s.id_sector = r.id_sector;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.buscar_requerimientos(
    p_estado INTEGER,
    p_sector INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_id_tercerizadora VARCHAR,
    p_recupero BOOLEAN,
    p_texto VARCHAR
)
    RETURNS SETOF compras.requerimiento_base_row
AS $func$
DECLARE
v_texto VARCHAR;
    v_cuil VARCHAR;
BEGIN
    v_texto := NULLIF(
        upper(btrim(p_texto)),
        ''
    );

    v_cuil := NULLIF(
        regexp_replace(
            COALESCE(
                p_afiliado_cuil_titular,
                ''
            ),
            '[^0-9]',
            '',
            'g'
        ),
        ''
    );

RETURN QUERY
SELECT rb.*
FROM compras.requerimiento_base() rb
WHERE (
    (
        p_estado = 99
            AND rb.id_estado = 99
        )
        OR (
        p_estado IS DISTINCT FROM 99
            AND rb.baja_fecha IS NULL
        )
    )
  AND (
    p_estado IS NULL
        OR rb.id_estado = p_estado
    )
  AND (
    p_sector IS NULL
        OR rb.id_sector = p_sector
    )
  AND (
    v_cuil IS NULL
        OR regexp_replace(
                   COALESCE(
                           rb.afiliado_cuil_titular,
                           ''
                   ),
                   '[^0-9]',
                   '',
                   'g'
           ) LIKE '%' || v_cuil || '%'
    )
  AND (
    p_afiliado_int IS NULL
        OR rb.afiliado_int = p_afiliado_int
    )
  AND (
    NULLIF(
            btrim(p_id_tercerizadora),
            ''
    ) IS NULL
        OR upper(
                   COALESCE(
                           rb.id_tercerizadora,
                           ''
                   )
           ) = upper(
                   btrim(p_id_tercerizadora)
               )
    )
  AND (
    p_recupero IS NULL
        OR rb.recupero = p_recupero
    )
  AND (
    v_texto IS NULL

        OR rb.id::VARCHAR = btrim(p_texto)

            OR upper(
                COALESCE(
                    rb.observaciones,
                    ''
                )
            ) LIKE '%' || v_texto || '%'

            OR upper(
                COALESCE(
                    rb.sector_descripcion,
                    ''
                )
            ) LIKE '%' || v_texto || '%'

            OR upper(
                COALESCE(
                    rb.afiliado_nombre_apellido,
                    ''
                )
            ) LIKE '%' || v_texto || '%'

            OR upper(
                COALESCE(
                    rb.afiliado_documento,
                    ''
                )
            ) LIKE '%' || v_texto || '%'

            OR EXISTS (
                SELECT 1
                  FROM compras.requerimiento_detalle d
                  JOIN compras.articulo a
                    ON a.id_articulo =
                       d.id_articulo
                 WHERE d.id_requerimiento = rb.id
                   AND d.baja_fecha IS NULL
                   AND (
                        upper(
                            COALESCE(
                                a.descripcion,
                                ''
                            )
                        ) LIKE '%' || v_texto || '%'

                        OR upper(
                            COALESCE(
                                d.observaciones,
                                ''
                            )
                        ) LIKE '%' || v_texto || '%'
                   )
            )
    )
ORDER BY rb.id DESC;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.get_requerimiento(
    p_id_requerimiento INTEGER
)
    RETURNS SETOF compras.requerimiento_base_row
AS $func$
BEGIN
RETURN QUERY
SELECT rb.*
FROM compras.requerimiento_base() rb
WHERE rb.id = p_id_requerimiento;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.get_estado_actual_requerimiento(
    p_id_requerimiento INTEGER
)
    RETURNS TABLE (
                      id INTEGER,
                      descripcion VARCHAR
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT
    r.estado,
    compras.estado_requerimiento_descripcion(
            r.estado
    )
FROM compras.requerimiento r
WHERE r.id_requerimiento =
      p_id_requerimiento;
END;
$func$
LANGUAGE plpgsql
STABLE;

-- =====================================================================
-- REQUERIMIENTOS: ESCRITURA
-- =====================================================================

CREATE FUNCTION compras.guardar_requerimiento(
    p_id INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_afiliado_id_ospim INTEGER,
    p_afiliado_nombre VARCHAR,
    p_afiliado_apellido VARCHAR,
    p_afiliado_documento_tipo VARCHAR,
    p_afiliado_documento_nro VARCHAR,
    p_afiliado_direccion VARCHAR,
    p_afiliado_localidad VARCHAR,
    p_afiliado_provincia VARCHAR,
    p_afiliado_celular VARCHAR,
    p_afiliado_telefono VARCHAR,
    p_afiliado_email VARCHAR,
    p_id_sector INTEGER,
    p_cargo_ospim INTEGER,
    p_cargo_tercerizadora INTEGER,
    p_id_tercerizadora VARCHAR,
    p_recupero BOOLEAN,
    p_observaciones TEXT,
    p_usuario VARCHAR
)
    RETURNS INTEGER
AS $func$
DECLARE
v_id INTEGER;
    v_usuario VARCHAR(100);

    v_afiliado_cuil VARCHAR(20);

    v_cuil_anterior VARCHAR(20);
    v_inte_anterior INTEGER;
    v_cambio_afiliado BOOLEAN;
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

    v_afiliado_cuil := NULLIF(
        btrim(p_afiliado_cuil_titular),
        ''
    );

    IF p_id IS NULL OR p_id <= 0 THEN
        INSERT INTO compras.requerimiento (
            estado,
            id_sector,

            afiliado_cuil_titular,
            afiliado_int,
            afiliado_id_ospim,

            afiliado_nombre,
            afiliado_apellido,
            afiliado_documento_tipo,
            afiliado_documento_nro,
            afiliado_direccion,
            afiliado_localidad,
            afiliado_provincia,
            afiliado_celular,
            afiliado_telefono,
            afiliado_email,

            cargo_ospim,
            cargo_tercerizadora,
            id_tercerizadora,
            recupero,

            observaciones,

            alta_usr
        )
        VALUES (
            1,
            p_id_sector,

            v_afiliado_cuil,
            p_afiliado_int,
            p_afiliado_id_ospim,

            NULLIF(btrim(p_afiliado_nombre), ''),
            NULLIF(btrim(p_afiliado_apellido), ''),
            NULLIF(btrim(p_afiliado_documento_tipo), ''),
            NULLIF(btrim(p_afiliado_documento_nro), ''),
            NULLIF(btrim(p_afiliado_direccion), ''),
            NULLIF(btrim(p_afiliado_localidad), ''),
            NULLIF(btrim(p_afiliado_provincia), ''),
            NULLIF(btrim(p_afiliado_celular), ''),
            NULLIF(btrim(p_afiliado_telefono), ''),
            NULLIF(btrim(p_afiliado_email), ''),

            COALESCE(p_cargo_ospim, 0),
            COALESCE(
                p_cargo_tercerizadora,
                0
            ),
            NULLIF(
                btrim(p_id_tercerizadora),
                ''
            ),
            COALESCE(p_recupero, FALSE),

            NULLIF(
                btrim(p_observaciones),
                ''
            ),

            v_usuario
        )
        RETURNING id_requerimiento
             INTO v_id;

RETURN v_id;
END IF;

SELECT
    r.afiliado_cuil_titular,
    r.afiliado_int
INTO
    v_cuil_anterior,
    v_inte_anterior
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id
  AND r.baja_fecha IS NULL;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento a modificar.';
END IF;

    v_cambio_afiliado :=
           v_cuil_anterior IS DISTINCT FROM v_afiliado_cuil
        OR v_inte_anterior IS DISTINCT FROM p_afiliado_int;

UPDATE compras.requerimiento
SET id_sector = p_id_sector,

    afiliado_cuil_titular =
        v_afiliado_cuil,
    afiliado_int =
        p_afiliado_int,
    afiliado_id_ospim =
        p_afiliado_id_ospim,

    afiliado_nombre =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_nombre), '')
            ELSE afiliado_nombre
            END,

    afiliado_apellido =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_apellido), '')
            ELSE afiliado_apellido
            END,

    afiliado_documento_tipo =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_documento_tipo), '')
            ELSE afiliado_documento_tipo
            END,

    afiliado_documento_nro =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_documento_nro), '')
            ELSE afiliado_documento_nro
            END,

    afiliado_direccion =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_direccion), '')
            ELSE afiliado_direccion
            END,

    afiliado_localidad =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_localidad), '')
            ELSE afiliado_localidad
            END,

    afiliado_provincia =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_provincia), '')
            ELSE afiliado_provincia
            END,

    afiliado_celular =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_celular), '')
            ELSE afiliado_celular
            END,

    afiliado_telefono =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_telefono), '')
            ELSE afiliado_telefono
            END,

    afiliado_email =
        CASE
            WHEN v_cambio_afiliado
                THEN NULLIF(btrim(p_afiliado_email), '')
            ELSE afiliado_email
            END,

    cargo_ospim =
        COALESCE(p_cargo_ospim, 0),

    cargo_tercerizadora =
        COALESCE(
                p_cargo_tercerizadora,
                0
        ),

    id_tercerizadora =
        NULLIF(
                btrim(p_id_tercerizadora),
                ''
        ),

    recupero =
        COALESCE(p_recupero, FALSE),

    observaciones =
        NULLIF(
                btrim(p_observaciones),
                ''
        ),

    modi_fecha = now(),
    modi_usr = v_usuario

WHERE id_requerimiento = p_id
  AND estado = 1
  AND baja_fecha IS NULL

    RETURNING id_requerimiento
INTO v_id;

IF v_id IS NULL THEN
        RAISE EXCEPTION
            'La estructura solo puede modificarse en estado PENDIENTE.';
END IF;

RETURN v_id;
END;
$func$
LANGUAGE plpgsql;


CREATE FUNCTION compras.cambiar_estado_requerimiento(
    p_id_requerimiento INTEGER,
    p_estado_nuevo INTEGER,
    p_usuario VARCHAR
)
    RETURNS VOID
AS $func$
DECLARE
v_estado_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

SELECT r.estado
INTO v_estado_actual
FROM compras.requerimiento r
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND (
    r.baja_fecha IS NULL
        OR r.estado = 99
    )
    FOR UPDATE;

IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento.';
END IF;

    IF p_estado_nuevo = v_estado_actual THEN
        RAISE EXCEPTION
            'La transicion al mismo estado no es valida.';
END IF;

UPDATE compras.requerimiento
SET estado = p_estado_nuevo,
    modi_fecha = now(),
    modi_usr = v_usuario,
    baja_usr =
        CASE
            WHEN p_estado_nuevo = 99
                THEN v_usuario
            ELSE baja_usr
            END
WHERE id_requerimiento =
      p_id_requerimiento
  AND estado =
      v_estado_actual;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
END IF;
END;
$func$
LANGUAGE plpgsql;


CREATE FUNCTION compras.borrar_requerimiento(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
    RETURNS VOID
AS $func$
BEGIN
    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        99,
        p_usuario
    );
END;
$func$
LANGUAGE plpgsql;


-- =====================================================================
-- DETALLES
-- =====================================================================

CREATE FUNCTION compras.get_requerimiento_detalle(
    p_id_requerimiento INTEGER
)
    RETURNS TABLE (
                      id INTEGER,
                      id_requerimiento INTEGER,
                      id_articulo INTEGER,
                      articulo VARCHAR,
                      cantidad INTEGER,
                      precio_unitario_estimado NUMERIC,
                      precio_total_estimado NUMERIC,
                      id_prestador INTEGER,
                      prestador_cuit VARCHAR,
                      prestador_razon_social VARCHAR,
                      observaciones TEXT
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT
    d.id_detalle,
    d.id_requerimiento,
    d.id_articulo,
    a.descripcion,
    d.cantidad,
    d.precio_unitario_estimado,
    d.precio_total_estimado,
    d.id_prestador,
    p.cuit::VARCHAR,
    p.descripcion::VARCHAR,
    d.observaciones
FROM compras.requerimiento_detalle d
         JOIN compras.articulo a
              ON a.id_articulo = d.id_articulo
         LEFT JOIN public.prestador p
                   ON p.id_prestador = d.id_prestador
WHERE d.id_requerimiento =
      p_id_requerimiento
  AND d.baja_fecha IS NULL
ORDER BY d.id_detalle;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.guardar_requerimiento_detalle(
    p_id INTEGER,
    p_id_requerimiento INTEGER,
    p_id_articulo INTEGER,
    p_cantidad INTEGER,
    p_observaciones TEXT,
    p_usuario VARCHAR
)
    RETURNS INTEGER
AS $func$
DECLARE
v_id INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

    IF p_cantidad IS NULL
       OR p_cantidad <= 0 THEN
        RAISE EXCEPTION
            'La cantidad debe ser mayor a cero.';
END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento r
         WHERE r.id_requerimiento =
               p_id_requerimiento
           AND r.estado = 1
           AND r.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'Los detalles estructurales solo pueden modificarse en estado PENDIENTE.';
END IF;

    IF p_id IS NULL OR p_id <= 0 THEN
        INSERT INTO compras.requerimiento_detalle (
            id_requerimiento,
            id_articulo,
            cantidad,

            precio_unitario_estimado,
            precio_total_estimado,
            id_prestador,

            observaciones,

            alta_usr
        )
        VALUES (
            p_id_requerimiento,
            p_id_articulo,
            p_cantidad,

            NULL,
            NULL,
            NULL,

            NULLIF(
                btrim(p_observaciones),
                ''
            ),

            v_usuario
        )
        RETURNING id_detalle
             INTO v_id;

RETURN v_id;
END IF;

UPDATE compras.requerimiento_detalle
SET id_articulo = p_id_articulo,
    cantidad = p_cantidad,

    precio_unitario_estimado = NULL,
    precio_total_estimado = NULL,
    id_prestador = NULL,

    observaciones =
        NULLIF(
                btrim(p_observaciones),
                ''
        ),

    modi_fecha = now(),
    modi_usr = v_usuario

WHERE id_detalle = p_id
  AND id_requerimiento =
      p_id_requerimiento
  AND baja_fecha IS NULL

    RETURNING id_detalle
INTO v_id;

IF v_id IS NULL THEN
        RAISE EXCEPTION
            'No se encontro el detalle a modificar.';
END IF;

RETURN v_id;
END;
$func$
LANGUAGE plpgsql;


CREATE FUNCTION compras.borrar_requerimiento_detalle(
    p_id_detalle INTEGER,
    p_usuario VARCHAR
)
    RETURNS VOID
AS $func$
DECLARE
v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

UPDATE compras.requerimiento_detalle d
SET baja_fecha = now(),
    baja_usr = v_usuario,
    modi_fecha = now(),
    modi_usr = v_usuario
WHERE d.id_detalle = p_id_detalle
  AND d.baja_fecha IS NULL
  AND EXISTS (
    SELECT 1
    FROM compras.requerimiento r
    WHERE r.id_requerimiento =
          d.id_requerimiento
      AND r.estado = 1
      AND r.baja_fecha IS NULL
);

IF NOT FOUND THEN
        RAISE EXCEPTION
            'El detalle no existe o el requerimiento no esta PENDIENTE.';
END IF;
END;
$func$
LANGUAGE plpgsql;

-- =====================================================================
-- PRESTADORES PARA COTIZACION
-- =====================================================================

CREATE FUNCTION compras.listar_prestadores_cotizacion_requerimiento(
    p_id_requerimiento INTEGER
)
    RETURNS TABLE (
                      id_prestador INTEGER,
                      descripcion VARCHAR,
                      cuit VARCHAR,
                      email VARCHAR,
                      id_tipo_prestador INTEGER,
                      tipo_prestador VARCHAR
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT DISTINCT
    p.id_prestador::INTEGER,
    p.descripcion::VARCHAR,
    p.cuit::VARCHAR,
    NULLIF(
            btrim(p.contacto),
            ''
    )::VARCHAR,
    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR
FROM compras.requerimiento r
         JOIN compras.sector_tipo_prestador stp
              ON stp.id_sector =
                 r.id_sector
                  AND stp.activo = TRUE
                  AND stp.baja_fecha IS NULL
         JOIN public.prestador p
              ON p.id_tipo_prestador =
                 stp.id_tipo_prestador
         LEFT JOIN trae_tipos_prestadores() tp
                   ON tp.id_tipo_prestador =
                      p.id_tipo_prestador
         LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
                   ON rcp.id_requerimiento =
                      r.id_requerimiento
                       AND rcp.id_prestador =
                           p.id_prestador
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (1, 2)
  AND r.baja_fecha IS NULL
  AND p.baja_fecha IS NULL
  AND COALESCE(
              p.solicitar_cotizacion,
              FALSE
      ) = TRUE
  AND (
    -- Primer envio o recuperacion: se listan todos los
    -- candidatos vigentes. registrar_cotizacion_prestador
    -- evita reenviar ENVIADO o PROCESANDO.
    r.estado = 1

        OR (
        r.estado = 2
            AND (
            rcp.id_prestador IS NULL
                OR rcp.estado_envio IN (
                                        'PENDIENTE',
                                        'ERROR',
                                        'EMAIL_INVALIDO'
                )
            )
        )
    )
ORDER BY 6, 2;
END;
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.registrar_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,
    p_usuario VARCHAR
)
    RETURNS BOOLEAN
AS $func$
DECLARE
v_email VARCHAR(320);
    v_usuario VARCHAR(100);
    v_reservado BOOLEAN;
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

SELECT NULLIF(
               btrim(p.contacto),
               ''
       )::VARCHAR
INTO v_email
FROM compras.requerimiento r
         JOIN compras.sector_tipo_prestador stp
              ON stp.id_sector =
                 r.id_sector
                  AND stp.activo = TRUE
                  AND stp.baja_fecha IS NULL
         JOIN public.prestador p
              ON p.id_prestador =
                 p_id_prestador
                  AND p.id_tipo_prestador =
                      stp.id_tipo_prestador
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (1, 2)
  AND r.baja_fecha IS NULL
  AND p.baja_fecha IS NULL
  AND COALESCE(
              p.solicitar_cotizacion,
              FALSE
      ) = TRUE
    LIMIT 1;

IF NOT FOUND THEN
        RETURN FALSE;
END IF;

    /*
     * Reserva atomica.
     *
     * INSERT nuevo:
     *   PROCESANDO, intento 1.
     *
     * Conflicto:
     *   solo vuelve a reservar PENDIENTE, ERROR o EMAIL_INVALIDO.
     *
     * ENVIADO y PROCESANDO no se vuelven a tomar. De esta forma dos
     * ejecuciones concurrentes no pueden obtener TRUE para la misma fila.
     */
INSERT INTO compras.requerimiento_cotizacion_prestador (
    id_requerimiento,
    id_prestador,
    estado_envio,
    intentos,
    email_destino,
    fecha_ultimo_intento,
    alta_usr
)
VALUES (
           p_id_requerimiento,
           p_id_prestador,
           'PROCESANDO',
           1,
           v_email,
           now(),
           v_usuario
       )
    ON CONFLICT (
        id_requerimiento,
        id_prestador
    )
    DO UPDATE
               SET estado_envio = 'PROCESANDO',
               intentos =
               compras.requerimiento_cotizacion_prestador.intentos
               + 1,
               email_destino = EXCLUDED.email_destino,
               fecha_ultimo_intento = now(),
               fecha_envio = NULL,
               ultimo_error = NULL,
               modi_fecha = now(),
               modi_usr = v_usuario
       WHERE compras.requerimiento_cotizacion_prestador.estado_envio
               IN (
               'PENDIENTE',
               'ERROR',
               'EMAIL_INVALIDO'
               )
               RETURNING TRUE
       INTO v_reservado;

RETURN COALESCE(
        v_reservado,
        FALSE
       );
END;
$func$
LANGUAGE plpgsql;


CREATE FUNCTION compras.finalizar_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,
    p_estado VARCHAR,
    p_error TEXT
)
    RETURNS BOOLEAN
AS $func$
DECLARE
v_estado VARCHAR(20);
    v_error TEXT;
BEGIN
    v_estado := upper(
        btrim(
            COALESCE(p_estado, '')
        )
    );

    IF v_estado NOT IN (
        'ENVIADO',
        'ERROR',
        'EMAIL_INVALIDO'
    ) THEN
        RAISE EXCEPTION
            'Estado final de notificacion invalido: %.',
            v_estado;
END IF;

    v_error := NULLIF(
        left(
            btrim(
                COALESCE(p_error, '')
            ),
            4000
        ),
        ''
    );

UPDATE compras.requerimiento_cotizacion_prestador
SET estado_envio = v_estado,

    fecha_envio =
        CASE
            WHEN v_estado = 'ENVIADO'
                THEN now()
            ELSE NULL
            END,

    ultimo_error =
        CASE
            WHEN v_estado = 'ENVIADO'
                THEN NULL
            ELSE v_error
            END,

    modi_fecha = now()

WHERE id_requerimiento =
      p_id_requerimiento
  AND id_prestador =
      p_id_prestador
  AND estado_envio =
      'PROCESANDO';

RETURN FOUND;
END;
$func$
LANGUAGE plpgsql;


CREATE FUNCTION compras.buscar_prestadores_enviados(
    p_id_requerimiento INTEGER,
    p_texto VARCHAR,
    p_limite INTEGER
)
    RETURNS TABLE (
                      id_prestador INTEGER,
                      descripcion VARCHAR,
                      cuit VARCHAR,
                      email VARCHAR,
                      id_tipo_prestador INTEGER,
                      tipo_prestador VARCHAR
                  )
    AS $func$
DECLARE
v_texto VARCHAR;
    v_cuit VARCHAR;
    v_limite INTEGER;
BEGIN
    v_texto := NULLIF(
        upper(btrim(p_texto)),
        ''
    );

    v_cuit := NULLIF(
        regexp_replace(
            COALESCE(p_texto, ''),
            '[^0-9]',
            '',
            'g'
        ),
        ''
    );

    v_limite := LEAST(
        GREATEST(
            COALESCE(p_limite, 20),
            1
        ),
        50
    );

RETURN QUERY
SELECT DISTINCT
    p.id_prestador::INTEGER,
    p.descripcion::VARCHAR,
    p.cuit::VARCHAR,
    p.contacto::VARCHAR,
    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR
FROM compras.requerimiento r
         JOIN compras.requerimiento_cotizacion_prestador rcp
              ON rcp.id_requerimiento =
                 r.id_requerimiento
                  AND rcp.estado_envio =
                      'ENVIADO'
         JOIN public.prestador p
              ON p.id_prestador =
                 rcp.id_prestador
         LEFT JOIN trae_tipos_prestadores() tp
                   ON tp.id_tipo_prestador =
                      p.id_tipo_prestador
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (2, 3)
  AND (
    v_texto IS NULL

        OR upper(
                   COALESCE(
                           p.descripcion,
                           ''
                   )
           ) LIKE '%' || v_texto || '%'

        OR (
        v_cuit IS NOT NULL
            AND regexp_replace(
                        COALESCE(
                                p.cuit,
                                ''
                        ),
                        '[^0-9]',
                        '',
                        'g'
                ) LIKE '%' || v_cuit || '%'
        )
    )
ORDER BY 2
    LIMIT v_limite;
END;
$func$
LANGUAGE plpgsql
STABLE;

-- =====================================================================
-- PDF
-- =====================================================================

CREATE FUNCTION compras.get_requerimiento_compra_pdf(
    p_id_requerimiento INTEGER
)
    RETURNS TABLE (
                      id_requerimiento INTEGER,
                      alta_fecha TIMESTAMP WITHOUT TIME ZONE,
                      alta_usr VARCHAR,

                      id_estado INTEGER,
                      estado_descripcion VARCHAR,

                      id_sector INTEGER,
                      sector_descripcion VARCHAR,
                      requiere_afiliado BOOLEAN,

                      afiliado_id_ospim INTEGER,
                      afiliado_int INTEGER,
                      afiliado_nombre_apellido VARCHAR,
                      afiliado_documento VARCHAR,

                      afiliado_direccion VARCHAR,
                      afiliado_localidad VARCHAR,
                      afiliado_provincia VARCHAR,
                      afiliado_celular VARCHAR,
                      afiliado_telefono VARCHAR,
                      afiliado_email VARCHAR,

                      cargo_ospim INTEGER,
                      cargo_tercerizadora INTEGER,
                      id_tercerizadora VARCHAR,
                      recupero BOOLEAN,
                      observaciones TEXT,

                      detalle_id INTEGER,
                      detalle_orden INTEGER,
                      id_articulo INTEGER,
                      articulo VARCHAR,
                      cantidad INTEGER,

                      precio_unitario_estimado NUMERIC,
                      precio_total_estimado NUMERIC,

                      prestador_razon_social VARCHAR,
                      prestador_cuit VARCHAR,

                      detalle_observaciones TEXT
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT
    rb.id,
    rb.alta_fecha,
    rb.alta_usr,

    rb.id_estado,
    rb.estado_descripcion,

    rb.id_sector,
    rb.sector_descripcion,
    rb.requiere_afiliado,

    rb.afiliado_id_ospim,
    rb.afiliado_int,
    rb.afiliado_nombre_apellido,
    rb.afiliado_documento,

    rb.afiliado_direccion,
    rb.afiliado_localidad,
    rb.afiliado_provincia,
    rb.afiliado_celular,
    rb.afiliado_telefono,
    rb.afiliado_email,

    rb.cargo_ospim,
    rb.cargo_tercerizadora,
    rb.id_tercerizadora,
    rb.recupero,
    rb.observaciones,

    d.id,

    CASE
        WHEN d.id IS NULL THEN NULL
        ELSE row_number() OVER (
                PARTITION BY rb.id
                ORDER BY d.id
            )::INTEGER
END,

        d.id_articulo,
        d.articulo,
        d.cantidad,

        d.precio_unitario_estimado,
        d.precio_total_estimado,

        d.prestador_razon_social,
        d.prestador_cuit,

        d.observaciones

    FROM compras.requerimiento_base() rb

    LEFT JOIN compras.get_requerimiento_detalle(
        p_id_requerimiento
    ) d
      ON d.id_requerimiento = rb.id

    WHERE rb.id = p_id_requerimiento

    ORDER BY d.id NULLS LAST;
END;
$func$
LANGUAGE plpgsql
STABLE;

-- =====================================================================
-- VALIDACIONES DE INSTALACION Y SMOKE TRANSACCIONAL
-- =====================================================================

DO $verificacion$
DECLARE
    v_sectores INTEGER;
    v_estados TEXT;
    v_guardar OID;
    v_pdf OID;
BEGIN
    SELECT count(*)
      INTO v_sectores
      FROM compras.sector_requerimiento
     WHERE activo = TRUE
       AND baja_fecha IS NULL;

    IF v_sectores <> 8 THEN
        RAISE EXCEPTION
            'La carga inicial de sectores es invalida. Total: %.',
            v_sectores;
    END IF;

    SELECT string_agg(
               e.id::TEXT || ':' || e.descripcion,
               ',' ORDER BY e.id
           )
      INTO v_estados
      FROM compras.listar_estados_requerimiento() e;

    IF v_estados <>
       '1:PENDIENTE,2:A COTIZAR,3:COTIZADO,4:RECLAMO (RP),5:ORDEN DE COMPRA,99:ANULADO' THEN
        RAISE EXCEPTION
            'Catalogo de estados inesperado: %.',
            v_estados;
    END IF;

    IF EXISTS (
        SELECT 1
          FROM pg_constraint c
          JOIN pg_namespace propio
            ON propio.oid = c.connamespace
          JOIN pg_class referida
            ON referida.oid = c.confrelid
          JOIN pg_namespace esquema_referido
            ON esquema_referido.oid = referida.relnamespace
         WHERE c.contype = 'f'
           AND propio.nspname = 'compras'
           AND esquema_referido.nspname <> 'compras'
    ) THEN
        RAISE EXCEPTION
            'El esquema compras contiene claves foraneas hacia otros esquemas.';
    END IF;

    IF to_regclass('public.prestador') IS NULL THEN
        RAISE EXCEPTION
            'No existe la dependencia de solo lectura public.prestador.';
    END IF;

    IF to_regprocedure('trae_tipos_prestadores()') IS NULL THEN
        RAISE EXCEPTION
            'No existe la dependencia de solo lectura trae_tipos_prestadores().';
    END IF;

    v_guardar := to_regprocedure(
        'compras.guardar_requerimiento(integer, character varying, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, integer, character varying, boolean, text, character varying)'
    )::OID;

    IF v_guardar IS NULL THEN
        RAISE EXCEPTION
            'Falta guardar_requerimiento con 21 argumentos canonicos.';
    END IF;

    IF (
        SELECT p.pronargs
          FROM pg_proc p
         WHERE p.oid = v_guardar
    ) <> 21 THEN
        RAISE EXCEPTION
            'guardar_requerimiento no tiene 21 argumentos.';
    END IF;

    v_pdf := to_regprocedure(
        'compras.get_requerimiento_compra_pdf(integer)'
    )::OID;

    IF v_pdf IS NULL THEN
        RAISE EXCEPTION
            'Falta get_requerimiento_compra_pdf(integer).';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM pg_proc p
         WHERE p.oid = v_pdf
           AND array_position(p.proargnames, 'afiliado_documento') IS NOT NULL
           AND array_position(p.proargnames, 'afiliado_id_ospim') IS NOT NULL
           AND array_position(p.proargnames, 'total_general') IS NULL
    ) THEN
        RAISE EXCEPTION
            'Contrato PDF incompatible.';
    END IF;

    PERFORM 1
      FROM compras.listar_tipos_prestador_sector(1)
     LIMIT 1;

    PERFORM 1
      FROM compras.listar_prestadores_cotizacion_requerimiento(0)
     LIMIT 1;

    PERFORM 1
      FROM compras.buscar_prestadores_enviados(0, NULL, 1)
     LIMIT 1;
END;
$verificacion$;


DO $smoke$
DECLARE
    v_id INTEGER;
    v_articulo INTEGER;
    v_detalle INTEGER;
    v_pdf_id_ospim INTEGER;
    v_pdf_documento VARCHAR;
    v_estado INTEGER;
BEGIN
    v_id := compras.guardar_requerimiento(
        NULL,
        '20111111112',
        1,
        123456,
        'Nombre',
        'Apellido',
        'DNI',
        '11222333',
        'Calle 123',
        'Localidad',
        'Provincia',
        '111-222',
        '333-444',
        'afiliado@example.com',
        1,
        100,
        0,
        NULL,
        FALSE,
        'Smoke esquema unificado',
        'smoke'
    );

    IF (
        SELECT r.afiliado_id_ospim
          FROM compras.requerimiento r
         WHERE r.id_requerimiento = v_id
    ) IS DISTINCT FROM 123456 THEN
        RAISE EXCEPTION
            'SMOKE: afiliado_id_ospim no persistio.';
    END IF;

    SELECT
        pdf.afiliado_id_ospim,
        pdf.afiliado_documento
      INTO
        v_pdf_id_ospim,
        v_pdf_documento
      FROM compras.get_requerimiento_compra_pdf(v_id) pdf
     LIMIT 1;

    IF v_pdf_id_ospim IS DISTINCT FROM 123456 THEN
        RAISE EXCEPTION
            'SMOKE: PDF no expone afiliado_id_ospim.';
    END IF;

    IF v_pdf_documento IS DISTINCT FROM 'DNI 11222333' THEN
        RAISE EXCEPTION
            'SMOKE: PDF no conserva afiliado_documento. Valor=%.',
            v_pdf_documento;
    END IF;

    v_articulo := compras.guardar_articulo(
        NULL,
        1,
        'Articulo smoke esquema unificado'
    );

    v_detalle := compras.guardar_requerimiento_detalle(
        NULL,
        v_id,
        v_articulo,
        2,
        'Detalle smoke',
        'smoke'
    );

    INSERT INTO compras.requerimiento_cotizacion_prestador (
        id_requerimiento,
        id_prestador,
        estado_envio,
        intentos,
        email_destino,
        fecha_envio,
        alta_usr
    )
    VALUES (
        v_id,
        9001,
        'ENVIADO',
        1,
        'prestador@example.com',
        now(),
        'smoke'
    );

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            1,
            'smoke'
        );

        RAISE EXCEPTION
            'SMOKE: se acepto transicion al mismo estado.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM = 'SMOKE: se acepto transicion al mismo estado.' THEN
                RAISE;
            END IF;

            IF SQLSTATE <> 'P0001'
               OR SQLERRM NOT LIKE
                  'La transicion al mismo estado no es valida.%' THEN
                RAISE EXCEPTION
                    'SMOKE: rechazo inesperado del mismo estado. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    PERFORM compras.cambiar_estado_requerimiento(
        v_id,
        2,
        'smoke'
    );

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            1,
            'smoke'
        );

        RAISE EXCEPTION
            'SMOKE: se acepto transicion 2 -> 1.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM = 'SMOKE: se acepto transicion 2 -> 1.' THEN
                RAISE;
            END IF;

            IF SQLSTATE <> 'P0001'
               OR SQLERRM <> 'Transicion de estado invalida: 2 -> 1.' THEN
                RAISE EXCEPTION
                    'SMOKE: rechazo inesperado 2 -> 1. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            4,
            'smoke'
        );

        RAISE EXCEPTION
            'SMOKE: se acepto transicion 2 -> 4.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM = 'SMOKE: se acepto transicion 2 -> 4.' THEN
                RAISE;
            END IF;

            IF SQLSTATE <> 'P0001'
               OR SQLERRM <> 'Transicion de estado invalida: 2 -> 4.' THEN
                RAISE EXCEPTION
                    'SMOKE: rechazo inesperado 2 -> 4. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            5,
            'smoke'
        );

        RAISE EXCEPTION
            'SMOKE: se acepto transicion 2 -> 5.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM = 'SMOKE: se acepto transicion 2 -> 5.' THEN
                RAISE;
            END IF;

            IF SQLSTATE <> 'P0001'
               OR SQLERRM <> 'Transicion de estado invalida: 2 -> 5.' THEN
                RAISE EXCEPTION
                    'SMOKE: rechazo inesperado 2 -> 5. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    UPDATE compras.requerimiento_detalle
       SET precio_unitario_estimado = 10.00,
           id_prestador = 9001,
           modi_usr = 'smoke'
     WHERE id_detalle = v_detalle;

    PERFORM compras.cambiar_estado_requerimiento(
        v_id,
        3,
        'smoke'
    );

    SELECT r.estado
      INTO v_estado
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = v_id;

    IF v_estado IS DISTINCT FROM 3 THEN
        RAISE EXCEPTION
            'SMOKE: 2 -> 3 no dejo el requerimiento COTIZADO.';
    END IF;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            2,
            'smoke'
        );

        RAISE EXCEPTION
            'SMOKE: se acepto salida desde COTIZADO.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM = 'SMOKE: se acepto salida desde COTIZADO.' THEN
                RAISE;
            END IF;

            IF SQLSTATE <> 'P0001'
               OR SQLERRM <>
                  'El requerimiento no puede modificarse en el estado actual.' THEN
                RAISE EXCEPTION
                    'SMOKE: rechazo inesperado desde COTIZADO. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    DELETE FROM compras.requerimiento_cotizacion_prestador
     WHERE id_requerimiento = v_id;

    DELETE FROM compras.requerimiento_detalle
     WHERE id_requerimiento = v_id;

    DELETE FROM compras.requerimiento
     WHERE id_requerimiento = v_id;

    DELETE FROM compras.articulo
     WHERE id_articulo = v_articulo;

    PERFORM setval(
        pg_get_serial_sequence(
            'compras.requerimiento',
            'id_requerimiento'
        ),
        1,
        FALSE
    );

    PERFORM setval(
        pg_get_serial_sequence(
            'compras.requerimiento_detalle',
            'id_detalle'
        ),
        1,
        FALSE
    );

    PERFORM setval(
        pg_get_serial_sequence(
            'compras.articulo',
            'id_articulo'
        ),
        1,
        FALSE
    );

    RAISE NOTICE 'COMPRAS_SCHEMA_SMOKE_OK';
END;
$smoke$;

COMMIT;

-- =====================================================================
-- CONSULTAS MANUALES DE VERIFICACION
-- =====================================================================

-- SELECT *
-- FROM compras.listar_estados_requerimiento();

-- SELECT *
-- FROM compras.listar_sector_requerimiento();

-- SELECT *
-- FROM compras.buscar_requerimientos(
--     NULL, NULL, NULL, NULL, NULL, NULL, NULL
-- );

-- SELECT *
-- FROM compras.listar_prestadores_cotizacion_requerimiento(
--     :id_requerimiento
-- );

-- SELECT *
-- FROM compras.buscar_prestadores_enviados(
--     :id_requerimiento,
--     :texto,
--     20
-- );

-- SELECT *
-- FROM compras.get_requerimiento_compra_pdf(
--     :id_requerimiento
-- );