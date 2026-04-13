CREATE OR REPLACE FUNCTION autorizaciones.reporte_autorizaciones_pmi(IN id_autorizacion_pmi character varying)
  RETURNS TABLE(fecha date, naci_fecha date, id_ospim integer, apellido character varying, nombre character varying, docu_numero character varying, nro_receta bigint, observaciones character varying, id_seccional integer, seccional character varying) AS
$BODY$

select aut.fecha, afi.naci_fecha, afi.id_ospim, afi.apellido, afi.nombre, afi.docu_numero, aut.nro_receta, 
       aut.observaciones, sec.id_seccional, sec.descripcion
       
from autorizaciones.autorizaciones_pmi aut, public.afiliado afi, public.seccional sec
where aut.cuil_titular=afi.cuil_titular
and aut.inte=afi.inte
and afi.id_seccional=sec.id_seccional

and (cast($1 as bigint) is null or (cast($1 as bigint) is not null and id_autorizacion_pmi=cast($1 as bigint)))
and (aut.baja_fecha is null)

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION autorizaciones.reporte_autorizaciones_pmi(character varying)
  OWNER TO postgres;