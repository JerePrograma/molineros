-- Repara la autorizacion corporativa que quedo en la version medica 20261013.
-- Referencia legacy: ReintegroServiceImpl.cambiarEstadoCuota / cambio_estado_cuota.sql.
-- Cuerpo canonico de 20261015_autorizacion_orden_compra_empresas.sql.
-- PostgreSQL 9.6+. Ejecutar TODO en CONEXION NUEVA, sin transaccion previa.
-- Archivo ISO-8859-1/LATIN1 sin BOM. Detenerse ante el primer error SQL.
-- Conserva datos, triggers y confirmacion/snapshots de OC.
-- Adapta solo el bloque generico anterior conocido; conserva el resto de esa funcion.
-- Requiere validador corporativo vigente si existe: un validador pre20261015 aborta.
-- No reaplicar 20261015 completo sobre una instalacion con la OC de 20261023.
-- El reporte temporal queda vacio si la transaccion falla; no informa exito falso.
DROP TABLE IF EXISTS pg_temp.codex_autorizacion_reparacion_20261026;
CREATE TEMP TABLE codex_autorizacion_reparacion_20261026 (resultado TEXT NOT NULL)
    ON COMMIT PRESERVE ROWS;
BEGIN;
SET LOCAL lock_timeout = '10s';
DO $precondicion$
DECLARE
    v_oid OID;
    v_def TEXT;
    v_hash TEXT;
BEGIN
    v_oid := to_regprocedure('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)');
    IF v_oid IS NULL THEN
        RAISE EXCEPTION 'Falta la autorizacion de Compras. Revisar la instalacion 20261013/20261015 antes de reparar.';
    END IF;
    SELECT md5(btrim(replace(prosrc, E'\r', ''))) INTO v_hash FROM pg_proc WHERE oid = v_oid;
    -- Hashes del cuerpo: 20261013; 20261013 con bloqueo RP 20261014; canonico 20261015.
    IF (v_hash, (SELECT length(btrim(replace(prosrc, E'\r', ''))) FROM pg_proc WHERE oid = v_oid))
       NOT IN (('50104e5fea81484ace9d568fea15e393', 1936),
               ('722b565d453f689504c846202053f42c', 2242),
               ('039b08cc801687b02e584dc2a13218ca', 5346)) THEN
        RAISE EXCEPTION 'Cuerpo de autorizacion desconocido (%). Se conservo sin reemplazar.', v_hash;
    END IF;
    IF to_regprocedure('compras.normalizar_sector(character varying)') IS NULL
       OR to_regprocedure('compras.normalizar_usuario(character varying)') IS NULL
       OR to_regprocedure('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)') IS NULL THEN
        RAISE EXCEPTION 'Faltan funciones del contrato de Empresa. Revisar 20261006/20261015 antes de reparar.';
    END IF;
    v_oid := to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)');
    IF v_oid IS NULL THEN
        RAISE EXCEPTION 'Falta la confirmacion de OC. Revisar 20261023_orden_compra_formulario antes de reparar.';
    END IF;
    SELECT regexp_replace(lower(prosrc), '[[:space:]]+', '', 'g') INTO v_def FROM pg_proc WHERE oid = v_oid;
    IF position('compras.bloquear_creacion_orden_compra(' IN v_def) = 0
       OR position('estado=7' IN v_def) = 0 THEN
        RAISE EXCEPTION 'Contrato de OC anterior o desconocido. Revisar 20261023_orden_compra_formulario antes de reparar; se conservo su cuerpo.';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'compras'
                   AND table_name = 'requerimiento_presupuesto' AND column_name = 'empresa_adjudicada')
       OR NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'compras'
                      AND table_name = 'requerimiento_presupuesto' AND column_name = 'empresa_sucursal')
       OR NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'compras'
                      AND table_name = 'requerimiento_presupuesto' AND column_name = 'descripcion_empresa')
       OR NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'compras'
                      AND table_name = 'requerimiento_presupuesto' AND column_name = 'empresa_cuit') THEN
        RAISE EXCEPTION 'Faltan columnas del contrato de Empresa. Revisar 20261006/20261015 antes de reparar.';
    END IF;
    v_oid := to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)');
    IF v_oid IS NULL THEN
        RAISE EXCEPTION 'Falta la ruta generica de estados. Revisar 20261015 antes de reparar.';
    END IF;
    v_oid := to_regprocedure('compras.validar_requerimiento_fila()');
    IF v_oid IS NOT NULL THEN
        SELECT regexp_replace(lower(prosrc), '[[:space:]]+', '', 'g') INTO v_def FROM pg_proc WHERE oid = v_oid;
        IF position('old.estado=1andnew.estado=6' IN v_def) = 0
           OR position('old.estado=6andnew.estado=7' IN v_def) = 0
           OR position('old.estado=7andnew.estado=5' IN v_def) = 0
           OR position('compras.normalizar_sector(v_sector_descripcion)in(''rrhh'',''sistemas'')' IN v_def) = 0 THEN
            RAISE EXCEPTION 'Validador anterior o desconocido. Revisar la migracion 20261015 antes de reparar; no se modificaron funciones.';
        END IF;
    END IF;
END;
$precondicion$;

DO $proteger_ruta_generica$
DECLARE v_oid OID; v_def TEXT; v_viejo TEXT; v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)');
    IF v_oid IS NULL THEN RETURN; END IF;
    v_def := replace(pg_get_functiondef(v_oid), E'\r', '');
    IF position('Autorizacion previa a la OC' IN v_def) > 0 THEN RETURN; END IF;

    v_viejo := $viejo$    -- La ruta generica tampoco permite saltear las reglas de Empresas.
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
    END IF;$viejo$;
    v_nuevo := $nuevo$    -- Autorizacion previa a la OC: tampoco puede omitirse por la ruta generica.
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
    END IF;$nuevo$;
    v_viejo := replace(v_viejo, E'\r', '');
    v_nuevo := replace(v_nuevo, E'\r', '');
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de proteger_ruta_generica. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    EXECUTE v_def;
END;
$proteger_ruta_generica$;
DO $verificar_ruta_generica$
DECLARE v_oid OID; v_def TEXT;
BEGIN
    v_oid := to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)');
    SELECT regexp_replace(lower(prosrc), '[[:space:]]+', '', 'g') INTO v_def FROM pg_proc WHERE oid = v_oid;
    IF position('ifp_estado_nuevoin(6,7)thenperformcompras.cambiar_estado_autorizacion_requerimiento(' IN v_def) = 0
       OR position('ifp_estado_nuevo=5thenperformcompras.confirmar_orden_compra_requerimiento(' IN v_def) = 0
       OR position('ifv_estado_actualin(6,7)then' IN v_def) = 0 THEN
        RAISE EXCEPTION 'Ruta generica anterior o desconocida. Revisar la migracion 20261015 antes de reparar; no se modificaron funciones.';
    END IF;
END;
$verificar_ruta_generica$;

CREATE OR REPLACE FUNCTION compras.cambiar_estado_autorizacion_requerimiento(
    p_id_requerimiento INTEGER,
    p_estado_nuevo INTEGER,
    p_usuario VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_estado_actual INTEGER;
    v_id_reclamo INTEGER;
    v_sector_descripcion VARCHAR(120);
    v_es_empresa BOOLEAN;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra.';
    END IF;

    IF p_estado_nuevo IS NULL OR p_estado_nuevo NOT IN (6, 7) THEN
        RAISE EXCEPTION 'El estado de autorizacion seleccionado no es valido.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT r.estado, sr.descripcion
      INTO v_estado_actual, v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
       AND sr.activo = TRUE AND sr.baja_fecha IS NULL
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento activo.';
    END IF;

    v_es_empresa := compras.normalizar_sector(v_sector_descripcion)
        IN ('RRHH', 'SISTEMAS');

    IF v_es_empresa THEN
        IF NOT (
                (v_estado_actual = 1 AND p_estado_nuevo = 6)
             OR (v_estado_actual = 6 AND p_estado_nuevo = 7)
        ) THEN
            RAISE EXCEPTION
                'RRHH o SISTEMAS deben pasar de PENDIENTE a PARA AUTORIZAR y luego a AUTORIZADO.';
        END IF;

        IF NOT EXISTS (
            SELECT 1
              FROM compras.requerimiento_detalle d
             WHERE d.id_requerimiento = p_id_requerimiento
               AND d.baja_fecha IS NULL
        ) THEN

            RAISE EXCEPTION
                'Debe existir al menos un detalle antes de enviar o autorizar el requerimiento.';
        END IF;

        IF NOT EXISTS (
            SELECT 1
              FROM compras.requerimiento_presupuesto rp
             WHERE rp.id_requerimiento = p_id_requerimiento
               AND rp.tipo_documento = 3
               AND rp.baja_fecha IS NULL
        ) THEN

            RAISE EXCEPTION
                'Debe existir al menos una cotizacion de Empresa activa antes de enviar o autorizar el requerimiento.';
        END IF;

        IF (SELECT count(*)
              FROM compras.requerimiento_presupuesto rp
             WHERE rp.id_requerimiento = p_id_requerimiento
               AND rp.tipo_documento = 3
               AND rp.baja_fecha IS NULL
               AND rp.empresa_adjudicada) <> 1 THEN

            RAISE EXCEPTION
                'Debe seleccionar exactamente una Empresa adjudicada con cotizacion activa.';
        END IF;

        IF EXISTS (
            SELECT 1
              FROM compras.requerimiento_presupuesto rp
             WHERE rp.id_requerimiento = p_id_requerimiento
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
    ELSE
        IF NOT (
                (v_estado_actual = 4 AND p_estado_nuevo = 6)
             OR (v_estado_actual = 6 AND p_estado_nuevo = 7)
        ) THEN
            RAISE EXCEPTION
                'El requerimiento no puede pasar al estado seleccionado desde su estado actual.';
        END IF;

        -- Orden compartido con la baja RP: primero RP, luego vinculo.
        PERFORM rp.id FROM autorizaciones.reclamos_prestacionales rp
        JOIN compras.requerimiento_reclamo_prestacional rr
          ON rr.id_reclamo_prestacional = rp.id
        WHERE rr.id_requerimiento = p_id_requerimiento
        FOR UPDATE OF rp;

        SELECT rr.id_reclamo_prestacional
          INTO v_id_reclamo
          FROM compras.requerimiento_reclamo_prestacional rr
          JOIN autorizaciones.reclamos_prestacionales rp
            ON rp.id = rr.id_reclamo_prestacional
           AND rp.baja_fecha IS NULL
         WHERE rr.id_requerimiento = p_id_requerimiento
           AND rr.estado = 'VINCULADO'
           AND rr.id_reclamo_prestacional > 0
         FOR UPDATE OF rr, rp;

        IF NOT FOUND THEN
            RAISE EXCEPTION
                'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.';
        END IF;
    END IF;

    UPDATE compras.requerimiento
       SET estado = p_estado_nuevo,
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$func$
LANGUAGE plpgsql;

DO $verificacion$
BEGIN
    IF (SELECT md5(btrim(replace(prosrc, E'\r', ''))) FROM pg_proc
         WHERE oid = to_regprocedure('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)'))
       IS DISTINCT FROM '039b08cc801687b02e584dc2a13218ca' THEN
        RAISE EXCEPTION 'No se verifico la funcion de autorizacion reparada.';
    END IF;
END;
$verificacion$;
INSERT INTO pg_temp.codex_autorizacion_reparacion_20261026 VALUES ('REPARACION_CONFIRMADA');
COMMIT;
SELECT COALESCE((SELECT resultado FROM pg_temp.codex_autorizacion_reparacion_20261026),
                'REPARACION_NO_CONFIRMADA') AS resultado, current_database() AS base,
       current_setting('server_version') AS version_postgresql,
       'compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)' AS funcion;
