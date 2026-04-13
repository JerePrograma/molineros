-- Function: insertar_op_ospim_lista(integer, integer, character varying)

-- DROP FUNCTION insertar_op_ospim_lista(integer, integer, character varying);

CREATE OR REPLACE FUNCTION insertar_op_ospim_lista(p_op_ospim_id integer, p_lista_id integer, p_usr character varying)
  RETURNS integer AS
$BODY$
    declare ban integer;  
  begin
	
	  
     insert into orden_pago_ospim_lista_reintegros (id_orden_pago_ospim ,
	id_lista_reintegro_pago,
	alta_fecha,
    alta_usr,
    modi_fecha,
    modi_usr)
     values (p_op_ospim_id, p_lista_id, localtimestamp, p_usr, localtimestamp, p_usr );
 
  return  1;
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insertar_op_ospim_lista(integer, integer, character varying) OWNER TO postgres;
