-- =====================================================================
-- MÓDULO: Compras - instalación canónica desde cero
-- PostgreSQL 9.6+
--
-- INSTALACIÓN COMPLETA:
--   Requiere que el esquema compras no exista y lo crea en una transacción.
--   NO es una migracion. NUNCA ejecutar si el schema compras ya existe.
--   Bases existentes: 20260923_refactor_compras_fase1_expand.sql; cleanup separado.
--   Las funciones externas y la configuracion global deben existir previamente.
--
-- Flujo funcional activo:
--   1  PENDIENTE
--   2  A_COTIZAR
--   3  COTIZADO
--   4  RECLAMO_RP
--   5  ORDEN_COMPRA (directa para RRHH/SISTEMAS con cotizacion de Empresa)
--
-- Estado lateral:
--   99 ANULADO
--
-- Contratos incorporados:
--   - guardar_requerimiento con 23 argumentos de entrada.
--   - afiliado actual por CUIL titular e integrante; cargo OSPIM y recupero derivados.
--   - persistencia de surge como cabecera del requerimiento.
--   - PDF con afiliado_id_ospim, integrante y documento.
--   - destinatario de cotización persistido por prestador.
--   - un presupuesto activo por requerimiento y prestador.
--   - estado individual COTIZADO mientras el presupuesto permanece activo.
--   - borrado lógico de detalles PENDIENTES.
--   - guardado atómico de cotización y cierre a COTIZADO.
--
-- Dependencias externas de solo lectura:
--   public.prestador
--   public.prestador_rubro
--   public.prestad_contacto_e
--   public.contacto_e
--   public.afi_situ_medica
--   informacion_afip.empresa
--   trae_tipos_prestadores()
--   autorizaciones.nomenclador
--   autorizaciones.nomenclador_detalle
--   autorizaciones.busca_nomenclador(...)
--   autorizaciones.busca_nomenclador_prest_med_compras(...) (existente, no se instala)
--
-- Ejecutar con psql -X -v ON_ERROR_STOP=1.
-- Si la sesión está abortada, ejecutar ROLLBACK antes de este archivo.
-- =====================================================================


BEGIN;

CREATE SCHEMA compras;

CREATE FUNCTION compras.buscar_empresas_cotizacion(
    p_cuit VARCHAR,
    p_descripcion VARCHAR,
    p_sucursal VARCHAR,
    p_limite INTEGER
)
RETURNS TABLE (
    cuit VARCHAR,
    sucursal VARCHAR,
    razon_soc VARCHAR
)
AS $func$
    SELECT
        btrim(e.cuit)::VARCHAR,
        btrim(e.sucursal)::VARCHAR,
        btrim(e.razon_soc)::VARCHAR
    FROM informacion_afip.empresa e
    WHERE e.baja_fecha IS NULL
      AND NULLIF(btrim(e.cuit), '') IS NOT NULL
      AND length(btrim(e.cuit)) <= 11
      AND NULLIF(btrim(e.sucursal), '') IS NOT NULL
      AND length(btrim(e.sucursal)) <= 6
      AND NULLIF(btrim(e.razon_soc), '') IS NOT NULL
      AND (
            NULLIF(btrim($1), '') IS NULL
            OR btrim(e.cuit) = btrim($1)
          )
      AND (
            NULLIF(btrim($2), '') IS NULL
            OR upper(e.razon_soc)
                LIKE '%' || upper(btrim($2)) || '%'
          )
      AND (
            NULLIF(btrim($3), '') IS NULL
            OR btrim(e.sucursal) = btrim($3)
          )
    ORDER BY
        e.razon_soc,
        e.cuit,
        e.sucursal
    LIMIT CASE
        WHEN COALESCE($4, 0) <= 0 THEN 100
        ELSE LEAST($4, 100)
    END;
$func$
LANGUAGE sql
STABLE;


CREATE FUNCTION compras.buscar_empresas_cotizacion_rapida(
    p_cuit VARCHAR,
    p_descripcion VARCHAR,
    p_sucursal VARCHAR,
    p_limite INTEGER
)
RETURNS TABLE (
    cuit VARCHAR,
    sucursal VARCHAR,
    razon_soc VARCHAR
)
AS $func$
DECLARE
    v_cuit VARCHAR := NULLIF(btrim(p_cuit), '');
    v_descripcion VARCHAR := NULLIF(btrim(p_descripcion), '');
    v_sucursal VARCHAR := NULLIF(btrim(p_sucursal), '');
    v_limite INTEGER := CASE
        WHEN COALESCE(p_limite, 0) <= 0 THEN 101
        ELSE LEAST(p_limite, 101)
    END;
BEGIN
    IF v_cuit IS NULL
       AND (
            v_descripcion IS NULL
            OR length(v_descripcion) < 3
       ) THEN

        RETURN;
    END IF;

    IF v_cuit IS NOT NULL
       AND (
            length(v_cuit) <> 11
            OR v_cuit !~ '^[0-9]{11}$'
       ) THEN

        RETURN;
    END IF;

    IF v_cuit IS NOT NULL THEN
        RETURN QUERY
        SELECT
            q.cuit,
            q.sucursal,
            q.razon_soc
        FROM (
            SELECT
                btrim(e.cuit)::VARCHAR AS cuit,
                btrim(e.sucursal)::VARCHAR AS sucursal,
                btrim(e.razon_soc)::VARCHAR AS razon_soc
            FROM informacion_afip.empresa e
            WHERE e.baja_fecha IS NULL
              AND e.cuit = v_cuit
              AND (
                    v_sucursal IS NULL
                    OR e.sucursal = v_sucursal
                  )
              AND (
                    v_descripcion IS NULL
                    OR upper(e.razon_soc)
                        LIKE '%' || upper(v_descripcion) || '%'
                  )
              AND NULLIF(btrim(e.cuit), '') IS NOT NULL
              AND length(btrim(e.cuit)) <= 11
              AND NULLIF(btrim(e.sucursal), '') IS NOT NULL
              AND length(btrim(e.sucursal)) <= 6
              AND NULLIF(btrim(e.razon_soc), '') IS NOT NULL
            ORDER BY e.cuit, e.sucursal
            LIMIT v_limite
        ) q
        ORDER BY q.razon_soc, q.cuit, q.sucursal;

        RETURN;
    END IF;

    RETURN QUERY
    SELECT
        q.cuit,
        q.sucursal,
        q.razon_soc
    FROM (
        SELECT
            btrim(e.cuit)::VARCHAR AS cuit,
            btrim(e.sucursal)::VARCHAR AS sucursal,
            btrim(e.razon_soc)::VARCHAR AS razon_soc
        FROM informacion_afip.empresa e
        WHERE e.baja_fecha IS NULL
          AND upper(e.razon_soc)
                LIKE '%' || upper(v_descripcion) || '%'
          AND (
                v_sucursal IS NULL
                OR btrim(e.sucursal) = v_sucursal
              )
          AND NULLIF(btrim(e.cuit), '') IS NOT NULL
          AND length(btrim(e.cuit)) <= 11
          AND NULLIF(btrim(e.sucursal), '') IS NOT NULL
          AND length(btrim(e.sucursal)) <= 6
          AND NULLIF(btrim(e.razon_soc), '') IS NOT NULL
        -- El subconjunto se corta sin orden interno por rendimiento.
        -- El orden exterior solo estabiliza la presentacion de esas filas.
        LIMIT v_limite
    ) q
    ORDER BY q.razon_soc, q.cuit, q.sucursal;
END;
$func$
LANGUAGE plpgsql
STABLE;

-- =====================================================================
-- TABLAS
-- =====================================================================

CREATE TABLE compras.sector_requerimiento (
    id_sector SERIAL PRIMARY KEY,
    descripcion VARCHAR(120) NOT NULL,
    requiere_afiliado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),
    baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    baja_usr VARCHAR(100)
);

CREATE UNIQUE INDEX uq_compras_sector_descripcion_activo
    ON compras.sector_requerimiento (lower(btrim(descripcion)))
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_sector_activo
    ON compras.sector_requerimiento (activo)
    WHERE baja_fecha IS NULL;


CREATE TABLE compras.tipo_prestacion (
    id_tipo_prestacion SMALLINT PRIMARY KEY,
    descripcion VARCHAR(120) NOT NULL,
    id_sector INTEGER NOT NULL
        REFERENCES compras.sector_requerimiento (id_sector),
    CONSTRAINT uq_compras_tipo_prestacion_descripcion
        UNIQUE (descripcion)
);


CREATE TABLE compras.requerimiento (
    id_requerimiento SERIAL PRIMARY KEY,
    estado INTEGER NOT NULL DEFAULT 1,
    id_sector INTEGER NOT NULL
                                           REFERENCES compras.sector_requerimiento (id_sector),
    afiliado_cuil_titular VARCHAR(20),
    afiliado_int INTEGER,
    cargo_tercerizadora INTEGER NOT NULL DEFAULT 0,
    id_tercerizadora VARCHAR(40),
    surge BOOLEAN NOT NULL DEFAULT FALSE,
    legales BOOLEAN NOT NULL DEFAULT FALSE,
    observaciones TEXT,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),
    baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    baja_usr VARCHAR(100),
    motivo_baja TEXT
);

CREATE INDEX ix_compras_requerimiento_estado
    ON compras.requerimiento (estado, id_requerimiento DESC);

CREATE INDEX ix_compras_requerimiento_sector
    ON compras.requerimiento (id_sector, id_requerimiento DESC)
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_requerimiento_afiliado
    ON compras.requerimiento (
                              afiliado_cuil_titular,
                              afiliado_int
        )
    WHERE baja_fecha IS NULL;

;

CREATE INDEX ix_compras_requerimiento_tercerizadora
    ON compras.requerimiento (id_tercerizadora)
    WHERE baja_fecha IS NULL
      AND id_tercerizadora IS NOT NULL;

CREATE INDEX ix_compras_requerimiento_alta
    ON compras.requerimiento (alta_fecha DESC);


CREATE TABLE compras.requerimiento_cotizacion_prestador (
    id_requerimiento INTEGER NOT NULL
                                                                REFERENCES compras.requerimiento (id_requerimiento),
    -- Identificador externo. Sin FK: el módulo no administra otros esquemas.
                                                            id_prestador INTEGER NOT NULL,
    estado_envio VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    intentos INTEGER NOT NULL DEFAULT 0,
    email_destino TEXT,
    fecha_creacion TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    fecha_ultimo_intento TIMESTAMP WITHOUT TIME ZONE,
    fecha_envio TIMESTAMP WITHOUT TIME ZONE,
    ultimo_error TEXT,
    alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),
    CONSTRAINT pk_compras_requerimiento_cotizacion_prestador
                                                                PRIMARY KEY (id_requerimiento, id_prestador)
);
CREATE INDEX ix_compras_cotizacion_requerimiento_estado
    ON compras.requerimiento_cotizacion_prestador (
                                                   id_requerimiento,
                                                   estado_envio
        );

CREATE INDEX ix_compras_cotizacion_prestador
    ON compras.requerimiento_cotizacion_prestador (id_prestador);

CREATE INDEX ix_compras_cotizacion_fecha
    ON compras.requerimiento_cotizacion_prestador (fecha_creacion DESC);

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
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
    CONSTRAINT pk_compras_pedido_cotizacion
        PRIMARY KEY (id_requerimiento, id_prestador, intento),
    CONSTRAINT fk_compras_pedido_cotizacion_envio
        FOREIGN KEY (id_requerimiento, id_prestador)
        REFERENCES compras.requerimiento_cotizacion_prestador (
            id_requerimiento,
            id_prestador
        )
);

CREATE UNIQUE INDEX uq_compras_pedido_cotizacion_file_entry
    ON compras.requerimiento_pedido_cotizacion (dl_file_entry_id);

CREATE INDEX ix_compras_pedido_cotizacion_requerimiento
    ON compras.requerimiento_pedido_cotizacion (
        id_requerimiento,
        id_prestador,
        intento DESC
    );

CREATE TABLE compras.requerimiento_presupuesto (
    id_requerimiento_presupuesto SERIAL PRIMARY KEY,
    id_requerimiento INTEGER NOT NULL
                                                       REFERENCES compras.requerimiento (id_requerimiento),
    tipo_documento SMALLINT NOT NULL DEFAULT 1,
    fecha_documento DATE,
    numero_receta VARCHAR(100),
    /*
     * Identificador externo.
     * No agregar FK porque public.prestador pertenece a otro modelo.
     */
                                                   id_prestador INTEGER,
    /*
     * Identidad funcional y snapshot de la Empresa para tipo 3.
     * No agregar FK porque informacion_afip.empresa pertenece a otro modelo.
     */
                                                   empresa_cuit VARCHAR(11),
    empresa_sucursal VARCHAR(6),
    /*
     * Identidad exacta del documento en Liferay Document Library.
     */
                                                   dl_group_id BIGINT NOT NULL,
    dl_folder_id BIGINT NOT NULL,
    dl_file_entry_id BIGINT NOT NULL,
    dl_file_uuid VARCHAR(75),
    /*
     * Snapshot del documento y del prestador al momento de cargarlo.
     */
                                                   nombre_original VARCHAR(255) NOT NULL,
    nombre_persistido VARCHAR(255) NOT NULL,
    titulo VARCHAR(240) NOT NULL,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
    baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    baja_usr VARCHAR(100),
    CONSTRAINT uq_compras_presupuesto_dl_file_entry
                                                       UNIQUE (dl_file_entry_id)
);

CREATE INDEX ix_compras_presupuesto_requerimiento_activo
    ON compras.requerimiento_presupuesto (
                                          id_requerimiento,
                                          id_requerimiento_presupuesto
        )
    WHERE baja_fecha IS NULL;

CREATE UNIQUE INDEX ux_compras_presupuesto_requerimiento_prestador_activo
    ON compras.requerimiento_presupuesto (
        id_requerimiento,
        id_prestador
    )
    WHERE baja_fecha IS NULL
      AND tipo_documento = 1;

CREATE UNIQUE INDEX ux_compras_presupuesto_requerimiento_empresa_activa
    ON compras.requerimiento_presupuesto (
        id_requerimiento,
        empresa_cuit,
        empresa_sucursal
    )
    WHERE baja_fecha IS NULL
      AND tipo_documento = 3;

CREATE INDEX ix_compras_orden_medica_receta_fecha_activa
    ON compras.requerimiento_presupuesto (
        numero_receta,
        fecha_documento,
        id_requerimiento
    )
    WHERE baja_fecha IS NULL
      AND tipo_documento = 2
      AND numero_receta IS NOT NULL;

CREATE INDEX ix_compras_presupuesto_folder_name
    ON compras.requerimiento_presupuesto (
                                          dl_folder_id,
                                          nombre_persistido
        );

CREATE TABLE compras.requerimiento_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_requerimiento INTEGER NOT NULL
                                                   REFERENCES compras.requerimiento (id_requerimiento),
    tipo_item VARCHAR(20) NOT NULL,
    id_tipo_prestacion SMALLINT
                                                   REFERENCES compras.tipo_prestacion (id_tipo_prestacion),
    id_prestacion INTEGER,
    id_medicamento INTEGER,
    troquel INTEGER,
    nombre_medicamento VARCHAR(500),
    cantidad INTEGER NOT NULL,
    observaciones TEXT,
    precio_unitario_estimado NUMERIC(18, 2),
    precio_total_estimado NUMERIC(18, 2),
    -- Identificador externo. Sin FK: el módulo no administra otros esquemas.
                                               id_prestador INTEGER,
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),
    baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    baja_usr VARCHAR(100)
);

CREATE INDEX ix_compras_detalle_requerimiento
    ON compras.requerimiento_detalle (
                                      id_requerimiento,
                                      id_detalle
        )
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_detalle_tipo_item
    ON compras.requerimiento_detalle (
                                      tipo_item
        )
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_detalle_tipo_prestacion
    ON compras.requerimiento_detalle (id_tipo_prestacion)
    WHERE baja_fecha IS NULL;

CREATE INDEX ix_compras_detalle_prestacion
    ON compras.requerimiento_detalle (
                                      id_prestacion
        )
    WHERE baja_fecha IS NULL
      AND id_prestacion IS NOT NULL;

CREATE INDEX ix_compras_detalle_medicamento
    ON compras.requerimiento_detalle (
                                      id_medicamento
        )
    WHERE baja_fecha IS NULL
      AND id_medicamento IS NOT NULL;

CREATE INDEX ix_compras_detalle_prestador
    ON compras.requerimiento_detalle (
                                      id_prestador
        )
    WHERE baja_fecha IS NULL
      AND id_prestador IS NOT NULL;

CREATE TABLE compras.requerimiento_reclamo_prestacional (
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
        REFERENCES compras.requerimiento (id_requerimiento)
);

CREATE UNIQUE INDEX ux_compras_requerimiento_reclamo_id_reclamo
    ON compras.requerimiento_reclamo_prestacional (
        id_reclamo_prestacional
    )
    WHERE id_reclamo_prestacional IS NOT NULL;

CREATE INDEX ix_compras_requerimiento_reclamo_estado
    ON compras.requerimiento_reclamo_prestacional (
        estado,
        reserva_fecha
    );

COMMENT ON TABLE compras.requerimiento_reclamo_prestacional IS
    'Relación uno a uno entre un requerimiento COTIZADO y su Reclamo Prestacional.';

COMMENT ON COLUMN compras.requerimiento_reclamo_prestacional.id_reclamo_prestacional IS
    'Identificador externo del esquema autorizaciones. No se declara FK cruzada porque compras no administra ese esquema.';

-- =====================================================================
-- DATOS INICIALES
-- =====================================================================

INSERT INTO compras.sector_requerimiento (
    id_sector,
    descripcion,
    requiere_afiliado,
    activo,
    alta_usr
)
VALUES
    (1, 'Farmacia', TRUE, TRUE, 'sistema'),
    (2, 'Prestaciones Médicas', TRUE, TRUE, 'sistema'),
    (3, 'Sistemas', FALSE, TRUE, 'sistema'),
    (4, 'RRHH', FALSE, TRUE, 'sistema'),
    (5, 'Legales', TRUE, TRUE, 'sistema'),
    (6, 'Otros', FALSE, TRUE, 'sistema');

WITH tipos (
    id_tipo_prestacion,
    descripcion,
    sector_normalizado
) AS (
    VALUES
        (1, 'ALIMENTACION', 'FARMACIA'),
        (2, 'MEDICAMENTOS', 'FARMACIA'),
        (3, 'PROTESIS TRAUMATOLOGIA', 'PRESTACIONES MEDICAS'),
        (4, 'PROTESIS CARDIOLOGIA', 'PRESTACIONES MEDICAS'),
        (5, 'PROTESIS GENERAL', 'PRESTACIONES MEDICAS'),
        (6, 'INSUMOS', 'PRESTACIONES MEDICAS'),
        (7, 'PAÑALES', 'PRESTACIONES MEDICAS')
)
INSERT INTO compras.tipo_prestacion (
    id_tipo_prestacion,
    descripcion,
    id_sector
)
SELECT
    t.id_tipo_prestacion,
    t.descripcion,
    s.id_sector
FROM tipos t
JOIN compras.sector_requerimiento s
  ON translate(
         upper(btrim(s.descripcion)),
         U&'\00C1\00C9\00CD\00D3\00DA\00DC\00C0\00C8\00CC\00D2\00D9',
         'AEIOUUAEIOU'
     ) = t.sector_normalizado
 AND s.activo = TRUE
 AND s.baja_fecha IS NULL;

SELECT setval(
               pg_get_serial_sequence(
                       'compras.sector_requerimiento',
                       'id_sector'
               ),
               7,
               TRUE
       );


CREATE TABLE IF NOT EXISTS compras.estado_requerimiento (
    id_estado INTEGER PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(80) NOT NULL,
    orden SMALLINT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    descripcion_visual VARCHAR(80)
);
INSERT INTO compras.estado_requerimiento
    (id_estado, codigo, descripcion, orden, activo, descripcion_visual)
VALUES
    (1, 'PENDIENTE', 'PENDIENTE', 1, TRUE, NULL),
    (2, 'A_COTIZAR', 'A COTIZAR', 2, TRUE, 'ENVIADO A COTIZAR'),
    (3, 'COTIZADO', 'COTIZADO', 3, TRUE, NULL),
    (4, 'RECLAMO_RP', 'RECLAMO (RP)', 4, TRUE, NULL),
    (5, 'ORDEN_COMPRA', 'ORDEN DE COMPRA', 5, TRUE, NULL),
    (99, 'ANULADO', 'ANULADO', 6, TRUE, NULL)
ON CONFLICT (id_estado) DO NOTHING;

ALTER TABLE compras.sector_requerimiento
    ADD COLUMN IF NOT EXISTS tipo_item VARCHAR(20),
    ADD COLUMN IF NOT EXISTS seleccionable_alta BOOLEAN,
    ADD COLUMN IF NOT EXISTS permite_cotizacion_empresa BOOLEAN,
    ADD COLUMN IF NOT EXISTS permite_orden_compra_directa BOOLEAN,
    ADD COLUMN IF NOT EXISTS busqueda_nomenclador_medica BOOLEAN,
    ADD COLUMN IF NOT EXISTS permite_medicamento_legacy BOOLEAN,
    ADD COLUMN IF NOT EXISTS sector_reclamo_prestacional VARCHAR(40);

-- Los tres ultimos atributos conservan el buscador y el contrato de RP.
UPDATE compras.sector_requerimiento s
SET tipo_item = c.tipo_item,
    seleccionable_alta = c.alta,
    permite_cotizacion_empresa = c.empresa,
    permite_orden_compra_directa = c.oc,
    busqueda_nomenclador_medica = c.medica,
    permite_medicamento_legacy = c.medicamento,
    sector_reclamo_prestacional = c.rp
FROM (VALUES
    (1, 'NOMENCLADOR', TRUE, FALSE, FALSE, FALSE, TRUE, 'FARMACIA'),
    (2, 'NOMENCLADOR', TRUE, FALSE, FALSE, TRUE, FALSE, 'PRESTACIONES MEDICAS'),
    (3, 'OBSERVACION', TRUE, TRUE, TRUE, FALSE, FALSE, NULL),
    (4, 'OBSERVACION', TRUE, TRUE, TRUE, FALSE, FALSE, NULL),
    (5, 'OBSERVACION', FALSE, FALSE, FALSE, FALSE, FALSE, 'LEGALES'),
    (6, 'OBSERVACION', FALSE, FALSE, FALSE, FALSE, FALSE, NULL)
) c(id, tipo_item, alta, empresa, oc, medica, medicamento, rp)
WHERE s.id_sector = c.id AND s.tipo_item IS NULL;
ALTER TABLE compras.sector_requerimiento
    ALTER COLUMN tipo_item SET NOT NULL,
    ALTER COLUMN seleccionable_alta SET NOT NULL,
    ALTER COLUMN permite_cotizacion_empresa SET NOT NULL,
    ALTER COLUMN permite_orden_compra_directa SET NOT NULL,
    ALTER COLUMN busqueda_nomenclador_medica SET NOT NULL,
    ALTER COLUMN permite_medicamento_legacy SET NOT NULL;

ALTER TABLE compras.tipo_prestacion ADD COLUMN IF NOT EXISTS rubro_prestador VARCHAR(120);
UPDATE compras.tipo_prestacion t SET rubro_prestador = c.rubro
FROM (VALUES (1, 'ALIMENTACION'), (2, 'MEDICAMENTOS'),
    (3, 'PROTESIS_TRAUMATOLOGIA'), (4, 'PROTESIS_CARDIOLOGIA'),
    (5, 'PROTESIS_GENERAL'), (6, 'INSUMOS'), (7, 'PAÑALES')) c(id, rubro)
WHERE t.id_tipo_prestacion = c.id AND t.rubro_prestador IS NULL;
ALTER TABLE compras.tipo_prestacion ALTER COLUMN rubro_prestador SET NOT NULL;

CREATE TABLE IF NOT EXISTS compras.tipo_prestacion_tipo_nomenclador (
    id_tipo_prestacion SMALLINT NOT NULL REFERENCES compras.tipo_prestacion(id_tipo_prestacion),
    id_tipo_nomenclador INTEGER NOT NULL,
    PRIMARY KEY (id_tipo_prestacion, id_tipo_nomenclador)
);
-- Universo del runtime: Farmacia 9; Protesis 2/3/4/6/14;
-- Panales 2/3/4/6; Insumos 10 y drift maestro confirmado a 14.
INSERT INTO compras.tipo_prestacion_tipo_nomenclador VALUES
    (1,9),(2,9),
    (3,2),(3,3),(3,4),(3,6),(3,14),
    (4,2),(4,3),(4,4),(4,6),(4,14),
    (5,2),(5,3),(5,4),(5,6),(5,14),
    (6,10),(6,14),(7,2),(7,3),(7,4),(7,6)
ON CONFLICT DO NOTHING;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
        WHERE conrelid = 'compras.requerimiento'::regclass AND contype = 'f'
          AND confrelid = 'compras.sector_requerimiento'::regclass
          AND conkey = ARRAY[(SELECT attnum FROM pg_attribute
             WHERE attrelid = 'compras.requerimiento'::regclass AND attname = 'id_sector')]::smallint[]) THEN
        ALTER TABLE compras.requerimiento ADD CONSTRAINT fk_compras_requerimiento_id_sector
            FOREIGN KEY (id_sector) REFERENCES compras.sector_requerimiento(id_sector);
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
        WHERE conrelid = 'compras.requerimiento'::regclass AND contype = 'f'
          AND confrelid = 'compras.estado_requerimiento'::regclass
          AND conkey = ARRAY[(SELECT attnum FROM pg_attribute
             WHERE attrelid = 'compras.requerimiento'::regclass AND attname = 'estado')]::smallint[]) THEN
        ALTER TABLE compras.requerimiento ADD CONSTRAINT fk_compras_requerimiento_estado
            FOREIGN KEY (estado) REFERENCES compras.estado_requerimiento(id_estado);
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
        WHERE conrelid = 'compras.requerimiento_detalle'::regclass AND contype = 'f'
          AND confrelid = 'compras.requerimiento'::regclass
          AND conkey = ARRAY[(SELECT attnum FROM pg_attribute
             WHERE attrelid = 'compras.requerimiento_detalle'::regclass AND attname = 'id_requerimiento')]::smallint[]) THEN
        ALTER TABLE compras.requerimiento_detalle ADD CONSTRAINT fk_compras_requerimiento_detalle_id_requerimiento
            FOREIGN KEY (id_requerimiento) REFERENCES compras.requerimiento(id_requerimiento);
    END IF;
END $ddl$;

DO $ddl$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
        WHERE conrelid = 'compras.requerimiento_detalle'::regclass AND contype = 'f'
          AND confrelid = 'compras.tipo_prestacion'::regclass
          AND conkey = ARRAY[(SELECT attnum FROM pg_attribute
             WHERE attrelid = 'compras.requerimiento_detalle'::regclass AND attname = 'id_tipo_prestacion')]::smallint[]) THEN
        ALTER TABLE compras.requerimiento_detalle ADD CONSTRAINT fk_compras_requerimiento_detalle_id_tipo_prestacion
            FOREIGN KEY (id_tipo_prestacion) REFERENCES compras.tipo_prestacion(id_tipo_prestacion);
    END IF;
END $ddl$;

CREATE OR REPLACE FUNCTION compras.id_estado_requerimiento(p_codigo VARCHAR)
RETURNS INTEGER LANGUAGE sql STABLE AS $function$
    SELECT e.id_estado FROM compras.estado_requerimiento e WHERE e.codigo = p_codigo;
$function$;

CREATE OR REPLACE FUNCTION compras.listar_tipos_nomenclador_compras()
RETURNS TABLE(id_tipo_nomenclador INTEGER, descripcion VARCHAR)
LANGUAGE sql STABLE AS $function$
    SELECT n.id_tipo_nomenclador::integer, n.descripcion::varchar
    FROM public.trae_tipos_nomenclador() n
    WHERE EXISTS (SELECT 1 FROM compras.tipo_prestacion_tipo_nomenclador c
                  WHERE c.id_tipo_nomenclador = n.id_tipo_nomenclador)
    ORDER BY n.id_tipo_nomenclador;
$function$;

CREATE OR REPLACE FUNCTION compras.resolver_identidad_afiliado(p_cuil VARCHAR, p_inte INTEGER)
RETURNS TABLE(cuil_titular VARCHAR, inte INTEGER)
LANGUAGE sql STABLE AS $function$
    -- Solo seguir cambios efectivos. Evita ciclos y no fabrica otra identidad.
    WITH RECURSIVE identidad(cuil, integrante, fecha, recorrido) AS (
        SELECT p_cuil, p_inte, '-infinity'::timestamp,
               ARRAY[p_cuil || ':' || p_inte::text]
        UNION ALL
        SELECT c.cuil_titular::varchar, c.inte, c.vigen_fecha::timestamp,
               i.recorrido || (c.cuil_titular || ':' || c.inte::text)
        FROM identidad i
        JOIN public.afi_cambio_cuil c
          ON c.cuil_titular_anterior = i.cuil AND c.inte_anterior = i.integrante
        WHERE c.vigen_fecha <= CURRENT_TIMESTAMP AND c.vigen_fecha >= i.fecha
          AND NOT (c.cuil_titular || ':' || c.inte::text) = ANY(i.recorrido)
    ), actuales AS (
        SELECT DISTINCT i.cuil,i.integrante,
            dense_rank() OVER (ORDER BY array_length(i.recorrido,1) DESC,i.fecha DESC) prioridad
        FROM identidad i
        JOIN public.afiliado a ON a.cuil_titular=i.cuil AND a.inte=i.integrante
    )
    SELECT a.cuil,a.integrante FROM actuales a
    WHERE a.prioridad=1 AND (SELECT count(*) FROM actuales x WHERE x.prioridad=1)=1;
$function$;

-- =====================================================================
-- FUNCIONES AUXILIARES
-- =====================================================================

CREATE FUNCTION compras.normalizar_usuario(
    p_usuario VARCHAR
)
    RETURNS VARCHAR
AS $func$
SELECT COALESCE(
               NULLIF(btrim($1), ''),
               current_user::VARCHAR
       );
$func$
LANGUAGE sql
STABLE;


CREATE FUNCTION compras.normalizar_sector(
    p_descripcion VARCHAR
)
    RETURNS VARCHAR
AS $func$
SELECT translate(
           upper(btrim(COALESCE($1, ''))),
           U&'\00C1\00C9\00CD\00D3\00DA\00DC\00C0\00C8\00CC\00D2\00D9',
           'AEIOUUAEIOU'
       );
$func$
LANGUAGE sql
IMMUTABLE;


CREATE OR REPLACE FUNCTION compras.es_requerimiento_habilitado_busqueda_empresa_cotizacion(p_id_requerimiento integer)
 RETURNS boolean
 LANGUAGE sql
 STABLE
AS $function$
    SELECT COALESCE($1, 0) > 0
       AND EXISTS (
            SELECT 1
            FROM compras.requerimiento r
            JOIN compras.sector_requerimiento sr
              ON sr.id_sector = r.id_sector
            WHERE r.id_requerimiento = $1
              AND r.estado = compras.id_estado_requerimiento('PENDIENTE')
              AND r.baja_fecha IS NULL
              AND sr.permite_cotizacion_empresa
       );
$function$;


;


;


CREATE OR REPLACE FUNCTION compras.listar_estados_requerimiento()
 RETURNS TABLE(id integer, descripcion varchar, codigo varchar, orden smallint, activo boolean, descripcion_visual varchar)
 LANGUAGE sql
 STABLE
AS $function$
SELECT e.id_estado, e.descripcion, e.codigo, e.orden, e.activo, COALESCE(e.descripcion_visual,e.descripcion) FROM compras.estado_requerimiento e WHERE e.activo ORDER BY e.orden;
$function$;

-- =====================================================================
-- REGLAS DE INTEGRIDAD
-- =====================================================================

;


;

;

;

-- =====================================================================
-- REQUERIMIENTOS: MODELO DE LECTURA
-- =====================================================================

CREATE TYPE compras.requerimiento_base_row AS (
    id INTEGER,

    alta_fecha TIMESTAMP WITHOUT TIME ZONE,
    alta_usr VARCHAR,

    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR,

    baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    baja_usr VARCHAR,

    afiliado_cuil_titular VARCHAR,
    afiliado_int INTEGER,
    afiliado_id_ospim INTEGER,

    afiliado_nombre VARCHAR,
    afiliado_apellido VARCHAR,
    afiliado_nombre_apellido VARCHAR,

    afiliado_documento_tipo VARCHAR,
    afiliado_documento_nro VARCHAR,
    afiliado_documento VARCHAR,

    afiliado_direccion VARCHAR,
    afiliado_localidad VARCHAR,
    afiliado_provincia VARCHAR,
    afiliado_celular VARCHAR,
    afiliado_telefono VARCHAR,
    afiliado_email VARCHAR,

    id_sector INTEGER,
    sector_descripcion VARCHAR,
    requiere_afiliado BOOLEAN,

    cargo_ospim INTEGER,
    cargo_tercerizadora INTEGER,
    id_tercerizadora VARCHAR,

    recupero BOOLEAN,
    surge BOOLEAN,
    observaciones TEXT,

    id_estado INTEGER,
    estado_descripcion VARCHAR,
    legales BOOLEAN,
    sector_tipo_item varchar,
    sector_seleccionable_alta boolean,
    sector_permite_cotizacion_empresa boolean,
    sector_permite_orden_compra_directa boolean,
    sector_busqueda_nomenclador_medica boolean,
    sector_permite_medicamento_legacy boolean,
    sector_sector_reclamo_prestacional varchar,
    estado_codigo varchar,
    estado_descripcion_visual varchar,
    sector_nomencladores integer[]
);


CREATE OR REPLACE FUNCTION compras.requerimiento_base()
 RETURNS SETOF compras.requerimiento_base_row
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN
RETURN QUERY
SELECT
    r.id_requerimiento,

    r.alta_fecha,
    r.alta_usr,

    r.modi_fecha,
    r.modi_usr,

    r.baja_fecha,
    r.baja_usr,

    COALESCE(ident.cuil_titular,r.afiliado_cuil_titular)::varchar,
    COALESCE(ident.inte,r.afiliado_int),
    a.id_ospim::integer,

    a.nombre::varchar,
    a.apellido::varchar,

    NULLIF(
            concat_ws(
                    ', ',
                    NULLIF(btrim(a.apellido::varchar), ''),
                    NULLIF(btrim(a.nombre::varchar), '')
            ),
            ''
    )::VARCHAR AS afiliado_nombre_apellido,

    a.documento_tipo::varchar,
    a.docu_numero::varchar,

    NULLIF(
            concat_ws(
                    ' ',
                    NULLIF(
                            btrim(a.documento_tipo::varchar),
                            ''
                    ),
                    NULLIF(
                            btrim(a.docu_numero::varchar),
                            ''
                    )
            ),
            ''
    )::VARCHAR AS afiliado_documento,

    NULLIF(btrim(concat_ws(' ', NULLIF(btrim(d.afidom_calle),''), NULLIF(btrim(d.afidom_numero),''), CASE WHEN NULLIF(btrim(d.afidom_piso),'') IS NOT NULL THEN 'Piso ' || btrim(d.afidom_piso) END, CASE WHEN NULLIF(btrim(d.afidom_depto),'') IS NOT NULL THEN 'Dto. ' || btrim(d.afidom_depto) END, CASE WHEN NULLIF(btrim(d.afidom_oficina),'') IS NOT NULL THEN 'Of. ' || btrim(d.afidom_oficina) END)), '')::varchar,
    d.afidom_localidad_nombre::varchar,
    d.afidom_provincia_nombre::varchar,
    NULLIF(concat_ws(' ', NULLIF(btrim(d.afidom_cod_area_celular),''), NULLIF(btrim(d.afidom_celular),'')), '')::varchar,
    NULLIF(concat_ws(' ', NULLIF(btrim(d.afidom_cod_area_telefono),''), NULLIF(btrim(d.afidom_telefono),'')), '')::varchar,
    a.email::varchar,

    r.id_sector,
    s.descripcion,
    s.requiere_afiliado,

    (100 - r.cargo_tercerizadora)::integer,
    r.cargo_tercerizadora,
    r.id_tercerizadora,

    (r.cargo_tercerizadora > 0),
    r.surge,
    r.observaciones,

    r.estado,
    e.descripcion,
    r.legales,
    s.tipo_item,
    s.seleccionable_alta,
    s.permite_cotizacion_empresa,
    s.permite_orden_compra_directa,
    s.busqueda_nomenclador_medica,
    s.permite_medicamento_legacy,
    s.sector_reclamo_prestacional,
    e.codigo,
    COALESCE(e.descripcion_visual,e.descripcion),
    ARRAY(SELECT DISTINCT c.id_tipo_nomenclador FROM compras.tipo_prestacion t JOIN compras.tipo_prestacion_tipo_nomenclador c ON c.id_tipo_prestacion=t.id_tipo_prestacion WHERE t.id_sector=s.id_sector ORDER BY c.id_tipo_nomenclador)
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento s
              ON s.id_sector = r.id_sector
         JOIN compras.estado_requerimiento e ON e.id_estado = r.estado
         LEFT JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
         LEFT JOIN public.afiliado a ON a.cuil_titular = ident.cuil_titular AND a.inte = ident.inte
         LEFT JOIN LATERAL (
             SELECT domicilio.* FROM (
                 SELECT x.*, 0 AS prioridad FROM public.busca_afiliado_domicilio(a.cuil_titular,a.inte) x
                 UNION ALL
                 SELECT x.*, 1 AS prioridad FROM public.busca_afiliado_domicilio(a.cuil_titular,0) x WHERE a.inte <> 0
             ) domicilio ORDER BY prioridad, afidom_id_domicilio DESC LIMIT 1
         ) d ON TRUE;
END;
$function$;


;


CREATE FUNCTION compras.buscar_requerimientos(
    p_estado INTEGER,
    p_sector INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_id_tercerizadora VARCHAR,
    p_recupero BOOLEAN,
    p_surge BOOLEAN,
    p_texto VARCHAR
)
    RETURNS SETOF compras.requerimiento_base_row
AS $func$
BEGIN
RETURN QUERY
SELECT *
FROM compras.buscar_requerimientos(
    p_estado,
    p_sector,
    p_afiliado_cuil_titular,
    p_afiliado_int,
    p_id_tercerizadora,
    p_recupero,
    p_surge,
    p_texto,
    NULL::DATE,
    NULL::DATE
);
END;
$func$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION compras.buscar_requerimientos(p_estado integer, p_sector integer, p_afiliado_cuil_titular character varying, p_afiliado_int integer, p_id_tercerizadora character varying, p_recupero boolean, p_surge boolean, p_texto character varying, p_fecha_alta_desde date, p_fecha_alta_hasta date, p_id_requerimiento_compra integer)
 RETURNS SETOF compras.requerimiento_base_row
 LANGUAGE plpgsql
 STABLE
AS $function$
DECLARE
    v_texto VARCHAR;
    v_cuil VARCHAR;
BEGIN
    v_texto := NULLIF(
        upper(btrim(p_texto)),
        ''
    );

    v_cuil := NULLIF(
        regexp_replace(
            COALESCE(
                p_afiliado_cuil_titular,
                ''
            ),
            '[^0-9]',
            '',
            'g'
        ),
        ''
    );

    RETURN QUERY
    SELECT rb.*
    FROM compras.requerimiento_base() rb
    WHERE (
        (
            p_estado = compras.id_estado_requerimiento('ANULADO')
            AND rb.id_estado = compras.id_estado_requerimiento('ANULADO')
        )
        OR (
            p_estado IS DISTINCT FROM compras.id_estado_requerimiento('ANULADO')
            AND rb.baja_fecha IS NULL
        )
    )
    AND (
        p_estado IS NULL
        OR rb.id_estado = p_estado
    )
    AND (
        p_sector IS NULL
        OR rb.id_sector = p_sector
    )
    AND (
        p_id_requerimiento_compra IS NULL
        OR rb.id = p_id_requerimiento_compra
    )
    AND (
        v_cuil IS NULL
        OR regexp_replace(
            COALESCE(
                rb.afiliado_cuil_titular,
                ''
            ),
            '[^0-9]',
            '',
            'g'
        ) LIKE '%' || v_cuil || '%'
    )
    AND (
        p_afiliado_int IS NULL
        OR rb.afiliado_int = p_afiliado_int
    )
    AND (
        NULLIF(
            btrim(p_id_tercerizadora),
            ''
        ) IS NULL
        OR upper(
            COALESCE(
                rb.id_tercerizadora,
                ''
            )
        ) = upper(
            btrim(p_id_tercerizadora)
        )
    )
    AND (
        p_recupero IS NULL
        OR rb.recupero = p_recupero
    )
    AND (
        p_surge IS NULL
        OR rb.surge = p_surge
    )
    AND (
        p_fecha_alta_desde IS NULL
        OR rb.alta_fecha >= p_fecha_alta_desde
    )
    AND (
        p_fecha_alta_hasta IS NULL
        OR rb.alta_fecha
            < p_fecha_alta_hasta + INTERVAL '1 day'
    )
    AND (
        v_texto IS NULL

        OR rb.id::VARCHAR = btrim(p_texto)

        OR upper(
            COALESCE(
                rb.observaciones,
                ''
            )
        ) LIKE '%' || v_texto || '%'

        OR upper(
            COALESCE(
                rb.sector_descripcion,
                ''
            )
        ) LIKE '%' || v_texto || '%'

        OR upper(
            COALESCE(
                rb.afiliado_nombre_apellido,
                ''
            )
        ) LIKE '%' || v_texto || '%'

        OR upper(
            COALESCE(
                rb.afiliado_documento,
                ''
            )
        ) LIKE '%' || v_texto || '%'

        OR EXISTS (
            SELECT 1
            FROM compras.requerimiento_detalle d
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
            WHERE d.id_requerimiento = rb.id
              AND d.baja_fecha IS NULL
              AND (
                  upper(
                      COALESCE(
                          d.tipo_item,
                          ''
                      )
                  ) LIKE '%' || v_texto || '%'

                  OR upper(
                      COALESCE(n.codigo, '')
                  ) LIKE '%' || v_texto || '%'

                  OR upper(
                      COALESCE(n.descripcion, '')
                  ) LIKE '%' || v_texto || '%'

                  OR upper(
                      COALESCE(d.nombre_medicamento, '')
                  ) LIKE '%' || v_texto || '%'

                  OR COALESCE(d.troquel::VARCHAR, '')
                      LIKE '%' || btrim(p_texto) || '%'

                  OR upper(
                      COALESCE(
                          d.observaciones,
                          ''
                      )
                  ) LIKE '%' || v_texto || '%'
              )
        )
    )
    ORDER BY rb.id DESC;
END;
$function$;

ALTER FUNCTION compras.buscar_requerimientos(
    integer,
    integer,
    character varying,
    integer,
    character varying,
    boolean,
    boolean,
    character varying,
    date,
    date,
    integer
)
    OWNER TO postgres;

CREATE OR REPLACE FUNCTION compras.buscar_requerimientos(
    p_estado integer,
    p_sector integer,
    p_afiliado_cuil_titular character varying,
    p_afiliado_int integer,
    p_id_tercerizadora character varying,
    p_recupero boolean,
    p_surge boolean,
    p_texto character varying,
    p_fecha_alta_desde date,
    p_fecha_alta_hasta date
)
    RETURNS SETOF compras.requerimiento_base_row
    LANGUAGE 'plpgsql'
    COST 100
    STABLE
    ROWS 1000
AS $BODY$
BEGIN
    RETURN QUERY
    SELECT *
    FROM compras.buscar_requerimientos(
        p_estado,
        p_sector,
        p_afiliado_cuil_titular,
        p_afiliado_int,
        p_id_tercerizadora,
        p_recupero,
        p_surge,
        p_texto,
        p_fecha_alta_desde,
        p_fecha_alta_hasta,
        NULL::INTEGER
    );
END;
$BODY$;

ALTER FUNCTION compras.buscar_requerimientos(
    integer,
    integer,
    character varying,
    integer,
    character varying,
    boolean,
    boolean,
    character varying,
    date,
    date
)
    OWNER TO postgres;

CREATE FUNCTION compras.get_requerimiento(
    p_id_requerimiento INTEGER
)
    RETURNS SETOF compras.requerimiento_base_row
AS $func$
BEGIN
RETURN QUERY
SELECT rb.*
FROM compras.requerimiento_base() rb
WHERE rb.id = p_id_requerimiento;
END;
$func$
LANGUAGE plpgsql
STABLE;


-- ============================================================
-- SECTORES
-- ============================================================

CREATE OR REPLACE FUNCTION compras.es_sector_seleccionable_compras(p_id_sector integer)
 RETURNS boolean
 LANGUAGE sql
 STABLE
AS $function$
    SELECT EXISTS (
        SELECT 1
        FROM compras.sector_requerimiento s
        WHERE s.id_sector = p_id_sector
          AND s.activo = TRUE
          AND s.baja_fecha IS NULL
          AND s.seleccionable_alta
    );
$function$;


-- =====================================================================
-- REQUERIMIENTOS: ESCRITURA
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento(p_id integer, p_afiliado_cuil_titular character varying, p_afiliado_int integer, p_afiliado_id_ospim integer, p_afiliado_nombre character varying, p_afiliado_apellido character varying, p_afiliado_documento_tipo character varying, p_afiliado_documento_nro character varying, p_afiliado_direccion character varying, p_afiliado_localidad character varying, p_afiliado_provincia character varying, p_afiliado_celular character varying, p_afiliado_telefono character varying, p_afiliado_email character varying, p_id_sector integer, p_cargo_ospim integer, p_cargo_tercerizadora integer, p_id_tercerizadora character varying, p_recupero boolean, p_surge boolean, p_legales boolean, p_observaciones text, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
v_id INTEGER;
    v_usuario VARCHAR(100);
    v_afiliado_cuil VARCHAR(20);
    v_cuil_anterior VARCHAR(20);
    v_inte_anterior INTEGER;
    v_cambio_afiliado BOOLEAN;
BEGIN
    v_usuario := compras.normalizar_usuario(p_usuario);
    v_afiliado_cuil := NULLIF(btrim(p_afiliado_cuil_titular), '');

    IF p_id IS NULL OR p_id <= 0 THEN
        IF NOT compras.es_sector_seleccionable_compras(p_id_sector) THEN
            RAISE EXCEPTION
                'El sector informado no está habilitado para nuevas compras.';
        END IF;

        INSERT INTO compras.requerimiento (
        estado,
        id_sector,
        afiliado_cuil_titular,
        afiliado_int,
        cargo_tercerizadora,
        id_tercerizadora,
        surge,
        legales,
        observaciones,
        alta_usr
    )
        VALUES (
        compras.id_estado_requerimiento('PENDIENTE'),
        p_id_sector,
        v_afiliado_cuil,
        p_afiliado_int,
        COALESCE(p_cargo_tercerizadora, 0),
        NULLIF(btrim(p_id_tercerizadora), ''),
        COALESCE(p_surge, FALSE),
        COALESCE(p_legales, FALSE),
        NULLIF(btrim(p_observaciones), ''),
        v_usuario
    )
        RETURNING id_requerimiento INTO v_id;

        RETURN v_id;
    END IF;

    SELECT COALESCE(ident.cuil_titular,r.afiliado_cuil_titular), COALESCE(ident.inte,r.afiliado_int)
    INTO v_cuil_anterior, v_inte_anterior
    FROM compras.requerimiento r
    LEFT JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
    WHERE r.id_requerimiento = p_id
      AND r.baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontró el requerimiento a modificar.';
    END IF;

    v_cambio_afiliado :=
           v_cuil_anterior IS DISTINCT FROM v_afiliado_cuil
        OR v_inte_anterior IS DISTINCT FROM p_afiliado_int;

    IF v_cambio_afiliado THEN
        RAISE EXCEPTION
            'El afiliado del requerimiento no puede modificarse una vez creado.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM compras.requerimiento r
        WHERE r.id_requerimiento = p_id
          AND (
              r.id_sector IS DISTINCT FROM p_id_sector
              OR r.legales IS DISTINCT FROM COALESCE(p_legales, FALSE)
          )
    ) THEN
        RAISE EXCEPTION
            'El sector y la marca LEGALES son inmutables después del alta.';
    END IF;

    UPDATE compras.requerimiento
    SET cargo_tercerizadora = COALESCE(p_cargo_tercerizadora, 0),
        id_tercerizadora = NULLIF(btrim(p_id_tercerizadora), ''),
        surge = COALESCE(p_surge, FALSE),
        observaciones = NULLIF(btrim(p_observaciones), ''),
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id
      AND estado = compras.id_estado_requerimiento('PENDIENTE')
      AND baja_fecha IS NULL
    RETURNING id_requerimiento INTO v_id;

    IF v_id IS NULL THEN
        RAISE EXCEPTION
            'La estructura solo puede modificarse en estado PENDIENTE.';
    END IF;

    RETURN v_id;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.cambiar_estado_requerimiento(p_id_requerimiento integer, p_estado_nuevo integer, p_usuario character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
v_estado_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

SELECT r.estado
INTO v_estado_actual
FROM compras.requerimiento r
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND (
    r.baja_fecha IS NULL
        OR r.estado = compras.id_estado_requerimiento('ANULADO')
    )
    FOR UPDATE;

IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION
            'No se encontró el requerimiento.';
END IF;

    IF p_estado_nuevo = v_estado_actual THEN
        RAISE EXCEPTION
            'La transicion al mismo estado no es válida.';
END IF;

UPDATE compras.requerimiento
SET estado = p_estado_nuevo,
    modi_fecha = now(),
    modi_usr = v_usuario,
    baja_fecha = CASE WHEN p_estado_nuevo = compras.id_estado_requerimiento('ANULADO') THEN COALESCE(baja_fecha,now()) ELSE baja_fecha END,
    baja_usr =
        CASE
            WHEN p_estado_nuevo = compras.id_estado_requerimiento('ANULADO')
                THEN v_usuario
            ELSE baja_usr
            END
WHERE id_requerimiento =
      p_id_requerimiento
  AND estado =
      v_estado_actual;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
END IF;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.anular_requerimiento(p_id_requerimiento integer, p_motivo_baja text, p_usuario character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_estado_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

    SELECT r.estado
      INTO v_estado_actual
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento.';
    END IF;

    IF NOT (
            (v_estado_actual = compras.id_estado_requerimiento('PENDIENTE'))
         OR (v_estado_actual = compras.id_estado_requerimiento('A_COTIZAR'))
    ) THEN
        RAISE EXCEPTION
            'El requerimiento no puede anularse desde el estado actual.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = compras.id_estado_requerimiento('ANULADO'),
           motivo_baja = COALESCE(p_motivo_baja, ''),
           modi_fecha = now(),
           modi_usr = v_usuario,
           baja_fecha = now(),
           baja_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(p_id_requerimiento integer, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_estado INTEGER;
    v_permite_operacion BOOLEAN;
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    SELECT
        r.estado,
        sr.permite_orden_compra_directa
    INTO
        v_estado,
        v_permite_operacion
    FROM compras.requerimiento r
    JOIN compras.sector_requerimiento sr
      ON sr.id_sector = r.id_sector
     AND sr.activo = TRUE
     AND sr.baja_fecha IS NULL
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE OF r;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento activo.';
    END IF;

    IF v_estado = compras.id_estado_requerimiento('ORDEN_COMPRA') THEN
        RETURN compras.id_estado_requerimiento('ORDEN_COMPRA');
    END IF;

    IF v_estado <> compras.id_estado_requerimiento('PENDIENTE') THEN
        RAISE EXCEPTION
            'El requerimiento solo puede pasar a ORDEN_COMPRA desde PENDIENTE.';
    END IF;

    IF NOT v_permite_operacion THEN

        RAISE EXCEPTION
            'Solo los requerimientos de RRHH o SISTEMAS pueden pasar directamente a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_detalle d
         WHERE d.id_requerimiento = p_id_requerimiento
           AND d.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
    END IF;

    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        compras.id_estado_requerimiento('ORDEN_COMPRA'),
        p_usuario
    );

    RETURN compras.id_estado_requerimiento('ORDEN_COMPRA');
END;
$function$;

CREATE OR REPLACE FUNCTION compras.confirmar_envio_a_cotizar(p_id_requerimiento integer, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
v_estado INTEGER;
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

SELECT r.estado
INTO v_estado
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontró el requerimiento activo.';
END IF;

    /*
     * Idempotencia:
     * otro proceso pudo confirmar el envío y cambiar el estado.
     */
    IF v_estado = compras.id_estado_requerimiento('A_COTIZAR') THEN
        RETURN compras.id_estado_requerimiento('A_COTIZAR');
END IF;

    IF v_estado <> compras.id_estado_requerimiento('PENDIENTE') THEN
        RAISE EXCEPTION
            'Solo un requerimiento PENDIENTE puede pasar a A COTIZAR.';
END IF;

    /*
     * No se cambia el estado si no existe al menos un envio
     * efectivamente persistido como ENVIADO.
     */
    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_cotizacion_prestador rcp
         WHERE rcp.id_requerimiento = p_id_requerimiento
           AND rcp.estado_envio = 'ENVIADO'
    ) THEN
        RETURN compras.id_estado_requerimiento('PENDIENTE');
END IF;

    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        compras.id_estado_requerimiento('A_COTIZAR'),
        p_usuario
    );

RETURN compras.id_estado_requerimiento('A_COTIZAR');
END;
$function$;

-- =====================================================================
-- DETALLES
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.get_requerimiento_detalle(p_id_requerimiento integer)
 RETURNS TABLE(id integer, id_requerimiento integer, tipo_item character varying, codigo_item character varying, descripcion_item character varying, id_prestacion integer, id_tipo_nomenclador integer, codigo_nomenclador character varying, descripcion_nomenclador character varying, id_medicamento integer, troquel integer, nombre_medicamento character varying, cantidad integer, precio_unitario_estimado numeric, precio_total_estimado numeric, id_prestador integer, prestador_cuit character varying, prestador_razon_social character varying, observaciones text)
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN
RETURN QUERY
SELECT
    d.id_detalle,
    d.id_requerimiento,

    d.tipo_item::VARCHAR,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN COALESCE(
                d.troquel::VARCHAR,
                d.id_medicamento::VARCHAR
                 )
        ELSE n.codigo
        END::VARCHAR AS codigo_item,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN d.nombre_medicamento
        ELSE n.descripcion
        END::VARCHAR AS descripcion_item,

    d.id_prestacion,
    n.id_tipo_nomenclador,
    n.codigo,
    n.descripcion,

    d.id_medicamento,
    d.troquel,
    d.nombre_medicamento,

    d.cantidad,
    d.precio_unitario_estimado,
    d.precio_total_estimado,

    d.id_prestador,
    p.cuit::VARCHAR,
    p.descripcion::VARCHAR,

    d.observaciones
FROM compras.requerimiento_detalle d
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
         LEFT JOIN public.prestador p
                   ON p.id_prestador = d.id_prestador
WHERE d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL
ORDER BY d.id_detalle;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_detalle_clasificado(p_id_requerimiento integer)
 RETURNS TABLE(id integer, id_requerimiento integer, tipo_item character varying, id_tipo_prestacion integer, tipo_prestacion character varying, codigo_item character varying, descripcion_item character varying, id_prestacion integer, id_tipo_nomenclador integer, codigo_nomenclador character varying, descripcion_nomenclador character varying, id_medicamento integer, troquel integer, nombre_medicamento character varying, cantidad integer, precio_unitario_estimado numeric, precio_total_estimado numeric, id_prestador integer, prestador_cuit character varying, prestador_razon_social character varying, observaciones text)
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN
RETURN QUERY
SELECT
    d.id_detalle,
    d.id_requerimiento,

    d.tipo_item::VARCHAR,
    d.id_tipo_prestacion::INTEGER,
    tp.descripcion::VARCHAR,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN COALESCE(
                d.troquel::VARCHAR,
                d.id_medicamento::VARCHAR
                 )
        ELSE n.codigo
        END::VARCHAR AS codigo_item,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN d.nombre_medicamento
        ELSE n.descripcion
        END::VARCHAR AS descripcion_item,

    d.id_prestacion,
    n.id_tipo_nomenclador,
    n.codigo,
    n.descripcion,

    d.id_medicamento,
    d.troquel,
    d.nombre_medicamento,

    d.cantidad,
    d.precio_unitario_estimado,
    d.precio_total_estimado,

    d.id_prestador,
    p.cuit::VARCHAR,
    p.descripcion::VARCHAR,

    d.observaciones
FROM compras.requerimiento_detalle d
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
         LEFT JOIN compras.tipo_prestacion tp
                   ON tp.id_tipo_prestacion = d.id_tipo_prestacion
         LEFT JOIN public.prestador p
                   ON p.id_prestador = d.id_prestador
WHERE d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL
ORDER BY d.id_detalle;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento_detalle(p_id integer, p_id_requerimiento integer, p_tipo_item character varying, p_id_prestacion integer, p_id_tipo_nomenclador integer, p_codigo_nomenclador character varying, p_descripcion_nomenclador character varying, p_id_medicamento integer, p_troquel integer, p_nombre_medicamento character varying, p_cantidad integer, p_observaciones text, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_tipo VARCHAR;
    v_usuario VARCHAR := compras.normalizar_usuario(p_usuario);
BEGIN
    -- La validacion funcional vive en EditarRequerimientoCompraHelper.
    -- El estado y la identidad se comprueban con el bloqueo de cabecera.
    PERFORM 1 FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento AND r.estado = compras.id_estado_requerimiento('PENDIENTE')
      AND r.baja_fecha IS NULL FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Los detalles estructurales solo pueden modificarse en estado PENDIENTE.';
    END IF;
    IF COALESCE(p_id,0) <= 0 THEN
        INSERT INTO compras.requerimiento_detalle
            (
        id_requerimiento,
        tipo_item,
        id_prestacion,
        cantidad,
        observaciones,
        alta_usr
    )
        VALUES (
        p_id_requerimiento,
        p_tipo_item,
        p_id_prestacion,
        p_cantidad,
        NULLIF(btrim(p_observaciones), ''),
        v_usuario
    )
        RETURNING id_detalle INTO v_id;
    ELSE
        SELECT d.tipo_item INTO v_tipo FROM compras.requerimiento_detalle d
        WHERE d.id_detalle = p_id AND d.id_requerimiento = p_id_requerimiento
          AND d.baja_fecha IS NULL FOR UPDATE;
        IF NOT FOUND OR v_tipo IS DISTINCT FROM p_tipo_item THEN
            RAISE EXCEPTION 'No se encontro el detalle activo del tipo informado.';
        END IF;
        UPDATE compras.requerimiento_detalle
        SET id_prestacion = CASE WHEN v_tipo = 'NOMENCLADOR' THEN p_id_prestacion ELSE id_prestacion END,
            cantidad = p_cantidad, observaciones = NULLIF(btrim(p_observaciones), ''),
            precio_unitario_estimado = NULL, precio_total_estimado = NULL, id_prestador = NULL,
            modi_fecha = now(), modi_usr = v_usuario
        WHERE id_detalle = p_id AND id_requerimiento = p_id_requerimiento AND baja_fecha IS NULL
        RETURNING id_detalle INTO v_id;
    END IF;
    RETURN v_id;
END;
$function$;


;

;

;

;

CREATE FUNCTION compras.guardar_requerimiento_detalle_clasificado(
    p_id INTEGER,
    p_id_requerimiento INTEGER,
    p_tipo_item VARCHAR,
    p_id_prestacion INTEGER,
    p_id_tipo_nomenclador INTEGER,
    p_codigo_nomenclador VARCHAR,
    p_descripcion_nomenclador VARCHAR,
    p_id_medicamento INTEGER,
    p_troquel INTEGER,
    p_nombre_medicamento VARCHAR,
    p_cantidad INTEGER,
    p_observaciones TEXT,
    p_id_tipo_prestacion INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id_detalle INTEGER;
BEGIN
    v_id_detalle := compras.guardar_requerimiento_detalle(
        p_id,
        p_id_requerimiento,
        p_tipo_item,
        p_id_prestacion,
        p_id_tipo_nomenclador,
        p_codigo_nomenclador,
        p_descripcion_nomenclador,
        p_id_medicamento,
        p_troquel,
        p_nombre_medicamento,
        p_cantidad,
        p_observaciones,
        p_usuario
    );

    UPDATE compras.requerimiento_detalle
    SET id_tipo_prestacion = p_id_tipo_prestacion
    WHERE id_detalle = v_id_detalle
      AND id_requerimiento = p_id_requerimiento
      AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se pudo asociar el tipo al detalle del requerimiento.';
    END IF;

    RETURN v_id_detalle;
END;
$func$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION compras.borrar_requerimiento_detalle(p_id_detalle integer, p_usuario character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
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


    SELECT d.id_requerimiento
    INTO v_id_requerimiento
    FROM compras.requerimiento_detalle d
    WHERE d.id_detalle = p_id_detalle
      AND d.baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontró el detalle activo a borrar.';

    END IF;


    SELECT r.estado
    INTO v_estado
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = v_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo del detalle.';

    END IF;


    IF v_estado NOT IN (compras.id_estado_requerimiento('PENDIENTE'), compras.id_estado_requerimiento('A_COTIZAR')) THEN
        RAISE EXCEPTION
            'Los detalles solo pueden borrarse en estado PENDIENTE o ENVIADO A COTIZAR.';

    END IF;


    PERFORM 1
    FROM compras.requerimiento_detalle d
    WHERE d.id_detalle = p_id_detalle
      AND d.id_requerimiento = v_id_requerimiento
      AND d.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El detalle ya no se encuentra activo.';

    END IF;


    IF v_estado = compras.id_estado_requerimiento('A_COTIZAR') THEN

        SELECT count(*)
        INTO v_total_detalles_activos
        FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = v_id_requerimiento
          AND d.baja_fecha IS NULL;

        IF v_total_detalles_activos <= 1 THEN
            RAISE EXCEPTION
                'El requerimiento ENVIADO A COTIZAR debe conservar al menos una prestación.';

        END IF;

    END IF;


    UPDATE compras.requerimiento_detalle
    SET baja_fecha = now(),
        baja_usr = v_usuario,
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_detalle = p_id_detalle
      AND id_requerimiento = v_id_requerimiento
      AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El detalle ya no se encuentra activo.';

    END IF;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.finalizar_cotizacion_requerimiento(p_id_requerimiento integer, p_ids_detalle integer[], p_precios_unitarios numeric[], p_id_prestador integer, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
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
    -- modificar la cotización.
    IF v_estado = compras.id_estado_requerimiento('COTIZADO') THEN
        RETURN compras.id_estado_requerimiento('COTIZADO');
END IF;

    IF v_estado <> compras.id_estado_requerimiento('A_COTIZAR') THEN
        RAISE EXCEPTION
            'La cotización solo puede guardarse en estado A COTIZAR.';
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
            'La cotización contiene identificadores de detalle inválidos.';
END IF;

    IF (
SELECT count(*)
FROM unnest(p_ids_detalle) AS ids(id_detalle)
    ) <> (
        SELECT count(DISTINCT ids.id_detalle)
          FROM unnest(p_ids_detalle) AS ids(id_detalle)
    ) THEN
        RAISE EXCEPTION
            'La cotización contiene detalles duplicados.';
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
            'La cotización debe informar exactamente todos los detalles activos.';
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

    -- Bloquea la estructura completa antes de actualizar valores de cotización.
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
        RETURN compras.id_estado_requerimiento('A_COTIZAR');
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
            'Debe existir un presupuesto activo del prestador adjudicado antes de cerrar la cotización.';
END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_cotizacion_prestador rcp
         WHERE rcp.id_requerimiento = p_id_requerimiento
           AND rcp.id_prestador = p_id_prestador
           AND rcp.estado_envio = 'COTIZADO'
    ) THEN
        RAISE EXCEPTION
            'El prestador adjudicado debe encontrarse COTIZADO antes de cerrar la cotización.';
END IF;

    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        compras.id_estado_requerimiento('COTIZADO'),
        v_usuario
    );

RETURN compras.id_estado_requerimiento('COTIZADO');
END;
$function$;


-- =====================================================================
-- PRESTADORES PARA COTIZACIÓN
-- =====================================================================

/*
 * Fuente canónica de 0..N destinatarios del prestador:
 * public.prestad_contacto_e -> public.contacto_e.
 *
 * El resultado se separa por ';' para conservar el contrato Java vigente.
 */
CREATE OR REPLACE FUNCTION compras.resolver_emails_cotizacion_prestador(p_id_prestador integer)
 RETURNS text
 LANGUAGE sql
 STABLE
AS $function$
SELECT string_agg(
           contacto.email,
           ';' ORDER BY
               contacto.fecha_referencia DESC NULLS LAST,
               contacto.id_contacto_e DESC,
               lower(contacto.email)
       )
FROM (
    SELECT DISTINCT ON (
        lower(btrim(ce.contacto))
    )
        btrim(ce.contacto) AS email,
        COALESCE(
            ce.modi_fecha,
            ce.alta_fecha,
            ce.vigen_desde,
            pce.vigen_desde
        ) AS fecha_referencia,
        ce.id_contacto_e
    FROM public.prestad_contacto_e pce
    JOIN public.contacto_e ce
      ON ce.id_contacto_e = pce.id_contacto_e
    WHERE pce.id_prestador = p_id_prestador
      AND upper(btrim(COALESCE(ce.tipo_contacto_e, ''))) = 'E'
      AND ce.baja_fecha IS NULL
      AND (pce.vigen_desde IS NULL OR pce.vigen_desde <= LOCALTIMESTAMP)
      AND (ce.vigen_desde IS NULL OR ce.vigen_desde <= LOCALTIMESTAMP)
      AND NULLIF(btrim(ce.contacto), '') IS NOT NULL
      AND NULLIF(btrim(ce.contacto), '')
          ~* '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$'
    ORDER BY
        lower(btrim(ce.contacto)),
        COALESCE(
            ce.modi_fecha,
            ce.alta_fecha,
            ce.vigen_desde,
            pce.vigen_desde
        ) DESC NULLS LAST,
        ce.id_contacto_e DESC
) contacto;
$function$;

/*
 * Compatibilidad legacy para callers que esperan un único VARCHAR.
 * La fuente sigue siendo exclusivamente el resolver plural canónico.
 */
CREATE FUNCTION
compras.resolver_email_cotizacion_prestador(
    p_id_prestador INTEGER
)
RETURNS VARCHAR
AS $func$
SELECT NULLIF(
           split_part(
               COALESCE(
                   compras.resolver_emails_cotizacion_prestador($1),
                   ''
               ),
               ';',
               1
           ),
           ''
       )::VARCHAR;
$func$
LANGUAGE sql
STABLE;

CREATE OR REPLACE FUNCTION compras.listar_prestadores_cotizacion_requerimiento(p_id_requerimiento integer)
 RETURNS TABLE(id_prestador integer, descripcion character varying, cuit character varying, email character varying, id_tipo_prestador integer, tipo_prestador character varying)
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN
RETURN QUERY
SELECT DISTINCT
    p.id_prestador::INTEGER,
    p.descripcion::VARCHAR,
    p.cuit::VARCHAR,
    NULLIF(
        split_part(
            COALESCE(
                compras.resolver_emails_cotizacion_prestador(
                    p.id_prestador
                ),
                ''
            ),
            ';',
            1
        ),
        ''
    )::VARCHAR AS email,
    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR
FROM compras.requerimiento r
         JOIN public.prestador p
              ON COALESCE(
                     p.solicitar_cotizacion,
                     FALSE
                 ) = TRUE
                 AND p.baja_fecha IS NULL
         LEFT JOIN trae_tipos_prestadores() tp
                   ON tp.id_tipo_prestador =
                      p.id_tipo_prestador
         LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
                   ON rcp.id_requerimiento =
                      r.id_requerimiento
                       AND rcp.id_prestador =
                           p.id_prestador
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (compras.id_estado_requerimiento('PENDIENTE'), compras.id_estado_requerimiento('A_COTIZAR'))
  AND r.baja_fecha IS NULL
  AND EXISTS (
      SELECT 1
      FROM compras.requerimiento_detalle d
      JOIN compras.tipo_prestacion t
        ON t.id_tipo_prestacion = d.id_tipo_prestacion
      JOIN public.prestador_rubro pr
        ON pr.id_prestador = p.id_prestador
       AND pr.rubro = t.rubro_prestador
      WHERE d.id_requerimiento = r.id_requerimiento
        AND d.baja_fecha IS NULL
  )
  AND (
    -- Primer envio o recuperación: se listan todos los
    -- candidatos vigentes. registrar_cotizacion_prestador
    -- evita reenviar ENVIADO o PROCESANDO.
    r.estado = compras.id_estado_requerimiento('PENDIENTE')

        OR (
        r.estado = compras.id_estado_requerimiento('A_COTIZAR')
            AND (
            rcp.id_prestador IS NULL
                OR rcp.estado_envio IN (
                                        'PENDIENTE',
                                        'ERROR',
                                        'EMAIL_INVALIDO'
                )
            )
        )
    )
ORDER BY 6, 2;
END;
$function$;


CREATE OR REPLACE FUNCTION compras.registrar_cotizacion_prestador(p_id_requerimiento integer, p_id_prestador integer, p_usuario character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
v_email VARCHAR(320);
    v_usuario VARCHAR(100);
    v_reservado BOOLEAN;
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

SELECT
    compras.resolver_email_cotizacion_prestador(
            p_id_prestador
    )
INTO
    v_email
FROM compras.requerimiento r
         JOIN public.prestador p
              ON p.id_prestador =
                 p_id_prestador
                 AND p.baja_fecha IS NULL
                 AND COALESCE(
                         p.solicitar_cotizacion,
                         FALSE
                     ) = TRUE
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND r.estado IN (compras.id_estado_requerimiento('PENDIENTE'), compras.id_estado_requerimiento('A_COTIZAR'))
  AND r.baja_fecha IS NULL
  AND EXISTS (
      SELECT 1
      FROM compras.requerimiento_detalle d
      JOIN compras.tipo_prestacion t
        ON t.id_tipo_prestacion = d.id_tipo_prestacion
      JOIN public.prestador_rubro pr
        ON pr.id_prestador = p.id_prestador
       AND pr.rubro = t.rubro_prestador
      WHERE d.id_requerimiento = r.id_requerimiento
        AND d.baja_fecha IS NULL
  )
    LIMIT 1;

IF NOT FOUND THEN
        RETURN FALSE;
END IF;

    /*
     * Reserva atómica.
     *
     * INSERT nuevo:
     *   PROCESANDO, intento 1.
     *
     * Conflicto:
     *   solo vuelve a reservar PENDIENTE, ERROR o EMAIL_INVALIDO.
     *
     * ENVIADO y PROCESANDO no se vuelven a tomar. De esta forma dos
     * ejecuciones concurrentes no pueden obtener TRUE para la misma fila.
     */
INSERT INTO compras.requerimiento_cotizacion_prestador (
    id_requerimiento,
    id_prestador,
    estado_envio,
    intentos,
    email_destino,
    fecha_ultimo_intento,
    alta_usr
)
VALUES (
           p_id_requerimiento,
           p_id_prestador,
           'PROCESANDO',
           1,
           v_email,
           now(),
           v_usuario
       )
    ON CONFLICT (
        id_requerimiento,
        id_prestador
    )
    DO UPDATE
               SET estado_envio = 'PROCESANDO',
               intentos =
               compras.requerimiento_cotizacion_prestador.intentos
               + 1,
               email_destino = EXCLUDED.email_destino,
               fecha_ultimo_intento = now(),
               fecha_envio = NULL,
               ultimo_error = NULL,
               modi_fecha = now(),
               modi_usr = v_usuario
       WHERE compras.requerimiento_cotizacion_prestador.estado_envio
               IN (
               'PENDIENTE',
               'ERROR',
               'EMAIL_INVALIDO'
               )
               RETURNING TRUE
       INTO v_reservado;

RETURN COALESCE(
        v_reservado,
        FALSE
       );
END;
$function$;


CREATE FUNCTION compras.finalizar_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,
    p_estado VARCHAR,
    p_error TEXT
)
    RETURNS BOOLEAN
AS $func$
DECLARE
v_estado VARCHAR(20);
    v_error TEXT;
BEGIN
    v_estado := upper(
        btrim(
            COALESCE(p_estado, '')
        )
    );

    IF v_estado NOT IN (
        'ENVIADO',
        'ERROR',
        'EMAIL_INVALIDO'
    ) THEN
        RAISE EXCEPTION
            'Estado final de notificación inválido: %.',
            v_estado;
END IF;

    v_error := NULLIF(
        left(
            btrim(
                COALESCE(p_error, '')
            ),
            4000
        ),
        ''
    );

UPDATE compras.requerimiento_cotizacion_prestador
SET estado_envio = v_estado,

    fecha_envio =
        CASE
            WHEN v_estado = 'ENVIADO'
                THEN now()
            ELSE NULL
            END,

    ultimo_error =
        CASE
            WHEN v_estado = 'ENVIADO'
                THEN NULL
            ELSE v_error
            END,

    modi_fecha = now()

WHERE id_requerimiento =
      p_id_requerimiento
  AND id_prestador =
      p_id_prestador
  AND estado_envio =
      'PROCESANDO';

RETURN FOUND;
END;
$func$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION compras.listar_prestadores_enviados(p_id_requerimiento integer, p_limite integer)
 RETURNS TABLE(id_prestador integer, descripcion character varying, cuit character varying, email character varying, email_destino character varying, id_tipo_prestador integer, tipo_prestador character varying, estado_envio character varying)
 LANGUAGE plpgsql
 STABLE
AS $function$
DECLARE
v_limite INTEGER;
BEGIN
    IF p_id_requerimiento IS NULL
            OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

    IF p_limite IS NULL
            OR p_limite <= 0 THEN

        RAISE EXCEPTION
            'El limite de prestadores debe ser mayor que cero.';
END IF;

    v_limite := p_limite;

RETURN QUERY
SELECT DISTINCT
    p.id_prestador::INTEGER,
    p.descripcion::VARCHAR,
    p.cuit::VARCHAR,

    compras.resolver_emails_cotizacion_prestador(
            p.id_prestador
    )::VARCHAR AS email,

    NULLIF(
            btrim(rcp.email_destino),
            ''
    )::VARCHAR AS email_destino,

    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR,
    rcp.estado_envio::VARCHAR

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
  AND r.estado IN (compras.id_estado_requerimiento('A_COTIZAR'), compras.id_estado_requerimiento('COTIZADO'), compras.id_estado_requerimiento('RECLAMO_RP'), compras.id_estado_requerimiento('ORDEN_COMPRA'), compras.id_estado_requerimiento('ANULADO'))
  AND (
    r.estado <> compras.id_estado_requerimiento('A_COTIZAR')
        OR compras.es_prestador_compatible_cotizacion(
            r.id_requerimiento,
            p.id_prestador
        )
    )

ORDER BY
    2,
    3,
    1

    LIMIT v_limite;
END;
$function$;


CREATE OR REPLACE FUNCTION compras.hay_prestadores_pendientes_notificacion(
    p_id_requerimiento INTEGER
)
    RETURNS BOOLEAN
AS $func$
BEGIN
    IF p_id_requerimiento IS NULL
            OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

RETURN EXISTS (
    SELECT 1
    FROM compras.listar_prestadores_cotizacion_requerimiento(
            p_id_requerimiento
         )
);
END;
$func$
LANGUAGE plpgsql
STABLE;

-- =====================================================================
-- PDF
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.get_requerimiento_compra_pdf(p_id_requerimiento integer)
 RETURNS TABLE(id_requerimiento integer, alta_fecha timestamp without time zone, alta_usr character varying, id_estado integer, estado_descripcion character varying, id_sector integer, sector_descripcion character varying, requiere_afiliado boolean, afiliado_id_ospim integer, afiliado_int integer, afiliado_nombre_apellido character varying, afiliado_documento character varying, afiliado_direccion character varying, afiliado_localidad character varying, afiliado_provincia character varying, afiliado_celular character varying, afiliado_telefono character varying, afiliado_email character varying, afiliado_seccional character varying, cargo_ospim integer, cargo_tercerizadora integer, id_tercerizadora character varying, recupero boolean, surge boolean, observaciones text, detalle_id integer, detalle_orden integer, tipo_item character varying, codigo_item character varying, descripcion_item character varying, id_prestacion integer, id_tipo_nomenclador integer, codigo_nomenclador character varying, descripcion_nomenclador character varying, id_medicamento integer, troquel integer, nombre_medicamento character varying, cantidad integer, precio_unitario_estimado numeric, precio_total_estimado numeric, prestador_razon_social character varying, prestador_cuit character varying, detalle_observaciones text)
 LANGUAGE plpgsql
 STABLE
AS $function$
BEGIN

RETURN QUERY

SELECT
    rb.id,
    rb.alta_fecha,
    rb.alta_usr,

    rb.id_estado,
    rb.estado_descripcion,

    rb.id_sector,
    rb.sector_descripcion,
    rb.requiere_afiliado,

    rb.afiliado_id_ospim,
    rb.afiliado_int,
    rb.afiliado_nombre_apellido,
    rb.afiliado_documento,

    rb.afiliado_direccion,
    rb.afiliado_localidad,
    rb.afiliado_provincia,
    rb.afiliado_celular,
    rb.afiliado_telefono,
    rb.afiliado_email,

    sec.descripcion AS afiliado_seccional,

    rb.cargo_ospim,
    rb.cargo_tercerizadora,
    rb.id_tercerizadora,
    rb.recupero,
    rb.surge,
    rb.observaciones,

    d.id,

    CASE
        WHEN d.id IS NULL THEN NULL
        ELSE row_number() OVER (
            PARTITION BY rb.id
            ORDER BY d.id
        )::INTEGER
    END AS detalle_orden,

    d.tipo_item,
    d.codigo_item,
    d.descripcion_item,

    d.id_prestacion,
    d.id_tipo_nomenclador,
    d.codigo_nomenclador,
    d.descripcion_nomenclador,

    d.id_medicamento,
    d.troquel,
    d.nombre_medicamento,

    d.cantidad,

    d.precio_unitario_estimado,
    d.precio_total_estimado,

    d.prestador_razon_social,
    d.prestador_cuit,

    d.observaciones

FROM compras.requerimiento_base() rb

LEFT JOIN LATERAL compras.resolver_identidad_afiliado(rb.afiliado_cuil_titular,rb.afiliado_int) ident ON TRUE
LEFT JOIN public.afiliado afi
       ON afi.cuil_titular = ident.cuil_titular
      AND afi.inte = ident.inte

LEFT JOIN public.seccional sec
       ON sec.id_seccional = afi.id_seccional

LEFT JOIN compras.get_requerimiento_detalle(
    p_id_requerimiento
) d
       ON d.id_requerimiento = rb.id

WHERE rb.id = p_id_requerimiento

ORDER BY d.id NULLS LAST;

END;
$function$;

ALTER FUNCTION compras.get_requerimiento_compra_pdf(integer)
    OWNER TO postgres;

-- =====================================================================
-- FUNCIONES DE PRESUPUESTOS, VíNCULOS Y COTIZACIÓN
-- =====================================================================


CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(p_id_requerimiento integer, p_id_prestador integer, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_descripcion_prestador character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_estado_envio VARCHAR(20);
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_id_prestador IS NULL OR p_id_prestador <= 0 THEN
        RAISE EXCEPTION
            'El prestador informado no es válido.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN
        RAISE EXCEPTION
            'La identidad del documento de presupuesto no es válida.';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT r.estado
      INTO v_estado_requerimiento
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND OR v_estado_requerimiento <> compras.id_estado_requerimiento('A_COTIZAR') THEN
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
$function$;

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(p_id_requerimiento integer, p_tipo_documento smallint, p_id_prestador integer, p_empresa_cuit character varying, p_empresa_sucursal character varying, p_descripcion_empresa character varying, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_descripcion_prestador character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_permite_operacion BOOLEAN;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_tipo_documento IS NULL OR p_tipo_documento <> 3 THEN
        RAISE EXCEPTION
            'El tipo documental no corresponde a una cotización de Empresa.';
    END IF;

    IF p_id_prestador IS NOT NULL
       OR NULLIF(btrim(p_descripcion_prestador), '') IS NOT NULL THEN

        RAISE EXCEPTION
            'Una cotización de Empresa no puede asociarse a un prestador.';
    END IF;

    IF NULLIF(btrim(p_empresa_cuit), '') IS NULL
       OR length(btrim(p_empresa_cuit)) > 11
       OR NULLIF(btrim(p_empresa_sucursal), '') IS NULL
       OR length(btrim(p_empresa_sucursal)) > 6
       OR NULLIF(btrim(p_descripcion_empresa), '') IS NULL
       OR length(btrim(p_descripcion_empresa)) > 200 THEN

        RAISE EXCEPTION
            'La identidad de la Empresa de la cotización no es válida.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN

        RAISE EXCEPTION
            'La identidad del documento de cotización no es válida.';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT
        r.estado,
        sr.permite_cotizacion_empresa
      INTO
        v_estado_requerimiento,
        v_permite_operacion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> compras.id_estado_requerimiento('PENDIENTE')
       OR NOT v_permite_operacion THEN

        RAISE EXCEPTION
            'La cotización de Empresa requiere un requerimiento activo '
            'de RRHH o SISTEMAS en estado PENDIENTE.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.empresa_cuit = btrim(p_empresa_cuit)
           AND rp.empresa_sucursal = btrim(p_empresa_sucursal)
           AND rp.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene una cotización activa para este requerimiento.';
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
        tipo_documento,
        fecha_documento,
        id_prestador,
        empresa_cuit,
        empresa_sucursal,
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
        3,
        NULL,
        NULL,
        btrim(p_empresa_cuit),
        btrim(p_empresa_sucursal),
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        btrim(p_titulo),
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    RETURN v_id;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_orden_medica(p_id_requerimiento integer, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_fecha_documento date, p_numero_receta character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_cuil_titular VARCHAR(20);
    v_inte INTEGER;
    v_numero_receta VARCHAR;
    v_id_requerimiento_duplicado INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_fecha_documento IS NULL THEN
        RAISE EXCEPTION
            'Debe informar la fecha del adjunto.';
    END IF;

    IF p_dl_group_id IS NULL
       OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL
       OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL
       OR p_dl_file_entry_id <= 0
       OR NULLIF(btrim(p_dl_file_uuid), '') IS NULL THEN

        RAISE EXCEPTION
            'La identidad del adjunto no es válida.';
    END IF;

    IF NULLIF(btrim(p_nombre_original), '') IS NULL
       OR NULLIF(btrim(p_nombre_persistido), '') IS NULL THEN

        RAISE EXCEPTION
            'Los nombres del adjunto no son válidos.';
    END IF;

    /*
     * p_titulo es opcional y se conserva en la firma para no romper callers.
     * Si llega vacío se utiliza el nombre original como título técnico.
     */

    IF p_numero_receta IS NOT NULL
       AND length(p_numero_receta) > 100 THEN

        RAISE EXCEPTION
            'El número de receta admite hasta 100 caracteres.';
    END IF;

    v_numero_receta :=
        NULLIF(
            regexp_replace(
                upper(btrim(COALESCE(p_numero_receta, ''))),
                '[[:space:]]+',
                '',
                'g'
            ),
            ''
        );

    IF v_numero_receta IS NOT NULL
       AND length(v_numero_receta) > 100 THEN

        RAISE EXCEPTION
            'El número de receta admite hasta 100 caracteres.';
    END IF;

    v_usuario :=
        COALESCE(
            NULLIF(btrim(p_usuario), ''),
            'sistema'
        );

    SELECT
        r.estado,
        r.afiliado_cuil_titular,
        r.afiliado_int
    INTO
        v_estado_requerimiento,
        v_cuil_titular,
        v_inte
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo informado.';
    END IF;

    IF v_estado_requerimiento <> compras.id_estado_requerimiento('PENDIENTE') THEN
        RAISE EXCEPTION
            'El adjunto solo puede registrarse durante '
            'el alta de un requerimiento PENDIENTE.';
    END IF;

    IF v_cuil_titular IS NOT NULL THEN
        SELECT a.cuil_titular,a.inte INTO v_cuil_titular,v_inte
        FROM compras.resolver_identidad_afiliado(v_cuil_titular,v_inte) a;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'No se pudo resolver la identidad actual del afiliado.';
        END IF;
    END IF;

    /*
     * La receta es opcional. Si se informa y el requerimiento tiene
     * afiliado, el advisory lock serializa la clave funcional antes
     * de revalidar el duplicado exacto. Las colisiones del hash solo
     * amplían la serialización: nunca producen un falso duplicado.
     */
    IF v_numero_receta IS NOT NULL
       AND NULLIF(btrim(v_cuil_titular), '') IS NOT NULL
       AND v_inte IS NOT NULL THEN

        PERFORM pg_advisory_xact_lock(
            hashtext('compras.orden_medica.receta'),
            hashtext(
                v_cuil_titular
                || chr(31)
                || v_inte::VARCHAR
                || chr(31)
                || to_char(p_fecha_documento, 'YYYY-MM-DD')
                || chr(31)
                || v_numero_receta
            )
        );

        SELECT rp.id_requerimiento
        INTO v_id_requerimiento_duplicado
        FROM compras.requerimiento_presupuesto rp
        INNER JOIN compras.requerimiento r
            ON r.id_requerimiento = rp.id_requerimiento
        JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
        WHERE rp.tipo_documento = 2
          AND rp.baja_fecha IS NULL
          AND rp.numero_receta = v_numero_receta
          AND rp.fecha_documento = p_fecha_documento
          AND r.estado <> compras.id_estado_requerimiento('ANULADO')
          AND ident.cuil_titular = v_cuil_titular
          AND ident.inte = v_inte
        ORDER BY
            rp.id_requerimiento,
            rp.id_requerimiento_presupuesto
        LIMIT 1;

        IF FOUND THEN
            RAISE EXCEPTION
                'El adjunto ya fue cargado con fecha % y número de receta % en el requerimiento %.',
                to_char(p_fecha_documento, 'DD-MM-YYYY'),
                v_numero_receta,
                v_id_requerimiento_duplicado;
        END IF;
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
        tipo_documento,
        fecha_documento,
        numero_receta,
        id_prestador,
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
        2,
        p_fecha_documento,
        v_numero_receta,
        NULL,
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        btrim(p_dl_file_uuid),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        COALESCE(
            NULLIF(btrim(p_titulo), ''),
            btrim(p_nombre_original)
        ),
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    RETURN v_id;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.baja_requerimiento_presupuesto(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer, p_usuario character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
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

    IF NOT FOUND OR v_estado_requerimiento <> compras.id_estado_requerimiento('A_COTIZAR') THEN
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
$function$;

CREATE OR REPLACE FUNCTION compras.reactivar_requerimiento_presupuesto(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
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

    IF NOT FOUND OR v_estado_requerimiento <> compras.id_estado_requerimiento('A_COTIZAR') THEN
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
$function$;

CREATE OR REPLACE FUNCTION compras.baja_cotizacion_empresa_requerimiento(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer, p_usuario character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_estado_requerimiento INTEGER;
    v_permite_operacion BOOLEAN;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RETURN FALSE;
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT
        r.estado,
        sr.permite_cotizacion_empresa
      INTO
        v_estado_requerimiento,
        v_permite_operacion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> compras.id_estado_requerimiento('PENDIENTE')
       OR NOT v_permite_operacion THEN

        RAISE EXCEPTION
            'Las cotizaciones de Empresas solo pueden eliminarse '
            'en requerimientos PENDIENTES de RRHH o SISTEMAS.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET baja_fecha = now(),
           baja_usr = v_usuario
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3
       AND baja_fecha IS NULL;

    RETURN FOUND;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.reactivar_cotizacion_empresa_requerimiento(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_empresa_cuit VARCHAR(11);
    v_empresa_sucursal VARCHAR(6);
    v_estado_requerimiento INTEGER;
    v_permite_operacion BOOLEAN;
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RETURN FALSE;
    END IF;

    SELECT
        r.estado,
        sr.permite_cotizacion_empresa
      INTO
        v_estado_requerimiento,
        v_permite_operacion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> compras.id_estado_requerimiento('PENDIENTE')
       OR NOT v_permite_operacion THEN

        RAISE EXCEPTION
            'Las cotizaciones de Empresas solo pueden reactivarse '
            'en requerimientos PENDIENTES de RRHH o SISTEMAS.';
    END IF;

    SELECT
        rp.empresa_cuit,
        rp.empresa_sucursal
      INTO
        v_empresa_cuit,
        v_empresa_sucursal
      FROM compras.requerimiento_presupuesto rp
     WHERE rp.id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND rp.id_requerimiento = p_id_requerimiento
       AND rp.tipo_documento = 3
       AND rp.baja_fecha IS NOT NULL
     FOR UPDATE;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.empresa_cuit = v_empresa_cuit
           AND rp.empresa_sucursal = v_empresa_sucursal
           AND rp.baja_fecha IS NULL
           AND rp.id_requerimiento_presupuesto
                <> p_id_requerimiento_presupuesto
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene otra cotización activa para este requerimiento.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET baja_fecha = NULL,
           baja_usr = NULL
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3
       AND baja_fecha IS NOT NULL;

    RETURN FOUND;
END;
$function$;

-- =====================================================================
-- REQUERIMIENTO COTIZADO -> RECLAMO PRESTACIONAL
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.reservar_reclamo_prestacional(p_id_requerimiento integer, p_token_reserva character varying, p_usuario character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
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
            'No se pudo validar el contexto de creación del Reclamo Prestacional.';
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

    IF v_estado_requerimiento <> compras.id_estado_requerimiento('COTIZADO') THEN
        RAISE EXCEPTION
            'El requerimiento % no se encuentra COTIZADO.',
            p_id_requerimiento;
END IF;

    IF NULLIF(btrim(v_afiliado_cuil), '') IS NULL
            OR v_afiliado_int IS NULL THEN
        RAISE EXCEPTION
            'El requerimiento % no posee un afiliado válido.',
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
        'Ya existe una creación de Reclamo Prestacional en proceso para el requerimiento %.',
        p_id_requerimiento;
END;
$function$;

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
            'La reserva del requerimiento % no es válida.',
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

/*
 * ============================================================
 * 1. LISTADO DE CANDIDATOS
 * ============================================================
 *
 * Wrapper estable para el servicio Java.
 *
 * Se apoya en:
 *
 * compras.listar_prestadores_cotizacion_requerimiento(integer)
 *
 * El email singular del candidato se ignora: el flujo productivo resuelve
 * todos los destinatarios exclusivamente desde la fuente canónica plural.
 */
CREATE OR REPLACE FUNCTION
compras.listar_prestadores_notificacion_cotizacion(
    p_id_requerimiento INTEGER
)
RETURNS TABLE (
    id_prestador INTEGER,
    descripcion TEXT,
    cuit TEXT,
    email TEXT,
    id_tipo_prestador INTEGER,
    tipo_prestador TEXT
)
LANGUAGE sql
STABLE
AS
$function$
SELECT
    candidato.id_prestador::INTEGER,
    candidato.descripcion::TEXT,
    candidato.cuit::TEXT,
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
$function$;


/*
 * ============================================================
 * 2. DIAGNÓSTICO GENERAL
 * ============================================================
 *
 * Diferencia:
 *
 * - prestadores_habilitados:
 *   solicitar_cotizacion = true y sin baja.
 *
 * - prestadores_compatibles_sector:
 *   habilitados con al menos un rubro que coincide con un tipo de prestación
 *   activo del requerimiento. El nombre se conserva por contrato legacy.
 *
 * - prestadores_bloqueados_estado_previo:
 *   compatibles que ya estaban ENVIADO, COTIZADO o PROCESANDO antes
 *   de confeccionar la lista de candidatos.
 */
CREATE OR REPLACE FUNCTION compras.diagnosticar_prestadores_notificacion_cotizacion(p_id_requerimiento integer)
 RETURNS TABLE(id_sector integer, prestadores_habilitados integer, prestadores_compatibles_sector integer, prestadores_bloqueados_estado_previo integer)
 LANGUAGE sql
 STABLE
AS $function$
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
                         WHEN d_rubro.id_detalle IS NOT NULL
                             THEN p.id_prestador
        END
    )::INTEGER
        AS prestadores_compatibles_sector,

    COUNT(
            DISTINCT CASE
                         WHEN d_rubro.id_detalle IS NOT NULL
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

         LEFT JOIN public.prestador_rubro pr
                   ON pr.id_prestador = p.id_prestador

         LEFT JOIN compras.tipo_prestacion tp_rubro
                   ON pr.rubro =
                      tp_rubro.rubro_prestador

         LEFT JOIN compras.requerimiento_detalle d_rubro
                   ON d_rubro.id_requerimiento = r.id_requerimiento
                       AND d_rubro.id_tipo_prestacion =
                           tp_rubro.id_tipo_prestacion
                       AND d_rubro.baja_fecha IS NULL

         LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
                   ON rcp.id_requerimiento = r.id_requerimiento
                       AND rcp.id_prestador = p.id_prestador

WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL

GROUP BY
    r.id_sector;
$function$;


/*
 * ============================================================
 * 3. RESERVA ATÓMICA
 * ============================================================
 *
 * Devuelve:
 *
 * reservado = true
 *   La fila quedó PROCESANDO y esta ejecución obtuvo la
 *   reserva exclusiva.
 *
 * reservado = false
 *   No se debe intentar enviar. La causa queda informada
 *   mediante motivo_codigo y motivo_descripcion.
 *
 * La fila se bloquea con FOR UPDATE para impedir que dos
 * ejecuciones envíen simultáneamente al mismo prestador.
 */
CREATE OR REPLACE FUNCTION compras.reservar_notificacion_cotizacion_prestador(p_id_requerimiento integer, p_id_prestador integer, p_usuario character varying)
 RETURNS TABLE(reservado boolean, estado_envio text, email_destino text, motivo_codigo text, motivo_descripcion text)
 LANGUAGE plpgsql
AS $function$
DECLARE
v_usuario VARCHAR(100);
    v_estado_requerimiento INTEGER;
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
    r.estado
INTO
    v_estado_requerimiento
FROM compras.requerimiento r
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.baja_fecha IS NULL
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo %.',
            p_id_requerimiento;
END IF;

IF v_estado_requerimiento NOT IN (compras.id_estado_requerimiento('PENDIENTE'), compras.id_estado_requerimiento('A_COTIZAR')) THEN
        RAISE EXCEPTION
            'El requerimiento % no se encuentra disponible para notificar prestadores.',
            p_id_requerimiento;
END IF;

/*
 * Primero se valida la existencia funcional del prestador.
 *
 * No se usa el resultado de esta consulta para resolver
 * el email. La validación y la resolución son responsabilidades
 * separadas.
 */
PERFORM 1
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
        'o no está habilitado para cotizar.',
        p_id_prestador;
END IF;

/*
 * Se obtienen los destinatarios mediante la política canónica plural.
 *
 * Puede devolver NULL. Eso no constituye una falla de reserva:
 * el servicio Java debe clasificarlo como EMAIL_INVALIDO
 * o como advertencia cuando se utiliza el destino temporal.
 */
SELECT
    compras.resolver_emails_cotizacion_prestador(
            p_id_prestador
    )
INTO
    v_emails_reales;

IF NOT EXISTS (
    SELECT 1
    FROM compras.requerimiento_detalle d
    JOIN compras.tipo_prestacion tp
      ON tp.id_tipo_prestacion = d.id_tipo_prestacion
    JOIN public.prestador_rubro pr
      ON pr.id_prestador = p_id_prestador
     AND pr.rubro = tp.rubro_prestador
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL
) THEN
    RAISE EXCEPTION
        'El prestador % no posee un rubro compatible con los tipos de prestación activos del requerimiento %.',
        p_id_prestador,
        p_id_requerimiento;
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
$function$;

/*
 * ============================================================
 * 4. FINALIZACIÓN ATÓMICA
 * ============================================================
 *
 * Sólo permite finalizar una fila que continúa PROCESANDO.
 *
 * Estados finales aceptados:
 *
 * ENVIADO
 * ERROR
 * EMAIL_INVALIDO
 */
;

CREATE OR REPLACE FUNCTION compras.finalizar_notificacion_cotizacion_prestador(
    p_id_requerimiento integer,
    p_id_prestador integer,
    p_estado character varying,
    p_error text,
    p_usuario character varying
)
RETURNS TABLE(
    actualizado boolean,
    estado_anterior text,
    estado_actual text,
    motivo text
)
LANGUAGE 'plpgsql'
COST 100
VOLATILE
ROWS 1000
AS $function$

DECLARE
    v_usuario VARCHAR(100);
    v_estado_solicitado VARCHAR(20);
    v_estado_anterior VARCHAR(20);
    v_error TEXT;

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


    v_estado_solicitado :=
        UPPER(
            BTRIM(
                COALESCE(
                    p_estado,
                    ''
                )
            )
        );


    IF v_estado_solicitado NOT IN (
        'ENVIADO',
        'ERROR',
        'EMAIL_INVALIDO'
    ) THEN

        RAISE EXCEPTION
            'Estado final no permitido: %.',
            v_estado_solicitado;

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


    v_error :=
        CASE
            WHEN p_error IS NULL THEN
                NULL
            ELSE
                LEFT(
                    p_error,
                    4000
                )
        END;


    SELECT
        rcp.estado_envio
    INTO
        v_estado_anterior
    FROM compras.requerimiento_cotizacion_prestador rcp
    WHERE rcp.id_requerimiento =
          p_id_requerimiento
      AND rcp.id_prestador =
          p_id_prestador
    FOR UPDATE;


    IF NOT FOUND THEN

        RETURN QUERY
        SELECT
            FALSE,
            NULL::TEXT,
            NULL::TEXT,
            (
                'No existe una fila de notificación '
                    || 'para el requerimiento y prestador.'
            )::TEXT;

        RETURN;

    END IF;


    IF v_estado_anterior <> 'PROCESANDO' THEN

        RETURN QUERY
        SELECT
            FALSE,
            v_estado_anterior::TEXT,
            v_estado_anterior::TEXT,
            (
                'La fila no se encontraba PROCESANDO. '
                    || 'No se modifico el estado.'
            )::TEXT;

        RETURN;

    END IF;


    UPDATE compras.requerimiento_cotizacion_prestador
    SET
        estado_envio =
            v_estado_solicitado,

        fecha_envio =
            CASE
                WHEN v_estado_solicitado = 'ENVIADO'
                    THEN clock_timestamp()
                ELSE NULL
            END,

        ultimo_error =
            CASE
                WHEN v_estado_solicitado = 'ENVIADO'
                    THEN v_error
                ELSE COALESCE(
                    v_error,
                    'Error sin detalle informado.'
                )
            END,

        modi_fecha =
            clock_timestamp(),

        modi_usr =
            v_usuario

    WHERE id_requerimiento =
          p_id_requerimiento
      AND id_prestador =
          p_id_prestador
      AND estado_envio =
          'PROCESANDO';


    IF NOT FOUND THEN

        RETURN QUERY
        SELECT
            FALSE,
            v_estado_anterior::TEXT,
            v_estado_anterior::TEXT,
            (
                'La fila cambio de estado antes de '
                    || 'completar la finalización.'
            )::TEXT;

        RETURN;

    END IF;


    RETURN QUERY
    SELECT
        TRUE,
        v_estado_anterior::TEXT,
        v_estado_solicitado::TEXT,
        (
            'El estado final fue persistido correctamente.'
        )::TEXT;

END;

$function$;


ALTER FUNCTION compras.finalizar_notificacion_cotizacion_prestador(
    integer,
    integer,
    character varying,
    text,
    character varying
)
OWNER TO postgres;

/*
 * ============================================================
 * 5. VALIDACIÓNES POSTERIORES A LA INSTALACIÓN
 * ============================================================
 */

/*
 * No ejecutar las pruebas de reserva/finalización sobre un
 * requerimiento productivo sin reemplazar los identificadores.
 *
 * Ejemplo:
 *
 * SELECT *
 * FROM compras.reservar_notificacion_cotizacion_prestador(
 *     1,
 *     123,
 *     'prueba_sql'
 * );
 *
 * SELECT *
 * FROM compras.finalizar_notificacion_cotizacion_prestador(
 *     1,
 *     123,
 *     'ERROR',
 *     'Prueba controlada de instalación.',
 *     'prueba_sql'
 * );
 */
-- =====================================================================
-- =====================================================================
-- DOCUMENTOS DE PEDIDO DE COTIZACIÓN
-- =====================================================================

CREATE FUNCTION compras.registrar_pedido_cotizacion_documento(
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
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    IF p_id_prestador IS NULL OR p_id_prestador <= 0 THEN
        RAISE EXCEPTION
            'Debe informar el prestador.';
    END IF;

    IF p_dl_group_id IS NULL
       OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL
       OR p_dl_folder_id <= 0
       OR p_dl_file_entry_id IS NULL
       OR p_dl_file_entry_id <= 0
       OR NULLIF(btrim(p_dl_file_uuid), '') IS NULL THEN

        RAISE EXCEPTION
            'La identidad del pedido de cotización en Document Library no es válida.';
    END IF;

    IF NULLIF(btrim(p_nombre_original), '') IS NULL
       OR NULLIF(btrim(p_nombre_persistido), '') IS NULL
       OR NULLIF(btrim(p_titulo), '') IS NULL THEN

        RAISE EXCEPTION
            'Los datos documentales del pedido de cotización no son válidos.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT rcp.estado_envio, rcp.intentos
    INTO v_estado_envio, v_intento
    FROM compras.requerimiento_cotizacion_prestador rcp
    WHERE rcp.id_requerimiento = p_id_requerimiento
      AND rcp.id_prestador = p_id_prestador
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe la notificación del prestador para el requerimiento.';
    END IF;

    IF v_estado_envio <> 'PROCESANDO' THEN
        RAISE EXCEPTION
            'El pedido de cotización solo puede registrarse durante un envio PROCESANDO.';
    END IF;

    IF v_intento IS NULL OR v_intento <= 0 THEN
        RAISE EXCEPTION
            'La notificación no posee un número de intento válido.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM compras.requerimiento_pedido_cotizacion pc
        WHERE pc.id_requerimiento = p_id_requerimiento
          AND pc.id_prestador = p_id_prestador
          AND pc.intento = v_intento
          AND pc.dl_group_id = p_dl_group_id
          AND pc.dl_folder_id = p_dl_folder_id
          AND pc.dl_file_entry_id = p_dl_file_entry_id
          AND pc.dl_file_uuid = btrim(p_dl_file_uuid)
          AND pc.nombre_original = btrim(p_nombre_original)
          AND pc.nombre_persistido = btrim(p_nombre_persistido)
          AND pc.titulo = btrim(p_titulo)
    ) THEN
        RETURN v_intento;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM compras.requerimiento_pedido_cotizacion pc
        WHERE pc.id_requerimiento = p_id_requerimiento
          AND pc.id_prestador = p_id_prestador
          AND pc.intento = v_intento
    ) THEN
        RAISE EXCEPTION
            'El intento actual ya posee otro pedido de cotización registrado.';
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
    ) VALUES (
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

-- =====================================================================
-- COMPATIBILIDAD DE PRESTADOR POR RUBRO Y TIPO DE PRESTACIÓN
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.es_prestador_compatible_cotizacion(p_id_requerimiento integer, p_id_prestador integer)
 RETURNS boolean
 LANGUAGE sql
 STABLE
AS $function$
    SELECT EXISTS (
        SELECT 1
        FROM compras.requerimiento r
        JOIN public.prestador p
          ON p.id_prestador = p_id_prestador
         AND p.baja_fecha IS NULL
         AND COALESCE(p.solicitar_cotizacion, FALSE) = TRUE
        JOIN compras.requerimiento_detalle d
          ON d.id_requerimiento = r.id_requerimiento
         AND d.baja_fecha IS NULL
        JOIN compras.tipo_prestacion tp
          ON tp.id_tipo_prestacion = d.id_tipo_prestacion
        JOIN public.prestador_rubro pr
          ON pr.id_prestador = p.id_prestador
         AND pr.rubro = tp.rubro_prestador
        WHERE r.id_requerimiento = p_id_requerimiento
          AND r.baja_fecha IS NULL
    );
$function$;

;

;

-- =====================================================================
-- BAJAS DIFERIDAS + SURGE + COTIZACIÓN: UNA SOLA TRANSACCIÓN
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.guardar_cotizacion_requerimiento(p_id_requerimiento integer, p_ids_detalle integer[], p_precios_unitarios numeric[], p_ids_detalle_eliminados integer[], p_id_prestador integer, p_surge boolean, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_estado INTEGER;
    v_total_activos INTEGER;
    v_total_conservados INTEGER;
    v_total_eliminados INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT r.estado
    INTO v_estado
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe el requerimiento activo informado.';
    END IF;

    IF v_estado = compras.id_estado_requerimiento('COTIZADO') THEN
        RETURN compras.id_estado_requerimiento('COTIZADO');
    END IF;

    IF v_estado <> compras.id_estado_requerimiento('A_COTIZAR') THEN
        RAISE EXCEPTION
            'La cotización sólo puede guardarse en estado A COTIZAR.';
    END IF;

    PERFORM 1
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL
    ORDER BY d.id_detalle
    FOR UPDATE;

    SELECT COUNT(*)
    INTO v_total_activos
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL;

    v_total_conservados := COALESCE(array_length(p_ids_detalle, 1), 0);
    v_total_eliminados := COALESCE(array_length(p_ids_detalle_eliminados, 1), 0);

    IF v_total_conservados <= 0 THEN
        RAISE EXCEPTION
            'Debe conservar al menos una prestación antes de guardar la cotización.';
    END IF;

    IF v_total_conservados
       <> COALESCE(array_length(p_precios_unitarios, 1), 0) THEN
        RAISE EXCEPTION
            'La cantidad de prestaciones y precios no coincide.';
    END IF;

    IF v_total_activos <> v_total_conservados + v_total_eliminados THEN
        RAISE EXCEPTION
            'La lista final no coincide con las prestaciones activas.';
    END IF;

    IF (SELECT COUNT(*) FROM unnest(p_ids_detalle) AS ids(id_detalle))
       <> (SELECT COUNT(DISTINCT id_detalle)
           FROM unnest(p_ids_detalle) AS ids(id_detalle))
       OR (SELECT COUNT(*)
           FROM unnest(p_ids_detalle_eliminados) AS ids(id_detalle))
       <> (SELECT COUNT(DISTINCT id_detalle)
           FROM unnest(p_ids_detalle_eliminados) AS ids(id_detalle))
       OR EXISTS (
           SELECT 1
           FROM unnest(p_ids_detalle) AS conservados(id_detalle)
           JOIN unnest(p_ids_detalle_eliminados) AS eliminados(id_detalle)
             ON eliminados.id_detalle = conservados.id_detalle
       ) THEN
        RAISE EXCEPTION
            'La lista de prestaciones contiene IDs repetidos o superpuestos.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM (
            SELECT unnest(p_ids_detalle) AS id_detalle
            UNION ALL
            SELECT unnest(p_ids_detalle_eliminados) AS id_detalle
        ) recibidos
        LEFT JOIN compras.requerimiento_detalle d
          ON d.id_detalle = recibidos.id_detalle
         AND d.id_requerimiento = p_id_requerimiento
         AND d.baja_fecha IS NULL
        WHERE recibidos.id_detalle IS NULL
           OR recibidos.id_detalle <= 0
           OR d.id_detalle IS NULL
    ) THEN
        RAISE EXCEPTION
            'La lista de prestaciones fue manipulada o quedó desactualizada.';
    END IF;

    UPDATE compras.requerimiento_detalle
    SET baja_fecha = now(),
        baja_usr = v_usuario,
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id_requerimiento
      AND id_detalle = ANY(p_ids_detalle_eliminados)
      AND baja_fecha IS NULL;

    IF FOUND AND (
        SELECT COUNT(*)
        FROM compras.requerimiento_detalle
        WHERE id_requerimiento = p_id_requerimiento
          AND baja_fecha IS NULL
    ) <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento no puede quedar sin prestaciones activas.';
    END IF;

    UPDATE compras.requerimiento
    SET surge = COALESCE(p_surge, surge),
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id_requerimiento
      AND estado = compras.id_estado_requerimiento('A_COTIZAR')
      AND baja_fecha IS NULL;

    IF p_id_prestador IS NOT NULL
       AND NOT compras.es_prestador_compatible_cotizacion(
           p_id_requerimiento,
           p_id_prestador
       ) THEN
        RAISE EXCEPTION
            'El prestador adjudicado no es compatible con las prestaciones conservadas.';
    END IF;

    RETURN compras.finalizar_cotizacion_requerimiento(
        p_id_requerimiento,
        p_ids_detalle,
        p_precios_unitarios,
        p_id_prestador,
        v_usuario
    );
END;
$function$;

CREATE FUNCTION compras.guardar_cotizacion_requerimiento_call(
    p_id_requerimiento INTEGER,
    p_ids_detalle_array VARCHAR,
    p_precios_unitarios_array VARCHAR,
    p_ids_detalle_eliminados_array VARCHAR,
    p_id_prestador INTEGER,
    p_surge BOOLEAN,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
    SELECT compras.guardar_cotizacion_requerimiento(
        p_id_requerimiento,
        CAST(p_ids_detalle_array AS INTEGER[]),
        CAST(p_precios_unitarios_array AS NUMERIC[]),
        CAST(COALESCE(NULLIF(BTRIM(p_ids_detalle_eliminados_array), ''), '{}') AS INTEGER[]),
        p_id_prestador,
        p_surge,
        p_usuario
    );
$func$
LANGUAGE sql
VOLATILE;

-- Funciones canónicas consumidas por las fachadas JDBC de Compras.
CREATE OR REPLACE FUNCTION compras.buscar_items_historicos_afiliado(p_cuil_titular character varying, p_inte integer, p_id_sector integer, p_id_requerimiento_excluir integer, p_limite integer)
 RETURNS TABLE(id_prestacion integer, id_tipo_nomenclador integer, codigo character varying, descripcion character varying)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        historico.id_prestacion,
        historico.id_tipo_nomenclador,
        historico.codigo,
        historico.descripcion
    FROM (
        SELECT DISTINCT ON (d.id_prestacion, n.id_tipo_nomenclador)
            d.id_prestacion,
            n.id_tipo_nomenclador,
            NULLIF(BTRIM(n.codigo), '') AS codigo,
            NULLIF(BTRIM(n.descripcion), '') AS descripcion,
            r.alta_fecha AS fecha_requerimiento,
            r.id_requerimiento AS id_requerimiento_origen,
            d.id_detalle AS id_detalle_origen
        FROM compras.requerimiento r
        JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
        INNER JOIN compras.requerimiento_detalle d
            ON d.id_requerimiento = r.id_requerimiento
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
        WHERE ident.cuil_titular = p_cuil_titular
          AND ident.inte = p_inte
          AND r.id_sector = p_id_sector
          AND r.id_requerimiento <> p_id_requerimiento_excluir
          AND r.baja_fecha IS NULL
          AND d.baja_fecha IS NULL
          AND d.tipo_item = 'NOMENCLADOR'
          AND d.id_prestacion IS NOT NULL
          AND d.id_prestacion > 0
          AND n.id_tipo_nomenclador IS NOT NULL
          AND n.id_tipo_nomenclador > 0
          AND NULLIF(BTRIM(n.codigo), '') IS NOT NULL
          AND NULLIF(BTRIM(n.descripcion), '') IS NOT NULL
        ORDER BY
            d.id_prestacion,
            n.id_tipo_nomenclador,
            r.alta_fecha DESC NULLS LAST,
            r.id_requerimiento DESC,
            d.id_detalle DESC
    ) historico
    ORDER BY
        historico.fecha_requerimiento DESC NULLS LAST,
        historico.id_requerimiento_origen DESC,
        historico.id_detalle_origen DESC
    LIMIT p_limite;
$function$;

CREATE OR REPLACE FUNCTION compras.buscar_items_historicos_afiliado_clasificado(p_cuil_titular character varying, p_inte integer, p_id_sector integer, p_id_requerimiento_excluir integer, p_limite integer)
 RETURNS TABLE(id_prestacion integer, id_tipo_nomenclador integer, codigo character varying, descripcion character varying, id_tipo_prestacion integer)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        historico.id_prestacion,
        historico.id_tipo_nomenclador,
        historico.codigo,
        historico.descripcion,
        historico.id_tipo_prestacion
    FROM (
        SELECT DISTINCT ON (d.id_prestacion, n.id_tipo_nomenclador)
            d.id_prestacion,
            n.id_tipo_nomenclador,
            NULLIF(BTRIM(n.codigo), '')::VARCHAR AS codigo,
            NULLIF(BTRIM(n.descripcion), '')::VARCHAR AS descripcion,
            d.id_tipo_prestacion::INTEGER AS id_tipo_prestacion,
            r.alta_fecha AS fecha_requerimiento,
            r.id_requerimiento AS id_requerimiento_origen,
            d.id_detalle AS id_detalle_origen
        FROM compras.requerimiento r
        JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
        INNER JOIN compras.requerimiento_detalle d
            ON d.id_requerimiento = r.id_requerimiento
        LEFT JOIN autorizaciones.nomenclador n ON n.id_prestacion = d.id_prestacion
        WHERE ident.cuil_titular = p_cuil_titular
          AND ident.inte = p_inte
          AND r.id_sector = p_id_sector
          AND r.id_requerimiento <> p_id_requerimiento_excluir
          AND r.baja_fecha IS NULL
          AND d.baja_fecha IS NULL
          AND d.tipo_item = 'NOMENCLADOR'
          AND d.id_prestacion IS NOT NULL
          AND d.id_prestacion > 0
          AND n.id_tipo_nomenclador IS NOT NULL
          AND n.id_tipo_nomenclador > 0
          AND NULLIF(BTRIM(n.codigo), '') IS NOT NULL
          AND NULLIF(BTRIM(n.descripcion), '') IS NOT NULL
        ORDER BY
            d.id_prestacion,
            n.id_tipo_nomenclador,
            r.alta_fecha DESC NULLS LAST,
            r.id_requerimiento DESC,
            d.id_detalle DESC
    ) historico
    ORDER BY
        historico.fecha_requerimiento DESC NULLS LAST,
        historico.id_requerimiento_origen DESC,
        historico.id_detalle_origen DESC
    LIMIT p_limite;
$function$;

-- Regla canónica de duplicados: persona + prestación + fecha de Orden Médica.
CREATE OR REPLACE FUNCTION compras.existe_requerimiento_duplicado(p_cuil_titular character varying, p_inte integer, p_id_prestacion integer, p_fecha_orden_medica date, p_id_requerimiento_excluir integer)
 RETURNS boolean
 LANGUAGE sql
 STABLE
AS $function$
SELECT
    CASE
        WHEN NULLIF(btrim(p_cuil_titular), '') IS NULL
            OR p_inte IS NULL
            OR p_inte < 0
            OR p_id_prestacion IS NULL
            OR p_id_prestacion <= 0
            OR p_fecha_orden_medica IS NULL
            THEN FALSE
        ELSE EXISTS (
            SELECT 1
            FROM compras.requerimiento r
        JOIN LATERAL compras.resolver_identidad_afiliado(r.afiliado_cuil_titular,r.afiliado_int) ident ON TRUE
            INNER JOIN compras.requerimiento_detalle d
                ON d.id_requerimiento = r.id_requerimiento
            INNER JOIN compras.requerimiento_presupuesto rp
                ON rp.id_requerimiento = r.id_requerimiento
            WHERE r.baja_fecha IS NULL
              AND r.estado <> compras.id_estado_requerimiento('ANULADO')
              AND ident.cuil_titular = btrim(p_cuil_titular)
              AND ident.inte = p_inte
              AND d.baja_fecha IS NULL
              AND d.tipo_item = 'NOMENCLADOR'
              AND d.id_prestacion = p_id_prestacion
              AND rp.baja_fecha IS NULL
              AND rp.tipo_documento = 2
              AND rp.fecha_documento = p_fecha_orden_medica
              AND (
                    p_id_requerimiento_excluir IS NULL
                    OR p_id_requerimiento_excluir <= 0
                    OR r.id_requerimiento <> p_id_requerimiento_excluir
                  )
            LIMIT 1
        )
    END;
$function$;

-- =====================================================================
-- BÚSQUEDA TÉCNICA DE PRESTACIONES MÉDICAS
-- =====================================================================




-- =====================================================================
-- CONSULTAS JDBC LEGACY: ACCESO EXCLUSIVO MEDIANTE CALL
-- =====================================================================

CREATE OR REPLACE FUNCTION compras.listar_sectores_requerimiento()
 RETURNS TABLE(id integer, descripcion character varying, requiere_afiliado boolean, tipo_item varchar, seleccionable_alta boolean, permite_cotizacion_empresa boolean, permite_orden_compra_directa boolean, busqueda_nomenclador_medica boolean, permite_medicamento_legacy boolean, sector_reclamo_prestacional varchar, nomencladores integer[])
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        s.id_sector::INTEGER AS id,
        s.descripcion::VARCHAR AS descripcion,
        s.requiere_afiliado,
        s.tipo_item,
        s.seleccionable_alta,
        s.permite_cotizacion_empresa,
        s.permite_orden_compra_directa,
        s.busqueda_nomenclador_medica,
        s.permite_medicamento_legacy,
        s.sector_reclamo_prestacional,
        ARRAY(SELECT DISTINCT c.id_tipo_nomenclador FROM compras.tipo_prestacion t JOIN compras.tipo_prestacion_tipo_nomenclador c ON c.id_tipo_prestacion=t.id_tipo_prestacion WHERE t.id_sector=s.id_sector ORDER BY c.id_tipo_nomenclador)
    FROM compras.sector_requerimiento s
    WHERE compras.es_sector_seleccionable_compras(s.id_sector)
    ORDER BY s.descripcion;
$function$;


CREATE OR REPLACE FUNCTION compras.listar_tipos_prestacion()
 RETURNS TABLE(id_tipo_prestacion integer, descripcion character varying, id_sector integer, sector_descripcion character varying, rubro_prestador varchar, nomencladores integer[])
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        t.id_tipo_prestacion::INTEGER AS id_tipo_prestacion,
        t.descripcion::VARCHAR AS descripcion,
        t.id_sector::INTEGER AS id_sector,
        s.descripcion::VARCHAR AS sector_descripcion,
        t.rubro_prestador,
        ARRAY(SELECT c.id_tipo_nomenclador FROM compras.tipo_prestacion_tipo_nomenclador c WHERE c.id_tipo_prestacion = t.id_tipo_prestacion ORDER BY c.id_tipo_nomenclador)
    FROM compras.tipo_prestacion t
    JOIN compras.sector_requerimiento s
      ON s.id_sector = t.id_sector
    WHERE s.activo = TRUE
      AND s.baja_fecha IS NULL
    ORDER BY t.id_tipo_prestacion;
$function$;


CREATE OR REPLACE FUNCTION compras.get_estado_requerimiento(p_id_requerimiento integer)
 RETURNS TABLE(id integer, descripcion varchar, codigo varchar, orden smallint, activo boolean, descripcion_visual varchar)
 LANGUAGE sql
 STABLE
AS $function$
SELECT e.id_estado, e.descripcion, e.codigo, e.orden, e.activo, COALESCE(e.descripcion_visual,e.descripcion) FROM compras.estado_requerimiento e JOIN compras.requerimiento r ON r.estado = e.id_estado WHERE r.id_requerimiento = p_id_requerimiento;
$function$;


CREATE OR REPLACE FUNCTION compras.get_sector_requerimiento(p_id_sector integer)
 RETURNS TABLE(id integer, descripcion character varying, requiere_afiliado boolean, tipo_item varchar, seleccionable_alta boolean, permite_cotizacion_empresa boolean, permite_orden_compra_directa boolean, busqueda_nomenclador_medica boolean, permite_medicamento_legacy boolean, sector_reclamo_prestacional varchar, nomencladores integer[])
 LANGUAGE sql
 STABLE
AS $function$
    SELECT
        s.id_sector::INTEGER AS id,
        s.descripcion::VARCHAR AS descripcion,
        s.requiere_afiliado,
        s.tipo_item,
        s.seleccionable_alta,
        s.permite_cotizacion_empresa,
        s.permite_orden_compra_directa,
        s.busqueda_nomenclador_medica,
        s.permite_medicamento_legacy,
        s.sector_reclamo_prestacional,
        ARRAY(SELECT DISTINCT c.id_tipo_nomenclador FROM compras.tipo_prestacion t JOIN compras.tipo_prestacion_tipo_nomenclador c ON c.id_tipo_prestacion=t.id_tipo_prestacion WHERE t.id_sector=s.id_sector ORDER BY c.id_tipo_nomenclador)
    FROM compras.sector_requerimiento s
    WHERE s.id_sector = $1
      AND s.activo = TRUE
      AND s.baja_fecha IS NULL;
$function$;


CREATE OR REPLACE FUNCTION compras.listar_documentos_requerimiento(p_id_requerimiento integer, p_tipo_documento integer)
 RETURNS TABLE(
    id_requerimiento_presupuesto integer,
    id_requerimiento integer,
    tipo_documento smallint,
    fecha_documento date,
    numero_receta character varying(100),
    id_prestador integer,
    empresa_cuit character varying(11),
    empresa_sucursal character varying(6),
    descripcion_empresa character varying(200),
    dl_group_id bigint,
    dl_folder_id bigint,
    dl_file_entry_id bigint,
    dl_file_uuid character varying(75),
    nombre_original character varying(255),
    nombre_persistido character varying(255),
    titulo character varying(240),
    descripcion_prestador character varying(500),
    alta_fecha timestamp without time zone,
    alta_usr character varying(100),
    baja_fecha timestamp without time zone,
    baja_usr character varying(100)
)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.id_requerimiento_presupuesto,
        rp.id_requerimiento,
        rp.tipo_documento,
        rp.fecha_documento,
        rp.numero_receta,
        rp.id_prestador,
        rp.empresa_cuit,
        rp.empresa_sucursal,
        e.razon_soc::varchar,
        rp.dl_group_id,
        rp.dl_folder_id,
        rp.dl_file_entry_id,
        rp.dl_file_uuid,
        rp.nombre_original,
        rp.nombre_persistido,
        rp.titulo,
        p.descripcion::varchar,
        rp.alta_fecha,
        rp.alta_usr,
        rp.baja_fecha,
        rp.baja_usr
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    LEFT JOIN informacion_afip.empresa e ON e.cuit = rp.empresa_cuit AND e.sucursal = rp.empresa_sucursal
    WHERE rp.id_requerimiento = $1
      AND rp.tipo_documento = $2
      AND rp.baja_fecha IS NULL
    ORDER BY
        rp.alta_fecha DESC,
        rp.id_requerimiento_presupuesto DESC;
$function$;


CREATE OR REPLACE FUNCTION compras.get_documento_requerimiento(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer, p_tipo_documento integer)
 RETURNS TABLE(
    id_requerimiento_presupuesto integer,
    id_requerimiento integer,
    tipo_documento smallint,
    fecha_documento date,
    numero_receta character varying(100),
    id_prestador integer,
    empresa_cuit character varying(11),
    empresa_sucursal character varying(6),
    descripcion_empresa character varying(200),
    dl_group_id bigint,
    dl_folder_id bigint,
    dl_file_entry_id bigint,
    dl_file_uuid character varying(75),
    nombre_original character varying(255),
    nombre_persistido character varying(255),
    titulo character varying(240),
    descripcion_prestador character varying(500),
    alta_fecha timestamp without time zone,
    alta_usr character varying(100),
    baja_fecha timestamp without time zone,
    baja_usr character varying(100)
)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.id_requerimiento_presupuesto,
        rp.id_requerimiento,
        rp.tipo_documento,
        rp.fecha_documento,
        rp.numero_receta,
        rp.id_prestador,
        rp.empresa_cuit,
        rp.empresa_sucursal,
        e.razon_soc::varchar,
        rp.dl_group_id,
        rp.dl_folder_id,
        rp.dl_file_entry_id,
        rp.dl_file_uuid,
        rp.nombre_original,
        rp.nombre_persistido,
        rp.titulo,
        p.descripcion::varchar,
        rp.alta_fecha,
        rp.alta_usr,
        rp.baja_fecha,
        rp.baja_usr
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    LEFT JOIN informacion_afip.empresa e ON e.cuit = rp.empresa_cuit AND e.sucursal = rp.empresa_sucursal
    WHERE rp.id_requerimiento_presupuesto = $1
      AND rp.id_requerimiento = $2
      AND rp.tipo_documento = $3
      AND rp.baja_fecha IS NULL;
$function$;


CREATE OR REPLACE FUNCTION compras.listar_ordenes_medicas_requerimiento(p_id_requerimiento integer)
 RETURNS TABLE(
    id_requerimiento_presupuesto integer,
    id_requerimiento integer,
    tipo_documento smallint,
    fecha_documento date,
    numero_receta character varying(100),
    id_prestador integer,
    empresa_cuit character varying(11),
    empresa_sucursal character varying(6),
    descripcion_empresa character varying(200),
    dl_group_id bigint,
    dl_folder_id bigint,
    dl_file_entry_id bigint,
    dl_file_uuid character varying(75),
    nombre_original character varying(255),
    nombre_persistido character varying(255),
    titulo character varying(240),
    descripcion_prestador character varying(500),
    alta_fecha timestamp without time zone,
    alta_usr character varying(100),
    baja_fecha timestamp without time zone,
    baja_usr character varying(100)
)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.id_requerimiento_presupuesto,
        rp.id_requerimiento,
        rp.tipo_documento,
        rp.fecha_documento,
        rp.numero_receta,
        rp.id_prestador,
        rp.empresa_cuit,
        rp.empresa_sucursal,
        e.razon_soc::varchar,
        rp.dl_group_id,
        rp.dl_folder_id,
        rp.dl_file_entry_id,
        rp.dl_file_uuid,
        rp.nombre_original,
        rp.nombre_persistido,
        rp.titulo,
        p.descripcion::varchar,
        rp.alta_fecha,
        rp.alta_usr,
        rp.baja_fecha,
        rp.baja_usr
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    LEFT JOIN informacion_afip.empresa e ON e.cuit = rp.empresa_cuit AND e.sucursal = rp.empresa_sucursal
    WHERE rp.id_requerimiento = $1
      AND rp.tipo_documento = 2
      AND rp.baja_fecha IS NULL;
$function$;


CREATE FUNCTION compras.tiene_situacion_medica_vigente(
    p_cuil_titular VARCHAR,
    p_inte INTEGER
)
RETURNS BOOLEAN
AS $func$
    SELECT EXISTS (
        SELECT 1
        FROM public.afi_situ_medica sm
        WHERE sm.cuil_titular = $1
          AND sm.inte = $2
          AND sm.baja_fecha IS NULL
          AND (
                sm.vigen_hasta IS NULL
                OR sm.vigen_hasta > CURRENT_DATE
              )
    );
$func$
LANGUAGE sql
STABLE;


CREATE FUNCTION compras.listar_prestadores_adjudicados(
    p_id_requerimiento INTEGER
)
RETURNS TABLE (
    id_prestador INTEGER
)
AS $func$
    SELECT DISTINCT
        d.id_prestador::INTEGER AS id_prestador
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = $1
      AND d.baja_fecha IS NULL;
$func$
LANGUAGE sql
STABLE;


CREATE OR REPLACE FUNCTION compras.listar_prestadores_adjudicados_batch(p_ids_requerimientos text)
 RETURNS TABLE(id_requerimiento integer, id_prestador integer, descripcion character varying)
 LANGUAGE sql
 STABLE
AS $function$
    -- Misma identidad que listar_prestadores_adjudicados: detalle activo.
    -- DISTINCT incluye NULL para detectar detalles parcialmente adjudicados.
    -- No se une a presupuestos/documentos: no multiplican adjudicatarios.
    SELECT DISTINCT d.id_requerimiento::INTEGER,
           d.id_prestador::INTEGER,
           p.descripcion::VARCHAR
      FROM compras.requerimiento_detalle d
      JOIN compras.requerimiento r ON r.id_requerimiento = d.id_requerimiento
      LEFT JOIN public.prestador p ON p.id_prestador = d.id_prestador
     WHERE d.id_requerimiento = ANY(p_ids_requerimientos::INTEGER[])
       AND d.baja_fecha IS NULL
       AND r.baja_fecha IS NULL
       AND r.estado <> compras.id_estado_requerimiento('ANULADO')
     ORDER BY d.id_requerimiento::INTEGER, d.id_prestador::INTEGER;
$function$;


CREATE OR REPLACE FUNCTION compras.listar_presupuestos_prestador(p_id_requerimiento integer, p_id_prestador integer)
 RETURNS TABLE(
    id_requerimiento_presupuesto integer,
    id_requerimiento integer,
    tipo_documento smallint,
    fecha_documento date,
    numero_receta character varying(100),
    id_prestador integer,
    empresa_cuit character varying(11),
    empresa_sucursal character varying(6),
    descripcion_empresa character varying(200),
    dl_group_id bigint,
    dl_folder_id bigint,
    dl_file_entry_id bigint,
    dl_file_uuid character varying(75),
    nombre_original character varying(255),
    nombre_persistido character varying(255),
    titulo character varying(240),
    descripcion_prestador character varying(500),
    alta_fecha timestamp without time zone,
    alta_usr character varying(100),
    baja_fecha timestamp without time zone,
    baja_usr character varying(100)
)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.id_requerimiento_presupuesto,
        rp.id_requerimiento,
        rp.tipo_documento,
        rp.fecha_documento,
        rp.numero_receta,
        rp.id_prestador,
        rp.empresa_cuit,
        rp.empresa_sucursal,
        e.razon_soc::varchar,
        rp.dl_group_id,
        rp.dl_folder_id,
        rp.dl_file_entry_id,
        rp.dl_file_uuid,
        rp.nombre_original,
        rp.nombre_persistido,
        rp.titulo,
        p.descripcion::varchar,
        rp.alta_fecha,
        rp.alta_usr,
        rp.baja_fecha,
        rp.baja_usr
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    LEFT JOIN informacion_afip.empresa e ON e.cuit = rp.empresa_cuit AND e.sucursal = rp.empresa_sucursal
    WHERE rp.id_requerimiento = $1
      AND rp.id_prestador = $2
      AND rp.tipo_documento = 1
      AND rp.baja_fecha IS NULL
    ORDER BY rp.id_requerimiento_presupuesto;
$function$;


CREATE FUNCTION compras.get_pedido_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER
)
RETURNS SETOF compras.requerimiento_pedido_cotizacion
AS $func$
    SELECT pc.*
    FROM compras.requerimiento_pedido_cotizacion pc
    JOIN compras.requerimiento_cotizacion_prestador rcp
      ON rcp.id_requerimiento = pc.id_requerimiento
     AND rcp.id_prestador = pc.id_prestador
    WHERE pc.id_requerimiento = $1
      AND pc.id_prestador = $2
      AND pc.intento = rcp.intentos
      AND rcp.estado_envio IN ('ENVIADO', 'COTIZADO')
    ORDER BY pc.intento DESC
    LIMIT 1;
$func$
LANGUAGE sql
STABLE;


CREATE OR REPLACE FUNCTION compras.listar_configuracion_correos_rubro(p_id_tipo_prestacion integer)
 RETURNS TABLE(id_prestador integer, descripcion character varying, cuit character varying, email character varying, id_tipo_prestador integer, tipo_prestador character varying)
 LANGUAGE sql
 STABLE
AS $function$
    SELECT DISTINCT
        p.id_prestador::INTEGER AS id_prestador,
        p.descripcion::VARCHAR AS descripcion,
        p.cuit::VARCHAR AS cuit,
        compras.resolver_emails_cotizacion_prestador(
            p.id_prestador
        )::VARCHAR AS email,
        p.id_tipo_prestador::INTEGER AS id_tipo_prestador,
        tp.descripcion::VARCHAR AS tipo_prestador
    FROM compras.tipo_prestacion t
    JOIN compras.sector_requerimiento s
      ON s.id_sector = t.id_sector
    JOIN public.prestador_rubro pr
      ON pr.rubro = t.rubro_prestador
    JOIN public.prestador p
      ON p.id_prestador = pr.id_prestador
    LEFT JOIN trae_tipos_prestadores() tp
      ON tp.id_tipo_prestador = p.id_tipo_prestador
    WHERE t.id_tipo_prestacion = $1
      AND s.activo = TRUE
      AND s.baja_fecha IS NULL
      AND COALESCE(p.solicitar_cotizacion, FALSE) = TRUE
      AND p.baja_fecha IS NULL
    ORDER BY 2, 1;
$function$;


CREATE FUNCTION compras.get_requerimiento_reclamo_prestacional(
    p_id_requerimiento INTEGER
)
RETURNS SETOF compras.requerimiento_reclamo_prestacional
AS $func$
    SELECT relacion.*
    FROM compras.requerimiento_reclamo_prestacional relacion
    WHERE relacion.id_requerimiento = $1;
$func$
LANGUAGE sql
STABLE;


CREATE FUNCTION compras.listar_relaciones_reclamo_prestacional_batch(
    p_estado VARCHAR,
    p_ids_requerimientos TEXT
)
RETURNS SETOF compras.requerimiento_reclamo_prestacional
AS $func$
    SELECT relacion.*
    FROM compras.requerimiento_reclamo_prestacional relacion
    WHERE relacion.estado = $1
      AND relacion.id_reclamo_prestacional IS NOT NULL
      AND relacion.id_requerimiento = ANY ($2::INTEGER[])
    ORDER BY relacion.id_requerimiento;
$func$
LANGUAGE sql
STABLE;


CREATE FUNCTION
compras.listar_relaciones_reclamo_prestacional_por_reclamo(
    p_id_reclamo_prestacional INTEGER,
    p_estado VARCHAR
)
RETURNS SETOF compras.requerimiento_reclamo_prestacional
AS $func$
    SELECT relacion.*
    FROM compras.requerimiento_reclamo_prestacional relacion
    JOIN compras.requerimiento requerimiento
      ON requerimiento.id_requerimiento = relacion.id_requerimiento
    WHERE relacion.id_reclamo_prestacional = $1
      AND relacion.estado = $2
      AND requerimiento.baja_fecha IS NULL
    ORDER BY relacion.id_requerimiento;
$func$
LANGUAGE sql
STABLE;


CREATE FUNCTION compras.liberar_reserva_reclamo_prestacional(
    p_id_requerimiento INTEGER,
    p_token_reserva VARCHAR,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_filas_afectadas INTEGER;
BEGIN
    DELETE FROM compras.requerimiento_reclamo_prestacional
    WHERE id_requerimiento = p_id_requerimiento
      AND estado = 'RESERVADO'
      AND id_reclamo_prestacional IS NULL
      AND token_reserva = btrim(p_token_reserva);

    GET DIAGNOSTICS v_filas_afectadas = ROW_COUNT;

    -- p_usuario se conserva como parte del contrato JDBC legacy.
    RETURN v_filas_afectadas > 0;
END;
$func$
LANGUAGE plpgsql
VOLATILE;


CREATE FUNCTION compras.bloquear_requerimiento_reclamo_prestacional(
    p_id_requerimiento INTEGER
)
RETURNS BOOLEAN
AS $func$
BEGIN
    PERFORM pg_advisory_xact_lock(5391184, p_id_requerimiento);
    RETURN TRUE;
END;
$func$
LANGUAGE plpgsql
VOLATILE;


CREATE FUNCTION compras.get_estado_requerimiento_for_update(
    p_id_requerimiento INTEGER
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
BEGIN
    SELECT r.estado
      INTO v_estado
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    RETURN COALESCE(v_estado, 0);
END;
$func$
LANGUAGE plpgsql
VOLATILE;

CREATE OR REPLACE FUNCTION compras.buscar_prestadores_enviados(p_id_requerimiento integer, p_texto character varying, p_limite integer)
 RETURNS TABLE(id_prestador integer, descripcion character varying, cuit character varying, email character varying, email_destino character varying, id_tipo_prestador integer, tipo_prestador character varying, estado_envio character varying)
 LANGUAGE plpgsql
 STABLE
AS $function$
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
    compras.resolver_emails_cotizacion_prestador(
            p.id_prestador
    )::VARCHAR AS email,
    NULLIF(
            btrim(rcp.email_destino),
            ''
    )::VARCHAR AS email_destino,
    p.id_tipo_prestador::INTEGER,
    tp.descripcion::VARCHAR,
    rcp.estado_envio::VARCHAR
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
  AND r.estado IN (compras.id_estado_requerimiento('A_COTIZAR'), compras.id_estado_requerimiento('COTIZADO'), compras.id_estado_requerimiento('RECLAMO_RP'), compras.id_estado_requerimiento('ORDEN_COMPRA'), compras.id_estado_requerimiento('ANULADO'))
  AND (
    r.estado <> compras.id_estado_requerimiento('A_COTIZAR')
        OR compras.es_prestador_compatible_cotizacion(
            r.id_requerimiento,
            p.id_prestador
        )
    )
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
$function$;

CREATE OR REPLACE FUNCTION compras.calcular_total_detalle_fila()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN

    /*
     * INSERT:
     * solo calcula si no se informó explícitamente un total.
     */
    IF TG_OP = 'INSERT'
       AND NEW.precio_total_estimado IS NULL
       AND NEW.precio_unitario_estimado IS NOT NULL THEN

        NEW.precio_total_estimado :=
            round(
                NEW.cantidad * NEW.precio_unitario_estimado,
                2
            );

    /*
     * UPDATE:
     * si el caller no modificó explícitamente el total,
     * se recalcula.
     *
     * Esto permite que la aplicación siga funcionando y,
     * al mismo tiempo, que un UPDATE manual pueda establecer
     * explícitamente precio_total_estimado.
     */
    ELSIF TG_OP = 'UPDATE'
       AND (NEW.cantidad IS DISTINCT FROM OLD.cantidad
            OR NEW.precio_unitario_estimado IS DISTINCT FROM OLD.precio_unitario_estimado)
       AND NEW.precio_total_estimado
            IS NOT DISTINCT FROM OLD.precio_total_estimado THEN

        IF NEW.precio_unitario_estimado IS NULL THEN
            NEW.precio_total_estimado := NULL;
        ELSE
            NEW.precio_total_estimado :=
                round(
                    NEW.cantidad * NEW.precio_unitario_estimado,
                    2
                );
        END IF;

    END IF;

    RETURN NEW;
END;
$function$;

CREATE OR REPLACE FUNCTION compras.liberar_copia_cotizacion_requerimiento(p_id_requerimiento integer)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN

    UPDATE compras.requerimiento
    SET copia_cotizacion_enviada = FALSE
    WHERE id_requerimiento = p_id_requerimiento
      AND copia_cotizacion_enviada = TRUE;

END;
$function$;

CREATE OR REPLACE FUNCTION compras.reservar_copia_cotizacion_requerimiento(p_id_requerimiento integer)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
BEGIN

    UPDATE compras.requerimiento
    SET copia_cotizacion_enviada = TRUE
    WHERE id_requerimiento = p_id_requerimiento
      AND baja_fecha IS NULL
      AND copia_cotizacion_enviada = FALSE;

    RETURN FOUND;

END;
$function$;
CREATE TRIGGER trg_compras_detalle_calcular_total BEFORE INSERT OR UPDATE OF cantidad, precio_unitario_estimado ON compras.requerimiento_detalle FOR EACH ROW EXECUTE PROCEDURE compras.calcular_total_detalle_fila();

COMMIT;
