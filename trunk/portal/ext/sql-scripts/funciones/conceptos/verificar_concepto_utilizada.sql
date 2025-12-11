drop fUNCTION verificar_concepto_utilizado(p_id_concepto integer);

create or replace fUNCTION verificar_concepto_utilizado(p_id_concepto integer, p_desde date, p_hasta date) 
RETURNS boolean
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN

return (case when exists (select 1 from concepto_comprobante cc, comprobante c
							where cc.cuit = c.cuit
							and cc.compro_nro = c.compro_nro
							and cc.compro_letra = c.compro_letra
							and cc.compro_sucu = c.compro_sucu
							and cc.id_punto_venta = c.id_punto_venta
							and cc.compro_tipo = c.compro_tipo
							and cc.concepto_id = p_id_concepto 
							and cast(c.fecha_recepcion  as date) >= cast(p_desde as date) 
							and cast(c.fecha_recepcion  as date) <= cast(p_hasta as date)) 
		or exists (select 1 from recibo_conceptos rc, recibo r
							where rc.recibo_id = r.id
							and rc.caja_concepto_id = p_id_concepto
							and cast(r.fecha as date) >= cast(p_desde as date) 
							and cast(r.fecha as date) <= cast(p_hasta as date))
		or exists (select 1 from tipo_mov_bcrio where concepto_id =  p_id_concepto
							--que se solape al ppio
							and ((cast(valido_desde as date) <= cast(p_desde as date) and cast(valido_hasta as date) >= cast(p_desde as date))
								--que se solape al final
								or (cast(valido_desde as date) <= cast(p_hasta as date) and cast(valido_hasta as date) >= cast(p_hasta as date))
								--que se solape en el medio
								or (cast(valido_desde as date) >= cast(p_desde as date) and cast(valido_hasta as date) <= cast(p_hasta as date)))
					)
		or exists (select 1 from nomenclador_conceptos where concepto_id =  p_id_concepto
							--que se solape al ppio
							and ((cast(valido_desde as date) <= cast(p_desde as date) and cast(valido_hasta as date) >= cast(p_desde as date))
								--que se solape al final
								or (cast(valido_desde as date) <= cast(p_hasta as date) and cast(valido_hasta as date) >= cast(p_hasta as date))
								--que se solape en el medio
								or (cast(valido_desde as date) >= cast(p_desde as date) and cast(valido_hasta as date) <= cast(p_hasta as date)))
				)
		or exists (select 1 from concepto_transferencia where concepto_id =  p_id_concepto
							--que se solape al ppio
							and ((cast(valido_desde as date) <= cast(p_desde as date) and cast(valido_hasta as date) >= cast(p_desde as date))
								--que se solape al final
								or (cast(valido_desde as date) <= cast(p_hasta as date) and cast(valido_hasta as date) >= cast(p_hasta as date))
								--que se solape en el medio
								or (cast(valido_desde as date) >= cast(p_desde as date) and cast(valido_hasta as date) <= cast(p_hasta as date)))
					)
	or exists (select 1 from parametros_conceptos where id_concepto =  p_id_concepto
							--que se solape al ppio
							and ((cast(valido_desde as date) <= cast(p_desde as date) and cast(valido_hasta as date) >= cast(p_desde as date))
								--que se solape al final
								or (cast(valido_desde as date) <= cast(p_hasta as date) and cast(valido_hasta as date) >= cast(p_hasta as date))
								--que se solape en el medio
								or (cast(valido_desde as date) >= cast(p_desde as date) and cast(valido_hasta as date) <= cast(p_hasta as date)))
					)
		then 1 else 0 end);
END;
$BODY$;
