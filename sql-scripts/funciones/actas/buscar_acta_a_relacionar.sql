CREATE OR REPLACE FUNCTION buscar_datos_acta_a_relacionar(p_numero varchar) 
RETURNS TABLE(
  id integer,
  acta_numero character varying(8),
  tipo character(3),
  importe numeric(10,2))
    LANGUAGE sql
    AS $BODY$


select a.id, a.numero, ap.tipo, sum(ap.importe + ap. interes) from acta a
inner join acta_pagos ap
on  a.id = ap.acta_id
where a.numero=$1
group by a.id, a.numero, ap.tipo


$BODY$;


ALTER FUNCTION buscar_datos_acta_a_relacionar(p_numero varchar)  OWNER TO postgres;

--
