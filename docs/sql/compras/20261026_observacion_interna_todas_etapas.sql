-- Compras: observacion interna en todas las etapas y consulta de anulados.
-- Aplicar luego de 20261023_orden_compra_formulario.sql. Reejecutable.
-- El rol AUTORIZAR_COMPRAS se valida en la Action; PostgreSQL controla estado y fila.
BEGIN;

DO $precondicion$
BEGIN
    IF to_regprocedure('compras.validar_requerimiento_fila()') IS NULL
       OR to_regprocedure('compras.buscar_requerimientos(integer,integer,character varying,integer,character varying,boolean,boolean,character varying,date,date,integer)') IS NULL
       OR to_regprocedure('compras.guardar_observacion_interna_requerimiento(integer,text,character varying)') IS NULL
       OR NOT EXISTS (
           SELECT 1 FROM pg_trigger
            WHERE tgrelid = to_regclass('compras.requerimiento')
              AND tgname = 'trg_compras_requerimiento_validar'
              AND NOT tgisinternal
       ) THEN
        RAISE EXCEPTION 'Faltan funciones o trigger de Compras; instale primero las migraciones anteriores.';
    END IF;
END;
$precondicion$;

CREATE OR REPLACE FUNCTION compras.validar_requerimiento_fila()
    RETURNS TRIGGER
AS $func$
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
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.buscar_requerimientos(
    p_estado integer,
    p_sector integer,
    p_afiliado_cuil_titular character varying,
    p_afiliado_int integer,
    p_id_tercerizadora character varying,
    p_recupero boolean,
    p_surge boolean,
    p_texto character varying,
    p_fecha_alta_desde date,
    p_fecha_alta_hasta date,
    p_id_requerimiento_compra integer
)
    RETURNS SETOF compras.requerimiento_base_row
    LANGUAGE 'plpgsql'
    COST 100
    STABLE
    ROWS 1000
AS $BODY$
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
    WHERE (rb.baja_fecha IS NULL OR rb.id_estado = 99)
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
                      COALESCE(d.codigo_nomenclador, '')
                  ) LIKE '%' || v_texto || '%'

                  OR upper(
                      COALESCE(d.descripcion_nomenclador, '')
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
$BODY$;

CREATE OR REPLACE FUNCTION compras.guardar_observacion_interna_requerimiento(
    p_id_requerimiento INTEGER,
    p_observacion_interna TEXT,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra que desea editar.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);
    UPDATE compras.requerimiento
       SET observacion_interna = NULLIF(btrim(p_observacion_interna), ''),
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND ((estado IN (1, 2, 3, 4, 5, 6, 7) AND baja_fecha IS NULL)
            OR estado = 99)
     RETURNING id_requerimiento INTO v_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'La observacion interna no puede modificarse en el estado actual. Actualice la pantalla.';
    END IF;
    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;

COMMIT;
