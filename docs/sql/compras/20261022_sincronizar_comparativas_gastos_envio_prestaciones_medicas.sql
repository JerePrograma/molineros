-- Sincroniza las comparativas del cambio 9999009 -> 101002.
-- Prestaciones Medicas: requerimientos activos de cualquier estado.
-- Conserva cantidades, precios y los demas items cotizados.
BEGIN;

WITH destinos AS (
    SELECT r.id_requerimiento,
           min(d.id_prestacion) AS id_prestacion
    FROM compras.requerimiento r
    JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector
    JOIN compras.requerimiento_detalle d
      ON d.id_requerimiento = r.id_requerimiento
    WHERE compras.normalizar_sector(s.descripcion) = 'PRESTACIONES MEDICAS'
      AND r.baja_fecha IS NULL
      AND d.baja_fecha IS NULL
      AND d.tipo_item = 'NOMENCLADOR'
      AND btrim(d.codigo_nomenclador) = '101002'
    GROUP BY r.id_requerimiento
    HAVING count(DISTINCT d.id_prestacion) = 1
)
UPDATE compras.requerimiento_comparativa_detalle cd
SET id_prestacion = destino.id_prestacion,
    modi_fecha = now(),
    modi_usr = current_user
FROM compras.requerimiento_comparativa c
JOIN destinos destino ON destino.id_requerimiento = c.id_requerimiento
WHERE cd.id_comparativa = c.id_comparativa
  AND cd.id_prestacion = 10609
  AND NOT EXISTS (
      SELECT 1
      FROM compras.requerimiento_detalle d
      WHERE d.id_requerimiento = c.id_requerimiento
        AND d.baja_fecha IS NULL
        AND d.id_prestacion = cd.id_prestacion
  )
RETURNING c.id_requerimiento,
          c.id_prestador,
          cd.id_comparativa,
          cd.id_detalle,
          cd.id_prestacion,
          cd.cantidad,
          cd.importe_unitario;

COMMIT;
