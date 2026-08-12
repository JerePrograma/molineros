/*
 * Orden médica en requerimientos de compra.
 *
 * ORDEN DE DESPLIEGUE
 * 1. En una base existente, aplicar este script antes de desplegar Java/JSP.
 * 2. compras_schema.sql queda reservado para una instalación nueva.
 *
 * Ejecución sugerida:
 * psql -X -v ON_ERROR_STOP=1 -f 20260812_orden_medica_requerimiento.sql
 */

BEGIN;

LOCK TABLE compras.requerimiento_presupuesto
    IN SHARE ROW EXCLUSIVE MODE;

ALTER TABLE compras.requerimiento_presupuesto
    ADD COLUMN IF NOT EXISTS tipo_documento SMALLINT;

UPDATE compras.requerimiento_presupuesto
   SET tipo_documento = 1
 WHERE tipo_documento IS NULL;

ALTER TABLE compras.requerimiento_presupuesto
    ALTER COLUMN tipo_documento SET DEFAULT 1,
    ALTER COLUMN tipo_documento SET NOT NULL,
    ADD COLUMN IF NOT EXISTS fecha_documento DATE,
    ALTER COLUMN id_prestador DROP NOT NULL;

DO $preflight$
BEGIN
    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.tipo_documento NOT IN (1, 2)
            OR (
                rp.tipo_documento = 1
                AND (
                    rp.id_prestador IS NULL
                    OR rp.id_prestador <= 0
                )
            )
            OR (
                rp.tipo_documento = 2
                AND (
                    rp.id_prestador IS NOT NULL
                    OR rp.fecha_documento IS NULL
                    OR rp.titulo <> 'Orden médica'
                    OR rp.descripcion_prestador IS NOT NULL
                )
            )
    ) THEN
        RAISE EXCEPTION
            'Existen documentos de requerimiento incompatibles con los tipos PRESUPUESTO/ORDEN_MEDICA.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.baja_fecha IS NULL
           AND rp.tipo_documento = 1
         GROUP BY rp.id_requerimiento, rp.id_prestador
        HAVING count(*) > 1
    ) THEN
        RAISE EXCEPTION
            'Existen presupuestos activos duplicados por requerimiento y prestador.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.baja_fecha IS NULL
           AND rp.tipo_documento = 2
         GROUP BY rp.id_requerimiento
        HAVING count(*) > 1
    ) THEN
        RAISE EXCEPTION
            'Existen Órdenes médicas activas duplicadas por requerimiento.';
    END IF;
END;
$preflight$ LANGUAGE plpgsql;

ALTER TABLE compras.requerimiento_presupuesto
    DROP CONSTRAINT IF EXISTS ck_compras_presupuesto_prestador,
    DROP CONSTRAINT IF EXISTS ck_compras_presupuesto_tipo_documento,
    DROP CONSTRAINT IF EXISTS ck_compras_presupuesto_fecha_documento,
    DROP CONSTRAINT IF EXISTS ck_compras_orden_medica_datos;

ALTER TABLE compras.requerimiento_presupuesto
    ADD CONSTRAINT ck_compras_presupuesto_tipo_documento
        CHECK (tipo_documento IN (1, 2)) NOT VALID,
    ADD CONSTRAINT ck_compras_presupuesto_prestador
        CHECK (
            (
                tipo_documento = 1
                AND id_prestador IS NOT NULL
                AND id_prestador > 0
            )
            OR (
                tipo_documento = 2
                AND id_prestador IS NULL
            )
        ) NOT VALID,
    ADD CONSTRAINT ck_compras_presupuesto_fecha_documento
        CHECK (
            tipo_documento <> 2
            OR fecha_documento IS NOT NULL
        ) NOT VALID,
    ADD CONSTRAINT ck_compras_orden_medica_datos
        CHECK (
            tipo_documento <> 2
            OR (
                titulo = 'Orden médica'
                AND descripcion_prestador IS NULL
            )
        ) NOT VALID;

ALTER TABLE compras.requerimiento_presupuesto
    VALIDATE CONSTRAINT ck_compras_presupuesto_tipo_documento,
    VALIDATE CONSTRAINT ck_compras_presupuesto_prestador,
    VALIDATE CONSTRAINT ck_compras_presupuesto_fecha_documento,
    VALIDATE CONSTRAINT ck_compras_orden_medica_datos;

DROP INDEX IF EXISTS
    compras.ux_compras_presupuesto_requerimiento_prestador_activo;

CREATE UNIQUE INDEX ux_compras_presupuesto_requerimiento_prestador_activo
    ON compras.requerimiento_presupuesto (
        id_requerimiento,
        id_prestador
    )
    WHERE baja_fecha IS NULL
      AND tipo_documento = 1;

DROP INDEX IF EXISTS
    compras.ux_compras_orden_medica_requerimiento_activa;

CREATE UNIQUE INDEX ux_compras_orden_medica_requerimiento_activa
    ON compras.requerimiento_presupuesto (
        id_requerimiento
    )
    WHERE baja_fecha IS NULL
      AND tipo_documento = 2;

/* Las funciones dependientes se reemplazan completas a continuación. */

CREATE OR REPLACE FUNCTION compras.validar_requerimiento_fila()
    RETURNS TRIGGER
AS $func$
DECLARE
v_requiere_afiliado BOOLEAN;
    v_cambio_estructura BOOLEAN;
    v_usuario VARCHAR(100);
BEGIN
SELECT s.requiere_afiliado
INTO v_requiere_afiliado
FROM compras.sector_requerimiento s
WHERE s.id_sector = NEW.id_sector
  AND s.activo = TRUE
  AND s.baja_fecha IS NULL;

IF v_requiere_afiliado IS NULL THEN
        RAISE EXCEPTION
            'El sector informado no existe o no esta activo.';
END IF;

    IF TG_OP = 'INSERT' THEN

        IF NEW.estado <> 1 THEN
            RAISE EXCEPTION
                'Un requerimiento nuevo debe crearse en estado PENDIENTE.';
END IF;

ELSE

        /*
         * RECLAMO_RP, ORDEN_COMPRA y ANULADO son completamente
         * inmutables una vez alcanzados.
         */
        IF OLD.estado IN (4, 5, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        /*
         * COTIZADO tambien permanece bloqueado, salvo por la transicion
         * funcional COTIZADO -> RECLAMO_RP.
         *
         * Se permite que cambiar_estado_requerimiento actualice
         * modi_fecha y modi_usr durante esa transicion.
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
            OR NEW.surge
                IS DISTINCT FROM OLD.surge
            OR NEW.observaciones
                IS DISTINCT FROM OLD.observaciones;

        IF v_cambio_estructura
           AND OLD.estado <> 1 THEN

            RAISE EXCEPTION
                'La estructura solo puede modificarse en estado PENDIENTE.';
END IF;

        IF NEW.estado IS DISTINCT FROM OLD.estado THEN

            /*
             * Transiciones funcionales actualmente soportadas:
             *
             * 1 PENDIENTE -> 2 A_COTIZAR
             * 1 PENDIENTE -> 99 ANULADO
             * 2 A_COTIZAR -> 3 COTIZADO
             * 2 A_COTIZAR -> 99 ANULADO
             * 3 COTIZADO -> 4 RECLAMO_RP
             *
             * ORDEN_COMPRA (5) continua sin transicion activa.
             */
            IF NOT (
                    (OLD.estado = 1 AND NEW.estado IN (2, 99))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
                 OR (OLD.estado = 3 AND NEW.estado = 4)
            ) THEN

                RAISE EXCEPTION
                    'Transicion de estado invalida: % -> %.',
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
                        'No se puede cerrar una cotizacion sin detalles.';
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
                        'No se puede cerrar la cotizacion: existen detalles incompletos o invalidos.';
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

        NEW.cargo_ospim := 100;
        NEW.cargo_tercerizadora := 0;
        NEW.id_tercerizadora := NULL;
        NEW.recupero := FALSE;

END IF;

RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.guardar_cotizacion_requerimiento(
    p_id_requerimiento INTEGER,
    p_ids_detalle INTEGER[],
    p_precios_unitarios NUMERIC[],
    p_id_prestador INTEGER,
    p_usuario VARCHAR
)
    RETURNS INTEGER
AS $func$
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
    -- modificar la cotizacion.
    IF v_estado = 3 THEN
        RETURN 3;
END IF;

    IF v_estado <> 2 THEN
        RAISE EXCEPTION
            'La cotizacion solo puede guardarse en estado A COTIZAR.';
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
            'La cotizacion contiene identificadores de detalle invalidos.';
END IF;

    IF (
SELECT count(*)
FROM unnest(p_ids_detalle) AS ids(id_detalle)
    ) <> (
        SELECT count(DISTINCT ids.id_detalle)
          FROM unnest(p_ids_detalle) AS ids(id_detalle)
    ) THEN
        RAISE EXCEPTION
            'La cotizacion contiene detalles duplicados.';
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
            'La cotizacion debe informar exactamente todos los detalles activos.';
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

    -- Bloquea la estructura completa antes de actualizar valores de cotizacion.
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
        RETURN 2;
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
            'Debe existir un presupuesto activo del prestador adjudicado antes de cerrar la cotizacion.';
END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_cotizacion_prestador rcp
         WHERE rcp.id_requerimiento = p_id_requerimiento
           AND rcp.id_prestador = p_id_prestador
           AND rcp.estado_envio = 'COTIZADO'
    ) THEN
        RAISE EXCEPTION
            'El prestador adjudicado debe encontrarse COTIZADO antes de cerrar la cotizacion.';
END IF;

    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        3,
        v_usuario
    );

RETURN 3;
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,
    p_dl_group_id BIGINT,
    p_dl_folder_id BIGINT,
    p_dl_file_entry_id BIGINT,
    p_dl_file_uuid VARCHAR,
    p_nombre_original VARCHAR,
    p_nombre_persistido VARCHAR,
    p_titulo VARCHAR,
    p_descripcion_prestador VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_estado_envio VARCHAR(20);
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento informado no es valido.';
    END IF;

    IF p_id_prestador IS NULL OR p_id_prestador <= 0 THEN
        RAISE EXCEPTION
            'El prestador informado no es valido.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN
        RAISE EXCEPTION
            'La identidad del documento de presupuesto no es valida.';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT r.estado
      INTO v_estado_requerimiento
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND OR v_estado_requerimiento <> 2 THEN
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
        descripcion_prestador,
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
        NULLIF(btrim(p_descripcion_prestador), ''),
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
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_orden_medica(
    p_id_requerimiento INTEGER,
    p_dl_group_id BIGINT,
    p_dl_folder_id BIGINT,
    p_dl_file_entry_id BIGINT,
    p_dl_file_uuid VARCHAR,
    p_nombre_original VARCHAR,
    p_nombre_persistido VARCHAR,
    p_titulo VARCHAR,
    p_fecha_documento DATE,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'El requerimiento informado no es valido.';
END IF;

    IF p_fecha_documento IS NULL THEN
        RAISE EXCEPTION
            'Debe informar la fecha de la Orden médica.';
END IF;

    IF p_dl_group_id IS NULL
       OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL
       OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL
       OR p_dl_file_entry_id <= 0
       OR NULLIF(btrim(p_dl_file_uuid), '') IS NULL THEN

        RAISE EXCEPTION
            'La identidad del documento de Orden médica no es válida.';
END IF;

    IF NULLIF(btrim(p_nombre_original), '') IS NULL
       OR NULLIF(btrim(p_nombre_persistido), '') IS NULL THEN

        RAISE EXCEPTION
            'Los nombres del documento de Orden médica no son válidos.';
END IF;

    IF btrim(COALESCE(p_titulo, '')) <> 'Orden médica' THEN
        RAISE EXCEPTION
            'El título del documento debe ser Orden médica.';
END IF;

    v_usuario :=
        COALESCE(
            NULLIF(btrim(p_usuario), ''),
            'sistema'
        );

    /*
     * Se conserva el bloqueo del requerimiento.
     *
     * Además de validar que continúe activo/PENDIENTE,
     * serializa las registraciones documentales concurrentes
     * para el mismo requerimiento.
     */
SELECT r.estado
INTO v_estado_requerimiento
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo informado.';
END IF;

    IF v_estado_requerimiento <> 1 THEN
        RAISE EXCEPTION
            'La Orden médica solo puede registrarse durante '
            'el alta de un requerimiento PENDIENTE.';
END IF;

    /*
     * IMPORTANTE:
     *
     * No se valida la existencia previa de otra Orden médica.
     * Un mismo requerimiento puede tener varias Órdenes médicas
     * activas.
     *
     * Cada archivo conserva su propia fila y su propia
     * fecha_documento.
     */
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
    descripcion_prestador,
    alta_usr
)
VALUES (
           p_id_requerimiento,
           2,
           p_fecha_documento,
           NULL,
           p_dl_group_id,
           p_dl_folder_id,
           p_dl_file_entry_id,
           btrim(p_dl_file_uuid),
           btrim(p_nombre_original),
           btrim(p_nombre_persistido),
           'Orden médica',
           NULL,
           v_usuario
       )
    RETURNING id_requerimiento_presupuesto
INTO v_id;

RETURN v_id;
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_orden_medica(
    p_id_requerimiento INTEGER
)
RETURNS SETOF compras.requerimiento_presupuesto
AS $func$
BEGIN
RETURN QUERY
SELECT rp.*
FROM compras.requerimiento_presupuesto rp
WHERE rp.id_requerimiento = p_id_requerimiento
  AND rp.tipo_documento = 2
  AND rp.baja_fecha IS NULL;
END;
$func$
LANGUAGE plpgsql
STABLE;

DROP FUNCTION IF EXISTS compras.listar_requerimiento_presupuestos(INTEGER);

CREATE OR REPLACE FUNCTION compras.listar_requerimiento_presupuestos(
    p_id_requerimiento INTEGER
)
RETURNS TABLE (
    id_requerimiento_presupuesto INTEGER,
    id_requerimiento INTEGER,
    id_prestador INTEGER,
    tipo_documento SMALLINT,
    fecha_documento DATE,
    dl_group_id BIGINT,
    dl_folder_id BIGINT,
    dl_file_entry_id BIGINT,
    dl_file_uuid VARCHAR,
    nombre_original VARCHAR,
    nombre_persistido VARCHAR,
    titulo VARCHAR,
    descripcion_prestador VARCHAR,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE,
    alta_usr VARCHAR
)
AS $func$
BEGIN
RETURN QUERY
SELECT
    rp.id_requerimiento_presupuesto,
    rp.id_requerimiento,
    rp.id_prestador,
    rp.tipo_documento,
    rp.fecha_documento,
    rp.dl_group_id,
    rp.dl_folder_id,
    rp.dl_file_entry_id,
    rp.dl_file_uuid,
    rp.nombre_original,
    rp.nombre_persistido,
    rp.titulo,
    rp.descripcion_prestador,
    rp.alta_fecha,
    rp.alta_usr
FROM compras.requerimiento_presupuesto rp
WHERE rp.id_requerimiento = p_id_requerimiento
  AND rp.tipo_documento = 1
  AND rp.baja_fecha IS NULL
ORDER BY
    rp.alta_fecha DESC,
    rp.id_requerimiento_presupuesto DESC;
END;
$func$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_presupuesto(
    p_id_requerimiento_presupuesto INTEGER,
    p_id_requerimiento INTEGER
)
RETURNS SETOF compras.requerimiento_presupuesto
AS $func$
BEGIN
RETURN QUERY
SELECT rp.*
FROM compras.requerimiento_presupuesto rp
WHERE rp.id_requerimiento_presupuesto =
      p_id_requerimiento_presupuesto
  AND rp.id_requerimiento = p_id_requerimiento
  AND rp.tipo_documento = 1
  AND rp.baja_fecha IS NULL;
END;
$func$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION compras.baja_requerimiento_presupuesto(
    p_id_requerimiento_presupuesto INTEGER,
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
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

    IF NOT FOUND OR v_estado_requerimiento <> 2 THEN
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
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.reactivar_requerimiento_presupuesto(
    p_id_requerimiento_presupuesto INTEGER,
    p_id_requerimiento INTEGER
)
RETURNS BOOLEAN
AS $func$
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

    IF NOT FOUND OR v_estado_requerimiento <> 2 THEN
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
$func$
LANGUAGE plpgsql;


COMMIT;
