CREATE OR REPLACE FUNCTION autorizaciones.valida_plan_molinero(cuil_titular_p character varying, inte_p integer)
  RETURNS boolean AS
$BODY$
declare resultado boolean;
BEGIN

resultado= p.molinero from public.afiliado a, public.plan p, public.afi_plan ap
where a.cuil_titular=$1
and a.inte=$2
and (a.cuil_titular=ap.cuil_titular)
and ap.inte=0
and ap.vigen_desde=(select max(vigen_desde) from afi_plan app
		    where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte
		    and (app.baja_fecha is null or app.baja_fecha>current_date))
and (ap.baja_fecha is null)
and (ap.id_plan=p.id_plan);

if resultado is null then resultado=false; end if;

return resultado;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION autorizaciones.valida_plan_molinero(character varying, integer)
  OWNER TO postgres;