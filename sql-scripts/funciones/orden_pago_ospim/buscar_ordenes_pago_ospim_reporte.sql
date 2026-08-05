
drop FUNCTION buscar_ordenes_pago_ospim_reporte(p_date_ini date, p_date_fin date, p_incluirProveedores boolean , p_incluirLiquidaciones boolean , p_incluirReintegros boolean ) ;


CREATE OR REPLACE FUNCTION buscar_ordenes_pago_ospim_reporte(IN p_date_ini date, IN p_date_fin date, IN p_incluirproveedores boolean, IN p_incluirliquidaciones boolean, IN p_incluirreintegros boolean)
  RETURNS TABLE(op__id_orden_pago integer, op__importe numeric, op__id_seccional integer, op__prestador boolean, op__farmacia boolean, op__cuit_acreedor character varying, op__sucu_acreedor character varying, op__observaciones character varying, op__alta_fecha timestamp without time zone, op__alta_usr character varying, op__alta_ip character varying, op__modi_fecha timestamp without time zone, op__modi_usr character varying, op__modi_ip character varying, op__baja_fecha timestamp without time zone, op__baja_usr character varying, op__baja_ip character varying, e__razon_soc character varying, liquidacion boolean, reintegro boolean, mostrar_en_cuadro boolean) AS
$BODY$

select  
id_orden_pago ,
 importe ,
 id_seccional ,
 prestador ,
 farmacia ,
 cuit_acreedor,
 sucu_acreedor,
 observaciones,
 fecha,
 alta_usr,
 alta_ip,
 modi_fecha,
 modi_usr,
 modi_ip,
 baja_fecha,
 baja_usr,
 baja_ip,
 razon_soc,
 liquidacion,
 reintegro,
 usarEnCuadro
from (
	select *,
	  case when exists (select 1 from orden_pago_ospim_liquidaciones where id_orden_pago_ospim = ops.id_orden_pago) then true else false end as liquidacion,
	  case when exists (select 1 from orden_pago_ospim_lista_reintegros where  id_orden_pago_ospim = ops.id_orden_pago ) then true else false end as reintegro
	from (
	select  
	 opo.id_orden_pago,
	 opo.importe,
	 opo.id_seccional,
	 opo.prestador,
	 opo.farmacia,
	 opo.cuit_acreedor,
	 opo.sucu_acreedor,
	 opo.observaciones,
	 opo.alta_fecha as fecha,
	 opo.alta_usr,
	 opo.alta_ip,
	 opo.modi_fecha,
	 opo.modi_usr,
	 opo.modi_ip ,
	 null as baja_fecha,
	 null as baja_usr,
	 null as baja_ip,
	 case when e.razon_soc is null then opo.afiliado_razon_social else e.razon_soc  end,
	  case when (opo.baja_fecha >= $1 and opo.baja_fecha <= $2 and opo.alta_Fecha >= $1 and opo.alta_fecha<=$2) then false else true end as usarEnCuadro
	from orden_pago_ospim opo
	left outer join empresa e
	on opo.cuit_acreedor = e.cuit
	and opo.sucu_acreedor = e.sucursal
	where cast(opo.alta_fecha as date)>=$1
	and cast(opo.alta_fecha as date)<=$2
	and id_orden_pago <> 60
	union all
	select  
	 opo.id_orden_pago,
	 opo.importe,
	 opo.id_seccional,
	 opo.prestador,
	 opo.farmacia,
	 opo.cuit_acreedor,
	 opo.sucu_acreedor,
	 opo.observaciones,
	 opo.baja_fecha as fecha,
	 opo.alta_usr,
	 opo.alta_ip,
	 opo.modi_fecha,
	 opo.modi_usr,
	 opo.modi_ip ,
	 opo.baja_fecha,
	 opo.baja_usr,
	 opo.baja_ip ,
	 case when e.razon_soc is null then opo.afiliado_razon_social else e.razon_soc  end,
	 case when (opo.baja_fecha >= $1 and opo.baja_fecha <= $2 and opo.alta_Fecha >= $1 and opo.alta_fecha<=$2) then false else true end as usarEnCuadro
	from orden_pago_ospim opo
	left outer join empresa e
	on opo.cuit_acreedor = e.cuit
	and opo.sucu_acreedor = e.sucursal
	where opo.baja_fecha is not null
	and cast(opo.baja_fecha as date)>=$1
	and cast(opo.baja_fecha as date)<=$2
	and id_orden_pago <> 60) ops 
) ops_final 
WHERE (($4 = true and liquidacion = true)  
or ($5 = true and reintegro = true))
or ($3 = true and liquidacion = false and reintegro = false)

order by cast(fecha as date) asc, id_orden_pago asc ,baja_fecha desc

$BODY$
  LANGUAGE sql VOLATILE

