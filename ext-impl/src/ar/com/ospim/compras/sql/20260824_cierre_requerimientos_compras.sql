BEGIN;

-- =====================================================================
-- REQUERIMIENTO: MARCA LEGALES PROPIA
-- =====================================================================

ALTER TABLE compras.requerimiento
    ADD COLUMN IF NOT EXISTS legales BOOLEAN;

UPDATE compras.requerimiento
SET legales = FALSE
WHERE legales IS NULL;

ALTER TABLE compras.requerimiento
    ALTER COLUMN legales SET DEFAULT FALSE;

ALTER TABLE compras.requerimiento
    ALTER COLUMN legales SET NOT NULL;

DO $do$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n
          ON n.oid = t.typnamespace
        JOIN pg_attribute a
          ON a.attrelid = t.typrelid
        WHERE n.nspname = 'compras'
          AND t.typname = 'requerimiento_base_row'
          AND a.attname = 'legales'
          AND a.attnum > 0
          AND NOT a.attisdropped
    ) THEN
        ALTER TYPE compras.requerimiento_base_row
            ADD ATTRIBUTE legales BOOLEAN;
    END IF;
END;
$do$;

CREATE OR REPLACE FUNCTION compras.requerimiento_base()
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
    NULLIF(concat_ws(', ', NULLIF(btrim(r.afiliado_apellido), ''),
                            NULLIF(btrim(r.afiliado_nombre), '')), '')::VARCHAR,
    r.afiliado_documento_tipo,
    r.afiliado_documento_nro,
    NULLIF(concat_ws(' ', NULLIF(btrim(r.afiliado_documento_tipo), ''),
                           NULLIF(btrim(r.afiliado_documento_nro), '')), '')::VARCHAR,
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
    r.surge,
    r.observaciones,
    r.estado,
    compras.estado_requerimiento_descripcion(r.estado),
    r.legales
FROM compras.requerimiento r
JOIN compras.sector_requerimiento s
  ON s.id_sector = r.id_sector;
END;
$func$
LANGUAGE plpgsql
STABLE;

-- =====================================================================
-- SECTORES SELECCIONABLES: NO ALTERA HISTÓRICOS
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.es_sector_seleccionable_compras(
    p_id_sector INTEGER
)
RETURNS BOOLEAN
AS $func$
    SELECT EXISTS (
        SELECT 1
        FROM compras.sector_requerimiento s
        WHERE s.id_sector = p_id_sector
          AND s.activo = TRUE
          AND s.baja_fecha IS NULL
          AND UPPER(BTRIM(s.descripcion)) NOT IN ('LEGALES', 'OTROS')
    );
$func$
LANGUAGE sql
STABLE;

CREATE OR REPLACE FUNCTION compras.listar_sector_requerimiento()
RETURNS TABLE (
    id INTEGER,
    descripcion VARCHAR,
    requiere_afiliado BOOLEAN
)
AS $func$
    SELECT s.id_sector, s.descripcion, s.requiere_afiliado
    FROM compras.sector_requerimiento s
    WHERE compras.es_sector_seleccionable_compras(s.id_sector)
    ORDER BY s.descripcion;
$func$
LANGUAGE sql
STABLE;

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
    p_surge BOOLEAN,
    p_legales BOOLEAN,
    p_observaciones TEXT,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
    v_legales_actual BOOLEAN;
    v_id_sector_actual INTEGER;
    v_cuil_actual VARCHAR(20);
    v_integrante_actual INTEGER;
BEGIN
    IF p_id IS NULL OR p_id <= 0 THEN
        IF NOT compras.es_sector_seleccionable_compras(p_id_sector) THEN
            RAISE EXCEPTION
                'El sector informado no está habilitado para nuevas compras.';
        END IF;
    ELSE
        SELECT
            r.legales,
            r.id_sector,
            r.afiliado_cuil_titular,
            r.afiliado_int
        INTO
            v_legales_actual,
            v_id_sector_actual,
            v_cuil_actual,
            v_integrante_actual
        FROM compras.requerimiento r
        WHERE r.id_requerimiento = p_id
          AND r.baja_fecha IS NULL
        FOR UPDATE;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'No se encontró el requerimiento a modificar.';
        END IF;

        IF v_legales_actual IS DISTINCT FROM COALESCE(p_legales, FALSE) THEN
            RAISE EXCEPTION
                'La marca LEGALES sólo puede definirse durante el alta.';
        END IF;

        IF v_id_sector_actual IS DISTINCT FROM p_id_sector THEN
            RAISE EXCEPTION
                'El sector no puede modificarse después del alta.';
        END IF;

        IF v_cuil_actual IS DISTINCT FROM NULLIF(BTRIM(p_afiliado_cuil_titular), '')
           OR v_integrante_actual IS DISTINCT FROM p_afiliado_int THEN
            RAISE EXCEPTION
                'El afiliado no puede modificarse después del alta.';
        END IF;
    END IF;

    v_id := compras.guardar_requerimiento(
        p_id,
        p_afiliado_cuil_titular,
        p_afiliado_int,
        p_afiliado_id_ospim,
        p_afiliado_nombre,
        p_afiliado_apellido,
        p_afiliado_documento_tipo,
        p_afiliado_documento_nro,
        p_afiliado_direccion,
        p_afiliado_localidad,
        p_afiliado_provincia,
        p_afiliado_celular,
        p_afiliado_telefono,
        p_afiliado_email,
        p_id_sector,
        p_cargo_ospim,
        p_cargo_tercerizadora,
        p_id_tercerizadora,
        p_recupero,
        p_surge,
        p_observaciones,
        p_usuario
    );

    IF p_id IS NULL OR p_id <= 0 THEN
        UPDATE compras.requerimiento
        SET legales = COALESCE(p_legales, FALSE)
        WHERE id_requerimiento = v_id;
    END IF;

    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;

-- =====================================================================
-- CONFIGURACIÓN PRESTADOR POR SECTOR Y TIPO DE COTIZACIÓN
-- =====================================================================

ALTER TABLE compras.sector_tipo_prestador
    ADD COLUMN IF NOT EXISTS id_tipo_prestacion SMALLINT;

DO $do$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'compras.sector_tipo_prestador'::regclass
          AND conname = 'pk_compras_sector_tipo_prestador'
    ) THEN
        ALTER TABLE compras.sector_tipo_prestador
            DROP CONSTRAINT pk_compras_sector_tipo_prestador;
    END IF;
END;
$do$;

UPDATE compras.sector_tipo_prestador stp
SET id_tipo_prestacion = catalogo.id_tipo_prestacion
FROM (
    SELECT id_sector, MIN(id_tipo_prestacion) AS id_tipo_prestacion
    FROM compras.tipo_prestacion
    GROUP BY id_sector
) catalogo
WHERE catalogo.id_sector = stp.id_sector
  AND stp.id_tipo_prestacion IS NULL;

DO $do$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM compras.sector_tipo_prestador
        WHERE id_tipo_prestacion IS NULL
    ) THEN
        RAISE EXCEPTION
            'No se pudo clasificar toda la configuración sector/prestador.';
    END IF;
END;
$do$;

INSERT INTO compras.sector_tipo_prestador (
    id_sector,
    id_tipo_prestacion,
    id_tipo_prestador,
    activo,
    alta_fecha,
    alta_usr,
    modi_fecha,
    modi_usr,
    baja_fecha,
    baja_usr
)
SELECT
    stp.id_sector,
    tp.id_tipo_prestacion,
    stp.id_tipo_prestador,
    stp.activo,
    stp.alta_fecha,
    stp.alta_usr,
    stp.modi_fecha,
    stp.modi_usr,
    stp.baja_fecha,
    stp.baja_usr
FROM compras.sector_tipo_prestador stp
JOIN compras.tipo_prestacion tp
  ON tp.id_sector = stp.id_sector
WHERE tp.id_tipo_prestacion <> stp.id_tipo_prestacion
  AND NOT EXISTS (
      SELECT 1
      FROM compras.sector_tipo_prestador existente
      WHERE existente.id_sector = stp.id_sector
        AND existente.id_tipo_prestacion = tp.id_tipo_prestacion
        AND existente.id_tipo_prestador = stp.id_tipo_prestador
  );

ALTER TABLE compras.sector_tipo_prestador
    ALTER COLUMN id_tipo_prestacion SET NOT NULL;

DO $do$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'compras.sector_tipo_prestador'::regclass
          AND conname = 'fk_compras_sector_tipo_prestador_cotizacion'
    ) THEN
        ALTER TABLE compras.sector_tipo_prestador
            ADD CONSTRAINT fk_compras_sector_tipo_prestador_cotizacion
            FOREIGN KEY (id_tipo_prestacion)
            REFERENCES compras.tipo_prestacion (id_tipo_prestacion);
    END IF;
END;
$do$;

ALTER TABLE compras.sector_tipo_prestador
    ADD CONSTRAINT pk_compras_sector_tipo_prestador
    PRIMARY KEY (
        id_sector,
        id_tipo_prestacion,
        id_tipo_prestador
    );

CREATE INDEX IF NOT EXISTS ix_compras_sector_tipo_cotizacion_activo
    ON compras.sector_tipo_prestador (
        id_sector,
        id_tipo_prestacion,
        id_tipo_prestador
    )
    WHERE activo = TRUE AND baja_fecha IS NULL;

CREATE OR REPLACE FUNCTION
compras.listar_configuracion_prestador_tipo_cotizacion(
    p_id_sector INTEGER
)
RETURNS TABLE (
    id_tipo_prestacion INTEGER,
    tipo_cotizacion VARCHAR,
    id_tipo_prestador INTEGER,
    descripcion VARCHAR,
    activo BOOLEAN
)
AS $func$
    SELECT
        tc.id_tipo_prestacion::INTEGER,
        tc.descripcion::VARCHAR,
        tp.id_tipo_prestador::INTEGER,
        tp.descripcion::VARCHAR,
        COALESCE(stp.activo, FALSE)::BOOLEAN
    FROM compras.tipo_prestacion tc
    CROSS JOIN trae_tipos_prestadores() tp
    LEFT JOIN compras.sector_tipo_prestador stp
      ON stp.id_sector = tc.id_sector
     AND stp.id_tipo_prestacion = tc.id_tipo_prestacion
     AND stp.id_tipo_prestador = tp.id_tipo_prestador::INTEGER
     AND stp.baja_fecha IS NULL
    WHERE tc.id_sector = p_id_sector
    ORDER BY tc.id_tipo_prestacion, tp.descripcion;
$func$
LANGUAGE sql
STABLE;

CREATE OR REPLACE FUNCTION
compras.guardar_sector_tipo_prestador_cotizacion(
    p_id_sector INTEGER,
    p_id_tipo_prestacion INTEGER,
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
        FROM compras.tipo_prestacion tp
        WHERE tp.id_tipo_prestacion = p_id_tipo_prestacion
          AND tp.id_sector = p_id_sector
    ) THEN
        RAISE EXCEPTION
            'El tipo de cotización no corresponde al sector informado.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM trae_tipos_prestadores() tp
        WHERE tp.id_tipo_prestador = p_id_tipo_prestador
    ) THEN
        RAISE EXCEPTION 'El tipo de prestador informado no existe.';
    END IF;

    INSERT INTO compras.sector_tipo_prestador (
        id_sector,
        id_tipo_prestacion,
        id_tipo_prestador,
        activo,
        alta_usr
    ) VALUES (
        p_id_sector,
        p_id_tipo_prestacion,
        p_id_tipo_prestador,
        COALESCE(p_activo, TRUE),
        v_usuario
    )
    ON CONFLICT (
        id_sector,
        id_tipo_prestacion,
        id_tipo_prestador
    ) DO UPDATE
    SET activo = EXCLUDED.activo,
        modi_fecha = now(),
        modi_usr = v_usuario,
        baja_fecha = NULL,
        baja_usr = NULL;
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.guardar_sector_tipo_prestador(
    p_id_sector INTEGER,
    p_id_tipo_prestador INTEGER,
    p_activo BOOLEAN,
    p_usuario VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_tipo RECORD;
BEGIN
    FOR v_tipo IN
        SELECT id_tipo_prestacion
        FROM compras.tipo_prestacion
        WHERE id_sector = p_id_sector
    LOOP
        PERFORM compras.guardar_sector_tipo_prestador_cotizacion(
            p_id_sector,
            v_tipo.id_tipo_prestacion,
            p_id_tipo_prestador,
            p_activo,
            p_usuario
        );
    END LOOP;
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.es_prestador_compatible_cotizacion(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER
)
RETURNS BOOLEAN
AS $func$
    SELECT EXISTS (
        SELECT 1
        FROM compras.requerimiento r
        JOIN public.prestador p
          ON p.id_prestador = p_id_prestador
         AND p.baja_fecha IS NULL
         AND COALESCE(p.solicitar_cotizacion, FALSE) = TRUE
        JOIN compras.sector_tipo_prestador stp
          ON stp.id_sector = r.id_sector
         AND stp.id_tipo_prestador = p.id_tipo_prestador
         AND stp.activo = TRUE
         AND stp.baja_fecha IS NULL
        WHERE r.id_requerimiento = p_id_requerimiento
          AND r.baja_fecha IS NULL
          AND EXISTS (
              SELECT 1
              FROM compras.requerimiento_detalle d
              WHERE d.id_requerimiento = r.id_requerimiento
                AND d.id_tipo_prestacion = stp.id_tipo_prestacion
                AND d.baja_fecha IS NULL
          )
    );
$func$
LANGUAGE sql
STABLE;

CREATE OR REPLACE FUNCTION compras.validar_prestador_cotizacion_fila()
RETURNS TRIGGER
AS $func$
BEGIN
    IF NOT compras.es_prestador_compatible_cotizacion(
        NEW.id_requerimiento,
        NEW.id_prestador
    ) THEN
        RAISE EXCEPTION
            'El prestador no es compatible con el sector y tipo de cotización.';
    END IF;

    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_compras_cotizacion_prestador_compatible
    ON compras.requerimiento_cotizacion_prestador;

CREATE TRIGGER trg_compras_cotizacion_prestador_compatible
    BEFORE INSERT OR UPDATE OF id_requerimiento, id_prestador
    ON compras.requerimiento_cotizacion_prestador
    FOR EACH ROW
    EXECUTE PROCEDURE compras.validar_prestador_cotizacion_fila();

CREATE OR REPLACE FUNCTION compras.listar_prestadores_cotizacion_requerimiento(
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
    SELECT DISTINCT
        p.id_prestador::INTEGER,
        p.descripcion::VARCHAR,
        p.cuit::VARCHAR,
        compras.resolver_email_cotizacion_prestador(
            p.id_prestador
        )::VARCHAR,
        p.id_tipo_prestador::INTEGER,
        tp.descripcion::VARCHAR
    FROM compras.requerimiento r
    JOIN compras.sector_tipo_prestador stp
      ON stp.id_sector = r.id_sector
     AND stp.activo = TRUE
     AND stp.baja_fecha IS NULL
    JOIN public.prestador p
      ON p.id_tipo_prestador = stp.id_tipo_prestador
    LEFT JOIN trae_tipos_prestadores() tp
      ON tp.id_tipo_prestador = p.id_tipo_prestador
    LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
      ON rcp.id_requerimiento = r.id_requerimiento
     AND rcp.id_prestador = p.id_prestador
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.estado IN (1, 2)
      AND r.baja_fecha IS NULL
      AND p.baja_fecha IS NULL
      AND COALESCE(p.solicitar_cotizacion, FALSE) = TRUE
      AND EXISTS (
          SELECT 1
          FROM compras.requerimiento_detalle d
          WHERE d.id_requerimiento = r.id_requerimiento
            AND d.id_tipo_prestacion = stp.id_tipo_prestacion
            AND d.baja_fecha IS NULL
      )
      AND (
          r.estado = 1
          OR (
              r.estado = 2
              AND (
                  rcp.id_prestador IS NULL
                  OR rcp.estado_envio IN (
                      'PENDIENTE', 'ERROR', 'EMAIL_INVALIDO'
                  )
              )
          )
      )
    ORDER BY 6, 2;
$func$
LANGUAGE sql
STABLE;

-- =====================================================================
-- EMAIL CANÓNICO DEL PRESTADOR: CONTACTO ELECTRÓNICO TIPO E
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.resolver_email_cotizacion_prestador(
    p_id_prestador INTEGER
)
RETURNS VARCHAR
AS $func$
    SELECT NULLIF(BTRIM(ce.contacto), '')::VARCHAR
    FROM public.prestad_contacto_e pce
    JOIN public.contacto_e ce
      ON ce.id_contacto_e = pce.id_contacto_e
    JOIN public.prestador p
      ON p.id_prestador = pce.id_prestador
     AND p.baja_fecha IS NULL
    WHERE pce.id_prestador = p_id_prestador
      AND UPPER(BTRIM(COALESCE(ce.tipo_contacto_e, ''))) = 'E'
      AND ce.baja_fecha IS NULL
      AND (pce.vigen_desde IS NULL OR pce.vigen_desde <= LOCALTIMESTAMP)
      AND (ce.vigen_desde IS NULL OR ce.vigen_desde <= LOCALTIMESTAMP)
      AND NULLIF(BTRIM(ce.contacto), '')
          ~* '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$'
    ORDER BY COALESCE(ce.modi_fecha, ce.alta_fecha,
                      ce.vigen_desde, pce.vigen_desde) DESC NULLS LAST,
             ce.id_contacto_e DESC
    LIMIT 1;
$func$
LANGUAGE sql
STABLE;

-- =====================================================================
-- BAJAS DIFERIDAS + SURGE + COTIZACIÓN: UNA SOLA TRANSACCIÓN
-- =====================================================================

CREATE FUNCTION compras.guardar_cotizacion_requerimiento(
    p_id_requerimiento INTEGER,
    p_ids_detalle INTEGER[],
    p_precios_unitarios NUMERIC[],
    p_ids_detalle_eliminados INTEGER[],
    p_id_prestador INTEGER,
    p_surge BOOLEAN,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_total_activos INTEGER;
    v_total_conservados INTEGER;
    v_total_eliminados INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT r.estado
    INTO v_estado
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe el requerimiento activo informado.';
    END IF;

    IF v_estado = 3 THEN
        RETURN 3;
    END IF;

    IF v_estado <> 2 THEN
        RAISE EXCEPTION
            'La cotización sólo puede guardarse en estado A COTIZAR.';
    END IF;

    PERFORM 1
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL
    ORDER BY d.id_detalle
    FOR UPDATE;

    SELECT COUNT(*)
    INTO v_total_activos
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL;

    v_total_conservados := COALESCE(array_length(p_ids_detalle, 1), 0);
    v_total_eliminados := COALESCE(array_length(p_ids_detalle_eliminados, 1), 0);

    IF v_total_conservados <= 0 THEN
        RAISE EXCEPTION
            'Debe conservar al menos una prestación antes de guardar la cotización.';
    END IF;

    IF v_total_conservados
       <> COALESCE(array_length(p_precios_unitarios, 1), 0) THEN
        RAISE EXCEPTION
            'La cantidad de prestaciones y precios no coincide.';
    END IF;

    IF v_total_activos <> v_total_conservados + v_total_eliminados THEN
        RAISE EXCEPTION
            'La lista final no coincide con las prestaciones activas.';
    END IF;

    IF (SELECT COUNT(*) FROM unnest(p_ids_detalle) AS ids(id_detalle))
       <> (SELECT COUNT(DISTINCT id_detalle)
           FROM unnest(p_ids_detalle) AS ids(id_detalle))
       OR (SELECT COUNT(*)
           FROM unnest(p_ids_detalle_eliminados) AS ids(id_detalle))
       <> (SELECT COUNT(DISTINCT id_detalle)
           FROM unnest(p_ids_detalle_eliminados) AS ids(id_detalle))
       OR EXISTS (
           SELECT 1
           FROM unnest(p_ids_detalle) AS conservados(id_detalle)
           JOIN unnest(p_ids_detalle_eliminados) AS eliminados(id_detalle)
             ON eliminados.id_detalle = conservados.id_detalle
       ) THEN
        RAISE EXCEPTION
            'La lista de prestaciones contiene IDs repetidos o superpuestos.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM (
            SELECT unnest(p_ids_detalle) AS id_detalle
            UNION ALL
            SELECT unnest(p_ids_detalle_eliminados) AS id_detalle
        ) recibidos
        LEFT JOIN compras.requerimiento_detalle d
          ON d.id_detalle = recibidos.id_detalle
         AND d.id_requerimiento = p_id_requerimiento
         AND d.baja_fecha IS NULL
        WHERE recibidos.id_detalle IS NULL
           OR recibidos.id_detalle <= 0
           OR d.id_detalle IS NULL
    ) THEN
        RAISE EXCEPTION
            'La lista de prestaciones fue manipulada o quedó desactualizada.';
    END IF;

    UPDATE compras.requerimiento_detalle
    SET baja_fecha = now(),
        baja_usr = v_usuario,
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id_requerimiento
      AND id_detalle = ANY(p_ids_detalle_eliminados)
      AND baja_fecha IS NULL;

    IF FOUND AND (
        SELECT COUNT(*)
        FROM compras.requerimiento_detalle
        WHERE id_requerimiento = p_id_requerimiento
          AND baja_fecha IS NULL
    ) <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento no puede quedar sin prestaciones activas.';
    END IF;

    UPDATE compras.requerimiento
    SET surge = COALESCE(p_surge, surge),
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id_requerimiento
      AND estado = 2
      AND baja_fecha IS NULL;

    IF p_id_prestador IS NOT NULL
       AND NOT compras.es_prestador_compatible_cotizacion(
           p_id_requerimiento,
           p_id_prestador
       ) THEN

        RAISE EXCEPTION
            'El prestador adjudicado no es compatible con las prestaciones conservadas.';
    END IF;

    RETURN compras.guardar_cotizacion_requerimiento(
        p_id_requerimiento,
        p_ids_detalle,
        p_precios_unitarios,
        p_id_prestador,
        v_usuario
    );
END;
$func$
LANGUAGE plpgsql;

CREATE FUNCTION compras.guardar_cotizacion_requerimiento_call(
    p_id_requerimiento INTEGER,
    p_ids_detalle_array VARCHAR,
    p_precios_unitarios_array VARCHAR,
    p_ids_detalle_eliminados_array VARCHAR,
    p_id_prestador INTEGER,
    p_surge BOOLEAN,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
    SELECT compras.guardar_cotizacion_requerimiento(
        p_id_requerimiento,
        CAST(p_ids_detalle_array AS INTEGER[]),
        CAST(p_precios_unitarios_array AS NUMERIC[]),
        CAST(COALESCE(NULLIF(BTRIM(p_ids_detalle_eliminados_array), ''), '{}') AS INTEGER[]),
        p_id_prestador,
        p_surge,
        p_usuario
    );
$func$
LANGUAGE sql
VOLATILE;

COMMIT;
