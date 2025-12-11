CREATE OR REPLACE FUNCTION derivar_convenios()
  RETURNS integer AS
$BODY$
DECLARE _record_periodos_conv RECORD;
DECLARE _record_pagos_conv RECORD;
DECLARE pendiente_a_derivar_conv_v numeric;
DECLARE subtotal_aux_conv numeric;
DECLARE nomina_conv_aux numeric;
BEGIN

drop table if exists periodos_conv_pagar;
drop table if exists pagos_pendientes_conv;
--drop table if exists nomina_a_derivar;

--NOMINA DE CONVENIOS DONDE EXISTA UN PAGO SIN DERIVAR
create temp table periodos_conv_pagar as
select c.id, ca.acta_id, c.cuit, c.sucursal, c.fecha_pago, ap.cuil, ap.periodo, ap.remuneracion_declarada, sum(ap.subtotal+ap.interes)*0.75 as subtotal --SE DERIVA A OMINT EL 75%
from convenio c
inner join convenio_actas ca
on c.id=ca.convenio_id
inner join acta_periodos ap
on ap.acta_id=ca.acta_id
where (c.alta_usr ='agerbasi' or c.alta_usr = 'ezambello') 
and ap.subtotal <>0
and not exists (select 1 from liquidacion_actas lab where lab.cuit=c.cuit and lab.cuil=ap.cuil and lab.periodo_d=ap.periodo)
group by c.id, ca.acta_id, c.cuit, c.sucursal, c.fecha_pago, ap.cuil, ap.periodo, ap.remuneracion_declarada;

--PAGOS ANTERIORES AL DIA DE LA FECHA SOBRE ESTA NOMINA
--IMPORTES PENDIENTES DE ESTA NOMINA, ANTERIORES AL DIA DE LA FECHA --CHEQUES
create temp table pagos_pendientes_conv as
select apa.convenio_id, rcp.recibo_concepto_id, rcp.recibo_ingreso_id, trunc(rcp.pendiente_derivar,2) as pendiente_derivar
from recibo_ingresos ri, convenio_pagos apa, cheque c, recibo_conceptos rc, recibo_conceptos_pagos rcp, recibo rcb
where ri.fecha<=CURRENT_DATE
and apa.baja_fecha is null
and rcp.recibo_ingreso_id = ri.id
and ri.nro_cheque = c.nro_cheque
and ri.id_banco = c.id_banco
and rcp.recibo_concepto_id=rc.id
and ri.id=rcp.recibo_ingreso_id
and c.id_estado in (3,4,6)
and rc.convenio_id is not null
and rc.convenio_id=apa.convenio_id
and rcb.id=ri.recibo_id
and rcb.fecha>'20110101'
and rcb.baja_fecha is null
and exists (select 1 from periodos_a_pagar pap where pap.id=apa.convenio_id);


--EFECTIVO
insert into pagos_pendientes_conv(convenio_id, recibo_concepto_id, recibo_ingreso_id, pendiente_derivar)
select apa.convenio_id, rcp.recibo_concepto_id, rcp.recibo_ingreso_id, trunc(rcp.pendiente_derivar,2) as pendiente_derivar
from recibo_ingresos ri, convenio_pagos apa, recibo_conceptos rc, recibo_conceptos_pagos rcp, recibo rcb
where ri.fecha<=CURRENT_DATE
and apa.baja_fecha is null
and apa.id=rc.convenio_id
and rcp.recibo_concepto_id=rc.id
and ri.id=rcp.recibo_ingreso_id
and rc.convenio_id is not null
and ri.nro_cheque is null
and rcb.id=ri.recibo_id
and rcb.fecha>'20110101'
and rcb.baja_fecha is null
and exists (select 1 from periodos_a_pagar pap where pap.id=apa.convenio_id);





/*create temp table pagos_pendientes_conv as
select convenio_id, id as pago_id, sum(pendiente_derivar) as pendiente_derivar
from convenio_pagos apa
where tipo='PGO'
and fecha_pago<=CURRENT_DATE
and baja_fecha is null
and exists (select 1 from periodos_conv_pagar pap where pap.id=apa.convenio_id)
group by convenio_id, apa.id;*/

--create temp table nomina_a_derivar (numero varchar, cuit varchar, cuil varchar, periodo date, remuneracion_declarada numeric, subtotal numeric);

--PAGO DE CONVENIOS
FOR _record_pagos_conv IN SELECT convenio_id, recibo_concepto_id, recibo_ingreso_id, pendiente_derivar from pagos_pendientes_conv order by convenio_id LOOP

        pendiente_a_derivar_conv_v=_record_pagos_conv.pendiente_derivar;
        subtotal_aux_conv=1;
	nomina_conv_aux=1;
        --WHILE (subtotal_aux_conv>0 AND subtotal_aux_conv<pendiente_a_derivar_conv_v AND nomina_conv_aux>0) LOOP
		RAISE INFO 'DENTRO DEL WHILE';
		FOR _record_periodos_conv IN SELECT id, cuit, cuil, periodo,remuneracion_declarada, subtotal, count(*) as total  
					     FROM periodos_conv_pagar pcp
					     WHERE id=_record_pagos_conv.convenio_id 
					     and not exists (select 1 
							     from nomina_a_derivar nad 
							     where cast(nad.numero as integer)=pcp.id 
							     and nad.cuit=pcp.cuit 
							     and nad.cuil=pcp.cuil 
							     and nad.periodo=pcp.periodo)
					     group by id, cuit, cuil, periodo,remuneracion_declarada, subtotal LOOP
			RAISE INFO 'DENTRO DEL FOR: %',_record_periodos_conv.total;		     
			subtotal_aux_conv=round(cast(_record_periodos_conv.subtotal as numeric),1);			
			nomina_conv_aux=_record_periodos_conv.total;
			RAISE INFO 'DENTRO DEL FOR 2 : % nomina!%',subtotal_aux_conv,nomina_conv_aux;		     
			IF (subtotal_aux_conv<=pendiente_a_derivar_conv_v) then
				RAISE INFO 'DENTRO DEL IF';
				INSERT INTO nomina_a_derivar(numero, cuit, cuil, periodo, remuneracion_declarada, subtotal, acta_convenio)
				values(_record_periodos_conv.id, _record_periodos_conv.cuit, _record_periodos_conv.cuil, _record_periodos_conv.periodo, _record_periodos_conv.remuneracion_declarada, subtotal_aux_conv,'C');
				RAISE INFO 'antes CONV %1 %2',_record_periodos_conv.id, pendiente_a_derivar_conv_v;
				pendiente_a_derivar_conv_v=pendiente_a_derivar_conv_v-subtotal_aux_conv;
				RAISE INFO 'despues CONV %1', pendiente_a_derivar_conv_v;
				nomina_conv_aux=nomina_conv_aux-1;
			end if;
		END LOOP;
		RAISE INFO 'subotal_aux_conv=%  pendiente_a_derivar_conv_v=% nomina_conv_aux=% id_convenio=%',subtotal_aux_conv,pendiente_a_derivar_conv_v,nomina_conv_aux,_record_periodos_conv.id;
	--END LOOP;

	RAISE INFO 'SETEO PENDIENTE CONV %1', pendiente_a_derivar_conv_v;
	update recibo_conceptos_pagos apb
	set pendiente_derivar=pendiente_a_derivar_conv_v	
	where apb.recibo_concepto_id= _record_pagos_conv.recibo_concepto_id
	and apb.recibo_ingreso_id= _record_pagos_conv.recibo_ingreso_id;		    
	
	

END LOOP;

RETURN 1;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
