CREATE OR REPLACE FUNCTION buscar_afiliados_componente_fecharef(IN cuil_v character, IN inte_v integer, IN tipodoc_v character, IN nrodoc_v character, IN seccional_v integer, IN apellido_v character, IN nombre_v character, IN entidad_in integer, IN afi_numero integer, IN fecha_ref timestamp without time zone)
  RETURNS TABLE(cuil character varying, inte integer, id_parentesco_sss integer, parentesco character varying, apellido character varying, nombre character varying, tdoc character varying, documento character varying, id_seccional integer, seccional character varying, id_ospim integer, id_ospim_baja_fecha timestamp without time zone, id_uoma integer, id_uoma_baja_fecha timestamp without time zone, id_amtima integer, id_amtima_baja_fecha timestamp without time zone, ingreso date, baja_fecha timestamp without time zone, id_plan integer, nombre_plan character varying, alta_fecha date, discapacitado character varying, fecha_nacimiento date, desc_tercerizadora character varying) AS
$BODY$
	select 	a.cuil_titular,
		a.inte,
		a.id_parentesco_sss, 
		pa.descripcion as parentesco,
		apellido,
		nombre,
		documento_tipo,
		docu_numero,		
		s.id_seccional,		
		s.descripcion,
		id_ospim ,
		id_ospim_baja_fecha ,
		id_uoma ,
		id_uoma_baja_fecha ,
		id_amtima,
		id_amtima_baja_fecha,		
		a.ingre_fecha,
		a.baja_fecha, 
		p.id_plan,
		p.descripcion as nombre_plan, 
		cast (a.vigen_fecha as date),
		a.discapacitado,
		a.naci_fecha,
		t.descripcion as desc_tercerizadora
		
	from afiliado a 
	inner join parentesco_sss pa on a.id_parentesco_sss = pa.codigo
	inner join seccional s
	on a.id_seccional= s.id_seccional
	and ($5 is null or ($5 is not null and s.id_seccional=$5))	
	
	left outer join
	afi_plan ap on a.cuil_titular = ap.cuil_titular and ap.inte=0
	
	and ap.alta_fecha = (select max(ap2.alta_fecha) from afi_plan ap2 where ap2.cuil_titular = ap.cuil_titular and ap2.inte = ap.inte
	and (ap2.vigen_desde is null or ap2.vigen_desde <= $10)
	and (ap2.baja_fecha is null  or ap2.baja_fecha > $10))
	and (ap.vigen_desde is null or ap.vigen_desde <= $10)
	and (ap.baja_fecha is null  or ap.baja_fecha > $10)
	
	left outer join plan p on ap.id_plan = p.id_plan				  		
	
	left outer join afi_tercerizadora_servicio ats 	
	on a.cuil_titular = ats.cuil_titular and ats.inte = 0
	and ats.fecha_inicio_pres <= localtimestamp and (ats.fecha_fin_pres is null or localtimestamp <= ats.fecha_fin_pres)	
	and ats.baja_fecha is null
	
	left outer join tercerizadora_servicio t on ats.id_tercerizadora = t.id_tercerizadora

	where ($1 is null or ($1 is not null and a.cuil_titular=$1))
	and ($2 is null or ($2 is not null and a.inte=$2))
	and ($3 is null or ($3 is not null and documento_tipo=$3))
	and ($4 is null or ($4 is not null and docu_numero=$4))
	and ($6 is null or ($6 is not null and upper(apellido) like '%'||upper($6)||'%'))
	and ($7 is null or ($7 is not null and upper(nombre) like '%'||upper($7)||'%'))
	and ($9 is null or ($9 is not null and ((id_ospim = $9 and $8 = 0) or (id_uoma = $9 and $8 = 1) or (id_amtima = $9 and $8 = 2))))
			
	order by 1,2
	
	
	
	limit 20 	
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;