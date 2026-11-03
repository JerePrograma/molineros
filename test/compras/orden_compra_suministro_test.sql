\set ON_ERROR_STOP on
\encoding LATIN1
-- Aplicar despues del fixture de normalizacion, solo en base aislada.
-- No crea bases ni cambia datos del portal. Deja el seguimiento vacio para JDBC.
DO $guard$
BEGIN
    IF current_database() NOT LIKE 'compras_orden_test_edicion_%'
       OR to_regclass('compras.requerimiento_orden_compra') IS NULL
       OR (SELECT count(*) FROM information_schema.columns
            WHERE table_schema='compras' AND table_name='requerimiento_orden_compra') <> 16 THEN
        RAISE EXCEPTION 'Se requiere el fixture normalizado aislado de OC.';
    END IF;
END;
$guard$;
CREATE TEMP TABLE suministro_cabeceras_antes AS
    SELECT id_orden_compra,to_jsonb(o) AS datos FROM compras.requerimiento_orden_compra o;
CREATE TEMP TABLE suministro_detalles_antes AS
    SELECT id_orden_compra,orden,to_jsonb(d) AS datos FROM compras.requerimiento_orden_compra_detalle d;
\ir ../../docs/sql/compras/20261103_orden_compra_suministro.sql
DO $empty$
BEGIN
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_orden_compra_suministro),
        'El fixture requiere ausencia inicial de gestiones de suministro.';
END;
$empty$;
INSERT INTO compras.requerimiento_orden_compra_suministro
    (id_orden_compra,fecha_contacto,persona_contactada,medio_contacto,detalle,
     respuesta_1,respuesta_2,respuesta_3,respuesta_4,respuesta_5,alta_usr)
SELECT id_orden_compra,DATE '2026-11-03','Persona fixture','Telefono','Detalle fixture',
       'SI','NO','NO_CORRESPONDE','',NULL,'suministro_upgrade_fixture'
  FROM compras.requerimiento_orden_compra WHERE id_requerimiento=1001;
CREATE TEMP TABLE suministro_guardado_antes_upgrade AS
    SELECT id_orden_compra,to_jsonb(s) AS datos FROM compras.requerimiento_orden_compra_suministro s;
\ir ../../docs/sql/compras/20261103_orden_compra_suministro.sql
\ir ../../docs/sql/compras/20261103_orden_compra_suministro.sql
DO $test$
DECLARE
    v_id INTEGER;
    v_col INTEGER;
    v_rechazado BOOLEAN;
BEGIN
    ASSERT NOT EXISTS (
        SELECT 1 FROM compras.requerimiento_orden_compra o
        JOIN suministro_cabeceras_antes a USING(id_orden_compra)
        WHERE to_jsonb(o) IS DISTINCT FROM a.datos),
        'La migracion no debe cambiar las cabeceras comerciales.';
    ASSERT NOT EXISTS (
        SELECT 1 FROM compras.requerimiento_orden_compra_detalle d
        JOIN suministro_detalles_antes a USING(id_orden_compra,orden)
        WHERE to_jsonb(d) IS DISTINCT FROM a.datos),
        'La migracion no debe cambiar articulos o cantidades.';
    ASSERT NOT EXISTS (
        SELECT 1 FROM compras.requerimiento_orden_compra_suministro s
        JOIN suministro_guardado_antes_upgrade a USING(id_orden_compra)
        WHERE to_jsonb(s) IS DISTINCT FROM a.datos),
        'La reejecucion debe preservar seguimiento y auditoria.';
    SELECT id_orden_compra INTO STRICT v_id FROM compras.requerimiento_orden_compra WHERE id_requerimiento=1001;
    ASSERT (SELECT version_edicion=1 AND alta_fecha IS NOT NULL AND modi_fecha IS NULL
              FROM compras.requerimiento_orden_compra_suministro WHERE id_orden_compra=v_id);
    FOR v_col IN 1..5 LOOP
        v_rechazado:=FALSE;
        BEGIN
            EXECUTE format('UPDATE compras.requerimiento_orden_compra_suministro SET respuesta_%s=''TAL_VEZ'' WHERE id_orden_compra=$1',v_col) USING v_id;
        EXCEPTION WHEN check_violation THEN v_rechazado:=TRUE;
        END;
        ASSERT v_rechazado, 'Debe rechazar respuestas fuera de la lista en cada pregunta.';
    END LOOP;
    v_rechazado:=FALSE;
    BEGIN
        INSERT INTO compras.requerimiento_orden_compra_suministro
        SELECT * FROM compras.requerimiento_orden_compra_suministro WHERE id_orden_compra=v_id;
    EXCEPTION WHEN unique_violation THEN v_rechazado:=TRUE;
    END;
    ASSERT v_rechazado, 'Una sola gestion por OC.';
    v_rechazado:=FALSE;
    BEGIN
        INSERT INTO compras.requerimiento_orden_compra_suministro(id_orden_compra,alta_usr)
        VALUES ((SELECT max(id_orden_compra)+100000 FROM compras.requerimiento_orden_compra),'fixture_fk');
    EXCEPTION WHEN foreign_key_violation THEN v_rechazado:=TRUE;
    END;
    ASSERT v_rechazado, 'No debe existir gestion sin OC.';
    v_rechazado:=FALSE;
    BEGIN
        UPDATE compras.requerimiento_orden_compra_suministro SET persona_contactada=repeat('x',101) WHERE id_orden_compra=v_id;
    EXCEPTION WHEN string_data_right_truncation THEN v_rechazado:=TRUE;
    END;
    ASSERT v_rechazado, 'Persona limitada a cien caracteres.';
    v_rechazado:=FALSE;
    BEGIN
        UPDATE compras.requerimiento_orden_compra_suministro SET medio_contacto=repeat('x',101) WHERE id_orden_compra=v_id;
    EXCEPTION WHEN string_data_right_truncation THEN v_rechazado:=TRUE;
    END;
    ASSERT v_rechazado, 'Medio limitado a cien caracteres.';
    v_rechazado:=FALSE;
    BEGIN
        UPDATE compras.requerimiento_orden_compra_suministro SET detalle=repeat('x',4001) WHERE id_orden_compra=v_id;
    EXCEPTION WHEN string_data_right_truncation THEN v_rechazado:=TRUE;
    END;
    ASSERT v_rechazado, 'Detalle limitado a cuatro mil caracteres.';
    v_rechazado:=FALSE;
    BEGIN
        UPDATE compras.requerimiento_orden_compra_suministro SET version_edicion=0 WHERE id_orden_compra=v_id;
    EXCEPTION WHEN check_violation THEN v_rechazado:=TRUE;
    END;
    ASSERT v_rechazado, 'La version persistida debe ser positiva.';
    v_rechazado:=FALSE;
    BEGIN
        UPDATE compras.requerimiento_orden_compra_suministro SET alta_usr=' ' WHERE id_orden_compra=v_id;
    EXCEPTION WHEN check_violation THEN v_rechazado:=TRUE;
    END;
    ASSERT v_rechazado, 'La auditoria de alta no debe estar vacia.';
END;
$test$;
DELETE FROM compras.requerimiento_orden_compra_suministro
 WHERE alta_usr='suministro_upgrade_fixture'
   AND id_orden_compra=(SELECT id_orden_compra FROM compras.requerimiento_orden_compra WHERE id_requerimiento=1001);
DO $final$
BEGIN
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento_orden_compra_suministro),
        'El test SQL no debe dejar seguimiento previo al roundtrip JDBC.';
END;
$final$;
SELECT 'orden_compra_suministro_upgrade: OK' AS resultado;