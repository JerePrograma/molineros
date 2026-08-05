-- Function: trae_direcciones(character varying)

-- DROP FUNCTION trae_direcciones(character varying);

CREATE OR REPLACE FUNCTION trae_direcciones(calle_p character varying)
  RETURNS SETOF character varying AS
$BODY$
select DISTINCT calle
    from cod_postal_caba 
    WHERE calle ILIKE $1||'%'  
    group by calle    
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_direcciones(character varying) OWNER TO postgres;
