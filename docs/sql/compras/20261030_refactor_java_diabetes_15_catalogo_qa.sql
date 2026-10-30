-- SOLO QA12.1.1.12/devmolineros. NO ejecutar en produccion28.
-- Preparar catalogo antes de activar asociacion; este archivo NO la modifica.
-- Fuente readonly: fuente_catalogo15_QA12_aprobada_3.json SHA256 a8df7f778727a9772cc7bd05a9cdc3a81299d8809278cc127edc7c5ad24bc874
-- Referencia: sql-scripts/funciones/conceptos/insertar_nomenclador_concepto.sql.
-- Tres IDs nuevos desde la secuencia real del DEFAULT; source11504/11505/11506 intactos.
-- Copia 3maestros,3planes,16conceptos EXACTOS (incl solapes11504).
-- No copiar Farmacia9/BOMBA11436/genericos. Sin tablas auxiliares persistentes.
-- Capturar los NOTICE de mapping y resultados como evidencia; secuencias pueden dejar gaps ante rollback.
BEGIN;
SET LOCAL statement_timeout='30s';
SET LOCAL lock_timeout='2s';
SET LOCAL search_path=pg_catalog,public,autorizaciones,compras;
DO $guard_qa12$
BEGIN
    IF inet_server_addr() IS DISTINCT FROM '12.1.1.12'::inet OR current_database()<>'devmolineros' THEN
        RAISE EXCEPTION 'Destino incorrecto. Catalogo Diabetes permitido SOLO en QA12/devmolineros.';
    END IF;
    IF current_setting('session_replication_role')<>'origin' OR current_setting('transaction_read_only')<>'off' THEN
        RAISE EXCEPTION 'Se requiere sesion normal de escritura QA12, sin bypass de triggers.';
    END IF;
    IF length(current_user)>15 THEN RAISE EXCEPTION 'Identidad de auditoria excede el contrato varchar15.'; END IF;
END;
$guard_qa12$;
LOCK TABLE public.tipo_nomenclador,public.plan,public.concepto_maestro,public.nomenclador_concepto_tipo IN SHARE MODE;
LOCK TABLE autorizaciones.nomenclador,public.plan_prestacion,public.nomenclador_conceptos IN SHARE ROW EXCLUSIVE MODE;
DO $catalogo_qa12$
DECLARE
    f RECORD; c RECORD; t RECORD;
    persistido autorizaciones.nomenclador%ROWTYPE;
    cantidad INTEGER; id_nuevo INTEGER; creado BOOLEAN;
    secuencia REGCLASS; secuencia_master REGCLASS; secuencia_conceptos REGCLASS;
    ultimo BIGINT; llamada BOOLEAN; maximo BIGINT; limite_master BIGINT; limite_conceptos BIGINT; id_concepto INTEGER;
    esperados TEXT[]; actuales TEXT[];
BEGIN
    IF NOT EXISTS(SELECT 1 FROM public.tipo_nomenclador WHERE id_tipo_nomenclador=15
            AND descripcion='INSUMOS DIABETES' AND cod_tipo_nomenclador='ID') THEN
        RAISE EXCEPTION 'Catalogo15 QA12 distinto de ID/INSUMOS DIABETES; no modificar.';
    END IF;
    IF NOT EXISTS(SELECT 1 FROM public.plan WHERE id_plan=1 AND descripcion='INTEGRAL' AND baja_fecha IS NULL)
       OR NOT EXISTS(SELECT 1 FROM public.concepto_maestro WHERE id=5 AND descripcion_original='GASTOS CONSULTORIO'
            AND valido_desde=DATE '1800-01-01' AND valido_hasta=DATE '2999-01-01')
       OR NOT EXISTS(SELECT 1 FROM public.concepto_maestro WHERE id=37 AND descripcion_original='HONORARIOS MEDICOS CONSULTORIO'
            AND valido_desde=DATE '1800-01-01' AND valido_hasta=DATE '2999-01-01')
       OR (SELECT count(*) FROM public.nomenclador_concepto_tipo WHERE
            (id=1 AND descripcion='Honorarios Ambulatorio') OR (id=2 AND descripcion='Honorarios Internacion') OR
            (id=3 AND descripcion='Gastos Ambulatorio') OR (id=4 AND descripcion='Gastos Internacion'))<>4 THEN
        RAISE EXCEPTION 'Padres plan/concepto/tipo QA12 no equivalen a fuente28; no inferir por ID.';
    END IF;
    IF EXISTS(SELECT 1 FROM pg_trigger WHERE tgrelid IN ('autorizaciones.nomenclador'::regclass,
            'public.plan_prestacion'::regclass,'public.nomenclador_conceptos'::regclass)
            AND NOT tgisinternal AND tgenabled<>'D') THEN
        RAISE EXCEPTION 'Existen triggers no certificados en maestros/planes/conceptos; revisar efectos antes de copiar.';
    END IF;
    -- Secuencias reales del DEFAULT: solo nextval monotono; nunca setval.
    -- QA12 master esta atrasada11391/max11503: consumir hasta superar max y source11506.
    -- Los gaps de secuencia no se revierten, como en cualquier INSERT/rollback PostgreSQL.
    FOR t IN SELECT * FROM (VALUES ('autorizaciones.nomenclador','id_prestacion'),
            ('public.nomenclador_conceptos','id')) AS origen(tabla,columna)
    LOOP
        SELECT s.oid::regclass INTO STRICT secuencia
        FROM pg_attrdef ad JOIN pg_attribute a ON a.attrelid=ad.adrelid AND a.attnum=ad.adnum
        JOIN pg_depend d ON d.classid='pg_attrdef'::regclass AND d.objid=ad.oid AND d.refclassid='pg_class'::regclass
        JOIN pg_class s ON s.oid=d.refobjid AND s.relkind='S'
        WHERE ad.adrelid=t.tabla::regclass AND a.attname=t.columna;
        EXECUTE format('SELECT last_value,is_called FROM %s',secuencia) INTO ultimo,llamada;
        EXECUTE format('SELECT coalesce(max(%I),0) FROM %s',t.columna,t.tabla::regclass) INTO maximo;
        IF t.tabla='autorizaciones.nomenclador' THEN
            secuencia_master=secuencia; limite_master=greatest(maximo,11506);
        ELSE
            secuencia_conceptos=secuencia; limite_conceptos=maximo;
        END IF;
        RAISE NOTICE 'QA12 secuencia %: last=%, called=%, max_tabla=%; solo nextval, sin setval.',secuencia,ultimo,llamada,maximo;
    END LOOP;
    FOR f IN SELECT * FROM (VALUES
            (11504,'9100059'::varchar(10),'SET DE INFUSION MMT 397'::varchar(150),'19a582c0b24ce5f5eab87b98d5e221ef'::varchar(32)),
            (11505,'9100060'::varchar(10),'SENSORES 7020'::varchar(150),'ef03eb98aa3c3770fceb53ed5687addc'::varchar(32)),
            (11506,'9100061'::varchar(10),'RESERVORIO MMT 332'::varchar(150),'8dad2bd43dc484ff2e23c6cdd96ab099'::varchar(32))
        ) AS fuente(id_origen,codigo,descripcion,md5_origen)
    LOOP
        SELECT count(*) INTO cantidad FROM autorizaciones.nomenclador
        WHERE id_tipo_nomenclador=15 AND btrim(codigo)=f.codigo;
        IF cantidad>1 THEN RAISE EXCEPTION 'Clave natural tipo15/codigo % duplicada; no modificar.',f.codigo; END IF;
        creado := cantidad=0;
        IF creado THEN
            LOOP
                id_nuevo=nextval(secuencia_master);
                EXIT WHEN id_nuevo>limite_master;
            END LOOP;
            limite_master=id_nuevo;
            INSERT INTO autorizaciones.nomenclador (
                id_prestacion,
                descripcion, marca_rein_liq, norma, id_especialidad, codigo, importe, q_gal, q_gal_ay, anest, gto, q_ay, resolucion, unidad_arancelaria, forma_facturacion, val_gal_honorario, val_gal_gtos, importe_gastos, importe_honorarios, coef_gastos, coef_honorarios, sur, modulo, observaciones, codigo_hospital, requiere_autorizacion, supra, id_sss, cirugia, plan_basico, enviar_ws_tercerizadora,
                id_tipo_nomenclador,alta_fecha,alta_usr,modi_fecha,modi_usr,baja_fecha,baja_usr)
            VALUES (
                id_nuevo,f.descripcion, 3, NULL, 0, f.codigo, '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '2026', NULL, NULL, '0.0000', '0.0000', '0.000000', '0.000000', '1.000000', '0.000000', FALSE, NULL, '', '', FALSE, FALSE, NULL, FALSE, NULL, FALSE,
                15,localtimestamp,current_user,localtimestamp,current_user,NULL,NULL)
            RETURNING id_prestacion INTO id_nuevo;
            INSERT INTO public.plan_prestacion (id_prestacion,id_plan,tope_cantidad,tope_importe,tope_individ_cantidad,tope_individ_importe)
            VALUES(id_nuevo,1,NULL,NULL,NULL,NULL);
            FOR c IN SELECT * FROM (VALUES
                (11504::integer,'9100059'::varchar(100),convert_from(decode('53455420444520494e46555349c3934e204d4d5420333937','hex'),'UTF8')::varchar(100),'1800-01-01'::date,'2999-01-01'::date,1::integer,37::integer),
                (11504::integer,'9100059'::varchar(100),'SET DE INFUSION MMT 397'::varchar(100),'2026-08-01'::date,'2999-01-01'::date,1::integer,37::integer),
                (11504::integer,'9100059'::varchar(100),convert_from(decode('53455420444520494e46555349c3934e204d4d5420333937','hex'),'UTF8')::varchar(100),'1800-01-01'::date,'2999-01-01'::date,2::integer,37::integer),
                (11504::integer,'9100059'::varchar(100),'SET DE INFUSION MMT 397'::varchar(100),'2026-08-01'::date,'2999-01-01'::date,2::integer,37::integer),
                (11504::integer,'9100059'::varchar(100),convert_from(decode('53455420444520494e46555349c3934e204d4d5420333937','hex'),'UTF8')::varchar(100),'1800-01-01'::date,'2999-01-01'::date,3::integer,5::integer),
                (11504::integer,'9100059'::varchar(100),'SET DE INFUSION MMT 397'::varchar(100),'2026-08-01'::date,'2999-01-01'::date,3::integer,5::integer),
                (11504::integer,'9100059'::varchar(100),convert_from(decode('53455420444520494e46555349c3934e204d4d5420333937','hex'),'UTF8')::varchar(100),'1800-01-01'::date,'2999-01-01'::date,4::integer,5::integer),
                (11504::integer,'9100059'::varchar(100),'SET DE INFUSION MMT 397'::varchar(100),'2026-08-01'::date,'2999-01-01'::date,4::integer,5::integer),
                (11505::integer,'9100060'::varchar(100),'SENSORES 7020'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,1::integer,37::integer),
                (11505::integer,'9100060'::varchar(100),'SENSORES 7020'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,2::integer,37::integer),
                (11505::integer,'9100060'::varchar(100),'SENSORES 7020'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,3::integer,5::integer),
                (11505::integer,'9100060'::varchar(100),'SENSORES 7020'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,4::integer,5::integer),
                (11506::integer,'9100061'::varchar(100),'RESERVORIO MMT 332'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,1::integer,37::integer),
                (11506::integer,'9100061'::varchar(100),'RESERVORIO MMT 332'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,2::integer,37::integer),
                (11506::integer,'9100061'::varchar(100),'RESERVORIO MMT 332'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,3::integer,5::integer),
                (11506::integer,'9100061'::varchar(100),'RESERVORIO MMT 332'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,4::integer,5::integer)
            ) AS datos(id_origen,codigo,descripcion,valido_desde,valido_hasta,tipo_id,concepto_id) WHERE id_origen=f.id_origen
            LOOP
                LOOP
                    id_concepto=nextval(secuencia_conceptos);
                    EXIT WHEN id_concepto>limite_conceptos;
                END LOOP;
                limite_conceptos=id_concepto;
                INSERT INTO public.nomenclador_conceptos (id,id_prestacion,codigo,descripcion,valido_desde,valido_hasta,
                        tipo_id,concepto_id,alta_fecha,alta_usr,modi_fecha,modi_usr)
                VALUES(id_concepto,id_nuevo,c.codigo,c.descripcion,c.valido_desde,c.valido_hasta,c.tipo_id,c.concepto_id,
                        current_date,current_user,current_date,current_user);
            END LOOP;
        ELSE
            SELECT id_prestacion INTO STRICT id_nuevo FROM autorizaciones.nomenclador
            WHERE id_tipo_nomenclador=15 AND btrim(codigo)=f.codigo;
        END IF;
        SELECT n.* INTO STRICT persistido FROM autorizaciones.nomenclador n WHERE n.id_prestacion=id_nuevo;
        IF ROW(persistido.descripcion, persistido.marca_rein_liq, persistido.norma, persistido.id_especialidad, persistido.codigo, persistido.importe, persistido.q_gal, persistido.q_gal_ay, persistido.anest, persistido.gto, persistido.q_ay, persistido.resolucion, persistido.unidad_arancelaria, persistido.forma_facturacion, persistido.val_gal_honorario, persistido.val_gal_gtos, persistido.importe_gastos, persistido.importe_honorarios, persistido.coef_gastos, persistido.coef_honorarios, persistido.sur, persistido.modulo, persistido.observaciones, persistido.codigo_hospital, persistido.requiere_autorizacion, persistido.supra, persistido.id_sss, persistido.cirugia, persistido.plan_basico, persistido.enviar_ws_tercerizadora)
                IS DISTINCT FROM ROW(f.descripcion, 3::smallint, NULL::character varying(500), 0::integer, f.codigo, '0.00'::numeric(11,2), '0.00'::numeric(10,2), '0.00'::numeric(10,2), '0.00'::numeric(10,2), '0.00'::numeric(10,2), '0.00'::numeric(10,2), '2026'::character varying(15), NULL::character varying(500), NULL::character varying(500), '0.0000'::numeric(10,4), '0.0000'::numeric(10,4), '0.000000'::numeric(10,6), '0.000000'::numeric(10,6), '1.000000'::numeric(10,6), '0.000000'::numeric(10,6), FALSE::boolean, NULL::boolean, ''::character varying(500), ''::character varying(15), FALSE::boolean, FALSE::boolean, NULL::integer, FALSE::boolean, NULL::boolean, FALSE::boolean)
                OR persistido.baja_fecha IS NOT NULL OR persistido.baja_usr IS NOT NULL THEN
            RAISE EXCEPTION 'Deriva en canonico tipo15/codigo %; no reparar ni sobrescribir.',f.codigo;
        END IF;
        IF (SELECT count(*) FROM public.plan_prestacion WHERE id_prestacion=id_nuevo)<>1
           OR NOT EXISTS(SELECT 1 FROM public.plan_prestacion WHERE id_prestacion=id_nuevo AND id_plan=1
                AND tope_cantidad IS NULL AND tope_importe IS NULL AND tope_individ_cantidad IS NULL AND tope_individ_importe IS NULL)
           OR EXISTS(SELECT 1 FROM autorizaciones.nomenclador_plan WHERE id_prestacion=id_nuevo) THEN
            RAISE EXCEPTION 'Planes/modalidades de codigo % no equivalen a fuente; no modificar.',f.codigo;
        END IF;
        SELECT array_agg(ROW(codigo,descripcion,tipo_id,concepto_id,valido_desde,valido_hasta)::text
                ORDER BY codigo,descripcion,tipo_id,concepto_id,valido_desde,valido_hasta)
        INTO esperados FROM (VALUES
                (11504::integer,'9100059'::varchar(100),convert_from(decode('53455420444520494e46555349c3934e204d4d5420333937','hex'),'UTF8')::varchar(100),'1800-01-01'::date,'2999-01-01'::date,1::integer,37::integer),
                (11504::integer,'9100059'::varchar(100),'SET DE INFUSION MMT 397'::varchar(100),'2026-08-01'::date,'2999-01-01'::date,1::integer,37::integer),
                (11504::integer,'9100059'::varchar(100),convert_from(decode('53455420444520494e46555349c3934e204d4d5420333937','hex'),'UTF8')::varchar(100),'1800-01-01'::date,'2999-01-01'::date,2::integer,37::integer),
                (11504::integer,'9100059'::varchar(100),'SET DE INFUSION MMT 397'::varchar(100),'2026-08-01'::date,'2999-01-01'::date,2::integer,37::integer),
                (11504::integer,'9100059'::varchar(100),convert_from(decode('53455420444520494e46555349c3934e204d4d5420333937','hex'),'UTF8')::varchar(100),'1800-01-01'::date,'2999-01-01'::date,3::integer,5::integer),
                (11504::integer,'9100059'::varchar(100),'SET DE INFUSION MMT 397'::varchar(100),'2026-08-01'::date,'2999-01-01'::date,3::integer,5::integer),
                (11504::integer,'9100059'::varchar(100),convert_from(decode('53455420444520494e46555349c3934e204d4d5420333937','hex'),'UTF8')::varchar(100),'1800-01-01'::date,'2999-01-01'::date,4::integer,5::integer),
                (11504::integer,'9100059'::varchar(100),'SET DE INFUSION MMT 397'::varchar(100),'2026-08-01'::date,'2999-01-01'::date,4::integer,5::integer),
                (11505::integer,'9100060'::varchar(100),'SENSORES 7020'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,1::integer,37::integer),
                (11505::integer,'9100060'::varchar(100),'SENSORES 7020'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,2::integer,37::integer),
                (11505::integer,'9100060'::varchar(100),'SENSORES 7020'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,3::integer,5::integer),
                (11505::integer,'9100060'::varchar(100),'SENSORES 7020'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,4::integer,5::integer),
                (11506::integer,'9100061'::varchar(100),'RESERVORIO MMT 332'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,1::integer,37::integer),
                (11506::integer,'9100061'::varchar(100),'RESERVORIO MMT 332'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,2::integer,37::integer),
                (11506::integer,'9100061'::varchar(100),'RESERVORIO MMT 332'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,3::integer,5::integer),
                (11506::integer,'9100061'::varchar(100),'RESERVORIO MMT 332'::varchar(100),'1800-01-01'::date,'2999-01-01'::date,4::integer,5::integer)
            ) AS datos(id_origen,codigo,descripcion,valido_desde,valido_hasta,tipo_id,concepto_id) WHERE id_origen=f.id_origen;
        SELECT array_agg(ROW(codigo,descripcion,tipo_id,concepto_id,valido_desde,valido_hasta)::text
                ORDER BY codigo,descripcion,tipo_id,concepto_id,valido_desde,valido_hasta)
        INTO actuales FROM public.nomenclador_conceptos WHERE id_prestacion=id_nuevo;
        IF esperados IS DISTINCT FROM actuales THEN
            RAISE EXCEPTION 'Multiset de conceptos codigo % distinto de fuente; no normalizar solapes ni agregar duplicados.',f.codigo;
        END IF;
        RAISE NOTICE 'QA12 catalogo15: source28_id=%, nuevo_id=%, codigo=%, creado=%, source_md5=%',
                f.id_origen,id_nuevo,f.codigo,creado,f.md5_origen;
    END LOOP;
END;
$catalogo_qa12$;
COMMIT;
-- Asociacion sector2/rubro8 queda intacta; activarla solo despues de Java compatible.
SELECT id_prestacion,id_tipo_nomenclador,codigo,descripcion,baja_fecha FROM autorizaciones.nomenclador
WHERE id_tipo_nomenclador=15 AND codigo IN ('9100059','9100060','9100061') ORDER BY codigo;
