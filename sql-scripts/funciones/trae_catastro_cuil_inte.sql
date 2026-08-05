CREATE OR REPLACE FUNCTION trae_catastro_cuil_inte(cuil_titular character varying, inte integer)
RETURNS TABLE(
	c_id integer,
    c_cuil_titular character varying,
    c_inte integer,
    c_fecha_prestacion timestamp without time zone,
    c_id_prestacion integer,
    c_codigo character varying,
    c_pieza character varying,
    c_cara character varying    
 )
 LANGUAGE sql
 AS $BODY$   
    
 SELECT id, cuil_titular, inte, fecha, id_prestacion, codigo, pieza, 
       cara
  FROM afi_catastral_odo aco
	  where 
	  aco.cuil_titular = $1
	  and aco.inte = $2;	  
$BODY$;