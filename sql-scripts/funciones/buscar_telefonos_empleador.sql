CREATE OR REPLACE FUNCTION buscar_telefonos_empleador(cuit character,
 sucur character varying) 
RETURNS TABLE(id_telefono integer,
 tipo_tele character varying,
 vigen_desde timestamp without time zone,
 codigo_pais character varying,
 codigo_area character varying,
 numero character varying,
 extension character varying,
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
	tel.id_telefono,
	tel.tipo_tele,
	tel.vigen_desde,
	tel.codigo_pais,
	tel.codigo_area,
	tel.numero,
	tel.extension,
	tel.observaciones,
	tel.alta_fecha,
	tel.alta_usr,
	tel.modi_fecha,
	tel.modi_usr,
	tel.baja_fecha,
	tel.baja_usr
	from emp_telefono etel
	inner join telefono tel
	on etel.id_telefono = tel.id_telefono
	where  etel.cuit = $1
		and etel.sucursal = $2
	and etel.vigen_desde <= LOCALTIMESTAMP 
	and tel.baja_fecha is  null
	order by tel.id_telefono asc
	
	$BODY$;


ALTER FUNCTION public.buscar_telefonos_empleador(cuit character, sucur character varying) OWNER TO postgres;

--
