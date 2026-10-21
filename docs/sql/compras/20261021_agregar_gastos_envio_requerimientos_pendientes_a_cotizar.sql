-- Agrega Gastos de envio de drogueria a los requerimientos activos
-- PENDIENTE (1) y A COTIZAR (2). Cantidad inicial fija: 1.
-- Solo incorpora el item si no existe un detalle activo de esa prestacion.
BEGIN;

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
    r.id_requerimiento,
    'NOMENCLADOR',
    n.id_prestacion,
    n.id_tipo_nomenclador,
    n.codigo,
    n.descripcion,
    1,
    CURRENT_USER
FROM compras.requerimiento r
JOIN autorizaciones.nomenclador n ON n.id_prestacion = 10609
WHERE r.estado IN (1, 2)
  AND r.baja_fecha IS NULL
  AND NOT EXISTS (
      SELECT 1
      FROM compras.requerimiento_detalle d
      WHERE d.id_requerimiento = r.id_requerimiento
        AND d.id_prestacion = n.id_prestacion
        AND d.baja_fecha IS NULL
  );

COMMIT;
