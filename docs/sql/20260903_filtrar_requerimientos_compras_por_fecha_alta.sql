-- Incorpora el filtro opcional por fecha de alta de requerimientos.
-- Conserva la firma de ocho parametros como wrapper compatible.
-- Ejecutar con:
-- psql -X -v ON_ERROR_STOP=1 -f 20260903_filtrar_requerimientos_compras_por_fecha_alta.sql

\encoding LATIN1

BEGIN;

DO $precondition$
BEGIN
    IF to_regtype(
           'compras.requerimiento_base_row'
       ) IS NULL
       OR to_regprocedure(
              'compras.buscar_requerimientos(integer,integer,character varying,integer,character varying,boolean,boolean,character varying)'
          ) IS NULL THEN

        RAISE EXCEPTION
            'No esta disponible la busqueda base de requerimientos.';
    END IF;
END;
$precondition$;

CREATE OR REPLACE FUNCTION compras.buscar_requerimientos(
    p_estado INTEGER,
    p_sector INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_id_tercerizadora VARCHAR,
    p_recupero BOOLEAN,
    p_surge BOOLEAN,
    p_texto VARCHAR,
    p_fecha_alta_desde DATE,
    p_fecha_alta_hasta DATE
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
    p_surge IS NULL
        OR rb.surge = p_surge
    )
  AND (
    p_fecha_alta_desde IS NULL
        OR rb.alta_fecha >= p_fecha_alta_desde
    )
  AND (
    p_fecha_alta_hasta IS NULL
        OR rb.alta_fecha
            < p_fecha_alta_hasta + INTERVAL '1 day'
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
                 WHERE d.id_requerimiento = rb.id
                   AND d.baja_fecha IS NULL
                   AND (
                        upper(
                            COALESCE(
                                d.tipo_item,
                                ''
                            )
                        ) LIKE '%' || v_texto || '%'

                        OR upper(COALESCE(d.codigo_nomenclador, ''))
                           LIKE '%' || v_texto || '%'

                        OR upper(COALESCE(d.descripcion_nomenclador, ''))
                           LIKE '%' || v_texto || '%'

                        OR upper(COALESCE(d.nombre_medicamento, ''))
                           LIKE '%' || v_texto || '%'

                        OR COALESCE(d.troquel::VARCHAR, '')
                           LIKE '%' || btrim(p_texto) || '%'

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


CREATE OR REPLACE FUNCTION compras.buscar_requerimientos(
    p_estado INTEGER,
    p_sector INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_id_tercerizadora VARCHAR,
    p_recupero BOOLEAN,
    p_surge BOOLEAN,
    p_texto VARCHAR
)
    RETURNS SETOF compras.requerimiento_base_row
AS $func$
BEGIN
RETURN QUERY
SELECT *
FROM compras.buscar_requerimientos(
    p_estado,
    p_sector,
    p_afiliado_cuil_titular,
    p_afiliado_int,
    p_id_tercerizadora,
    p_recupero,
    p_surge,
    p_texto,
    NULL::DATE,
    NULL::DATE
);
END;
$func$
LANGUAGE plpgsql
STABLE;


COMMIT;
