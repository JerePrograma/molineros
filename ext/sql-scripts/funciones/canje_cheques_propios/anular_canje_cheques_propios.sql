CREATE OR REPLACE FUNCTION anular_canje_cheques_propios(p_id integer,
 p_usuario character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

	update canje_cheques_propios  set baja_fecha = localtimestamp , baja_usr = p_usuario where id = p_id;
	return 1;
  end;  
$BODY$;

