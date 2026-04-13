CREATE OR REPLACE FUNCTION correo.insertar_paquete(p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
  insert into correo.paquete(alta_fecha ,
    alta_usr ,
    modi_fecha ,
    modi_usr, estado)
  
  values (localtimestamp, p_usr, localtimestamp, p_usr, 'ENVIADO'); 
   
  return currval('correo.paquete_id_seq');
  end;  
$BODY$;



