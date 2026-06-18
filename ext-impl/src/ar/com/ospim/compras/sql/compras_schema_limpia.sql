CREATE SCHEMA IF NOT EXISTS compras;

CREATE TABLE IF NOT EXISTS compras.sector_requerimiento (
    id_sector serial PRIMARY KEY,
    descripcion varchar(120) NOT NULL,
    requiere_afiliado boolean NOT NULL DEFAULT false,
    activo boolean NOT NULL DEFAULT true,
    alta_fecha timestamp without time zone NOT NULL DEFAULT now(),
    alta_usr varchar(100) NOT NULL,
    modi_fecha timestamp without time zone,
    modi_usr varchar(100),
    baja_fecha timestamp without time zone,
    baja_usr varchar(100)
);

CREATE TABLE IF NOT EXISTS compras.articulo (
    id_articulo serial PRIMARY KEY,
    id_sector integer NOT NULL REFERENCES compras.sector_requerimiento(id_sector),
    descripcion varchar(200) NOT NULL,
    activo boolean NOT NULL DEFAULT true,
    baja_fecha timestamp without time zone
);

CREATE TABLE IF NOT EXISTS compras.sector_tipo_prestador (
    id_sector integer NOT NULL REFERENCES compras.sector_requerimiento(id_sector),
    id_tipo_prestador integer NOT NULL,
    activo boolean NOT NULL DEFAULT true,
    alta_fecha timestamp without time zone NOT NULL DEFAULT now(),
    alta_usr varchar(100) NOT NULL,
    modi_fecha timestamp without time zone,
    modi_usr varchar(100),
    baja_fecha timestamp without time zone,
    baja_usr varchar(100),
    PRIMARY KEY (id_sector, id_tipo_prestador)
);

CREATE TABLE IF NOT EXISTS compras.requerimiento (
    id_requerimiento serial PRIMARY KEY,
    estado integer NOT NULL DEFAULT 1,
    id_sector integer NOT NULL REFERENCES compras.sector_requerimiento(id_sector),
    afiliado_cuil_titular varchar(20),
    afiliado_int integer,
    afiliado_nombre varchar(120),
    afiliado_apellido varchar(120),
    afiliado_documento_tipo varchar(10),
    afiliado_documento_nro varchar(30),
    afiliado_direccion varchar(250),
    afiliado_localidad varchar(120),
    afiliado_provincia varchar(120),
    afiliado_celular varchar(80),
    afiliado_telefono varchar(80),
    afiliado_email varchar(160),
    cargo_ospim integer NOT NULL DEFAULT 0,
    cargo_tercerizadora integer NOT NULL DEFAULT 0,
    id_tercerizadora varchar(40),
    recupero boolean NOT NULL DEFAULT false,
    observaciones text,
    alta_fecha timestamp without time zone NOT NULL DEFAULT now(),
    alta_usr varchar(100) NOT NULL,
    modi_fecha timestamp without time zone,
    modi_usr varchar(100),
    baja_fecha timestamp without time zone,
    baja_usr varchar(100),
    CONSTRAINT requerimiento_estado_chk CHECK (estado IN (1, 2, 3, 4, 5, 99)),
    CONSTRAINT requerimiento_cargos_chk CHECK (
        cargo_ospim >= 0
        AND cargo_tercerizadora >= 0
        AND cargo_ospim + cargo_tercerizadora <= 100
    )
);

CREATE INDEX IF NOT EXISTS ix_compras_requerimiento_estado
    ON compras.requerimiento(estado)
    WHERE baja_fecha IS NULL;

CREATE INDEX IF NOT EXISTS ix_compras_requerimiento_sector
    ON compras.requerimiento(id_sector)
    WHERE baja_fecha IS NULL;

CREATE TABLE IF NOT EXISTS compras.requerimiento_detalle (
    id_detalle serial PRIMARY KEY,
    id_requerimiento integer NOT NULL REFERENCES compras.requerimiento(id_requerimiento),
    id_articulo integer NOT NULL REFERENCES compras.articulo(id_articulo),
    cantidad integer NOT NULL,
    observaciones text,
    precio_unitario_estimado numeric(18, 2),
    precio_total_estimado numeric(18, 2),
    id_prestador integer REFERENCES liquidaciones.prestador(id_prestador),
    alta_fecha timestamp without time zone NOT NULL DEFAULT now(),
    alta_usr varchar(100) NOT NULL,
    modi_fecha timestamp without time zone,
    modi_usr varchar(100),
    baja_fecha timestamp without time zone,
    baja_usr varchar(100),
    CONSTRAINT requerimiento_detalle_cantidad_chk CHECK (cantidad > 0),
    CONSTRAINT requerimiento_detalle_precio_unitario_chk CHECK (
        precio_unitario_estimado IS NULL OR precio_unitario_estimado >= 0
    ),
    CONSTRAINT requerimiento_detalle_precio_total_chk CHECK (
        precio_total_estimado IS NULL OR precio_total_estimado >= 0
    )
);

CREATE INDEX IF NOT EXISTS ix_compras_detalle_requerimiento
    ON compras.requerimiento_detalle(id_requerimiento)
    WHERE baja_fecha IS NULL;

CREATE INDEX IF NOT EXISTS ix_compras_detalle_prestador
    ON compras.requerimiento_detalle(id_prestador)
    WHERE baja_fecha IS NULL AND id_prestador IS NOT NULL;

CREATE TABLE IF NOT EXISTS compras.requerimiento_cotizacion_prestador (
    id_requerimiento integer NOT NULL REFERENCES compras.requerimiento(id_requerimiento),
    id_prestador integer NOT NULL REFERENCES liquidaciones.prestador(id_prestador),
    estado_envio varchar(20) NOT NULL DEFAULT 'PENDIENTE',
    intentos integer NOT NULL DEFAULT 0,
    email_destino varchar(320),
    fecha_creacion timestamp without time zone NOT NULL DEFAULT now(),
    fecha_ultimo_intento timestamp without time zone,
    fecha_envio timestamp without time zone,
    ultimo_error text,
    alta_usr varchar(100) NOT NULL,
    modi_fecha timestamp without time zone,
    modi_usr varchar(100),
    PRIMARY KEY (id_requerimiento, id_prestador),
    CONSTRAINT cotizacion_prestador_estado_chk CHECK (
        estado_envio IN ('PENDIENTE', 'PROCESANDO', 'ENVIADO', 'ERROR', 'EMAIL_INVALIDO')
    ),
    CONSTRAINT cotizacion_prestador_intentos_chk CHECK (intentos >= 0)
);

CREATE INDEX IF NOT EXISTS ix_compras_cotizacion_prestador_estado
    ON compras.requerimiento_cotizacion_prestador(id_requerimiento, estado_envio);

CREATE INDEX IF NOT EXISTS ix_compras_cotizacion_prestador_prestador
    ON compras.requerimiento_cotizacion_prestador(id_prestador);

CREATE OR REPLACE FUNCTION compras.estado_requerimiento_descripcion(p_estado integer)
RETURNS varchar AS $$
BEGIN
    RETURN CASE p_estado
        WHEN 1 THEN 'Pendiente'
        WHEN 2 THEN 'A cotizar'
        WHEN 3 THEN 'Cotizado'
        WHEN 4 THEN 'Autorizado'
        WHEN 5 THEN 'Orden de compra'
        WHEN 99 THEN 'Anulado'
        ELSE 'Desconocido'
    END;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

CREATE OR REPLACE FUNCTION compras.validar_detalle_estado_requerimiento()
RETURNS trigger AS $$
DECLARE
    v_estado integer;
BEGIN
    SELECT r.estado
      INTO v_estado
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = NEW.id_requerimiento;

    IF v_estado = 1
       AND (NEW.precio_unitario_estimado IS NOT NULL
            OR NEW.precio_total_estimado IS NOT NULL
            OR NEW.id_prestador IS NOT NULL) THEN
        RAISE EXCEPTION 'Un requerimiento Pendiente no puede tener datos de cotizacion.';
    END IF;

    IF v_estado = 3
       AND NEW.baja_fecha IS NULL
       AND (NEW.precio_unitario_estimado IS NULL
            OR NEW.precio_total_estimado IS NULL
            OR NEW.id_prestador IS NULL) THEN
        RAISE EXCEPTION 'Un requerimiento Cotizado requiere precio y prestador en todos los detalles.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_compras_detalle_estado ON compras.requerimiento_detalle;
CREATE CONSTRAINT TRIGGER trg_compras_detalle_estado
AFTER INSERT OR UPDATE ON compras.requerimiento_detalle
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE PROCEDURE compras.validar_detalle_estado_requerimiento();

CREATE OR REPLACE FUNCTION compras.validar_cierre_cotizado()
RETURNS trigger AS $$
BEGIN
    IF NEW.estado = 3 THEN
        IF NOT EXISTS (
            SELECT 1
              FROM compras.requerimiento_detalle d
             WHERE d.id_requerimiento = NEW.id_requerimiento
               AND d.baja_fecha IS NULL
        ) THEN
            RAISE EXCEPTION 'No se puede cerrar una cotizacion sin detalles.';
        END IF;

        IF EXISTS (
            SELECT 1
              FROM compras.requerimiento_detalle d
             WHERE d.id_requerimiento = NEW.id_requerimiento
               AND d.baja_fecha IS NULL
               AND (
                   d.cantidad <= 0
                   OR d.precio_unitario_estimado IS NULL
                   OR d.precio_total_estimado IS NULL
                   OR d.id_prestador IS NULL
               )
        ) THEN
            RAISE EXCEPTION 'No se puede cerrar una cotizacion incompleta.';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_compras_requerimiento_cierre ON compras.requerimiento;
CREATE CONSTRAINT TRIGGER trg_compras_requerimiento_cierre
AFTER UPDATE OF estado ON compras.requerimiento
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE PROCEDURE compras.validar_cierre_cotizado();

CREATE OR REPLACE FUNCTION compras.listar_estados_requerimiento()
RETURNS TABLE(id integer, descripcion varchar) AS $$
BEGIN
    RETURN QUERY
    SELECT *
      FROM (VALUES
          (1, 'Pendiente'::varchar),
          (2, 'A cotizar'::varchar),
          (3, 'Cotizado'::varchar),
          (4, 'Autorizado'::varchar),
          (5, 'Orden de compra'::varchar),
          (99, 'Anulado'::varchar)
      ) estados(id, descripcion);
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.listar_sector_requerimiento()
RETURNS TABLE(id integer, descripcion varchar, requiere_afiliado boolean) AS $$
BEGIN
    RETURN QUERY
    SELECT s.id_sector, s.descripcion, s.requiere_afiliado
      FROM compras.sector_requerimiento s
     WHERE s.baja_fecha IS NULL
       AND s.activo = true
     ORDER BY s.descripcion;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.get_sector_requerimiento(p_id_sector integer)
RETURNS TABLE(id integer, descripcion varchar, requiere_afiliado boolean) AS $$
BEGIN
    RETURN QUERY
    SELECT s.id_sector, s.descripcion, s.requiere_afiliado
      FROM compras.sector_requerimiento s
     WHERE s.id_sector = p_id_sector
       AND s.baja_fecha IS NULL;
END;
$$ LANGUAGE plpgsql STABLE;

DROP TYPE IF EXISTS compras.requerimiento_base_row CASCADE;
CREATE TYPE compras.requerimiento_base_row AS (
    id integer,
    alta_fecha timestamp without time zone,
    alta_usr varchar,
    modi_fecha timestamp without time zone,
    modi_usr varchar,
    baja_fecha timestamp without time zone,
    baja_usr varchar,
    afiliado_cuil_titular varchar,
    afiliado_int integer,
    afiliado_nombre varchar,
    afiliado_apellido varchar,
    afiliado_nombre_apellido varchar,
    afiliado_documento_tipo varchar,
    afiliado_documento_nro varchar,
    afiliado_documento varchar,
    afiliado_direccion varchar,
    afiliado_localidad varchar,
    afiliado_provincia varchar,
    afiliado_celular varchar,
    afiliado_telefono varchar,
    afiliado_email varchar,
    id_sector integer,
    sector_descripcion varchar,
    requiere_afiliado boolean,
    cargo_ospim integer,
    cargo_tercerizadora integer,
    id_tercerizadora varchar,
    recupero boolean,
    observaciones text,
    id_estado integer,
    estado_descripcion varchar
);

CREATE OR REPLACE FUNCTION compras.requerimiento_base()
RETURNS SETOF compras.requerimiento_base_row AS $$
BEGIN
    RETURN QUERY
    SELECT r.id_requerimiento,
           r.alta_fecha,
           r.alta_usr,
           r.modi_fecha,
           r.modi_usr,
           r.baja_fecha,
           r.baja_usr,
           r.afiliado_cuil_titular,
           r.afiliado_int,
           r.afiliado_nombre,
           r.afiliado_apellido,
           trim(coalesce(r.afiliado_apellido, '') || ', ' || coalesce(r.afiliado_nombre, ''))::varchar,
           r.afiliado_documento_tipo,
           r.afiliado_documento_nro,
           trim(coalesce(r.afiliado_documento_tipo, '') || ' ' || coalesce(r.afiliado_documento_nro, ''))::varchar,
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
           r.observaciones,
           r.estado,
           compras.estado_requerimiento_descripcion(r.estado)
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.buscar_requerimientos(
    p_estado integer,
    p_sector integer,
    p_afiliado_cuil_titular varchar,
    p_afiliado_int integer,
    p_id_tercerizadora varchar,
    p_recupero boolean,
    p_texto varchar
)
RETURNS SETOF compras.requerimiento_base_row AS $$
BEGIN
    RETURN QUERY
    SELECT rb.*
      FROM compras.requerimiento_base() rb
     WHERE rb.baja_fecha IS NULL
       AND (p_estado IS NULL OR rb.id_estado = p_estado)
       AND (p_sector IS NULL OR rb.id_sector = p_sector)
       AND (p_afiliado_cuil_titular IS NULL OR rb.afiliado_cuil_titular = p_afiliado_cuil_titular)
       AND (p_afiliado_int IS NULL OR rb.afiliado_int = p_afiliado_int)
       AND (p_id_tercerizadora IS NULL OR rb.id_tercerizadora = p_id_tercerizadora)
       AND (p_recupero IS NULL OR rb.recupero = p_recupero)
       AND (
           p_texto IS NULL
           OR upper(coalesce(rb.observaciones, '')) LIKE '%' || upper(p_texto) || '%'
           OR upper(coalesce(rb.sector_descripcion, '')) LIKE '%' || upper(p_texto) || '%'
           OR upper(coalesce(rb.afiliado_nombre_apellido, '')) LIKE '%' || upper(p_texto) || '%'
       )
     ORDER BY rb.id DESC;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.get_requerimiento(p_id_requerimiento integer)
RETURNS SETOF compras.requerimiento_base_row AS $$
BEGIN
    RETURN QUERY
    SELECT rb.*
      FROM compras.requerimiento_base() rb
     WHERE rb.id = p_id_requerimiento;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.get_estado_actual_requerimiento(p_id_requerimiento integer)
RETURNS TABLE(id integer, descripcion varchar) AS $$
BEGIN
    RETURN QUERY
    SELECT r.estado, compras.estado_requerimiento_descripcion(r.estado)
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_detalle(p_id_requerimiento integer)
RETURNS TABLE(
    id integer,
    id_requerimiento integer,
    id_articulo integer,
    articulo varchar,
    cantidad integer,
    precio_unitario_estimado numeric,
    precio_total_estimado numeric,
    id_prestador integer,
    prestador_cuit varchar,
    prestador_razon_social varchar,
    observaciones text
) AS $$
BEGIN
    RETURN QUERY
    SELECT d.id_detalle,
           d.id_requerimiento,
           d.id_articulo,
           a.descripcion,
           d.cantidad,
           d.precio_unitario_estimado,
           d.precio_total_estimado,
           d.id_prestador,
           p.cuit,
           p.descripcion,
           d.observaciones
      FROM compras.requerimiento_detalle d
      JOIN compras.articulo a ON a.id_articulo = d.id_articulo
      LEFT JOIN liquidaciones.prestador p ON p.id_prestador = d.id_prestador
     WHERE d.id_requerimiento = p_id_requerimiento
       AND d.baja_fecha IS NULL
     ORDER BY d.id_detalle;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento(
    p_id integer,
    p_afiliado_cuil_titular varchar,
    p_afiliado_int integer,
    p_id_sector integer,
    p_cargo_ospim integer,
    p_cargo_tercerizadora integer,
    p_id_tercerizadora varchar,
    p_recupero boolean,
    p_observaciones text,
    p_usuario varchar
)
RETURNS integer AS $$
DECLARE
    v_id integer;
BEGIN
    IF p_id IS NULL OR p_id <= 0 THEN
        INSERT INTO compras.requerimiento (
            estado, afiliado_cuil_titular, afiliado_int, id_sector,
            cargo_ospim, cargo_tercerizadora, id_tercerizadora,
            recupero, observaciones, alta_usr
        ) VALUES (
            1, p_afiliado_cuil_titular, p_afiliado_int, p_id_sector,
            coalesce(p_cargo_ospim, 0), coalesce(p_cargo_tercerizadora, 0),
            p_id_tercerizadora, coalesce(p_recupero, false), p_observaciones,
            coalesce(p_usuario, current_user)
        )
        RETURNING id_requerimiento INTO v_id;

        RETURN v_id;
    END IF;

    UPDATE compras.requerimiento
       SET afiliado_cuil_titular = p_afiliado_cuil_titular,
           afiliado_int = p_afiliado_int,
           id_sector = p_id_sector,
           cargo_ospim = coalesce(p_cargo_ospim, 0),
           cargo_tercerizadora = coalesce(p_cargo_tercerizadora, 0),
           id_tercerizadora = p_id_tercerizadora,
           recupero = coalesce(p_recupero, false),
           observaciones = p_observaciones,
           modi_fecha = now(),
           modi_usr = coalesce(p_usuario, current_user)
     WHERE id_requerimiento = p_id
       AND estado = 1
       AND baja_fecha IS NULL
     RETURNING id_requerimiento INTO v_id;

    IF v_id IS NULL THEN
        RAISE EXCEPTION 'La estructura solo puede modificarse en estado Pendiente.';
    END IF;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento_detalle(
    p_id integer,
    p_id_requerimiento integer,
    p_id_articulo integer,
    p_cantidad integer,
    p_precio_unitario_estimado numeric,
    p_precio_total_estimado numeric,
    p_id_prestador integer,
    p_observaciones text,
    p_usuario varchar
)
RETURNS integer AS $$
DECLARE
    v_id integer;
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento r
         WHERE r.id_requerimiento = p_id_requerimiento
           AND r.estado = 1
           AND r.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION 'Los detalles estructurales solo pueden modificarse en estado Pendiente.';
    END IF;

    IF p_id IS NULL OR p_id <= 0 THEN
        INSERT INTO compras.requerimiento_detalle (
            id_requerimiento, id_articulo, cantidad,
            precio_unitario_estimado, precio_total_estimado, id_prestador,
            observaciones, alta_usr
        ) VALUES (
            p_id_requerimiento, p_id_articulo, p_cantidad,
            NULL, NULL, NULL, p_observaciones, coalesce(p_usuario, current_user)
        )
        RETURNING id_detalle INTO v_id;

        RETURN v_id;
    END IF;

    UPDATE compras.requerimiento_detalle
       SET id_articulo = p_id_articulo,
           cantidad = p_cantidad,
           precio_unitario_estimado = NULL,
           precio_total_estimado = NULL,
           id_prestador = NULL,
           observaciones = p_observaciones,
           modi_fecha = now(),
           modi_usr = coalesce(p_usuario, current_user)
     WHERE id_detalle = p_id
       AND id_requerimiento = p_id_requerimiento
       AND baja_fecha IS NULL
     RETURNING id_detalle INTO v_id;

    IF v_id IS NULL THEN
        RAISE EXCEPTION 'No se encontro el detalle a modificar.';
    END IF;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.borrar_requerimiento_detalle(
    p_id_detalle integer,
    p_usuario varchar
)
RETURNS void AS $$
DECLARE
    v_id_requerimiento integer;
BEGIN
    SELECT d.id_requerimiento
      INTO v_id_requerimiento
      FROM compras.requerimiento_detalle d
     WHERE d.id_detalle = p_id_detalle;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento r
         WHERE r.id_requerimiento = v_id_requerimiento
           AND r.estado = 1
           AND r.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION 'Los detalles solo pueden eliminarse en estado Pendiente.';
    END IF;

    UPDATE compras.requerimiento_detalle
       SET baja_fecha = now(),
           baja_usr = coalesce(p_usuario, current_user)
     WHERE id_detalle = p_id_detalle
       AND baja_fecha IS NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.borrar_requerimiento(
    p_id_requerimiento integer,
    p_usuario varchar
)
RETURNS void AS $$
BEGIN
    UPDATE compras.requerimiento
       SET estado = 99,
           baja_fecha = now(),
           baja_usr = coalesce(p_usuario, current_user)
     WHERE id_requerimiento = p_id_requerimiento
       AND estado IN (1, 2)
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El requerimiento no puede anularse en su estado actual.';
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.cambiar_estado_requerimiento(
    p_id_requerimiento integer,
    p_estado_nuevo integer,
    p_usuario varchar
)
RETURNS void AS $$
DECLARE
    v_estado_actual integer;
BEGIN
    SELECT r.estado
      INTO v_estado_actual
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION 'No se encontro el requerimiento.';
    END IF;

    IF NOT (
        (v_estado_actual = 1 AND p_estado_nuevo IN (2, 99))
        OR (v_estado_actual = 2 AND p_estado_nuevo IN (3, 99))
    ) THEN
        RAISE EXCEPTION 'Transicion de estado invalida.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = p_estado_nuevo,
           modi_fecha = now(),
           modi_usr = coalesce(p_usuario, current_user),
           baja_fecha = CASE WHEN p_estado_nuevo = 99 THEN now() ELSE baja_fecha END,
           baja_usr = CASE WHEN p_estado_nuevo = 99 THEN coalesce(p_usuario, current_user) ELSE baja_usr END
     WHERE id_requerimiento = p_id_requerimiento;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.listar_articulos(
    p_id_sector integer,
    p_texto varchar
)
RETURNS TABLE(id integer, id_sector integer, sector_descripcion varchar, descripcion varchar) AS $$
BEGIN
    RETURN QUERY
    SELECT a.id_articulo, a.id_sector, s.descripcion, a.descripcion
      FROM compras.articulo a
      JOIN compras.sector_requerimiento s ON s.id_sector = a.id_sector
     WHERE a.baja_fecha IS NULL
       AND a.activo = true
       AND (p_id_sector IS NULL OR a.id_sector = p_id_sector)
       AND (p_texto IS NULL OR upper(a.descripcion) LIKE '%' || upper(p_texto) || '%')
     ORDER BY s.descripcion, a.descripcion;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.get_articulo(p_id_articulo integer)
RETURNS TABLE(id integer, id_sector integer, sector_descripcion varchar, descripcion varchar) AS $$
BEGIN
    RETURN QUERY
    SELECT a.id_articulo, a.id_sector, s.descripcion, a.descripcion
      FROM compras.articulo a
      JOIN compras.sector_requerimiento s ON s.id_sector = a.id_sector
     WHERE a.id_articulo = p_id_articulo
       AND a.baja_fecha IS NULL;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.guardar_articulo(
    p_id integer,
    p_id_sector integer,
    p_descripcion varchar
)
RETURNS integer AS $$
DECLARE
    v_id integer;
BEGIN
    IF p_id IS NULL OR p_id <= 0 THEN
        INSERT INTO compras.articulo(id_sector, descripcion)
        VALUES (p_id_sector, p_descripcion)
        RETURNING id_articulo INTO v_id;
        RETURN v_id;
    END IF;

    UPDATE compras.articulo
       SET id_sector = p_id_sector,
           descripcion = p_descripcion
     WHERE id_articulo = p_id
     RETURNING id_articulo INTO v_id;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.borrar_articulo(p_id_articulo integer)
RETURNS void AS $$
BEGIN
    UPDATE compras.articulo
       SET activo = false,
           baja_fecha = now()
     WHERE id_articulo = p_id_articulo;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.listar_tipos_prestador_sector(p_id_sector integer)
RETURNS TABLE(id_tipo_prestador integer, descripcion varchar, activo boolean) AS $$
BEGIN
    RETURN QUERY
    SELECT tp.id_tipo_prestador,
           tp.descripcion,
           coalesce(stp.activo, false)
      FROM liquidaciones.tipo_prestador tp
      LEFT JOIN compras.sector_tipo_prestador stp
        ON stp.id_tipo_prestador = tp.id_tipo_prestador
       AND stp.id_sector = p_id_sector
       AND stp.baja_fecha IS NULL
     ORDER BY tp.descripcion;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.desactivar_tipos_prestador_sector(
    p_id_sector integer,
    p_usuario varchar
)
RETURNS void AS $$
BEGIN
    UPDATE compras.sector_tipo_prestador
       SET activo = false,
           modi_fecha = now(),
           modi_usr = coalesce(p_usuario, current_user)
     WHERE id_sector = p_id_sector
       AND baja_fecha IS NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.guardar_sector_tipo_prestador(
    p_id_sector integer,
    p_id_tipo_prestador integer,
    p_activo boolean,
    p_usuario varchar
)
RETURNS void AS $$
BEGIN
    INSERT INTO compras.sector_tipo_prestador(
        id_sector, id_tipo_prestador, activo, alta_usr
    ) VALUES (
        p_id_sector, p_id_tipo_prestador, coalesce(p_activo, true),
        coalesce(p_usuario, current_user)
    )
    ON CONFLICT (id_sector, id_tipo_prestador) DO UPDATE
       SET activo = EXCLUDED.activo,
           modi_fecha = now(),
           modi_usr = coalesce(p_usuario, current_user),
           baja_fecha = NULL,
           baja_usr = NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.listar_prestadores_cotizacion_requerimiento(
    p_id_requerimiento integer
)
RETURNS TABLE(
    id_prestador integer,
    descripcion varchar,
    cuit varchar,
    email varchar,
    id_tipo_prestador integer,
    tipo_prestador varchar
) AS $$
BEGIN
    RETURN QUERY
    SELECT p.id_prestador,
           p.descripcion,
           p.cuit,
           p.email,
           p.id_tipo_prestador,
           tp.descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_tipo_prestador stp
        ON stp.id_sector = r.id_sector
       AND stp.activo = true
       AND stp.baja_fecha IS NULL
      JOIN liquidaciones.prestador p
        ON p.id_tipo_prestador = stp.id_tipo_prestador
      LEFT JOIN liquidaciones.tipo_prestador tp
        ON tp.id_tipo_prestador = p.id_tipo_prestador
      LEFT JOIN compras.requerimiento_cotizacion_prestador rcp
        ON rcp.id_requerimiento = r.id_requerimiento
       AND rcp.id_prestador = p.id_prestador
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.estado IN (1, 2)
       AND r.baja_fecha IS NULL
       AND coalesce(p.solicitar_cotizacion, false) = true
       AND p.baja_fecha IS NULL
       AND (rcp.estado_envio IS NULL OR rcp.estado_envio IN ('PENDIENTE', 'ERROR', 'EMAIL_INVALIDO'))
     ORDER BY p.descripcion;
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.registrar_cotizacion_prestador(
    p_id_requerimiento integer,
    p_id_prestador integer,
    p_usuario varchar
)
RETURNS boolean AS $$
DECLARE
    v_email varchar;
    v_estado varchar;
BEGIN
    SELECT p.email
      INTO v_email
      FROM liquidaciones.prestador p
     WHERE p.id_prestador = p_id_prestador
       AND p.baja_fecha IS NULL;

    INSERT INTO compras.requerimiento_cotizacion_prestador(
        id_requerimiento, id_prestador, estado_envio, intentos,
        email_destino, fecha_ultimo_intento, alta_usr
    ) VALUES (
        p_id_requerimiento, p_id_prestador, 'PROCESANDO', 1,
        v_email, now(), coalesce(p_usuario, current_user)
    )
    ON CONFLICT (id_requerimiento, id_prestador) DO UPDATE
       SET estado_envio = 'PROCESANDO',
           intentos = compras.requerimiento_cotizacion_prestador.intentos + 1,
           email_destino = EXCLUDED.email_destino,
           fecha_ultimo_intento = now(),
           modi_fecha = now(),
           modi_usr = coalesce(p_usuario, current_user)
     WHERE compras.requerimiento_cotizacion_prestador.estado_envio
           IN ('PENDIENTE', 'ERROR', 'EMAIL_INVALIDO');

    SELECT rcp.estado_envio
      INTO v_estado
      FROM compras.requerimiento_cotizacion_prestador rcp
     WHERE rcp.id_requerimiento = p_id_requerimiento
       AND rcp.id_prestador = p_id_prestador;

    RETURN v_estado = 'PROCESANDO';
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.finalizar_cotizacion_prestador(
    p_id_requerimiento integer,
    p_id_prestador integer,
    p_estado varchar,
    p_error text
)
RETURNS boolean AS $$
BEGIN
    UPDATE compras.requerimiento_cotizacion_prestador
       SET estado_envio = p_estado,
           fecha_envio = CASE WHEN p_estado = 'ENVIADO' THEN now() ELSE fecha_envio END,
           ultimo_error = p_error,
           modi_fecha = now()
     WHERE id_requerimiento = p_id_requerimiento
       AND id_prestador = p_id_prestador
       AND estado_envio = 'PROCESANDO';

    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.buscar_prestadores_enviados(
    p_id_requerimiento integer,
    p_texto varchar,
    p_limite integer
)
RETURNS TABLE(
    id_prestador integer,
    descripcion varchar,
    cuit varchar,
    email varchar,
    id_tipo_prestador integer,
    tipo_prestador varchar
) AS $$
DECLARE
    v_texto varchar;
    v_cuit varchar;
BEGIN
    v_texto := upper(coalesce(trim(p_texto), ''));
    v_cuit := regexp_replace(coalesce(p_texto, ''), '[^0-9]', '', 'g');

    RETURN QUERY
    SELECT DISTINCT p.id_prestador,
           p.descripcion,
           p.cuit,
           p.email,
           p.id_tipo_prestador,
           tp.descripcion
      FROM compras.requerimiento_cotizacion_prestador rcp
      JOIN liquidaciones.prestador p
        ON p.id_prestador = rcp.id_prestador
      LEFT JOIN liquidaciones.tipo_prestador tp
        ON tp.id_tipo_prestador = p.id_tipo_prestador
     WHERE rcp.id_requerimiento = p_id_requerimiento
       AND rcp.estado_envio = 'ENVIADO'
       AND (
           v_texto = ''
           OR upper(p.descripcion) LIKE '%' || v_texto || '%'
           OR regexp_replace(coalesce(p.cuit, ''), '[^0-9]', '', 'g') LIKE '%' || v_cuit || '%'
       )
     ORDER BY p.descripcion
     LIMIT LEAST(GREATEST(coalesce(p_limite, 20), 1), 50);
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.get_requerimiento_compra_pdf(p_id_requerimiento integer)
RETURNS TABLE(
    id_requerimiento integer,
    alta_fecha timestamp without time zone,
    alta_usr varchar,
    id_estado integer,
    estado_descripcion varchar,
    id_sector integer,
    sector_descripcion varchar,
    requiere_afiliado boolean,
    afiliado_cuil_titular varchar,
    afiliado_int integer,
    afiliado_nombre_apellido varchar,
    afiliado_documento varchar,
    afiliado_direccion varchar,
    afiliado_localidad varchar,
    afiliado_provincia varchar,
    afiliado_celular varchar,
    afiliado_telefono varchar,
    afiliado_email varchar,
    cargo_ospim integer,
    cargo_tercerizadora integer,
    id_tercerizadora varchar,
    recupero boolean,
    observaciones text,
    detalle_id integer,
    detalle_orden integer,
    id_articulo integer,
    articulo varchar,
    cantidad integer,
    precio_unitario_estimado numeric,
    precio_total_estimado numeric,
    prestador_razon_social varchar,
    prestador_cuit varchar,
    detalle_observaciones text,
    total_general numeric
) AS $$
BEGIN
    RETURN QUERY
    SELECT rb.id,
           rb.alta_fecha,
           rb.alta_usr,
           rb.id_estado,
           rb.estado_descripcion,
           rb.id_sector,
           rb.sector_descripcion,
           rb.requiere_afiliado,
           rb.afiliado_cuil_titular,
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
           row_number() OVER (ORDER BY d.id)::integer,
           d.id_articulo,
           d.articulo,
           d.cantidad,
           d.precio_unitario_estimado,
           d.precio_total_estimado,
           d.prestador_razon_social,
           d.prestador_cuit,
           d.observaciones,
           sum(coalesce(d.precio_total_estimado, 0)) OVER (PARTITION BY rb.id)
      FROM compras.requerimiento_base() rb
      LEFT JOIN compras.get_requerimiento_detalle(p_id_requerimiento) d
        ON d.id_requerimiento = rb.id
     WHERE rb.id = p_id_requerimiento
     ORDER BY d.id;
END;
$$ LANGUAGE plpgsql STABLE;
