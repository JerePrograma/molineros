-- Function: borra_acta_no_os(integer, date, character varying)

-- DROP FUNCTION borra_acta_no_os(integer, date, character varying);

CREATE OR REPLACE FUNCTION borra_acta_no_os(p_acta_id integer, p_baja_fecha date, p_usuario character varying)
  RETURNS integer AS
$BODY$
declare res integer;
declare borrar integer;
  begin
	 borrar=0;
	 borrar=(select 1 where exists (select 1 
									 from acta_no_os 
									 where acta_cerrada =false 
									 and  id = p_acta_id));
									 
	if (borrar=1) then
		--delete from acta_periodos where acta_id = p_acta_id;
		--delete from acta_detalle_inspectores where acta_id = p_acta_id;
		delete from acta_no_os_pagos where acta_relacion_id in (select id from acta_relacion where acta_id = p_acta_id);
		delete from acta_no_os_relacion where acta_id = p_acta_id; 

		CREATE TEMP TABLE cheque_tmp (nro_cheque numeric(15,0), banco_cheque integer);
		
		insert into  cheque_tmp
		select  nro_cheque, banco_cheque
		from acta_no_os_pagos
		where banco_cheque is not null
		and nro_cheque is not null
		and acta_id = p_acta_id;
		
		delete from acta_no_os_pagos where acta_id = p_acta_id;

		delete from cheque 
		using  cheque_tmp ap
		where  cheque.nro_cheque  = ap.nro_cheque
		and cheque.id_banco = ap.banco_cheque;

		delete from acta_no_os_inspector where id_acta = p_acta_id;	
		delete from acta_no_os where id = p_acta_id;	

		drop TABLE cheque_tmp;
	else
		update acta_no_os 
		set  baja_fecha = p_baja_fecha,
		baja_usr = p_usuario
		where id = p_acta_id;
		
		update acta_no_os_pagos set baja_fecha = p_baja_fecha,
		baja_usr = p_usuario
		where acta_id = p_acta_id;
		
		update acta_no_os_relacion set baja_fecha = p_baja_fecha,
		baja_usr = p_usuario
		where acta_id = p_acta_id;
		
		update acta_no_os_periodos set baja_fecha = p_baja_fecha,
		baja_usr = p_usuario
		where acta_id = p_acta_id;
	end if;

	return 1;


  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION borra_acta_no_os(integer, date, character varying)
  OWNER TO postgres;

