
DROP FUNCTION buscar_detalle_asiento(p_asiento_id integer) ;
CREATE OR REPLACE FUNCTION buscar_detalle_asiento(p_asiento_id integer) 
RETURNS TABLE(
 id integer,
 asiento_id integer,
 pase integer,
 id_plan_cuentas integer,
 comprobante character varying,
 debe numeric,
 haber numeric,
 observaciones character varying,
 plan_cuentas_numero character varying,
 plan_cuentas_cuenta character varying)
    LANGUAGE sql
    AS $BODY$

select  da.id ,
 da.asiento_id,
 da.pase,
 da.id_plan_cuentas,
 da.comprobante,
 da.debe,
 da.haber,
 da.observaciones,
 pc.numero,
 pc.cuenta
 from asiento a, detalle_asiento da, plan_cuentas pc
 where asiento_id = $1
 and a.id = da.asiento_id
 and da.id_plan_cuentas = pc.id_cuenta_maestro
 and cast(pc.valido_desde as date)<=cast(a.fecha as date)
 and cast(pc.valido_hasta as date)>=cast(a.fecha as date)
 order by pase;

$BODY$;
