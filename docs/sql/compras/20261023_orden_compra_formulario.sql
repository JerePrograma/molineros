-- Orden de Compra emitida desde requerimientos AUTORIZADOS.
-- Ejecutar una vez sobre el esquema Compras vigente.
BEGIN;

-- Orden de Compra emitida: cabecera y detalle independientes del formulario.
CREATE TABLE compras.requerimiento_orden_compra (
    id_orden_compra SERIAL PRIMARY KEY,
    id_requerimiento INTEGER NOT NULL UNIQUE
        REFERENCES compras.requerimiento (id_requerimiento),
    fecha_emision DATE NOT NULL,
    fecha_orden_medica DATE,
    fecha_recepcion DATE,
    moneda VARCHAR(10),
    proveedor VARCHAR(250) NOT NULL,
    proveedor_cuit VARCHAR(20),
    proveedor_domicilio VARCHAR(250),
    proveedor_contacto VARCHAR(250),
    proveedor_email VARCHAR(250),
    proveedor_telefono VARCHAR(100),
    condicion VARCHAR(1000),
    forma_pago VARCHAR(250),
    plazo_pago VARCHAR(250),
    entrega VARCHAR(1000),
    observaciones VARCHAR(4000),
    confeccionado_por VARCHAR(250) NOT NULL,
    telefono_compras VARCHAR(100),
    solicitante VARCHAR(250),
    plan VARCHAR(120),
    id_reclamo_prestacional INTEGER NOT NULL DEFAULT 0,
    iva NUMERIC(18,2) CHECK (iva >= 0),
    iibb NUMERIC(18,2) CHECK (iibb >= 0),
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL,
    CHECK (btrim(proveedor) <> ''),
    CHECK (btrim(confeccionado_por) <> ''),
    CHECK (btrim(alta_usr) <> ''),
    CHECK (moneda IN ('ARS', 'USD'))
);

CREATE TABLE compras.requerimiento_orden_compra_detalle (
    id_orden_compra INTEGER NOT NULL
        REFERENCES compras.requerimiento_orden_compra (id_orden_compra),
    orden INTEGER NOT NULL CHECK (orden > 0),
    id_requerimiento_detalle INTEGER NOT NULL
        REFERENCES compras.requerimiento_detalle (id_detalle),
    codigo_item VARCHAR(100),
    descripcion_item TEXT NOT NULL CHECK (btrim(descripcion_item) <> ''),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(18,2) CHECK (precio_unitario >= 0),
    precio_total NUMERIC(18,2) CHECK (precio_total >= 0),
    PRIMARY KEY (id_orden_compra, orden),
    UNIQUE (id_orden_compra, id_requerimiento_detalle),
    CHECK (precio_total = round(cantidad * precio_unitario, 2)),
    CONSTRAINT ck_compras_oc_detalle_importes_conjuntos CHECK (
        (precio_unitario IS NULL AND precio_total IS NULL)
        OR (precio_unitario IS NOT NULL AND precio_total IS NOT NULL
            AND precio_total = round(cantidad * precio_unitario, 2))
    )
);

CREATE OR REPLACE FUNCTION compras.bloquear_creacion_orden_compra(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector_descripcion VARCHAR(120);
    v_id_orden INTEGER;
    v_id_reclamo INTEGER;
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra.';
    END IF;
    IF NULLIF(btrim(p_usuario), '') IS NULL OR length(p_usuario) > 100 THEN
        RAISE EXCEPTION 'No se pudo determinar el usuario de auditoria.';
    END IF;

    SELECT r.estado, sr.descripcion
      INTO v_estado, v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
       AND sr.activo = TRUE AND sr.baja_fecha IS NULL
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento activo.';
    END IF;

    SELECT id_orden_compra INTO v_id_orden
      FROM compras.requerimiento_orden_compra
     WHERE id_requerimiento = p_id_requerimiento;
    IF v_estado = 5 AND v_id_orden IS NOT NULL THEN
        RETURN v_id_orden;
    END IF;
    IF v_estado <> 7 THEN
        RAISE EXCEPTION 'La Orden de Compra solo puede crearse desde AUTORIZADO.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM compras.requerimiento_detalle
                    WHERE id_requerimiento = p_id_requerimiento
                      AND baja_fecha IS NULL) THEN
        RAISE EXCEPTION 'Debe existir al menos un detalle para crear la Orden de Compra.';
    END IF;

    IF compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS') THEN
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

    ELSE
        -- Mismo orden de bloqueo que la autorizacion y la baja del RP.
        PERFORM rp.id FROM autorizaciones.reclamos_prestacionales rp
        JOIN compras.requerimiento_reclamo_prestacional rr
          ON rr.id_reclamo_prestacional = rp.id
        WHERE rr.id_requerimiento = p_id_requerimiento
        FOR UPDATE OF rp;

        SELECT rr.id_reclamo_prestacional INTO v_id_reclamo
          FROM compras.requerimiento_reclamo_prestacional rr
          JOIN autorizaciones.reclamos_prestacionales rp
            ON rp.id = rr.id_reclamo_prestacional AND rp.baja_fecha IS NULL
         WHERE rr.id_requerimiento = p_id_requerimiento
           AND rr.estado = 'VINCULADO' AND rr.id_reclamo_prestacional > 0
         FOR UPDATE OF rr, rp;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.';
        END IF;
        IF (SELECT count(DISTINCT d.id_prestador)
              FROM compras.requerimiento_detalle d
             WHERE d.id_requerimiento = p_id_requerimiento
               AND d.baja_fecha IS NULL) <> 1
           OR EXISTS (
                SELECT 1 FROM compras.requerimiento_detalle d
                 WHERE d.id_requerimiento = p_id_requerimiento
                   AND d.baja_fecha IS NULL
                   AND (d.id_prestador IS NULL OR d.id_prestador <= 0
                        OR d.cantidad <= 0
                        OR d.precio_unitario_estimado IS NULL
                        OR d.precio_unitario_estimado < 0
                        OR d.precio_total_estimado IS NULL
                        OR d.precio_total_estimado < 0
                        OR d.precio_total_estimado <>
                            round(d.cantidad * d.precio_unitario_estimado, 2)
                        OR NOT EXISTS (
                            SELECT 1 FROM compras.requerimiento_presupuesto rp
                             WHERE rp.id_requerimiento = p_id_requerimiento
                               AND rp.id_prestador = d.id_prestador
                               AND rp.tipo_documento = 1 AND rp.baja_fecha IS NULL))) THEN
            RAISE EXCEPTION 'La Orden de Compra requiere un prestador adjudicado unico y detalles cotizados completos.';
        END IF;
    END IF;
    RETURN COALESCE(v_id_orden, 0);
END;
$func$
LANGUAGE plpgsql;

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
         * ORDEN_COMPRA y ANULADO son completamente
         * inmutables una vez alcanzados.
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
        RETURN 5;
    END IF;
    v_id_orden := compras.bloquear_creacion_orden_compra(p_id_requerimiento, p_usuario);
    IF v_id_orden > 0 AND (
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

COMMIT;
