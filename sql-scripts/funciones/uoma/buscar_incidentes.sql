create type busqueda_incidente as (fecha timestamp without time zone, cuil_titular varchar, nombre varchar, apellido varchar,
detalle_incidente text, id_seccional integer, seccional varchar, id_incidente integer, docu_tipo varchar, nro_doc varchar, 
id_secc_afi integer, descrip_secc_afi varchar, baja_fecha_afi timestamp without time zone, fecha_recepcion date)
-- Function: uoma.buscar_incidentes(character varying, integer, character varying, character varying, integer, character varying, character varying, integer, integer, integer, timestamp without time zone, timestamp without time zone)

-- DROP FUNCTION uoma.buscar_incidentes(character varying, integer, character varying, character varying, integer, character varying, character varying, integer, integer, integer, timestamp without time zone, timestamp without time zone);


CREATE OR REPLACE FUNCTION uoma.buscar_incidentes(cuil_v character varying, inte_v integer, tipodoc_v character varying, nrodoc_v character varying, seccional_int_v integer, apellido_v character varying, nombre_v character varying, id_ospim_v integer, id_uoma_v integer, id_amtima_v integer, fecha_desde timestamp without time zone, fecha_hasta timestamp without time zone, seccional_afiliado_v integer)
  RETURNS SETOF busqueda_incidente AS
$BODY$
BEGIN 
	return query
	select i.fecha, a.cuil_titular, a.nombre,a.apellido, i.detalle_incidente, s2.id_seccional, s2.descripcion, i.id_incidente,
	a.documento_tipo, a.docu_numero, s.id_seccional, s.descripcion, a.baja_fecha, i.fecha_recepcion
	from uoma.incidente_unidad_operativa i, afiliado a, seccional s, seccional s2
	where a.cuil_titular=i.cuil_titular	
	and a.inte=i.inte
	and s.id_seccional=a.id_seccional
	and s2.id_seccional=i.id_seccional
	and (cuil_v is null or (cuil_v is not null and i.cuil_titular=cuil_v))
	--and ((inte_v is null) or (inte_v is not null and i.inte=inte_v))
	and (tipoDoc_v is null or (tipoDoc_v is not null and a.documento_tipo=tipoDoc_v))
	and (nroDoc_v is null or (nroDoc_v is not null and a.docu_numero=nroDoc_v))
	and (seccional_int_v is null or (seccional_int_v  is not null and i.id_seccional=seccional_int_v))
	and (seccional_afiliado_v is null or (seccional_afiliado_v  is not null and a.id_seccional=seccional_afiliado_v))
	and (apellido_v is null or (apellido_v is not null and a.apellido like '%'||apellido_v||'%'))
	and (nombre_v is null or (nombre_v is not null and a.nombre like '%'||nombre_v||'%'))
	and (id_ospim_v is null or (id_ospim_v is not null and a.id_ospim=id_ospim_v))
	and (id_uoma_v is null or (id_uoma_v is not null and a.id_uoma=id_uoma_v))
	and (id_amtima_v is null or (id_amtima_v is not null and a.id_amtima=id_amtima_v))
	and (fecha_desde is null or (fecha_desde is not null and i.fecha>=fecha_desde))
	and (fecha_hasta is null or (fecha_hasta is not null and i.fecha<=fecha_hasta))
	order by s2.descripcion, a.apellido, a.nombre;
	
END; 				    
$BODY$
  LANGUAGE plpgsql VOLATILE
