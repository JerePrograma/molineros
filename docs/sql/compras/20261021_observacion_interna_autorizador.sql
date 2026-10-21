-- Compras: guardado exclusivo de observacion interna en requerimientos activos.
-- Estados habilitados 1 a 7; no modifica estructura, detalles ni estado.
-- El permiso AUTORIZAR_COMPRAS se valida en la Action; la funcion controla estado y baja.
-- Requiere la columna observacion_interna (migracion 20261008).
-- Instalar antes del Java/JSP de esta entrega. No aplicada a QA por Codex.

BEGIN;

CREATE OR REPLACE FUNCTION compras.guardar_observacion_interna_requerimiento(
    p_id_requerimiento INTEGER,
    p_observacion_interna TEXT,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra que desea editar.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);
    UPDATE compras.requerimiento
       SET observacion_interna = NULLIF(btrim(p_observacion_interna), ''),
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado IN (1, 2, 3, 4, 5, 6, 7)
       AND baja_fecha IS NULL
     RETURNING id_requerimiento INTO v_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'La observacion interna no puede modificarse en el estado actual. Actualice la pantalla.';
    END IF;
    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;

COMMIT;
