-- MOLINEROS / COMPRAS - Efectos SQL, corte Java del 30/10/2026.
-- ISO-8859-1 sin BOM. Compatible PostgreSQL 9.6.
-- Fuente: catalogo REAL de devmolineros relevado el 30/10/2026.
-- No contiene credenciales, correos ni datos de afiliados.
-- Este paquete NO autoriza operar produccion: entrega para proxima migracion.
-- No ejecutado contra DB por el autor de este archivo.

-- Verificacion de SOLO LECTURA. Falla ante deriva, sin simplificar objetos.
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ READ ONLY;
SET LOCAL statement_timeout = '15s';
SET LOCAL lock_timeout = '2s';
DO $verificar_efectos$
DECLARE
    esperado RECORD;
    id_funcion OID;
    hash_actual TEXT;
BEGIN
    FOR esperado IN
        SELECT * FROM (VALUES
            ('compras.agregar_gastos_envio_requerimiento()', '1001331fac5332e2429526992765c13e'),
            ('compras.baja_vinculo_reclamo_prestacional(integer,character varying)', 'a05aa540deefc8d2a5a93f042b2ca102'),
            ('compras.baja_vinculo_reclamo_prestacional_trigger()', 'a33c36ade63ab98bceba282309bf283f'),
            ('compras.calcular_total_detalle_fila()', '757c0cf62a2b28b0782fdfad56e49d1a'),
            ('compras.completar_baja_requerimiento_fila()', 'f9c7d6751c6580ffce70bbb448a15def'),
            ('compras.validar_codigo_prestacion_detalle_fila()', 'f585417a0d6b9381b9d57a1b2ea78a43')
        ) firmas(firma, hash_md5)
    LOOP
        id_funcion := to_regprocedure(esperado.firma);
        IF id_funcion IS NULL THEN
            RAISE EXCEPTION 'Falta la funcion instalada esperada: %', esperado.firma;
        END IF;
        IF md5(pg_get_functiondef(id_funcion)) IS DISTINCT FROM esperado.hash_md5 THEN
            RAISE EXCEPTION 'Deriva de definicion instalada: %', esperado.firma;
        END IF;
    END LOOP;
    FOR esperado IN
        SELECT * FROM (VALUES
            ('autorizaciones', 'reclamos_prestacionales', 'trg_compras_baja_vinculo_reclamo', '8affbca47fd3deac1ba245996fa9e4ac'),
            ('compras', 'requerimiento', 'trg_compras_requerimiento_completar_baja', 'd08e74884d3e9f56c5d1c95e2e8ccdf6'),
            ('compras', 'requerimiento', 'trg_compras_requerimiento_gastos_envio', 'b788c73806a48ead0a4d732108b6fc69'),
            ('compras', 'requerimiento_detalle', 'tr_compras_detalle_codigo_prestacion', '2439e7d743aeff6522b705306e46210d'),
            ('compras', 'requerimiento_detalle', 'trg_compras_detalle_calcular_total', '71a3dd1695448c2fe75421aeec6e06b4')
        ) disparadores(esquema, tabla, nombre, hash_md5)
    LOOP
        SELECT md5(pg_get_triggerdef(t.oid, true)) INTO hash_actual
        FROM pg_trigger t
        JOIN pg_class c ON c.oid = t.tgrelid
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = esperado.esquema AND c.relname = esperado.tabla
          AND t.tgname = esperado.nombre AND NOT t.tgisinternal
          AND t.tgenabled = 'O';
        IF hash_actual IS DISTINCT FROM esperado.hash_md5 THEN
            RAISE EXCEPTION 'Trigger ausente, deshabilitado o distinto: %.%.%',
                    esperado.esquema, esperado.tabla, esperado.nombre;
        END IF;
    END LOOP;
END;
$verificar_efectos$;
SELECT 'VERIFICADOS: seis cuerpos y cinco triggers originales; no se retiro ningun efecto.' AS resultado;
ROLLBACK;
