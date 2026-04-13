
CREATE OR REPLACE FUNCTION generar_siguiente_debito_amtima(p_tipo character varying)
  RETURNS integer AS
$BODY$
    declare sec integer;
  begin

update compro_tipo_amtima ct set secuencia = secuencia + 1 where ct.compro_tipo = 'NDB'; 
	  
sec = secuencia 
from compro_tipo_amtima
where compro_tipo = 'NDB';
   
return sec;
end; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
