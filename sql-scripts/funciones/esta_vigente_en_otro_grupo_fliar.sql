create function esta_vigente_en_otro_grupo_fliar(IN cuil_titular_p character varying, IN inte_p integer)

  RETURNS TABLE(cuil_titular character varying, cuil character varying, inte integer) AS
$BODY$

select cuil_titular, cuil, inte 
from afiliado a 
where cuil_titular= $1 and inte= $2
and exists (select 1 
            from afiliado a2 
            where a.cuil_titular<>a2.cuil_titular and a.cuil=a2.cuil 
	    and (baja_fecha is null or baja_fecha>current_date)) 
and baja_fecha<current_date;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 100;