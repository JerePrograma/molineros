-- Agrega Gastos de envio de drogueria a cada nuevo requerimiento.
-- Cantidad inicial fija: 1. El detalle se puede modificar o eliminar despues.
BEGIN;

CREATE OR REPLACE FUNCTION compras.agregar_gastos_envio_requerimiento()
RETURNS TRIGGER
AS $$
BEGIN
    INSERT INTO compras.requerimiento_detalle (
        id_requerimiento,
        tipo_item,
        id_prestacion,
        id_tipo_nomenclador,
        codigo_nomenclador,
        descripcion_nomenclador,
        cantidad,
        alta_usr
    )
    SELECT
        NEW.id_requerimiento,
        'NOMENCLADOR',
        n.id_prestacion,
        n.id_tipo_nomenclador,
        n.codigo,
        n.descripcion,
        1,
        NEW.alta_usr
    FROM autorizaciones.nomenclador n
    WHERE n.id_prestacion = 10609;

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_compras_requerimiento_gastos_envio
    ON compras.requerimiento;

CREATE TRIGGER trg_compras_requerimiento_gastos_envio
    AFTER INSERT ON compras.requerimiento
    FOR EACH ROW
    EXECUTE PROCEDURE compras.agregar_gastos_envio_requerimiento();

COMMIT;
