-- Correcciones funcionales de la reunión de Compras.
-- PostgreSQL 9.6+.
-- Migración incremental: no elimina tablas ni datos.

BEGIN;

ALTER TABLE compras.requerimiento
    ADD COLUMN IF NOT EXISTS afiliado_id_ospim INTEGER;

COMMENT ON COLUMN compras.requerimiento.afiliado_id_ospim IS
    'Snapshot del número de afiliado OSPIM usado por el Pedido de presupuesto.';

DO $backfill$
BEGIN
    IF to_regclass('public.afiliado') IS NOT NULL
       AND EXISTS (
            SELECT 1
              FROM information_schema.columns
             WHERE table_schema = 'public'
               AND table_name = 'afiliado'
               AND column_name = 'cuil_titular'
       )
       AND EXISTS (
            SELECT 1
              FROM information_schema.columns
             WHERE table_schema = 'public'
               AND table_name = 'afiliado'
               AND column_name = 'inte'
       )
       AND EXISTS (
            SELECT 1
              FROM information_schema.columns
             WHERE table_schema = 'public'
               AND table_name = 'afiliado'
               AND column_name = 'id_ospim'
       ) THEN
        UPDATE compras.requerimiento r
           SET afiliado_id_ospim = a.id_ospim
          FROM public.afiliado a
         WHERE r.afiliado_id_ospim IS NULL
           AND r.afiliado_cuil_titular = a.cuil_titular
           AND r.afiliado_int = a.inte
           AND a.id_ospim IS NOT NULL
           AND (
                SELECT count(*)
                  FROM public.afiliado candidato
                 WHERE candidato.cuil_titular =
                       r.afiliado_cuil_titular
                   AND candidato.inte = r.afiliado_int
                   AND candidato.id_ospim IS NOT NULL
           ) = 1;
    END IF;
END;
$backfill$;

DO $tipo$
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM pg_type t
          JOIN pg_namespace n
            ON n.oid = t.typnamespace
          JOIN pg_class c
            ON c.reltype = t.oid
          JOIN pg_attribute a
            ON a.attrelid = c.oid
         WHERE n.nspname = 'compras'
           AND t.typname = 'requerimiento_base_row'
           AND a.attname = 'afiliado_id_ospim'
           AND a.attnum > 0
           AND NOT a.attisdropped
    ) THEN
        ALTER TYPE compras.requerimiento_base_row
            ADD ATTRIBUTE afiliado_id_ospim INTEGER;
    END IF;
END;
$tipo$;

CREATE OR REPLACE FUNCTION compras.estado_requerimiento_descripcion(
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

CREATE OR REPLACE FUNCTION compras.listar_estados_requerimiento()
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

DO $actualizar_texto_trigger$
DECLARE
    v_funcion REGPROCEDURE;
    v_definicion TEXT;
BEGIN
    v_funcion := to_regprocedure(
        'compras.validar_requerimiento_fila()'
    );

    IF v_funcion IS NOT NULL THEN
        SELECT pg_get_functiondef(v_funcion)
          INTO v_definicion;

        v_definicion := replace(
            v_definicion,
            'Auto' || 'rizado y Orden de compra son estados reservados.',
            'RECLAMO (RP) y ORDEN DE COMPRA son estados de solo lectura.'
        );

        v_definicion := replace(
            v_definicion,
            'AUTORI' || 'ZADO y ORDEN DE COMPRA son estados reservados.',
            'RECLAMO (RP) y ORDEN DE COMPRA son estados de solo lectura.'
        );

        EXECUTE v_definicion;
    END IF;
END;
$actualizar_texto_trigger$;

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
        r.afiliado_nombre,
        r.afiliado_apellido,
        NULLIF(
            concat_ws(
                ', ',
                NULLIF(btrim(r.afiliado_apellido), ''),
                NULLIF(btrim(r.afiliado_nombre), '')
            ),
            ''
        )::VARCHAR,
        r.afiliado_documento_tipo,
        r.afiliado_documento_nro,
        NULLIF(
            concat_ws(
                ' ',
                NULLIF(btrim(r.afiliado_documento_tipo), ''),
                NULLIF(btrim(r.afiliado_documento_nro), '')
            ),
            ''
        )::VARCHAR,
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
        compras.estado_requerimiento_descripcion(r.estado),
        r.afiliado_id_ospim
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento s
        ON s.id_sector = r.id_sector;
END;
$func$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION compras.validar_afiliado_id_ospim_fila()
RETURNS TRIGGER
AS $func$
BEGIN
    IF NEW.afiliado_id_ospim
       IS DISTINCT FROM OLD.afiliado_id_ospim
       AND OLD.estado <> 1 THEN
        RAISE EXCEPTION
            'La estructura solo puede modificarse en estado PENDIENTE.';
    END IF;

    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS
    trg_compras_requerimiento_validar_afiliado_id_ospim
    ON compras.requerimiento;

CREATE TRIGGER trg_compras_requerimiento_validar_afiliado_id_ospim
BEFORE UPDATE OF afiliado_id_ospim
ON compras.requerimiento
FOR EACH ROW
EXECUTE PROCEDURE compras.validar_afiliado_id_ospim_fila();

CREATE OR REPLACE FUNCTION compras.validar_envio_transicion_a_cotizar()
RETURNS TRIGGER
AS $func$
BEGIN
    IF OLD.estado = 1
       AND NEW.estado = 2
       AND NOT EXISTS (
            SELECT 1
              FROM compras.requerimiento_cotizacion_prestador rcp
             WHERE rcp.id_requerimiento = NEW.id_requerimiento
               AND rcp.estado_envio = 'ENVIADO'
       ) THEN
        RAISE EXCEPTION
            'Debe existir al menos un prestador notificado como ENVIADO antes de pasar a A COTIZAR.';
    END IF;

    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS
    trg_compras_requerimiento_validar_envio_a_cotizar
    ON compras.requerimiento;

CREATE TRIGGER trg_compras_requerimiento_validar_envio_a_cotizar
BEFORE UPDATE OF estado
ON compras.requerimiento
FOR EACH ROW
EXECUTE PROCEDURE compras.validar_envio_transicion_a_cotizar();

CREATE OR REPLACE FUNCTION compras.validar_precio_cotizacion_no_negativo()
RETURNS TRIGGER
AS $func$
BEGIN
    IF NEW.precio_unitario_estimado < 0 THEN
        RAISE EXCEPTION
            'El precio unitario estimado no puede ser negativo.';
    END IF;

    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS
    trg_compras_detalle_validar_precio_no_negativo
    ON compras.requerimiento_detalle;

CREATE TRIGGER trg_compras_detalle_validar_precio_no_negativo
BEFORE INSERT OR UPDATE OF precio_unitario_estimado
ON compras.requerimiento_detalle
FOR EACH ROW
EXECUTE PROCEDURE compras.validar_precio_cotizacion_no_negativo();

DO $guardar$
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM pg_proc p
          JOIN pg_namespace n
            ON n.oid = p.pronamespace
         WHERE n.nspname = 'compras'
           AND p.proname = 'guardar_requerimiento'
           AND p.pronargs = 21
    ) THEN
        EXECUTE $create$
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
            BEGIN
                v_id := compras.guardar_requerimiento(
                    p_id,
                    p_afiliado_cuil_titular,
                    p_afiliado_int,
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
                    p_observaciones,
                    p_usuario
                );

                UPDATE compras.requerimiento
                   SET afiliado_id_ospim = p_afiliado_id_ospim
                 WHERE id_requerimiento = v_id
                   AND estado = 1
                   AND baja_fecha IS NULL;

                IF NOT FOUND THEN
                    RAISE EXCEPTION
                        'La estructura solo puede modificarse en estado PENDIENTE.';
                END IF;

                RETURN v_id;
            END;
            $func$
            LANGUAGE plpgsql
        $create$;
    END IF;
END;
$guardar$;

DROP FUNCTION IF EXISTS
    compras.get_requerimiento_compra_pdf(INTEGER);

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

COMMENT ON FUNCTION compras.get_requerimiento_compra_pdf(INTEGER) IS
    'Datos del Pedido de presupuesto con snapshot de número OSPIM.';

DO $validacion$
DECLARE
    v_estados INTEGER;
BEGIN
    SELECT count(*)
      INTO v_estados
      FROM compras.listar_estados_requerimiento();

    IF v_estados <> 6 THEN
        RAISE EXCEPTION
            'El catálogo de estados de Compras debe contener seis filas.';
    END IF;

    IF compras.estado_requerimiento_descripcion(4)
       <> 'RECLAMO (RP)'
       OR compras.estado_requerimiento_descripcion(5)
       <> 'ORDEN DE COMPRA' THEN
        RAISE EXCEPTION
            'Las descripciones de los estados 4 y 5 son inválidas.';
    END IF;
END;
$validacion$;

COMMIT;
