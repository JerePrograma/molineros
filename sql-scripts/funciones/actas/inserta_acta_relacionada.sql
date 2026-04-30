CREATE OR REPLACE FUNCTION inserta_acta_relacionada(p_acta_id integer,
 p_acta_relacionada_id integer,
 p_importe numeric,
 p_saldo numeric,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare v_periodo date;
BEGIN
	
insert into acta_relacion (acta_id, acta_relacionada_id, importe, saldo, alta_fecha,alta_usr, modi_fecha, modi_usr) 
values (p_acta_id, p_acta_relacionada_id, p_importe, p_saldo, localtimestamp, p_usr, localtimestamp, p_usr);

insert into acta_pagos (acta_id, tipo, fecha_pago, importe, interes, alta_fecha ,
  alta_usr,  modi_fecha, modi_usr, acta_relacion_id, forma)
values (p_acta_relacionada_id, 'PGO', localtimestamp, p_importe, 0, 
localtimestamp, p_usr,localtimestamp, p_usr, currval('acta_relacion_id_seq'), 'E');

return currval('acta_relacion_id_seq');
END;
$BODY$;


ALTER FUNCTION public.inserta_acta_relacionada(p_acta_id integer, p_acta_relacionada_id integer, p_importe numeric, p_saldo numeric, p_usr character varying) OWNER TO postgres;

--
