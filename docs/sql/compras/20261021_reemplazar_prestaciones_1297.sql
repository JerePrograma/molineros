-- Correccion puntual AUTORIZADA del requerimiento 1297. PostgreSQL 9.6+.
-- ASCII, ISO-8859-1 sin BOM. NO EJECUTADO en la base real por Codex.
-- Ejecutar TODO en una conexion nueva, con respaldo y la aplicacion detenida.
-- 3 protesis conservan ID/rubro4. 9 insumos: baja origen + alta rubro6.
-- Cemento cantidad2; las otras11 cantidad1. Sobran3 filas070204: baja logica.
-- Las12 bajas historicas se distinguen en el mapeo:9 reemplazos+3 sobrantes.
-- No cambia estado, configuracion, funciones, triggers, envio ni documentos.
-- Referencias: corregir_item_requerimiento, borrar_requerimiento_detalle,
-- INSERT canonico de 20261013_sector_nomenclador.sql:606.
-- Liquidaciones: EditarLiquidacionServiceImpl.actualizaLiquidacionPrestacionEntry.
-- Leer RESULTADO tras COMMIT: REEMPLAZO_CONFIRMADO / YA_ESTABA_CORREGIDO /
-- ERROR_SIN_CAMBIOS. Un exit0 del cliente no reemplaza esa columna.

BEGIN;
SET LOCAL standard_conforming_strings = on;
SET LOCAL lock_timeout = '10s';
SET LOCAL statement_timeout = '60s';

DROP TABLE IF EXISTS pg_temp.req1297_objetivo;
CREATE TEMP TABLE req1297_objetivo (
    orden INTEGER PRIMARY KEY,
    id_prestacion INTEGER UNIQUE NOT NULL,
    codigo VARCHAR NOT NULL,
    id_tipo_nomenclador INTEGER NOT NULL,
    id_tipo_prestacion INTEGER NOT NULL,
    cantidad INTEGER NOT NULL
) ON COMMIT PRESERVE ROWS;
INSERT INTO req1297_objetivo VALUES
    (1, 11482, '9100046', 14, 4, 1),
    (2, 11483, '9100047', 14, 4, 1),
    (3, 11484, '9100048', 14, 4, 1),
    (4, 11485, '9100049', 10, 6, 1),
    (5, 11486, '9100050', 10, 6, 2),
    (6, 11488, '9100051', 10, 6, 1),
    (7, 11489, '9100052', 10, 6, 1),
    (8, 11490, '9100053', 10, 6, 1),
    (9, 11491, '9100054', 10, 6, 1),
    (10, 11492, '9100055', 10, 6, 1),
    (11, 11493, '9100056', 10, 6, 1),
    (12, 11494, '9100057', 10, 6, 1);

DROP TABLE IF EXISTS pg_temp.req1297_mapeo;
CREATE TEMP TABLE req1297_mapeo (
    orden INTEGER PRIMARY KEY,
    id_detalle_origen INTEGER UNIQUE NOT NULL,
    id_detalle_destino INTEGER,
    id_prestacion INTEGER,
    codigo VARCHAR,
    cantidad INTEGER NOT NULL,
    accion TEXT NOT NULL
) ON COMMIT PRESERVE ROWS;
DROP TABLE IF EXISTS pg_temp.req1297_resultado;
CREATE TEMP TABLE req1297_resultado (
    resultado TEXT, etapa TEXT, sqlstate TEXT, mensaje TEXT,
    detalle TEXT, sugerencia TEXT
) ON COMMIT PRESERVE ROWS;

DO $reemplazo_1297$
DECLARE
    v_etapa TEXT := 'precondiciones';
    v_faltantes TEXT;
    v_cabecera TEXT;
    v_sector INTEGER;
    v_estado INTEGER;
    v_usuario VARCHAR(100);
    v_item RECORD;
    v_id INTEGER;
    v_fuentes INTEGER;
    v_correctos INTEGER;
    v_sqlstate TEXT;
    v_mensaje TEXT;
    v_detalle TEXT;
    v_sugerencia TEXT;
BEGIN
    BEGIN
        IF current_setting('server_version_num')::INTEGER < 90600
           OR current_setting('session_replication_role') <> 'origin' THEN
            RAISE EXCEPTION 'Se requiere PostgreSQL9.6+ y session_replication_role=origin.';
        END IF;
        SELECT string_agg(nombre, ', ' ORDER BY nombre) INTO v_faltantes
        FROM (VALUES
            ('compras.requerimiento'), ('compras.requerimiento_detalle'),
            ('compras.sector_requerimiento'), ('compras.tipo_prestacion'),
            ('compras.sector_nomenclador'), ('autorizaciones.nomenclador'),
            ('compras.requerimiento_reclamo_prestacional'),
            ('compras.requerimiento_presupuesto'),
            ('compras.requerimiento_comparativa'),
            ('compras.requerimiento_comparativa_detalle'),
            ('compras.requerimiento_cotizacion_prestador'),
            ('compras.requerimiento_pedido_cotizacion')
        ) t(nombre) WHERE to_regclass(nombre) IS NULL;
        IF v_faltantes IS NOT NULL THEN
            RAISE EXCEPTION 'Faltan tablas necesarias.' USING DETAIL = v_faltantes;
        END IF;
        SELECT string_agg(firma, E'\n' ORDER BY firma) INTO v_faltantes
        FROM (VALUES
            ('compras.bloquear_requerimiento_reclamo_prestacional(integer)'),
            ('compras.corregir_item_requerimiento(integer,integer,integer,integer,character varying,character varying,character varying)'),
            ('compras.borrar_requerimiento_detalle(integer,character varying)'),
            ('compras.validar_nomenclador_configurado(integer,integer,integer,integer)'),
            ('compras.normalizar_sector(character varying)'),
            ('compras.normalizar_usuario(character varying)')
        ) t(firma) WHERE to_regprocedure(firma) IS NULL;
        IF v_faltantes IS NOT NULL THEN
            RAISE EXCEPTION 'Faltan funciones del circuito vigente.' USING DETAIL = v_faltantes;
        END IF;
        -- Mismo preflight del instalador13/10: no desactivar un trigger
        -- legacy incompatible con las altas puntuales en estado2.
        IF EXISTS (
            SELECT 1 FROM pg_trigger t JOIN pg_proc p ON p.oid = t.tgfoid
            JOIN pg_namespace n ON n.oid = p.pronamespace
            WHERE t.tgrelid = 'compras.requerimiento_detalle'::regclass
              AND NOT t.tgisinternal AND t.tgenabled <> 'D'
              AND n.nspname = 'compras'
              AND p.proname = 'validar_requerimiento_detalle_fila'
        ) THEN
            RAISE EXCEPTION 'Hay un trigger legacy activo incompatible con esta reparacion.'
                USING HINT = 'Relevar la definicion instalada; no desactivar controles para continuar.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM pg_constraint
            WHERE contype = 'f'
              AND confrelid = 'compras.requerimiento_detalle'::regclass
        ) THEN
            RAISE EXCEPTION 'Hay referencias externas a IDs de detalle que requieren revision antes de crear los nueve reemplazos.';
        END IF;

        v_etapa := 'bloqueos y estado';
        PERFORM compras.bloquear_requerimiento_reclamo_prestacional(1297);
        SELECT r.estado, r.id_sector, row_to_json(r)::TEXT
        INTO v_estado, v_sector, v_cabecera
        FROM compras.requerimiento r
        WHERE r.id_requerimiento = 1297 AND r.baja_fecha IS NULL FOR UPDATE;
        IF NOT FOUND OR v_estado <> 2 THEN
            RAISE EXCEPTION 'El requerimiento1297 debe estar activo en ENVIADO A COTIZAR(2).';
        END IF;
        PERFORM 1 FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = 1297 ORDER BY d.id_detalle FOR UPDATE;
        IF EXISTS (SELECT 1 FROM compras.requerimiento_reclamo_prestacional
                   WHERE id_requerimiento = 1297) THEN
            RAISE EXCEPTION 'El requerimiento1297 tiene un RP o una reserva de RP. No se modifica.';
        END IF;
        PERFORM 1 FROM compras.requerimiento_presupuesto
        WHERE id_requerimiento = 1297 ORDER BY id_requerimiento_presupuesto FOR UPDATE;
        PERFORM 1 FROM compras.requerimiento_cotizacion_prestador
        WHERE id_requerimiento = 1297 ORDER BY id_prestador FOR UPDATE;
        PERFORM 1 FROM compras.requerimiento_pedido_cotizacion
        WHERE id_requerimiento = 1297 ORDER BY id_prestador, intento FOR UPDATE;
        IF EXISTS (SELECT 1 FROM compras.requerimiento_presupuesto
                   WHERE id_requerimiento = 1297 AND baja_fecha IS NULL
                     AND tipo_documento IN (1, 3))
           OR EXISTS (SELECT 1 FROM compras.requerimiento_comparativa
                      WHERE id_requerimiento = 1297)
           OR EXISTS (SELECT 1 FROM compras.requerimiento_cotizacion_prestador
                      WHERE id_requerimiento = 1297 AND estado_envio = 'PROCESANDO') THEN
            RAISE EXCEPTION 'Hay cotizaciones, comparativas o un envio en curso. Revisar antes de reemplazar prestaciones.';
        END IF;

        v_etapa := 'catalogo y clasificacion';
        LOCK TABLE compras.sector_nomenclador IN SHARE MODE;
        PERFORM 1 FROM compras.sector_requerimiento
        WHERE id_sector = v_sector AND activo = TRUE AND baja_fecha IS NULL
          AND compras.normalizar_sector(descripcion) = 'PRESTACIONES MEDICAS'
        FOR SHARE;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'El sector no corresponde a PRESTACIONES MEDICAS activo.';
        END IF;
        PERFORM 1 FROM compras.tipo_prestacion
        WHERE id_tipo_prestacion IN (4, 6) ORDER BY id_tipo_prestacion FOR SHARE;
        IF (SELECT count(*) FROM compras.tipo_prestacion
            WHERE id_sector = v_sector
              AND ((id_tipo_prestacion = 4
                    AND compras.normalizar_sector(descripcion) = 'PROTESIS CARDIOLOGIA')
                OR (id_tipo_prestacion = 6
                    AND compras.normalizar_sector(descripcion) = 'INSUMOS'))) <> 2 THEN
            RAISE EXCEPTION 'Los rubros4/6 no coinciden con PROTESIS CARDIOLOGIA/INSUMOS del sector.';
        END IF;
        PERFORM 1 FROM autorizaciones.nomenclador n
        JOIN pg_temp.req1297_objetivo o ON o.id_prestacion = n.id_prestacion
        ORDER BY n.id_prestacion FOR SHARE OF n;
        SELECT string_agg(o.id_prestacion::TEXT || '/' || o.codigo, ', ' ORDER BY o.orden)
        INTO v_faltantes FROM pg_temp.req1297_objetivo o
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = o.id_prestacion
        WHERE n.id_prestacion IS NULL OR n.baja_fecha IS NOT NULL
           OR btrim(n.codigo) IS DISTINCT FROM o.codigo
           OR n.id_tipo_nomenclador IS DISTINCT FROM o.id_tipo_nomenclador
           OR n.marca_rein_liq IS DISTINCT FROM 3::SMALLINT
           OR NULLIF(btrim(n.descripcion), '') IS NULL;
        IF v_faltantes IS NOT NULL THEN
            RAISE EXCEPTION 'Los destinos no coinciden con el catalogo activo proporcionado.'
                USING DETAIL = v_faltantes;
        END IF;
        FOR v_item IN SELECT * FROM pg_temp.req1297_objetivo ORDER BY orden LOOP
            PERFORM compras.validar_nomenclador_configurado(
                v_sector, v_item.id_tipo_prestacion,
                v_item.id_prestacion, v_item.id_tipo_nomenclador);
        END LOOP;
        v_usuario := compras.normalizar_usuario(current_user::VARCHAR);
        IF length(v_usuario) > 100 THEN
            RAISE EXCEPTION 'El usuario SQL supera la longitud admitida para auditoria.';
        END IF;

        v_etapa := 'forma inicial o reejecucion';
        SELECT count(*) INTO v_fuentes FROM compras.requerimiento_detalle
        WHERE id_requerimiento = 1297 AND baja_fecha IS NULL
          AND btrim(codigo_nomenclador) = '070204';
        SELECT count(DISTINCT o.id_prestacion) INTO v_correctos
        FROM pg_temp.req1297_objetivo o
        JOIN compras.requerimiento_detalle d ON d.id_prestacion = o.id_prestacion
        JOIN autorizaciones.nomenclador n ON n.id_prestacion = o.id_prestacion
        WHERE d.id_requerimiento = 1297 AND d.baja_fecha IS NULL
          AND d.tipo_item = 'NOMENCLADOR' AND d.cantidad = o.cantidad
          AND d.id_tipo_prestacion = o.id_tipo_prestacion
          AND d.id_tipo_nomenclador = o.id_tipo_nomenclador
          AND btrim(d.codigo_nomenclador) = o.codigo
          AND btrim(d.descripcion_nomenclador) = btrim(n.descripcion);
        IF v_fuentes = 0 AND v_correctos = 12
           AND (SELECT count(*) FROM compras.requerimiento_detalle d
                JOIN pg_temp.req1297_objetivo o ON o.id_prestacion = d.id_prestacion
                WHERE d.id_requerimiento = 1297 AND d.baja_fecha IS NULL) = 12 THEN
            INSERT INTO pg_temp.req1297_resultado VALUES (
                'YA_ESTABA_CORREGIDO', v_etapa, NULL,
                'Los doce destinos y sus cantidades ya coinciden. No se modificaron datos.',
                NULL, NULL);
            RETURN;
        END IF;
        IF v_fuentes <> 15
           OR (SELECT count(*) FROM compras.requerimiento_detalle
               WHERE id_requerimiento = 1297 AND baja_fecha IS NULL
                 AND btrim(codigo_nomenclador) = '070204' AND cantidad = 1) <> 14
           OR (SELECT count(*) FROM compras.requerimiento_detalle
               WHERE id_requerimiento = 1297 AND baja_fecha IS NULL
                 AND btrim(codigo_nomenclador) = '070204' AND cantidad = 2) <> 1 THEN
            RAISE EXCEPTION 'Se requieren quince filas070204: catorce cantidad1 y una cantidad2. Estado parcial o distinto; no se modifica.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM compras.requerimiento_detalle d
            JOIN pg_temp.req1297_objetivo o ON o.id_prestacion = d.id_prestacion
            WHERE d.id_requerimiento = 1297 AND d.baja_fecha IS NULL
        ) THEN
            RAISE EXCEPTION 'El requerimiento ya contiene algun destino. Revisar el estado parcial.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM compras.requerimiento_detalle d
            WHERE d.id_requerimiento = 1297 AND d.baja_fecha IS NULL
              AND btrim(d.codigo_nomenclador) = '070204'
              AND (d.tipo_item IS DISTINCT FROM 'NOMENCLADOR'
                OR d.id_tipo_prestacion IS DISTINCT FROM 4::SMALLINT
                OR d.id_prestacion IS NULL OR d.id_tipo_nomenclador IS NULL
                OR d.id_medicamento IS NOT NULL OR d.troquel IS NOT NULL
                OR d.nombre_medicamento IS NOT NULL OR d.id_prestador IS NOT NULL
                OR COALESCE(d.precio_unitario_estimado, 0) <> 0
                OR COALESCE(d.precio_total_estimado, 0) <> 0
                OR compras.normalizar_sector(d.descripcion_nomenclador)
                   IS DISTINCT FROM 'DOBLE REEMPLAZO VALVULAR CARDIACO')
        ) OR (SELECT count(DISTINCT id_prestacion) FROM compras.requerimiento_detalle
              WHERE id_requerimiento = 1297 AND baja_fecha IS NULL
                AND btrim(codigo_nomenclador) = '070204') <> 1 THEN
            RAISE EXCEPTION 'Las filas originales difieren de la prestacion/rubro sin cotizar informada.';
        END IF;
        IF (SELECT count(DISTINCT COALESCE(NULLIF(btrim(observaciones), ''), ''))
            FROM compras.requerimiento_detalle
            WHERE id_requerimiento = 1297 AND baja_fecha IS NULL
              AND btrim(codigo_nomenclador) = '070204' AND cantidad = 1) > 1 THEN
            RAISE EXCEPTION 'Las filas cantidad1 tienen observaciones individuales distintas.'
                USING HINT = 'Definir un mapeo por ID de detalle antes de reasignar esas notas.';
        END IF;

        v_etapa := 'snapshot y mapeo';
        CREATE TEMP TABLE req1297_detalles_antes ON COMMIT DROP AS
        SELECT d.* FROM compras.requerimiento_detalle d WHERE d.id_requerimiento = 1297;
        CREATE TEMP TABLE req1297_relacionados_antes ON COMMIT DROP AS
        SELECT 'PRESUPUESTO'::TEXT clase, row_to_json(p)::TEXT datos
        FROM compras.requerimiento_presupuesto p WHERE p.id_requerimiento = 1297
        UNION ALL SELECT 'NOTIFICACION', row_to_json(p)::TEXT
        FROM compras.requerimiento_cotizacion_prestador p WHERE p.id_requerimiento = 1297
        UNION ALL SELECT 'PEDIDO', row_to_json(p)::TEXT
        FROM compras.requerimiento_pedido_cotizacion p WHERE p.id_requerimiento = 1297;
        INSERT INTO pg_temp.req1297_mapeo
            (orden, id_detalle_origen, id_prestacion, codigo, cantidad, accion)
        SELECT o.orden, d.id_detalle, o.id_prestacion, o.codigo, o.cantidad,
               CASE WHEN o.id_tipo_prestacion = 4 THEN 'CORREGIR_MISMO_ID'
                    ELSE 'REEMPLAZAR_COMO_INSUMO' END
        FROM (
            SELECT id_detalle, row_number() OVER (ORDER BY id_detalle) numero
            FROM pg_temp.req1297_detalles_antes
            WHERE baja_fecha IS NULL AND btrim(codigo_nomenclador) = '070204'
              AND cantidad = 1
        ) d
        JOIN (
            SELECT t.*, row_number() OVER (ORDER BY orden) numero
            FROM pg_temp.req1297_objetivo t WHERE cantidad = 1
        ) o ON o.numero = d.numero;
        INSERT INTO pg_temp.req1297_mapeo
            (orden, id_detalle_origen, id_prestacion, codigo, cantidad, accion)
        SELECT o.orden, d.id_detalle, o.id_prestacion, o.codigo, o.cantidad,
               'REEMPLAZAR_COMO_INSUMO'
        FROM pg_temp.req1297_detalles_antes d CROSS JOIN pg_temp.req1297_objetivo o
        WHERE d.baja_fecha IS NULL AND btrim(d.codigo_nomenclador) = '070204'
          AND d.cantidad = 2 AND o.id_prestacion = 11486;
        INSERT INTO pg_temp.req1297_mapeo
            (orden, id_detalle_origen, codigo, cantidad, accion)
        SELECT 100 + row_number() OVER (ORDER BY d.id_detalle)::INTEGER,
               d.id_detalle, '070204', d.cantidad, 'BAJA_SOBRANTE'
        FROM pg_temp.req1297_detalles_antes d
        WHERE d.baja_fecha IS NULL AND btrim(d.codigo_nomenclador) = '070204'
          AND NOT EXISTS (SELECT 1 FROM pg_temp.req1297_mapeo m
                          WHERE m.id_detalle_origen = d.id_detalle);
        IF (SELECT count(*) FROM pg_temp.req1297_mapeo) <> 15
           OR (SELECT count(*) FROM pg_temp.req1297_mapeo
               WHERE accion = 'BAJA_SOBRANTE') <> 3 THEN
            RAISE EXCEPTION 'El mapeo no cubre exactamente doce destinos y tres sobrantes.';
        END IF;

        v_etapa := 'tres correcciones';
        FOR v_item IN
            SELECT m.*, n.id_tipo_nomenclador, n.codigo codigo_real, n.descripcion
            FROM pg_temp.req1297_mapeo m
            JOIN autorizaciones.nomenclador n ON n.id_prestacion = m.id_prestacion
            WHERE m.accion = 'CORREGIR_MISMO_ID' ORDER BY m.orden
        LOOP
            v_id := compras.corregir_item_requerimiento(
                1297, v_item.id_detalle_origen, v_item.id_prestacion,
                v_item.id_tipo_nomenclador, btrim(v_item.codigo_real),
                btrim(v_item.descripcion), v_usuario);
            IF v_id IS DISTINCT FROM v_item.id_detalle_origen THEN
                RAISE EXCEPTION 'La correccion no devolvio el mismo ID de detalle.';
            END IF;
            UPDATE pg_temp.req1297_mapeo SET id_detalle_destino = v_id
            WHERE orden = v_item.orden;
        END LOOP;
        v_etapa := 'doce bajas logicas';
        FOR v_item IN SELECT * FROM pg_temp.req1297_mapeo
                      WHERE accion <> 'CORREGIR_MISMO_ID' ORDER BY id_detalle_origen LOOP
            PERFORM compras.borrar_requerimiento_detalle(v_item.id_detalle_origen, v_usuario);
        END LOOP;
        v_etapa := 'nueve altas de insumos';
        FOR v_item IN
            SELECT m.*, n.id_tipo_nomenclador, n.codigo codigo_real, n.descripcion,
                   d.observaciones, d.precio_unitario_estimado, d.precio_total_estimado
            FROM pg_temp.req1297_mapeo m
            JOIN pg_temp.req1297_detalles_antes d ON d.id_detalle = m.id_detalle_origen
            JOIN autorizaciones.nomenclador n ON n.id_prestacion = m.id_prestacion
            WHERE m.accion = 'REEMPLAZAR_COMO_INSUMO' ORDER BY m.orden
        LOOP
            INSERT INTO compras.requerimiento_detalle (
                id_requerimiento, tipo_item, id_tipo_prestacion,
                id_prestacion, id_tipo_nomenclador, codigo_nomenclador,
                descripcion_nomenclador, id_medicamento, troquel, nombre_medicamento,
                cantidad, precio_unitario_estimado, precio_total_estimado,
                id_prestador, observaciones, alta_usr
            ) VALUES (
                1297, 'NOMENCLADOR', 6, v_item.id_prestacion,
                v_item.id_tipo_nomenclador, btrim(v_item.codigo_real),
                btrim(v_item.descripcion), NULL, NULL, NULL, v_item.cantidad,
                v_item.precio_unitario_estimado, v_item.precio_total_estimado,
                NULL, v_item.observaciones, v_usuario
            ) RETURNING id_detalle INTO v_id;
            UPDATE pg_temp.req1297_mapeo SET id_detalle_destino = v_id
            WHERE orden = v_item.orden;
        END LOOP;

        v_etapa := 'validacion final y constraints diferidas';
        SET CONSTRAINTS ALL IMMEDIATE;
        IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle
                   WHERE id_requerimiento = 1297 AND baja_fecha IS NULL
                     AND btrim(codigo_nomenclador) = '070204')
           OR (SELECT count(DISTINCT o.id_prestacion) FROM pg_temp.req1297_objetivo o
               JOIN compras.requerimiento_detalle d ON d.id_prestacion = o.id_prestacion
               JOIN autorizaciones.nomenclador n ON n.id_prestacion = o.id_prestacion
               WHERE d.id_requerimiento = 1297 AND d.baja_fecha IS NULL
                 AND d.tipo_item = 'NOMENCLADOR' AND d.cantidad = o.cantidad
                 AND d.id_tipo_prestacion = o.id_tipo_prestacion
                 AND d.id_tipo_nomenclador = o.id_tipo_nomenclador
                 AND d.codigo_nomenclador = btrim(n.codigo)
                 AND d.descripcion_nomenclador = btrim(n.descripcion)) <> 12
           OR (SELECT count(*) FROM compras.requerimiento_detalle d
               JOIN pg_temp.req1297_objetivo o ON o.id_prestacion = d.id_prestacion
               WHERE d.id_requerimiento = 1297 AND d.baja_fecha IS NULL) <> 12 THEN
            RAISE EXCEPTION 'El resultado no coincide con las doce prestaciones y cantidades solicitadas.';
        END IF;
        IF (SELECT count(*) FROM pg_temp.req1297_mapeo m
            JOIN compras.requerimiento_detalle d ON d.id_detalle = m.id_detalle_origen
            WHERE m.accion <> 'CORREGIR_MISMO_ID'
              AND d.baja_fecha IS NOT NULL AND d.baja_usr = v_usuario
              AND d.modi_fecha IS NOT NULL AND d.modi_usr = v_usuario) <> 12
           OR (SELECT count(*) FROM pg_temp.req1297_mapeo m
               JOIN compras.requerimiento_detalle d ON d.id_detalle = m.id_detalle_destino
               WHERE m.accion = 'REEMPLAZAR_COMO_INSUMO'
                 AND d.alta_usr = v_usuario AND d.baja_fecha IS NULL) <> 9 THEN
            RAISE EXCEPTION 'La auditoria no confirma doce bajas logicas y nueve altas.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM pg_temp.req1297_mapeo m
            JOIN pg_temp.req1297_detalles_antes a ON a.id_detalle = m.id_detalle_origen
            JOIN compras.requerimiento_detalle d ON d.id_detalle = m.id_detalle_origen
            WHERE m.accion <> 'CORREGIR_MISMO_ID'
              AND (to_jsonb(d) - 'baja_fecha' - 'baja_usr' - 'modi_fecha' - 'modi_usr')
                  IS DISTINCT FROM
                  (to_jsonb(a) - 'baja_fecha' - 'baja_usr' - 'modi_fecha' - 'modi_usr')
        ) OR EXISTS (
            SELECT 1 FROM pg_temp.req1297_mapeo m
            JOIN pg_temp.req1297_detalles_antes a ON a.id_detalle = m.id_detalle_origen
            JOIN compras.requerimiento_detalle d ON d.id_detalle = m.id_detalle_destino
            WHERE m.accion = 'CORREGIR_MISMO_ID'
              AND ((to_jsonb(d) - 'id_prestacion' - 'id_tipo_nomenclador'
                       - 'codigo_nomenclador' - 'descripcion_nomenclador'
                       - 'modi_fecha' - 'modi_usr')
                   IS DISTINCT FROM
                   (to_jsonb(a) - 'id_prestacion' - 'id_tipo_nomenclador'
                       - 'codigo_nomenclador' - 'descripcion_nomenclador'
                       - 'modi_fecha' - 'modi_usr')
                OR d.modi_usr IS DISTINCT FROM v_usuario OR d.modi_fecha IS NULL)
        ) OR EXISTS (
            SELECT 1 FROM pg_temp.req1297_mapeo m
            JOIN pg_temp.req1297_detalles_antes a ON a.id_detalle = m.id_detalle_origen
            JOIN compras.requerimiento_detalle d ON d.id_detalle = m.id_detalle_destino
            WHERE m.accion = 'REEMPLAZAR_COMO_INSUMO'
              AND (ROW(d.cantidad, d.observaciones, d.precio_unitario_estimado,
                       d.precio_total_estimado, d.id_prestador, d.id_medicamento,
                       d.troquel, d.nombre_medicamento)
                   IS DISTINCT FROM
                   ROW(a.cantidad, a.observaciones, a.precio_unitario_estimado,
                       a.precio_total_estimado, a.id_prestador, a.id_medicamento,
                       a.troquel, a.nombre_medicamento))
        ) THEN
            RAISE EXCEPTION 'Se alteraron campos o notas que debian conservarse en los reemplazos.';
        END IF;
        -- Todo detalle ajeno (incluido9999009) debe seguir byte-identico
        -- en su representacion JSON y sin bajas ni alteraciones indirectas.
        IF EXISTS (
            SELECT 1 FROM pg_temp.req1297_detalles_antes a
            WHERE NOT EXISTS (SELECT 1 FROM pg_temp.req1297_mapeo m
                              WHERE m.id_detalle_origen = a.id_detalle)
              AND NOT EXISTS (
                  SELECT 1 FROM compras.requerimiento_detalle d
                  WHERE d.id_detalle = a.id_detalle AND row_to_json(d)::TEXT = row_to_json(a)::TEXT
              )
        ) OR EXISTS (
            SELECT 1 FROM compras.requerimiento_detalle d
            WHERE d.id_requerimiento = 1297
              AND NOT EXISTS (SELECT 1 FROM pg_temp.req1297_detalles_antes a
                              WHERE a.id_detalle = d.id_detalle)
              AND NOT EXISTS (SELECT 1 FROM pg_temp.req1297_mapeo m
                              WHERE m.id_detalle_destino = d.id_detalle)
        ) THEN
            RAISE EXCEPTION 'Se alteraron detalles ajenos o aparecieron filas no previstas.';
        END IF;
        IF (SELECT row_to_json(r)::TEXT FROM compras.requerimiento r
            WHERE r.id_requerimiento = 1297) IS DISTINCT FROM v_cabecera THEN
            RAISE EXCEPTION 'Se altero la cabecera o su estado. Se revierte la operacion.';
        END IF;
        CREATE TEMP TABLE req1297_relacionados_despues ON COMMIT DROP AS
        SELECT 'PRESUPUESTO'::TEXT clase, row_to_json(p)::TEXT datos FROM compras.requerimiento_presupuesto p
            WHERE p.id_requerimiento = 1297
            UNION ALL SELECT 'NOTIFICACION', row_to_json(p)::TEXT
            FROM compras.requerimiento_cotizacion_prestador p WHERE p.id_requerimiento = 1297
            UNION ALL SELECT 'PEDIDO', row_to_json(p)::TEXT
            FROM compras.requerimiento_pedido_cotizacion p WHERE p.id_requerimiento = 1297;
        IF EXISTS (
            SELECT clase, datos FROM pg_temp.req1297_relacionados_antes
            EXCEPT SELECT clase, datos FROM pg_temp.req1297_relacionados_despues
        ) OR EXISTS (
            SELECT clase, datos FROM pg_temp.req1297_relacionados_despues
            EXCEPT SELECT clase, datos FROM pg_temp.req1297_relacionados_antes
        ) THEN
            RAISE EXCEPTION 'Se alteraron documentos o notificaciones originales.';
        END IF;
        INSERT INTO pg_temp.req1297_resultado VALUES (
            'REEMPLAZO_CONFIRMADO', v_etapa, NULL,
            'Doce prestaciones: tres PROTESIS CARDIOLOGIA y nueve INSUMOS; cemento2 y resto1.',
            'Tres IDs conservados, nueve nuevos, nueve bajas por reemplazo y tres por sobrante.',
            NULL);
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_sqlstate = RETURNED_SQLSTATE, v_mensaje = MESSAGE_TEXT,
            v_detalle = PG_EXCEPTION_DETAIL, v_sugerencia = PG_EXCEPTION_HINT;
        INSERT INTO pg_temp.req1297_resultado VALUES (
            'ERROR_SIN_CAMBIOS', v_etapa, v_sqlstate, v_mensaje, v_detalle, v_sugerencia);
    END;
END;
$reemplazo_1297$;

COMMIT;

-- Resultados emitidos DESPUES de COMMIT. Si hay ERROR no hay mapeo aplicado.
SELECT m.accion, m.id_detalle_origen, m.id_detalle_destino,
       m.id_prestacion, m.codigo, n.descripcion, m.cantidad,
       d.id_tipo_prestacion
FROM pg_temp.req1297_mapeo m
LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = m.id_prestacion
LEFT JOIN compras.requerimiento_detalle d ON d.id_detalle = m.id_detalle_destino
ORDER BY m.orden;

SELECT d.id_detalle, d.id_prestacion, d.codigo_nomenclador AS codigo,
       d.descripcion_nomenclador AS descripcion, d.cantidad,
       d.id_tipo_prestacion, t.descripcion AS tipo_cotizacion
FROM compras.requerimiento_detalle d
LEFT JOIN compras.tipo_prestacion t ON t.id_tipo_prestacion = d.id_tipo_prestacion
WHERE d.id_requerimiento = 1297 AND d.baja_fecha IS NULL
ORDER BY d.codigo_nomenclador, d.id_detalle;

-- ULTIMA grilla: resultado confirmado, incluso en pgAdmin.
SELECT resultado, current_database() AS base, current_user AS operador,
       1297 AS id_requerimiento, etapa, sqlstate, mensaje, detalle, sugerencia
FROM pg_temp.req1297_resultado;
