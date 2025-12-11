drop FUNCTION borra_acta(p_acta_id integer,
 p_usuario character varying); 
 
CREATE OR REPLACE FUNCTION borra_acta(p_acta_id integer, p_baja_fecha date,
 p_usuario character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
declare borrar integer;
  begin
	 borrar=0;
	 borrar=(select 1 where exists (select 1 
									 from acta 
									 where acta_cerrada =false 
									 and  id = p_acta_id));
									 
	if (borrar=1) then
		delete from acta_periodos where acta_id = p_acta_id;
		delete from acta_detalle_inspectores where acta_id = p_acta_id;
		delete from acta_pagos where acta_relacion_id in (select id from acta_relacion where acta_id = p_acta_id);
		delete from acta_relacion where acta_id = p_acta_id; 

		CREATE TEMP TABLE cheque_tmp (nro_cheque numeric(15,0), banco_cheque integer);
		
		insert into  cheque_tmp
		select  nro_cheque, banco_cheque
		from acta_pagos
		where banco_cheque is not null
		and nro_cheque is not null
		and acta_id = p_acta_id;
		
		delete from acta_pagos where acta_id = p_acta_id;

		delete from cheque 
		using  cheque_tmp ap
		where  cheque.nro_cheque  = ap.nro_cheque
		and cheque.id_banco = ap.banco_cheque;

		delete from acta_inspector where id_acta = p_acta_id;	
		delete from acta where id = p_acta_id;	

		drop TABLE cheque_tmp;
	else
		update acta 
		set  baja_fecha = p_baja_fecha,
		baja_usr = p_usuario
		where id = p_acta_id;
		
		update acta_pagos set baja_fecha = p_baja_fecha,
		baja_usr = p_usuario
		where acta_id = p_acta_id;
		
		update acta_relacion set baja_fecha = p_baja_fecha,
		baja_usr = p_usuario
		where acta_id = p_acta_id;
		
		update acta_periodos set baja_fecha = p_baja_fecha,
		baja_usr = p_usuario
		where acta_id = p_acta_id;
	end if;

	return 1;


  end;  
$BODY$;

