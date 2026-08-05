CREATE OR REPLACE FUNCTION existen_bonos(tipo_bono_v integer, nro_bono_desde integer, nro_bono_hasta integer)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
DECLARE integr INTEGER;
BEGIN
  	
  integr = count(*) from bonos where tipo_bono=tipo_bono_v and (nro_bono >= nro_bono_desde and nro_bono <= nro_bono_hasta);
  
  return integr;
END;
$BODY$;
