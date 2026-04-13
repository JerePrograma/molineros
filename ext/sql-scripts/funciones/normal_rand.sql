CREATE OR REPLACE FUNCTION normal_rand(integer,
 double precision,
 double precision) 
RETURNS SETOF double precision
    LANGUAGE c STRICT
    AS '$libdir/tablefunc', 'normal_rand';


ALTER FUNCTION public.normal_rand(integer, double precision, double precision) OWNER TO postgres;

--
