DROP FUNCTION verificar_equivalencias_conceptos_completo(p_fecha_hasta date);
CREATE OR REPLACE FUNCTION verificar_equivalencias_conceptos_completo(p_desde date, p_hasta date) 
RETURNS boolean
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
  begin
	res=0;
	res = (case when exists (select 1 from concepto_comprobante cc, comprobante c
							where cc.cuit = c.cuit
							and cc.compro_nro = c.compro_nro
							and cc.compro_letra = c.compro_letra
							and cc.compro_sucu = c.compro_sucu
							and cc.id_punto_venta = c.id_punto_venta
							and cc.compro_tipo = c.compro_tipo
							and cast(c.fecha_recepcion  as date) >= cast(p_desde as date) 
							and cast(c.fecha_recepcion  as date) <= cast(p_hasta as date)
							and not exists (select 1 from conceptos
											where id_concepto_maestro = cc.concepto_id 
											and cast(c.fecha_recepcion  as date) >= cast(valido_desde as date) 
											and cast(c.fecha_recepcion  as date) <= cast(valido_hasta as date))
							and cc.concepto_id  <> (select id from concepto_maestro where descripcion_original = 'AJUSTE')) 
		or exists (select 1 from recibo_conceptos rc, recibo r
							where rc.recibo_id = r.id
							and cast(r.fecha as date) >= cast(p_desde as date) 
							and cast(r.fecha as date) <= cast(p_hasta as date)
							and not exists (select 1 from conceptos 
											where id_concepto_maestro = rc.caja_concepto_id 
											and cast(r.fecha  as date) >= cast(valido_desde as date) 
											and cast(r.fecha  as date) <= cast(valido_hasta as date))
							and rc.caja_concepto_id   <> (select id from concepto_maestro where descripcion_original = 'AJUSTE'))
		or exists (select 1 from movimiento_banco  m,  tipo_mov_bcrio t
							where m.id_tipo_mov = t.id_tipo_mov
							and cast(m.fecha_movimiento as date) >= cast(p_desde as date) 
							and cast(m.fecha_movimiento as date) <= cast(p_hasta as date)
							and not exists (select 1 from conceptos 
											where id_concepto_maestro = t.concepto_id
											and cast(m.fecha_movimiento  as date) >= cast(valido_desde as date) 
											and cast(m.fecha_movimiento as date) <= cast(valido_hasta as date)	)
								and t.concepto_id <> (select id from concepto_maestro where descripcion_original = 'AJUSTE'))
		then 1 else 0 end);
		
	
	if (res is null or res = 0 ) then 
		res = 0;
	end if;		
	
	if res <> 0 then
		return false;
	end if;
	if res = 0 then
		return true;
	end if;
  end;  
$BODY$;