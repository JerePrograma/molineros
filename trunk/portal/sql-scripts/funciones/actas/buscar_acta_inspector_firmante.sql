CREATE OR REPLACE FUNCTION buscar_acta_inspector_firmante(p_id integer) 
RETURNS TABLE(id integer,
 nombre character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying)
    LANGUAGE sql
    AS $BODY$
	select  i.id,
  i.nombre,
  i.alta_fecha,
  i.alta_usr,
  i.modi_fecha,
  i.modi_usr,
  i.baja_fecha,
  i.baja_usr
	from acta_inspector ai
	inner join inspector i
	on ai.id_acta = $1
	and ai.id_inspector = i.id
	
	$BODY$;


ALTER FUNCTION public.buscar_acta_inspector_firmante(p_id integer) OWNER TO postgres;

--
