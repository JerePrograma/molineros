CREATE OR REPLACE FUNCTION listado_valores(IN p_fecha_ini date, IN p_fecha_fin date, IN p_cuit character varying, IN p_id_banco integer, 
IN p_depositados integer, IN p_reemplazados integer, IN p_rechazados integer, IN p_fecha_ini_dpto date, IN p_fecha_fin_dpto date, IN p_fecha_ini_reem date, IN p_fecha_fin_reem date, IN p_fecha_ini_rech date, IN p_fecha_fin_rech date, IN p_cta_bcria integer)
  RETURNS TABLE(fecha_vto_cheque timestamp without time zone, fecha date, numero character varying, importe_total numeric, cuit character varying, razon_soc character varying, nro_cheque numeric, importe_cheque numeric, banco character varying, fecha_deposito date, cta_deposito character varying, fecha_reemplazo date, fecha_rechazado date, fecha_reemplazo_rechazado date) AS
$BODY$

select c.fecha,
	recibos.fecha, 
	recibos.numero, 
	recibos.importe as importe_total, 
	c.cuit,  
	e.razon_soc, 
	recibos.nro_cheque,
	c.importe,
	b.descripcion as banco,
	deposito.fecha as fecha_deposito,
	cta.descripcion || '-' || cta.nro_cuenta || '/' || cta.sucursal as cta_deposito,
	reemplazos.fecha as fecha_reemplazo,
	rechazo.fecha as fecha_rechazado,
	reemplazos_rechazados.fecha as fecha_reemplazo_rechazado
	
from (--Recibo de cheques
	select r.fecha, 'Recibo ' || r.numero as numero, r.cuit, r.importe, ri.nro_cheque, ri.id_banco
	from recibo r
	inner join recibo_ingresos ri
	on r.id = ri.recibo_id
	and ri.nro_cheque is not null
	where r.baja_fecha is null	
	and ($3 is null or ($3 is not null and r.cuit = $3))
	and ($4 is null or ($4 is not null and ri.id_banco = $4))
) recibos
left outer join (--Reemplazo cheque no depositado
		select r.fecha, 'Recibo ' || r.numero as numero, rc.nro_cheque_no_depositado as nro_cheque, rc.id_banco_no_depositado as id_banco
		from recibo r
		inner join recibo_conceptos rc
		on r.id = rc.recibo_id
		and rc.nro_cheque_no_depositado is not null
		where r.baja_fecha is null
		and ($3 is null or ($3 is not null and r.cuit = $3))
		and ($4 is null or ($4 is not null and rc.id_banco_no_depositado = $4))
) reemplazos
on recibos.nro_cheque = reemplazos.nro_cheque
and recibos.id_banco = reemplazos.id_banco
left outer join  (--Reemplazo de cheque rechazado
		select r.fecha, 'Recibo ' || r.numero as numero, r.cuit, r.importe, rc.nro_cheque_rechazado as nro_cheque, rc.id_banco_rechazado as id_banco
		from recibo r
		inner join recibo_conceptos rc
		on r.id = rc.recibo_id
		and rc.nro_cheque_rechazado is not null
		where r.baja_fecha is null
		and ($3 is null or ($3 is not null and r.cuit = $3))
		and ($4 is null or ($4 is not null and rc.id_banco_rechazado = $4))
) reemplazos_rechazados
on recibos.nro_cheque = reemplazos_rechazados.nro_cheque
and recibos.id_banco = reemplazos_rechazados.id_banco
left outer join (--Deposito
		select mb.fecha_movimiento as fecha, 'Mov. Bcrio ' || mb.fecha_movimiento as numero,  mb.importe_movimiento, mbi.nro_cheque, mbi.id_banco, mb.id_cuenta_bcria
		from movimiento_banco  mb
		inner join movimiento_banco_items mbi
		on mb.id_movimiento = mbi.id_movimiento
		inner join cheque c
		on mbi.nro_cheque = c.nro_cheque
		and mbi.id_banco = c.id_banco
		where mb.baja_fecha is null
		and mbi.id_estado_cheque_nuevo = 4		
) deposito
on recibos.nro_cheque = deposito.nro_cheque
and recibos.id_banco = deposito.id_banco
left outer join (--Rechazo de cheque
		select mb.fecha_movimiento as fecha, 'Mov. Bcrio ' || mb.fecha_movimiento as numero, mb.importe_movimiento, mbi.nro_cheque, mbi.id_banco
		from movimiento_banco  mb
		inner join movimiento_banco_items mbi
		on mb.id_movimiento = mbi.id_movimiento
		inner join cheque c
		on mbi.nro_cheque = c.nro_cheque
		and mbi.id_banco = c.id_banco
		where mb.baja_fecha is null
		and mbi.id_estado_cheque_nuevo = 5
) rechazo
on recibos.nro_cheque = rechazo.nro_cheque
and recibos.id_banco = rechazo.id_banco
inner join cheque c
on recibos.nro_cheque = c.nro_cheque
and recibos.id_banco = c.id_banco
left outer join empresa e
on c.cuit = e.cuit
and '000' = e.sucursal
left outer join banco b
on recibos.id_banco = b.id_banco
left outer join cuenta_bcria cta
on deposito.id_cuenta_bcria = cta.id_cuenta_bcria
where ($5 is null or ($5 is not null and (	($5 = 1 and deposito.fecha is not null and deposito.fecha>=$8 and deposito.fecha<=$9 )
						or ($5 = 0 and (deposito.fecha is null or deposito.fecha>=$9))
					  )
							
			)
	)
and ($6 is null or ($6 is not null and (   ( $6 = 1 and  (reemplazos.fecha is not null and reemplazos.fecha>=$10 and reemplazos.fecha<=$11) 
					     or(reemplazos_rechazados.fecha is not null and reemplazos.fecha>=$10 and reemplazos.fecha<=$11) )
					or ( $6 = 0 and  ((reemplazos.fecha is null or reemplazos.fecha>=$11) and (reemplazos_rechazados.fecha is null or reemplazos_rechazados.fecha>=$11)))
					  					 )
						
					)
   )
and ($7 is null or ($7 is not null and (    ($7 = 1 and rechazo.fecha is not null and rechazo.fecha>=$12 and rechazo.fecha<=$13)
					  or ($7 = 0 and (rechazo.fecha is null or rechazo.fecha>=$13))
					)
			)
	)
and ($14 is null or ($14 is not null and cta.id_cuenta_bcria=$14))	
and cast(c.fecha as date)>= $1  and cast(c.fecha as date)<= $2
and (($5 is null or $5=1) or ($5=0 and recibos.fecha<=$9))
and (($6 is null or $6=1) or ($6=0 and recibos.fecha<=$11))
and (($7 is null or $7=1) or ($7=0 and recibos.fecha<=$13))
order by c.fecha, nro_cheque, numero;
	

$BODY$
  LANGUAGE 'sql' VOLATILE
