CREATE OR REPLACE FUNCTION busacar_aportes_para_fecha_vigencia_afi(p_cuil character varying, p_inte integer)
  RETURNS SETOF aportes_validos AS
$BODY$
    declare resultDom integer;
  begin

	  return  query select ap.id_aporte, ap.fecha_egre from  afi_plan_aporte app, afi_aportes ap, afiliado a
		where app.aporte_alta_fecha = ap.alta_fecha
		and app.id_aporte = ap.id_aporte
		and app.cuil_titular = ap.cuil_titular
		and app.cuil_titular = a.cuil_titular
		and app.inte = ap.inte
		and app.inte = a.inte
		and a.cuil_titular = p_cuil
		and a.inte = 0
		and ap.fecha_ingre <= a.vigen_fecha
		and (ap.fecha_egre is null or ap.fecha_egre >= a.vigen_fecha);
		
	  
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE

