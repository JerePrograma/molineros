-- Reemplaza 9999009 por 101002 en Prestaciones Medicas.
-- Incluye requerimientos activos de cualquier estado.
-- Conserva cantidad, rubro, importes y los demas detalles.
BEGIN;

WITH nomenclador_destino AS (
    SELECT n.id_prestacion,
           n.id_tipo_nomenclador,
           btrim(n.codigo) AS codigo,
           btrim(n.descripcion) AS descripcion,
           count(*) OVER () AS coincidencias
    FROM autorizaciones.nomenclador n
    WHERE btrim(n.codigo) = '101002'
      AND n.baja_fecha IS NULL
)
UPDATE compras.requerimiento_detalle d
SET id_prestacion           = n.id_prestacion,
    id_tipo_nomenclador     = n.id_tipo_nomenclador,
    codigo_nomenclador      = n.codigo,
    descripcion_nomenclador = n.descripcion,
    modi_fecha             = now(),
    modi_usr               = current_user
FROM nomenclador_destino n,
     compras.requerimiento r
     JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector
WHERE d.id_requerimiento = r.id_requerimiento
  AND compras.normalizar_sector(s.descripcion) = 'PRESTACIONES MEDICAS'
  AND r.baja_fecha IS NULL
  AND d.tipo_item = 'NOMENCLADOR'
  AND d.baja_fecha IS NULL
  AND btrim(d.codigo_nomenclador) = '9999009'
  AND n.coincidencias = 1
RETURNING d.id_requerimiento,
          d.id_detalle,
          d.id_tipo_prestacion,
          d.id_prestacion,
          d.id_tipo_nomenclador,
          d.codigo_nomenclador,
          d.descripcion_nomenclador,
          d.cantidad;

COMMIT;
