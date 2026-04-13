drop FUNCTION reemplazar_concepto(
 p_id_concepto_viejo integer, 
 p_concepto_viejo_valido_desde date,
 p_concepto_viejo_valido_hasta date,
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
 
 CREATE OR REPLACE FUNCTION reemplazar_concepto(
 p_id_concepto_viejo integer, 
 p_concepto_viejo_valido_desde date,
 p_concepto_viejo_valido_hasta date,
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
	 		p_id_concepto_viejo
	 	);
 	
 	--el nuevo concepto se solapa con el fin del concepto anterior
	if (p_valido_desde > p_concepto_viejo_valido_desde and  p_valido_hasta  = p_concepto_viejo_valido_hasta) then
		UPDATE conceptos
		   SET  valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_concepto_maestro=p_id_concepto_viejo
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
	end if;
	--el concepto nuevo se solapa con el inicio del concepto anterior
 	if (p_valido_desde = p_concepto_viejo_valido_desde and  p_valido_hasta  < p_concepto_viejo_valido_hasta) then
		UPDATE conceptos
		   SET  valido_desde = p_valido_hasta + interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_concepto_maestro=p_id_concepto_viejo
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
	end if;
	
	--el concepto nuevo se solapa en el medio del periodo de validez del anterior
 	if (p_valido_desde > p_concepto_viejo_valido_desde and  p_valido_hasta  < p_concepto_viejo_valido_hasta) then
		 --inserto y manejo lo que quedo a la izquierda
		 insert into conceptos (descripcion,  numero_cuenta,  liquidaciones ,   egreso,  ingreso,  cuenta_pasivo,  sub_egreso,  sub_ingreso,  valido_desde,
		  valido_hasta ,  id_plan_cuenta,  id_plan_cuenta_pasivo, alta_fecha, alta_usr, modi_fecha, modi_usr, id_concepto_maestro) 
		  select  descripcion,  numero_cuenta,  liquidaciones ,   egreso,  ingreso,  cuenta_pasivo,  sub_egreso,  sub_ingreso,
		 		 p_valido_hasta + interval '1 day' ,  p_concepto_viejo_valido_hasta, id_plan_cuenta,  id_plan_cuenta_pasivo,
		 		 current_date, p_usr, current_date, p_usr, p_id_concepto_viejo
		  from conceptos 
 		 WHERE id_concepto_maestro=p_id_concepto_viejo
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
			
 		--manejo lo que quedo a la derecha
		UPDATE conceptos
		   SET  valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_concepto_maestro=p_id_concepto_viejo
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
	end if; 
 	
 	 return p_id_concepto_viejo;
END;
$BODY$;
