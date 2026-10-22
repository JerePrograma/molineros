-- Impedir prestaciones repetidas por codigo en cualquier instancia de carga.
-- No modifica ni elimina duplicados historicos. Compatible con PostgreSQL 9.6.
BEGIN;
SET LOCAL lock_timeout = '5s';

DO $preflight$
DECLARE
    v_oid OID := to_regprocedure('compras.validar_codigo_prestacion_detalle_fila()');
BEGIN
    IF v_oid IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_proc p WHERE p.oid = v_oid
          AND md5(replace(p.prosrc, E'\r\n', E'\n')) = '47b7747319a3e7138500ba1b1114617d'
          AND p.prorettype = 'trigger'::regtype AND NOT p.prosecdef
    ) THEN
        RAISE EXCEPTION 'Variante desconocida del control de codigos duplicados. No se aplicaron cambios.';
    END IF;
END;
$preflight$;

CREATE OR REPLACE FUNCTION compras.validar_codigo_prestacion_detalle_fila()
RETURNS trigger
LANGUAGE plpgsql
AS $func$
DECLARE
    v_codigo TEXT;
BEGIN
    IF NEW.baja_fecha IS NOT NULL OR NEW.tipo_item <> 'NOMENCLADOR' THEN
        RETURN NEW;
    END IF;

    -- El bloqueo de cabecera serializa altas y cambios del mismo requerimiento.
    PERFORM 1 FROM compras.requerimiento r
     WHERE r.id_requerimiento = NEW.id_requerimiento
     FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe el requerimiento del detalle.';
    END IF;

    SELECT NULLIF(upper(btrim(n.codigo)), '') INTO v_codigo
      FROM autorizaciones.nomenclador n
     WHERE n.id_prestacion = NEW.id_prestacion;
    v_codigo := COALESCE(v_codigo, NULLIF(upper(btrim(NEW.codigo_nomenclador)), ''));

    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_detalle d
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
        WHERE d.id_requerimiento = NEW.id_requerimiento AND d.baja_fecha IS NULL
          AND d.id_detalle IS DISTINCT FROM NEW.id_detalle
          AND (d.id_prestacion = NEW.id_prestacion
               OR (v_codigo IS NOT NULL AND v_codigo = upper(btrim(
                       COALESCE(NULLIF(btrim(n.codigo), ''), d.codigo_nomenclador)))))
    ) THEN
        RAISE EXCEPTION 'El código de prestación ya está incluido en el requerimiento.';
    END IF;
    RETURN NEW;
END;
$func$;

DO $install$
DECLARE
    v_trigger RECORD;
BEGIN
    SELECT t.* INTO v_trigger FROM pg_trigger t
     WHERE t.tgrelid = 'compras.requerimiento_detalle'::regclass
       AND t.tgname = 'tr_compras_detalle_codigo_prestacion';
    IF FOUND THEN
        IF v_trigger.tgfoid <> 'compras.validar_codigo_prestacion_detalle_fila()'::regprocedure
           OR v_trigger.tgtype <> 23 OR v_trigger.tgenabled <> 'O'
           OR pg_get_triggerdef(v_trigger.oid) <> 'CREATE TRIGGER tr_compras_detalle_codigo_prestacion BEFORE INSERT OR UPDATE OF id_requerimiento, tipo_item, id_prestacion, codigo_nomenclador, baja_fecha ON compras.requerimiento_detalle FOR EACH ROW EXECUTE PROCEDURE compras.validar_codigo_prestacion_detalle_fila()' THEN
            RAISE EXCEPTION 'Variante desconocida del trigger de codigos duplicados. No se aplicaron cambios.';
        END IF;
    ELSE
        CREATE TRIGGER tr_compras_detalle_codigo_prestacion
            BEFORE INSERT OR UPDATE OF id_requerimiento, tipo_item, id_prestacion,
                codigo_nomenclador, baja_fecha
            ON compras.requerimiento_detalle
            FOR EACH ROW
            EXECUTE PROCEDURE compras.validar_codigo_prestacion_detalle_fila();
    END IF;
END;
$install$;

REVOKE ALL ON FUNCTION compras.validar_codigo_prestacion_detalle_fila() FROM PUBLIC;
COMMIT;
