-- EJEMPLO OPERATIVO SEPARADO, NO EJECUTADO POR CODEX.
-- PostgreSQL 9.6. ISO-8859-1 sin BOM.
-- SOLO ejecutar despues de confirmar funcionalmente que INSUMOS DIABETES
-- debe buscar y guardar exclusivamente el nomenclador 10. Su nombre no
-- implica esa regla. Este cambio NO forma parte de la migracion compatible.
-- No modifica compras.tipo_prestacion ni public.prestador_rubro.
-- El ID se resuelve de nuevo; NO supone que sea 6 ni el 8 observado el 13/10.

-- 1. Resolver el ID real y verificar el sector antes de configurar.
SELECT t.id_tipo_prestacion, t.descripcion, t.id_sector,
       s.descripcion AS sector, s.activo, s.baja_fecha,
       CASE WHEN EXISTS (
           SELECT 1 FROM compras.sector_nomenclador c
           WHERE c.id_sector = t.id_sector
             AND c.id_tipo_prestacion = t.id_tipo_prestacion
       ) THEN 'ESPECIFICA'
         WHEN EXISTS (
           SELECT 1 FROM compras.sector_nomenclador c
           WHERE c.id_sector = t.id_sector AND c.id_tipo_prestacion IS NULL
       ) THEN 'GENERAL HEREDADA' ELSE 'SIN CONFIGURACION' END AS origen
FROM compras.tipo_prestacion t
JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector
WHERE compras.normalizar_sector(t.descripcion) = 'INSUMOS DIABETES';

-- 2. Cambio de negocio explicito: reemplazar SU conjunto especifico por 10.
BEGIN;
DO $configurar_diabetes$
DECLARE
    v_rubro SMALLINT;
    v_sector INTEGER;
    v_cantidad INTEGER;
BEGIN
    SELECT count(*), min(t.id_tipo_prestacion), min(t.id_sector)
    INTO v_cantidad, v_rubro, v_sector
    FROM compras.tipo_prestacion t
    JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector
    WHERE compras.normalizar_sector(t.descripcion) = 'INSUMOS DIABETES'
      AND compras.normalizar_sector(s.descripcion) = 'PRESTACIONES MEDICAS'
      AND s.activo = TRUE AND s.baja_fecha IS NULL;
    IF v_cantidad <> 1 THEN
        RAISE EXCEPTION 'Se requiere un unico rubro INSUMOS DIABETES perteneciente al sector activo PRESTACIONES MEDICAS; encontrados: %.', v_cantidad;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM public.tipo_nomenclador WHERE id_tipo_nomenclador = 10) THEN
        RAISE EXCEPTION 'No existe el tipo real de nomenclador 10 en el catalogo.';
    END IF;
    -- Elimina solo asociaciones propias de ESTE rubro. No modifica la
    -- configuracion general, otros rubros ni prestaciones historicas.
    DELETE FROM compras.sector_nomenclador
    WHERE id_sector = v_sector AND id_tipo_prestacion = v_rubro;
    INSERT INTO compras.sector_nomenclador
        (id_sector, id_tipo_prestacion, id_tipo_nomenclador)
    VALUES (v_sector, v_rubro, 10);
    IF (SELECT count(*) FROM compras.resolver_sector_nomenclador(v_sector, v_rubro)) <> 1 THEN
        RAISE EXCEPTION 'No quedo una unica asociacion efectiva para INSUMOS DIABETES.';
    END IF;
END;
$configurar_diabetes$;
COMMIT;

-- 3. Confirmar el conjunto efectivo: solo 10, sin sumar el general.
SELECT t.id_tipo_prestacion, t.descripcion, s.id_sector,
       s.descripcion AS sector, 'ESPECIFICA'::VARCHAR AS origen,
       c.id_tipo_nomenclador, tn.descripcion AS nomenclador,
       c.marca_rein_liq, c.codigo_nomenclador
FROM compras.tipo_prestacion t
JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector
CROSS JOIN LATERAL compras.resolver_sector_nomenclador(s.id_sector, t.id_tipo_prestacion) c
LEFT JOIN public.tipo_nomenclador tn ON tn.id_tipo_nomenclador = c.id_tipo_nomenclador
WHERE compras.normalizar_sector(t.descripcion) = 'INSUMOS DIABETES'
  AND compras.normalizar_sector(s.descripcion) = 'PRESTACIONES MEDICAS'
  AND s.activo = TRUE AND s.baja_fecha IS NULL;

-- Quitar la ultima asociacion especifica volveria a heredar las generales
-- del sector, si existen. No equivale a bloquear totalmente este rubro.
