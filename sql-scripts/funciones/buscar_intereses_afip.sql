CREATE OR REPLACE FUNCTION buscar_intereses_afip() 
RETURNS TABLE(	fecha_inicio date,
	fecha_fin date,
	interes_por_dia numeric(5,5))
    LANGUAGE sql
    AS $BODY$

select 	fecha_inicio ,
	fecha_fin ,
	interes_por_dia from interes_afip;

$BODY$;
ALTER FUNCTION public.buscar_intereses_afip()  OWNER TO postgres;

--

