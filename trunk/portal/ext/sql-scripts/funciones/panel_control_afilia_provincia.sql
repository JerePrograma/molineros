CREATE OR REPLACE FUNCTION panel_control_afilia_provincia(fecha_desde date, fecha_hasta date)
  RETURNS SETOF panel_control_result AS
$BODY$
declare periodo_viejo date;
declare periodo_nuevo date;
BEGIN


drop table if exists result;
create temp table result (orden int, periodo date, descripcion varchar, titulares bigint, beneficiarios bigint);

periodo_viejo=fecha_desde;
periodo_nuevo=fecha_desde+Interval '1 month';

WHILE periodo_nuevo<=fecha_hasta LOOP
	RAISE INFO 'PERIODO VIEJO: %',periodo_viejo;
	RAISE INFO 'PERIODO NUEVO: %',periodo_nuevo;

	drop table if exists aux_viejos;
	drop table if exists aux_nuevos;
	drop table if exists aux_bajas;
	drop table if exists aux_altas;

	--TOTAL BENEFICIARIOS VIGENTES A LA FECHA
	create temp table aux_viejos as 
	select periodo_viejo as periodo,*
	from afiliado a
	where (baja_fecha is null or baja_fecha>=periodo_viejo)
	--and (vigen_fecha<=fecha_desde or (vigen_fecha>=fecha_desde and vigen_fecha<=fecha_hasta))
	--LO HACEMOS POR APORTE
	and exists (select 1 from afi_aportes ap
		    where ap.cuil_titular=a.cuil_titular
		    and ap.inte=0
		    and (ap.fecha_egre is null or ap.fecha_egre>=periodo_viejo)
		    and (ap.baja_fecha is null or ap.baja_fecha>=periodo_viejo)
		    and ap.fecha_ingre<periodo_viejo
		    and id_aporte in (select id_aporte from aporte where es_os=true));
	
		    
	create temp table aux_nuevos as 
	select periodo_nuevo as periodo, *
	from afiliado a
	where (baja_fecha is null or baja_fecha>=periodo_nuevo)
	--and (vigen_fecha<=fecha_desde or (vigen_fecha>=fecha_desde and vigen_fecha<=fecha_hasta))
	--LO HACEMOS POR APORTE
	and exists (select 1 from afi_aportes ap
		    where ap.cuil_titular=a.cuil_titular
		    and ap.inte=0
		    and (ap.fecha_egre is null or ap.fecha_egre>=periodo_nuevo)
		    and (ap.baja_fecha is null or ap.baja_fecha>=periodo_nuevo)
		    and ap.fecha_ingre<periodo_nuevo
		    and id_aporte in (select id_aporte from aporte where es_os=true));
	
	alter table aux_viejos add provincia varchar;
	alter table aux_nuevos add provincia varchar;

	update aux_viejos av
	set provincia=p.detalle
	from afi_domicilio ats, provincia p
	where av.cuil_titular=ats.cuil_titular	
	and ats.inte=0	
	and p.id_provincia=ats.provincia
	and (ats.baja_fecha is null or ats.baja_fecha>=periodo_nuevo)
	and av.provincia is null;	
	
	
	update aux_nuevos av
	set provincia=p.detalle
	from afi_domicilio ats, provincia p
	where av.cuil_titular=ats.cuil_titular
	and ats.inte=0	
	and p.id_provincia=ats.provincia
	and (ats.baja_fecha is null or ats.baja_fecha>=periodo_nuevo)
	and av.provincia is null;	

	update aux_viejos av
	set provincia=p.detalle
	from afi_domicilio ats, provincia p
	where av.cuil_titular=ats.cuil_titular
	and ats.inte=0	
	and p.id_provincia=ats.provincia	
	and av.provincia is null;	
	
	update aux_nuevos av
	set provincia=p.detalle
	from afi_domicilio ats, provincia p
	where av.cuil_titular=ats.cuil_titular
	and ats.inte=0	
	and p.id_provincia=ats.provincia	
	and av.provincia is null;	

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                  
	select 1,
	       periodo,
	       case when provincia is null then 'SIN PROVINCIA' else provincia end||' - VIGENTES', 
	       case when id_parentesco_sss = 0 then count(*) end as titulares ,
	       case when id_parentesco_sss <> 0 then  count(*) end as beneficiarios 
	from aux_viejos av	
	group by periodo, id_parentesco_sss = 0, id_parentesco_sss <> 0; 

		
	create temp table aux_bajas AS
	select *
	from aux_viejos v
	where not exists (select 1 from aux_nuevos n
			  where n.cuil_titular=v.cuil_titular
			  and n.inte=v.inte);
	
	create temp table aux_altas AS
	select *
	from aux_nuevos v
	where not exists (select 1 from aux_viejos n
			  where n.cuil_titular=v.cuil_titular
			  and n.inte=v.inte);
			  

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                  
	select 3, 
	       periodo_viejo,
	       case when provincia is null then 'SIN PROVINCIA' else provincia end || ' - BAJAS'  , 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios 
	from aux_bajas v		
	group by periodo_viejo, provincia, id_parentesco_sss = 0, id_parentesco_sss <> 0;

	
		 
	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                    
	select 2,
	       periodo_viejo, 
	       case when provincia is null then 'SIN PROVINCIA' else provincia end|| ' - ALTAS', 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios
	from aux_altas v	
	group by periodo_viejo, provincia,id_parentesco_sss = 0, id_parentesco_sss <> 0;		  		
	
	periodo_viejo=periodo_nuevo;
	periodo_nuevo=periodo_viejo+interval '1 month';
END LOOP;

return query
select periodo, descripcion, max(titulares), max(beneficiarios) from result
group by periodo, descripcion, orden
order by periodo, orden, descripcion;



END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;