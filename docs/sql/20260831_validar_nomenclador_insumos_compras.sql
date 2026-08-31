-- Separa el nomenclador de Insumos del resto de Prestaciones Medicas.
-- No modifica datos: reemplaza la validacion diferida del detalle.
-- Ejecutar con:
-- psql -X -v ON_ERROR_STOP=1 -f 20260831_validar_nomenclador_insumos_compras.sql

\encoding LATIN1

BEGIN;

DO $precondition$
BEGIN
    IF to_regclass(
           'compras.requerimiento_detalle'
       ) IS NULL THEN

        RAISE EXCEPTION
            'No existe compras.requerimiento_detalle.';
    END IF;

    IF to_regprocedure(
           'compras.validar_tipo_prestacion_detalle_nuevo()'
       ) IS NULL THEN

        RAISE EXCEPTION
            'No existe la validacion diferida del tipo de prestacion.';
    END IF;
END;
$precondition$;

CREATE OR REPLACE FUNCTION compras.validar_tipo_prestacion_detalle_nuevo()
RETURNS TRIGGER
AS $func$
DECLARE
    v_id_tipo_prestacion SMALLINT;
    v_id_tipo_nomenclador INTEGER;
    v_cantidad_tipos_sector INTEGER;
BEGIN
    SELECT
        d.id_tipo_prestacion,
        d.id_tipo_nomenclador,
        (
            SELECT count(*)
            FROM compras.tipo_prestacion t
            WHERE t.id_sector = r.id_sector
        )
    INTO
        v_id_tipo_prestacion,
        v_id_tipo_nomenclador,
        v_cantidad_tipos_sector
    FROM compras.requerimiento_detalle d
    JOIN compras.requerimiento r
      ON r.id_requerimiento = d.id_requerimiento
    WHERE d.id_detalle = NEW.id_detalle
      AND d.baja_fecha IS NULL
      AND r.baja_fecha IS NULL;

    IF TG_OP = 'INSERT'
       AND FOUND
       AND v_cantidad_tipos_sector > 0
       AND v_id_tipo_prestacion IS NULL THEN

        RAISE EXCEPTION
            'Debe seleccionar el tipo de prestación.';
    END IF;

    IF FOUND
       AND v_id_tipo_prestacion = 6
       AND v_id_tipo_nomenclador IS DISTINCT FROM 10 THEN

        RAISE EXCEPTION
            'Para Insumos el tipo de nomenclador debe ser 10.';
    ELSIF FOUND
          AND v_id_tipo_prestacion <> 6
          AND v_id_tipo_nomenclador = 10 THEN

        RAISE EXCEPTION
            'El nomenclador tipo 10 corresponde exclusivamente a Insumos.';
    END IF;

    RETURN NULL;
END;
$func$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tr_compras_detalle_tipo_prestacion_nuevo
    ON compras.requerimiento_detalle;

CREATE CONSTRAINT TRIGGER tr_compras_detalle_tipo_prestacion_nuevo
    AFTER INSERT OR UPDATE
    ON compras.requerimiento_detalle
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW
    EXECUTE PROCEDURE compras.validar_tipo_prestacion_detalle_nuevo();

DO $postcondition$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_trigger t
        JOIN pg_class c
          ON c.oid = t.tgrelid
        JOIN pg_namespace n
          ON n.oid = c.relnamespace
        WHERE n.nspname = 'compras'
          AND c.relname = 'requerimiento_detalle'
          AND t.tgname = 'tr_compras_detalle_tipo_prestacion_nuevo'
          AND NOT t.tgisinternal
          AND t.tgdeferrable
          AND t.tginitdeferred
          AND (t.tgtype::INTEGER & 4) = 4
          AND (t.tgtype::INTEGER & 16) = 16
    ) THEN

        RAISE EXCEPTION
            'No quedo activa la validacion diferida para INSERT y UPDATE.';
    END IF;
END;
$postcondition$;

COMMIT;
