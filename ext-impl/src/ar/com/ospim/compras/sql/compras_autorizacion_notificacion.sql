-- Reserva exclusiva del correo de autorizacion, independiente del estado de compra.
-- Adaptado de requerimiento_cotizacion_prestador y su INSERT ON CONFLICT.
-- Aplicar antes del codigo de recuperacion del correo. PostgreSQL 9.6.
-- No infiere envios historicos ni modifica requerimientos o prestadores.
BEGIN;

CREATE TABLE IF NOT EXISTS compras.requerimiento_autorizacion_notificacion (
    id_requerimiento INTEGER PRIMARY KEY
        REFERENCES compras.requerimiento (id_requerimiento),
    token VARCHAR(36) NOT NULL,
    estado_envio VARCHAR(10) NOT NULL
        CHECK (estado_envio IN ('ENVIANDO', 'ENVIADO', 'INCIERTO')),
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    alta_usr VARCHAR(255) NOT NULL,
    fecha_resultado TIMESTAMP WITHOUT TIME ZONE,
    CHECK ((estado_envio = 'ENVIANDO' AND fecha_resultado IS NULL)
        OR (estado_envio IN ('ENVIADO', 'INCIERTO') AND fecha_resultado IS NOT NULL))
);

-- Los registros previos conservan resultado desconocido (NULL).
-- INCIERTO mantiene bloqueada la reserva aunque el resultado conocido sea parcial o rechazo.
ALTER TABLE compras.requerimiento_autorizacion_notificacion
    ADD COLUMN IF NOT EXISTS resultado_smtp VARCHAR(10);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'compras.requerimiento_autorizacion_notificacion'::regclass
          AND conname = 'requerimiento_autorizacion_resultado_smtp_ck'
    ) THEN
        ALTER TABLE compras.requerimiento_autorizacion_notificacion
            ADD CONSTRAINT requerimiento_autorizacion_resultado_smtp_ck
            CHECK (resultado_smtp IS NULL
                OR (estado_envio = 'ENVIADO' AND resultado_smtp = 'ACEPTADO')
                OR (estado_envio = 'INCIERTO'
                    AND resultado_smtp IN ('RECHAZADO', 'PARCIAL', 'INCIERTO')));
    END IF;
END;
$$;

COMMIT;
