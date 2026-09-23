-- FASE 1: EXPAND / COMPATIBILIDAD. PostgreSQL 9.6.5. ISO-8859-1 sin BOM.
-- SOLO para compras existente. Nunca ejecutar compras_schema.sql como migracion.
-- Ejecutar en ventana sin escrituras con psql -X -v ON_ERROR_STOP=1 -f <archivo>.
-- Ensayo: sustituir UNICAMENTE el COMMIT final por ROLLBACK (ver guia).
-- No elimina datos ni columnas. Los nueve retornos incompatibles se recrean
-- por firma exacta SOLO si el retorno instalado difiere; propietario y permisos
-- se conservan. Es la excepcion expresa a la prohibicion de DROP en esta fase.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '120s';

DO $estructura$
BEGIN
    IF to_regnamespace('compras') IS NULL THEN
        RAISE EXCEPTION 'FASE 1 requiere el schema compras existente';
    END IF;
    IF to_regprocedure('autorizaciones.busca_nomenclador_prest_med_compras(integer,character varying,integer,character varying,boolean,character varying)') IS NULL THEN
        RAISE EXCEPTION 'Falta la funcion externa existente del buscador medico; esta migracion no la crea';
    END IF;
    IF current_setting('server_version_num')::integer < 90600 THEN
        RAISE EXCEPTION 'Se requiere PostgreSQL 9.6';
    END IF;
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE connamespace='compras'::regnamespace AND contype='c') THEN
        RAISE EXCEPTION 'Hay CHECKs no relevados: revisar compatibilidad antes de migrar';
    END IF;
    IF EXISTS (SELECT 1 FROM (VALUES
        ('requerimiento','afiliado_id_ospim'),
        ('requerimiento','afiliado_nombre'),
        ('requerimiento','afiliado_apellido'),
        ('requerimiento','afiliado_documento_tipo'),
        ('requerimiento','afiliado_documento_nro'),
        ('requerimiento','afiliado_direccion'),
        ('requerimiento','afiliado_localidad'),
        ('requerimiento','afiliado_provincia'),
        ('requerimiento','afiliado_celular'),
        ('requerimiento','afiliado_telefono'),
        ('requerimiento','afiliado_email'),
        ('requerimiento','cargo_ospim'),
        ('requerimiento','recupero'),
        ('requerimiento_detalle','id_tipo_nomenclador'),
        ('requerimiento_detalle','codigo_nomenclador'),
        ('requerimiento_detalle','descripcion_nomenclador'),
        ('requerimiento_presupuesto','descripcion_prestador'),
        ('requerimiento_presupuesto','descripcion_empresa')
    ) x(tabla,columna) WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns c WHERE c.table_schema='compras'
          AND c.table_name=x.tabla AND c.column_name=x.columna)) THEN
        RAISE EXCEPTION 'Faltan columnas originales: no corresponde a la base previa a cleanup';
    END IF;
    IF (SELECT count(*) FROM pg_trigger t WHERE NOT t.tgisinternal
        AND t.tgrelid IN ('compras.requerimiento'::regclass,'compras.requerimiento_detalle'::regclass)
        AND t.tgname IN ('trg_compras_requerimiento_completar_baja','trg_compras_detalle_calcular_total')
        AND t.tgenabled='O') <> 2 THEN
        RAISE EXCEPTION 'Faltan los dos triggers activos relevados';
    END IF;
    -- Solo los objetos nuevos heredan owner/ACL del contrato legacy equivalente.
    PERFORM set_config('compras.refactor_objetos_nuevos',concat_ws(',',
        CASE WHEN to_regclass('compras.estado_requerimiento') IS NULL THEN 'estado_requerimiento' END,
        CASE WHEN to_regclass('compras.tipo_prestacion_tipo_nomenclador') IS NULL THEN 'tipo_prestacion_tipo_nomenclador' END,
        CASE WHEN to_regprocedure('compras.id_estado_requerimiento(character varying)') IS NULL THEN 'id_estado_requerimiento' END,
        CASE WHEN to_regprocedure('compras.listar_tipos_nomenclador_compras()') IS NULL THEN 'listar_tipos_nomenclador_compras' END,
        CASE WHEN to_regprocedure('compras.resolver_identidad_afiliado(character varying,integer)') IS NULL THEN 'resolver_identidad_afiliado' END),true);
    RAISE NOTICE 'FASE 1: DB=%, PostgreSQL=%, transaccion=%',current_database(),version(),txid_current();
END $estructura$;

LOCK TABLE
    compras.requerimiento,
    compras.requerimiento_detalle,
    compras.requerimiento_presupuesto,
    compras.requerimiento_cotizacion_prestador,
    compras.requerimiento_pedido_cotizacion,
    compras.requerimiento_reclamo_prestacional
IN SHARE ROW EXCLUSIVE MODE;

DO $huerfanos$
BEGIN
    IF EXISTS (SELECT 1 FROM compras.requerimiento r LEFT JOIN compras.sector_requerimiento s ON s.id_sector=r.id_sector WHERE s.id_sector IS NULL) THEN
        RAISE EXCEPTION 'Requerimientos huerfanos de sector';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.tipo_prestacion t LEFT JOIN compras.sector_requerimiento s ON s.id_sector=t.id_sector WHERE s.id_sector IS NULL) THEN
        RAISE EXCEPTION 'Tipos de prestacion huerfanos de sector';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL)
       OR EXISTS (SELECT 1 FROM compras.requerimiento_presupuesto d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL)
       OR EXISTS (SELECT 1 FROM compras.requerimiento_cotizacion_prestador d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL)
       OR EXISTS (SELECT 1 FROM compras.requerimiento_pedido_cotizacion d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL)
       OR EXISTS (SELECT 1 FROM compras.requerimiento_reclamo_prestacional d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL) THEN
        RAISE EXCEPTION 'Detalles/documentos/cotizaciones/RP huerfanos de requerimiento';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d LEFT JOIN compras.tipo_prestacion t ON t.id_tipo_prestacion=d.id_tipo_prestacion WHERE d.id_tipo_prestacion IS NOT NULL AND t.id_tipo_prestacion IS NULL) THEN
        RAISE EXCEPTION 'Detalles huerfanos de tipo de prestacion';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_pedido_cotizacion d LEFT JOIN compras.requerimiento_cotizacion_prestador c ON c.id_requerimiento=d.id_requerimiento AND c.id_prestador=d.id_prestador WHERE c.id_requerimiento IS NULL) THEN
        RAISE EXCEPTION 'Documentos de pedido huerfanos de relacion de cotizacion';
    END IF;
    RAISE NOTICE 'HUERFANOS internos: 0';
END $huerfanos$;

-- Solo se guardan estadisticas de auditoria en la sesion/transaccion.
-- No se crean tablas auxiliares ni copias de datos. Los hashes son controles
-- adicionales; la conservacion se apoya tambien en la ausencia de DML historico.
DO $conservacion$
DECLARE
    actual JSONB := '{}'::jsonb;
    tabla JSONB;
    pk TEXT[];
    metadata JSONB;
BEGIN
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento',min(t.id_requerimiento), 'max_id_requerimiento',max(t.id_requerimiento),
        'activos',count(*) FILTER(WHERE t.baja_fecha IS NULL),
        'baja',count(*) FILTER(WHERE t.baja_fecha IS NOT NULL),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento t;
    actual := actual || jsonb_build_object('requerimiento',tabla);
    RAISE NOTICE 'PRE requerimiento: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_detalle'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_detalle']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_detalle';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_detalle',min(t.id_detalle), 'max_id_detalle',max(t.id_detalle),
        'activos',count(*) FILTER(WHERE t.baja_fecha IS NULL),
        'baja',count(*) FILTER(WHERE t.baja_fecha IS NOT NULL),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_detalle t;
    actual := actual || jsonb_build_object('requerimiento_detalle',tabla);
    RAISE NOTICE 'PRE requerimiento_detalle: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_presupuesto'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento_presupuesto']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_presupuesto';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento_presupuesto',min(t.id_requerimiento_presupuesto), 'max_id_requerimiento_presupuesto',max(t.id_requerimiento_presupuesto),
        'activos',count(*) FILTER(WHERE t.baja_fecha IS NULL),
        'baja',count(*) FILTER(WHERE t.baja_fecha IS NOT NULL),
        'documentos_dl',count(t.dl_file_entry_id),
        'archivos_dl_distintos',count(DISTINCT t.dl_file_entry_id),
        'presupuestos',count(*) FILTER(WHERE t.tipo_documento=1),
        'ordenes_medicas',count(*) FILTER(WHERE t.tipo_documento=2),
        'cotizaciones_empresa',count(*) FILTER(WHERE t.tipo_documento=3),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_presupuesto t;
    actual := actual || jsonb_build_object('requerimiento_presupuesto',tabla);
    RAISE NOTICE 'PRE requerimiento_presupuesto: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_cotizacion_prestador'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento','id_prestador']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_cotizacion_prestador';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento',min(t.id_requerimiento), 'max_id_requerimiento',max(t.id_requerimiento),
        'min_id_prestador',min(t.id_prestador), 'max_id_prestador',max(t.id_prestador),
        'intentos',COALESCE(sum(t.intentos),0),
        'emails_reservados',count(t.email_destino),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_cotizacion_prestador t;
    actual := actual || jsonb_build_object('requerimiento_cotizacion_prestador',tabla);
    RAISE NOTICE 'PRE requerimiento_cotizacion_prestador: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_pedido_cotizacion'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento','id_prestador','intento']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_pedido_cotizacion';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento',min(t.id_requerimiento), 'max_id_requerimiento',max(t.id_requerimiento),
        'min_id_prestador',min(t.id_prestador), 'max_id_prestador',max(t.id_prestador),
        'min_intento',min(t.intento), 'max_intento',max(t.intento),
        'documentos_dl',count(t.dl_file_entry_id),
        'archivos_dl_distintos',count(DISTINCT t.dl_file_entry_id),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_pedido_cotizacion t;
    actual := actual || jsonb_build_object('requerimiento_pedido_cotizacion',tabla);
    RAISE NOTICE 'PRE requerimiento_pedido_cotizacion: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_reclamo_prestacional'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_reclamo_prestacional';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento',min(t.id_requerimiento), 'max_id_requerimiento',max(t.id_requerimiento),
        'relaciones_rp',count(t.id_reclamo_prestacional),
        'tokens_reservados',count(t.token_reserva),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_reclamo_prestacional t;
    actual := actual || jsonb_build_object('requerimiento_reclamo_prestacional',tabla);
    RAISE NOTICE 'PRE requerimiento_reclamo_prestacional: %',tabla;
    -- OIDs, definiciones y atributos fisicos de las seis tablas se conservan.
    -- Las nuevas FKs se verifican aparte, por eso no se comparan aqui.
    SELECT jsonb_build_object(
        'columnas',(SELECT jsonb_agg(row_to_json(x) ORDER BY x.tabla,x.attnum)
            FROM (SELECT c.relname tabla,a.attnum,a.attname,a.atttypid,a.atttypmod,
                         a.attnotnull,pg_get_expr(d.adbin,d.adrelid) defecto
                  FROM pg_class c JOIN pg_attribute a ON a.attrelid=c.oid
                  LEFT JOIN pg_attrdef d ON d.adrelid=a.attrelid AND d.adnum=a.attnum
                  WHERE c.relnamespace='compras'::regnamespace
                    AND c.relname = ANY(ARRAY['requerimiento','requerimiento_detalle','requerimiento_presupuesto','requerimiento_cotizacion_prestador','requerimiento_pedido_cotizacion','requerimiento_reclamo_prestacional'])
                    AND a.attnum>0 AND NOT a.attisdropped) x),
        'pks',(SELECT jsonb_agg(row_to_json(x) ORDER BY x.oid)
            FROM (SELECT c.oid,c.conrelid,c.conindid,pg_get_constraintdef(c.oid) definicion
                  FROM pg_constraint c WHERE c.contype='p'
                    AND c.conrelid = ANY(ARRAY['compras.requerimiento'::regclass,'compras.requerimiento_detalle'::regclass,'compras.requerimiento_presupuesto'::regclass,'compras.requerimiento_cotizacion_prestador'::regclass,'compras.requerimiento_pedido_cotizacion'::regclass,'compras.requerimiento_reclamo_prestacional'::regclass])) x),
        'indices',(SELECT jsonb_agg(row_to_json(x) ORDER BY x.indexrelid)
            FROM (SELECT i.indexrelid,pg_get_indexdef(i.indexrelid) definicion
                  FROM pg_index i WHERE i.indrelid = ANY(ARRAY['compras.requerimiento'::regclass,'compras.requerimiento_detalle'::regclass,'compras.requerimiento_presupuesto'::regclass,'compras.requerimiento_cotizacion_prestador'::regclass,'compras.requerimiento_pedido_cotizacion'::regclass,'compras.requerimiento_reclamo_prestacional'::regclass])) x),
        'triggers',(SELECT jsonb_agg(row_to_json(x) ORDER BY x.oid)
            FROM (SELECT t.oid,t.tgenabled,pg_get_triggerdef(t.oid) definicion
                  FROM pg_trigger t WHERE NOT t.tgisinternal
                    AND t.tgrelid = ANY(ARRAY['compras.requerimiento'::regclass,'compras.requerimiento_detalle'::regclass,'compras.requerimiento_presupuesto'::regclass,'compras.requerimiento_cotizacion_prestador'::regclass,'compras.requerimiento_pedido_cotizacion'::regclass,'compras.requerimiento_reclamo_prestacional'::regclass])) x))
    INTO metadata;
    actual := actual || jsonb_build_object('estructura',metadata);
    PERFORM set_config('compras.refactor_conservacion',actual::text,true);
END $conservacion$;

DO $pre$ BEGIN
    IF current_setting('server_version_num')::integer < 90600 THEN
        RAISE EXCEPTION 'Se requiere PostgreSQL 9.6 o superior';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento WHERE cargo_tercerizadora IS NULL OR cargo_tercerizadora NOT BETWEEN 0 AND 100) THEN
        RAISE EXCEPTION 'Cargos fuera de rango: revisar antes de migrar';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento WHERE estado NOT IN (1,2,3,4,5,99)) THEN
        RAISE EXCEPTION 'Estado no catalogado: revisar antes de migrar';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.sector_requerimiento WHERE id_sector NOT IN (1,2,3,4,5,6)) THEN
        RAISE EXCEPTION 'Sector sin configuracion relevada';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion=d.id_prestacion WHERE d.tipo_item='NOMENCLADOR' AND n.id_prestacion IS NULL) THEN
        RAISE EXCEPTION 'Detalle sin maestro de nomenclador';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_presupuesto d LEFT JOIN public.prestador p ON p.id_prestador=d.id_prestador WHERE d.id_prestador > 0 AND p.id_prestador IS NULL) THEN
        RAISE EXCEPTION 'Documento sin maestro de prestador';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_presupuesto d LEFT JOIN informacion_afip.empresa e ON e.cuit=d.empresa_cuit AND e.sucursal=d.empresa_sucursal WHERE NULLIF(btrim(d.empresa_cuit),'') IS NOT NULL AND e.cuit IS NULL) THEN
        RAISE EXCEPTION 'Documento sin maestro de empresa por CUIT y sucursal';
    END IF;
    IF EXISTS (SELECT 1 FROM pg_trigger t JOIN pg_proc p ON p.oid=t.tgfoid JOIN pg_namespace n ON n.oid=p.pronamespace WHERE NOT t.tgisinternal AND n.nspname='compras' AND p.proname IN ('validar_requerimiento_fila','validar_requerimiento_detalle_fila','validar_tipo_prestacion_detalle_fila','validar_tipo_prestacion_detalle_nuevo','validar_prestador_cotizacion_fila')) THEN
        RAISE EXCEPTION 'Hay validadores conectados a triggers: no coincide con el relevamiento';
    END IF;
END $pre$;
-- Diagnostico solamente: conserva recupero almacenado y los cargos sin UPDATE.
DO $diagnostico$
DECLARE discrepancia RECORD;
BEGIN
    IF EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid='compras.requerimiento'::regclass
          AND attname='recupero' AND NOT attisdropped) THEN
        FOR discrepancia IN EXECUTE
            'SELECT id_requerimiento, cargo_tercerizadora, recupero
             FROM compras.requerimiento
             WHERE recupero IS DISTINCT FROM (cargo_tercerizadora > 0)
             ORDER BY id_requerimiento'
        LOOP
            RAISE NOTICE 'RECUPERO discrepante: requerimiento=%, cargo_tercerizadora=%, recupero=%',
                discrepancia.id_requerimiento, discrepancia.cargo_tercerizadora, discrepancia.recupero;
        END LOOP;
    END IF;
END $diagnostico$;

CREATE TABLE IF NOT EXISTS compras.estado_requerimiento (
    id_estado INTEGER PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(80) NOT NULL,
    orden SMALLINT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    descripcion_visual VARCHAR(80)
);
INSERT INTO compras.estado_requerimiento
    (id_estado, codigo, descripcion, orden, activo, descripcion_visual)
VALUES
    (1, 'PENDIENTE', 'PENDIENTE', 1, TRUE, NULL),
    (2, 'A_COTIZAR', 'A COTIZAR', 2, TRUE, 'ENVIADO A COTIZAR'),
    (3, 'COTIZADO', 'COTIZADO', 3, TRUE, NULL),
    (4, 'RECLAMO_RP', 'RECLAMO (RP)', 4, TRUE, NULL),
    (5, 'ORDEN_COMPRA', 'ORDEN DE COMPRA', 5, TRUE, NULL),
    (99, 'ANULADO', 'ANULADO', 6, TRUE, NULL)
ON CONFLICT (id_estado) DO NOTHING;

ALTER TABLE compras.sector_requerimiento
    ADD COLUMN IF NOT EXISTS tipo_item VARCHAR(20),
    ADD COLUMN IF NOT EXISTS seleccionable_alta BOOLEAN,
    ADD COLUMN IF NOT EXISTS permite_cotizacion_empresa BOOLEAN,
    ADD COLUMN IF NOT EXISTS permite_orden_compra_directa BOOLEAN,
    ADD COLUMN IF NOT EXISTS busqueda_nomenclador_medica BOOLEAN,
    ADD COLUMN IF NOT EXISTS permite_medicamento_legacy BOOLEAN,
    ADD COLUMN IF NOT EXISTS sector_reclamo_prestacional VARCHAR(40);

-- Los tres ultimos atributos conservan el buscador y el contrato de RP.
UPDATE compras.sector_requerimiento s
SET tipo_item = c.tipo_item,
    seleccionable_alta = c.alta,
    permite_cotizacion_empresa = c.empresa,
    permite_orden_compra_directa = c.oc,
    busqueda_nomenclador_medica = c.medica,
    permite_medicamento_legacy = c.medicamento,
    sector_reclamo_prestacional = c.rp
FROM (VALUES
    (1, 'NOMENCLADOR', TRUE, FALSE, FALSE, FALSE, TRUE, 'FARMACIA'),
    (2, 'NOMENCLADOR', TRUE, FALSE, FALSE, TRUE, FALSE, 'PRESTACIONES MEDICAS'),
    (3, 'OBSERVACION', TRUE, TRUE, TRUE, FALSE, FALSE, NULL),
    (4, 'OBSERVACION', TRUE, TRUE, TRUE, FALSE, FALSE, NULL),
    (5, 'OBSERVACION', FALSE, FALSE, FALSE, FALSE, FALSE, 'LEGALES'),
    (6, 'OBSERVACION', FALSE, FALSE, FALSE, FALSE, FALSE, NULL)
) c(id, tipo_item, alta, empresa, oc, medica, medicamento, rp)
WHERE s.id_sector = c.id AND s.tipo_item IS NULL;
ALTER TABLE compras.sector_requerimiento
    ALTER COLUMN tipo_item SET NOT NULL,
    ALTER COLUMN seleccionable_alta SET NOT NULL,
    ALTER COLUMN permite_cotizacion_empresa SET NOT NULL,
    ALTER COLUMN permite_orden_compra_directa SET NOT NULL,
    ALTER COLUMN busqueda_nomenclador_medica SET NOT NULL,
    ALTER COLUMN permite_medicamento_legacy SET NOT NULL;

ALTER TABLE compras.tipo_prestacion ADD COLUMN IF NOT EXISTS rubro_prestador VARCHAR(120);
UPDATE compras.tipo_prestacion t SET rubro_prestador = c.rubro
FROM (VALUES (1, 'ALIMENTACION'), (2, 'MEDICAMENTOS'),
    (3, 'PROTESIS_TRAUMATOLOGIA'), (4, 'PROTESIS_CARDIOLOGIA'),
    (5, 'PROTESIS_GENERAL'), (6, 'INSUMOS'), (7, 'PAÑALES')) c(id, rubro)
WHERE t.id_tipo_prestacion = c.id AND t.rubro_prestador IS NULL;
ALTER TABLE compras.tipo_prestacion ALTER COLUMN rubro_prestador SET NOT NULL;

CREATE TABLE IF NOT EXISTS compras.tipo_prestacion_tipo_nomenclador (
    id_tipo_prestacion SMALLINT NOT NULL,
    id_tipo_nomenclador INTEGER NOT NULL,
    PRIMARY KEY (id_tipo_prestacion, id_tipo_nomenclador)
);
-- Universo del runtime: Farmacia 9; Protesis 2/3/4/6/14;
-- Panales 2/3/4/6; Insumos 10 y drift maestro confirmado a 14.
INSERT INTO compras.tipo_prestacion_tipo_nomenclador (id_tipo_prestacion,id_tipo_nomenclador) VALUES
    (1,9),(2,9),
    (3,2),(3,3),(3,4),(3,6),(3,14),
    (4,2),(4,3),(4,4),(4,6),(4,14),
    (5,2),(5,3),(5,4),(5,6),(5,14),
    (6,10),(6,14),(7,2),(7,3),(7,4),(7,6)
ON CONFLICT DO NOTHING;

-- Todas las referencias se verifican antes de agregar/validar FKs.
-- NOT VALID separa instalacion de validacion; no evita la ventana de bloqueo
-- requerida por la auditoria transaccional de conservacion.
DO $fk$
DECLARE existente NAME;
BEGIN
    IF EXISTS (SELECT 1 FROM compras.requerimiento d LEFT JOIN compras.sector_requerimiento p ON p.id_sector=d.id_sector
               WHERE d.id_sector IS NOT NULL AND p.id_sector IS NULL) THEN
        RAISE EXCEPTION 'Huerfanos: requerimiento.id_sector -> sector_requerimiento.id_sector';
    END IF;
    SELECT c.conname INTO existente FROM pg_constraint c
    WHERE c.conrelid='compras.requerimiento'::regclass AND c.contype='f'
      AND c.confrelid='compras.sector_requerimiento'::regclass
      AND c.conkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.conrelid AND attname='id_sector')]::smallint[]
      AND c.confkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.confrelid AND attname='id_sector')]::smallint[];
    IF existente IS NULL THEN
        ALTER TABLE compras.requerimiento ADD CONSTRAINT fk_compras_requerimiento_id_sector
            FOREIGN KEY (id_sector) REFERENCES compras.sector_requerimiento(id_sector) NOT VALID;
        existente := 'fk_compras_requerimiento_id_sector';
    END IF;
    EXECUTE format('ALTER TABLE compras.requerimiento VALIDATE CONSTRAINT %I',existente);
END $fk$;

DO $fk$
DECLARE existente NAME;
BEGIN
    IF EXISTS (SELECT 1 FROM compras.requerimiento d LEFT JOIN compras.estado_requerimiento p ON p.id_estado=d.estado
               WHERE d.estado IS NOT NULL AND p.id_estado IS NULL) THEN
        RAISE EXCEPTION 'Huerfanos: requerimiento.estado -> estado_requerimiento.id_estado';
    END IF;
    SELECT c.conname INTO existente FROM pg_constraint c
    WHERE c.conrelid='compras.requerimiento'::regclass AND c.contype='f'
      AND c.confrelid='compras.estado_requerimiento'::regclass
      AND c.conkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.conrelid AND attname='estado')]::smallint[]
      AND c.confkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.confrelid AND attname='id_estado')]::smallint[];
    IF existente IS NULL THEN
        ALTER TABLE compras.requerimiento ADD CONSTRAINT fk_compras_requerimiento_estado
            FOREIGN KEY (estado) REFERENCES compras.estado_requerimiento(id_estado) NOT VALID;
        existente := 'fk_compras_requerimiento_estado';
    END IF;
    EXECUTE format('ALTER TABLE compras.requerimiento VALIDATE CONSTRAINT %I',existente);
END $fk$;

DO $fk$
DECLARE existente NAME;
BEGIN
    IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d LEFT JOIN compras.requerimiento p ON p.id_requerimiento=d.id_requerimiento
               WHERE d.id_requerimiento IS NOT NULL AND p.id_requerimiento IS NULL) THEN
        RAISE EXCEPTION 'Huerfanos: requerimiento_detalle.id_requerimiento -> requerimiento.id_requerimiento';
    END IF;
    SELECT c.conname INTO existente FROM pg_constraint c
    WHERE c.conrelid='compras.requerimiento_detalle'::regclass AND c.contype='f'
      AND c.confrelid='compras.requerimiento'::regclass
      AND c.conkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.conrelid AND attname='id_requerimiento')]::smallint[]
      AND c.confkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.confrelid AND attname='id_requerimiento')]::smallint[];
    IF existente IS NULL THEN
        ALTER TABLE compras.requerimiento_detalle ADD CONSTRAINT fk_compras_requerimiento_detalle_id_requerimiento
            FOREIGN KEY (id_requerimiento) REFERENCES compras.requerimiento(id_requerimiento) NOT VALID;
        existente := 'fk_compras_requerimiento_detalle_id_requerimiento';
    END IF;
    EXECUTE format('ALTER TABLE compras.requerimiento_detalle VALIDATE CONSTRAINT %I',existente);
END $fk$;

DO $fk$
DECLARE existente NAME;
BEGIN
    IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d LEFT JOIN compras.tipo_prestacion p ON p.id_tipo_prestacion=d.id_tipo_prestacion
               WHERE d.id_tipo_prestacion IS NOT NULL AND p.id_tipo_prestacion IS NULL) THEN
        RAISE EXCEPTION 'Huerfanos: requerimiento_detalle.id_tipo_prestacion -> tipo_prestacion.id_tipo_prestacion';
    END IF;
    SELECT c.conname INTO existente FROM pg_constraint c
    WHERE c.conrelid='compras.requerimiento_detalle'::regclass AND c.contype='f'
      AND c.confrelid='compras.tipo_prestacion'::regclass
      AND c.conkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.conrelid AND attname='id_tipo_prestacion')]::smallint[]
      AND c.confkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.confrelid AND attname='id_tipo_prestacion')]::smallint[];
    IF existente IS NULL THEN
        ALTER TABLE compras.requerimiento_detalle ADD CONSTRAINT fk_compras_requerimiento_detalle_id_tipo_prestacion
            FOREIGN KEY (id_tipo_prestacion) REFERENCES compras.tipo_prestacion(id_tipo_prestacion) NOT VALID;
        existente := 'fk_compras_requerimiento_detalle_id_tipo_prestacion';
    END IF;
    EXECUTE format('ALTER TABLE compras.requerimiento_detalle VALIDATE CONSTRAINT %I',existente);
END $fk$;

DO $fk$
DECLARE existente NAME;
BEGIN
    IF EXISTS (SELECT 1 FROM compras.tipo_prestacion_tipo_nomenclador d LEFT JOIN compras.tipo_prestacion p ON p.id_tipo_prestacion=d.id_tipo_prestacion
               WHERE d.id_tipo_prestacion IS NOT NULL AND p.id_tipo_prestacion IS NULL) THEN
        RAISE EXCEPTION 'Huerfanos: tipo_prestacion_tipo_nomenclador.id_tipo_prestacion -> tipo_prestacion.id_tipo_prestacion';
    END IF;
    SELECT c.conname INTO existente FROM pg_constraint c
    WHERE c.conrelid='compras.tipo_prestacion_tipo_nomenclador'::regclass AND c.contype='f'
      AND c.confrelid='compras.tipo_prestacion'::regclass
      AND c.conkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.conrelid AND attname='id_tipo_prestacion')]::smallint[]
      AND c.confkey=ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid=c.confrelid AND attname='id_tipo_prestacion')]::smallint[];
    IF existente IS NULL THEN
        ALTER TABLE compras.tipo_prestacion_tipo_nomenclador ADD CONSTRAINT fk_compras_tipo_prestacion_tipo_nomenclador_id_tipo_prestacion
            FOREIGN KEY (id_tipo_prestacion) REFERENCES compras.tipo_prestacion(id_tipo_prestacion) NOT VALID;
        existente := 'fk_compras_tipo_prestacion_tipo_nomenclador_id_tipo_prestacion';
    END IF;
    EXECUTE format('ALTER TABLE compras.tipo_prestacion_tipo_nomenclador VALIDATE CONSTRAINT %I',existente);
END $fk$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'sector_tipo_item' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE sector_tipo_item varchar;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'sector_seleccionable_alta' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE sector_seleccionable_alta boolean;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'sector_permite_cotizacion_empresa' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE sector_permite_cotizacion_empresa boolean;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'sector_permite_orden_compra_directa' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE sector_permite_orden_compra_directa boolean;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'sector_busqueda_nomenclador_medica' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE sector_busqueda_nomenclador_medica boolean;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'sector_permite_medicamento_legacy' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE sector_permite_medicamento_legacy boolean;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'sector_sector_reclamo_prestacional' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE sector_sector_reclamo_prestacional varchar;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'estado_codigo' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE estado_codigo varchar;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'estado_descripcion_visual' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE estado_descripcion_visual varchar;
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_attribute
        WHERE attrelid = (SELECT typrelid FROM pg_type WHERE oid = 'compras.requerimiento_base_row'::regtype)
          AND attname = 'sector_nomencladores' AND NOT attisdropped) THEN
        ALTER TYPE compras.requerimiento_base_row ADD ATTRIBUTE sector_nomencladores integer[];
    END IF;
END $ddl$;


CREATE OR REPLACE FUNCTION compras.id_estado_requerimiento(p_codigo VARCHAR)
RETURNS INTEGER LANGUAGE sql STABLE AS $function$
    SELECT e.id_estado FROM compras.estado_requerimiento e WHERE e.codigo = p_codigo;
$function$;

CREATE OR REPLACE FUNCTION compras.listar_tipos_nomenclador_compras()
RETURNS TABLE(id_tipo_nomenclador INTEGER, descripcion VARCHAR)
LANGUAGE sql STABLE AS $function$
    SELECT n.id_tipo_nomenclador::integer, n.descripcion::varchar
    FROM public.trae_tipos_nomenclador() n
    WHERE EXISTS (SELECT 1 FROM compras.tipo_prestacion_tipo_nomenclador c
                  WHERE c.id_tipo_nomenclador = n.id_tipo_nomenclador)
    ORDER BY n.id_tipo_nomenclador;
$function$;

CREATE OR REPLACE FUNCTION compras.resolver_identidad_afiliado(p_cuil VARCHAR, p_inte INTEGER)
RETURNS TABLE(cuil_titular VARCHAR, inte INTEGER)
LANGUAGE sql STABLE AS $function$
    -- Solo seguir cambios efectivos. Evita ciclos y no fabrica otra identidad.
    WITH RECURSIVE identidad(cuil, integrante, fecha, recorrido) AS (
        SELECT p_cuil, p_inte, '-infinity'::timestamp,
               ARRAY[p_cuil || ':' || p_inte::text]
        UNION ALL
        SELECT c.cuil_titular::varchar, c.inte, c.vigen_fecha::timestamp,
               i.recorrido || (c.cuil_titular || ':' || c.inte::text)
        FROM identidad i
        JOIN public.afi_cambio_cuil c
          ON c.cuil_titular_anterior = i.cuil AND c.inte_anterior = i.integrante
        WHERE c.vigen_fecha <= CURRENT_TIMESTAMP AND c.vigen_fecha >= i.fecha
          AND NOT (c.cuil_titular || ':' || c.inte::text) = ANY(i.recorrido)
    ), actuales AS (
        SELECT DISTINCT i.cuil,i.integrante,
            dense_rank() OVER (ORDER BY array_length(i.recorrido,1) DESC,i.fecha DESC) prioridad
        FROM identidad i
        JOIN public.afiliado a ON a.cuil_titular=i.cuil AND a.inte=i.integrante
    )
    SELECT a.cuil,a.integrante FROM actuales a
    WHERE a.prioridad=1 AND (SELECT count(*) FROM actuales x WHERE x.prioridad=1)=1;
$function$;

DO $maestros$ BEGIN
    IF EXISTS (SELECT 1 FROM compras.requerimiento r
        LEFT JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) a ON TRUE
        WHERE r.afiliado_cuil_titular IS NOT NULL AND a.cuil_titular IS NULL) THEN
        RAISE EXCEPTION 'Afiliado sin identidad actual resoluble';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d
        JOIN autorizaciones.nomenclador n ON n.id_prestacion=d.id_prestacion
        WHERE d.tipo_item='NOMENCLADOR' AND d.id_tipo_prestacion IS NOT NULL
          AND NOT EXISTS (SELECT 1 FROM compras.tipo_prestacion_tipo_nomenclador c
              WHERE c.id_tipo_prestacion=d.id_tipo_prestacion AND c.id_tipo_nomenclador=n.id_tipo_nomenclador)) THEN
        RAISE EXCEPTION 'Clasificacion existente fuera de la configuracion de nomencladores';
    END IF;
END $maestros$;

CREATE OR REPLACE FUNCTION compras.anular_requerimiento(p_id_requerimiento integer, p_motivo_baja text, p_usuario character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_estado_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

    SELECT r.estado
      INTO v_estado_actual
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento.';
    END IF;

    IF NOT (
            (v_estado_actual = compras.id_estado_requerimiento('PENDIENTE'))
         OR (v_estado_actual = compras.id_estado_requerimiento('A_COTIZAR'))
    ) THEN
        RAISE EXCEPTION
            'El requerimiento no puede anularse desde el estado actual.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = compras.id_estado_requerimiento('ANULADO'),
           motivo_baja = COALESCE(p_motivo_baja, ''),
           modi_fecha = now(),
           modi_usr = v_usuario,
           baja_fecha = now(),
           baja_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.baja_cotizacion_empresa_requerimiento(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer, p_usuario character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_estado_requerimiento INTEGER;
    v_permite_operacion BOOLEAN;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RETURN FALSE;
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT
        r.estado,
        sr.permite_cotizacion_empresa
      INTO
        v_estado_requerimiento,
        v_permite_operacion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> compras.id_estado_requerimiento('PENDIENTE')
       OR NOT v_permite_operacion THEN

        RAISE EXCEPTION
            'Las cotizaciones de Empresas solo pueden eliminarse '
            'en requerimientos PENDIENTES de RRHH o SISTEMAS.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET baja_fecha = now(),
           baja_usr = v_usuario
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3
       AND baja_fecha IS NULL;

    RETURN FOUND;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.baja_requerimiento_presupuesto(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer, p_usuario character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id_prestador INTEGER;
    v_estado_requerimiento INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN
        RETURN FALSE;
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT r.estado
      INTO v_estado_requerimiento
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND OR v_estado_requerimiento <> compras.id_estado_requerimiento('A_COTIZAR') THEN
        RAISE EXCEPTION
            'Los presupuestos solo pueden eliminarse en estado A COTIZAR.';
    END IF;

    SELECT rp.id_prestador
      INTO v_id_prestador
      FROM compras.requerimiento_presupuesto rp
     WHERE rp.id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND rp.id_requerimiento = p_id_requerimiento
       AND rp.tipo_documento = 1
       AND rp.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    PERFORM 1
      FROM compras.requerimiento_cotizacion_prestador rcp
     WHERE rcp.id_requerimiento = p_id_requerimiento
       AND rcp.id_prestador = v_id_prestador
     FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el prestador notificado asociado al presupuesto.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET baja_fecha = now(),
           baja_usr = v_usuario
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 1
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 1
           AND rp.id_prestador = v_id_prestador
           AND rp.baja_fecha IS NULL
    ) THEN
        UPDATE compras.requerimiento_cotizacion_prestador
           SET estado_envio = 'ENVIADO',
               modi_fecha = now(),
               modi_usr = v_usuario
         WHERE id_requerimiento = p_id_requerimiento
           AND id_prestador = v_id_prestador
           AND estado_envio = 'COTIZADO';

        IF NOT FOUND THEN
            RAISE EXCEPTION
                'No se pudo restaurar el estado ENVIADO del prestador.';
        END IF;
    END IF;

    RETURN TRUE;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.borrar_requerimiento_detalle(p_id_detalle integer, p_usuario character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id_requerimiento INTEGER;
    v_estado INTEGER;
    v_total_detalles_activos INTEGER;
    v_usuario VARCHAR(100);
BEGIN

    IF p_id_detalle IS NULL
       OR p_id_detalle <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el detalle del requerimiento.';

    END IF;


    v_usuario :=
        compras.normalizar_usuario(
            p_usuario
        );


    SELECT d.id_requerimiento
    INTO v_id_requerimiento
    FROM compras.requerimiento_detalle d
    WHERE d.id_detalle = p_id_detalle
      AND d.baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontró el detalle activo a borrar.';

    END IF;


    SELECT r.estado
    INTO v_estado
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = v_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo del detalle.';

    END IF;


    IF v_estado NOT IN (compras.id_estado_requerimiento('PENDIENTE'), compras.id_estado_requerimiento('A_COTIZAR')) THEN
        RAISE EXCEPTION
            'Los detalles solo pueden borrarse en estado PENDIENTE o ENVIADO A COTIZAR.';

    END IF;


    PERFORM 1
    FROM compras.requerimiento_detalle d
    WHERE d.id_detalle = p_id_detalle
      AND d.id_requerimiento = v_id_requerimiento
      AND d.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El detalle ya no se encuentra activo.';

    END IF;


    IF v_estado = compras.id_estado_requerimiento('A_COTIZAR') THEN

        SELECT count(*)
        INTO v_total_detalles_activos
        FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = v_id_requerimiento
          AND d.baja_fecha IS NULL;

        IF v_total_detalles_activos <= 1 THEN
            RAISE EXCEPTION
                'El requerimiento ENVIADO A COTIZAR debe conservar al menos una prestación.';

        END IF;

    END IF;


    UPDATE compras.requerimiento_detalle
    SET baja_fecha = now(),
        baja_usr = v_usuario,
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_detalle = p_id_detalle
      AND id_requerimiento = v_id_requerimiento
      AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El detalle ya no se encuentra activo.';

    END IF;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.buscar_items_historicos_afiliado(p_cuil_titular character varying, p_inte integer, p_id_sector integer, p_id_requerimiento_excluir integer, p_limite integer)
 RETURNS TABLE(id_prestacion integer, id_tipo_nomenclador integer, codigo character varying, descripcion character varying)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        historico.id_prestacion,
        historico.id_tipo_nomenclador,
        historico.codigo,
        historico.descripcion
    FROM (
        SELECT DISTINCT ON (d.id_prestacion, n.id_tipo_nomenclador)
            d.id_prestacion,
            n.id_tipo_nomenclador,
            NULLIF(BTRIM(n.codigo), '') AS codigo,
            NULLIF(BTRIM(n.descripcion), '') AS descripcion,
            r.alta_fecha AS fecha_requerimiento,
            r.id_requerimiento AS id_requerimiento_origen,
            d.id_detalle AS id_detalle_origen
        FROM compras.requerimiento r
        JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
        INNER JOIN compras.requerimiento_detalle d
            ON d.id_requerimiento = r.id_requerimiento
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
        WHERE ident.cuil_titular = p_cuil_titular
          AND ident.inte = p_inte
          AND r.id_sector = p_id_sector
          AND r.id_requerimiento <> p_id_requerimiento_excluir
          AND r.baja_fecha IS NULL
          AND d.baja_fecha IS NULL
          AND d.tipo_item = 'NOMENCLADOR'
          AND d.id_prestacion IS NOT NULL
          AND d.id_prestacion > 0
          AND n.id_tipo_nomenclador IS NOT NULL
          AND n.id_tipo_nomenclador > 0
          AND NULLIF(BTRIM(n.codigo), '') IS NOT NULL
          AND NULLIF(BTRIM(n.descripcion), '') IS NOT NULL
        ORDER BY
            d.id_prestacion,
            n.id_tipo_nomenclador,
            r.alta_fecha DESC NULLS LAST,
            r.id_requerimiento DESC,
            d.id_detalle DESC
    ) historico
    ORDER BY
        historico.fecha_requerimiento DESC NULLS LAST,
        historico.id_requerimiento_origen DESC,
        historico.id_detalle_origen DESC
    LIMIT p_limite;
$function$;

CREATE OR REPLACE FUNCTION compras.buscar_items_historicos_afiliado_clasificado(p_cuil_titular character varying, p_inte integer, p_id_sector integer, p_id_requerimiento_excluir integer, p_limite integer)
 RETURNS TABLE(id_prestacion integer, id_tipo_nomenclador integer, codigo character varying, descripcion character varying, id_tipo_prestacion integer)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        historico.id_prestacion,
        historico.id_tipo_nomenclador,
        historico.codigo,
        historico.descripcion,
        historico.id_tipo_prestacion
    FROM (
        SELECT DISTINCT ON (d.id_prestacion, n.id_tipo_nomenclador)
            d.id_prestacion,
            n.id_tipo_nomenclador,
            NULLIF(BTRIM(n.codigo), '')::VARCHAR AS codigo,
            NULLIF(BTRIM(n.descripcion), '')::VARCHAR AS descripcion,
            d.id_tipo_prestacion::INTEGER AS id_tipo_prestacion,
            r.alta_fecha AS fecha_requerimiento,
            r.id_requerimiento AS id_requerimiento_origen,
            d.id_detalle AS id_detalle_origen
        FROM compras.requerimiento r
        JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
        INNER JOIN compras.requerimiento_detalle d
            ON d.id_requerimiento = r.id_requerimiento
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
        WHERE ident.cuil_titular = p_cuil_titular
          AND ident.inte = p_inte
          AND r.id_sector = p_id_sector
          AND r.id_requerimiento <> p_id_requerimiento_excluir
          AND r.baja_fecha IS NULL
          AND d.baja_fecha IS NULL
          AND d.tipo_item = 'NOMENCLADOR'
          AND d.id_prestacion IS NOT NULL
          AND d.id_prestacion > 0
          AND n.id_tipo_nomenclador IS NOT NULL
          AND n.id_tipo_nomenclador > 0
          AND NULLIF(BTRIM(n.codigo), '') IS NOT NULL
          AND NULLIF(BTRIM(n.descripcion), '') IS NOT NULL
        ORDER BY
            d.id_prestacion,
            n.id_tipo_nomenclador,
            r.alta_fecha DESC NULLS LAST,
            r.id_requerimiento DESC,
            d.id_detalle DESC
    ) historico
    ORDER BY
        historico.fecha_requerimiento DESC NULLS LAST,
        historico.id_requerimiento_origen DESC,
        historico.id_detalle_origen DESC
    LIMIT p_limite;
$function$;

CREATE OR REPLACE FUNCTION compras.buscar_prestadores_enviados(p_id_requerimiento integer, p_texto character varying, p_limite integer)
 RETURNS TABLE(id_prestador integer, descripcion character varying, cuit character varying, email character varying, email_destino character varying, id_tipo_prestador integer, tipo_prestador character varying, estado_envio character varying)
 LANGUAGE plpgsql
 STABLE
AS $function$
DECLARE
v_texto VARCHAR;
    v_cuit VARCHAR;
    v_limite INTEGER;
BEGIN
    v_texto := NULLIF(
        upper(btrim(p_texto)),
        ''
    );

    v_cuit := NULLIF(
        regexp_replace(
            COALESCE(p_texto, ''),
            '[^0-9]',
            '',
            'g'
        ),
        ''
    );

    v_limite := LEAST(
        GREATEST(
            COALESCE(p_limite, 20),
            1
        ),
        50
    );

RETURN QUERY
SELECT DISTINCT
    p.id_prestador::INTEGER,
    p.descripcion::VARCHAR,
    p.cuit::VARCHAR,
    compras.resolver_emails_cotizacion_prestador(
            p.id_prestador
    )::VARCHAR AS email,
    NULLIF(
            btrim(rcp.email_destino),
            ''
    )::VARCHAR AS email_destino,
    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR,
    rcp.estado_envio::VARCHAR
FROM compras.requerimiento r
         JOIN compras.requerimiento_cotizacion_prestador rcp
              ON rcp.id_requerimiento =
                 r.id_requerimiento
                   AND rcp.estado_envio IN (
                       'ENVIADO',
                       'COTIZADO'
                   )
         JOIN public.prestador p
              ON p.id_prestador =
                 rcp.id_prestador
         LEFT JOIN trae_tipos_prestadores() tp
                   ON tp.id_tipo_prestador =
                      p.id_tipo_prestador
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (compras.id_estado_requerimiento('A_COTIZAR'), compras.id_estado_requerimiento('COTIZADO'), compras.id_estado_requerimiento('RECLAMO_RP'), compras.id_estado_requerimiento('ORDEN_COMPRA'), compras.id_estado_requerimiento('ANULADO'))
  AND (
    r.estado <> compras.id_estado_requerimiento('A_COTIZAR')
        OR compras.es_prestador_compatible_cotizacion(
            r.id_requerimiento,
            p.id_prestador
        )
    )
  AND (
    v_texto IS NULL

        OR upper(
                   COALESCE(
                           p.descripcion,
                           ''
                   )
           ) LIKE '%' || v_texto || '%'

        OR (
        v_cuit IS NOT NULL
            AND regexp_replace(
                        COALESCE(
                                p.cuit,
                                ''
                        ),
                        '[^0-9]',
                        '',
                        'g'
                ) LIKE '%' || v_cuit || '%'
        )
    )
ORDER BY 2
    LIMIT v_limite;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.buscar_requerimientos(p_estado integer, p_sector integer, p_afiliado_cuil_titular character varying, p_afiliado_int integer, p_id_tercerizadora character varying, p_recupero boolean, p_surge boolean, p_texto character varying, p_fecha_alta_desde date, p_fecha_alta_hasta date, p_id_requerimiento_compra integer)
 RETURNS SETOF compras.requerimiento_base_row
 LANGUAGE plpgsql
 STABLE
AS $function$
DECLARE
    v_texto VARCHAR;
    v_cuil VARCHAR;
BEGIN
    v_texto := NULLIF(
        upper(btrim(p_texto)),
        ''
    );

    v_cuil := NULLIF(
        regexp_replace(
            COALESCE(
                p_afiliado_cuil_titular,
                ''
            ),
            '[^0-9]',
            '',
            'g'
        ),
        ''
    );

    RETURN QUERY
    SELECT rb.*
    FROM compras.requerimiento_base() rb
    WHERE (
        (
            p_estado = compras.id_estado_requerimiento('ANULADO')
            AND rb.id_estado = compras.id_estado_requerimiento('ANULADO')
        )
        OR (
            p_estado IS DISTINCT FROM compras.id_estado_requerimiento('ANULADO')
            AND rb.baja_fecha IS NULL
        )
    )
    AND (
        p_estado IS NULL
        OR rb.id_estado = p_estado
    )
    AND (
        p_sector IS NULL
        OR rb.id_sector = p_sector
    )
    AND (
        p_id_requerimiento_compra IS NULL
        OR rb.id = p_id_requerimiento_compra
    )
    AND (
        v_cuil IS NULL
        OR regexp_replace(
            COALESCE(
                rb.afiliado_cuil_titular,
                ''
            ),
            '[^0-9]',
            '',
            'g'
        ) LIKE '%' || v_cuil || '%'
    )
    AND (
        p_afiliado_int IS NULL
        OR rb.afiliado_int = p_afiliado_int
    )
    AND (
        NULLIF(
            btrim(p_id_tercerizadora),
            ''
        ) IS NULL
        OR upper(
            COALESCE(
                rb.id_tercerizadora,
                ''
            )
        ) = upper(
            btrim(p_id_tercerizadora)
        )
    )
    AND (
        p_recupero IS NULL
        OR rb.recupero = p_recupero
    )
    AND (
        p_surge IS NULL
        OR rb.surge = p_surge
    )
    AND (
        p_fecha_alta_desde IS NULL
        OR rb.alta_fecha >= p_fecha_alta_desde
    )
    AND (
        p_fecha_alta_hasta IS NULL
        OR rb.alta_fecha
            < p_fecha_alta_hasta + INTERVAL '1 day'
    )
    AND (
        v_texto IS NULL

        OR rb.id::VARCHAR = btrim(p_texto)

        OR upper(
            COALESCE(
                rb.observaciones,
                ''
            )
        ) LIKE '%' || v_texto || '%'

        OR upper(
            COALESCE(
                rb.sector_descripcion,
                ''
            )
        ) LIKE '%' || v_texto || '%'

        OR upper(
            COALESCE(
                rb.afiliado_nombre_apellido,
                ''
            )
        ) LIKE '%' || v_texto || '%'

        OR upper(
            COALESCE(
                rb.afiliado_documento,
                ''
            )
        ) LIKE '%' || v_texto || '%'

        OR EXISTS (
            SELECT 1
            FROM compras.requerimiento_detalle d
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
            WHERE d.id_requerimiento = rb.id
              AND d.baja_fecha IS NULL
              AND (
                  upper(
                      COALESCE(
                          d.tipo_item,
                          ''
                      )
                  ) LIKE '%' || v_texto || '%'

                  OR upper(
                      COALESCE(n.codigo, '')
                  ) LIKE '%' || v_texto || '%'

                  OR upper(
                      COALESCE(n.descripcion, '')
                  ) LIKE '%' || v_texto || '%'

                  OR upper(
                      COALESCE(d.nombre_medicamento, '')
                  ) LIKE '%' || v_texto || '%'

                  OR COALESCE(d.troquel::VARCHAR, '')
                      LIKE '%' || btrim(p_texto) || '%'

                  OR upper(
                      COALESCE(
                          d.observaciones,
                          ''
                      )
                  ) LIKE '%' || v_texto || '%'
              )
        )
    )
    ORDER BY rb.id DESC;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.calcular_total_detalle_fila()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN

    /*
     * INSERT:
     * solo calcula si no se informó explícitamente un total.
     */
    IF TG_OP = 'INSERT'
       AND NEW.precio_total_estimado IS NULL
       AND NEW.precio_unitario_estimado IS NOT NULL THEN

        NEW.precio_total_estimado :=
            round(
                NEW.cantidad * NEW.precio_unitario_estimado,
                2
            );

    /*
     * UPDATE:
     * si el caller no modificó explícitamente el total,
     * se recalcula.
     *
     * Esto permite que la aplicación siga funcionando y,
     * al mismo tiempo, que un UPDATE manual pueda establecer
     * explícitamente precio_total_estimado.
     */
    ELSIF TG_OP = 'UPDATE'
       AND (NEW.cantidad IS DISTINCT FROM OLD.cantidad
            OR NEW.precio_unitario_estimado IS DISTINCT FROM OLD.precio_unitario_estimado)
       AND NEW.precio_total_estimado
            IS NOT DISTINCT FROM OLD.precio_total_estimado THEN

        IF NEW.precio_unitario_estimado IS NULL THEN
            NEW.precio_total_estimado := NULL;
        ELSE
            NEW.precio_total_estimado :=
                round(
                    NEW.cantidad * NEW.precio_unitario_estimado,
                    2
                );
        END IF;

    END IF;

    RETURN NEW;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.cambiar_estado_requerimiento(p_id_requerimiento integer, p_estado_nuevo integer, p_usuario character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
v_estado_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

SELECT r.estado
INTO v_estado_actual
FROM compras.requerimiento r
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND (
    r.baja_fecha IS NULL
        OR r.estado = compras.id_estado_requerimiento('ANULADO')
    )
    FOR UPDATE;

IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION
            'No se encontró el requerimiento.';
END IF;

    IF p_estado_nuevo = v_estado_actual THEN
        RAISE EXCEPTION
            'La transicion al mismo estado no es válida.';
END IF;

UPDATE compras.requerimiento
SET estado = p_estado_nuevo,
    modi_fecha = now(),
    modi_usr = v_usuario,
    baja_fecha = CASE WHEN p_estado_nuevo = compras.id_estado_requerimiento('ANULADO') THEN COALESCE(baja_fecha,now()) ELSE baja_fecha END,
    baja_usr =
        CASE
            WHEN p_estado_nuevo = compras.id_estado_requerimiento('ANULADO')
                THEN v_usuario
            ELSE baja_usr
            END
WHERE id_requerimiento =
      p_id_requerimiento
  AND estado =
      v_estado_actual;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
END IF;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.confirmar_envio_a_cotizar(p_id_requerimiento integer, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
v_estado INTEGER;
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

SELECT r.estado
INTO v_estado
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontró el requerimiento activo.';
END IF;

    /*
     * Idempotencia:
     * otro proceso pudo confirmar el envío y cambiar el estado.
     */
    IF v_estado = compras.id_estado_requerimiento('A_COTIZAR') THEN
        RETURN compras.id_estado_requerimiento('A_COTIZAR');
END IF;

    IF v_estado <> compras.id_estado_requerimiento('PENDIENTE') THEN
        RAISE EXCEPTION
            'Solo un requerimiento PENDIENTE puede pasar a A COTIZAR.';
END IF;

    /*
     * No se cambia el estado si no existe al menos un envio
     * efectivamente persistido como ENVIADO.
     */
    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_cotizacion_prestador rcp
         WHERE rcp.id_requerimiento = p_id_requerimiento
           AND rcp.estado_envio = 'ENVIADO'
    ) THEN
        RETURN compras.id_estado_requerimiento('PENDIENTE');
END IF;

    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        compras.id_estado_requerimiento('A_COTIZAR'),
        p_usuario
    );

RETURN compras.id_estado_requerimiento('A_COTIZAR');
END;
$function$;

CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(p_id_requerimiento integer, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_estado INTEGER;
    v_permite_operacion BOOLEAN;
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    SELECT
        r.estado,
        sr.permite_orden_compra_directa
    INTO
        v_estado,
        v_permite_operacion
    FROM compras.requerimiento r
    JOIN compras.sector_requerimiento sr
      ON sr.id_sector = r.id_sector
     AND sr.activo = TRUE
     AND sr.baja_fecha IS NULL
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE OF r;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento activo.';
    END IF;

    IF v_estado = compras.id_estado_requerimiento('ORDEN_COMPRA') THEN
        RETURN compras.id_estado_requerimiento('ORDEN_COMPRA');
    END IF;

    IF v_estado <> compras.id_estado_requerimiento('PENDIENTE') THEN
        RAISE EXCEPTION
            'El requerimiento solo puede pasar a ORDEN_COMPRA desde PENDIENTE.';
    END IF;

    IF NOT v_permite_operacion THEN

        RAISE EXCEPTION
            'Solo los requerimientos de RRHH o SISTEMAS pueden pasar directamente a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_detalle d
         WHERE d.id_requerimiento = p_id_requerimiento
           AND d.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
    END IF;

    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        compras.id_estado_requerimiento('ORDEN_COMPRA'),
        p_usuario
    );

    RETURN compras.id_estado_requerimiento('ORDEN_COMPRA');
END;
$function$;

CREATE OR REPLACE FUNCTION compras.diagnosticar_prestadores_notificacion_cotizacion(p_id_requerimiento integer)
 RETURNS TABLE(id_sector integer, prestadores_habilitados integer, prestadores_compatibles_sector integer, prestadores_bloqueados_estado_previo integer)
 LANGUAGE sql
 STABLE
AS $function$
SELECT
    r.id_sector,

    COUNT(
            DISTINCT CASE
                         WHEN p.id_prestador IS NOT NULL
                             THEN p.id_prestador
        END
    )::INTEGER
        AS prestadores_habilitados,

    COUNT(
            DISTINCT CASE
                         WHEN d_rubro.id_detalle IS NOT NULL
                             THEN p.id_prestador
        END
    )::INTEGER
        AS prestadores_compatibles_sector,

    COUNT(
            DISTINCT CASE
                         WHEN d_rubro.id_detalle IS NOT NULL
                              AND rcp.estado_envio IN (
                                                       'ENVIADO',
                                                       'COTIZADO',
                                                       'PROCESANDO'
                                 )
                             THEN p.id_prestador
        END
    )::INTEGER
        AS prestadores_bloqueados_estado_previo

FROM compras.requerimiento r

         LEFT JOIN public.prestador p
                   ON COALESCE(
                              p.solicitar_cotizacion,
                              FALSE
                      ) = TRUE
                       AND p.baja_fecha IS NULL

         LEFT JOIN public.prestador_rubro pr
                   ON pr.id_prestador = p.id_prestador

         LEFT JOIN compras.tipo_prestacion tp_rubro
                   ON pr.rubro =
                      tp_rubro.rubro_prestador

         LEFT JOIN compras.requerimiento_detalle d_rubro
                   ON d_rubro.id_requerimiento = r.id_requerimiento
                       AND d_rubro.id_tipo_prestacion =
                           tp_rubro.id_tipo_prestacion
                       AND d_rubro.baja_fecha IS NULL

         LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
                   ON rcp.id_requerimiento = r.id_requerimiento
                       AND rcp.id_prestador = p.id_prestador

WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL

GROUP BY
    r.id_sector;
$function$;

CREATE OR REPLACE FUNCTION compras.es_prestador_compatible_cotizacion(p_id_requerimiento integer, p_id_prestador integer)
 RETURNS boolean
 LANGUAGE sql
 STABLE
AS $function$
    SELECT EXISTS (
        SELECT 1
        FROM compras.requerimiento r
        JOIN public.prestador p
          ON p.id_prestador = p_id_prestador
         AND p.baja_fecha IS NULL
         AND COALESCE(p.solicitar_cotizacion, FALSE) = TRUE
        JOIN compras.requerimiento_detalle d
          ON d.id_requerimiento = r.id_requerimiento
         AND d.baja_fecha IS NULL
        JOIN compras.tipo_prestacion tp
          ON tp.id_tipo_prestacion = d.id_tipo_prestacion
        JOIN public.prestador_rubro pr
          ON pr.id_prestador = p.id_prestador
         AND pr.rubro = tp.rubro_prestador
        WHERE r.id_requerimiento = p_id_requerimiento
          AND r.baja_fecha IS NULL
    );
$function$;

CREATE OR REPLACE FUNCTION compras.es_requerimiento_habilitado_busqueda_empresa_cotizacion(p_id_requerimiento integer)
 RETURNS boolean
 LANGUAGE sql
 STABLE
AS $function$
    SELECT COALESCE($1, 0) > 0
       AND EXISTS (
            SELECT 1
            FROM compras.requerimiento r
            JOIN compras.sector_requerimiento sr
              ON sr.id_sector = r.id_sector
            WHERE r.id_requerimiento = $1
              AND r.estado = compras.id_estado_requerimiento('PENDIENTE')
              AND r.baja_fecha IS NULL
              AND sr.permite_cotizacion_empresa
       );
$function$;

CREATE OR REPLACE FUNCTION compras.es_sector_seleccionable_compras(p_id_sector integer)
 RETURNS boolean
 LANGUAGE sql
 STABLE
AS $function$
    SELECT EXISTS (
        SELECT 1
        FROM compras.sector_requerimiento s
        WHERE s.id_sector = p_id_sector
          AND s.activo = TRUE
          AND s.baja_fecha IS NULL
          AND s.seleccionable_alta
    );
$function$;

CREATE OR REPLACE FUNCTION compras.existe_requerimiento_duplicado(p_cuil_titular character varying, p_inte integer, p_id_prestacion integer, p_fecha_orden_medica date, p_id_requerimiento_excluir integer)
 RETURNS boolean
 LANGUAGE sql
 STABLE
AS $function$
SELECT
    CASE
        WHEN NULLIF(btrim(p_cuil_titular), '') IS NULL
            OR p_inte IS NULL
            OR p_inte < 0
            OR p_id_prestacion IS NULL
            OR p_id_prestacion <= 0
            OR p_fecha_orden_medica IS NULL
            THEN FALSE
        ELSE EXISTS (
            SELECT 1
            FROM compras.requerimiento r
        JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
            INNER JOIN compras.requerimiento_detalle d
                ON d.id_requerimiento = r.id_requerimiento
            INNER JOIN compras.requerimiento_presupuesto rp
                ON rp.id_requerimiento = r.id_requerimiento
            WHERE r.baja_fecha IS NULL
              AND r.estado <> compras.id_estado_requerimiento('ANULADO')
              AND ident.cuil_titular = btrim(p_cuil_titular)
              AND ident.inte = p_inte
              AND d.baja_fecha IS NULL
              AND d.tipo_item = 'NOMENCLADOR'
              AND d.id_prestacion = p_id_prestacion
              AND rp.baja_fecha IS NULL
              AND rp.tipo_documento = 2
              AND rp.fecha_documento = p_fecha_orden_medica
              AND (
                    p_id_requerimiento_excluir IS NULL
                    OR p_id_requerimiento_excluir <= 0
                    OR r.id_requerimiento <> p_id_requerimiento_excluir
                  )
            LIMIT 1
        )
    END;
$function$;

CREATE OR REPLACE FUNCTION compras.finalizar_cotizacion_requerimiento(p_id_requerimiento integer, p_ids_detalle integer[], p_precios_unitarios numeric[], p_id_prestador integer, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
v_estado INTEGER;
    v_usuario VARCHAR(100);
    v_total_detalles INTEGER;
    v_total_ids INTEGER;
    v_total_precios INTEGER;
    v_indice INTEGER;
    v_id_detalle INTEGER;
    v_precio NUMERIC;
    v_completa BOOLEAN;
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

SELECT r.estado
INTO v_estado
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo informado.';
END IF;

    -- Una repeticion del mismo POST luego del cierre no debe reabrir ni
    -- modificar la cotización.
    IF v_estado = compras.id_estado_requerimiento('COTIZADO') THEN
        RETURN compras.id_estado_requerimiento('COTIZADO');
END IF;

    IF v_estado <> compras.id_estado_requerimiento('A_COTIZAR') THEN
        RAISE EXCEPTION
            'La cotización solo puede guardarse en estado A COTIZAR.';
END IF;

    IF p_ids_detalle IS NULL
       OR p_precios_unitarios IS NULL
       OR array_ndims(p_ids_detalle) <> 1
       OR array_ndims(p_precios_unitarios) <> 1 THEN
        RAISE EXCEPTION
            'Debe informar arreglos unidimensionales de detalles y precios.';
END IF;

    v_total_ids := COALESCE(array_length(p_ids_detalle, 1), 0);
    v_total_precios := COALESCE(array_length(p_precios_unitarios, 1), 0);

    IF v_total_ids <= 0 OR v_total_ids <> v_total_precios THEN
        RAISE EXCEPTION
            'La cantidad de detalles y precios no coincide.';
END IF;

    IF EXISTS (
        SELECT 1
          FROM unnest(p_ids_detalle) AS ids(id_detalle)
         WHERE ids.id_detalle IS NULL
            OR ids.id_detalle <= 0
    ) THEN
        RAISE EXCEPTION
            'La cotización contiene identificadores de detalle inválidos.';
END IF;

    IF (
SELECT count(*)
FROM unnest(p_ids_detalle) AS ids(id_detalle)
    ) <> (
        SELECT count(DISTINCT ids.id_detalle)
          FROM unnest(p_ids_detalle) AS ids(id_detalle)
    ) THEN
        RAISE EXCEPTION
            'La cotización contiene detalles duplicados.';
END IF;

    IF EXISTS (
        SELECT 1
          FROM unnest(p_precios_unitarios) AS precios(precio)
         WHERE precios.precio IS NOT NULL
           AND (
                precios.precio < 0
                OR precios.precio::TEXT = 'NaN'
           )
    ) THEN
        RAISE EXCEPTION
            'Los precios unitarios deben ser nulos o mayores o iguales que cero.';
END IF;

SELECT count(*)
INTO v_total_detalles
FROM compras.requerimiento_detalle d
WHERE d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL;

IF v_total_detalles <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento no contiene detalles activos.';
END IF;

    IF v_total_detalles <> v_total_ids THEN
        RAISE EXCEPTION
            'La cotización debe informar exactamente todos los detalles activos.';
END IF;

    IF EXISTS (
        SELECT 1
          FROM unnest(p_ids_detalle) AS ids(id_detalle)
          LEFT JOIN compras.requerimiento_detalle d
            ON d.id_detalle = ids.id_detalle
           AND d.id_requerimiento = p_id_requerimiento
           AND d.baja_fecha IS NULL
         WHERE d.id_detalle IS NULL
    ) THEN
        RAISE EXCEPTION
            'La lista de detalles fue manipulada o pertenece a otro requerimiento.';
END IF;

    -- Bloquea la estructura completa antes de actualizar valores de cotización.
    PERFORM 1
      FROM compras.requerimiento_detalle d
     WHERE d.id_requerimiento = p_id_requerimiento
       AND d.baja_fecha IS NULL
     ORDER BY d.id_detalle
     FOR UPDATE;

IF p_id_prestador IS NOT NULL THEN
        IF p_id_prestador <= 0 THEN
            RAISE EXCEPTION
                'El prestador adjudicado debe ser mayor que cero.';
END IF;

        IF NOT EXISTS (
            SELECT 1
              FROM compras.requerimiento_cotizacion_prestador rcp
             WHERE rcp.id_requerimiento = p_id_requerimiento
               AND rcp.id_prestador = p_id_prestador
               AND rcp.estado_envio IN ('ENVIADO', 'COTIZADO')
        ) THEN
            RAISE EXCEPTION
                'El prestador adjudicado no fue notificado correctamente para este requerimiento.';
END IF;
END IF;

FOR v_indice IN 1..v_total_ids LOOP
        v_id_detalle := p_ids_detalle[
            array_lower(p_ids_detalle, 1) + v_indice - 1
        ];

        v_precio := p_precios_unitarios[
            array_lower(p_precios_unitarios, 1) + v_indice - 1
        ];

UPDATE compras.requerimiento_detalle
SET precio_unitario_estimado = v_precio,
    id_prestador = p_id_prestador,
    modi_fecha = now(),
    modi_usr = v_usuario
WHERE id_detalle = v_id_detalle
  AND id_requerimiento = p_id_requerimiento
  AND baja_fecha IS NULL;

IF NOT FOUND THEN
            RAISE EXCEPTION
                'No se pudo actualizar el detalle %.', v_id_detalle;
END IF;
END LOOP;

SELECT NOT EXISTS (
    SELECT 1
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL
      AND (
        d.precio_unitario_estimado IS NULL
            OR d.precio_total_estimado IS NULL
            OR d.id_prestador IS NULL
        )
)
INTO v_completa;

IF NOT v_completa THEN
        RETURN compras.id_estado_requerimiento('A_COTIZAR');
END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 1
           AND rp.id_prestador = p_id_prestador
           AND rp.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'Debe existir un presupuesto activo del prestador adjudicado antes de cerrar la cotización.';
END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_cotizacion_prestador rcp
         WHERE rcp.id_requerimiento = p_id_requerimiento
           AND rcp.id_prestador = p_id_prestador
           AND rcp.estado_envio = 'COTIZADO'
    ) THEN
        RAISE EXCEPTION
            'El prestador adjudicado debe encontrarse COTIZADO antes de cerrar la cotización.';
END IF;

    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        compras.id_estado_requerimiento('COTIZADO'),
        v_usuario
    );

RETURN compras.id_estado_requerimiento('COTIZADO');
END;
$function$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id_requerimiento_presupuesto','id_requerimiento','tipo_documento','fecha_documento','numero_receta','id_prestador','empresa_cuit','empresa_sucursal','descripcion_empresa','dl_group_id','dl_folder_id','dl_file_entry_id','dl_file_uuid','nombre_original','nombre_persistido','titulo','descripcion_prestador','alta_fecha','alta_usr','baja_fecha','baja_usr']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'integer'::regtype::oid,'smallint'::regtype::oid,'date'::regtype::oid,'varchar'::regtype::oid,'integer'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'bigint'::regtype::oid,'bigint'::regtype::oid,'bigint'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'timestamp'::regtype::oid,'varchar'::regtype::oid,'timestamp'::regtype::oid,'varchar'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.get_documento_requerimiento(integer, integer, integer)');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.get_documento_requerimiento(integer, integer, integer)')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.get_documento_requerimiento(integer, integer, integer)';
    END IF;
    SELECT obj_description(to_regprocedure('compras.get_documento_requerimiento(integer, integer, integer)'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.get_documento_requerimiento(integer, integer, integer)');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.get_documento_requerimiento(integer, integer, integer) RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.get_documento_requerimiento(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer, p_tipo_documento integer)
 RETURNS TABLE(
    id_requerimiento_presupuesto integer,
    id_requerimiento integer,
    tipo_documento smallint,
    fecha_documento date,
    numero_receta character varying(100),
    id_prestador integer,
    empresa_cuit character varying(11),
    empresa_sucursal character varying(6),
    descripcion_empresa character varying(200),
    dl_group_id bigint,
    dl_folder_id bigint,
    dl_file_entry_id bigint,
    dl_file_uuid character varying(75),
    nombre_original character varying(255),
    nombre_persistido character varying(255),
    titulo character varying(240),
    descripcion_prestador character varying(500),
    alta_fecha timestamp without time zone,
    alta_usr character varying(100),
    baja_fecha timestamp without time zone,
    baja_usr character varying(100)
)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.id_requerimiento_presupuesto,
        rp.id_requerimiento,
        rp.tipo_documento,
        rp.fecha_documento,
        rp.numero_receta,
        rp.id_prestador,
        rp.empresa_cuit,
        rp.empresa_sucursal,
        e.razon_soc::varchar,
        rp.dl_group_id,
        rp.dl_folder_id,
        rp.dl_file_entry_id,
        rp.dl_file_uuid,
        rp.nombre_original,
        rp.nombre_persistido,
        rp.titulo,
        p.descripcion::varchar,
        rp.alta_fecha,
        rp.alta_usr,
        rp.baja_fecha,
        rp.baja_usr
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    LEFT JOIN informacion_afip.empresa e ON e.cuit = rp.empresa_cuit AND e.sucursal = rp.empresa_sucursal
    WHERE rp.id_requerimiento_presupuesto = $1
      AND rp.id_requerimiento = $2
      AND rp.tipo_documento = $3
      AND rp.baja_fecha IS NULL;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.get_documento_requerimiento(integer, integer, integer) OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.get_documento_requerimiento(integer, integer, integer)')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.get_documento_requerimiento(integer, integer, integer) FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.get_documento_requerimiento(integer, integer, integer) TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.get_documento_requerimiento(integer, integer, integer) IS %L',comentario);
    END IF;
END $contrato$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id','descripcion','codigo','orden','activo','descripcion_visual']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'smallint'::regtype::oid,'boolean'::regtype::oid,'varchar'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.get_estado_requerimiento(integer)');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.get_estado_requerimiento(integer)')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.get_estado_requerimiento(integer)';
    END IF;
    SELECT obj_description(to_regprocedure('compras.get_estado_requerimiento(integer)'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.get_estado_requerimiento(integer)');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.get_estado_requerimiento(integer) RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.get_estado_requerimiento(p_id_requerimiento integer)
 RETURNS TABLE(id integer, descripcion varchar, codigo varchar, orden smallint, activo boolean, descripcion_visual varchar)
 LANGUAGE sql
 STABLE
AS $function$
SELECT e.id_estado, e.descripcion, e.codigo, e.orden, e.activo, COALESCE(e.descripcion_visual,e.descripcion) FROM compras.estado_requerimiento e JOIN compras.requerimiento r ON r.estado = e.id_estado WHERE r.id_requerimiento = p_id_requerimiento;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.get_estado_requerimiento(integer) OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.get_estado_requerimiento(integer)')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.get_estado_requerimiento(integer) FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.get_estado_requerimiento(integer) TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.get_estado_requerimiento(integer) IS %L',comentario);
    END IF;
END $contrato$;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_compra_pdf(p_id_requerimiento integer)
 RETURNS TABLE(id_requerimiento integer, alta_fecha timestamp without time zone, alta_usr character varying, id_estado integer, estado_descripcion character varying, id_sector integer, sector_descripcion character varying, requiere_afiliado boolean, afiliado_id_ospim integer, afiliado_int integer, afiliado_nombre_apellido character varying, afiliado_documento character varying, afiliado_direccion character varying, afiliado_localidad character varying, afiliado_provincia character varying, afiliado_celular character varying, afiliado_telefono character varying, afiliado_email character varying, afiliado_seccional character varying, cargo_ospim integer, cargo_tercerizadora integer, id_tercerizadora character varying, recupero boolean, surge boolean, observaciones text, detalle_id integer, detalle_orden integer, tipo_item character varying, codigo_item character varying, descripcion_item character varying, id_prestacion integer, id_tipo_nomenclador integer, codigo_nomenclador character varying, descripcion_nomenclador character varying, id_medicamento integer, troquel integer, nombre_medicamento character varying, cantidad integer, precio_unitario_estimado numeric, precio_total_estimado numeric, prestador_razon_social character varying, prestador_cuit character varying, detalle_observaciones text)
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN

RETURN QUERY

SELECT
    rb.id,
    rb.alta_fecha,
    rb.alta_usr,

    rb.id_estado,
    rb.estado_descripcion,

    rb.id_sector,
    rb.sector_descripcion,
    rb.requiere_afiliado,

    rb.afiliado_id_ospim,
    rb.afiliado_int,
    rb.afiliado_nombre_apellido,
    rb.afiliado_documento,

    rb.afiliado_direccion,
    rb.afiliado_localidad,
    rb.afiliado_provincia,
    rb.afiliado_celular,
    rb.afiliado_telefono,
    rb.afiliado_email,

    sec.descripcion AS afiliado_seccional,

    rb.cargo_ospim,
    rb.cargo_tercerizadora,
    rb.id_tercerizadora,
    rb.recupero,
    rb.surge,
    rb.observaciones,

    d.id,

    CASE
        WHEN d.id IS NULL THEN NULL
        ELSE row_number() OVER (
            PARTITION BY rb.id
            ORDER BY d.id
        )::INTEGER
    END AS detalle_orden,

    d.tipo_item,
    d.codigo_item,
    d.descripcion_item,

    d.id_prestacion,
    d.id_tipo_nomenclador,
    d.codigo_nomenclador,
    d.descripcion_nomenclador,

    d.id_medicamento,
    d.troquel,
    d.nombre_medicamento,

    d.cantidad,

    d.precio_unitario_estimado,
    d.precio_total_estimado,

    d.prestador_razon_social,
    d.prestador_cuit,

    d.observaciones

FROM compras.requerimiento_base() rb

LEFT JOIN LATERAL compras.resolver_identidad_afiliado(rb.afiliado_cuil_titular,rb.afiliado_int) ident ON TRUE
LEFT JOIN public.afiliado afi
       ON afi.cuil_titular = ident.cuil_titular
      AND afi.inte = ident.inte

LEFT JOIN public.seccional sec
       ON sec.id_seccional = afi.id_seccional

LEFT JOIN compras.get_requerimiento_detalle(
    p_id_requerimiento
) d
       ON d.id_requerimiento = rb.id

WHERE rb.id = p_id_requerimiento

ORDER BY d.id NULLS LAST;

END;
$function$;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_detalle(p_id_requerimiento integer)
 RETURNS TABLE(id integer, id_requerimiento integer, tipo_item character varying, codigo_item character varying, descripcion_item character varying, id_prestacion integer, id_tipo_nomenclador integer, codigo_nomenclador character varying, descripcion_nomenclador character varying, id_medicamento integer, troquel integer, nombre_medicamento character varying, cantidad integer, precio_unitario_estimado numeric, precio_total_estimado numeric, id_prestador integer, prestador_cuit character varying, prestador_razon_social character varying, observaciones text)
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN
RETURN QUERY
SELECT
    d.id_detalle,
    d.id_requerimiento,

    d.tipo_item::VARCHAR,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN COALESCE(
                d.troquel::VARCHAR,
                d.id_medicamento::VARCHAR
                 )
        ELSE n.codigo
        END::VARCHAR AS codigo_item,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN d.nombre_medicamento
        ELSE n.descripcion
        END::VARCHAR AS descripcion_item,

    d.id_prestacion,
    n.id_tipo_nomenclador,
    n.codigo,
    n.descripcion,

    d.id_medicamento,
    d.troquel,
    d.nombre_medicamento,

    d.cantidad,
    d.precio_unitario_estimado,
    d.precio_total_estimado,

    d.id_prestador,
    p.cuit::VARCHAR,
    p.descripcion::VARCHAR,

    d.observaciones
FROM compras.requerimiento_detalle d
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
         LEFT JOIN public.prestador p
                   ON p.id_prestador = d.id_prestador
WHERE d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL
ORDER BY d.id_detalle;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_detalle_clasificado(p_id_requerimiento integer)
 RETURNS TABLE(id integer, id_requerimiento integer, tipo_item character varying, id_tipo_prestacion integer, tipo_prestacion character varying, codigo_item character varying, descripcion_item character varying, id_prestacion integer, id_tipo_nomenclador integer, codigo_nomenclador character varying, descripcion_nomenclador character varying, id_medicamento integer, troquel integer, nombre_medicamento character varying, cantidad integer, precio_unitario_estimado numeric, precio_total_estimado numeric, id_prestador integer, prestador_cuit character varying, prestador_razon_social character varying, observaciones text)
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN
RETURN QUERY
SELECT
    d.id_detalle,
    d.id_requerimiento,

    d.tipo_item::VARCHAR,
    d.id_tipo_prestacion::INTEGER,
    tp.descripcion::VARCHAR,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN COALESCE(
                d.troquel::VARCHAR,
                d.id_medicamento::VARCHAR
                 )
        ELSE n.codigo
        END::VARCHAR AS codigo_item,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN d.nombre_medicamento
        ELSE n.descripcion
        END::VARCHAR AS descripcion_item,

    d.id_prestacion,
    n.id_tipo_nomenclador,
    n.codigo,
    n.descripcion,

    d.id_medicamento,
    d.troquel,
    d.nombre_medicamento,

    d.cantidad,
    d.precio_unitario_estimado,
    d.precio_total_estimado,

    d.id_prestador,
    p.cuit::VARCHAR,
    p.descripcion::VARCHAR,

    d.observaciones
FROM compras.requerimiento_detalle d
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
         LEFT JOIN compras.tipo_prestacion tp
                   ON tp.id_tipo_prestacion = d.id_tipo_prestacion
         LEFT JOIN public.prestador p
                   ON p.id_prestador = d.id_prestador
WHERE d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL
ORDER BY d.id_detalle;
END;
$function$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id','descripcion','requiere_afiliado','tipo_item','seleccionable_alta','permite_cotizacion_empresa','permite_orden_compra_directa','busqueda_nomenclador_medica','permite_medicamento_legacy','sector_reclamo_prestacional','nomencladores']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'varchar'::regtype::oid,'boolean'::regtype::oid,'varchar'::regtype::oid,'boolean'::regtype::oid,'boolean'::regtype::oid,'boolean'::regtype::oid,'boolean'::regtype::oid,'boolean'::regtype::oid,'varchar'::regtype::oid,'integer[]'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.get_sector_requerimiento(integer)');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.get_sector_requerimiento(integer)')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.get_sector_requerimiento(integer)';
    END IF;
    SELECT obj_description(to_regprocedure('compras.get_sector_requerimiento(integer)'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.get_sector_requerimiento(integer)');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.get_sector_requerimiento(integer) RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.get_sector_requerimiento(p_id_sector integer)
 RETURNS TABLE(id integer, descripcion character varying, requiere_afiliado boolean, tipo_item varchar, seleccionable_alta boolean, permite_cotizacion_empresa boolean, permite_orden_compra_directa boolean, busqueda_nomenclador_medica boolean, permite_medicamento_legacy boolean, sector_reclamo_prestacional varchar, nomencladores integer[])
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        s.id_sector::INTEGER AS id,
        s.descripcion::VARCHAR AS descripcion,
        s.requiere_afiliado,
        s.tipo_item,
        s.seleccionable_alta,
        s.permite_cotizacion_empresa,
        s.permite_orden_compra_directa,
        s.busqueda_nomenclador_medica,
        s.permite_medicamento_legacy,
        s.sector_reclamo_prestacional,
        ARRAY(SELECT DISTINCT c.id_tipo_nomenclador FROM compras.tipo_prestacion t JOIN compras.tipo_prestacion_tipo_nomenclador c ON c.id_tipo_prestacion=t.id_tipo_prestacion WHERE t.id_sector=s.id_sector ORDER BY c.id_tipo_nomenclador)
    FROM compras.sector_requerimiento s
    WHERE s.id_sector = $1
      AND s.activo = TRUE
      AND s.baja_fecha IS NULL;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.get_sector_requerimiento(integer) OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.get_sector_requerimiento(integer)')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.get_sector_requerimiento(integer) FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.get_sector_requerimiento(integer) TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.get_sector_requerimiento(integer) IS %L',comentario);
    END IF;
END $contrato$;

CREATE OR REPLACE FUNCTION compras.guardar_cotizacion_requerimiento(p_id_requerimiento integer, p_ids_detalle integer[], p_precios_unitarios numeric[], p_ids_detalle_eliminados integer[], p_id_prestador integer, p_surge boolean, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_estado INTEGER;
    v_total_activos INTEGER;
    v_total_conservados INTEGER;
    v_total_eliminados INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT r.estado
    INTO v_estado
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe el requerimiento activo informado.';
    END IF;

    IF v_estado = compras.id_estado_requerimiento('COTIZADO') THEN
        RETURN compras.id_estado_requerimiento('COTIZADO');
    END IF;

    IF v_estado <> compras.id_estado_requerimiento('A_COTIZAR') THEN
        RAISE EXCEPTION
            'La cotización sólo puede guardarse en estado A COTIZAR.';
    END IF;

    PERFORM 1
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL
    ORDER BY d.id_detalle
    FOR UPDATE;

    SELECT COUNT(*)
    INTO v_total_activos
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL;

    v_total_conservados := COALESCE(array_length(p_ids_detalle, 1), 0);
    v_total_eliminados := COALESCE(array_length(p_ids_detalle_eliminados, 1), 0);

    IF v_total_conservados <= 0 THEN
        RAISE EXCEPTION
            'Debe conservar al menos una prestación antes de guardar la cotización.';
    END IF;

    IF v_total_conservados
       <> COALESCE(array_length(p_precios_unitarios, 1), 0) THEN
        RAISE EXCEPTION
            'La cantidad de prestaciones y precios no coincide.';
    END IF;

    IF v_total_activos <> v_total_conservados + v_total_eliminados THEN
        RAISE EXCEPTION
            'La lista final no coincide con las prestaciones activas.';
    END IF;

    IF (SELECT COUNT(*) FROM unnest(p_ids_detalle) AS ids(id_detalle))
       <> (SELECT COUNT(DISTINCT id_detalle)
           FROM unnest(p_ids_detalle) AS ids(id_detalle))
       OR (SELECT COUNT(*)
           FROM unnest(p_ids_detalle_eliminados) AS ids(id_detalle))
       <> (SELECT COUNT(DISTINCT id_detalle)
           FROM unnest(p_ids_detalle_eliminados) AS ids(id_detalle))
       OR EXISTS (
           SELECT 1
           FROM unnest(p_ids_detalle) AS conservados(id_detalle)
           JOIN unnest(p_ids_detalle_eliminados) AS eliminados(id_detalle)
             ON eliminados.id_detalle = conservados.id_detalle
       ) THEN
        RAISE EXCEPTION
            'La lista de prestaciones contiene IDs repetidos o superpuestos.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM (
            SELECT unnest(p_ids_detalle) AS id_detalle
            UNION ALL
            SELECT unnest(p_ids_detalle_eliminados) AS id_detalle
        ) recibidos
        LEFT JOIN compras.requerimiento_detalle d
          ON d.id_detalle = recibidos.id_detalle
         AND d.id_requerimiento = p_id_requerimiento
         AND d.baja_fecha IS NULL
        WHERE recibidos.id_detalle IS NULL
           OR recibidos.id_detalle <= 0
           OR d.id_detalle IS NULL
    ) THEN
        RAISE EXCEPTION
            'La lista de prestaciones fue manipulada o quedó desactualizada.';
    END IF;

    UPDATE compras.requerimiento_detalle
    SET baja_fecha = now(),
        baja_usr = v_usuario,
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id_requerimiento
      AND id_detalle = ANY(p_ids_detalle_eliminados)
      AND baja_fecha IS NULL;

    IF FOUND AND (
        SELECT COUNT(*)
        FROM compras.requerimiento_detalle
        WHERE id_requerimiento = p_id_requerimiento
          AND baja_fecha IS NULL
    ) <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento no puede quedar sin prestaciones activas.';
    END IF;

    UPDATE compras.requerimiento
    SET surge = COALESCE(p_surge, surge),
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id_requerimiento
      AND estado = compras.id_estado_requerimiento('A_COTIZAR')
      AND baja_fecha IS NULL;

    IF p_id_prestador IS NOT NULL
       AND NOT compras.es_prestador_compatible_cotizacion(
           p_id_requerimiento,
           p_id_prestador
       ) THEN
        RAISE EXCEPTION
            'El prestador adjudicado no es compatible con las prestaciones conservadas.';
    END IF;

    RETURN compras.finalizar_cotizacion_requerimiento(
        p_id_requerimiento,
        p_ids_detalle,
        p_precios_unitarios,
        p_id_prestador,
        v_usuario
    );
END;
$function$;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento(p_id integer, p_afiliado_cuil_titular character varying, p_afiliado_int integer, p_afiliado_id_ospim integer, p_afiliado_nombre character varying, p_afiliado_apellido character varying, p_afiliado_documento_tipo character varying, p_afiliado_documento_nro character varying, p_afiliado_direccion character varying, p_afiliado_localidad character varying, p_afiliado_provincia character varying, p_afiliado_celular character varying, p_afiliado_telefono character varying, p_afiliado_email character varying, p_id_sector integer, p_cargo_ospim integer, p_cargo_tercerizadora integer, p_id_tercerizadora character varying, p_recupero boolean, p_surge boolean, p_legales boolean, p_observaciones text, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
v_id INTEGER;
    v_usuario VARCHAR(100);
    v_afiliado_cuil VARCHAR(20);
    v_cuil_anterior VARCHAR(20);
    v_inte_anterior INTEGER;
    v_cambio_afiliado BOOLEAN;
BEGIN
    v_usuario := compras.normalizar_usuario(p_usuario);
    v_afiliado_cuil := NULLIF(btrim(p_afiliado_cuil_titular), '');

    IF p_id IS NULL OR p_id <= 0 THEN
        IF NOT compras.es_sector_seleccionable_compras(p_id_sector) THEN
            RAISE EXCEPTION
                'El sector informado no está habilitado para nuevas compras.';
        END IF;

        INSERT INTO compras.requerimiento (
        estado,
        id_sector,
        afiliado_cuil_titular,
        afiliado_int,
        cargo_tercerizadora,
        id_tercerizadora,
        surge,
        legales,
        observaciones,
        alta_usr
    )
        VALUES (
        compras.id_estado_requerimiento('PENDIENTE'),
        p_id_sector,
        v_afiliado_cuil,
        p_afiliado_int,
        COALESCE(p_cargo_tercerizadora, 0),
        NULLIF(btrim(p_id_tercerizadora), ''),
        COALESCE(p_surge, FALSE),
        COALESCE(p_legales, FALSE),
        NULLIF(btrim(p_observaciones), ''),
        v_usuario
    )
        RETURNING id_requerimiento INTO v_id;

        RETURN v_id;
    END IF;

    SELECT COALESCE(ident.cuil_titular,r.afiliado_cuil_titular), COALESCE(ident.inte,r.afiliado_int)
    INTO v_cuil_anterior, v_inte_anterior
    FROM compras.requerimiento r
    LEFT JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
    WHERE r.id_requerimiento = p_id
      AND r.baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontró el requerimiento a modificar.';
    END IF;

    v_cambio_afiliado :=
           v_cuil_anterior IS DISTINCT FROM v_afiliado_cuil
        OR v_inte_anterior IS DISTINCT FROM p_afiliado_int;

    IF v_cambio_afiliado THEN
        RAISE EXCEPTION
            'El afiliado del requerimiento no puede modificarse una vez creado.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM compras.requerimiento r
        WHERE r.id_requerimiento = p_id
          AND (
              r.id_sector IS DISTINCT FROM p_id_sector
              OR r.legales IS DISTINCT FROM COALESCE(p_legales, FALSE)
          )
    ) THEN
        RAISE EXCEPTION
            'El sector y la marca LEGALES son inmutables después del alta.';
    END IF;

    UPDATE compras.requerimiento
    SET cargo_tercerizadora = COALESCE(p_cargo_tercerizadora, 0),
        id_tercerizadora = NULLIF(btrim(p_id_tercerizadora), ''),
        surge = COALESCE(p_surge, FALSE),
        observaciones = NULLIF(btrim(p_observaciones), ''),
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id
      AND estado = compras.id_estado_requerimiento('PENDIENTE')
      AND baja_fecha IS NULL
    RETURNING id_requerimiento INTO v_id;

    IF v_id IS NULL THEN
        RAISE EXCEPTION
            'La estructura solo puede modificarse en estado PENDIENTE.';
    END IF;

    RETURN v_id;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento_detalle(p_id integer, p_id_requerimiento integer, p_tipo_item character varying, p_id_prestacion integer, p_id_tipo_nomenclador integer, p_codigo_nomenclador character varying, p_descripcion_nomenclador character varying, p_id_medicamento integer, p_troquel integer, p_nombre_medicamento character varying, p_cantidad integer, p_observaciones text, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_tipo VARCHAR;
    v_usuario VARCHAR := compras.normalizar_usuario(p_usuario);
BEGIN
    -- La validacion funcional vive en EditarRequerimientoCompraHelper.
    -- El estado y la identidad se comprueban con el bloqueo de cabecera.
    PERFORM 1 FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento AND r.estado = compras.id_estado_requerimiento('PENDIENTE')
      AND r.baja_fecha IS NULL FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Los detalles estructurales solo pueden modificarse en estado PENDIENTE.';
    END IF;
    IF COALESCE(p_id,0) <= 0 THEN
        INSERT INTO compras.requerimiento_detalle
            (
        id_requerimiento,
        tipo_item,
        id_prestacion,
        cantidad,
        observaciones,
        alta_usr
    )
        VALUES (
        p_id_requerimiento,
        p_tipo_item,
        p_id_prestacion,
        p_cantidad,
        NULLIF(btrim(p_observaciones), ''),
        v_usuario
    )
        RETURNING id_detalle INTO v_id;
    ELSE
        SELECT d.tipo_item INTO v_tipo FROM compras.requerimiento_detalle d
        WHERE d.id_detalle = p_id AND d.id_requerimiento = p_id_requerimiento
          AND d.baja_fecha IS NULL FOR UPDATE;
        IF NOT FOUND OR v_tipo IS DISTINCT FROM p_tipo_item THEN
            RAISE EXCEPTION 'No se encontro el detalle activo del tipo informado.';
        END IF;
        UPDATE compras.requerimiento_detalle
        SET id_prestacion = CASE WHEN v_tipo = 'NOMENCLADOR' THEN p_id_prestacion ELSE id_prestacion END,
            cantidad = p_cantidad, observaciones = NULLIF(btrim(p_observaciones), ''),
            precio_unitario_estimado = NULL, precio_total_estimado = NULL, id_prestador = NULL,
            modi_fecha = now(), modi_usr = v_usuario
        WHERE id_detalle = p_id AND id_requerimiento = p_id_requerimiento AND baja_fecha IS NULL
        RETURNING id_detalle INTO v_id;
    END IF;
    RETURN v_id;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.listar_configuracion_correos_rubro(p_id_tipo_prestacion integer)
 RETURNS TABLE(id_prestador integer, descripcion character varying, cuit character varying, email character varying, id_tipo_prestador integer, tipo_prestador character varying)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT DISTINCT
        p.id_prestador::INTEGER AS id_prestador,
        p.descripcion::VARCHAR AS descripcion,
        p.cuit::VARCHAR AS cuit,
        compras.resolver_emails_cotizacion_prestador(
            p.id_prestador
        )::VARCHAR AS email,
        p.id_tipo_prestador::INTEGER AS id_tipo_prestador,
        tp.descripcion::VARCHAR AS tipo_prestador
    FROM compras.tipo_prestacion t
    JOIN compras.sector_requerimiento s
      ON s.id_sector = t.id_sector
    JOIN public.prestador_rubro pr
      ON pr.rubro = t.rubro_prestador
    JOIN public.prestador p
      ON p.id_prestador = pr.id_prestador
    LEFT JOIN trae_tipos_prestadores() tp
      ON tp.id_tipo_prestador = p.id_tipo_prestador
    WHERE t.id_tipo_prestacion = $1
      AND s.activo = TRUE
      AND s.baja_fecha IS NULL
      AND COALESCE(p.solicitar_cotizacion, FALSE) = TRUE
      AND p.baja_fecha IS NULL
    ORDER BY 2, 1;
$function$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id_requerimiento_presupuesto','id_requerimiento','tipo_documento','fecha_documento','numero_receta','id_prestador','empresa_cuit','empresa_sucursal','descripcion_empresa','dl_group_id','dl_folder_id','dl_file_entry_id','dl_file_uuid','nombre_original','nombre_persistido','titulo','descripcion_prestador','alta_fecha','alta_usr','baja_fecha','baja_usr']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'integer'::regtype::oid,'smallint'::regtype::oid,'date'::regtype::oid,'varchar'::regtype::oid,'integer'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'bigint'::regtype::oid,'bigint'::regtype::oid,'bigint'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'timestamp'::regtype::oid,'varchar'::regtype::oid,'timestamp'::regtype::oid,'varchar'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_documentos_requerimiento(integer, integer)');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_documentos_requerimiento(integer, integer)')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.listar_documentos_requerimiento(integer, integer)';
    END IF;
    SELECT obj_description(to_regprocedure('compras.listar_documentos_requerimiento(integer, integer)'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.listar_documentos_requerimiento(integer, integer)');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.listar_documentos_requerimiento(integer, integer) RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.listar_documentos_requerimiento(p_id_requerimiento integer, p_tipo_documento integer)
 RETURNS TABLE(
    id_requerimiento_presupuesto integer,
    id_requerimiento integer,
    tipo_documento smallint,
    fecha_documento date,
    numero_receta character varying(100),
    id_prestador integer,
    empresa_cuit character varying(11),
    empresa_sucursal character varying(6),
    descripcion_empresa character varying(200),
    dl_group_id bigint,
    dl_folder_id bigint,
    dl_file_entry_id bigint,
    dl_file_uuid character varying(75),
    nombre_original character varying(255),
    nombre_persistido character varying(255),
    titulo character varying(240),
    descripcion_prestador character varying(500),
    alta_fecha timestamp without time zone,
    alta_usr character varying(100),
    baja_fecha timestamp without time zone,
    baja_usr character varying(100)
)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.id_requerimiento_presupuesto,
        rp.id_requerimiento,
        rp.tipo_documento,
        rp.fecha_documento,
        rp.numero_receta,
        rp.id_prestador,
        rp.empresa_cuit,
        rp.empresa_sucursal,
        e.razon_soc::varchar,
        rp.dl_group_id,
        rp.dl_folder_id,
        rp.dl_file_entry_id,
        rp.dl_file_uuid,
        rp.nombre_original,
        rp.nombre_persistido,
        rp.titulo,
        p.descripcion::varchar,
        rp.alta_fecha,
        rp.alta_usr,
        rp.baja_fecha,
        rp.baja_usr
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    LEFT JOIN informacion_afip.empresa e ON e.cuit = rp.empresa_cuit AND e.sucursal = rp.empresa_sucursal
    WHERE rp.id_requerimiento = $1
      AND rp.tipo_documento = $2
      AND rp.baja_fecha IS NULL
    ORDER BY
        rp.alta_fecha DESC,
        rp.id_requerimiento_presupuesto DESC;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.listar_documentos_requerimiento(integer, integer) OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.listar_documentos_requerimiento(integer, integer)')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.listar_documentos_requerimiento(integer, integer) FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.listar_documentos_requerimiento(integer, integer) TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.listar_documentos_requerimiento(integer, integer) IS %L',comentario);
    END IF;
END $contrato$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id','descripcion','codigo','orden','activo','descripcion_visual']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'smallint'::regtype::oid,'boolean'::regtype::oid,'varchar'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_estados_requerimiento()');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_estados_requerimiento()')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.listar_estados_requerimiento()';
    END IF;
    SELECT obj_description(to_regprocedure('compras.listar_estados_requerimiento()'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.listar_estados_requerimiento()');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.listar_estados_requerimiento() RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.listar_estados_requerimiento()
 RETURNS TABLE(id integer, descripcion varchar, codigo varchar, orden smallint, activo boolean, descripcion_visual varchar)
 LANGUAGE sql
 STABLE
AS $function$
SELECT e.id_estado, e.descripcion, e.codigo, e.orden, e.activo, COALESCE(e.descripcion_visual,e.descripcion) FROM compras.estado_requerimiento e WHERE e.activo ORDER BY e.orden;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.listar_estados_requerimiento() OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.listar_estados_requerimiento()')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.listar_estados_requerimiento() FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.listar_estados_requerimiento() TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.listar_estados_requerimiento() IS %L',comentario);
    END IF;
END $contrato$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id_requerimiento_presupuesto','id_requerimiento','tipo_documento','fecha_documento','numero_receta','id_prestador','empresa_cuit','empresa_sucursal','descripcion_empresa','dl_group_id','dl_folder_id','dl_file_entry_id','dl_file_uuid','nombre_original','nombre_persistido','titulo','descripcion_prestador','alta_fecha','alta_usr','baja_fecha','baja_usr']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'integer'::regtype::oid,'smallint'::regtype::oid,'date'::regtype::oid,'varchar'::regtype::oid,'integer'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'bigint'::regtype::oid,'bigint'::regtype::oid,'bigint'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'timestamp'::regtype::oid,'varchar'::regtype::oid,'timestamp'::regtype::oid,'varchar'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_ordenes_medicas_requerimiento(integer)');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_ordenes_medicas_requerimiento(integer)')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.listar_ordenes_medicas_requerimiento(integer)';
    END IF;
    SELECT obj_description(to_regprocedure('compras.listar_ordenes_medicas_requerimiento(integer)'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.listar_ordenes_medicas_requerimiento(integer)');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.listar_ordenes_medicas_requerimiento(integer) RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.listar_ordenes_medicas_requerimiento(p_id_requerimiento integer)
 RETURNS TABLE(
    id_requerimiento_presupuesto integer,
    id_requerimiento integer,
    tipo_documento smallint,
    fecha_documento date,
    numero_receta character varying(100),
    id_prestador integer,
    empresa_cuit character varying(11),
    empresa_sucursal character varying(6),
    descripcion_empresa character varying(200),
    dl_group_id bigint,
    dl_folder_id bigint,
    dl_file_entry_id bigint,
    dl_file_uuid character varying(75),
    nombre_original character varying(255),
    nombre_persistido character varying(255),
    titulo character varying(240),
    descripcion_prestador character varying(500),
    alta_fecha timestamp without time zone,
    alta_usr character varying(100),
    baja_fecha timestamp without time zone,
    baja_usr character varying(100)
)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.id_requerimiento_presupuesto,
        rp.id_requerimiento,
        rp.tipo_documento,
        rp.fecha_documento,
        rp.numero_receta,
        rp.id_prestador,
        rp.empresa_cuit,
        rp.empresa_sucursal,
        e.razon_soc::varchar,
        rp.dl_group_id,
        rp.dl_folder_id,
        rp.dl_file_entry_id,
        rp.dl_file_uuid,
        rp.nombre_original,
        rp.nombre_persistido,
        rp.titulo,
        p.descripcion::varchar,
        rp.alta_fecha,
        rp.alta_usr,
        rp.baja_fecha,
        rp.baja_usr
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    LEFT JOIN informacion_afip.empresa e ON e.cuit = rp.empresa_cuit AND e.sucursal = rp.empresa_sucursal
    WHERE rp.id_requerimiento = $1
      AND rp.tipo_documento = 2
      AND rp.baja_fecha IS NULL;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.listar_ordenes_medicas_requerimiento(integer) OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.listar_ordenes_medicas_requerimiento(integer)')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.listar_ordenes_medicas_requerimiento(integer) FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.listar_ordenes_medicas_requerimiento(integer) TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.listar_ordenes_medicas_requerimiento(integer) IS %L',comentario);
    END IF;
END $contrato$;

CREATE OR REPLACE FUNCTION compras.listar_prestadores_adjudicados_batch(p_ids_requerimientos text)
 RETURNS TABLE(id_requerimiento integer, id_prestador integer, descripcion character varying)
 LANGUAGE sql
 STABLE
AS $function$
    -- Misma identidad que listar_prestadores_adjudicados: detalle activo.
    -- DISTINCT incluye NULL para detectar detalles parcialmente adjudicados.
    -- No se une a presupuestos/documentos: no multiplican adjudicatarios.
    SELECT DISTINCT d.id_requerimiento::INTEGER,
           d.id_prestador::INTEGER,
           p.descripcion::VARCHAR
      FROM compras.requerimiento_detalle d
      JOIN compras.requerimiento r ON r.id_requerimiento = d.id_requerimiento
      LEFT JOIN public.prestador p ON p.id_prestador = d.id_prestador
     WHERE d.id_requerimiento = ANY(p_ids_requerimientos::INTEGER[])
       AND d.baja_fecha IS NULL
       AND r.baja_fecha IS NULL
       AND r.estado <> compras.id_estado_requerimiento('ANULADO')
     ORDER BY d.id_requerimiento::INTEGER, d.id_prestador::INTEGER;
$function$;

CREATE OR REPLACE FUNCTION compras.listar_prestadores_cotizacion_requerimiento(p_id_requerimiento integer)
 RETURNS TABLE(id_prestador integer, descripcion character varying, cuit character varying, email character varying, id_tipo_prestador integer, tipo_prestador character varying)
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN
RETURN QUERY
SELECT DISTINCT
    p.id_prestador::INTEGER,
    p.descripcion::VARCHAR,
    p.cuit::VARCHAR,
    NULLIF(
        split_part(
            COALESCE(
                compras.resolver_emails_cotizacion_prestador(
                    p.id_prestador
                ),
                ''
            ),
            ';',
            1
        ),
        ''
    )::VARCHAR AS email,
    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR
FROM compras.requerimiento r
         JOIN public.prestador p
              ON COALESCE(
                     p.solicitar_cotizacion,
                     FALSE
                 ) = TRUE
                 AND p.baja_fecha IS NULL
         LEFT JOIN trae_tipos_prestadores() tp
                   ON tp.id_tipo_prestador =
                      p.id_tipo_prestador
         LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
                   ON rcp.id_requerimiento =
                      r.id_requerimiento
                       AND rcp.id_prestador =
                           p.id_prestador
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (compras.id_estado_requerimiento('PENDIENTE'), compras.id_estado_requerimiento('A_COTIZAR'))
  AND r.baja_fecha IS NULL
  AND EXISTS (
      SELECT 1
      FROM compras.requerimiento_detalle d
      JOIN compras.tipo_prestacion t
        ON t.id_tipo_prestacion = d.id_tipo_prestacion
      JOIN public.prestador_rubro pr
        ON pr.id_prestador = p.id_prestador
       AND pr.rubro = t.rubro_prestador
      WHERE d.id_requerimiento = r.id_requerimiento
        AND d.baja_fecha IS NULL
  )
  AND (
    -- Primer envio o recuperación: se listan todos los
    -- candidatos vigentes. registrar_cotizacion_prestador
    -- evita reenviar ENVIADO o PROCESANDO.
    r.estado = compras.id_estado_requerimiento('PENDIENTE')

        OR (
        r.estado = compras.id_estado_requerimiento('A_COTIZAR')
            AND (
            rcp.id_prestador IS NULL
                OR rcp.estado_envio IN (
                                        'PENDIENTE',
                                        'ERROR',
                                        'EMAIL_INVALIDO'
                )
            )
        )
    )
ORDER BY 6, 2;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.listar_prestadores_enviados(p_id_requerimiento integer, p_limite integer)
 RETURNS TABLE(id_prestador integer, descripcion character varying, cuit character varying, email character varying, email_destino character varying, id_tipo_prestador integer, tipo_prestador character varying, estado_envio character varying)
 LANGUAGE plpgsql
 STABLE
AS $function$
DECLARE
v_limite INTEGER;
BEGIN
    IF p_id_requerimiento IS NULL
            OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

    IF p_limite IS NULL
            OR p_limite <= 0 THEN

        RAISE EXCEPTION
            'El limite de prestadores debe ser mayor que cero.';
END IF;

    v_limite := p_limite;

RETURN QUERY
SELECT DISTINCT
    p.id_prestador::INTEGER,
    p.descripcion::VARCHAR,
    p.cuit::VARCHAR,

    compras.resolver_emails_cotizacion_prestador(
            p.id_prestador
    )::VARCHAR AS email,

    NULLIF(
            btrim(rcp.email_destino),
            ''
    )::VARCHAR AS email_destino,

    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR,
    rcp.estado_envio::VARCHAR

FROM compras.requerimiento r

         JOIN compras.requerimiento_cotizacion_prestador rcp
              ON rcp.id_requerimiento =
                 r.id_requerimiento
                  AND rcp.estado_envio IN (
                                           'ENVIADO',
                                           'COTIZADO'
                      )

         JOIN public.prestador p
              ON p.id_prestador =
                 rcp.id_prestador

         LEFT JOIN trae_tipos_prestadores() tp
                   ON tp.id_tipo_prestador =
                      p.id_tipo_prestador

WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (compras.id_estado_requerimiento('A_COTIZAR'), compras.id_estado_requerimiento('COTIZADO'), compras.id_estado_requerimiento('RECLAMO_RP'), compras.id_estado_requerimiento('ORDEN_COMPRA'), compras.id_estado_requerimiento('ANULADO'))
  AND (
    r.estado <> compras.id_estado_requerimiento('A_COTIZAR')
        OR compras.es_prestador_compatible_cotizacion(
            r.id_requerimiento,
            p.id_prestador
        )
    )

ORDER BY
    2,
    3,
    1

    LIMIT v_limite;
END;
$function$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id_requerimiento_presupuesto','id_requerimiento','tipo_documento','fecha_documento','numero_receta','id_prestador','empresa_cuit','empresa_sucursal','descripcion_empresa','dl_group_id','dl_folder_id','dl_file_entry_id','dl_file_uuid','nombre_original','nombre_persistido','titulo','descripcion_prestador','alta_fecha','alta_usr','baja_fecha','baja_usr']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'integer'::regtype::oid,'smallint'::regtype::oid,'date'::regtype::oid,'varchar'::regtype::oid,'integer'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'bigint'::regtype::oid,'bigint'::regtype::oid,'bigint'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'timestamp'::regtype::oid,'varchar'::regtype::oid,'timestamp'::regtype::oid,'varchar'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_presupuestos_prestador(integer, integer)');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_presupuestos_prestador(integer, integer)')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.listar_presupuestos_prestador(integer, integer)';
    END IF;
    SELECT obj_description(to_regprocedure('compras.listar_presupuestos_prestador(integer, integer)'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.listar_presupuestos_prestador(integer, integer)');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.listar_presupuestos_prestador(integer, integer) RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.listar_presupuestos_prestador(p_id_requerimiento integer, p_id_prestador integer)
 RETURNS TABLE(
    id_requerimiento_presupuesto integer,
    id_requerimiento integer,
    tipo_documento smallint,
    fecha_documento date,
    numero_receta character varying(100),
    id_prestador integer,
    empresa_cuit character varying(11),
    empresa_sucursal character varying(6),
    descripcion_empresa character varying(200),
    dl_group_id bigint,
    dl_folder_id bigint,
    dl_file_entry_id bigint,
    dl_file_uuid character varying(75),
    nombre_original character varying(255),
    nombre_persistido character varying(255),
    titulo character varying(240),
    descripcion_prestador character varying(500),
    alta_fecha timestamp without time zone,
    alta_usr character varying(100),
    baja_fecha timestamp without time zone,
    baja_usr character varying(100)
)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.id_requerimiento_presupuesto,
        rp.id_requerimiento,
        rp.tipo_documento,
        rp.fecha_documento,
        rp.numero_receta,
        rp.id_prestador,
        rp.empresa_cuit,
        rp.empresa_sucursal,
        e.razon_soc::varchar,
        rp.dl_group_id,
        rp.dl_folder_id,
        rp.dl_file_entry_id,
        rp.dl_file_uuid,
        rp.nombre_original,
        rp.nombre_persistido,
        rp.titulo,
        p.descripcion::varchar,
        rp.alta_fecha,
        rp.alta_usr,
        rp.baja_fecha,
        rp.baja_usr
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    LEFT JOIN informacion_afip.empresa e ON e.cuit = rp.empresa_cuit AND e.sucursal = rp.empresa_sucursal
    WHERE rp.id_requerimiento = $1
      AND rp.id_prestador = $2
      AND rp.tipo_documento = 1
      AND rp.baja_fecha IS NULL
    ORDER BY rp.id_requerimiento_presupuesto;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.listar_presupuestos_prestador(integer, integer) OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.listar_presupuestos_prestador(integer, integer)')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.listar_presupuestos_prestador(integer, integer) FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.listar_presupuestos_prestador(integer, integer) TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.listar_presupuestos_prestador(integer, integer) IS %L',comentario);
    END IF;
END $contrato$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id','descripcion','requiere_afiliado','tipo_item','seleccionable_alta','permite_cotizacion_empresa','permite_orden_compra_directa','busqueda_nomenclador_medica','permite_medicamento_legacy','sector_reclamo_prestacional','nomencladores']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'varchar'::regtype::oid,'boolean'::regtype::oid,'varchar'::regtype::oid,'boolean'::regtype::oid,'boolean'::regtype::oid,'boolean'::regtype::oid,'boolean'::regtype::oid,'boolean'::regtype::oid,'varchar'::regtype::oid,'integer[]'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_sectores_requerimiento()');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_sectores_requerimiento()')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.listar_sectores_requerimiento()';
    END IF;
    SELECT obj_description(to_regprocedure('compras.listar_sectores_requerimiento()'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.listar_sectores_requerimiento()');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.listar_sectores_requerimiento() RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.listar_sectores_requerimiento()
 RETURNS TABLE(id integer, descripcion character varying, requiere_afiliado boolean, tipo_item varchar, seleccionable_alta boolean, permite_cotizacion_empresa boolean, permite_orden_compra_directa boolean, busqueda_nomenclador_medica boolean, permite_medicamento_legacy boolean, sector_reclamo_prestacional varchar, nomencladores integer[])
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        s.id_sector::INTEGER AS id,
        s.descripcion::VARCHAR AS descripcion,
        s.requiere_afiliado,
        s.tipo_item,
        s.seleccionable_alta,
        s.permite_cotizacion_empresa,
        s.permite_orden_compra_directa,
        s.busqueda_nomenclador_medica,
        s.permite_medicamento_legacy,
        s.sector_reclamo_prestacional,
        ARRAY(SELECT DISTINCT c.id_tipo_nomenclador FROM compras.tipo_prestacion t JOIN compras.tipo_prestacion_tipo_nomenclador c ON c.id_tipo_prestacion=t.id_tipo_prestacion WHERE t.id_sector=s.id_sector ORDER BY c.id_tipo_nomenclador)
    FROM compras.sector_requerimiento s
    WHERE compras.es_sector_seleccionable_compras(s.id_sector)
    ORDER BY s.descripcion;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.listar_sectores_requerimiento() OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.listar_sectores_requerimiento()')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.listar_sectores_requerimiento() FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.listar_sectores_requerimiento() TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.listar_sectores_requerimiento() IS %L',comentario);
    END IF;
END $contrato$;

DO $contrato$
DECLARE propietario NAME; permisos ACLITEM[]; permiso RECORD; destinatario TEXT; recrear BOOLEAN; comentario TEXT;
BEGIN
    SELECT NOT (p.proretset AND p.prorettype='record'::regtype
        AND ARRAY(SELECT p.proargnames[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['id_tipo_prestacion','descripcion','id_sector','sector_descripcion','rubro_prestador','nomencladores']::text[]
        AND ARRAY(SELECT p.proallargtypes[i] FROM generate_subscripts(p.proallargtypes,1) i WHERE p.proargmodes[i] IN ('o','b','t'))=ARRAY['integer'::regtype::oid,'varchar'::regtype::oid,'integer'::regtype::oid,'varchar'::regtype::oid,'varchar'::regtype::oid,'integer[]'::regtype::oid]::oid[])
    INTO recrear FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_tipos_prestacion()');
    IF recrear AND EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure('compras.listar_tipos_prestacion()')
                          AND (p.prosecdef OR p.proconfig IS NOT NULL OR p.proleakproof)) THEN
        RAISE EXCEPTION 'Seguridad/configuracion especial no relevada: compras.listar_tipos_prestacion()';
    END IF;
    SELECT obj_description(to_regprocedure('compras.listar_tipos_prestacion()'),'pg_proc') INTO comentario;
    SELECT pg_get_userbyid(proowner), COALESCE(proacl,acldefault('f',proowner))
    INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure('compras.listar_tipos_prestacion()');
    IF recrear THEN
        -- RESTRICT aborta si existe una dependencia no contemplada. Nunca CASCADE.
        DROP FUNCTION compras.listar_tipos_prestacion() RESTRICT;
    END IF;
    EXECUTE $definicion$CREATE OR REPLACE FUNCTION compras.listar_tipos_prestacion()
 RETURNS TABLE(id_tipo_prestacion integer, descripcion character varying, id_sector integer, sector_descripcion character varying, rubro_prestador varchar, nomencladores integer[])
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        t.id_tipo_prestacion::INTEGER AS id_tipo_prestacion,
        t.descripcion::VARCHAR AS descripcion,
        t.id_sector::INTEGER AS id_sector,
        s.descripcion::VARCHAR AS sector_descripcion,
        t.rubro_prestador,
        ARRAY(SELECT c.id_tipo_nomenclador FROM compras.tipo_prestacion_tipo_nomenclador c WHERE c.id_tipo_prestacion = t.id_tipo_prestacion ORDER BY c.id_tipo_nomenclador)
    FROM compras.tipo_prestacion t
    JOIN compras.sector_requerimiento s
      ON s.id_sector = t.id_sector
    WHERE s.activo = TRUE
      AND s.baja_fecha IS NULL
    ORDER BY t.id_tipo_prestacion;
$function$;$definicion$;
    IF recrear AND propietario IS NOT NULL THEN
        EXECUTE format('ALTER FUNCTION compras.listar_tipos_prestacion() OWNER TO %I',propietario);
        FOR permiso IN SELECT a.* FROM pg_proc p,
            LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a
            WHERE p.oid=to_regprocedure('compras.listar_tipos_prestacion()')
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'REVOKE ALL ON FUNCTION compras.listar_tipos_prestacion() FROM ' || destinatario;
        END LOOP;
        FOR permiso IN SELECT * FROM aclexplode(permisos)
        LOOP
            destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
            EXECUTE 'GRANT EXECUTE ON FUNCTION compras.listar_tipos_prestacion() TO ' || destinatario
                || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
        END LOOP;
    END IF;
    IF recrear AND comentario IS NOT NULL THEN
        EXECUTE format('COMMENT ON FUNCTION compras.listar_tipos_prestacion() IS %L',comentario);
    END IF;
END $contrato$;

CREATE OR REPLACE FUNCTION compras.reactivar_cotizacion_empresa_requerimiento(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_empresa_cuit VARCHAR(11);
    v_empresa_sucursal VARCHAR(6);
    v_estado_requerimiento INTEGER;
    v_permite_operacion BOOLEAN;
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RETURN FALSE;
    END IF;

    SELECT
        r.estado,
        sr.permite_cotizacion_empresa
      INTO
        v_estado_requerimiento,
        v_permite_operacion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> compras.id_estado_requerimiento('PENDIENTE')
       OR NOT v_permite_operacion THEN

        RAISE EXCEPTION
            'Las cotizaciones de Empresas solo pueden reactivarse '
            'en requerimientos PENDIENTES de RRHH o SISTEMAS.';
    END IF;

    SELECT
        rp.empresa_cuit,
        rp.empresa_sucursal
      INTO
        v_empresa_cuit,
        v_empresa_sucursal
      FROM compras.requerimiento_presupuesto rp
     WHERE rp.id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND rp.id_requerimiento = p_id_requerimiento
       AND rp.tipo_documento = 3
       AND rp.baja_fecha IS NOT NULL
     FOR UPDATE;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.empresa_cuit = v_empresa_cuit
           AND rp.empresa_sucursal = v_empresa_sucursal
           AND rp.baja_fecha IS NULL
           AND rp.id_requerimiento_presupuesto
                <> p_id_requerimiento_presupuesto
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene otra cotización activa para este requerimiento.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET baja_fecha = NULL,
           baja_usr = NULL
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3
       AND baja_fecha IS NOT NULL;

    RETURN FOUND;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.reactivar_requerimiento_presupuesto(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id_prestador INTEGER;
    v_estado_requerimiento INTEGER;
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN
        RETURN FALSE;
    END IF;

    SELECT r.estado
      INTO v_estado_requerimiento
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND OR v_estado_requerimiento <> compras.id_estado_requerimiento('A_COTIZAR') THEN
        RAISE EXCEPTION
            'Los presupuestos solo pueden reactivarse en estado A COTIZAR.';
    END IF;

    SELECT rp.id_prestador
      INTO v_id_prestador
      FROM compras.requerimiento_presupuesto rp
     WHERE rp.id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND rp.id_requerimiento = p_id_requerimiento
       AND rp.tipo_documento = 1
       AND rp.baja_fecha IS NOT NULL
     FOR UPDATE;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    PERFORM 1
      FROM compras.requerimiento_cotizacion_prestador rcp
     WHERE rcp.id_requerimiento = p_id_requerimiento
       AND rcp.id_prestador = v_id_prestador
     FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el prestador notificado asociado al presupuesto.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 1
           AND rp.id_prestador = v_id_prestador
           AND rp.baja_fecha IS NULL
           AND rp.id_requerimiento_presupuesto <> p_id_requerimiento_presupuesto
    ) THEN
        RAISE EXCEPTION
            'El prestador ya tiene otro presupuesto activo para este requerimiento.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET baja_fecha = NULL,
           baja_usr = NULL
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 1
       AND baja_fecha IS NOT NULL;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    UPDATE compras.requerimiento_cotizacion_prestador
       SET estado_envio = 'COTIZADO',
           modi_fecha = now(),
           modi_usr = 'sistema'
     WHERE id_requerimiento = p_id_requerimiento
       AND id_prestador = v_id_prestador
       AND estado_envio IN ('ENVIADO', 'COTIZADO');

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se pudo restaurar el estado COTIZADO del prestador.';
    END IF;

    RETURN TRUE;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.registrar_cotizacion_prestador(p_id_requerimiento integer, p_id_prestador integer, p_usuario character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
v_email VARCHAR(320);
    v_usuario VARCHAR(100);
    v_reservado BOOLEAN;
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

SELECT
    compras.resolver_email_cotizacion_prestador(
            p_id_prestador
    )
INTO
    v_email
FROM compras.requerimiento r
         JOIN public.prestador p
              ON p.id_prestador =
                 p_id_prestador
                 AND p.baja_fecha IS NULL
                 AND COALESCE(
                         p.solicitar_cotizacion,
                         FALSE
                     ) = TRUE
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (compras.id_estado_requerimiento('PENDIENTE'), compras.id_estado_requerimiento('A_COTIZAR'))
  AND r.baja_fecha IS NULL
  AND EXISTS (
      SELECT 1
      FROM compras.requerimiento_detalle d
      JOIN compras.tipo_prestacion t
        ON t.id_tipo_prestacion = d.id_tipo_prestacion
      JOIN public.prestador_rubro pr
        ON pr.id_prestador = p.id_prestador
       AND pr.rubro = t.rubro_prestador
      WHERE d.id_requerimiento = r.id_requerimiento
        AND d.baja_fecha IS NULL
  )
    LIMIT 1;

IF NOT FOUND THEN
        RETURN FALSE;
END IF;

    /*
     * Reserva atómica.
     *
     * INSERT nuevo:
     *   PROCESANDO, intento 1.
     *
     * Conflicto:
     *   solo vuelve a reservar PENDIENTE, ERROR o EMAIL_INVALIDO.
     *
     * ENVIADO y PROCESANDO no se vuelven a tomar. De esta forma dos
     * ejecuciones concurrentes no pueden obtener TRUE para la misma fila.
     */
INSERT INTO compras.requerimiento_cotizacion_prestador (
    id_requerimiento,
    id_prestador,
    estado_envio,
    intentos,
    email_destino,
    fecha_ultimo_intento,
    alta_usr
)
VALUES (
           p_id_requerimiento,
           p_id_prestador,
           'PROCESANDO',
           1,
           v_email,
           now(),
           v_usuario
       )
    ON CONFLICT (
        id_requerimiento,
        id_prestador
    )
    DO UPDATE
               SET estado_envio = 'PROCESANDO',
               intentos =
               compras.requerimiento_cotizacion_prestador.intentos
               + 1,
               email_destino = EXCLUDED.email_destino,
               fecha_ultimo_intento = now(),
               fecha_envio = NULL,
               ultimo_error = NULL,
               modi_fecha = now(),
               modi_usr = v_usuario
       WHERE compras.requerimiento_cotizacion_prestador.estado_envio
               IN (
               'PENDIENTE',
               'ERROR',
               'EMAIL_INVALIDO'
               )
               RETURNING TRUE
       INTO v_reservado;

RETURN COALESCE(
        v_reservado,
        FALSE
       );
END;
$function$;

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_orden_medica(p_id_requerimiento integer, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_fecha_documento date, p_numero_receta character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_cuil_titular VARCHAR(20);
    v_inte INTEGER;
    v_numero_receta VARCHAR;
    v_id_requerimiento_duplicado INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_fecha_documento IS NULL THEN
        RAISE EXCEPTION
            'Debe informar la fecha del adjunto.';
    END IF;

    IF p_dl_group_id IS NULL
       OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL
       OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL
       OR p_dl_file_entry_id <= 0
       OR NULLIF(btrim(p_dl_file_uuid), '') IS NULL THEN

        RAISE EXCEPTION
            'La identidad del adjunto no es válida.';
    END IF;

    IF NULLIF(btrim(p_nombre_original), '') IS NULL
       OR NULLIF(btrim(p_nombre_persistido), '') IS NULL THEN

        RAISE EXCEPTION
            'Los nombres del adjunto no son válidos.';
    END IF;

    /*
     * p_titulo es opcional y se conserva en la firma para no romper callers.
     * Si llega vacío se utiliza el nombre original como título técnico.
     */

    IF p_numero_receta IS NOT NULL
       AND length(p_numero_receta) > 100 THEN

        RAISE EXCEPTION
            'El número de receta admite hasta 100 caracteres.';
    END IF;

    v_numero_receta :=
        NULLIF(
            regexp_replace(
                upper(btrim(COALESCE(p_numero_receta, ''))),
                '[[:space:]]+',
                '',
                'g'
            ),
            ''
        );

    IF v_numero_receta IS NOT NULL
       AND length(v_numero_receta) > 100 THEN

        RAISE EXCEPTION
            'El número de receta admite hasta 100 caracteres.';
    END IF;

    v_usuario :=
        COALESCE(
            NULLIF(btrim(p_usuario), ''),
            'sistema'
        );

    SELECT
        r.estado,
        r.afiliado_cuil_titular,
        r.afiliado_int
    INTO
        v_estado_requerimiento,
        v_cuil_titular,
        v_inte
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo informado.';
    END IF;

    IF v_estado_requerimiento <> compras.id_estado_requerimiento('PENDIENTE') THEN
        RAISE EXCEPTION
            'El adjunto solo puede registrarse durante '
            'el alta de un requerimiento PENDIENTE.';
    END IF;

    IF v_cuil_titular IS NOT NULL THEN
        SELECT a.cuil_titular,a.inte INTO v_cuil_titular,v_inte
        FROM compras.resolver_identidad_afiliado(v_cuil_titular,v_inte) a;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'No se pudo resolver la identidad actual del afiliado.';
        END IF;
    END IF;

    /*
     * La receta es opcional. Si se informa y el requerimiento tiene
     * afiliado, el advisory lock serializa la clave funcional antes
     * de revalidar el duplicado exacto. Las colisiones del hash solo
     * amplían la serialización: nunca producen un falso duplicado.
     */
    IF v_numero_receta IS NOT NULL
       AND NULLIF(btrim(v_cuil_titular), '') IS NOT NULL
       AND v_inte IS NOT NULL THEN

        PERFORM pg_advisory_xact_lock(
            hashtext('compras.orden_medica.receta'),
            hashtext(
                v_cuil_titular
                || chr(31)
                || v_inte::VARCHAR
                || chr(31)
                || to_char(p_fecha_documento, 'YYYY-MM-DD')
                || chr(31)
                || v_numero_receta
            )
        );

        SELECT rp.id_requerimiento
        INTO v_id_requerimiento_duplicado
        FROM compras.requerimiento_presupuesto rp
        INNER JOIN compras.requerimiento r
            ON r.id_requerimiento = rp.id_requerimiento
        JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
        WHERE rp.tipo_documento = 2
          AND rp.baja_fecha IS NULL
          AND rp.numero_receta = v_numero_receta
          AND rp.fecha_documento = p_fecha_documento
          AND r.estado <> compras.id_estado_requerimiento('ANULADO')
          AND ident.cuil_titular = v_cuil_titular
          AND ident.inte = v_inte
        ORDER BY
            rp.id_requerimiento,
            rp.id_requerimiento_presupuesto
        LIMIT 1;

        IF FOUND THEN
            RAISE EXCEPTION
                'El adjunto ya fue cargado con fecha % y número de receta % en el requerimiento %.',
                to_char(p_fecha_documento, 'DD-MM-YYYY'),
                v_numero_receta,
                v_id_requerimiento_duplicado;
        END IF;
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
        tipo_documento,
        fecha_documento,
        numero_receta,
        id_prestador,
        dl_group_id,
        dl_folder_id,
        dl_file_entry_id,
        dl_file_uuid,
        nombre_original,
        nombre_persistido,
        titulo,
        alta_usr
    )
    VALUES (
        p_id_requerimiento,
        2,
        p_fecha_documento,
        v_numero_receta,
        NULL,
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        btrim(p_dl_file_uuid),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        COALESCE(
            NULLIF(btrim(p_titulo), ''),
            btrim(p_nombre_original)
        ),
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    RETURN v_id;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(p_id_requerimiento integer, p_id_prestador integer, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_descripcion_prestador character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_estado_envio VARCHAR(20);
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_id_prestador IS NULL OR p_id_prestador <= 0 THEN
        RAISE EXCEPTION
            'El prestador informado no es válido.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN
        RAISE EXCEPTION
            'La identidad del documento de presupuesto no es válida.';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT r.estado
      INTO v_estado_requerimiento
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND OR v_estado_requerimiento <> compras.id_estado_requerimiento('A_COTIZAR') THEN
        RAISE EXCEPTION
            'El requerimiento no se encuentra activo y en estado A COTIZAR.';
    END IF;

    SELECT rcp.estado_envio
      INTO v_estado_envio
      FROM compras.requerimiento_cotizacion_prestador rcp
     WHERE rcp.id_requerimiento = p_id_requerimiento
       AND rcp.id_prestador = p_id_prestador
     FOR UPDATE;

    IF NOT FOUND OR v_estado_envio <> 'ENVIADO' THEN
        RAISE EXCEPTION
            'El prestador no se encuentra ENVIADO y disponible para cotizar.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 1
           AND rp.id_prestador = p_id_prestador
           AND rp.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'El prestador ya tiene un presupuesto activo para este requerimiento.';
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
        tipo_documento,
        fecha_documento,
        id_prestador,
        dl_group_id,
        dl_folder_id,
        dl_file_entry_id,
        dl_file_uuid,
        nombre_original,
        nombre_persistido,
        titulo,
        alta_usr
    )
    VALUES (
        p_id_requerimiento,
        1,
        NULL,
        p_id_prestador,
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        btrim(p_titulo),
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    UPDATE compras.requerimiento_cotizacion_prestador
       SET estado_envio = 'COTIZADO',
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND id_prestador = p_id_prestador
       AND estado_envio = 'ENVIADO';

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se pudo marcar como COTIZADO al prestador del presupuesto.';
    END IF;

    RETURN v_id;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(p_id_requerimiento integer, p_tipo_documento smallint, p_id_prestador integer, p_empresa_cuit character varying, p_empresa_sucursal character varying, p_descripcion_empresa character varying, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_descripcion_prestador character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_permite_operacion BOOLEAN;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_tipo_documento IS NULL OR p_tipo_documento <> 3 THEN
        RAISE EXCEPTION
            'El tipo documental no corresponde a una cotización de Empresa.';
    END IF;

    IF p_id_prestador IS NOT NULL
       OR NULLIF(btrim(p_descripcion_prestador), '') IS NOT NULL THEN

        RAISE EXCEPTION
            'Una cotización de Empresa no puede asociarse a un prestador.';
    END IF;

    IF NULLIF(btrim(p_empresa_cuit), '') IS NULL
       OR length(btrim(p_empresa_cuit)) > 11
       OR NULLIF(btrim(p_empresa_sucursal), '') IS NULL
       OR length(btrim(p_empresa_sucursal)) > 6
       OR NULLIF(btrim(p_descripcion_empresa), '') IS NULL
       OR length(btrim(p_descripcion_empresa)) > 200 THEN

        RAISE EXCEPTION
            'La identidad de la Empresa de la cotización no es válida.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN

        RAISE EXCEPTION
            'La identidad del documento de cotización no es válida.';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT
        r.estado,
        sr.permite_cotizacion_empresa
      INTO
        v_estado_requerimiento,
        v_permite_operacion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> compras.id_estado_requerimiento('PENDIENTE')
       OR NOT v_permite_operacion THEN

        RAISE EXCEPTION
            'La cotización de Empresa requiere un requerimiento activo '
            'de RRHH o SISTEMAS en estado PENDIENTE.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.empresa_cuit = btrim(p_empresa_cuit)
           AND rp.empresa_sucursal = btrim(p_empresa_sucursal)
           AND rp.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene una cotización activa para este requerimiento.';
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
        tipo_documento,
        fecha_documento,
        id_prestador,
        empresa_cuit,
        empresa_sucursal,
        dl_group_id,
        dl_folder_id,
        dl_file_entry_id,
        dl_file_uuid,
        nombre_original,
        nombre_persistido,
        titulo,
        alta_usr
    )
    VALUES (
        p_id_requerimiento,
        3,
        NULL,
        NULL,
        btrim(p_empresa_cuit),
        btrim(p_empresa_sucursal),
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        btrim(p_titulo),
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    RETURN v_id;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.requerimiento_base()
 RETURNS SETOF compras.requerimiento_base_row
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN
RETURN QUERY
SELECT
    r.id_requerimiento,

    r.alta_fecha,
    r.alta_usr,

    r.modi_fecha,
    r.modi_usr,

    r.baja_fecha,
    r.baja_usr,

    COALESCE(ident.cuil_titular,r.afiliado_cuil_titular)::varchar,
    COALESCE(ident.inte,r.afiliado_int),
    a.id_ospim::integer,

    a.nombre::varchar,
    a.apellido::varchar,

    NULLIF(
            concat_ws(
                    ', ',
                    NULLIF(btrim(a.apellido::varchar), ''),
                    NULLIF(btrim(a.nombre::varchar), '')
            ),
            ''
    )::VARCHAR AS afiliado_nombre_apellido,

    a.documento_tipo::varchar,
    a.docu_numero::varchar,

    NULLIF(
            concat_ws(
                    ' ',
                    NULLIF(
                            btrim(a.documento_tipo::varchar),
                            ''
                    ),
                    NULLIF(
                            btrim(a.docu_numero::varchar),
                            ''
                    )
            ),
            ''
    )::VARCHAR AS afiliado_documento,

    NULLIF(btrim(concat_ws(' ', NULLIF(btrim(d.afidom_calle),''), NULLIF(btrim(d.afidom_numero),''), CASE WHEN NULLIF(btrim(d.afidom_piso),'') IS NOT NULL THEN 'Piso ' || btrim(d.afidom_piso) END, CASE WHEN NULLIF(btrim(d.afidom_depto),'') IS NOT NULL THEN 'Dto. ' || btrim(d.afidom_depto) END, CASE WHEN NULLIF(btrim(d.afidom_oficina),'') IS NOT NULL THEN 'Of. ' || btrim(d.afidom_oficina) END)), '')::varchar,
    d.afidom_localidad_nombre::varchar,
    d.afidom_provincia_nombre::varchar,
    NULLIF(concat_ws(' ', NULLIF(btrim(d.afidom_cod_area_celular),''), NULLIF(btrim(d.afidom_celular),'')), '')::varchar,
    NULLIF(concat_ws(' ', NULLIF(btrim(d.afidom_cod_area_telefono),''), NULLIF(btrim(d.afidom_telefono),'')), '')::varchar,
    a.email::varchar,

    r.id_sector,
    s.descripcion,
    s.requiere_afiliado,

    (100 - r.cargo_tercerizadora)::integer,
    r.cargo_tercerizadora,
    r.id_tercerizadora,

    (r.cargo_tercerizadora > 0),
    r.surge,
    r.observaciones,

    r.estado,
    e.descripcion,
    r.legales,
    s.tipo_item,
    s.seleccionable_alta,
    s.permite_cotizacion_empresa,
    s.permite_orden_compra_directa,
    s.busqueda_nomenclador_medica,
    s.permite_medicamento_legacy,
    s.sector_reclamo_prestacional,
    e.codigo,
    COALESCE(e.descripcion_visual,e.descripcion),
    ARRAY(SELECT DISTINCT c.id_tipo_nomenclador FROM compras.tipo_prestacion t JOIN compras.tipo_prestacion_tipo_nomenclador c ON c.id_tipo_prestacion=t.id_tipo_prestacion WHERE t.id_sector=s.id_sector ORDER BY c.id_tipo_nomenclador)
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento s
              ON s.id_sector = r.id_sector
         JOIN compras.estado_requerimiento e ON e.id_estado = r.estado
         LEFT JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
         LEFT JOIN public.afiliado a ON a.cuil_titular = ident.cuil_titular AND a.inte = ident.inte
         LEFT JOIN LATERAL (
             SELECT domicilio.* FROM (
                 SELECT x.*, 0 AS prioridad FROM public.busca_afiliado_domicilio(a.cuil_titular,a.inte) x
                 UNION ALL
                 SELECT x.*, 1 AS prioridad FROM public.busca_afiliado_domicilio(a.cuil_titular,0) x WHERE a.inte <> 0
             ) domicilio ORDER BY prioridad, afidom_id_domicilio DESC LIMIT 1
         ) d ON TRUE;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.reservar_notificacion_cotizacion_prestador(p_id_requerimiento integer, p_id_prestador integer, p_usuario character varying)
 RETURNS TABLE(reservado boolean, estado_envio text, email_destino text, motivo_codigo text, motivo_descripcion text)
 LANGUAGE plpgsql
AS $function$
DECLARE
v_usuario VARCHAR(100);
    v_estado_requerimiento INTEGER;
    v_emails_reales TEXT;
    v_email_guardado TEXT;
    v_estado_actual VARCHAR(20);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'El id de requerimiento debe ser mayor que cero.';
END IF;

    IF p_id_prestador IS NULL
       OR p_id_prestador <= 0 THEN

        RAISE EXCEPTION
            'El id de prestador debe ser mayor que cero.';
END IF;

    v_usuario :=
        LEFT(
            COALESCE(
                NULLIF(
                    BTRIM(p_usuario),
                    ''
                ),
                'sistema'
            ),
            100
        );

SELECT
    r.estado
INTO
    v_estado_requerimiento
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo %.',
            p_id_requerimiento;
END IF;

IF v_estado_requerimiento NOT IN (compras.id_estado_requerimiento('PENDIENTE'), compras.id_estado_requerimiento('A_COTIZAR')) THEN
        RAISE EXCEPTION
            'El requerimiento % no se encuentra disponible para notificar prestadores.',
            p_id_requerimiento;
END IF;

/*
 * Primero se valida la existencia funcional del prestador.
 *
 * No se usa el resultado de esta consulta para resolver
 * el email. La validación y la resolución son responsabilidades
 * separadas.
 */
PERFORM 1
FROM public.prestador p
WHERE p.id_prestador = p_id_prestador
  AND COALESCE(
          p.solicitar_cotizacion,
          FALSE
      ) = TRUE
  AND p.baja_fecha IS NULL;

IF NOT FOUND THEN
    RAISE EXCEPTION
        'El prestador % no existe, esta dado de baja '
        'o no está habilitado para cotizar.',
        p_id_prestador;
END IF;

/*
 * Se obtienen los destinatarios mediante la política canónica plural.
 *
 * Puede devolver NULL. Eso no constituye una falla de reserva:
 * el servicio Java debe clasificarlo como EMAIL_INVALIDO
 * o como advertencia cuando se utiliza el destino temporal.
 */
SELECT
    compras.resolver_emails_cotizacion_prestador(
            p_id_prestador
    )
INTO
    v_emails_reales;

IF NOT EXISTS (
    SELECT 1
    FROM compras.requerimiento_detalle d
    JOIN compras.tipo_prestacion tp
      ON tp.id_tipo_prestacion = d.id_tipo_prestacion
    JOIN public.prestador_rubro pr
      ON pr.id_prestador = p_id_prestador
     AND pr.rubro = tp.rubro_prestador
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL
) THEN
    RAISE EXCEPTION
        'El prestador % no posee un rubro compatible con los tipos de prestación activos del requerimiento %.',
        p_id_prestador,
        p_id_requerimiento;
END IF;

    /*
     * Se crea la fila si todavía no existe.
     *
     * ON CONFLICT evita una excepción si dos transacciones
     * intentan crearla simultáneamente.
     */
INSERT INTO compras.requerimiento_cotizacion_prestador (
    id_requerimiento,
    id_prestador,
    estado_envio,
    intentos,
    email_destino,
    fecha_creacion,
    alta_usr
)
VALUES (
           p_id_requerimiento,
           p_id_prestador,
           'PENDIENTE',
           0,
           v_emails_reales,
           clock_timestamp(),
           v_usuario
       )
    ON CONFLICT (
        id_requerimiento,
        id_prestador
    )
    DO NOTHING;

/*
 * El bloqueo garantiza que sólo una ejecución pueda
 * analizar y modificar esta fila a la vez.
 */
SELECT
    rcp.estado_envio,
    rcp.email_destino
INTO
    v_estado_actual,
    v_email_guardado
FROM compras.requerimiento_cotizacion_prestador rcp
WHERE rcp.id_requerimiento =
      p_id_requerimiento
  AND rcp.id_prestador =
      p_id_prestador
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se pudo crear ni localizar la fila de '
            'notificación para requerimiento % y prestador %.',
            p_id_requerimiento,
            p_id_prestador;
END IF;

    IF v_estado_actual = 'ENVIADO' THEN
        RETURN QUERY
SELECT
    FALSE,
    v_estado_actual::TEXT,
    v_email_guardado::TEXT,
    'YA_ENVIADO'::TEXT,
    (
        'El prestador ya se encontraba ENVIADO. '
            || 'No se realizo un reenvio.'
        )::TEXT;

RETURN;
END IF;

    IF v_estado_actual = 'COTIZADO' THEN
        RETURN QUERY
SELECT
    FALSE,
    v_estado_actual::TEXT,
    v_email_guardado::TEXT,
    'YA_COTIZADO'::TEXT,
    (
        'El prestador ya se encontraba COTIZADO. '
            || 'No se realizo un reenvio.'
        )::TEXT;

RETURN;
END IF;

    IF v_estado_actual = 'PROCESANDO' THEN
        RETURN QUERY
SELECT
    FALSE,
    v_estado_actual::TEXT,
    v_email_guardado::TEXT,
    'YA_PROCESANDO'::TEXT,
    (
        'El prestador ya se encontraba PROCESANDO, '
            || 'posiblemente por otra ejecución concurrente.'
        )::TEXT;

RETURN;
END IF;

    /*
     * Los estados reintentables son:
     *
     * PENDIENTE
     * ERROR
     * EMAIL_INVALIDO
     */
UPDATE compras.requerimiento_cotizacion_prestador
SET
    estado_envio =
        'PROCESANDO',

    intentos =
        intentos + 1,

    email_destino =
        v_emails_reales,

    fecha_ultimo_intento =
        clock_timestamp(),

    fecha_envio =
        NULL,

    ultimo_error =
        NULL,

    modi_fecha =
        clock_timestamp(),

    modi_usr =
        v_usuario

WHERE id_requerimiento =
      p_id_requerimiento
  AND id_prestador =
      p_id_prestador;

RETURN QUERY
SELECT
    TRUE,
    'PROCESANDO'::TEXT,
    v_emails_reales::TEXT,
    'RESERVA_OTORGADA'::TEXT,
    (
        'La ejecución obtuvo la reserva exclusiva '
            || 'y la fila quedó PROCESANDO.'
        )::TEXT;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.reservar_reclamo_prestacional(p_id_requerimiento integer, p_token_reserva character varying, p_usuario character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
v_estado_requerimiento INTEGER;
    v_baja_fecha TIMESTAMP WITHOUT TIME ZONE;
    v_afiliado_cuil VARCHAR(20);
    v_afiliado_int INTEGER;
    v_estado_vinculo VARCHAR(20);
    v_token_actual VARCHAR(64);
    v_id_reclamo INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

    IF NULLIF(btrim(p_token_reserva), '') IS NULL THEN
        RAISE EXCEPTION
            'No se pudo validar el contexto de creación del Reclamo Prestacional.';
END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

SELECT
    r.estado,
    r.baja_fecha,
    r.afiliado_cuil_titular,
    r.afiliado_int
INTO
    v_estado_requerimiento,
    v_baja_fecha,
    v_afiliado_cuil,
    v_afiliado_int
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id_requerimiento
    FOR UPDATE;

IF NOT FOUND OR v_baja_fecha IS NOT NULL THEN
        RAISE EXCEPTION
            'No existe un requerimiento de compra activo con id %.',
            p_id_requerimiento;
END IF;

    IF v_estado_requerimiento <> compras.id_estado_requerimiento('COTIZADO') THEN
        RAISE EXCEPTION
            'El requerimiento % no se encuentra COTIZADO.',
            p_id_requerimiento;
END IF;

    IF NULLIF(btrim(v_afiliado_cuil), '') IS NULL
            OR v_afiliado_int IS NULL THEN
        RAISE EXCEPTION
            'El requerimiento % no posee un afiliado válido.',
            p_id_requerimiento;
END IF;

INSERT INTO compras.requerimiento_reclamo_prestacional (
    id_requerimiento,
    estado,
    token_reserva,
    reserva_fecha,
    alta_fecha,
    alta_usr
)
VALUES (
           p_id_requerimiento,
           'RESERVADO',
           btrim(p_token_reserva),
           now(),
           now(),
           v_usuario
       )
    ON CONFLICT (id_requerimiento) DO NOTHING;

IF FOUND THEN
        RETURN TRUE;
END IF;

SELECT
    rr.estado,
    rr.token_reserva,
    rr.id_reclamo_prestacional
INTO
    v_estado_vinculo,
    v_token_actual,
    v_id_reclamo
FROM compras.requerimiento_reclamo_prestacional rr
WHERE rr.id_requerimiento = p_id_requerimiento
    FOR UPDATE;

IF v_estado_vinculo = 'VINCULADO' THEN
        RAISE EXCEPTION
            'El requerimiento % ya posee el Reclamo Prestacional %.',
            p_id_requerimiento,
            v_id_reclamo;
END IF;

    IF v_estado_vinculo = 'ERROR' THEN
        RAISE EXCEPTION
            'El requerimiento % posee el Reclamo Prestacional % pendiente de reconciliacion.',
            p_id_requerimiento,
            v_id_reclamo;
END IF;

    RAISE EXCEPTION
        'Ya existe una creación de Reclamo Prestacional en proceso para el requerimiento %.',
        p_id_requerimiento;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.resolver_emails_cotizacion_prestador(p_id_prestador integer)
 RETURNS text
 LANGUAGE sql
 STABLE
AS $function$
SELECT string_agg(
           contacto.email,
           ';' ORDER BY
               contacto.fecha_referencia DESC NULLS LAST,
               contacto.id_contacto_e DESC,
               lower(contacto.email)
       )
FROM (
    SELECT DISTINCT ON (
        lower(btrim(ce.contacto))
    )
        btrim(ce.contacto) AS email,
        COALESCE(
            ce.modi_fecha,
            ce.alta_fecha,
            ce.vigen_desde,
            pce.vigen_desde
        ) AS fecha_referencia,
        ce.id_contacto_e
    FROM public.prestad_contacto_e pce
    JOIN public.contacto_e ce
      ON ce.id_contacto_e = pce.id_contacto_e
    WHERE pce.id_prestador = p_id_prestador
      AND upper(btrim(COALESCE(ce.tipo_contacto_e, ''))) = 'E'
      AND ce.baja_fecha IS NULL
      AND (pce.vigen_desde IS NULL OR pce.vigen_desde <= LOCALTIMESTAMP)
      AND (ce.vigen_desde IS NULL OR ce.vigen_desde <= LOCALTIMESTAMP)
      AND NULLIF(btrim(ce.contacto), '') IS NOT NULL
      AND NULLIF(btrim(ce.contacto), '')
          ~* '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$'
    ORDER BY
        lower(btrim(ce.contacto)),
        COALESCE(
            ce.modi_fecha,
            ce.alta_fecha,
            ce.vigen_desde,
            pce.vigen_desde
        ) DESC NULLS LAST,
        ce.id_contacto_e DESC
) contacto;
$function$;

-- Permisos de los objetos nuevos: mismo owner y privilegios que las tablas/
-- consultas legacy de configuracion. En reejecuciones no se pisan permisos.
DO $permisos_nuevos$
DECLARE objeto RECORD; propietario NAME; permisos ACLITEM[]; permiso RECORD;
        destinatario TEXT; ddl TEXT; actual ACLITEM[];
BEGIN
    FOR objeto IN SELECT * FROM (VALUES
        ('estado_requerimiento','TABLE','compras.estado_requerimiento','compras.sector_requerimiento'),
        ('tipo_prestacion_tipo_nomenclador','TABLE','compras.tipo_prestacion_tipo_nomenclador','compras.tipo_prestacion'),
        ('id_estado_requerimiento','FUNCTION','compras.id_estado_requerimiento(character varying)','compras.listar_estados_requerimiento()'),
        ('listar_tipos_nomenclador_compras','FUNCTION','compras.listar_tipos_nomenclador_compras()','compras.listar_tipos_prestacion()'),
        ('resolver_identidad_afiliado','FUNCTION','compras.resolver_identidad_afiliado(character varying,integer)','compras.get_requerimiento(integer)')
    ) x(nombre,tipo,destino,referencia)
    LOOP
        IF objeto.nombre=ANY(string_to_array(current_setting('compras.refactor_objetos_nuevos'),',')) THEN
            IF objeto.tipo='TABLE' THEN
                SELECT pg_get_userbyid(relowner),COALESCE(relacl,acldefault('r',relowner))
                INTO propietario,permisos FROM pg_class WHERE oid=to_regclass(objeto.referencia);
            ELSE
                SELECT pg_get_userbyid(proowner),COALESCE(proacl,acldefault('f',proowner))
                INTO propietario,permisos FROM pg_proc WHERE oid=to_regprocedure(objeto.referencia);
            END IF;
            IF propietario IS NULL THEN
                RAISE EXCEPTION 'Falta referencia de permisos: %',objeto.referencia;
            END IF;
            ddl := objeto.tipo || ' ' || objeto.destino;
            EXECUTE format('ALTER %s OWNER TO %I',ddl,propietario);
            IF objeto.tipo='TABLE' THEN
                SELECT COALESCE(relacl,acldefault('r',relowner)) INTO actual FROM pg_class WHERE oid=to_regclass(objeto.destino);
            ELSE
                SELECT COALESCE(proacl,acldefault('f',proowner)) INTO actual FROM pg_proc WHERE oid=to_regprocedure(objeto.destino);
            END IF;
            FOR permiso IN SELECT DISTINCT grantee FROM aclexplode(actual)
            LOOP
                destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
                EXECUTE 'REVOKE ALL ON ' || ddl || ' FROM ' || destinatario;
            END LOOP;
            FOR permiso IN SELECT * FROM aclexplode(permisos)
            LOOP
                destinatario := CASE WHEN permiso.grantee=0 THEN 'PUBLIC' ELSE quote_ident(pg_get_userbyid(permiso.grantee)) END;
                EXECUTE 'GRANT ' || permiso.privilege_type || ' ON ' || ddl || ' TO ' || destinatario
                    || CASE WHEN permiso.is_grantable THEN ' WITH GRANT OPTION' ELSE '' END;
            END LOOP;
        END IF;
    END LOOP;
END $permisos_nuevos$;

DO $catalogos$
BEGIN
    IF EXISTS (SELECT 1 FROM (VALUES (1,'PENDIENTE'),(2,'A_COTIZAR'),(3,'COTIZADO'),
        (4,'RECLAMO_RP'),(5,'ORDEN_COMPRA'),(99,'ANULADO')) x(id,codigo)
        LEFT JOIN compras.estado_requerimiento e ON e.id_estado=x.id
        WHERE e.codigo IS DISTINCT FROM x.codigo) THEN
        RAISE EXCEPTION 'IDs/codigos de estados incompatibles con el contrato Java';
    END IF;
    IF (SELECT count(*) FROM pg_class WHERE relnamespace='compras'::regnamespace AND relkind='r') <> 10 THEN
        RAISE EXCEPTION 'Se esperaban las ocho tablas existentes y las dos nuevas';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento r LEFT JOIN compras.estado_requerimiento e ON e.id_estado=r.estado WHERE e.id_estado IS NULL) THEN
        RAISE EXCEPTION 'Estado sin catalogo';
    END IF;
END $catalogos$;

DO $huerfanos$
BEGIN
    IF EXISTS (SELECT 1 FROM compras.requerimiento r LEFT JOIN compras.sector_requerimiento s ON s.id_sector=r.id_sector WHERE s.id_sector IS NULL) THEN
        RAISE EXCEPTION 'Requerimientos huerfanos de sector';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.tipo_prestacion t LEFT JOIN compras.sector_requerimiento s ON s.id_sector=t.id_sector WHERE s.id_sector IS NULL) THEN
        RAISE EXCEPTION 'Tipos de prestacion huerfanos de sector';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL)
       OR EXISTS (SELECT 1 FROM compras.requerimiento_presupuesto d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL)
       OR EXISTS (SELECT 1 FROM compras.requerimiento_cotizacion_prestador d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL)
       OR EXISTS (SELECT 1 FROM compras.requerimiento_pedido_cotizacion d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL)
       OR EXISTS (SELECT 1 FROM compras.requerimiento_reclamo_prestacional d LEFT JOIN compras.requerimiento r ON r.id_requerimiento=d.id_requerimiento WHERE r.id_requerimiento IS NULL) THEN
        RAISE EXCEPTION 'Detalles/documentos/cotizaciones/RP huerfanos de requerimiento';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d LEFT JOIN compras.tipo_prestacion t ON t.id_tipo_prestacion=d.id_tipo_prestacion WHERE d.id_tipo_prestacion IS NOT NULL AND t.id_tipo_prestacion IS NULL) THEN
        RAISE EXCEPTION 'Detalles huerfanos de tipo de prestacion';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_pedido_cotizacion d LEFT JOIN compras.requerimiento_cotizacion_prestador c ON c.id_requerimiento=d.id_requerimiento AND c.id_prestador=d.id_prestador WHERE c.id_requerimiento IS NULL) THEN
        RAISE EXCEPTION 'Documentos de pedido huerfanos de relacion de cotizacion';
    END IF;
    RAISE NOTICE 'HUERFANOS internos: 0';
END $huerfanos$;

-- Solo se guardan estadisticas de auditoria en la sesion/transaccion.
-- No se crean tablas auxiliares ni copias de datos. Los hashes son controles
-- adicionales; la conservacion se apoya tambien en la ausencia de DML historico.
DO $conservacion$
DECLARE
    actual JSONB := '{}'::jsonb;
    tabla JSONB;
    pk TEXT[];
    metadata JSONB;
BEGIN
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento',min(t.id_requerimiento), 'max_id_requerimiento',max(t.id_requerimiento),
        'activos',count(*) FILTER(WHERE t.baja_fecha IS NULL),
        'baja',count(*) FILTER(WHERE t.baja_fecha IS NOT NULL),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento t;
    actual := actual || jsonb_build_object('requerimiento',tabla);
    RAISE NOTICE 'POST requerimiento: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_detalle'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_detalle']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_detalle';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_detalle',min(t.id_detalle), 'max_id_detalle',max(t.id_detalle),
        'activos',count(*) FILTER(WHERE t.baja_fecha IS NULL),
        'baja',count(*) FILTER(WHERE t.baja_fecha IS NOT NULL),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_detalle t;
    actual := actual || jsonb_build_object('requerimiento_detalle',tabla);
    RAISE NOTICE 'POST requerimiento_detalle: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_presupuesto'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento_presupuesto']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_presupuesto';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento_presupuesto',min(t.id_requerimiento_presupuesto), 'max_id_requerimiento_presupuesto',max(t.id_requerimiento_presupuesto),
        'activos',count(*) FILTER(WHERE t.baja_fecha IS NULL),
        'baja',count(*) FILTER(WHERE t.baja_fecha IS NOT NULL),
        'documentos_dl',count(t.dl_file_entry_id),
        'archivos_dl_distintos',count(DISTINCT t.dl_file_entry_id),
        'presupuestos',count(*) FILTER(WHERE t.tipo_documento=1),
        'ordenes_medicas',count(*) FILTER(WHERE t.tipo_documento=2),
        'cotizaciones_empresa',count(*) FILTER(WHERE t.tipo_documento=3),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_presupuesto t;
    actual := actual || jsonb_build_object('requerimiento_presupuesto',tabla);
    RAISE NOTICE 'POST requerimiento_presupuesto: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_cotizacion_prestador'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento','id_prestador']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_cotizacion_prestador';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento',min(t.id_requerimiento), 'max_id_requerimiento',max(t.id_requerimiento),
        'min_id_prestador',min(t.id_prestador), 'max_id_prestador',max(t.id_prestador),
        'intentos',COALESCE(sum(t.intentos),0),
        'emails_reservados',count(t.email_destino),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_cotizacion_prestador t;
    actual := actual || jsonb_build_object('requerimiento_cotizacion_prestador',tabla);
    RAISE NOTICE 'POST requerimiento_cotizacion_prestador: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_pedido_cotizacion'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento','id_prestador','intento']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_pedido_cotizacion';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento',min(t.id_requerimiento), 'max_id_requerimiento',max(t.id_requerimiento),
        'min_id_prestador',min(t.id_prestador), 'max_id_prestador',max(t.id_prestador),
        'min_intento',min(t.intento), 'max_intento',max(t.intento),
        'documentos_dl',count(t.dl_file_entry_id),
        'archivos_dl_distintos',count(DISTINCT t.dl_file_entry_id),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_pedido_cotizacion t;
    actual := actual || jsonb_build_object('requerimiento_pedido_cotizacion',tabla);
    RAISE NOTICE 'POST requerimiento_pedido_cotizacion: %',tabla;
    SELECT array_agg(a.attname::text ORDER BY k.ord)
    INTO pk
    FROM pg_constraint c
    JOIN pg_index i ON i.indexrelid=c.conindid AND i.indisvalid AND i.indisready
    CROSS JOIN LATERAL unnest(c.conkey) WITH ORDINALITY k(attnum,ord)
    JOIN pg_attribute a ON a.attrelid=c.conrelid AND a.attnum=k.attnum
    WHERE c.conrelid='compras.requerimiento_reclamo_prestacional'::regclass AND c.contype='p';
    IF pk IS DISTINCT FROM ARRAY['id_requerimiento']::text[] THEN
        RAISE EXCEPTION 'PK ausente, invalida o inesperada: compras.requerimiento_reclamo_prestacional';
    END IF;
    SELECT jsonb_build_object(
        'filas',count(*),
        'min_id_requerimiento',min(t.id_requerimiento), 'max_id_requerimiento',max(t.id_requerimiento),
        'relaciones_rp',count(t.id_reclamo_prestacional),
        'tokens_reservados',count(t.token_reserva),
        'huella_a',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0),
        'huella_b',COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0))
    INTO tabla FROM compras.requerimiento_reclamo_prestacional t;
    actual := actual || jsonb_build_object('requerimiento_reclamo_prestacional',tabla);
    RAISE NOTICE 'POST requerimiento_reclamo_prestacional: %',tabla;
    -- OIDs, definiciones y atributos fisicos de las seis tablas se conservan.
    -- Las nuevas FKs se verifican aparte, por eso no se comparan aqui.
    SELECT jsonb_build_object(
        'columnas',(SELECT jsonb_agg(row_to_json(x) ORDER BY x.tabla,x.attnum)
            FROM (SELECT c.relname tabla,a.attnum,a.attname,a.atttypid,a.atttypmod,
                         a.attnotnull,pg_get_expr(d.adbin,d.adrelid) defecto
                  FROM pg_class c JOIN pg_attribute a ON a.attrelid=c.oid
                  LEFT JOIN pg_attrdef d ON d.adrelid=a.attrelid AND d.adnum=a.attnum
                  WHERE c.relnamespace='compras'::regnamespace
                    AND c.relname = ANY(ARRAY['requerimiento','requerimiento_detalle','requerimiento_presupuesto','requerimiento_cotizacion_prestador','requerimiento_pedido_cotizacion','requerimiento_reclamo_prestacional'])
                    AND a.attnum>0 AND NOT a.attisdropped) x),
        'pks',(SELECT jsonb_agg(row_to_json(x) ORDER BY x.oid)
            FROM (SELECT c.oid,c.conrelid,c.conindid,pg_get_constraintdef(c.oid) definicion
                  FROM pg_constraint c WHERE c.contype='p'
                    AND c.conrelid = ANY(ARRAY['compras.requerimiento'::regclass,'compras.requerimiento_detalle'::regclass,'compras.requerimiento_presupuesto'::regclass,'compras.requerimiento_cotizacion_prestador'::regclass,'compras.requerimiento_pedido_cotizacion'::regclass,'compras.requerimiento_reclamo_prestacional'::regclass])) x),
        'indices',(SELECT jsonb_agg(row_to_json(x) ORDER BY x.indexrelid)
            FROM (SELECT i.indexrelid,pg_get_indexdef(i.indexrelid) definicion
                  FROM pg_index i WHERE i.indrelid = ANY(ARRAY['compras.requerimiento'::regclass,'compras.requerimiento_detalle'::regclass,'compras.requerimiento_presupuesto'::regclass,'compras.requerimiento_cotizacion_prestador'::regclass,'compras.requerimiento_pedido_cotizacion'::regclass,'compras.requerimiento_reclamo_prestacional'::regclass])) x),
        'triggers',(SELECT jsonb_agg(row_to_json(x) ORDER BY x.oid)
            FROM (SELECT t.oid,t.tgenabled,pg_get_triggerdef(t.oid) definicion
                  FROM pg_trigger t WHERE NOT t.tgisinternal
                    AND t.tgrelid = ANY(ARRAY['compras.requerimiento'::regclass,'compras.requerimiento_detalle'::regclass,'compras.requerimiento_presupuesto'::regclass,'compras.requerimiento_cotizacion_prestador'::regclass,'compras.requerimiento_pedido_cotizacion'::regclass,'compras.requerimiento_reclamo_prestacional'::regclass])) x))
    INTO metadata;
    actual := actual || jsonb_build_object('estructura',metadata);
    IF current_setting('compras.refactor_conservacion',true) IS NULL
       OR actual IS DISTINCT FROM current_setting('compras.refactor_conservacion')::jsonb THEN
        RAISE EXCEPTION 'Cambio de contenido, contadores, PK, columnas, indices o triggers: se revierte FASE 1';
    END IF;
    RAISE NOTICE 'CONSERVACION OK: seis tablas transaccionales y su estructura original intactas';
END $conservacion$;

COMMIT;
