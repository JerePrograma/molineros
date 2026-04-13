
CREATE OR REPLACE FUNCTION cambiar_estado_cheque(
 p_nro_cheque numeric, p_id_banco integer, p_estado_id integer , p_user character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

	  update cheque set id_estado = p_estado_id where nro_cheque = p_nro_cheque and id_banco = p_id_banco;
  return 1;
  end;  
$BODY$;


ALTER FUNCTION public.cambiar_estado_cheque(
 p_nro_cheque numeric, p_id_banco integer, p_estado_id integer , p_user character varying)   OWNER TO postgres;

--
