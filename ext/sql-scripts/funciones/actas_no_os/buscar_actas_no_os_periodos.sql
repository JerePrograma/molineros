CREATE OR REPLACE FUNCTION buscar_actas_no_os_periodos(IN p_id integer)
  RETURNS TABLE(id integer, acta_id integer, periodo date, cuil character, remuneracion_declarada numeric, calculado numeric, decreto numeric, pagado numeric, subtotal numeric, interes numeric, alta_fecha timestamp without time zone, alta_usr character varying, alta_ip character varying, modi_fecha timestamp without time zone, modi_usr character varying, modi_ip character varying, baja_fecha timestamp without time zone, baja_usr character varying, baja_ip character varying, apellido character varying, nombre character varying, pagado_fecha date, agregado_manual boolean) AS
$BODY$

select id,
  acta_id ,
  periodo,
  cuil ,
  remuneracion_declarada,
  calculado,
  decreto,
  pagado,
  subtotal,
  interes,
  alta_fecha,
  alta_usr,
  alta_ip,
  modi_fecha,
  modi_usr,
  modi_ip,
  baja_fecha,
  baja_usr ,
  baja_ip,
  apellido,
  nombre,
  pagado_fecha,
  agregado_manual
from acta_no_os_periodos
where acta_id=$1
and baja_fecha is null
order by periodo, cuil asc;
$BODY$
  LANGUAGE sql VOLATILE

