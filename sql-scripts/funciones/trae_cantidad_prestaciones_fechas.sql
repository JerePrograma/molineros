drop function trae_cantidad_prestaciones_fechas(cuil_v character,
 inte_v integer,
 prestacion integer,
 fecha_desde_v timestamp without time zone,
 fecha_hasta_v timestamp without time zone
 )

CREATE OR REPLACE FUNCTION trae_cantidad_prestaciones_fechas(cuil_v character,
 inte_v integer,
 prestacion integer,
 fecha_desde_v timestamp without time zone,
 fecha_hasta_v timestamp without time zone,
 cuit_v character varying,
 sucu_v character varying
 )
  RETURNS numeric AS
$BODY$
declare cantidad_reintegros numeric;
declare cantidad_liquidaciones numeric;

begin

cantidad_reintegros = sum(rp.cantidad)  from reintegro r, reintegro_prestacion rp
where 
r.id_reintegro = rp.id_reintegro
and r.cuil_titular = $1
and r.inte = $2
and rp.id_prestacion = $3
and rp.periodo >= $4
and rp.periodo <= $5
and (rp.cuit_entidad = $6 or rp.cuit = $6)
and r.baja_usr is null;

if (cantidad_reintegros is null) then cantidad_reintegros=0; end if;
	
cantidad_liquidaciones = sum(lp.cantidad)  from liquidacion_prestacion lp, liquidacion l, prestador p 
where 
    lp.cuil_titular = $1
and lp.inte = $2
and lp.id_prestacion = $3
and lp.periodo >= $4
and lp.periodo <= $5
and lp.baja_usr is null
and lp.id_liquidacion = l.id_liquidacion
and l.id_prestador = p.id_prestador
and l.baja_usr is null
and p.cuit = $6;

if (cantidad_liquidaciones is null) then cantidad_liquidaciones=0; end if;

return cantidad_reintegros + cantidad_liquidaciones;	

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
