-- Incorporar observacion_interna sin reemplazar el guardado vigente.
-- Ejecutar antes de instalar el codigo que utiliza la variante de 24 argumentos.
BEGIN;

DO $block$
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'compras'
           AND table_name = 'requerimiento'
           AND column_name = 'observacion_interna'
    ) THEN
        ALTER TABLE compras.requerimiento
            ADD COLUMN observacion_interna TEXT;
    END IF;
END;
$block$;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento(
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
    p_usuario VARCHAR,
    p_observacion_interna TEXT
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
BEGIN
    v_id := compras.guardar_requerimiento(
        p_id, p_afiliado_cuil_titular, p_afiliado_int,
        p_afiliado_id_ospim, p_afiliado_nombre, p_afiliado_apellido,
        p_afiliado_documento_tipo, p_afiliado_documento_nro,
        p_afiliado_direccion, p_afiliado_localidad, p_afiliado_provincia,
        p_afiliado_celular, p_afiliado_telefono, p_afiliado_email,
        p_id_sector, p_cargo_ospim, p_cargo_tercerizadora,
        p_id_tercerizadora, p_recupero, p_surge, p_legales,
        p_observaciones, p_usuario
    );

    UPDATE compras.requerimiento
       SET observacion_interna = NULLIF(btrim(p_observacion_interna), '')
     WHERE id_requerimiento = v_id
       AND estado = 1
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'La observacion interna solo puede modificarse en estado PENDIENTE.';
    END IF;

    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;

COMMIT;
