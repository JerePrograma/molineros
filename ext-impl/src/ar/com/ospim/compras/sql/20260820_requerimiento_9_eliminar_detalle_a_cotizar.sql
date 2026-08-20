/*
 * Compras - Requerimiento 9.
 *
 * Permite eliminar detalles en:
 *
 * - PENDIENTE.
 * - A COTIZAR / ENVIADO A COTIZAR, siempre que permanezca
 *   al menos un detalle activo.
 *
 * No habilita:
 *
 * - crear detalles en A COTIZAR;
 * - editar estructura en A COTIZAR;
 * - eliminar detalles desde COTIZADO, RECLAMO_RP,
 *   ORDEN_COMPRA o ANULADO;
 * - reactivar detalles dados de baja;
 * - alterar estructura o cotizacion aprovechando una baja.
 *
 * PostgreSQL 9.6+.
 *
 * Ejecucion sugerida:
 *
 * psql -X -v ON_ERROR_STOP=1 \
 *   -f 20260820_requerimiento_9_eliminar_detalle_a_cotizar.sql
 */

BEGIN;


/* =====================================================================
 * VALIDACION CANONICA DE DETALLES
 * =====================================================================
 *
 * Se conserva la validacion existente y se incorpora una unica excepcion
 * funcional: la baja logica en estados 1 y 2.
 *
 * La baja se procesa antes de las validaciones tecnicas del detalle para
 * evitar que una prestacion historica tenga que revalidarse contra
 * nomencladores actuales solamente para poder ser eliminada.
 * ===================================================================== */

CREATE OR REPLACE FUNCTION compras.validar_requerimiento_detalle_fila()
RETURNS TRIGGER
AS $func$
DECLARE
v_estado INTEGER;
    v_sector VARCHAR(200);
    v_tipo_item VARCHAR(20);
    v_tipo_item_anterior VARCHAR(20);
    v_tipo_item_esperado VARCHAR(20);
    v_id_tipo_nomenclador_real INTEGER;
    v_total_detalles_activos INTEGER;
BEGIN

SELECT
    r.estado,
    translate(
            upper(btrim(sr.descripcion)),
            '¡…Õ”⁄‹·ÈÌÛ˙¸',
            'AEIOUUAEIOUU'
    )
INTO
    v_estado,
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


    /* =================================================================
     * BAJA LOGICA
     * =================================================================
     *
     * La baja constituye una operacion diferente de una modificacion
     * estructural.
     *
     * PENDIENTE:
     *     se permite quitar el detalle.
     *
     * A COTIZAR:
     *     se permite quitarlo solamente si queda al menos un detalle.
     *
     * Otros estados:
     *     quedan bloqueados.
     *
     * Este bloque tambien protege contra UPDATE manual utilizado para
     * modificar otros campos junto con baja_fecha.
     * ================================================================= */

    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NULL
       AND NEW.baja_fecha IS NOT NULL THEN

        IF v_estado NOT IN (1, 2) THEN
            RAISE EXCEPTION
                'El detalle no puede eliminarse en el estado actual.';
END IF;


        /*
         * Una baja no puede modificar simultaneamente estructura,
         * cotizacion ni datos de alta.
         *
         * Solamente pueden variar:
         *
         * - baja_fecha
         * - baja_usr
         * - modi_fecha
         * - modi_usr
         */

        IF NEW.id_detalle
                IS DISTINCT FROM OLD.id_detalle

           OR NEW.id_requerimiento
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

           OR NEW.precio_unitario_estimado
                IS DISTINCT FROM OLD.precio_unitario_estimado

           OR NEW.precio_total_estimado
                IS DISTINCT FROM OLD.precio_total_estimado

           OR NEW.id_prestador
                IS DISTINCT FROM OLD.id_prestador

           OR NEW.alta_fecha
                IS DISTINCT FROM OLD.alta_fecha

           OR NEW.alta_usr
                IS DISTINCT FROM OLD.alta_usr THEN

            RAISE EXCEPTION
                'La eliminacion de un detalle no puede modificar su estructura ni sus datos de cotizacion.';
END IF;


        IF NULLIF(
            btrim(NEW.baja_usr),
            ''
        ) IS NULL THEN

            RAISE EXCEPTION
                'Debe informar el usuario que elimina el detalle.';
END IF;


        /*
         * En A COTIZAR se serializan todas las bajas del mismo
         * requerimiento utilizando la cabecera como lock comun.
         *
         * Esto evita que dos transacciones observen simultaneamente
         * dos detalles activos y ambas eliminen uno, dejando cero.
         */

        IF v_estado = 2 THEN

            PERFORM 1
            FROM compras.requerimiento r
            WHERE r.id_requerimiento = NEW.id_requerimiento
              AND r.baja_fecha IS NULL
            FOR UPDATE;

IF NOT FOUND THEN
                RAISE EXCEPTION
                    'El requerimiento ya no se encuentra activo.';
END IF;


SELECT count(*)
INTO v_total_detalles_activos
FROM compras.requerimiento_detalle d
WHERE d.id_requerimiento = NEW.id_requerimiento
  AND d.baja_fecha IS NULL;


IF v_total_detalles_activos <= 1 THEN
                RAISE EXCEPTION
                    'El requerimiento ENVIADO A COTIZAR debe conservar al menos una prestacion.';
END IF;

END IF;


        /*
         * No continuar con la validacion estructural.
         *
         * La fila solamente esta siendo dada de baja y ya se valido
         * expresamente que ningun otro dato haya cambiado.
         */

RETURN NEW;

END IF;


    /* =================================================================
     * CONFIGURACION DEL TIPO DE DETALLE SEGUN SECTOR
     * ================================================================= */

    IF v_sector IN (
        'FARMACIA',
        'DISCAPACIDAD',
        'ODONTOLOGIA',
        'PRESTACIONES MEDICAS'
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


    v_tipo_item :=
        upper(
            btrim(
                COALESCE(
                    NEW.tipo_item,
                    ''
                )
            )
        );


    /* =================================================================
     * TIPO TECNICO
     * ================================================================= */

    IF TG_OP = 'INSERT' THEN

        IF v_tipo_item <> v_tipo_item_esperado THEN
            RAISE EXCEPTION
                'El sector % requiere detalles de tipo %.',
                v_sector,
                v_tipo_item_esperado;
END IF;

ELSE

        v_tipo_item_anterior :=
            upper(
                btrim(
                    COALESCE(
                        OLD.tipo_item,
                        ''
                    )
                )
            );


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


    /* =================================================================
     * VALIDACION TECNICA DEL CONTENIDO
     * ================================================================= */

    IF v_tipo_item = 'MEDICAMENTO' THEN

        IF NEW.id_medicamento IS NULL
           OR NEW.id_medicamento <= 0
           OR NULLIF(
                btrim(
                    NEW.nombre_medicamento
                ),
                ''
           ) IS NULL THEN

            RAISE EXCEPTION
                'El medicamento historico debe conservar id y nombre.';
END IF;


    ELSIF v_tipo_item = 'OBSERVACION' THEN

        IF NULLIF(
            btrim(
                NEW.observaciones
            ),
            ''
        ) IS NULL THEN

            RAISE EXCEPTION
                'Debe informar las observaciones del detalle.';
END IF;


        IF NEW.id_prestacion IS NOT NULL
           OR NEW.id_tipo_nomenclador IS NOT NULL
           OR NULLIF(
                btrim(
                    NEW.codigo_nomenclador
                ),
                ''
           ) IS NOT NULL
           OR NULLIF(
                btrim(
                    NEW.descripcion_nomenclador
                ),
                ''
           ) IS NOT NULL
           OR NEW.id_medicamento IS NOT NULL
           OR NEW.troquel IS NOT NULL
           OR NULLIF(
                btrim(
                    NEW.nombre_medicamento
                ),
                ''
           ) IS NOT NULL THEN

            RAISE EXCEPTION
                'Un detalle de observacion no puede contener datos tecnicos.';
END IF;


ELSE

        IF NEW.id_medicamento IS NOT NULL
           OR NEW.troquel IS NOT NULL
           OR NULLIF(
                btrim(
                    NEW.nombre_medicamento
                ),
                ''
           ) IS NOT NULL THEN

            RAISE EXCEPTION
                'Un detalle de nomenclador no puede contener datos de medicamento.';
END IF;


        IF NEW.id_prestacion IS NULL
           OR NEW.id_prestacion <= 0
           OR NEW.id_tipo_nomenclador IS NULL
           OR NEW.id_tipo_nomenclador <= 0
           OR NULLIF(
                btrim(
                    NEW.codigo_nomenclador
                ),
                ''
           ) IS NULL
           OR NULLIF(
                btrim(
                    NEW.descripcion_nomenclador
                ),
                ''
           ) IS NULL THEN

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


    /* =================================================================
     * REGLAS POR ESTADO
     * ================================================================= */

    IF TG_OP = 'INSERT'
       AND v_estado <> 1 THEN

        RAISE EXCEPTION
            'Los detalles solo pueden crearse en estado PENDIENTE.';
END IF;


    IF TG_OP = 'UPDATE' THEN

        IF v_estado = 1 THEN

            NULL;


        ELSIF v_estado = 2 THEN

            /*
             * En A COTIZAR solamente se permiten modificaciones
             * correspondientes al flujo de cotizacion.
             *
             * La baja logica autorizada ya fue procesada y retornada
             * al comienzo de la funcion.
             */

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


    /* =================================================================
     * DATOS DE COTIZACION
     * ================================================================= */

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
                WHERE rcp.id_requerimiento =
                      NEW.id_requerimiento
                  AND rcp.id_prestador =
                      NEW.id_prestador
                  AND rcp.estado_envio IN (
                      'ENVIADO',
                      'COTIZADO'
                  )
           ) THEN

            RAISE EXCEPTION
                'El prestador seleccionado no fue notificado correctamente para este requerimiento.';
END IF;

END IF;


RETURN NEW;

END;
$func$
LANGUAGE plpgsql;


/* =====================================================================
 * BORRADO LOGICO DE DETALLE
 * =====================================================================
 *
 * Mantiene exactamente la firma consumida actualmente desde Java:
 *
 *     compras.borrar_requerimiento_detalle(INTEGER, VARCHAR)
 *
 * PENDIENTE:
 *     conserva la semantica historica y permite eliminar.
 *
 * A COTIZAR:
 *     permite eliminar siempre que permanezca al menos un detalle activo.
 *
 * El lock se toma en orden:
 *
 *     requerimiento -> detalle
 *
 * para mantener el mismo orden general utilizado por las operaciones de
 * cotizacion y evitar introducir inversiones innecesarias de locks.
 * ===================================================================== */

CREATE OR REPLACE FUNCTION compras.borrar_requerimiento_detalle(
    p_id_detalle INTEGER,
    p_usuario VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
v_id_requerimiento INTEGER;
    v_estado INTEGER;
    v_total_detalles_activos INTEGER;
    v_usuario VARCHAR(100);
BEGIN

    IF p_id_detalle IS NULL
       OR p_id_detalle <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el detalle del requerimiento.';
END IF;


    v_usuario :=
        compras.normalizar_usuario(
            p_usuario
        );


    /*
     * Se obtiene primero el padre sin bloquear la fila detalle.
     *
     * El lock autoritativo se toma luego sobre el requerimiento para
     * respetar siempre el orden:
     *
     *     requerimiento -> detalle
     */

SELECT d.id_requerimiento
INTO v_id_requerimiento
FROM compras.requerimiento_detalle d
WHERE d.id_detalle = p_id_detalle
  AND d.baja_fecha IS NULL;


IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontro el detalle activo a borrar.';
END IF;


    /*
     * La cabecera serializa:
     *
     * - eliminacion vs eliminacion;
     * - eliminacion vs cierre de cotizacion;
     * - eliminacion vs cambio de estado.
     */

SELECT r.estado
INTO v_estado
FROM compras.requerimiento r
WHERE r.id_requerimiento =
      v_id_requerimiento
  AND r.baja_fecha IS NULL
    FOR UPDATE;


IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo del detalle.';
END IF;


    IF v_estado NOT IN (1, 2) THEN
        RAISE EXCEPTION
            'Los detalles solo pueden borrarse en estado PENDIENTE o ENVIADO A COTIZAR.';
END IF;


    /*
     * Una vez bloqueado el requerimiento se bloquea la fila concreta.
     *
     * Si otra operacion la elimino mientras esperabamos el lock de
     * cabecera, el intento se rechaza.
     */

    PERFORM 1
    FROM compras.requerimiento_detalle d
    WHERE d.id_detalle = p_id_detalle
      AND d.id_requerimiento =
          v_id_requerimiento
      AND d.baja_fecha IS NULL
    FOR UPDATE;


IF NOT FOUND THEN
        RAISE EXCEPTION
            'El detalle ya no se encuentra activo.';
END IF;


    /*
     * En A COTIZAR nunca puede quedar el requerimiento sin detalles.
     *
     * Debido al FOR UPDATE anterior sobre compras.requerimiento,
     * dos bajas concurrentes del mismo requerimiento quedan
     * serializadas antes de ejecutar este conteo.
     */

    IF v_estado = 2 THEN

SELECT count(*)
INTO v_total_detalles_activos
FROM compras.requerimiento_detalle d
WHERE d.id_requerimiento =
      v_id_requerimiento
  AND d.baja_fecha IS NULL;


IF v_total_detalles_activos <= 1 THEN
            RAISE EXCEPTION
                'El requerimiento ENVIADO A COTIZAR debe conservar al menos una prestacion.';
END IF;

END IF;


UPDATE compras.requerimiento_detalle
SET baja_fecha = now(),
    baja_usr = v_usuario,
    modi_fecha = now(),
    modi_usr = v_usuario
WHERE id_detalle = p_id_detalle
  AND id_requerimiento =
      v_id_requerimiento
  AND baja_fecha IS NULL;


IF NOT FOUND THEN
        RAISE EXCEPTION
            'El detalle ya no se encuentra activo.';
END IF;

END;
$func$
LANGUAGE plpgsql;


COMMIT;