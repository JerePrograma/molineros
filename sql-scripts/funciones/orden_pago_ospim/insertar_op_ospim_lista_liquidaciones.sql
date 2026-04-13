 CREATE OR REPLACE FUNCTION insertar_op_ospim_lista_liquidaciones(p_op_ospim_id integer,
 p_liquidacion_id integer, p_usr character varying)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare ban integer;  
  begin
		  
     insert into orden_pago_ospim_liquidaciones (id_orden_pago_ospim,
	id_liquidacion,
	alta_fecha,
    alta_usr,
    modi_fecha,
    modi_usr)
     values (p_op_ospim_id, p_liquidacion_id, localtimestamp, p_usr, localtimestamp, p_usr );
 
  return  1;
  end;  
$BODY$;