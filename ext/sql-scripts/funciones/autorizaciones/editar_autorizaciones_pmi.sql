CREATE OR REPLACE FUNCTION autorizaciones.alta_autorizaciones_pmi(tipo_receta_p character varying, fecha_p date, cuil_titular_p character varying, inte_p integer, observaciones_p character varying, alta_usr_p character varying)
  RETURNS SETOF autorizaciones.autorizaciones_pmi_type AS
$BODY$
declare nro_auto_p int;
declare cont int;
BEGIN
nro_auto_p=nextval('autorizaciones.autorizacion_pmi_id_seq'::regclass);
cont=0;
LOOP
	INSERT INTO autorizaciones.autorizaciones_pmi(tipo_receta,fecha,cuil_titular,inte,alta_fecha ,alta_usr, id_autorizacion_pmi, observaciones) VALUES
	(tipo_receta_p,fecha_p,cuil_titular_p,inte_p,LOCALTIMESTAMP ,alta_usr_p ,nro_auto_p, observaciones_p);
	cont=cont+1;
EXIT WHEN cont>3;
END LOOP;
return query
select id_autorizacion_pmi, tipo_receta, fecha, cuil_titular, inte, alta_fecha, alta_usr, nro_receta, observaciones
from autorizaciones.autorizaciones_pmi
where id_autorizacion_pmi=nro_auto_p;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION autorizaciones.alta_autorizaciones_pmi(character varying, date, character varying, integer, character varying, character varying)
  OWNER TO postgres;