-- Baja RP -> baja logica del vinculo Compras. PostgreSQL 9.6.
-- Mantiene la funcion legacy autorizaciones.borra_reclamo_prestacional.
BEGIN;

ALTER TABLE compras.requerimiento_reclamo_prestacional
    ADD COLUMN IF NOT EXISTS baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS baja_usr VARCHAR(100);

-- Conservar la ausencia de CHECK en ambientes que no los tienen instalados.
DO $checks$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint
               WHERE conrelid = 'compras.requerimiento_reclamo_prestacional'::REGCLASS
                 AND conname = 'ck_compras_requerimiento_reclamo_estado') THEN
        ALTER TABLE compras.requerimiento_reclamo_prestacional
            DROP CONSTRAINT ck_compras_requerimiento_reclamo_estado;
        ALTER TABLE compras.requerimiento_reclamo_prestacional
            ADD CONSTRAINT ck_compras_requerimiento_reclamo_estado
            CHECK (estado IN ('RESERVADO', 'VINCULADO', 'ERROR', 'BAJA'));
    END IF;
    IF EXISTS (SELECT 1 FROM pg_constraint
               WHERE conrelid = 'compras.requerimiento_reclamo_prestacional'::REGCLASS
                 AND conname = 'ck_compras_requerimiento_reclamo_datos') THEN
        ALTER TABLE compras.requerimiento_reclamo_prestacional
            DROP CONSTRAINT ck_compras_requerimiento_reclamo_datos;
        ALTER TABLE compras.requerimiento_reclamo_prestacional
            ADD CONSTRAINT ck_compras_requerimiento_reclamo_datos CHECK (
                (estado = 'RESERVADO' AND id_reclamo_prestacional IS NULL
                    AND NULLIF(btrim(token_reserva), '') IS NOT NULL)
                OR (estado IN ('VINCULADO', 'ERROR') AND id_reclamo_prestacional IS NOT NULL)
                OR (estado = 'BAJA' AND id_reclamo_prestacional IS NOT NULL AND baja_fecha IS NOT NULL)
            );
    END IF;
END;
$checks$;

CREATE OR REPLACE FUNCTION compras.baja_vinculo_reclamo_prestacional(
    p_id_reclamo_prestacional INTEGER,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_baja_fecha TIMESTAMP WITHOUT TIME ZONE;
    v_usuario VARCHAR(100);
    v_filas INTEGER;
BEGIN
    SELECT rp.baja_fecha INTO v_baja_fecha
      FROM autorizaciones.reclamos_prestacionales rp
     WHERE rp.id = p_id_reclamo_prestacional
     FOR UPDATE;
    IF NOT FOUND OR v_baja_fecha IS NULL THEN
        RETURN FALSE;
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);
    UPDATE compras.requerimiento_reclamo_prestacional
       SET estado = 'BAJA',
           baja_fecha = v_baja_fecha,
           baja_usr = v_usuario,
           token_reserva = NULL,
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_reclamo_prestacional = p_id_reclamo_prestacional
       AND estado IN ('VINCULADO', 'ERROR');
    GET DIAGNOSTICS v_filas = ROW_COUNT;
    RETURN v_filas > 0;
END;
$func$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION compras.baja_vinculo_reclamo_prestacional_trigger()
RETURNS TRIGGER
AS $func$
BEGIN
    PERFORM compras.baja_vinculo_reclamo_prestacional(NEW.id, NEW.baja_usr);
    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_compras_baja_vinculo_reclamo
    ON autorizaciones.reclamos_prestacionales;
CREATE TRIGGER trg_compras_baja_vinculo_reclamo
    AFTER UPDATE
    ON autorizaciones.reclamos_prestacionales
    FOR EACH ROW WHEN (NEW.baja_fecha IS NOT NULL)
    EXECUTE PROCEDURE compras.baja_vinculo_reclamo_prestacional_trigger();

CREATE OR REPLACE FUNCTION compras.puede_anular_requerimiento(
    p_id_requerimiento INTEGER
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_estado INTEGER;
    v_id_reclamo INTEGER;
BEGIN
    SELECT r.estado INTO v_estado
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL;
    IF NOT FOUND OR v_estado NOT IN (1, 2, 3, 4) THEN
        RETURN FALSE;
    END IF;

    -- Mismo orden de bloqueo que la baja RP: RP antes del vinculo.
    FOR v_id_reclamo IN
        SELECT rp.id FROM autorizaciones.reclamos_prestacionales rp
        JOIN compras.requerimiento_reclamo_prestacional rr
          ON rr.id_reclamo_prestacional = rp.id
        WHERE rr.id_requerimiento = p_id_requerimiento
        ORDER BY rp.id FOR UPDATE OF rp
    LOOP
        NULL;
    END LOOP;

    IF v_estado IN (1, 2, 3) THEN
        RETURN NOT EXISTS (
            SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
            WHERE rr.id_requerimiento = p_id_requerimiento
              AND (rr.estado <> 'BAJA' OR rr.baja_fecha IS NULL OR NOT EXISTS (
                  SELECT 1 FROM autorizaciones.reclamos_prestacionales rp
                  WHERE rp.id = rr.id_reclamo_prestacional
                    AND rp.baja_fecha IS NOT NULL
              ))
        );
    END IF;

    RETURN EXISTS (
        SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
        JOIN autorizaciones.reclamos_prestacionales rp
          ON rp.id = rr.id_reclamo_prestacional
        WHERE rr.id_requerimiento = p_id_requerimiento
          AND rr.estado = 'BAJA' AND rr.baja_fecha IS NOT NULL
          AND rp.baja_fecha IS NOT NULL
    );
END;
$func$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION compras.anular_requerimiento(
    p_id_requerimiento INTEGER,
    p_motivo_baja TEXT,
    p_usuario VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_estado_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(p_usuario);
    PERFORM compras.bloquear_requerimiento_reclamo_prestacional(p_id_requerimiento);
    SELECT r.estado INTO v_estado_actual FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento activo.';
    END IF;
    IF NOT compras.puede_anular_requerimiento(p_id_requerimiento) THEN
        RAISE EXCEPTION 'El requerimiento no puede anularse: verifique su estado y la baja del RP asociado.';
    END IF;
    UPDATE compras.requerimiento
       SET estado = 99,
           motivo_baja = COALESCE(p_motivo_baja, ''),
           modi_fecha = now(), modi_usr = v_usuario,
           baja_fecha = now(), baja_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual AND baja_fecha IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.listar_relaciones_reclamo_prestacional_batch(
    p_estado VARCHAR,
    p_ids_requerimientos TEXT
)
RETURNS SETOF compras.requerimiento_reclamo_prestacional
AS $func$
    SELECT relacion.* FROM compras.requerimiento_reclamo_prestacional relacion
    WHERE relacion.id_reclamo_prestacional IS NOT NULL
      AND relacion.id_requerimiento = ANY ($2::INTEGER[])
      AND (($1 IS NOT NULL AND relacion.estado = $1)
        OR ($1 IS NULL AND (relacion.estado = 'VINCULADO'
          OR (relacion.estado = 'BAJA' AND relacion.baja_fecha IS NOT NULL
            AND EXISTS (SELECT 1 FROM autorizaciones.reclamos_prestacionales rp
                        WHERE rp.id = relacion.id_reclamo_prestacional
                          AND rp.baja_fecha IS NOT NULL)))))
    ORDER BY relacion.id_requerimiento;
$func$
LANGUAGE sql STABLE;

DO $adaptar_validacion$
DECLARE
    v_oid OID;
    v_def TEXT;
    v_viejo TEXT;
    v_nuevo TEXT;
    v_inicio INTEGER;
BEGIN
    v_oid := to_regprocedure('compras.validar_requerimiento_fila()');
    IF v_oid IS NULL THEN RETURN; END IF;
    v_def := pg_get_functiondef(v_oid);
    IF position('v_fila_anulacion compras.requerimiento%ROWTYPE;' IN v_def) > 0 THEN
        RETURN;
    END IF;
    v_viejo := '    v_usuario VARCHAR(100);';
    v_nuevo := E'    v_usuario VARCHAR(100);\n    v_fila_anulacion compras.requerimiento%ROWTYPE;';
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de validar_requerimiento_fila. No se modifico su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);
    v_inicio := position(E'BEGIN\n' IN v_def);
    IF v_inicio = 0 THEN
        RAISE EXCEPTION 'No se encontro el bloque de validar_requerimiento_fila.';
    END IF;
    v_nuevo := $guard$
    -- Anulacion acotada: preserva cabecera y comprueba el RP bajo bloqueo.
    IF TG_OP = 'UPDATE' AND NEW.estado = 99 AND OLD.estado IN (1, 2, 3, 4) THEN
        IF NOT compras.puede_anular_requerimiento(OLD.id_requerimiento) THEN
            RAISE EXCEPTION 'El requerimiento no puede anularse desde su estado o vinculo actual.';
        END IF;
        v_fila_anulacion := NEW;
        v_fila_anulacion.estado := OLD.estado;
        v_fila_anulacion.motivo_baja := OLD.motivo_baja;
        v_fila_anulacion.modi_fecha := OLD.modi_fecha;
        v_fila_anulacion.modi_usr := OLD.modi_usr;
        v_fila_anulacion.baja_fecha := OLD.baja_fecha;
        v_fila_anulacion.baja_usr := OLD.baja_usr;
        IF v_fila_anulacion IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION 'La anulacion debe conservar los datos del requerimiento.';
        END IF;
        NEW.baja_fecha := COALESCE(NEW.baja_fecha, now());
        NEW.baja_usr := compras.normalizar_usuario(COALESCE(NEW.baja_usr, NEW.modi_usr));
        RETURN NEW;
    END IF;
$guard$;
    v_def := overlay(v_def placing v_nuevo from v_inicio + length(E'BEGIN\n') for 0);
    EXECUTE v_def;
END;
$adaptar_validacion$;

-- Alinear el bloqueo de autorizacion con la baja concurrente del RP.
DO $orden_autorizacion$
DECLARE v_oid OID; v_def TEXT; v_viejo TEXT; v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)');
    IF v_oid IS NULL THEN RETURN; END IF;
    v_def := pg_get_functiondef(v_oid);
    IF position('Orden compartido con la baja RP' IN v_def) > 0 THEN RETURN; END IF;
    v_viejo := E'    SELECT rr.id_reclamo_prestacional\n      INTO v_id_reclamo';
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de cambiar_estado_autorizacion_requerimiento.';
    END IF;
    v_nuevo := $bloqueo$
    -- Orden compartido con la baja RP: primero RP, luego vinculo.
    PERFORM rp.id FROM autorizaciones.reclamos_prestacionales rp
    JOIN compras.requerimiento_reclamo_prestacional rr
      ON rr.id_reclamo_prestacional = rp.id
    WHERE rr.id_requerimiento = p_id_requerimiento
    FOR UPDATE OF rp;

$bloqueo$ || v_viejo;
    EXECUTE replace(v_def, v_viejo, v_nuevo);
END;
$orden_autorizacion$;

-- Reconciliar asociaciones cuyos RP ya estaban dados de baja.
DO $reconciliar$
DECLARE v_rp RECORD;
BEGIN
    FOR v_rp IN
        SELECT DISTINCT rp.id, rp.baja_usr
          FROM autorizaciones.reclamos_prestacionales rp
          JOIN compras.requerimiento_reclamo_prestacional rr
            ON rr.id_reclamo_prestacional = rp.id
         WHERE rp.baja_fecha IS NOT NULL AND rr.estado IN ('VINCULADO', 'ERROR')
    LOOP
        PERFORM compras.baja_vinculo_reclamo_prestacional(v_rp.id, v_rp.baja_usr);
    END LOOP;
END;
$reconciliar$;

COMMIT;
