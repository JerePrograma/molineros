CREATE OR REPLACE FUNCTION verificar_acta_con_recibo(p_acta_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
  begin
	res=0;
	res=(select 1 where exists (	select  1 
					from recibo r, 
					recibo_conceptos rc
					where r.id = rc.recibo_id
					and rc.acta_id = p_acta_id
					and r.baja_fecha is null)
	);
	if (res is null or res = 0 ) then 
		res = 0;
	end if;		
	
	return res;
  end;  
$BODY$;