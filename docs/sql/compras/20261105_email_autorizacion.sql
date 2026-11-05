-- Destinatarios del aviso de Compras al enviar un RP a autorizar.
-- PostgreSQL 9.6 / ISO-8859-1 sin BOM. Preparado; no ejecutado en produccion.
-- Dejar vacio por pedido funcional. Completar por SystemConfig antes de usar el envio.
-- No reemplaza una direccion ya configurada. Admite varias separadas por ; o ,.
BEGIN;
SET LOCAL statement_timeout = '15s';
SET LOCAL lock_timeout = '2s';
LOCK TABLE public.system_config IN SHARE ROW EXCLUSIVE MODE;
DO $configuracion$
BEGIN
    IF (SELECT count(*) FROM public.system_config WHERE id='COMPRAS_EMAIL_AUTORIZACION') > 1 THEN
        RAISE EXCEPTION 'La clave COMPRAS_EMAIL_AUTORIZACION esta duplicada; revisar la configuracion.';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM public.system_config WHERE id='COMPRAS_EMAIL_AUTORIZACION') THEN
        INSERT INTO public.system_config(id, valor) VALUES ('COMPRAS_EMAIL_AUTORIZACION', '');
    END IF;
END;
$configuracion$;
COMMIT;
