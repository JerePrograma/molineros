-- Compras: gasto automatico de Prestaciones Medicas y reemplazo 10415 -> 11503.
-- PostgreSQL 9.6+; ISO-8859-1 sin BOM. Ejecutar TODO en una conexion nueva.
-- psql -X -v ON_ERROR_STOP=1 con PGCLIENTENCODING=LATIN1, o todo en pgAdmin.
-- Aplicar despues de los parches anteriores de gastos; no reinstalarlos luego.
-- Reemplaza TODOS los 10415 de detalles y comparativas de Compras:
-- cualquier sector/estado, incluidos requerimientos y detalles dados de baja.
-- Conserva IDs de fila, rubros, cantidades, precios, adjudicaciones y bajas.
-- Tipo/codigo/descripcion se leen del 11503 activo; no se modifica el maestro.
-- Referencias: 20261022_corregir_envios_por_sector.sql y preflight 20261013.
-- Liquidaciones: sql-scripts/funciones/actualiza_prestacion_liquidacion.sql
-- (cambio de referencia con auditoria, conservando la identidad de la fila).
BEGIN;
SET LOCAL standard_conforming_strings = on;
SET LOCAL lock_timeout = '10s';
SET LOCAL statement_timeout = '60s';

-- Impide altas/ediciones concurrentes entre el preflight y la correccion.
LOCK TABLE compras.requerimiento, compras.requerimiento_detalle,
           compras.requerimiento_comparativa,
           compras.requerimiento_comparativa_detalle
    IN SHARE ROW EXCLUSIVE MODE;

DO $correccion$
DECLARE
    v_destino autorizaciones.nomenclador%ROWTYPE;
    v_detalles BIGINT;
    v_comparativas BIGINT;
BEGIN
    IF current_setting('session_replication_role') <> 'origin' THEN
        RAISE EXCEPTION 'Se requiere session_replication_role=origin; no se deben omitir controles.';
    END IF;

    SELECT n.* INTO v_destino
    FROM autorizaciones.nomenclador n
    WHERE n.id_prestacion = 11503 AND n.baja_fecha IS NULL
    FOR SHARE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe activa la prestacion destino 11503. No se aplicaron cambios.';
    END IF;
    IF v_destino.id_tipo_nomenclador IS NULL OR v_destino.id_tipo_nomenclador <= 0
       OR NULLIF(btrim(v_destino.codigo), '') IS NULL
       OR NULLIF(btrim(v_destino.descripcion), '') IS NULL THEN
        RAISE EXCEPTION 'La prestacion 11503 debe tener tipo, codigo y descripcion completos.';
    END IF;

    -- Mismo criterio del parche 13/10: estos validadores estaban huerfanos
    -- en el ambiente verificado. No desactivar controles de otro ambiente.
    IF EXISTS (
        SELECT 1 FROM pg_trigger t
        JOIN pg_proc p ON p.oid = t.tgfoid
        JOIN pg_namespace n ON n.oid = p.pronamespace
        WHERE t.tgrelid = 'compras.requerimiento_detalle'::regclass
          AND NOT t.tgisinternal AND t.tgenabled <> 'D'
          AND n.nspname = 'compras'
          AND p.proname = 'validar_requerimiento_detalle_fila'
    ) THEN
        RAISE EXCEPTION 'Hay un trigger legacy activo incompatible con esta correccion masiva.'
            USING HINT = 'Relevar la definicion instalada; no desactivar controles para continuar.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_detalle
        WHERE id_prestacion = 10415 AND tipo_item IS DISTINCT FROM 'NOMENCLADOR'
    ) THEN
        RAISE EXCEPTION 'Hay detalles 10415 con tipo_item distinto de NOMENCLADOR. Revisar antes de corregir.';
    END IF;

    -- No fusionar filas ni perder cantidades/precios si ya existe el destino.
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_comparativa_detalle origen
        JOIN compras.requerimiento_comparativa_detalle destino
          ON destino.id_comparativa = origen.id_comparativa
         AND destino.id_prestacion = 11503
        WHERE origen.id_prestacion = 10415
    ) THEN
        RAISE EXCEPTION 'Una comparativa contiene 10415 y 11503. Revisar la colision; no se aplicaron cambios.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_detalle origen
        JOIN compras.requerimiento_detalle otro
          ON otro.id_requerimiento = origen.id_requerimiento
         AND otro.id_detalle <> origen.id_detalle
         AND otro.tipo_item = 'NOMENCLADOR' AND otro.baja_fecha IS NULL
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = otro.id_prestacion
        WHERE origen.id_prestacion = 10415 AND origen.baja_fecha IS NULL
          AND (otro.id_prestacion IN (10415, 11503)
               OR upper(btrim(COALESCE(NULLIF(btrim(n.codigo), ''),
                                      otro.codigo_nomenclador))) = upper(btrim(v_destino.codigo)))
    ) THEN
        RAISE EXCEPTION 'El reemplazo generaria una prestacion o codigo duplicado en un requerimiento activo. No se aplicaron cambios.';
    END IF;

    UPDATE compras.requerimiento_comparativa_detalle
    SET id_prestacion = v_destino.id_prestacion,
        modi_fecha = now(), modi_usr = current_user
    WHERE id_prestacion = 10415;
    GET DIAGNOSTICS v_comparativas = ROW_COUNT;

    UPDATE compras.requerimiento_detalle
    SET id_prestacion = v_destino.id_prestacion,
        id_tipo_nomenclador = v_destino.id_tipo_nomenclador,
        codigo_nomenclador = btrim(v_destino.codigo),
        descripcion_nomenclador = btrim(v_destino.descripcion),
        modi_fecha = now(), modi_usr = current_user
    WHERE id_prestacion = 10415;
    GET DIAGNOSTICS v_detalles = ROW_COUNT;

    RAISE NOTICE 'Reemplazo 10415 -> 11503. Detalles: %. Comparativas: %.',
                 v_detalles, v_comparativas;
END;
$correccion$;

-- Misma funcion y contrato del trigger vigente; cambia solo la rama medica.
CREATE OR REPLACE FUNCTION compras.agregar_gastos_envio_requerimiento()
RETURNS TRIGGER
AS $$
DECLARE
    v_sector TEXT;
    v_id_prestacion INTEGER;
    v_item autorizaciones.nomenclador%ROWTYPE;
BEGIN
    SELECT upper(translate(btrim(s.descripcion),
               U&'\00C1\00C9\00CD\00D3\00DA\00DC\00E1\00E9\00ED\00F3\00FA\00FC',
               'AEIOUUaeiouu'))
    INTO v_sector
    FROM compras.sector_requerimiento s
    WHERE s.id_sector = NEW.id_sector;

    IF v_sector IN ('SISTEMAS', 'RRHH') THEN
        RETURN NEW;
    END IF;

    v_id_prestacion := CASE WHEN v_sector = 'PRESTACIONES MEDICAS'
                           THEN 11503 ELSE 10609 END;
    SELECT n.* INTO STRICT v_item
    FROM autorizaciones.nomenclador n
    WHERE n.id_prestacion = v_id_prestacion AND n.baja_fecha IS NULL;

    INSERT INTO compras.requerimiento_detalle (
        id_requerimiento, tipo_item, id_prestacion, id_tipo_nomenclador,
        codigo_nomenclador, descripcion_nomenclador, cantidad, alta_usr
    ) VALUES (
        NEW.id_requerimiento, 'NOMENCLADOR', v_item.id_prestacion,
        v_item.id_tipo_nomenclador, btrim(v_item.codigo),
        btrim(v_item.descripcion), 1, NEW.alta_usr
    );
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_compras_requerimiento_gastos_envio
    ON compras.requerimiento;
CREATE TRIGGER trg_compras_requerimiento_gastos_envio
    AFTER INSERT ON compras.requerimiento
    FOR EACH ROW
    EXECUTE PROCEDURE compras.agregar_gastos_envio_requerimiento();

COMMIT;

-- CONTROL POSTERIOR: pendientes = 0 en ambas tablas, sin excluir bajas.
SELECT (SELECT count(*) FROM compras.requerimiento_detalle
        WHERE id_prestacion = 10415) AS detalles_pendientes_10415,
       (SELECT count(*) FROM compras.requerimiento_comparativa_detalle
        WHERE id_prestacion = 10415) AS comparativas_pendientes_10415;

-- Trigger habilitado O y regla medica true en una instalacion normal.
SELECT t.tgname, t.tgenabled AS habilitado,
       position('THEN 11503 ELSE 10609' IN p.prosrc) > 0
           AS funcion_contiene_regla_medicas_11503
FROM pg_trigger t
JOIN pg_proc p ON p.oid = t.tgfoid
WHERE t.tgrelid = 'compras.requerimiento'::regclass
  AND t.tgname = 'trg_compras_requerimiento_gastos_envio'
  AND NOT t.tgisinternal;
