CREATE OR REPLACE FUNCTION actualiza_organismo(id_organismo_p integer, nombre_p character varying, ambito_p character varying, telefono_p character varying, web_p character varying, observaciones_p character varying, sigla_p character varying, orbita_p character varying, p_user character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN

update organismo
set denominacion=nombre_p,
ambito=ambito_p,
telefono=telefono_p,
web=web_p,
observaciones=observaciones_p,
sigla=sigla_p,
orbita=orbita_p,
modi_usr=p_user,
modi_fecha=current_timestamp
where id_organismo=id_organismo_p;

return id_organismo_p;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
