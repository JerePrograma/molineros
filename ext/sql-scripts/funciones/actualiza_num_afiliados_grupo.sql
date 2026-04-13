CREATE OR REPLACE FUNCTION actualiza_num_afiliados_grupo(cuil_p character varying, inte_p integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare n_ospim integer;
declare f_ospim timestamp without time zone;
declare n_uoma integer;
declare f_uoma timestamp without time zone;
declare n_amtima integer;
declare f_amtima timestamp without time zone;
BEGIN

n_ospim = a1.id_ospim
		from afiliado a1 
		where a1.cuil_titular = cuil_p and a1.inte = 0;
f_ospim = a1.id_ospim_baja_fecha
		from afiliado a1 
		where a1.cuil_titular = cuil_p and a1.inte = 0;
				
n_uoma = a1.id_uoma
		from afiliado a1 
		where a1.cuil_titular = cuil_p and a1.inte = 0;
f_uoma = a1.id_uoma_baja_fecha
		from afiliado a1 
		where a1.cuil_titular = cuil_p and a1.inte = 0;

n_amtima = a1.id_amtima
		from afiliado a1 
		where a1.cuil_titular = cuil_p and a1.inte = 0;
f_amtima = a1.id_amtima_baja_fecha
		from afiliado a1 
		where a1.cuil_titular = cuil_p and a1.inte = 0;
		
	 
update afiliado set id_ospim = n_ospim, id_uoma = n_uoma, id_amtima = n_amtima, id_amtima_baja_fecha =f_amtima,
id_uoma_baja_fecha = f_uoma,id_ospim_baja_fecha = f_ospim
where cuil_titular = cuil_p and inte=inte_p;		

RETURN 1;
END;
$BODY$;


ALTER FUNCTION public.actualiza_num_afiliados_grupo(cuil_p character varying) OWNER TO postgres;

--
