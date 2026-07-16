BEGIN;

CREATE SCHEMA IF NOT EXISTS autorizaciones;

CREATE TABLE IF NOT EXISTS autorizaciones.reclamo_appmobile_outbox (
    id BIGSERIAL PRIMARY KEY,
    id_reclamo INTEGER NOT NULL,
    id_reintegro_app INTEGER NOT NULL,
    estado_destino VARCHAR(20) NOT NULL,
    estado_proceso VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    intentos INTEGER NOT NULL DEFAULT 0,
    proximo_intento TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    bloqueado_hasta TIMESTAMP WITHOUT TIME ZONE NULL,
    ultimo_error VARCHAR(2000) NULL,
    creado_en TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    actualizado_en TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    procesado_en TIMESTAMP WITHOUT TIME ZONE NULL,
    CONSTRAINT ck_reclamo_appmobile_outbox_estado
        CHECK (estado_proceso IN ('PENDIENTE', 'PROCESANDO', 'PROCESADO')),
    CONSTRAINT ck_reclamo_appmobile_outbox_reintegro
        CHECK (id_reintegro_app > 0),
    CONSTRAINT ck_reclamo_appmobile_outbox_intentos
        CHECK (intentos >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS
    ux_reclamo_appmobile_outbox_pendiente
ON autorizaciones.reclamo_appmobile_outbox (
    id_reintegro_app,
    estado_destino
)
WHERE procesado_en IS NULL;

CREATE INDEX IF NOT EXISTS
    ix_reclamo_appmobile_outbox_proceso
ON autorizaciones.reclamo_appmobile_outbox (
    estado_proceso,
    proximo_intento,
    id
);

COMMENT ON TABLE autorizaciones.reclamo_appmobile_outbox IS
    'Outbox durable para sincronizar estados de Reclamos Prestacionales con AppMobile.';

COMMENT ON COLUMN autorizaciones.reclamo_appmobile_outbox.estado_proceso IS
    'PENDIENTE, PROCESANDO o PROCESADO.';

COMMENT ON COLUMN autorizaciones.reclamo_appmobile_outbox.bloqueado_hasta IS
    'Lease temporal para recuperar trabajos abandonados por caída del proceso.';

COMMIT;

-- Verificación posterior:
-- SELECT estado_proceso, count(*)
-- FROM autorizaciones.reclamo_appmobile_outbox
-- GROUP BY estado_proceso;
