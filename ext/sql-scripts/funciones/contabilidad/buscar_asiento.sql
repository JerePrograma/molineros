
CREATE OR REPLACE FUNCTION buscar_asiento(p_id integer) 
RETURNS TABLE(
 id integer,
 fecha timestamp without time zone,
 descripcion character varying,
 automatico boolean,
 numero integer,
 ejercicio_desde date,
 ejercicio_hasta date)
    LANGUAGE sql
    AS $BODY$

select  id ,
 fecha ,
 descripcion ,
 automatico ,
 numero ,
 ejercicio_desde ,
 ejercicio_hasta 
 from asiento
 where id = $1;

$BODY$;
