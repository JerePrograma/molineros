CREATE OR REPLACE FUNCTION cambiar_recibo_efectivo_estado(p_recibo_ingreso_id integer, p_estado_id integer,   p_user character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare res integer;
BEGIN
	update recibo_ingresos set id_estado_efectivo = p_estado_id, modi_fecha = localtimestamp, modi_usr = p_user where id = p_recibo_ingreso_id;
	
return 1;
END;
$BODY$;


ALTER FUNCTION public.cambiar_recibo_efectivo_estado(p_recibo_ingreso_id integer, p_estado_id integer,   p_user character varying)   OWNER TO postgres;

--
