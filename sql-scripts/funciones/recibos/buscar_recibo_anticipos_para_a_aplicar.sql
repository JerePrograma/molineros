drop function buscar_recibo_anticipos_para_a_aplicar (p_cuit character varying, p_sucu character varying);

create or replace function buscar_recibo_anticipos_para_a_aplicar (p_cuit character varying, p_sucu character varying)
returns table(
id integer,
recibo_id integer,
numero character varying,
importe numeric,
recibo_fecha date)
 LANGUAGE sql
    AS $BODY$


select rc.id, r.id, r.numero, rc.concepto_importe_adicional, r.fecha
from recibo_conceptos rc
inner join recibo r
on rc.recibo_id = r.id
where rc.caja_concepto_id = 122
and r.cuit = $1
and r.sucursal =  $2
and not exists (select 1 from recibo_ingresos where id_anticipo_recibo_concepto = rc.id);


$BODY$;

