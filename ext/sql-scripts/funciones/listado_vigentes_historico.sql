--en esta archivo: listado para version plan nuevo y para version 1.0

CREATE OR REPLACE FUNCTION listado_vigentes_historico(id_terc_v character varying, tipo_v integer, fecha date)
  RETURNS SETOF afiliados_vigentes AS
$BODY$
BEGIN
drop table if exists afiliados_vigentes_result;


     
create table afiliados_vigentes_result as 
	select 	a.id_ospim, 
		a.seccional , 
		a.id_tercerizadora, 
		a.cuil_titular, 
		a.cuil, 
		a.inte,
		a.id_parentesco_sss, 
		p.descripcion as parentesco, 
		a.apellido, 
		a.nombre, 
		a.documento_tipo, 
		a.docu_numero, 
		a.naci_fecha, 
		a.sexo,
		a.id_estado_civil_sss, 
		ec.descripcion as civil_esta, 
		a.nacionalidad, 
		a.provincia, 
		a.localidad, 
		a.postal_codi, 
		a.calle, 
		a.numero, 
		a.piso, 
		a.depto, 
		a.telefono,
		a.categoria, 
		a.ramo, 
		a.id_plan, 
		a.plan, 
		a.ingre_fecha, 
		a.baja_fecha, 
		a.id_uoma, 
		a.cuit, 
		a.razon_soc,
		a.fecha_ospim,
		a.os_anterior,
		a.discapacitado,
		cast(null as varchar),
		a.valor_capita,
		cast(null as date)
	from  informes.listado_tercerizadora a
	inner join parentesco_sss p on a.id_parentesco_sss = p.codigo
	inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo
	where id_tercerizadora=id_terc_v
	and to_char(a.fecha_listado,'dd/MM/yyyy') =to_char(fecha,'dd/MM/yyyy')
	order by a.cuil_titular, a.inte;


if(tipo_v=2) then
	perform reporte_diferencias_padron(id_terc_v, fecha);
	return query
	select  
	id_ospim, 
		seccional , 
		id_tercerizadora, 
		cuil_titular, 
		cuil, 
		inte,
		id_parentesco_sss, 
		parentesco, 
		apellido, 
		nombre, 
		documento_tipo, 
		docu_numero, 
		naci_fecha, 
		sexo,
		id_estado_civil_sss, 
		civil_esta, 
		nacionalidad, 
		provincia, 
		localidad, 
		postal_codi, 
		calle, 
		numero, 
		piso, 
		depto, 
		telefono,
		categoria, 
		ramo, 
		id_plan, 
		plan, 
		ingre_fecha, 
		baja_fecha, 
		id_uoma, 
		cuit, 
		razon_soc,
		fecha_ospim,
		os_anterior,
		discapacitado,
		modificacion,--cast(null as varchar),		
		cast(null as numeric), 	
		cast(null as date)
	from afiliados_vigentes_diferencia ;
	
else
	return query		   
	select 	id_ospim, 
		seccional , 
		id_tercerizadora, 
		cuil_titular, 
		cuil, 
		inte,
		id_parentesco_sss, 
		parentesco, 
		apellido, 
		nombre, 
		documento_tipo, 
		docu_numero, 
		naci_fecha, 
		sexo,
		id_estado_civil_sss, 
		civil_esta, 
		nacionalidad, 
		provincia, 
		localidad, 
		postal_codi, 
		calle, 
		numero, 
		piso, 
		depto, 
		telefono,
		categoria, 
		ramo, 
		id_plan, 
		plan, 
		ingre_fecha, 
		baja_fecha, 
		id_uoma, 
		cuit, 
		razon_soc,
		fecha_ospim,
		os_anterior,
		discapacitado,
		cast(null as varchar),		
		valor_capita, 
		cast(null as date)
		from afiliados_vigentes_result		
	order by cuil_titular;
end if;	

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;

  
CREATE OR REPLACE FUNCTION planes_v_1_0.listado_vigentes_historico(id_terc_v character varying, tipo_v integer, fecha date)
  RETURNS SETOF afiliados_vigentes AS
$BODY$
BEGIN
drop table if exists afiliados_vigentes_result;


     
create table afiliados_vigentes_result as 
	select 	a.id_ospim, 
		a.seccional , 
		a.id_tercerizadora, 
		a.cuil_titular, 
		a.cuil, 
		a.inte,
		a.id_parentesco_sss, 
		cast(upper(p.descripcion) as varchar) as parentesco,  
		a.apellido, 
		a.nombre, 
		a.documento_tipo, 
		a.docu_numero, 
		a.naci_fecha, 
		a.sexo, 
		a.id_estado_civil_sss, 
		ec.decripcion as civil_esta, 
		a.nacionalidad, 
		a.provincia, 
		a.localidad, 
		a.postal_codi, 
		a.calle, 
		a.numero, 
		a.piso, 
		a.depto, 
		a.telefono,
		a.categoria, 
		a.ramo, 
		a.id_plan, 
		a.plan, 
		a.ingre_fecha, 
		a.baja_fecha, 
		a.id_uoma, 
		a.cuit, 
		a.razon_soc,
		a.fecha_ospim,
		a.os_anterior,
		a.discapacitado,
		cast(null as varchar),
		a.valor_capita,
		cast(null as date)
	from  informes.listado_tercerizadora a
	inner join parentesco_sss p on a.id_parentesco_sss=p.codigo
	inner join estado_civil_sss ec on a.id_estado_civil_sss=ec.codigo
	where id_tercerizadora=id_terc_v
	and to_char(a.fecha_listado,'dd/MM/yyyy') =to_char(fecha,'dd/MM/yyyy')
	order by a.cuil_titular, a.inte;


if(tipo_v=2) then
	perform reporte_diferencias_padron(id_terc_v, fecha);
	return query
	select  
	id_ospim, 
		seccional , 
		id_tercerizadora, 
		cuil_titular, 
		cuil, 
		inte, 
		id_parentesco_sss,
		parentesco, 
		apellido, 
		nombre, 
		documento_tipo, 
		docu_numero, 
		naci_fecha, 
		sexo,
		id_estado_civil_sss, 
		civil_esta, 
		nacionalidad, 
		provincia, 
		localidad, 
		postal_codi, 
		calle, 
		numero, 
		piso, 
		depto, 
		telefono,
		categoria, 
		ramo, 
		id_plan, 
		plan, 
		ingre_fecha, 
		baja_fecha, 
		id_uoma, 
		cuit, 
		razon_soc,
		fecha_ospim,
		os_anterior,
		discapacitado,
		modificacion,--cast(null as varchar),		
		cast(null as numeric), 	
		cast(null as date)
	from afiliados_vigentes_diferencia ;
	
else
	return query		   
	select 	id_ospim, 
		seccional , 
		id_tercerizadora, 
		cuil_titular, 
		cuil, 
		inte,
		id_parentesco_sss, 
		parentesco, 
		apellido, 
		nombre, 
		documento_tipo, 
		docu_numero, 
		naci_fecha, 
		sexo,
		id_estado_civil_sss, 
		civil_esta, 
		nacionalidad, 
		provincia, 
		localidad, 
		postal_codi, 
		calle, 
		numero, 
		piso, 
		depto, 
		telefono,
		categoria, 
		ramo, 
		id_plan, 
		plan, 
		ingre_fecha, 
		baja_fecha, 
		id_uoma, 
		cuit, 
		razon_soc,
		fecha_ospim,
		os_anterior,
		discapacitado,
		cast(null as varchar),		
		valor_capita, 
		cast(null as date)
		from afiliados_vigentes_result		
	order by cuil_titular;
end if;	

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
  