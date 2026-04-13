CREATE OR REPLACE FUNCTION tiene_conyuge_cuit(cuil_tit character varying, prentesco1 int, parentesco2 int)
  RETURNS integer AS
$BODY$
DECLARE 
 integr INTEGER;
BEGIN
  	
  integr = inte from afiliado 
		where cuil_titular = $1 
		and (id_parentesco_sss = $2 
		    or id_parentesco_sss = $3) 
		and (baja_fecha is null or baja_fecha > current_date) limit 1;
  if integr is null then
  integr = 0;
  end if;
  return integr;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
