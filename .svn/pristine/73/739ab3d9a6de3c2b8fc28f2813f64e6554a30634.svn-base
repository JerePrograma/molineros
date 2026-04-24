CREATE OR REPLACE FUNCTION listado_super_altas_bajas(fecha_desde date, alta_baja integer, 
registrar_envio boolean)
  RETURNS SETOF listado_super_altas2 AS
$BODY$
declare periodo_viejo date;
declare periodo_nuevo date;
declare fecha_cierre_v date;
BEGIN



drop table if exists result;
create temp table result (ope varchar, obra_social varchar,
	cuit varchar,
	cuil_titular varchar,	
	parentesco varchar,
	cuil varchar,
	documento_tipo varchar,
	docu_numero varchar,
	ape_nombre varchar,
	sexo varchar,
	estado_civil varchar,
	naci_fecha varchar,
        nacionalidad varchar,
        calle varchar,
        numero varchar,
        piso varchar,
        depto varchar,
        localidad varchar,
        postal_codi varchar,
        provincia varchar,
        tipo_domi varchar,
        telefono varchar,
        situ_revista varchar,
        discapacitado varchar,
        tipo_beneficiario varchar,
        fecha_alta_os varchar,        
        fecha_cierre varchar,
        aportante_titular integer,
        inte integer,
        cuil_verdadero varchar);

periodo_viejo=fecha_desde;
periodo_nuevo=fecha_desde+Interval '1 month';
fecha_cierre_v=fecha_desde+Interval '1 month'-Interval '1 day';
--WHILE periodo_nuevo<=fecha_hasta LOOP
	RAISE INFO 'PERIODO VIEJO: %',periodo_viejo;
	RAISE INFO 'PERIODO NUEVO: %',periodo_nuevo;

	drop table if exists aux_viejos;
	drop table if exists aux_nuevos;

	--TOTAL BENEFICIARIOS VIGENTES A LA FECHA
	create temp table aux_viejos as 
	select periodo_viejo as periodo,*
	from afiliado a
	where (baja_fecha is null or baja_fecha>=periodo_viejo)
	and a.vigen_fecha<periodo_viejo	
	--LO HACEMOS POR APORTE
	and exists (select 1 from afi_aportes ap
		    where ap.cuil_titular=a.cuil_titular
		    and ap.inte=0
		    and (ap.fecha_egre is null or ap.fecha_egre>=periodo_viejo)
		    and (ap.baja_fecha is null or ap.baja_fecha>=periodo_viejo)
		    and ap.fecha_ingre<periodo_viejo
		    and id_aporte in (select id_aporte from aporte where es_os is true));--(1,2,7,8,9,10,12,13,14,15));	    
		    
	create temp table aux_nuevos as 
	select periodo_nuevo as periodo, *
	from afiliado a
	where (baja_fecha is null or baja_fecha>=periodo_nuevo)	
	and a.vigen_fecha<periodo_nuevo
	--LO HACEMOS POR APORTE
	and exists (select 1 from afi_aportes ap
		    where ap.cuil_titular=a.cuil_titular
		    and ap.inte=0
		    and (ap.fecha_egre is null or ap.fecha_egre>=periodo_nuevo)
		    and (ap.baja_fecha is null or ap.baja_fecha>=periodo_nuevo)
		    and ap.fecha_ingre<periodo_nuevo
		    and id_aporte in (select id_aporte from aporte where es_os is true));	

if alta_baja=2 then 				       
        insert into result(ope,obra_social, cuit, cuil_titular, parentesco, cuil, documento_tipo, docu_numero, ape_nombre,
			   sexo, estado_civil, naci_fecha, nacionalidad, calle, numero, piso, depto, localidad,
			   postal_codi, provincia, tipo_domi, telefono, situ_revista, discapacitado, tipo_beneficiario,
			   fecha_alta_os, fecha_cierre,aportante_titular,inte)                  
	select cast('BAJAS' as varchar),
	cast(rtrim('112608') as varchar) as obra_social,
	lpad( cast (' ' as varchar), 11, ' ')  as cuit,
	cast(rtrim(a.cuil_titular) as varchar),
	--lpad( cast (p.id_sssuper as varchar), 2, '0') as parentesco,
	lpad( cast (a.id_parentesco_sss as varchar), 2, '0') as parentesco,
	cast(rtrim(cuil) as varchar),
	cast(rtrim(documento_tipo) as varchar),
	lpad( cast (docu_numero as varchar), 8, '0') as docu_numero,
	rpad( cast (apellido||' '||nombre  as character(30)), 30, ' ') as ape_nombre,
	rtrim(UPPER(sexo)),
	--case when a.civil_esta='DESCONOCIDO' then '01' else cast(lpad( cast (s.id_sssuper as varchar), 2, '0') as varchar) end  as estado_civil,
	lpad( cast (a.id_estado_civil_sss as varchar), 2, '0') as estado_civil,
	to_char(a.naci_fecha, 'DDMMYYYY'),
        lpad( cast (n.id_sssuper as varchar), 3, '0') as nacionalidad,
        rpad( cast (' ' as varchar), 20, ' ') as calle,
        rpad( cast (' ' as varchar), 5, ' ') as numero,
        rpad( cast (' ' as varchar), 4, ' ')   as piso,
        rpad( cast (' ' as varchar), 4, ' ') as depto,
        rpad( cast (' ' as varchar), 20, ' ') as localidad,
        rpad( cast (' ' as varchar), 6, ' ') as postal_codi,
        rpad( cast (' ' as varchar), 2, ' ')  as provincia,
        cast('01' as varchar) as tipo_domi,
        rpad( cast (' ' as varchar), 20, ' ') as telefono,
        rpad( cast (' ' as varchar), 2, ' ')  as situ_revista,
        lpad( cast (discapacitado as varchar), 2, '0') as discapacitado,
        lpad( cast (' ' as varchar), 2, '0') as tipo_beneficiario,
        to_char(a.vigen_fecha, 'DDMMYYYY') as fecha_alta_os,
        cast(to_char(fecha_cierre_v,'DDMMYYYY') as varchar) as fecha_cierre,
        aportante_titular,
        a.inte	
	from afiliado a
	--left outer join civil_esta_super s
	--on upper(a.civil_esta)=s.descripcion		
	left outer join nacionalidad n
	on a.nacionalidad=n.id
	--left outer join tipo_paren_super p
	--on upper (a.parentesco)= p.descripcion   	
	where exists (select 1 from afi_aportes ap
		    where ap.cuil_titular=a.cuil_titular
		    and ap.inte=0
		    /*and (ap.fecha_egre is null or ap.fecha_egre<periodo_viejo)
		    and (ap.baja_fecha is null or ap.baja_fecha<periodo_viejo)*/
		    and ap.fecha_ingre<=periodo_viejo
		    and ap.fecha_egre is not null
		    --and ap.fecha_egre>'20110101' --SINO ES IMPRESIONANTE LA CANTIDAD DE NO INFORMADOS
		    and ap.modi_fecha>'20110101'
		    and id_aporte in (select id_aporte from aporte where es_os is true))	    		    	
	and a.fecha_baja_super is null	
	--and rtrim(upper(a.parentesco))='TITULAR'
	and a.id_parentesco_sss=0  --'TITULAR'
	--and baja_fecha is not null	
	union
	select cast('BAJAS' as varchar),
	cast(rtrim('112608') as varchar) as obra_social,
	lpad( cast (' ' as varchar), 11, ' ')  as cuit,
	cast(rtrim(a.cuil_titular) as varchar),
	--lpad( cast (p.id_sssuper as varchar), 2, '0') as parentesco,
	lpad( cast (a.id_parentesco_sss as varchar), 2, '0') as parentesco,
	cast(rtrim(cuil) as varchar),
	cast(rtrim(documento_tipo) as varchar),
	lpad( cast (docu_numero as varchar), 8, '0') as docu_numero,
	rpad( cast (apellido||' '||nombre  as character(30)), 30, ' ') as ape_nombre,
	rtrim(UPPER(sexo)),
	--case when a.civil_esta='DESCONOCIDO' then '01' else cast(lpad( cast (s.id_sssuper as varchar), 2, '0') as varchar) end  as estado_civil,
	lpad( cast (a.id_estado_civil_sss as varchar), 2, '0') as estado_civil,
	to_char(a.naci_fecha, 'DDMMYYYY'),
        lpad( cast (n.id_sssuper as varchar), 3, '0') as nacionalidad,
        rpad( cast (' ' as varchar), 20, ' ') as calle,
        rpad( cast (' ' as varchar), 5, ' ') as numero,
        rpad( cast (' ' as varchar), 4, ' ')   as piso,
        rpad( cast (' ' as varchar), 4, ' ') as depto,
        rpad( cast (' ' as varchar), 20, ' ') as localidad,
        rpad( cast (' ' as varchar), 6, ' ') as postal_codi,
        rpad( cast (' ' as varchar), 2, ' ')  as provincia,
        cast('01' as varchar) as tipo_domi,
        rpad( cast (' ' as varchar), 20, ' ') as telefono,
        rpad( cast (' ' as varchar), 2, ' ')  as situ_revista,
        lpad( cast (discapacitado as varchar), 2, '0') as discapacitado,
        lpad( cast (' ' as varchar), 2, '0') as tipo_beneficiario,
        to_char(a.vigen_fecha, 'DDMMYYYY') as fecha_alta_os,
        cast(to_char(fecha_cierre_v,'DDMMYYYY') as varchar) as fecha_cierre,
        aportante_titular,
        a.inte	
	from afiliado a
	--left outer join civil_esta_super s
	--on upper(a.civil_esta)=s.descripcion		
	left outer join nacionalidad n
	on a.nacionalidad=n.id
	--left outer join tipo_paren_super p
	--on upper (a.parentesco)= p.descripcion   
	where a.baja_fecha is not null and a.baja_fecha<periodo_viejo
	and a.baja_fecha>'20100101'
	and a.fecha_baja_super is null	
	and rtrim(upper(a.parentesco))<>'TITULAR'
	and exists (select 1 from afi_aportes ap
		    where ap.cuil_titular=a.cuil_titular
		    and ap.inte=0		    
		    and id_aporte in (select id_aporte from aporte where es_os is true))	  	
	--and baja_fecha is not null	
	order by 4, 5; 
	

	--SOLO BAJAS APO TITULARES NO TITULARES
	/*insert into result(ope,obra_social, cuit, cuil_titular, parentesco, cuil, documento_tipo, docu_numero, ape_nombre,
			   sexo, estado_civil, naci_fecha, nacionalidad, calle, numero, piso, depto, localidad,
			   postal_codi, provincia, tipo_domi, telefono, situ_revista, discapacitado, tipo_beneficiario,
			   fecha_alta_os, fecha_cierre,aportante_titular,inte)   
	select  cast('BAJAS' as varchar),
		cast(rtrim('112608') as varchar) as obra_social,
		lpad( cast (' ' as varchar), 11, ' ')  as cuit,
		cast(rtrim(v.cuil_titular) as varchar),	
		lpad( cast ('0' as varchar), 2, '0') as parentesco,
		cast(rtrim(v.cuil) as varchar),
		cast(rtrim(documento_tipo) as varchar),
		lpad( cast (docu_numero as varchar), 8, '0') as docu_numero,
		rpad( cast (apellido||' '||nombre  as character(30)), 30, ' ') as ape_nombre,
		rtrim(UPPER(sexo)),
		case when v.civil_esta='DESCONOCIDO' then '01' else cast(lpad( cast (s.id_sssuper as varchar), 2, '0') as varchar) end  as estado_civil,
		to_char(v.naci_fecha, 'DDMMYYYY'),
		lpad( cast (n.id_sssuper as varchar), 3, '0') as nacionalidad,
		rpad( cast (' ' as varchar), 20, ' ') as calle,
		rpad( cast (' ' as varchar), 5, ' ') as numero,
		rpad( cast (' ' as varchar), 4, ' ')   as piso,
		rpad( cast (' ' as varchar), 4, ' ') as depto,
		rpad( cast (' ' as varchar), 20, ' ') as localidad,
		rpad( cast (' ' as varchar), 6, ' ') as postal_codi,
		rpad( cast (' ' as varchar), 2, ' ')  as provincia,
		cast('01' as varchar) as tipo_domi,
		rpad( cast (' ' as varchar), 20, ' ') as telefono,
		rpad( cast (' ' as varchar), 2, ' ')  as situ_revista,
		lpad( cast (discapacitado as varchar), 1, '0') as discapacitado,
		lpad( cast (' ' as varchar), 2, '0') as tipo_beneficiario,
		to_char(v.vigen_fecha, 'DDMMYYYY') as fecha_alta_os,
		cast(to_char(fecha_cierre_v,'DDMMYYYY') as varchar) as fecha_cierre,
		aportante_titular,
		v.inte
	from afiliado v
	left outer join civil_esta_super s
	on upper(v.civil_esta)=s.descripcion		
	left outer join nacionalidad n
	on v.nacionalidad=n.id
	left outer join tipo_paren_super p
	on upper (v.parentesco)= p.descripcion  
	inner join afi_aportes ap
	on v.aportante_titular=1
	and v.inte<>0
	and ap.cuil_titular=v.cuil_titular
	and ap.inte=0
	and ap.id_aporte in (select 1 from aporte where es_os=true)
	and (ap.baja_fecha is null or ap.baja_fecha>current_date)
	and (ap.fecha_egre is null or ap.fecha_egre>current_date)
	where (v.baja_fecha is null or v.baja_fecha>current_date);*/

end if;	

if alta_baja=1 then 	

	insert into result(ope,obra_social, cuit, cuil_titular, parentesco, cuil, documento_tipo, docu_numero, ape_nombre,
			   sexo, estado_civil, naci_fecha, nacionalidad, calle, numero, piso, depto, localidad,
			   postal_codi, provincia, tipo_domi, telefono, situ_revista, discapacitado, tipo_beneficiario,
			   fecha_alta_os, fecha_cierre,aportante_titular,inte, cuil_verdadero)                  
	select cast('ALTAS' as varchar),
	cast(rtrim('112608') as varchar) as obra_social,
	lpad( cast (' ' as varchar), 11, ' ')  as cuit,
	cast(rtrim(v.cuil_titular) as varchar),	
	--lpad( cast (p.id_sssuper as varchar), 2, '0') as parentesco,
	lpad( cast (a.id_parentesco_sss as varchar), 2, '0') as parentesco,
	cast(rtrim(cuil) as varchar),
	cast(rtrim(documento_tipo) as varchar),
	lpad( cast (docu_numero as varchar), 8, '0') as docu_numero,
	rpad( cast (apellido||' '||nombre  as character(30)), 30, ' ') as ape_nombre,
	rtrim(UPPER(sexo)),
	--case when v.civil_esta='DESCONOCIDO' then '01' else cast(lpad( cast (s.id_sssuper as varchar), 2, '0') as varchar) end  as estado_civil,
	lpad( cast (a.id_estado_civil_sss as varchar), 2, '0') as estado_civil,
	to_char(v.naci_fecha, 'DDMMYYYY'),
        lpad( cast (n.id_sssuper as varchar), 3, '0') as nacionalidad,
        rpad( cast (' ' as varchar), 20, ' ') as calle,
        rpad( cast (' ' as varchar), 5, ' ') as numero,
        rpad( cast (' ' as varchar), 4, ' ')   as piso,
        rpad( cast (' ' as varchar), 4, ' ') as depto,
        rpad( cast (' ' as varchar), 20, ' ') as localidad,
        rpad( cast (' ' as varchar), 6, ' ') as postal_codi,
        rpad( cast (' ' as varchar), 2, ' ')  as provincia,
        cast('01' as varchar) as tipo_domi,
        rpad( cast (' ' as varchar), 20, ' ') as telefono,
        rpad( cast (' ' as varchar), 2, ' ')  as situ_revista,
        lpad( cast (discapacitado as varchar), 2, '0') as discapacitado,
        lpad( cast (' ' as varchar), 2, '0') as tipo_beneficiario,
        to_char(v.vigen_fecha, 'DDMMYYYY') as fecha_alta_os,
        cast(to_char(fecha_cierre_v,'DDMMYYYY') as varchar) as fecha_cierre,
        aportante_titular,
        v.inte, 
        v.cuil_titular
	from aux_nuevos v	
	--left outer join civil_esta_super s
	--on upper(v.civil_esta)=s.descripcion		
	left outer join nacionalidad n
	on v.nacionalidad=n.id
	--left outer join tipo_paren_super p
	--on upper (v.parentesco)= p.descripcion   	
	where not exists (select 1 from aux_viejos n
			  where n.cuil_titular=v.cuil_titular
			  and n.inte=v.inte);
			  
	--ALTAS DE OTROS PERIODOS (LOS QUE TIENEN FECHA DE SUPER VACIA)
	
	insert into result(ope,obra_social, cuit, cuil_titular, parentesco, cuil, documento_tipo, docu_numero, ape_nombre,
			   sexo, estado_civil, naci_fecha, nacionalidad, calle, numero, piso, depto, localidad,
			   postal_codi, provincia, tipo_domi, telefono, situ_revista, discapacitado, tipo_beneficiario,
			   fecha_alta_os, fecha_cierre,aportante_titular,inte, cuil_verdadero)                  
	select cast('ALTAS' as varchar),
	cast(rtrim('112608') as varchar) as obra_social,
	lpad( cast (' ' as varchar), 11, ' ')  as cuit,
	cast(rtrim(a.cuil_titular) as varchar),
	--lpad( cast (p.id_sssuper as varchar), 2, '0') as parentesco,
	lpad( cast (a.id_parentesco_sss as varchar), 2, '0') as parentesco,
	cast(rtrim(cuil) as varchar),
	cast(rtrim(documento_tipo) as varchar),
	lpad( cast (docu_numero as varchar), 8, '0') as docu_numero,
	rpad( cast (apellido||' '||nombre  as character(30)), 30, ' ') as ape_nombre,
	rtrim(UPPER(sexo)),
	--case when a.civil_esta='DESCONOCIDO' then '01' else cast(lpad( cast (s.id_sssuper as varchar), 2, '0') as varchar) end  as estado_civil,
	lpad( cast (a.id_estado_civil as varchar), 2, '0') as estado_civil,
	to_char(a.naci_fecha, 'DDMMYYYY'),
        lpad( cast (n.id_sssuper as varchar), 3, '0') as nacionalidad,
        rpad( cast (' ' as varchar), 20, ' ') as calle,
        rpad( cast (' ' as varchar), 5, ' ') as numero,
        rpad( cast (' ' as varchar), 4, ' ')   as piso,
        rpad( cast (' ' as varchar), 4, ' ') as depto,
        rpad( cast (' ' as varchar), 20, ' ') as localidad,
        rpad( cast (' ' as varchar), 6, ' ') as postal_codi,
        rpad( cast (' ' as varchar), 2, ' ')  as provincia,
        cast('01' as varchar) as tipo_domi,
        rpad( cast (' ' as varchar), 20, ' ') as telefono,
        rpad( cast (' ' as varchar), 2, ' ')  as situ_revista,
        lpad( cast (discapacitado as varchar), 2, '0') as discapacitado,
        lpad( cast (' ' as varchar), 2, '0') as tipo_beneficiario,
        to_char(a.vigen_fecha, 'DDMMYYYY') as fecha_alta_os,
        cast(to_char(fecha_cierre_v,'DDMMYYYY') as varchar) as fecha_cierre,
        aportante_titular,
        a.inte,
        a.cuil_titular	
	from afiliado a
	--left outer join civil_esta_super s
	--on upper(a.civil_esta)=s.descripcion		
	left outer join nacionalidad n
	on a.nacionalidad=n.id
	--left outer join tipo_paren_super p
	--on upper (a.parentesco)= p.descripcion   		
	where (a.baja_fecha is null or a.baja_fecha>=periodo_viejo)
	and a.vigen_fecha<=periodo_viejo	
	--LO HACEMOS POR APORTE
	and exists (select 1 from afi_aportes ap
		    where ap.cuil_titular=a.cuil_titular
		    and ap.inte=0
		    and (ap.fecha_egre is null or ap.fecha_egre>=periodo_viejo)
		    and (ap.baja_fecha is null or ap.baja_fecha>=periodo_viejo)
		    and ap.fecha_ingre<periodo_viejo
		    and id_aporte in (select id_aporte from aporte where es_os is true))
		    --and ap.alta_fecha>'20120101')
	and fecha_pres_super is null
	and not exists (select 1 from result r where a.cuil_titular=r.cuil_titular and a.inte=r.inte);

	
end if;
	
	update result v
	set cuit=l.cuit
	from afi_situ_laboral l
	where v.cuil_titular=l.cuil_titular
	and (l.fecha_egre is null or l.fecha_egre >= fecha_desde)
	and (l.baja_fecha is null or l.baja_fecha>= fecha_desde)
	and l.inte=case when v.aportante_titular<>1 then 0 else v.inte end		   
	and l.fecha_ingre=(select max(fecha_ingre) 
			   from afi_situ_laboral asl2 
			   where asl2.cuil_titular=l.cuil_titular 
			   and asl2.inte=case when v.aportante_titular<>1 then 0 else v.inte end
			   and (fecha_egre is null or fecha_egre >= fecha_desde) 
			   and (baja_fecha is null or baja_fecha>= fecha_desde));

	update result v
	set cuit=l.cuit
	from afi_situ_laboral l
	where v.cuil_titular=l.cuil_titular	
	and (l.baja_fecha is null or l.baja_fecha>= fecha_desde)
	and l.inte=case when v.aportante_titular<>1 then 0 else v.inte end
	and (v.cuit is null or rtrim(v.cuit)='');

	

	update result v
	set situ_revista=lpad( cast (id_revista_sssalud as varchar), 2, '0')	
	from afi_situ_laboral l, situacion_revista r
	where v.cuil_titular=l.cuil_titular
	and (l.fecha_egre is null or l.fecha_egre >= fecha_desde)
	and (l.baja_fecha is null or l.baja_fecha>= fecha_desde)
	and l.inte=case when v.aportante_titular<>1 then 0 else v.inte end		   
	and l.fecha_ingre=(select max(fecha_ingre) 
			   from afi_situ_laboral asl2 
			   where asl2.cuil_titular=l.cuil_titular 
			   and asl2.inte=case when v.aportante_titular<>1 then 0 else v.inte end
			   and (fecha_egre is null or fecha_egre >= fecha_desde) 
			   and (baja_fecha is null or baja_fecha>= fecha_desde))
        and l.id_revista=r.id_situ_revista;
	    
        
	
	update result v	
	set tipo_beneficiario=lpad( cast (cl.id_sssuper as varchar), 2, '0')
	from afi_situ_laboral l, categoria_laboral cl
	where v.cuil_titular=l.cuil_titular
	and (l.fecha_egre is null or l.fecha_egre >= fecha_desde)
	and (l.baja_fecha is null or l.baja_fecha>= fecha_desde)
	and l.inte=case when v.aportante_titular<>1 then 0 else v.inte end		   
	and l.fecha_ingre=(select max(fecha_ingre) 
			   from afi_situ_laboral asl2 
			   where asl2.cuil_titular=l.cuil_titular 
			   and asl2.inte=case when v.aportante_titular<>1 then 0 else v.inte end
			   and (fecha_egre is null or fecha_egre >= fecha_desde) 
			   and (baja_fecha is null or baja_fecha>= fecha_desde))        
	and l.id_categoria=cl.id_categoria;    

	update result v
	set cuit=v.cuil
	where v.tipo_beneficiario in ('04','05','07','11')
	and v.aportante_titular=1;

	update result v
	set calle=rpad( cast (case when d.calle is null then ' ' else d.calle end as character(20)), 20, ' '),
	    numero=rpad( cast (case when d.numero is null then ' ' else d.numero end as character(5)), 5, ' '),
	    piso=rpad( cast (case when d.piso is null then ' ' else d.piso end as character(4)), 4, ' ') ,
	    depto=rpad( cast (case when d.depto is null then ' ' else d.depto end as character(4)), 4, ' '),
	    localidad=rpad( cast (case when lo.detalle is null then ' ' else lo.detalle end as character(20)), 20, ' '),
	    postal_codi=lpad( cast (case when d.postal_codi is null then ' ' else d.postal_codi end as character(6)), 6, '0'),
	    provincia=lpad( cast (pr.id_sssalud as varchar), 2, '0'),
	    telefono=rpad( cast (case when d.telefono is null then ' ' else d.telefono end as character(20)), 20, ' ')  
	from afi_domicilio d, provincia pr, localidad lo
	where v.cuil_titular=d.cuil_titular
	and d.inte=0
	and (d.baja_fecha is null or d.baja_fecha>=fecha_desde)
	and d.provincia=pr.id_provincia
	and d.localidad=lo.id_localidad;

	update result v
	set cuil_titular=cuil,
	    parentesco=0
	where v.aportante_titular=1
	and inte<>0;    

	update result v
	set situ_revista=lpad( cast ('3' as varchar), 2, '0')		
	where (situ_revista is null or rtrim(situ_revista)='');	

if registrar_envio and alta_baja=1 then
	RAISE INFO 'REGISTRO';
	insert into informes.fechas_informe_super(cuil_titular,inte, fecha_alta)
	select a.cuil_titular,a.inte, a.fecha_pres_super
	from afiliado a, result r
	where a.cuil_titular=r.cuil_verdadero
	and a.inte=r.inte
	and a.fecha_pres_super is not null;

	update afiliado	a	
	set fecha_pres_super=current_date
	from result r
	where a.cuil_titular=r.cuil_verdadero
	and a.inte=r.inte;
end if;  

if registrar_envio and alta_baja=2 then
	RAISE INFO 'REGISTRO';
	insert into informes.fechas_informe_super(cuil_titular,inte, fecha_baja)
	select a.cuil_titular, a.inte, a.fecha_baja_super
	from afiliado a, result r
	where a.cuil_titular=r.cuil_titular
	and a.inte=r.inte
	and a.fecha_baja_super is not null;

	update afiliado	a	
	set fecha_baja_super=current_date
	from result r
	where a.cuil_titular=r.cuil_titular
	and a.inte=r.inte;
end if;  

	
return query
select  ope, 
	obra_social,
	cuit,
	cuil_titular,
	cast (lpad( cast (parentesco as varchar), 2, '0') as varchar),
	cuil,
	documento_tipo,
	docu_numero,
	ape_nombre,
	sexo,
	estado_civil,
	naci_fecha,
        nacionalidad,
        calle,
        numero,
        piso,
        depto,
        localidad,
        postal_codi,
        provincia,
        tipo_domi,
        telefono,
        situ_revista,
        discapacitado,
        tipo_beneficiario,
        fecha_alta_os,        
        fecha_cierre 
from result
where case when alta_baja is null then ope=ope when alta_baja=1 then ope='ALTAS' when alta_baja=2 then ope like 'BAJAS%' end;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;