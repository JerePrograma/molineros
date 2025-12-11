CREATE OR REPLACE FUNCTION trae_tipo_reintegro_lista_reporte(id_lista integer, id_reintegro integer) 
RETURNS TABLE(tipo_reintegro character varying)
    LANGUAGE sql
    AS $BODY$

select tipo_reintegro as tipo_reintegro from lista_reintegro_reporte_detalle  where id_lista_reintegro_reporte = $1 and id_reintegro = $2;

$BODY$;