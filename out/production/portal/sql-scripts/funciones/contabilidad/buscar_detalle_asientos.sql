DROP FUNCTION buscar_detalle_asientos(p_desde date, p_hasta date) ;

CREATE OR REPLACE FUNCTION buscar_detalle_asientos(IN p_desde date, IN p_hasta date, IN p_asiento_desde integer, IN p_asiento_hasta integer, IN p_incluir_automaticos boolean, IN p_incluir_manuales boolean)
  RETURNS TABLE(id integer, asiento_id integer, pase integer, id_plan_cuentas integer, comprobante character varying, debe numeric, haber numeric, observaciones character varying, plan_cuentas_numero character varying, plan_cuentas_cuenta character varying, asiento_fecha date, asiento_numero integer) AS
$BODY$

select  da.id ,
 da.asiento_id,
 da.pase,
 da.id_plan_cuentas,
 da.comprobante,
 da.debe,
 da.haber,
 case when (da.observaciones is null or length(rtrim(observaciones))=0)  then a.descripcion else da.observaciones end,
 pc.numero,
 pc.cuenta,
 cast(a.fecha as date),
 a.numero
 from detalle_asiento da, asiento a, plan_cuentas pc
 where da.asiento_id = a.id
 and cast(a.fecha as date)>=cast($1 as date)
 and cast(a.fecha as date)<=cast($2 as date)
 and da.id_plan_cuentas = pc.id_cuenta_maestro
 and cast(pc.valido_desde as date)<=cast(a.fecha as date)
 and cast(pc.valido_hasta as date)>=cast(a.fecha as date)
 and ($3 is null or ($3 is not null and a.numero >= $3))
 and ($4 is null or ($4 is not null and a.numero <= $4))
 and ($5 = true or ($5 = false and a.automatico = false))
 and ($6 = true or ($6 = false and a.automatico = true))
 order by pase;

$BODY$
  LANGUAGE sql VOLATILE
