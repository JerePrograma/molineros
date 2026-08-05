
CREATE OR REPLACE FUNCTION buscar_id_concepto_ajuste( ) 
RETURNS TABLE(id integer)
    LANGUAGE sql
    AS $BODY$

	select id from concepto_maestro where descripcion_original = 'AJUSTE';
 
$BODY$;
