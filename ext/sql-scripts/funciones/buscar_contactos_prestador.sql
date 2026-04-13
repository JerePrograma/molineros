CREATE OR REPLACE FUNCTION buscar_contactos_prestador(id_prestador integer) 
RETURNS TABLE(id_contacto_e integer,
 tipo_contacto_e character varying,
 vigen_desde timestamp without time zone,
 contacto character varying,
 observaciones character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying)
    LANGUAGE sql
    AS $BODY$
	select 
	cont.id_contacto_e,
  cont.tipo_contacto_e,
  cont.vigen_desde,
  cont.contacto,
  cont.observaciones,
  cont.alta_fecha,
  cont.alta_usr,
  cont.modi_fecha,
  cont.modi_usr,
  cont.baja_fecha,
  cont.baja_usr  
	from  prestad_contacto_e econt
	inner join contacto_e cont
	on econt.id_contacto_e = cont.id_contacto_e
	where econt.vigen_desde <= LOCALTIMESTAMP 
	and cont.baja_fecha is  null
	and econt.id_prestador  = $1
	order by id_contacto_e asc
	$BODY$;


ALTER FUNCTION public.buscar_contactos_prestador(id_prestador integer) OWNER TO postgres;

--
