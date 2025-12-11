create type trae_farmacias_result as(id_farmacia integer, camara character varying, farmacia character varying, cuit character varying, codigo character varying, calle character varying, telefono character varying, cod_farm character varying, sucursal character varying, porc_descuento numeric, cheque_a_nombre_de varchar)
 
CREATE OR REPLACE FUNCTION trae_farmacias()
  RETURNS SETOF trae_farmacias_result AS
$BODY$
BEGIN
drop table if exists aux;

create temp table aux as
    select 
      id_farmacia,		
      camara,
	  farmacia,
	  cuit,
	  codigo,
	  calle,
	  telefono,
	  cod_farm,
	  sucursal,
	  porc_descuento,
	  cast(null as varchar) as a_nombre_de
    from farmacia c;

update aux x
set a_nombre_de=ch.a_nombre_de
from orden_pago_ospim o, orden_pago_ospim_pagos op, cheque ch
where o.cuit_acreedor =x.cuit
and o.id_orden_pago=(select max(id_orden_pago) from orden_pago_ospim o2 where o2.cuit_acreedor=o.cuit_acreedor and baja_fecha is null)
and o.id_orden_pago=op.id_orden_pago
and ch.nro_cheque=op.nro_cheque;

return query 
select 
      id_farmacia,		
      camara,
	  farmacia,
	  cuit,
	  codigo,
	  calle,
	  telefono,
	  cod_farm,
	  sucursal,
	  porc_descuento,
	  a_nombre_de
from aux
order by farmacia;	  

    
    
END;
$BODY$

  LANGUAGE plpgsql VOLATILE
