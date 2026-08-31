-- Corrige destinatarios y prestadores disponibles durante la cotizacion.
-- No modifica datos historicos ni elimina notificaciones o presupuestos.
-- Ejecutar con:
-- psql -X -v ON_ERROR_STOP=1 -f 20260831_filtrar_contactos_y_prestadores_cotizacion.sql

\encoding LATIN1

BEGIN;

DO $precondition$
BEGIN
    IF to_regprocedure(
           'compras.resolver_emails_cotizacion_prestador(integer)'
       ) IS NULL
       OR to_regprocedure(
              'compras.buscar_prestadores_enviados(integer,character varying,integer)'
          ) IS NULL
       OR to_regprocedure(
              'compras.listar_prestadores_enviados(integer,integer)'
          ) IS NULL
       OR to_regprocedure(
              'compras.es_prestador_compatible_cotizacion(integer,integer)'
          ) IS NULL THEN

        RAISE EXCEPTION
            'No estan disponibles las funciones requeridas de Compras.';
    END IF;
END;
$precondition$;

CREATE OR REPLACE FUNCTION compras.resolver_emails_cotizacion_prestador(
    p_id_prestador INTEGER
)
RETURNS TEXT
AS $func$
SELECT string_agg(
           contacto.email,
           ';' ORDER BY
               contacto.prioridad,
               contacto.fecha_referencia DESC NULLS LAST,
               contacto.id_contacto_e DESC,
               lower(contacto.email)
       )
FROM (
    SELECT DISTINCT ON (
        lower(btrim(ce.contacto))
    )
        btrim(ce.contacto) AS email,
        CASE upper(btrim(COALESCE(ce.tipo_contacto_e, '')))
            WHEN 'E' THEN 1
            WHEN 'F' THEN 2
            ELSE 3
        END AS prioridad,
        COALESCE(
            ce.modi_fecha,
            ce.alta_fecha,
            ce.vigen_desde,
            pce.vigen_desde
        ) AS fecha_referencia,
        ce.id_contacto_e
    FROM public.prestad_contacto_e pce
    JOIN public.contacto_e ce
      ON ce.id_contacto_e = pce.id_contacto_e
    WHERE pce.id_prestador = p_id_prestador
      AND upper(btrim(COALESCE(ce.tipo_contacto_e, ''))) = 'E'
      AND ce.baja_fecha IS NULL
      AND (pce.vigen_desde IS NULL OR pce.vigen_desde <= LOCALTIMESTAMP)
      AND (ce.vigen_desde IS NULL OR ce.vigen_desde <= LOCALTIMESTAMP)
      AND NULLIF(btrim(ce.contacto), '') IS NOT NULL
      AND NULLIF(btrim(ce.contacto), '')
          ~* '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$'
    ORDER BY
        lower(btrim(ce.contacto)),
        CASE upper(btrim(COALESCE(ce.tipo_contacto_e, '')))
            WHEN 'E' THEN 1
            WHEN 'F' THEN 2
            ELSE 3
        END,
        COALESCE(
            ce.modi_fecha,
            ce.alta_fecha,
            ce.vigen_desde,
            pce.vigen_desde
        ) DESC NULLS LAST,
        ce.id_contacto_e DESC
) contacto;
$func$
LANGUAGE sql
STABLE;

CREATE OR REPLACE FUNCTION compras.buscar_prestadores_enviados(
    p_id_requerimiento INTEGER,
    p_texto VARCHAR,
    p_limite INTEGER
)
RETURNS TABLE (
    id_prestador INTEGER,
    descripcion VARCHAR,
    cuit VARCHAR,
    email VARCHAR,
    email_destino VARCHAR,
    id_tipo_prestador INTEGER,
    tipo_prestador VARCHAR,
    estado_envio VARCHAR
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
        compras.resolver_emails_cotizacion_prestador(
            p.id_prestador
        )::VARCHAR AS email,
        NULLIF(
            btrim(rcp.email_destino),
            ''
        )::VARCHAR AS email_destino,
        p.id_tipo_prestador::INTEGER,
        tp.descripcion::VARCHAR,
        rcp.estado_envio::VARCHAR
    FROM compras.requerimiento r
    JOIN compras.requerimiento_cotizacion_prestador rcp
      ON rcp.id_requerimiento = r.id_requerimiento
     AND rcp.estado_envio IN (
         'ENVIADO',
         'COTIZADO'
     )
    JOIN public.prestador p
      ON p.id_prestador = rcp.id_prestador
    LEFT JOIN trae_tipos_prestadores() tp
      ON tp.id_tipo_prestador = p.id_tipo_prestador
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.estado IN (2, 3, 4, 5, 99)
      AND (
        r.estado <> 2
            OR compras.es_prestador_compatible_cotizacion(
                r.id_requerimiento,
                p.id_prestador
            )
        )
      AND (
        v_texto IS NULL
            OR upper(COALESCE(p.descripcion, ''))
                LIKE '%' || v_texto || '%'
            OR (
            v_cuit IS NOT NULL
                AND regexp_replace(
                    COALESCE(p.cuit, ''),
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

CREATE OR REPLACE FUNCTION compras.listar_prestadores_enviados(
    p_id_requerimiento INTEGER,
    p_limite INTEGER
)
RETURNS TABLE (
    id_prestador INTEGER,
    descripcion VARCHAR,
    cuit VARCHAR,
    email VARCHAR,
    email_destino VARCHAR,
    id_tipo_prestador INTEGER,
    tipo_prestador VARCHAR,
    estado_envio VARCHAR
)
AS $func$
DECLARE
    v_limite INTEGER;
BEGIN
    IF p_id_requerimiento IS NULL
            OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    IF p_limite IS NULL
            OR p_limite <= 0 THEN

        RAISE EXCEPTION
            'El limite de prestadores debe ser mayor que cero.';
    END IF;

    v_limite := p_limite;

    RETURN QUERY
    SELECT DISTINCT
        p.id_prestador::INTEGER,
        p.descripcion::VARCHAR,
        p.cuit::VARCHAR,
        compras.resolver_emails_cotizacion_prestador(
            p.id_prestador
        )::VARCHAR AS email,
        NULLIF(
            btrim(rcp.email_destino),
            ''
        )::VARCHAR AS email_destino,
        p.id_tipo_prestador::INTEGER,
        tp.descripcion::VARCHAR,
        rcp.estado_envio::VARCHAR
    FROM compras.requerimiento r
    JOIN compras.requerimiento_cotizacion_prestador rcp
      ON rcp.id_requerimiento = r.id_requerimiento
     AND rcp.estado_envio IN (
         'ENVIADO',
         'COTIZADO'
     )
    JOIN public.prestador p
      ON p.id_prestador = rcp.id_prestador
    LEFT JOIN trae_tipos_prestadores() tp
      ON tp.id_tipo_prestador = p.id_tipo_prestador
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.estado IN (
          2,
          3,
          4,
          5,
          99
      )
      AND (
        r.estado <> 2
            OR compras.es_prestador_compatible_cotizacion(
                r.id_requerimiento,
                p.id_prestador
            )
        )
    ORDER BY
        2,
        3,
        1
    LIMIT v_limite;
END;
$func$
LANGUAGE plpgsql
STABLE;

DO $postcondition$
DECLARE
    v_resolver TEXT;
    v_buscar TEXT;
    v_listar TEXT;
BEGIN
    SELECT pg_get_functiondef(
               'compras.resolver_emails_cotizacion_prestador(integer)'::regprocedure
           )
    INTO v_resolver;

    SELECT pg_get_functiondef(
               'compras.buscar_prestadores_enviados(integer,character varying,integer)'::regprocedure
           )
    INTO v_buscar;

    SELECT pg_get_functiondef(
               'compras.listar_prestadores_enviados(integer,integer)'::regprocedure
           )
    INTO v_listar;

    IF position(
           'upper(btrim(COALESCE(ce.tipo_contacto_e, ''''))) = ''E'''
           IN v_resolver
       ) <= 0
       OR position('IN (''E'', ''F'')' IN v_resolver) > 0 THEN

        RAISE EXCEPTION
            'El resolver de emails no quedo limitado al tipo E.';
    END IF;

    IF position('r.estado <> 2' IN v_buscar) <= 0
       OR position(
              'compras.es_prestador_compatible_cotizacion('
              IN v_buscar
          ) <= 0
       OR position('r.estado <> 2' IN v_listar) <= 0
       OR position(
              'compras.es_prestador_compatible_cotizacion('
              IN v_listar
          ) <= 0 THEN

        RAISE EXCEPTION
            'Los prestadores enviados no quedaron filtrados por compatibilidad.';
    END IF;
END;
$postcondition$;

COMMIT;
