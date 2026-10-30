-- MOLINEROS / COMPRAS - Efectos SQL, corte Java del 30/10/2026.
-- ISO-8859-1 sin BOM. Compatible PostgreSQL 9.6.
-- Fuente: catalogo REAL de devmolineros relevado el 30/10/2026.
-- No contiene credenciales, correos ni datos de afiliados.
-- Este paquete NO autoriza operar produccion: entrega para proxima migracion.
-- No ejecutado contra DB por el autor de este archivo.

-- REVERSION LIMITADA DE TRIGGERS ORIGINALES.
-- Solo recrea los originales ausentes si sus funciones conservan HASH exacto.
-- Si un trigger ya existe e igual, no lo modifica. Ante deriva, aborta todo.
-- No revierte Java, configuracion, funciones cambiadas ni datos de negocio.
-- Java viejo puede no admitir tipo15: no borrar/reclasificar cargas para
-- permitir una vuelta atras. Validar artefacto y compatibilidad antes de usar.
-- Este archivo es recuperacion condicionada, NO certificacion de rollback.

BEGIN;
SET LOCAL statement_timeout = '15s';
SET LOCAL lock_timeout = '2s';
DO $restaurar_efectos$
DECLARE
    esperado RECORD;
    id_funcion OID;
    hash_actual TEXT;
    habilitado "char";
BEGIN
    FOR esperado IN
        SELECT * FROM (VALUES
            ('compras.agregar_gastos_envio_requerimiento()', 'ef9951e6eac71d8301599030962c816e'),
            ('compras.baja_vinculo_reclamo_prestacional(integer,character varying)', 'a05aa540deefc8d2a5a93f042b2ca102'),
            ('compras.baja_vinculo_reclamo_prestacional_trigger()', 'a33c36ade63ab98bceba282309bf283f'),
            ('compras.calcular_total_detalle_fila()', '757c0cf62a2b28b0782fdfad56e49d1a'),
            ('compras.completar_baja_requerimiento_fila()', 'f9c7d6751c6580ffce70bbb448a15def'),
            ('compras.validar_codigo_prestacion_detalle_fila()', 'f585417a0d6b9381b9d57a1b2ea78a43')
        ) firmas(firma, hash_md5)
    LOOP
        id_funcion := to_regprocedure(esperado.firma);
        IF id_funcion IS NULL THEN
            RAISE EXCEPTION 'Falta la funcion instalada esperada: %', esperado.firma;
        END IF;
        IF md5(pg_get_functiondef(id_funcion)) IS DISTINCT FROM esperado.hash_md5 THEN
            RAISE EXCEPTION 'Deriva de definicion instalada: %', esperado.firma;
        END IF;
    END LOOP;
    SELECT md5(pg_get_triggerdef(t.oid, true)), t.tgenabled INTO hash_actual, habilitado
    FROM pg_trigger t
    JOIN pg_class c ON c.oid = t.tgrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'autorizaciones' AND c.relname = 'reclamos_prestacionales'
      AND t.tgname = 'trg_compras_baja_vinculo_reclamo' AND NOT t.tgisinternal;
    IF hash_actual IS NULL THEN
        EXECUTE 'CREATE TRIGGER trg_compras_baja_vinculo_reclamo AFTER UPDATE ON autorizaciones.reclamos_prestacionales FOR EACH ROW WHEN (new.baja_fecha IS NOT NULL) EXECUTE PROCEDURE compras.baja_vinculo_reclamo_prestacional_trigger()';
    ELSIF hash_actual IS DISTINCT FROM '8affbca47fd3deac1ba245996fa9e4ac' THEN
        RAISE EXCEPTION 'Trigger existente distinto: trg_compras_baja_vinculo_reclamo';
    ELSIF habilitado IS DISTINCT FROM 'O' THEN
        RAISE EXCEPTION 'Trigger existente no habilitado origin: trg_compras_baja_vinculo_reclamo';
    END IF;

    SELECT md5(pg_get_triggerdef(t.oid, true)), t.tgenabled INTO hash_actual, habilitado
    FROM pg_trigger t
    JOIN pg_class c ON c.oid = t.tgrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND c.relname = 'requerimiento'
      AND t.tgname = 'trg_compras_requerimiento_completar_baja' AND NOT t.tgisinternal;
    IF hash_actual IS NULL THEN
        EXECUTE 'CREATE TRIGGER trg_compras_requerimiento_completar_baja BEFORE UPDATE OF estado ON compras.requerimiento FOR EACH ROW EXECUTE PROCEDURE compras.completar_baja_requerimiento_fila()';
    ELSIF hash_actual IS DISTINCT FROM 'd08e74884d3e9f56c5d1c95e2e8ccdf6' THEN
        RAISE EXCEPTION 'Trigger existente distinto: trg_compras_requerimiento_completar_baja';
    ELSIF habilitado IS DISTINCT FROM 'O' THEN
        RAISE EXCEPTION 'Trigger existente no habilitado origin: trg_compras_requerimiento_completar_baja';
    END IF;

    SELECT md5(pg_get_triggerdef(t.oid, true)), t.tgenabled INTO hash_actual, habilitado
    FROM pg_trigger t
    JOIN pg_class c ON c.oid = t.tgrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND c.relname = 'requerimiento'
      AND t.tgname = 'trg_compras_requerimiento_gastos_envio' AND NOT t.tgisinternal;
    IF hash_actual IS NULL THEN
        EXECUTE 'CREATE TRIGGER trg_compras_requerimiento_gastos_envio AFTER INSERT ON compras.requerimiento FOR EACH ROW EXECUTE PROCEDURE compras.agregar_gastos_envio_requerimiento()';
    ELSIF hash_actual IS DISTINCT FROM 'b788c73806a48ead0a4d732108b6fc69' THEN
        RAISE EXCEPTION 'Trigger existente distinto: trg_compras_requerimiento_gastos_envio';
    ELSIF habilitado IS DISTINCT FROM 'O' THEN
        RAISE EXCEPTION 'Trigger existente no habilitado origin: trg_compras_requerimiento_gastos_envio';
    END IF;

    SELECT md5(pg_get_triggerdef(t.oid, true)), t.tgenabled INTO hash_actual, habilitado
    FROM pg_trigger t
    JOIN pg_class c ON c.oid = t.tgrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND c.relname = 'requerimiento_detalle'
      AND t.tgname = 'tr_compras_detalle_codigo_prestacion' AND NOT t.tgisinternal;
    IF hash_actual IS NULL THEN
        EXECUTE 'CREATE TRIGGER tr_compras_detalle_codigo_prestacion BEFORE INSERT OR UPDATE OF id_requerimiento, tipo_item, id_prestacion, codigo_nomenclador, baja_fecha ON compras.requerimiento_detalle FOR EACH ROW EXECUTE PROCEDURE compras.validar_codigo_prestacion_detalle_fila()';
    ELSIF hash_actual IS DISTINCT FROM '2439e7d743aeff6522b705306e46210d' THEN
        RAISE EXCEPTION 'Trigger existente distinto: tr_compras_detalle_codigo_prestacion';
    ELSIF habilitado IS DISTINCT FROM 'O' THEN
        RAISE EXCEPTION 'Trigger existente no habilitado origin: tr_compras_detalle_codigo_prestacion';
    END IF;

    SELECT md5(pg_get_triggerdef(t.oid, true)), t.tgenabled INTO hash_actual, habilitado
    FROM pg_trigger t
    JOIN pg_class c ON c.oid = t.tgrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND c.relname = 'requerimiento_detalle'
      AND t.tgname = 'trg_compras_detalle_calcular_total' AND NOT t.tgisinternal;
    IF hash_actual IS NULL THEN
        EXECUTE 'CREATE TRIGGER trg_compras_detalle_calcular_total BEFORE INSERT OR UPDATE OF cantidad, precio_unitario_estimado ON compras.requerimiento_detalle FOR EACH ROW EXECUTE PROCEDURE compras.calcular_total_detalle_fila()';
    ELSIF hash_actual IS DISTINCT FROM '71a3dd1695448c2fe75421aeec6e06b4' THEN
        RAISE EXCEPTION 'Trigger existente distinto: trg_compras_detalle_calcular_total';
    ELSIF habilitado IS DISTINCT FROM 'O' THEN
        RAISE EXCEPTION 'Trigger existente no habilitado origin: trg_compras_detalle_calcular_total';
    END IF;

END;
$restaurar_efectos$;
COMMIT;
