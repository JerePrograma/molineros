\set ON_ERROR_STOP on

-- Requiere registros canonicos activos. Consultas orientativas para QA:
-- SELECT * FROM buscar_medicamentos(0, 0, NULL, NULL, NULL, NULL)
--  WHERE fecha_baja IS NULL LIMIT 1;
-- SELECT * FROM autorizaciones.busca_nomenclador(NULL, NULL, NULL, NULL, FALSE, NULL)
--  WHERE baja_fecha IS NULL LIMIT 1;
--
-- Ejecucion:
-- psql -X -v ON_ERROR_STOP=1 -v med_id=... -v med_troquel=... \
--   -v med_nombre='...' -v nom_id=... -v nom_tipo=... \
--   -v nom_codigo='...' -v nom_descripcion='...' -f compras_smoke_test.sql

\if :{?med_id}
\else
\echo 'Falta -v med_id=<id_medicamento activo>'
\quit 3
\endif
\if :{?med_troquel}
\else
\set med_troquel NULL
\endif
\if :{?med_nombre}
\else
\echo 'Falta -v med_nombre=<nombre y presentacion canonicos>'
\quit 3
\endif
\if :{?nom_id}
\else
\echo 'Falta -v nom_id=<id_prestacion activo>'
\quit 3
\endif
\if :{?nom_tipo}
\else
\echo 'Falta -v nom_tipo=<id_tipo_nomenclador canonico>'
\quit 3
\endif
\if :{?nom_codigo}
\else
\echo 'Falta -v nom_codigo=<codigo canonico>'
\quit 3
\endif
\if :{?nom_descripcion}
\else
\echo 'Falta -v nom_descripcion=<descripcion canonica>'
\quit 3
\endif

BEGIN;

CREATE TEMP TABLE compras_smoke_ids (
    clave TEXT PRIMARY KEY,
    id INTEGER NOT NULL
) ON COMMIT DROP;

CREATE TEMP TABLE compras_smoke_config (
    med_id INTEGER NOT NULL,
    med_troquel INTEGER,
    med_nombre TEXT NOT NULL,
    nom_id INTEGER NOT NULL,
    nom_tipo INTEGER NOT NULL,
    nom_codigo TEXT NOT NULL,
    nom_descripcion TEXT NOT NULL
) ON COMMIT DROP;

INSERT INTO compras_smoke_config VALUES (
    :med_id,
    :med_troquel,
    :'med_nombre',
    :nom_id,
    :nom_tipo,
    :'nom_codigo',
    :'nom_descripcion'
);

CREATE FUNCTION pg_temp.compras_expect_error(
    p_prueba TEXT,
    p_sql TEXT
)
RETURNS VOID
LANGUAGE plpgsql
AS $func$
BEGIN
    BEGIN
        EXECUTE p_sql;
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'PASS %: [%] %', p_prueba, SQLSTATE, SQLERRM;
        RETURN;
    END;

    RAISE EXCEPTION 'FAIL %: la operacion debio fallar', p_prueba;
END;
$func$;

WITH creado AS (
    INSERT INTO compras.requerimiento (
        id_sector,
        afiliado_cuil_titular,
        afiliado_int,
        observaciones,
        alta_usr
    )
    SELECT id_sector, '20000000000', 0, 'SMOKE FARMACIA', 'smoke_sql'
    FROM compras.sector_requerimiento
    WHERE upper(descripcion) = 'FARMACIA'
    RETURNING id_requerimiento
)
INSERT INTO compras_smoke_ids
SELECT 'farmacia', id_requerimiento FROM creado;

WITH creado AS (
    INSERT INTO compras.requerimiento (
        id_sector,
        afiliado_cuil_titular,
        afiliado_int,
        observaciones,
        alta_usr
    )
    SELECT id_sector, '20000000000', 0, 'SMOKE PRESTACIONES', 'smoke_sql'
    FROM compras.sector_requerimiento
    WHERE translate(
        upper(descripcion),
        'ÁÉÍÓÚÜáéíóúü',
        'AEIOUUAEIOUU'
    ) = 'PRESTACIONES MEDICAS'
    RETURNING id_requerimiento
)
INSERT INTO compras_smoke_ids
SELECT 'prestaciones', id_requerimiento FROM creado;

WITH creado AS (
    INSERT INTO compras.requerimiento (
        id_sector,
        observaciones,
        alta_usr
    )
    SELECT id_sector, 'SMOKE LEGALES', 'smoke_sql'
    FROM compras.sector_requerimiento
    WHERE upper(descripcion) = 'LEGALES'
    RETURNING id_requerimiento
)
INSERT INTO compras_smoke_ids
SELECT 'legales', id_requerimiento FROM creado;

WITH guardado AS (
    SELECT compras.guardar_requerimiento_detalle(
        NULL,
        (SELECT id FROM compras_smoke_ids WHERE clave = 'farmacia'),
        'MEDICAMENTO',
        :nom_id,
        :nom_tipo,
        :'nom_codigo',
        :'nom_descripcion',
        :med_id,
        :med_troquel,
        :'med_nombre',
        2,
        'SMOKE MEDICAMENTO',
        'smoke_sql'
    ) AS id
)
INSERT INTO compras_smoke_ids
SELECT 'detalle_medicamento', id FROM guardado;

WITH guardado AS (
    SELECT compras.guardar_requerimiento_detalle(
        NULL,
        (SELECT id FROM compras_smoke_ids WHERE clave = 'prestaciones'),
        'NOMENCLADOR',
        :nom_id,
        :nom_tipo,
        :'nom_codigo',
        :'nom_descripcion',
        :med_id,
        :med_troquel,
        :'med_nombre',
        3,
        'SMOKE NOMENCLADOR PM',
        'smoke_sql'
    ) AS id
)
INSERT INTO compras_smoke_ids
SELECT 'detalle_prestaciones', id FROM guardado;

WITH guardado AS (
    SELECT compras.guardar_requerimiento_detalle(
        NULL,
        (SELECT id FROM compras_smoke_ids WHERE clave = 'legales'),
        'NOMENCLADOR',
        :nom_id,
        :nom_tipo,
        :'nom_codigo',
        :'nom_descripcion',
        NULL,
        NULL,
        NULL,
        1,
        'SMOKE NOMENCLADOR LEGALES',
        'smoke_sql'
    ) AS id
)
INSERT INTO compras_smoke_ids
SELECT 'detalle_legales', id FROM guardado;

DO $assert$
DECLARE
    v_med_id INTEGER;
    v_med_troquel INTEGER;
    v_med_nombre TEXT;
    v_nom_id INTEGER;
    v_nom_tipo INTEGER;
    v_nom_codigo TEXT;
    v_nom_descripcion TEXT;
BEGIN
    SELECT med_id, med_troquel, med_nombre,
           nom_id, nom_tipo, nom_codigo, nom_descripcion
    INTO v_med_id, v_med_troquel, v_med_nombre,
         v_nom_id, v_nom_tipo, v_nom_codigo, v_nom_descripcion
    FROM compras_smoke_config;

    IF NOT EXISTS (
        SELECT 1
        FROM compras.requerimiento_detalle
        WHERE id_detalle = (SELECT id FROM compras_smoke_ids WHERE clave = 'detalle_medicamento')
          AND tipo_item = 'MEDICAMENTO'
          AND id_medicamento = v_med_id
          AND nombre_medicamento = v_med_nombre
          AND id_prestacion IS NULL
          AND id_tipo_nomenclador IS NULL
          AND codigo_nomenclador IS NULL
          AND descripcion_nomenclador IS NULL
    ) THEN
        RAISE EXCEPTION 'FAIL medicamento valido o nulls cruzados';
    END IF;

    IF (SELECT troquel FROM compras.requerimiento_detalle
        WHERE id_detalle = (SELECT id FROM compras_smoke_ids WHERE clave = 'detalle_medicamento'))
       IS DISTINCT FROM v_med_troquel THEN
        RAISE EXCEPTION 'FAIL troquel opcional';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM compras.requerimiento_detalle
        WHERE id_detalle IN (
            (SELECT id FROM compras_smoke_ids WHERE clave = 'detalle_prestaciones'),
            (SELECT id FROM compras_smoke_ids WHERE clave = 'detalle_legales')
        )
          AND tipo_item = 'NOMENCLADOR'
          AND id_prestacion = v_nom_id
          AND id_tipo_nomenclador = v_nom_tipo
          AND codigo_nomenclador = v_nom_codigo
          AND descripcion_nomenclador = v_nom_descripcion
          AND id_medicamento IS NULL
          AND troquel IS NULL
          AND nombre_medicamento IS NULL
        GROUP BY tipo_item
        HAVING count(*) = 2
    ) THEN
        RAISE EXCEPTION 'FAIL nomencladores validos o nulls cruzados';
    END IF;
END;
$assert$;

SELECT pg_temp.compras_expect_error(
    'Farmacia rechaza NOMENCLADOR',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''NOMENCLADOR'',%s,%s,%L,%L,NULL,NULL,NULL,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'farmacia'),
        :nom_id,
        :nom_tipo,
        :'nom_codigo',
        :'nom_descripcion'
    )
);

SELECT pg_temp.compras_expect_error(
    'Prestaciones Medicas rechaza MEDICAMENTO',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''MEDICAMENTO'',NULL,NULL,NULL,NULL,%s,%s,%L,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'prestaciones'),
        :med_id,
        COALESCE(:med_troquel::TEXT, 'NULL'),
        :'med_nombre'
    )
);

SELECT pg_temp.compras_expect_error(
    'Legales rechaza MEDICAMENTO',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''MEDICAMENTO'',NULL,NULL,NULL,NULL,%s,%s,%L,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'legales'),
        :med_id,
        COALESCE(:med_troquel::TEXT, 'NULL'),
        :'med_nombre'
    )
);

SELECT pg_temp.compras_expect_error(
    'Medicamento sin id',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''MEDICAMENTO'',NULL,NULL,NULL,NULL,NULL,NULL,%L,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'farmacia'),
        :'med_nombre'
    )
);

SELECT pg_temp.compras_expect_error(
    'Medicamento sin nombre',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''MEDICAMENTO'',NULL,NULL,NULL,NULL,%s,NULL,NULL,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'farmacia'),
        :med_id
    )
);

SELECT pg_temp.compras_expect_error(
    'Nomenclador sin id_prestacion',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''NOMENCLADOR'',NULL,%s,%L,%L,NULL,NULL,NULL,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'prestaciones'),
        :nom_tipo,
        :'nom_codigo',
        :'nom_descripcion'
    )
);

SELECT pg_temp.compras_expect_error(
    'Nomenclador sin id_tipo_nomenclador',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''NOMENCLADOR'',%s,NULL,%L,%L,NULL,NULL,NULL,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'prestaciones'),
        :nom_id,
        :'nom_codigo',
        :'nom_descripcion'
    )
);

SELECT pg_temp.compras_expect_error(
    'Nomenclador sin codigo',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''NOMENCLADOR'',%s,%s,NULL,%L,NULL,NULL,NULL,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'prestaciones'),
        :nom_id,
        :nom_tipo,
        :'nom_descripcion'
    )
);

SELECT pg_temp.compras_expect_error(
    'Nomenclador sin descripcion',
    format(
        'SELECT compras.guardar_requerimiento_detalle(NULL,%s,''NOMENCLADOR'',%s,%s,%L,NULL,NULL,NULL,NULL,1,NULL,''smoke'')',
        (SELECT id FROM compras_smoke_ids WHERE clave = 'prestaciones'),
        :nom_id,
        :nom_tipo,
        :'nom_codigo'
    )
);

DO $assert$
DECLARE
    v_med_id INTEGER;
    v_med_troquel INTEGER;
    v_med_nombre TEXT;
    v_nom_codigo TEXT;
    v_nom_descripcion TEXT;
BEGIN
    SELECT med_id, med_troquel, med_nombre, nom_codigo, nom_descripcion
    INTO v_med_id, v_med_troquel, v_med_nombre, v_nom_codigo, v_nom_descripcion
    FROM compras_smoke_config;

    IF NOT EXISTS (
        SELECT 1
        FROM compras.get_requerimiento_detalle(
            (SELECT id FROM compras_smoke_ids WHERE clave = 'farmacia')
        )
        WHERE codigo_item = COALESCE(v_med_troquel::TEXT, v_med_id::TEXT)
          AND descripcion_item = v_med_nombre
    ) THEN
        RAISE EXCEPTION 'FAIL lectura de medicamento';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM compras.get_requerimiento_detalle(
            (SELECT id FROM compras_smoke_ids WHERE clave = 'prestaciones')
        )
        WHERE codigo_item = v_nom_codigo
          AND descripcion_item = v_nom_descripcion
    ) THEN
        RAISE EXCEPTION 'FAIL lectura de nomenclador';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM compras.buscar_requerimientos(NULL,NULL,NULL,NULL,NULL,NULL,NULL,COALESCE(v_med_troquel::TEXT, v_med_id::TEXT)))
       OR NOT EXISTS (SELECT 1 FROM compras.buscar_requerimientos(NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_med_nombre))
       OR NOT EXISTS (SELECT 1 FROM compras.buscar_requerimientos(NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_nom_codigo))
       OR NOT EXISTS (SELECT 1 FROM compras.buscar_requerimientos(NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_nom_descripcion)) THEN
        RAISE EXCEPTION 'FAIL busqueda por campos tecnicos';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM compras.get_requerimiento_compra_pdf(
            (SELECT id FROM compras_smoke_ids WHERE clave = 'farmacia')
        )
        WHERE tipo_item = 'MEDICAMENTO'
          AND descripcion_item = v_med_nombre
    ) THEN
        RAISE EXCEPTION 'FAIL dataset PDF medicamento';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM pg_proc p
        JOIN pg_namespace n ON n.oid = p.pronamespace
        WHERE n.nspname = 'compras'
          AND p.prokind = 'f'
          AND (
              pg_get_functiondef(p.oid) ILIKE '%id_articulo%'
              OR pg_get_functiondef(p.oid) ILIKE '%compras.articulo%'
          )
    ) THEN
        RAISE EXCEPTION 'FAIL dependencia residual de articulos';
    END IF;
END;
$assert$;

SELECT 'SQL_SMOKE_OK' AS resultado;

ROLLBACK;
