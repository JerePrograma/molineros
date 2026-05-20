-- ============================================================
-- MODULO: Requerimientos de Compras
-- Base pensada para PostgreSQL 9.6+ y uso desde JDBC CallableStatement
-- ============================================================

BEGIN;

-- ============================================================
-- 1) Tablas
-- ============================================================

CREATE TABLE IF NOT EXISTS requerimiento_compra (
    id_requerimiento_compra SERIAL PRIMARY KEY,
    numero INTEGER NOT NULL UNIQUE,
    sector_id INTEGER NULL,
    solicitante_usr VARCHAR(75) NOT NULL,
    entidad VARCHAR(75) NULL,
    prioridad INTEGER NOT NULL DEFAULT 2,
    estado INTEGER NOT NULL DEFAULT 1,
    fecha_alta TIMESTAMP NOT NULL DEFAULT now(),
    alta_usr VARCHAR(75) NOT NULL,
    fecha_modi TIMESTAMP NULL,
    modi_usr VARCHAR(75) NULL,
    baja_fecha TIMESTAMP NULL,
    baja_usr VARCHAR(75) NULL,
    fecha_necesidad DATE NULL,
    motivo VARCHAR(255) NOT NULL,
    observaciones TEXT NULL,
    importe_estimado_total NUMERIC(14,2) NOT NULL DEFAULT 0,
    id_orden_compra INTEGER NULL
);

CREATE TABLE IF NOT EXISTS requerimiento_compra_item (
    id_item SERIAL PRIMARY KEY,
    id_requerimiento_compra INTEGER NOT NULL REFERENCES requerimiento_compra(id_requerimiento_compra),
    descripcion VARCHAR(255) NOT NULL,
    cantidad NUMERIC(14,2) NOT NULL DEFAULT 1,
    unidad_medida VARCHAR(30) NULL,
    importe_estimado NUMERIC(14,2) NOT NULL DEFAULT 0,
    observaciones TEXT NULL,
    estado INTEGER NOT NULL DEFAULT 1,
    baja_fecha TIMESTAMP NULL,
    baja_usr VARCHAR(75) NULL
);

CREATE TABLE IF NOT EXISTS requerimiento_compra_historial (
    id_historial SERIAL PRIMARY KEY,
    id_requerimiento_compra INTEGER NOT NULL REFERENCES requerimiento_compra(id_requerimiento_compra),
    estado_anterior INTEGER NULL,
    estado_nuevo INTEGER NOT NULL,
    usuario VARCHAR(75) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    comentario TEXT NULL
);

CREATE TABLE IF NOT EXISTS requerimiento_compra_adjunto (
    id_adjunto SERIAL PRIMARY KEY,
    id_requerimiento_compra INTEGER NOT NULL REFERENCES requerimiento_compra(id_requerimiento_compra),
    file_entry_id BIGINT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_archivo VARCHAR(100) NULL,
    alta_usr VARCHAR(75) NOT NULL,
    alta_fecha TIMESTAMP NOT NULL DEFAULT now(),
    baja_fecha TIMESTAMP NULL,
    baja_usr VARCHAR(75) NULL
);

CREATE INDEX IF NOT EXISTS idx_req_compra_estado ON requerimiento_compra(estado);
CREATE INDEX IF NOT EXISTS idx_req_compra_fecha_alta ON requerimiento_compra(fecha_alta);
CREATE INDEX IF NOT EXISTS idx_req_compra_sector ON requerimiento_compra(sector_id);
CREATE INDEX IF NOT EXISTS idx_req_compra_solicitante ON requerimiento_compra(solicitante_usr);
CREATE INDEX IF NOT EXISTS idx_req_compra_oc ON requerimiento_compra(id_orden_compra);
CREATE INDEX IF NOT EXISTS idx_req_compra_item_req ON requerimiento_compra_item(id_requerimiento_compra);
CREATE INDEX IF NOT EXISTS idx_req_compra_hist_req ON requerimiento_compra_historial(id_requerimiento_compra);
CREATE INDEX IF NOT EXISTS idx_req_compra_adj_req ON requerimiento_compra_adjunto(id_requerimiento_compra);

-- ============================================================
-- 2) Numerador simple
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS requerimiento_compra_numero_seq START 1;

-- ============================================================
-- 3) Funciones de búsqueda / lectura
-- ============================================================

CREATE OR REPLACE FUNCTION buscar_requerimientos_compra(
    p_numero INTEGER,
    p_fecha_desde DATE,
    p_fecha_hasta DATE,
    p_sector_id INTEGER,
    p_solicitante_usr VARCHAR,
    p_entidad VARCHAR,
    p_prioridad INTEGER,
    p_estado INTEGER,
    p_texto VARCHAR
)
RETURNS TABLE (
    id_requerimiento_compra INTEGER,
    numero INTEGER,
    sector_id INTEGER,
    sector_descripcion VARCHAR,
    solicitante_usr VARCHAR,
    entidad VARCHAR,
    prioridad INTEGER,
    estado INTEGER,
    fecha_alta TIMESTAMP,
    alta_usr VARCHAR,
    fecha_modi TIMESTAMP,
    modi_usr VARCHAR,
    baja_fecha TIMESTAMP,
    baja_usr VARCHAR,
    fecha_necesidad DATE,
    motivo VARCHAR,
    observaciones TEXT,
    importe_estimado_total NUMERIC,
    id_orden_compra INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.id_requerimiento_compra,
        r.numero,
        r.sector_id,
        NULL::VARCHAR AS sector_descripcion,
        r.solicitante_usr,
        r.entidad,
        r.prioridad,
        r.estado,
        r.fecha_alta,
        r.alta_usr,
        r.fecha_modi,
        r.modi_usr,
        r.baja_fecha,
        r.baja_usr,
        r.fecha_necesidad,
        r.motivo,
        r.observaciones,
        r.importe_estimado_total,
        r.id_orden_compra
    FROM requerimiento_compra r
    WHERE r.baja_fecha IS NULL
      AND (p_numero IS NULL OR r.numero = p_numero)
      AND (p_fecha_desde IS NULL OR r.fecha_alta::DATE >= p_fecha_desde)
      AND (p_fecha_hasta IS NULL OR r.fecha_alta::DATE <= p_fecha_hasta)
      AND (p_sector_id IS NULL OR r.sector_id = p_sector_id)
      AND (p_solicitante_usr IS NULL OR lower(r.solicitante_usr) LIKE '%' || lower(p_solicitante_usr) || '%')
      AND (p_entidad IS NULL OR lower(r.entidad) LIKE '%' || lower(p_entidad) || '%')
      AND (p_prioridad IS NULL OR r.prioridad = p_prioridad)
      AND (p_estado IS NULL OR r.estado = p_estado)
      AND (
            p_texto IS NULL
            OR lower(r.motivo) LIKE '%' || lower(p_texto) || '%'
            OR lower(coalesce(r.observaciones, '')) LIKE '%' || lower(p_texto) || '%'
      )
    ORDER BY r.fecha_alta DESC, r.numero DESC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_requerimiento_compra(p_id_requerimiento_compra INTEGER)
RETURNS TABLE (
    id_requerimiento_compra INTEGER,
    numero INTEGER,
    sector_id INTEGER,
    sector_descripcion VARCHAR,
    solicitante_usr VARCHAR,
    entidad VARCHAR,
    prioridad INTEGER,
    estado INTEGER,
    fecha_alta TIMESTAMP,
    alta_usr VARCHAR,
    fecha_modi TIMESTAMP,
    modi_usr VARCHAR,
    baja_fecha TIMESTAMP,
    baja_usr VARCHAR,
    fecha_necesidad DATE,
    motivo VARCHAR,
    observaciones TEXT,
    importe_estimado_total NUMERIC,
    id_orden_compra INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.id_requerimiento_compra,
        r.numero,
        r.sector_id,
        NULL::VARCHAR AS sector_descripcion,
        r.solicitante_usr,
        r.entidad,
        r.prioridad,
        r.estado,
        r.fecha_alta,
        r.alta_usr,
        r.fecha_modi,
        r.modi_usr,
        r.baja_fecha,
        r.baja_usr,
        r.fecha_necesidad,
        r.motivo,
        r.observaciones,
        r.importe_estimado_total,
        r.id_orden_compra
    FROM requerimiento_compra r
    WHERE r.id_requerimiento_compra = p_id_requerimiento_compra;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_requerimiento_compra_items(p_id_requerimiento_compra INTEGER)
RETURNS TABLE (
    id_item INTEGER,
    id_requerimiento_compra INTEGER,
    descripcion VARCHAR,
    cantidad NUMERIC,
    unidad_medida VARCHAR,
    importe_estimado NUMERIC,
    observaciones TEXT,
    estado INTEGER,
    baja_fecha TIMESTAMP,
    baja_usr VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        i.id_item,
        i.id_requerimiento_compra,
        i.descripcion,
        i.cantidad,
        i.unidad_medida,
        i.importe_estimado,
        i.observaciones,
        i.estado,
        i.baja_fecha,
        i.baja_usr
    FROM requerimiento_compra_item i
    WHERE i.id_requerimiento_compra = p_id_requerimiento_compra
      AND i.baja_fecha IS NULL
    ORDER BY i.id_item;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_requerimiento_compra_historial(p_id_requerimiento_compra INTEGER)
RETURNS TABLE (
    id_historial INTEGER,
    id_requerimiento_compra INTEGER,
    estado_anterior INTEGER,
    estado_nuevo INTEGER,
    usuario VARCHAR,
    fecha TIMESTAMP,
    comentario TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        h.id_historial,
        h.id_requerimiento_compra,
        h.estado_anterior,
        h.estado_nuevo,
        h.usuario,
        h.fecha,
        h.comentario
    FROM requerimiento_compra_historial h
    WHERE h.id_requerimiento_compra = p_id_requerimiento_compra
    ORDER BY h.fecha DESC, h.id_historial DESC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_requerimiento_compra_adjuntos(p_id_requerimiento_compra INTEGER)
RETURNS TABLE (
    id_adjunto INTEGER,
    id_requerimiento_compra INTEGER,
    file_entry_id BIGINT,
    nombre_archivo VARCHAR,
    tipo_archivo VARCHAR,
    alta_usr VARCHAR,
    alta_fecha TIMESTAMP,
    baja_fecha TIMESTAMP,
    baja_usr VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        a.id_adjunto,
        a.id_requerimiento_compra,
        a.file_entry_id,
        a.nombre_archivo,
        a.tipo_archivo,
        a.alta_usr,
        a.alta_fecha,
        a.baja_fecha,
        a.baja_usr
    FROM requerimiento_compra_adjunto a
    WHERE a.id_requerimiento_compra = p_id_requerimiento_compra
      AND a.baja_fecha IS NULL
    ORDER BY a.alta_fecha DESC, a.id_adjunto DESC;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- 4) ABM
-- ============================================================

CREATE OR REPLACE FUNCTION guardar_requerimiento_compra(
    p_id_requerimiento_compra INTEGER,
    p_sector_id INTEGER,
    p_solicitante_usr VARCHAR,
    p_entidad VARCHAR,
    p_prioridad INTEGER,
    p_fecha_necesidad DATE,
    p_motivo VARCHAR,
    p_observaciones TEXT,
    p_importe_estimado_total NUMERIC,
    p_id_orden_compra INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER AS $$
DECLARE
    v_id INTEGER;
    v_numero INTEGER;
BEGIN
    IF p_id_requerimiento_compra IS NULL OR p_id_requerimiento_compra = 0 THEN
        v_numero := nextval('requerimiento_compra_numero_seq');

        INSERT INTO requerimiento_compra (
            numero,
            sector_id,
            solicitante_usr,
            entidad,
            prioridad,
            estado,
            fecha_alta,
            alta_usr,
            fecha_necesidad,
            motivo,
            observaciones,
            importe_estimado_total,
            id_orden_compra
        ) VALUES (
            v_numero,
            p_sector_id,
            p_solicitante_usr,
            p_entidad,
            coalesce(p_prioridad, 2),
            1,
            now(),
            p_usuario,
            p_fecha_necesidad,
            p_motivo,
            p_observaciones,
            coalesce(p_importe_estimado_total, 0),
            p_id_orden_compra
        )
        RETURNING id_requerimiento_compra INTO v_id;

        INSERT INTO requerimiento_compra_historial (
            id_requerimiento_compra,
            estado_anterior,
            estado_nuevo,
            usuario,
            fecha,
            comentario
        ) VALUES (
            v_id,
            NULL,
            1,
            p_usuario,
            now(),
            'Alta de requerimiento'
        );

        RETURN v_id;
    END IF;

    UPDATE requerimiento_compra
       SET sector_id = p_sector_id,
           solicitante_usr = p_solicitante_usr,
           entidad = p_entidad,
           prioridad = coalesce(p_prioridad, prioridad),
           fecha_necesidad = p_fecha_necesidad,
           motivo = p_motivo,
           observaciones = p_observaciones,
           importe_estimado_total = coalesce(p_importe_estimado_total, 0),
           id_orden_compra = p_id_orden_compra,
           fecha_modi = now(),
           modi_usr = p_usuario
     WHERE id_requerimiento_compra = p_id_requerimiento_compra;

    RETURN p_id_requerimiento_compra;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION guardar_requerimiento_compra_item(
    p_id_item INTEGER,
    p_id_requerimiento_compra INTEGER,
    p_descripcion VARCHAR,
    p_cantidad NUMERIC,
    p_unidad_medida VARCHAR,
    p_importe_estimado NUMERIC,
    p_observaciones TEXT
)
RETURNS INTEGER AS $$
DECLARE
    v_id INTEGER;
BEGIN
    IF p_id_item IS NULL OR p_id_item = 0 THEN
        INSERT INTO requerimiento_compra_item (
            id_requerimiento_compra,
            descripcion,
            cantidad,
            unidad_medida,
            importe_estimado,
            observaciones,
            estado
        ) VALUES (
            p_id_requerimiento_compra,
            p_descripcion,
            coalesce(p_cantidad, 1),
            p_unidad_medida,
            coalesce(p_importe_estimado, 0),
            p_observaciones,
            1
        )
        RETURNING id_item INTO v_id;

        RETURN v_id;
    END IF;

    UPDATE requerimiento_compra_item
       SET descripcion = p_descripcion,
           cantidad = coalesce(p_cantidad, cantidad),
           unidad_medida = p_unidad_medida,
           importe_estimado = coalesce(p_importe_estimado, importe_estimado),
           observaciones = p_observaciones
     WHERE id_item = p_id_item;

    RETURN p_id_item;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION borrar_requerimiento_compra_item(
    p_id_item INTEGER,
    p_usuario VARCHAR
)
RETURNS VOID AS $$
BEGIN
    UPDATE requerimiento_compra_item
       SET baja_fecha = now(),
           baja_usr = p_usuario,
           estado = 0
     WHERE id_item = p_id_item;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION borrar_requerimiento_compra(
    p_id_requerimiento_compra INTEGER,
    p_usuario VARCHAR
)
RETURNS VOID AS $$
DECLARE
    v_estado_anterior INTEGER;
BEGIN
    SELECT estado INTO v_estado_anterior
      FROM requerimiento_compra
     WHERE id_requerimiento_compra = p_id_requerimiento_compra;

    UPDATE requerimiento_compra
       SET baja_fecha = now(),
           baja_usr = p_usuario,
           estado = 8
     WHERE id_requerimiento_compra = p_id_requerimiento_compra;

    INSERT INTO requerimiento_compra_historial (
        id_requerimiento_compra,
        estado_anterior,
        estado_nuevo,
        usuario,
        fecha,
        comentario
    ) VALUES (
        p_id_requerimiento_compra,
        v_estado_anterior,
        8,
        p_usuario,
        now(),
        'Anulación/Baja de requerimiento'
    );
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION cambiar_estado_requerimiento_compra(
    p_id_requerimiento_compra INTEGER,
    p_estado_nuevo INTEGER,
    p_comentario TEXT,
    p_usuario VARCHAR
)
RETURNS VOID AS $$
DECLARE
    v_estado_anterior INTEGER;
BEGIN
    SELECT estado INTO v_estado_anterior
      FROM requerimiento_compra
     WHERE id_requerimiento_compra = p_id_requerimiento_compra;

    UPDATE requerimiento_compra
       SET estado = p_estado_nuevo,
           fecha_modi = now(),
           modi_usr = p_usuario
     WHERE id_requerimiento_compra = p_id_requerimiento_compra;

    INSERT INTO requerimiento_compra_historial (
        id_requerimiento_compra,
        estado_anterior,
        estado_nuevo,
        usuario,
        fecha,
        comentario
    ) VALUES (
        p_id_requerimiento_compra,
        v_estado_anterior,
        p_estado_nuevo,
        p_usuario,
        now(),
        p_comentario
    );
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION guardar_requerimiento_compra_adjunto(
    p_id_requerimiento_compra INTEGER,
    p_file_entry_id BIGINT,
    p_nombre_archivo VARCHAR,
    p_tipo_archivo VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER AS $$
DECLARE
    v_id INTEGER;
BEGIN
    INSERT INTO requerimiento_compra_adjunto (
        id_requerimiento_compra,
        file_entry_id,
        nombre_archivo,
        tipo_archivo,
        alta_usr,
        alta_fecha
    ) VALUES (
        p_id_requerimiento_compra,
        p_file_entry_id,
        p_nombre_archivo,
        p_tipo_archivo,
        p_usuario,
        now()
    )
    RETURNING id_adjunto INTO v_id;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION borrar_requerimiento_compra_adjunto(
    p_id_adjunto INTEGER,
    p_usuario VARCHAR
)
RETURNS VOID AS $$
BEGIN
    UPDATE requerimiento_compra_adjunto
       SET baja_fecha = now(),
           baja_usr = p_usuario
     WHERE id_adjunto = p_id_adjunto;
END;
$$ LANGUAGE plpgsql;

COMMIT;


