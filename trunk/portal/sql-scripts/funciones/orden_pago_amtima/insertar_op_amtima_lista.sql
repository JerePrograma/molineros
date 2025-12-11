CREATE OR REPLACE FUNCTION insertar_op_amtima_lista(p_op_amtima_id integer, p_lista_id integer, p_usr character varying)
  RETURNS integer AS
$BODY$
    declare ban integer;  
  begin
	
	  
     insert into orden_pago_amtima_lista_reintegros (id_orden_pago_amtima ,
	id_lista_reintegro_pago,
	alta_fecha,
    alta_usr,
    modi_fecha,
    modi_usr)
     values (p_op_amtima_id, p_lista_id, localtimestamp, p_usr, localtimestamp, p_usr );
 
  return  1;
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
