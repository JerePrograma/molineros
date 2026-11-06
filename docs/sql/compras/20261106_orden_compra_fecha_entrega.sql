-- Fecha estimada de entrega confirmada por el proveedor.
-- Referencia: 20261026_orden_compra_edicion_condiciones.sql, upgrade aditivo.
-- Las OC historicas conservan NULL; el Helper exige fecha al crear una OC nueva.
-- No cambia firmas SQL ni rellena fechas supuestas para ordenes ya emitidas.
BEGIN;
DO $upgrade$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'compras' AND table_name = 'requerimiento_orden_compra'
        AND column_name = 'fecha_estimada_entrega') THEN
        ALTER TABLE compras.requerimiento_orden_compra
            ADD COLUMN fecha_estimada_entrega DATE;
    END IF;
END;
$upgrade$;
COMMIT;
