-- Reparación no destructiva de acentuación para una base Compras existente.
-- La versión entregable se codifica en ISO-8859-1 y debe ejecutarse con psql:
-- psql -X -v ON_ERROR_STOP=1 -f 20260824_reparar_acentuacion_compras_sin_titulo_obligatorio.sql

BEGIN;

LOCK TABLE compras.requerimiento_presupuesto IN SHARE ROW EXCLUSIVE MODE;

-- Una Orden médica ya no requiere un título determinado. Se conserva únicamente
-- la regla propia del tipo documental: no corresponde descripción de prestador.
ALTER TABLE compras.requerimiento_presupuesto
DROP CONSTRAINT IF EXISTS ck_compras_orden_medica_datos;

ALTER TABLE compras.requerimiento_presupuesto
    ADD CONSTRAINT ck_compras_orden_medica_datos
        CHECK (
            tipo_documento <> 2
                OR descripcion_prestador IS NULL
            );

UPDATE compras.sector_requerimiento
SET descripcion = 'Prestaciones Médicas',
    modi_fecha = now(),
    modi_usr = 'reparacion-acentuacion-iso-8859-1'
WHERE id_sector = 2
  AND descripcion IS DISTINCT FROM 'Prestaciones Médicas';

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
            'Debe informar la fecha de la Orden médica.';
END IF;

    IF p_dl_group_id IS NULL
       OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL
       OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL
       OR p_dl_file_entry_id <= 0
       OR NULLIF(btrim(p_dl_file_uuid), '') IS NULL THEN

        RAISE EXCEPTION
            'La identidad del documento de Orden médica no es válida.';
END IF;

    -- Estos nombres son metadatos técnicos obtenidos de la carga; no se exige
    -- ningún texto o formato particular al usuario.
    IF NULLIF(btrim(p_nombre_original), '') IS NULL
       OR NULLIF(btrim(p_nombre_persistido), '') IS NULL THEN

        RAISE EXCEPTION
            'No se recibiá correctamente la información técnica del archivo.';
END IF;

    -- p_titulo es opcional y acepta cualquier texto. Se conserva en la firma
    -- para no romper las llamadas existentes desde la aplicación. Si llega
    -- vacío, el INSERT utiliza autométicamente el nombre original del archivo.

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
            'La Orden médica solo puede registrarse durante '
            'el alta de un requerimiento PENDIENTE.';
END IF;

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
                'La Orden médica ya fue cargada con fecha % y número de receta % en el requerimiento %.',
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

COMMIT;
