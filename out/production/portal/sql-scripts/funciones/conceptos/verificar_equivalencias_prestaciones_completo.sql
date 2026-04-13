DROP verificar_equivalencias_prestaciones_completo(p_fecha_hasta date);
CREATE OR REPLACE FUNCTION verificar_equivalencias_prestaciones_completo(p_fecha_desde date, p_fecha_hasta date) 
RETURNS boolean
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
  begin
		res=0;
		res = (case when exists (select 1 from nomenclador n 
				where not exists ( select 1 from nomenclador_conceptos where id_prestacion = n.id_prestacion
						and cast(valido_desde  as date) <= cast(p_fecha_desde as date) 
						and cast(valido_hasta as date) >= cast(p_fecha_desde as date))
				and baja_fecha is null) then 1 else 0 end);
		
		if (res is null or res = 0 ) then 
			res = 0;
		end if;		
		
		if res <> 0 then
			return false;
		end if;
		if res = 0 then
			return true;
		end if;
	
  end;  
$BODY$; 