CREATE OR REPLACE FUNCTION lastdateofmonth(date) 
RETURNS date
    LANGUAGE sql
    AS $BODY$
SELECT CAST(date_trunc('month', $1) + interval '1 month'
- interval '1 day' as date);
$BODY$;


ALTER FUNCTION public.lastdateofmonth(date) OWNER TO postgres;

--
