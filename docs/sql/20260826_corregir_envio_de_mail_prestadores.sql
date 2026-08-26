CREATE OR REPLACE FUNCTION compras.resolver_emails_cotizacion_prestador(
    p_id_prestador integer
)
RETURNS text
LANGUAGE 'sql'
COST 100
STABLE
AS $BODY$

SELECT
    string_agg(
            contacto.email,
            ';'
                ORDER BY
                contacto.prioridad,
            contacto.fecha_referencia DESC NULLS LAST,
            contacto.id_contacto_e DESC,
            LOWER(contacto.email)
    )
FROM (
         SELECT DISTINCT ON (
             LOWER(
             BTRIM(ce.contacto)
             )
             )
             BTRIM(ce.contacto) AS email,

             CASE
             WHEN UPPER(
             BTRIM(
             COALESCE(
             ce.tipo_contacto_e,
             ''
             )
             )
             ) = 'E'
             THEN 1

             WHEN UPPER(
             BTRIM(
             COALESCE(
             ce.tipo_contacto_e,
             ''
             )
             )
             ) = 'F'
             THEN 2

             ELSE 3
             END AS prioridad,

             COALESCE(
             ce.modi_fecha,
             ce.alta_fecha,
             ce.vigen_desde,
             pce.vigen_desde
             ) AS fecha_referencia,

             ce.id_contacto_e

         FROM public.prestad_contacto_e pce

             INNER JOIN public.contacto_e ce
         ON ce.id_contacto_e =
             pce.id_contacto_e

         WHERE pce.id_prestador =
             p_id_prestador

           AND UPPER(
             BTRIM(
             COALESCE(
             ce.tipo_contacto_e,
             ''
             )
             )
             ) IN ('E', 'F')

           AND ce.baja_fecha IS NULL

           AND (
             pce.vigen_desde IS NULL
            OR pce.vigen_desde <= LOCALTIMESTAMP
             )

           AND (
             ce.vigen_desde IS NULL
            OR ce.vigen_desde <= LOCALTIMESTAMP
             )

           AND NULLIF(
             BTRIM(ce.contacto),
             ''
             ) IS NOT NULL

           AND NULLIF(
             BTRIM(ce.contacto),
             ''
             )
             ~* '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$'

         ORDER BY
             LOWER(
             BTRIM(ce.contacto)
             ),

             CASE
             WHEN UPPER(
             BTRIM(
             COALESCE(
             ce.tipo_contacto_e,
             ''
             )
             )
             ) = 'E'
             THEN 1

             WHEN UPPER(
             BTRIM(
             COALESCE(
             ce.tipo_contacto_e,
             ''
             )
             )
             ) = 'F'
             THEN 2

             ELSE 3
             END,

             COALESCE(
             ce.modi_fecha,
             ce.alta_fecha,
             ce.vigen_desde,
             pce.vigen_desde
             ) DESC NULLS LAST,

             ce.id_contacto_e DESC

     ) contacto;

$BODY$;

ALTER FUNCTION compras.resolver_emails_cotizacion_prestador(integer)
    OWNER TO postgres;

CREATE OR REPLACE FUNCTION compras.reservar_notificacion_cotizacion_prestador(
    p_id_requerimiento integer,
    p_id_prestador integer,
    p_usuario character varying
)
RETURNS TABLE(
    reservado boolean,
    estado_envio text,
    email_destino text,
    motivo_codigo text,
    motivo_descripcion text
)
LANGUAGE 'plpgsql'
COST 100
VOLATILE
ROWS 1000
AS $BODY$

DECLARE
v_usuario VARCHAR(100);
    v_id_sector INTEGER;
    v_estado_requerimiento INTEGER;

    /*
     * Contiene 0..N emails separados por ';'.
     *
     * TEXT es deliberado: una lista de varios emails
     * puede superar fácilmente 320 caracteres.
     */
    v_emails_reales TEXT;
    v_email_guardado TEXT;

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
    r.id_sector,
    r.estado
INTO
    v_id_sector,
    v_estado_requerimiento
FROM compras.requerimiento r
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.baja_fecha IS NULL
    FOR UPDATE;


IF NOT FOUND THEN

        RAISE EXCEPTION
            'No existe el requerimiento activo %.',
            p_id_requerimiento;

END IF;


    IF v_estado_requerimiento NOT IN (1, 2) THEN

        RAISE EXCEPTION
            'El requerimiento % no se encuentra disponible para notificar prestadores.',
            p_id_requerimiento;

END IF;


    /*
     * La existencia funcional del prestador se valida
     * independientemente de sus contactos electrónicos.
     */
    PERFORM 1
    FROM public.prestador p
    WHERE p.id_prestador =
          p_id_prestador
      AND COALESCE(
              p.solicitar_cotizacion,
              FALSE
          ) = TRUE
      AND p.baja_fecha IS NULL;


    IF NOT FOUND THEN

        RAISE EXCEPTION
            'El prestador % no existe, esta dado de baja '
            'o no está habilitado para cotizar.',
            p_id_prestador;

END IF;


    /*
     * Fuente canónica de destinatarios:
     *
     * prestad_contacto_e -> contacto_e
     *
     * No existe fallback hacia prestador ni ninguna
     * otra fuente.
     */
SELECT
    compras.resolver_emails_cotizacion_prestador(
            p_id_prestador
    )
INTO
    v_emails_reales;


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
           v_emails_reales,
           clock_timestamp(),
           v_usuario
       )
    ON CONFLICT (
        id_requerimiento,
        id_prestador
    )
    DO NOTHING;


/*
 * Una única reserva sigue existiendo por
 * requerimiento + prestador.
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
            'notificación para requerimiento % y prestador %.',
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
            || 'posiblemente por otra ejecución concurrente.'
        )::TEXT;

RETURN;

END IF;


    /*
     * Estados reintentables:
     *
     * PENDIENTE
     * ERROR
     * EMAIL_INVALIDO
     *
     * Los destinatarios se recalculan en cada nuevo intento.
     * Esto permite incorporar modificaciones realizadas
     * sobre prestad_contacto_e / contacto_e.
     */
UPDATE compras.requerimiento_cotizacion_prestador
SET
    estado_envio =
        'PROCESANDO',

    intentos =
        intentos + 1,

    email_destino =
        v_emails_reales,

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
    v_emails_reales::TEXT,
    'RESERVA_OTORGADA'::TEXT,
    (
        'La ejecución obtuvo la reserva exclusiva '
            || 'y la fila quedó PROCESANDO.'
        )::TEXT;

END;

$BODY$;

ALTER FUNCTION compras.reservar_notificacion_cotizacion_prestador(
    integer,
    integer,
    character varying
    )
    OWNER TO postgres;

CREATE OR REPLACE FUNCTION compras.listar_prestadores_notificacion_cotizacion(
    p_id_requerimiento integer
)
RETURNS TABLE(
    id_prestador integer,
    descripcion text,
    cuit text,
    email text,
    id_tipo_prestador integer,
    tipo_prestador text
)
LANGUAGE 'sql'
COST 100
STABLE
ROWS 1000
AS $BODY$

SELECT
    candidato.id_prestador::INTEGER,

    candidato.descripcion::TEXT,

    candidato.cuit::TEXT,

        /*
         * El email del candidato original se ignora.
         *
         * Los destinatarios se obtienen exclusivamente
         * desde prestad_contacto_e -> contacto_e.
         */
    compras.resolver_emails_cotizacion_prestador(
            candidato.id_prestador
    )::TEXT AS email,

    candidato.id_tipo_prestador::INTEGER,

    candidato.tipo_prestador::TEXT

FROM compras.listar_prestadores_cotizacion_requerimiento(
             p_id_requerimiento
     ) candidato

ORDER BY
    candidato.descripcion,
    candidato.id_prestador;

$BODY$;

ALTER FUNCTION compras.listar_prestadores_notificacion_cotizacion(integer)
    OWNER TO postgres;