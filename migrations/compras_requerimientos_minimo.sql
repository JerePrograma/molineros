-- ============================================================
-- MODULO: Compras - Requerimientos
-- PostgreSQL 9.6+
-- Modelo nuevo con historial de estado activo
-- ============================================================

BEGIN;

DROP SCHEMA IF EXISTS compras CASCADE;
CREATE SCHEMA compras;

CREATE TABLE compras.estados_requerimiento (
    id INTEGER PRIMARY KEY,
    descripcion VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE compras.sector_requerimiento (
    id SERIAL PRIMARY KEY,
    descripcion VARCHAR(120) NOT NULL UNIQUE,
    requiere_afiliado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE compras.requerimientos (
    id SERIAL PRIMARY KEY,

    alta_fecha TIMESTAMP NOT NULL DEFAULT now(),
    alta_usr VARCHAR(75) NOT NULL DEFAULT 'sistema',
    modi_fecha TIMESTAMP NULL,
    modi_usr VARCHAR(75) NULL,
    baja_fecha TIMESTAMP NULL,
    baja_usr VARCHAR(75) NULL,

    afiliado_cuil_titular VARCHAR(20) NULL,
    afiliado_int INTEGER NULL,

    id_sector INTEGER NOT NULL REFERENCES compras.sector_requerimiento(id),

    cargo_ospim SMALLINT NOT NULL DEFAULT 0,
    cargo_tercerizadora SMALLINT NOT NULL DEFAULT 0,

    id_tercerizadora INTEGER NULL,

    recupero BOOLEAN NOT NULL DEFAULT FALSE,

    observaciones TEXT NULL,

    CHECK (afiliado_int IS NULL OR afiliado_int >= 0),
    CHECK (cargo_ospim BETWEEN 0 AND 100),
    CHECK (cargo_tercerizadora BETWEEN 0 AND 100),
    CHECK ((cargo_ospim + cargo_tercerizadora) BETWEEN 0 AND 100),
    CHECK (cargo_tercerizadora = 0 OR id_tercerizadora IS NOT NULL)
);

CREATE TABLE compras.requerimientos_estados (
    id SERIAL PRIMARY KEY,

    id_requerimiento INTEGER NOT NULL REFERENCES compras.requerimientos(id),
    id_estado INTEGER NOT NULL REFERENCES compras.estados_requerimiento(id),

    alta_fecha TIMESTAMP NOT NULL DEFAULT now(),
    alta_usr VARCHAR(75) NOT NULL DEFAULT 'sistema',

    baja_fecha TIMESTAMP NULL,
    baja_usr VARCHAR(75) NULL
);

CREATE UNIQUE INDEX uq_req_estado_actual
    ON compras.requerimientos_estados(id_requerimiento)
    WHERE baja_fecha IS NULL;

CREATE TABLE compras.requerimiento_detalle (
    id SERIAL PRIMARY KEY,

    id_requerimiento INTEGER NOT NULL REFERENCES compras.requerimientos(id),

    articulo TEXT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario_estimado NUMERIC(14, 2) NULL,
    precio_total_estimado NUMERIC(14, 2) NULL,
    observaciones TEXT NULL,

    CHECK (length(trim(articulo)) > 0),
    CHECK (cantidad > 0),
    CHECK (precio_unitario_estimado IS NULL OR precio_unitario_estimado >= 0),
    CHECK (precio_total_estimado IS NULL OR precio_total_estimado >= 0)
);

CREATE INDEX idx_compras_req_sector ON compras.requerimientos(id_sector);
CREATE INDEX idx_compras_req_afiliado ON compras.requerimientos(afiliado_cuil_titular, afiliado_int);
CREATE INDEX idx_compras_req_tercerizadora ON compras.requerimientos(id_tercerizadora);
CREATE INDEX idx_compras_req_recupero ON compras.requerimientos(recupero);
CREATE INDEX idx_compras_req_alta ON compras.requerimientos(alta_fecha);
CREATE INDEX idx_compras_req_baja ON compras.requerimientos(baja_fecha);
CREATE INDEX idx_compras_req_estados_req ON compras.requerimientos_estados(id_requerimiento);
CREATE INDEX idx_compras_req_estados_estado ON compras.requerimientos_estados(id_estado);
CREATE INDEX idx_compras_req_detalle_req ON compras.requerimiento_detalle(id_requerimiento);

INSERT INTO compras.estados_requerimiento (id, descripcion)
VALUES
    (1, 'Borrador'),
    (2, 'Cotizado'),
    (3, 'Anulado');

INSERT INTO compras.sector_requerimiento (descripcion, requiere_afiliado)
VALUES
    ('Farmacia', TRUE),
    ('Prestaciones Medicas', TRUE),
    ('Auditoria Medica', TRUE),
    ('Monotributo', TRUE),
    ('Sistemas', FALSE),
    ('RRHH', FALSE),
    ('Legales', FALSE),
    ('Otros', FALSE);

CREATE OR REPLACE FUNCTION compras.listar_estados_requerimiento()
RETURNS TABLE (
    id INTEGER,
    descripcion VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT e.id, e.descripcion
    FROM compras.estados_requerimiento e
    ORDER BY e.id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.listar_sector_requerimiento()
RETURNS TABLE (
    id INTEGER,
    descripcion VARCHAR,
    requiere_afiliado BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT s.id, s.descripcion, s.requiere_afiliado
    FROM compras.sector_requerimiento s
    ORDER BY s.descripcion;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.get_sector_requerimiento(p_id_sector INTEGER)
RETURNS TABLE (
    id INTEGER,
    descripcion VARCHAR,
    requiere_afiliado BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT s.id, s.descripcion, s.requiere_afiliado
    FROM compras.sector_requerimiento s
    WHERE s.id = p_id_sector;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.get_estado_actual_requerimiento(p_id_requerimiento INTEGER)
RETURNS TABLE (
    id INTEGER,
    descripcion VARCHAR,
    alta_fecha TIMESTAMP,
    alta_usr VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT e.id, e.descripcion, re.alta_fecha, re.alta_usr
    FROM compras.requerimientos_estados re
    JOIN compras.estados_requerimiento e ON e.id = re.id_estado
    WHERE re.id_requerimiento = p_id_requerimiento
      AND re.baja_fecha IS NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.buscar_requerimientos(
    p_id_estado INTEGER,
    p_id_sector INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_id_tercerizadora INTEGER,
    p_recupero BOOLEAN,
    p_texto VARCHAR
) RETURNS TABLE (
    id INTEGER,
    alta_fecha TIMESTAMP,
    alta_usr VARCHAR,
    modi_fecha TIMESTAMP,
    modi_usr VARCHAR,
    baja_fecha TIMESTAMP,
    baja_usr VARCHAR,
    afiliado_cuil_titular VARCHAR,
    afiliado_int INTEGER,
    id_sector INTEGER,
    sector_descripcion VARCHAR,
    requiere_afiliado BOOLEAN,
    cargo_ospim INTEGER,
    cargo_tercerizadora INTEGER,
    id_tercerizadora INTEGER,
    recupero BOOLEAN,
    observaciones TEXT,
    id_estado INTEGER,
    estado_descripcion VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT r.id,
           r.alta_fecha,
           r.alta_usr,
           r.modi_fecha,
           r.modi_usr,
           r.baja_fecha,
           r.baja_usr,
           r.afiliado_cuil_titular,
           r.afiliado_int,
           r.id_sector,
           s.descripcion AS sector_descripcion,
           s.requiere_afiliado,
           r.cargo_ospim::INTEGER,
           r.cargo_tercerizadora::INTEGER,
           r.id_tercerizadora,
           r.recupero,
           r.observaciones,
           e.id AS id_estado,
           e.descripcion AS estado_descripcion
    FROM compras.requerimientos r
    JOIN compras.sector_requerimiento s ON s.id = r.id_sector
    JOIN compras.requerimientos_estados re
      ON re.id_requerimiento = r.id
     AND re.baja_fecha IS NULL
    JOIN compras.estados_requerimiento e ON e.id = re.id_estado
    WHERE r.baja_fecha IS NULL
      AND (p_id_estado IS NULL OR e.id = p_id_estado)
      AND (p_id_sector IS NULL OR r.id_sector = p_id_sector)
      AND (p_afiliado_cuil_titular IS NULL OR r.afiliado_cuil_titular LIKE '%' || trim(p_afiliado_cuil_titular) || '%')
      AND (p_afiliado_int IS NULL OR r.afiliado_int = p_afiliado_int)
      AND (p_id_tercerizadora IS NULL OR r.id_tercerizadora = p_id_tercerizadora)
      AND (p_recupero IS NULL OR r.recupero = p_recupero)
      AND (
            p_texto IS NULL
            OR lower(coalesce(r.observaciones, '')) LIKE '%' || lower(trim(p_texto)) || '%'
            OR lower(coalesce(s.descripcion, '')) LIKE '%' || lower(trim(p_texto)) || '%'
            OR coalesce(r.afiliado_cuil_titular, '') LIKE '%' || trim(p_texto) || '%'
            OR EXISTS (
                SELECT 1
                FROM compras.requerimiento_detalle d
                WHERE d.id_requerimiento = r.id
                  AND (
                        lower(coalesce(d.articulo, '')) LIKE '%' || lower(trim(p_texto)) || '%'
                        OR lower(coalesce(d.observaciones, '')) LIKE '%' || lower(trim(p_texto)) || '%'
                  )
            )
      )
    ORDER BY r.alta_fecha DESC, r.id DESC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.get_requerimiento(p_id INTEGER)
RETURNS TABLE (
    id INTEGER,
    alta_fecha TIMESTAMP,
    alta_usr VARCHAR,
    modi_fecha TIMESTAMP,
    modi_usr VARCHAR,
    baja_fecha TIMESTAMP,
    baja_usr VARCHAR,
    afiliado_cuil_titular VARCHAR,
    afiliado_int INTEGER,
    id_sector INTEGER,
    sector_descripcion VARCHAR,
    requiere_afiliado BOOLEAN,
    cargo_ospim INTEGER,
    cargo_tercerizadora INTEGER,
    id_tercerizadora INTEGER,
    recupero BOOLEAN,
    observaciones TEXT,
    id_estado INTEGER,
    estado_descripcion VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT r.id,
           r.alta_fecha,
           r.alta_usr,
           r.modi_fecha,
           r.modi_usr,
           r.baja_fecha,
           r.baja_usr,
           r.afiliado_cuil_titular,
           r.afiliado_int,
           r.id_sector,
           s.descripcion AS sector_descripcion,
           s.requiere_afiliado,
           r.cargo_ospim::INTEGER,
           r.cargo_tercerizadora::INTEGER,
           r.id_tercerizadora,
           r.recupero,
           r.observaciones,
           e.id AS id_estado,
           e.descripcion AS estado_descripcion
    FROM compras.requerimientos r
    JOIN compras.sector_requerimiento s ON s.id = r.id_sector
    JOIN compras.requerimientos_estados re
      ON re.id_requerimiento = r.id
     AND re.baja_fecha IS NULL
    JOIN compras.estados_requerimiento e ON e.id = re.id_estado
    WHERE r.id = p_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_detalle(p_id_requerimiento INTEGER)
RETURNS TABLE (
    id INTEGER,
    id_requerimiento INTEGER,
    articulo TEXT,
    cantidad INTEGER,
    precio_unitario_estimado NUMERIC,
    precio_total_estimado NUMERIC,
    observaciones TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT d.id,
           d.id_requerimiento,
           d.articulo,
           d.cantidad,
           d.precio_unitario_estimado,
           d.precio_total_estimado,
           d.observaciones
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
    ORDER BY d.id ASC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento(
    p_id INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_id_sector INTEGER,
    p_cargo_ospim INTEGER,
    p_cargo_tercerizadora INTEGER,
    p_id_tercerizadora INTEGER,
    p_recupero BOOLEAN,
    p_observaciones TEXT,
    p_usuario VARCHAR
) RETURNS INTEGER AS $$
DECLARE
    v_id INTEGER;
    v_requiere_afiliado BOOLEAN;
    v_estado_actual INTEGER;
    v_cargo_ospim INTEGER;
    v_cargo_tercerizadora INTEGER;
BEGIN
    IF p_id_sector IS NULL OR p_id_sector <= 0 THEN
        RAISE EXCEPTION 'Debe informar el sector.';
    END IF;

    SELECT s.requiere_afiliado
    INTO v_requiere_afiliado
    FROM compras.sector_requerimiento s
    WHERE s.id = p_id_sector;

    IF v_requiere_afiliado IS NULL THEN
        RAISE EXCEPTION 'El sector informado no existe.';
    END IF;

    IF v_requiere_afiliado = TRUE THEN
        IF p_afiliado_cuil_titular IS NULL OR length(trim(p_afiliado_cuil_titular)) = 0 THEN
            RAISE EXCEPTION 'Debe informar el CUIL titular del afiliado.';
        END IF;

        IF p_afiliado_int IS NULL OR p_afiliado_int < 0 THEN
            RAISE EXCEPTION 'Debe informar el integrante del afiliado.';
        END IF;
    END IF;

    v_cargo_ospim := COALESCE(p_cargo_ospim, 0);
    v_cargo_tercerizadora := COALESCE(p_cargo_tercerizadora, 0);

    IF v_cargo_ospim < 0 OR v_cargo_ospim > 100 THEN
        RAISE EXCEPTION 'Cargo OSPIM debe estar entre 0 y 100.';
    END IF;

    IF v_cargo_tercerizadora < 0 OR v_cargo_tercerizadora > 100 THEN
        RAISE EXCEPTION 'Cargo tercerizadora debe estar entre 0 y 100.';
    END IF;

    IF v_cargo_ospim + v_cargo_tercerizadora > 100 THEN
        RAISE EXCEPTION 'La suma de cargos no puede superar 100.';
    END IF;

    IF v_cargo_tercerizadora > 0 AND p_id_tercerizadora IS NULL THEN
        RAISE EXCEPTION 'Debe informar la tercerizadora cuando su cargo es mayor a cero.';
    END IF;

    IF p_id IS NULL OR p_id <= 0 THEN
        INSERT INTO compras.requerimientos (
            alta_usr,
            afiliado_cuil_titular,
            afiliado_int,
            id_sector,
            cargo_ospim,
            cargo_tercerizadora,
            id_tercerizadora,
            recupero,
            observaciones
        ) VALUES (
            trim(COALESCE(p_usuario, 'sistema')),
            NULLIF(trim(COALESCE(p_afiliado_cuil_titular, '')), ''),
            p_afiliado_int,
            p_id_sector,
            v_cargo_ospim,
            v_cargo_tercerizadora,
            p_id_tercerizadora,
            COALESCE(p_recupero, FALSE),
            NULLIF(trim(COALESCE(p_observaciones, '')), '')
        )
        RETURNING id INTO v_id;

        INSERT INTO compras.requerimientos_estados (
            id_requerimiento,
            id_estado,
            alta_usr
        ) VALUES (
            v_id,
            1,
            trim(COALESCE(p_usuario, 'sistema'))
        );

        RETURN v_id;
    END IF;

    SELECT re.id_estado
    INTO v_estado_actual
    FROM compras.requerimientos_estados re
    WHERE re.id_requerimiento = p_id
      AND re.baja_fecha IS NULL;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION 'El requerimiento no tiene estado activo.';
    END IF;

    IF v_estado_actual <> 1 THEN
        RAISE EXCEPTION 'Solo se pueden editar requerimientos en estado Borrador.';
    END IF;

    UPDATE compras.requerimientos
    SET afiliado_cuil_titular = NULLIF(trim(COALESCE(p_afiliado_cuil_titular, '')), ''),
        afiliado_int = p_afiliado_int,
        id_sector = p_id_sector,
        cargo_ospim = v_cargo_ospim,
        cargo_tercerizadora = v_cargo_tercerizadora,
        id_tercerizadora = p_id_tercerizadora,
        recupero = COALESCE(p_recupero, FALSE),
        observaciones = NULLIF(trim(COALESCE(p_observaciones, '')), ''),
        modi_fecha = now(),
        modi_usr = trim(COALESCE(p_usuario, 'sistema'))
    WHERE id = p_id
      AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento a modificar.';
    END IF;

    RETURN p_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento_detalle(
    p_id INTEGER,
    p_id_requerimiento INTEGER,
    p_articulo TEXT,
    p_cantidad INTEGER,
    p_precio_unitario_estimado NUMERIC,
    p_precio_total_estimado NUMERIC,
    p_observaciones TEXT,
    p_usuario VARCHAR
) RETURNS INTEGER AS $$
DECLARE
    v_id INTEGER;
    v_estado_actual INTEGER;
    v_cantidad INTEGER;
    v_precio_total NUMERIC;
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento.';
    END IF;

    SELECT re.id_estado
    INTO v_estado_actual
    FROM compras.requerimientos_estados re
    WHERE re.id_requerimiento = p_id_requerimiento
      AND re.baja_fecha IS NULL;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION 'No se encontro el estado activo del requerimiento.';
    END IF;

    IF v_estado_actual <> 1 THEN
        RAISE EXCEPTION 'Solo se pueden editar detalles de requerimientos en estado Borrador.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM compras.requerimientos r
        WHERE r.id = p_id_requerimiento
          AND r.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION 'No se encontro el requerimiento informado.';
    END IF;

    IF p_articulo IS NULL OR length(trim(p_articulo)) = 0 THEN
        RAISE EXCEPTION 'Debe informar el articulo.';
    END IF;

    v_cantidad := COALESCE(p_cantidad, 1);
    IF v_cantidad <= 0 THEN
        RAISE EXCEPTION 'La cantidad debe ser mayor a cero.';
    END IF;

    IF p_precio_unitario_estimado IS NOT NULL AND p_precio_unitario_estimado < 0 THEN
        RAISE EXCEPTION 'El precio unitario estimado no puede ser negativo.';
    END IF;

    IF p_precio_total_estimado IS NOT NULL AND p_precio_total_estimado < 0 THEN
        RAISE EXCEPTION 'El precio total estimado no puede ser negativo.';
    END IF;

    v_precio_total := p_precio_total_estimado;
    IF v_precio_total IS NULL AND p_precio_unitario_estimado IS NOT NULL THEN
        v_precio_total := v_cantidad * p_precio_unitario_estimado;
    END IF;

    IF p_id IS NULL OR p_id <= 0 THEN
        INSERT INTO compras.requerimiento_detalle (
            id_requerimiento,
            articulo,
            cantidad,
            precio_unitario_estimado,
            precio_total_estimado,
            observaciones
        ) VALUES (
            p_id_requerimiento,
            trim(p_articulo),
            v_cantidad,
            p_precio_unitario_estimado,
            v_precio_total,
            NULLIF(trim(COALESCE(p_observaciones, '')), '')
        )
        RETURNING id INTO v_id;

        RETURN v_id;
    END IF;

    UPDATE compras.requerimiento_detalle
    SET articulo = trim(p_articulo),
        cantidad = v_cantidad,
        precio_unitario_estimado = p_precio_unitario_estimado,
        precio_total_estimado = v_precio_total,
        observaciones = NULLIF(trim(COALESCE(p_observaciones, '')), '')
    WHERE id = p_id
      AND id_requerimiento = p_id_requerimiento;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el detalle a modificar.';
    END IF;

    RETURN p_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.borrar_requerimiento_detalle(
    p_id INTEGER,
    p_usuario VARCHAR
) RETURNS VOID AS $$
DECLARE
    v_estado_actual INTEGER;
BEGIN
    SELECT re.id_estado
    INTO v_estado_actual
    FROM compras.requerimiento_detalle d
    JOIN compras.requerimientos_estados re
      ON re.id_requerimiento = d.id_requerimiento
     AND re.baja_fecha IS NULL
    WHERE d.id = p_id;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION 'No se encontro el detalle a borrar.';
    END IF;

    IF v_estado_actual <> 1 THEN
        RAISE EXCEPTION 'Solo se pueden borrar detalles de requerimientos en estado Borrador.';
    END IF;

    DELETE FROM compras.requerimiento_detalle
    WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el detalle a borrar.';
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.cambiar_estado_requerimiento(
    p_id_requerimiento INTEGER,
    p_id_estado INTEGER,
    p_usuario VARCHAR
) RETURNS VOID AS $$
DECLARE
    v_estado_actual INTEGER;
    v_usuario VARCHAR;
BEGIN
    v_usuario := trim(COALESCE(p_usuario, 'sistema'));

    IF NOT EXISTS (
        SELECT 1
        FROM compras.estados_requerimiento e
        WHERE e.id = p_id_estado
    ) THEN
        RAISE EXCEPTION 'Estado de requerimiento invalido.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM compras.requerimientos r
        WHERE r.id = p_id_requerimiento
          AND r.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION 'No se encontro el requerimiento.';
    END IF;

    SELECT re.id_estado
    INTO v_estado_actual
    FROM compras.requerimientos_estados re
    WHERE re.id_requerimiento = p_id_requerimiento
      AND re.baja_fecha IS NULL
    FOR UPDATE;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION 'El requerimiento no tiene estado activo.';
    END IF;

    IF v_estado_actual = p_id_estado THEN
        RETURN;
    END IF;

    IF v_estado_actual = 3 THEN
        RAISE EXCEPTION 'El requerimiento ya se encuentra anulado.';
    END IF;

    IF NOT (
        (v_estado_actual = 1 AND p_id_estado IN (2, 3))
        OR (v_estado_actual = 2 AND p_id_estado = 3)
    ) THEN
        RAISE EXCEPTION 'La transicion de estado solicitada no es valida.';
    END IF;

    UPDATE compras.requerimientos_estados
    SET baja_fecha = now(),
        baja_usr = v_usuario
    WHERE id_requerimiento = p_id_requerimiento
      AND baja_fecha IS NULL;

    INSERT INTO compras.requerimientos_estados (
        id_requerimiento,
        id_estado,
        alta_usr
    ) VALUES (
        p_id_requerimiento,
        p_id_estado,
        v_usuario
    );

    UPDATE compras.requerimientos
    SET modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id = p_id_requerimiento;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.borrar_requerimiento(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
) RETURNS VOID AS $$
BEGIN
    PERFORM compras.cambiar_estado_requerimiento(p_id_requerimiento, 3, p_usuario);
END;
$$ LANGUAGE plpgsql;

COMMIT;
