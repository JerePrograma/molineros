BEGIN;

CREATE TABLE IF NOT EXISTS compras.requerimiento_reclamo_prestacional (
    id_requerimiento INTEGER NOT NULL,
    id_reclamo_prestacional INTEGER,
    estado VARCHAR(20) NOT NULL,
    token_reserva VARCHAR(64),
    reserva_fecha TIMESTAMP WITHOUT TIME ZONE,
    ultimo_error TEXT,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),

    CONSTRAINT pk_compras_requerimiento_reclamo
        PRIMARY KEY (id_requerimiento),

    CONSTRAINT fk_compras_requerimiento_reclamo_req
        FOREIGN KEY (id_requerimiento)
        REFERENCES compras.requerimiento (id_requerimiento),

    CONSTRAINT ck_compras_requerimiento_reclamo_estado
        CHECK (estado IN ('RESERVADO', 'VINCULADO', 'ERROR')),

    CONSTRAINT ck_compras_requerimiento_reclamo_datos
        CHECK (
            (estado = 'RESERVADO'
                AND id_reclamo_prestacional IS NULL
                AND NULLIF(btrim(token_reserva), '') IS NOT NULL)
            OR
            (estado IN ('VINCULADO', 'ERROR')
                AND id_reclamo_prestacional IS NOT NULL)
        )
);

CREATE UNIQUE INDEX IF NOT EXISTS
    ux_compras_requerimiento_reclamo_id_reclamo
    ON compras.requerimiento_reclamo_prestacional (
        id_reclamo_prestacional
    )
    WHERE id_reclamo_prestacional IS NOT NULL;

CREATE INDEX IF NOT EXISTS
    ix_compras_requerimiento_reclamo_estado
    ON compras.requerimiento_reclamo_prestacional (
        estado,
        reserva_fecha
    );

COMMENT ON TABLE compras.requerimiento_reclamo_prestacional IS
    'Relacion uno a uno entre un requerimiento COTIZADO y su Reclamo Prestacional.';

COMMENT ON COLUMN compras.requerimiento_reclamo_prestacional.id_reclamo_prestacional IS
    'Identificador externo del esquema autorizaciones. No se declara FK cruzada para preservar el ciclo de vida legacy.';

CREATE OR REPLACE FUNCTION compras.get_requerimiento_reclamo_prestacional(
    p_id_requerimiento INTEGER
)
RETURNS TABLE (
    id_requerimiento INTEGER,
    id_reclamo_prestacional INTEGER,
    estado VARCHAR,
    token_reserva VARCHAR,
    reserva_fecha TIMESTAMP WITHOUT TIME ZONE,
    ultimo_error TEXT,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE,
    alta_usr VARCHAR,
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR
)
AS $func$
BEGIN
    RETURN QUERY
    SELECT
        rr.id_requerimiento,
        rr.id_reclamo_prestacional,
        rr.estado,
        rr.token_reserva,
        rr.reserva_fecha,
        rr.ultimo_error,
        rr.alta_fecha,
        rr.alta_usr,
        rr.modi_fecha,
        rr.modi_usr
    FROM compras.requerimiento_reclamo_prestacional rr
    WHERE rr.id_requerimiento = p_id_requerimiento;
END;
$func$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION compras.reservar_reclamo_prestacional(
    p_id_requerimiento INTEGER,
    p_token_reserva VARCHAR,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_estado_requerimiento INTEGER;
    v_baja_fecha TIMESTAMP WITHOUT TIME ZONE;
    v_afiliado_cuil VARCHAR(20);
    v_afiliado_int INTEGER;
    v_estado_vinculo VARCHAR(20);
    v_token_actual VARCHAR(64);
    v_id_reclamo INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    IF NULLIF(btrim(p_token_reserva), '') IS NULL THEN
        RAISE EXCEPTION
            'No se pudo validar el contexto de creacion del Reclamo Prestacional.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT
        r.estado,
        r.baja_fecha,
        r.afiliado_cuil_titular,
        r.afiliado_int
    INTO
        v_estado_requerimiento,
        v_baja_fecha,
        v_afiliado_cuil,
        v_afiliado_int
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
    FOR UPDATE;

    IF NOT FOUND OR v_baja_fecha IS NOT NULL THEN
        RAISE EXCEPTION
            'No existe un requerimiento de compra activo con id %.',
            p_id_requerimiento;
    END IF;

    IF v_estado_requerimiento <> 3 THEN
        RAISE EXCEPTION
            'El requerimiento % no se encuentra COTIZADO.',
            p_id_requerimiento;
    END IF;

    IF NULLIF(btrim(v_afiliado_cuil), '') IS NULL
            OR v_afiliado_int IS NULL THEN
        RAISE EXCEPTION
            'El requerimiento % no posee un afiliado valido.',
            p_id_requerimiento;
    END IF;

    INSERT INTO compras.requerimiento_reclamo_prestacional (
        id_requerimiento,
        estado,
        token_reserva,
        reserva_fecha,
        alta_fecha,
        alta_usr
    )
    VALUES (
        p_id_requerimiento,
        'RESERVADO',
        btrim(p_token_reserva),
        now(),
        now(),
        v_usuario
    )
    ON CONFLICT (id_requerimiento) DO NOTHING;

    IF FOUND THEN
        RETURN TRUE;
    END IF;

    SELECT
        rr.estado,
        rr.token_reserva,
        rr.id_reclamo_prestacional
    INTO
        v_estado_vinculo,
        v_token_actual,
        v_id_reclamo
    FROM compras.requerimiento_reclamo_prestacional rr
    WHERE rr.id_requerimiento = p_id_requerimiento
    FOR UPDATE;

    IF v_estado_vinculo = 'VINCULADO' THEN
        RAISE EXCEPTION
            'El requerimiento % ya posee el Reclamo Prestacional %.',
            p_id_requerimiento,
            v_id_reclamo;
    END IF;

    IF v_estado_vinculo = 'ERROR' THEN
        RAISE EXCEPTION
            'El requerimiento % posee el Reclamo Prestacional % pendiente de reconciliacion.',
            p_id_requerimiento,
            v_id_reclamo;
    END IF;

    RAISE EXCEPTION
        'Ya existe una creacion de Reclamo Prestacional en proceso para el requerimiento %.',
        p_id_requerimiento;
END;
$func$
LANGUAGE plpgsql
VOLATILE;

CREATE OR REPLACE FUNCTION compras.finalizar_reclamo_prestacional(
    p_id_requerimiento INTEGER,
    p_token_reserva VARCHAR,
    p_id_reclamo_prestacional INTEGER,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_estado VARCHAR(20);
    v_token_actual VARCHAR(64);
    v_id_reclamo_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_reclamo_prestacional IS NULL
            OR p_id_reclamo_prestacional <= 0 THEN
        RAISE EXCEPTION
            'Debe informar el Reclamo Prestacional creado.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT
        rr.estado,
        rr.token_reserva,
        rr.id_reclamo_prestacional
    INTO
        v_estado,
        v_token_actual,
        v_id_reclamo_actual
    FROM compras.requerimiento_reclamo_prestacional rr
    WHERE rr.id_requerimiento = p_id_requerimiento
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe una reserva para el requerimiento %.',
            p_id_requerimiento;
    END IF;

    IF v_estado = 'VINCULADO'
            AND v_id_reclamo_actual = p_id_reclamo_prestacional THEN
        RETURN TRUE;
    END IF;

    IF v_estado <> 'RESERVADO'
            OR v_token_actual IS DISTINCT FROM btrim(p_token_reserva) THEN
        RAISE EXCEPTION
            'La reserva del requerimiento % no es valida.',
            p_id_requerimiento;
    END IF;

    UPDATE compras.requerimiento_reclamo_prestacional
       SET id_reclamo_prestacional = p_id_reclamo_prestacional,
           estado = 'VINCULADO',
           token_reserva = NULL,
           ultimo_error = NULL,
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento;

    RETURN TRUE;
END;
$func$
LANGUAGE plpgsql
VOLATILE;

CREATE OR REPLACE FUNCTION compras.liberar_reserva_reclamo_prestacional(
    p_id_requerimiento INTEGER,
    p_token_reserva VARCHAR,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
BEGIN
    DELETE FROM compras.requerimiento_reclamo_prestacional
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = 'RESERVADO'
       AND id_reclamo_prestacional IS NULL
       AND token_reserva = btrim(p_token_reserva);

    RETURN FOUND;
END;
$func$
LANGUAGE plpgsql
VOLATILE;

CREATE OR REPLACE FUNCTION compras.marcar_error_reclamo_prestacional(
    p_id_requerimiento INTEGER,
    p_token_reserva VARCHAR,
    p_id_reclamo_prestacional INTEGER,
    p_error TEXT,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_usuario VARCHAR(100);
    v_estado VARCHAR(20);
    v_id_reclamo_actual INTEGER;
BEGIN
    IF p_id_reclamo_prestacional IS NULL
            OR p_id_reclamo_prestacional <= 0 THEN
        RAISE EXCEPTION
            'Debe informar el Reclamo Prestacional creado.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    UPDATE compras.requerimiento_reclamo_prestacional
       SET id_reclamo_prestacional = p_id_reclamo_prestacional,
           estado = 'ERROR',
           ultimo_error = left(
               COALESCE(
                   NULLIF(btrim(p_error), ''),
                   'Error de vinculacion no especificado.'
               ),
               2000
           ),
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = 'RESERVADO'
       AND token_reserva = btrim(p_token_reserva);

    IF FOUND THEN
        RETURN TRUE;
    END IF;

    SELECT
        rr.estado,
        rr.id_reclamo_prestacional
    INTO
        v_estado,
        v_id_reclamo_actual
    FROM compras.requerimiento_reclamo_prestacional rr
    WHERE rr.id_requerimiento = p_id_requerimiento;

    RETURN v_estado = 'ERROR'
       AND v_id_reclamo_actual = p_id_reclamo_prestacional;
END;
$func$
LANGUAGE plpgsql
VOLATILE;

COMMIT;
