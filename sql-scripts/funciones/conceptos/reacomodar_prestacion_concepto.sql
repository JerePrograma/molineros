drop FUNCTION reacomodar_prestacion_concepto(
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_original_id integer,
 p_concepto_nuevo_id integer,
 p_concepto_nuevo_particion_posterior_id integer);
 
DROP FUNCTION reacomodar_prestacion_concepto(
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_original_id integer,
 p_concepto_nuevo_id integer,
 p_concepto_nuevo_particion_posterior_id integer,
 p_usr character varying) ;