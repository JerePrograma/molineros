-- Function: trae_codpostal(character varying)

-- DROP FUNCTION trae_codpostal(character varying);

CREATE OR REPLACE FUNCTION trae_codpostal(IN calle_p character varying)
  RETURNS TABLE(calle character varying, cp integer, altura_inicio integer, altura_fin integer) AS
$BODY$
select calle,
    cp,
    altura_inicio,
    altura_fin
    from cod_postal_caba 
    WHERE calle ILIKE $1
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_codpostal(character varying) OWNER TO postgres;
