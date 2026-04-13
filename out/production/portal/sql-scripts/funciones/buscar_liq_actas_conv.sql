create type buscar_liq_actas_conv as (fecha_liq date, fecha_obligacion varchar, acta varchar, numero_recibo varchar, cuit varchar, razon_soc varchar, 
cuil varchar, afiliado text, periodo date, remuneracion numeric, importe numeric, tercerizadora varchar)

CREATE OR REPLACE FUNCTION buscar_liq_actas_conv(fecha_liq date)
  RETURNS SETOF buscar_liq_actas_conv AS
$BODY$
DECLARE _record_recibos RECORD;
DECLARE _record_recibos_conv RECORD;
BEGIN
drop table if exists aux;
CREATE TEMP TABLE AUX AS
select  fecha_liq,
	fecha_obligacion,
	numero,
	cast('' as varchar) as recibo, 
	e.cuit, 
	e.razon_soc, 
	a.cuil, 
	a.apellido||', '||a.nombre as afiliado, 
	periodo_d, 
	remune, 
	omint_d,
	ts.descripcion
from liquidacion_actas l
left outer join empresa e
on l.cuit=e.cuit
and e.sucursal='000'
left outer join afiliado a
on a.cuil_titular=l.cuil
and a.inte=0
left outer join afi_tercerizadora_servicio ats
on ats.cuil_titular=l.cuil
and ats.inte=0
and (ats.fecha_inicio_pres<=l.fecha_liq)
and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>=l.periodo_d)
and (ats.baja_fecha is null or ats.baja_fecha>=l.periodo_d)
left outer join tercerizadora_servicio ts
on ats.id_tercerizadora=ts.id_tercerizadora
where l.fecha_liq =fecha_liq;


FOR _record_recibos IN SELECT ac.numero as acta_numero,r.numero as recibo_id from recibo_conceptos rc, recibo r, acta ac
			where acta_id in (select distinct a.id from acta a, aux x where a.numero=x.numero)
			and rc.recibo_id=r.id 
			and rc.acta_id=ac.id LOOP
   update aux x
   set recibo= recibo ||'-'|| cast(_record_recibos.recibo_id as varchar)
   where x.numero= _record_recibos.acta_numero;
END LOOP;

FOR _record_recibos_conv IN SELECT co.id as convenio_numero,r.numero as recibo_id 
			    from recibo_conceptos rc, recibo r, convenio co
			    where co.id in (select distinct a.id from convenio a, aux x where a.id=cast(x.numero as integer))
			    and rc.recibo_id=r.id 
			    and rc.convenio_id=co.id LOOP
   update aux x
   set recibo= recibo || cast(_record_recibos_conv.recibo_id as varchar)
   where cast(x.numero as integer)= _record_recibos_conv.convenio_numero;
END LOOP;

--select * from recibo_conceptos rc where rc.convenio_id=171
--select * from convenio where id=171

return query 
select  fecha_liq,
	fecha_obligacion,
	numero,
	recibo, 
	cuit, 
	razon_soc, 
	cuil, 
	afiliado, 
	periodo_d, 
	remune, 
	omint_d,
	descripcion
from aux;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_liq_actas_conv(date) OWNER TO postgres;
