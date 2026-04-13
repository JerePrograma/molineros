CREATE OR REPLACE FUNCTION insertar_lista_reintegro_farmacia_reporte(p_id_seccional integer,p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
  insert into lista_reintegro_farmacia_reporte( id_seccional,alta_fecha ,
    alta_usr ,
    modi_fecha ,
    modi_usr )
  
  values (p_id_seccional, localtimestamp, p_usr, localtimestamp, p_usr);    
  return currval('lista_reintegro_farmacia_reporte_id_seq');
  end;  
$BODY$;
--
