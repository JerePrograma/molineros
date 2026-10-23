-- Orden de Compra directa: conservar ausencias de datos sin completar ceros.
-- Aplicar sobre la instalacion que ya contiene las tablas de Orden de Compra.
-- No modifica snapshots existentes. Puede ejecutarse nuevamente.
BEGIN;

ALTER TABLE compras.requerimiento_orden_compra
    ALTER COLUMN moneda DROP NOT NULL,
    ALTER COLUMN iva DROP NOT NULL,
    ALTER COLUMN iva DROP DEFAULT,
    ALTER COLUMN iibb DROP NOT NULL,
    ALTER COLUMN iibb DROP DEFAULT;

ALTER TABLE compras.requerimiento_orden_compra_detalle
    ALTER COLUMN precio_unitario DROP NOT NULL,
    ALTER COLUMN precio_total DROP NOT NULL;

-- Conservar los checks existentes, incluso sus nombres automaticos legacy.
DO $ajustar_importes$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
         WHERE conrelid = 'compras.requerimiento_orden_compra_detalle'::regclass
           AND conname = 'ck_compras_oc_detalle_importes_conjuntos'
    ) THEN
        ALTER TABLE compras.requerimiento_orden_compra_detalle
            ADD CONSTRAINT ck_compras_oc_detalle_importes_conjuntos CHECK (
                (precio_unitario IS NULL AND precio_total IS NULL)
                OR (precio_unitario IS NOT NULL AND precio_total IS NOT NULL
                    AND precio_total = round(cantidad * precio_unitario, 2))
            );
    END IF;
END;
$ajustar_importes$;

COMMIT;
