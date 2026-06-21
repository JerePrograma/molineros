-- Ejecutar únicamente sobre una base descartable cargada con el esquema
-- canónico de Compras y los stubs de public.prestador/trae_tipos_prestadores().

DO $smoke$
DECLARE
    v_id INTEGER;
    v_articulo_uno INTEGER;
    v_articulo_dos INTEGER;
    v_detalle_uno INTEGER;
    v_detalle_dos INTEGER;
    v_estados TEXT;
    v_total NUMERIC;
BEGIN
    INSERT INTO public.prestador (
        id_prestador,
        descripcion,
        cuit,
        contacto,
        id_tipo_prestador,
        solicitar_cotizacion
    )
    VALUES
        (
            20,
            'Prestador enviado',
            '20123456789',
            'enviado@example.com',
            1,
            TRUE
        ),
        (
            21,
            'Prestador no enviado',
            '20987654321',
            'no-enviado@example.com',
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
        123456,
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

    IF (
        SELECT afiliado_id_ospim
          FROM compras.requerimiento
         WHERE id_requerimiento = v_id
    ) IS NOT NULL THEN
        RAISE EXCEPTION
            'Se conservó id_ospim sin afiliado asociado.';
    END IF;

    INSERT INTO compras.articulo (
        id_sector,
        descripcion,
        alta_usr
    )
    VALUES (
        5,
        'Artículo smoke uno',
        'smoke'
    )
    RETURNING id_articulo INTO v_articulo_uno;

    INSERT INTO compras.articulo (
        id_sector,
        descripcion,
        alta_usr
    )
    VALUES (
        5,
        'Artículo smoke dos',
        'smoke'
    )
    RETURNING id_articulo INTO v_articulo_dos;

    v_detalle_uno := compras.guardar_requerimiento_detalle(
        NULL,
        v_id,
        v_articulo_uno,
        2,
        'Detalle smoke uno',
        'smoke'
    );

    v_detalle_dos := compras.guardar_requerimiento_detalle(
        NULL,
        v_id,
        v_articulo_dos,
        3,
        'Detalle smoke dos',
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
    VALUES
        (
            v_id,
            20,
            'ERROR',
            1,
            'enviado@example.com',
            NULL,
            'smoke'
        ),
        (
            v_id,
            21,
            'ERROR',
            1,
            'no-enviado@example.com',
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
           SET cantidad = 999
         WHERE id_detalle = v_detalle_uno;
        RAISE EXCEPTION
            'Se permitió modificar cantidad en A COTIZAR.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'Se permitió modificar cantidad en A COTIZAR.' THEN
                RAISE;
            END IF;
    END;

    BEGIN
        UPDATE compras.requerimiento_detalle
           SET precio_unitario_estimado = -1
         WHERE id_detalle = v_detalle_uno;
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
        UPDATE compras.requerimiento_detalle
           SET precio_unitario_estimado = 10.00,
               id_prestador = 21
         WHERE id_detalle = v_detalle_uno;
        RAISE EXCEPTION
            'Se permitió adjudicar un prestador no ENVIADO.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'Se permitió adjudicar un prestador no ENVIADO.' THEN
                RAISE;
            END IF;
    END;

    UPDATE compras.requerimiento_detalle
       SET precio_unitario_estimado = 10.00,
           precio_total_estimado = 999.99,
           id_prestador = 20,
           modi_usr = 'smoke'
     WHERE id_detalle = v_detalle_uno;

    SELECT precio_total_estimado
      INTO v_total
      FROM compras.requerimiento_detalle
     WHERE id_detalle = v_detalle_uno;

    IF v_total <> 20.00 THEN
        RAISE EXCEPTION
            'El total manipulado no fue recalculado: %.',
            v_total;
    END IF;

    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(
            v_id,
            3,
            'smoke'
        );
        RAISE EXCEPTION
            'Un solo detalle completo cerró la cotización.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'Un solo detalle completo cerró la cotización.' THEN
                RAISE;
            END IF;
    END;

    IF (
        SELECT estado
          FROM compras.requerimiento
         WHERE id_requerimiento = v_id
    ) <> 2 THEN
        RAISE EXCEPTION
            'La cotización incompleta no permaneció en A COTIZAR.';
    END IF;

    BEGIN
        UPDATE compras.requerimiento_detalle
           SET precio_unitario_estimado = 12.00,
               id_prestador = 20
         WHERE id_detalle = v_detalle_uno;

        UPDATE compras.requerimiento_detalle
           SET precio_unitario_estimado = -1
         WHERE id_detalle = v_detalle_dos;

        RAISE EXCEPTION
            'La operación intermedia inválida no falló.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'La operación intermedia inválida no falló.' THEN
                RAISE;
            END IF;
    END;

    SELECT precio_total_estimado
      INTO v_total
      FROM compras.requerimiento_detalle
     WHERE id_detalle = v_detalle_uno;

    IF v_total <> 20.00 THEN
        RAISE EXCEPTION
            'No se revirtió el cambio previo al error intermedio: %.',
            v_total;
    END IF;

    UPDATE compras.requerimiento_detalle
       SET precio_unitario_estimado = 5.00,
           precio_total_estimado = 0.01,
           id_prestador = 20,
           modi_usr = 'smoke'
     WHERE id_detalle = v_detalle_dos;

    SELECT precio_total_estimado
      INTO v_total
      FROM compras.requerimiento_detalle
     WHERE id_detalle = v_detalle_dos;

    IF v_total <> 15.00 THEN
        RAISE EXCEPTION
            'El segundo total no fue recalculado: %.',
            v_total;
    END IF;

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
        UPDATE compras.requerimiento_detalle
           SET precio_unitario_estimado = 15.00
         WHERE id_detalle = v_detalle_uno;
        RAISE EXCEPTION
            'Se permitió modificar un detalle COTIZADO.';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLERRM =
               'Se permitió modificar un detalle COTIZADO.' THEN
                RAISE;
            END IF;
    END;

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
