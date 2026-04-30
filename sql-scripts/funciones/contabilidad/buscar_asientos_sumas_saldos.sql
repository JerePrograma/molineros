
DROP FUNCTION buscar_asientos_sumas_saldos(p_desde date, p_hasta date) ;
DROP FUNCTION buscar_asientos_sumas_saldos(p_desde date, p_hasta date, p_incluir_automaticos boolean, p_incluir_manuales boolean);

CREATE OR REPLACE FUNCTION buscar_asientos_sumas_saldos(p_desde date, p_hasta date,
	 p_incluir_automaticos boolean, p_incluir_manuales boolean, p_incluir_asiento_inicial boolean) 
RETURNS TABLE(
 plan_cuentas_numero character varying,
 plan_cuentas_cuenta character varying,
 debe numeric,
 haber numeric)
    LANGUAGE sql
    AS $BODY$

select pc.numero, pc.cuenta, sum(debe), sum(haber)
 from detalle_asiento da, asiento a, plan_cuentas pc
 where da.asiento_id = a.id
 and cast(a.fecha as date)>=cast($1 as date)
 and cast(a.fecha as date)<=cast($2 as date)
 and da.id_plan_cuentas = pc.id_cuenta_maestro
 and cast(pc.valido_desde as date)<=cast(a.fecha as date)
 and cast(pc.valido_hasta as date)>=cast(a.fecha as date)
 and ($3 = true or ($3 = false and a.automatico = false))
 and ($4 = true or ($4 = false and a.automatico = true))
 and ($5 = true or ($5 = false and a.numero <> 1))
 group by pc.numero, pc.cuenta
 order by 1;
$BODY$;
