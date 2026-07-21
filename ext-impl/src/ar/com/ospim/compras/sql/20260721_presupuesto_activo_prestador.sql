-- =====================================================================
-- MODULO: Compras
-- ACTUALIZACION: presupuesto activo unico por requerimiento/prestador
-- COMPATIBILIDAD: PostgreSQL 9.6
--
-- ORDEN DE DESPLIEGUE
-- Base existente:
-- El SQL debe aplicarse antes de desplegar Java/JSP.
--   1. Realizar backup y poner el modulo Compras en mantenimiento.
--   2. Ejecutar:
--      psql -X -v ON_ERROR_STOP=1 -f 20260721_presupuesto_activo_prestador.sql
--      Alternativa: definir PGCLIENTENCODING=LATIN1 antes de ejecutar psql.
--   3. Desplegar Java/JSP solamente despues de confirmar el COMMIT.
--
-- Instalacion nueva:
--   Ejecutar compras_schema.sql. No ejecutar este script.
--
-- Este script no elimina ni corrige presupuestos activos duplicados.
-- Ante datos incompatibles aborta con un diagnostico para resolucion manual.
-- =====================================================================

\set ON_ERROR_STOP on
\encoding LATIN1

BEGIN;

DO $preflight_estructura$
DECLARE
    v_faltantes TEXT;
BEGIN
    SELECT string_agg(v.objeto, ', ' ORDER BY v.objeto)
      INTO v_faltantes
      FROM (
          VALUES
              ('compras.requerimiento', to_regclass('compras.requerimiento') IS NULL),
              (
                  'compras.requerimiento_cotizacion_prestador',
                  to_regclass(
                      'compras.requerimiento_cotizacion_prestador'
                  ) IS NULL
              ),
              (
                  'compras.requerimiento_presupuesto',
                  to_regclass('compras.requerimiento_presupuesto') IS NULL
              )
      ) AS v(objeto, falta)
     WHERE v.falta;

    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION
            'No se puede actualizar Compras. Faltan objetos: %.',
            v_faltantes;
    END IF;

    SELECT string_agg(
               v.tabla || '.' || v.columna,
               ', '
               ORDER BY v.tabla, v.columna
           )
      INTO v_faltantes
      FROM (
          VALUES
              ('requerimiento', 'id_requerimiento'),
              ('requerimiento', 'estado'),
              ('requerimiento', 'baja_fecha'),
              (
                  'requerimiento_cotizacion_prestador',
                  'id_requerimiento'
              ),
              (
                  'requerimiento_cotizacion_prestador',
                  'id_prestador'
              ),
              (
                  'requerimiento_cotizacion_prestador',
                  'estado_envio'
              ),
              (
                  'requerimiento_cotizacion_prestador',
                  'fecha_envio'
              ),
              (
                  'requerimiento_cotizacion_prestador',
                  'modi_fecha'
              ),
              (
                  'requerimiento_cotizacion_prestador',
                  'modi_usr'
              ),
              ('requerimiento_presupuesto', 'id_requerimiento'),
              ('requerimiento_presupuesto', 'id_prestador'),
              (
                  'requerimiento_presupuesto',
                  'id_requerimiento_presupuesto'
              ),
              ('requerimiento_presupuesto', 'baja_fecha')
      ) AS v(tabla, columna)
     WHERE NOT EXISTS (
         SELECT 1
           FROM information_schema.columns c
          WHERE c.table_schema = 'compras'
            AND c.table_name = v.tabla
            AND c.column_name = v.columna
     );

    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION
            'No se puede actualizar Compras. Faltan columnas: %.',
            v_faltantes;
    END IF;
END;
$preflight_estructura$;

LOCK TABLE compras.requerimiento
    IN SHARE ROW EXCLUSIVE MODE;

LOCK TABLE compras.requerimiento_cotizacion_prestador
    IN SHARE ROW EXCLUSIVE MODE;

LOCK TABLE compras.requerimiento_presupuesto
    IN SHARE ROW EXCLUSIVE MODE;

DO $preflight_datos$
DECLARE
    v_cantidad BIGINT;
    v_detalle TEXT;
BEGIN
    SELECT count(*)
      INTO v_cantidad
      FROM (
          SELECT
              rp.id_requerimiento,
              rp.id_prestador
          FROM compras.requerimiento_presupuesto rp
          WHERE rp.baja_fecha IS NULL
          GROUP BY
              rp.id_requerimiento,
              rp.id_prestador
          HAVING count(*) > 1
      ) duplicados;

    IF v_cantidad > 0 THEN
        SELECT string_agg(
                   format(
                       '(requerimiento=%s, prestador=%s, activos=%s)',
                       d.id_requerimiento,
                       d.id_prestador,
                       d.activos
                   ),
                   '; '
                   ORDER BY d.id_requerimiento, d.id_prestador
               )
          INTO v_detalle
          FROM (
              SELECT
                  rp.id_requerimiento,
                  rp.id_prestador,
                  count(*) AS activos
              FROM compras.requerimiento_presupuesto rp
              WHERE rp.baja_fecha IS NULL
              GROUP BY
                  rp.id_requerimiento,
                  rp.id_prestador
              HAVING count(*) > 1
              ORDER BY
                  rp.id_requerimiento,
                  rp.id_prestador
              LIMIT 20
          ) d;

        RAISE EXCEPTION
            'Hay % combinaciones con presupuestos activos duplicados. Primeros casos: %.',
            v_cantidad,
            v_detalle
            USING HINT =
                'Resolver manualmente los duplicados; el script no elimina datos.';
    END IF;

    SELECT count(*)
      INTO v_cantidad
      FROM compras.requerimiento_presupuesto rp
      LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
        ON rcp.id_requerimiento = rp.id_requerimiento
       AND rcp.id_prestador = rp.id_prestador
     WHERE rp.baja_fecha IS NULL
       AND rcp.id_requerimiento IS NULL;

    IF v_cantidad > 0 THEN
        SELECT string_agg(
                   format(
                       '(presupuesto=%s, requerimiento=%s, prestador=%s)',
                       d.id_requerimiento_presupuesto,
                       d.id_requerimiento,
                       d.id_prestador
                   ),
                   '; '
                   ORDER BY d.id_requerimiento_presupuesto
               )
          INTO v_detalle
          FROM (
              SELECT
                  rp.id_requerimiento_presupuesto,
                  rp.id_requerimiento,
                  rp.id_prestador
              FROM compras.requerimiento_presupuesto rp
              LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
                ON rcp.id_requerimiento = rp.id_requerimiento
               AND rcp.id_prestador = rp.id_prestador
              WHERE rp.baja_fecha IS NULL
                AND rcp.id_requerimiento IS NULL
              ORDER BY rp.id_requerimiento_presupuesto
              LIMIT 20
          ) d;

        RAISE EXCEPTION
            'Hay % presupuestos activos sin fila de cotizacion. Primeros casos: %.',
            v_cantidad,
            v_detalle
            USING HINT =
                'Crear o reconciliar manualmente la fila de cotizacion antes de reintentar.';
    END IF;

    SELECT count(*)
      INTO v_cantidad
      FROM compras.requerimiento_presupuesto rp
      JOIN compras.requerimiento_cotizacion_prestador rcp
        ON rcp.id_requerimiento = rp.id_requerimiento
       AND rcp.id_prestador = rp.id_prestador
     WHERE rp.baja_fecha IS NULL
       AND (
           rcp.estado_envio NOT IN ('ENVIADO', 'COTIZADO')
           OR rcp.fecha_envio IS NULL
       );

    IF v_cantidad > 0 THEN
        SELECT string_agg(
                   format(
                       '(presupuesto=%s, requerimiento=%s, prestador=%s, estado=%s, fecha_envio=%s)',
                       d.id_requerimiento_presupuesto,
                       d.id_requerimiento,
                       d.id_prestador,
                       d.estado_envio,
                       COALESCE(d.fecha_envio::TEXT, 'NULL')
                   ),
                   '; '
                   ORDER BY d.id_requerimiento_presupuesto
               )
          INTO v_detalle
          FROM (
              SELECT
                  rp.id_requerimiento_presupuesto,
                  rp.id_requerimiento,
                  rp.id_prestador,
                  rcp.estado_envio,
                  rcp.fecha_envio
              FROM compras.requerimiento_presupuesto rp
              JOIN compras.requerimiento_cotizacion_prestador rcp
                ON rcp.id_requerimiento = rp.id_requerimiento
               AND rcp.id_prestador = rp.id_prestador
              WHERE rp.baja_fecha IS NULL
                AND (
                    rcp.estado_envio NOT IN ('ENVIADO', 'COTIZADO')
                    OR rcp.fecha_envio IS NULL
                )
              ORDER BY rp.id_requerimiento_presupuesto
              LIMIT 20
          ) d;

        RAISE EXCEPTION
            'Hay % presupuestos activos con estado de prestador incompatible. Primeros casos: %.',
            v_cantidad,
            v_detalle
            USING HINT =
                'Reconciliar manualmente estado_envio y fecha_envio antes de reintentar.';
    END IF;

    SELECT count(*)
      INTO v_cantidad
      FROM compras.requerimiento_cotizacion_prestador rcp
     WHERE rcp.estado_envio = 'COTIZADO'
       AND NOT EXISTS (
           SELECT 1
             FROM compras.requerimiento_presupuesto rp
            WHERE rp.id_requerimiento = rcp.id_requerimiento
              AND rp.id_prestador = rcp.id_prestador
              AND rp.baja_fecha IS NULL
       );

    IF v_cantidad > 0 THEN
        SELECT string_agg(
                   format(
                       '(requerimiento=%s, prestador=%s)',
                       d.id_requerimiento,
                       d.id_prestador
                   ),
                   '; '
                   ORDER BY d.id_requerimiento, d.id_prestador
               )
          INTO v_detalle
          FROM (
              SELECT
                  rcp.id_requerimiento,
                  rcp.id_prestador
              FROM compras.requerimiento_cotizacion_prestador rcp
              WHERE rcp.estado_envio = 'COTIZADO'
                AND NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento = rcp.id_requerimiento
                       AND rp.id_prestador = rcp.id_prestador
                       AND rp.baja_fecha IS NULL
                )
              ORDER BY rcp.id_requerimiento, rcp.id_prestador
              LIMIT 20
          ) d;

        RAISE EXCEPTION
            'Hay % prestadores COTIZADO sin presupuesto activo. Primeros casos: %.',
            v_cantidad,
            v_detalle
            USING HINT =
                'Reconciliar manualmente el estado o la asociacion antes de reintentar.';
    END IF;
END;
$preflight_datos$;

DO $actualizar_checks$
DECLARE
    v_constraint RECORD;
BEGIN
    FOR v_constraint IN
        SELECT c.conname
          FROM pg_constraint c
         WHERE c.conrelid =
               'compras.requerimiento_cotizacion_prestador'::regclass
           AND c.contype = 'c'
           AND pg_get_constraintdef(c.oid) ILIKE '%estado_envio%'
           AND (
               c.conname IN (
                   'ck_compras_cotizacion_estado_envio',
                   'ck_compras_cotizacion_fecha_envio'
               )
               OR (
                   pg_get_constraintdef(c.oid) ILIKE '%PENDIENTE%'
                   AND pg_get_constraintdef(c.oid) ILIKE '%PROCESANDO%'
                   AND pg_get_constraintdef(c.oid) ILIKE '%ENVIADO%'
                   AND pg_get_constraintdef(c.oid) ILIKE '%ERROR%'
                   AND pg_get_constraintdef(c.oid) ILIKE '%EMAIL_INVALIDO%'
               )
               OR pg_get_constraintdef(c.oid) ILIKE '%fecha_envio%'
           )
    LOOP
        EXECUTE format(
            'ALTER TABLE compras.requerimiento_cotizacion_prestador DROP CONSTRAINT %I',
            v_constraint.conname
        );
    END LOOP;
END;
$actualizar_checks$;

ALTER TABLE compras.requerimiento_cotizacion_prestador
    ADD CONSTRAINT ck_compras_cotizacion_estado_envio
    CHECK (
        estado_envio IN (
            'PENDIENTE',
            'PROCESANDO',
            'ENVIADO',
            'COTIZADO',
            'ERROR',
            'EMAIL_INVALIDO'
        )
    );

ALTER TABLE compras.requerimiento_cotizacion_prestador
    ADD CONSTRAINT ck_compras_cotizacion_fecha_envio
    CHECK (
        estado_envio NOT IN ('ENVIADO', 'COTIZADO')
        OR fecha_envio IS NOT NULL
    );

UPDATE compras.requerimiento_cotizacion_prestador rcp
   SET estado_envio = 'COTIZADO',
       modi_fecha = now(),
       modi_usr = 'migracion_20260721'
  FROM compras.requerimiento_presupuesto rp
 WHERE rp.baja_fecha IS NULL
   AND rcp.id_requerimiento = rp.id_requerimiento
   AND rcp.id_prestador = rp.id_prestador
   AND rcp.estado_envio = 'ENVIADO';

DO $indice_unico$
DECLARE
    v_index_oid OID;
    v_correcto BOOLEAN := FALSE;
BEGIN
    SELECT i.indexrelid,
           i.indisunique
           AND i.indisvalid
           AND i.indisready
           AND i.indnatts = 2
           AND pg_get_indexdef(i.indexrelid, 1, TRUE) =
               'id_requerimiento'
           AND pg_get_indexdef(i.indexrelid, 2, TRUE) =
               'id_prestador'
           AND regexp_replace(
                   lower(pg_get_expr(i.indpred, i.indrelid)),
                   '[[:space:]]+',
                   '',
                   'g'
               ) = '(baja_fechaisnull)'
      INTO v_index_oid, v_correcto
      FROM pg_index i
      JOIN pg_class idx
        ON idx.oid = i.indexrelid
      JOIN pg_namespace n
        ON n.oid = idx.relnamespace
     WHERE n.nspname = 'compras'
       AND idx.relname =
           'ux_compras_presupuesto_requerimiento_prestador_activo';

    IF v_index_oid IS NOT NULL AND NOT COALESCE(v_correcto, FALSE) THEN
        EXECUTE
            'DROP INDEX compras.ux_compras_presupuesto_requerimiento_prestador_activo';
        v_index_oid := NULL;
    END IF;

    IF v_index_oid IS NULL THEN
        EXECUTE $sql$
            CREATE UNIQUE INDEX
                ux_compras_presupuesto_requerimiento_prestador_activo
            ON compras.requerimiento_presupuesto (
                id_requerimiento,
                id_prestador
            )
            WHERE baja_fecha IS NULL
        $sql$;
    END IF;
END;
$indice_unico$;

DROP INDEX IF EXISTS
    compras.ix_compras_presupuesto_prestador_activo;

-- Las funciones siguientes se copian del esquema canonico corregido.

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
        IF OLD.estado IN (3, 4, 5, 99)
           AND NEW IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        v_cambio_estructura :=
               NEW.id_sector IS DISTINCT FROM OLD.id_sector
            OR NEW.afiliado_cuil_titular
                IS DISTINCT FROM OLD.afiliado_cuil_titular
            OR NEW.afiliado_int IS DISTINCT FROM OLD.afiliado_int
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
            OR NEW.cargo_ospim IS DISTINCT FROM OLD.cargo_ospim
            OR NEW.cargo_tercerizadora
                IS DISTINCT FROM OLD.cargo_tercerizadora
            OR NEW.id_tercerizadora
                IS DISTINCT FROM OLD.id_tercerizadora
            OR NEW.recupero IS DISTINCT FROM OLD.recupero
            OR NEW.surge IS DISTINCT FROM OLD.surge
            OR NEW.observaciones IS DISTINCT FROM OLD.observaciones;

        IF v_cambio_estructura AND OLD.estado <> 1 THEN
            RAISE EXCEPTION
                'La estructura solo puede modificarse en estado PENDIENTE.';
END IF;

        IF NEW.estado IS DISTINCT FROM OLD.estado THEN
            IF NOT (
                    (OLD.estado = 1 AND NEW.estado IN (2, 99))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
            ) THEN
                RAISE EXCEPTION
                    'Transicion de estado invalida: % -> %.',
                    OLD.estado,
                    NEW.estado;
END IF;

            IF NEW.estado IN (4, 5) THEN
                RAISE EXCEPTION
                    'RECLAMO (RP) y ORDEN DE COMPRA son estados de solo lectura.';
END IF;

            IF OLD.estado = 1 AND NEW.estado = 2 THEN
                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento = NEW.id_requerimiento
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

            IF OLD.estado = 2 AND NEW.estado = 3 THEN
                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento = NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN
                    RAISE EXCEPTION
                        'No se puede cerrar una cotizacion sin detalles.';
END IF;

                IF EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento = NEW.id_requerimiento
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
                v_usuario := compras.normalizar_usuario(
                    COALESCE(NEW.modi_usr, NEW.baja_usr)
                );

                NEW.baja_fecha := COALESCE(
                    NEW.baja_fecha,
                    now()
                );
                NEW.baja_usr := COALESCE(
                    NULLIF(btrim(NEW.baja_usr), ''),
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

CREATE OR REPLACE FUNCTION compras.validar_requerimiento_detalle_fila()
RETURNS TRIGGER
AS $func$
DECLARE
v_estado INTEGER;
    v_id_sector_requerimiento INTEGER;
    v_sector VARCHAR(200);
    v_tipo_item VARCHAR(20);
    v_tipo_item_anterior VARCHAR(20);
    v_tipo_item_esperado VARCHAR(20);
    v_id_tipo_nomenclador_real INTEGER;
BEGIN
SELECT
    r.estado,
    r.id_sector,
    translate(
            upper(btrim(sr.descripcion)),
            'ÁÉÍÓÚÜáéíóúü',
            'AEIOUUAEIOUU'
    )
INTO
    v_estado,
    v_id_sector_requerimiento,
    v_sector
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento sr
              ON sr.id_sector = r.id_sector
WHERE r.id_requerimiento = NEW.id_requerimiento
  AND r.baja_fecha IS NULL;

IF v_estado IS NULL THEN
        RAISE EXCEPTION
            'No existe un requerimiento activo para el detalle.';
END IF;

    IF v_sector IN (
        'FARMACIA',
        'DISCAPACIDAD',
        'ODONTOLOGIA',
        'PRESTACIONES MEDICAS',
        'MONOTRIBUTO'
    ) THEN
        v_tipo_item_esperado := 'NOMENCLADOR';
    ELSIF v_sector IN (
        'RRHH',
        'LEGALES',
        'SISTEMAS',
        'OTROS'
    ) THEN
        v_tipo_item_esperado := 'OBSERVACION';
    ELSE
        RAISE EXCEPTION
            'El sector % no tiene configurado un tipo de detalle para Compras.',
            v_sector;
END IF;

    v_tipo_item := upper(btrim(COALESCE(NEW.tipo_item, '')));

    IF TG_OP = 'INSERT' THEN
        IF v_tipo_item <> v_tipo_item_esperado THEN
            RAISE EXCEPTION
                'El sector % requiere detalles de tipo %.',
                v_sector,
                v_tipo_item_esperado;
END IF;
ELSE
        v_tipo_item_anterior :=
            upper(btrim(COALESCE(OLD.tipo_item, '')));

        IF v_tipo_item_anterior = 'MEDICAMENTO' THEN
            IF v_tipo_item <> 'MEDICAMENTO' THEN
                RAISE EXCEPTION
                    'El detalle historico de medicamento no puede convertirse directamente.';
END IF;

            IF NEW.id_requerimiento
                    IS DISTINCT FROM OLD.id_requerimiento
               OR NEW.id_prestacion
                    IS DISTINCT FROM OLD.id_prestacion
               OR NEW.id_tipo_nomenclador
                    IS DISTINCT FROM OLD.id_tipo_nomenclador
               OR NEW.codigo_nomenclador
                    IS DISTINCT FROM OLD.codigo_nomenclador
               OR NEW.descripcion_nomenclador
                    IS DISTINCT FROM OLD.descripcion_nomenclador
               OR NEW.id_medicamento
                    IS DISTINCT FROM OLD.id_medicamento
               OR NEW.troquel
                    IS DISTINCT FROM OLD.troquel
               OR NEW.nombre_medicamento
                    IS DISTINCT FROM OLD.nombre_medicamento THEN

                RAISE EXCEPTION
                    'El detalle historico de medicamento solo permite modificar cantidad y observaciones.';
END IF;

        ELSIF v_tipo_item_anterior = 'NOMENCLADOR' THEN
            IF v_tipo_item <> 'NOMENCLADOR' THEN
                RAISE EXCEPTION
                    'Un detalle de nomenclador no puede convertirse a otro tipo.';
END IF;
        ELSIF v_tipo_item_anterior = 'OBSERVACION' THEN
            IF v_tipo_item <> 'OBSERVACION' THEN
                RAISE EXCEPTION
                    'Un detalle de observacion no puede convertirse a otro tipo.';
END IF;
ELSE
            RAISE EXCEPTION
                'El detalle persistido tiene un tipo tecnico desconocido.';
END IF;
END IF;

    NEW.tipo_item := v_tipo_item;

    IF v_tipo_item <> 'MEDICAMENTO'
       AND v_tipo_item <> v_tipo_item_esperado THEN
        RAISE EXCEPTION
            'El sector % requiere detalles de tipo %.',
            v_sector,
            v_tipo_item_esperado;
END IF;

    IF v_tipo_item = 'MEDICAMENTO' THEN
        IF NEW.id_medicamento IS NULL
           OR NEW.id_medicamento <= 0
           OR NULLIF(btrim(NEW.nombre_medicamento), '') IS NULL THEN

            RAISE EXCEPTION
                'El medicamento historico debe conservar id y nombre.';
END IF;
    ELSIF v_tipo_item = 'OBSERVACION' THEN
        IF NULLIF(btrim(NEW.observaciones), '') IS NULL THEN
            RAISE EXCEPTION
                'Debe informar las observaciones del detalle.';
END IF;

        IF NEW.id_prestacion IS NOT NULL
           OR NEW.id_tipo_nomenclador IS NOT NULL
           OR NULLIF(btrim(NEW.codigo_nomenclador), '') IS NOT NULL
           OR NULLIF(btrim(NEW.descripcion_nomenclador), '') IS NOT NULL
           OR NEW.id_medicamento IS NOT NULL
           OR NEW.troquel IS NOT NULL
           OR NULLIF(btrim(NEW.nombre_medicamento), '') IS NOT NULL THEN

            RAISE EXCEPTION
                'Un detalle de observacion no puede contener datos tecnicos.';
END IF;
ELSE
        IF NEW.id_medicamento IS NOT NULL
           OR NEW.troquel IS NOT NULL
           OR NULLIF(btrim(NEW.nombre_medicamento), '') IS NOT NULL THEN

            RAISE EXCEPTION
                'Un detalle de nomenclador no puede contener datos de medicamento.';
END IF;

        IF NEW.id_prestacion IS NULL
           OR NEW.id_prestacion <= 0
           OR NEW.id_tipo_nomenclador IS NULL
           OR NEW.id_tipo_nomenclador <= 0
           OR NULLIF(btrim(NEW.codigo_nomenclador), '') IS NULL
           OR NULLIF(btrim(NEW.descripcion_nomenclador), '') IS NULL THEN

            RAISE EXCEPTION
                'El nomenclador debe tener prestacion, tipo real positivo, codigo y descripcion.';
END IF;

SELECT n.id_tipo_nomenclador
INTO v_id_tipo_nomenclador_real
FROM autorizaciones.nomenclador n
WHERE n.id_prestacion = NEW.id_prestacion
  AND n.baja_fecha IS NULL;

IF NOT FOUND THEN
            RAISE EXCEPTION
                'La prestacion seleccionada no existe o no esta activa.';
END IF;

        IF NEW.id_tipo_nomenclador
                <> v_id_tipo_nomenclador_real THEN

            RAISE EXCEPTION
                'El tipo de nomenclador informado no corresponde a la prestacion seleccionada.';
END IF;

        IF v_sector = 'FARMACIA' THEN
            IF v_id_tipo_nomenclador_real <> 9 THEN
                RAISE EXCEPTION
                    'Para el sector Farmacia el tipo de nomenclador debe ser 9.';
END IF;
ELSE
            IF v_id_tipo_nomenclador_real = 9 THEN
                RAISE EXCEPTION
                    'El nomenclador tipo 9 solo puede utilizarse en el sector Farmacia.';
END IF;
END IF;
END IF;

    IF TG_OP = 'INSERT' AND v_estado <> 1 THEN
        RAISE EXCEPTION
            'Los detalles solo pueden crearse en estado PENDIENTE.';
END IF;

    IF TG_OP = 'UPDATE' THEN
        IF v_estado = 1 THEN
            NULL;

        ELSIF v_estado = 2 THEN
            IF NEW.id_requerimiento
                    IS DISTINCT FROM OLD.id_requerimiento
               OR NEW.tipo_item
                    IS DISTINCT FROM OLD.tipo_item
               OR NEW.id_prestacion
                    IS DISTINCT FROM OLD.id_prestacion
               OR NEW.id_tipo_nomenclador
                    IS DISTINCT FROM OLD.id_tipo_nomenclador
               OR NEW.codigo_nomenclador
                    IS DISTINCT FROM OLD.codigo_nomenclador
               OR NEW.descripcion_nomenclador
                    IS DISTINCT FROM OLD.descripcion_nomenclador
               OR NEW.id_medicamento
                    IS DISTINCT FROM OLD.id_medicamento
               OR NEW.troquel
                    IS DISTINCT FROM OLD.troquel
               OR NEW.nombre_medicamento
                    IS DISTINCT FROM OLD.nombre_medicamento
               OR NEW.cantidad
                    IS DISTINCT FROM OLD.cantidad
               OR NEW.observaciones
                    IS DISTINCT FROM OLD.observaciones
               OR NEW.baja_fecha
                    IS DISTINCT FROM OLD.baja_fecha
               OR NEW.baja_usr
                    IS DISTINCT FROM OLD.baja_usr THEN

                RAISE EXCEPTION
                    'En estado A COTIZAR la estructura del detalle esta bloqueada.';
END IF;
ELSE
            RAISE EXCEPTION
                'El detalle no puede modificarse en el estado actual.';
END IF;
END IF;

    IF v_estado = 1 THEN
        IF NEW.precio_unitario_estimado IS NOT NULL
           OR NEW.precio_total_estimado IS NOT NULL
           OR NEW.id_prestador IS NOT NULL THEN

            RAISE EXCEPTION
                'Un requerimiento PENDIENTE no puede tener datos de cotizacion.';
END IF;

    ELSIF v_estado = 2 THEN
        IF NEW.precio_unitario_estimado < 0 THEN
            RAISE EXCEPTION
                'El precio unitario estimado no puede ser negativo.';
END IF;

        IF NEW.precio_unitario_estimado IS NULL THEN
            NEW.precio_total_estimado := NULL;
ELSE
            NEW.precio_total_estimado :=
                round(
                    NEW.cantidad
                    * NEW.precio_unitario_estimado,
                    2
                );
END IF;

        IF NEW.id_prestador IS NOT NULL
           AND NOT EXISTS (
                SELECT 1
                FROM compras.requerimiento_cotizacion_prestador rcp
                WHERE rcp.id_requerimiento = NEW.id_requerimiento
                  AND rcp.id_prestador = NEW.id_prestador
                  AND rcp.estado_envio IN ('ENVIADO', 'COTIZADO')
           ) THEN

            RAISE EXCEPTION
                'El prestador seleccionado no fue notificado correctamente para este requerimiento.';
END IF;
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

CREATE OR REPLACE FUNCTION compras.buscar_prestadores_enviados(
    p_id_requerimiento INTEGER,
    p_texto VARCHAR,
    p_limite INTEGER
)
    RETURNS TABLE (
                      id_prestador INTEGER,
                      descripcion VARCHAR,
                      cuit VARCHAR,
                      email VARCHAR,
                      id_tipo_prestador INTEGER,
                      tipo_prestador VARCHAR
                  )
    AS $func$
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
    p.contacto::VARCHAR,
    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR
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
  AND r.estado IN (2, 3)
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
$func$
LANGUAGE plpgsql
STABLE;

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
           AND rp.id_prestador = p_id_prestador
           AND rp.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'El prestador ya tiene un presupuesto activo para este requerimiento.';
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
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
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
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

CREATE OR REPLACE FUNCTION
compras.diagnosticar_prestadores_notificacion_cotizacion(
    p_id_requerimiento INTEGER
)
RETURNS TABLE (
    id_sector INTEGER,
    prestadores_habilitados INTEGER,
    prestadores_compatibles_sector INTEGER,
    prestadores_bloqueados_estado_previo INTEGER
)
LANGUAGE sql
STABLE
AS
$function$
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
                         WHEN stp.id_tipo_prestador IS NOT NULL
                             THEN p.id_prestador
        END
    )::INTEGER
        AS prestadores_compatibles_sector,

    COUNT(
            DISTINCT CASE
                         WHEN stp.id_tipo_prestador IS NOT NULL
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

         LEFT JOIN compras.sector_tipo_prestador stp
                   ON stp.id_sector = r.id_sector
                       AND stp.id_tipo_prestador = p.id_tipo_prestador
                       AND stp.activo = TRUE
                       AND stp.baja_fecha IS NULL

         LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
                   ON rcp.id_requerimiento = r.id_requerimiento
                       AND rcp.id_prestador = p.id_prestador

WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL

GROUP BY
    r.id_sector;
$function$;

CREATE OR REPLACE FUNCTION
compras.reservar_notificacion_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,
    p_usuario VARCHAR
)
RETURNS TABLE (
    reservado BOOLEAN,
    estado_envio TEXT,
    email_destino TEXT,
    motivo_codigo TEXT,
    motivo_descripcion TEXT
)
LANGUAGE plpgsql
AS
$function$
DECLARE
v_usuario VARCHAR(100);
    v_id_sector INTEGER;
    v_email_real VARCHAR(320);
    v_email_guardado VARCHAR(320);
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
    r.id_sector
INTO
    v_id_sector
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo %.',
            p_id_requerimiento;
END IF;

SELECT
    NULLIF(
            BTRIM(p.contacto),
            ''
    )
INTO
    v_email_real
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
            'o no esta habilitado para cotizar.',
            p_id_prestador;
END IF;

    IF NOT EXISTS (
        SELECT
            1
        FROM public.prestador p
        INNER JOIN compras.sector_tipo_prestador stp
            ON stp.id_tipo_prestador =
                p.id_tipo_prestador
           AND stp.id_sector =
                v_id_sector
           AND stp.activo = TRUE
           AND stp.baja_fecha IS NULL
        WHERE p.id_prestador =
            p_id_prestador
          AND p.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'El prestador % no es compatible con el sector %.',
            p_id_prestador,
            v_id_sector;
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
           v_email_real,
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
            'notificacion para requerimiento % y prestador %.',
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
            || 'posiblemente por otra ejecucion concurrente.'
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
        v_email_real,

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
    v_email_real::TEXT,
    'RESERVA_OTORGADA'::TEXT,
    (
        'La ejecucion obtuvo la reserva exclusiva '
            || 'y la fila quedo PROCESANDO.'
        )::TEXT;
END;
$function$;

DO $postflight$
DECLARE
    v_faltantes TEXT;
    v_indice_valido BOOLEAN;
BEGIN
    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
          JOIN compras.requerimiento_cotizacion_prestador rcp
            ON rcp.id_requerimiento = rp.id_requerimiento
           AND rcp.id_prestador = rp.id_prestador
         WHERE rp.baja_fecha IS NULL
           AND rcp.estado_envio <> 'COTIZADO'
    ) THEN
        RAISE EXCEPTION
            'Postflight: quedo un presupuesto activo sin prestador COTIZADO.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_cotizacion_prestador rcp
         WHERE rcp.estado_envio = 'COTIZADO'
           AND NOT EXISTS (
               SELECT 1
                 FROM compras.requerimiento_presupuesto rp
                WHERE rp.id_requerimiento = rcp.id_requerimiento
                  AND rp.id_prestador = rcp.id_prestador
                  AND rp.baja_fecha IS NULL
           )
    ) THEN
        RAISE EXCEPTION
            'Postflight: quedo un prestador COTIZADO sin presupuesto activo.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.baja_fecha IS NULL
         GROUP BY
             rp.id_requerimiento,
             rp.id_prestador
        HAVING count(*) > 1
    ) THEN
        RAISE EXCEPTION
            'Postflight: permanecen presupuestos activos duplicados.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM pg_constraint c
         WHERE c.conrelid =
               'compras.requerimiento_cotizacion_prestador'::regclass
           AND c.conname = 'ck_compras_cotizacion_estado_envio'
           AND pg_get_constraintdef(c.oid) ILIKE '%COTIZADO%'
    ) THEN
        RAISE EXCEPTION
            'Postflight: el CHECK de estados no admite COTIZADO.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM pg_constraint c
         WHERE c.conrelid =
               'compras.requerimiento_cotizacion_prestador'::regclass
           AND c.conname = 'ck_compras_cotizacion_fecha_envio'
           AND pg_get_constraintdef(c.oid) ILIKE '%COTIZADO%'
           AND pg_get_constraintdef(c.oid) ILIKE '%fecha_envio%'
    ) THEN
        RAISE EXCEPTION
            'Postflight: el CHECK de fecha no cubre COTIZADO.';
    END IF;

    SELECT i.indisunique
           AND i.indisvalid
           AND i.indisready
           AND i.indnatts = 2
           AND pg_get_indexdef(i.indexrelid, 1, TRUE) =
               'id_requerimiento'
           AND pg_get_indexdef(i.indexrelid, 2, TRUE) =
               'id_prestador'
           AND regexp_replace(
                   lower(pg_get_expr(i.indpred, i.indrelid)),
                   '[[:space:]]+',
                   '',
                   'g'
               ) = '(baja_fechaisnull)'
      INTO v_indice_valido
      FROM pg_index i
      JOIN pg_class idx
        ON idx.oid = i.indexrelid
      JOIN pg_namespace n
        ON n.oid = idx.relnamespace
     WHERE n.nspname = 'compras'
       AND idx.relname =
           'ux_compras_presupuesto_requerimiento_prestador_activo';

    IF NOT COALESCE(v_indice_valido, FALSE) THEN
        RAISE EXCEPTION
            'Postflight: el indice unico parcial no coincide con el contrato.';
    END IF;

    SELECT string_agg(v.nombre, ', ' ORDER BY v.nombre)
      INTO v_faltantes
      FROM (
          VALUES
              ('validar_requerimiento_fila'),
              ('validar_requerimiento_detalle_fila'),
              ('guardar_cotizacion_requerimiento'),
              ('buscar_prestadores_enviados'),
              ('registrar_requerimiento_presupuesto'),
              ('baja_requerimiento_presupuesto'),
              ('reactivar_requerimiento_presupuesto'),
              ('diagnosticar_prestadores_notificacion_cotizacion'),
              ('reservar_notificacion_cotizacion_prestador')
      ) AS v(nombre)
     WHERE NOT EXISTS (
         SELECT 1
           FROM pg_proc p
           JOIN pg_namespace n
             ON n.oid = p.pronamespace
          WHERE n.nspname = 'compras'
            AND p.proname = v.nombre
     );

    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION
            'Postflight: faltan funciones actualizadas: %.',
            v_faltantes;
    END IF;
END;
$postflight$;

COMMIT;
