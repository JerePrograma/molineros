-- SOLO QA. Datos confirmados de produccion; ISO-8859-1 sin BOM.
-- Alta directa: si 11503 ya existe, el INSERT falla sin sobrescribirlo.
-- Referencia: sql-scripts/funciones/conceptos/insertar_nomenclador_concepto.sql.
BEGIN;
LOCK TABLE autorizaciones.nomenclador IN SHARE ROW EXCLUSIVE MODE;

INSERT INTO autorizaciones.nomenclador (
    id_prestacion, descripcion, codigo, id_tipo_nomenclador, marca_rein_liq,
    id_especialidad, importe, coef_gastos, coef_honorarios,
    alta_fecha, alta_usr, modi_fecha, modi_usr
) VALUES (
    11503, 'GASTOS DE GESTION PROVEEDOR', '9100058', 3, 3,
    0, 0, 0, 0,
    localtimestamp, current_user, localtimestamp, current_user
);

-- Avanzar la secuencia sin retroceder su valor actual.
SELECT setval(
    pg_get_serial_sequence('autorizaciones.nomenclador', 'id_prestacion'),
    GREATEST(
        (SELECT max(id_prestacion) FROM autorizaciones.nomenclador),
        nextval(pg_get_serial_sequence('autorizaciones.nomenclador', 'id_prestacion'))
    ),
    TRUE
);

COMMIT;