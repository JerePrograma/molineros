-- FASE 2: CLEANUP DIFERIDO. PostgreSQL 9.6.5. ISO-8859-1 sin BOM.
-- NO EJECUTAR AHORA. Requiere FASE 1 + Java desplegado + smoke UI/PDF/Excel/
-- mail/Document Library/RP + QA y revision de consumidores externos/dinamicos.
-- La aprobacion se registra en la sesion, nunca se deduce desde la DB:
-- SET compras.cleanup_autorizado = 'FASE1_CODIGO_UI_PDF_EXCEL_MAIL_DL_RP_QA_OK';
-- Ejecutar solamente despues, con psql -X -v ON_ERROR_STOP=1.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '120s';
DO $autorizacion$
BEGIN
    IF current_setting('compras.cleanup_autorizado',true) IS DISTINCT FROM
       'FASE1_CODIGO_UI_PDF_EXCEL_MAIL_DL_RP_QA_OK' THEN
        RAISE EXCEPTION 'CLEANUP BLOQUEADO: falta validacion completa y autorizacion explicita';
    END IF;
    IF to_regclass('compras.estado_requerimiento') IS NULL
       OR to_regclass('compras.tipo_prestacion_tipo_nomenclador') IS NULL THEN
        RAISE EXCEPTION 'Falta aplicar fase 1';
    END IF;
END $autorizacion$;

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

-- La identidad exacta de los cuerpos valida las lecturas/escrituras adaptadas.
-- Se normaliza a UTF-8 para que la huella no dependa del encoding de la DB.
DO $runtime$
DECLARE f RECORD; permitidos OID[] := ARRAY[]::oid[];
BEGIN
    FOR f IN SELECT * FROM (VALUES
        ('compras.buscar_empresas_cotizacion(varchar,varchar,varchar,integer)','06891ff1608f9a8e3d0ed73451a56e94',FALSE),
        ('compras.buscar_empresas_cotizacion_rapida(varchar,varchar,varchar,integer)','ad500cacd44bcab48d7fd87c6b0bd846',FALSE),
        ('compras.id_estado_requerimiento(varchar)','d326b1249f0d0cfd9c327cbc738569de',TRUE),
        ('compras.listar_tipos_nomenclador_compras()','46329d04e13ff24c98f711e0bf996055',TRUE),
        ('compras.resolver_identidad_afiliado(varchar,integer)','f02fed8ab0a327ab08ef75d1565e4783',TRUE),
        ('compras.normalizar_usuario(varchar)','b3d4b498af51c80ca47eb60624ae5276',FALSE),
        ('compras.normalizar_sector(varchar)','0c12c424c020dd2a6280b0e7e68f5efb',FALSE),
        ('compras.es_requerimiento_habilitado_busqueda_empresa_cotizacion(integer)','06741e6f07e9c332a953223079610852',TRUE),
        ('compras.listar_estados_requerimiento()','ee3cb9d135332ae8bfff3014c4ef6843',TRUE),
        ('compras.requerimiento_base()','c34d871b69542fe770c4c6414ec8036c',TRUE),
        ('compras.buscar_requerimientos(integer,integer,varchar,integer,varchar,boolean,boolean,varchar)','b35c3e18533372109eda2b71fd26d317',TRUE),
        ('compras.buscar_requerimientos(integer,integer,varchar,integer,varchar,boolean,boolean,varchar,date,date,integer)','c92750caab036f16aa2f008d218c3b60',TRUE),
        ('compras.buscar_requerimientos(integer,integer,varchar,integer,varchar,boolean,boolean,varchar,date,date)','d31acf7f146312aecb89a938fb99757c',TRUE),
        ('compras.get_requerimiento(integer)','ac1b940a9ca92a628421c08aed52259d',FALSE),
        ('compras.es_sector_seleccionable_compras(integer)','27f3023f58e2212ba4a584de72aaf7b3',TRUE),
        ('compras.guardar_requerimiento(integer,varchar,integer,integer,varchar,varchar,varchar,varchar,varchar,varchar,varchar,varchar,varchar,varchar,integer,integer,integer,varchar,boolean,boolean,boolean,text,varchar)','880f92455c377465f6b27eb3e2bc6560',TRUE),
        ('compras.cambiar_estado_requerimiento(integer,integer,varchar)','b5170ebd6c6eceb1affc6e1e3fd479b5',TRUE),
        ('compras.anular_requerimiento(integer,text,varchar)','fc12b03ba9d26b88a3585a6c9721682f',TRUE),
        ('compras.confirmar_orden_compra_requerimiento(integer,varchar)','fee33aabdbc4ba1095b143e0a65cb709',TRUE),
        ('compras.confirmar_envio_a_cotizar(integer,varchar)','bd02ad49da38a615757cc9d0cedf74a1',TRUE),
        ('compras.get_requerimiento_detalle(integer)','b83fb5ca74c4f7803dc375d9ec482426',TRUE),
        ('compras.get_requerimiento_detalle_clasificado(integer)','7ea140033f5bc75e5f791d7e7944db2e',TRUE),
        ('compras.guardar_requerimiento_detalle(integer,integer,varchar,integer,integer,varchar,varchar,integer,integer,varchar,integer,text,varchar)','e35ad450767661ad8b923e175417f90e',TRUE),
        ('compras.guardar_requerimiento_detalle_clasificado(integer,integer,varchar,integer,integer,varchar,varchar,integer,integer,varchar,integer,text,integer,varchar)','f2eca47dd6573aae231c0a061634e143',FALSE),
        ('compras.borrar_requerimiento_detalle(integer,varchar)','9280078995490473c431afd888c495cc',TRUE),
        ('compras.finalizar_cotizacion_requerimiento(integer,integer[],numeric[],integer,varchar)','91e13ff8ccc4a806a140403f043c70ef',TRUE),
        ('compras.resolver_emails_cotizacion_prestador(integer)','6ecdc178904055b464ffed9821365f71',TRUE),
        ('compras.resolver_email_cotizacion_prestador(integer)','8165215e8a7e2dea8e361dd1b4dc0486',FALSE),
        ('compras.listar_prestadores_cotizacion_requerimiento(integer)','a8e2f0c86ae1f5671aa1f8e3482e5071',TRUE),
        ('compras.registrar_cotizacion_prestador(integer,integer,varchar)','6a2448236680fb9805ea42218afdc4a8',TRUE),
        ('compras.finalizar_cotizacion_prestador(integer,integer,varchar,text)','8bbc6d6a57ed0da223659d8da011fc5e',FALSE),
        ('compras.listar_prestadores_enviados(integer,integer)','b040cb2547c8a5de866e5eeaf550cfeb',TRUE),
        ('compras.hay_prestadores_pendientes_notificacion(integer)','f411f84c1f3b152af0fb43ba23f134ed',FALSE),
        ('compras.get_requerimiento_compra_pdf(integer)','6e9128a6c7630d5f1235b0fa555c4d49',TRUE),
        ('compras.registrar_requerimiento_presupuesto(integer,integer,bigint,bigint,bigint,varchar,varchar,varchar,varchar,varchar,varchar)','88d2c8b2976dac78c0c9ad38dd25b3d0',TRUE),
        ('compras.registrar_requerimiento_presupuesto(integer,smallint,integer,varchar,varchar,varchar,bigint,bigint,bigint,varchar,varchar,varchar,varchar,varchar,varchar)','5a290a063b1763237dfb66f906a572ad',TRUE),
        ('compras.registrar_requerimiento_orden_medica(integer,bigint,bigint,bigint,varchar,varchar,varchar,varchar,date,varchar,varchar)','879b93797fe81317884e9ea082f51841',TRUE),
        ('compras.baja_requerimiento_presupuesto(integer,integer,varchar)','8c1b19f9c1a74f29f96473cdb5f490d3',TRUE),
        ('compras.reactivar_requerimiento_presupuesto(integer,integer)','0b224e6c426d9f5b38bcf3fc1bc5258b',TRUE),
        ('compras.baja_cotizacion_empresa_requerimiento(integer,integer,varchar)','bf760f758999f67e1ca9e60e99e8a033',TRUE),
        ('compras.reactivar_cotizacion_empresa_requerimiento(integer,integer)','0b317fdd6a980659539b38f74c43dd61',TRUE),
        ('compras.reservar_reclamo_prestacional(integer,varchar,varchar)','c8c19216f8887a90c15b9b34e27909dd',TRUE),
        ('compras.finalizar_reclamo_prestacional(integer,varchar,integer,varchar)','53558f6954b1a863529091a88c15cfb5',FALSE),
        ('compras.marcar_error_reclamo_prestacional(integer,varchar,integer,text,varchar)','c050b19256fba537d43098ed90a2ed8d',FALSE),
        ('compras.listar_prestadores_notificacion_cotizacion(integer)','ff77be9f35130a25d843c1427172a007',FALSE),
        ('compras.diagnosticar_prestadores_notificacion_cotizacion(integer)','a2415cbe00d007a8fa2d72dcf21f04a1',TRUE),
        ('compras.reservar_notificacion_cotizacion_prestador(integer,integer,varchar)','36e4971dcfd8a9eb5ed2e19266a64550',TRUE),
        ('compras.finalizar_notificacion_cotizacion_prestador(integer,integer,varchar,text,varchar)','3004e339a8ab9568888542f8531ac070',FALSE),
        ('compras.registrar_pedido_cotizacion_documento(integer,integer,bigint,bigint,bigint,varchar,varchar,varchar,varchar,varchar)','46dd46e4bde55eaa630d149996ea55c4',FALSE),
        ('compras.es_prestador_compatible_cotizacion(integer,integer)','2fa7f26097b9d0ee231880ed632c799a',TRUE),
        ('compras.guardar_cotizacion_requerimiento(integer,integer[],numeric[],integer[],integer,boolean,varchar)','553f8e46521431a3c233a1236889d469',TRUE),
        ('compras.guardar_cotizacion_requerimiento_call(integer,varchar,varchar,varchar,integer,boolean,varchar)','cb496afe272c440f9f0042fc9d8889cc',FALSE),
        ('compras.buscar_items_historicos_afiliado(varchar,integer,integer,integer,integer)','872f09920e9cf28826a854df5c80b50b',TRUE),
        ('compras.buscar_items_historicos_afiliado_clasificado(varchar,integer,integer,integer,integer)','a7502be57720aa88e29e848e72640ab4',TRUE),
        ('compras.existe_requerimiento_duplicado(varchar,integer,integer,date,integer)','32554b56f4938bbf47f7d71e0387bed7',TRUE),
        ('compras.listar_sectores_requerimiento()','fbcf02e286b6218838757123a660f13f',TRUE),
        ('compras.listar_tipos_prestacion()','3afaeba466848d5c01ebadd04a96bdb0',TRUE),
        ('compras.get_estado_requerimiento(integer)','82dc80bd9b8ef22c418268d0252e027e',TRUE),
        ('compras.get_sector_requerimiento(integer)','6557d780157beeb59fb3392be5a6d60e',TRUE),
        ('compras.listar_documentos_requerimiento(integer,integer)','87dcea014a417ef43a18c908c5bf5a49',TRUE),
        ('compras.get_documento_requerimiento(integer,integer,integer)','5dbdba6779d7d22e389f0ea86a3d6077',TRUE),
        ('compras.listar_ordenes_medicas_requerimiento(integer)','aafb2a7c964cfcf31ccdf80f383a06bd',TRUE),
        ('compras.tiene_situacion_medica_vigente(varchar,integer)','4856d55ae5f169c73208a5eab82ca6d9',FALSE),
        ('compras.listar_prestadores_adjudicados(integer)','a3479ce28a58f30c9496b788776ce5a3',FALSE),
        ('compras.listar_prestadores_adjudicados_batch(text)','5beece3cfe3fc5fd67db4d8463358f3d',TRUE),
        ('compras.listar_presupuestos_prestador(integer,integer)','7ac6a0b741c01476f95afd861717aa2d',TRUE),
        ('compras.get_pedido_cotizacion_prestador(integer,integer)','e4db919b07d885d2ebf3b23284f716c4',FALSE),
        ('compras.listar_configuracion_correos_rubro(integer)','fb91f7f430f8b08beb6c6f54f8c39e6f',TRUE),
        ('compras.get_requerimiento_reclamo_prestacional(integer)','b421b2fa1d1b67ba5fcba78267f0d044',FALSE),
        ('compras.listar_relaciones_reclamo_prestacional_batch(varchar,text)','f5f2c4a86661b986737499644fb1396c',FALSE),
        ('compras.listar_relaciones_reclamo_prestacional_por_reclamo(integer,varchar)','5e6aed6d08b9dc3511b9131b429c16e5',FALSE),
        ('compras.liberar_reserva_reclamo_prestacional(integer,varchar,varchar)','76ce7da80ed173e2ab17627f81602c5e',FALSE),
        ('compras.bloquear_requerimiento_reclamo_prestacional(integer)','f335ac350fc15eece6d51a81618d06bf',FALSE),
        ('compras.get_estado_requerimiento_for_update(integer)','9b8ac267ea0f8c1b29ac9e7e3ab00eae',FALSE),
        ('compras.buscar_prestadores_enviados(integer,varchar,integer)','1673ee24ecdff32248546fe707fff7e1',TRUE),
        ('compras.calcular_total_detalle_fila()','a34ecdb1693baf80cba15fce741719cd',TRUE),
        ('compras.liberar_copia_cotizacion_requerimiento(integer)','9b4e9b2538e1bfa86a250ad56f1cbfdb',FALSE),
        ('compras.reservar_copia_cotizacion_requerimiento(integer)','e21969af6f038135b3254a8dde5fe0d6',FALSE)
    ) x(firma,huella,obligatoria)
    LOOP
        IF (f.obligatoria OR to_regprocedure(f.firma) IS NOT NULL) AND NOT EXISTS (SELECT 1 FROM pg_proc p WHERE p.oid=to_regprocedure(f.firma)
                       AND md5(convert_to(replace(p.prosrc,E'\r\n',E'\n'),'UTF8'))=f.huella) THEN
            RAISE EXCEPTION 'Contrato pendiente de revision antes de cleanup: %',f.firma;
        END IF;
        IF to_regprocedure(f.firma) IS NOT NULL THEN
            permitidos := array_append(permitidos,to_regprocedure(f.firma)::oid);
        END IF;
    END LOOP;
    PERFORM set_config('compras.cleanup_consumidores',array_to_string(permitidos,','),true);
END $runtime$;

-- El trigger solo se retira despues de verificar los cuerpos atomicos de
-- anular_requerimiento y cambiar_estado_requerimiento en el bloque anterior.
DO $baja$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_trigger WHERE tgrelid='compras.requerimiento'::regclass
               AND tgname='trg_compras_requerimiento_completar_baja') THEN
        IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgrelid='compras.requerimiento'::regclass
               AND tgname='trg_compras_requerimiento_completar_baja' AND NOT tgisinternal
               AND tgfoid=to_regprocedure('compras.completar_baja_requerimiento_fila()')
               AND pg_get_triggerdef(oid)='CREATE TRIGGER trg_compras_requerimiento_completar_baja BEFORE UPDATE OF estado ON compras.requerimiento FOR EACH ROW EXECUTE PROCEDURE compras.completar_baja_requerimiento_fila()')
           OR NOT EXISTS (SELECT 1 FROM pg_proc WHERE oid=to_regprocedure('compras.completar_baja_requerimiento_fila()')
               AND md5(convert_to(replace(prosrc,E'\r\n',E'\n'),'UTF8'))='af71588a4d447459b3214071be909371') THEN
            RAISE EXCEPTION 'Trigger de baja distinto del relevado';
        END IF;
        DROP TRIGGER trg_compras_requerimiento_completar_baja ON compras.requerimiento RESTRICT;
    END IF;
END $baja$;

-- Antes de CADA funcion: firma exacta, ningun trigger y ningun caller ajeno
-- al conjunto retirado. Tambien se detectan llamadas sin prefijo de schema.
DO $funciones_muertas$
DECLARE firma TEXT; objeto OID; nombre TEXT; caller TEXT;
BEGIN
    FOREACH firma IN ARRAY ARRAY['compras.validar_requerimiento_fila()','compras.validar_requerimiento_detalle_fila()','compras.validar_tipo_prestacion_detalle_fila()','compras.validar_tipo_prestacion_detalle_nuevo()','compras.validar_prestador_cotizacion_fila()','compras.completar_baja_requerimiento_fila()','compras.normalizar_rubro(character varying)','compras.estado_requerimiento_descripcion(integer)']
    LOOP
        objeto := to_regprocedure(firma);
        IF objeto IS NOT NULL THEN
            SELECT proname INTO nombre FROM pg_proc WHERE oid=objeto;
            IF EXISTS (SELECT 1 FROM pg_trigger WHERE tgfoid=objeto) THEN
                RAISE EXCEPTION 'Funcion todavia asociada a trigger: %',firma;
            END IF;
            SELECT p.oid::regprocedure::text INTO caller FROM pg_proc p
            WHERE p.oid<>objeto AND NOT (p.pronamespace='compras'::regnamespace AND p.proname=ANY(ARRAY['validar_requerimiento_fila','validar_requerimiento_detalle_fila','validar_tipo_prestacion_detalle_fila','validar_tipo_prestacion_detalle_nuevo','validar_prestador_cotizacion_fila','completar_baja_requerimiento_fila','normalizar_rubro','estado_requerimiento_descripcion']))
              AND p.prosrc ~ ('\m' || nombre || '[[:space:]]*\(') LIMIT 1;
            IF caller IS NOT NULL THEN
                RAISE EXCEPTION 'Funcion % aun llamada desde %',firma,caller;
            END IF;
            IF EXISTS (SELECT 1 FROM pg_depend WHERE refclassid='pg_proc'::regclass
                       AND refobjid=objeto AND deptype IN ('n','a')) THEN
                RAISE EXCEPTION 'Dependencia registrada impide retirar %',firma;
            END IF;
            EXECUTE 'DROP FUNCTION ' || firma || ' RESTRICT';
        END IF;
    END LOOP;
END $funciones_muertas$;

DO $indice$
DECLARE objeto OID := to_regclass('compras.ix_compras_requerimiento_afiliado_id_ospim');
BEGIN
    IF objeto IS NOT NULL THEN
        IF NOT EXISTS (SELECT 1 FROM pg_index i
                       JOIN pg_attribute a ON a.attrelid=i.indrelid AND a.attname='afiliado_id_ospim'
                       WHERE i.indexrelid=objeto AND i.indrelid='compras.requerimiento'::regclass
                         AND i.indnatts=1 AND i.indkey[0]=a.attnum
                         AND NOT i.indisprimary AND NOT i.indisunique)
           OR EXISTS (SELECT 1 FROM pg_constraint WHERE conindid=objeto) THEN
            RAISE EXCEPTION 'Indice distinto del redundante relevado';
        END IF;
        DROP INDEX compras.ix_compras_requerimiento_afiliado_id_ospim RESTRICT;
    END IF;
END $indice$;

-- Antes de CADA columna se comprueba que siga existiendo y que no queden
-- dependencias catalogadas, incluso indices/constraints que DROP COLUMN
-- retiraria automaticamente. Solo se permite el DEFAULT propio de la columna.
-- Los consumidores SQL/JDBC externos o con SQL dinamico requieren la revision
-- operativa declarada al habilitar esta fase: pg_depend no puede acreditarla.
DO $columnas$
DECLARE c RECORD; relacion OID; atributo SMALLINT; caller TEXT;
BEGIN
    FOR c IN SELECT * FROM (VALUES
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
    ) x(tabla,columna)
    LOOP
        relacion := to_regclass('compras.' || c.tabla);
        SELECT attnum INTO atributo FROM pg_attribute
        WHERE attrelid=relacion AND attname=c.columna AND NOT attisdropped;
        IF atributo IS NOT NULL THEN
            SELECT p.oid::regprocedure::text INTO caller FROM pg_proc p
            WHERE NOT p.oid=ANY(string_to_array(current_setting('compras.cleanup_consumidores'),',')::oid[])
              AND p.prosrc ~ ('\m' || c.tabla || '\M')
              AND p.prosrc ~ ('\m' || c.columna || '\M') LIMIT 1;
            IF caller IS NOT NULL THEN
                RAISE EXCEPTION 'Consumidor no revisado para %.%: %',c.tabla,c.columna,caller;
            END IF;
            IF EXISTS (SELECT 1 FROM pg_depend WHERE refclassid='pg_class'::regclass
                       AND refobjid=relacion AND refobjsubid=atributo
                       AND NOT (classid='pg_attrdef'::regclass AND objid IN
                           (SELECT oid FROM pg_attrdef WHERE adrelid=relacion AND adnum=atributo))) THEN
                RAISE EXCEPTION 'Dependencia pendiente: compras.%.%',c.tabla,c.columna;
            END IF;
            EXECUTE format('ALTER TABLE compras.%I DROP COLUMN %I RESTRICT',c.tabla,c.columna);
        END IF;
    END LOOP;
END $columnas$;

DO $final$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns c JOIN (VALUES
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
    ) x(tabla,columna) ON c.table_name=x.tabla AND c.column_name=x.columna
       WHERE c.table_schema='compras') THEN
        RAISE EXCEPTION 'No se completo el retiro de las 18 columnas';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname='trg_compras_detalle_calcular_total'
        AND tgrelid='compras.requerimiento_detalle'::regclass AND NOT tgisinternal AND tgenabled='O') THEN
        RAISE EXCEPTION 'Debe conservarse el trigger de total editable';
    END IF;
END $final$;
COMMIT;
