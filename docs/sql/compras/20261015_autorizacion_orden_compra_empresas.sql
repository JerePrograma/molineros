-- Autorizacion antes de la Orden de Compra de RRHH/SISTEMAS.
-- Referencias: cambio_estado_cuota.sql / ReintegroServiceImpl.cambiarEstadoCuota;
-- contratos de Compras de 20261006, 20261013 y orden de bloqueo RP de 20261014.
-- PostgreSQL 9.6. Aplicar despues de 20261013_autorizacion_requerimiento.sql.
-- No crea triggers ni modifica datos. Java/JSP requieren esta migracion.
-- ISO-8859-1 sin BOM. No aplicada por Codex a QA.

BEGIN;

DO $precondicion$
DECLARE v_faltantes TEXT; v_def TEXT;
BEGIN
    SELECT string_agg(requisito, E'\n' ORDER BY requisito)
      INTO v_faltantes
      FROM (VALUES
          ('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying) - instalar 20261013',
           to_regprocedure('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)') IS NOT NULL),
          ('compras.cambiar_estado_requerimiento(integer,integer,character varying)',
           to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)') IS NOT NULL),
          ('compras.confirmar_orden_compra_requerimiento(integer,character varying)',
           to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)') IS NOT NULL),
          ('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)',
           to_regprocedure('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)') IS NOT NULL),
          ('compras.normalizar_sector(character varying)',
           to_regprocedure('compras.normalizar_sector(character varying)') IS NOT NULL),
          ('compras.normalizar_usuario(character varying)',
           to_regprocedure('compras.normalizar_usuario(character varying)') IS NOT NULL),
          ('indice compras.ux_compras_empresa_adjudicada_activa - instalar contrato 20260925/20261006',
           to_regclass('compras.ux_compras_empresa_adjudicada_activa') IS NOT NULL),
          ('columna compras.requerimiento_presupuesto.empresa_adjudicada',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'empresa_adjudicada' AND NOT attisdropped)),
          ('columna compras.requerimiento_presupuesto.descripcion_empresa',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'descripcion_empresa' AND NOT attisdropped))
      ) AS requisitos(requisito, existe)
     WHERE NOT existe;
    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Faltan prerequisitos de autorizacion de Compras:%', E'\n' || v_faltantes;
    END IF;

    v_def := pg_get_functiondef(to_regprocedure(
        'compras.confirmar_orden_compra_requerimiento(integer,character varying)'));
    IF position('rp.empresa_adjudicada' IN v_def) = 0
       OR position('rp.descripcion_empresa' IN v_def) = 0
       OR position('compras.buscar_empresas_cotizacion(' IN v_def) = 0 THEN
        RAISE EXCEPTION 'Contrato de Empresa desconocido: instalar/revisar 20261006 antes de migrar.';
    END IF;
END;
$precondicion$;

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

CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector_descripcion VARCHAR(120);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    SELECT
        r.estado,
        sr.descripcion
    INTO
        v_estado,
        v_sector_descripcion
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

    IF v_estado = 5 THEN
        RETURN 5;
    END IF;

    IF v_estado <> 7 THEN
        RAISE EXCEPTION
            'El requerimiento solo puede pasar a ORDEN_COMPRA desde AUTORIZADO.';
    END IF;

    IF compras.normalizar_sector(
            v_sector_descripcion
       ) NOT IN ('RRHH', 'SISTEMAS') THEN

        RAISE EXCEPTION
            'Solo los requerimientos de RRHH o SISTEMAS pueden crear una ORDEN_COMPRA.';
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

    UPDATE compras.requerimiento
       SET estado = 5,
           modi_fecha = now(),
           modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = 7
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
    END IF;

    RETURN 5;
END;
$func$
LANGUAGE plpgsql;

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
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de proteger_ruta_generica. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    EXECUTE v_def;
END;
$proteger_ruta_generica$;

-- Adapta unicamente fragmentos conocidos y preserva la configuracion de triggers.
DO $adaptar_validacion$
DECLARE v_oid OID; v_def TEXT; v_viejo TEXT; v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.validar_requerimiento_fila()');
    IF v_oid IS NULL THEN RETURN; END IF;
    v_def := replace(pg_get_functiondef(v_oid), E'\r', '');
    IF position('Autorizacion previa a la OC' IN v_def) > 0 THEN RETURN; END IF;

    v_viejo := $viejo$        IF OLD.estado IN (5, 7, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        IF OLD.estado IN (4, 6)
           AND NEW IS DISTINCT FROM OLD THEN

            v_fila_transicion := NEW;
            v_fila_transicion.estado := OLD.estado;
            v_fila_transicion.modi_fecha := OLD.modi_fecha;
            v_fila_transicion.modi_usr := OLD.modi_usr;

            IF NOT (
                    (OLD.estado = 4 AND NEW.estado = 6)
                 OR (OLD.estado = 6 AND NEW.estado = 7)
               )
               OR v_fila_transicion IS DISTINCT FROM OLD THEN

                RAISE EXCEPTION
                    'El requerimiento no puede modificarse en el estado actual.';
            END IF;
        END IF;$viejo$;
    v_nuevo := $nuevo$        IF OLD.estado IN (5, 99)
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
                 OR (OLD.estado = 7 AND NEW.estado = 5
                     AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS'))
               )
               OR v_fila_transicion IS DISTINCT FROM OLD THEN

                RAISE EXCEPTION
                    'El requerimiento no puede modificarse en el estado actual.';
            END IF;
        END IF;$nuevo$;
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de adaptar_validacion. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    v_viejo := $viejo$                    (OLD.estado = 1 AND NEW.estado IN (2, 5, 99))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
                 OR (OLD.estado = 3 AND NEW.estado = 4)
                 OR (OLD.estado = 4 AND NEW.estado = 6)
                 OR (OLD.estado = 6 AND NEW.estado = 7)$viejo$;
    v_nuevo := $nuevo$                    (OLD.estado = 1 AND NEW.estado IN (2, 99))
                 OR (OLD.estado = 1 AND NEW.estado = 6
                     AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS'))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
                 OR (OLD.estado = 3 AND NEW.estado = 4)
                 OR (OLD.estado = 4 AND NEW.estado = 6
                     AND compras.normalizar_sector(v_sector_descripcion) NOT IN ('RRHH', 'SISTEMAS'))
                 OR (OLD.estado = 6 AND NEW.estado = 7)
                 OR (OLD.estado = 7 AND NEW.estado = 5
                     AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS'))$nuevo$;
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de adaptar_validacion. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    v_viejo := $viejo$            IF OLD.estado = 1
               AND NEW.estado = 5 THEN

                IF compras.normalizar_sector(
                        v_sector_descripcion
                   ) NOT IN ('RRHH', 'SISTEMAS') THEN

                    RAISE EXCEPTION
                        'Solo los requerimientos de RRHH o SISTEMAS pueden pasar directamente a ORDEN_COMPRA.';
                END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento =
                           NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
                END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento =
                           NEW.id_requerimiento
                       AND rp.tipo_documento = 3
                       AND rp.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
                END IF;

            END IF;$viejo$;
    v_nuevo := $nuevo$            IF ((OLD.estado = 1 AND NEW.estado = 6)
                OR (OLD.estado = 6 AND NEW.estado = 7))
               AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS')
               OR (OLD.estado = 7 AND NEW.estado = 5) THEN

                IF compras.normalizar_sector(v_sector_descripcion) NOT IN ('RRHH', 'SISTEMAS') THEN
                    RAISE EXCEPTION 'Solo RRHH o SISTEMAS pueden crear una ORDEN_COMPRA.';
                END IF;

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
            END IF;$nuevo$;
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de adaptar_validacion. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    EXECUTE v_def;
END;
$adaptar_validacion$;

COMMIT;
