CREATE OR REPLACE FUNCTION trae_prestaciones_reintegro() 
RETURNS TABLE(id_prestacion integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_prestacion,descripcion from nomenclador 
where marca_rein_liq = 2 --solo reintegro
and baja_Fecha is null
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_prestaciones_reintegro() OWNER TO postgres;

--
