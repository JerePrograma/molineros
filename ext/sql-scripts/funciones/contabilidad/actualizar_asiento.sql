 
CREATE OR REPLACE function actualizar_asiento(
 p_fecha  timestamp without time zone,
 p_descripcion character varying,
 p_automatico boolean,
 p_numero integer,
 p_ejercicio_desde date,
 p_ejercicio_hasta date,
 p_username character varying,
 p_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
update  asiento
set fecha = p_fecha,  descripcion = p_descripcion, 
numero = p_numero, 
modi_fecha = localtimestamp,
modi_usr = p_username
where id = p_id;
 
return 1;
END;
$BODY$;
