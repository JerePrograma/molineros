\set ON_ERROR_STOP on
\encoding LATIN1
-- Prueba aislada: nunca ejecutar contra una base del portal.
-- Fixture y ASSERT adaptados de items_historicos_afiliado_test.sql.
DO $test$
BEGIN
    IF current_database() NOT LIKE 'compras_gastos_medicas_test_%' THEN
        RAISE EXCEPTION 'Esta prueba requiere una base aislada compras_gastos_medicas_test_*.';
    END IF;
    IF EXISTS (SELECT 1 FROM pg_namespace WHERE nspname IN ('compras', 'autorizaciones')) THEN
        RAISE EXCEPTION 'La base de prueba debe estar vacia.';
    END IF;
END;
$test$;

CREATE SCHEMA compras;
CREATE SCHEMA autorizaciones;
CREATE TABLE autorizaciones.nomenclador (
    id_prestacion INTEGER PRIMARY KEY,
    id_tipo_nomenclador INTEGER NOT NULL,
    codigo VARCHAR(100),
    descripcion VARCHAR(500),
    baja_fecha TIMESTAMP WITHOUT TIME ZONE
);
CREATE TABLE compras.sector_requerimiento (
    id_sector INTEGER PRIMARY KEY,
    descripcion VARCHAR(200)
);
CREATE TABLE compras.requerimiento (
    id_requerimiento INTEGER PRIMARY KEY,
    id_sector INTEGER REFERENCES compras.sector_requerimiento,
    estado INTEGER NOT NULL,
    alta_usr VARCHAR(100) NOT NULL,
    baja_fecha TIMESTAMP WITHOUT TIME ZONE
);
CREATE TABLE compras.requerimiento_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_requerimiento INTEGER NOT NULL REFERENCES compras.requerimiento,
    tipo_item VARCHAR(20),
    id_prestacion INTEGER REFERENCES autorizaciones.nomenclador,
    id_tipo_nomenclador INTEGER,
    codigo_nomenclador VARCHAR(100),
    descripcion_nomenclador VARCHAR(500),
    id_tipo_prestacion INTEGER,
    cantidad INTEGER,
    precio_unitario_estimado NUMERIC(18,2),
    precio_total_estimado NUMERIC(18,2),
    id_prestador INTEGER,
    observaciones TEXT,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100),
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),
    baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    baja_usr VARCHAR(100)
);
CREATE UNIQUE INDEX uq_fixture_detalle_prestacion
    ON compras.requerimiento_detalle (id_requerimiento, id_prestacion)
    WHERE baja_fecha IS NULL AND id_prestacion IS NOT NULL;
CREATE TABLE compras.requerimiento_comparativa (
    id_comparativa INTEGER PRIMARY KEY,
    id_requerimiento INTEGER NOT NULL REFERENCES compras.requerimiento,
    id_prestador INTEGER NOT NULL
);
CREATE TABLE compras.requerimiento_comparativa_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_comparativa INTEGER NOT NULL REFERENCES compras.requerimiento_comparativa,
    id_prestacion INTEGER NOT NULL REFERENCES autorizaciones.nomenclador,
    cantidad INTEGER,
    importe_unitario NUMERIC(18,2),
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100),
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),
    CONSTRAINT uq_compras_comparativa_prestacion UNIQUE (id_comparativa, id_prestacion)
);

-- Catalogo sintetico: destino con otro tipo y descripcion, mismo codigo de gasto.
INSERT INTO autorizaciones.nomenclador VALUES
    (10415, 3, '101002', 'Gasto incorrecto de otro concepto', NULL),
    (11503, 15, ' 101002 ', ' Gasto administrativo medico correcto ', NULL),
    (10609, 9, '9999009', 'Gastos de envio de drogueria', NULL),
    (20000, 10, '700001', 'Otro item conservado', NULL);
INSERT INTO compras.sector_requerimiento VALUES
    (1, U&'  Prestaciones M\00E9dicas  '),
    (2, 'prestaciones medicas'),
    (3, ' SISTEMAS '),
    (4, 'RRHH'),
    (5, 'OTROS'),
    (6, 'FARMACIA');

-- Instalar la revision que contiene el defecto antes de probar la correccion.
\ir ../../docs/sql/compras/20261022_corregir_envios_por_sector.sql
INSERT INTO compras.requerimiento VALUES
    (1, 1, 1, 'fixture', NULL),
    (2, 2, 2, 'fixture', NULL),
    (3, 3, 1, 'fixture', NULL),
    (4, 4, 1, 'fixture', NULL),
    (5, 5, 1, 'fixture', NULL),
    (6, 6, 1, 'fixture', NULL),
    (7, 1, 3, 'fixture', NULL),
    (8, 1, 4, 'fixture', NULL),
    (9, 1, 6, 'fixture', NULL),
    (10, 1, 7, 'fixture', NULL),
    (11, 1, 7, 'fixture', '2026-10-20');
DO $test$
BEGIN
    ASSERT (SELECT id_prestacion = 10415 AND id_tipo_nomenclador = 3
                   AND codigo_nomenclador = '101002'
                   AND descripcion_nomenclador = 'Gasto incorrecto de otro concepto'
              FROM compras.requerimiento_detalle WHERE id_requerimiento = 1),
        'El trigger anterior debe reproducir la insercion incorrecta de 10415.';
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_detalle
                       WHERE id_requerimiento IN (3, 4)),
        'El fixture debe reproducir la exclusion vigente de Sistemas y RRHH.';
    ASSERT (SELECT count(*) FROM compras.requerimiento_detalle
             WHERE id_requerimiento IN (5, 6) AND id_prestacion = 10609) = 2,
        'Los sectores restantes deben usar el gasto de drogueria anterior.';
END;
$test$;
SELECT 'gastos_envio_prestaciones_medicas: DEFECTO_10415_REPRODUCIDO' AS resultado;
-- FIN_BASELINE_10415: la migracion nueva se instala despues de este marcador.

-- Otros sectores y bajas tambien deben incluirse en el reemplazo de todos10415.
INSERT INTO compras.requerimiento_detalle (
    id_requerimiento, tipo_item, id_prestacion, id_tipo_nomenclador,
    codigo_nomenclador, descripcion_nomenclador, cantidad, alta_usr
)
SELECT r.id_requerimiento, 'NOMENCLADOR', n.id_prestacion,
       n.id_tipo_nomenclador, n.codigo, n.descripcion, 1, 'fixture'
  FROM compras.requerimiento r CROSS JOIN autorizaciones.nomenclador n
 WHERE r.id_requerimiento IN (3, 4, 5) AND n.id_prestacion = 10415;
INSERT INTO compras.requerimiento_detalle (
    id_requerimiento, tipo_item, id_prestacion, id_tipo_nomenclador,
    codigo_nomenclador, descripcion_nomenclador, cantidad, alta_usr,
    baja_fecha, baja_usr
)
SELECT 2, 'NOMENCLADOR', n.id_prestacion, n.id_tipo_nomenclador,
       n.codigo, 'Snapshot historico dado de baja', 2, 'fixture_baja',
       TIMESTAMP '2026-10-20', 'fixture_baja'
  FROM autorizaciones.nomenclador n WHERE n.id_prestacion = 10415;
INSERT INTO compras.requerimiento_detalle (
    id_requerimiento, tipo_item, id_prestacion, id_tipo_nomenclador,
    codigo_nomenclador, descripcion_nomenclador, cantidad, alta_usr
)
SELECT r.id_requerimiento, 'NOMENCLADOR', n.id_prestacion,
       n.id_tipo_nomenclador, n.codigo, n.descripcion, 4, 'fixture_otro'
  FROM compras.requerimiento r CROSS JOIN autorizaciones.nomenclador n
 WHERE r.id_requerimiento IN (1, 6) AND n.id_prestacion = 20000;
UPDATE compras.requerimiento_detalle
   SET id_tipo_prestacion = 8, cantidad = 3,
       precio_unitario_estimado = 12.34, precio_total_estimado = 37.02,
       id_prestador = 50, observaciones = 'Observacion original conservada',
       alta_fecha = TIMESTAMP '2026-09-01 10:00:00',
       modi_fecha = TIMESTAMP '2026-09-02 11:00:00', modi_usr = 'fixture_original';
UPDATE compras.requerimiento_detalle
   SET baja_fecha = TIMESTAMP '2026-10-20', baja_usr = 'fixture_baja'
 WHERE id_requerimiento = 11;
INSERT INTO compras.requerimiento_comparativa VALUES
    (1, 1, 50), (2, 2, 51), (3, 5, 52), (4, 11, 53);
INSERT INTO compras.requerimiento_comparativa_detalle (
    id_comparativa, id_prestacion, cantidad, importe_unitario,
    alta_fecha, alta_usr, modi_fecha, modi_usr
)
SELECT c.id_comparativa, 10415, c.id_comparativa + 1, 100.25,
       TIMESTAMP '2026-09-03 12:00:00', 'fixture_comparativa',
       TIMESTAMP '2026-09-04 13:00:00', 'fixture_original'
  FROM compras.requerimiento_comparativa c;
INSERT INTO compras.requerimiento_comparativa_detalle (
    id_comparativa, id_prestacion, cantidad, importe_unitario,
    alta_fecha, alta_usr, modi_fecha, modi_usr
)
VALUES (1, 20000, 4, 222.50, TIMESTAMP '2026-09-03 12:00:00',
        'fixture_otro', TIMESTAMP '2026-09-04 13:00:00', 'fixture_original');

-- Mantener el control real de duplicados habilitado durante la migracion.
\ir ../../docs/sql/compras/20261022_impedir_prestaciones_codigo_repetido.sql
CREATE TEMP TABLE cabeceras_antes_gastos AS TABLE compras.requerimiento;
CREATE TEMP TABLE detalles_antes_gastos AS TABLE compras.requerimiento_detalle;
CREATE TEMP TABLE comparativas_antes_gastos AS TABLE compras.requerimiento_comparativa;
CREATE TEMP TABLE cotizados_antes_gastos AS TABLE compras.requerimiento_comparativa_detalle;

\ir ../../docs/sql/compras/20261030_corregir_gastos_prestaciones_medicas_11503.sql
DO $test$
BEGIN
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_detalle WHERE id_prestacion = 10415),
        'No debe quedar ningun10415 en detalles, incluidos otros sectores, estados y bajas.';
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_comparativa_detalle WHERE id_prestacion = 10415),
        'No debe quedar ningun10415 en comparativas.';
    ASSERT (SELECT count(*) FROM compras.requerimiento_detalle) =
           (SELECT count(*) FROM detalles_antes_gastos),
        'El reemplazo no debe crear ni eliminar detalles.';
    ASSERT (SELECT count(*) FROM compras.requerimiento_comparativa_detalle) =
           (SELECT count(*) FROM cotizados_antes_gastos),
        'El reemplazo no debe crear ni eliminar cotizaciones.';
    ASSERT NOT EXISTS (
        SELECT 1 FROM detalles_antes_gastos a
        LEFT JOIN compras.requerimiento_detalle d USING (id_detalle)
        WHERE a.id_prestacion = 10415
          AND (d.id_detalle IS NULL OR d.id_prestacion IS DISTINCT FROM 11503
               OR d.id_tipo_nomenclador IS DISTINCT FROM 15
               OR d.codigo_nomenclador IS DISTINCT FROM '101002'
               OR d.descripcion_nomenclador IS DISTINCT FROM 'Gasto administrativo medico correcto'
               OR d.modi_usr IS DISTINCT FROM current_user::TEXT
               OR d.modi_fecha IS NULL OR d.modi_fecha IS NOT DISTINCT FROM a.modi_fecha)
    ), 'Cada10415 debe conservar su ID y recibir los datos canonicos completos de11503 y auditoria.';
    ASSERT NOT EXISTS (
        SELECT 1 FROM detalles_antes_gastos a
        JOIN compras.requerimiento_detalle d USING (id_detalle)
        WHERE (to_jsonb(d)-'id_prestacion'-'id_tipo_nomenclador'-'codigo_nomenclador'
                -'descripcion_nomenclador'-'modi_fecha'-'modi_usr')
           IS DISTINCT FROM
              (to_jsonb(a)-'id_prestacion'-'id_tipo_nomenclador'-'codigo_nomenclador'
                -'descripcion_nomenclador'-'modi_fecha'-'modi_usr')
    ), 'Debe conservar rubro, cantidad, precios, prestador, observaciones, altas y bajas.';
    ASSERT NOT EXISTS (
        SELECT 1 FROM detalles_antes_gastos a
        JOIN compras.requerimiento_detalle d USING (id_detalle)
        WHERE a.id_prestacion <> 10415 AND to_jsonb(d) IS DISTINCT FROM to_jsonb(a)
    ), 'Los demas detalles y su auditoria deben quedar intactos.';
    ASSERT NOT EXISTS (
        SELECT 1 FROM cotizados_antes_gastos a
        LEFT JOIN compras.requerimiento_comparativa_detalle cd USING (id_detalle)
        WHERE a.id_prestacion = 10415
          AND (cd.id_detalle IS NULL OR cd.id_prestacion IS DISTINCT FROM 11503
               OR cd.modi_usr IS DISTINCT FROM current_user::TEXT
               OR cd.modi_fecha IS NULL OR cd.modi_fecha IS NOT DISTINCT FROM a.modi_fecha)
    ), 'Las comparativas deben conservar su ID y apuntar a11503 con auditoria.';
    ASSERT NOT EXISTS (
        SELECT 1 FROM cotizados_antes_gastos a
        JOIN compras.requerimiento_comparativa_detalle cd USING (id_detalle)
        WHERE (to_jsonb(cd)-'id_prestacion'-'modi_fecha'-'modi_usr')
           IS DISTINCT FROM (to_jsonb(a)-'id_prestacion'-'modi_fecha'-'modi_usr')
    ), 'Debe conservar la cantidad, el importe y el alta de cada item cotizado.';
    ASSERT NOT EXISTS (
        SELECT 1 FROM cotizados_antes_gastos a
        JOIN compras.requerimiento_comparativa_detalle cd USING (id_detalle)
        WHERE a.id_prestacion <> 10415 AND to_jsonb(cd) IS DISTINCT FROM to_jsonb(a)
    ), 'Los otros items cotizados y su auditoria deben quedar intactos.';
    ASSERT NOT EXISTS (
        (SELECT * FROM compras.requerimiento EXCEPT SELECT * FROM cabeceras_antes_gastos)
        UNION ALL
        (SELECT * FROM cabeceras_antes_gastos EXCEPT SELECT * FROM compras.requerimiento)
    ), 'Las cabeceras, sus estados y bajas deben conservarse.';
    ASSERT NOT EXISTS (
        (SELECT * FROM compras.requerimiento_comparativa EXCEPT SELECT * FROM comparativas_antes_gastos)
        UNION ALL
        (SELECT * FROM comparativas_antes_gastos EXCEPT SELECT * FROM compras.requerimiento_comparativa)
    ), 'Las cabeceras de comparativa y los prestadores deben conservarse.';
    ASSERT (SELECT t.tgenabled = 'O' FROM pg_trigger t
             WHERE t.tgrelid = 'compras.requerimiento_detalle'::regclass
               AND t.tgname = 'tr_compras_detalle_codigo_prestacion'),
        'La migracion debe conservar habilitado el control de codigos duplicados.';
END;
$test$;

CREATE TEMP TABLE detalles_despues_gastos AS TABLE compras.requerimiento_detalle;
CREATE TEMP TABLE cotizados_despues_gastos AS TABLE compras.requerimiento_comparativa_detalle;
\ir ../../docs/sql/compras/20261030_corregir_gastos_prestaciones_medicas_11503.sql
DO $test$
BEGIN
    ASSERT NOT EXISTS (
        (SELECT * FROM compras.requerimiento_detalle EXCEPT SELECT * FROM detalles_despues_gastos)
        UNION ALL
        (SELECT * FROM detalles_despues_gastos EXCEPT SELECT * FROM compras.requerimiento_detalle)
    ), 'Reejecutar debe conservar todos los detalles, incluidos modi_fecha y modi_usr.';
    ASSERT NOT EXISTS (
        (SELECT * FROM compras.requerimiento_comparativa_detalle EXCEPT SELECT * FROM cotizados_despues_gastos)
        UNION ALL
        (SELECT * FROM cotizados_despues_gastos EXCEPT SELECT * FROM compras.requerimiento_comparativa_detalle)
    ), 'Reejecutar debe conservar todas las comparativas, incluida su auditoria.';
END;
$test$;

INSERT INTO compras.requerimiento VALUES
    (101, 1, 1, 'fixture_nuevo', NULL),
    (102, 2, 1, 'fixture_nuevo', NULL),
    (103, 3, 1, 'fixture_nuevo', NULL),
    (104, 4, 1, 'fixture_nuevo', NULL),
    (105, 5, 1, 'fixture_nuevo', NULL),
    (106, 6, 1, 'fixture_nuevo', NULL);
DO $test$
BEGIN
    ASSERT (SELECT count(*) FROM compras.requerimiento_detalle
             WHERE id_requerimiento IN (101, 102)) = 2,
        'El trigger nuevo debe crear exactamente un gasto por requerimiento medico.';
    ASSERT (SELECT count(*) FROM compras.requerimiento_detalle
             WHERE id_requerimiento IN (101, 102)
               AND tipo_item = 'NOMENCLADOR' AND id_prestacion = 11503
               AND id_tipo_nomenclador = 15 AND codigo_nomenclador = '101002'
               AND descripcion_nomenclador = 'Gasto administrativo medico correcto'
               AND cantidad = 1 AND alta_usr = 'fixture_nuevo') = 2,
        'Sectores medicos normalizados deben usar11503 con metadatos canonicos y cantidad1.';
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_detalle
                       WHERE id_requerimiento IN (103, 104)),
        'El trigger debe seguir omitiendo Sistemas y RRHH.';
    ASSERT (SELECT count(*) FROM compras.requerimiento_detalle
             WHERE id_requerimiento IN (105, 106) AND id_prestacion = 10609
               AND id_tipo_nomenclador = 9 AND codigo_nomenclador = '9999009'
               AND cantidad = 1 AND alta_usr = 'fixture_nuevo') = 2,
        'Los demas sectores deben conservar el gasto de drogueria10609.';
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_detalle WHERE id_prestacion = 10415),
        'Ningun nuevo requerimiento debe reintroducir10415.';
END;
$test$;
SELECT 'gastos_envio_prestaciones_medicas: OK' AS resultado;
