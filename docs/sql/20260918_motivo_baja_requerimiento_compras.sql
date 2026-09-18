BEGIN;

DO $block$
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'compras'
           AND table_name = 'requerimiento'
           AND column_name = 'motivo_baja'
    ) THEN
        ALTER TABLE compras.requerimiento
            ADD COLUMN motivo_baja TEXT;
    END IF;
END;
$block$;

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
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

    SELECT r.estado
      INTO v_estado_actual
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento.';
    END IF;

    IF NOT (
            (v_estado_actual = 1)
         OR (v_estado_actual = 2)
    ) THEN
        RAISE EXCEPTION
            'El requerimiento no puede anularse desde el estado actual.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = 99,
           motivo_baja = COALESCE(p_motivo_baja, ''),
           modi_fecha = now(),
           modi_usr = v_usuario,
           baja_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$func$
LANGUAGE plpgsql;

COMMIT;
