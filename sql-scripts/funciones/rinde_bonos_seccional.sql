CREATE OR REPLACE FUNCTION rinde_bonos_seccional(tipo_bono_v integer, seccional_v integer, fecha_rendicion_v date, nro_bono_desde integer, nro_bono_hasta integer, username character varying)
  RETURNS integer AS
$BODY$
begin

update bonos_seccional
set fecha_rendido=fecha_rendicion_v, rendicion_usr=username
where tipo_bono=tipo_bono_v
and id_seccional=seccional_v
and nro_bono>=nro_bono_desde
and nro_bono<=nro_bono_hasta
and fecha_rendido is null;

return 1;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE