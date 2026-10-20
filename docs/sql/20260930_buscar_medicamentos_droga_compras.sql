-- Filtro por droga para el nomenclador de medicamentos de Compras.
-- Devuelve todos los troqueles coincidentes, sin paginacion.
-- Conserva la comparacion por droga, la fecha y las bajas del buscador OSPIM.

BEGIN;

CREATE OR REPLACE FUNCTION farmacia.buscar_medicamentos_droga(
    droga_v character varying)
RETURNS TABLE(troquel numeric)
AS $BODY$
BEGIN
    return query
    select distinct m.troquel
    from public.medicamentos m
    where droga_v is not null
    and droga_v <> ''
    and translate(
        upper(m.droga),
        chr(193) || chr(201) || chr(205) || chr(211)
            || chr(218) || chr(220) || chr(209) || chr(192)
            || chr(200) || chr(204) || chr(210) || chr(217)
            || chr(196) || chr(203) || chr(207) || chr(214)
            || chr(220) || chr(194) || chr(202) || chr(206)
            || chr(212) || chr(219),
        'AEIOUUNAEIOUAEIOUAEIOU'
    ) like '%' || translate(
        upper(droga_v),
        chr(193) || chr(201) || chr(205) || chr(211)
            || chr(218) || chr(220) || chr(209) || chr(192)
            || chr(200) || chr(204) || chr(210) || chr(217)
            || chr(196) || chr(203) || chr(207) || chr(214)
            || chr(220) || chr(194) || chr(202) || chr(206)
            || chr(212) || chr(219),
        'AEIOUUNAEIOUAEIOUAEIOU'
    ) || '%'
    and m.fecha <= current_date
    and m.baja_fecha is null;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100
ROWS 1000;

COMMIT;
