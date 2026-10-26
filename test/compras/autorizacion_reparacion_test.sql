\set ON_ERROR_STOP on
-- Runner: base aislada compras_autorizacion_test_*, fixture inicial de orden_compra_test.sql
-- hasta su primer \ir, migracion 20261023_orden_compra_formulario.sql,
-- cambiar_estado_requerimiento canonica del schema y autorizacion original 20261013.
-- No usar el schema completo ni ejecutar sobre datos del portal.
DO $test$
BEGIN
    IF current_database() NOT LIKE 'compras_autorizacion_test_%' THEN
        RAISE EXCEPTION 'Esta prueba requiere una base aislada compras_autorizacion_test_*.';
    END IF;
    IF EXISTS (SELECT 1 FROM pg_trigger WHERE tgrelid = 'compras.requerimiento'::regclass AND NOT tgisinternal) THEN
        RAISE EXCEPTION 'El fixture inicial debe estar sin trigger para probar ambas configuraciones.';
    END IF;
    ASSERT (SELECT md5(btrim(replace(prosrc, E'\r', ''))) FROM pg_proc
             WHERE oid = to_regprocedure('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)'))
           = '50104e5fea81484ace9d568fea15e393', 'La reproduccion debe usar la funcion original 20261013.';
END;
$test$;
INSERT INTO compras.sector_requerimiento (id_sector, descripcion, requiere_afiliado)
VALUES (3, 'RRHH', FALSE);
INSERT INTO compras.requerimiento (id_requerimiento, estado, id_sector,
    afiliado_cuil_titular, afiliado_int)
VALUES (9001, 1, 1, NULL, NULL), (9002, 4, 2, '20111111112', 0),
       (9003, 1, 1, NULL, NULL), (9004, 1, 1, NULL, NULL),
       (9005, 4, 2, '20111111112', 0), (9006, 4, 2, '20111111112', 0),
       (9007, 1, 2, '20111111112', 0), (9008, 1, 1, NULL, NULL),
       (9010, 1, 1, NULL, NULL), (9011, 4, 2, '20111111112', 0),
       (9012, 1, 3, NULL, NULL);
INSERT INTO compras.requerimiento_detalle
    (id_detalle, id_requerimiento, cantidad)
SELECT id_requerimiento, id_requerimiento, 1
  FROM compras.requerimiento WHERE id_requerimiento BETWEEN 9001 AND 9012;
INSERT INTO compras.requerimiento_presupuesto
    (id_requerimiento, tipo_documento, empresa_adjudicada, descripcion_empresa)
VALUES (9001, 3, TRUE, 'Empresa Fixture'), (9004, 3, TRUE, 'Empresa Fixture A'),
       (9004, 3, TRUE, 'Empresa Fixture B'), (9008, 3, TRUE, 'Empresa Fixture'),
       (9010, 3, TRUE, 'Empresa Fixture'), (9012, 3, TRUE, 'Empresa Fixture');
INSERT INTO autorizaciones.reclamos_prestacionales (id, baja_fecha)
VALUES (9002, NULL), (9006, now()), (9011, NULL);
INSERT INTO compras.requerimiento_reclamo_prestacional
    (id_requerimiento, id_reclamo_prestacional, estado)
VALUES (9002, 9002, 'VINCULADO'), (9006, 9006, 'VINCULADO'), (9011, 9011, 'VINCULADO');
CREATE TEMP TABLE snapshots_autorizacion AS
    SELECT id_requerimiento, to_jsonb(r) AS fila FROM compras.requerimiento r;
CREATE TEMP TABLE funciones_autorizacion_preservadas AS
    SELECT oid, oid = to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)') AS normalizar,
           md5(CASE WHEN oid = to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)')
                    THEN replace(pg_get_functiondef(oid), E'\r', '') ELSE pg_get_functiondef(oid) END) AS hash
      FROM pg_proc
     WHERE oid IN (
        to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)'),
        to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)'),
        to_regprocedure('compras.validar_requerimiento_fila()'),
        to_regprocedure('compras.bloquear_creacion_orden_compra(integer,character varying)'));
-- Replica del bloque generico previo: tomado literalmente del adaptador 20261015.
DO $fixture_generica_vieja$
DECLARE v_def TEXT; v_actual TEXT; v_anterior TEXT;
BEGIN
    v_def := replace(pg_get_functiondef(to_regprocedure(
        'compras.cambiar_estado_requerimiento(integer,integer,character varying)')), E'\r', '');
    v_actual := $actual$    -- Autorizacion previa a la OC: tampoco puede omitirse por la ruta generica.
    IF p_estado_nuevo IN (6, 7) THEN
        PERFORM compras.cambiar_estado_autorizacion_requerimiento(
                p_id_requerimiento, p_estado_nuevo, p_usuario);
        RETURN;
    END IF;

    IF p_estado_nuevo = 5 THEN
        PERFORM compras.confirmar_orden_compra_requerimiento(
                p_id_requerimiento, p_usuario);
        RETURN;
    END IF;

    IF v_estado_actual IN (6, 7) THEN
        RAISE EXCEPTION
            'El requerimiento no puede modificarse en el estado actual.';
    END IF;$actual$;
    v_anterior := $anterior$    -- La ruta generica tampoco permite saltear las reglas de Empresas.
    IF p_estado_nuevo = 5 AND EXISTS (
        SELECT 1
          FROM compras.requerimiento r
          JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
         WHERE r.id_requerimiento = p_id_requerimiento
           AND compras.normalizar_sector(sr.descripcion) IN ('RRHH', 'SISTEMAS')
    ) THEN
        PERFORM compras.confirmar_orden_compra_requerimiento(
                p_id_requerimiento, p_usuario);
        RETURN;
    END IF;$anterior$;
    v_actual := replace(v_actual, E'\r', '');
    v_anterior := replace(v_anterior, E'\r', '');
    ASSERT length(v_def) - length(replace(v_def, v_actual, '')) = length(v_actual),
           'El fixture debe partir de la ruta generica canonica.';
    EXECUTE replace(v_def, v_actual, v_anterior);
END;
$fixture_generica_vieja$;
DO $test$
DECLARE v_contexto TEXT;
BEGIN
    BEGIN
        PERFORM compras.cambiar_estado_autorizacion_requerimiento(9001, 6, 'fixture');
        RAISE EXCEPTION 'Falto reproducir el rechazo corporativo original.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'El requerimiento no puede pasar al estado seleccionado desde su estado actual.' THEN RAISE; END IF;
        GET STACKED DIAGNOSTICS v_contexto = PG_EXCEPTION_CONTEXT;
        RAISE NOTICE 'Defecto reproducido: % / %', SQLERRM, v_contexto;
    END;
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento r JOIN snapshots_autorizacion s USING(id_requerimiento)
                        WHERE to_jsonb(r) IS DISTINCT FROM s.fila), 'La reproduccion fallida debe conservar las filas.';
END;
$test$;
\ir ../../docs/sql/compras/20261026_reparar_autorizacion_requerimiento.sql
DO $test$
BEGIN
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento r JOIN snapshots_autorizacion s USING(id_requerimiento)
                        WHERE to_jsonb(r) IS DISTINCT FROM s.fila), 'La reparacion no debe cambiar requerimientos.';
    ASSERT NOT EXISTS (SELECT 1 FROM funciones_autorizacion_preservadas f
                        WHERE md5(CASE WHEN f.normalizar THEN replace(pg_get_functiondef(f.oid), E'\r', '')
                                 ELSE pg_get_functiondef(f.oid) END) IS DISTINCT FROM f.hash), 'No debe retroceder OC ni cambiar generica/validador.';
    PERFORM compras.cambiar_estado_requerimiento(9001, 6, 'fixture_envio');
    ASSERT (SELECT estado FROM compras.requerimiento WHERE id_requerimiento = 9001) = 6;
    PERFORM compras.cambiar_estado_autorizacion_requerimiento(9001, 7, 'fixture_autoriza');
    ASSERT (SELECT estado = 7 AND modi_usr = 'fixture_autoriza' AND modi_fecha IS NOT NULL
              FROM compras.requerimiento WHERE id_requerimiento = 9001);
    PERFORM compras.cambiar_estado_autorizacion_requerimiento(9002, 6, 'fixture_envio');
    PERFORM compras.cambiar_estado_requerimiento(9002, 7, 'fixture_autoriza');
    ASSERT (SELECT estado FROM compras.requerimiento WHERE id_requerimiento = 9002) = 7;
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento r JOIN snapshots_autorizacion s USING(id_requerimiento)
                       WHERE (to_jsonb(r) - 'estado' - 'modi_fecha' - 'modi_usr')
                             IS DISTINCT FROM (s.fila - 'estado' - 'modi_fecha' - 'modi_usr')),
           'Enviar y autorizar deben conservar los datos funcionales.';
END;
$test$;
-- Misma instalacion, ahora con el validador real vigente en la tabla.
CREATE TRIGGER trg_compras_requerimiento_validar
BEFORE INSERT OR UPDATE ON compras.requerimiento FOR EACH ROW
EXECUTE PROCEDURE compras.validar_requerimiento_fila();
TRUNCATE snapshots_autorizacion;
INSERT INTO snapshots_autorizacion SELECT id_requerimiento, to_jsonb(r) FROM compras.requerimiento r;
\ir ../../docs/sql/compras/20261026_reparar_autorizacion_requerimiento.sql
DO $test$
DECLARE v_caso RECORD; v_fila JSONB; v_id_orden INTEGER;
BEGIN
    ASSERT NOT EXISTS (SELECT 1 FROM compras.requerimiento r JOIN snapshots_autorizacion s USING(id_requerimiento)
                        WHERE to_jsonb(r) IS DISTINCT FROM s.fila), 'La reejecucion debe conservar las filas.';
    ASSERT NOT EXISTS (SELECT 1 FROM funciones_autorizacion_preservadas f
                        WHERE md5(CASE WHEN f.normalizar THEN replace(pg_get_functiondef(f.oid), E'\r', '')
                                 ELSE pg_get_functiondef(f.oid) END) IS DISTINCT FROM f.hash), 'Reejecutar debe conservar OC, generica y validador.';
    PERFORM compras.cambiar_estado_requerimiento(9010, 6, 'fixture_envio');
    PERFORM compras.cambiar_estado_autorizacion_requerimiento(9010, 7, 'fixture_autoriza');
    PERFORM compras.cambiar_estado_autorizacion_requerimiento(9011, 6, 'fixture_envio');
    PERFORM compras.cambiar_estado_requerimiento(9011, 7, 'fixture_autoriza');
    PERFORM compras.cambiar_estado_autorizacion_requerimiento(9012, 6, 'fixture_envio');
    PERFORM compras.cambiar_estado_autorizacion_requerimiento(9012, 7, 'fixture_autoriza');
    ASSERT (SELECT count(*) FROM compras.requerimiento WHERE id_requerimiento IN (9010, 9011, 9012) AND estado = 7) = 3;
    -- Emitir OC con snapshot real despues de autorizar Sistemas; confirmar otra vez es idempotente.
    PERFORM compras.bloquear_creacion_orden_compra(9010, 'fixture_oc');
    INSERT INTO compras.requerimiento_orden_compra
        (id_requerimiento, fecha_emision, moneda, proveedor, confeccionado_por, alta_usr)
    VALUES (9010, CURRENT_DATE, 'ARS', 'Empresa Fixture', 'Operador Fixture', 'fixture_oc')
    RETURNING id_orden_compra INTO v_id_orden;
    INSERT INTO compras.requerimiento_orden_compra_detalle
        (id_orden_compra, orden, id_requerimiento_detalle, descripcion_item,
         cantidad, precio_unitario, precio_total)
    VALUES (v_id_orden, 1, 9010, 'Item Sistemas', 1, 10, 10);
    ASSERT compras.confirmar_orden_compra_requerimiento(9010, 'fixture_oc') = 5;
    SELECT to_jsonb(r) INTO v_fila FROM compras.requerimiento r WHERE id_requerimiento = 9010;
    ASSERT compras.confirmar_orden_compra_requerimiento(9010, 'otro_usuario') = 5;
    ASSERT (SELECT to_jsonb(r) FROM compras.requerimiento r WHERE id_requerimiento = 9010) = v_fila,
           'Repetir la confirmacion de OC debe conservar su auditoria.';
    ASSERT compras.bloquear_creacion_orden_compra(9010, 'fixture_oc') = v_id_orden;
    FOR v_caso IN SELECT * FROM (VALUES
        (9003, 6, 'Debe existir al menos una cotizacion de Empresa activa antes de enviar o autorizar el requerimiento.'),
        (9004, 6, 'Debe seleccionar exactamente una Empresa adjudicada con cotizacion activa.'),
        (9005, 6, 'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.'),
        (9006, 6, 'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.'),
        (9007, 6, 'El requerimiento no puede pasar al estado seleccionado desde su estado actual.'),
        (9008, 7, 'RRHH o SISTEMAS deben pasar de PENDIENTE a PARA AUTORIZAR y luego a AUTORIZADO.'),
        (9001, 7, 'RRHH o SISTEMAS deben pasar de PENDIENTE a PARA AUTORIZAR y luego a AUTORIZADO.'),
        (9011, 6, 'El requerimiento no puede pasar al estado seleccionado desde su estado actual.'),
        (9008, 5, 'El estado de autorizacion seleccionado no es valido.')
    ) AS casos(id, destino, mensaje)
    LOOP
        SELECT to_jsonb(r) INTO v_fila FROM compras.requerimiento r WHERE id_requerimiento = v_caso.id;
        BEGIN
            PERFORM compras.cambiar_estado_autorizacion_requerimiento(v_caso.id, v_caso.destino, 'fixture');
            RAISE EXCEPTION 'Falto rechazar caso % -> %.', v_caso.id, v_caso.destino;
        EXCEPTION WHEN OTHERS THEN
            IF SQLERRM <> v_caso.mensaje THEN RAISE; END IF;
        END;
        ASSERT (SELECT to_jsonb(r) FROM compras.requerimiento r WHERE id_requerimiento = v_caso.id) = v_fila,
               'El rechazo debe conservar estado, datos y auditoria.';
    END LOOP;
    BEGIN
        PERFORM compras.cambiar_estado_requerimiento(9008, 5, 'fixture');
        RAISE EXCEPTION 'La ruta generica no debe omitir la autorizacion para crear OC.';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM <> 'La Orden de Compra solo puede crearse desde AUTORIZADO.' THEN RAISE; END IF;
    END;
    ASSERT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_compras_requerimiento_validar'
                    AND tgrelid = 'compras.requerimiento'::regclass AND tgenabled = 'O');
END;
$test$;
SELECT 'autorizacion_reparacion: OK' AS resultado;
