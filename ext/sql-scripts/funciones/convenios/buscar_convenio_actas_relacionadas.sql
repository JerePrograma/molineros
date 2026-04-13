CREATE OR REPLACE FUNCTION buscar_convenio_actas_relacionadas(p_convenioid integer) 
RETURNS TABLE(cr__convenio_id integer,
 cr__acta_id integer,
 cr__importe numeric,
 cr__saldo numeric,
 cr__alta_fecha timestamp without time zone,
 cr__alta_usr character varying,
 cr__alta_ip character varying,
 cr__modi_fecha timestamp without time zone,
 cr__modi_usr character varying,
 cr__modi_ip character varying,
 cr__baja_fecha timestamp without time zone,
 cr__baja_usr character varying,
 cr__baja_ip character varying,
 cr__id integer,
 a__acta_relacionada_nro character varying)
    LANGUAGE sql
    AS $BODY$

select 	cr.convenio_id,
  cr.acta_id,
  cr.importe ,
  cr.saldo ,
  cr.alta_fecha,
  cr.alta_usr,
  cr.alta_ip ,
  cr.modi_fecha,
  cr.modi_usr,
  cr.modi_ip,
  cr.baja_fecha,
  cr.baja_usr,
  cr.baja_ip,
  cr.id,
  a2.numero
from convenio_actas cr
inner join acta a2
on cr.acta_id = a2.id
where  convenio_id=$1 ;
$BODY$;


ALTER FUNCTION public.buscar_convenio_actas_relacionadas(p_id integer) OWNER TO postgres;

--
