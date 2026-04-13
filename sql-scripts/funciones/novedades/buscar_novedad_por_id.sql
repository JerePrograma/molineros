CREATE OR REPLACE FUNCTION novedades_sss.buscar_novedad_por_id(id_novedad_p integer)
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

FROM novedades_sss.novedades nov, parentesco_sss p, estado_civil_sss ec, 
       nacionalidad nac, situacion_revista sr

WHERE
    nov.codigo_parentesco = p.codigo
and nov.estado_civil = ec.codigo
and nov.nacionalidad = nac.id_sssuper
and nov.situacion_revista = sr.id_revista_sssalud
and nov.id = id_novedad_p 

order by nov.id;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 100;