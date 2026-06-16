BEGIN;

CREATE TABLE IF NOT EXISTS compras.sector_tipo_prestador (
    id_sector INTEGER NOT NULL,
    id_tipo_prestador INTEGER NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(75),

    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(75),

    CONSTRAINT pk_sector_tipo_prestador
        PRIMARY KEY (id_sector, id_tipo_prestador)
);

CREATE INDEX IF NOT EXISTS idx_sector_tipo_prestador_sector_activo
    ON compras.sector_tipo_prestador (id_sector, activo);

CREATE INDEX IF NOT EXISTS idx_sector_tipo_prestador_tipo_activo
    ON compras.sector_tipo_prestador (id_tipo_prestador, activo);

CREATE TABLE IF NOT EXISTS compras.requerimiento_cotizacion_prestador (
    id_requerimiento INTEGER NOT NULL,
    id_sector INTEGER NOT NULL,
    id_prestador INTEGER NOT NULL,
    email_destino VARCHAR(320),
    estado_envio VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    intentos INTEGER NOT NULL DEFAULT 0,
    ultimo_intento_fecha TIMESTAMP WITHOUT TIME ZONE,
    enviado_fecha TIMESTAMP WITHOUT TIME ZONE,
    ultimo_error TEXT,
    alta_usr VARCHAR(75) NOT NULL DEFAULT 'sistema',
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),

    CONSTRAINT pk_requerimiento_cotizacion_prestador
        PRIMARY KEY (id_requerimiento, id_prestador),

    CONSTRAINT fk_req_cotizacion_requerimiento
        FOREIGN KEY (id_requerimiento)
        REFERENCES compras.requerimientos (id),

    CONSTRAINT fk_req_cotizacion_sector
        FOREIGN KEY (id_sector)
        REFERENCES compras.sector_requerimiento (id),

    CONSTRAINT fk_req_cotizacion_prestador
        FOREIGN KEY (id_prestador)
        REFERENCES public.prestador (id_prestador),

    CONSTRAINT ck_req_cotizacion_estado
        CHECK (estado_envio IN (
            'PENDIENTE',
            'PROCESANDO',
            'ENVIADO',
            'ERROR',
            'EMAIL_INVALIDO'
        )),

    CONSTRAINT ck_req_cotizacion_intentos
        CHECK (intentos >= 0),

    CONSTRAINT ck_req_cotizacion_enviado_fecha
        CHECK (
            estado_envio <> 'ENVIADO'
            OR enviado_fecha IS NOT NULL
        )
);

CREATE INDEX IF NOT EXISTS idx_req_cotizacion_sector
    ON compras.requerimiento_cotizacion_prestador (id_sector);

CREATE INDEX IF NOT EXISTS idx_req_cotizacion_prestador
    ON compras.requerimiento_cotizacion_prestador (id_prestador);

CREATE INDEX IF NOT EXISTS idx_req_cotizacion_fecha
    ON compras.requerimiento_cotizacion_prestador (alta_fecha);

CREATE INDEX IF NOT EXISTS idx_req_cotizacion_req_estado
    ON compras.requerimiento_cotizacion_prestador (
        id_requerimiento,
        estado_envio
    );

/*
 * Devuelve solamente prestadores habilitados para el sector del requerimiento.
 *
 * Tambien permite recuperar envios fallidos o tomas abandonadas. El limite de
 * tres intentos evita que un error permanente dispare correos indefinidamente.
 */
CREATE OR REPLACE FUNCTION compras.listar_prestadores_cotizacion_requerimiento(
    p_id_requerimiento_compra INTEGER
)
RETURNS TABLE (
    id_prestador INTEGER,
    descripcion VARCHAR,
    cuit VARCHAR,
    email VARCHAR,
    id_tipo_prestador INTEGER,
    tipo_prestador VARCHAR
)
AS $$
BEGIN
    RETURN QUERY
    SELECT DISTINCT
        p.id_prestador,
        p.descripcion::VARCHAR,
        p.cuit::VARCHAR,
        NULLIF(trim(COALESCE(p.contacto, '')), '')::VARCHAR AS email,
        p.id_tipo_prestador,
        tp.descripcion::VARCHAR AS tipo_prestador
    FROM compras.requerimientos r

    JOIN compras.sector_tipo_prestador stp
      ON stp.id_sector = r.id_sector
     AND stp.activo = TRUE

    JOIN public.prestador p
      ON p.id_tipo_prestador = stp.id_tipo_prestador

    JOIN trae_tipos_prestadores() tp
      ON tp.id_tipo_prestador = p.id_tipo_prestador

    LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
      ON rcp.id_requerimiento = r.id
     AND rcp.id_prestador = p.id_prestador

    WHERE r.id = p_id_requerimiento_compra
      AND r.baja_fecha IS NULL
      AND COALESCE(p.solicitar_cotizacion, FALSE) = TRUE
      AND p.baja_fecha IS NULL
      AND COALESCE(rcp.intentos, 0) < 3
      AND (
            rcp.id_prestador IS NULL
         OR rcp.estado_envio IN ('ERROR', 'EMAIL_INVALIDO')
         OR (
                rcp.estado_envio = 'PENDIENTE'
            AND rcp.alta_fecha <= now() - INTERVAL '5 minutes'
         )
         OR (
                rcp.estado_envio = 'PROCESANDO'
            AND rcp.ultimo_intento_fecha IS NOT NULL
            AND rcp.ultimo_intento_fecha <= now() - INTERVAL '30 minutes'
         )
      )

    ORDER BY
        tp.descripcion,
        p.descripcion;
END;
$$ LANGUAGE plpgsql;

/*
 * Reserva el envio. La PK impide que dos solicitudes simultaneas creen dos
 * registros. Solo rehabilita errores o trabajos claramente abandonados.
 */
CREATE OR REPLACE FUNCTION compras.registrar_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,
    p_usuario VARCHAR
)
RETURNS BOOLEAN AS $$
DECLARE
    v_id_sector INTEGER;
    v_email_destino VARCHAR(320);
    v_usuario VARCHAR(75);
    v_insertados INTEGER;
BEGIN
    v_usuario := COALESCE(NULLIF(trim(p_usuario), ''), 'sistema');

    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento.';
    END IF;

    IF p_id_prestador IS NULL OR p_id_prestador <= 0 THEN
        RAISE EXCEPTION 'Debe informar el prestador.';
    END IF;

    SELECT r.id_sector
      INTO v_id_sector
      FROM compras.requerimientos r
     WHERE r.id = p_id_requerimiento
       AND r.baja_fecha IS NULL;

    IF v_id_sector IS NULL THEN
        RAISE EXCEPTION 'No se encontro el requerimiento informado.';
    END IF;

    SELECT NULLIF(trim(COALESCE(p.contacto, '')), '')::VARCHAR
      INTO v_email_destino
      FROM public.prestador p
      JOIN compras.sector_tipo_prestador stp
        ON stp.id_sector = v_id_sector
       AND stp.id_tipo_prestador = p.id_tipo_prestador
       AND stp.activo = TRUE
     WHERE p.id_prestador = p_id_prestador
       AND p.baja_fecha IS NULL
       AND COALESCE(p.solicitar_cotizacion, FALSE) = TRUE;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    INSERT INTO compras.requerimiento_cotizacion_prestador (
        id_requerimiento,
        id_sector,
        id_prestador,
        email_destino,
        estado_envio,
        alta_usr
    ) VALUES (
        p_id_requerimiento,
        v_id_sector,
        p_id_prestador,
        v_email_destino,
        'PENDIENTE',
        v_usuario
    )
    ON CONFLICT (id_requerimiento, id_prestador)
    DO UPDATE SET
        id_sector = EXCLUDED.id_sector,
        email_destino = EXCLUDED.email_destino,
        estado_envio = 'PENDIENTE',
        enviado_fecha = NULL,
        ultimo_error = NULL
    WHERE compras.requerimiento_cotizacion_prestador.intentos < 3
      AND (
            compras.requerimiento_cotizacion_prestador.estado_envio
                IN ('ERROR', 'EMAIL_INVALIDO')
         OR (
                compras.requerimiento_cotizacion_prestador.estado_envio = 'PENDIENTE'
            AND compras.requerimiento_cotizacion_prestador.alta_fecha
                <= now() - INTERVAL '5 minutes'
         )
         OR (
                compras.requerimiento_cotizacion_prestador.estado_envio = 'PROCESANDO'
            AND compras.requerimiento_cotizacion_prestador.ultimo_intento_fecha IS NOT NULL
            AND compras.requerimiento_cotizacion_prestador.ultimo_intento_fecha
                <= now() - INTERVAL '30 minutes'
         )
      );

    GET DIAGNOSTICS v_insertados = ROW_COUNT;

    RETURN v_insertados > 0;
END;
$$ LANGUAGE plpgsql;

COMMIT;

/*
 * Verificacion operativa sugerida:
 *
 * SELECT id_requerimiento,
 *        id_prestador,
 *        email_destino,
 *        estado_envio,
 *        intentos,
 *        ultimo_intento_fecha,
 *        enviado_fecha,
 *        ultimo_error
 *   FROM compras.requerimiento_cotizacion_prestador
 *  WHERE id_requerimiento = :id_requerimiento
 *  ORDER BY id_prestador;
 */
