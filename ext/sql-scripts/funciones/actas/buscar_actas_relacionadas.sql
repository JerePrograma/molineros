drop  FUNCTION buscar_actas_relacionadas(p_id integer) ;

CREATE OR REPLACE FUNCTION buscar_actas_relacionadas(p_id integer) 
RETURNS TABLE(ar__acta_id integer,
 ar__acta_relacionada_id integer,
 ar__importe numeric,
 ar__saldo numeric,
 ar__alta_fecha timestamp without time zone,
 ar__alta_usr character varying,
 ar__alta_ip character varying,
 ar__modi_fecha timestamp without time zone,
 ar__modi_usr character varying,
 ar__modi_ip character varying,
 ar__baja_fecha timestamp without time zone,
 ar__baja_usr character varying,
 ar__baja_ip character varying,
 ar__id integer,
 a__acta_relacionada_nro character varying,
 a__acta_relacionada_fecha_pago timestamp without time zone)
    LANGUAGE sql
    AS $BODY$

select 	ar.acta_id,
  ar.acta_relacionada_id,
  ar.importe ,
  ar.saldo ,
  ar.alta_fecha,
  ar.alta_usr,
  ar.alta_ip ,
  ar.modi_fecha,
  ar.modi_usr,
  ar.modi_ip,
  ar.baja_fecha,
  ar.baja_usr,
  ar.baja_ip,
  ar.id,
  a2.numero,
  a2.fecha_pago
from acta_relacion ar
inner join acta a2
on ar.acta_relacionada_id = a2.id
where ($1 is null or ($1 is not null  and acta_id=$1))
$BODY$;


ALTER FUNCTION public.buscar_actas_relacionadas(p_id integer) OWNER TO postgres;

--
