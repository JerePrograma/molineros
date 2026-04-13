CREATE OR REPLACE FUNCTION novedades_sss.buscar_novedades_empleadores_altas(IN periodo_desde_p date, IN periodo_hasta_p date)
  RETURNS TABLE(plan_actual_desc character varying, plan_actual_id integer, cuil_titular character varying, inte integer, 
		apellido character varying, nombre character varying, periodo timestamp without time zone, importeaportesocialuoma numeric, 
		importearticulo46 numeric, importecuotaamtima numeric, importecuotasocialuoma numeric, importecuotausufructo numeric, 
		importeadherenteamtima numeric, empresa_cuit character varying, ospim boolean, uoma boolean, amtimaadherente boolean, 
		amtimacuota boolean, usufructo boolean, plan_que_corresponde_desc character varying, plan_que_corresponde_id integer,
		total_registros_v integer) AS
$BODY$
declare total_registros_v int;
BEGIN

drop table if exists novedad_empleador_result;
     
create temp table novedad_empleador_result as 
	select cast('SIN PLAN' as character varying) as plan_actual_desc, 
	       cast(0 as integer) as plan_actual_id,
	       cast(portal_emp.afiliado_cuil_titular as character varying), 
	       portal_emp.afiliado_inte,
	       portal_emp.apellido,
	       portal_emp.nombre,
	       portal_emp.periodo,
	       portal_emp.importeaportesocialuoma,
	       portal_emp.importearticulo46, 
	       portal_emp.importecuotaamtima, 
	       portal_emp.importecuotasocialuoma, 
	       portal_emp.importecuotausufructo, 
	       portal_emp.importeadherenteamtima,
	       portal_emp.empresa_cuit,
	       cast(false as boolean) as ospim,
	       case when portal_emp.importeaportesocialuoma > 0 then cast(true as boolean) else cast(false as boolean) end as uoma,
	       case when portal_emp.importeadherenteamtima > 0 then cast(true as boolean) else cast(false as boolean) end as amtimaadherente,
	       case when portal_emp.importecuotaamtima > 0 then cast(true as boolean) else cast(false as boolean) end as amtimacuota, 
	       case when portal_emp.importecuotausufructo > 0 then cast(true as boolean) else cast(false as boolean) end as usufructo,
	       cast(null as character varying) as plan_que_corresponde_desc,
	       cast(null as integer) as plan_que_corresponde_id 
	from DBLINK('dbname=portalempresas port=5432 host=12.1.1.5 
			user=postgres password=barracud5',
                'select 
		 afiliado_cuil_titular,
		 afiliado_inte,
		 apellido,
		 nombre,
		 periodo, 
		 importeaportesocialuoma,
		 importearticulo46,
		 importecuotaamtima,
		 importecuotasocialuoma,
		 importecuotausufructo,
		 importeadherenteamtima,
		 empresa_cuit
		 from detalledeclaracionjurada dd
		 inner join declaracionjurada d 
		 on dd.declaracionjurada_id=d.id
		 inner join afiliado af
		 on dd.afiliado_cuil_titular = af.cuil_titular and dd.afiliado_inte = af.inte
		 where cast(d.periodo as date)>= cast('''||periodo_desde_p||''' as date) 
		   and cast(d.periodo as date)<= cast('''||periodo_hasta_p||''' as date)
 		   and d.numerosecuencia=(select max(dj2.numerosecuencia)
					  from declaracionjurada dj2 
					  where dj2.periodo=d.periodo and dj2.cerrada=true
					  and dj2.empresa_cuit=d.empresa_cuit)
		 order by afiliado_cuil_titular') AS portal_emp(afiliado_cuil_titular text, afiliado_inte int,
		 apellido character varying, nombre character varying, 
		 periodo timestamp without time zone, importeaportesocialuoma numeric,
		 importearticulo46 numeric, importecuotaamtima numeric, 
		 importecuotasocialuoma numeric, importecuotausufructo numeric, 
		 importeadherenteamtima numeric, empresa_cuit character varying) 
where not exists (select 1 from afiliado a where a.cuil_titular = portal_emp.afiliado_cuil_titular)
/*group by cast(portal_emp.afiliado_cuil_titular as character varying), 
	       portal_emp.afiliado_inte, portal_emp.apellido, portal_emp.nombre*/ ;	

/* Necesito actualizar si tiene aporte ospim */
update novedad_empleador_result r
set ospim='true'
from detalle_declaracion_jurada_2011 dj
where r.empresa_cuit=dj.cuit
and r.afiliado_cuil_titular=dj.cuil
and r.periodo=dj.periodo
and secobligacion=(select max(secobligacion) 
		   from detalle_declaracion_jurada_2011 djj 
		   where djj.cuit=dj.cuit
		   and djj.cuil=dj.cuil
		   and djj.periodo=dj.periodo);

/* Ahora evaluamos por cada aporte cual sería el plan correspondiente */
--INTEGRAL
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 1 
and r.ospim='true'
and r.uoma='true'
and r.amtimacuota='true'
and r.amtimaadherente='false';
--and usufructo='true'--lo sacamos porque esta dentro de los casos para revisar

--TOTAL
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 2 
and r.uoma='true'
and r.ospim='true'
and r.amtimacuota='false'
and r.amtimaadherente='false';
--and usufructo='true'--lo sacamos porque esta dentro de los casos para revisar (casos en que los afiliados tienen dos empresas)
--COBERTURA
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 3
and r.uoma='false'
and r.ospim='true'
and r.amtimacuota='false'
and r.amtimaadherente='false'
and r.usufructo='false'; 

--OSPIM_AMTIMA ADH
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 5
and r.uoma='false'
and r.ospim='true'
and r.amtimacuota='false'
and r.amtimaadherente='true'
and r.usufructo='false'; 

--SINDICATO
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 6 
and r.uoma='true'
and r.ospim='false'
and r.amtimacuota='false'
and r.amtimaadherente='false';
--and r.usufructo='true' 

--AMTIMA ACTIVO
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 7 
and r.uoma='false'
and r.ospim='false'
and r.amtimacuota='true'
and r.amtimaadherente='false';
--and r.usufructo='true'
 
--AMTIMA-SINDICATO
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 8 
and r.uoma='true'
and r.ospim='false'
and r.amtimacuota='true'
and r.amtimaadherente='false';
--and r.usufructo='true' 

--USUFRUCTO
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 18
and r.uoma='false'
and r.ospim='false'
and r.amtimacuota='false'
and r.amtimaadherente='false'
and r.usufructo='true'; 

--COBERTURA-USUFRUCTO
update novedad_empleador_result r
set plan_que_corresponde_id= p.id_plan, 
    plan_que_corresponde_desc = p.descripcion
from plan p
where p.id_plan = 19 
and r.uoma='false'
and r.ospim='true'
and r.amtimacuota='false'
and r.amtimaadherente='false'
and r.usufructo='true'; 

--SIN APORTES --ARTICULO 46

update novedad_empleador_result r
set plan_que_corresponde_id= 0, 
    plan_que_corresponde_desc = 'SIN APORTES'
where r.uoma='false'
and r.ospim='false'
and r.amtimacuota='false'
and r.amtimaadherente='false'
and r.usufructo='false';

total_registros_v = count(*) from novedad_empleador_result;

return query select r.* , total_registros_v
	     from novedad_empleador_result r
             limit 50;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;