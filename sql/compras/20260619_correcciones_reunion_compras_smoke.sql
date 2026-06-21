-- Ejecutar únicamente sobre una base descartable cargada con el esquema
-- canónico de Compras y los stubs de public.prestador/trae_tipos_prestadores().

DO $smoke$
DECLARE
    v_id INTEGER;
    v_articulo INTEGER;
    v_detalle INTEGER;
    v_estados TEXT;
BEGIN
    INSERT INTO public.prestador (
        id_prestador,
        descripcion,
        cuit,
        contacto,
        id_tipo_prestador,
        solicitar_cotizacion
    )
    VALUES (
        20,
        'Prestador prueba',
        '20123456789',
        'prestador@example.com',
        1,
        TRUE
    );

    INSERT INTO compras.sector_tipo_prestador (
        id_sector,
        id_tipo_prestador,
        alta_usr
    )
    VALUES (5, 1, 'smoke');

    v_id := compras.guardar_requerimiento(
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        5,
        100,
        0,
        NULL,
        FALSE,
        'Smoke',
        'smoke'
    );

    INSERT INTO compras.articulo (
        id_sector,
        descripcion,
        alta_usr
    )
    VALUES (
        5,
        'Artículo smoke',
        'smoke'
    )
    RETURNING id_articulo INTO v_articulo;

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
        20,
        'ERROR',
        1,
        'prestador@example.com',
        NULL,
        'smoke'
    );

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            2,
            'smoke'
        );
        RAISE EXCEPTION
            'Se permitió pasar a A COTIZAR sin un envío ENVIADO.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'Se permitió pasar a A COTIZAR sin un envío ENVIADO.' THEN
                RAISE;
            END IF;
    END;

    UPDATE compras.requerimiento_cotizacion_prestador
       SET estado_envio = 'ENVIADO',
           fecha_envio = now(),
           ultimo_error = NULL
     WHERE id_requerimiento = v_id
       AND id_prestador = 20;

    PERFORM compras.cambiar_estado_requerimiento(
        v_id,
        2,
        'smoke'
    );

    BEGIN
        UPDATE compras.requerimiento_detalle
           SET precio_unitario_estimado = -1
         WHERE id_detalle = v_detalle;
        RAISE EXCEPTION
            'Se permitió un precio unitario negativo.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'Se permitió un precio unitario negativo.' THEN
                RAISE;
            END IF;
    END;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            3,
            'smoke'
        );
        RAISE EXCEPTION
            'La cotización incompleta no fue rechazada.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'La cotización incompleta no fue rechazada.' THEN
                RAISE;
            END IF;
    END;

    UPDATE compras.requerimiento_detalle
       SET precio_unitario_estimado = 10.00,
           precio_total_estimado = 20.00,
           id_prestador = 20,
           modi_usr = 'smoke'
     WHERE id_detalle = v_detalle;

    PERFORM compras.cambiar_estado_requerimiento(
        v_id,
        3,
        'smoke'
    );

    IF (
        SELECT estado
          FROM compras.requerimiento
         WHERE id_requerimiento = v_id
    ) <> 3 THEN
        RAISE EXCEPTION
            'La cotización completa no pasó a COTIZADO.';
    END IF;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            4,
            'smoke'
        );
        RAISE EXCEPTION
            'Se permitió una salida desde COTIZADO.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'Se permitió una salida desde COTIZADO.' THEN
                RAISE;
            END IF;
    END;

    SELECT string_agg(
               id || ':' || descripcion,
               ',' ORDER BY id
           )
      INTO v_estados
      FROM compras.listar_estados_requerimiento();

    IF v_estados <>
       '1:PENDIENTE,2:A COTIZAR,3:COTIZADO,4:RECLAMO (RP),5:ORDEN DE COMPRA,99:ANULADO' THEN
        RAISE EXCEPTION
            'Catálogo inesperado: %',
            v_estados;
    END IF;

    IF pg_get_function_result(
            'compras.get_requerimiento_compra_pdf(integer)'::REGPROCEDURE
       ) ILIKE '%total_general%'
       OR pg_get_function_result(
            'compras.get_requerimiento_compra_pdf(integer)'::REGPROCEDURE
       ) ILIKE '%afiliado_cuil_titular%'
       OR pg_get_function_result(
            'compras.get_requerimiento_compra_pdf(integer)'::REGPROCEDURE
       ) NOT ILIKE '%afiliado_id_ospim%' THEN
        RAISE EXCEPTION
            'El contrato PDF conserva columnas eliminadas.';
    END IF;
END;
$smoke$;

SELECT 'SQL_SMOKE_OK' AS resultado;
