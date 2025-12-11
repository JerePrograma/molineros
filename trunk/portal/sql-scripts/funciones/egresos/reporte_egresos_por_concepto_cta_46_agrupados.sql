drop function reporte_egresos_por_concepto_cta_46_agrupados(p_fecha_ini date, p_fecha_fin date);
drop type return_reporte_egresos_por_concepto_cta_46_agrupados;

create type return_reporte_egresos_por_concepto_cta_46_agrupados as
(id_grupo integer,
descripcion character varying, 
importe numeric
);

CREATE OR REPLACE FUNCTION reporte_egresos_por_concepto_cta_46_agrupados(p_fecha_ini date, p_fecha_fin date)
  RETURNS SETOF return_reporte_egresos_por_concepto_cta_46_agrupados AS
$BODY$
declare cuit_v varchar;
BEGIN

drop table if exists temp_egresos_por_concepto;

create temp table temp_egresos_por_concepto as
select concepto_id, sum(importe)  as importe, entidad
from (
select cc.concepto_id, case when comp.debito_para_egreso = true then -1 * cc.importe else cc.importe end as importe,
		case when (op.cuit_acreedor = '30520634971' and op.sucu_acreedor = '1') then 'OMINT' 
			else case when (op.cuit_acreedor = '33703421259' and op.sucu_acreedor = '000') then 'MYS' 
				else 
				  case when (op.cuit_acreedor = '30660227659' and op.sucu_acreedor = '000') then 'CHIVILCOY' 
				       else
						case when (op.cuit_acreedor = '30500010912' and op.sucu_acreedor = '000') then 'NACION' 
						else
						case when (op.cuit_acreedor = '30550245309' and op.sucu_acreedor= '1') then 'OMINT' 
						else
						case when (op.cuit_acreedor = '30546128403' and op.sucu_acreedor= '000') then 'CEMIC' 
						else
						case when (op.cuit_acreedor = '30522428163' and op.sucu_acreedor= '000') then 'GALENO' else '' end
						end
						end
					end
				end
			end  
		end as entidad --PARA HACER LA SEPARACION ESPECIAL EN CASO DE QUE ESTEN PAGANDO APORTES
from orden_pago_ospim op,
     comprobante_orden_pago_ospim copo,
     concepto_comprobante cc,
     comprobante comp
where op.id_orden_pago=copo.id_orden_pago_ospim
and copo.cuit = cc.cuit
and copo.compro_sucu = cc.compro_sucu
and copo.compro_letra = cc.compro_letra
and copo.compro_tipo = cc.compro_tipo
and copo.compro_nro = cc.compro_nro
and copo.id_punto_venta = cc.id_punto_venta
and comp.cuit = cc.cuit
and comp.compro_sucu = cc.compro_sucu
and comp.compro_letra = cc.compro_letra
and comp.compro_tipo = cc.compro_tipo
and comp.compro_nro = cc.compro_nro
and comp.id_punto_venta = cc.id_punto_venta
and CAST(op.alta_fecha AS DATE) >= $1 and CAST(op.alta_fecha AS DATE) <=$2
and (op.baja_fecha is null or (cast(op.baja_fecha as date) > cast($2 as date)))
and exists (select 1 from orden_pago_ospim_pagos opp
	    where opp.id_orden_pago=op.id_orden_pago
	    and (id_cta_bcria_cheque=2
	    or id_cta_bcria_retencion=2
	    or id_cta_bcria_transf_bcria=2
	    or id_cta_bcria_debito_crio=2))
union all
select cc.concepto_id,  -1* (case when comp.debito_para_egreso = true then -1 * cc.importe else cc.importe end) as importe,
		case when (op.cuit_acreedor = '30520634971' and op.sucu_acreedor = '1') then 'OMINT' 
			else case when (op.cuit_acreedor = '33703421259' and op.sucu_acreedor = '000') then 'MYS' 
				else 
				  case when (op.cuit_acreedor = '30660227659' and op.sucu_acreedor = '000') then 'CHIVILCOY' 
				       else
						case when (op.cuit_acreedor = '30500010912' and op.sucu_acreedor = '000') then 'NACION' 
						else 						
						case when (op.cuit_acreedor = '30550245309' and op.sucu_acreedor= '1') then 'OMINT' 
						else
						case when (op.cuit_acreedor = '30546128403' and op.sucu_acreedor= '000') then 'CEMIC' 
						else
						case when (op.cuit_acreedor = '30522428163' and op.sucu_acreedor= '000') then 'GALENO' else '' end
						end
						end
					end
				end
			end  
		end as entidad
from orden_pago_ospim op,
     comprobante_orden_pago_ospim copo,
     concepto_comprobante cc,
     comprobante comp
where op.id_orden_pago=copo.id_orden_pago_ospim
and copo.cuit = cc.cuit
and copo.compro_sucu = cc.compro_sucu
and copo.compro_letra = cc.compro_letra
and copo.compro_tipo = cc.compro_tipo
and copo.compro_nro = cc.compro_nro
and copo.id_punto_venta = cc.id_punto_venta
and comp.cuit = cc.cuit
and comp.compro_sucu = cc.compro_sucu
and comp.compro_letra = cc.compro_letra
and comp.compro_tipo = cc.compro_tipo
and comp.compro_nro = cc.compro_nro
and comp.id_punto_venta = cc.id_punto_venta
and cast(op.baja_fecha as date)>= $1 and cast(op.baja_fecha as date)<=$2
and cast(op.alta_fecha as date) < $1
and exists (select 1 from orden_pago_ospim_pagos opp
	    where opp.id_orden_pago=op.id_orden_pago
	    and (id_cta_bcria_cheque=2
	    or id_cta_bcria_retencion=2
	    or id_cta_bcria_transf_bcria=2
	    or id_cta_bcria_debito_crio=2))
union all
--ANTIC
select c.concepto_id,  -1* (c.importe) as importe,
		case when (opo.cuit_acreedor = '30520634971' and opo.sucu_acreedor = '1') then 'OMINT' 
			else case when (opo.cuit_acreedor = '33703421259' and opo.sucu_acreedor = '000') then 'MYS' 
				else 
				  case when (opo.cuit_acreedor = '30660227659' and opo.sucu_acreedor = '000') then 'CHIVILCOY' 
				       else
						case when (opo.cuit_acreedor = '30500010912' and opo.sucu_acreedor = '000') then 'NACION' 
						else 
						case when (opo.cuit_acreedor = '30550245309' and opo.sucu_acreedor= '1') then 'OMINT' 
						else
						case when (opo.cuit_acreedor = '30546128403' and opo.sucu_acreedor= '000') then 'CEMIC' 
						else
						case when (opo.cuit_acreedor = '30522428163' and opo.sucu_acreedor= '000') then 'GALENO' else '' end
						end
						end
					end
				end
			end  
		end as entidad
from 
orden_pago_ospim opo,
orden_pago_ospim_pagos opop,
concepto_comprobante c
where opo.id_orden_pago = opop.id_orden_pago
and opop.compro_tipo_antic = c.compro_tipo
and opop.compro_sucu_antic = c.compro_sucu
and opop.compro_letra_antic = c.compro_letra
and opop.compro_nro_antic = c.compro_nro
and opop.cuit_antic = c.cuit
and opop.id_punto_venta_antic = c.id_punto_venta
and cast(opo.alta_fecha as date) >= $1 and cast(opo.alta_fecha as date)<=$2
and (opo.baja_fecha is null or (cast(opo.baja_fecha as date) > cast($2 as date)))
and ((exists  (select 1 from orden_pago_ospim_pagos opp
	    where opp.id_orden_pago=opo.id_orden_pago
	    and (id_cta_bcria_cheque=2
	    or id_cta_bcria_retencion=2
	    or id_cta_bcria_transf_bcria=2
	    or id_cta_bcria_debito_crio=2)) 
	and not exists (select 1 from orden_pago_ospim_pagos opp
	    where opp.id_orden_pago=opo.id_orden_pago
	    and (id_cta_bcria_cheque<>2
	    or id_cta_bcria_retencion<>2
	    or id_cta_bcria_transf_bcria<>2
	    or id_cta_bcria_debito_crio<>2))) )
    and exists (select 1 from comprobante_orden_pago_ospim copo2,
				orden_pago_ospim_pagos opop2
				where   opop.compro_tipo_antic = copo2.compro_tipo
					and opop.compro_sucu_antic = copo2.compro_sucu
					and opop.compro_letra_antic = copo2.compro_letra
					and opop.compro_nro_antic = copo2.compro_nro
					and opop.cuit_antic = copo2.cuit
					and opop.id_punto_venta_antic = copo2.id_punto_venta
					and copo2.id_orden_pago_ospim  = opop2.id_orden_pago
					and (opop2.id_cta_bcria_cheque=2
					    or opop2.id_cta_bcria_retencion=2
					    or opop2.id_cta_bcria_transf_bcria=2
					    or opop2.id_cta_bcria_debito_crio=2))
union all
select c.concepto_id,  (c.importe) as importe,
		case when (opo.cuit_acreedor = '30520634971' and opo.sucu_acreedor = '1') then 'OMINT' 
			else case when (opo.cuit_acreedor = '33703421259' and opo.sucu_acreedor = '000') then 'MYS' 
				else 
				  case when (opo.cuit_acreedor = '30660227659' and opo.sucu_acreedor = '000') then 'CHIVILCOY' 
				       else
						case when (opo.cuit_acreedor = '30500010912' and opo.sucu_acreedor = '000') then 'NACION' 
						else
						case when (opo.cuit_acreedor = '30550245309' and opo.sucu_acreedor= '1') then 'OMINT' 
						else
						case when (opo.cuit_acreedor = '30546128403' and opo.sucu_acreedor= '000') then 'CEMIC' 
						else
						case when (opo.cuit_acreedor = '30522428163' and opo.sucu_acreedor= '000') then 'GALENO' else '' end
						end
						end
						
					end
				end
			end  
		end as entidad
from 
orden_pago_ospim opo,
orden_pago_ospim_pagos opop,
concepto_comprobante c
where opo.id_orden_pago = opop.id_orden_pago
and opop.compro_tipo_antic = c.compro_tipo
and opop.compro_sucu_antic = c.compro_sucu
and opop.compro_letra_antic = c.compro_letra
and opop.compro_nro_antic = c.compro_nro
and opop.cuit_antic = c.cuit
and opop.id_punto_venta_antic = c.id_punto_venta
and cast(opo.baja_fecha as date)>= $1 and cast(opo.baja_fecha as date)<=$2
and cast(opo.alta_fecha as date)< $1
) asd
group by concepto_id, entidad;

return query
	select  g.id_grupo, 
		 g.descripcion,
		sum(importe) as importe
	from temp_egresos_por_concepto c
	inner join concepto_maestro cm
	on c.concepto_id = cm.id
	left outer join grupos_concepto  gc
	on c.concepto_id = gc.id_concepto
	left outer join grupo g
	on gc.id_grupo = g.id_grupo
	where cm.descripcion_original <> 'AJUSTE'
	and ((entidad = '' /*and cm.descripcion_original not in (select descripcion from temp_egresos_por_concepto where entidad <> '') */) -- para excluir el registro por si pagaron a un nuevo acreedor con el concepto APORTES
		or	(entidad is not null and (
				(entidad = 'MYS' and g.id_grupo = 12)
				or (entidad = 'CHIVILCOY' and g.id_grupo = 12)
				or (entidad = 'OMINT' and g.id_grupo = 3) 
				or (entidad= 'NACION' and g.id_grupo=2)
				or (entidad= 'CEMIC' and g.id_grupo=12)
				or (entidad= 'GALENO' and g.id_grupo=12))))	
							
	group by g.id_grupo, g.descripcion
	having g.id_grupo is not null
	union all
	select  null,  'SIN CATEGORIZAR: ' || cm.descripcion_original || (case when g.id_grupo in (12,3,2) then ' - PAGO A NUEVO ACREEDOR' else '' end),
			importe
	from temp_egresos_por_concepto c
	inner join concepto_maestro cm
	on c.concepto_id = cm.id
	left outer join grupos_concepto  gc
	on c.concepto_id = gc.id_concepto
	left outer join grupo g
	on gc.id_grupo = g.id_grupo
	where id_grupo_concepto is null
	and cm.descripcion_original <> 'AJUSTE'
	or (entidad = '' and cm.descripcion_original  in (select descripcion from temp_egresos_por_concepto where entidad <> '') and g.id_grupo = 12 ) -- para incluir el registro de pago a nuevo acreedor con concepto APORTES
	order by 1,2;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE

