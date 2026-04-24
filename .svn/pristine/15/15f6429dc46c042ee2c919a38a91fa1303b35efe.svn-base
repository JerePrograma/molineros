CREATE OR REPLACE FUNCTION trae_plan_afiliado_x_aportes(cuilv character varying,
 intev integer) 
RETURNS TABLE(id_plan integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
	select id_plana,pl.descripcion
	--from (select inte, count(*) as suma from afi_aportes where cuil_titular=$1 and baja_fecha is null group by inte) as sumaDeAportesAfiliado,
	from (select inte, count(distinct id_aporte) as suma from afi_aportes where cuil_titular=$1 and (baja_fecha is null or baja_fecha > current_timestamp)
	group by inte) as sumaDeAportesAfiliado,
	--(select id_plan as id_plana, inte,  count(*) as suma 
	(select p.id_plan as id_plana, inte,  count(distinct a.id_aporte) as suma 
		from plan_aporte p,afi_aportes a 
		where p.id_aporte=a.id_aporte 
		and a.cuil_titular=$1 	
		and (a.baja_fecha is null or baja_fecha > current_timestamp)
		and (a.fecha_egre is null or a.fecha_egre > current_timestamp)
		group by p.id_plan, inte) as sumaCoincidenciasPlanAporte,
	(select x.id_plan as id_planb, count(*) as suma 
		from plan_aporte x 
		group by x.id_plan) as sumaAportesPlan,
	plan pl
	where sumaDeAportesAfiliado.suma=sumaCoincidenciasPlanAporte.suma
	and sumaCoincidenciasPlanAporte.inte=sumaDeAportesAfiliado.inte
	and sumaAportesPlan.id_planb=sumaCoincidenciasPlanAporte.id_plana
	and sumaAportesPlan.suma=sumaCoincidenciasPlanAporte.suma
	and pl.id_plan=id_plana
	order by pl.peso desc
	$BODY$;


ALTER FUNCTION public.trae_plan_afiliado_x_aportes(cuilv character varying, intev integer) OWNER TO postgres;

--
