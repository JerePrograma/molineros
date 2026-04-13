create type busqueda_incidente_solo as (fecha timestamp without time zone, cuil_titular varchar, nombre varchar, apellido varchar,
detalle_incidente text, id_seccional integer, seccional varchar,id_incidente integer, 
docu_tipo varchar, nro_doc varchar, id_secc_afi integer, descrip_secc_afi varchar, baja_fecha timestamp without time zone,
   id_provincia integer, id_localidad integer,
calle varchar, numero varchar, piso varchar, depto varchar, cod_postal varchar, observacion varchar, id_domicilio integer, fecha_recepcion date)
-- Function: uoma.buscar_incidente(integer)

-- DROP FUNCTION uoma.buscar_incidente(integer);

CREATE OR REPLACE FUNCTION uoma.buscar_incidente(id_incidente_v integer)
  RETURNS SETOF busqueda_incidente_solo AS
$BODY$
BEGIN 
	return query
	select i.fecha, a.cuil_titular, a.nombre,a.apellido, i.detalle_incidente, s2.id_seccional, s2.descripcion, i.id_incidente,
	a.documento_tipo, a.docu_numero, s.id_seccional, s.descripcion, a.baja_fecha, d.provincia, d.localidad, d.calle,
	d.numero, d.piso, d.depto, d.postal_codi, d.observaciones, d.id_domicilio, i.fecha_recepcion
	from uoma.incidente_unidad_operativa i, afiliado a, seccional s, seccional s2, uoma.domicilio d
	where a.cuil_titular=i.cuil_titular	
	and a.inte=i.inte
	and s.id_seccional=a.id_seccional
	and i.id_seccional=s2.id_seccional
	and i.id_incidente=id_incidente_v	
	and d.id_domicilio=i.id_domicilio;
	
END; 				    
$BODY$
  LANGUAGE plpgsql VOLATILE

