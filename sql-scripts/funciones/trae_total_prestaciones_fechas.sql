drop function trae_total_prestaciones_fechas(cuil_v character,
 inte_v integer,
 prestacion integer,
 fecha_desde_v timestamp without time zone,
 fecha_hasta_v timestamp without time zone);

 
 
CREATE OR REPLACE FUNCTION trae_total_prestaciones_fechas(cuil_v character,
 inte_v integer,
 prestacion integer,
 fecha_desde_v timestamp without time zone,
 fecha_hasta_v timestamp without time zone,
 cuit_v character varying,
 sucu_v character varying
 )

RETURNS NUMERIC AS
$BODY$
declare cantidad_prestaciones_reintegros numeric;
declare cantidad_prestaciones_liquidaciones numeric;

begin

cantidad_prestaciones_reintegros = sum(rp.cantidad * rp.importe)  from reintegro r, reintegro_prestacion rp
where 
r.id_reintegro = rp.id_reintegro
and r.cuil_titular = $1
and r.inte = $2
and rp.id_prestacion = $3
and rp.periodo >= $4
and rp.periodo <= $5
and (rp.cuit_entidad = $6 or rp.cuit = $6)
and r.baja_usr is null;

if (cantidad_prestaciones_reintegros is null) then cantidad_prestaciones_reintegros=0; end if;
	
cantidad_prestaciones_liquidaciones = sum(lp.cantidad * lp.importe)  from liquidacion_prestacion lp, liquidacion l, prestador p 
where lp.cuil_titular = $1
and lp.inte = $2
and lp.id_prestacion = $3
and lp.periodo >= $4
and lp.periodo <= $5
and lp.baja_usr is null
and lp.id_liquidacion = l.id_liquidacion
and l.id_prestador = p.id_prestador
and p.cuit = $6
and l.baja_usr is null;

if (cantidad_prestaciones_liquidaciones is null) then cantidad_prestaciones_liquidaciones=0; end if;

return cantidad_prestaciones_reintegros + cantidad_prestaciones_liquidaciones;	

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
