CREATE OR REPLACE FUNCTION trae_ultimo_per_liq_debitos_terceros() 
RETURNS TABLE(periodo timestamp without time zone)
    LANGUAGE sql
    AS $BODY$

select max(periodo_hasta) from liquidacion_debitos_terceros;
$BODY$;