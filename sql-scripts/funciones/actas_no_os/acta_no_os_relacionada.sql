CREATE OR REPLACE FUNCTION acta_no_os_relacionada(p_acta_id integer)
  RETURNS integer AS
$BODY$
declare res integer;
  begin
	res=0;
	res=(select 1 where exists (select  1 from acta_no_os_relacion ar
					inner join acta_no_os a
					on ar.acta_id = a.id
					and ar.acta_relacionada_id = p_acta_id 
					and a.cierre_fecha is not null 
					and a.baja_fecha is null));
	if (res is null or res = 0 ) then 
		res=(select 1 where exists (select  1 from convenio_actas_no_os ca, convenio_no_os c
									where acta_id = p_acta_id
									and ca.convenio_id = c.id
									and ca.baja_fecha is null
									and c.baja_fecha is null));
	end if;		
	
	return res;
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE

