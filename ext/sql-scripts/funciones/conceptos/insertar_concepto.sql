DROP FUNCTION insertar_concepto(
 p_descripcion character varying,
 p_id_plan_cuentas integer,
 p_id_plan_cuentas_pasivo integer,
 p_liquidaciones boolean,
 p_egreso boolean,
 p_ingreso boolean,
 p_valido_desde date,
 p_valido_hasta date);
 
 drop   FUNCTION insertar_concepto(
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
 
 CREATE OR REPLACE FUNCTION insertar_concepto(
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

insert into concepto_maestro (descripcion_original,  valido_desde, valido_hasta , modi_fecha, modi_usr, alta_fecha, alta_usr) 
 values (p_descripcion, 
 		p_valido_desde,
 		p_valido_hasta,
 		current_date,
 		p_usr,
 		current_date,
 		p_usr
 	);

insert into conceptos (descripcion,  numero_cuenta,  liquidaciones ,   egreso,  ingreso,  cuenta_pasivo,  sub_ingreso, sub_egreso, valido_desde,
  valido_hasta ,  id_plan_cuenta,  id_plan_cuenta_pasivo, modi_fecha, modi_usr, alta_fecha, alta_usr, id_concepto_maestro) 
 values (p_descripcion, 
 		(select numero from plan_cuentas_maestro where id = p_id_plan_cuentas),
 		p_liquidaciones,
 		p_egreso,
 		p_ingreso,
 		(select numero from plan_cuentas_maestro where id = p_id_plan_cuentas_pasivo),
		p_sub_ingreso, 
 		p_sub_egreso,
 		p_valido_desde,
 		p_valido_hasta,
 		p_id_plan_cuentas,
 		p_id_plan_cuentas_pasivo,
 		current_date,
 		p_usr,
 		current_date,
 		p_usr,
 		currval('concepto_maestro_id_seq')
 	);
 
return currval('concepto_maestro_id_seq');
END;
$BODY$;
