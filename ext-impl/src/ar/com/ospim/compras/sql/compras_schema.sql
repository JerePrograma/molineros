-- =====================================================================
-- MÓDULO: Compras - instalación canónica desde cero
-- PostgreSQL 9.6+
--
-- INSTALACIÓN COMPLETA:
--   Requiere que el esquema compras no exista y lo crea en una transacción.
--   Este es el único archivo SQL requerido para desplegar el módulo.
--   No depende de migraciones, includes ni pasos manuales.
--
-- Flujo funcional activo:
--   1  PENDIENTE
--   2  A_COTIZAR
--   3  COTIZADO
--   4  RECLAMO_RP
--
-- Estado reconocido de solo lectura, sin transición activa:
--   5  ORDEN_COMPRA
--
-- Estado lateral:
--   99 ANULADO
--
-- Contratos incorporados:
--   - guardar_requerimiento con 23 argumentos de entrada.
--   - persistencia de afiliado_id_ospim como snapshot.
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
--
-- Ejecutar con psql -X -v ON_ERROR_STOP=1.
-- Si la sesión está abortada, ejecutar ROLLBACK antes de este archivo.
-- =====================================================================

\encoding LATIN1

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
                                              baja_usr VARCHAR(100),

                                              CONSTRAINT ck_compras_sector_descripcion
                                                  CHECK (length(btrim(descripcion)) > 0)
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

    CONSTRAINT ck_compras_tipo_prestacion_id
        CHECK (id_tipo_prestacion BETWEEN 1 AND 7),
    CONSTRAINT ck_compras_tipo_prestacion_descripcion
        CHECK (NULLIF(btrim(descripcion), '') IS NOT NULL),
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
                                       afiliado_id_ospim INTEGER,

    -- Snapshot para consulta e impresion.
                                       afiliado_nombre VARCHAR(120),
                                       afiliado_apellido VARCHAR(120),
                                       afiliado_documento_tipo VARCHAR(10),
                                       afiliado_documento_nro VARCHAR(30),
                                       afiliado_direccion VARCHAR(250),
                                       afiliado_localidad VARCHAR(120),
                                       afiliado_provincia VARCHAR(120),
                                       afiliado_celular VARCHAR(80),
                                       afiliado_telefono VARCHAR(80),
                                       afiliado_email VARCHAR(160),

                                       cargo_ospim INTEGER NOT NULL DEFAULT 100,
                                       cargo_tercerizadora INTEGER NOT NULL DEFAULT 0,
                                       id_tercerizadora VARCHAR(40),
                                       recupero BOOLEAN NOT NULL DEFAULT FALSE,
                                       surge BOOLEAN NOT NULL DEFAULT FALSE,
                                       legales BOOLEAN NOT NULL DEFAULT FALSE,

                                       observaciones TEXT,

                                       alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                       alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',
                                       modi_fecha TIMESTAMP WITHOUT TIME ZONE,
                                       modi_usr VARCHAR(100),
                                       baja_fecha TIMESTAMP WITHOUT TIME ZONE,
                                       baja_usr VARCHAR(100),

                                       CONSTRAINT ck_compras_requerimiento_estado
                                           CHECK (estado IN (1, 2, 3, 4, 5, 99)),

                                       CONSTRAINT ck_compras_requerimiento_afiliado_int
                                           CHECK (afiliado_int IS NULL OR afiliado_int >= 0),

                                       CONSTRAINT ck_compras_requerimiento_cargo_ospim
                                           CHECK (cargo_ospim BETWEEN 0 AND 100),

                                       CONSTRAINT ck_compras_requerimiento_cargo_tercerizadora
                                           CHECK (cargo_tercerizadora BETWEEN 0 AND 100),

                                       CONSTRAINT ck_compras_requerimiento_cargos_total
                                           CHECK (cargo_ospim + cargo_tercerizadora = 100),

                                       CONSTRAINT ck_compras_requerimiento_tercerizadora
                                           CHECK (
                                               cargo_tercerizadora = 0
                                                   OR NULLIF(btrim(id_tercerizadora), '') IS NOT NULL
                                               ),

                                       CONSTRAINT ck_compras_requerimiento_recupero
                                           CHECK (recupero = (cargo_tercerizadora > 0))
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

CREATE INDEX ix_compras_requerimiento_afiliado_id_ospim
    ON compras.requerimiento (afiliado_id_ospim)
    WHERE baja_fecha IS NULL
      AND afiliado_id_ospim IS NOT NULL;

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
                                                                PRIMARY KEY (id_requerimiento, id_prestador),

                                                            CONSTRAINT ck_compras_cotizacion_estado_envio
                                                                CHECK (
                                                                    estado_envio IN (
                                                                                     'PENDIENTE',
                                                                                     'PROCESANDO',
                                                                                     'ENVIADO',
                                                                                     'COTIZADO',
                                                                                     'ERROR',
                                                                                     'EMAIL_INVALIDO'
                                                                        )
                                                                    ),

                                                            CONSTRAINT ck_compras_cotizacion_intentos
                                                                CHECK (intentos >= 0),

                                                            CONSTRAINT ck_compras_cotizacion_fecha_envio
                                                                CHECK (
                                                                    estado_envio NOT IN (
                                                                                         'ENVIADO',
                                                                                         'COTIZADO'
                                                                        )
                                                                        OR fecha_envio IS NOT NULL
                                                                    )
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
        CHECK (NULLIF(btrim(dl_file_uuid), '') IS NOT NULL),
    CONSTRAINT ck_compras_pedido_cotizacion_nombre_original
        CHECK (NULLIF(btrim(nombre_original), '') IS NOT NULL),
    CONSTRAINT ck_compras_pedido_cotizacion_nombre_persistido
        CHECK (NULLIF(btrim(nombre_persistido), '') IS NOT NULL),
    CONSTRAINT ck_compras_pedido_cotizacion_titulo
        CHECK (NULLIF(btrim(titulo), '') IS NOT NULL)
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
                                                   descripcion_empresa VARCHAR(200),

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
                                                   descripcion_prestador VARCHAR(500),

                                                   alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                                                   alta_usr VARCHAR(100) NOT NULL DEFAULT 'sistema',

                                                   baja_fecha TIMESTAMP WITHOUT TIME ZONE,
                                                   baja_usr VARCHAR(100),

                                                   CONSTRAINT ck_compras_presupuesto_requerimiento
                                                       CHECK (id_requerimiento > 0),

                                                   CONSTRAINT ck_compras_presupuesto_tipo_documento
                                                       CHECK (tipo_documento IN (1, 2, 3)),

                                                   CONSTRAINT ck_compras_presupuesto_prestador
                                                       CHECK (
                                                           (
                                                               tipo_documento = 1
                                                               AND id_prestador IS NOT NULL
                                                               AND id_prestador > 0
                                                           )
                                                           OR (
                                                               tipo_documento IN (2, 3)
                                                               AND id_prestador IS NULL
                                                           )
                                                       ),

                                                   CONSTRAINT ck_compras_presupuesto_fecha_documento
                                                       CHECK (
                                                           tipo_documento <> 2
                                                           OR fecha_documento IS NOT NULL
                                                       ),

                                                   CONSTRAINT ck_compras_orden_medica_numero_receta
                                                       CHECK (
                                                           (
                                                               tipo_documento IN (1, 3)
                                                               AND numero_receta IS NULL
                                                           )
                                                           OR (
                                                               tipo_documento = 2
                                                               AND (
                                                                   numero_receta IS NULL
                                                                   OR (
                                                                       NULLIF(
                                                                           regexp_replace(
                                                                               upper(btrim(numero_receta)),
                                                                               '[[:space:]]+',
                                                                               '',
                                                                               'g'
                                                                           ),
                                                                           ''
                                                                       ) IS NOT NULL
                                                                       AND numero_receta = regexp_replace(
                                                                           upper(btrim(numero_receta)),
                                                                           '[[:space:]]+',
                                                                           '',
                                                                           'g'
                                                                       )
                                                                   )
                                                               )
                                                           )
                                                       ),

                                                   CONSTRAINT ck_compras_presupuesto_group
                                                       CHECK (dl_group_id > 0),

                                                   CONSTRAINT ck_compras_presupuesto_folder
                                                       CHECK (dl_folder_id >= 0),

                                                   CONSTRAINT ck_compras_presupuesto_file_entry
                                                       CHECK (dl_file_entry_id > 0),

                                                   CONSTRAINT ck_compras_presupuesto_nombre_original
                                                       CHECK (NULLIF(btrim(nombre_original), '') IS NOT NULL),

                                                   CONSTRAINT ck_compras_presupuesto_nombre_persistido
                                                       CHECK (NULLIF(btrim(nombre_persistido), '') IS NOT NULL),

                                                   CONSTRAINT ck_compras_presupuesto_titulo
                                                       CHECK (NULLIF(btrim(titulo), '') IS NOT NULL),

                                                   CONSTRAINT ck_compras_orden_medica_datos
                                                       CHECK (
                                                           tipo_documento = 1
                                                           OR descripcion_prestador IS NULL
                                                       ),

                                                   CONSTRAINT ck_compras_presupuesto_empresa_datos
                                                       CHECK (
                                                           (
                                                               tipo_documento IN (1, 2)
                                                               AND empresa_cuit IS NULL
                                                               AND empresa_sucursal IS NULL
                                                               AND descripcion_empresa IS NULL
                                                           )
                                                           OR (
                                                               tipo_documento = 3
                                                               AND NULLIF(btrim(empresa_cuit), '') IS NOT NULL
                                                               AND NULLIF(btrim(empresa_sucursal), '') IS NOT NULL
                                                               AND NULLIF(btrim(descripcion_empresa), '') IS NOT NULL
                                                               AND empresa_cuit = btrim(empresa_cuit)
                                                               AND empresa_sucursal = btrim(empresa_sucursal)
                                                               AND descripcion_empresa = btrim(descripcion_empresa)
                                                           )
                                                       ),

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
                                               id_tipo_nomenclador INTEGER,
                                               codigo_nomenclador VARCHAR(100),
                                               descripcion_nomenclador VARCHAR(500),

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
                                               baja_usr VARCHAR(100),

                                               CONSTRAINT ck_compras_detalle_tipo_item
                                                   CHECK (
                                                       tipo_item IN (
                                                                     'NOMENCLADOR',
                                                                     'MEDICAMENTO',
                                                                     'OBSERVACION'
                                                           )
                                                       ),

                                               CONSTRAINT ck_compras_detalle_nomenclador_requerido
                                                   CHECK (
                                                       tipo_item <> 'NOMENCLADOR'
                                                           OR (
                                                           id_prestacion IS NOT NULL
                                                               AND id_prestacion > 0
                                                               AND id_tipo_nomenclador IS NOT NULL
                                                               AND id_tipo_nomenclador > 0
                                                               AND codigo_nomenclador IS NOT NULL
                                                               AND length(btrim(codigo_nomenclador)) > 0
                                                               AND descripcion_nomenclador IS NOT NULL
                                                               AND length(btrim(descripcion_nomenclador)) > 0
                                                           )
                                                       ),

                                               CONSTRAINT ck_compras_detalle_medicamento_requerido
                                                   CHECK (
                                                       tipo_item <> 'MEDICAMENTO'
                                                           OR (
                                                           id_medicamento IS NOT NULL
                                                               AND id_medicamento > 0
                                                               AND nombre_medicamento IS NOT NULL
                                                               AND length(btrim(nombre_medicamento)) > 0
                                                           )
                                                       ),

                                               CONSTRAINT ck_compras_detalle_nomenclador_sin_medicamento
                                                   CHECK (
                                                       tipo_item <> 'NOMENCLADOR'
                                                           OR (
                                                           id_medicamento IS NULL
                                                               AND troquel IS NULL
                                                               AND nombre_medicamento IS NULL
                                                           )
                                                       ),

                                               CONSTRAINT ck_compras_detalle_medicamento_sin_nomenclador
                                                   CHECK (
                                                       tipo_item <> 'MEDICAMENTO'
                                                           OR (
                                                           id_prestacion IS NULL
                                                               AND id_tipo_nomenclador IS NULL
                                                               AND codigo_nomenclador IS NULL
                                                               AND descripcion_nomenclador IS NULL
                                                           )
                                                       ),

                                               CONSTRAINT ck_compras_detalle_observacion_requerida
                                                   CHECK (
                                                       tipo_item <> 'OBSERVACION'
                                                           OR NULLIF(btrim(observaciones), '') IS NOT NULL
                                                       ),

                                               CONSTRAINT ck_compras_detalle_observacion_sin_datos_tecnicos
                                                   CHECK (
                                                       tipo_item <> 'OBSERVACION'
                                                           OR (
                                                           id_prestacion IS NULL
                                                               AND id_tipo_nomenclador IS NULL
                                                               AND codigo_nomenclador IS NULL
                                                               AND descripcion_nomenclador IS NULL
                                                               AND id_medicamento IS NULL
                                                               AND troquel IS NULL
                                                               AND nombre_medicamento IS NULL
                                                           )
                                                       ),

                                               CONSTRAINT ck_compras_detalle_troquel
                                                   CHECK (
                                                       troquel IS NULL
                                                           OR troquel > 0
                                                       ),

                                               CONSTRAINT ck_compras_detalle_cantidad
                                                   CHECK (
                                                       cantidad > 0
                                                       ),

                                               CONSTRAINT ck_compras_detalle_precio_unitario
                                                   CHECK (
                                                       precio_unitario_estimado IS NULL
                                                           OR precio_unitario_estimado >= 0
                                                       ),

                                               CONSTRAINT ck_compras_detalle_precio_total
                                                   CHECK (
                                                       precio_total_estimado IS NULL
                                                           OR precio_total_estimado >= 0
                                                       )
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


CREATE FUNCTION compras.normalizar_rubro(
    p_rubro VARCHAR
)
    RETURNS VARCHAR
AS $func$
SELECT translate(
           upper(btrim(replace(COALESCE($1, ''), '_', ' '))),
           U&'\00C1\00C0\00C4\00C2\00C9\00C8\00CB\00CA\00CD\00CC\00CF\00CE\00D3\00D2\00D6\00D4\00DA\00D9\00DC\00DB',
           'AAAAEEEEIIIIOOOOUUUU'
       );
$func$
LANGUAGE sql
IMMUTABLE;


CREATE FUNCTION compras.estado_requerimiento_descripcion(
    p_estado INTEGER
)
    RETURNS VARCHAR
AS $func$
BEGIN
RETURN CASE p_estado
           WHEN 1 THEN 'PENDIENTE'
           WHEN 2 THEN 'A COTIZAR'
           WHEN 3 THEN 'COTIZADO'
           WHEN 4 THEN 'RECLAMO (RP)'
           WHEN 5 THEN 'ORDEN DE COMPRA'
           WHEN 99 THEN 'ANULADO'
           ELSE 'DESCONOCIDO'
    END;
END;
$func$
LANGUAGE plpgsql
IMMUTABLE;


CREATE FUNCTION compras.listar_estados_requerimiento()
    RETURNS TABLE (
                      id INTEGER,
                      descripcion VARCHAR
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT *
FROM (
         VALUES
             (1, 'PENDIENTE'::VARCHAR),
             (2, 'A COTIZAR'::VARCHAR),
             (3, 'COTIZADO'::VARCHAR),
             (4, 'RECLAMO (RP)'::VARCHAR),
             (5, 'ORDEN DE COMPRA'::VARCHAR),
             (99, 'ANULADO'::VARCHAR)
     ) estados(id, descripcion);
END;
$func$
LANGUAGE plpgsql
IMMUTABLE;

-- =====================================================================
-- REGLAS DE INTEGRIDAD
-- =====================================================================

CREATE FUNCTION compras.validar_requerimiento_fila()
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
            'El sector informado no existe o no está activo.';
END IF;

    IF TG_OP = 'INSERT' THEN

        IF NEW.estado <> 1 THEN
            RAISE EXCEPTION
                'Un requerimiento nuevo debe crearse en estado PENDIENTE.';
END IF;

ELSE

        /*
         * RECLAMO_RP, ORDEN_COMPRA y ANULADO son completamente
         * inmutables una vez alcanzados.
         */
        IF OLD.estado IN (4, 5, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        /*
         * COTIZADO también permanece bloqueado, salvo por la transición
         * funcional COTIZADO -> RECLAMO_RP.
         *
         * Se permite que cambiar_estado_requerimiento actualice
         * modi_fecha y modi_usr durante esa transición.
         */
        IF OLD.estado = 3
           AND NEW IS DISTINCT FROM OLD
           AND (
                  NEW.estado IS DISTINCT FROM 4
               OR NEW.id_requerimiento
                    IS DISTINCT FROM OLD.id_requerimiento
               OR NEW.alta_fecha
                    IS DISTINCT FROM OLD.alta_fecha
               OR NEW.alta_usr
                    IS DISTINCT FROM OLD.alta_usr
               OR NEW.baja_fecha
                    IS DISTINCT FROM OLD.baja_fecha
               OR NEW.baja_usr
                    IS DISTINCT FROM OLD.baja_usr
           ) THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        v_cambio_estructura :=
               NEW.id_sector IS DISTINCT FROM OLD.id_sector
            OR NEW.afiliado_cuil_titular
                IS DISTINCT FROM OLD.afiliado_cuil_titular
            OR NEW.afiliado_int
                IS DISTINCT FROM OLD.afiliado_int
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
            OR NEW.cargo_ospim
                IS DISTINCT FROM OLD.cargo_ospim
            OR NEW.cargo_tercerizadora
                IS DISTINCT FROM OLD.cargo_tercerizadora
            OR NEW.id_tercerizadora
                IS DISTINCT FROM OLD.id_tercerizadora
            OR NEW.recupero
                IS DISTINCT FROM OLD.recupero
            OR NEW.legales
                IS DISTINCT FROM OLD.legales
            OR NEW.observaciones
                IS DISTINCT FROM OLD.observaciones;

        IF v_cambio_estructura
           AND OLD.estado <> 1 THEN

            RAISE EXCEPTION
                'La estructura solo puede modificarse en estado PENDIENTE.';
END IF;

        /*
         * SURGE no forma parte de la estructura del requerimiento.
         * Puede ajustarse durante la preparacion (PENDIENTE) y durante
         * la cotización (A_COTIZAR), pero queda congelado al cerrarla.
         */
        IF NEW.surge IS DISTINCT FROM OLD.surge
           AND OLD.estado NOT IN (1, 2) THEN

            RAISE EXCEPTION
                'SURGE solo puede modificarse en estado PENDIENTE o ENVIADO A COTIZAR.';
END IF;

        IF NEW.estado IS DISTINCT FROM OLD.estado THEN

            /*
             * Transiciones funcionales actualmente soportadas:
             *
             * 1 PENDIENTE -> 2 A_COTIZAR
             * 1 PENDIENTE -> 99 ANULADO
             * 2 A_COTIZAR -> 3 COTIZADO
             * 2 A_COTIZAR -> 99 ANULADO
             * 3 COTIZADO -> 4 RECLAMO_RP
             *
             * ORDEN_COMPRA (5) continúa sin transición activa.
             */
            IF NOT (
                    (OLD.estado = 1 AND NEW.estado IN (2, 99))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
                 OR (OLD.estado = 3 AND NEW.estado = 4)
            ) THEN

                RAISE EXCEPTION
                    'Transicion de estado inválida: % -> %.',
                    OLD.estado,
                    NEW.estado;
END IF;

            IF OLD.estado = 3
               AND NEW.estado = 4
               AND NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_reclamo_prestacional rr
                     WHERE rr.id_requerimiento = NEW.id_requerimiento
                       AND rr.estado = 'VINCULADO'
                       AND rr.id_reclamo_prestacional IS NOT NULL
               ) THEN

                RAISE EXCEPTION
                    'El requerimiento debe tener un Reclamo Prestacional vinculado antes de cambiar a RECLAMO_RP.';
END IF;

            IF OLD.estado = 1
               AND NEW.estado = 2 THEN

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento =
                           NEW.id_requerimiento
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

            IF OLD.estado = 2
               AND NEW.estado = 3 THEN

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento =
                           NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'No se puede cerrar una cotización sin detalles.';
END IF;

                IF EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento =
                           NEW.id_requerimiento
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
                                   AND rp.tipo_documento = 1
                                   AND rp.id_prestador =
                                       d.id_prestador
                                   AND rp.baja_fecha IS NULL
                           )
                       )
                ) THEN

                    RAISE EXCEPTION
                        'No se puede cerrar la cotización: existen detalles incompletos o inválidos.';
END IF;

END IF;

            IF NEW.estado = 99 THEN

                v_usuario :=
                    compras.normalizar_usuario(
                        COALESCE(
                            NEW.modi_usr,
                            NEW.baja_usr
                        )
                    );

                NEW.baja_fecha :=
                    COALESCE(
                        NEW.baja_fecha,
                        now()
                    );

                NEW.baja_usr :=
                    COALESCE(
                        NULLIF(
                            btrim(NEW.baja_usr),
                            ''
                        ),
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


CREATE TRIGGER trg_compras_requerimiento_validar
    BEFORE INSERT OR UPDATE
                         ON compras.requerimiento
                         FOR EACH ROW
                         EXECUTE PROCEDURE compras.validar_requerimiento_fila();

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
    compras.normalizar_sector(sr.descripcion)
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

    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NOT NULL
       AND NEW IS DISTINCT FROM OLD THEN

        RAISE EXCEPTION
            'Un detalle dado de baja no puede modificarse ni reactivarse.';
END IF;

    /*
     * La baja lógica es una operación funcional diferente de un cambio
     * estructural. En A_COTIZAR se permite siempre que quede al menos un
     * detalle activo. Ningun otro dato puede cambiar en la misma sentencia.
     */
    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NULL
       AND NEW.baja_fecha IS NOT NULL THEN

        IF v_estado NOT IN (1, 2) THEN
            RAISE EXCEPTION
                'El detalle no puede eliminarse en el estado actual.';
END IF;

        IF NEW.id_detalle IS DISTINCT FROM OLD.id_detalle
           OR NEW.id_requerimiento IS DISTINCT FROM OLD.id_requerimiento
           OR NEW.tipo_item IS DISTINCT FROM OLD.tipo_item
           OR NEW.id_tipo_prestacion IS DISTINCT FROM OLD.id_tipo_prestacion
           OR NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion
           OR NEW.id_tipo_nomenclador IS DISTINCT FROM OLD.id_tipo_nomenclador
           OR NEW.codigo_nomenclador IS DISTINCT FROM OLD.codigo_nomenclador
           OR NEW.descripcion_nomenclador IS DISTINCT FROM OLD.descripcion_nomenclador
           OR NEW.id_medicamento IS DISTINCT FROM OLD.id_medicamento
           OR NEW.troquel IS DISTINCT FROM OLD.troquel
           OR NEW.nombre_medicamento IS DISTINCT FROM OLD.nombre_medicamento
           OR NEW.cantidad IS DISTINCT FROM OLD.cantidad
           OR NEW.precio_unitario_estimado IS DISTINCT FROM OLD.precio_unitario_estimado
           OR NEW.precio_total_estimado IS DISTINCT FROM OLD.precio_total_estimado
           OR NEW.id_prestador IS DISTINCT FROM OLD.id_prestador
           OR NEW.observaciones IS DISTINCT FROM OLD.observaciones
           OR NEW.alta_fecha IS DISTINCT FROM OLD.alta_fecha
           OR NEW.alta_usr IS DISTINCT FROM OLD.alta_usr THEN

            RAISE EXCEPTION
                'La baja del detalle no puede modificar estructura ni cotización.';
END IF;

        IF NULLIF(btrim(NEW.baja_usr), '') IS NULL THEN
            RAISE EXCEPTION
                'Debe informar el usuario que elimina el detalle.';
END IF;

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
                    'El requerimiento ENVIADO A COTIZAR debe conservar al menos una prestación.';
END IF;
END IF;

        RETURN NEW;
END IF;

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
                    'El detalle histórico de medicamento no puede convertirse directamente.';
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
                    'El detalle histórico de medicamento solo permite modificar cantidad y observaciones.';
END IF;

        ELSIF v_tipo_item_anterior = 'NOMENCLADOR' THEN
            IF v_tipo_item <> 'NOMENCLADOR' THEN
                RAISE EXCEPTION
                    'Un detalle de nomenclador no puede convertirse a otro tipo.';
END IF;
        ELSIF v_tipo_item_anterior = 'OBSERVACION' THEN
            IF v_tipo_item <> 'OBSERVACION' THEN
                RAISE EXCEPTION
                    'Un detalle de observación no puede convertirse a otro tipo.';
END IF;
ELSE
            RAISE EXCEPTION
                'El detalle persistido tiene un tipo técnico desconocido.';
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
                'El medicamento histórico debe conservar id y nombre.';
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
                'Un detalle de observación no puede contener datos técnicos.';
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
                'El nomenclador debe tener prestación, tipo real positivo, código y descripción.';
END IF;

SELECT n.id_tipo_nomenclador
INTO v_id_tipo_nomenclador_real
FROM autorizaciones.nomenclador n
WHERE n.id_prestacion = NEW.id_prestacion
  AND n.baja_fecha IS NULL;

IF NOT FOUND THEN
            RAISE EXCEPTION
                'La prestación seleccionada no existe o no está activa.';
END IF;

        IF NEW.id_tipo_nomenclador <> v_id_tipo_nomenclador_real THEN
            RAISE EXCEPTION
                'El tipo de nomenclador informado no corresponde a la prestación seleccionada.';
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
            IF NEW.id_requerimiento IS DISTINCT FROM OLD.id_requerimiento
               OR NEW.tipo_item IS DISTINCT FROM OLD.tipo_item
               OR NEW.id_tipo_prestacion IS DISTINCT FROM OLD.id_tipo_prestacion
               OR NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion
               OR NEW.id_tipo_nomenclador IS DISTINCT FROM OLD.id_tipo_nomenclador
               OR NEW.codigo_nomenclador IS DISTINCT FROM OLD.codigo_nomenclador
               OR NEW.descripcion_nomenclador IS DISTINCT FROM OLD.descripcion_nomenclador
               OR NEW.id_medicamento IS DISTINCT FROM OLD.id_medicamento
               OR NEW.troquel IS DISTINCT FROM OLD.troquel
               OR NEW.nombre_medicamento IS DISTINCT FROM OLD.nombre_medicamento
               OR NEW.cantidad IS DISTINCT FROM OLD.cantidad
               OR NEW.observaciones IS DISTINCT FROM OLD.observaciones
               OR NEW.baja_fecha IS DISTINCT FROM OLD.baja_fecha
               OR NEW.baja_usr IS DISTINCT FROM OLD.baja_usr THEN
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
                'Un requerimiento PENDIENTE no puede tener datos de cotización.';
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
                round(NEW.cantidad * NEW.precio_unitario_estimado, 2);
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

CREATE TRIGGER trg_compras_detalle_validar
    BEFORE INSERT OR UPDATE
                         ON compras.requerimiento_detalle
                         FOR EACH ROW
                         EXECUTE PROCEDURE compras.validar_requerimiento_detalle_fila();

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
    legales BOOLEAN
    );


CREATE FUNCTION compras.requerimiento_base()
    RETURNS SETOF compras.requerimiento_base_row
AS $func$
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

    r.afiliado_cuil_titular,
    r.afiliado_int,
    r.afiliado_id_ospim,

    r.afiliado_nombre,
    r.afiliado_apellido,

    NULLIF(
            concat_ws(
                    ', ',
                    NULLIF(btrim(r.afiliado_apellido), ''),
                    NULLIF(btrim(r.afiliado_nombre), '')
            ),
            ''
    )::VARCHAR AS afiliado_nombre_apellido,

    r.afiliado_documento_tipo,
    r.afiliado_documento_nro,

    NULLIF(
            concat_ws(
                    ' ',
                    NULLIF(
                            btrim(r.afiliado_documento_tipo),
                            ''
                    ),
                    NULLIF(
                            btrim(r.afiliado_documento_nro),
                            ''
                    )
            ),
            ''
    )::VARCHAR AS afiliado_documento,

    r.afiliado_direccion,
    r.afiliado_localidad,
    r.afiliado_provincia,
    r.afiliado_celular,
    r.afiliado_telefono,
    r.afiliado_email,

    r.id_sector,
    s.descripcion,
    s.requiere_afiliado,

    r.cargo_ospim,
    r.cargo_tercerizadora,
    r.id_tercerizadora,

    r.recupero,
    r.surge,
    r.observaciones,

    r.estado,
    compras.estado_requerimiento_descripcion(
            r.estado
    ),
    r.legales
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento s
              ON s.id_sector = r.id_sector;
END;
$func$
LANGUAGE plpgsql
STABLE;


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
        p_estado = 99
            AND rb.id_estado = 99
        )
        OR (
        p_estado IS DISTINCT FROM 99
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
                 WHERE d.id_requerimiento = rb.id
                   AND d.baja_fecha IS NULL
                   AND (
                        upper(
                            COALESCE(
                                d.tipo_item,
                                ''
                            )
                        ) LIKE '%' || v_texto || '%'

                        OR upper(COALESCE(d.codigo_nomenclador, ''))
                           LIKE '%' || v_texto || '%'

                        OR upper(COALESCE(d.descripcion_nomenclador, ''))
                           LIKE '%' || v_texto || '%'

                        OR upper(COALESCE(d.nombre_medicamento, ''))
                           LIKE '%' || v_texto || '%'

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
$func$
LANGUAGE plpgsql
STABLE;


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

CREATE FUNCTION compras.es_sector_seleccionable_compras(
    p_id_sector INTEGER
)
RETURNS BOOLEAN
AS $func$
    SELECT EXISTS (
        SELECT 1
        FROM compras.sector_requerimiento s
        WHERE s.id_sector = p_id_sector
          AND s.activo = TRUE
          AND s.baja_fecha IS NULL
          AND UPPER(BTRIM(s.descripcion)) NOT IN ('LEGALES', 'OTROS')
    );
$func$
LANGUAGE sql
STABLE;


-- =====================================================================
-- REQUERIMIENTOS: ESCRITURA
-- =====================================================================

CREATE FUNCTION compras.guardar_requerimiento(
    p_id INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_afiliado_id_ospim INTEGER,
    p_afiliado_nombre VARCHAR,
    p_afiliado_apellido VARCHAR,
    p_afiliado_documento_tipo VARCHAR,
    p_afiliado_documento_nro VARCHAR,
    p_afiliado_direccion VARCHAR,
    p_afiliado_localidad VARCHAR,
    p_afiliado_provincia VARCHAR,
    p_afiliado_celular VARCHAR,
    p_afiliado_telefono VARCHAR,
    p_afiliado_email VARCHAR,
    p_id_sector INTEGER,
    p_cargo_ospim INTEGER,
    p_cargo_tercerizadora INTEGER,
    p_id_tercerizadora VARCHAR,
    p_recupero BOOLEAN,
    p_surge BOOLEAN,
    p_legales BOOLEAN,
    p_observaciones TEXT,
    p_usuario VARCHAR
)
    RETURNS INTEGER
AS $func$
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
            estado, id_sector,
            afiliado_cuil_titular, afiliado_int, afiliado_id_ospim,
            afiliado_nombre, afiliado_apellido,
            afiliado_documento_tipo, afiliado_documento_nro,
            afiliado_direccion, afiliado_localidad, afiliado_provincia,
            afiliado_celular, afiliado_telefono, afiliado_email,
            cargo_ospim, cargo_tercerizadora, id_tercerizadora,
            recupero, surge, legales, observaciones, alta_usr
        )
        VALUES (
            1, p_id_sector,
            v_afiliado_cuil, p_afiliado_int, p_afiliado_id_ospim,
            NULLIF(btrim(p_afiliado_nombre), ''),
            NULLIF(btrim(p_afiliado_apellido), ''),
            NULLIF(btrim(p_afiliado_documento_tipo), ''),
            NULLIF(btrim(p_afiliado_documento_nro), ''),
            NULLIF(btrim(p_afiliado_direccion), ''),
            NULLIF(btrim(p_afiliado_localidad), ''),
            NULLIF(btrim(p_afiliado_provincia), ''),
            NULLIF(btrim(p_afiliado_celular), ''),
            NULLIF(btrim(p_afiliado_telefono), ''),
            NULLIF(btrim(p_afiliado_email), ''),
            COALESCE(p_cargo_ospim, 0),
            COALESCE(p_cargo_tercerizadora, 0),
            NULLIF(btrim(p_id_tercerizadora), ''),
            COALESCE(p_recupero, FALSE),
            COALESCE(p_surge, FALSE),
            COALESCE(p_legales, FALSE),
            NULLIF(btrim(p_observaciones), ''),
            v_usuario
        )
        RETURNING id_requerimiento INTO v_id;

        RETURN v_id;
    END IF;

    SELECT r.afiliado_cuil_titular, r.afiliado_int
    INTO v_cuil_anterior, v_inte_anterior
    FROM compras.requerimiento r
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
    SET cargo_ospim = COALESCE(p_cargo_ospim, 0),
        cargo_tercerizadora = COALESCE(p_cargo_tercerizadora, 0),
        id_tercerizadora = NULLIF(btrim(p_id_tercerizadora), ''),
        recupero = COALESCE(p_recupero, FALSE),
        surge = COALESCE(p_surge, FALSE),
        observaciones = NULLIF(btrim(p_observaciones), ''),
        modi_fecha = now(),
        modi_usr = v_usuario
    WHERE id_requerimiento = p_id
      AND estado = 1
      AND baja_fecha IS NULL
    RETURNING id_requerimiento INTO v_id;

    IF v_id IS NULL THEN
        RAISE EXCEPTION
            'La estructura solo puede modificarse en estado PENDIENTE.';
    END IF;

    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;

CREATE FUNCTION compras.cambiar_estado_requerimiento(
    p_id_requerimiento INTEGER,
    p_estado_nuevo INTEGER,
    p_usuario VARCHAR
)
    RETURNS VOID
AS $func$
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
        OR r.estado = 99
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
    baja_usr =
        CASE
            WHEN p_estado_nuevo = 99
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
$func$
LANGUAGE plpgsql;

CREATE FUNCTION compras.confirmar_envio_a_cotizar(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
    RETURNS INTEGER
AS $func$
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
    IF v_estado = 2 THEN
        RETURN 2;
END IF;

    IF v_estado <> 1 THEN
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
        RETURN 1;
END IF;

    PERFORM compras.cambiar_estado_requerimiento(
        p_id_requerimiento,
        2,
        p_usuario
    );

RETURN 2;
END;
$func$
LANGUAGE plpgsql;

-- =====================================================================
-- DETALLES
-- =====================================================================

CREATE FUNCTION compras.get_requerimiento_detalle(
    p_id_requerimiento INTEGER
)
    RETURNS TABLE (
                      id INTEGER,
                      id_requerimiento INTEGER,

                      tipo_item VARCHAR,
                      codigo_item VARCHAR,
                      descripcion_item VARCHAR,

                      id_prestacion INTEGER,
                      id_tipo_nomenclador INTEGER,
                      codigo_nomenclador VARCHAR,
                      descripcion_nomenclador VARCHAR,

                      id_medicamento INTEGER,
                      troquel INTEGER,
                      nombre_medicamento VARCHAR,

                      cantidad INTEGER,
                      precio_unitario_estimado NUMERIC,
                      precio_total_estimado NUMERIC,

                      id_prestador INTEGER,
                      prestador_cuit VARCHAR,
                      prestador_razon_social VARCHAR,

                      observaciones TEXT
                  )
    AS $func$
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
        ELSE d.codigo_nomenclador
        END::VARCHAR AS codigo_item,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN d.nombre_medicamento
        ELSE d.descripcion_nomenclador
        END::VARCHAR AS descripcion_item,

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

    d.id_prestador,
    p.cuit::VARCHAR,
    p.descripcion::VARCHAR,

    d.observaciones
FROM compras.requerimiento_detalle d
         LEFT JOIN public.prestador p
                   ON p.id_prestador = d.id_prestador
WHERE d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL
ORDER BY d.id_detalle;
END;
$func$
LANGUAGE plpgsql
STABLE;

CREATE FUNCTION compras.get_requerimiento_detalle_clasificado(
    p_id_requerimiento INTEGER
)
    RETURNS TABLE (
                      id INTEGER,
                      id_requerimiento INTEGER,

                      tipo_item VARCHAR,
                      id_tipo_prestacion INTEGER,
                      tipo_prestacion VARCHAR,
                      codigo_item VARCHAR,
                      descripcion_item VARCHAR,

                      id_prestacion INTEGER,
                      id_tipo_nomenclador INTEGER,
                      codigo_nomenclador VARCHAR,
                      descripcion_nomenclador VARCHAR,

                      id_medicamento INTEGER,
                      troquel INTEGER,
                      nombre_medicamento VARCHAR,

                      cantidad INTEGER,
                      precio_unitario_estimado NUMERIC,
                      precio_total_estimado NUMERIC,

                      id_prestador INTEGER,
                      prestador_cuit VARCHAR,
                      prestador_razon_social VARCHAR,

                      observaciones TEXT
                  )
    AS $func$
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
        ELSE d.codigo_nomenclador
        END::VARCHAR AS codigo_item,

    CASE
        WHEN d.tipo_item = 'MEDICAMENTO'
            THEN d.nombre_medicamento
        ELSE d.descripcion_nomenclador
        END::VARCHAR AS descripcion_item,

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

    d.id_prestador,
    p.cuit::VARCHAR,
    p.descripcion::VARCHAR,

    d.observaciones
FROM compras.requerimiento_detalle d
         LEFT JOIN compras.tipo_prestacion tp
                   ON tp.id_tipo_prestacion = d.id_tipo_prestacion
         LEFT JOIN public.prestador p
                   ON p.id_prestador = d.id_prestador
WHERE d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL
ORDER BY d.id_detalle;
END;
$func$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento_detalle(
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
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
v_id INTEGER;
    v_usuario VARCHAR(100);
    v_tipo_item VARCHAR(20);
    v_tipo_item_actual VARCHAR(20);
    v_tipo_item_esperado VARCHAR(20);
    v_sector VARCHAR(200);
    v_id_tipo_nomenclador_real INTEGER;
BEGIN
    v_usuario :=
        compras.normalizar_usuario(
            p_usuario
        );

    v_tipo_item :=
        upper(
            btrim(
                COALESCE(
                    p_tipo_item,
                    ''
                )
            )
        );

    IF v_tipo_item NOT IN (
        'NOMENCLADOR',
        'MEDICAMENTO',
        'OBSERVACION'
    ) THEN
        RAISE EXCEPTION
            'Tipo de item inválido.';
END IF;

    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

    IF p_cantidad IS NULL
       OR p_cantidad <= 0 THEN

        RAISE EXCEPTION
            'La cantidad debe ser mayor a cero.';
END IF;

SELECT compras.normalizar_sector(sr.descripcion)
INTO v_sector
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento sr
              ON sr.id_sector = r.id_sector
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.estado = 1
  AND r.baja_fecha IS NULL
    FOR UPDATE OF r;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'Los detalles estructurales solo pueden modificarse en estado PENDIENTE.';
END IF;

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

    /*
     * ALTAS
     *
     * El sector determina si el detalle usa nomenclador u observación.
     */
    IF p_id IS NULL
       OR p_id <= 0 THEN

        IF v_tipo_item <> v_tipo_item_esperado THEN
            RAISE EXCEPTION
                'El sector % requiere detalles de tipo %.',
                v_sector,
                v_tipo_item_esperado;
END IF;

        IF v_tipo_item = 'OBSERVACION' THEN
            IF NULLIF(btrim(p_observaciones), '') IS NULL THEN
                RAISE EXCEPTION
                    'Debe informar las observaciones del detalle.';
END IF;

            IF p_id_prestacion IS NOT NULL
               OR p_id_tipo_nomenclador IS NOT NULL
               OR NULLIF(btrim(p_codigo_nomenclador), '') IS NOT NULL
               OR NULLIF(btrim(p_descripcion_nomenclador), '') IS NOT NULL
               OR p_id_medicamento IS NOT NULL
               OR p_troquel IS NOT NULL
               OR NULLIF(btrim(p_nombre_medicamento), '') IS NOT NULL THEN

                RAISE EXCEPTION
                    'Un detalle de observación no puede contener datos técnicos.';
END IF;
ELSE

        IF p_id_prestacion IS NULL
           OR p_id_prestacion <= 0 THEN

            RAISE EXCEPTION
                'Debe informar la prestación del nomenclador.';
END IF;

        IF p_id_tipo_nomenclador IS NULL
           OR p_id_tipo_nomenclador <= 0 THEN

            RAISE EXCEPTION
                'Debe informar el tipo real de nomenclador.';
END IF;

SELECT n.id_tipo_nomenclador
INTO v_id_tipo_nomenclador_real
FROM autorizaciones.nomenclador n
WHERE n.id_prestacion = p_id_prestacion
  AND n.baja_fecha IS NULL;

IF NOT FOUND THEN
            RAISE EXCEPTION
                'La prestación seleccionada no existe o no está activa.';
END IF;

        IF v_id_tipo_nomenclador_real
                <> p_id_tipo_nomenclador THEN

            RAISE EXCEPTION
                'El tipo de nomenclador informado no corresponde a la prestación seleccionada.';
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

        IF p_codigo_nomenclador IS NULL
           OR length(
               btrim(
                   p_codigo_nomenclador
               )
           ) = 0 THEN

            RAISE EXCEPTION
                'Debe informar el código de nomenclador.';
END IF;

        IF p_descripcion_nomenclador IS NULL
           OR length(
               btrim(
                   p_descripcion_nomenclador
               )
           ) = 0 THEN

            RAISE EXCEPTION
                'Debe informar la descripción del nomenclador.';
END IF;

        IF p_id_medicamento IS NOT NULL
           OR p_troquel IS NOT NULL
           OR NULLIF(
               btrim(
                   p_nombre_medicamento
               ),
               ''
           ) IS NOT NULL THEN

            RAISE EXCEPTION
                'Un detalle nuevo de nomenclador no puede contener datos de medicamento.';
END IF;
END IF;

INSERT INTO compras.requerimiento_detalle (
    id_requerimiento,
    tipo_item,

    id_prestacion,
    id_tipo_nomenclador,
    codigo_nomenclador,
    descripcion_nomenclador,

    id_medicamento,
    troquel,
    nombre_medicamento,

    cantidad,
    precio_unitario_estimado,
    precio_total_estimado,
    id_prestador,

    observaciones,
    alta_usr
)
VALUES (
           p_id_requerimiento,
           v_tipo_item,

           CASE WHEN v_tipo_item = 'NOMENCLADOR'
                THEN p_id_prestacion ELSE NULL END,
           CASE WHEN v_tipo_item = 'NOMENCLADOR'
                THEN v_id_tipo_nomenclador_real ELSE NULL END,
           CASE WHEN v_tipo_item = 'NOMENCLADOR' THEN NULLIF(
                   btrim(
                           p_codigo_nomenclador
                   ),
                   ''
           ) ELSE NULL END,
           CASE WHEN v_tipo_item = 'NOMENCLADOR' THEN NULLIF(
                   btrim(
                           p_descripcion_nomenclador
                   ),
                   ''
           ) ELSE NULL END,

           NULL,
           NULL,
           NULL,

           p_cantidad,
           NULL,
           NULL,
           NULL,

           NULLIF(
                   btrim(
                           p_observaciones
                   ),
                   ''
           ),
           v_usuario
       )
    RETURNING id_detalle
INTO v_id;

RETURN v_id;
END IF;

    /*
     * EDICIONES
     *
     * Se bloquea la fila y se determina el tipo realmente
     * persistido. El tipo recibido por HTTP no es autoritativo.
     */
SELECT d.tipo_item
INTO v_tipo_item_actual
FROM compras.requerimiento_detalle d
WHERE d.id_detalle = p_id
  AND d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontró el detalle activo a modificar.';
END IF;

    IF v_tipo_item_actual = 'OBSERVACION' THEN
        IF v_tipo_item_esperado <> 'OBSERVACION'
           OR v_tipo_item <> 'OBSERVACION' THEN
            RAISE EXCEPTION
                'Un detalle de observación no puede convertirse a otro tipo.';
END IF;

        IF NULLIF(btrim(p_observaciones), '') IS NULL THEN
            RAISE EXCEPTION
                'Debe informar las observaciones del detalle.';
END IF;

        IF p_id_prestacion IS NOT NULL
           OR p_id_tipo_nomenclador IS NOT NULL
           OR NULLIF(btrim(p_codigo_nomenclador), '') IS NOT NULL
           OR NULLIF(btrim(p_descripcion_nomenclador), '') IS NOT NULL
           OR p_id_medicamento IS NOT NULL
           OR p_troquel IS NOT NULL
           OR NULLIF(btrim(p_nombre_medicamento), '') IS NOT NULL THEN

            RAISE EXCEPTION
                'Un detalle de observación no puede contener datos técnicos.';
END IF;

UPDATE compras.requerimiento_detalle
SET cantidad = p_cantidad,
    precio_unitario_estimado = NULL,
    precio_total_estimado = NULL,
    id_prestador = NULL,
    observaciones = NULLIF(btrim(p_observaciones), ''),
    modi_fecha = now(),
    modi_usr = v_usuario
WHERE id_detalle = p_id
  AND id_requerimiento = p_id_requerimiento
  AND baja_fecha IS NULL
    RETURNING id_detalle
INTO v_id;

RETURN v_id;
END IF;

    /*
     * Histórico MEDICAMENTO:
     *
     * sólo cambia Cantidad y Observaciones.
     * No se toca ID, troquel ni nombre.
     */
    IF v_tipo_item_actual = 'MEDICAMENTO' THEN

        IF v_tipo_item <> 'MEDICAMENTO' THEN
            RAISE EXCEPTION
                'El detalle histórico de medicamento no puede convertirse directamente.';
END IF;

UPDATE compras.requerimiento_detalle
SET cantidad = p_cantidad,

    precio_unitario_estimado = NULL,
    precio_total_estimado = NULL,
    id_prestador = NULL,

    observaciones =
        NULLIF(
                btrim(
                        p_observaciones
                ),
                ''
        ),

    modi_fecha = now(),
    modi_usr = v_usuario
WHERE id_detalle = p_id
  AND id_requerimiento = p_id_requerimiento
  AND baja_fecha IS NULL
    RETURNING id_detalle
INTO v_id;

IF v_id IS NULL THEN
            RAISE EXCEPTION
                'No se encontró el detalle histórico a modificar.';
END IF;

RETURN v_id;
END IF;

    IF v_tipo_item_actual <> 'NOMENCLADOR' THEN
        RAISE EXCEPTION
            'El detalle persistido tiene un tipo técnico desconocido.';
END IF;

    IF v_tipo_item_esperado <> 'NOMENCLADOR' THEN
        RAISE EXCEPTION
            'El sector % requiere detalles de tipo %.',
            v_sector,
            v_tipo_item_esperado;
END IF;

    IF v_tipo_item <> 'NOMENCLADOR' THEN
        RAISE EXCEPTION
            'Un detalle de nomenclador no puede convertirse a otro tipo.';
END IF;

    IF p_id_prestacion IS NULL
       OR p_id_prestacion <= 0 THEN

        RAISE EXCEPTION
            'Debe informar la prestación del nomenclador.';
END IF;

    IF p_id_tipo_nomenclador IS NULL
       OR p_id_tipo_nomenclador <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el tipo real de nomenclador.';
END IF;

SELECT n.id_tipo_nomenclador
INTO v_id_tipo_nomenclador_real
FROM autorizaciones.nomenclador n
WHERE n.id_prestacion = p_id_prestacion
  AND n.baja_fecha IS NULL;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'La prestación seleccionada no existe o no está activa.';
END IF;

    IF v_id_tipo_nomenclador_real
            <> p_id_tipo_nomenclador THEN

        RAISE EXCEPTION
            'El tipo de nomenclador informado no corresponde a la prestación seleccionada.';
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

    IF p_codigo_nomenclador IS NULL
       OR length(
           btrim(
               p_codigo_nomenclador
           )
       ) = 0 THEN

        RAISE EXCEPTION
            'Debe informar el código de nomenclador.';
END IF;

    IF p_descripcion_nomenclador IS NULL
       OR length(
           btrim(
               p_descripcion_nomenclador
           )
       ) = 0 THEN

        RAISE EXCEPTION
            'Debe informar la descripción del nomenclador.';
END IF;

    IF p_id_medicamento IS NOT NULL
       OR p_troquel IS NOT NULL
       OR NULLIF(
           btrim(
               p_nombre_medicamento
           ),
           ''
       ) IS NOT NULL THEN

        RAISE EXCEPTION
            'El detalle de nomenclador contiene datos de medicamento.';
END IF;

UPDATE compras.requerimiento_detalle
SET tipo_item = 'NOMENCLADOR',

    id_prestacion =
        p_id_prestacion,

    id_tipo_nomenclador =
        v_id_tipo_nomenclador_real,

    codigo_nomenclador =
        NULLIF(
                btrim(
                        p_codigo_nomenclador
                ),
                ''
        ),

    descripcion_nomenclador =
        NULLIF(
                btrim(
                        p_descripcion_nomenclador
                ),
                ''
        ),

    id_medicamento = NULL,
    troquel = NULL,
    nombre_medicamento = NULL,

    cantidad = p_cantidad,

    precio_unitario_estimado = NULL,
    precio_total_estimado = NULL,
    id_prestador = NULL,

    observaciones =
        NULLIF(
                btrim(
                        p_observaciones
                ),
                ''
        ),

    modi_fecha = now(),
    modi_usr = v_usuario
WHERE id_detalle = p_id
  AND id_requerimiento = p_id_requerimiento
  AND baja_fecha IS NULL
    RETURNING id_detalle
INTO v_id;

IF v_id IS NULL THEN
        RAISE EXCEPTION
            'No se encontró el detalle a modificar.';
END IF;

RETURN v_id;
END;
$func$
LANGUAGE plpgsql;


CREATE FUNCTION compras.validar_tipo_prestacion_detalle_fila()
RETURNS TRIGGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_id_sector_requerimiento INTEGER;
    v_id_sector_tipo INTEGER;
BEGIN
    SELECT r.estado, r.id_sector
    INTO v_estado, v_id_sector_requerimiento
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = NEW.id_requerimiento
      AND r.baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo del detalle.';
    END IF;

    IF TG_OP = 'UPDATE'
       AND NEW.id_tipo_prestacion
            IS DISTINCT FROM OLD.id_tipo_prestacion THEN

        IF v_estado <> 1 THEN
            RAISE EXCEPTION
                'El tipo de prestación solo puede modificarse en estado PENDIENTE.';
        END IF;

        IF OLD.id_tipo_prestacion IS NOT NULL
           AND NEW.id_tipo_prestacion IS NULL THEN

            RAISE EXCEPTION
                'El tipo de prestación ya informado no puede quitarse.';
        END IF;
    END IF;

    IF NEW.id_tipo_prestacion IS NULL THEN
        RETURN NEW;
    END IF;

    SELECT t.id_sector
    INTO v_id_sector_tipo
    FROM compras.tipo_prestacion t
    WHERE t.id_tipo_prestacion = NEW.id_tipo_prestacion;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El tipo de prestación informado no existe.';
    END IF;

    IF v_id_sector_tipo <> v_id_sector_requerimiento THEN
        RAISE EXCEPTION
            'El tipo de prestación no corresponde al sector del requerimiento.';
    END IF;

    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

CREATE TRIGGER tr_compras_detalle_tipo_prestacion
    BEFORE INSERT OR UPDATE OF id_tipo_prestacion, id_requerimiento
    ON compras.requerimiento_detalle
    FOR EACH ROW
    EXECUTE PROCEDURE compras.validar_tipo_prestacion_detalle_fila();

CREATE FUNCTION compras.validar_tipo_prestacion_detalle_nuevo()
RETURNS TRIGGER
AS $func$
DECLARE
    v_id_tipo_prestacion SMALLINT;
    v_id_tipo_nomenclador INTEGER;
    v_cantidad_tipos_sector INTEGER;
BEGIN
    SELECT
        d.id_tipo_prestacion,
        d.id_tipo_nomenclador,
        (
            SELECT count(*)
            FROM compras.tipo_prestacion t
            WHERE t.id_sector = r.id_sector
        )
    INTO
        v_id_tipo_prestacion,
        v_id_tipo_nomenclador,
        v_cantidad_tipos_sector
    FROM compras.requerimiento_detalle d
    JOIN compras.requerimiento r
      ON r.id_requerimiento = d.id_requerimiento
    WHERE d.id_detalle = NEW.id_detalle
      AND d.baja_fecha IS NULL
      AND r.baja_fecha IS NULL;

    IF TG_OP = 'INSERT'
       AND FOUND
       AND v_cantidad_tipos_sector > 0
       AND v_id_tipo_prestacion IS NULL THEN

        RAISE EXCEPTION
            'Debe seleccionar el tipo de prestación.';
    END IF;

    IF FOUND
       AND v_id_tipo_prestacion = 6
       AND v_id_tipo_nomenclador IS DISTINCT FROM 10 THEN

        RAISE EXCEPTION
            'Para Insumos el tipo de nomenclador debe ser 10.';
    ELSIF FOUND
          AND v_id_tipo_prestacion <> 6
          AND v_id_tipo_nomenclador = 10 THEN

        RAISE EXCEPTION
            'El nomenclador tipo 10 corresponde exclusivamente a Insumos.';
    END IF;

    RETURN NULL;
END;
$func$
LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER tr_compras_detalle_tipo_prestacion_nuevo
    AFTER INSERT OR UPDATE
    ON compras.requerimiento_detalle
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW
    EXECUTE PROCEDURE compras.validar_tipo_prestacion_detalle_nuevo();

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


CREATE FUNCTION compras.borrar_requerimiento_detalle(
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


    IF v_estado NOT IN (1, 2) THEN
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


    IF v_estado = 2 THEN

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
$func$
LANGUAGE plpgsql;

CREATE FUNCTION compras.finalizar_cotizacion_requerimiento(
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
    -- modificar la cotización.
    IF v_estado = 3 THEN
        RETURN 3;
END IF;

    IF v_estado <> 2 THEN
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
        RETURN 2;
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
        3,
        v_usuario
    );

RETURN 3;
END;
$func$
LANGUAGE plpgsql;


-- =====================================================================
-- PRESTADORES PARA COTIZACIÓN
-- =====================================================================

/*
 * Fuente canónica de 0..N destinatarios del prestador:
 * public.prestad_contacto_e -> public.contacto_e.
 *
 * El resultado se separa por ';' para conservar el contrato Java vigente.
 */
CREATE FUNCTION compras.resolver_emails_cotizacion_prestador(
    p_id_prestador INTEGER
)
RETURNS TEXT
AS $func$
SELECT string_agg(
           contacto.email,
           ';' ORDER BY
               contacto.prioridad,
               contacto.fecha_referencia DESC NULLS LAST,
               contacto.id_contacto_e DESC,
               lower(contacto.email)
       )
FROM (
    SELECT DISTINCT ON (
        lower(btrim(ce.contacto))
    )
        btrim(ce.contacto) AS email,
        CASE upper(btrim(COALESCE(ce.tipo_contacto_e, '')))
            WHEN 'E' THEN 1
            WHEN 'F' THEN 2
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
        CASE upper(btrim(COALESCE(ce.tipo_contacto_e, '')))
            WHEN 'E' THEN 1
            WHEN 'F' THEN 2
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
$func$
LANGUAGE sql
STABLE;

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

CREATE FUNCTION compras.listar_prestadores_cotizacion_requerimiento(
    p_id_requerimiento INTEGER
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
  AND r.estado IN (1, 2)
  AND r.baja_fecha IS NULL
  AND EXISTS (
      SELECT 1
      FROM compras.requerimiento_detalle d
      JOIN compras.tipo_prestacion t
        ON t.id_tipo_prestacion = d.id_tipo_prestacion
      JOIN public.prestador_rubro pr
        ON pr.id_prestador = p.id_prestador
       AND compras.normalizar_rubro(pr.rubro) = t.descripcion
      WHERE d.id_requerimiento = r.id_requerimiento
        AND d.baja_fecha IS NULL
  )
  AND (
    -- Primer envio o recuperación: se listan todos los
    -- candidatos vigentes. registrar_cotizacion_prestador
    -- evita reenviar ENVIADO o PROCESANDO.
    r.estado = 1

        OR (
        r.estado = 2
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
$func$
LANGUAGE plpgsql
STABLE;


CREATE FUNCTION compras.registrar_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,
    p_usuario VARCHAR
)
    RETURNS BOOLEAN
AS $func$
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
  AND r.estado IN (1, 2)
  AND r.baja_fecha IS NULL
  AND EXISTS (
      SELECT 1
      FROM compras.requerimiento_detalle d
      JOIN compras.tipo_prestacion t
        ON t.id_tipo_prestacion = d.id_tipo_prestacion
      JOIN public.prestador_rubro pr
        ON pr.id_prestador = p.id_prestador
       AND compras.normalizar_rubro(pr.rubro) = t.descripcion
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
$func$
LANGUAGE plpgsql;


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


CREATE FUNCTION compras.buscar_prestadores_enviados(
    p_id_requerimiento INTEGER,
    p_texto VARCHAR,
    p_limite INTEGER
)
    RETURNS TABLE (
                      id_prestador INTEGER,
                      descripcion VARCHAR,
                      cuit VARCHAR,
                      email VARCHAR,
                      email_destino VARCHAR,
                      id_tipo_prestador INTEGER,
                      tipo_prestador VARCHAR,
                      estado_envio VARCHAR
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
  AND r.estado IN (2, 3, 4, 5, 99)
  AND (
    r.estado <> 2
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
$func$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION compras.listar_prestadores_enviados(
    p_id_requerimiento INTEGER,
    p_limite INTEGER
)
    RETURNS TABLE (
        id_prestador INTEGER,
        descripcion VARCHAR,
        cuit VARCHAR,
        email VARCHAR,
        email_destino VARCHAR,
        id_tipo_prestador INTEGER,
        tipo_prestador VARCHAR,
        estado_envio VARCHAR
    )
AS $func$
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
  AND r.estado IN (
                   2,
                   3,
                   4,
                   5,
                   99
    )
  AND (
    r.estado <> 2
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
$func$
LANGUAGE plpgsql
STABLE;


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

CREATE FUNCTION compras.get_requerimiento_compra_pdf(
    p_id_requerimiento INTEGER
)
    RETURNS TABLE (
                      id_requerimiento INTEGER,
                      alta_fecha TIMESTAMP WITHOUT TIME ZONE,
                      alta_usr VARCHAR,

                      id_estado INTEGER,
                      estado_descripcion VARCHAR,

                      id_sector INTEGER,
                      sector_descripcion VARCHAR,
                      requiere_afiliado BOOLEAN,

                      afiliado_id_ospim INTEGER,
                      afiliado_int INTEGER,
                      afiliado_nombre_apellido VARCHAR,
                      afiliado_documento VARCHAR,

                      afiliado_direccion VARCHAR,
                      afiliado_localidad VARCHAR,
                      afiliado_provincia VARCHAR,
                      afiliado_celular VARCHAR,
                      afiliado_telefono VARCHAR,
                      afiliado_email VARCHAR,

                      cargo_ospim INTEGER,
                      cargo_tercerizadora INTEGER,
                      id_tercerizadora VARCHAR,
                      recupero BOOLEAN,
                      observaciones TEXT,

                      detalle_id INTEGER,
                      detalle_orden INTEGER,

                      tipo_item VARCHAR,
                      codigo_item VARCHAR,
                      descripcion_item VARCHAR,

                      id_prestacion INTEGER,
                      id_tipo_nomenclador INTEGER,
                      codigo_nomenclador VARCHAR,
                      descripcion_nomenclador VARCHAR,

                      id_medicamento INTEGER,
                      troquel INTEGER,
                      nombre_medicamento VARCHAR,

                      cantidad INTEGER,

                      precio_unitario_estimado NUMERIC,
                      precio_total_estimado NUMERIC,

                      prestador_razon_social VARCHAR,
                      prestador_cuit VARCHAR,

                      detalle_observaciones TEXT
                  )
    AS $func$
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

    rb.cargo_ospim,
    rb.cargo_tercerizadora,
    rb.id_tercerizadora,
    rb.recupero,
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

    LEFT JOIN compras.get_requerimiento_detalle(
        p_id_requerimiento
    ) d
      ON d.id_requerimiento = rb.id

    WHERE rb.id = p_id_requerimiento

    ORDER BY d.id NULLS LAST;
END;
$func$
LANGUAGE plpgsql
STABLE;

-- =====================================================================
-- FUNCIONES DE PRESUPUESTOS, VíNCULOS Y COTIZACIÓN
-- =====================================================================


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
        descripcion_prestador,
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

CREATE FUNCTION compras.registrar_requerimiento_presupuesto(
    p_id_requerimiento INTEGER,
    p_tipo_documento SMALLINT,
    p_id_prestador INTEGER,
    p_empresa_cuit VARCHAR,
    p_empresa_sucursal VARCHAR,
    p_descripcion_empresa VARCHAR,
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
    v_sector_descripcion VARCHAR(200);
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
        sr.descripcion
      INTO
        v_estado_requerimiento,
        v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> 1
       OR compras.normalizar_sector(v_sector_descripcion)
            NOT IN ('RRHH', 'SISTEMAS') THEN

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
        descripcion_empresa,
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
        3,
        NULL,
        NULL,
        btrim(p_empresa_cuit),
        btrim(p_empresa_sucursal),
        btrim(p_descripcion_empresa),
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        btrim(p_titulo),
        NULL,
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_orden_medica(
    p_id_requerimiento INTEGER,
    p_dl_group_id BIGINT,
    p_dl_folder_id BIGINT,
    p_dl_file_entry_id BIGINT,
    p_dl_file_uuid VARCHAR,
    p_nombre_original VARCHAR,
    p_nombre_persistido VARCHAR,
    p_titulo VARCHAR,
    p_fecha_documento DATE,
    p_numero_receta VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
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

    IF v_estado_requerimiento <> 1 THEN
        RAISE EXCEPTION
            'El adjunto solo puede registrarse durante '
            'el alta de un requerimiento PENDIENTE.';
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
        WHERE rp.tipo_documento = 2
          AND rp.baja_fecha IS NULL
          AND rp.numero_receta = v_numero_receta
          AND rp.fecha_documento = p_fecha_documento
          AND r.estado <> 99
          AND r.afiliado_cuil_titular = v_cuil_titular
          AND r.afiliado_int = v_inte
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
        descripcion_prestador,
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
        NULL,
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

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
$func$
LANGUAGE plpgsql;

CREATE FUNCTION compras.baja_cotizacion_empresa_requerimiento(
    p_id_requerimiento_presupuesto INTEGER,
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_estado_requerimiento INTEGER;
    v_sector_descripcion VARCHAR(200);
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
        sr.descripcion
      INTO
        v_estado_requerimiento,
        v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> 1
       OR compras.normalizar_sector(v_sector_descripcion)
            NOT IN ('RRHH', 'SISTEMAS') THEN

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
$func$
LANGUAGE plpgsql;

CREATE FUNCTION compras.reactivar_cotizacion_empresa_requerimiento(
    p_id_requerimiento_presupuesto INTEGER,
    p_id_requerimiento INTEGER
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_empresa_cuit VARCHAR(11);
    v_empresa_sucursal VARCHAR(6);
    v_estado_requerimiento INTEGER;
    v_sector_descripcion VARCHAR(200);
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RETURN FALSE;
    END IF;

    SELECT
        r.estado,
        sr.descripcion
      INTO
        v_estado_requerimiento,
        v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> 1
       OR compras.normalizar_sector(v_sector_descripcion)
            NOT IN ('RRHH', 'SISTEMAS') THEN

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
$func$
LANGUAGE plpgsql;

-- =====================================================================
-- REQUERIMIENTO COTIZADO -> RECLAMO PRESTACIONAL
-- =====================================================================

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

    IF v_estado_requerimiento <> 3 THEN
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
                   ON compras.normalizar_rubro(pr.rubro) =
                      tp_rubro.descripcion

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

IF v_estado_requerimiento NOT IN (1, 2) THEN
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
     AND compras.normalizar_rubro(pr.rubro) = tp.descripcion
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
CREATE OR REPLACE FUNCTION
compras.finalizar_notificacion_cotizacion_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER,
    p_estado VARCHAR,
    p_error TEXT,
    p_usuario VARCHAR
)
RETURNS TABLE (
    actualizado BOOLEAN,
    estado_anterior TEXT,
    estado_actual TEXT,
    motivo TEXT
)
LANGUAGE plpgsql
AS
$function$
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
                THEN NULL
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

CREATE FUNCTION compras.es_prestador_compatible_cotizacion(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER
)
RETURNS BOOLEAN
AS $func$
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
         AND compras.normalizar_rubro(pr.rubro) = tp.descripcion
        WHERE r.id_requerimiento = p_id_requerimiento
          AND r.baja_fecha IS NULL
    );
$func$
LANGUAGE sql
STABLE;

CREATE FUNCTION compras.validar_prestador_cotizacion_fila()
RETURNS TRIGGER
AS $func$
BEGIN
    IF NOT compras.es_prestador_compatible_cotizacion(
        NEW.id_requerimiento,
        NEW.id_prestador
    ) THEN
        RAISE EXCEPTION
            'El prestador no posee un rubro compatible con el tipo de prestación.';
    END IF;

    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;

CREATE TRIGGER trg_compras_cotizacion_prestador_compatible
    BEFORE INSERT OR UPDATE OF id_requerimiento, id_prestador
    ON compras.requerimiento_cotizacion_prestador
    FOR EACH ROW
    EXECUTE PROCEDURE compras.validar_prestador_cotizacion_fila();

-- =====================================================================
-- BAJAS DIFERIDAS + SURGE + COTIZACIÓN: UNA SOLA TRANSACCIÓN
-- =====================================================================

CREATE FUNCTION compras.guardar_cotizacion_requerimiento(
    p_id_requerimiento INTEGER,
    p_ids_detalle INTEGER[],
    p_precios_unitarios NUMERIC[],
    p_ids_detalle_eliminados INTEGER[],
    p_id_prestador INTEGER,
    p_surge BOOLEAN,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
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

    IF v_estado = 3 THEN
        RETURN 3;
    END IF;

    IF v_estado <> 2 THEN
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
      AND estado = 2
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
$func$
LANGUAGE plpgsql;

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
CREATE OR REPLACE FUNCTION compras.buscar_items_historicos_afiliado(
    p_cuil_titular VARCHAR,
    p_inte INTEGER,
    p_id_sector INTEGER,
    p_id_requerimiento_excluir INTEGER,
    p_limite INTEGER
)
RETURNS TABLE (
    id_prestacion INTEGER,
    id_tipo_nomenclador INTEGER,
    codigo VARCHAR,
    descripcion VARCHAR
)
AS $func$
    SELECT
        historico.id_prestacion,
        historico.id_tipo_nomenclador,
        historico.codigo,
        historico.descripcion
    FROM (
        SELECT DISTINCT ON (d.id_prestacion, d.id_tipo_nomenclador)
            d.id_prestacion,
            d.id_tipo_nomenclador,
            NULLIF(BTRIM(d.codigo_nomenclador), '') AS codigo,
            NULLIF(BTRIM(d.descripcion_nomenclador), '') AS descripcion,
            r.alta_fecha AS fecha_requerimiento,
            r.id_requerimiento AS id_requerimiento_origen,
            d.id_detalle AS id_detalle_origen
        FROM compras.requerimiento r
        INNER JOIN compras.requerimiento_detalle d
            ON d.id_requerimiento = r.id_requerimiento
        WHERE r.afiliado_cuil_titular = p_cuil_titular
          AND r.afiliado_int = p_inte
          AND r.id_sector = p_id_sector
          AND r.id_requerimiento <> p_id_requerimiento_excluir
          AND r.baja_fecha IS NULL
          AND d.baja_fecha IS NULL
          AND d.tipo_item = 'NOMENCLADOR'
          AND d.id_prestacion IS NOT NULL
          AND d.id_prestacion > 0
          AND d.id_tipo_nomenclador IS NOT NULL
          AND d.id_tipo_nomenclador > 0
          AND NULLIF(BTRIM(d.codigo_nomenclador), '') IS NOT NULL
          AND NULLIF(BTRIM(d.descripcion_nomenclador), '') IS NOT NULL
        ORDER BY
            d.id_prestacion,
            d.id_tipo_nomenclador,
            r.alta_fecha DESC NULLS LAST,
            r.id_requerimiento DESC,
            d.id_detalle DESC
    ) historico
    ORDER BY
        historico.fecha_requerimiento DESC NULLS LAST,
        historico.id_requerimiento_origen DESC,
        historico.id_detalle_origen DESC
    LIMIT p_limite;
$func$
LANGUAGE sql
STABLE;

-- Regla canónica de duplicados: persona + prestación + fecha de Orden Médica.
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
            INNER JOIN compras.requerimiento_detalle d
                ON d.id_requerimiento = r.id_requerimiento
            INNER JOIN compras.requerimiento_presupuesto rp
                ON rp.id_requerimiento = r.id_requerimiento
            WHERE r.baja_fecha IS NULL
              AND r.estado <> 99
              AND r.afiliado_cuil_titular = btrim(p_cuil_titular)
              AND r.afiliado_int = p_inte
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
$func$
LANGUAGE sql
STABLE;

-- =====================================================================
-- BÚSQUEDA TÉCNICA DE PRESTACIONES MÉDICAS
-- =====================================================================

CREATE OR REPLACE FUNCTION autorizaciones.busca_nomenclador_prest_med_compras(
    tiponomenclador_p INTEGER,
    descripcionnomenclador_p VARCHAR,
    especialidad_p INTEGER,
    codigonomenclador_p VARCHAR,
    recuperasur_p BOOLEAN,
    resolucionnomenclador_p VARCHAR
)
RETURNS SETOF autorizaciones.nomenclador_detalle
AS $func$
BEGIN
    RETURN QUERY
    SELECT n.*
    FROM autorizaciones.busca_nomenclador(
        tiponomenclador_p,
        NULL,
        especialidad_p,
        codigonomenclador_p,
        recuperasur_p,
        resolucionnomenclador_p
    ) n
    WHERE n.id_tipo_nomenclador <> 1
      AND (
          tiponomenclador_p IS NULL
          OR n.id_tipo_nomenclador = tiponomenclador_p
      )
      AND (
          descripcionnomenclador_p IS NULL
          OR btrim(descripcionnomenclador_p) = ''
          OR translate(
              upper(COALESCE(n.descripcion, '')),
              chr(193) || chr(201) || chr(205) || chr(211)
                  || chr(218) || chr(220) || chr(209) || chr(192)
                  || chr(200) || chr(204) || chr(210) || chr(217)
                  || chr(196) || chr(203) || chr(207) || chr(214)
                  || chr(220) || chr(194) || chr(202) || chr(206)
                  || chr(212) || chr(219),
              'AEIOUUNAEIOUAEIOUAEIOU'
          ) LIKE '%' || translate(
              upper(btrim(descripcionnomenclador_p)),
              chr(193) || chr(201) || chr(205) || chr(211)
                  || chr(218) || chr(220) || chr(209) || chr(192)
                  || chr(200) || chr(204) || chr(210) || chr(217)
                  || chr(196) || chr(203) || chr(207) || chr(214)
                  || chr(220) || chr(194) || chr(202) || chr(206)
                  || chr(212) || chr(219),
              'AEIOUUNAEIOUAEIOUAEIOU'
          ) || '%'
      );
END;
$func$
LANGUAGE plpgsql
VOLATILE;

COMMIT;
