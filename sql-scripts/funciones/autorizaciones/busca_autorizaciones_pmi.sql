CREATE OR REPLACE FUNCTION autorizaciones.busca_autorizaciones_pmi(IN fecha date, IN cuil_titular character varying, IN inte integer, IN nro_receta bigint)
  RETURNS TABLE(id_ospim integer, id_autorizacion_pmi integer, fecha date, naci_fecha date, apellido character varying, nombre character varying, cuil_titular character varying, inte integer, baja_fecha timestamp without time zone, nro_receta bigint, docu_numero character varying, documento_tipo character varying, baja_afi timestamp without time zone, id_seccional integer, descripcion_secc character varying, observaciones character varying) AS
$BODY$

select afi.id_ospim, aut.id_autorizacion_pmi, aut.fecha, afi.naci_fecha, afi.apellido, afi.nombre, aut.cuil_titular, aut.inte, aut.baja_fecha, aut.nro_receta, afi.docu_numero, afi.documento_tipo, afi.baja_fecha, afi.id_seccional, secc.descripcion, aut.observaciones
from autorizaciones.autorizaciones_pmi aut, public.afiliado afi, public.seccional secc
where aut.cuil_titular=afi.cuil_titular
and afi.id_seccional=secc.id_seccional
and aut.inte=afi.inte
and ($1 is null or ($1 is not null and aut.fecha=$1))
and ($2 is null or ($2 is not null and aut.cuil_titular=$2))
and ($3 is null or ($3 is not null and aut.inte=$3))
and ($4 is null or ($4 is not null and aut.nro_receta=$4))
order by aut.id_autorizacion_pmi, aut.nro_receta;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION autorizaciones.busca_autorizaciones_pmi(date, character varying, integer, bigint)
  OWNER TO postgres;
