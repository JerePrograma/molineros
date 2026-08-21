BEGIN;

/* ============================================================
 * PUNTO 10 - TIPO FUNCIONAL POR DETALLE
 * ============================================================ */

CREATE TABLE compras.tipo_prestacion (
    id_tipo_prestacion SMALLINT PRIMARY KEY,
    descripcion VARCHAR(120) NOT NULL,
    id_sector INTEGER NOT NULL
        REFERENCES compras.sector_requerimiento (id_sector),

    CONSTRAINT ck_compras_tipo_prestacion_id
        CHECK (id_tipo_prestacion BETWEEN 1 AND 7),

    CONSTRAINT ck_compras_tipo_prestacion_descripcion
        CHECK (NULLIF(btrim(descripcion), '') IS NOT NULL),

    CONSTRAINT uq_compras_tipo_prestacion_descripcion
        UNIQUE (descripcion)
);

WITH tipos (
    id_tipo_prestacion,
    descripcion,
    sector_normalizado
) AS (
    VALUES
        (1, 'Alimentación', 'FARMACIA'),
        (2, 'Medicamentos', 'FARMACIA'),
        (3, 'Prótesis Traumatología', 'PRESTACIONES MEDICAS'),
        (4, 'Prótesis Cardiología', 'PRESTACIONES MEDICAS'),
        (5, 'Prótesis General', 'PRESTACIONES MEDICAS'),
        (6, 'Insumos', 'PRESTACIONES MEDICAS'),
        (7, 'Pañales', 'PRESTACIONES MEDICAS')
)
INSERT INTO compras.tipo_prestacion (
    id_tipo_prestacion,
    descripcion,
    id_sector
)
SELECT
    t.id_tipo_prestacion,
    t.descripcion,
    s.id_sector
FROM tipos t
JOIN compras.sector_requerimiento s
  ON translate(
         upper(btrim(s.descripcion)),
         'ÁÉÍÓÚÜáéíóúü',
         'AEIOUUAEIOUU'
     ) = t.sector_normalizado
 AND s.activo = TRUE
 AND s.baja_fecha IS NULL;

DO $bloque$
BEGIN
    IF (SELECT count(*) FROM compras.tipo_prestacion) <> 7 THEN
        RAISE EXCEPTION
            'No se pudieron asociar los siete tipos de prestacion a sus sectores.';
    END IF;
END;
$bloque$;

ALTER TABLE compras.requerimiento_detalle
    ADD COLUMN id_tipo_prestacion SMALLINT;

ALTER TABLE compras.requerimiento_detalle
    ADD CONSTRAINT fk_compras_detalle_tipo_prestacion
        FOREIGN KEY (id_tipo_prestacion)
        REFERENCES compras.tipo_prestacion (id_tipo_prestacion);

CREATE INDEX ix_compras_detalle_tipo_prestacion
    ON compras.requerimiento_detalle (id_tipo_prestacion)
    WHERE baja_fecha IS NULL;

CREATE FUNCTION compras.listar_tipos_prestacion()
RETURNS TABLE (
    id_tipo_prestacion INTEGER,
    descripcion VARCHAR,
    id_sector INTEGER,
    sector_descripcion VARCHAR
)
AS $func$
    SELECT
        t.id_tipo_prestacion::INTEGER,
        t.descripcion,
        t.id_sector,
        s.descripcion
    FROM compras.tipo_prestacion t
    JOIN compras.sector_requerimiento s
      ON s.id_sector = t.id_sector
    WHERE s.activo = TRUE
      AND s.baja_fecha IS NULL
    ORDER BY t.id_tipo_prestacion;
$func$
LANGUAGE sql
STABLE;

CREATE FUNCTION compras.validar_tipo_prestacion_detalle_fila()
RETURNS TRIGGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_id_sector_requerimiento INTEGER;
    v_id_sector_tipo INTEGER;
BEGIN
    SELECT r.estado, r.id_sector
    INTO v_estado, v_id_sector_requerimiento
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = NEW.id_requerimiento
      AND r.baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo del detalle.';
    END IF;

    IF TG_OP = 'UPDATE'
       AND NEW.id_tipo_prestacion
            IS DISTINCT FROM OLD.id_tipo_prestacion THEN

        IF v_estado <> 1 THEN
            RAISE EXCEPTION
                'El tipo de prestacion solo puede modificarse en estado PENDIENTE.';
        END IF;

        IF OLD.id_tipo_prestacion IS NOT NULL
           AND NEW.id_tipo_prestacion IS NULL THEN

            RAISE EXCEPTION
                'El tipo de prestacion ya informado no puede quitarse.';
        END IF;
    END IF;

    IF NEW.id_tipo_prestacion IS NULL THEN
        RETURN NEW;
    END IF;

    SELECT t.id_sector
    INTO v_id_sector_tipo
    FROM compras.tipo_prestacion t
    WHERE t.id_tipo_prestacion = NEW.id_tipo_prestacion;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El tipo de prestacion informado no existe.';
    END IF;

    IF v_id_sector_tipo <> v_id_sector_requerimiento THEN
        RAISE EXCEPTION
            'El tipo de prestacion no corresponde al sector del requerimiento.';
    END IF;

    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

CREATE TRIGGER tr_compras_detalle_tipo_prestacion
    BEFORE INSERT OR UPDATE OF id_tipo_prestacion, id_requerimiento
    ON compras.requerimiento_detalle
    FOR EACH ROW
    EXECUTE PROCEDURE compras.validar_tipo_prestacion_detalle_fila();

/*
 * El guardado clasificado invoca primero la función canónica existente.
 * La comprobación diferida permite que esa función inserte la fila y que el
 * wrapper asigne el tipo dentro de la misma sentencia/transacción. Un caller
 * que intente insertar directamente un detalle nuevo sin tipo queda bloqueado.
 */
CREATE FUNCTION compras.validar_tipo_prestacion_detalle_nuevo()
RETURNS TRIGGER
AS $func$
DECLARE
    v_id_tipo_prestacion SMALLINT;
    v_cantidad_tipos_sector INTEGER;
BEGIN
    SELECT
        d.id_tipo_prestacion,
        (
            SELECT count(*)
            FROM compras.tipo_prestacion t
            WHERE t.id_sector = r.id_sector
        )
    INTO
        v_id_tipo_prestacion,
        v_cantidad_tipos_sector
    FROM compras.requerimiento_detalle d
    JOIN compras.requerimiento r
      ON r.id_requerimiento = d.id_requerimiento
    WHERE d.id_detalle = NEW.id_detalle
      AND d.baja_fecha IS NULL
      AND r.baja_fecha IS NULL;

    IF FOUND
       AND v_cantidad_tipos_sector > 0
       AND v_id_tipo_prestacion IS NULL THEN

        RAISE EXCEPTION
            'Debe seleccionar el tipo de prestacion.';
    END IF;

    RETURN NULL;
END;
$func$
LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER tr_compras_detalle_tipo_prestacion_nuevo
    AFTER INSERT
    ON compras.requerimiento_detalle
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW
    EXECUTE PROCEDURE compras.validar_tipo_prestacion_detalle_nuevo();

CREATE FUNCTION compras.guardar_requerimiento_detalle_clasificado(
    p_id INTEGER,
    p_id_requerimiento INTEGER,
    p_tipo_item VARCHAR,
    p_id_prestacion INTEGER,
    p_id_tipo_nomenclador INTEGER,
    p_codigo_nomenclador VARCHAR,
    p_descripcion_nomenclador VARCHAR,
    p_id_medicamento INTEGER,
    p_troquel INTEGER,
    p_nombre_medicamento VARCHAR,
    p_cantidad INTEGER,
    p_observaciones TEXT,
    p_id_tipo_prestacion INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id_detalle INTEGER;
BEGIN
    v_id_detalle := compras.guardar_requerimiento_detalle(
        p_id,
        p_id_requerimiento,
        p_tipo_item,
        p_id_prestacion,
        p_id_tipo_nomenclador,
        p_codigo_nomenclador,
        p_descripcion_nomenclador,
        p_id_medicamento,
        p_troquel,
        p_nombre_medicamento,
        p_cantidad,
        p_observaciones,
        p_usuario
    );

    UPDATE compras.requerimiento_detalle
    SET id_tipo_prestacion = p_id_tipo_prestacion
    WHERE id_detalle = v_id_detalle
      AND id_requerimiento = p_id_requerimiento
      AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se pudo asociar el tipo al detalle del requerimiento.';
    END IF;

    RETURN v_id_detalle;
END;
$func$
LANGUAGE plpgsql;

CREATE FUNCTION compras.get_requerimiento_detalle_clasificado(
    p_id_requerimiento INTEGER
)
RETURNS TABLE (
    id INTEGER,
    id_requerimiento INTEGER,
    tipo_item VARCHAR,
    id_tipo_prestacion INTEGER,
    tipo_prestacion VARCHAR,
    codigo_item VARCHAR,
    descripcion_item VARCHAR,
    id_prestacion INTEGER,
    id_tipo_nomenclador INTEGER,
    codigo_nomenclador VARCHAR,
    descripcion_nomenclador VARCHAR,
    id_medicamento INTEGER,
    troquel INTEGER,
    nombre_medicamento VARCHAR,
    cantidad INTEGER,
    precio_unitario_estimado NUMERIC,
    precio_total_estimado NUMERIC,
    id_prestador INTEGER,
    prestador_cuit VARCHAR,
    prestador_razon_social VARCHAR,
    observaciones TEXT
)
AS $func$
BEGIN
    RETURN QUERY
    SELECT
        d.id_detalle,
        d.id_requerimiento,
        d.tipo_item::VARCHAR,
        d.id_tipo_prestacion::INTEGER,
        t.descripcion::VARCHAR,
        CASE
            WHEN d.tipo_item = 'MEDICAMENTO'
                THEN COALESCE(d.troquel::VARCHAR, d.id_medicamento::VARCHAR)
            ELSE d.codigo_nomenclador
        END::VARCHAR,
        CASE
            WHEN d.tipo_item = 'MEDICAMENTO'
                THEN d.nombre_medicamento
            ELSE d.descripcion_nomenclador
        END::VARCHAR,
        d.id_prestacion,
        d.id_tipo_nomenclador,
        d.codigo_nomenclador,
        d.descripcion_nomenclador,
        d.id_medicamento,
        d.troquel,
        d.nombre_medicamento,
        d.cantidad,
        d.precio_unitario_estimado,
        d.precio_total_estimado,
        d.id_prestador,
        p.cuit::VARCHAR,
        p.descripcion::VARCHAR,
        d.observaciones
    FROM compras.requerimiento_detalle d
    LEFT JOIN compras.tipo_prestacion t
      ON t.id_tipo_prestacion = d.id_tipo_prestacion
    LEFT JOIN public.prestador p
      ON p.id_prestador = d.id_prestador
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL
    ORDER BY d.id_detalle;
END;
$func$
LANGUAGE plpgsql
STABLE;

COMMIT;
