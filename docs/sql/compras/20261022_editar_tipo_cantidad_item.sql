-- Compras: editar tipo de cotizacion y cantidad antes del RP.
-- Referencias: corregir_item_requerimiento; EditarLiquidacionServiceImpl.actualizaLiquidacionPrestacionEntry.
-- PostgreSQL 9.6. No modifica filas de negocio ni activa/desactiva triggers.
-- Instalar antes del Java/JSP. Ante error: ROLLBACK; revisar el mensaje.
BEGIN;
SET LOCAL lock_timeout = '5s';
DO $instalar$
DECLARE
    v_firma7 TEXT := 'compras.corregir_item_requerimiento(integer,integer,integer,integer,character varying,character varying,character varying)';
    v_firma9 TEXT := 'compras.corregir_item_requerimiento(integer,integer,integer,integer,character varying,character varying,character varying,integer,integer)';
    v_nueva BOOLEAN;
    v_owner TEXT;
    v_acl RECORD;
    v_destino TEXT;
    v_firma TEXT;
    v_def TEXT;
    v_viejo TEXT;
    v_nuevo TEXT;
BEGIN
    IF to_regprocedure(v_firma7) IS NULL OR
       (SELECT md5(replace(prosrc, E'\r\n', E'\n')) FROM pg_proc
        WHERE oid = to_regprocedure(v_firma7)) NOT IN ('e955ebcda42864655d04afa5a4c5cf10', '4bef33915f3bd909be739524cec9ffa5') THEN
        RAISE EXCEPTION 'La correccion instalada difiere del contrato verificado. No se modifico.';
    END IF;
    v_nueva := to_regprocedure(v_firma9) IS NULL;
    IF NOT v_nueva AND (SELECT md5(replace(prosrc, E'\r\n', E'\n')) FROM pg_proc
                       WHERE oid = to_regprocedure(v_firma9)) <> 'c2cf811f57622a9e3105f5b99d98a2e1' THEN
        RAISE EXCEPTION 'La firma ampliada ya existe con otra definicion.';
    END IF;
    SELECT pg_get_userbyid(proowner) INTO v_owner FROM pg_proc WHERE oid = to_regprocedure(v_firma7);
    EXECUTE $fn9$CREATE OR REPLACE FUNCTION compras.corregir_item_requerimiento(
    p_id_requerimiento INTEGER,
    p_id_detalle INTEGER,
    p_id_prestacion INTEGER,
    p_id_tipo_nomenclador INTEGER,
    p_codigo_nomenclador VARCHAR,
    p_descripcion_nomenclador VARCHAR,
    p_usuario VARCHAR,
    p_id_tipo_prestacion INTEGER,
    p_cantidad INTEGER
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_id_sector INTEGER;
    v_detalle compras.requerimiento_detalle%ROWTYPE;
    v_nomenclador autorizaciones.nomenclador%ROWTYPE;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0
       OR p_id_detalle IS NULL OR p_id_detalle <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento y el item a corregir.';
    END IF;

    PERFORM compras.bloquear_requerimiento_reclamo_prestacional(p_id_requerimiento);
    SELECT r.estado, r.id_sector INTO v_estado, v_id_sector
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND OR v_estado NOT IN (1, 2, 3) THEN
        RAISE EXCEPTION 'El item no puede corregirse en el estado actual. Actualice la pantalla.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
        WHERE rr.id_requerimiento = p_id_requerimiento
    ) THEN
        RAISE EXCEPTION 'El requerimiento ya tiene una creacion de RP iniciada. Actualice la pantalla.';
    END IF;

    SELECT d.* INTO v_detalle FROM compras.requerimiento_detalle d
     WHERE d.id_detalle = p_id_detalle
       AND d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND OR v_detalle.tipo_item NOT IN ('NOMENCLADOR', 'MEDICAMENTO') THEN
        RAISE EXCEPTION 'No se encontro un item activo de medicamento o prestacion para corregir.';
    END IF;

    IF p_cantidad IS NULL OR p_cantidad <= 0 THEN
        RAISE EXCEPTION 'La cantidad debe ser mayor a cero.';
    END IF;
    IF v_detalle.id_tipo_prestacion IS NOT NULL AND p_id_tipo_prestacion IS NULL THEN
        RAISE EXCEPTION 'El tipo de prestacion ya informado no puede quitarse.';
    END IF;
    IF p_id_tipo_prestacion IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM compras.tipo_prestacion t
        WHERE t.id_tipo_prestacion = p_id_tipo_prestacion AND t.id_sector = v_id_sector
    ) THEN
        RAISE EXCEPTION 'El tipo de prestacion no corresponde al sector del requerimiento.';
    END IF;

    PERFORM compras.validar_nomenclador_configurado(
        v_id_sector, p_id_tipo_prestacion,
        p_id_prestacion, p_id_tipo_nomenclador);
    SELECT n.* INTO v_nomenclador FROM autorizaciones.nomenclador n
     WHERE n.id_prestacion = p_id_prestacion AND n.baja_fecha IS NULL;
    IF NOT FOUND
       OR v_nomenclador.id_tipo_nomenclador IS DISTINCT FROM p_id_tipo_nomenclador
       OR btrim(v_nomenclador.codigo) IS DISTINCT FROM btrim(p_codigo_nomenclador)
       OR btrim(v_nomenclador.descripcion) IS DISTINCT FROM btrim(p_descripcion_nomenclador) THEN
        RAISE EXCEPTION 'La seleccion no coincide con el nomenclador vigente. Actualice la pantalla.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
          AND d.id_detalle <> p_id_detalle AND d.id_prestacion = p_id_prestacion
    ) THEN
        RAISE EXCEPTION 'El requerimiento ya contiene la prestacion seleccionada.';
    END IF;

    IF v_detalle.id_tipo_prestacion IS NOT DISTINCT FROM p_id_tipo_prestacion
       AND v_detalle.cantidad = p_cantidad
       AND v_detalle.tipo_item = 'NOMENCLADOR'
       AND v_detalle.id_prestacion = p_id_prestacion
       AND v_detalle.id_tipo_nomenclador = p_id_tipo_nomenclador
       AND btrim(v_detalle.codigo_nomenclador) = btrim(v_nomenclador.codigo)
       AND btrim(v_detalle.descripcion_nomenclador) = btrim(v_nomenclador.descripcion) THEN
        RETURN p_id_detalle;
    END IF;

    PERFORM 1 FROM compras.requerimiento_comparativa c
     WHERE c.id_requerimiento = p_id_requerimiento
     ORDER BY c.id_comparativa FOR UPDATE;
    PERFORM 1 FROM compras.requerimiento_comparativa_detalle cd
      JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
     WHERE c.id_requerimiento = p_id_requerimiento
     ORDER BY cd.id_detalle FOR UPDATE OF cd;

    IF v_detalle.id_prestacion IS DISTINCT FROM p_id_prestacion THEN
        IF EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = p_id_requerimiento
              AND cd.id_prestacion = p_id_prestacion
        ) THEN
            RAISE EXCEPTION 'La comparativa ya contiene la prestacion de destino. No se modificaron importes.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM compras.requerimiento_detalle d
            WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
              AND d.id_detalle <> p_id_detalle AND d.id_prestacion = v_detalle.id_prestacion
        ) AND EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = p_id_requerimiento
              AND cd.id_prestacion = v_detalle.id_prestacion
        ) THEN
            RAISE EXCEPTION 'La prestacion anterior esta compartida. No se pueden reasignar sus importes.';
        END IF;
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);
    UPDATE compras.requerimiento_comparativa_detalle cd
       SET id_prestacion = p_id_prestacion, modi_fecha = now(), modi_usr = v_usuario
      FROM compras.requerimiento_comparativa c
     WHERE c.id_comparativa = cd.id_comparativa
       AND c.id_requerimiento = p_id_requerimiento
       AND cd.id_prestacion = v_detalle.id_prestacion
       AND cd.id_prestacion IS DISTINCT FROM p_id_prestacion;

    UPDATE compras.requerimiento_detalle
       SET tipo_item = 'NOMENCLADOR', id_prestacion = p_id_prestacion,
           id_tipo_prestacion = p_id_tipo_prestacion, cantidad = p_cantidad,
           precio_total_estimado = CASE WHEN precio_unitario_estimado IS NULL THEN NULL
               ELSE round(p_cantidad * precio_unitario_estimado, 2) END,
           id_tipo_nomenclador = v_nomenclador.id_tipo_nomenclador,
           codigo_nomenclador = btrim(v_nomenclador.codigo),
           descripcion_nomenclador = btrim(v_nomenclador.descripcion),
           id_medicamento = NULL, troquel = NULL, nombre_medicamento = NULL,
           modi_fecha = now(), modi_usr = v_usuario
     WHERE id_detalle = p_id_detalle AND id_requerimiento = p_id_requerimiento
       AND baja_fecha IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'El item ya no esta disponible. Actualice la pantalla.';
    END IF;
    IF v_detalle.id_prestador IS NOT NULL
       AND NOT compras.es_prestador_compatible_cotizacion(
           p_id_requerimiento, v_detalle.id_prestador) THEN
        RAISE EXCEPTION 'El prestador adjudicado no es compatible con la nueva prestacion. No se modificaron importes.';
    END IF;
    RETURN p_id_detalle;
END;
$func$
LANGUAGE plpgsql;$fn9$;
    EXECUTE $fn7$CREATE OR REPLACE FUNCTION compras.corregir_item_requerimiento(
    p_id_requerimiento INTEGER, p_id_detalle INTEGER,
    p_id_prestacion INTEGER, p_id_tipo_nomenclador INTEGER,
    p_codigo_nomenclador VARCHAR, p_descripcion_nomenclador VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_tipo INTEGER;
    v_cantidad INTEGER;
BEGIN
    PERFORM compras.bloquear_requerimiento_reclamo_prestacional(p_id_requerimiento);
    PERFORM 1 FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento FOR UPDATE;
    SELECT d.id_tipo_prestacion, d.cantidad INTO v_tipo, v_cantidad
      FROM compras.requerimiento_detalle d
     WHERE d.id_detalle = p_id_detalle AND d.id_requerimiento = p_id_requerimiento
       AND d.baja_fecha IS NULL FOR UPDATE;
    RETURN compras.corregir_item_requerimiento(
        p_id_requerimiento, p_id_detalle, p_id_prestacion, p_id_tipo_nomenclador,
        p_codigo_nomenclador, p_descripcion_nomenclador, p_usuario, v_tipo, v_cantidad);
END;
$func$
LANGUAGE plpgsql;$fn7$;

    v_firma := 'compras.validar_requerimiento_detalle_fila()';
    IF to_regprocedure(v_firma) IS NOT NULL THEN
        v_def := replace(pg_get_functiondef(to_regprocedure(v_firma)), E'\r\n', E'\n');
        v_viejo := replace($viejo$    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NULL AND NEW.baja_fecha IS NULL
       AND OLD.tipo_item IN ('NOMENCLADOR', 'MEDICAMENTO')
       AND NEW.tipo_item = 'NOMENCLADOR'
       AND (v_estado IN (2, 3) OR OLD.tipo_item = 'MEDICAMENTO')
       AND (NEW.tipo_item IS DISTINCT FROM OLD.tipo_item
            OR NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion
            OR NEW.id_tipo_nomenclador IS DISTINCT FROM OLD.id_tipo_nomenclador
            OR NEW.codigo_nomenclador IS DISTINCT FROM OLD.codigo_nomenclador
            OR NEW.descripcion_nomenclador IS DISTINCT FROM OLD.descripcion_nomenclador) THEN

        PERFORM compras.bloquear_requerimiento_reclamo_prestacional(NEW.id_requerimiento);
        SELECT r.estado, r.id_sector INTO v_estado, v_id_sector_correccion
          FROM compras.requerimiento r
         WHERE r.id_requerimiento = NEW.id_requerimiento AND r.baja_fecha IS NULL
         FOR UPDATE;
        IF NOT FOUND OR v_estado NOT IN (1, 2, 3)
           OR EXISTS (SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
                      WHERE rr.id_requerimiento = NEW.id_requerimiento) THEN
            RAISE EXCEPTION 'El item no puede corregirse despues de iniciar el RP.';
        END IF;
        v_fila_correccion := NEW;
        v_fila_correccion.tipo_item := OLD.tipo_item;
        v_fila_correccion.id_prestacion := OLD.id_prestacion;
        v_fila_correccion.id_tipo_nomenclador := OLD.id_tipo_nomenclador;
        v_fila_correccion.codigo_nomenclador := OLD.codigo_nomenclador;
        v_fila_correccion.descripcion_nomenclador := OLD.descripcion_nomenclador;
        v_fila_correccion.id_medicamento := OLD.id_medicamento;
        v_fila_correccion.troquel := OLD.troquel;
        v_fila_correccion.nombre_medicamento := OLD.nombre_medicamento;
        v_fila_correccion.modi_fecha := OLD.modi_fecha;
        v_fila_correccion.modi_usr := OLD.modi_usr;
        IF v_fila_correccion IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION 'La correccion del item debe conservar cantidades, observaciones e importes.';
        END IF;
        PERFORM compras.validar_nomenclador_configurado(
            v_id_sector_correccion, OLD.id_tipo_prestacion,
            NEW.id_prestacion, NEW.id_tipo_nomenclador);
        IF NEW.id_medicamento IS NOT NULL OR NEW.troquel IS NOT NULL
           OR NEW.nombre_medicamento IS NOT NULL OR NOT EXISTS (
            SELECT 1 FROM autorizaciones.nomenclador n
            WHERE n.id_prestacion = NEW.id_prestacion AND n.baja_fecha IS NULL
              AND btrim(n.codigo) = NEW.codigo_nomenclador
              AND btrim(n.descripcion) = NEW.descripcion_nomenclador
        ) THEN
            RAISE EXCEPTION 'La correccion debe conservar una referencia canonica de nomenclador.';
        END IF;
        IF NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion AND EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = NEW.id_requerimiento
              AND cd.id_prestacion = OLD.id_prestacion
        ) THEN
            RAISE EXCEPTION 'La correccion debe actualizar la comparativa en la misma transaccion.';
        END IF;
        IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d
                   WHERE d.id_requerimiento = NEW.id_requerimiento AND d.baja_fecha IS NULL
                     AND d.id_detalle <> NEW.id_detalle AND d.id_prestacion = NEW.id_prestacion) THEN
            RAISE EXCEPTION 'El requerimiento ya contiene la prestacion seleccionada.';
        END IF;
        RETURN NEW;
    END IF;$viejo$, E'\r\n', E'\n');
        v_nuevo := replace($nuevo$    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NULL AND NEW.baja_fecha IS NULL
       AND OLD.tipo_item IN ('NOMENCLADOR', 'MEDICAMENTO')
       AND NEW.tipo_item = 'NOMENCLADOR'
       AND (v_estado IN (2, 3) OR OLD.tipo_item = 'MEDICAMENTO')
       AND (NEW.tipo_item IS DISTINCT FROM OLD.tipo_item
            OR NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion
            OR NEW.id_tipo_nomenclador IS DISTINCT FROM OLD.id_tipo_nomenclador
            OR NEW.codigo_nomenclador IS DISTINCT FROM OLD.codigo_nomenclador
            OR NEW.descripcion_nomenclador IS DISTINCT FROM OLD.descripcion_nomenclador
            OR NEW.id_tipo_prestacion IS DISTINCT FROM OLD.id_tipo_prestacion
            OR NEW.cantidad IS DISTINCT FROM OLD.cantidad) THEN

        PERFORM compras.bloquear_requerimiento_reclamo_prestacional(NEW.id_requerimiento);
        SELECT r.estado, r.id_sector INTO v_estado, v_id_sector_correccion
          FROM compras.requerimiento r
         WHERE r.id_requerimiento = NEW.id_requerimiento AND r.baja_fecha IS NULL
         FOR UPDATE;
        IF NOT FOUND OR v_estado NOT IN (1, 2, 3)
           OR EXISTS (SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
                      WHERE rr.id_requerimiento = NEW.id_requerimiento) THEN
            RAISE EXCEPTION 'El item no puede corregirse despues de iniciar el RP.';
        END IF;
        IF NEW.cantidad IS NULL OR NEW.cantidad <= 0 THEN
            RAISE EXCEPTION 'La cantidad debe ser mayor a cero.';
        END IF;
        IF OLD.id_tipo_prestacion IS NOT NULL AND NEW.id_tipo_prestacion IS NULL THEN
            RAISE EXCEPTION 'El tipo de prestacion ya informado no puede quitarse.';
        END IF;
        IF NEW.id_tipo_prestacion IS NOT NULL AND NOT EXISTS (
            SELECT 1 FROM compras.tipo_prestacion t
            WHERE t.id_tipo_prestacion = NEW.id_tipo_prestacion
              AND t.id_sector = v_id_sector_correccion
        ) THEN
            RAISE EXCEPTION 'El tipo de prestacion no corresponde al sector del requerimiento.';
        END IF;
        NEW.precio_total_estimado := CASE WHEN NEW.precio_unitario_estimado IS NULL THEN NULL
            ELSE round(NEW.cantidad * NEW.precio_unitario_estimado, 2) END;
        v_fila_correccion := NEW;
        v_fila_correccion.id_tipo_prestacion := OLD.id_tipo_prestacion;
        v_fila_correccion.cantidad := OLD.cantidad;
        v_fila_correccion.precio_total_estimado := OLD.precio_total_estimado;
        v_fila_correccion.tipo_item := OLD.tipo_item;
        v_fila_correccion.id_prestacion := OLD.id_prestacion;
        v_fila_correccion.id_tipo_nomenclador := OLD.id_tipo_nomenclador;
        v_fila_correccion.codigo_nomenclador := OLD.codigo_nomenclador;
        v_fila_correccion.descripcion_nomenclador := OLD.descripcion_nomenclador;
        v_fila_correccion.id_medicamento := OLD.id_medicamento;
        v_fila_correccion.troquel := OLD.troquel;
        v_fila_correccion.nombre_medicamento := OLD.nombre_medicamento;
        v_fila_correccion.modi_fecha := OLD.modi_fecha;
        v_fila_correccion.modi_usr := OLD.modi_usr;
        IF v_fila_correccion IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION 'La correccion del item debe conservar observaciones, precios unitarios y adjudicacion.';
        END IF;
        PERFORM compras.validar_nomenclador_configurado(
            v_id_sector_correccion, NEW.id_tipo_prestacion,
            NEW.id_prestacion, NEW.id_tipo_nomenclador);
        IF NEW.id_medicamento IS NOT NULL OR NEW.troquel IS NOT NULL
           OR NEW.nombre_medicamento IS NOT NULL OR NOT EXISTS (
            SELECT 1 FROM autorizaciones.nomenclador n
            WHERE n.id_prestacion = NEW.id_prestacion AND n.baja_fecha IS NULL
              AND btrim(n.codigo) = NEW.codigo_nomenclador
              AND btrim(n.descripcion) = NEW.descripcion_nomenclador
        ) THEN
            RAISE EXCEPTION 'La correccion debe conservar una referencia canonica de nomenclador.';
        END IF;
        IF NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion AND EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = NEW.id_requerimiento
              AND cd.id_prestacion = OLD.id_prestacion
        ) THEN
            RAISE EXCEPTION 'La correccion debe actualizar la comparativa en la misma transaccion.';
        END IF;
        IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d
                   WHERE d.id_requerimiento = NEW.id_requerimiento AND d.baja_fecha IS NULL
                     AND d.id_detalle <> NEW.id_detalle AND d.id_prestacion = NEW.id_prestacion) THEN
            RAISE EXCEPTION 'El requerimiento ya contiene la prestacion seleccionada.';
        END IF;
        RETURN NEW;
    END IF;$nuevo$, E'\r\n', E'\n');
        IF position(v_nuevo IN v_def) = 0 THEN
            IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
                RAISE EXCEPTION 'Variante desconocida de la validacion de correccion. No se reemplazo.';
            END IF;
            EXECUTE replace(v_def, v_viejo, v_nuevo);
        END IF;
    END IF;
    v_firma := 'compras.validar_tipo_prestacion_detalle_fila()';
    IF to_regprocedure(v_firma) IS NOT NULL THEN
        v_def := replace(pg_get_functiondef(to_regprocedure(v_firma)), E'\r\n', E'\n');
        v_viejo := replace(U&'        IF v_estado <> 1 THEN
            RAISE EXCEPTION
                ''El tipo de prestaci\00F3n solo puede modificarse en estado PENDIENTE.'';
        END IF;', E'\r\n', E'\n');
        v_nuevo := replace($tipo_nuevo$        PERFORM compras.bloquear_requerimiento_reclamo_prestacional(NEW.id_requerimiento);
        SELECT r.estado INTO v_estado FROM compras.requerimiento r
         WHERE r.id_requerimiento = NEW.id_requerimiento AND r.baja_fecha IS NULL
         FOR UPDATE;
        IF NOT FOUND OR v_estado NOT IN (1, 2, 3) OR EXISTS (
            SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
            WHERE rr.id_requerimiento = NEW.id_requerimiento
        ) THEN
            RAISE EXCEPTION 'El tipo de prestacion solo puede modificarse antes de iniciar el RP.';
        END IF;$tipo_nuevo$, E'\r\n', E'\n');
        IF position(v_nuevo IN v_def) = 0 THEN
            IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
                RAISE EXCEPTION 'Variante desconocida de la validacion de tipo. No se reemplazo.';
            END IF;
            EXECUTE replace(v_def, v_viejo, v_nuevo);
        END IF;
    END IF;
    -- Patron ACL de 20261021_reparar_guardado_detalle_requerimiento.sql.
    -- Misma autorizacion EXECUTE, sin abrir PUBLIC ni usar SECURITY DEFINER.
    IF v_nueva THEN
        EXECUTE format('ALTER FUNCTION %s OWNER TO %I', v_firma9, v_owner);
        FOR v_acl IN SELECT DISTINCT a.grantee FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl, acldefault('f', p.proowner))) a
            WHERE p.oid = to_regprocedure(v_firma9)
        LOOP
            v_destino := CASE WHEN v_acl.grantee = 0 THEN 'PUBLIC'
                ELSE quote_ident(pg_get_userbyid(v_acl.grantee)) END;
            EXECUTE format('REVOKE ALL ON FUNCTION %s FROM %s', v_firma9, v_destino);
        END LOOP;
        FOR v_acl IN SELECT a.grantee, bool_or(a.is_grantable) AS con_opcion FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl, acldefault('f', p.proowner))) a
            WHERE p.oid = to_regprocedure(v_firma7) AND a.privilege_type = 'EXECUTE'
            GROUP BY a.grantee
        LOOP
            v_destino := CASE WHEN v_acl.grantee = 0 THEN 'PUBLIC'
                ELSE quote_ident(pg_get_userbyid(v_acl.grantee)) END;
            EXECUTE format('GRANT EXECUTE ON FUNCTION %s TO %s%s', v_firma9, v_destino,
                CASE WHEN v_acl.con_opcion THEN ' WITH GRANT OPTION' ELSE '' END);
        END LOOP;
    END IF;
END;
$instalar$;
COMMIT;
