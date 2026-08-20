BEGIN;

-- ============================================================
-- PEDIDO DE COTIZACION ENVIADO A PRESTADORES
-- ============================================================

CREATE TABLE compras.requerimiento_pedido_cotizacion (
                                                         id_requerimiento INTEGER NOT NULL,
                                                         id_prestador INTEGER NOT NULL,
                                                         intento INTEGER NOT NULL,

                                                         dl_group_id BIGINT NOT NULL,
                                                         dl_folder_id BIGINT NOT NULL,
                                                         dl_file_entry_id BIGINT NOT NULL,
                                                         dl_file_uuid VARCHAR(75) NOT NULL,

                                                         nombre_original VARCHAR(255) NOT NULL,
                                                         nombre_persistido VARCHAR(255) NOT NULL,
                                                         titulo VARCHAR(240) NOT NULL,

                                                         alta_fecha TIMESTAMP WITHOUT TIME ZONE
        NOT NULL DEFAULT now(),

                                                         alta_usr VARCHAR(100)
                                                             NOT NULL DEFAULT 'sistema',

                                                         CONSTRAINT pk_compras_pedido_cotizacion
                                                             PRIMARY KEY (
                                                                          id_requerimiento,
                                                                          id_prestador,
                                                                          intento
                                                                 ),

                                                         CONSTRAINT fk_compras_pedido_cotizacion_envio
                                                             FOREIGN KEY (
                                                                          id_requerimiento,
                                                                          id_prestador
                                                                 )
                                                                 REFERENCES compras.requerimiento_cotizacion_prestador (
                                                                                                                        id_requerimiento,
                                                                                                                        id_prestador
                                                                     ),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_requerimiento
                                                             CHECK (id_requerimiento > 0),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_prestador
                                                             CHECK (id_prestador > 0),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_intento
                                                             CHECK (intento > 0),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_group
                                                             CHECK (dl_group_id > 0),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_folder
                                                             CHECK (dl_folder_id > 0),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_file_entry
                                                             CHECK (dl_file_entry_id > 0),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_uuid
                                                             CHECK (
                                                                 NULLIF(
                                                                         btrim(dl_file_uuid),
                                                                         ''
                                                                 ) IS NOT NULL
                                                                 ),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_nombre_original
                                                             CHECK (
                                                                 NULLIF(
                                                                         btrim(nombre_original),
                                                                         ''
                                                                 ) IS NOT NULL
                                                                 ),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_nombre_persistido
                                                             CHECK (
                                                                 NULLIF(
                                                                         btrim(nombre_persistido),
                                                                         ''
                                                                 ) IS NOT NULL
                                                                 ),

                                                         CONSTRAINT ck_compras_pedido_cotizacion_titulo
                                                             CHECK (
                                                                 NULLIF(
                                                                         btrim(titulo),
                                                                         ''
                                                                 ) IS NOT NULL
                                                                 )
);

CREATE UNIQUE INDEX
    uq_compras_pedido_cotizacion_file_entry
    ON compras.requerimiento_pedido_cotizacion (
                                                dl_file_entry_id
        );

CREATE INDEX
    ix_compras_pedido_cotizacion_requerimiento
    ON compras.requerimiento_pedido_cotizacion (
                                                id_requerimiento,
                                                id_prestador,
                                                intento DESC
        );


-- ============================================================
-- REGISTRAR DOCUMENTO DEL INTENTO ACTUAL
-- ============================================================

CREATE OR REPLACE FUNCTION
compras.registrar_pedido_cotizacion_documento(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,

    p_dl_group_id BIGINT,
    p_dl_folder_id BIGINT,
    p_dl_file_entry_id BIGINT,
    p_dl_file_uuid VARCHAR,

    p_nombre_original VARCHAR,
    p_nombre_persistido VARCHAR,
    p_titulo VARCHAR,

    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
v_estado_envio VARCHAR(20);
    v_intento INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

    IF p_id_prestador IS NULL
       OR p_id_prestador <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el prestador.';
END IF;

    IF p_dl_group_id IS NULL
       OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL
       OR p_dl_folder_id <= 0
       OR p_dl_file_entry_id IS NULL
       OR p_dl_file_entry_id <= 0
       OR NULLIF(
            btrim(p_dl_file_uuid),
            ''
       ) IS NULL THEN

        RAISE EXCEPTION
            'La identidad del pedido de cotizacion '
            'en Document Library no es valida.';
END IF;

    IF NULLIF(
        btrim(p_nombre_original),
        ''
    ) IS NULL
       OR NULLIF(
        btrim(p_nombre_persistido),
        ''
    ) IS NULL
       OR NULLIF(
        btrim(p_titulo),
        ''
    ) IS NULL THEN

        RAISE EXCEPTION
            'Los datos documentales del pedido '
            'de cotizacion no son validos.';
END IF;

    v_usuario :=
        compras.normalizar_usuario(
            p_usuario
        );

    /*
     * La fila de notificacion es la autoridad del numero
     * de intento. El documento solo puede registrarse
     * mientras esta ejecucion conserva la reserva.
     */
SELECT
    rcp.estado_envio,
    rcp.intentos
INTO
    v_estado_envio,
    v_intento
FROM compras.requerimiento_cotizacion_prestador rcp
WHERE rcp.id_requerimiento =
      p_id_requerimiento
  AND rcp.id_prestador =
      p_id_prestador
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe la notificacion del prestador '
            'para el requerimiento.';
END IF;

    IF v_estado_envio <> 'PROCESANDO' THEN
        RAISE EXCEPTION
            'El pedido de cotizacion solo puede '
            'registrarse durante un envio PROCESANDO.';
END IF;

    IF v_intento IS NULL
       OR v_intento <= 0 THEN

        RAISE EXCEPTION
            'La notificacion no posee un numero '
            'de intento valido.';
END IF;

    /*
     * Idempotencia estricta:
     *
     * Si una repeticion corresponde exactamente al mismo
     * documento del mismo intento, se considera correcta.
     */
    IF EXISTS (
        SELECT 1
        FROM compras.requerimiento_pedido_cotizacion pc
        WHERE pc.id_requerimiento =
              p_id_requerimiento
          AND pc.id_prestador =
              p_id_prestador
          AND pc.intento =
              v_intento
          AND pc.dl_group_id =
              p_dl_group_id
          AND pc.dl_folder_id =
              p_dl_folder_id
          AND pc.dl_file_entry_id =
              p_dl_file_entry_id
          AND pc.dl_file_uuid =
              btrim(p_dl_file_uuid)
          AND pc.nombre_original =
              btrim(p_nombre_original)
          AND pc.nombre_persistido =
              btrim(p_nombre_persistido)
          AND pc.titulo =
              btrim(p_titulo)
    ) THEN

        RETURN v_intento;
END IF;

    IF EXISTS (
        SELECT 1
        FROM compras.requerimiento_pedido_cotizacion pc
        WHERE pc.id_requerimiento =
              p_id_requerimiento
          AND pc.id_prestador =
              p_id_prestador
          AND pc.intento =
              v_intento
    ) THEN

        RAISE EXCEPTION
            'El intento actual ya posee otro '
            'pedido de cotizacion registrado.';
END IF;

INSERT INTO compras.requerimiento_pedido_cotizacion (
    id_requerimiento,
    id_prestador,
    intento,

    dl_group_id,
    dl_folder_id,
    dl_file_entry_id,
    dl_file_uuid,

    nombre_original,
    nombre_persistido,
    titulo,

    alta_usr
)
VALUES (
           p_id_requerimiento,
           p_id_prestador,
           v_intento,

           p_dl_group_id,
           p_dl_folder_id,
           p_dl_file_entry_id,
           btrim(p_dl_file_uuid),

           btrim(p_nombre_original),
           btrim(p_nombre_persistido),
           btrim(p_titulo),

           v_usuario
       );

RETURN v_intento;
END;
$func$
LANGUAGE plpgsql
VOLATILE;


-- ============================================================
-- DOCUMENTO DEL ULTIMO ENVIO EXITOSO
-- ============================================================

CREATE OR REPLACE FUNCTION
compras.get_pedido_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER
)
RETURNS SETOF compras.requerimiento_pedido_cotizacion
AS $func$
SELECT pc.*
FROM compras.requerimiento_pedido_cotizacion pc

         INNER JOIN compras.requerimiento_cotizacion_prestador rcp
                    ON rcp.id_requerimiento =
                       pc.id_requerimiento
                        AND rcp.id_prestador =
                            pc.id_prestador

WHERE pc.id_requerimiento =
      p_id_requerimiento

  AND pc.id_prestador =
      p_id_prestador

    /*
     * La fila visible debe pertenecer exactamente
     * al intento que termino siendo el actual.
     */
  AND pc.intento =
      rcp.intentos

    /*
     * PROCESANDO/ERROR/EMAIL_INVALIDO nunca pueden
     * exponerse como un pedido efectivamente enviado.
     */
  AND rcp.estado_envio IN (
                           'ENVIADO',
                           'COTIZADO'
    )

ORDER BY
    pc.intento DESC

    LIMIT 1;
$func$
LANGUAGE sql
STABLE;

COMMIT;