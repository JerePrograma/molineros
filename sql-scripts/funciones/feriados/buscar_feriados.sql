CREATE OR REPLACE FUNCTION buscar_feriados() 
RETURNS TABLE(
   feriado date)
    LANGUAGE sql
    AS $BODY$
  
    

select fecha from feriado;

$BODY$;