-- Cierre QA Compras - contrato afiliado_id_ospim / PDF.
-- PostgreSQL 9.6+.
-- Migracion incremental no destructiva: valida contratos y completa snapshots
-- solo cuando public.afiliado tiene un unico id_ospim para la combinacion.

BEGIN;

DO $validacion_estructura$
DECLARE
    v_afiliado_id_ospim_tipo REGTYPE;
    v_requerimiento_id_ospim_tipo REGTYPE;
    v_trigger_count INTEGER;
BEGIN
    IF to_regclass('compras.requerimiento') IS NULL THEN
        RAISE EXCEPTION
            'Contrato incompatible: falta compras.requerimiento.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'compras'
           AND table_name = 'requerimiento'
           AND column_name = 'afiliado_id_ospim'
    ) THEN
        RAISE EXCEPTION
            'Contrato incompatible: falta compras.requerimiento.afiliado_id_ospim.';
    END IF;

    IF to_regclass('public.afiliado') IS NULL THEN
        RAISE EXCEPTION
            'Contrato incompatible: falta public.afiliado para backfill seguro.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'public'
           AND table_name = 'afiliado'
           AND column_name = 'cuil_titular'
    ) OR NOT EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'public'
           AND table_name = 'afiliado'
           AND column_name = 'inte'
    ) OR NOT EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'public'
           AND table_name = 'afiliado'
           AND column_name = 'id_ospim'
    ) THEN
        RAISE EXCEPTION
            'Contrato incompatible: public.afiliado debe exponer cuil_titular, inte e id_ospim.';
    END IF;

    SELECT a.atttypid
      INTO v_requerimiento_id_ospim_tipo
      FROM pg_attribute a
     WHERE a.attrelid = 'compras.requerimiento'::REGCLASS
       AND a.attname = 'afiliado_id_ospim'
       AND a.attisdropped = FALSE;

    SELECT a.atttypid
      INTO v_afiliado_id_ospim_tipo
      FROM pg_attribute a
     WHERE a.attrelid = 'public.afiliado'::REGCLASS
       AND a.attname = 'id_ospim'
       AND a.attisdropped = FALSE;

    IF v_requerimiento_id_ospim_tipo <> 'pg_catalog.int4'::REGTYPE
       OR v_afiliado_id_ospim_tipo <> 'pg_catalog.int4'::REGTYPE THEN
        RAISE EXCEPTION
            'Contrato incompatible: tipos id_ospim esperados INTEGER. compras.requerimiento.afiliado_id_ospim=%, public.afiliado.id_ospim=%.',
            v_requerimiento_id_ospim_tipo,
            v_afiliado_id_ospim_tipo;
    END IF;

    SELECT count(*)
      INTO v_trigger_count
      FROM pg_trigger t
      JOIN pg_class c
        ON c.oid = t.tgrelid
      JOIN pg_namespace n
        ON n.oid = c.relnamespace
     WHERE n.nspname = 'compras'
       AND c.relname = 'requerimiento'
       AND t.tgname = 'trg_compras_requerimiento_validar';

    IF v_trigger_count <> 1 THEN
        RAISE EXCEPTION
            'Contrato incompatible: se esperaba exactamente un trigger trg_compras_requerimiento_validar en compras.requerimiento. Total=%.',
            v_trigger_count;
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM pg_trigger t
          JOIN pg_class c
            ON c.oid = t.tgrelid
          JOIN pg_namespace n
            ON n.oid = c.relnamespace
         WHERE n.nspname = 'compras'
           AND c.relname = 'requerimiento'
           AND t.tgname = 'trg_compras_requerimiento_validar'
           AND t.tgisinternal = FALSE
           AND t.tgenabled = 'O'
           AND t.tgfoid =
               to_regprocedure(
                   'compras.validar_requerimiento_fila()'
               )::OID
    ) THEN
        RAISE EXCEPTION
            'Contrato incompatible: trg_compras_requerimiento_validar no esta habilitado normalmente o no ejecuta compras.validar_requerimiento_fila().';
    END IF;
END;
$validacion_estructura$;

LOCK TABLE compras.requerimiento IN ACCESS EXCLUSIVE MODE;

ALTER TABLE compras.requerimiento
    DISABLE TRIGGER trg_compras_requerimiento_validar;

WITH afiliado_unico AS (
    SELECT
        a.cuil_titular,
        a.inte,
        min(a.id_ospim) AS id_ospim
      FROM public.afiliado a
     WHERE a.cuil_titular IS NOT NULL
       AND a.inte IS NOT NULL
       AND a.id_ospim IS NOT NULL
     GROUP BY
        a.cuil_titular,
        a.inte
    HAVING count(DISTINCT a.id_ospim) = 1
)
UPDATE compras.requerimiento r
   SET afiliado_id_ospim = a.id_ospim
  FROM afiliado_unico a
 WHERE r.afiliado_id_ospim IS NULL
   AND r.afiliado_cuil_titular = a.cuil_titular
   AND r.afiliado_int = a.inte;

ALTER TABLE compras.requerimiento
    ENABLE TRIGGER trg_compras_requerimiento_validar;

DO $validacion_backfill$
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM pg_trigger t
          JOIN pg_class c
            ON c.oid = t.tgrelid
          JOIN pg_namespace n
            ON n.oid = c.relnamespace
         WHERE n.nspname = 'compras'
           AND c.relname = 'requerimiento'
           AND t.tgname = 'trg_compras_requerimiento_validar'
           AND t.tgisinternal = FALSE
           AND t.tgenabled = 'O'
           AND t.tgfoid =
               to_regprocedure(
                   'compras.validar_requerimiento_fila()'
               )::OID
    ) THEN
        RAISE EXCEPTION
            'Contrato incompatible: trg_compras_requerimiento_validar no quedo habilitado normalmente.';
    END IF;

    IF EXISTS (
        WITH afiliado_unico AS (
            SELECT
                a.cuil_titular,
                a.inte,
                min(a.id_ospim) AS id_ospim
              FROM public.afiliado a
             WHERE a.cuil_titular IS NOT NULL
               AND a.inte IS NOT NULL
               AND a.id_ospim IS NOT NULL
             GROUP BY
                a.cuil_titular,
                a.inte
            HAVING count(DISTINCT a.id_ospim) = 1
        )
        SELECT 1
          FROM compras.requerimiento r
          JOIN afiliado_unico a
            ON a.cuil_titular = r.afiliado_cuil_titular
           AND a.inte = r.afiliado_int
         WHERE r.afiliado_id_ospim IS NULL
         LIMIT 1
    ) THEN
        RAISE EXCEPTION
            'Backfill incompleto: quedan requerimientos elegibles con afiliado_id_ospim NULL.';
    END IF;
END;
$validacion_backfill$;

DO $validacion_contratos$
DECLARE
    v_guardar OID;
    v_pdf OID;
BEGIN
    v_guardar := to_regprocedure(
        'compras.guardar_requerimiento(integer, character varying, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, integer, character varying, boolean, text, character varying)'
    )::OID;

    IF v_guardar IS NULL THEN
        RAISE EXCEPTION
            'Contrato incompatible: falta compras.guardar_requerimiento con 21 argumentos canonicos.';
    END IF;

    IF (
        SELECT p.pronargs
          FROM pg_proc p
         WHERE p.oid = v_guardar
    ) <> 21 THEN
        RAISE EXCEPTION
            'Contrato incompatible: compras.guardar_requerimiento no tiene 21 argumentos.';
    END IF;

    v_pdf := to_regprocedure(
        'compras.get_requerimiento_compra_pdf(integer)'
    )::OID;

    IF v_pdf IS NULL THEN
        RAISE EXCEPTION
            'Contrato incompatible: falta compras.get_requerimiento_compra_pdf(integer).';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM pg_proc p
         WHERE p.oid = v_pdf
           AND array_position(p.proargnames, 'afiliado_documento') IS NOT NULL
           AND array_position(p.proargnames, 'afiliado_id_ospim') IS NOT NULL
    ) THEN
        RAISE EXCEPTION
            'Contrato incompatible: get_requerimiento_compra_pdf debe exponer afiliado_documento y afiliado_id_ospim.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM pg_proc p
         WHERE p.oid = v_pdf
           AND array_position(p.proargnames, 'total_general') IS NOT NULL
    ) THEN
        RAISE EXCEPTION
            'Contrato incompatible: get_requerimiento_compra_pdf no debe exponer total_general.';
    END IF;
END;
$validacion_contratos$;

COMMIT;
