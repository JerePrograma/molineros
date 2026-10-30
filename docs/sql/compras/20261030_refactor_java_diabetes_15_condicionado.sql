-- Nueva seleccion Diabetes SOLO QA12/devmolineros, con catalogo dedicado verificado.
-- No crea prestaciones ni cambia IDs. Aplicar despues del Java compatible.
-- Los efectos SQL vigentes se conservan; este archivo no autoriza retirarlos.
-- 1388 y sus detalles se conservan exactamente; correccion externa autorizada.
BEGIN;
SET LOCAL statement_timeout = '15s';
SET LOCAL lock_timeout = '2s';
DO $destino_qa12$
BEGIN
    IF inet_server_addr() IS DISTINCT FROM '12.1.1.12'::inet
       OR current_database() IS DISTINCT FROM 'devmolineros' THEN
        RAISE EXCEPTION 'La asociacion Diabetes solo se activa en QA12/devmolineros.';
    END IF;
    IF (SELECT count(*) FROM public.system_config WHERE id='COMPRAS_REFACTOR_CONFIG_VERSION')<>1
       OR NOT EXISTS(SELECT 1 FROM public.system_config
                     WHERE id='COMPRAS_REFACTOR_CONFIG_VERSION' AND valor='20261030') THEN
        RAISE EXCEPTION 'Falta la configuracion de la entrega Java; no activar tipo15.';
    END IF;
END;
$destino_qa12$;
LOCK TABLE compras.sector_nomenclador IN SHARE ROW EXCLUSIVE MODE;
DO $diabetes$
DECLARE
    -- Identidad real verificada en QA12; altas dedicadas copiadas de fuente comprobada.
    descripcion_aprobada VARCHAR := 'INSUMOS DIABETES';
    codigo_aprobado VARCHAR := 'ID';
BEGIN
    IF descripcion_aprobada IS NULL THEN
        RAISE EXCEPTION 'Falta indicar descripcion y codigo aprobados para el catalogo 15 de Diabetes.';
    END IF;
    PERFORM 1 FROM public.tipo_nomenclador WHERE id_tipo_nomenclador=15 FOR SHARE;
    PERFORM 1 FROM autorizaciones.nomenclador WHERE id_tipo_nomenclador=15 AND baja_fecha IS NULL FOR SHARE;
    IF NOT EXISTS (SELECT 1 FROM public.tipo_nomenclador WHERE id_tipo_nomenclador=15
                   AND descripcion=descripcion_aprobada AND cod_tipo_nomenclador IS NOT DISTINCT FROM codigo_aprobado) THEN
        RAISE EXCEPTION 'El catalogo 15 no coincide con la identidad aprobada para Diabetes.';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM public.tipo_nomenclador WHERE id_tipo_nomenclador=15)
       OR NOT EXISTS (SELECT 1 FROM autorizaciones.nomenclador WHERE id_tipo_nomenclador=15 AND baja_fecha IS NULL) THEN
        RAISE EXCEPTION 'Falta catalogo 15 o canonicos dedicados activos aprobados. No se inventa ni reclasifica el catalogo compartido.';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM compras.tipo_prestacion WHERE id_tipo_prestacion=8 AND id_sector=2 AND descripcion='INSUMOS DIABETES') THEN
        RAISE EXCEPTION 'El rubro instalado no coincide con el diagnostico.';
    END IF;
    IF (SELECT count(*) FROM compras.sector_nomenclador WHERE id_sector=2 AND id_tipo_prestacion=8) <> 1
       OR NOT EXISTS (SELECT 1 FROM compras.sector_nomenclador WHERE id_sector=2 AND id_tipo_prestacion=8
                       AND id_tipo_nomenclador IN (10,15) AND marca_rein_liq IS NULL AND codigo_nomenclador IS NULL) THEN
        RAISE EXCEPTION 'La asociacion Diabetes cambio; preservar y revisar la migracion.';
    END IF;
    UPDATE compras.sector_nomenclador SET id_tipo_nomenclador=15
    WHERE id_sector=2 AND id_tipo_prestacion=8 AND id_tipo_nomenclador=10
      AND marca_rein_liq IS NULL AND codigo_nomenclador IS NULL;
END;
$diabetes$;
COMMIT;
