-- Compras: precondiciones de la etapa Java de Orden de Compra.
-- No ejecutado. Solo consulta catalogo; no retira ni cambia objetos.
-- Referencia: definiciones instaladas capturadas el 2026-10-30.
-- Aplicar despues de revisar consumidores externos y smoke tests reales.
BEGIN TRANSACTION READ ONLY;
DO $validacion$
BEGIN
    IF md5(pg_get_functiondef(to_regprocedure('compras.bloquear_creacion_orden_compra(integer,character varying)'))) IS DISTINCT FROM 'c59938a071c4186fb7802dd2536d5130' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.bloquear_creacion_orden_compra(integer,character varying)';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)'))) IS DISTINCT FROM '13b57466874bc1fcfa9b7e6ddcc15615' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.confirmar_orden_compra_requerimiento(integer,character varying)';
    END IF;
    IF md5(pg_get_viewdef(to_regclass('compras.requerimiento_orden_compra_consulta'), true)) IS DISTINCT FROM '0c2fc772da54e0be2b5763cdef6700d2' THEN
        RAISE EXCEPTION 'Deriva de vista: compras.requerimiento_orden_compra_consulta';
    END IF;
    IF md5(pg_get_viewdef(to_regclass('compras.requerimiento_orden_compra_detalle_consulta'), true)) IS DISTINCT FROM '0a1b3471897bb97658b9be2496e99c47' THEN
        RAISE EXCEPTION 'Deriva de vista: compras.requerimiento_orden_compra_detalle_consulta';
    END IF;
END;
$validacion$;
ROLLBACK;
-- Las funciones de bloqueo/confirmacion permanecen instaladas para consumidores
-- externos no demostrables por el repositorio. Los callers OC Java ya coordinan
-- reglas en Helper y usan locks, lecturas y CAS por una sola transaccion JDBC.
-- No se incluye DROP habilitado; falta validacion real de la migracion siguiente.
