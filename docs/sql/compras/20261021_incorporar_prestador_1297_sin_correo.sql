-- Incorporacion manual solicitada: requerimiento 1297 / CUIT 33719082799.
-- No ejecuta notificaciones, reservas, generacion de pedidos ni correo.
-- Referencias: es_prestador_compatible_cotizacion y reserva de Compras;
-- Liquidaciones: PrestadorServiceImpl.getPrestadores (prestador real por CUIT).
-- fecha_envio es obligatoria por el CHECK legacy: en esta fila nueva registra
-- la fecha de incorporacion manual, NO un envio SMTP. intentos queda en cero.
-- Ejecutar este archivo completo en la base de uso y recargar el requerimiento.
-- Es independiente de los instaladores generales de Compras.

BEGIN;

DO $incorporacion_manual$
DECLARE
    v_id_prestador INTEGER;
    v_cantidad INTEGER;
    v_estado INTEGER;
    v_estado_envio VARCHAR(20);
    v_funcion REGPROCEDURE;
    v_cuerpo TEXT;
    v_definicion TEXT;
    v_actualizada TEXT;
BEGIN
    -- Mismo orden de bloqueo que la reserva de notificaciones.
    SELECT r.estado INTO v_estado
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = 1297
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND OR v_estado NOT IN (2, 3, 4, 5, 6, 7) THEN
        RAISE EXCEPTION
            'El requerimiento 1297 no esta activo en un estado que permita mostrar prestadores enviados.';
    END IF;

    SELECT count(*), min(p.id_prestador)
    INTO v_cantidad, v_id_prestador
    FROM public.prestador p
    WHERE p.baja_fecha IS NULL
      AND regexp_replace(COALESCE(p.cuit, ''), '[^0-9]', '', 'g')
          = '33719082799';

    IF v_cantidad <> 1 THEN
        RAISE EXCEPTION
            'Se esperaba un unico prestador activo para el CUIT 33719082799; encontrados: %.',
            v_cantidad;
    END IF;

    PERFORM 1 FROM public.prestador p
    WHERE p.id_prestador = v_id_prestador
      AND p.baja_fecha IS NULL
      AND regexp_replace(COALESCE(p.cuit, ''), '[^0-9]', '', 'g')
          = '33719082799'
    FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'El prestador cambio durante la incorporacion manual.';
    END IF;

    SELECT rcp.estado_envio INTO v_estado_envio
    FROM compras.requerimiento_cotizacion_prestador rcp
    WHERE rcp.id_requerimiento = 1297
      AND rcp.id_prestador = v_id_prestador
    FOR UPDATE;

    IF FOUND AND v_estado_envio NOT IN ('ENVIADO', 'COTIZADO') THEN
        RAISE EXCEPTION
            'El prestador tiene una notificacion en estado %. No se sobrescribe su historial ni un posible envio en curso.',
            v_estado_envio;
    END IF;

    -- Conservar toda la regla instalada, sus permisos y su identidad.
    v_funcion := to_regprocedure(
        'compras.es_prestador_compatible_cotizacion(integer,integer)'
    );
    IF v_funcion IS NULL THEN
        RAISE EXCEPTION 'No existe la funcion de compatibilidad de prestadores.';
    END IF;

    SELECT p.prosrc INTO v_cuerpo
    FROM pg_proc p JOIN pg_language l ON l.oid = p.prolang
    WHERE p.oid = v_funcion
      AND l.lanname = 'sql'
      AND p.prorettype = 'boolean'::regtype
      AND NOT p.proretset;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Contrato de compatibilidad desconocido. Revisar sin reemplazar reglas ajenas.';
    END IF;

    IF position('req1297_prestador33719082799_sin_correo' IN v_cuerpo) = 0 THEN
        IF v_cuerpo !~* '^[[:space:]]*SELECT[[:space:]]+EXISTS[[:space:]]*[(]' THEN
            RAISE EXCEPTION 'Cuerpo de compatibilidad desconocido. No se modifico la funcion.';
        END IF;
        v_definicion := pg_get_functiondef(v_funcion);
        v_actualizada := regexp_replace(
            v_cuerpo,
            '^[[:space:]]*SELECT[[:space:]]+EXISTS[[:space:]]*[(]',
            $excepcion$    SELECT
        /* req1297_prestador33719082799_sin_correo */
        EXISTS (
            SELECT 1
            FROM compras.requerimiento r_manual
            JOIN public.prestador p_manual
              ON p_manual.id_prestador = $2
             AND p_manual.baja_fecha IS NULL
            WHERE $1 = 1297
              AND r_manual.id_requerimiento = $1
              AND r_manual.baja_fecha IS NULL
              AND regexp_replace(
                  COALESCE(p_manual.cuit, ''), '[^0-9]', '', 'g'
              ) = '33719082799'
        )
        OR EXISTS ($excepcion$,
            'i'
        );
        EXECUTE replace(v_definicion, v_cuerpo, v_actualizada);
    END IF;

    IF NOT compras.es_prestador_compatible_cotizacion(1297, v_id_prestador) THEN
        RAISE EXCEPTION 'No se pudo habilitar el prestador para el requerimiento 1297.';
    END IF;

    INSERT INTO compras.requerimiento_cotizacion_prestador (
        id_requerimiento, id_prestador, estado_envio, intentos,
        email_destino, fecha_ultimo_intento, fecha_envio, alta_usr
    ) VALUES (
        1297, v_id_prestador, 'ENVIADO', 0,
        NULL, NULL, now(), left(current_user, 100)
    ) ON CONFLICT (id_requerimiento, id_prestador) DO NOTHING;

    IF NOT EXISTS (
        SELECT 1 FROM compras.listar_prestadores_enviados(1297, 2147483647) p
        WHERE p.id_prestador = v_id_prestador
          AND p.estado_envio IN ('ENVIADO', 'COTIZADO')
    ) THEN
        RAISE EXCEPTION 'El prestador no aparece en el listado. Se revierte toda la incorporacion.';
    END IF;

    RAISE NOTICE 'Prestador % incorporado al requerimiento 1297 sin envio de correo. Si ya estaba ENVIADO/COTIZADO se conservo su historial.',
        v_id_prestador;
END;
$incorporacion_manual$;

COMMIT;

SELECT rcp.id_requerimiento, p.id_prestador, p.descripcion, p.cuit,
       rcp.estado_envio, rcp.intentos, rcp.email_destino,
       rcp.fecha_ultimo_intento, rcp.fecha_envio
FROM compras.requerimiento_cotizacion_prestador rcp
JOIN public.prestador p ON p.id_prestador = rcp.id_prestador
WHERE rcp.id_requerimiento = 1297
  AND p.baja_fecha IS NULL
  AND regexp_replace(COALESCE(p.cuit, ''), '[^0-9]', '', 'g') = '33719082799';
