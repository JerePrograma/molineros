CREATE OR REPLACE FUNCTION trae_concepto_egreso_amtima() 
RETURNS TABLE(id integer,
 descripcion character varying,
 numero character varying,
 cuenta character varying)
    LANGUAGE sql
    AS $BODY$
  
 	select id,
    descripcion,
    numero,
    pc.cuenta
    from conceptos_amtima  c 
    inner join plan_cuentas_amtima pc
    on c.numero_cuenta = pc.numero
    where egreso = true
    order by descripcion;

$BODY$;


ALTER FUNCTION public.trae_concepto_egreso_amtima() OWNER TO postgres;

--