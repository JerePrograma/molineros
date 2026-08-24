-- Corrige la clasificacion del detalle para sectores con caracteres acentuados.
-- Debe ejecutarse despues de desplegar la version de aplicacion correspondiente:
-- psql -X -v ON_ERROR_STOP=1 -f 20260824_corregir_tipo_detalle_prestaciones_medicas.sql

BEGIN;

CREATE OR REPLACE FUNCTION compras.normalizar_sector(
    p_descripcion VARCHAR
)
    RETURNS VARCHAR
AS $func$
SELECT translate(
           upper(btrim(COALESCE($1, ''))),
           U&'\00C1\00C9\00CD\00D3\00DA\00DC\00C0\00C8\00CC\00D2\00D9',
           'AEIOUUAEIOU'
       );
$func$
LANGUAGE sql
IMMUTABLE;

DO $migration$
DECLARE
    v_oid OID;
    v_definicion TEXT;
    v_definicion_corregida TEXT;
BEGIN
    v_oid := to_regprocedure(
        'compras.validar_requerimiento_detalle_fila()'
    );

    IF v_oid IS NULL THEN
        RAISE EXCEPTION
            'No existe compras.validar_requerimiento_detalle_fila().';
    END IF;

    SELECT pg_get_functiondef(v_oid)
    INTO v_definicion;

    IF position(
           'compras.normalizar_sector(sr.descripcion)'
           IN v_definicion
       ) = 0 THEN

        v_definicion_corregida := regexp_replace(
            v_definicion,
            E'translate\\(.*\\)[[:space:]]+INTO[[:space:]]+v_estado,[[:space:]]+v_sector',
            'compras.normalizar_sector(sr.descripcion)'
                || E'\nINTO\n    v_estado,\n    v_sector'
        );

        IF v_definicion_corregida = v_definicion
           OR position(
                  'compras.normalizar_sector(sr.descripcion)'
                  IN v_definicion_corregida
              ) = 0 THEN

            RAISE EXCEPTION
                'No se pudo corregir la normalizacion del trigger de detalle.';
        END IF;

        EXECUTE v_definicion_corregida;
    END IF;

    v_oid := to_regprocedure(
        'compras.guardar_requerimiento_detalle('
            || 'integer,integer,character varying,integer,integer,'
            || 'character varying,character varying,integer,integer,'
            || 'character varying,integer,text,character varying)'
    );

    IF v_oid IS NULL THEN
        RAISE EXCEPTION
            'No existe la firma esperada de compras.guardar_requerimiento_detalle().';
    END IF;

    SELECT pg_get_functiondef(v_oid)
    INTO v_definicion;

    IF position(
           'compras.normalizar_sector(sr.descripcion)'
           IN v_definicion
       ) = 0 THEN

        v_definicion_corregida := regexp_replace(
            v_definicion,
            E'SELECT[[:space:]]+translate\\(.*\\)[[:space:]]+INTO[[:space:]]+v_sector',
            'SELECT compras.normalizar_sector(sr.descripcion)'
                || E'\nINTO v_sector'
        );

        IF v_definicion_corregida = v_definicion
           OR position(
                  'compras.normalizar_sector(sr.descripcion)'
                  IN v_definicion_corregida
              ) = 0 THEN

            RAISE EXCEPTION
                'No se pudo corregir la normalizacion del guardado de detalle.';
        END IF;

        EXECUTE v_definicion_corregida;
    END IF;

    IF compras.normalizar_sector(
           U&'PRESTACIONES M\00C9DICAS'
       ) <> 'PRESTACIONES MEDICAS' THEN

        RAISE EXCEPTION
            'La normalizacion de PRESTACIONES MEDICAS no quedo operativa.';
    END IF;

    IF position(
           'compras.normalizar_sector(sr.descripcion)'
           IN pg_get_functiondef(
               'compras.validar_requerimiento_detalle_fila()'::regprocedure
           )
       ) = 0 THEN

        RAISE EXCEPTION
            'El trigger de detalle no utiliza la normalizacion corregida.';
    END IF;

    IF position(
           'compras.normalizar_sector(sr.descripcion)'
           IN pg_get_functiondef(v_oid)
       ) = 0 THEN

        RAISE EXCEPTION
            'El guardado de detalle no utiliza la normalizacion corregida.';
    END IF;
END;
$migration$;

COMMIT;
