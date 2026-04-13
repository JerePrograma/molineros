CREATE OR REPLACE FUNCTION borrar_empresa(p_cuit character varying,
 p_sucu character varying,
 p_usuario character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
  begin

  res=1;
  res=(select 0 where exists (select  0 from afi_situ_laboral where cuit = p_cuit and sucursal = p_sucu));

   if (res is null) then
	update empresa 
	set  baja_fecha = LOCALTIMESTAMP,
	baja_usr = p_usuario
	where cuit = p_cuit
	and sucursal = p_sucu;
	res=1;

  end if;

  return res;
	
  end;  
$BODY$;


ALTER FUNCTION public.borrar_empresa(p_cuit character varying, p_sucu character varying, p_usuario character varying) OWNER TO postgres;

--
