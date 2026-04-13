CREATE OR REPLACE FUNCTION inserta_detalle_acta(p_id_aca integer,
 p_desde timestamp without time zone,
 p_hasta timestamp without time zone,
 p_capital numeric,
 p_interes numeric) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
INSERT INTO acta_detalle_inspectores(
            acta_id,  desde, hasta, capital, interes)
    VALUES (p_id_aca, p_desde, p_hasta,  p_capital, p_interes);

return 1;
END;
$BODY$;


ALTER FUNCTION public.inserta_detalle_acta(p_id_aca integer, p_desde timestamp without time zone, p_hasta timestamp without time zone, p_capital numeric, p_interes numeric) OWNER TO postgres;

--
