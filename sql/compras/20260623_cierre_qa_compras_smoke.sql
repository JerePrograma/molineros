-- Smoke test de cierre QA Compras.
-- Ejecutar sobre una base descartable con compras_schema.sql + migracion 20260623.
-- No deja datos: usa transaccion y ROLLBACK.

BEGIN;

DO $smoke$
DECLARE
    v_id INTEGER;
    v_articulo INTEGER;
    v_detalle INTEGER;
    v_estados TEXT;
    v_pdf_id_ospim INTEGER;
    v_pdf_documento VARCHAR;
    v_estado INTEGER;
BEGIN
    IF to_regprocedure(
        'compras.guardar_requerimiento(integer, character varying, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, integer, character varying, boolean, text, character varying)'
    ) IS NULL THEN
        RAISE EXCEPTION
            'SMOKE: falta firma canonica de 21 argumentos para guardar_requerimiento.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM pg_proc p
         WHERE p.oid = to_regprocedure(
                'compras.get_requerimiento_compra_pdf(integer)'
             )::OID
           AND array_position(p.proargnames, 'afiliado_documento') IS NOT NULL
           AND array_position(p.proargnames, 'afiliado_id_ospim') IS NOT NULL
           AND array_position(p.proargnames, 'total_general') IS NULL
    ) THEN
        RAISE EXCEPTION
            'SMOKE: contrato PDF incompatible.';
    END IF;

    SELECT string_agg(
               e.id::TEXT || ':' || e.descripcion,
               ',' ORDER BY e.id
           )
      INTO v_estados
      FROM compras.listar_estados_requerimiento() e;

    IF v_estados <>
       '1:PENDIENTE,2:A COTIZAR,3:COTIZADO,4:RECLAMO (RP),5:ORDEN DE COMPRA,99:ANULADO' THEN
        RAISE EXCEPTION
            'SMOKE: catalogo de estados inesperado: %.',
            v_estados;
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
        'Smoke cierre QA',
        'smoke'
    );

    IF (
        SELECT r.afiliado_id_ospim
          FROM compras.requerimiento r
         WHERE r.id_requerimiento = v_id
    ) <> 123456 THEN
        RAISE EXCEPTION
            'SMOKE: afiliado_id_ospim no persistio.';
    END IF;

    SELECT
        pdf.afiliado_id_ospim,
        pdf.afiliado_documento
      INTO
        v_pdf_id_ospim,
        v_pdf_documento
      FROM compras.get_requerimiento_compra_pdf(v_id) pdf
     LIMIT 1;

    IF v_pdf_id_ospim <> 123456 THEN
        RAISE EXCEPTION
            'SMOKE: PDF no expone afiliado_id_ospim.';
    END IF;

    IF v_pdf_documento <> 'DNI 11222333' THEN
        RAISE EXCEPTION
            'SMOKE: PDF no conserva afiliado_documento. Valor=%.',
            v_pdf_documento;
    END IF;

    v_articulo := compras.guardar_articulo(
        NULL,
        1,
        'Articulo smoke cierre QA'
    );

    v_detalle := compras.guardar_requerimiento_detalle(
        NULL,
        v_id,
        v_articulo,
        2,
        'Detalle smoke',
        'smoke'
    );

    INSERT INTO compras.requerimiento_cotizacion_prestador (
        id_requerimiento,
        id_prestador,
        estado_envio,
        intentos,
        email_destino,
        fecha_envio,
        alta_usr
    )
    VALUES (
        v_id,
        9001,
        'ENVIADO',
        1,
        'prestador@example.com',
        now(),
        'smoke'
    );

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            1,
            'smoke'
        );
        RAISE EXCEPTION
            'SMOKE: se acepto transicion al mismo estado. SQLSTATE=<sin error>, SQLERRM=<sin error>.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM LIKE
               'SMOKE: se acepto transicion al mismo estado.%' THEN
                RAISE;
            END IF;

            IF SQLSTATE = 'P0001'
               AND SQLERRM LIKE
                   'La transici%n al mismo estado no es v%lida.' THEN
                NULL;
            ELSE
                RAISE EXCEPTION
                    'SMOKE: error inesperado al rechazar transicion al mismo estado. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    PERFORM compras.cambiar_estado_requerimiento(
        v_id,
        2,
        'smoke'
    );

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            1,
            'smoke'
        );
        RAISE EXCEPTION
            'SMOKE: se acepto transicion hacia atras. SQLSTATE=<sin error>, SQLERRM=<sin error>.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM LIKE
               'SMOKE: se acepto transicion hacia atras.%' THEN
                RAISE;
            END IF;

            IF SQLSTATE = 'P0001'
               AND SQLERRM LIKE
                   'Transici%n de estado inv%lida: 2 -> 1.' THEN
                NULL;
            ELSE
                RAISE EXCEPTION
                    'SMOKE: error inesperado al rechazar transicion 2 -> 1. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            4,
            'smoke'
        );
        RAISE EXCEPTION
            'SMOKE: se acepto transicion activa a RECLAMO (RP). SQLSTATE=<sin error>, SQLERRM=<sin error>.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM LIKE
               'SMOKE: se acepto transicion activa a RECLAMO (RP).%' THEN
                RAISE;
            END IF;

            IF SQLSTATE = 'P0001'
               AND SQLERRM LIKE
                   'Transici%n de estado inv%lida: 2 -> 4.' THEN
                NULL;
            ELSE
                RAISE EXCEPTION
                    'SMOKE: error inesperado al rechazar transicion 2 -> 4. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            5,
            'smoke'
        );
        RAISE EXCEPTION
            'SMOKE: se acepto transicion activa a ORDEN DE COMPRA. SQLSTATE=<sin error>, SQLERRM=<sin error>.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM LIKE
               'SMOKE: se acepto transicion activa a ORDEN DE COMPRA.%' THEN
                RAISE;
            END IF;

            IF SQLSTATE = 'P0001'
               AND SQLERRM LIKE
                   'Transici%n de estado inv%lida: 2 -> 5.' THEN
                NULL;
            ELSE
                RAISE EXCEPTION
                    'SMOKE: error inesperado al rechazar transicion 2 -> 5. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    UPDATE compras.requerimiento_detalle
       SET precio_unitario_estimado = 10.00,
           id_prestador = 9001,
           modi_usr = 'smoke'
     WHERE id_detalle = v_detalle;

    PERFORM compras.cambiar_estado_requerimiento(
        v_id,
        3,
        'smoke'
    );

    SELECT r.estado
      INTO v_estado
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = v_id;

    IF v_estado <> 3 THEN
        RAISE EXCEPTION
            'SMOKE: 2 -> 3 no dejo el requerimiento COTIZADO.';
    END IF;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            2,
            'smoke'
        );
        RAISE EXCEPTION
            'SMOKE: se acepto salida desde COTIZADO. SQLSTATE=<sin error>, SQLERRM=<sin error>.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM LIKE
               'SMOKE: se acepto salida desde COTIZADO.%' THEN
                RAISE;
            END IF;

            IF SQLSTATE = 'P0001'
               AND SQLERRM =
                   'El requerimiento no puede modificarse en el estado actual.' THEN
                NULL;
            ELSE
                RAISE EXCEPTION
                    'SMOKE: error inesperado al rechazar salida desde COTIZADO. SQLSTATE=%, SQLERRM=%.',
                    SQLSTATE,
                    SQLERRM;
            END IF;
    END;

    RAISE NOTICE 'SQL_SMOKE_OK';
END;
$smoke$;

ROLLBACK;
