-- Edicion acotada de condiciones de OC emitidas. Reejecutable.
-- Conserva snapshots, auditoria de alta e identificadores existentes.
BEGIN;
DO $upgrade$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'compras' AND table_name = 'requerimiento_orden_compra'
        AND column_name = 'modi_fecha') THEN
        ALTER TABLE compras.requerimiento_orden_compra
            ADD COLUMN modi_fecha TIMESTAMP WITHOUT TIME ZONE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'compras' AND table_name = 'requerimiento_orden_compra'
        AND column_name = 'modi_usr') THEN
        ALTER TABLE compras.requerimiento_orden_compra ADD COLUMN modi_usr VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'compras' AND table_name = 'requerimiento_orden_compra'
        AND column_name = 'version_edicion') THEN
        ALTER TABLE compras.requerimiento_orden_compra
            ADD COLUMN version_edicion INTEGER NOT NULL DEFAULT 0
            CHECK (version_edicion >= 0);
    END IF;
END;
$upgrade$;
COMMIT;
