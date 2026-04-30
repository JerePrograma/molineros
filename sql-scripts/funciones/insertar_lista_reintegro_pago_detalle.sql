drop FUNCTION insertar_lista_reintegro_pago_detalle(p_id_lista integer, p_id_reinte integer, p_importe numeric(10,2))

--

CREATE OR REPLACE FUNCTION insertar_lista_reintegro_pago_detalle(p_id_lista integer, p_id_reinte integer, p_importe numeric(10,2), p_tipo character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
  insert into lista_reintegro_pago_detalle(id_lista_reintegro_pago ,
	id_reintegro , importe, tipo_reintegro)
  
  values (p_id_lista, p_id_reinte,p_importe,p_tipo); 
   
  return 1;
  end;  
$BODY$;
--
