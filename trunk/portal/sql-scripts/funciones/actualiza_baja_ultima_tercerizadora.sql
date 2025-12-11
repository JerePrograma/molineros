CREATE OR REPLACE FUNCTION actualiza_baja_ultima_tercerizadora(cuil_p character varying, fecha_fin_pres_p date, username character varying)
  RETURNS integer AS
$BODY$
begin
update afi_tercerizadora_servicio
set
fecha_fin_pres = fecha_fin_pres_p,
modi_fecha = current_date, 
modi_usr = username
where cuil_titular = cuil_p
and alta_fecha = (select max(a2.alta_fecha) from afi_tercerizadora_servicio a2 where a2.cuil_titular = cuil_p);

return 1;

end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;