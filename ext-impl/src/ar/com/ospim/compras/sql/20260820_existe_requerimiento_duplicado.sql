/*
 * Compras - control de requerimientos duplicados.
 *
 * Regla funcional:
 *
 * No debe existir otro requerimiento activo/no anulado para:
 *
 * - el mismo afiliado: CUIL titular + integrante;
 * - la misma prestacion;
 * - la misma fecha de Orden medica.
 *
 * El parametro p_id_requerimiento_excluir permite reutilizar la funcion
 * durante una edicion sin considerar al propio requerimiento.
 *
 * PostgreSQL 9.6+
 */

BEGIN;


CREATE OR REPLACE FUNCTION compras.existe_requerimiento_duplicado(
    p_cuil_titular VARCHAR,
    p_inte INTEGER,
    p_id_prestacion INTEGER,
    p_fecha_orden_medica DATE,
    p_id_requerimiento_excluir INTEGER
)
RETURNS BOOLEAN
AS $func$
SELECT
    CASE
        /*
         * Si la clave funcional no esta completa no puede afirmarse
         * la existencia de un duplicado.
         *
         * Las validaciones de obligatoriedad pertenecen al flujo
         * de alta/edicion; esta funcion responde exclusivamente
         * por la existencia del duplicado.
         */
        WHEN NULLIF(
                     btrim(p_cuil_titular),
                     ''
             ) IS NULL
            OR p_inte IS NULL
            OR p_inte < 0
            OR p_id_prestacion IS NULL
            OR p_id_prestacion <= 0
            OR p_fecha_orden_medica IS NULL
            THEN FALSE

        ELSE EXISTS (
            SELECT 1

            FROM compras.requerimiento r

                     INNER JOIN compras.requerimiento_detalle d
                                ON d.id_requerimiento =
                                   r.id_requerimiento

                     INNER JOIN compras.requerimiento_presupuesto rp
                                ON rp.id_requerimiento =
                                   r.id_requerimiento

            WHERE
                /*
                 * Requerimiento vigente.
                 *
                 * Se controla tanto baja logica como estado ANULADO
                 * para mantener el mismo criterio funcional utilizado
                 * por los controles documentales existentes.
                 */
                r.baja_fecha IS NULL

              AND r.estado <> 99

                /*
                 * Misma persona.
                 */
              AND r.afiliado_cuil_titular =
                  btrim(p_cuil_titular)

              AND r.afiliado_int =
                  p_inte

                /*
                 * Misma prestacion.
                 *
                 * El detalle debe encontrarse activo y ser una
                 * referencia de nomenclador real.
                 */
              AND d.baja_fecha IS NULL

              AND d.tipo_item =
                  'NOMENCLADOR'

              AND d.id_prestacion =
                  p_id_prestacion

                /*
                 * Misma fecha de Orden medica.
                 *
                 * tipo_documento = 2 identifica Orden medica
                 * en requerimiento_presupuesto.
                 */
              AND rp.baja_fecha IS NULL

              AND rp.tipo_documento =
                  2

              AND rp.fecha_documento =
                  p_fecha_orden_medica

                /*
                 * Alta:
                 *   normalmente llega 0 o NULL, por lo que se
                 *   consideran todos los requerimientos existentes.
                 *
                 * Edicion:
                 *   permite excluir el propio requerimiento.
                 */
              AND (
                p_id_requerimiento_excluir IS NULL
                    OR p_id_requerimiento_excluir <= 0
                    OR r.id_requerimiento
                    <> p_id_requerimiento_excluir
                )

            LIMIT 1
        )
        END;
$func$
LANGUAGE sql
STABLE;


COMMIT;