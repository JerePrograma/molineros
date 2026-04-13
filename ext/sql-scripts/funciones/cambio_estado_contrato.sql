CREATE OR REPLACE FUNCTION cambio_estado_contrato(id_contrato_ integer, estado_ integer, username character varying)
  RETURNS integer AS  
$BODY$	

begin

    update contrato
    set estado = estado_,
    modi_fecha = localtimestamp,
    modi_usr = username
    where id_contrato=id_contrato_;    
    
    return 1;
    
end;    
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;