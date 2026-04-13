CREATE OR REPLACE FUNCTION buscar_afiliados_opciones(cuil_v character, delegacion_v character, apellido_v character, nombre_v character, libro_v integer)
  RETURNS SETOF busca_afi_compo AS
$BODY$
declare cantidad_afiliados integer;
BEGIN
drop table if exists aux_busqueda;

create temp table aux_busqueda as 	
	select 	cuil as cuil_titular,
		nro_formulario as inte, 
		0 as id_parentesco_sss,
		null as parentesco,
		upper(apellido) as apellido,
		upper(nombre) as nombre,
		null as docu_tipo,
		cast(nro_formulario as varchar) as docu_numero,		
		cast(0 as integer) as id_seccional,		
		cast(upper(delegacion) as varchar) as descripcion,
		cast(0 as integer) as id_ospim ,
		cast(fecha_elecc as timestamp without time zone) as id_ospim_baja_fecha ,
		cast(0 as integer) as id_uoma ,
		cast(fecha_certi as timestamp without time zone) as id_uoma_baja_fecha ,
		cast(0 as integer) as id_amtima,
		cast(fecha_exportacion as timestamp without time zone) as id_amtima_baja_fecha,		
		current_date as ingre_fecha1,
		--aps.fecha_egre,
		cast(fecha_baja as timestamp without time zone) as baja_fecha, 
		0 as id_plan,
		cast('' as varchar) as nombre_plan,
		0 as id_motivo_baja,
		current_date as ingre_fecha,
		case when okdesdesss is true then cast('T' as varchar) else cast('F' as varchar) end as discapacitado,
		cast(fecha_entrega as timestamp without time zone) as vigen_fecha,
		current_date as naci_fecha,
		cast(null as varchar) as desc_tercerizadora	
	from afi_opciones_sss
	where ($1 is null or ($1 is not null and cuil=$1))
	and   ($2 is null or ($2 is not null and upper(delegacion) like '%'||upper($2)||'%'))		
	and ($3 is null or ($3 is not null and upper(apellido) like '%'||upper($3)||'%'))
	and ($4 is null or ($4 is not null and upper(nombre) like '%'||upper($4)||'%'))
	and ($5 is null or ($5 is not null and libro = $5))	
	and de_alta_portal is false
	and baja_fecha is null
	order by 1,2;

return query 
select cuil_titular,
		inte, 
		id_parentesco_sss,
		cast(parentesco as varchar),
		cast(apellido as varchar),
		cast(nombre as varchar),
		cast(docu_tipo as varchar),
		docu_numero,		
		id_seccional,		
		descripcion,
		id_ospim ,
		id_ospim_baja_fecha ,
		id_uoma ,
		id_uoma_baja_fecha ,
		id_amtima,
		id_amtima_baja_fecha,		
		ingre_fecha,
		--aps.fecha_egre,
		baja_fecha, 
		id_plan,
		nombre_plan,
		id_motivo_baja,
		ingre_fecha,
		discapacitado,
		vigen_fecha,
		naci_fecha,
		desc_tercerizadora 
from aux_busqueda;

END;	
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;