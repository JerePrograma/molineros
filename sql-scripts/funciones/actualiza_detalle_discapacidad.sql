CREATE OR REPLACE FUNCTION actualiza_detalle_discapacidad(
 character varying,
 integer,
 character varying,
 boolean,
 character varying,
 character varying,
 character varying
) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
   
  declare inte_existente numeric;
  begin
	
inte_existente = inte from detalle_discapacidad d where d.cuil_titular = $1 and d.inte = $2;  	  
	  
if (inte_existente is null) then 

INSERT INTO detalle_discapacidad(
            cuil_titular, inte, diagnostico, dependencia, telefono_contacto, 
            alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, cie_diez)
    VALUES ($1, $2, $3, $4, $5, 
            localtimestamp, $6, localtimestamp, $6, null, null, $7);

else 

update detalle_discapacidad
  set diagnostico = $3, dependencia = $4, telefono_contacto = $5, alta_fecha = localtimestamp,
  alta_usr = $6, modi_fecha = localtimestamp, modi_usr = $6, cie_diez = $7
  where cuil_titular = $1 and inte = $2;

end if;
  
  return 1;
  end;  
$BODY$;
