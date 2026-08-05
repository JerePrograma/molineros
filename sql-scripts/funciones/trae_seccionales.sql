CREATE OR REPLACE FUNCTION trae_seccionales() 
RETURNS TABLE(id_seccional integer,
 descripcion character varying,
 cheque_a_la_orden character varying,
 tipo character varying,
 id_domicilio integer,
 contacto character varying,
 observaciones character varying,
 vigen_fecha timestamp without time zone,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 alta_ip character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 modi_ip character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying,
 baja_ip character varying)
    LANGUAGE sql
    AS $BODY$
select id_seccional ,
    descripcion ,
    cheque_a_la_orden ,
    tipo ,
    id_domicilio ,
    contacto ,
    observaciones ,
    vigen_fecha ,
    alta_fecha ,
    alta_usr ,
    alta_ip ,
    modi_fecha,
    modi_usr ,
    modi_ip ,
    baja_fecha ,
    baja_usr ,
    baja_ip 
    from seccional 
    where imaginaria is null or imaginaria = 0
    order by descripcion
$BODY$;


ALTER FUNCTION public.trae_seccionales() OWNER TO postgres;
