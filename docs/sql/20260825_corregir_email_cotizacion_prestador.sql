-- Corrige la resolucion del destinatario de cotizaciones de prestadores.
-- Debe ejecutarse despues de desplegar la version de aplicacion correspondiente:
-- psql -X -v ON_ERROR_STOP=1 -f 20260825_corregir_email_cotizacion_prestador.sql

BEGIN;

DO $precondition$
BEGIN
    IF to_regprocedure(
           'compras.resolver_email_cotizacion_prestador(integer)'
       ) IS NULL THEN

        RAISE EXCEPTION
            'No existe compras.resolver_email_cotizacion_prestador(integer).';
    END IF;
END;
$precondition$;

CREATE OR REPLACE FUNCTION
compras.resolver_email_cotizacion_prestador(
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
      AND UPPER(BTRIM(COALESCE(ce.tipo_contacto_e, ''))) IN ('E', 'F')
      AND ce.baja_fecha IS NULL
      AND (pce.vigen_desde IS NULL OR pce.vigen_desde <= LOCALTIMESTAMP)
      AND (ce.vigen_desde IS NULL OR ce.vigen_desde <= LOCALTIMESTAMP)
      AND NULLIF(BTRIM(ce.contacto), '')
          ~* '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$'
    ORDER BY
        CASE UPPER(BTRIM(COALESCE(ce.tipo_contacto_e, '')))
            WHEN 'E' THEN 1
            WHEN 'F' THEN 2
            ELSE 3
        END,
        COALESCE(ce.modi_fecha, ce.alta_fecha,
                 ce.vigen_desde, pce.vigen_desde) DESC NULLS LAST,
        ce.id_contacto_e DESC
    LIMIT 1;
$func$
LANGUAGE sql
STABLE;

COMMIT;
