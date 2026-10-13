-- Compras: corregir identidad del mismo item antes de iniciar un RP.
-- Estados 1/2/3; cualquier relacion RP (RESERVADO/VINCULADO/ERROR) bloquea.
-- Preserva cantidad, observaciones, importes, adjudicacion, PDFs y IDs.
-- Referencia legacy: EditarLiquidacionServiceImpl.actualizaLiquidacionPrestacionEntry.
-- Catalogo QA consultado solo lectura el 13/10/2026, PostgreSQL 9.6.15.
-- Instalar antes del Java/JSP; no aplicado por Codex.

BEGIN;

CREATE OR REPLACE FUNCTION compras.corregir_item_requerimiento(
    p_id_requerimiento INTEGER,
    p_id_detalle INTEGER,
    p_id_prestacion INTEGER,
    p_id_tipo_nomenclador INTEGER,
    p_codigo_nomenclador VARCHAR,
    p_descripcion_nomenclador VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_id_sector INTEGER;
    v_detalle compras.requerimiento_detalle%ROWTYPE;
    v_nomenclador autorizaciones.nomenclador%ROWTYPE;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0
       OR p_id_detalle IS NULL OR p_id_detalle <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento y el item a corregir.';
    END IF;

    PERFORM compras.bloquear_requerimiento_reclamo_prestacional(p_id_requerimiento);
    SELECT r.estado, r.id_sector INTO v_estado, v_id_sector
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND OR v_estado NOT IN (1, 2, 3) THEN
        RAISE EXCEPTION 'El item no puede corregirse en el estado actual. Actualice la pantalla.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
        WHERE rr.id_requerimiento = p_id_requerimiento
    ) THEN
        RAISE EXCEPTION 'El requerimiento ya tiene una creacion de RP iniciada. Actualice la pantalla.';
    END IF;

    SELECT d.* INTO v_detalle FROM compras.requerimiento_detalle d
     WHERE d.id_detalle = p_id_detalle
       AND d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND OR v_detalle.tipo_item NOT IN ('NOMENCLADOR', 'MEDICAMENTO') THEN
        RAISE EXCEPTION 'No se encontro un item activo de medicamento o prestacion para corregir.';
    END IF;

    PERFORM compras.validar_nomenclador_configurado(
        v_id_sector, v_detalle.id_tipo_prestacion,
        p_id_prestacion, p_id_tipo_nomenclador);
    SELECT n.* INTO v_nomenclador FROM autorizaciones.nomenclador n
     WHERE n.id_prestacion = p_id_prestacion AND n.baja_fecha IS NULL;
    IF NOT FOUND
       OR v_nomenclador.id_tipo_nomenclador IS DISTINCT FROM p_id_tipo_nomenclador
       OR btrim(v_nomenclador.codigo) IS DISTINCT FROM btrim(p_codigo_nomenclador)
       OR btrim(v_nomenclador.descripcion) IS DISTINCT FROM btrim(p_descripcion_nomenclador) THEN
        RAISE EXCEPTION 'La seleccion no coincide con el nomenclador vigente. Actualice la pantalla.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
          AND d.id_detalle <> p_id_detalle AND d.id_prestacion = p_id_prestacion
    ) THEN
        RAISE EXCEPTION 'El requerimiento ya contiene la prestacion seleccionada.';
    END IF;

    IF v_detalle.tipo_item = 'NOMENCLADOR'
       AND v_detalle.id_prestacion = p_id_prestacion
       AND v_detalle.id_tipo_nomenclador = p_id_tipo_nomenclador
       AND btrim(v_detalle.codigo_nomenclador) = btrim(v_nomenclador.codigo)
       AND btrim(v_detalle.descripcion_nomenclador) = btrim(v_nomenclador.descripcion) THEN
        RETURN p_id_detalle;
    END IF;

    PERFORM 1 FROM compras.requerimiento_comparativa c
     WHERE c.id_requerimiento = p_id_requerimiento
     ORDER BY c.id_comparativa FOR UPDATE;
    PERFORM 1 FROM compras.requerimiento_comparativa_detalle cd
      JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
     WHERE c.id_requerimiento = p_id_requerimiento
     ORDER BY cd.id_detalle FOR UPDATE OF cd;

    IF v_detalle.id_prestacion IS DISTINCT FROM p_id_prestacion THEN
        IF EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = p_id_requerimiento
              AND cd.id_prestacion = p_id_prestacion
        ) THEN
            RAISE EXCEPTION 'La comparativa ya contiene la prestacion de destino. No se modificaron importes.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM compras.requerimiento_detalle d
            WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
              AND d.id_detalle <> p_id_detalle AND d.id_prestacion = v_detalle.id_prestacion
        ) AND EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = p_id_requerimiento
              AND cd.id_prestacion = v_detalle.id_prestacion
        ) THEN
            RAISE EXCEPTION 'La prestacion anterior esta compartida. No se pueden reasignar sus importes.';
        END IF;
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);
    UPDATE compras.requerimiento_comparativa_detalle cd
       SET id_prestacion = p_id_prestacion, modi_fecha = now(), modi_usr = v_usuario
      FROM compras.requerimiento_comparativa c
     WHERE c.id_comparativa = cd.id_comparativa
       AND c.id_requerimiento = p_id_requerimiento
       AND cd.id_prestacion = v_detalle.id_prestacion
       AND cd.id_prestacion IS DISTINCT FROM p_id_prestacion;

    UPDATE compras.requerimiento_detalle
       SET tipo_item = 'NOMENCLADOR', id_prestacion = p_id_prestacion,
           id_tipo_nomenclador = v_nomenclador.id_tipo_nomenclador,
           codigo_nomenclador = btrim(v_nomenclador.codigo),
           descripcion_nomenclador = btrim(v_nomenclador.descripcion),
           id_medicamento = NULL, troquel = NULL, nombre_medicamento = NULL,
           modi_fecha = now(), modi_usr = v_usuario
     WHERE id_detalle = p_id_detalle AND id_requerimiento = p_id_requerimiento
       AND baja_fecha IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'El item ya no esta disponible. Actualice la pantalla.';
    END IF;
    IF v_detalle.id_prestador IS NOT NULL
       AND NOT compras.es_prestador_compatible_cotizacion(
           p_id_requerimiento, v_detalle.id_prestador) THEN
        RAISE EXCEPTION 'El prestador adjudicado no es compatible con la nueva prestacion. No se modificaron importes.';
    END IF;
    RETURN p_id_detalle;
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.bloquear_guardado_comparativa(
    p_id_requerimiento INTEGER,
    p_prestaciones_esperadas VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_estado INTEGER;
    v_prestaciones INTEGER[];
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de la comparativa.';
    END IF;
    IF p_prestaciones_esperadas IS NULL
       OR p_prestaciones_esperadas !~ '^[1-9][0-9]*(,[1-9][0-9]*)*$' THEN
        RAISE EXCEPTION 'No se pudo validar la lista de prestaciones. Actualice la pantalla.';
    END IF;
    v_prestaciones := string_to_array(p_prestaciones_esperadas, ',')::INTEGER[];
    IF array_length(v_prestaciones, 1) <> (
           SELECT count(DISTINCT id) FROM unnest(v_prestaciones) id
       ) THEN
        RAISE EXCEPTION 'La lista de prestaciones no es valida. Actualice la pantalla.';
    END IF;

    PERFORM compras.bloquear_requerimiento_reclamo_prestacional(p_id_requerimiento);
    SELECT r.estado INTO v_estado FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND OR v_estado <> 2 THEN
        RAISE EXCEPTION 'La comparativa ya no puede modificarse en el estado actual. Actualice la pantalla.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
        WHERE rr.id_requerimiento = p_id_requerimiento
    ) THEN
        RAISE EXCEPTION 'El requerimiento ya tiene una creacion de RP iniciada. Actualice la pantalla.';
    END IF;
    PERFORM 1 FROM compras.requerimiento_detalle d
     WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
     ORDER BY d.id_detalle FOR UPDATE;
    IF EXISTS (
        SELECT id FROM unnest(v_prestaciones) id
        EXCEPT
        SELECT d.id_prestacion FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
    ) OR EXISTS (
        SELECT d.id_prestacion FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
        EXCEPT
        SELECT id FROM unnest(v_prestaciones) id
    ) OR array_length(v_prestaciones, 1) <> (
        SELECT count(*) FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION 'Las prestaciones cambiaron durante la carga. Actualice la pantalla.';
    END IF;
END;
$func$
LANGUAGE plpgsql;

-- Adaptacion limitada del trigger existente: no instalar triggers nuevos.
DO $adaptar_detalle$
DECLARE
    v_oid REGPROCEDURE;
    v_def TEXT;
    v_original TEXT;
    v_viejo TEXT;
    v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.validar_requerimiento_detalle_fila()');
    IF v_oid IS NULL THEN
        RETURN;
    END IF;
    v_def := pg_get_functiondef(v_oid);
    v_original := v_def;

    v_viejo := $viejo$    v_total_detalles_activos INTEGER;
BEGIN$viejo$;
    v_nuevo := $nuevo$    v_total_detalles_activos INTEGER;
    v_id_sector_correccion INTEGER;
    v_fila_correccion compras.requerimiento_detalle%ROWTYPE;
BEGIN$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
            RAISE EXCEPTION 'Variante desconocida del trigger de detalle. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    v_viejo := $viejo$    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NOT NULL$viejo$;
    v_nuevo := $nuevo$    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NULL AND NEW.baja_fecha IS NULL
       AND OLD.tipo_item IN ('NOMENCLADOR', 'MEDICAMENTO')
       AND NEW.tipo_item = 'NOMENCLADOR'
       AND (v_estado IN (2, 3) OR OLD.tipo_item = 'MEDICAMENTO')
       AND (NEW.tipo_item IS DISTINCT FROM OLD.tipo_item
            OR NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion
            OR NEW.id_tipo_nomenclador IS DISTINCT FROM OLD.id_tipo_nomenclador
            OR NEW.codigo_nomenclador IS DISTINCT FROM OLD.codigo_nomenclador
            OR NEW.descripcion_nomenclador IS DISTINCT FROM OLD.descripcion_nomenclador) THEN

        PERFORM compras.bloquear_requerimiento_reclamo_prestacional(NEW.id_requerimiento);
        SELECT r.estado, r.id_sector INTO v_estado, v_id_sector_correccion
          FROM compras.requerimiento r
         WHERE r.id_requerimiento = NEW.id_requerimiento AND r.baja_fecha IS NULL
         FOR UPDATE;
        IF NOT FOUND OR v_estado NOT IN (1, 2, 3)
           OR EXISTS (SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
                      WHERE rr.id_requerimiento = NEW.id_requerimiento) THEN
            RAISE EXCEPTION 'El item no puede corregirse despues de iniciar el RP.';
        END IF;
        v_fila_correccion := NEW;
        v_fila_correccion.tipo_item := OLD.tipo_item;
        v_fila_correccion.id_prestacion := OLD.id_prestacion;
        v_fila_correccion.id_tipo_nomenclador := OLD.id_tipo_nomenclador;
        v_fila_correccion.codigo_nomenclador := OLD.codigo_nomenclador;
        v_fila_correccion.descripcion_nomenclador := OLD.descripcion_nomenclador;
        v_fila_correccion.id_medicamento := OLD.id_medicamento;
        v_fila_correccion.troquel := OLD.troquel;
        v_fila_correccion.nombre_medicamento := OLD.nombre_medicamento;
        v_fila_correccion.modi_fecha := OLD.modi_fecha;
        v_fila_correccion.modi_usr := OLD.modi_usr;
        IF v_fila_correccion IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION 'La correccion del item debe conservar cantidades, observaciones e importes.';
        END IF;
        PERFORM compras.validar_nomenclador_configurado(
            v_id_sector_correccion, OLD.id_tipo_prestacion,
            NEW.id_prestacion, NEW.id_tipo_nomenclador);
        IF NEW.id_medicamento IS NOT NULL OR NEW.troquel IS NOT NULL
           OR NEW.nombre_medicamento IS NOT NULL OR NOT EXISTS (
            SELECT 1 FROM autorizaciones.nomenclador n
            WHERE n.id_prestacion = NEW.id_prestacion AND n.baja_fecha IS NULL
              AND btrim(n.codigo) = NEW.codigo_nomenclador
              AND btrim(n.descripcion) = NEW.descripcion_nomenclador
        ) THEN
            RAISE EXCEPTION 'La correccion debe conservar una referencia canonica de nomenclador.';
        END IF;
        IF NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion AND EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = NEW.id_requerimiento
              AND cd.id_prestacion = OLD.id_prestacion
        ) THEN
            RAISE EXCEPTION 'La correccion debe actualizar la comparativa en la misma transaccion.';
        END IF;
        IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d
                   WHERE d.id_requerimiento = NEW.id_requerimiento AND d.baja_fecha IS NULL
                     AND d.id_detalle <> NEW.id_detalle AND d.id_prestacion = NEW.id_prestacion) THEN
            RAISE EXCEPTION 'El requerimiento ya contiene la prestacion seleccionada.';
        END IF;
        RETURN NEW;
    END IF;

    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NOT NULL$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
            RAISE EXCEPTION 'Variante desconocida del trigger de detalle. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    IF v_def IS DISTINCT FROM v_original THEN
        EXECUTE v_def;
    END IF;
END;
$adaptar_detalle$;

COMMIT;
