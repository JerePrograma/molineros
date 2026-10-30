-- MOLINEROS / COMPRAS - Precondiciones notificaciones Java, 2026-10-30.
-- Solo catalogo. No ejecutado; no cambia datos, funciones ni triggers.
BEGIN TRANSACTION READ ONLY;
DO $validar_notificaciones$
BEGIN
    IF md5(pg_get_functiondef(to_regprocedure('compras.confirmar_envio_a_cotizar(integer,character varying)'))) IS DISTINCT FROM '756448bd5d5c0301a5312ad3c652cf00' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.confirmar_envio_a_cotizar';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.diagnosticar_prestadores_notificacion_cotizacion(integer)'))) IS DISTINCT FROM 'dc616bff8d95f7d33cf8d93fc9ae4912' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.diagnosticar_prestadores_notificacion_cotizacion';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.es_prestador_compatible_cotizacion(integer,integer)'))) IS DISTINCT FROM 'eaca26218b09570bf5499fff3768ba58' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.es_prestador_compatible_cotizacion';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.finalizar_notificacion_cotizacion_prestador(integer,integer,character varying,text,character varying)'))) IS DISTINCT FROM '0938df17a62e92549b7119adc4fd8f17' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.finalizar_notificacion_cotizacion_prestador';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.hay_prestadores_pendientes_notificacion(integer)'))) IS DISTINCT FROM '31ab2b64e22a0431a69f4a19ab51f4f3' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.hay_prestadores_pendientes_notificacion';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.listar_configuracion_correos_rubro(integer)'))) IS DISTINCT FROM '0b7e9085c82040280f19ccc5982a3e4d' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.listar_configuracion_correos_rubro';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.listar_prestadores_cotizacion_requerimiento(integer)'))) IS DISTINCT FROM 'a52a168e5040fe53f0855bf41c49543d' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.listar_prestadores_cotizacion_requerimiento';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.listar_prestadores_enviados(integer,integer)'))) IS DISTINCT FROM '54b79e68e1fe4444526bf4732d03a4ab' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.listar_prestadores_enviados';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.listar_prestadores_notificacion_cotizacion(integer)'))) IS DISTINCT FROM 'af825f3ce9c350f358d7ff966cba2b14' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.listar_prestadores_notificacion_cotizacion';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.registrar_pedido_cotizacion_documento(integer,integer,bigint,bigint,bigint,character varying,character varying,character varying,character varying,character varying)'))) IS DISTINCT FROM 'ece745d0753e82b23e0e9af92cda5e80' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.registrar_pedido_cotizacion_documento';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.reservar_notificacion_cotizacion_prestador(integer,integer,character varying)'))) IS DISTINCT FROM '0920a72525c6760f2b6a50027979dd07' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.reservar_notificacion_cotizacion_prestador';
    END IF;
    IF md5(pg_get_functiondef(to_regprocedure('compras.resolver_emails_cotizacion_prestador(integer)'))) IS DISTINCT FROM 'a2550cd264666ed0cfdf593d38807fd6' THEN
        RAISE EXCEPTION 'Deriva de definicion: compras.resolver_emails_cotizacion_prestador';
    END IF;
END;
$validar_notificaciones$;
ROLLBACK;
-- Ningun DROP habilitado. Validar escritores externos y smoke real antes del corte.
