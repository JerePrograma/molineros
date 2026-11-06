-- Encuesta de suministro confirmada: recepcion y satisfaccion de la entrega.
-- Campos propios; no reinterpreta ni elimina las cinco respuestas provisorias.
-- Referencia: 20261026_orden_compra_edicion_condiciones.sql, upgrade aditivo.
BEGIN;
DO $upgrade$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'compras' AND table_name = 'requerimiento_orden_compra_suministro'
        AND column_name = 'recepcion_suministro') THEN
        ALTER TABLE compras.requerimiento_orden_compra_suministro
            ADD COLUMN recepcion_suministro VARCHAR(15)
            CHECK (recepcion_suministro IS NULL OR recepcion_suministro
                IN ('', 'SI', 'PARCIALMENTE', 'NO'));
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'compras' AND table_name = 'requerimiento_orden_compra_suministro'
        AND column_name = 'satisfaccion_entrega') THEN
        ALTER TABLE compras.requerimiento_orden_compra_suministro
            ADD COLUMN satisfaccion_entrega VARCHAR(20)
            CHECK (satisfaccion_entrega IS NULL OR satisfaccion_entrega
                IN ('', 'MUY_SATISFECHO', 'SATISFECHO', 'NEUTRAL', 'INSATISFECHO', 'MUY_INSATISFECHO'));
    END IF;
END;
$upgrade$;
COMMIT;
