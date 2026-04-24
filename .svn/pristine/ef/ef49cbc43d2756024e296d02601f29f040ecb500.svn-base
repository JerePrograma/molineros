CREATE OR REPLACE FUNCTION insertar_catastro_item(
    c_cuil_titular character varying,
    c_inte integer,
    c_fecha timestamp without time zone,
    c_id_prestacion integer,
    c_codigo character varying,
    c_pieza character varying,
    c_cara character varying,
    c_user character varying
)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare c_existe integer;    
  begin
	  	c_existe = (select c_pieza from afi_catastral_odo where pieza = c_pieza and cara = c_cara and cuil_titular = c_cuil_titular and inte = c_inte);
		if (c_existe is null) then		
  		  INSERT INTO afi_catastral_odo(
            cuil_titular, inte, fecha, id_prestacion, codigo, pieza, 
            cara, alta_fecha, alta_usr, modi_fecha, modi_usr)
          VALUES (c_cuil_titular, c_inte, c_fecha, c_id_prestacion, c_codigo, c_pieza, c_cara, 
	            localtimestamp, c_user, localtimestamp, c_user);		
  		else 	
			UPDATE afi_catastral_odo
			SET fecha=c_fecha, id_prestacion=c_id_prestacion, codigo=c_codigo, 
			       modi_fecha=localtimestamp, modi_usr=c_user
			WHERE cuil_titular=c_cuil_titular and inte=c_inte and pieza=c_pieza and cara=c_cara;  
    	
		end if;
	
	RETURN 0;
  end;
$BODY$;
