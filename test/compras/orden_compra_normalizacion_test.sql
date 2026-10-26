\set ON_ERROR_STOP on
-- Fixture inicial extraido de orden_compra_test.sql por el runner.
-- Solo bases vacias compras_orden_test_edicion_* preparadas para esta prueba.
DO $test$
BEGIN
    IF current_database() NOT LIKE 'compras_orden_test_edicion_%'
       OR to_regclass('compras.requerimiento_orden_compra') IS NOT NULL THEN
        RAISE EXCEPTION 'La normalizacion requiere el fixture aislado inicial sin tablas OC.';
    END IF;
END;
$test$;
CREATE TABLE public.prestador (id_prestador INTEGER PRIMARY KEY, descripcion VARCHAR(250), cuit VARCHAR(20));
INSERT INTO public.prestador VALUES (50, 'Prestador Fixture', '30700000001'), (51, 'Otro Prestador', '30700000002');
ALTER TABLE compras.requerimiento_presupuesto
    ADD COLUMN id_requerimiento_presupuesto SERIAL PRIMARY KEY,
    ADD COLUMN descripcion_prestador VARCHAR(250),
    ADD COLUMN fecha_documento DATE;
UPDATE compras.requerimiento_presupuesto rp SET descripcion_prestador = p.descripcion
  FROM public.prestador p WHERE p.id_prestador = rp.id_prestador;
ALTER TABLE compras.requerimiento_detalle
    ADD COLUMN tipo_item VARCHAR(20), ADD COLUMN id_prestacion INTEGER,
    ADD COLUMN id_tipo_nomenclador INTEGER, ADD COLUMN codigo_nomenclador VARCHAR(100),
    ADD COLUMN descripcion_nomenclador VARCHAR(1000), ADD COLUMN id_medicamento INTEGER,
    ADD COLUMN troquel INTEGER, ADD COLUMN nombre_medicamento VARCHAR(1000), ADD COLUMN observaciones TEXT;
UPDATE compras.requerimiento_detalle
   SET tipo_item = CASE WHEN id_prestador IS NULL THEN 'OBSERVACION' ELSE 'NOMENCLADOR' END,
       observaciones = CASE WHEN id_prestador IS NULL THEN 'Equipo de prueba' ELSE NULL END,
       id_prestacion = CASE WHEN id_prestador IS NOT NULL THEN id_detalle ELSE NULL END,
       id_tipo_nomenclador = CASE WHEN id_prestador IS NOT NULL THEN 10 ELSE NULL END,
       codigo_nomenclador = CASE WHEN id_prestador IS NOT NULL THEN 'MED' || id_detalle ELSE NULL END,
       descripcion_nomenclador = CASE WHEN id_prestador IS NOT NULL THEN 'Prestacion de prueba' ELSE NULL END;
UPDATE compras.requerimiento_detalle SET observaciones = 'Equipo anterior' WHERE id_detalle = 2001;
-- El mapper Java normaliza el codigo; la vista SQL debe conservar ese mismo contrato.
UPDATE compras.requerimiento_detalle SET codigo_nomenclador = '  MED2002  ' WHERE id_detalle = 2002;
INSERT INTO compras.requerimiento (id_requerimiento, estado, id_sector, alta_usr)
VALUES (53, 7, 1, 'solicitante_fixture'), (54, 7, 1, 'solicitante_fixture');
INSERT INTO compras.requerimiento_detalle (id_detalle,id_requerimiento,cantidad,tipo_item,observaciones)
VALUES (2053,53,1,'OBSERVACION','CELULAR PRUEBA'), (2054,54,1,'OBSERVACION','Rollback de prueba');
INSERT INTO compras.requerimiento_presupuesto
    (id_requerimiento_presupuesto,id_requerimiento,tipo_documento,empresa_adjudicada,descripcion_empresa,empresa_cuit)
VALUES (53,53,3,TRUE,'LA PRUEBA','30700000001'), (54,54,3,TRUE,'Proveedor rollback',NULL);
\ir ../../docs/sql/compras/20261023_orden_compra_formulario.sql
\ir ../../docs/sql/compras/20261026_orden_compra_edicion_condiciones.sql
-- Reproducir el contrato instalado anterior, sin suponer aplicada la nulabilidad23.
ALTER TABLE compras.requerimiento_orden_compra
    ALTER COLUMN moneda SET NOT NULL,
    ALTER COLUMN iva SET NOT NULL, ALTER COLUMN iva SET DEFAULT 0,
    ALTER COLUMN iibb SET NOT NULL, ALTER COLUMN iibb SET DEFAULT 0;
ALTER TABLE compras.requerimiento_orden_compra_detalle
    ALTER COLUMN precio_unitario SET NOT NULL, ALTER COLUMN precio_total SET NOT NULL;
INSERT INTO compras.requerimiento_orden_compra
    (id_requerimiento,fecha_emision,moneda,proveedor,confeccionado_por,alta_usr,iva,iibb,observaciones)
VALUES (1001,DATE '2026-10-23','ARS','Empresa Fixture','fixture','fixture',4.20,0.80,'Historica conservada');
INSERT INTO compras.requerimiento_orden_compra_detalle
    (id_orden_compra,orden,id_requerimiento_detalle,descripcion_item,cantidad,precio_unitario,precio_total)
SELECT id_orden_compra,1,2001,'Equipo anterior',2,10,20 FROM compras.requerimiento_orden_compra WHERE id_requerimiento=1001;
SELECT compras.confirmar_orden_compra_requerimiento(1001,'fixture');
INSERT INTO compras.requerimiento_orden_compra
    (id_requerimiento,fecha_emision,moneda,proveedor,proveedor_cuit,confeccionado_por,alta_usr,id_reclamo_prestacional,iva,iibb)
VALUES (1002,DATE '2026-10-23','ARS','Prestador Fixture','30700000001','fixture','fixture',3002,0,0);
INSERT INTO compras.requerimiento_orden_compra_detalle
    (id_orden_compra,orden,id_requerimiento_detalle,codigo_item,descripcion_item,cantidad,precio_unitario,precio_total)
SELECT id_orden_compra,1,2002,'MED2002','Prestacion de prueba',2,12.34,24.68 FROM compras.requerimiento_orden_compra WHERE id_requerimiento=1002;
SELECT compras.confirmar_orden_compra_requerimiento(1002,'fixture');
CREATE TRIGGER trg_compras_requerimiento_validar BEFORE INSERT OR UPDATE ON compras.requerimiento
FOR EACH ROW EXECUTE PROCEDURE compras.validar_requerimiento_fila();
DO $rol_fixture$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname='oc_normalizacion_lectura') THEN
        CREATE ROLE oc_normalizacion_lectura NOLOGIN;
    END IF;
END;
$rol_fixture$;
GRANT USAGE ON SCHEMA compras TO oc_normalizacion_lectura;
GRANT SELECT ON compras.requerimiento_orden_compra TO oc_normalizacion_lectura WITH GRANT OPTION;
GRANT SELECT ON compras.requerimiento_orden_compra_detalle TO oc_normalizacion_lectura;
CREATE TEMP TABLE cabeceras_oc_antes_normalizar AS
SELECT id_orden_compra, to_jsonb(o)-'fecha_orden_medica'-'fecha_recepcion'-'moneda'-'proveedor'-'proveedor_cuit'
    -'proveedor_domicilio'-'proveedor_contacto'-'proveedor_email'-'proveedor_telefono'-'confeccionado_por'
    -'telefono_compras'-'solicitante'-'plan'-'id_reclamo_prestacional' AS datos
  FROM compras.requerimiento_orden_compra o;
CREATE TEMP TABLE detalles_oc_antes_normalizar AS
SELECT id_orden_compra,orden,id_requerimiento_detalle,cantidad,precio_unitario,precio_total
  FROM compras.requerimiento_orden_compra_detalle;
DO $test$
BEGIN
    ASSERT (SELECT is_nullable FROM information_schema.columns WHERE table_schema='compras'
            AND table_name='requerimiento_orden_compra' AND column_name='moneda')='NO';
    ASSERT (SELECT precio_unitario_estimado IS NULL FROM compras.requerimiento_detalle WHERE id_detalle=2001),
           'La prueba debe conservar una OC manual anterior que difiere de los importes actuales del requerimiento.';
END;
$test$;
\ir ../../docs/sql/compras/20261026_normalizar_orden_compra.sql
\ir ../../docs/sql/compras/20261026_normalizar_orden_compra.sql
DO $test$
BEGIN
    ASSERT (SELECT count(*) FROM information_schema.columns WHERE table_schema='compras'
            AND table_name='requerimiento_orden_compra')=16, 'La cabecera debe guardar solo16campos propios/referencias.';
    ASSERT (SELECT count(*) FROM information_schema.columns WHERE table_schema='compras'
            AND table_name='requerimiento_orden_compra_detalle')=5, 'El detalle debe guardar solo5campos propios/referencias.';
    ASSERT NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='compras'
            AND table_name='requerimiento_orden_compra' AND column_name IN ('moneda','proveedor','proveedor_cuit','solicitante','plan'));
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_orden_compra o JOIN cabeceras_oc_antes_normalizar a USING(id_orden_compra)
                       WHERE to_jsonb(o)-'id_presupuesto' IS DISTINCT FROM a.datos), 'La normalizacion debe conservar los campos propios de OC y su auditoria.';
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_orden_compra_detalle_consulta d
                       JOIN detalles_oc_antes_normalizar a USING(id_orden_compra,orden,id_requerimiento_detalle)
                       WHERE d.cantidad IS DISTINCT FROM a.cantidad OR d.precio_unitario IS DISTINCT FROM a.precio_unitario
                          OR d.precio_total IS DISTINCT FROM a.precio_total), 'Cantidad/precio emitidos y su total calculado deben conservarse.';
    ASSERT (SELECT proveedor='Empresa Fixture' AND moneda='ARS' AND confeccionado_por='fixture'
                   AND iva=4.20 AND iibb=0.80 FROM compras.requerimiento_orden_compra_consulta WHERE id_requerimiento=1001);
    ASSERT (SELECT proveedor='Prestador Fixture' AND proveedor_cuit='30700000001' AND id_reclamo_prestacional=3002
              FROM compras.requerimiento_orden_compra_consulta WHERE id_requerimiento=1002);
    ASSERT (SELECT descripcion_item='Equipo anterior' AND precio_unitario=10 AND precio_total=20
              FROM compras.requerimiento_orden_compra_detalle_consulta d JOIN compras.requerimiento_orden_compra o USING(id_orden_compra)
             WHERE o.id_requerimiento=1001);
    ASSERT compras.confirmar_orden_compra_requerimiento(1001,'otro_usuario')=5;
END;
$test$;
DO $permisos_fixture$
BEGIN
    ASSERT has_table_privilege('oc_normalizacion_lectura','compras.requerimiento_orden_compra_consulta','SELECT WITH GRANT OPTION');
    ASSERT has_table_privilege('oc_normalizacion_lectura','compras.requerimiento_orden_compra_detalle_consulta','SELECT');
    ASSERT NOT has_table_privilege('oc_normalizacion_lectura','compras.requerimiento_orden_compra_detalle_consulta','SELECT WITH GRANT OPTION');
    ASSERT NOT EXISTS (SELECT 1 FROM pg_class c, aclexplode(COALESCE(c.relacl,acldefault('r',c.relowner))) a
                       WHERE c.oid IN ('compras.requerimiento_orden_compra_consulta'::regclass,
                                       'compras.requerimiento_orden_compra_detalle_consulta'::regclass)
                         AND a.grantee=0 AND a.privilege_type='SELECT'), 'La migracion no debe publicar datos de OC.';
END;
$permisos_fixture$;
SET ROLE oc_normalizacion_lectura;
SELECT id_orden_compra,id_requerimiento,proveedor FROM compras.requerimiento_orden_compra_consulta ORDER BY id_orden_compra;
SELECT id_orden_compra,orden,descripcion_item FROM compras.requerimiento_orden_compra_detalle_consulta ORDER BY id_orden_compra,orden;
RESET ROLE;
SELECT 'orden_compra_normalizacion: OK' AS resultado;
