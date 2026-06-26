BEGIN;

DO $smoke$
DECLARE
    v_sector INTEGER;
    v_req_uno INTEGER;
    v_req_dos INTEGER;
    v_rel RECORD;
    v_bloqueado BOOLEAN;
    v_id_reclamo INTEGER := 2147480001;
BEGIN
    IF to_regclass(
        'compras.requerimiento_reclamo_prestacional'
    ) IS NULL THEN
        RAISE EXCEPTION
            'SMOKE: falta la tabla de vinculacion.';
    END IF;

    IF to_regprocedure(
        'compras.reservar_reclamo_prestacional(integer, character varying, character varying)'
    ) IS NULL THEN
        RAISE EXCEPTION
            'SMOKE: falta reservar_reclamo_prestacional.';
    END IF;

    SELECT s.id_sector
      INTO v_sector
      FROM compras.sector_requerimiento s
     WHERE s.baja_fecha IS NULL
     ORDER BY s.id_sector
     LIMIT 1;

    IF v_sector IS NULL THEN
        RAISE EXCEPTION
            'SMOKE: no existe un sector activo.';
    END IF;

    INSERT INTO compras.requerimiento (
        estado,
        id_sector,
        afiliado_cuil_titular,
        afiliado_int,
        cargo_ospim,
        cargo_tercerizadora,
        recupero,
        surge,
        alta_usr
    )
    VALUES (
        3,
        v_sector,
        '20384307400',
        0,
        100,
        0,
        FALSE,
        FALSE,
        'smoke-reclamo'
    )
    RETURNING id_requerimiento
         INTO v_req_uno;

    INSERT INTO compras.requerimiento (
        estado,
        id_sector,
        afiliado_cuil_titular,
        afiliado_int,
        cargo_ospim,
        cargo_tercerizadora,
        recupero,
        surge,
        alta_usr
    )
    VALUES (
        3,
        v_sector,
        '20384307400',
        0,
        100,
        0,
        FALSE,
        FALSE,
        'smoke-reclamo'
    )
    RETURNING id_requerimiento
         INTO v_req_dos;

    IF NOT compras.reservar_reclamo_prestacional(
        v_req_uno,
        'smoke-token-uno',
        'smoke'
    ) THEN
        RAISE EXCEPTION
            'SMOKE: no se pudo tomar la primera reserva.';
    END IF;

    v_bloqueado := FALSE;

    BEGIN
        PERFORM compras.reservar_reclamo_prestacional(
            v_req_uno,
            'smoke-token-uno',
            'smoke'
        );
    EXCEPTION
        WHEN OTHERS THEN
            v_bloqueado := TRUE;
    END;

    IF NOT v_bloqueado THEN
        RAISE EXCEPTION
            'SMOKE: un doble envio con el mismo token obtuvo la reserva.';
    END IF;

    v_bloqueado := FALSE;

    BEGIN
        PERFORM compras.reservar_reclamo_prestacional(
            v_req_uno,
            'smoke-token-dos',
            'smoke'
        );
    EXCEPTION
        WHEN OTHERS THEN
            v_bloqueado := TRUE;
    END;

    IF NOT v_bloqueado THEN
        RAISE EXCEPTION
            'SMOKE: una segunda reserva obtuvo el mismo requerimiento.';
    END IF;

    IF NOT compras.finalizar_reclamo_prestacional(
        v_req_uno,
        'smoke-token-uno',
        v_id_reclamo,
        'smoke'
    ) THEN
        RAISE EXCEPTION
            'SMOKE: no se pudo finalizar la vinculacion.';
    END IF;

    SELECT *
      INTO v_rel
      FROM compras.get_requerimiento_reclamo_prestacional(
          v_req_uno
      );

    IF v_rel.estado <> 'VINCULADO'
            OR v_rel.id_reclamo_prestacional <> v_id_reclamo THEN
        RAISE EXCEPTION
            'SMOKE: la vinculacion final no es correcta.';
    END IF;

    SELECT r.estado
      INTO v_sector
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = v_req_uno;

    IF v_sector <> 3 THEN
        RAISE EXCEPTION
            'SMOKE: el requerimiento dejo de estar COTIZADO.';
    END IF;

    PERFORM compras.reservar_reclamo_prestacional(
        v_req_dos,
        'smoke-token-tres',
        'smoke'
    );

    v_bloqueado := FALSE;

    BEGIN
        PERFORM compras.finalizar_reclamo_prestacional(
            v_req_dos,
            'smoke-token-tres',
            v_id_reclamo,
            'smoke'
        );
    EXCEPTION
        WHEN unique_violation THEN
            v_bloqueado := TRUE;
    END;

    IF NOT v_bloqueado THEN
        RAISE EXCEPTION
            'SMOKE: el mismo reclamo se vinculo dos veces.';
    END IF;

    IF NOT compras.liberar_reserva_reclamo_prestacional(
        v_req_dos,
        'smoke-token-tres',
        'smoke'
    ) THEN
        RAISE EXCEPTION
            'SMOKE: no se pudo liberar la reserva de prueba.';
    END IF;

    RAISE NOTICE
        'REQUERIMIENTO_RECLAMO_PRESTACIONAL_SMOKE_OK';
END;
$smoke$;

ROLLBACK;
