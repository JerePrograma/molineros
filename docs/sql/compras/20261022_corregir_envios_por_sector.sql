-- Correccion completa de envios. PostgreSQL 9.6+; ISO-8859-1 sin BOM.
-- Ejecutar TODO el archivo en una conexion nueva.
-- Destino confirmado: 10415 / tipo 3 / 101002.
-- Reemplaza los parches anteriores de envios: no volver a instalarlos despues.
BEGIN;
SET LOCAL standard_conforming_strings = on;

CREATE OR REPLACE FUNCTION compras.agregar_gastos_envio_requerimiento()
RETURNS TRIGGER
AS $$
DECLARE
    v_sector TEXT;
    v_id_prestacion INTEGER;
    v_item autorizaciones.nomenclador%ROWTYPE;
BEGIN
    SELECT upper(translate(btrim(s.descripcion),
               U&'\00C1\00C9\00CD\00D3\00DA\00DC\00E1\00E9\00ED\00F3\00FA\00FC',
               'AEIOUUaeiouu'))
    INTO v_sector
    FROM compras.sector_requerimiento s
    WHERE s.id_sector = NEW.id_sector;

    IF v_sector IN ('SISTEMAS', 'RRHH') THEN
        RETURN NEW;
    END IF;

    v_id_prestacion := CASE WHEN v_sector = 'PRESTACIONES MEDICAS'
                           THEN 10415 ELSE 10609 END;
    SELECT n.* INTO STRICT v_item
    FROM autorizaciones.nomenclador n
    WHERE n.id_prestacion = v_id_prestacion AND n.baja_fecha IS NULL;

    INSERT INTO compras.requerimiento_detalle (
        id_requerimiento, tipo_item, id_prestacion, id_tipo_nomenclador,
        codigo_nomenclador, descripcion_nomenclador, cantidad, alta_usr
    ) VALUES (
        NEW.id_requerimiento, 'NOMENCLADOR', v_item.id_prestacion,
        v_item.id_tipo_nomenclador, btrim(v_item.codigo),
        btrim(v_item.descripcion), 1, NEW.alta_usr
    );
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_compras_requerimiento_gastos_envio
    ON compras.requerimiento;
CREATE TRIGGER trg_compras_requerimiento_gastos_envio
    AFTER INSERT ON compras.requerimiento
    FOR EACH ROW
    EXECUTE PROCEDURE compras.agregar_gastos_envio_requerimiento();

DO $correccion$
DECLARE
    v_destino autorizaciones.nomenclador%ROWTYPE;
    v_medicos BIGINT;
    v_comparativas BIGINT;
    v_bajas BIGINT;
BEGIN
    SELECT n.* INTO v_destino
    FROM autorizaciones.nomenclador n
    WHERE n.id_prestacion = 10415 AND n.id_tipo_nomenclador = 3
      AND btrim(n.codigo) = '101002' AND n.baja_fecha IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe activa la prestacion 10415, tipo 3, codigo 101002. No se aplicaron cambios.';
    END IF;

    UPDATE compras.requerimiento_detalle d
    SET id_prestacion = v_destino.id_prestacion,
        id_tipo_nomenclador = v_destino.id_tipo_nomenclador,
        codigo_nomenclador = btrim(v_destino.codigo),
        descripcion_nomenclador = btrim(v_destino.descripcion),
        modi_fecha = now(), modi_usr = current_user
    FROM compras.requerimiento r
    JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector
    WHERE d.id_requerimiento = r.id_requerimiento
      AND upper(translate(btrim(s.descripcion),
              U&'\00C1\00C9\00CD\00D3\00DA\00DC\00E1\00E9\00ED\00F3\00FA\00FC',
              'AEIOUUaeiouu')) = 'PRESTACIONES MEDICAS'
      AND d.tipo_item = 'NOMENCLADOR' AND d.baja_fecha IS NULL
      AND (d.id_prestacion = 10609 OR btrim(d.codigo_nomenclador) = '9999009');
    GET DIAGNOSTICS v_medicos = ROW_COUNT;

    UPDATE compras.requerimiento_comparativa_detalle cd
    SET id_prestacion = 10415, modi_fecha = now(), modi_usr = current_user
    FROM compras.requerimiento_comparativa c
    JOIN compras.requerimiento r ON r.id_requerimiento = c.id_requerimiento
    JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector
    WHERE cd.id_comparativa = c.id_comparativa AND cd.id_prestacion = 10609
      AND upper(translate(btrim(s.descripcion),
              U&'\00C1\00C9\00CD\00D3\00DA\00DC\00E1\00E9\00ED\00F3\00FA\00FC',
              'AEIOUUaeiouu')) = 'PRESTACIONES MEDICAS'
      AND EXISTS (
          SELECT 1 FROM compras.requerimiento_detalle d
          WHERE d.id_requerimiento = r.id_requerimiento
            AND d.id_prestacion = 10415 AND d.baja_fecha IS NULL
      )
      AND NOT EXISTS (
          SELECT 1 FROM compras.requerimiento_detalle d
          WHERE d.id_requerimiento = r.id_requerimiento
            AND d.id_prestacion = 10609 AND d.baja_fecha IS NULL
      );
    GET DIAGNOSTICS v_comparativas = ROW_COUNT;

    UPDATE compras.requerimiento_detalle d
    SET baja_fecha = now(), baja_usr = current_user,
        modi_fecha = now(), modi_usr = current_user
    FROM compras.requerimiento r
    JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector
    WHERE d.id_requerimiento = r.id_requerimiento
      AND upper(btrim(s.descripcion)) IN ('SISTEMAS', 'RRHH')
      AND d.tipo_item = 'NOMENCLADOR' AND d.baja_fecha IS NULL
      AND (d.id_prestacion = 10609 OR btrim(d.codigo_nomenclador) = '9999009');
    GET DIAGNOSTICS v_bajas = ROW_COUNT;

    RAISE NOTICE 'Detalles medicos corregidos: %. Comparativas corregidas: %. Bajas Sistemas/RRHH: %.',
                 v_medicos, v_comparativas, v_bajas;
END;
$correccion$;

COMMIT;

-- CONTROL POSTERIOR: ambos pendientes deben ser 0.
WITH sectores AS (
    SELECT s.id_sector,
           upper(translate(btrim(s.descripcion),
               U&'\00C1\00C9\00CD\00D3\00DA\00DC\00E1\00E9\00ED\00F3\00FA\00FC',
               'AEIOUUaeiouu')) AS sector
    FROM compras.sector_requerimiento s
)
SELECT count(*) FILTER (WHERE s.sector = 'PRESTACIONES MEDICAS')
           AS medicas_pendientes_9999009,
       count(*) FILTER (WHERE s.sector IN ('SISTEMAS', 'RRHH'))
           AS sistemas_rrhh_pendientes_9999009
FROM compras.requerimiento_detalle d
JOIN compras.requerimiento r ON r.id_requerimiento = d.id_requerimiento
JOIN sectores s ON s.id_sector = r.id_sector
WHERE d.baja_fecha IS NULL AND d.tipo_item = 'NOMENCLADOR'
  AND (d.id_prestacion = 10609 OR btrim(d.codigo_nomenclador) = '9999009');

-- Trigger habilitado y rama medica instalada: O/true en una instalacion normal.
SELECT t.tgname,
       t.tgenabled AS habilitado,
       position('THEN 10415 ELSE 10609' IN p.prosrc) > 0
           AS funcion_contiene_regla_medicas_10415
FROM pg_trigger t
JOIN pg_proc p ON p.oid = t.tgfoid
WHERE t.tgrelid = 'compras.requerimiento'::regclass
  AND t.tgname = 'trg_compras_requerimiento_gastos_envio'
  AND NOT t.tgisinternal;

-- El requerimiento de la captura debe mostrar 10415 / 101002.
SELECT d.id_requerimiento, d.id_detalle, d.id_prestacion,
       d.id_tipo_nomenclador, d.codigo_nomenclador,
       d.descripcion_nomenclador, d.cantidad
FROM compras.requerimiento_detalle d
WHERE d.id_requerimiento = 1383 AND d.baja_fecha IS NULL
ORDER BY d.id_detalle;
