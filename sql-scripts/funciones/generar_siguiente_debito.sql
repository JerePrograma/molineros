-- Function: generar_siguiente_debito()

-- DROP FUNCTION generar_siguiente_debito();

CREATE OR REPLACE FUNCTION generar_siguiente_debito(p_tipo character varying)
  RETURNS integer AS
$BODY$
    declare sec integer;
  begin

update compro_tipo ct set secuencia = secuencia + 1 where ct.compro_tipo = 'NDB'; 
	  
sec = secuencia 
from compro_tipo
where compro_tipo = 'NDB';
   
return sec;
end; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION generar_siguiente_debito() OWNER TO postgres;
