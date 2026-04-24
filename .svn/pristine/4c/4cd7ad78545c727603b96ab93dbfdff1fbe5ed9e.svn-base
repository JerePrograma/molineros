CREATE OR REPLACE FUNCTION trae_motivos_baja_afiliado() 
RETURNS TABLE(id_motivo_baja integer,
 descripcion character varying,
 meses_a_baja integer)
    LANGUAGE sql
    AS $BODY$
select id_motivo_baja, 
       descripcion,
       meses_a_baja 
from motivo_baja
order by descripcion
$BODY$;



