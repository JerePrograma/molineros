-- Function: buscar_ordenes_pago_ospim_reporte(date, date, character varying, character varying, integer, character varying, character varying, integer, character varying)

-- DROP FUNCTION buscar_ordenes_pago_ospim_reporte(date, date, character varying, character varying, integer, character varying, character varying, integer, character varying);

create type buscar_ordenes_pago_ospim_result as (
op__id_orden_pago integer, 
op__importe numeric, 
op__id_seccional integer, 
op__prestador boolean, 
op__farmacia boolean, 
op__cuit_acreedor character varying, 
op__sucu_acreedor character varying, 
op__observaciones character varying, 
op__alta_fecha timestamp without time zone, 
op__alta_usr character varying, 
op__alta_ip character varying, 
op__modi_fecha timestamp without time zone, 
op__modi_usr character varying, 
op__modi_ip character varying, 
op__baja_fecha timestamp without time zone, 
op__baja_usr character varying, 
op__baja_ip character varying, 
e__razon_soc character varying) 


CREATE OR REPLACE FUNCTION buscar_ordenes_pago_ospim_reporte(p_date_ini date, p_date_fin date, cuit_p character varying, sucur_p character varying, id_prestador_p integer, compro_tipo_v character varying, compro_letra_v character varying, compro_sucur_v integer, compro_nro_v character varying)
  RETURNS SETOF buscar_ordenes_pago_ospim_result AS
$BODY$
declare cuit_v varchar;
declare entidad boolean;
BEGIN

drop table if exists aux_op;

create temp table aux_op as
select * from (
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
 case when e.razon_soc is null then opo.afiliado_razon_social else e.razon_soc  end as razon_soc
from orden_pago_ospim opo
left outer join empresa e
on opo.cuit_acreedor = e.cuit
and opo.sucu_acreedor = e.sucursal
where cast(opo.alta_fecha  as date)>=$1
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
 case when e.razon_soc is null then opo.afiliado_razon_social else e.razon_soc  end
from orden_pago_ospim opo
left outer join empresa e
on opo.cuit_acreedor = e.cuit
and opo.sucu_acreedor = e.sucursal
where opo.baja_fecha is not null
and cast(opo.baja_fecha as date)>=$1
and cast(opo.baja_fecha as date)<=$2
and id_orden_pago <> 60) ops order by cast(fecha as date) asc, id_orden_pago asc ,baja_fecha desc;

if cuit_p is null THEN 
   cuit_v=cuit from prestador where id_prestador=id_prestador_p;
else   
   cuit_v=cuit_p;
END IF;


if (cuit_v='30629138567' or cuit_v='30531143856' or cuit_v='30604119568') then
   entidad=true;
end if;

return query
select id_orden_pago,
       importe,
       id_seccional,
       prestador,
       farmacia,
       cuit_acreedor,
       sucu_acreedor,
       observaciones,
       fecha,
       alta_usr,
       alta_ip,
       modi_fecha,
       modi_usr,
       modi_ip ,
       baja_fecha,
       baja_usr,
       baja_ip,
       razon_soc
from aux_op a     
where (cuit_v is null or (cuit_v is not null and cuit_acreedor=cuit_v)) 
and ((sucur_p is null or (sucur_p is not null and sucu_acreedor=sucur_p)) or (entidad=true and (sucur_p is null or (sucur_p is not null and id_seccional=cast(sucur_p as integer)))))
and ( (compro_tipo_v is null and compro_letra_v is null and compro_sucur_v is null and compro_nro_v is null) or
exists (select 1 from comprobante_orden_pago_ospim copo 
	    where copo.id_orden_pago_ospim=a.id_orden_pago
            and (compro_tipo_v is null or (compro_tipo_v is not null and compro_tipo=compro_tipo_v))
	    and (compro_letra_v is null or (compro_letra_v is not null and compro_letra=compro_letra_v))
	    and (compro_sucur_v is null or (compro_sucur_v is not null and compro_sucu=compro_sucur_v))
            and (compro_nro_v  is null or (compro_nro_v  is not null and compro_nro=compro_nro_v)))
    );




END;
$BODY$
  LANGUAGE plpgsql VOLATILE

