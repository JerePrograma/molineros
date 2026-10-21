-- Compras y Prestadores: quitar edicion de pagina a roles funcionales.
-- Diagnostico 2026-10-21: lportal; grupo 10136; Layout 4075501 y 4076901.
-- Referencia funcional: Liquidaciones (Layout 11229), VIEW por roles de negocio
-- y UPDATE sin asignaciones. Referencia de guardas/transaccion:
-- 20261015_autorizacion_orden_compra_empresas.sql.
-- PostgreSQL 9.6 / Liferay 5.2 / ISO-8859-1 sin BOM.
-- Solo preparado. NO aplicado a una base real por Codex.
-- No ejecutar dentro del megascript de funciones de Compras.
-- Ejecutar completo en una sesion dedicada, sin transaccion previa abierta.
-- ROLLBACK es la salida predeterminada. Para persistir, revisar el resultado
-- y cambiar SOLO el ROLLBACK final por COMMIT, con autorizacion expresa.
-- Ante cualquier error ejecutar ROLLBACK; no continuar en estado 25P02.
-- Tras COMMIT invalidar la cache de permisos desde administracion de Liferay
-- o coordinar un reinicio del portal; recargar no garantiza invalidarla.
-- La administracion general con MANAGE_LAYOUTS conserva su comportamiento.

BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '30s';

CREATE TEMP TABLE correccion_update_paginas (
    plid BIGINT NOT NULL,
    layoutid BIGINT NOT NULL,
    friendlyurl VARCHAR(75) NOT NULL,
    permissionid BIGINT NOT NULL,
    viewpermissionid BIGINT NOT NULL,
    roleid BIGINT NOT NULL,
    rolename VARCHAR(75) NOT NULL,
    PRIMARY KEY (roleid, permissionid)
) ON COMMIT DROP;

INSERT INTO correccion_update_paginas VALUES
    (4075501, 33, '/prestadores', 4807002, 4807003, 4076813, 'ABM_Compras'),
    (4075501, 33, '/prestadores', 4807002, 4807003, 2875382, 'ABM_PRESTADOR'),
    (4076901, 34, '/compras', 4808202, 4808203, 4076813, 'ABM_Compras'),
    (4076901, 34, '/compras', 4808202, 4808203, 11375, 'ABM_Liquidaciones');

DO $correccion$
DECLARE
    v_validados INTEGER;
    v_asignados INTEGER;
    v_borrados INTEGER;
BEGIN
    IF current_database() <> 'lportal' THEN
        RAISE EXCEPTION 'Base incorrecta: se esperaba lportal. No se modificaron permisos.';
    END IF;

    -- Los IDs solo son validos si coinciden pagina, grupo, recurso, accion y rol.
    SELECT count(*) INTO v_validados
      FROM correccion_update_paginas e
      JOIN public.layout l ON l.plid = e.plid AND l.layoutid = e.layoutid
       AND l.groupid = 10136 AND l.privatelayout = FALSE
       AND l.friendlyurl = e.friendlyurl
      JOIN public.resource_ r ON r.primkey = e.plid::VARCHAR
      JOIN public.resourcecode c ON c.codeid = r.codeid
       AND c.name = 'com.liferay.portal.model.Layout' AND c.scope = 4
      JOIN public.permission_ p ON p.resourceid = r.resourceid
       AND p.permissionid = e.permissionid AND p.actionid = 'UPDATE'
       AND p.companyid = c.companyid
      JOIN public.permission_ pv ON pv.resourceid = r.resourceid
       AND pv.permissionid = e.viewpermissionid AND pv.actionid = 'VIEW'
       AND pv.companyid = c.companyid
      JOIN public.role_ ro ON ro.roleid = e.roleid AND ro.name = e.rolename
       AND ro.companyid = c.companyid
      JOIN public.roles_permissions rv ON rv.roleid = e.roleid
       AND rv.permissionid = e.viewpermissionid;

    IF v_validados <> 4 THEN
        RAISE EXCEPTION 'No coinciden las cuatro identidades o falta VIEW (% de 4). Revisar sin ampliar el alcance.', v_validados;
    END IF;

    PERFORM rp.roleid
      FROM public.roles_permissions rp
      JOIN correccion_update_paginas e ON e.roleid = rp.roleid
       AND e.permissionid = rp.permissionid
       FOR UPDATE OF rp;

    SELECT count(*) INTO v_asignados
      FROM public.roles_permissions rp
      JOIN correccion_update_paginas e ON e.roleid = rp.roleid
       AND e.permissionid = rp.permissionid;

    IF v_asignados NOT IN (0, 4) THEN
        RAISE EXCEPTION 'Estado parcial inesperado: % enlaces de 4. No se modificaron permisos.', v_asignados;
    END IF;

    DELETE FROM public.roles_permissions rp
     USING correccion_update_paginas e
     WHERE rp.roleid = e.roleid AND rp.permissionid = e.permissionid;
    GET DIAGNOSTICS v_borrados = ROW_COUNT;

    IF v_borrados <> v_asignados THEN
        RAISE EXCEPTION 'La cantidad de enlaces modificados no coincide con el diagnostico.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM public.roles_permissions rp
        JOIN correccion_update_paginas e ON e.roleid = rp.roleid
         AND e.permissionid = rp.permissionid
    ) THEN
        RAISE EXCEPTION 'Quedaron asignaciones UPDATE dentro del alcance.';
    END IF;

    RAISE NOTICE 'Enlaces UPDATE retirados: %. VIEW y permisos internos conservados.', v_borrados;
END;
$correccion$;

-- Deben aparecer cuatro filas con puede_ver=true y puede_editar_pagina=false.
SELECT e.friendlyurl AS pagina, e.rolename AS rol,
       EXISTS (SELECT 1 FROM public.roles_permissions rv
                WHERE rv.roleid = e.roleid
                  AND rv.permissionid = e.viewpermissionid) AS puede_ver,
       EXISTS (SELECT 1 FROM public.roles_permissions rp
                WHERE rp.roleid = e.roleid
                  AND rp.permissionid = e.permissionid) AS puede_editar_pagina
  FROM correccion_update_paginas e
 ORDER BY e.friendlyurl, e.rolename;

-- Ensayo reversible por defecto. COMMIT solo con autorizacion para persistir.
ROLLBACK;
