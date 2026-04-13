
DROP buscar_asientos(p_desde date, p_hasta date) ;

CREATE OR REPLACE FUNCTION buscar_asientos(p_desde date, p_hasta date, 
	p_asiento_desde integer, p_asiento_hasta integer, p_incluir_automaticos boolean, p_incluir_manuales boolean) 
RETURNS TABLE(
 id integer,
 fecha timestamp without time zone,
 descripcion character varying,
 automatico boolean,
 numero integer,
 ejercicio_desde date,
 ejercicio_hasta date)
    LANGUAGE sql
    AS $BODY$

select  id ,
 fecha ,
 descripcion ,
 automatico ,
 numero ,
 ejercicio_desde ,
 ejercicio_hasta 
 from asiento a
 where cast(fecha as date)>=cast($1 as date)
 and cast(fecha as date)<=cast($2 as date)
 and ($3 is null or ($3 is not null and a.numero >= $3))
 and ($4 is null or ($4 is not null and a.numero <= $4))
 and ($5 = true or ($5 = false and a.automatico = false))
 and ($6 = true or ($6 = false and a.automatico = true))
 order by numero;

$BODY$;
