\set ON_ERROR_STOP on
\encoding LATIN1
-- Ejecutar solo en una base vacia compras_historicos_test_*.
DO $test$
BEGIN
    IF current_database() NOT LIKE 'compras_historicos_test_%' THEN
        RAISE EXCEPTION 'Esta prueba requiere una base aislada compras_historicos_test_*.';
    END IF;
    IF EXISTS (SELECT 1 FROM pg_namespace WHERE nspname = 'compras') THEN
        RAISE EXCEPTION 'La base de prueba debe estar vacia.';
    END IF;
END;
$test$;

CREATE SCHEMA compras;
CREATE TABLE compras.requerimiento (
    id_requerimiento INTEGER PRIMARY KEY,
    afiliado_cuil_titular VARCHAR(11),
    afiliado_int INTEGER,
    id_sector INTEGER,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE,
    baja_fecha TIMESTAMP WITHOUT TIME ZONE
);
CREATE TABLE compras.requerimiento_detalle (
    id_detalle INTEGER PRIMARY KEY,
    id_requerimiento INTEGER REFERENCES compras.requerimiento,
    tipo_item VARCHAR(20),
    id_prestacion INTEGER,
    id_tipo_nomenclador INTEGER,
    codigo_nomenclador VARCHAR(40),
    descripcion_nomenclador VARCHAR(120),
    id_tipo_prestacion INTEGER,
    baja_fecha TIMESTAMP WITHOUT TIME ZONE
);

-- Datos sinteticos: principal, duplicado reciente, gastos y casos aislados.
INSERT INTO compras.requerimiento VALUES
    (1, '00000000001', 0, 1, '2026-01-01', NULL),
    (2, '00000000001', 0, 1, '2026-02-01', NULL),
    (3, '00000000001', 0, 1, '2026-03-01', NULL),
    (4, '00000000002', 0, 1, '2026-04-01', NULL),
    (5, '00000000001', 1, 1, '2026-04-01', NULL),
    (6, '00000000001', 0, 2, '2026-04-01', NULL),
    (7, '00000000001', 0, 1, '2026-04-01', NULL),
    (8, '00000000001', 0, 1, '2026-04-01', '2026-04-02'),
    (9, '00000000003', 0, 1, '2026-04-01', NULL);

INSERT INTO compras.requerimiento_detalle
SELECT n, 1, 'NOMENCLADOR', 1000 + n, 1,
       CASE n WHEN 21 THEN '1010020' WHEN 22 THEN '99990090'
           ELSE (700000 + n)::VARCHAR END,
       ('  Producto ' || n || '  ')::VARCHAR,
       CASE WHEN n = 1 THEN NULL ELSE 5 END, NULL
FROM generate_series(1, 22) n;
INSERT INTO compras.requerimiento_detalle VALUES
    (50, 2, 'NOMENCLADOR', 1022, 1, ' 99990090 ', ' Ultima version ', 7, NULL),
    (51, 3, 'NOMENCLADOR', 10415, 3, '101002', 'Gasto administrativo', 8, NULL),
    (52, 3, 'NOMENCLADOR', 10609, 1, ' 9999009 ', 'Gasto de envio', 8, NULL),
    (53, 3, 'NOMENCLADOR', 10416, 1, ' 101002 ', 'Gasto otro catalogo', 8, NULL),
    (54, 3, 'NOMENCLADOR', 10610, 3, '9999009', 'Envio otro catalogo', 8, NULL),
    (60, 4, 'NOMENCLADOR', 2004, 1, '800004', 'Otro titular', 5, NULL),
    (61, 5, 'NOMENCLADOR', 2005, 1, '800005', 'Otro integrante', 5, NULL),
    (62, 6, 'NOMENCLADOR', 2006, 1, '800006', 'Otro sector', 5, NULL),
    (63, 7, 'NOMENCLADOR', 2007, 1, '800007', 'Requerimiento actual', 5, NULL),
    (64, 8, 'NOMENCLADOR', 2008, 1, '800008', 'Requerimiento de baja', 5, NULL),
    (65, 1, 'NOMENCLADOR', 2009, 1, '800009', 'Detalle de baja', 5, '2026-04-02'),
    (66, 1, 'LIBRE', 2010, 1, '800010', 'Item libre', 5, NULL),
    (67, 9, 'NOMENCLADOR', 10415, 3, ' 101002 ', 'Solo gasto administrativo', 8, NULL),
    (68, 9, 'NOMENCLADOR', 10609, 1, ' 9999009 ', 'Solo gasto de envio', 8, NULL);

-- Firmas vacias que simulan el contrato instalado antes del upgrade.
CREATE FUNCTION compras.buscar_items_historicos_afiliado(
    p_cuil_titular VARCHAR, p_inte INTEGER, p_id_sector INTEGER,
    p_id_requerimiento_excluir INTEGER, p_limite INTEGER
)
RETURNS TABLE (id_prestacion INTEGER, id_tipo_nomenclador INTEGER,
    codigo VARCHAR, descripcion VARCHAR)
AS $func$
    SELECT NULL::INTEGER, NULL::INTEGER, NULL::VARCHAR, NULL::VARCHAR WHERE FALSE;
$func$ LANGUAGE sql STABLE;
CREATE FUNCTION compras.buscar_items_historicos_afiliado_clasificado(
    p_cuil_titular VARCHAR, p_inte INTEGER, p_id_sector INTEGER,
    p_id_requerimiento_excluir INTEGER, p_limite INTEGER
)
RETURNS TABLE (id_prestacion INTEGER, id_tipo_nomenclador INTEGER,
    codigo VARCHAR, descripcion VARCHAR, id_tipo_prestacion INTEGER)
AS $func$
    SELECT NULL::INTEGER, NULL::INTEGER, NULL::VARCHAR, NULL::VARCHAR,
        NULL::INTEGER WHERE FALSE;
$func$ LANGUAGE sql STABLE;

CREATE TEMP TABLE cabeceras_antes AS TABLE compras.requerimiento;
CREATE TEMP TABLE detalles_antes AS TABLE compras.requerimiento_detalle;
\ir ../../docs/sql/compras/20261026_excluir_gastos_items_historicos_afiliado.sql
\ir ../../docs/sql/compras/20261026_excluir_gastos_items_historicos_afiliado.sql

DO $test$
DECLARE
    v_esperados INTEGER[];
    v_ids INTEGER[];
BEGIN
    SELECT array_agg(n ORDER BY n DESC) INTO v_esperados
    FROM generate_series(1003, 1022) n;
    SELECT array_agg(id_prestacion) INTO v_ids
    FROM compras.buscar_items_historicos_afiliado('00000000001', 0, 1, 7, 20);
    ASSERT v_ids = v_esperados,
        'Debe excluir los gastos antes del limite y conservar los 20 mas recientes.';
    SELECT array_agg(id_prestacion) INTO v_ids
    FROM compras.buscar_items_historicos_afiliado_clasificado('00000000001', 0, 1, 7, 20);
    ASSERT v_ids = v_esperados,
        'La consulta clasificada debe conservar el mismo orden y limite.';

    ASSERT (SELECT count(*) FROM compras.buscar_items_historicos_afiliado(
        '00000000001', 0, 1, 7, 100)) = 22,
        'Debe conservar todos los validos, deduplicar y respetar persona, sector y bajas.';
    ASSERT (SELECT count(*) FROM compras.buscar_items_historicos_afiliado_clasificado(
        '00000000001', 0, 1, 7, 100)) = 22;
    ASSERT NOT EXISTS (
        SELECT * FROM compras.buscar_items_historicos_afiliado('00000000001', 0, 1, 7, 100)
        WHERE codigo IN ('101002', '9999009') OR id_prestacion NOT BETWEEN 1001 AND 1022
    ), 'Los gastos no deben sugerirse en ningun catalogo, incluso con espacios.';
    ASSERT NOT EXISTS (
        SELECT * FROM compras.buscar_items_historicos_afiliado_clasificado(
            '00000000001', 0, 1, 7, 100)
        WHERE codigo IN ('101002', '9999009') OR id_prestacion NOT BETWEEN 1001 AND 1022
    );
    ASSERT (SELECT codigo = '99990090' AND descripcion = 'Ultima version'
        FROM compras.buscar_items_historicos_afiliado('00000000001', 0, 1, 7, 100)
        WHERE id_prestacion = 1022), 'Debe usar la version mas reciente y normalizar espacios.';
    ASSERT (SELECT codigo = '99990090' AND descripcion = 'Ultima version'
                   AND id_tipo_prestacion = 7
        FROM compras.buscar_items_historicos_afiliado_clasificado('00000000001', 0, 1, 7, 100)
        WHERE id_prestacion = 1022), 'Debe preservar la clasificacion mas reciente.';
    ASSERT (SELECT id_tipo_prestacion IS NULL
        FROM compras.buscar_items_historicos_afiliado_clasificado('00000000001', 0, 1, 7, 100)
        WHERE id_prestacion = 1001), 'La clasificacion ausente debe permanecer nula.';
    ASSERT NOT EXISTS (
        SELECT * FROM compras.buscar_items_historicos_afiliado('00000000003', 0, 1, 0, 20)
    ), 'Un historial con solo gastos debe quedar vacio.';
    ASSERT NOT EXISTS (
        SELECT * FROM compras.buscar_items_historicos_afiliado_clasificado(
            '00000000003', 0, 1, 0, 20)
    );

    ASSERT NOT EXISTS (
        (SELECT * FROM compras.requerimiento EXCEPT SELECT * FROM cabeceras_antes)
        UNION ALL
        (SELECT * FROM cabeceras_antes EXCEPT SELECT * FROM compras.requerimiento)
    ), 'La migracion no debe modificar cabeceras.';
    ASSERT NOT EXISTS (
        (SELECT * FROM compras.requerimiento_detalle EXCEPT SELECT * FROM detalles_antes)
        UNION ALL
        (SELECT * FROM detalles_antes EXCEPT SELECT * FROM compras.requerimiento_detalle)
    ), 'La migracion debe conservar los gastos y todos los detalles persistidos.';
END;
$test$;
SELECT 'items_historicos_afiliado: OK' AS resultado;
