-- Consultas operativas SOLO LECTURA. PostgreSQL 9.6.
-- ISO-8859-1 sin BOM. Ejecutar despues de instalar 20261013_sector_nomenclador.sql.
-- NULL en rubro significa configuracion general; NULL en tipo de
-- nomenclador exige la marca/codigo concretos de la misma asociacion.
-- Dentro de una fila: AND. Entre filas efectivas: OR.
-- Eliminar la ultima fila especifica hace heredar la general, si existe.

BEGIN TRANSACTION READ ONLY;

-- Catalogos reales: sector, tipo de cotizacion y tipo de nomenclador.
SELECT s.id_sector, s.descripcion, s.activo, s.baja_fecha,
       t.id_tipo_prestacion, t.descripcion AS tipo_cotizacion
FROM compras.sector_requerimiento s
LEFT JOIN compras.tipo_prestacion t ON t.id_sector = s.id_sector
ORDER BY s.id_sector, t.id_tipo_prestacion;
SELECT id_tipo_nomenclador, descripcion FROM public.tipo_nomenclador
ORDER BY id_tipo_nomenclador;

-- Configuracion general explicita por sector.
SELECT s.id_sector, s.descripcion AS sector,
       c.id_tipo_nomenclador, tn.descripcion AS nomenclador,
       c.marca_rein_liq, c.codigo_nomenclador
FROM compras.sector_nomenclador c
JOIN compras.sector_requerimiento s ON s.id_sector = c.id_sector
LEFT JOIN public.tipo_nomenclador tn ON tn.id_tipo_nomenclador = c.id_tipo_nomenclador
WHERE c.id_tipo_prestacion IS NULL
ORDER BY s.id_sector, c.id_tipo_nomenclador, c.marca_rein_liq, c.codigo_nomenclador;

-- Configuracion especifica, incluido control de pertenencia del rubro.
SELECT s.id_sector, s.descripcion AS sector,
       t.id_tipo_prestacion, t.descripcion AS tipo_cotizacion,
       (t.id_sector = s.id_sector) AS rubro_pertenece_al_sector,
       c.id_tipo_nomenclador, tn.descripcion AS nomenclador,
       c.marca_rein_liq, c.codigo_nomenclador
FROM compras.sector_nomenclador c
JOIN compras.sector_requerimiento s ON s.id_sector = c.id_sector
JOIN compras.tipo_prestacion t ON t.id_tipo_prestacion = c.id_tipo_prestacion
LEFT JOIN public.tipo_nomenclador tn ON tn.id_tipo_nomenclador = c.id_tipo_nomenclador
WHERE c.id_tipo_prestacion IS NOT NULL
ORDER BY s.id_sector, t.id_tipo_prestacion, c.id_tipo_nomenclador,
         c.marca_rein_liq, c.codigo_nomenclador;

-- Que utiliza cada rubro: asociaciones propias o heredadas generales.
-- La fila sin rubro muestra el contexto general de cada sector activo.
-- LEFT JOIN conserva tambien los contextos SIN CONFIGURACION.
WITH contextos AS (
    SELECT s.id_sector, s.descripcion AS sector,
           NULL::INTEGER AS id_tipo_prestacion, NULL::VARCHAR AS tipo_cotizacion
    FROM compras.sector_requerimiento s WHERE s.activo = TRUE AND s.baja_fecha IS NULL
    UNION ALL
    SELECT s.id_sector, s.descripcion, t.id_tipo_prestacion::INTEGER, t.descripcion::VARCHAR
    FROM compras.tipo_prestacion t
    JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector
    WHERE s.activo = TRUE AND s.baja_fecha IS NULL
)
SELECT contexto.id_sector, contexto.sector,
       contexto.id_tipo_prestacion, contexto.tipo_cotizacion,
       CASE WHEN EXISTS (
           SELECT 1 FROM compras.sector_nomenclador propia
           WHERE propia.id_sector = contexto.id_sector
             AND propia.id_tipo_prestacion = contexto.id_tipo_prestacion
       ) THEN 'ESPECIFICA'
         WHEN EXISTS (
           SELECT 1 FROM compras.sector_nomenclador general
           WHERE general.id_sector = contexto.id_sector AND general.id_tipo_prestacion IS NULL
       ) THEN 'GENERAL' ELSE 'SIN CONFIGURACION' END AS origen,
       efectiva.id_tipo_nomenclador, tn.descripcion AS nomenclador,
       efectiva.marca_rein_liq, efectiva.codigo_nomenclador
FROM contextos contexto
LEFT JOIN LATERAL compras.resolver_sector_nomenclador(
    contexto.id_sector, contexto.id_tipo_prestacion
) efectiva ON TRUE
LEFT JOIN public.tipo_nomenclador tn ON tn.id_tipo_nomenclador = efectiva.id_tipo_nomenclador
ORDER BY contexto.id_sector, contexto.id_tipo_prestacion,
         efectiva.id_tipo_nomenclador, efectiva.marca_rein_liq, efectiva.codigo_nomenclador;

ROLLBACK;
