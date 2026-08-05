-- Function: "isnull"(timestamp, timestamp)

-- DROP FUNCTION "isnull"(timestamp, timestamp);

CREATE OR REPLACE FUNCTION "isnull"(timestamp, timestamp)
  RETURNS timestamp AS
$BODY$ 
SELECT (CASE (SELECT $1 is null) 
		WHEN true 
		THEN $2 
		ELSE $1 
	END) AS RESULT
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION "isnull"(timestamp, timestamp) OWNER TO postgres;
COMMENT ON FUNCTION "isnull"(timestamp, timestamp) IS 'Retorna o 2º arg se o 1º for
nulo';