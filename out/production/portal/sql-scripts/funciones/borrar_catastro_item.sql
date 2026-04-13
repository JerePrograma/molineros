CREATE OR REPLACE FUNCTION borrar_catastro_item (
 c_id integer,
 c_usuario character varying)

RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$  
  begin		  
	  delete from afi_catastral_odo c where 		 	
		c.id = c_id;	  
  return  0;
  end;
$BODY$;