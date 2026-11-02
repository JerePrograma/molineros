-- Normalizacion fisica de afiliado de Compras. Version 20261102_afiliado_v1.
-- SOLO QA12/devmolineros. ISO-8859-1 sin BOM. No ejecutar desde la aplicacion.
-- Referencia legacy: Liquidaciones usa CUIL+inte y compone los datos actuales.
-- Referencia de guardas: 20261030_refactor_java_configuracion.sql.
-- Baseline METADATA sin filas: .codex/artifacts/normalizacion-afiliado-20261102/baseline-metadata.json.
-- Los cuerpos anteriores/normalizados y sus fingerprints quedan en estos scripts; no se copian datos personales.
-- Firmas 23/24, base_row 34, PDF 43, 104 funciones y 5 triggers de usuario se conservan.
-- Reversion: reconstruye OBLIGATORIAMENTE los datos ACTUALES; los valores viejos se descartaron con autorizacion.
BEGIN;
SET LOCAL lock_timeout='2s';
SET LOCAL statement_timeout='15s';
-- No incluir compras: mantiene los nombres calificados de pg_get_functiondef del baseline.
SET LOCAL search_path=pg_catalog,public;
DO $destino$
BEGIN
    IF host(inet_server_addr()) IS DISTINCT FROM '12.1.1.12'
       OR current_database() IS DISTINCT FROM 'devmolineros' THEN
        RAISE EXCEPTION 'Destino incorrecto: solo QA12/devmolineros.';
    END IF;
    IF current_setting('transaction_read_only')<>'off' OR current_setting('session_replication_role')<>'origin' THEN
        RAISE EXCEPTION 'Se requiere una transaccion normal sin bypass de triggers.';
    END IF;
END;
$destino$;
LOCK TABLE compras.requerimiento IN ACCESS EXCLUSIVE MODE;
LOCK TABLE public.afiliado IN SHARE ROW EXCLUSIVE MODE;
DO $revertir_afiliado$
DECLARE
    v_tabla OID := 'compras.requerimiento'::regclass;
    v_copias INTEGER;
    v_cuil SMALLINT;
    v_inte SMALLINT;
    v_cuil_padron SMALLINT;
    v_inte_padron SMALLINT;
    v_funciones INTEGER;
    v_triggers INTEGER;
    v_manifest TEXT;
BEGIN
    SELECT count(*) INTO v_copias FROM pg_attribute
    WHERE attrelid=v_tabla AND attnum>0 AND NOT attisdropped AND attname=ANY(ARRAY['afiliado_id_ospim','afiliado_nombre','afiliado_apellido','afiliado_documento_tipo','afiliado_documento_nro','afiliado_direccion','afiliado_localidad','afiliado_provincia','afiliado_celular','afiliado_telefono','afiliado_email']);
    IF (SELECT count(*) FROM pg_index WHERE indrelid=v_tabla)<>(CASE WHEN v_copias=11 THEN 7 ELSE 6 END) THEN
        RAISE EXCEPTION 'Cantidad de indices locales distinta del baseline.';
    END IF;
    IF v_copias NOT IN (0,11) THEN
        RAISE EXCEPTION 'Estado parcial de columnas de afiliado; no modificar.';
    END IF;
    IF (SELECT count(*) FROM pg_attribute WHERE attrelid=v_tabla AND attnum>0 AND NOT attisdropped)
       <> (CASE WHEN v_copias=11 THEN 32 ELSE 21 END) THEN
        RAISE EXCEPTION 'La estructura de requerimiento difiere del baseline; no modificar.';
    END IF;
    -- Fingerprints de las columnas conservadas y reglas locales: DROP no debe retirar dependencias desconocidas.
    IF (SELECT md5(string_agg(a.attname||':'||format_type(a.atttypid,a.atttypmod)||':'||a.attnotnull::text||':'||COALESCE(pg_get_expr(d.adbin,d.adrelid),'<NULL>'),E'\n' ORDER BY a.attnum))
        FROM pg_attribute a LEFT JOIN pg_attrdef d ON d.adrelid=a.attrelid AND d.adnum=a.attnum
        WHERE a.attrelid=v_tabla AND a.attnum>0 AND NOT a.attisdropped AND NOT a.attname=ANY(ARRAY['afiliado_id_ospim','afiliado_nombre','afiliado_apellido','afiliado_documento_tipo','afiliado_documento_nro','afiliado_direccion','afiliado_localidad','afiliado_provincia','afiliado_celular','afiliado_telefono','afiliado_email'])) IS DISTINCT FROM 'ea4f47b117687b7d2841ef261ade0597'
       OR (SELECT md5(string_agg(c.conname||':'||pg_get_constraintdef(c.oid),E'\n' ORDER BY c.conname::text COLLATE "C"))
           FROM pg_constraint c WHERE c.conrelid=v_tabla AND c.conname<>'requerimiento_afiliado_fkey') IS DISTINCT FROM 'c044bf0ed188939f5056fff5a12a4934'
       OR (SELECT md5(string_agg(ic.relname||':'||pg_get_indexdef(i.indexrelid),E'\n' ORDER BY ic.relname::text COLLATE "C"))
           FROM pg_index i JOIN pg_class ic ON ic.oid=i.indexrelid WHERE i.indrelid=v_tabla
             AND ic.relname<>'ix_compras_requerimiento_afiliado_id_ospim' AND i.indisvalid AND i.indisready) IS DISTINCT FROM 'beee51984785292130c653021a6b3aff' THEN
        RAISE EXCEPTION 'Columnas conservadas, constraints o indices locales difieren del baseline.';
    END IF;
    IF (SELECT md5(string_agg(n.nspname||'.'||r.relname||':'||t.tgname||':'||t.tgenabled||':'||pg_get_triggerdef(t.oid,TRUE),E'\n'
        ORDER BY n.nspname::text COLLATE "C",r.relname::text COLLATE "C",t.tgname::text COLLATE "C"))
        FROM pg_trigger t JOIN pg_class r ON r.oid=t.tgrelid JOIN pg_namespace n ON n.oid=r.relnamespace
        JOIN pg_proc p ON p.oid=t.tgfoid JOIN pg_namespace pn ON pn.oid=p.pronamespace
        WHERE NOT t.tgisinternal AND (n.nspname='compras' OR pn.nspname='compras')) IS DISTINCT FROM 'b20adef34ba472bc939b3ed3e8cfc4cb' THEN
        RAISE EXCEPTION 'Las definiciones completas de triggers difieren del baseline.';
    END IF;
    SELECT attnum INTO v_cuil FROM pg_attribute WHERE attrelid=v_tabla AND attname='afiliado_cuil_titular' AND NOT attisdropped;
    SELECT attnum INTO v_inte FROM pg_attribute WHERE attrelid=v_tabla AND attname='afiliado_int' AND NOT attisdropped;
    SELECT attnum INTO v_cuil_padron FROM pg_attribute WHERE attrelid='public.afiliado'::regclass AND attname='cuil_titular' AND NOT attisdropped;
    SELECT attnum INTO v_inte_padron FROM pg_attribute WHERE attrelid='public.afiliado'::regclass AND attname='inte' AND NOT attisdropped;
    IF v_cuil IS NULL OR v_inte IS NULL OR v_cuil_padron IS NULL OR v_inte_padron IS NULL THEN
        RAISE EXCEPTION 'Faltan columnas de identidad; no modificar.';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_index i
        WHERE i.indexrelid=to_regclass('compras.ix_compras_requerimiento_afiliado')
          AND i.indrelid=v_tabla AND i.indisvalid AND i.indisready AND i.indnatts=2
          AND pg_get_indexdef(i.indexrelid,1,TRUE)='afiliado_cuil_titular'
          AND pg_get_indexdef(i.indexrelid,2,TRUE)='afiliado_int'
          AND pg_get_expr(i.indpred,i.indrelid)='(baja_fecha IS NULL)') THEN
        RAISE EXCEPTION 'El indice de CUIL e integrante no coincide con el baseline.';
    END IF;
    IF EXISTS(SELECT 1 FROM compras.requerimiento r LEFT JOIN public.afiliado a
        ON a.cuil_titular=r.afiliado_cuil_titular AND a.inte=r.afiliado_int
        WHERE (r.afiliado_cuil_titular IS NULL)<>(r.afiliado_int IS NULL)
           OR (r.afiliado_cuil_titular IS NOT NULL AND a.cuil_titular IS NULL)) THEN
        RAISE EXCEPTION 'Hay referencias de afiliado incompletas o inexistentes; no modificar.';
    END IF;
    SELECT count(*),md5(string_agg(p.proname||'('||pg_get_function_identity_arguments(p.oid)||')->'||
        pg_get_function_result(p.oid)||':'||md5(replace(pg_get_functiondef(p.oid),E'\r\n',E'\n')),
        E'\n' ORDER BY p.proname::text COLLATE "C",pg_get_function_identity_arguments(p.oid) COLLATE "C"))
    INTO v_funciones,v_manifest FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace WHERE n.nspname='compras';
    IF v_funciones<>104 OR v_manifest IS DISTINCT FROM (CASE WHEN v_copias=11 THEN '44f46d473022616f7097c5f489bbc7b8' ELSE '5e32d1f8ea4ce786e02e336f8c76a35c' END) THEN
        RAISE EXCEPTION 'Las funciones de Compras difieren del baseline o version normalizada; no modificar.';
    END IF;
    SELECT count(*) INTO v_triggers FROM pg_trigger t JOIN pg_class r ON r.oid=t.tgrelid
    JOIN pg_namespace n ON n.oid=r.relnamespace JOIN pg_proc p ON p.oid=t.tgfoid JOIN pg_namespace pn ON pn.oid=p.pronamespace
    WHERE NOT t.tgisinternal AND (n.nspname='compras' OR pn.nspname='compras');
    IF v_triggers<>5 OR EXISTS(SELECT 1 FROM pg_trigger t JOIN pg_class r ON r.oid=t.tgrelid
        JOIN pg_namespace n ON n.oid=r.relnamespace JOIN pg_proc p ON p.oid=t.tgfoid JOIN pg_namespace pn ON pn.oid=p.pronamespace
        WHERE NOT t.tgisinternal AND (n.nspname='compras' OR pn.nspname='compras') AND NOT ((n.nspname='autorizaciones' AND r.relname='reclamos_prestacionales' AND t.tgname='trg_compras_baja_vinculo_reclamo' AND t.tgenabled='O' AND pn.nspname='compras' AND p.proname='baja_vinculo_reclamo_prestacional_trigger') OR (n.nspname='compras' AND r.relname='requerimiento' AND t.tgname='trg_compras_requerimiento_completar_baja' AND t.tgenabled='O' AND pn.nspname='compras' AND p.proname='completar_baja_requerimiento_fila') OR (n.nspname='compras' AND r.relname='requerimiento' AND t.tgname='trg_compras_requerimiento_gastos_envio' AND t.tgenabled='O' AND pn.nspname='compras' AND p.proname='agregar_gastos_envio_requerimiento') OR (n.nspname='compras' AND r.relname='requerimiento_detalle' AND t.tgname='tr_compras_detalle_codigo_prestacion' AND t.tgenabled='O' AND pn.nspname='compras' AND p.proname='validar_codigo_prestacion_detalle_fila') OR (n.nspname='compras' AND r.relname='requerimiento_detalle' AND t.tgname='trg_compras_detalle_calcular_total' AND t.tgenabled='O' AND pn.nspname='compras' AND p.proname='calcular_total_detalle_fila'))) THEN
        RAISE EXCEPTION 'Los triggers de usuario de Compras difieren del baseline; no modificar.';
    END IF;
    IF (SELECT md5(string_agg(a.attname||':'||format_type(a.atttypid,a.atttypmod),';' ORDER BY a.attnum))
        FROM pg_attribute a WHERE a.attrelid='compras.requerimiento_base_row'::regclass AND a.attnum>0 AND NOT a.attisdropped)
       IS DISTINCT FROM '030638542ca7b603e21126602ff0d272'
       OR (SELECT md5(pg_get_function_result(p.oid)) FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace
           WHERE n.nspname='compras' AND p.proname='get_requerimiento_compra_pdf' AND p.pronargs=1) IS DISTINCT FROM '432c5c2b10f99079439877ae1965a1f3' THEN
        RAISE EXCEPTION 'El contrato base_row o PDF difiere del baseline; no modificar.';
    END IF;
    IF v_copias=0 AND (NOT EXISTS (SELECT 1 FROM pg_constraint c
        WHERE c.conrelid=v_tabla AND c.conname='requerimiento_afiliado_fkey' AND c.contype='f'
          AND c.confrelid='public.afiliado'::regclass AND c.convalidated
          AND c.confmatchtype='f' AND c.confupdtype='a' AND c.confdeltype='a' AND NOT c.condeferrable
          AND c.conkey=ARRAY[v_cuil,v_inte]
          AND c.confkey=ARRAY[v_cuil_padron,v_inte_padron])
        OR to_regclass('compras.ix_compras_requerimiento_afiliado_id_ospim') IS NOT NULL) THEN
        RAISE EXCEPTION 'La FK normalizada no coincide con MATCH FULL NO ACTION validada.';
    END IF;
    IF v_copias=11 THEN
        IF EXISTS (SELECT 1 FROM (VALUES ('afiliado_id_ospim','integer'),('afiliado_nombre','character varying(120)'),('afiliado_apellido','character varying(120)'),('afiliado_documento_tipo','character varying(10)'),('afiliado_documento_nro','character varying(30)'),('afiliado_direccion','character varying(250)'),('afiliado_localidad','character varying(120)'),('afiliado_provincia','character varying(120)'),('afiliado_celular','character varying(80)'),('afiliado_telefono','character varying(80)'),('afiliado_email','character varying(160)')) esperado(nombre,tipo)
            LEFT JOIN pg_attribute a ON a.attrelid=v_tabla AND a.attname=esperado.nombre AND a.attnum>0 AND NOT a.attisdropped
            LEFT JOIN pg_attrdef d ON d.adrelid=a.attrelid AND d.adnum=a.attnum
            WHERE a.attnum IS NULL OR format_type(a.atttypid,a.atttypmod)<>esperado.tipo OR a.attnotnull OR d.oid IS NOT NULL)
           OR EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid=v_tabla AND conname='requerimiento_afiliado_fkey')
           OR (SELECT pg_get_indexdef(to_regclass('compras.ix_compras_requerimiento_afiliado_id_ospim'))) IS DISTINCT FROM 'CREATE INDEX ix_compras_requerimiento_afiliado_id_ospim ON compras.requerimiento USING btree (afiliado_id_ospim) WHERE ((baja_fecha IS NULL) AND (afiliado_id_ospim IS NOT NULL))' THEN
            RAISE EXCEPTION 'Las copias previas o su indice tienen una definicion incompatible.';
        END IF;
        IF EXISTS(SELECT 1 FROM compras.requerimiento r WHERE
            (r.afiliado_cuil_titular IS NULL OR r.afiliado_int IS NULL) AND (NULLIF(btrim(r.afiliado_id_ospim::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_nombre::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_apellido::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_documento_tipo::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_documento_nro::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_direccion::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_localidad::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_provincia::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_celular::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_telefono::text),'') IS NOT NULL OR NULLIF(btrim(r.afiliado_email::text),'') IS NOT NULL)) THEN
            RAISE EXCEPTION 'Hay datos libres de afiliado sin referencia; no descartarlos.';
        END IF;
    END IF;
    IF v_copias=0 THEN
        ALTER TABLE compras.requerimiento DROP CONSTRAINT requerimiento_afiliado_fkey;
        ALTER TABLE compras.requerimiento
            ADD COLUMN afiliado_id_ospim integer,
            ADD COLUMN afiliado_nombre character varying(120),
            ADD COLUMN afiliado_apellido character varying(120),
            ADD COLUMN afiliado_documento_tipo character varying(10),
            ADD COLUMN afiliado_documento_nro character varying(30),
            ADD COLUMN afiliado_direccion character varying(250),
            ADD COLUMN afiliado_localidad character varying(120),
            ADD COLUMN afiliado_provincia character varying(120),
            ADD COLUMN afiliado_celular character varying(80),
            ADD COLUMN afiliado_telefono character varying(80),
            ADD COLUMN afiliado_email character varying(160);
        WITH actuales AS (SELECT r.id_requerimiento,
            a.id_ospim AS afiliado_id_ospim,
            a.nombre::VARCHAR AS afiliado_nombre,
            a.apellido::VARCHAR AS afiliado_apellido,
            a.documento_tipo::VARCHAR AS afiliado_documento_tipo,
            a.docu_numero::VARCHAR AS afiliado_documento_nro,
            NULLIF(concat_ws(' ',NULLIF(btrim(dom.calle),''),dom.numero::VARCHAR,CASE WHEN NULLIF(btrim(dom.piso),'') IS NOT NULL THEN 'Piso '||btrim(dom.piso) END,CASE WHEN NULLIF(btrim(dom.depto),'') IS NOT NULL THEN 'Dto '||btrim(dom.depto) END),'')::VARCHAR AS afiliado_direccion,
            loc.detalle::VARCHAR AS afiliado_localidad,
            prov.detalle::VARCHAR AS afiliado_provincia,
            NULLIF(concat_ws(' ',NULLIF(btrim(dom.cod_area_celular),''),NULLIF(btrim(dom.celular),'')),'')::VARCHAR AS afiliado_celular,
            NULLIF(concat_ws(' ',NULLIF(btrim(dom.cod_area_telefono),''),NULLIF(btrim(dom.telefono),'')),'')::VARCHAR AS afiliado_telefono,
            a.email::VARCHAR AS afiliado_email
FROM compras.requerimiento r
JOIN compras.sector_requerimiento s ON s.id_sector=r.id_sector
LEFT JOIN public.afiliado a ON a.cuil_titular=r.afiliado_cuil_titular AND a.inte=r.afiliado_int
LEFT JOIN LATERAL (
    SELECT ad.* FROM public.afi_domicilio ad
    WHERE ad.cuil_titular=a.cuil_titular AND (ad.inte=a.inte OR ad.inte=0)
      AND ad.domi_tipo='P' AND ad.alta_usr<>'datosveraz' AND ad.baja_fecha IS NULL
    ORDER BY CASE WHEN ad.inte=a.inte THEN 0 ELSE 1 END,ad.id_domicilio DESC LIMIT 1
) dom ON TRUE
LEFT JOIN public.localidad loc ON loc.id_localidad=dom.localidad
LEFT JOIN public.provincia prov ON prov.id_provincia=dom.provincia)
        UPDATE compras.requerimiento r SET
            afiliado_id_ospim=actuales.afiliado_id_ospim,
            afiliado_nombre=actuales.afiliado_nombre,
            afiliado_apellido=actuales.afiliado_apellido,
            afiliado_documento_tipo=actuales.afiliado_documento_tipo,
            afiliado_documento_nro=actuales.afiliado_documento_nro,
            afiliado_direccion=actuales.afiliado_direccion,
            afiliado_localidad=actuales.afiliado_localidad,
            afiliado_provincia=actuales.afiliado_provincia,
            afiliado_celular=actuales.afiliado_celular,
            afiliado_telefono=actuales.afiliado_telefono,
            afiliado_email=actuales.afiliado_email
        FROM actuales WHERE actuales.id_requerimiento=r.id_requerimiento;
        CREATE INDEX ix_compras_requerimiento_afiliado_id_ospim ON compras.requerimiento USING btree (afiliado_id_ospim) WHERE ((baja_fecha IS NULL) AND (afiliado_id_ospim IS NOT NULL));
        EXECUTE $ddl_restituir_guardar_requerimiento$
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
            estado, id_sector,
            afiliado_cuil_titular, afiliado_int, afiliado_id_ospim,
            afiliado_nombre, afiliado_apellido,
            afiliado_documento_tipo, afiliado_documento_nro,
            afiliado_direccion, afiliado_localidad, afiliado_provincia,
            afiliado_celular, afiliado_telefono, afiliado_email,
            cargo_ospim, cargo_tercerizadora, id_tercerizadora,
            recupero, surge, legales, observaciones, alta_usr
        )
        VALUES (
            1, p_id_sector,
            v_afiliado_cuil, p_afiliado_int, p_afiliado_id_ospim,
            NULLIF(btrim(p_afiliado_nombre), ''),
            NULLIF(btrim(p_afiliado_apellido), ''),
            NULLIF(btrim(p_afiliado_documento_tipo), ''),
            NULLIF(btrim(p_afiliado_documento_nro), ''),
            NULLIF(btrim(p_afiliado_direccion), ''),
            NULLIF(btrim(p_afiliado_localidad), ''),
            NULLIF(btrim(p_afiliado_provincia), ''),
            NULLIF(btrim(p_afiliado_celular), ''),
            NULLIF(btrim(p_afiliado_telefono), ''),
            NULLIF(btrim(p_afiliado_email), ''),
            COALESCE(p_cargo_ospim, 0),
            COALESCE(p_cargo_tercerizadora, 0),
            NULLIF(btrim(p_id_tercerizadora), ''),
            COALESCE(p_recupero, FALSE),
            COALESCE(p_surge, FALSE),
            COALESCE(p_legales, FALSE),
            NULLIF(btrim(p_observaciones), ''),
            v_usuario
        )
        RETURNING id_requerimiento INTO v_id;

        RETURN v_id;
    END IF;

    SELECT r.afiliado_cuil_titular, r.afiliado_int
    INTO v_cuil_anterior, v_inte_anterior
    FROM compras.requerimiento r
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
    SET cargo_ospim = COALESCE(p_cargo_ospim, 0),
        cargo_tercerizadora = COALESCE(p_cargo_tercerizadora, 0),
        id_tercerizadora = NULLIF(btrim(p_id_tercerizadora), ''),
        recupero = COALESCE(p_recupero, FALSE),
        surge = COALESCE(p_surge, FALSE),
        observaciones = NULLIF(btrim(p_observaciones), ''),
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id
      AND estado = 1
      AND baja_fecha IS NULL
    RETURNING id_requerimiento INTO v_id;

    IF v_id IS NULL THEN
        RAISE EXCEPTION
            'La estructura solo puede modificarse en estado PENDIENTE.';
    END IF;

    RETURN v_id;
END;
$function$;
$ddl_restituir_guardar_requerimiento$;
        EXECUTE $ddl_restituir_requerimiento_base$
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

    r.afiliado_cuil_titular,
    r.afiliado_int,
    r.afiliado_id_ospim,

    r.afiliado_nombre,
    r.afiliado_apellido,

    NULLIF(
            concat_ws(
                    ', ',
                    NULLIF(btrim(r.afiliado_apellido), ''),
                    NULLIF(btrim(r.afiliado_nombre), '')
            ),
            ''
    )::VARCHAR AS afiliado_nombre_apellido,

    r.afiliado_documento_tipo,
    r.afiliado_documento_nro,

    NULLIF(
            concat_ws(
                    ' ',
                    NULLIF(
                            btrim(r.afiliado_documento_tipo),
                            ''
                    ),
                    NULLIF(
                            btrim(r.afiliado_documento_nro),
                            ''
                    )
            ),
            ''
    )::VARCHAR AS afiliado_documento,

    r.afiliado_direccion,
    r.afiliado_localidad,
    r.afiliado_provincia,
    r.afiliado_celular,
    r.afiliado_telefono,
    r.afiliado_email,

    r.id_sector,
    s.descripcion,
    s.requiere_afiliado,

    r.cargo_ospim,
    r.cargo_tercerizadora,
    r.id_tercerizadora,

    r.recupero,
    r.surge,
    r.observaciones,

    r.estado,
    compras.estado_requerimiento_descripcion(
            r.estado
    ),
    r.legales
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento s
              ON s.id_sector = r.id_sector;
END;
$function$;
$ddl_restituir_requerimiento_base$;
        EXECUTE $ddl_restituir_validar_requerimiento_fila$
CREATE OR REPLACE FUNCTION compras.validar_requerimiento_fila()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_requiere_afiliado BOOLEAN;
    v_sector_descripcion VARCHAR(120);
    v_cambio_estructura BOOLEAN;
    v_usuario VARCHAR(100);
    v_fila_anulacion compras.requerimiento%ROWTYPE;
    v_fila_transicion compras.requerimiento%ROWTYPE;
    v_fila_observacion compras.requerimiento%ROWTYPE;
BEGIN
    -- Anulacion acotada: preserva cabecera y comprueba el RP bajo bloqueo.
    IF TG_OP = 'UPDATE' AND NEW.estado = 99 AND OLD.estado IN (1, 2, 3, 4) THEN
        IF NOT compras.puede_anular_requerimiento(OLD.id_requerimiento) THEN
            RAISE EXCEPTION 'El requerimiento no puede anularse desde su estado o vinculo actual.';
        END IF;
        v_fila_anulacion := NEW;
        v_fila_anulacion.estado := OLD.estado;
        v_fila_anulacion.motivo_baja := OLD.motivo_baja;
        v_fila_anulacion.modi_fecha := OLD.modi_fecha;
        v_fila_anulacion.modi_usr := OLD.modi_usr;
        v_fila_anulacion.baja_fecha := OLD.baja_fecha;
        v_fila_anulacion.baja_usr := OLD.baja_usr;
        IF v_fila_anulacion IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION 'La anulacion debe conservar los datos del requerimiento.';
        END IF;
        NEW.baja_fecha := COALESCE(NEW.baja_fecha, now());
        NEW.baja_usr := compras.normalizar_usuario(COALESCE(NEW.baja_usr, NEW.modi_usr));
        RETURN NEW;
    END IF;
    -- Permite solo observacion interna y auditoria, incluso en estados finales.
    IF TG_OP = 'UPDATE'
       AND ((OLD.estado IN (1, 2, 3, 4, 5, 6, 7)
             AND OLD.baja_fecha IS NULL) OR OLD.estado = 99) THEN
        v_fila_observacion := NEW;
        v_fila_observacion.observacion_interna := OLD.observacion_interna;
        v_fila_observacion.modi_fecha := OLD.modi_fecha;
        v_fila_observacion.modi_usr := OLD.modi_usr;
        IF v_fila_observacion IS NOT DISTINCT FROM OLD THEN
            RETURN NEW;
        END IF;
    END IF;
SELECT
    s.requiere_afiliado,
    s.descripcion
INTO
    v_requiere_afiliado,
    v_sector_descripcion
FROM compras.sector_requerimiento s
WHERE s.id_sector = NEW.id_sector
  AND s.activo = TRUE
  AND s.baja_fecha IS NULL;

IF v_requiere_afiliado IS NULL THEN
        RAISE EXCEPTION
            'El sector informado no existe o no está activo.';
END IF;

    IF TG_OP = 'INSERT' THEN

        IF NEW.estado <> 1 THEN
            RAISE EXCEPTION
                'Un requerimiento nuevo debe crearse en estado PENDIENTE.';
END IF;

ELSE

        /*
         * ORDEN_COMPRA y ANULADO solo admiten la observacion interna
         * validada antes; el resto de los datos permanece inmutable.
         */
        IF OLD.estado IN (5, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        -- Autorizacion previa a la OC: conservar toda la fila salvo estado y auditoria.
        IF OLD.estado IN (4, 6, 7)
           AND NEW IS DISTINCT FROM OLD THEN

            v_fila_transicion := NEW;
            v_fila_transicion.estado := OLD.estado;
            v_fila_transicion.modi_fecha := OLD.modi_fecha;
            v_fila_transicion.modi_usr := OLD.modi_usr;

            IF NOT (
                    (OLD.estado = 4 AND NEW.estado = 6
                     AND compras.normalizar_sector(v_sector_descripcion) NOT IN ('RRHH', 'SISTEMAS'))
                 OR (OLD.estado = 6 AND NEW.estado = 7)
                 OR (OLD.estado = 7 AND NEW.estado = 5)
               )
               OR v_fila_transicion IS DISTINCT FROM OLD THEN

                RAISE EXCEPTION
                    'El requerimiento no puede modificarse en el estado actual.';
            END IF;
        END IF;

        /*
         * COTIZADO también permanece bloqueado, salvo por la transición
         * funcional COTIZADO -> RECLAMO_RP.
         *
         * Se permite que cambiar_estado_requerimiento actualice
         * modi_fecha y modi_usr durante esa transición.
         */
        IF OLD.estado = 3
           AND NEW IS DISTINCT FROM OLD
           AND (
                  NEW.estado IS DISTINCT FROM 4
               OR NEW.id_requerimiento
                    IS DISTINCT FROM OLD.id_requerimiento
               OR NEW.alta_fecha
                    IS DISTINCT FROM OLD.alta_fecha
               OR NEW.alta_usr
                    IS DISTINCT FROM OLD.alta_usr
               OR NEW.baja_fecha
                    IS DISTINCT FROM OLD.baja_fecha
               OR NEW.baja_usr
                    IS DISTINCT FROM OLD.baja_usr
           ) THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        v_cambio_estructura :=
               NEW.id_sector IS DISTINCT FROM OLD.id_sector
            OR NEW.afiliado_cuil_titular
                IS DISTINCT FROM OLD.afiliado_cuil_titular
            OR NEW.afiliado_int
                IS DISTINCT FROM OLD.afiliado_int
            OR NEW.afiliado_id_ospim
                IS DISTINCT FROM OLD.afiliado_id_ospim
            OR NEW.afiliado_nombre
                IS DISTINCT FROM OLD.afiliado_nombre
            OR NEW.afiliado_apellido
                IS DISTINCT FROM OLD.afiliado_apellido
            OR NEW.afiliado_documento_tipo
                IS DISTINCT FROM OLD.afiliado_documento_tipo
            OR NEW.afiliado_documento_nro
                IS DISTINCT FROM OLD.afiliado_documento_nro
            OR NEW.afiliado_direccion
                IS DISTINCT FROM OLD.afiliado_direccion
            OR NEW.afiliado_localidad
                IS DISTINCT FROM OLD.afiliado_localidad
            OR NEW.afiliado_provincia
                IS DISTINCT FROM OLD.afiliado_provincia
            OR NEW.afiliado_celular
                IS DISTINCT FROM OLD.afiliado_celular
            OR NEW.afiliado_telefono
                IS DISTINCT FROM OLD.afiliado_telefono
            OR NEW.afiliado_email
                IS DISTINCT FROM OLD.afiliado_email
            OR NEW.cargo_ospim
                IS DISTINCT FROM OLD.cargo_ospim
            OR NEW.cargo_tercerizadora
                IS DISTINCT FROM OLD.cargo_tercerizadora
            OR NEW.id_tercerizadora
                IS DISTINCT FROM OLD.id_tercerizadora
            OR NEW.recupero
                IS DISTINCT FROM OLD.recupero
            OR NEW.legales
                IS DISTINCT FROM OLD.legales
            OR NEW.observaciones
                IS DISTINCT FROM OLD.observaciones;

        IF v_cambio_estructura
           AND OLD.estado <> 1 THEN

            RAISE EXCEPTION
                'La estructura solo puede modificarse en estado PENDIENTE.';
END IF;

        /*
         * SURGE no forma parte de la estructura del requerimiento.
         * Puede ajustarse durante la preparacion (PENDIENTE) y durante
         * la cotización (A_COTIZAR), pero queda congelado al cerrarla.
         */
        IF NEW.surge IS DISTINCT FROM OLD.surge
           AND OLD.estado NOT IN (1, 2) THEN

            RAISE EXCEPTION
                'SURGE solo puede modificarse en estado PENDIENTE o ENVIADO A COTIZAR.';
END IF;

        IF NEW.estado IS DISTINCT FROM OLD.estado THEN

            /*
             * Transiciones funcionales actualmente soportadas:
             *
             * 1 PENDIENTE -> 2 A_COTIZAR
             * 1 PENDIENTE -> 6 PARA_AUTORIZAR para RRHH/SISTEMAS
             * 1 PENDIENTE -> 99 ANULADO
             * 2 A_COTIZAR -> 3 COTIZADO
             * 2 A_COTIZAR -> 99 ANULADO
             * 3 COTIZADO -> 4 RECLAMO_RP
             * 4 RECLAMO_RP -> 6 PARA_AUTORIZAR
             * 6 PARA_AUTORIZAR -> 7 AUTORIZADO
             * 7 AUTORIZADO -> 5 ORDEN_COMPRA con adjudicacion y autorizacion previas
             */
            IF NOT (
                    (OLD.estado = 1 AND NEW.estado IN (2, 99))
                 OR (OLD.estado = 1 AND NEW.estado = 6
                     AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS'))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
                 OR (OLD.estado = 3 AND NEW.estado = 4)
                 OR (OLD.estado = 4 AND NEW.estado = 6
                     AND compras.normalizar_sector(v_sector_descripcion) NOT IN ('RRHH', 'SISTEMAS'))
                 OR (OLD.estado = 6 AND NEW.estado = 7)
                 OR (OLD.estado = 7 AND NEW.estado = 5)
            ) THEN

                RAISE EXCEPTION
                    'Transicion de estado inválida: % -> %.',
                    OLD.estado,
                    NEW.estado;
END IF;

            IF OLD.estado = 3
               AND NEW.estado = 4
               AND NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_reclamo_prestacional rr
                     WHERE rr.id_requerimiento = NEW.id_requerimiento
                       AND rr.estado = 'VINCULADO'
                       AND rr.id_reclamo_prestacional IS NOT NULL
               ) THEN

                RAISE EXCEPTION
                    'El requerimiento debe tener un Reclamo Prestacional vinculado antes de cambiar a RECLAMO_RP.';
END IF;

            IF OLD.estado = 1
               AND NEW.estado = 2 THEN

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento =
                           NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos un detalle antes de enviar a cotizar.';
END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_cotizacion_prestador rcp
                     WHERE rcp.id_requerimiento =
                           NEW.id_requerimiento
                       AND rcp.estado_envio = 'ENVIADO'
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos un prestador notificado como ENVIADO antes de pasar a A COTIZAR.';
END IF;

END IF;

            IF ((OLD.estado = 1 AND NEW.estado = 6)
                OR (OLD.estado = 6 AND NEW.estado = 7)
                OR (OLD.estado = 7 AND NEW.estado = 5))
               AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS') THEN

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento = NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
                END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento = NEW.id_requerimiento
                       AND rp.tipo_documento = 3
                       AND rp.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
                END IF;

                IF (SELECT count(*)
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento = NEW.id_requerimiento
                       AND rp.tipo_documento = 3
                       AND rp.baja_fecha IS NULL
                       AND rp.empresa_adjudicada) <> 1 THEN

                    RAISE EXCEPTION
                        'Debe seleccionar exactamente una Empresa adjudicada con cotizacion activa.';
                END IF;

                IF EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento = NEW.id_requerimiento
                       AND rp.tipo_documento = 3
                       AND rp.baja_fecha IS NULL
                       AND rp.empresa_adjudicada
                       AND (
                           NULLIF(btrim(rp.descripcion_empresa), '') IS NULL
                           OR (rp.empresa_cuit IS NOT NULL
                               AND (NULLIF(btrim(rp.empresa_cuit), '') IS NULL
                                    OR length(rp.empresa_cuit) > 11
                                    OR rp.empresa_cuit <> btrim(rp.empresa_cuit)))
                           OR (rp.empresa_sucursal IS NOT NULL
                               AND (rp.empresa_cuit IS NULL OR NOT EXISTS (
                                   SELECT 1 FROM compras.buscar_empresas_cotizacion(
                                       rp.empresa_cuit, NULL, rp.empresa_sucursal, 1)
                               )))
                       )
                ) THEN
                    RAISE EXCEPTION
                        'La Empresa adjudicada debe tener nombre y una identidad fiscal valida cuando este informada.';
                END IF;
            END IF;

            IF OLD.estado = 7 AND NEW.estado = 5
               AND compras.normalizar_sector(v_sector_descripcion) NOT IN ('RRHH', 'SISTEMAS') THEN
                PERFORM compras.bloquear_creacion_orden_compra(
                    NEW.id_requerimiento, NEW.modi_usr);
            END IF;

            IF OLD.estado = 2
               AND NEW.estado = 3 THEN

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento =
                           NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'No se puede cerrar una cotización sin detalles.';
END IF;

                IF EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento =
                           NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                       AND (
                              d.cantidad <= 0
                           OR d.precio_unitario_estimado IS NULL
                           OR d.precio_unitario_estimado < 0
                           OR d.precio_total_estimado IS NULL
                           OR d.id_prestador IS NULL
                           OR d.precio_total_estimado
                              <> round(
                                     d.cantidad
                                     * d.precio_unitario_estimado,
                                     2
                                 )
                           OR NOT EXISTS (
                                SELECT 1
                                  FROM compras.requerimiento_cotizacion_prestador rcp
                                 WHERE rcp.id_requerimiento =
                                       NEW.id_requerimiento
                                   AND rcp.id_prestador =
                                       d.id_prestador
                                   AND rcp.estado_envio =
                                       'COTIZADO'
                           )
                           OR NOT EXISTS (
                                SELECT 1
                                  FROM compras.requerimiento_presupuesto rp
                                 WHERE rp.id_requerimiento =
                                       NEW.id_requerimiento
                                   AND rp.tipo_documento = 1
                                   AND rp.id_prestador =
                                       d.id_prestador
                                   AND rp.baja_fecha IS NULL
                           )
                       )
                ) THEN

                    RAISE EXCEPTION
                        'No se puede cerrar la cotización: existen detalles incompletos o inválidos.';
END IF;

END IF;

            IF NEW.estado = 99 THEN

                v_usuario :=
                    compras.normalizar_usuario(
                        COALESCE(
                            NEW.modi_usr,
                            NEW.baja_usr
                        )
                    );

                NEW.baja_fecha :=
                    COALESCE(
                        NEW.baja_fecha,
                        now()
                    );

                NEW.baja_usr :=
                    COALESCE(
                        NULLIF(
                            btrim(NEW.baja_usr),
                            ''
                        ),
                        v_usuario
                    );

END IF;

END IF;

END IF;

    IF v_requiere_afiliado THEN

        IF NULLIF(
            btrim(NEW.afiliado_cuil_titular),
            ''
        ) IS NULL THEN

            RAISE EXCEPTION
                'Debe informar el CUIL titular del afiliado.';
END IF;

        IF NEW.afiliado_int IS NULL
           OR NEW.afiliado_int < 0 THEN

            RAISE EXCEPTION
                'Debe informar el integrante del afiliado.';
END IF;

ELSE

        NEW.afiliado_cuil_titular := NULL;
        NEW.afiliado_int := NULL;
        NEW.afiliado_id_ospim := NULL;

        NEW.afiliado_nombre := NULL;
        NEW.afiliado_apellido := NULL;
        NEW.afiliado_documento_tipo := NULL;
        NEW.afiliado_documento_nro := NULL;
        NEW.afiliado_direccion := NULL;
        NEW.afiliado_localidad := NULL;
        NEW.afiliado_provincia := NULL;
        NEW.afiliado_celular := NULL;
        NEW.afiliado_telefono := NULL;
        NEW.afiliado_email := NULL;

        IF compras.normalizar_sector(v_sector_descripcion)
                NOT IN ('RRHH', 'SISTEMAS') THEN
            NEW.cargo_ospim := 100;
            NEW.cargo_tercerizadora := 0;
        END IF;

        NEW.id_tercerizadora := NULL;
        NEW.recupero := COALESCE(NEW.cargo_tercerizadora, 0) > 0;

END IF;

RETURN NEW;
END;
$function$;
$ddl_restituir_validar_requerimiento_fila$;
    END IF;
    SELECT count(*),md5(string_agg(p.proname||'('||pg_get_function_identity_arguments(p.oid)||')->'||
        pg_get_function_result(p.oid)||':'||md5(replace(pg_get_functiondef(p.oid),E'\r\n',E'\n')),
        E'\n' ORDER BY p.proname::text COLLATE "C",pg_get_function_identity_arguments(p.oid) COLLATE "C"))
    INTO v_funciones,v_manifest FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace WHERE n.nspname='compras';
    IF v_funciones<>104 OR v_manifest IS DISTINCT FROM '44f46d473022616f7097c5f489bbc7b8' THEN
        RAISE EXCEPTION 'Las funciones posteriores no coinciden con la version esperada.';
    END IF;
    -- Verificacion final dentro de los mismos locks y transaccion.
    SELECT count(*) INTO v_copias FROM pg_attribute WHERE attrelid=v_tabla AND attnum>0 AND NOT attisdropped AND attname=ANY(ARRAY['afiliado_id_ospim','afiliado_nombre','afiliado_apellido','afiliado_documento_tipo','afiliado_documento_nro','afiliado_direccion','afiliado_localidad','afiliado_provincia','afiliado_celular','afiliado_telefono','afiliado_email']);
    IF v_copias<>11 OR (SELECT count(*) FROM pg_attribute WHERE attrelid=v_tabla AND attnum>0 AND NOT attisdropped)<>32 THEN
        RAISE EXCEPTION 'Postcondicion de columnas incumplida; revertir toda la transaccion.';
    END IF;
    -- Fingerprints de las columnas conservadas y reglas locales: DROP no debe retirar dependencias desconocidas.
    IF (SELECT md5(string_agg(a.attname||':'||format_type(a.atttypid,a.atttypmod)||':'||a.attnotnull::text||':'||COALESCE(pg_get_expr(d.adbin,d.adrelid),'<NULL>'),E'\n' ORDER BY a.attnum))
        FROM pg_attribute a LEFT JOIN pg_attrdef d ON d.adrelid=a.attrelid AND d.adnum=a.attnum
        WHERE a.attrelid=v_tabla AND a.attnum>0 AND NOT a.attisdropped AND NOT a.attname=ANY(ARRAY['afiliado_id_ospim','afiliado_nombre','afiliado_apellido','afiliado_documento_tipo','afiliado_documento_nro','afiliado_direccion','afiliado_localidad','afiliado_provincia','afiliado_celular','afiliado_telefono','afiliado_email'])) IS DISTINCT FROM 'ea4f47b117687b7d2841ef261ade0597'
       OR (SELECT md5(string_agg(c.conname||':'||pg_get_constraintdef(c.oid),E'\n' ORDER BY c.conname::text COLLATE "C"))
           FROM pg_constraint c WHERE c.conrelid=v_tabla AND c.conname<>'requerimiento_afiliado_fkey') IS DISTINCT FROM 'c044bf0ed188939f5056fff5a12a4934'
       OR (SELECT md5(string_agg(ic.relname||':'||pg_get_indexdef(i.indexrelid),E'\n' ORDER BY ic.relname::text COLLATE "C"))
           FROM pg_index i JOIN pg_class ic ON ic.oid=i.indexrelid WHERE i.indrelid=v_tabla
             AND ic.relname<>'ix_compras_requerimiento_afiliado_id_ospim' AND i.indisvalid AND i.indisready) IS DISTINCT FROM 'beee51984785292130c653021a6b3aff' THEN
        RAISE EXCEPTION 'Columnas conservadas, constraints o indices locales difieren del baseline.';
    END IF;
    IF (SELECT md5(string_agg(n.nspname||'.'||r.relname||':'||t.tgname||':'||t.tgenabled||':'||pg_get_triggerdef(t.oid,TRUE),E'\n'
        ORDER BY n.nspname::text COLLATE "C",r.relname::text COLLATE "C",t.tgname::text COLLATE "C"))
        FROM pg_trigger t JOIN pg_class r ON r.oid=t.tgrelid JOIN pg_namespace n ON n.oid=r.relnamespace
        JOIN pg_proc p ON p.oid=t.tgfoid JOIN pg_namespace pn ON pn.oid=p.pronamespace
        WHERE NOT t.tgisinternal AND (n.nspname='compras' OR pn.nspname='compras')) IS DISTINCT FROM 'b20adef34ba472bc939b3ed3e8cfc4cb' THEN
        RAISE EXCEPTION 'Las definiciones completas de triggers difieren del baseline.';
    END IF;
    IF EXISTS(SELECT 1 FROM pg_constraint WHERE conrelid=v_tabla AND conname='requerimiento_afiliado_fkey') OR pg_get_indexdef(to_regclass('compras.ix_compras_requerimiento_afiliado_id_ospim')) IS DISTINCT FROM 'CREATE INDEX ix_compras_requerimiento_afiliado_id_ospim ON compras.requerimiento USING btree (afiliado_id_ospim) WHERE ((baja_fecha IS NULL) AND (afiliado_id_ospim IS NOT NULL))' THEN RAISE EXCEPTION 'Postcondicion de reversion incumplida.'; END IF;
END;
$revertir_afiliado$;
COMMIT;
