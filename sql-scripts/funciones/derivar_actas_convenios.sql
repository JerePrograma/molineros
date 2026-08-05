create type result_deriva_acta_convenios as (fecha_obligacion date, numero varchar, cuit varchar, razon_soc varchar, cuil varchar, ape_nombre text, periodo date, remuneracion numeric, subtotal numeric, id_terc varchar)
-- Function: derivar_actas_convenios()

-- DROP FUNCTION derivar_actas_convenios();


CREATE OR REPLACE FUNCTION derivar_actas_convenios()
  RETURNS SETOF result_deriva_acta_convenios AS
$BODY$
DECLARE _record_periodos RECORD;
DECLARE _record_pagos RECORD;
DECLARE pendiente_a_derivar_v numeric;
DECLARE subtotal_aux numeric;
DECLARE nomina_aux numeric;
BEGIN

drop table if exists periodos_a_pagar;
drop table if exists pagos_pendientes;
drop table if exists nomina_a_derivar;

--NOMINA DE ACTAS DONDE EXISTE UN PAGO SIN DERIVAR
create temp table periodos_a_pagar as
select a.id, a.numero, a.cuit, a.sucursal, a.fecha_pago, ap.cuil, ap.periodo, ap.remuneracion_declarada, sum(ap.subtotal+ap.interes)*0.75 as subtotal --SE DERIVA EL 75% A OMINT
from acta a
inner join acta_periodos ap
on ap.acta_id=a.id
where a.cierre_fecha is not null 
and (a.alta_usr ='agerbasi' or a.alta_usr = 'ezambello') 
and a.baja_fecha is null
and ap.baja_fecha is null
and ap.subtotal <>0
and molinera=false
and not exists (select 1 from liquidacion_actas la where la.cuit=a.cuit and la.cuil=ap.cuil and la.periodo_d=ap.periodo)
and not exists (select 1 from acta_pagos apb where convenio_acta_id is not null and apb.acta_id=ap.acta_id and apb.baja_fecha is null)
and acta_id not in (1453,1398)
group by a.id, a.numero, a.cuit, a.sucursal, a.fecha_pago, ap.cuil, ap.periodo, ap.remuneracion_declarada
order by numero;


--ACTAS RELACIONADAS
insert into periodos_a_pagar (id, numero, cuit, sucursal, fecha_pago, cuil, periodo, remuneracion_declarada, subtotal)
select a.id, a.numero, a.cuit, a.sucursal, a.fecha_pago, ape.cuil, ape.periodo, ape.remuneracion_declarada, sum(ape.subtotal+ape.interes)*0.75 
from acta a
inner join acta_relacion ar
on a.id=ar.acta_id
inner join acta_periodos ape
on ar.acta_relacionada_id=ape.acta_id
where a.cierre_fecha is not null 
and (a.alta_usr ='agerbasi' or a.alta_usr = 'ezambello') 
and a.baja_fecha is null
and ar.baja_fecha is null
and ape.baja_fecha is null
group by a.id, a.numero, a.cuit, a.sucursal, a.fecha_pago,ape.cuil, ape.periodo, ape.remuneracion_declarada;


--IMPORTES PENDIENTES DE ESTA NOMINA, ANTERIORES AL DIA DE LA FECHA --CHEQUES
create temp table pagos_pendientes as
select apa.acta_id, rcp.recibo_concepto_id, rcp.recibo_ingreso_id, trunc(rcp.pendiente_derivar,2) as pendiente_derivar
from recibo_ingresos ri, acta_pagos apa, cheque c, recibo_conceptos rc, recibo_conceptos_pagos rcp, recibo rcb
where ri.fecha<=CURRENT_DATE
and apa.baja_fecha is null
and apa.acta_relacion_id is null
and apa.convenio_acta_id is null
and rcp.recibo_ingreso_id = ri.id
and ri.nro_cheque = c.nro_cheque
and ri.id_banco = c.id_banco
and rcp.recibo_concepto_id=rc.id
and ri.id=rcp.recibo_ingreso_id
and c.id_estado in (3,4,6)
and rc.acta_id is not null
and rc.acta_id=apa.acta_id
and rcb.id=ri.recibo_id
and rcb.fecha>'20110101'
and rcb.baja_fecha is null
and exists (select 1 from periodos_a_pagar pap where pap.id=apa.acta_id);


--EFECTIVO
insert into pagos_pendientes(acta_id, recibo_concepto_id, recibo_ingreso_id, pendiente_derivar)
select apa.acta_id, rcp.recibo_concepto_id, rcp.recibo_ingreso_id, trunc(rcp.pendiente_derivar,2) as pendiente_derivar
from recibo_ingresos ri, acta_pagos apa, recibo_conceptos rc, recibo_conceptos_pagos rcp, recibo rcb
where ri.fecha<=CURRENT_DATE
and apa.baja_fecha is null
and apa.acta_relacion_id is null
and apa.convenio_acta_id is null
and apa.id=rc.acta_id
and rcp.recibo_concepto_id=rc.id
and ri.id=rcp.recibo_ingreso_id
and rc.acta_id is not null
and ri.nro_cheque is null
and rcb.id=ri.recibo_id
and rcb.fecha>'20110101'
and rcb.baja_fecha is null
and exists (select 1 from periodos_a_pagar pap where pap.id=apa.acta_id);



/*select acta_id, id as pago_id, trunc(sum(pendiente_derivar),2) as pendiente_derivar
from acta_pagos apa
where tipo='PGO'
and fecha_pago<=CURRENT_DATE
and baja_fecha is null
and acta_relacion_id is null
and convenio_acta_id is null
--and exists (select 1 from periodos_a_pagar pap where pap.id=apa.acta_id)
group by acta_id, apa.id;*/


create temp table nomina_a_derivar (numero varchar, cuit varchar, cuil varchar, periodo date, fecha_obligacion date, remuneracion_declarada numeric, subtotal numeric, acta_convenio varchar);

FOR _record_pagos IN SELECT acta_id, recibo_concepto_id, recibo_ingreso_id, pendiente_derivar from pagos_pendientes order by acta_id LOOP
	RAISE INFO 'DENTRO DEL FOR SUPERIOR';
        pendiente_a_derivar_v=_record_pagos.pendiente_derivar;
        subtotal_aux=1;
	nomina_aux=1;
        --WHILE (subtotal_aux>0 AND subtotal_aux<pendiente_a_derivar_v AND  nomina_aux>0) LOOP
        RAISE INFO 'DENTRO DEL WHILE';
		FOR _record_periodos IN SELECT numero, cuit, cuil, periodo,remuneracion_declarada, subtotal, fecha_pago, count(*) as total  
					FROM periodos_a_pagar pap
					WHERE id=_record_pagos.acta_id 
					and not exists (select 1 from nomina_a_derivar nad where nad.numero=pap.numero and nad.cuit=pap.cuit and nad.cuil=pap.cuil and nad.periodo=pap.periodo)
					group by numero, cuit, cuil, periodo, remuneracion_declarada, subtotal, fecha_pago
					LOOP
		        RAISE INFO 'DENTRO DEL FOR';
			nomina_aux=_record_periodos.total;
			subtotal_aux=round(cast(_record_periodos.subtotal as numeric),1);
			RAISE INFO 'subtotal_aux=% pendiente_a_derivar_v=%', subtotal_aux, pendiente_a_derivar_v;
			IF (subtotal_aux<=pendiente_a_derivar_v) then
				
				INSERT INTO nomina_a_derivar(numero, cuit, cuil, periodo, fecha_obligacion, remuneracion_declarada, subtotal, acta_convenio)
				values(_record_periodos.numero, _record_periodos.cuit, _record_periodos.cuil, _record_periodos.periodo, _record_periodos.fecha_pago, _record_periodos.remuneracion_declarada, subtotal_aux, 'A');
				RAISE INFO 'antes % %', pendiente_a_derivar_v,_record_periodos.cuil ;
				pendiente_a_derivar_v=pendiente_a_derivar_v-subtotal_aux;
				nomina_aux=nomina_aux-1;
				RAISE INFO 'despues %1', pendiente_a_derivar_v;

			end if;
		END LOOP;
	--END LOOP;

	RAISE INFO 'SETEO PENDIENTE %1', pendiente_a_derivar_v;
	update recibo_conceptos_pagos apb
	set pendiente_derivar=pendiente_a_derivar_v	
	where apb.recibo_concepto_id=_record_pagos.recibo_concepto_id
	and apb.recibo_ingreso_id=_record_pagos.recibo_ingreso_id;		    	

END LOOP;

perform derivar_convenios();

RAISE INFO 'INSERTO EN LIQUIDACIONES HISTORICAS';
INSERT INTO liquidacion_actas(
            numero, cuit, cuil, periodo_d, remune, omint_d, nuevo, fecha_obligacion, fecha_liq, acta_convenio)
select numero, cuit, cuil, periodo, remuneracion_declarada,subtotal, true, fecha_obligacion, current_date, acta_convenio
from nomina_a_derivar;

return query 
select fecha_obligacion,numero, e.cuit, e.razon_soc, n.cuil, a.apellido||', '||a.nombre, n.periodo, remuneracion_declarada, subtotal, ats.id_tercerizadora
from nomina_a_derivar n
left outer join empresa e
on e.cuit=n.cuit
and e.sucursal='000'
left outer join afiliado a
on a.cuil_titular=n.cuil
and inte=0
left outer join afi_tercerizadora_servicio  ats
on ats.cuil_titular=a.cuil_titular
and ats.inte=0
and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>current_date)
and (ats.baja_fecha is null or ats.baja_fecha>current_date);

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE