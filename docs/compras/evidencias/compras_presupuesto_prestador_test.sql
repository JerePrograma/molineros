\set ON_ERROR_STOP on
\encoding LATIN1

-- Prueba transaccional del presupuesto activo por requerimiento y prestador.
-- Ejecutar solo sobre una base descartable que ya tenga compras_schema.sql:
-- psql -X -v ON_ERROR_STOP=1 -f compras_presupuesto_prestador_test.sql DB

BEGIN;

CREATE FUNCTION pg_temp.compras_assert(
    p_prueba TEXT,
    p_condicion BOOLEAN
)
RETURNS VOID
LANGUAGE plpgsql
AS $function$
BEGIN
    IF NOT COALESCE(p_condicion, FALSE) THEN
        RAISE EXCEPTION 'Fallo de contrato: %', p_prueba;
    END IF;
END;
$function$;

CREATE FUNCTION pg_temp.compras_expect_error(
    p_prueba TEXT,
    p_sql TEXT,
    p_fragmento TEXT
)
RETURNS VOID
LANGUAGE plpgsql
AS $function$
DECLARE
    v_error TEXT;
BEGIN
    BEGIN
        EXECUTE p_sql;
    EXCEPTION
        WHEN OTHERS THEN
            v_error := SQLERRM;
    END;

    IF v_error IS NULL THEN
        RAISE EXCEPTION
            'Fallo de contrato: % no produjo el error esperado',
            p_prueba;
    END IF;

    IF NULLIF(p_fragmento, '') IS NOT NULL
       AND position(p_fragmento IN v_error) = 0 THEN
        RAISE EXCEPTION
            'Fallo de contrato: % produjo [%], se esperaba [%]',
            p_prueba,
            v_error,
            p_fragmento;
    END IF;
END;
$function$;

INSERT INTO compras.requerimiento (
    estado,
    id_sector,
    alta_usr
)
VALUES (
    1,
    7,
    'test_presupuesto_activo'
)
RETURNING id_requerimiento AS req_id
\gset

INSERT INTO compras.requerimiento_detalle (
    id_requerimiento,
    tipo_item,
    cantidad,
    observaciones,
    alta_usr
)
VALUES (
    :req_id,
    'OBSERVACION',
    1,
    'Detalle contractual',
    'test_presupuesto_activo'
)
RETURNING id_detalle AS detalle_id
\gset

INSERT INTO compras.requerimiento_cotizacion_prestador (
    id_requerimiento,
    id_prestador,
    estado_envio,
    intentos,
    email_destino,
    fecha_envio,
    alta_usr
)
VALUES
    (
        :req_id,
        910001,
        'ENVIADO',
        1,
        'prestador1@example.invalid',
        clock_timestamp(),
        'test_presupuesto_activo'
    ),
    (
        :req_id,
        910002,
        'ENVIADO',
        1,
        'prestador2@example.invalid',
        clock_timestamp(),
        'test_presupuesto_activo'
    );

SELECT compras.cambiar_estado_requerimiento(
    :req_id,
    2,
    'test_presupuesto_activo'
);

SELECT compras.registrar_requerimiento_presupuesto(
    :req_id,
    910001,
    1,
    0,
    991001,
    'test-uuid-1',
    'presupuesto-1.pdf',
    'presupuesto-1.pdf',
    'Presupuesto 1',
    'Prestador contractual 1',
    'test_presupuesto_activo'
) AS presupuesto_id
\gset

SELECT pg_temp.compras_assert(
    'la primera alta crea una sola asociacion activa',
    (
        SELECT count(*) = 1
          FROM compras.requerimiento_presupuesto
         WHERE id_requerimiento = :req_id
           AND id_prestador = 910001
           AND baja_fecha IS NULL
    )
);

SELECT pg_temp.compras_assert(
    'la primera alta cambia ENVIADO a COTIZADO',
    (
        SELECT estado_envio = 'COTIZADO'
          FROM compras.requerimiento_cotizacion_prestador
         WHERE id_requerimiento = :req_id
           AND id_prestador = 910001
    )
);

SELECT pg_temp.compras_assert(
    'cargar un presupuesto no cambia el estado general',
    (
        SELECT estado = 2
          FROM compras.requerimiento
         WHERE id_requerimiento = :req_id
    )
);

SELECT pg_temp.compras_expect_error(
    'segunda alta por la funcion oficial',
    format(
        $sql$
        SELECT compras.registrar_requerimiento_presupuesto(
            %s, 910001, 1, 0, 991002, 'test-uuid-2',
            'presupuesto-2.pdf', 'presupuesto-2.pdf',
            'Presupuesto 2', 'Prestador contractual 1',
            'test_presupuesto_activo'
        )
        $sql$,
        :req_id
    ),
    'ENVIADO'
);

SELECT pg_temp.compras_expect_error(
    'el indice parcial rechaza una segunda asociacion activa',
    format(
        $sql$
        INSERT INTO compras.requerimiento_presupuesto (
            id_requerimiento, id_prestador, dl_group_id, dl_folder_id,
            dl_file_entry_id, dl_file_uuid, nombre_original,
            nombre_persistido, titulo, descripcion_prestador, alta_usr
        ) VALUES (
            %s, 910001, 1, 0, 991003, 'test-uuid-3',
            'presupuesto-3.pdf', 'presupuesto-3.pdf',
            'Presupuesto 3', 'Prestador contractual 1',
            'test_presupuesto_activo'
        )
        $sql$,
        :req_id
    ),
    'ux_compras_presupuesto_requerimiento_prestador_activo'
);

CREATE FUNCTION pg_temp.compras_fallar_transicion_cotizado()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $function$
BEGIN
    IF NEW.id_prestador = 910002
       AND OLD.estado_envio = 'ENVIADO'
       AND NEW.estado_envio = 'COTIZADO' THEN
        RAISE EXCEPTION 'Falla simulada al persistir COTIZADO';
    END IF;

    RETURN NEW;
END;
$function$;

CREATE TRIGGER trg_compras_test_fallar_cotizado
    BEFORE UPDATE
    ON compras.requerimiento_cotizacion_prestador
    FOR EACH ROW
    EXECUTE PROCEDURE pg_temp.compras_fallar_transicion_cotizado();

SELECT pg_temp.compras_expect_error(
    'un fallo al persistir COTIZADO revierte toda el alta',
    format(
        $sql$
        SELECT compras.registrar_requerimiento_presupuesto(
            %s, 910002, 1, 0, 991005, 'test-uuid-4',
            'presupuesto-4.pdf', 'presupuesto-4.pdf',
            'Presupuesto 4', 'Prestador contractual 2',
            'test_presupuesto_activo'
        )
        $sql$,
        :req_id
    ),
    'Falla simulada al persistir COTIZADO'
);

DROP TRIGGER trg_compras_test_fallar_cotizado
    ON compras.requerimiento_cotizacion_prestador;

SELECT pg_temp.compras_assert(
    'el alta fallida no deja una asociacion activa',
    NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto
         WHERE id_requerimiento = :req_id
           AND id_prestador = 910002
           AND baja_fecha IS NULL
    )
);

SELECT pg_temp.compras_assert(
    'el alta fallida conserva ENVIADO',
    (
        SELECT estado_envio = 'ENVIADO'
          FROM compras.requerimiento_cotizacion_prestador
         WHERE id_requerimiento = :req_id
           AND id_prestador = 910002
    )
);

SELECT pg_temp.compras_assert(
    'la baja logica se completa',
    compras.baja_requerimiento_presupuesto(
        :presupuesto_id,
        :req_id,
        'test_presupuesto_activo'
    )
);

SELECT pg_temp.compras_assert(
    'la baja deja el presupuesto inactivo',
    (
        SELECT baja_fecha IS NOT NULL
          FROM compras.requerimiento_presupuesto
         WHERE id_requerimiento_presupuesto = :presupuesto_id
    )
);

SELECT pg_temp.compras_assert(
    'la baja restaura COTIZADO a ENVIADO',
    (
        SELECT estado_envio = 'ENVIADO'
          FROM compras.requerimiento_cotizacion_prestador
         WHERE id_requerimiento = :req_id
           AND id_prestador = 910001
    )
);

SELECT pg_temp.compras_assert(
    'la reactivacion se completa',
    compras.reactivar_requerimiento_presupuesto(
        :presupuesto_id,
        :req_id
    )
);

SELECT pg_temp.compras_assert(
    'la reactivacion restaura COTIZADO',
    (
        SELECT estado_envio = 'COTIZADO'
          FROM compras.requerimiento_cotizacion_prestador
         WHERE id_requerimiento = :req_id
           AND id_prestador = 910001
    )
);

INSERT INTO compras.requerimiento_presupuesto (
    id_requerimiento,
    id_prestador,
    dl_group_id,
    dl_folder_id,
    dl_file_entry_id,
    dl_file_uuid,
    nombre_original,
    nombre_persistido,
    titulo,
    descripcion_prestador,
    alta_usr,
    baja_fecha,
    baja_usr
)
VALUES (
    :req_id,
    910001,
    1,
    0,
    991004,
    'test-uuid-5',
    'presupuesto-inactivo.pdf',
    'presupuesto-inactivo.pdf',
    'Presupuesto inactivo',
    'Prestador contractual 1',
    'test_presupuesto_activo',
    clock_timestamp(),
    'test_presupuesto_activo'
)
RETURNING id_requerimiento_presupuesto AS inactivo_id
\gset

SELECT pg_temp.compras_expect_error(
    'no se reactiva un historico si ya existe otro activo',
    format(
        'SELECT compras.reactivar_requerimiento_presupuesto(%s, %s)',
        :inactivo_id,
        :req_id
    ),
    'otro presupuesto activo'
);

SELECT pg_temp.compras_assert(
    'la reactivacion rechazada conserva el historico inactivo',
    (
        SELECT baja_fecha IS NOT NULL
          FROM compras.requerimiento_presupuesto
         WHERE id_requerimiento_presupuesto = :inactivo_id
    )
);

SELECT pg_temp.compras_assert(
    'el guardado acepta al prestador COTIZADO con presupuesto activo',
    compras.guardar_cotizacion_requerimiento(
        :req_id,
        ARRAY[:detalle_id],
        ARRAY[100.00::NUMERIC],
        910001,
        'test_presupuesto_activo'
    ) = 3
);

SELECT pg_temp.compras_assert(
    'el requerimiento general queda COTIZADO solo al cerrar la cotizacion',
    (
        SELECT estado = 3
          FROM compras.requerimiento
         WHERE id_requerimiento = :req_id
    )
);

SELECT pg_temp.compras_expect_error(
    'no se elimina un presupuesto luego del cierre general',
    format(
        $sql$
        SELECT compras.baja_requerimiento_presupuesto(
            %s, %s, 'test_presupuesto_activo'
        )
        $sql$,
        :presupuesto_id,
        :req_id
    ),
    'A COTIZAR'
);

ROLLBACK;

\echo 'CONTRATO_COMPRAS_PRESUPUESTO_POSTGRESQL_OK'
