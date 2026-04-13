create type correspondencia_result as (destino varchar, id_domicilio_remitente integer, calle_remitente varchar, piso_remitente varchar,
depto_remitente varchar, oficina_remitente varchar, postal_codi_remitente varchar, barrio_remitente varchar, telefono_remitente varchar, 
observaciones_remitente varchar, id_provincia_remitente integer, id_localidad_remitente integer, numero_remitente varchar, 
id_domicilio_destina integer, calle_destina varchar, piso_destina varchar,
depto_destina varchar, oficina_destina varchar, postal_codi_destina varchar, barrio_destina varchar, telefono_destina varchar, 
observaciones_destina varchar, id_provincia_destina integer, id_localidad_destina integer, numero_destina varchar, 
apellido_remitente varchar, nombre_remitente varchar, apellido_destinatario varchar, nombre_destinatario varchar,
fecha_envio_recepcion date, tipo_correo integer, edificio_recep varchar, observaciones varchar, seccional_remitente integer, seccional_remitente_nombre varchar,
seccional_destinatario integer, seccional_destinatario_nombre varchar, edificio_origen varchar, edificio_destino varchar, 
id_correspondencia integer, id_localidad_corr integer, id_provincia_corr integer, gastos_seccional boolean, reintegro boolean, 
padrones boolean, discapacidad boolean, otros boolean,  datos_factura varchar, razon_prestador_remitente varchar, razon_prestador_destinatario varchar,
tipo_envio varchar, oblea varchar, facturacion boolean, documentacion boolean, tesoreria boolean, medicamentos boolean, 
cod_farmacia varchar, farmacia varchar)

-- Function: uoma.buscar_correspondencia_por_id(integer)

-- DROP FUNCTION uoma.buscar_correspondencia_por_id(integer);

CREATE OR REPLACE FUNCTION uoma.buscar_correspondencia_por_id(id_correspondencia_v integer)
  RETURNS SETOF correspondencia_result AS
$BODY$
BEGIN

return query 
select destino, d.id_domicilio as id_domicilio_remitente, d.calle as calle_remitente , d.piso as piso_remitente,
d.depto as depto_remitente, d.oficina as oficina_remitente, d.postal_codi as postal_codi_remitente, d.barrio as  barrio_remitente, 
d.telefono as telefono_remitente, d.observaciones as observaciones_remitente, d.provincia as id_provincia_remitente, 
d.localidad as id_localidad_remitente, d.numero as  numero_remitente, d2.id_domicilio as id_domicilio_destina, d2.calle as calle_destina, 
d2.piso as piso_destina, d2.depto as depto_destina, d2.oficina as oficina_destina, d2.postal_codi as postal_codi_destina, 
d2.barrio as  barrio_destina, d2.telefono as telefono_destina, d2.observaciones as observaciones_destina, d2.provincia as id_provincia_destina, 
d2.localidad as id_localidad_destina, d2.numero as  numero_destina, apellido_remitente, nombre_remitente, apellido_destinatario, 
nombre_destinatario, fecha_envio_recepcion, tipo_correo, edificio_recep, c.observaciones, seccional_remitente, s.descripcion,
seccional_destinatario, s2.descripcion, edificio_origen, edificio_destino, id_correspondencia, id_localidad, id_provincia,
gastos_seccional, reintegro, padrones, discapacidad, otros, datos_factura, razon_prestador_remitente, razon_prestador_destinatario,
tipo_envio, codigo_oblea,facturacion, documentacion, tesoreria, medicamentos, cod_farmacia, farmacia
from uoma.correspondencia c
left outer join uoma.domicilio_correspondencia d
on d.id_domicilio=c.id_domicilio_remitente
left outer join uoma.domicilio_correspondencia d2
on d2.id_domicilio=c.id_domicilio_destinatario
left outer join seccional s
on s.id_seccional=c.seccional_remitente
left outer join seccional s2
on s2.id_seccional=c.seccional_destinatario
where c.id_correspondencia=id_correspondencia_v;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
