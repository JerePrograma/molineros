\set ON_ERROR_STOP on
\encoding LATIN1
-- Solo sobre una base vacia compras_orden_test_*. El fixture aplica ese guard.
\ir orden_compra_test.sql
CREATE TEMP TABLE snapshots_oc_antes_upgrade AS
    SELECT id_orden_compra, to_jsonb(o) AS fila
      FROM compras.requerimiento_orden_compra o;
\ir ../../docs/sql/compras/20261026_orden_compra_edicion_condiciones.sql
\ir ../../docs/sql/compras/20261026_orden_compra_edicion_condiciones.sql
DO $test$
BEGIN
    ASSERT NOT EXISTS (
        SELECT 1 FROM compras.requerimiento_orden_compra o
        JOIN snapshots_oc_antes_upgrade s USING (id_orden_compra)
        WHERE (to_jsonb(o) - 'modi_fecha' - 'modi_usr' - 'version_edicion') <> s.fila
           OR o.modi_fecha IS NOT NULL OR o.modi_usr IS NOT NULL OR o.version_edicion <> 0
    ), 'El upgrade debe conservar todos los snapshots y la auditoria de alta.';
    ASSERT (SELECT count(*) FROM information_schema.columns
        WHERE table_schema = 'compras' AND table_name = 'requerimiento_orden_compra'
        AND column_name IN ('modi_fecha', 'modi_usr', 'version_edicion')) = 3;
END;
$test$;
SELECT 'orden_compra_edicion_upgrade: OK' AS resultado;
