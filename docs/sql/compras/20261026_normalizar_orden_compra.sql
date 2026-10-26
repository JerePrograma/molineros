-- Normalizacion de Orden de Compra: moneda fija ARS y fuentes relacionadas.
-- Referencia: Liquidaciones/buscar_ordenes_pago_ospim_reporte.sql, JOIN de acreedor.
-- PostgreSQL 9.6+. Archivo ISO-8859-1/LATIN1 sin BOM.
-- Aplicacion detenida, backup verificado y CONEXION NUEVA sin transaccion previa.
-- Ejecutar TODO y detenerse ante el primer error. No usa CASCADE ni inventa datos.
-- Preserva numeros, auditoria, condiciones editables, cantidad/precio e IVA/IIBB aprobados.
-- Los datos sin fuente (contactos, recepcion, plan) deben estar vacios antes de eliminarlos.
DROP TABLE IF EXISTS pg_temp.compras_normalizacion_oc_resultado;
CREATE TEMP TABLE compras_normalizacion_oc_resultado (resultado TEXT NOT NULL)
    ON COMMIT PRESERVE ROWS;
BEGIN;
SET LOCAL lock_timeout = '10s';
DO $precondicion$
DECLARE v_faltantes TEXT; v_extra TEXT;
BEGIN
    IF to_regclass('compras.requerimiento_orden_compra') IS NULL
       OR to_regclass('compras.requerimiento_orden_compra_detalle') IS NULL THEN
        RAISE EXCEPTION 'Faltan las tablas de Orden de Compra. Revisar la instalacion de 20261023.';
    END IF;
    IF to_regprocedure('compras.bloquear_creacion_orden_compra(integer,character varying)') IS NULL
       OR to_regprocedure('compras.normalizar_sector(character varying)') IS NULL
       OR to_regprocedure('compras.normalizar_usuario(character varying)') IS NULL THEN
        RAISE EXCEPTION 'Faltan funciones del circuito de Orden de Compra.';
    END IF;
    SELECT string_agg(x.tabla || '.' || x.columna, ', ' ORDER BY x.tabla, x.columna)
      INTO v_faltantes
      FROM (VALUES
         ('requerimiento_presupuesto','id_requerimiento_presupuesto'),
         ('requerimiento_presupuesto','id_requerimiento'), ('requerimiento_presupuesto','tipo_documento'),
         ('requerimiento_presupuesto','empresa_adjudicada'), ('requerimiento_presupuesto','descripcion_empresa'),
         ('requerimiento_presupuesto','empresa_cuit'), ('requerimiento_presupuesto','descripcion_prestador'),
         ('requerimiento_presupuesto','id_prestador'), ('requerimiento_presupuesto','fecha_documento'),
         ('requerimiento_presupuesto','baja_fecha'),
         ('requerimiento_detalle','tipo_item'), ('requerimiento_detalle','nombre_medicamento'),
         ('requerimiento_detalle','troquel'), ('requerimiento_detalle','id_medicamento'),
         ('requerimiento_detalle','codigo_nomenclador'), ('requerimiento_detalle','descripcion_nomenclador'),
         ('requerimiento_detalle','observaciones'), ('requerimiento','alta_usr')
      ) x(tabla,columna)
      WHERE NOT EXISTS (SELECT 1 FROM information_schema.columns c
                         WHERE c.table_schema = 'compras' AND c.table_name = x.tabla
                           AND c.column_name = x.columna);
    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Faltan fuentes del contrato de Compras: %.', v_faltantes;
    END IF;
    -- Solo se reemplazan los dos cuerpos conocidos; una regla local se preserva.
    IF NOT EXISTS (SELECT 1 FROM pg_proc
                    WHERE oid = to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)')
                      AND (md5(replace(prosrc, E'\r', '')), length(replace(prosrc, E'\r', '')))
                          IN (('eeae1fde2108d11c332c0101d64c2fa3', 1871), ('6a3737e2739bec7775c16bc46d720912', 4049))) THEN
        RAISE EXCEPTION 'Cuerpo de confirmacion de OC desconocido. Se conservo sin reemplazar.';
    END IF;
    -- Un catalogo parcial o con columnas desconocidas se conserva para revision.
    SELECT string_agg(table_name || '.' || column_name, ', ' ORDER BY table_name, ordinal_position)
      INTO v_extra FROM information_schema.columns
     WHERE table_schema = 'compras'
       AND ((table_name = 'requerimiento_orden_compra' AND column_name NOT IN (
           'id_orden_compra','id_requerimiento','id_presupuesto','fecha_emision',
           'fecha_orden_medica','fecha_recepcion','moneda','proveedor','proveedor_cuit',
           'proveedor_domicilio','proveedor_contacto','proveedor_email','proveedor_telefono',
           'condicion','forma_pago','plazo_pago','entrega','observaciones','confeccionado_por',
           'telefono_compras','solicitante','plan','id_reclamo_prestacional','iva','iibb',
           'alta_fecha','alta_usr','modi_fecha','modi_usr','version_edicion'))
        OR (table_name = 'requerimiento_orden_compra_detalle' AND column_name NOT IN (
           'id_orden_compra','orden','id_requerimiento_detalle','codigo_item','descripcion_item',
           'cantidad','precio_unitario','precio_total')));
    IF v_extra IS NOT NULL THEN
        RAISE EXCEPTION 'Columnas de OC desconocidas; no se eliminaron datos: %.', v_extra;
    END IF;
    IF (EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='compras'
                 AND table_name='requerimiento_orden_compra' AND column_name='moneda'))
       IS DISTINCT FROM
       (EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='compras'
                 AND table_name='requerimiento_orden_compra_detalle' AND column_name='descripcion_item')) THEN
        RAISE EXCEPTION 'La normalizacion de OC esta incompleta. Revisar el catalogo antes de continuar.';
    END IF;
END;
$precondicion$;
LOCK TABLE compras.requerimiento_orden_compra, compras.requerimiento_orden_compra_detalle
    IN ACCESS EXCLUSIVE MODE;
LOCK TABLE compras.requerimiento, compras.requerimiento_detalle,
    compras.requerimiento_presupuesto, compras.requerimiento_reclamo_prestacional
    IN SHARE MODE;
ALTER TABLE compras.requerimiento_orden_compra
    ADD COLUMN IF NOT EXISTS modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS modi_usr VARCHAR(100),
    ADD COLUMN IF NOT EXISTS version_edicion INTEGER NOT NULL DEFAULT 0 CHECK (version_edicion >= 0),
    ADD COLUMN IF NOT EXISTS id_presupuesto INTEGER;
DO $vincular_origen$
DECLARE v_ids TEXT;
BEGIN
    SELECT string_agg(o.id_orden_compra::TEXT, ', ' ORDER BY o.id_orden_compra) INTO v_ids
      FROM compras.requerimiento_orden_compra o
     WHERE o.id_presupuesto IS NULL AND (
        SELECT count(*) FROM compras.requerimiento_presupuesto rp
        JOIN compras.requerimiento r ON r.id_requerimiento = o.id_requerimiento
        JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
        WHERE rp.id_requerimiento = o.id_requerimiento AND rp.baja_fecha IS NULL
          AND ((compras.normalizar_sector(sr.descripcion) IN ('RRHH','SISTEMAS')
                AND rp.tipo_documento = 3 AND rp.empresa_adjudicada)
            OR (compras.normalizar_sector(sr.descripcion) NOT IN ('RRHH','SISTEMAS')
                AND rp.tipo_documento = 1 AND rp.id_prestador = (
                    SELECT min(id_prestador) FROM compras.requerimiento_detalle
                     WHERE id_requerimiento = o.id_requerimiento AND baja_fecha IS NULL
                    HAVING count(DISTINCT id_prestador) = 1
                       AND count(*) = count(id_prestador))))
     ) <> 1;
    IF v_ids IS NOT NULL THEN
        RAISE EXCEPTION 'No se pudo identificar una cotizacion de origen unica para las OC: %.', v_ids;
    END IF;
    UPDATE compras.requerimiento_orden_compra o SET id_presupuesto = (
        SELECT rp.id_requerimiento_presupuesto FROM compras.requerimiento_presupuesto rp
        JOIN compras.requerimiento r ON r.id_requerimiento = o.id_requerimiento
        JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
        WHERE rp.id_requerimiento = o.id_requerimiento AND rp.baja_fecha IS NULL
          AND ((compras.normalizar_sector(sr.descripcion) IN ('RRHH','SISTEMAS')
                AND rp.tipo_documento = 3 AND rp.empresa_adjudicada)
            OR (compras.normalizar_sector(sr.descripcion) NOT IN ('RRHH','SISTEMAS')
                AND rp.tipo_documento = 1 AND rp.id_prestador = (
                    SELECT min(id_prestador) FROM compras.requerimiento_detalle
                     WHERE id_requerimiento = o.id_requerimiento AND baja_fecha IS NULL
                    HAVING count(DISTINCT id_prestador) = 1
                       AND count(*) = count(id_prestador))))
    ) WHERE o.id_presupuesto IS NULL;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_orden_compra o
        JOIN compras.requerimiento r ON r.id_requerimiento = o.id_requerimiento
        JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
        LEFT JOIN compras.requerimiento_presupuesto rp
          ON rp.id_requerimiento_presupuesto = o.id_presupuesto
         AND rp.id_requerimiento = o.id_requerimiento
        WHERE rp.id_requerimiento_presupuesto IS NULL
           OR (compras.normalizar_sector(sr.descripcion) IN ('RRHH','SISTEMAS')
               AND rp.tipo_documento <> 3)
           OR (compras.normalizar_sector(sr.descripcion) NOT IN ('RRHH','SISTEMAS')
               AND rp.tipo_documento <> 1)
    ) THEN
        RAISE EXCEPTION 'La cotizacion de origen no pertenece al mismo requerimiento o tiene otro tipo.';
    END IF;
END;
$vincular_origen$;

-- Referencia legacy: buscar_ordenes_pago_ospim_reporte.sql cruza el acreedor.
-- La FK de presupuesto conserva el origen incluso ante una baja logica posterior.
CREATE OR REPLACE VIEW compras.requerimiento_orden_compra_consulta AS
SELECT o.id_orden_compra, o.id_requerimiento, o.id_presupuesto,
       o.fecha_emision,
       om.fecha AS fecha_orden_medica,
       NULL::DATE AS fecha_recepcion,
       'ARS'::VARCHAR(10) AS moneda,
       CASE WHEN rp.tipo_documento = 3 THEN rp.descripcion_empresa
            ELSE COALESCE(NULLIF(btrim(rp.descripcion_prestador), ''), p.descripcion)
       END::VARCHAR AS proveedor,
       CASE WHEN rp.tipo_documento = 3 THEN COALESCE(rp.empresa_cuit, '')
            ELSE COALESCE(p.cuit::VARCHAR, '') END::VARCHAR AS proveedor_cuit,
       NULL::VARCHAR AS proveedor_domicilio,
       NULL::VARCHAR AS proveedor_contacto,
       NULL::VARCHAR AS proveedor_email,
       NULL::VARCHAR AS proveedor_telefono,
       o.condicion, o.forma_pago, o.plazo_pago, o.entrega, o.observaciones,
       o.alta_usr::VARCHAR AS confeccionado_por,
       NULL::VARCHAR AS telefono_compras,
       r.alta_usr::VARCHAR AS solicitante,
       NULL::VARCHAR AS plan,
       COALESCE(rr.id_reclamo_prestacional, 0)::INTEGER AS id_reclamo_prestacional,
       o.iva, o.iibb, o.alta_fecha, o.alta_usr, o.modi_fecha, o.modi_usr,
       o.version_edicion
  FROM compras.requerimiento_orden_compra o
  JOIN compras.requerimiento r ON r.id_requerimiento = o.id_requerimiento
  LEFT JOIN compras.requerimiento_presupuesto rp
    ON rp.id_requerimiento_presupuesto = o.id_presupuesto
   AND rp.id_requerimiento = o.id_requerimiento
  LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
  LEFT JOIN compras.requerimiento_reclamo_prestacional rr
    ON rr.id_requerimiento = o.id_requerimiento
  LEFT JOIN LATERAL (
       SELECT CASE WHEN count(DISTINCT fecha_documento) = 1
                   THEN min(fecha_documento) END AS fecha
         FROM compras.requerimiento_presupuesto
        WHERE id_requerimiento = o.id_requerimiento
          AND tipo_documento = 2 AND baja_fecha IS NULL
  ) om ON TRUE;

CREATE OR REPLACE VIEW compras.requerimiento_orden_compra_detalle_consulta AS
SELECT od.id_orden_compra, od.orden, od.id_requerimiento_detalle,
       CASE WHEN d.tipo_item = 'MEDICAMENTO'
            THEN COALESCE(d.troquel::VARCHAR, d.id_medicamento::VARCHAR, '')
            WHEN d.tipo_item = 'NOMENCLADOR'
            THEN COALESCE(btrim(d.codigo_nomenclador, E' \t\n\r\f' || chr(11)), '')
            ELSE '' END::VARCHAR AS codigo_item,
       concat_ws(E'\n',
           NULLIF(btrim(CASE WHEN d.tipo_item = 'MEDICAMENTO' THEN d.nombre_medicamento
                            WHEN d.tipo_item = 'NOMENCLADOR' THEN d.descripcion_nomenclador
                            ELSE '' END, E' \t\n\r\f' || chr(11)), ''),
           NULLIF(btrim(d.observaciones, E' \t\n\r\f' || chr(11)), '')
       )::TEXT AS descripcion_item,
       od.cantidad, od.precio_unitario,
       round(od.cantidad * od.precio_unitario, 2)::NUMERIC AS precio_total
  FROM compras.requerimiento_orden_compra_detalle od
  JOIN compras.requerimiento_orden_compra o USING (id_orden_compra)
  JOIN compras.requerimiento_detalle d
    ON d.id_detalle = od.id_requerimiento_detalle
   AND d.id_requerimiento = o.id_requerimiento;

-- Una vista requiere su propio SELECT: conservar los roles del contrato previo.
-- No se revocan permisos existentes ni se agrega PUBLIC si la tabla no lo tenia.
DO $permisos_consulta$
DECLARE v_permiso RECORD; v_rol TEXT;
BEGIN
    FOR v_permiso IN
        SELECT DISTINCT f.vista, p.grantee, p.is_grantable
          FROM (VALUES
             ('requerimiento_orden_compra', 'requerimiento_orden_compra_consulta'),
             ('requerimiento_orden_compra_detalle', 'requerimiento_orden_compra_detalle_consulta')
          ) f(tabla,vista)
          JOIN information_schema.table_privileges p
            ON p.table_schema = 'compras' AND p.table_name = f.tabla
         WHERE p.privilege_type = 'SELECT'
         ORDER BY f.vista, p.grantee, p.is_grantable
    LOOP
        v_rol := CASE WHEN v_permiso.grantee = 'PUBLIC' THEN 'PUBLIC'
                      ELSE quote_ident(v_permiso.grantee) END;
        EXECUTE format('GRANT SELECT ON TABLE compras.%I TO %s%s',
            v_permiso.vista, v_rol,
            CASE WHEN v_permiso.is_grantable = 'YES' THEN ' WITH GRANT OPTION' ELSE '' END);
    END LOOP;
END;
$permisos_consulta$;

DO $conservar_datos$
DECLARE v_ids TEXT;
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='compras'
                AND table_name='requerimiento_orden_compra' AND column_name='moneda') THEN
        SELECT string_agg(o.id_orden_compra::TEXT, ', ' ORDER BY o.id_orden_compra) INTO v_ids
          FROM compras.requerimiento_orden_compra o
          JOIN compras.requerimiento_orden_compra_consulta v USING (id_orden_compra)
         WHERE NULLIF(btrim(o.moneda), '') IS NOT NULL AND btrim(o.moneda) <> 'ARS';
        IF v_ids IS NOT NULL THEN
            RAISE EXCEPTION 'Las OC % tienen moneda distinta de pesos. No se convirtieron importes ni eliminaron datos.', v_ids;
        END IF;
        SELECT string_agg(o.id_orden_compra::TEXT, ', ' ORDER BY o.id_orden_compra) INTO v_ids
          FROM compras.requerimiento_orden_compra o
          JOIN compras.requerimiento_orden_compra_consulta v USING (id_orden_compra)
         WHERE COALESCE(btrim(o.proveedor), '') IS DISTINCT FROM COALESCE(btrim(v.proveedor), '')
            OR COALESCE(btrim(o.proveedor_cuit), '') IS DISTINCT FROM COALESCE(btrim(v.proveedor_cuit), '')
            OR o.fecha_orden_medica IS DISTINCT FROM v.fecha_orden_medica
            OR COALESCE(o.id_reclamo_prestacional, 0) IS DISTINCT FROM v.id_reclamo_prestacional
            OR (NULLIF(btrim(o.solicitante), '') IS NOT NULL
                AND btrim(o.solicitante) IS DISTINCT FROM v.solicitante)
            OR o.fecha_recepcion IS NOT NULL
            OR NULLIF(btrim(o.proveedor_domicilio), '') IS NOT NULL
            OR NULLIF(btrim(o.proveedor_contacto), '') IS NOT NULL
            OR NULLIF(btrim(o.proveedor_email), '') IS NOT NULL
            OR NULLIF(btrim(o.proveedor_telefono), '') IS NOT NULL
            OR NULLIF(btrim(o.telefono_compras), '') IS NOT NULL
            OR NULLIF(btrim(o.plan), '') IS NOT NULL;
        IF v_ids IS NOT NULL THEN
            RAISE EXCEPTION 'Las OC % contienen datos sin fuente equivalente. Se conservaron las columnas; revisar antes de normalizar.', v_ids;
        END IF;
        SELECT string_agg(DISTINCT od.id_orden_compra::TEXT, ', ') INTO v_ids
          FROM compras.requerimiento_orden_compra_detalle od
          LEFT JOIN compras.requerimiento_orden_compra_detalle_consulta v
            ON v.id_orden_compra = od.id_orden_compra AND v.orden = od.orden
         WHERE v.id_orden_compra IS NULL
            OR COALESCE(od.codigo_item, '') IS DISTINCT FROM COALESCE(v.codigo_item, '')
            OR od.descripcion_item IS DISTINCT FROM v.descripcion_item
            OR od.precio_total IS DISTINCT FROM v.precio_total;
        IF v_ids IS NOT NULL THEN
            RAISE EXCEPTION 'Los detalles de las OC % no se reconstruyen exactamente desde su origen. No se eliminaron datos.', v_ids;
        END IF;
    END IF;
END;
$conservar_datos$;
ALTER TABLE compras.requerimiento_orden_compra
    ALTER COLUMN id_presupuesto SET NOT NULL,
    ALTER COLUMN iva DROP NOT NULL, ALTER COLUMN iva DROP DEFAULT,
    ALTER COLUMN iibb DROP NOT NULL, ALTER COLUMN iibb DROP DEFAULT,
    DROP COLUMN IF EXISTS fecha_orden_medica,
    DROP COLUMN IF EXISTS fecha_recepcion,
    DROP COLUMN IF EXISTS moneda,
    DROP COLUMN IF EXISTS proveedor,
    DROP COLUMN IF EXISTS proveedor_cuit,
    DROP COLUMN IF EXISTS proveedor_domicilio,
    DROP COLUMN IF EXISTS proveedor_contacto,
    DROP COLUMN IF EXISTS proveedor_email,
    DROP COLUMN IF EXISTS proveedor_telefono,
    DROP COLUMN IF EXISTS confeccionado_por,
    DROP COLUMN IF EXISTS telefono_compras,
    DROP COLUMN IF EXISTS solicitante,
    DROP COLUMN IF EXISTS plan,
    DROP COLUMN IF EXISTS id_reclamo_prestacional;
ALTER TABLE compras.requerimiento_orden_compra_detalle
    ALTER COLUMN precio_unitario DROP NOT NULL,
    DROP COLUMN IF EXISTS codigo_item,
    DROP COLUMN IF EXISTS descripcion_item,
    DROP COLUMN IF EXISTS precio_total;
DO $clave_origen$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
                    WHERE conrelid='compras.requerimiento_orden_compra'::regclass
                      AND confrelid='compras.requerimiento_presupuesto'::regclass
                      AND contype='f'
                      AND conkey=ARRAY[(SELECT attnum FROM pg_attribute
                          WHERE attrelid='compras.requerimiento_orden_compra'::regclass
                            AND attname='id_presupuesto')]::SMALLINT[]
                      AND confkey=ARRAY[(SELECT attnum FROM pg_attribute
                          WHERE attrelid='compras.requerimiento_presupuesto'::regclass
                            AND attname='id_requerimiento_presupuesto')]::SMALLINT[]) THEN
        ALTER TABLE compras.requerimiento_orden_compra
            ADD CONSTRAINT fk_compras_oc_presupuesto FOREIGN KEY (id_presupuesto)
            REFERENCES compras.requerimiento_presupuesto (id_requerimiento_presupuesto);
    END IF;
END;
$clave_origen$;

CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_id_orden INTEGER;
BEGIN
    SELECT r.estado INTO v_estado FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL FOR UPDATE OF r;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento activo.';
    END IF;
    IF v_estado = 5 THEN
        SELECT o.id_orden_compra INTO v_id_orden
          FROM compras.requerimiento_orden_compra o
          JOIN compras.requerimiento_presupuesto rp
            ON rp.id_requerimiento_presupuesto = o.id_presupuesto
           AND rp.id_requerimiento = o.id_requerimiento
         WHERE o.id_requerimiento = p_id_requerimiento;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'No existe una Orden de Compra emitida para el requerimiento.';
        END IF;
        RETURN 5;
    END IF;
    v_id_orden := compras.bloquear_creacion_orden_compra(p_id_requerimiento, p_usuario);
    IF v_id_orden <= 0 THEN
        RAISE EXCEPTION 'La Orden de Compra debe guardarse antes de confirmar.';
    END IF;
    -- La FK simple evita un indice redundante; el contrato atomico valida el origen.
    IF NOT EXISTS (
        SELECT 1 FROM compras.requerimiento_orden_compra o
        JOIN compras.requerimiento_presupuesto rp
          ON rp.id_requerimiento_presupuesto = o.id_presupuesto
         AND rp.id_requerimiento = o.id_requerimiento
        JOIN compras.requerimiento r ON r.id_requerimiento = o.id_requerimiento
        JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
        WHERE o.id_orden_compra = v_id_orden AND rp.baja_fecha IS NULL
          AND ((compras.normalizar_sector(sr.descripcion) IN ('RRHH', 'SISTEMAS')
                AND rp.tipo_documento = 3 AND rp.empresa_adjudicada)
            OR (compras.normalizar_sector(sr.descripcion) NOT IN ('RRHH', 'SISTEMAS')
                AND rp.tipo_documento = 1 AND rp.id_prestador = (
                    SELECT min(id_prestador) FROM compras.requerimiento_detalle
                     WHERE id_requerimiento = p_id_requerimiento AND baja_fecha IS NULL
                    HAVING count(DISTINCT id_prestador) = 1
                       AND count(*) = count(id_prestador))))
    ) THEN
        RAISE EXCEPTION 'La cotizacion de origen de la Orden de Compra no corresponde al requerimiento adjudicado.';
    END IF;
    IF (
        NOT EXISTS (SELECT 1 FROM compras.requerimiento_orden_compra_detalle
                     WHERE id_orden_compra = v_id_orden)
        OR EXISTS (
            SELECT 1 FROM compras.requerimiento_orden_compra_detalle od
            LEFT JOIN compras.requerimiento_detalle d
              ON d.id_detalle = od.id_requerimiento_detalle
             AND d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
            WHERE od.id_orden_compra = v_id_orden AND d.id_detalle IS NULL)
        OR EXISTS (
            SELECT 1 FROM compras.requerimiento_detalle d
             WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
               AND NOT EXISTS (
                   SELECT 1 FROM compras.requerimiento_orden_compra_detalle od
                    WHERE od.id_orden_compra = v_id_orden
                      AND od.id_requerimiento_detalle = d.id_detalle))) THEN
        RAISE EXCEPTION 'El detalle de la Orden de Compra no coincide con el requerimiento.';
    END IF;
    IF EXISTS (SELECT 1 FROM compras.requerimiento_orden_compra_detalle_consulta
                WHERE id_orden_compra = v_id_orden
                  AND NULLIF(btrim(descripcion_item), '') IS NULL) THEN
        RAISE EXCEPTION 'Los detalles de la Orden de Compra deben conservar su descripcion de origen.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = 5, modi_fecha = now(),
           modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = 7 AND baja_fecha IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'El requerimiento fue modificado por otro proceso.';
    END IF;
    RETURN 5;
END;
$func$
LANGUAGE plpgsql;

DO $verificar_normalizacion$
BEGIN
    IF (SELECT count(*) FROM information_schema.columns WHERE table_schema='compras'
         AND table_name='requerimiento_orden_compra') <> 16
       OR (SELECT count(*) FROM information_schema.columns WHERE table_schema='compras'
            AND table_name='requerimiento_orden_compra_detalle') <> 5 THEN
        RAISE EXCEPTION 'No se verifico el contrato normalizado de OC de 16 y 5 columnas.';
    END IF;
END;
$verificar_normalizacion$;
INSERT INTO pg_temp.compras_normalizacion_oc_resultado VALUES ('NORMALIZACION_OC_CONFIRMADA');
COMMIT;
SELECT COALESCE((SELECT resultado FROM pg_temp.compras_normalizacion_oc_resultado),
                'NORMALIZACION_OC_NO_CONFIRMADA') AS resultado,
       current_database() AS base, current_setting('server_version') AS version_postgresql;
