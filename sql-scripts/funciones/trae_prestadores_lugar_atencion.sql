CREATE OR REPLACE FUNCTION trae_prestadores_lugar_atencion() 
RETURNS TABLE(id_prestador integer,
 cuit character varying,
 descripcion character varying,
 id_domicilio integer)
    LANGUAGE sql
    AS $BODY$
select p.id_prestador,p.cuit,p.descripcion,pla.id_domicilio from prestador p, prestad_lugar_atencion pla
where p.id_prestador = pla.id_prestador and (pla.baja_fecha is null or pla.baja_fecha > current_timestamp)
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_prestadores_lugar_atencion() OWNER TO postgres;

--
