\set ON_ERROR_STOP on
-- Ejecutar solamente en una base vacia creada para esta prueba.
-- Los contratos externos son fixtures; el bloqueo, confirmacion y trigger
-- se cargan de la migracion real, sin copiar sus implementaciones.
DO $test$
BEGIN
    IF current_database() NOT LIKE 'compras_orden_test_%' THEN
        RAISE EXCEPTION 'Esta prueba requiere una base aislada compras_orden_test_*.';
    END IF;
    IF EXISTS (SELECT 1 FROM pg_namespace WHERE nspname = 'compras') THEN
        RAISE EXCEPTION 'La base de prueba debe estar vacia.';
    END IF;
END;
$test$;
CREATE SCHEMA compras;
CREATE SCHEMA autorizaciones;
CREATE TABLE compras.sector_requerimiento (
    id_sector INTEGER PRIMARY KEY, descripcion VARCHAR(120),
    activo BOOLEAN DEFAULT TRUE, baja_fecha TIMESTAMP,
    requiere_afiliado BOOLEAN DEFAULT FALSE
);
CREATE TABLE compras.requerimiento (
                                       id_requerimiento SERIAL PRIMARY KEY,

                                       estado INTEGER NOT NULL DEFAULT 1,

                                       id_sector INTEGER NOT NULL
                                           REFERENCES compras.sector_requerimiento (id_sector),

                                       afiliado_cuil_titular VARCHAR(20),
                                       afiliado_int INTEGER,
                                       afiliado_id_ospim INTEGER,

    -- Snapshot para consulta e impresion.
                                       afiliado_nombre VARCHAR(120),
                                       afiliado_apellido VARCHAR(120),
                                       afiliado_documento_tipo VARCHAR(10),
                                       afiliado_documento_nro VARCHAR(30),
                                       afiliado_direccion VARCHAR(250),
                                       afiliado_localidad VARCHAR(120),
                                       afiliado_provincia VARCHAR(120),
                                       afiliado_celular VARCHAR(80),
                                       afiliado_telefono VARCHAR(80),
                                       afiliado_email VARCHAR(160),

                                       cargo_ospim INTEGER NOT NULL DEFAULT 100,
                                       cargo_tercerizadora INTEGER NOT NULL DEFAULT 0,
                                       id_tercerizadora VARCHAR(40),
                                       recupero BOOLEAN NOT NULL DEFAULT FALSE,
                                       surge BOOLEAN NOT NULL DEFAULT FALSE,
                                       legales BOOLEAN NOT NULL DEFAULT FALSE,

                                       observaciones TEXT,
                                       observacion_interna TEXT,

                                       alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                       alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
                                       modi_fecha TIMESTAMP WITHOUT TIME ZONE,
                                       modi_usr VARCHAR(100),
                                       baja_fecha TIMESTAMP WITHOUT TIME ZONE,
                                       baja_usr VARCHAR(100),
                                       motivo_baja TEXT,

                                       CONSTRAINT ck_compras_requerimiento_estado
                                           CHECK (estado IN (1, 2, 3, 4, 5, 6, 7, 99)),

                                       CONSTRAINT ck_compras_requerimiento_afiliado_int
                                           CHECK (afiliado_int IS NULL OR afiliado_int >= 0),

                                       CONSTRAINT ck_compras_requerimiento_cargo_ospim
                                           CHECK (cargo_ospim BETWEEN 0 AND 100),

                                       CONSTRAINT ck_compras_requerimiento_cargo_tercerizadora
                                           CHECK (cargo_tercerizadora BETWEEN 0 AND 100),

                                       CONSTRAINT ck_compras_requerimiento_cargos_total
                                           CHECK (cargo_ospim + cargo_tercerizadora = 100),

                                       CONSTRAINT ck_compras_requerimiento_tercerizadora
                                           CHECK (
                                               cargo_tercerizadora = 0
                                                   OR NULLIF(btrim(id_tercerizadora), '') IS NOT NULL
                                               ),

                                       CONSTRAINT ck_compras_requerimiento_recupero
                                           CHECK (recupero = (cargo_tercerizadora > 0))
);
CREATE TABLE compras.requerimiento_detalle (
    id_detalle INTEGER PRIMARY KEY,
    id_requerimiento INTEGER REFERENCES compras.requerimiento,
    id_prestador INTEGER, cantidad INTEGER, precio_unitario_estimado NUMERIC(18,2),
    precio_total_estimado NUMERIC(18,2), baja_fecha TIMESTAMP
);
CREATE TABLE compras.requerimiento_presupuesto (
    id_requerimiento INTEGER, tipo_documento INTEGER,
    id_prestador INTEGER, empresa_adjudicada BOOLEAN,
    empresa_cuit VARCHAR(11), empresa_sucursal VARCHAR(6),
    descripcion_empresa VARCHAR(200), baja_fecha TIMESTAMP
);
CREATE TABLE compras.requerimiento_cotizacion_prestador (
    id_requerimiento INTEGER, id_prestador INTEGER, estado_envio VARCHAR(20)
);
CREATE TABLE autorizaciones.reclamos_prestacionales (
    id INTEGER PRIMARY KEY, baja_fecha TIMESTAMP
);
CREATE TABLE compras.requerimiento_reclamo_prestacional (
    id_requerimiento INTEGER, id_reclamo_prestacional INTEGER,
    estado VARCHAR(20)
);
CREATE FUNCTION compras.normalizar_sector(VARCHAR) RETURNS VARCHAR
    AS $$SELECT upper(btrim($1))::VARCHAR$$ LANGUAGE SQL;
CREATE FUNCTION compras.normalizar_usuario(VARCHAR) RETURNS VARCHAR
    AS $$SELECT COALESCE(NULLIF(btrim($1), ''), 'sistema')::VARCHAR$$ LANGUAGE SQL;
CREATE FUNCTION compras.puede_anular_requerimiento(INTEGER) RETURNS BOOLEAN
    AS $$SELECT FALSE$$ LANGUAGE SQL;
CREATE FUNCTION compras.buscar_empresas_cotizacion(VARCHAR, VARCHAR, VARCHAR, INTEGER)
    RETURNS TABLE (cuit VARCHAR, sucursal VARCHAR, razon_soc VARCHAR)
    AS $$SELECT NULL::VARCHAR, NULL::VARCHAR, NULL::VARCHAR WHERE FALSE$$ LANGUAGE SQL;

INSERT INTO compras.sector_requerimiento (id_sector, descripcion, requiere_afiliado)
VALUES (1, 'SISTEMAS', FALSE), (2, 'PRESTACIONES MEDICAS', TRUE);
INSERT INTO compras.requerimiento (id_requerimiento, estado, id_sector,
    afiliado_cuil_titular, afiliado_int)
VALUES (1001, 7, 1, NULL, NULL), (1002, 7, 2, '20111111112', 0),
       (1003, 1, 1, NULL, NULL), (1004, 7, 1, NULL, NULL),
       (1005, 7, 2, '20111111112', 0), (1006, 7, 2, '20111111112', 0),
       (1007, 7, 1, NULL, NULL), (1008, 7, 1, NULL, NULL),
       (1009, 7, 1, NULL, NULL);
INSERT INTO compras.requerimiento_detalle
    (id_detalle, id_requerimiento, id_prestador, cantidad,
     precio_unitario_estimado, precio_total_estimado)
VALUES (2001, 1001, NULL, 2, NULL, NULL), (2002, 1002, 50, 2, 12.34, 24.68),
       (2003, 1003, NULL, 1, NULL, NULL), (2004, 1004, NULL, 1, NULL, NULL),
       (2005, 1005, 50, 1, 10, 10), (2006, 1006, 50, 1, 10, 10),
       (2016, 1006, 51, 1, 20, 20), (2007, 1007, NULL, 1, NULL, NULL),
       (2008, 1008, NULL, 1, NULL, NULL), (2009, 1009, NULL, 1, NULL, NULL);
INSERT INTO compras.requerimiento_presupuesto
    (id_requerimiento, tipo_documento, empresa_adjudicada, descripcion_empresa)
VALUES (1001, 3, TRUE, 'Empresa Fixture'), (1007, 3, TRUE, 'Empresa Fixture'),
       (1008, 3, TRUE, 'Empresa Fixture'), (1009, 3, TRUE, 'Empresa Fixture');
INSERT INTO compras.requerimiento_presupuesto
    (id_requerimiento, tipo_documento, id_prestador)
VALUES (1002, 1, 50), (1005, 1, 50), (1006, 1, 50), (1006, 1, 51);
INSERT INTO autorizaciones.reclamos_prestacionales (id) VALUES (3002), (3006);
INSERT INTO compras.requerimiento_reclamo_prestacional
    (id_requerimiento, id_reclamo_prestacional, estado)
VALUES (1002, 3002, 'VINCULADO'), (1006, 3006, 'VINCULADO');

\ir ../../docs/sql/compras/20261023_orden_compra_formulario.sql
CREATE TRIGGER trg_compras_requerimiento_validar
BEFORE INSERT OR UPDATE ON compras.requerimiento FOR EACH ROW
EXECUTE PROCEDURE compras.validar_requerimiento_fila();

DO $test$
DECLARE
    v_id INTEGER;
BEGIN
    ASSERT compras.bloquear_creacion_orden_compra(1001, 'fixture') = 0;
    INSERT INTO compras.requerimiento_orden_compra
        (id_requerimiento, fecha_emision, moneda, proveedor, confeccionado_por,
         alta_usr, iva, iibb, observaciones)
    VALUES (1001, DATE '2026-10-23', 'ARS', 'Empresa Fixture', 'Operador Fixture',
            'fixture', 4.20, 0.80, 'Snapshot verificable')
    RETURNING id_orden_compra INTO v_id;
    INSERT INTO compras.requerimiento_orden_compra_detalle
        (id_orden_compra, orden, id_requerimiento_detalle, codigo_item,
         descripcion_item, cantidad, precio_unitario, precio_total)
    VALUES (v_id, 1, 2001, 'SYS', 'Equipo de prueba', 2, 10, 20);
    ASSERT compras.confirmar_orden_compra_requerimiento(1001, 'fixture') = 5;
    ASSERT (SELECT estado FROM compras.requerimiento WHERE id_requerimiento = 1001) = 5;
    ASSERT compras.bloquear_creacion_orden_compra(1001, 'fixture') = v_id;
    ASSERT compras.confirmar_orden_compra_requerimiento(1001, 'fixture') = 5;
    ASSERT (SELECT count(*) FROM compras.requerimiento_orden_compra
             WHERE id_requerimiento = 1001) = 1;
    ASSERT (SELECT sum(d.precio_total) + o.iva + o.iibb
              FROM compras.requerimiento_orden_compra o
              JOIN compras.requerimiento_orden_compra_detalle d USING (id_orden_compra)
             WHERE o.id_requerimiento = 1001 GROUP BY o.iva, o.iibb) = 25;
    BEGIN
        UPDATE compras.requerimiento SET observaciones = 'No permitido'
         WHERE id_requerimiento = 1001;
        RAISE EXCEPTION 'Falto rechazar modificacion de requerimiento emitido.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'El requerimiento no puede modificarse en el estado actual.' THEN RAISE; END IF;
    END;
END;
$test$;

DO $test$
DECLARE
    v_id INTEGER;
BEGIN
    ASSERT compras.bloquear_creacion_orden_compra(1002, 'fixture') = 0;
    INSERT INTO compras.requerimiento_orden_compra
        (id_requerimiento, fecha_emision, moneda, proveedor, confeccionado_por,
         alta_usr, plan, id_reclamo_prestacional)
    VALUES (1002, DATE '2026-10-23', 'ARS', 'Prestador Fixture', 'Operador Fixture',
            'fixture', 'Plan Fixture', 3002) RETURNING id_orden_compra INTO v_id;
    INSERT INTO compras.requerimiento_orden_compra_detalle
        (id_orden_compra, orden, id_requerimiento_detalle, descripcion_item,
         cantidad, precio_unitario, precio_total)
    VALUES (v_id, 1, 2002, 'Prestacion de prueba', 2, 12.34, 24.68);
    ASSERT compras.confirmar_orden_compra_requerimiento(1002, 'fixture') = 5;
    ASSERT (SELECT estado FROM compras.requerimiento WHERE id_requerimiento = 1002) = 5;
    ASSERT compras.bloquear_creacion_orden_compra(1002, 'fixture') = v_id;
    ASSERT (SELECT id_requerimiento_detalle
              FROM compras.requerimiento_orden_compra_detalle
             WHERE id_orden_compra = v_id) = 2002;
END;
$test$;

DO $test$
BEGIN
    BEGIN
        PERFORM compras.bloquear_creacion_orden_compra(1003, 'fixture');
        RAISE EXCEPTION 'Falto rechazar estado no autorizado.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'La Orden de Compra solo puede crearse desde AUTORIZADO.' THEN RAISE; END IF;
    END;
    BEGIN
        PERFORM compras.bloquear_creacion_orden_compra(1004, 'fixture');
        RAISE EXCEPTION 'Falto rechazar empresa no adjudicada.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM NOT LIKE 'Debe existir al menos una cotizacion de Empresa%' THEN RAISE; END IF;
    END;
    BEGIN
        PERFORM compras.bloquear_creacion_orden_compra(1005, 'fixture');
        RAISE EXCEPTION 'Falto rechazar RP ausente.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.' THEN RAISE; END IF;
    END;
    BEGIN
        PERFORM compras.bloquear_creacion_orden_compra(1006, 'fixture');
        RAISE EXCEPTION 'Falto rechazar prestadores mezclados.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'La Orden de Compra requiere un prestador adjudicado unico y detalles cotizados completos.' THEN RAISE; END IF;
    END;
    BEGIN
        PERFORM compras.bloquear_creacion_orden_compra(1007, '');
        RAISE EXCEPTION 'Falto rechazar usuario ausente.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'No se pudo determinar el usuario de auditoria.' THEN RAISE; END IF;
    END;
    -- Un error de detalle debe revertir cabecera y conservar AUTORIZADO.
    BEGIN
        PERFORM compras.bloquear_creacion_orden_compra(1008, 'fixture');
        INSERT INTO compras.requerimiento_orden_compra
            (id_requerimiento, fecha_emision, moneda, proveedor, confeccionado_por, alta_usr)
        VALUES (1008, CURRENT_DATE, 'ARS', 'Empresa Fixture', 'Operador Fixture', 'fixture');
        PERFORM compras.confirmar_orden_compra_requerimiento(1008, 'fixture');
        RAISE EXCEPTION 'Falto rechazar snapshot sin detalles.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'El detalle de la Orden de Compra no coincide con el requerimiento.' THEN RAISE; END IF;
    END;
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_orden_compra WHERE id_requerimiento = 1008);
    ASSERT (SELECT estado FROM compras.requerimiento WHERE id_requerimiento = 1008) = 7;
END;
$test$;
DO $test$
DECLARE
    v_id INTEGER;
BEGIN
    BEGIN
        UPDATE compras.requerimiento SET estado = 5, modi_usr = 'fixture'
         WHERE id_requerimiento = 1005;
        RAISE EXCEPTION 'Falto validar RP en el trigger de transicion directa.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.' THEN RAISE; END IF;
    END;
    BEGIN
        PERFORM compras.bloquear_creacion_orden_compra(1009, 'fixture');
        INSERT INTO compras.requerimiento_orden_compra
            (id_requerimiento, fecha_emision, moneda, proveedor, confeccionado_por, alta_usr)
        VALUES (1009, CURRENT_DATE, 'ARS', 'Empresa Fixture', 'Operador Fixture', 'fixture')
        RETURNING id_orden_compra INTO v_id;
        INSERT INTO compras.requerimiento_orden_compra_detalle
            (id_orden_compra, orden, id_requerimiento_detalle, descripcion_item,
             cantidad, precio_unitario, precio_total)
        VALUES (v_id, 1, 2007, 'Detalle de otro requerimiento', 1, 10, 10);
        PERFORM compras.confirmar_orden_compra_requerimiento(1009, 'fixture');
        RAISE EXCEPTION 'Falto rechazar detalle de otro requerimiento.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'El detalle de la Orden de Compra no coincide con el requerimiento.' THEN RAISE; END IF;
    END;
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_orden_compra WHERE id_requerimiento = 1009);
    ASSERT (SELECT estado FROM compras.requerimiento WHERE id_requerimiento = 1009) = 7;
END;
$test$;
SELECT 'orden_compra_test: OK' AS resultado;
