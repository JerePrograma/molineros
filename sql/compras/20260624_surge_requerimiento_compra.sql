BEGIN;

ALTER TABLE compras.requerimiento
    ADD COLUMN IF NOT EXISTS surge BOOLEAN;

UPDATE compras.requerimiento
   SET surge = FALSE
 WHERE surge IS NULL;

ALTER TABLE compras.requerimiento
    ALTER COLUMN surge SET DEFAULT FALSE;

ALTER TABLE compras.requerimiento
    ALTER COLUMN surge SET NOT NULL;

COMMENT ON COLUMN compras.requerimiento.surge IS
    'Indica si el requerimiento fue marcado funcionalmente como surge.';

DO $migracion$
DECLARE
    v_tipo_rel OID;
    v_surge_attnum INTEGER;
    v_observaciones_attnum INTEGER;
BEGIN
    SELECT t.typrelid
      INTO v_tipo_rel
      FROM pg_type t
      JOIN pg_namespace n
        ON n.oid = t.typnamespace
     WHERE n.nspname = 'compras'
       AND t.typname = 'requerimiento_base_row'
       AND t.typrelid <> 0;

    IF v_tipo_rel IS NULL THEN
        RAISE EXCEPTION
            'No existe el tipo compras.requerimiento_base_row.';
    END IF;

    SELECT a.attnum
      INTO v_surge_attnum
      FROM pg_attribute a
     WHERE a.attrelid = v_tipo_rel
       AND a.attname = 'surge'
       AND NOT a.attisdropped;

    IF v_surge_attnum IS NULL THEN
        ALTER TYPE compras.requerimiento_base_row
            ADD ATTRIBUTE surge BOOLEAN;
    END IF;

    SELECT a.attnum
      INTO v_surge_attnum
      FROM pg_attribute a
     WHERE a.attrelid = v_tipo_rel
       AND a.attname = 'surge'
       AND NOT a.attisdropped;

    SELECT a.attnum
      INTO v_observaciones_attnum
      FROM pg_attribute a
     WHERE a.attrelid = v_tipo_rel
       AND a.attname = 'observaciones'
       AND NOT a.attisdropped;

    IF v_surge_attnum IS NULL THEN
        RAISE EXCEPTION
            'No se pudo agregar surge a compras.requerimiento_base_row.';
    END IF;

    IF v_surge_attnum > v_observaciones_attnum THEN
        EXECUTE $sql$
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
    ),

    r.surge
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento s
              ON s.id_sector = r.id_sector;
END;
$func$
LANGUAGE plpgsql
STABLE;
$sql$;
    END IF;
END;
$migracion$;

DROP FUNCTION IF EXISTS compras.guardar_requerimiento(
    INTEGER,
    VARCHAR,
    INTEGER,
    INTEGER,
    VARCHAR,
    VARCHAR,
    VARCHAR,
    VARCHAR,
    VARCHAR,
    VARCHAR,
    VARCHAR,
    VARCHAR,
    VARCHAR,
    VARCHAR,
    INTEGER,
    INTEGER,
    INTEGER,
    VARCHAR,
    BOOLEAN,
    TEXT,
    VARCHAR
);

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento(
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
            surge,

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
            COALESCE(p_surge, FALSE),

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

    surge =
        COALESCE(p_surge, FALSE),

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

COMMIT;
