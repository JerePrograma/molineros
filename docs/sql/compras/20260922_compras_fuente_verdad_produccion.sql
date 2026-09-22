/*
 * MOLINEROS - COMPRAS
 * FUENTE DE VERDAD DE LA BASE INSTALADA
 *
 * Archivo sugerido en el repositorio:
 *   docs/sql/compras/20260922_compras_fuente_verdad_produccion.sql
 *
 * Fecha de la fotografia recibida: 2026-09-22 11:21:10 -03
 * Base observada: devmolineros
 * PostgreSQL observado: 9.6.5
 *
 * IMPORTANTE
 * ==========
 * Este archivo NO es una migracion ni un instalador.
 * Es un relevamiento de SOLO LECTURA, compatible con PostgreSQL 9.6,
 * cuyo resultado es la fuente de verdad del estado SQL realmente instalado.
 *
 * Orden de autoridad para cualquier analisis posterior:
 *   1. Resultado de este archivo ejecutado contra la base objetivo.
 *   2. Este snapshot documentado y su .md asociado.
 *   3. SQL versionado en el repositorio.
 *
 * Nunca inferir produccion desde compras_schema.sql si contradice este relevamiento.
 *
 * HECHOS YA COMPROBADOS EN LA EJECUCION DEL 2026-09-22
 * ======================================================
 * - schema compras owner: postgres
 * - 8 tablas
 * - 81 funciones en schema compras
 * - 4 secuencias
 * - 15 constraints totales
 * - 0 CHECK constraints
 * - 0 views
 * - 0 materialized views
 * - 2 triggers de usuario efectivamente instalados:
 *     trg_compras_requerimiento_completar_baja
 *     trg_compras_detalle_calcular_total
 * - Existen funciones validar_* RETURNS trigger que NO aparecieron asociadas
 *   a triggers de usuario. Su existencia no implica que hoy se ejecuten.
 * - compras.requerimiento_detalle no mostro una FK a requerimiento.
 * - compras.requerimiento no mostro una FK a sector_requerimiento.
 * - recupero vs (cargo_tercerizadora > 0): 1 inconsistencia sobre 39 filas.
 * - cargo_ospim + cargo_tercerizadora = 100: 0 inconsistencias sobre 39 filas.
 * - precio_total_estimado = round(cantidad * precio_unitario_estimado, 2):
 *   0 inconsistencias sobre 15 filas comparables.
 * - 39 requerimientos: 35 con clave de afiliado completa y 4 sin ella.
 * - 73 detalles: 69 NOMENCLADOR y 4 OBSERVACION; 0 MEDICAMENTO observados.
 *
 * El objetivo de repetir este script es confirmar si esos hechos siguen vigentes.
 */

BEGIN;
SET TRANSACTION READ ONLY;
SET LOCAL statement_timeout = '120s';

/* ================================================================
 * 00. CONTEXTO
 * ================================================================ */
SELECT
    '00_CONTEXTO'::text AS seccion,
    CURRENT_TIMESTAMP AS fecha_consulta,
    CURRENT_DATABASE() AS base,
    CURRENT_USER AS usuario,
    VERSION() AS version_postgresql,
    CURRENT_SETTING('server_version') AS server_version,
    CURRENT_SETTING('server_encoding') AS server_encoding,
    CURRENT_SETTING('client_encoding') AS client_encoding,
    CURRENT_SETTING('TimeZone') AS timezone,
    CURRENT_SETTING('search_path') AS search_path;


/* ================================================================
 * 01. RESUMEN DEL SCHEMA COMPRAS
 * Una fila por tipo de objeto. Facil de comparar entre ambientes.
 * ================================================================ */
SELECT tipo, cantidad
FROM (
    SELECT 'TABLES'::text AS tipo, COUNT(*)::bigint AS cantidad
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND c.relkind IN ('r', 'p')

    UNION ALL

    SELECT 'FUNCTIONS', COUNT(*)::bigint
    FROM pg_proc p
    JOIN pg_namespace n ON n.oid = p.pronamespace
    WHERE n.nspname = 'compras' AND NOT p.proisagg

    UNION ALL

    SELECT 'SEQUENCES', COUNT(*)::bigint
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND c.relkind = 'S'

    UNION ALL

    SELECT 'CONSTRAINTS', COUNT(*)::bigint
    FROM pg_constraint con
    JOIN pg_class c ON c.oid = con.conrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras'

    UNION ALL

    SELECT 'CHECK_CONSTRAINTS', COUNT(*)::bigint
    FROM pg_constraint con
    JOIN pg_class c ON c.oid = con.conrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND con.contype = 'c'

    UNION ALL

    SELECT 'USER_TRIGGERS', COUNT(*)::bigint
    FROM pg_trigger t
    JOIN pg_class c ON c.oid = t.tgrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND NOT t.tgisinternal

    UNION ALL

    SELECT 'VIEWS', COUNT(*)::bigint
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND c.relkind = 'v'

    UNION ALL

    SELECT 'MATERIALIZED_VIEWS', COUNT(*)::bigint
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'compras' AND c.relkind = 'm'
) resumen
ORDER BY tipo;


/* ================================================================
 * 02. TABLAS Y CANTIDAD EXACTA DE FILAS
 * Son tablas pequenas en el relevamiento actual.
 * ================================================================ */
SELECT 'requerimiento'::text AS tabla, COUNT(*)::bigint AS filas
FROM compras.requerimiento
UNION ALL
SELECT 'requerimiento_cotizacion_prestador', COUNT(*) FROM compras.requerimiento_cotizacion_prestador
UNION ALL
SELECT 'requerimiento_detalle', COUNT(*) FROM compras.requerimiento_detalle
UNION ALL
SELECT 'requerimiento_pedido_cotizacion', COUNT(*) FROM compras.requerimiento_pedido_cotizacion
UNION ALL
SELECT 'requerimiento_presupuesto', COUNT(*) FROM compras.requerimiento_presupuesto
UNION ALL
SELECT 'requerimiento_reclamo_prestacional', COUNT(*) FROM compras.requerimiento_reclamo_prestacional
UNION ALL
SELECT 'sector_requerimiento', COUNT(*) FROM compras.sector_requerimiento
UNION ALL
SELECT 'tipo_prestacion', COUNT(*) FROM compras.tipo_prestacion
ORDER BY tabla;


/* ================================================================
 * 03. COLUMNAS REALES DE TABLAS Y TIPO COMPUESTO
 * Incluye defaults y orden fisico/contractual.
 * ================================================================ */
SELECT
    n.nspname AS schema,
    c.relname AS objeto,
    CASE c.relkind
        WHEN 'r' THEN 'TABLE'
        WHEN 'p' THEN 'PARTITIONED TABLE'
        WHEN 'c' THEN 'COMPOSITE TYPE'
        WHEN 'v' THEN 'VIEW'
        WHEN 'm' THEN 'MATERIALIZED VIEW'
        ELSE c.relkind::text
    END AS tipo_objeto,
    a.attnum AS posicion,
    a.attname AS columna,
    pg_catalog.format_type(a.atttypid, a.atttypmod) AS tipo,
    a.attnotnull AS not_null,
    pg_get_expr(ad.adbin, ad.adrelid) AS valor_default,
    col_description(a.attrelid, a.attnum) AS comentario
FROM pg_attribute a
JOIN pg_class c ON c.oid = a.attrelid
JOIN pg_namespace n ON n.oid = c.relnamespace
LEFT JOIN pg_attrdef ad
       ON ad.adrelid = a.attrelid
      AND ad.adnum = a.attnum
WHERE n.nspname = 'compras'
  AND c.relkind IN ('r', 'p', 'c', 'v', 'm')
  AND a.attnum > 0
  AND NOT a.attisdropped
ORDER BY c.relname, a.attnum;


/* ================================================================
 * 04. CONSTRAINTS REALES
 * Incluye relaciones entrantes y salientes.
 * Si no aparece un CHECK, no se considera instalado.
 * ================================================================ */
SELECT
    ns.nspname AS schema_origen,
    rel.relname AS tabla_origen,
    con.conname AS constraint_name,
    CASE con.contype
        WHEN 'p' THEN 'PRIMARY KEY'
        WHEN 'f' THEN 'FOREIGN KEY'
        WHEN 'u' THEN 'UNIQUE'
        WHEN 'c' THEN 'CHECK'
        WHEN 'x' THEN 'EXCLUSION'
        ELSE con.contype::text
    END AS tipo,
    ns_ref.nspname AS schema_referenciado,
    rel_ref.relname AS tabla_referenciada,
    con.convalidated AS validado,
    pg_get_constraintdef(con.oid, true) AS definicion
FROM pg_constraint con
LEFT JOIN pg_class rel ON rel.oid = con.conrelid
LEFT JOIN pg_namespace ns ON ns.oid = rel.relnamespace
LEFT JOIN pg_class rel_ref ON rel_ref.oid = con.confrelid
LEFT JOIN pg_namespace ns_ref ON ns_ref.oid = rel_ref.relnamespace
WHERE ns.nspname = 'compras'
   OR ns_ref.nspname = 'compras'
ORDER BY schema_origen, tabla_origen, tipo, constraint_name;


/* ================================================================
 * 05. INDICES REALES
 * ================================================================ */
SELECT
    ns.nspname AS schema,
    tbl.relname AS tabla,
    idx.relname AS indice,
    i.indisprimary AS primary_key,
    i.indisunique AS unique_index,
    i.indisvalid AS valido,
    i.indisready AS listo,
    pg_get_indexdef(idx.oid) AS definicion
FROM pg_index i
JOIN pg_class idx ON idx.oid = i.indexrelid
JOIN pg_class tbl ON tbl.oid = i.indrelid
JOIN pg_namespace ns ON ns.oid = tbl.relnamespace
WHERE ns.nspname = 'compras'
ORDER BY tbl.relname, idx.relname;


/* ================================================================
 * 06. TRIGGERS DE USUARIO EFECTIVAMENTE INSTALADOS
 * No listar triggers internos de FK: los constraints ya los describen.
 * ================================================================ */
SELECT
    ns.nspname AS schema,
    tbl.relname AS tabla,
    trg.tgname AS trigger,
    CASE trg.tgenabled
        WHEN 'O' THEN 'ENABLED'
        WHEN 'D' THEN 'DISABLED'
        WHEN 'R' THEN 'REPLICA'
        WHEN 'A' THEN 'ALWAYS'
        ELSE trg.tgenabled::text
    END AS estado,
    proc_ns.nspname AS funcion_schema,
    proc.proname AS funcion,
    pg_get_triggerdef(trg.oid, true) AS definicion_trigger,
    pg_get_functiondef(proc.oid) AS definicion_funcion
FROM pg_trigger trg
JOIN pg_class tbl ON tbl.oid = trg.tgrelid
JOIN pg_namespace ns ON ns.oid = tbl.relnamespace
JOIN pg_proc proc ON proc.oid = trg.tgfoid
JOIN pg_namespace proc_ns ON proc_ns.oid = proc.pronamespace
WHERE ns.nspname = 'compras'
  AND NOT trg.tgisinternal
ORDER BY tbl.relname, trg.tgname;


/* ================================================================
 * 07. FUNCIONES RETURNS TRIGGER QUE EXISTEN PERO NO ESTAN ASOCIADAS
 * Esto evita confundir "funcion presente" con "regla ejecutada".
 * ================================================================ */
SELECT
    n.nspname AS schema,
    p.proname AS funcion,
    oidvectortypes(p.proargtypes) AS argumentos,
    pg_get_functiondef(p.oid) AS definicion
FROM pg_proc p
JOIN pg_namespace n ON n.oid = p.pronamespace
WHERE n.nspname = 'compras'
  AND NOT p.proisagg
  AND pg_get_function_result(p.oid) = 'trigger'
  AND NOT EXISTS (
      SELECT 1
      FROM pg_trigger t
      WHERE t.tgfoid = p.oid
        AND NOT t.tgisinternal
  )
ORDER BY p.proname, oidvectortypes(p.proargtypes);


/* ================================================================
 * 08. TODAS LAS FUNCIONES REALES DE COMPRAS
 * Esta seccion sustituye al SQL versionado como evidencia de firmas/cuerpos.
 * Compatible con PostgreSQL 9.6: filtra agregados ANTES de pedir definicion.
 * ================================================================ */
SELECT
    n.nspname AS schema,
    p.proname AS funcion,
    oidvectortypes(p.proargtypes) AS argumentos_identidad,
    pg_get_function_arguments(p.oid) AS argumentos_completos,
    pg_get_function_result(p.oid) AS retorno,
    l.lanname AS lenguaje,
    CASE p.provolatile
        WHEN 'i' THEN 'IMMUTABLE'
        WHEN 's' THEN 'STABLE'
        WHEN 'v' THEN 'VOLATILE'
        ELSE p.provolatile::text
    END AS volatilidad,
    p.proisstrict AS strict,
    p.prosecdef AS security_definer,
    pg_get_userbyid(p.proowner) AS owner,
    pg_get_functiondef(p.oid) AS definicion
FROM pg_proc p
JOIN pg_namespace n ON n.oid = p.pronamespace
JOIN pg_language l ON l.oid = p.prolang
WHERE n.nspname = 'compras'
  AND NOT p.proisagg
ORDER BY p.proname, oidvectortypes(p.proargtypes);


/* ================================================================
 * 09. FUNCIONES EXTERNAS QUE REFERENCIAN TEXTUALMENTE COMPRAS
 * Corrige el relevamiento anterior que fallo con array_agg.
 * CASE evita invocar pg_get_functiondef sobre agregados en PostgreSQL 9.6.
 * ================================================================ */
SELECT
    f.schema,
    f.funcion,
    f.argumentos,
    f.retorno,
    f.definicion
FROM (
    SELECT
        n.nspname AS schema,
        p.proname AS funcion,
        oidvectortypes(p.proargtypes) AS argumentos,
        pg_get_function_result(p.oid) AS retorno,
        CASE
            WHEN NOT p.proisagg THEN pg_get_functiondef(p.oid)
            ELSE NULL
        END AS definicion
    FROM pg_proc p
    JOIN pg_namespace n ON n.oid = p.pronamespace
    WHERE n.nspname <> 'compras'
      AND n.nspname NOT IN ('pg_catalog', 'information_schema')
) f
WHERE f.definicion ILIKE '%compras.%'
ORDER BY f.schema, f.funcion, f.argumentos;


/* ================================================================
 * 10. VISTAS EXTERNAS QUE REFERENCIAN COMPRAS
 * ================================================================ */
SELECT
    n.nspname AS schema,
    c.relname AS vista,
    CASE c.relkind
        WHEN 'v' THEN 'VIEW'
        WHEN 'm' THEN 'MATERIALIZED VIEW'
    END AS tipo,
    pg_get_viewdef(c.oid, true) AS definicion
FROM pg_class c
JOIN pg_namespace n ON n.oid = c.relnamespace
WHERE n.nspname <> 'compras'
  AND n.nspname NOT IN ('pg_catalog', 'information_schema')
  AND c.relkind IN ('v', 'm')
  AND pg_get_viewdef(c.oid, true) ILIKE '%compras.%'
ORDER BY n.nspname, c.relname;


/* ================================================================
 * 11. SECUENCIAS Y COLUMNAS PROPIETARIAS
 * ================================================================ */
SELECT
    seq_ns.nspname AS sequence_schema,
    seq.relname AS sequence_name,
    tbl_ns.nspname AS tabla_schema,
    tbl.relname AS tabla,
    att.attname AS columna
FROM pg_class seq
JOIN pg_namespace seq_ns ON seq_ns.oid = seq.relnamespace
LEFT JOIN pg_depend dep
       ON dep.classid = 'pg_class'::regclass
      AND dep.objid = seq.oid
      AND dep.deptype IN ('a', 'i')
LEFT JOIN pg_class tbl ON tbl.oid = dep.refobjid
LEFT JOIN pg_namespace tbl_ns ON tbl_ns.oid = tbl.relnamespace
LEFT JOIN pg_attribute att
       ON att.attrelid = tbl.oid
      AND att.attnum = dep.refobjsubid
WHERE seq.relkind = 'S'
  AND (seq_ns.nspname = 'compras' OR tbl_ns.nspname = 'compras')
ORDER BY sequence_schema, sequence_name;


/* ================================================================
 * 12. MAESTROS EXTERNOS DIRECTAMENTE USADOS POR COMPRAS
 * Solo estructura: no duplica datos personales en la salida.
 * Lista basada en cuerpos de funciones reales observados.
 * ================================================================ */
SELECT
    n.nspname AS schema,
    c.relname AS objeto,
    a.attnum AS posicion,
    a.attname AS columna,
    pg_catalog.format_type(a.atttypid, a.atttypmod) AS tipo,
    a.attnotnull AS not_null,
    pg_get_expr(ad.adbin, ad.adrelid) AS valor_default
FROM pg_attribute a
JOIN pg_class c ON c.oid = a.attrelid
JOIN pg_namespace n ON n.oid = c.relnamespace
LEFT JOIN pg_attrdef ad
       ON ad.adrelid = a.attrelid
      AND ad.adnum = a.attnum
WHERE a.attnum > 0
  AND NOT a.attisdropped
  AND (
       (n.nspname = 'public' AND c.relname IN (
            'afiliado',
            'seccional',
            'prestador',
            'prestador_rubro',
            'prestad_contacto_e',
            'contacto_e',
            'afi_situ_medica',
            'tercerizadora_servicio'
       ))
       OR (n.nspname = 'autorizaciones' AND c.relname = 'nomenclador')
       OR (n.nspname = 'informacion_afip' AND c.relname = 'empresa')
  )
ORDER BY n.nspname, c.relname, a.attnum;


/* ================================================================
 * 13. FUNCIONES LEGACY DE LOS MAESTROS
 * Inventario por nombre para encontrar reutilizacion antes de crear SQL nuevo.
 * ================================================================ */
SELECT
    f.schema,
    f.funcion,
    f.argumentos,
    f.retorno,
    f.definicion
FROM (
    SELECT
        n.nspname AS schema,
        p.proname AS funcion,
        oidvectortypes(p.proargtypes) AS argumentos,
        pg_get_function_result(p.oid) AS retorno,
        CASE
            WHEN NOT p.proisagg THEN pg_get_functiondef(p.oid)
            ELSE NULL
        END AS definicion
    FROM pg_proc p
    JOIN pg_namespace n ON n.oid = p.pronamespace
    WHERE n.nspname NOT IN ('pg_catalog', 'information_schema')
      AND (
           lower(p.proname) LIKE '%afiliado%'
        OR lower(p.proname) LIKE '%prestador%'
        OR lower(p.proname) LIKE '%nomenclador%'
        OR lower(p.proname) LIKE '%tercerizadora%'
        OR lower(p.proname) LIKE '%empresa%'
      )
) f
WHERE f.definicion IS NOT NULL
ORDER BY f.funcion, f.schema, f.argumentos;


/* ================================================================
 * 14. PRIVILEGIOS DEL SCHEMA
 * ================================================================ */
SELECT
    table_schema,
    table_name,
    grantor,
    grantee,
    privilege_type,
    is_grantable
FROM information_schema.table_privileges
WHERE table_schema = 'compras'
ORDER BY table_name, grantee, privilege_type;

SELECT
    routine_schema,
    routine_name,
    grantor,
    grantee,
    privilege_type,
    is_grantable
FROM information_schema.routine_privileges
WHERE routine_schema = 'compras'
ORDER BY routine_name, grantee;


/* ================================================================
 * 15. INTEGRIDAD / REDUNDANCIAS ARITMETICAS
 * No solo cuenta: tambien lista los IDs inconsistentes.
 * ================================================================ */

/* 15.1 recupero debe investigarse antes de derivarlo: hubo 1 mismatch. */
SELECT
    COUNT(*)::bigint AS filas_totales,
    SUM(CASE WHEN recupero IS DISTINCT FROM (cargo_tercerizadora > 0) THEN 1 ELSE 0 END)::bigint
        AS inconsistencias
FROM compras.requerimiento;

SELECT
    id_requerimiento,
    estado,
    cargo_ospim,
    cargo_tercerizadora,
    recupero,
    id_tercerizadora,
    alta_fecha,
    modi_fecha,
    baja_fecha
FROM compras.requerimiento
WHERE recupero IS DISTINCT FROM (cargo_tercerizadora > 0)
ORDER BY id_requerimiento;

/* 15.2 complemento de cargos. */
SELECT
    COUNT(*)::bigint AS filas_totales,
    SUM(CASE WHEN cargo_ospim + cargo_tercerizadora <> 100 THEN 1 ELSE 0 END)::bigint
        AS inconsistencias
FROM compras.requerimiento;

SELECT
    id_requerimiento,
    cargo_ospim,
    cargo_tercerizadora
FROM compras.requerimiento
WHERE cargo_ospim + cargo_tercerizadora <> 100
ORDER BY id_requerimiento;

/* 15.3 total derivable del detalle. */
SELECT
    COUNT(*)::bigint AS filas_totales,
    SUM(
        CASE
            WHEN cantidad IS NOT NULL
             AND precio_unitario_estimado IS NOT NULL
             AND precio_total_estimado IS NOT NULL
            THEN 1 ELSE 0
        END
    )::bigint AS filas_comparables,
    SUM(
        CASE
            WHEN cantidad IS NOT NULL
             AND precio_unitario_estimado IS NOT NULL
             AND precio_total_estimado IS NOT NULL
             AND precio_total_estimado IS DISTINCT FROM round(cantidad * precio_unitario_estimado, 2)
            THEN 1 ELSE 0
        END
    )::bigint AS inconsistencias
FROM compras.requerimiento_detalle;

SELECT
    id_detalle,
    id_requerimiento,
    cantidad,
    precio_unitario_estimado,
    precio_total_estimado,
    round(cantidad * precio_unitario_estimado, 2) AS total_calculado
FROM compras.requerimiento_detalle
WHERE cantidad IS NOT NULL
  AND precio_unitario_estimado IS NOT NULL
  AND precio_total_estimado IS NOT NULL
  AND precio_total_estimado IS DISTINCT FROM round(cantidad * precio_unitario_estimado, 2)
ORDER BY id_requerimiento, id_detalle;


/* ================================================================
 * 16. AFILIADO: IDENTIDAD, DUPLICACION FISICA Y RESOLUCION ACTUAL
 * No imprime nombres, documentos, domicilios ni contactos.
 * ================================================================ */
SELECT
    COUNT(*)::bigint AS requerimientos,
    SUM(CASE WHEN afiliado_cuil_titular IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_cuil,
    SUM(CASE WHEN afiliado_int IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_integrante,
    SUM(CASE WHEN afiliado_cuil_titular IS NOT NULL AND afiliado_int IS NOT NULL THEN 1 ELSE 0 END)::bigint
        AS con_clave_completa,
    SUM(CASE WHEN afiliado_id_ospim IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_id_ospim_snapshot,
    SUM(CASE WHEN afiliado_nombre IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_nombre_snapshot,
    SUM(CASE WHEN afiliado_apellido IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_apellido_snapshot,
    SUM(CASE WHEN afiliado_documento_tipo IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_tipo_doc_snapshot,
    SUM(CASE WHEN afiliado_documento_nro IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_nro_doc_snapshot,
    SUM(CASE WHEN afiliado_direccion IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_direccion_snapshot,
    SUM(CASE WHEN afiliado_localidad IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_localidad_snapshot,
    SUM(CASE WHEN afiliado_provincia IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_provincia_snapshot,
    SUM(CASE WHEN afiliado_celular IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_celular_snapshot,
    SUM(CASE WHEN afiliado_telefono IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_telefono_snapshot,
    SUM(CASE WHEN afiliado_email IS NOT NULL THEN 1 ELSE 0 END)::bigint AS con_email_snapshot
FROM compras.requerimiento;

/* Claves de Compras que ya no resuelven contra el maestro actual. */
SELECT
    r.id_requerimiento,
    r.afiliado_cuil_titular,
    r.afiliado_int,
    r.estado,
    r.baja_fecha
FROM compras.requerimiento r
LEFT JOIN public.afiliado a
       ON a.cuil_titular = r.afiliado_cuil_titular
      AND a.inte = r.afiliado_int
WHERE r.afiliado_cuil_titular IS NOT NULL
  AND r.afiliado_int IS NOT NULL
  AND a.cuil_titular IS NULL
ORDER BY r.id_requerimiento;


/* ================================================================
 * 17. PRESTADORES: REFERENCIAS QUE NO RESUELVEN AL MAESTRO
 * ================================================================ */
SELECT origen, id_requerimiento, id_prestador
FROM (
    SELECT
        'requerimiento_cotizacion_prestador'::text AS origen,
        rcp.id_requerimiento,
        rcp.id_prestador
    FROM compras.requerimiento_cotizacion_prestador rcp
    LEFT JOIN public.prestador p ON p.id_prestador = rcp.id_prestador
    WHERE p.id_prestador IS NULL

    UNION ALL

    SELECT
        'requerimiento_detalle',
        d.id_requerimiento,
        d.id_prestador
    FROM compras.requerimiento_detalle d
    LEFT JOIN public.prestador p ON p.id_prestador = d.id_prestador
    WHERE d.id_prestador IS NOT NULL
      AND p.id_prestador IS NULL

    UNION ALL

    SELECT
        'requerimiento_presupuesto',
        rp.id_requerimiento,
        rp.id_prestador
    FROM compras.requerimiento_presupuesto rp
    LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador
    WHERE rp.id_prestador IS NOT NULL
      AND p.id_prestador IS NULL
) faltantes
ORDER BY origen, id_requerimiento, id_prestador;


/* ================================================================
 * 18. NOMENCLADOR: DISTRIBUCION Y REFERENCIAS ACTUALES
 * ================================================================ */
SELECT
    d.tipo_item,
    d.id_tipo_prestacion,
    d.id_tipo_nomenclador,
    COUNT(*)::bigint AS cantidad
FROM compras.requerimiento_detalle d
GROUP BY d.tipo_item, d.id_tipo_prestacion, d.id_tipo_nomenclador
ORDER BY d.tipo_item, d.id_tipo_prestacion, d.id_tipo_nomenclador;

SELECT
    d.id_detalle,
    d.id_requerimiento,
    d.id_prestacion,
    d.id_tipo_nomenclador AS tipo_guardado,
    n.id_tipo_nomenclador AS tipo_actual,
    CASE
        WHEN n.id_prestacion IS NULL THEN 'NO_EXISTE_EN_MAESTRO'
        WHEN n.baja_fecha IS NOT NULL THEN 'BAJA_EN_MAESTRO'
        WHEN d.id_tipo_nomenclador IS DISTINCT FROM n.id_tipo_nomenclador THEN 'TIPO_DIFERENTE'
        ELSE 'OK'
    END AS estado_referencia
FROM compras.requerimiento_detalle d
LEFT JOIN autorizaciones.nomenclador n
       ON n.id_prestacion = d.id_prestacion
WHERE d.id_prestacion IS NOT NULL
  AND (
       n.id_prestacion IS NULL
       OR n.baja_fecha IS NOT NULL
       OR d.id_tipo_nomenclador IS DISTINCT FROM n.id_tipo_nomenclador
  )
ORDER BY d.id_requerimiento, d.id_detalle;


/* ================================================================
 * 19. RELACIONES INTERNAS SIN FK QUE CONVIENE VIGILAR
 * No agrega constraints: solo detecta orfandad real.
 * ================================================================ */
SELECT
    'requerimiento_detalle -> requerimiento'::text AS relacion,
    d.id_detalle::bigint AS id_hijo,
    d.id_requerimiento::bigint AS id_padre
FROM compras.requerimiento_detalle d
LEFT JOIN compras.requerimiento r
       ON r.id_requerimiento = d.id_requerimiento
WHERE r.id_requerimiento IS NULL

UNION ALL

SELECT
    'requerimiento -> sector_requerimiento',
    r.id_requerimiento::bigint,
    r.id_sector::bigint
FROM compras.requerimiento r
LEFT JOIN compras.sector_requerimiento s
       ON s.id_sector = r.id_sector
WHERE s.id_sector IS NULL
ORDER BY relacion, id_hijo;


/* ================================================================
 * 20. CAMPOS CANDIDATOS A NORMALIZACION
 * Inventario fisico. La decision se toma en codigo + SQL, no aqui.
 * ================================================================ */
SELECT
    table_name,
    ordinal_position,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'compras'
  AND (
       column_name LIKE 'afiliado_%'
       OR column_name IN (
            'cargo_ospim',
            'cargo_tercerizadora',
            'recupero',
            'id_prestador',
            'email_destino',
            'descripcion_prestador',
            'descripcion_empresa',
            'empresa_cuit',
            'empresa_sucursal',
            'id_prestacion',
            'id_tipo_nomenclador',
            'codigo_nomenclador',
            'descripcion_nomenclador',
            'id_medicamento',
            'troquel',
            'nombre_medicamento',
            'cantidad',
            'precio_unitario_estimado',
            'precio_total_estimado'
       )
  )
ORDER BY table_name, ordinal_position;


/* ================================================================
 * FIN
 * ================================================================ */
ROLLBACK;
