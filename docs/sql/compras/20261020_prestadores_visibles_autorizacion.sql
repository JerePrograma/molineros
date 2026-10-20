-- Compras: conservar los prestadores notificados en PARA AUTORIZAR y AUTORIZADO.
-- Referencias: listar_prestadores_enviados vigente y migracion de autorizacion
-- 20261013_autorizacion_requerimiento.sql (parche guardado de funcion instalada).
-- No modifica datos, roles ni transiciones. Ejecutar antes del smoke test.
-- Conserva el cuerpo instalado y cambia unicamente el predicado conocido.

BEGIN;

DO $prestadores_visibles$
DECLARE
    v_funcion REGPROCEDURE;
    v_definicion TEXT;
    v_normalizada TEXT;
    v_actualizada TEXT;
BEGIN
    v_funcion := to_regprocedure(
        'compras.listar_prestadores_enviados(integer,integer)'
    );

    IF v_funcion IS NULL THEN
        RAISE EXCEPTION
            'No existe compras.listar_prestadores_enviados(integer,integer).';
    END IF;

    v_definicion := pg_get_functiondef(v_funcion);
    v_normalizada := regexp_replace(
        lower(v_definicion), '[[:space:]]', '', 'g'
    );

    IF position('r.estadoin(2,3,4,5,6,7,99)' IN v_normalizada) > 0 THEN
        RAISE NOTICE
            'listar_prestadores_enviados ya contempla los estados 6 y 7.';
        RETURN;
    END IF;

    IF position('r.estadoin(2,3,4,5,99)' IN v_normalizada) = 0 THEN
        RAISE EXCEPTION
            'Predicado de estados desconocido en listar_prestadores_enviados. Revisar la funcion instalada sin reemplazar reglas ajenas.';
    END IF;

    v_actualizada := regexp_replace(
        v_definicion,
        'r[.]estado[[:space:]]+IN[[:space:]]*[(][[:space:]]*2[[:space:]]*,[[:space:]]*3[[:space:]]*,[[:space:]]*4[[:space:]]*,[[:space:]]*5[[:space:]]*,[[:space:]]*99[[:space:]]*[)]',
        'r.estado IN (2, 3, 4, 5, 6, 7, 99)',
        'i'
    );

    IF v_actualizada = v_definicion THEN
        RAISE EXCEPTION
            'No se pudo ampliar el predicado de listar_prestadores_enviados.';
    END IF;

    EXECUTE v_actualizada;
END;
$prestadores_visibles$;

COMMIT;