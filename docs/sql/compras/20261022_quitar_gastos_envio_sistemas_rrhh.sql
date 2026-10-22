-- Quita el envio 9999009 de Sistemas y RRHH y evita nuevas altas alli.
-- Baja logica de detalles: conserva el historial. Sin filtro de estado.
BEGIN;

CREATE OR REPLACE FUNCTION compras.agregar_gastos_envio_requerimiento()
RETURNS TRIGGER
AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM compras.sector_requerimiento s
        WHERE s.id_sector = NEW.id_sector
          AND compras.normalizar_sector(s.descripcion) IN ('SISTEMAS', 'RRHH')
    ) THEN
        RETURN NEW;
    END IF;

    INSERT INTO compras.requerimiento_detalle (
        id_requerimiento,
        tipo_item,
        id_prestacion,
        id_tipo_nomenclador,
        codigo_nomenclador,
        descripcion_nomenclador,
        cantidad,
        alta_usr
    )
    SELECT
        NEW.id_requerimiento,
        'NOMENCLADOR',
        n.id_prestacion,
        n.id_tipo_nomenclador,
        n.codigo,
        n.descripcion,
        1,
        NEW.alta_usr
    FROM autorizaciones.nomenclador n
    WHERE n.id_prestacion = 10609;

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

UPDATE compras.requerimiento_detalle d
SET baja_fecha = now(),
    baja_usr = current_user,
    modi_fecha = now(),
    modi_usr = current_user
FROM compras.requerimiento r
JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector
WHERE d.id_requerimiento = r.id_requerimiento
  AND compras.normalizar_sector(s.descripcion) IN ('SISTEMAS', 'RRHH')
  AND d.tipo_item = 'NOMENCLADOR'
  AND btrim(d.codigo_nomenclador) = '9999009'
  AND d.baja_fecha IS NULL
RETURNING d.id_requerimiento,
          d.id_detalle,
          d.codigo_nomenclador,
          d.baja_fecha,
          d.baja_usr;

COMMIT;
