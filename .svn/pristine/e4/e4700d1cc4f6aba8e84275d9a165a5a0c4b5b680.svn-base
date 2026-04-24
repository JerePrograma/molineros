
CREATE OR REPLACE FUNCTION reporte_diferencias_padron(id_terc_v character varying, fecha date)
  RETURNS integer AS
$BODY$
declare fecha_max timestamp with time zone;
BEGIN
drop table if exists afiliados_vigentes_diferencia;

if fecha is null then 
fecha_max=max(fecha_listado)
	  from informes.listado_tercerizadora ii
	  where ii.id_tercerizadora=id_terc_v;
RAISE INFO 'FECHA: %',fecha_max;
else 
fecha_max=max(fecha_listado)
	  from informes.listado_tercerizadora ii
	  where ii.id_tercerizadora=id_terc_v
	  and fecha_listado<fecha;
end if;	  

create table afiliados_vigentes_diferencia as 
select 	id_ospim, 
	seccional, 
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
	cast('ALTAS' as varchar) as modificacion
from afiliados_vigentes_result a
where not exists (select * from informes.listado_tercerizadora i
	          where i.id_tercerizadora=id_terc_v
	          and i.cuil_titular=a.cuil_titular
	          and i.inte=a.inte
	          and i.fecha_listado=fecha_max);

insert into afiliados_vigentes_diferencia(id_ospim,seccional, id_tercerizadora, cuil_titular, cuil, inte, id_parentesco_sss, parentesco, 
					  apellido, nombre, documento_tipo, docu_numero, naci_fecha, sexo, id_estado_civil_sss, civil_esta, nacionalidad, 
					  provincia, localidad, postal_codi, calle, numero, piso, depto, telefono, categoria, 
					  ramo, id_plan, plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim,
					  os_anterior,discapacitado, modificacion)				       
select  id_ospim, 
	seccional, 
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
	cast('BAJAS' as varchar)	          
from informes.listado_tercerizadora i
where i.id_tercerizadora=id_terc_v
and i.fecha_listado=fecha_max
and not exists (select * from afiliados_vigentes_result a
	          where i.id_tercerizadora=id_terc_v
	          and i.cuil_titular=a.cuil_titular
	          and i.inte=a.inte);

insert into afiliados_vigentes_diferencia(id_ospim,seccional, id_tercerizadora, cuil_titular, cuil, inte, id_parentesco_sss, parentesco, 
					  apellido, nombre, documento_tipo, docu_numero, naci_fecha, sexo, id_estado_civil_sss, civil_esta, nacionalidad, 
					  provincia, localidad, postal_codi, calle, numero, piso, depto, telefono, categoria, 
					  ramo, id_plan, plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim,
					  os_anterior,discapacitado, modificacion)				       
select  i.id_ospim, 
	i.seccional, 
	i.id_tercerizadora, 
	i.cuil_titular, 
	i.cuil, 
	i.inte, 
	i.id_parentesco_sss,
	i.parentesco, 
	i.apellido, 
	i.nombre, 
	i.documento_tipo, 
	i.docu_numero, 
	i.naci_fecha, 
	i.sexo, 
	i.id_estado_civil_sss,
	i.civil_esta, 
	i.nacionalidad, 
	i.provincia, 
	i.localidad, 
	i.postal_codi, 
	i.calle, 
	i.numero, 
	i.piso, 
	i.depto, 
	i.telefono,
	i.categoria, 
	i.ramo, 
	i.id_plan, 
	i.plan, 
	i.ingre_fecha, 
	i.baja_fecha, 
	i.id_uoma, 
	i.cuit, 
	i.razon_soc,
	i.fecha_ospim,
	i.os_anterior,
	i.discapacitado,
	cast('MODIFICACIONES' as varchar)	          
from informes.listado_tercerizadora i, afiliados_vigentes_result a
where i.id_tercerizadora=id_terc_v
and i.fecha_listado=fecha_max
and i.cuil_titular=a.cuil_titular
and i.inte=a.inte
and (i.id_ospim<>a.id_ospim or 
     i.seccional<>a.seccional or
     i.id_tercerizadora<>a.id_tercerizadora or
     i.cuil_titular <> a.cuil_titular or
     i.cuil <> a.cuil or
     i.inte <> a.inte or
     --i.parentesco <> a.parentesco or
     i.id_parentesco_sss <> a.id_parentesco_sss or
     i.apellido <> a.apellido or
     i.nombre <> a.nombre or
     i.documento_tipo <> a.documento_tipo or 
     i.docu_numero <> a.docu_numero or 
     i.naci_fecha <> a.naci_fecha or 
     i.sexo <> a.sexo or 
     --i.civil_esta <> a.civil_esta or
     i.id_estado_civil_sss <> a.id_estado_civil_sss or
     i.nacionalidad <> a.nacionalidad or
     i.provincia <> a.provincia or
     i.localidad <> a.localidad or
     i.postal_codi <> a.postal_codi or
     i.calle <> a.calle or
     i.numero <> a.numero or
     i.piso <> a.piso or
     i.depto <> a.depto or
     i.telefono <> a.telefono or
     i.categoria <> a.categoria or
     i.ramo <> a.ramo or
     i.id_plan <> a.id_plan or
     i.plan <> a.plan or
     i.ingre_fecha <> a.ingre_fecha or
     i.baja_fecha <> a.baja_fecha or
     i.cuit <> a.cuit or	
     i.fecha_ospim <> a.fecha_ospim or
     i.os_anterior <> a.os_anterior or
     i.discapacitado <> a.discapacitado);
	
	          
return 1;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;