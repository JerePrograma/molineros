\set ON_ERROR_STOP on
-- Requiere una base vacia aislada. Las tablas OC son el DDL del commit 6c382267.
DO $test$
BEGIN
    IF current_database() NOT LIKE 'compras_orden_upgrade_test_%' THEN
        RAISE EXCEPTION 'Esta prueba requiere una base aislada compras_orden_upgrade_test_*.';
    END IF;
    IF EXISTS (SELECT 1 FROM pg_namespace WHERE nspname = 'compras') THEN
        RAISE EXCEPTION 'La base de prueba debe estar vacia.';
    END IF;
END;
$test$;
CREATE SCHEMA compras;
CREATE TABLE compras.requerimiento (id_requerimiento INTEGER PRIMARY KEY);
CREATE TABLE compras.requerimiento_detalle (id_detalle INTEGER PRIMARY KEY);
INSERT INTO compras.requerimiento VALUES (1), (2), (3);
INSERT INTO compras.requerimiento_detalle VALUES (1), (2), (3);
CREATE TABLE compras.requerimiento_orden_compra (
    id_orden_compra SERIAL PRIMARY KEY,
    id_requerimiento INTEGER NOT NULL UNIQUE
        REFERENCES compras.requerimiento (id_requerimiento),
    fecha_emision DATE NOT NULL,
    fecha_orden_medica DATE,
    fecha_recepcion DATE,
    moneda VARCHAR(10) NOT NULL,
    proveedor VARCHAR(250) NOT NULL,
    proveedor_cuit VARCHAR(20),
    proveedor_domicilio VARCHAR(250),
    proveedor_contacto VARCHAR(250),
    proveedor_email VARCHAR(250),
    proveedor_telefono VARCHAR(100),
    condicion VARCHAR(1000),
    forma_pago VARCHAR(250),
    plazo_pago VARCHAR(250),
    entrega VARCHAR(1000),
    observaciones VARCHAR(4000),
    confeccionado_por VARCHAR(250) NOT NULL,
    telefono_compras VARCHAR(100),
    solicitante VARCHAR(250),
    plan VARCHAR(120),
    id_reclamo_prestacional INTEGER NOT NULL DEFAULT 0,
    iva NUMERIC(18,2) NOT NULL DEFAULT 0 CHECK (iva >= 0),
    iibb NUMERIC(18,2) NOT NULL DEFAULT 0 CHECK (iibb >= 0),
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL,
    CHECK (btrim(proveedor) <> ''),
    CHECK (btrim(confeccionado_por) <> ''),
    CHECK (btrim(alta_usr) <> ''),
    CHECK (moneda IN ('ARS', 'USD'))
);

CREATE TABLE compras.requerimiento_orden_compra_detalle (
    id_orden_compra INTEGER NOT NULL
        REFERENCES compras.requerimiento_orden_compra (id_orden_compra),
    orden INTEGER NOT NULL CHECK (orden > 0),
    id_requerimiento_detalle INTEGER NOT NULL
        REFERENCES compras.requerimiento_detalle (id_detalle),
    codigo_item VARCHAR(100),
    descripcion_item TEXT NOT NULL CHECK (btrim(descripcion_item) <> ''),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(18,2) NOT NULL CHECK (precio_unitario >= 0),
    precio_total NUMERIC(18,2) NOT NULL CHECK (precio_total >= 0),
    PRIMARY KEY (id_orden_compra, orden),
    UNIQUE (id_orden_compra, id_requerimiento_detalle),
    CHECK (precio_total = round(cantidad * precio_unitario, 2))
);


INSERT INTO compras.requerimiento_orden_compra
    (id_requerimiento, fecha_emision, moneda, proveedor, confeccionado_por,
     alta_usr, iva, iibb, observaciones)
VALUES (1, DATE '2026-10-23', 'ARS', 'Empresa anterior', 'Operador anterior',
        'fixture', 4.20, 0.80, 'Conservar sin cambios');
INSERT INTO compras.requerimiento_orden_compra_detalle
    (id_orden_compra, orden, id_requerimiento_detalle, descripcion_item,
     cantidad, precio_unitario, precio_total)
VALUES (1, 1, 1, 'Equipo anterior', 2, 10, 20);

\ir ../../docs/sql/compras/20261023_orden_compra_generacion_directa.sql
\ir ../../docs/sql/compras/20261023_orden_compra_generacion_directa.sql
DO $test$
BEGIN
    ASSERT (SELECT count(*) FROM compras.requerimiento_orden_compra) = 1;
    ASSERT (SELECT moneda = 'ARS' AND iva = 4.20 AND iibb = 0.80
                   AND observaciones = 'Conservar sin cambios'
              FROM compras.requerimiento_orden_compra WHERE id_orden_compra = 1);
    ASSERT (SELECT precio_unitario = 10 AND precio_total = 20 AND cantidad = 2
              FROM compras.requerimiento_orden_compra_detalle WHERE id_orden_compra = 1);
END;
$test$;
INSERT INTO compras.requerimiento_orden_compra
    (id_requerimiento, fecha_emision, proveedor, confeccionado_por, alta_usr)
VALUES (2, CURRENT_DATE, 'Empresa sin datos monetarios', 'Operador', 'fixture');
INSERT INTO compras.requerimiento_orden_compra_detalle
    (id_orden_compra, orden, id_requerimiento_detalle, descripcion_item, cantidad)
VALUES (2, 1, 2, 'Equipo sin precio informado', 2);
DO $test$
BEGIN
    ASSERT (SELECT moneda IS NULL AND iva IS NULL AND iibb IS NULL
              FROM compras.requerimiento_orden_compra WHERE id_requerimiento = 2);
    ASSERT (SELECT precio_unitario IS NULL AND precio_total IS NULL
              FROM compras.requerimiento_orden_compra_detalle WHERE id_orden_compra = 2);
    BEGIN
        UPDATE compras.requerimiento_orden_compra_detalle SET precio_unitario = 10
         WHERE id_orden_compra = 2;
        RAISE EXCEPTION 'Falto rechazar precio unitario sin total.';
    EXCEPTION WHEN check_violation THEN ASSERT SQLSTATE = '23514';
    END;
    BEGIN
        UPDATE compras.requerimiento_orden_compra_detalle SET precio_total = 20
         WHERE id_orden_compra = 2;
        RAISE EXCEPTION 'Falto rechazar total sin precio unitario.';
    EXCEPTION WHEN check_violation THEN ASSERT SQLSTATE = '23514';
    END;
    BEGIN
        UPDATE compras.requerimiento_orden_compra_detalle SET precio_unitario = -10, precio_total = -20
         WHERE id_orden_compra = 2;
        RAISE EXCEPTION 'Falto rechazar importes negativos.';
    EXCEPTION WHEN check_violation THEN ASSERT SQLSTATE = '23514';
    END;
    BEGIN
        UPDATE compras.requerimiento_orden_compra SET moneda = 'XXX' WHERE id_requerimiento = 2;
        RAISE EXCEPTION 'Falto rechazar moneda no admitida.';
    EXCEPTION WHEN check_violation THEN ASSERT SQLSTATE = '23514';
    END;
    ASSERT (SELECT moneda IS NULL AND iva IS NULL AND iibb IS NULL
              FROM compras.requerimiento_orden_compra WHERE id_requerimiento = 2);
END;
$test$;
SELECT 'orden_compra_upgrade_test: OK' AS resultado;
