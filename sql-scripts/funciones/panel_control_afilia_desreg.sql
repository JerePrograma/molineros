CREATE OR REPLACE FUNCTION panel_control_afilia_desreg(fecha_desde date, fecha_hasta date)
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
	
	alter table aux_viejos add tipo varchar;
	alter table aux_nuevos add tipo varchar;
	
	update aux_viejos av
	set tipo='MOLINEROS'
	from afi_tercerizadora_servicio ats
	where av.cuil_titular=ats.cuil_titular
	and ats.inte=0
	and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>=periodo_nuevo)
	and (ats.baja_fecha is null or ats.baja_fecha>=periodo_nuevo)
	and (ats.id_tercerizadora='CSA' or ats.id_tercerizadora='USU')
	and tipo is null;	
	
	update aux_viejos av
	set tipo='DESREGULADOS'
	from afi_tercerizadora_servicio ats
	where av.cuil_titular=ats.cuil_titular
	and ats.inte=0
	and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>=periodo_nuevo)
	and (ats.baja_fecha is null or ats.baja_fecha>=periodo_nuevo)
	and ats.id_tercerizadora<>'CSA' and ats.id_tercerizadora<>'USU'
	/*and (exists (select 1 from afi_opciones_sss ao where ao.cuil=av.cuil_titular)	
	     or  not exists (select 1 from afi_estados_histo aeh
			     where aeh.cuil_titular=av.cuil_titular
			     and descripcion_operacion='REI'))*/
	and tipo is null;	
	
	update aux_viejos av
	set tipo='DESREGULADOS'
	where exists (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and ats.id_tercerizadora<>'CSA' and ats.id_tercerizadora<>'USU')
	/*and (exists (select 1 from afi_opciones_sss ao where ao.cuil=av.cuil_titular)	
	     or  not exists (select 1 from afi_estados_histo aeh
			     where aeh.cuil_titular=av.cuil_titular
			     and descripcion_operacion='REI'))	      */
	AND NOT EXISTS (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and (ats.id_tercerizadora='CSA' or ats.id_tercerizadora='USU')) 		
	and tipo is null;	
	
	update aux_viejos av
	set tipo='MOLINEROS'
	where exists (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and (ats.id_tercerizadora='CSA' or ats.id_tercerizadora='USU'))
        AND NOT EXISTS (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and ats.id_tercerizadora<>'CSA' and ats.id_tercerizadora<>'USU') 				      
	and tipo is null;	

	update aux_viejos av
	set tipo='MOLINEROS'
	where exists (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and (ats.id_tercerizadora='CSA' or ats.id_tercerizadora='USU')
		      and fecha_fin_pres=(select max(fecha_fin_pres) from afi_tercerizadora_servicio ats2
					  where ats2.cuil_titular=ats.cuil_titular
					  and ats2.inte=ats.inte
					  and ats2.baja_fecha is null or ats2.baja_fecha>periodo_nuevo))        
	and tipo is null;	

	update aux_viejos av
	set tipo='DESREGULADOS'
	where exists (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and (ats.id_tercerizadora<>'CSA' or ats.id_tercerizadora<>'USU')
		      and fecha_fin_pres=(select max(fecha_fin_pres) from afi_tercerizadora_servicio ats2
					  where ats2.cuil_titular=ats.cuil_titular
					  and ats2.inte=ats.inte
					  and ats2.baja_fecha is null or ats2.baja_fecha>periodo_nuevo))        
	and tipo is null;	
	

	update aux_nuevos av
	set tipo='MOLINEROS'
	from afi_tercerizadora_servicio ats
	where av.cuil_titular=ats.cuil_titular
	and ats.inte=0
	and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>=periodo_nuevo)
	and (ats.baja_fecha is null or ats.baja_fecha>=periodo_nuevo)
	and (ats.id_tercerizadora='CSA' or ats.id_tercerizadora='USU');	
	
	update aux_nuevos av
	set tipo='DESREGULADOS'
	from afi_tercerizadora_servicio ats
	where av.cuil_titular=ats.cuil_titular
	/*and (exists (select 1 from afi_opciones_sss ao where ao.cuil=av.cuil_titular)	
	     or  not exists (select 1 from afi_estados_histo aeh
			     where aeh.cuil_titular=av.cuil_titular
			     and descripcion_operacion='REI'))*/
	and ats.inte=0
	and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>=periodo_nuevo)
	and (ats.baja_fecha is null or ats.baja_fecha>=periodo_nuevo)
	and ats.id_tercerizadora<>'CSA' and ats.id_tercerizadora<>'USU';	
	
	update aux_nuevos av
	set tipo='DESREGULADOS'
	where exists (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and ats.id_tercerizadora<>'CSA' and ats.id_tercerizadora<>'USU')
	/*and (exists (select 1 from afi_opciones_sss ao where ao.cuil=av.cuil_titular)	
	     or  not exists (select 1 from afi_estados_histo aeh
			     where aeh.cuil_titular=av.cuil_titular
			     and descripcion_operacion='REI'))*/
	AND NOT EXISTS (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and (ats.id_tercerizadora='CSA' or ats.id_tercerizadora='USU')) 	
	and tipo is null;	
	
	update aux_nuevos av
	set tipo='MOLINEROS'
	where exists (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and (ats.id_tercerizadora='CSA' or ats.id_tercerizadora='USU'))
	AND NOT EXISTS (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and ats.id_tercerizadora<>'CSA' and ats.id_tercerizadora<>'USU') 	
	and tipo is null;	

	update aux_nuevos av
	set tipo='MOLINEROS'
	where exists (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and (ats.id_tercerizadora='CSA' or ats.id_tercerizadora='USU')
		      and fecha_fin_pres=(select max(fecha_fin_pres) from afi_tercerizadora_servicio ats2
					  where ats2.cuil_titular=ats.cuil_titular
					  and ats2.inte=ats.inte
					  and ats2.baja_fecha is null or ats2.baja_fecha>periodo_nuevo))        
	and tipo is null;	

	update aux_nuevos av
	set tipo='DESREGULADOS'
	where exists (select 1 from afi_tercerizadora_servicio ats
		      where ats.cuil_titular=av.cuil_titular
		      and ats.inte=0
		      and (ats.id_tercerizadora<>'CSA' or ats.id_tercerizadora<>'USU')
		      and fecha_fin_pres=(select max(fecha_fin_pres) from afi_tercerizadora_servicio ats2
					  where ats2.cuil_titular=ats.cuil_titular
					  and ats2.inte=ats.inte
					  and ats2.baja_fecha is null or ats2.baja_fecha>periodo_nuevo))
	and tipo is null;  
	
	update aux_nuevos av
	set tipo='EXTRAÑO'
	where tipo is null;	

	update aux_viejos av
	set tipo='EXTRAÑO'
	where tipo is null;	

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                  
	select 1,
	       periodo,
	       cast('VIGENTES MOLINEROS'as varchar) as descripcion, 
	       case when id_parentesco_sss = 0 then count(*) end as titulares ,
	       case when id_parentesco_sss <> 0 then  count(*) end as beneficiarios 
	from aux_viejos av
	where av.tipo='MOLINEROS'
	group by periodo, id_parentesco_sss = 0, id_parentesco_sss <> 0; 

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                  
	select 1,
	       periodo,
	       cast('VIGENTES DESREGULADOS'as varchar) as descripcion, 
	       case when id_parentesco_sss = 0 then count(*) end as titulares ,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios 
	from aux_viejos av
	where av.tipo='DESREGULADOS'
	group by periodo, id_parentesco_sss = 0, id_parentesco_sss <> 0;        

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                  
	select 1,
	       periodo,
	       cast('VIGENTES SIN TERCERIZADORA'as varchar) as descripcion, 
	       case when id_parentesco_sss = 0 then count(*) end as titulares ,
	       case when id_parentesco_sss <> 0 then  count(*) end as beneficiarios 
	from aux_viejos av
	where av.tipo='EXTRAÑO'
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
	       cast('BAJAS MOLINERAS' as varchar) as descripcion, 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios 
	from aux_bajas v	
	where tipo='MOLINEROS'
	group by periodo_viejo, id_parentesco_sss = 0, id_parentesco_sss <> 0;

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                  
	select 3, 
	       periodo_viejo,
	       cast('BAJAS DESREGULADOS' as varchar) as descripcion, 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios 
	from aux_bajas v	
	where tipo='DESREGULADOS'
	group by periodo_viejo, id_parentesco_sss = 0, id_parentesco_sss <> 0;

        insert into result(orden, periodo, descripcion, titulares, beneficiarios)                  
	select 3, 
	       periodo_viejo,
	       cast('BAJAS SIN TERCERIZADORA' as varchar) as descripcion, 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios 
	from aux_bajas v	
	where tipo='EXTRAÑO'
	group by periodo_viejo, id_parentesco_sss = 0, id_parentesco_sss <> 0;		 
		 
	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                    
	select 2,
	       periodo_viejo, 'ALTAS MOLINERAS', 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios
	from aux_altas v
	where tipo='MOLINEROS'
	group by periodo_viejo, id_parentesco_sss = 0, id_parentesco_sss <> 0;		  

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                    
	select 2,
	       periodo_viejo, 'ALTAS DESREGULADOS', 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios
	from aux_altas v
	where tipo='DESREGULADOS'
	and (exists (select 1 from afi_opciones_sss ao where ao.cuil=v.cuil_titular)	
	     or  not exists (select 1 from afi_estados_histo aeh
			     where aeh.cuil_titular=v.cuil_titular
			     and descripcion_operacion='REI'))
	group by periodo_viejo, id_parentesco_sss = 0, id_parentesco_sss <> 0;

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                    
	select 2,
	       periodo_viejo, 'REINCORPORACIONES DESREGULADOS', 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios
	from aux_altas v
	where tipo='DESREGULADOS'
	and ( not exists (select 1 from afi_opciones_sss ao where ao.cuil=v.cuil_titular)	
	     and  exists (select 1 from afi_estados_histo aeh
			     where aeh.cuil_titular=v.cuil_titular
			     and descripcion_operacion='REI'))
	group by periodo_viejo, id_parentesco_sss = 0, id_parentesco_sss <> 0;			  

	insert into result(orden, periodo, descripcion, titulares, beneficiarios)                    
	select 2,
	       periodo_viejo, 'ALTAS SIN TERCERIZADORA', 
	       case when id_parentesco_sss = 0 then count(*) end as titulares,
	       case when id_parentesco_sss <> 0 then count(*) end as beneficiarios
	from aux_altas v
	where tipo='EXTRAÑO'
	group by periodo_viejo, id_parentesco_sss = 0, id_parentesco_sss <> 0;		  
	
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