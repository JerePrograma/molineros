-- QA12 PostgreSQL 9.6; ISO-8859-1 sin BOM.
-- Referencia: 20261026_normalizar_orden_compra.sql:78-86; misma guarda/bloqueo.
-- Verificacion de conexion: Liquidaciones/EditarLiquidacionServiceImpl:551-605.
-- Evidencia: checks_version_OC_metadata_sin_PII_20261102.json SHA8d4cd37075f5.
-- Solo cambia el CHECK secundario; no DML, funciones, triggers, indices ni CASCADE.
BEGIN;
SET LOCAL lock_timeout='2s';
SET LOCAL statement_timeout='15s';
SET LOCAL search_path=pg_catalog,public,compras;
DO $check_version_oc$
DECLARE
 v_rel OID;
 v_att SMALLINT;
 v_type OID;
 v_notnull BOOLEAN;
 v_keeper RECORD;
 v_copy RECORD;
 v_def TEXT;
BEGIN
 IF host(inet_server_addr()) IS DISTINCT FROM '12.1.1.12'
    OR inet_server_port() IS DISTINCT FROM 5432
    OR current_database() IS DISTINCT FROM 'devmolineros' THEN
  RAISE EXCEPTION 'CHECK_OC_QA12_DATABASE_GUARD';
 END IF;
 IF current_setting('server_version_num')::INTEGER NOT BETWEEN 90600 AND 99999 THEN
  RAISE EXCEPTION 'CHECK_OC_POSTGRESQL96_GUARD';
 END IF;
 v_rel:=to_regclass('compras.requerimiento_orden_compra');
 IF v_rel IS NULL THEN RAISE EXCEPTION 'CHECK_OC_TABLE_MISSING'; END IF;
 LOCK TABLE ONLY compras.requerimiento_orden_compra IN ACCESS EXCLUSIVE MODE;
 IF to_regclass('compras.requerimiento_orden_compra')::OID IS DISTINCT FROM v_rel
    OR NOT EXISTS(SELECT 1 FROM pg_class WHERE oid=v_rel AND relkind='r') THEN
  RAISE EXCEPTION 'CHECK_OC_RELATION_IDENTITY_DRIFT_AFTER_LOCK';
 END IF;
 IF EXISTS(SELECT 1 FROM pg_inherits WHERE inhparent=v_rel OR inhrelid=v_rel) THEN
  RAISE EXCEPTION 'CHECK_OC_INHERITANCE_UNSUPPORTED';
 END IF;
 SELECT attnum,atttypid,attnotnull INTO v_att,v_type,v_notnull
 FROM pg_attribute WHERE attrelid=v_rel AND attname='version_edicion'
 AND attnum>0 AND NOT attisdropped;
 IF v_att IS NULL OR v_type IS DISTINCT FROM 'integer'::regtype::OID
    OR v_notnull IS DISTINCT FROM TRUE THEN
  RAISE EXCEPTION 'CHECK_OC_COLUMN_DRIFT';
 END IF;
 SELECT c.oid,c.* INTO v_keeper FROM pg_constraint c WHERE c.conrelid=v_rel
 AND conname='requerimiento_orden_compra_version_edicion_check';
 IF v_keeper.oid IS NULL OR v_keeper.contype<>'c'
    OR v_keeper.conkey IS DISTINCT FROM ARRAY[v_att]::SMALLINT[]
    OR NOT v_keeper.convalidated OR v_keeper.connoinherit
    OR NOT v_keeper.conislocal OR v_keeper.coninhcount<>0
    OR v_keeper.condeferrable OR v_keeper.condeferred THEN
  RAISE EXCEPTION 'CHECK_OC_KEEPER_FLAGS_DRIFT';
 END IF;
 v_def:=pg_get_constraintdef(v_keeper.oid,TRUE);
 IF v_def IS DISTINCT FROM 'CHECK (version_edicion >= 0)'
    OR v_keeper.consrc IS DISTINCT FROM '(version_edicion >= 0)'
    OR obj_description(v_keeper.oid,'pg_constraint') IS NOT NULL THEN
  RAISE EXCEPTION 'CHECK_OC_KEEPER_DEFINITION_DRIFT';
 END IF;
 SELECT c.oid,c.* INTO v_copy FROM pg_constraint c WHERE c.conrelid=v_rel
 AND conname='requerimiento_orden_compra_version_edicion_check1';
 IF v_copy.oid IS NOT NULL THEN
  IF v_copy.contype IS DISTINCT FROM v_keeper.contype
     OR v_copy.conkey IS DISTINCT FROM v_keeper.conkey
     OR v_copy.convalidated IS DISTINCT FROM v_keeper.convalidated
     OR v_copy.connoinherit IS DISTINCT FROM v_keeper.connoinherit
     OR v_copy.conislocal IS DISTINCT FROM v_keeper.conislocal
     OR v_copy.coninhcount IS DISTINCT FROM v_keeper.coninhcount
     OR v_copy.condeferrable IS DISTINCT FROM v_keeper.condeferrable
     OR v_copy.condeferred IS DISTINCT FROM v_keeper.condeferred
     OR v_copy.consrc IS DISTINCT FROM v_keeper.consrc
     OR pg_get_constraintdef(v_copy.oid,TRUE) IS DISTINCT FROM v_def
     OR regexp_replace(v_copy.conbin::TEXT,':location -?[0-9]+',':location #','g')
       IS DISTINCT FROM regexp_replace(v_keeper.conbin::TEXT,':location -?[0-9]+',':location #','g')
     OR obj_description(v_copy.oid,'pg_constraint') IS NOT NULL THEN
   RAISE EXCEPTION 'CHECK_OC_COPY_DEFINITION_OR_FLAGS_DRIFT';
  END IF;
  IF EXISTS(SELECT 1 FROM pg_depend WHERE refclassid='pg_constraint'::regclass
            AND refobjid=v_copy.oid) THEN
   RAISE EXCEPTION 'CHECK_OC_COPY_HAS_DEPENDENTS';
  END IF;
 END IF;
 IF v_copy.oid IS NULL THEN
  ALTER TABLE ONLY compras.requerimiento_orden_compra
   ADD CONSTRAINT requerimiento_orden_compra_version_edicion_check1
   CHECK (version_edicion >= 0);
 END IF;
 IF NOT EXISTS(SELECT 1 FROM pg_constraint WHERE oid=v_keeper.oid)
    OR NOT EXISTS(SELECT 1 FROM pg_constraint WHERE conrelid=v_rel
      AND conname='requerimiento_orden_compra_version_edicion_check1'
      AND convalidated AND contype='c'
      AND pg_get_constraintdef(oid,TRUE)=v_def) THEN
  RAISE EXCEPTION 'CHECK_OC_REVERSE_POSTCONDITION_FAILED';
 END IF;
END;
$check_version_oc$;
COMMIT;
