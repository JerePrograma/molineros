
  CREATE OR REPLACE FUNCTION novedades_sss.buscar_novedades_detalle(cuil_titular_p character varying, cuil_p character varying, documento_tipo_p character varying, documento_numero_p integer, apellido_nombre_p character varying, codigo_movimiento_p character varying, tipo_origen_p character varying, fecha_proc_p date, offset_p integer)
  RETURNS SETOF novedades_sss.novedades_detalle AS
$BODY$
declare total_registros_v int;
BEGIN


if(offset_p>0) then
offset_p=offset_p*50;
end if;

total_registros_v=count(*) from novedades_sss.buscar_novedades_detalle(cuil_titular_p, cuil_p, 
			documento_tipo_p, documento_numero_p, apellido_nombre_p, 
			codigo_movimiento_p, tipo_origen_p, fecha_proc_p);

return query

SELECT  nov.id,
	nov.id_proceso,
        nov.codigo_ooss,
	nov.cuit_empleador,
	nov.cuil_titular,
	nov.codigo_parentesco,
	p.descripcion,
	nov.cuil,
	nov.documento_tipo,
	nov.documento_numero,
	nov.apellido_nombre,
	nov.sexo,
	nov.estado_civil,
	ec.descripcion,
	nov.fecha_nacimiento,
	nov.nacionalidad,
	nac.detalle,
	nov.calle,
	nov.numero_puerta,
	nov.piso,
	nov.departamento,
	nov.localidad,
	nov.codigo_postal,
	nov.provincia,
	nov.tipo_domicilio,
	nov.telefono,
	nov.situacion_revista,
	sr.detalle,
	nov.incapacidad,
	nov.tipo_beneficiario_titular,
	nov.fecha_alta_en_ooss,
	nov.fecha_cierre_presentacion,
	nov.codigo_movimiento,
	nov.detalle_novedad,
	nov.alta_fecha,
	nov.alta_usr,
	nov.modi_fecha,
	nov.modi_usr,
	nov.baja_fecha,
	nov.baja_usr,
	total_registros_v

FROM novedades_sss.novedades nov, novedades_sss.archivos_novedades arn, parentesco_sss p, 
	estado_civil_sss ec, nacionalidad nac, situacion_revista sr

WHERE
nov.id_proceso=arn.id_proceso
and nov.codigo_parentesco = p.codigo
and nov.estado_civil = ec.codigo
and nov.nacionalidad = nac.id_sssuper
and nov.situacion_revista = sr.id_revista_sssalud
and (cuil_titular_p is null or cuil_titular_p = '' or (cuil_titular_p is not null and nov.cuil_titular=cuil_titular_p))
and (cuil_p is null or cuil_p = '' or (cuil_p is not null and nov.cuil=cuil_p))
and (documento_tipo_p is null or documento_tipo_p = '' or (documento_tipo_p is not null and nov.documento_tipo=documento_tipo_p))
and (documento_numero_p is null or documento_numero_p = 0 or (documento_numero is not null and nov.documento_numero = documento_numero_p))
and (apellido_nombre_p is null or apellido_nombre_p = '' or (apellido_nombre_p is not null and UPPER(nov.apellido_nombre) like UPPER('%' || apellido_nombre_p || '%') ))
and (tipo_origen_p is null or tipo_origen_p = '' or (tipo_origen_p is not null and arn.descripcion=tipo_origen_p))
and (fecha_proc_p is null or (fecha_proc_p is not null and arn.fecha_archivo=fecha_proc_p))
--and (codigo_movimiento_p is null or codigo_movimiento_p = '' or (codigo_movimiento_p is not null and nov.codigo_movimiento=codigo_movimiento_p))
and (codigo_movimiento_p is null or codigo_movimiento_p = '' or 
    (codigo_movimiento_p is not null and nov.codigo_movimiento=codigo_movimiento_p
    and	
    case when codigo_movimiento_p = 'AO' then not exists (select 1 from afi_opciones_sss aos 
	   where aos.cuil= nov.cuil and nov.cuit_empleador = aos.cuit and aos.baja_fecha is null)
	   and not exists (select 1 from afiliado a 
	   where a.cuil= nov.cuil and a.baja_fecha is null)    
	 when codigo_movimiento_p = 'BO' then exists (select 1 from afiliado a 
	-- where a.cuil= nov.cuil and a.baja_fecha is null)
	   where a.documento_tipo=nov.documento_tipo and a.docu_numero=cast(nov.documento_numero as character varying)
	   and a.baja_fecha is null)
	 when codigo_movimiento_p in ('CC','AP','MC') then 
	 not exists (select 1 from afi_cambio_cuil acc 
			     where acc.documento_tipo_anterior=nov.documento_tipo 
			     and acc.documento_numero_anterior=cast(nov.documento_numero as character varying)) 
	 and exists (select 1 from afiliado a 
		     where a.documento_tipo=nov.documento_tipo 
		     and a.docu_numero=cast(nov.documento_numero as character varying)
		     and a.cuil != nov.cuil)  --and a.baja_fecha is null)	
	 else nov.codigo_movimiento=codigo_movimiento_p end  
    ) 
    )
order by nov.cuil_titular, nov.id

limit 50
offset offset_p;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
  
CREATE OR REPLACE FUNCTION novedades_sss.buscar_novedades_detalle(cuil_titular_p character varying, cuil_p character varying, documento_tipo_p character varying, documento_numero_p integer, apellido_nombre_p character varying, codigo_movimiento_p character varying, tipo_origen_p character varying, fecha_proc_p date)
  RETURNS SETOF novedades_sss.novedades_detalle AS
$BODY$

BEGIN

return query

SELECT  nov.id,
	nov.id_proceso,
        nov.codigo_ooss,
	nov.cuit_empleador,
	nov.cuil_titular,
	nov.codigo_parentesco,
	p.descripcion,
	nov.cuil,
	nov.documento_tipo,
	nov.documento_numero,
	nov.apellido_nombre,
	nov.sexo,
	nov.estado_civil,
	ec.descripcion,
	nov.fecha_nacimiento,
	nov.nacionalidad,
	nac.detalle,
	nov.calle,
	nov.numero_puerta,
	nov.piso,
	nov.departamento,
	nov.localidad,
	nov.codigo_postal,
	nov.provincia,
	nov.tipo_domicilio,
	nov.telefono,
	nov.situacion_revista,
	sr.detalle,
	nov.incapacidad,
	nov.tipo_beneficiario_titular,
	nov.fecha_alta_en_ooss,
	nov.fecha_cierre_presentacion,
	nov.codigo_movimiento,
	nov.detalle_novedad,
	nov.alta_fecha,
	nov.alta_usr,
	nov.modi_fecha,
	nov.modi_usr,
	nov.baja_fecha,
	nov.baja_usr,
	0

FROM novedades_sss.novedades nov, novedades_sss.archivos_novedades arn, parentesco_sss p, 
	estado_civil_sss ec, nacionalidad nac, situacion_revista sr

WHERE
nov.id_proceso=arn.id_proceso
and nov.codigo_parentesco = p.codigo
and nov.estado_civil = ec.codigo
and nov.nacionalidad = nac.id_sssuper
and nov.situacion_revista = sr.id_revista_sssalud
and (cuil_titular_p is null or cuil_titular_p = '' or (cuil_titular_p is not null and nov.cuil_titular=cuil_titular_p))
and (cuil_p is null or cuil_p = '' or (cuil_p is not null and nov.cuil=cuil_p))
and (documento_tipo_p is null or documento_tipo_p = '' or (documento_tipo_p is not null and nov.documento_tipo=documento_tipo_p))
and (documento_numero_p is null or documento_numero_p = 0 or (documento_numero is not null and nov.documento_numero = documento_numero_p))
and (apellido_nombre_p is null or apellido_nombre_p = '' or (apellido_nombre_p is not null and UPPER(nov.apellido_nombre) like UPPER('%' || apellido_nombre_p || '%') ))
--and (codigo_movimiento_p is null or codigo_movimiento_p = '' or (codigo_movimiento_p is not null and nov.codigo_movimiento=codigo_movimiento_p))
and (tipo_origen_p is null or tipo_origen_p = '' or (tipo_origen_p is not null and arn.descripcion=tipo_origen_p))
and (fecha_proc_p is null or (fecha_proc_p is not null and arn.fecha_archivo=fecha_proc_p))
and (codigo_movimiento_p is null or codigo_movimiento_p = '' or 
    (codigo_movimiento_p is not null and nov.codigo_movimiento=codigo_movimiento_p
    and 
    case when codigo_movimiento_p = 'AO' then not exists (select 1 from afi_opciones_sss aos 
	   where aos.cuil= nov.cuil and nov.cuit_empleador = aos.cuit and aos.baja_fecha is null)
	   and not exists (select 1 from afiliado a 
	   where a.cuil= nov.cuil and a.baja_fecha is null)    
	 when codigo_movimiento_p = 'BO' then exists (select 1 from afiliado a 
	-- where a.cuil= nov.cuil and a.baja_fecha is null)
	   where a.documento_tipo=nov.documento_tipo and a.docu_numero=cast(nov.documento_numero as character varying)
	   and a.baja_fecha is null)
	 when codigo_movimiento_p in ('CC','AP','MC') then
		not exists(select 1 
			   from afi_cambio_cuil acc
			   where acc.documento_tipo_anterior=nov.documento_tipo 
			   and acc.documento_numero_anterior = cast(nov.documento_numero as character varying))  --and a.baja_fecha is null)	
		and exists (select 1 
			    from afiliado a 
			    where a.documento_tipo=nov.documento_tipo 
			    and a.docu_numero=cast(nov.documento_numero as character varying)
			    and a.cuil != nov.cuil)
			    
	 else nov.codigo_movimiento=codigo_movimiento_p end	 
    ) 
    )
order by nov.cuil_titular, nov.id;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;