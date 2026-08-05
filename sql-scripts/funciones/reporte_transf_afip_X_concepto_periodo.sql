/*create type result_reporte_afip_conc_periodo as (cod_conc varchar,
						 descripcion varchar,
						 total numeric,
						 dto_anticipo numeric)
*/

CREATE OR REPLACE FUNCTION reporte_transf_afip_x_concepto_periodo(periodo_v date)
  RETURNS SETOF result_reporte_afip_conc_periodo AS
$BODY$
BEGIN

drop table if exists temp_afip;
drop table if exists temp_anticipos;

CREATE TEMP TABLE temp_afip AS
select cod_conc, descripcion, sum(importe) as importe, cast(0 as numeric) as dto_anticipo
from os_aportes_detalle o, conceptos_transf_os c
where o.concepto_transf = c.cod_conc
and fecha_transf>cast(periodo_v as date) and fecha_transf<=lastDateOfMonth(periodo_v)
group by cod_conc, descripcion
order by descripcion;

--DTO. ANTICIPOS
CREATE TEMP TABLE temp_anticipos AS
select cod_conc, descripcion, sum(importe)*-1 as dto
from os_aportes_detalle o, conceptos_transf_os c
where o.concepto_transf = c.cod_conc
and fecha_transf>cast(periodo_v as date) and fecha_transf<=lastDateOfMonth(periodo_v)
and sucur like 'NO_%'
group by cod_conc, descripcion
order by descripcion;

update temp_afip a
set dto_anticipo=t.dto
from temp_anticipos t
where a.cod_conc=t.cod_conc;

--AUTOGESTION
insert into temp_afip(cod_conc, descripcion, importe)
select 'HAU','Hospitales Autogestión',sum(importe_transferencia)*-1
from detalle_transferencia_externa 
where fecha_transferencia>cast(periodo_v as date) and fecha_transferencia<=lastDateOfMonth(periodo_v);

return query
select cod_conc, descripcion, importe, dto_anticipo
from temp_afip
order by cod_conc;


END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
