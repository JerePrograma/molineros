BEGIN;

-- ============================================================
-- 1. ELIMINAR TODOS LOS CHECK DEL ESQUEMA COMPRAS
--    NO toca PK, FK, UNIQUE ni NOT NULL.
-- ============================================================

DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT
            n.nspname AS esquema,
            c.relname AS tabla,
            con.conname AS restriccion
        FROM pg_constraint con
        JOIN pg_class c
          ON c.oid = con.conrelid
        JOIN pg_namespace n
          ON n.oid = c.relnamespace
        WHERE n.nspname = 'compras'
          AND con.contype = 'c'
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I DROP CONSTRAINT %I',
            r.esquema,
            r.tabla,
            r.restriccion
        );
    END LOOP;
END;
$$;


-- ============================================================
-- 2. ELIMINAR TRIGGERS DE VALIDACION FUNCIONAL
-- ============================================================

DROP TRIGGER IF EXISTS tr_compras_detalle_tipo_prestacion
    ON compras.requerimiento_detalle;

DROP TRIGGER IF EXISTS tr_compras_detalle_tipo_prestacion_nuevo
    ON compras.requerimiento_detalle;

DROP TRIGGER IF EXISTS trg_compras_cotizacion_prestador_compatible
    ON compras.requerimiento_cotizacion_prestador;


-- ============================================================
-- 3. REEMPLAZAR EL TRIGGER DE DETALLE
--
-- El original validaba/bloqueaba modificaciones Y calculaba
-- precio_total_estimado.
--
-- Se elimina toda validacion pero se conserva el calculo que
-- necesita el flujo normal de Guardar cotizacion.
-- ============================================================

DROP TRIGGER IF EXISTS trg_compras_detalle_validar
    ON compras.requerimiento_detalle;


CREATE OR REPLACE FUNCTION compras.calcular_total_detalle_fila()
RETURNS TRIGGER
AS $$
BEGIN

    /*
     * INSERT:
     * solo calcula si no se informó explícitamente un total.
     */
    IF TG_OP = 'INSERT'
       AND NEW.precio_total_estimado IS NULL
       AND NEW.precio_unitario_estimado IS NOT NULL THEN

        NEW.precio_total_estimado :=
            round(
                NEW.cantidad * NEW.precio_unitario_estimado,
                2
            );

    /*
     * UPDATE:
     * si el caller no modificó explícitamente el total,
     * se recalcula.
     *
     * Esto permite que la aplicación siga funcionando y,
     * al mismo tiempo, que un UPDATE manual pueda establecer
     * explícitamente precio_total_estimado.
     */
    ELSIF TG_OP = 'UPDATE'
       AND NEW.precio_total_estimado
            IS NOT DISTINCT FROM OLD.precio_total_estimado THEN

        IF NEW.precio_unitario_estimado IS NULL THEN
            NEW.precio_total_estimado := NULL;
        ELSE
            NEW.precio_total_estimado :=
                round(
                    NEW.cantidad * NEW.precio_unitario_estimado,
                    2
                );
        END IF;

    END IF;

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;


CREATE TRIGGER trg_compras_detalle_calcular_total
    BEFORE INSERT OR UPDATE OF cantidad, precio_unitario_estimado
    ON compras.requerimiento_detalle
    FOR EACH ROW
    EXECUTE PROCEDURE compras.calcular_total_detalle_fila();


-- ============================================================
-- 4. REEMPLAZAR TRIGGER DE REQUERIMIENTO
--
-- El original bloqueaba estados, edición, afiliado, SURGE, etc.
--
-- Se eliminan TODAS esas restricciones.
-- Solo se conserva el automatismo de baja al pasar a ANULADO,
-- porque el flujo normal depende de baja_fecha.
-- ============================================================

DROP TRIGGER IF EXISTS trg_compras_requerimiento_validar
    ON compras.requerimiento;


CREATE OR REPLACE FUNCTION compras.completar_baja_requerimiento_fila()
RETURNS TRIGGER
AS $$
BEGIN

    IF NEW.estado = 99
       AND OLD.estado IS DISTINCT FROM 99 THEN

        NEW.baja_fecha :=
            COALESCE(
                NEW.baja_fecha,
                now()
            );

        NEW.baja_usr :=
            COALESCE(
                NULLIF(btrim(NEW.baja_usr), ''),
                NULLIF(btrim(NEW.modi_usr), ''),
                current_user::VARCHAR
            );

    END IF;

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;


CREATE TRIGGER trg_compras_requerimiento_completar_baja
    BEFORE UPDATE OF estado
    ON compras.requerimiento
    FOR EACH ROW
    EXECUTE PROCEDURE compras.completar_baja_requerimiento_fila();


COMMIT;