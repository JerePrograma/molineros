create type reporte_egresos_prestaciones_result as (codigo varchar, cta text, descripcion varchar, no_discapacidad numeric, discapacidad numeric, total numeric)


CREATE OR REPLACE FUNCTION REPORTE_EGRESOS_PRESTACIONES(vigen_desde date, vigen_hasta date) 
RETURNS setof reporte_egresos_prestaciones_result
AS
$body$
BEGIN

drop table if exists aux;
--return query
--COLUMNA NO DISCAPACIDAD
--Reporte de lo pagado por periodo y por prestaciones para NO DISCAPACITADOS
create temp table aux as 
select c.codigo, '79848/46' as cuenta, c.descripcion, trunc(sum(importe_total), 2) as no_discapacidad, 0 as discapacidad, 0 as total
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and discapacitado='0'
group by codigo, c.descripcion,c.cta
union 
select '','79848/46', 'ODONTOLOGIA', trunc(sum(importe_total), 2) as no_discapacidad, 0 as discapacidad, 0
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo not in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and discapacitado='0'
and (tipo_consumo like '%REINTEGRO%PROTESIS%' OR tipo_consumo like '%REINTEGRO%ORTOPEDIA%' or tipo_consumo like 'ODONTOLOGIA%GENERAL')
group by c.cta
union
select '','79848/46', 'OTROS REINTEGROS PRESTACIONALES', trunc(sum(importe_total), 2) as no_discapacidad, 0 as discapacidad, 0
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo not in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and discapacitado='0'
and tipo_consumo like '%REINTEGRO%PRESTACI%'
group by c.cta
union
select '','79848/46', 'OTROS LIQUIDACIONES PRESTACIONALES', trunc(sum(importe_total), 2) as no_discapacidad, 0 as discapacidad, 0
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo not in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and discapacitado='0'
and tipo_consumo like '%LIQUIDACION%' 
group by c.cta
--order by codigo
union 
--COLUMNA DISCAPACIDAD
--Reporte de lo pagado por periodo y por prestaciones para DISCAPACITADOS
select c.codigo,'79848/46', c.descripcion, 0, trunc(sum(importe_total), 2), 0
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and discapacitado='1'
group by codigo, c.descripcion, c.cta
union
select '', '79848/46', 'ODONTOLOGIA', 0, trunc(sum(importe_total), 2), 0
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo not in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and discapacitado='1'
and (tipo_consumo like '%REINTEGRO%PROTESIS%' OR tipo_consumo like '%REINTEGRO%ORTOPEDIA%' or tipo_consumo like 'ODONTOLOGIA%GENERAL')
group by c.cta
union
select '', '79848/46', 'OTROS REINTEGROS PRESTACIONALES', 0, trunc(sum(importe_total), 2), 0
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo not in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and discapacitado='1'
and tipo_consumo like '%REINTEGRO%PRESTACI%'
group by c.cta
union
select '', '79848/46', 'OTROS LIQUIDACIONES PRESTACIONALES', 0, trunc(sum(importe_total), 2), 0
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo not in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and discapacitado='1'
and tipo_consumo like '%LIQUIDACION%'
group by c.cta
--order by codigo
--REINTEGROS POR COMPROBANTES
union
select '','79848/46','OTROS REINTEGROS (X COMPROBANTES)', 0, 0,sum(importe)
from
(select sum(cc.importe) as importe
from orden_pago_ospim opo, 
comprobante_orden_pago_ospim copo, 
comprobante c, 
concepto_comprobante cc
where 
--op
cast(opo.alta_fecha as date) >= vigen_desde
and cast(opo.alta_fecha as date) <= vigen_hasta
and (opo.baja_fecha is null or (opo.baja_fecha is not null and date_trunc('month',opo.baja_fecha) > date_trunc('month',opo.alta_fecha)))
and opo.id_orden_pago = copo.id_orden_pago_ospim
--comprobante
and copo.cuit = c.cuit
and copo.compro_nro = c.compro_nro
and copo.compro_tipo = c.compro_tipo
and copo.compro_sucu = c.compro_sucu
and copo.compro_letra = c.compro_letra
and copo.id_punto_venta = c.id_punto_venta
--concepto comprobante
and c.cuit = cc.cuit
and c.compro_nro = cc.compro_nro
and c.compro_tipo = cc.compro_tipo
and c.compro_sucu = cc.compro_sucu
and c.compro_letra = cc.compro_letra
and c.id_punto_venta = cc.id_punto_venta
and (cc.concepto_id = 132 or cc.concepto_id = 425)
and not exists (select 1 from orden_pago_ospim_lista_reintegros  where id_orden_pago_ospim  = opo.id_orden_pago)
union all
select -1 * sum(cc.importe) as importe
from orden_pago_ospim opo, 
comprobante_orden_pago_ospim copo, 
comprobante c, 
concepto_comprobante cc
where 
--op
cast(opo.alta_fecha as date) < vigen_desde
and cast(opo.baja_fecha as date) >= vigen_desde
and cast(opo.baja_fecha as date) <= vigen_hasta
and (opo.baja_fecha is null or (opo.baja_fecha is not null and date_trunc('month',opo.baja_fecha) > date_trunc('month',opo.alta_fecha)))
and opo.id_orden_pago = copo.id_orden_pago_ospim
--comprobante
and copo.cuit = c.cuit
and copo.compro_nro = c.compro_nro
and copo.compro_tipo = c.compro_tipo
and copo.compro_sucu = c.compro_sucu
and copo.compro_letra = c.compro_letra
and copo.id_punto_venta = c.id_punto_venta
--concepto comprobante
and c.cuit = cc.cuit
and c.compro_nro = cc.compro_nro
and c.compro_tipo = cc.compro_tipo
and c.compro_sucu = cc.compro_sucu
and c.compro_letra = cc.compro_letra
and c.id_punto_venta = cc.id_punto_venta
and (cc.concepto_id = 132 or cc.concepto_id = 425)
and not exists (select 1 from orden_pago_ospim_lista_reintegros  where id_orden_pago_ospim  = opo.id_orden_pago)
) a
union all
--DEBITOS A OMINT POR LIQUIDACIONES
select '','79848/46','LIQUIDACIONES CONVENIOS GLOBALES',0,0,sum(importe)
from (select sum(cc.importe) as importe
from orden_pago_ospim opo, 
comprobante_orden_pago_ospim copo, 
comprobante c, 
concepto_comprobante cc
where 
--op
cast(opo.alta_fecha as date) >= vigen_desde
and cast(opo.alta_fecha as date) <= vigen_hasta
and (opo.baja_fecha is null or (opo.baja_fecha is not null and date_trunc('month',opo.baja_fecha) > date_trunc('month',opo.alta_fecha)))
and opo.id_orden_pago = copo.id_orden_pago_ospim
--comprobante
and copo.cuit = c.cuit
and copo.compro_nro = c.compro_nro
and copo.compro_tipo = c.compro_tipo
and copo.compro_sucu = c.compro_sucu
and copo.compro_letra = c.compro_letra
and copo.id_punto_venta = c.id_punto_venta
--concepto comprobante
and c.cuit = cc.cuit
and c.compro_nro = cc.compro_nro
and c.compro_tipo = cc.compro_tipo
and c.compro_sucu = cc.compro_sucu
and c.compro_letra = cc.compro_letra
and c.id_punto_venta = cc.id_punto_venta
and (cc.concepto_id = 127 or cc.concepto_id = 448)
union all
select -1 * sum(cc.importe) as importe
from orden_pago_ospim opo, 
comprobante_orden_pago_ospim copo, 
comprobante c, 
concepto_comprobante cc
where 
--op
cast(opo.alta_fecha as date) < vigen_desde
and cast(opo.baja_fecha as date) >= vigen_desde
and cast(opo.baja_fecha as date) <= vigen_hasta
and (opo.baja_fecha is null or (opo.baja_fecha is not null and date_trunc('month',opo.baja_fecha) > date_trunc('month',opo.alta_fecha)))
and opo.id_orden_pago = copo.id_orden_pago_ospim
--comprobante
and copo.cuit = c.cuit
and copo.compro_nro = c.compro_nro
and copo.compro_tipo = c.compro_tipo
and copo.compro_sucu = c.compro_sucu
and copo.compro_letra = c.compro_letra
and copo.id_punto_venta = c.id_punto_venta
--concepto comprobante
and c.cuit = cc.cuit
and c.compro_nro = cc.compro_nro
and c.compro_tipo = cc.compro_tipo
and c.compro_sucu = cc.compro_sucu
and c.compro_letra = cc.compro_letra
and c.id_punto_venta = cc.id_punto_venta
and (cc.concepto_id = 127 or cc.concepto_id = 448)
) a
-------------------------------------
union 
-----AMTIMA-Resto de Medicamentos - Consultorio
select  '',case when cuenta=2 then '79848/46' else 'OTRAS CUENTAS' end , 'AMTIMA - RESTO MEDICAMENTOS - CONSULTORIO', 0,0,importe
from reporte_egresos_por_concepto_cta_46_y_otra(vigen_desde, vigen_hasta) 
where descripcion = 'RESTO MEDICAMENTOS - CONSULTORIO'
--order by 2,1,3
union 
select '000000','79848/46','Debitado a Omint x Reintegros',0,0,sum(debito) from 
(select sum(a.debitado_omint) as debito
from 
(select * from consumo_afiliado_pago (null,null,'20120501','20120531') where tipo_consumo='REINTEGRO PRESTACIONAL' )a
union 
select sum(b. debitado_omint) from 
(select * from consumo_afiliado_pago (null,null,'20120501','20120531') 
where (tipo_consumo like '%REINTEGRO%PROTESIS%' OR tipo_consumo like '%REINTEGRO%ORTOPEDIA%' or tipo_consumo like 'ODONTOLOGIA%GENERAL') )b) c;


return query 
select codigo, cuenta, descripcion, sum(no_discapacidad), sum(discapacidad), sum(case when total is not null and total<> 0 then total else no_discapacidad+discapacidad end)
from aux
group by codigo, cuenta, descripcion
order by codigo desc , cuenta, descripcion;

END;
$body$
Language 'plpgsql'

