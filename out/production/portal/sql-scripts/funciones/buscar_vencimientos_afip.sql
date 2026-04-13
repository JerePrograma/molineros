CREATE OR REPLACE FUNCTION buscar_vencimientos_afip() 
RETURNS TABLE(	  periodo date, 
  dig_verif_desde integer, 
  dig_verif_hta integer, 
  fecha date)
    LANGUAGE sql
    AS $BODY$

select   periodo , 
  dig_verif_desde , 
  dig_verif_hta , 
  fecha  from afip_vencimiento_cuit;

$BODY$;
ALTER FUNCTION public.buscar_vencimientos_afip()  OWNER TO postgres;

--

