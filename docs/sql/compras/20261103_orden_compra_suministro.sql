-- Seguimiento editable por OC emitida. No altera condiciones comerciales.
-- Reejecutable; conserva las gestiones ya guardadas.
BEGIN;
CREATE TABLE IF NOT EXISTS compras.requerimiento_orden_compra_suministro (
    id_orden_compra INTEGER PRIMARY KEY
        REFERENCES compras.requerimiento_orden_compra (id_orden_compra),
    fecha_contacto DATE,
    persona_contactada VARCHAR(100),
    medio_contacto VARCHAR(100),
    detalle VARCHAR(4000),
    respuesta_1 VARCHAR(15),
    respuesta_2 VARCHAR(15),
    respuesta_3 VARCHAR(15),
    respuesta_4 VARCHAR(15),
    respuesta_5 VARCHAR(15),
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL CHECK (btrim(alta_usr) <> ''),
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100) CHECK (modi_usr IS NULL OR btrim(modi_usr) <> ''),
    version_edicion INTEGER NOT NULL DEFAULT 1 CHECK (version_edicion > 0),
    CHECK (respuesta_1 IS NULL OR respuesta_1 IN ('', 'SI', 'NO', 'NO_CORRESPONDE')),
    CHECK (respuesta_2 IS NULL OR respuesta_2 IN ('', 'SI', 'NO', 'NO_CORRESPONDE')),
    CHECK (respuesta_3 IS NULL OR respuesta_3 IN ('', 'SI', 'NO', 'NO_CORRESPONDE')),
    CHECK (respuesta_4 IS NULL OR respuesta_4 IN ('', 'SI', 'NO', 'NO_CORRESPONDE')),
    CHECK (respuesta_5 IS NULL OR respuesta_5 IN ('', 'SI', 'NO', 'NO_CORRESPONDE'))
);
COMMIT;
