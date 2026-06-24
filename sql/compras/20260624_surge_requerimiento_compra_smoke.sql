BEGIN;

DO $smoke$
DECLARE
    v_id INTEGER;
    v_surge BOOLEAN;
    v_old_function OID;
    v_new_function OID;
BEGIN
    v_old_function := to_regprocedure(
        'compras.guardar_requerimiento(integer, character varying, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, integer, character varying, boolean, text, character varying)'
    )::OID;

    IF v_old_function IS NOT NULL THEN
        RAISE EXCEPTION
            'SMOKE: todavia existe guardar_requerimiento con 21 argumentos.';
    END IF;

    v_new_function := to_regprocedure(
        'compras.guardar_requerimiento(integer, character varying, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, integer, character varying, boolean, boolean, text, character varying)'
    )::OID;

    IF v_new_function IS NULL THEN
        RAISE EXCEPTION
            'SMOKE: no existe guardar_requerimiento con 22 argumentos.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM information_schema.columns c
         WHERE c.table_schema = 'compras'
           AND c.table_name = 'requerimiento'
           AND c.column_name = 'surge'
           AND c.is_nullable = 'NO'
           AND c.column_default IS NOT NULL
    ) THEN
        RAISE EXCEPTION
            'SMOKE: columna compras.requerimiento.surge incompleta.';
    END IF;

    v_id := compras.guardar_requerimiento(
        NULL,
        '20111111112',
        1,
        123456,
        'Nombre',
        'Apellido',
        'DNI',
        '11222333',
        'Calle 123',
        'Localidad',
        'Provincia',
        '111-222',
        '333-444',
        'afiliado@example.com',
        1,
        100,
        0,
        NULL,
        FALSE,
        FALSE,
        'Smoke surge false',
        'smoke'
    );

    SELECT r.surge
      INTO v_surge
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = v_id;

    IF v_surge IS DISTINCT FROM FALSE THEN
        RAISE EXCEPTION
            'SMOKE: alta con surge false no persistio FALSE.';
    END IF;

    v_id := compras.guardar_requerimiento(
        v_id,
        '20111111112',
        1,
        123456,
        'Nombre',
        'Apellido',
        'DNI',
        '11222333',
        'Calle 123',
        'Localidad',
        'Provincia',
        '111-222',
        '333-444',
        'afiliado@example.com',
        1,
        100,
        0,
        NULL,
        FALSE,
        TRUE,
        'Smoke surge true',
        'smoke'
    );

    SELECT r.surge
      INTO v_surge
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = v_id;

    IF v_surge IS DISTINCT FROM TRUE THEN
        RAISE EXCEPTION
            'SMOKE: edicion de surge false a true no persistio TRUE.';
    END IF;

    v_id := compras.guardar_requerimiento(
        v_id,
        '20111111112',
        1,
        123456,
        'Nombre',
        'Apellido',
        'DNI',
        '11222333',
        'Calle 123',
        'Localidad',
        'Provincia',
        '111-222',
        '333-444',
        'afiliado@example.com',
        1,
        100,
        0,
        NULL,
        FALSE,
        FALSE,
        'Smoke surge false again',
        'smoke'
    );

    SELECT r.surge
      INTO v_surge
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = v_id;

    IF v_surge IS DISTINCT FROM FALSE THEN
        RAISE EXCEPTION
            'SMOKE: edicion de surge true a false no persistio FALSE.';
    END IF;

    RAISE NOTICE 'SURGE_REQUERIMIENTO_SMOKE_OK';
END;
$smoke$;

ROLLBACK;
