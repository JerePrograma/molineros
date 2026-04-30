drop actualizar_concepto(p_id_concepto integer,
 p_descripcion character varying,
 p_id_plan_cuentas integer,
 p_id_plan_cuentas_pasivo integer,
  p_liquidaciones boolean,
 p_egreso boolean,
 p_ingreso boolean,
 p_valido_desde date,
 p_valido_hasta date,
 p_sub_egreso boolean,
 p_sub_ingreso boolean) ;
 
 CREATE OR REPLACE FUNCTION actualizar_concepto(p_id_concepto integer,
 p_descripcion character varying,
 p_id_plan_cuentas integer,
 p_id_plan_cuentas_pasivo integer,
  p_liquidaciones boolean,
 p_egreso boolean,
 p_ingreso boolean,
 p_valido_desde date,
 p_valido_hasta date,
 p_sub_egreso boolean,
 p_sub_ingreso boolean,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
UPDATE conceptos
   SET descripcion = p_descripcion,
   id_plan_cuenta = p_id_plan_cuentas,
   id_plan_cuenta_pasivo = p_id_plan_cuentas_pasivo,
   liquidaciones = p_liquidaciones,
   egreso = p_egreso,
   ingreso = p_ingreso,
   sub_egreso = p_sub_egreso,
   sub_ingreso = p_sub_ingreso, 
   modi_usr = p_usr,
   modi_fecha = current_date,
   numero_cuenta = (select numero from plan_cuentas_maestro where id = p_id_plan_cuentas),
   cuenta_pasivo = (select numero from plan_cuentas_maestro where id = p_id_plan_cuentas_pasivo)
 WHERE id_concepto_maestro=p_id_concepto
 and cast(valido_desde as date) = cast(p_valido_desde as date)
 and cast(valido_hasta as date) = cast(p_valido_hasta  as date);

return 1;
END;
$BODY$;
