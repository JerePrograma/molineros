CREATE OR REPLACE FUNCTION trae_ultima_tercerizadora_afi(cuil_titular_p character varying)
  RETURNS TABLE(id_tercerizadora character varying,
 descripcion character varying,
 fecha_ingreso date,
 fecha_egreso date) AS
$BODY$
BEGIN

return query
select ats.id_tercerizadora, ts.descripcion, ats.fecha_inicio_pres, ats.fecha_fin_pres
from afi_tercerizadora_servicio ats 
inner join tercerizadora_servicio ts on ats.id_tercerizadora = ts.id_tercerizadora, 
(select cast(cuil_titular_p as varchar) as cuil, (select 1  from afi_tercerizadora_servicio ats_ 
				     where ats_.cuil_titular=cuil_titular_p
				     and ats_.baja_fecha is null 
				     and ats_.fecha_fin_pres is null) as existe) b
where ats.cuil_titular=cuil_titular_p
and ats.cuil_titular=b.cuil
and ((existe=1 and ats.fecha_fin_pres is null)  
     or (existe is null and ats.fecha_fin_pres=(select max(ats__.fecha_fin_pres) 
						from afi_tercerizadora_servicio ats__ 
						where ats__.cuil_titular=cuil_titular_p
						and ats__.baja_fecha is null)));

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;

