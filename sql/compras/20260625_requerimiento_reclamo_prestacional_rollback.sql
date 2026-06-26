BEGIN;

DO $rollback$
BEGIN
    IF to_regclass(
        'compras.requerimiento_reclamo_prestacional'
    ) IS NOT NULL
       AND EXISTS (
           SELECT 1
             FROM compras.requerimiento_reclamo_prestacional
       ) THEN

        RAISE EXCEPTION
            'Rollback cancelado: existen vinculaciones de Reclamo Prestacional. Exporte y reconcilie los datos antes de eliminar la tabla.';
    END IF;
END;
$rollback$;

DROP FUNCTION IF EXISTS
    compras.marcar_error_reclamo_prestacional(
        INTEGER,
        VARCHAR,
        INTEGER,
        TEXT,
        VARCHAR
    );

DROP FUNCTION IF EXISTS
    compras.liberar_reserva_reclamo_prestacional(
        INTEGER,
        VARCHAR,
        VARCHAR
    );

DROP FUNCTION IF EXISTS
    compras.finalizar_reclamo_prestacional(
        INTEGER,
        VARCHAR,
        INTEGER,
        VARCHAR
    );

DROP FUNCTION IF EXISTS
    compras.reservar_reclamo_prestacional(
        INTEGER,
        VARCHAR,
        VARCHAR
    );

DROP FUNCTION IF EXISTS
    compras.get_requerimiento_reclamo_prestacional(
        INTEGER
    );

DROP TABLE IF EXISTS
    compras.requerimiento_reclamo_prestacional;

COMMIT;
