drop FUNCTION insertar_lista_reintegro_pago(p_usr character varying);


CREATE OR REPLACE FUNCTION insertar_lista_reintegro_pago(p_id_seccional integer, p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
  insert into lista_reintegro_pago( id_seccional,alta_fecha ,
    alta_usr ,
    modi_fecha ,
    modi_usr )
  
  values (p_id_seccional, localtimestamp, p_usr, localtimestamp, p_usr); 
   
  return currval('lista_reintegro_pago_id_seq');
  end;  
$BODY$;
--


