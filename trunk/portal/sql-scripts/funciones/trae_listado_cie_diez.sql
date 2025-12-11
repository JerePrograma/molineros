CREATE OR REPLACE FUNCTION trae_listado_cie_diez() 
RETURNS TABLE(codigo character varying,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select codigo, 
       codigo || '-' || substring(descripcion from 1 for 100)          
from cie_diez
order by codigo
$BODY$;