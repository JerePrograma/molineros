CREATE OR REPLACE FUNCTION inserta_llamado(cuit_v character varying, fecha_llamado date, observaciones_v character varying, estado_v character varying, carta_doc_v character varying, ubicacion_carpeta_v character varying, molinera_v boolean, tipo_contacto_v character varying, username character varying)
  RETURNS integer AS
$BODY$
declare currentTimestamp timestamp without time zone;
begin

if fecha_llamado=current_date 
then 
currentTimestamp=current_timestamp;
else 
currentTimestamp=cast(fecha_llamado as timestamp without time zone);
end if;

INSERT INTO estudio_empresas_info(cuit,estado,fecha, carta_doc, ubicacion_carpeta, molinera) 
values(cuit_v, estado_v, currentTimestamp, carta_doc_v, ubicacion_carpeta_v, molinera_v);


INSERT INTO estudio_llamadas_empresas(cuit, fecha, observaciones, tipo_contacto, usuario)
values(cuit_v, currentTimestamp, observaciones_v, tipo_contacto_v, username);


return 0;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

