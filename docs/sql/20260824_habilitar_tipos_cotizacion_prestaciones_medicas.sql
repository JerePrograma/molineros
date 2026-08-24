-- Completa el catalogo de tipos de cotizacion en una base Compras existente.
-- La version entregable se codifica en ISO-8859-1 y debe ejecutarse con psql:
-- psql -X -v ON_ERROR_STOP=1 -f 20260824_habilitar_tipos_cotizacion_prestaciones_medicas.sql

BEGIN;

LOCK TABLE compras.sector_requerimiento IN SHARE MODE;
LOCK TABLE compras.tipo_prestacion IN SHARE ROW EXCLUSIVE MODE;

DO $migration$
DECLARE
    v_id_sector INTEGER;
    v_cantidad_sectores INTEGER;
    v_cantidad_tipos INTEGER;
BEGIN
    SELECT
        min(s.id_sector),
        count(*)
    INTO
        v_id_sector,
        v_cantidad_sectores
    FROM compras.sector_requerimiento s
    WHERE translate(
              upper(btrim(s.descripcion)),
              U&'\00C1\00C9\00CD\00D3\00DA\00DC\00C0\00C8\00CC\00D2\00D9',
              'AEIOUUAEIOU'
          ) = 'PRESTACIONES MEDICAS'
      AND s.activo = TRUE
      AND s.baja_fecha IS NULL;

    IF v_cantidad_sectores <> 1 THEN
        RAISE EXCEPTION
            'Se esperaba un unico sector activo PRESTACIONES MEDICAS y se encontraron %.',
            v_cantidad_sectores;
    END IF;

    INSERT INTO compras.tipo_prestacion (
        id_tipo_prestacion,
        descripcion,
        id_sector
    )
    VALUES
        (3, U&'Pr\00F3tesis Traumatolog\00EDa', v_id_sector),
        (4, U&'Pr\00F3tesis Cardiolog\00EDa', v_id_sector),
        (5, U&'Pr\00F3tesis General', v_id_sector),
        (6, 'Insumos', v_id_sector),
        (7, U&'Pa\00F1ales', v_id_sector)
    ON CONFLICT (id_tipo_prestacion)
    DO UPDATE
    SET descripcion = EXCLUDED.descripcion,
        id_sector = EXCLUDED.id_sector;

    SELECT count(*)
    INTO v_cantidad_tipos
    FROM compras.tipo_prestacion t
    WHERE t.id_sector = v_id_sector
      AND t.id_tipo_prestacion IN (3, 4, 5, 6, 7);

    IF v_cantidad_tipos <> 5 THEN
        RAISE EXCEPTION
            'No se pudo completar el catalogo de tipos para PRESTACIONES MEDICAS.';
    END IF;
END;
$migration$;

COMMIT;
