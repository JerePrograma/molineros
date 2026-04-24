drop FUNCTION trae_motivos_debito(); 

CREATE OR REPLACE FUNCTION trae_motivos_debito() 
RETURNS TABLE(id_motivo_debito smallint,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_motivo, 
       descripcion 
from motivo m
where m.compro_tipo = 'NDB' 
order by descripcion
$BODY$;