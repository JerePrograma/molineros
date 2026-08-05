
create or replace fUNCTION verificar_cuenta_utilizada(p_id_plan_cuenta integer, p_desde date, p_hasta date) 
RETURNS boolean
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN

return (case when exists (select 1 from conceptos where (id_plan_cuenta = p_id_plan_cuenta or id_plan_cuenta_pasivo = p_id_plan_cuenta )
							--que se solape al ppio
							and ((cast(valido_desde as date) <= cast(p_desde as date) and cast(valido_hasta as date) >= cast(p_desde as date))
								--que se solape al final
								or (cast(valido_desde as date) <= cast(p_hasta as date) and cast(valido_hasta as date) >= cast(p_hasta as date))
								--que se solape en el medio
								or (cast(valido_desde as date) >= cast(p_desde as date) and cast(valido_hasta as date) <= cast(p_hasta as date)))
				)
		or exists (select 1 from parametros_contabilidad  where id_plan_cuenta =  p_id_plan_cuenta
							--que se solape al ppio
							and ((cast(valido_desde as date) <= cast(p_desde as date) and cast(valido_hasta as date) >= cast(p_desde as date))
								--que se solape al final
								or (cast(valido_desde as date) <= cast(p_hasta as date) and cast(valido_hasta as date) >= cast(p_hasta as date))
								--que se solape en el medio
								or (cast(valido_desde as date) >= cast(p_desde as date) and cast(valido_hasta as date) <= cast(p_hasta as date)))
					)
		or exists (select 1 from  efectivo_estado  where id_plan_cuenta =  p_id_plan_cuenta)
		or exists (select 1 from  cuenta_bcria  where id_plan_cuenta =  p_id_plan_cuenta)
				
		then 1 else 0 end);
END;
$BODY$;
