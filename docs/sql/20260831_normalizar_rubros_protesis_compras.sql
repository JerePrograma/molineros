-- Normaliza los rubros externos de Protesis que usan guion bajo.
-- No modifica datos: reemplaza solamente la funcion canonica de Compras.
-- Ejecutar con:
-- psql -X -v ON_ERROR_STOP=1 -f 20260831_normalizar_rubros_protesis_compras.sql

\encoding LATIN1

BEGIN;

DO $precondition$
BEGIN
    IF to_regprocedure(
           'compras.normalizar_rubro(character varying)'
       ) IS NULL THEN

        RAISE EXCEPTION
            'No existe compras.normalizar_rubro(character varying).';
    END IF;
END;
$precondition$;

CREATE OR REPLACE FUNCTION compras.normalizar_rubro(
    p_rubro VARCHAR
)
    RETURNS VARCHAR
AS $func$
SELECT translate(
           upper(btrim(replace(COALESCE($1, ''), '_', ' '))),
           U&'\00C1\00C0\00C4\00C2\00C9\00C8\00CB\00CA\00CD\00CC\00CF\00CE\00D3\00D2\00D6\00D4\00DA\00D9\00DC\00DB',
           'AAAAEEEEIIIIOOOOUUUU'
       );
$func$
LANGUAGE sql
IMMUTABLE;

DO $postcondition$
BEGIN
    IF compras.normalizar_rubro(
           'PROTESIS_TRAUMATOLOGIA'
       ) <> 'PROTESIS TRAUMATOLOGIA'
       OR compras.normalizar_rubro(
              'PROTESIS_CARDIOLOGIA'
          ) <> 'PROTESIS CARDIOLOGIA'
       OR compras.normalizar_rubro(
              'PROTESIS_GENERAL'
          ) <> 'PROTESIS GENERAL' THEN

        RAISE EXCEPTION
            'La normalizacion de rubros de Protesis no es valida.';
    END IF;
END;
$postcondition$;

COMMIT;
