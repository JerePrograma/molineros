\set ON_ERROR_STOP on
\encoding LATIN1
DO $guard$ BEGIN
 IF current_database() NOT LIKE 'compras_orden_test_edicion_%' OR to_regclass('compras.requerimiento_orden_compra') IS NULL THEN RAISE EXCEPTION 'Solo fixture aislado OC normalizada.'; END IF;
END $guard$;
CREATE TEMP TABLE seguimiento_cabecera_antes AS SELECT id_orden_compra,to_jsonb(o) AS datos FROM compras.requerimiento_orden_compra o;
CREATE TEMP TABLE seguimiento_detalle_antes AS SELECT id_orden_compra,orden,to_jsonb(d) AS datos FROM compras.requerimiento_orden_compra_detalle d;
\ir ../../docs/sql/compras/20261106_orden_compra_seguimiento.sql
DO $empty$ BEGIN ASSERT NOT EXISTS(SELECT 1 FROM compras.requerimiento_orden_compra_seguimiento), 'El fixture requiere seguimiento vacio.'; END $empty$;
INSERT INTO compras.requerimiento_orden_compra_seguimiento(id_orden_compra,calificacion_proveedor,observaciones_proveedor,factura_ingresada,numero_factura,fecha_factura,estado_liquidacion,liquidacion_auditada,estado_pago,id_liquidacion,id_orden_pago,alta_usr)
 SELECT id_orden_compra,'APTO','Manual fixture','SI','A-0001',DATE '2026-11-06','LIQUIDADA',TRUE,'PAGADO',123,456,'seguimiento_sql_fixture' FROM compras.requerimiento_orden_compra WHERE id_requerimiento=1001;
CREATE TEMP TABLE seguimiento_guardado_antes AS SELECT id_orden_compra,to_jsonb(s) AS datos FROM compras.requerimiento_orden_compra_seguimiento s;
\ir ../../docs/sql/compras/20261106_orden_compra_seguimiento.sql
\ir ../../docs/sql/compras/20261106_orden_compra_seguimiento.sql
DO $test$
DECLARE v_id INTEGER;v_sql TEXT;v_rechazado BOOLEAN;
BEGIN
 ASSERT NOT EXISTS(SELECT 1 FROM compras.requerimiento_orden_compra o JOIN seguimiento_cabecera_antes a USING(id_orden_compra) WHERE to_jsonb(o) IS DISTINCT FROM a.datos), 'No modificar cabecera comercial.';
 ASSERT NOT EXISTS(SELECT 1 FROM compras.requerimiento_orden_compra_detalle d JOIN seguimiento_detalle_antes a USING(id_orden_compra,orden) WHERE to_jsonb(d) IS DISTINCT FROM a.datos), 'No modificar detalle comercial.';
 ASSERT NOT EXISTS(SELECT 1 FROM compras.requerimiento_orden_compra_seguimiento s JOIN seguimiento_guardado_antes a USING(id_orden_compra) WHERE to_jsonb(s) IS DISTINCT FROM a.datos), 'Reejecucion conserva datos y auditoria.';
 SELECT id_orden_compra INTO STRICT v_id FROM compras.requerimiento_orden_compra WHERE id_requerimiento=1001;
 FOREACH v_sql IN ARRAY ARRAY['calificacion_proveedor=''DESCONOCIDO''','factura_ingresada=''X''','estado_liquidacion=''DESCONOCIDO''','estado_pago=''DESCONOCIDO''','id_liquidacion=0','id_orden_pago=-1','version_edicion=0','alta_usr='' ''','modi_usr='' ''','estado_liquidacion=''PENDIENTE'',liquidacion_auditada=TRUE','estado_liquidacion=NULL,liquidacion_auditada=TRUE'] LOOP
  v_rechazado:=FALSE;
  BEGIN EXECUTE 'UPDATE compras.requerimiento_orden_compra_seguimiento SET '||v_sql||' WHERE id_orden_compra=$1' USING v_id;
  EXCEPTION WHEN check_violation THEN v_rechazado:=TRUE;END;
  ASSERT v_rechazado,'Debe rechazar valor fuera de dominio.';
 END LOOP;
 v_rechazado:=FALSE;BEGIN UPDATE compras.requerimiento_orden_compra_seguimiento SET observaciones_proveedor=repeat('x',4001) WHERE id_orden_compra=v_id;EXCEPTION WHEN string_data_right_truncation THEN v_rechazado:=TRUE;END;ASSERT v_rechazado,'Limite observaciones.';
 v_rechazado:=FALSE;BEGIN UPDATE compras.requerimiento_orden_compra_seguimiento SET numero_factura=repeat('x',101) WHERE id_orden_compra=v_id;EXCEPTION WHEN string_data_right_truncation THEN v_rechazado:=TRUE;END;ASSERT v_rechazado,'Limite numero factura.';
 v_rechazado:=FALSE;BEGIN INSERT INTO compras.requerimiento_orden_compra_seguimiento(id_orden_compra,alta_usr)VALUES(v_id,'duplicado');EXCEPTION WHEN unique_violation THEN v_rechazado:=TRUE;END;ASSERT v_rechazado,'Una fila por OC.';
 v_rechazado:=FALSE;BEGIN INSERT INTO compras.requerimiento_orden_compra_seguimiento(id_orden_compra,alta_usr)VALUES(v_id+100000,'huerfano');EXCEPTION WHEN foreign_key_violation THEN v_rechazado:=TRUE;END;ASSERT v_rechazado,'Requiere OC.';
END $test$;
DELETE FROM compras.requerimiento_orden_compra_seguimiento WHERE alta_usr='seguimiento_sql_fixture';
SELECT 'orden_compra_seguimiento_sql: OK' AS resultado;
