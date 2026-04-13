DROP FUNCTION insertar_asiento(
 p_fecha  timestamp without time zone,
 p_descripcion character varying,
 p_automatico boolean,
 p_numero integer,
 p_ejercicio_desde date,
 p_ejercicio_hasta date,
 p_username character varying);
 
CREATE OR REPLACE FUNCTION insertar_asiento(
 p_fecha  timestamp without time zone,
 p_descripcion character varying,
 p_automatico boolean,
 p_ejercicio_desde date,
 p_ejercicio_hasta date,
 p_username character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare v_nro integer;
BEGIN
v_nro=max(numero)  from asiento where cast(ejercicio_desde as date) = cast(p_ejercicio_desde as date);
if (v_nro is null) then
	v_nro =1;
end if;
v_nro = v_nro +1;
insert into asiento(fecha, descripcion, automatico, numero, ejercicio_desde, ejercicio_hasta, alta_fecha, alta_usr, modi_fecha, modi_usr)
values (p_fecha, p_descripcion, p_automatico, v_nro, p_ejercicio_desde, p_ejercicio_hasta, localtimestamp, p_username, localtimestamp, p_username);
 
return currval('asiento_id_seq');
END;
$BODY$;
