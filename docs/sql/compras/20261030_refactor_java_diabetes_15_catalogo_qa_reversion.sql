-- SOLO QA12.1.1.12/devmolineros. REVERSION CONSERVADORA.
-- ESTE ARCHIVO NO HACE ROLLBACK DE DATOS DE CATALOGO.
-- Los IDs nuevos se conservan activos, aun sin consumo. No DELETE/UPDATE ni baja logica.
-- Revertir Java/asociacion requiere su procedimiento coordinado; nunca borrar maestros compartidos.
-- No existe mapping persistente que permita inferir propiedad solo por codigo/descripcion.
BEGIN;
SET LOCAL statement_timeout='15s';
SET LOCAL lock_timeout='2s';
DO $reversion_conservadora$
BEGIN
    IF inet_server_addr() IS DISTINCT FROM '12.1.1.12'::inet OR current_database()<>'devmolineros' THEN
        RAISE EXCEPTION 'Destino incorrecto; esta guardia solo corresponde a QA12/devmolineros.';
    END IF;
    IF NOT EXISTS(SELECT 1 FROM public.tipo_nomenclador WHERE id_tipo_nomenclador=15
            AND descripcion='INSUMOS DIABETES' AND cod_tipo_nomenclador='ID') THEN
        RAISE EXCEPTION 'Catalogo15 no coincide; no retirar identidades por inferencia.';
    END IF;
    IF EXISTS(SELECT 1 FROM autorizaciones.nomenclador WHERE id_tipo_nomenclador=15
            AND btrim(codigo) IN ('9100059','9100060','9100061')) THEN
        RAISE EXCEPTION 'No se revierte el catalogo: conservar IDs dedicados activos y sus planes/conceptos. Reversion de Java/asociacion debe coordinarse aparte; no borrar historial ni referencias.';
    END IF;
    RAISE NOTICE 'No hay canonicos dedicados de esta copia; ninguna mutacion de catalogo realizada.';
END;
$reversion_conservadora$;
COMMIT;
