create type result_correspondencia as (destino varchar, fecha_envio_recepcion date, tipo_correo integer, edificio_recep varchar,
observaciones varchar, seccional_remitente integer, seccional_remitente_nombre varchar, edificio_origen varchar, edificio_destino varchar,
id_correspondencia integer, id_localidad_corr integer, localidad_corr_nombre varchar, id_provincia_corr integer, provincia_corr_nombre varchar, 
gastos_seccional boolean,
reintegro boolean, padrones boolean, discapacidad boolean, otros boolean, datos_factura varchar, razon_prestador_remitente varchar,
razon_prestador_destinatario varchar, tipo_envio varchar, codigo_oblea varchar, documentacion boolean, facturacion boolean, tesoreria boolean,
medicamentos boolean,
cod_farmacia varchar, farmacia varchar, alta_usr varchar)


CREATE OR REPLACE FUNCTION uoma.buscar_correspondencia(destino_p character varying, lugar_recep_p character varying, envio_rec_desde_p date, envio_rec_hasta_p date, id_corr_desde integer, id_corr_hasta integer, tipo_corr integer, remitente character varying, destinatario character varying, receptor character varying, razon_prestador_p character varying, provincia_p integer, localidad_p integer, id_seccional_remi_p integer)
  RETURNS SETOF result_correspondencia AS
$BODY$
BEGIN

return query
select destino, fecha_envio_recepcion, tipo_correo, edificio_recep,
 c.observaciones, seccional_remitente, s.descripcion, edificio_origen, edificio_destino, id_correspondencia, c.id_localidad, l.detalle, 
 c.id_provincia, p.detalle, gastos_seccional, reintegro, padrones, discapacidad, otros, datos_factura, 
 cast(ltrim(coalesce(razon_prestador_remitente,'')||' '||coalesce(farmacia,'')) as varchar), 
razon_prestador_destinatario,tipo_envio, codigo_oblea, documentacion, facturacion, tesoreria, medicamentos, cod_farmacia, farmacia, c.alta_usr
from uoma.correspondencia c 
left outer join seccional s
on s.id_seccional=c.seccional_remitente
left outer join localidad l
on l.id_localidad=c.id_localidad
left outer join provincia p
on p.id_provincia=c.id_provincia
where destino=destino_p
and (lugar_recep_p is null or (lugar_recep_p is not null and c.edificio_recep=lugar_recep_p))
and fecha_envio_recepcion>=envio_rec_desde_p
and fecha_envio_recepcion<=envio_rec_hasta_p
and (id_corr_desde is null or (id_corr_desde is not null and id_correspondencia>=id_corr_desde))
and (id_corr_hasta is null or (id_corr_hasta is not null and id_correspondencia<=id_corr_hasta))
and (tipo_corr is null or (tipo_corr is not null and tipo_correo=tipo_corr))
and (remitente is null or (remitente is not null and upper(nombre_remitente)||upper(apellido_remitente) like '%'||upper(remitente)||'%'))
and (destinatario is null or (destinatario is not null and upper(nombre_destinatario)||upper(apellido_destinatario) like '%'||upper(destinatario)||'%'))
and (razon_prestador_p is null or (razon_prestador_p is not null and upper(razon_prestador_remitente)||upper(razon_prestador_remitente) like '%'||upper(razon_prestador_p)||'%'))
and (localidad_p is null or (localidad_p is not null and c.id_localidad=localidad_p))
and (provincia_p is null or (provincia_p is not null and c.id_provincia=provincia_p))
and (id_seccional_remi_p is null or (id_seccional_remi_p is not null and seccional_remitente<=id_seccional_remi_p))
and (receptor is null or (receptor is not null and upper(c.alta_usr) like '%'||upper(receptor)||'%'))
order by id_correspondencia desc;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE

