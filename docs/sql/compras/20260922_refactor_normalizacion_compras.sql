-- RETIRADO. No ejecutar el incremental monolitico del 2026-09-22.
-- Reemplazado por:
--   20260923_refactor_compras_fase1_expand.sql (datos y columnas conservados)
--   20260923_refactor_compras_fase2_cleanup.sql (NO ejecutar hasta validar QA)
-- Este archivo nunca redirige ni aplica implicitamente ninguna fase.
DO $retirado$
BEGIN
    RAISE EXCEPTION 'Incremental retirado: utilizar FASE 1; FASE 2 requiere validacion posterior';
END $retirado$;
