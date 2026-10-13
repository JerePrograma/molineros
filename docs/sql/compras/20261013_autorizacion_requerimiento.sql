-- Compras: estados PARA AUTORIZAR (6) y AUTORIZADO (7).
-- Referencia JDBC legacy: ReintegroServiceImpl.cambiarEstadoCuota.
-- Catalogo QA leido el 13/10/2026: PostgreSQL 9.6.15, sin CHECK de estado
-- y sin trigger asociado a validar_requerimiento_fila().
-- No reemplazar cambiar_estado_requerimiento() ni agregar triggers/constraints.
-- Instalar antes de publicar Java/JSP. No se aplico a QA desde Codex.

BEGIN;

DO $compatibilidad$
DECLARE
    v_constraint RECORD;
    v_def VARCHAR;
BEGIN
    IF to_regprocedure(
        'compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)'
    ) IS NULL AND EXISTS (
        SELECT 1 FROM compras.requerimiento WHERE estado IN (6, 7)
    ) THEN
        RAISE EXCEPTION
            'Los estados 6 o 7 ya estan en uso. Revise su significado antes de migrar.';
    END IF;

    FOR v_constraint IN
        SELECT conname, pg_get_constraintdef(oid) AS definicion
          FROM pg_constraint
         WHERE conrelid = 'compras.requerimiento'::regclass
           AND contype = 'c'
           AND position('estado' IN lower(pg_get_constraintdef(oid))) > 0
    LOOP
        v_def := regexp_replace(v_constraint.definicion, '[[:space:]]', '', 'g');
        IF v_constraint.conname <> 'ck_compras_requerimiento_estado'
           OR v_def NOT IN (
              'CHECK((estado=ANY(ARRAY[1,2,3,4,5,99])))',
              'CHECK((estado=ANY(ARRAY[1,2,3,4,5,6,7,99])))'
           ) THEN
            RAISE EXCEPTION
                'CHECK de estado desconocido: %. Revisar sin reemplazar reglas ajenas.',
                v_constraint.conname;
        END IF;

        IF v_def = 'CHECK((estado=ANY(ARRAY[1,2,3,4,5,99])))' THEN
            ALTER TABLE compras.requerimiento
                DROP CONSTRAINT ck_compras_requerimiento_estado;
            ALTER TABLE compras.requerimiento
                ADD CONSTRAINT ck_compras_requerimiento_estado
                CHECK (estado IN (1, 2, 3, 4, 5, 6, 7, 99));
        END IF;
    END LOOP;
END;
$compatibilidad$;

CREATE OR REPLACE FUNCTION compras.estado_requerimiento_descripcion(
    p_estado INTEGER
)
    RETURNS VARCHAR
AS $func$
BEGIN
RETURN CASE p_estado
           WHEN 1 THEN 'PENDIENTE'
           WHEN 2 THEN 'A COTIZAR'
           WHEN 3 THEN 'COTIZADO'
           WHEN 4 THEN 'RECLAMO (RP)'
           WHEN 5 THEN 'ORDEN DE COMPRA'
           WHEN 6 THEN 'PARA AUTORIZAR'
           WHEN 7 THEN 'AUTORIZADO'
           WHEN 99 THEN 'ANULADO'
           ELSE 'DESCONOCIDO'
    END;
END;
$func$
LANGUAGE plpgsql
IMMUTABLE;

CREATE OR REPLACE FUNCTION compras.listar_estados_requerimiento()
    RETURNS TABLE (
                      id INTEGER,
                      descripcion VARCHAR
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT *
FROM (
         VALUES
             (1, 'PENDIENTE'::VARCHAR),
             (2, 'A COTIZAR'::VARCHAR),
             (3, 'COTIZADO'::VARCHAR),
             (4, 'RECLAMO (RP)'::VARCHAR),
             (5, 'ORDEN DE COMPRA'::VARCHAR),
             (6, 'PARA AUTORIZAR'::VARCHAR),
             (7, 'AUTORIZADO'::VARCHAR),
             (99, 'ANULADO'::VARCHAR)
     ) estados(id, descripcion);
END;
$func$
LANGUAGE plpgsql
IMMUTABLE;

CREATE OR REPLACE FUNCTION compras.cambiar_estado_autorizacion_requerimiento(
    p_id_requerimiento INTEGER,
    p_estado_nuevo INTEGER,
    p_usuario VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_estado_actual INTEGER;
    v_id_reclamo INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra.';
    END IF;

    IF p_estado_nuevo IS NULL OR p_estado_nuevo NOT IN (6, 7) THEN
        RAISE EXCEPTION 'El estado de autorizacion seleccionado no es valido.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT r.estado
      INTO v_estado_actual
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento activo.';
    END IF;

    IF NOT (
            (v_estado_actual = 4 AND p_estado_nuevo = 6)
         OR (v_estado_actual = 6 AND p_estado_nuevo = 7)
    ) THEN
        RAISE EXCEPTION
            'El requerimiento no puede pasar al estado seleccionado desde su estado actual.';
    END IF;

    SELECT rr.id_reclamo_prestacional
      INTO v_id_reclamo
      FROM compras.requerimiento_reclamo_prestacional rr
      JOIN autorizaciones.reclamos_prestacionales rp
        ON rp.id = rr.id_reclamo_prestacional
       AND rp.baja_fecha IS NULL
     WHERE rr.id_requerimiento = p_id_requerimiento
       AND rr.estado = 'VINCULADO'
       AND rr.id_reclamo_prestacional > 0
     FOR UPDATE OF rr, rp;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = p_estado_nuevo,
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$func$
LANGUAGE plpgsql;

-- Preserva toda validacion instalada fuera de la transicion de autorizacion.
-- Si la funcion no existe, no se crea una validacion nueva ni un trigger.
DO $adaptar_trigger$
DECLARE
    v_oid REGPROCEDURE;
    v_def TEXT;
    v_original TEXT;
    v_viejo TEXT;
    v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.validar_requerimiento_fila()');
    IF v_oid IS NULL THEN
        RETURN;
    END IF;

    v_def := pg_get_functiondef(v_oid);
    v_original := v_def;

    v_viejo := $viejo$    v_cambio_estructura BOOLEAN;
    v_usuario VARCHAR(100);$viejo$;
    v_nuevo := $nuevo$    v_cambio_estructura BOOLEAN;
    v_usuario VARCHAR(100);
    v_fila_transicion compras.requerimiento%ROWTYPE;$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, ''))
           <> length(v_viejo) THEN
            RAISE EXCEPTION
                'Variante desconocida de validar_requerimiento_fila. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    v_viejo := $viejo$        IF OLD.estado IN (4, 5, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;$viejo$;
    v_nuevo := $nuevo$        IF OLD.estado IN (5, 7, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        IF OLD.estado IN (4, 6)
           AND NEW IS DISTINCT FROM OLD THEN

            v_fila_transicion := NEW;
            v_fila_transicion.estado := OLD.estado;
            v_fila_transicion.modi_fecha := OLD.modi_fecha;
            v_fila_transicion.modi_usr := OLD.modi_usr;

            IF NOT (
                    (OLD.estado = 4 AND NEW.estado = 6)
                 OR (OLD.estado = 6 AND NEW.estado = 7)
               )
               OR v_fila_transicion IS DISTINCT FROM OLD THEN

                RAISE EXCEPTION
                    'El requerimiento no puede modificarse en el estado actual.';
            END IF;
        END IF;$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, ''))
           <> length(v_viejo) THEN
            RAISE EXCEPTION
                'Variante desconocida de validar_requerimiento_fila. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    v_viejo := $viejo$                 OR (OLD.estado = 3 AND NEW.estado = 4)
            ) THEN$viejo$;
    v_nuevo := $nuevo$                 OR (OLD.estado = 3 AND NEW.estado = 4)
                 OR (OLD.estado = 4 AND NEW.estado = 6)
                 OR (OLD.estado = 6 AND NEW.estado = 7)
            ) THEN$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, ''))
           <> length(v_viejo) THEN
            RAISE EXCEPTION
                'Variante desconocida de validar_requerimiento_fila. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    v_def := replace(v_def,
        'RECLAMO_RP, ORDEN_COMPRA y ANULADO son completamente',
        'ORDEN_COMPRA, AUTORIZADO y ANULADO son completamente');
    IF v_def IS DISTINCT FROM v_original THEN
        EXECUTE v_def;
    END IF;
END;
$adaptar_trigger$;

COMMIT;
