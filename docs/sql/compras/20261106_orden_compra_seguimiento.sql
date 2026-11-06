-- Seguimiento operativo manual por OC emitida, separado de la encuesta y condiciones.
-- Los IDs informados son referencias manuales; no altera contabilidad, facturas ni pagos.
-- Reejecutable: conserva los datos y la auditoria existentes; no inventa estados.
BEGIN;
CREATE TABLE IF NOT EXISTS compras.requerimiento_orden_compra_seguimiento (
    id_orden_compra INTEGER PRIMARY KEY
        REFERENCES compras.requerimiento_orden_compra (id_orden_compra),
    calificacion_proveedor VARCHAR(25)
        CHECK (calificacion_proveedor IS NULL OR calificacion_proveedor IN ('', 'APTO', 'PENDIENTE_ENTREGA', 'NO_RESPONDE')),
    observaciones_proveedor VARCHAR(4000),
    factura_ingresada VARCHAR(2)
        CHECK (factura_ingresada IS NULL OR factura_ingresada IN ('', 'SI', 'NO')),
    numero_factura VARCHAR(100),
    fecha_factura DATE,
    estado_liquidacion VARCHAR(25)
        CHECK (estado_liquidacion IS NULL OR estado_liquidacion IN ('', 'PENDIENTE', 'LIQUIDADA', 'NO_CORRESPONDE')),
    liquidacion_auditada BOOLEAN NOT NULL DEFAULT FALSE,
    estado_pago VARCHAR(25)
        CHECK (estado_pago IS NULL OR estado_pago IN ('', 'PENDIENTE_PAGO', 'PAGADO', 'NO_CORRESPONDE')),
    id_liquidacion INTEGER CHECK (id_liquidacion IS NULL OR id_liquidacion > 0),
    id_orden_pago INTEGER CHECK (id_orden_pago IS NULL OR id_orden_pago > 0),
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL CHECK (btrim(alta_usr) <> ''),
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100) CHECK (modi_usr IS NULL OR btrim(modi_usr) <> ''),
    version_edicion INTEGER NOT NULL DEFAULT 1 CHECK (version_edicion > 0),
    CHECK (NOT liquidacion_auditada OR COALESCE(estado_liquidacion = 'LIQUIDADA', FALSE))
);
COMMIT;
