CREATE OR REPLACE FUNCTION buscar_liquidaciones(IN id_liquidacion integer, IN fecha_desde timestamp without time zone, 
IN fecha_hasta timestamp without time zone, IN pediodo_desde timestamp without time zone, IN periodo_hasta timestamp without time zone, 
IN codprestad integer, IN id_prestador integer, IN entidad character varying, IN compro_a_debitar_tipo character varying, 
IN compro_a_debitar_letra character varying, IN sucu integer, IN compro_a_debitar_numero character varying, IN estado integer, IN id_orden_pago integer)
  RETURNS TABLE(id_liquidacion integer, fecha timestamp without time zone, periodo timestamp without time zone, compro_a_debitar_tipo character varying, 
  compro_a_debitar_letra character varying, sucu integer, compro_a_debitar_numero character varying, b_fecha timestamp without time zone, b_usr character varying, 
  cantidad numeric, importe numeric, id_prestador integer, cuit character varying, descripcion character varying, id_orden_pago integer, nro_cheque numeric, 
  fecha_op timestamp without time zone, importe_total numeric, debitado numeric, observaciones character varying, estado integer, id_orden_compra integer, fecha_emitido timestamp without time zone, 
  baja_fecha_op boolean, fecha_recibido timestamp without time zone) AS
$BODY$
	select 	
		l.id_liquidacion,
		l.fecha,
		l.periodo,
		l.compro_a_debitar_tipo,
		l.compro_a_debitar_letra,
		l.sucu,
		l.compro_a_debitar_numero,
		l.baja_fecha,
		l.baja_usr,
		lp.cantidad,
		lp.importe,
		pd.id_prestador,
		pd.cuit,
		pd.descripcion,				
		
		case when opol.baja_fecha is null then opo.id_orden_pago else 0 end,
		case when opol.baja_fecha is null then opos.nro_cheque else cast (0 as numeric) end,
		case when opol.baja_fecha is null then opo.alta_fecha else cast (null as timestamp without time zone) end as fecha_		,
		
		l.importe,
		l.debitado,
		l.observaciones,
		l.estado,
		l.id_orden_compra,
		l.fecha_emitido,
		
		opol.baja_fecha is not null,
		
		l.fecha_recibido
	
	from
		liquidacion l
		left outer join
		liquidacion_prestacion lp on l.id_liquidacion = lp.id_liquidacion
		left outer join
		prestador pd on l.id_prestador = pd.id_prestador
		left outer join
		orden_pago_ospim_liquidaciones opol
		on l.id_liquidacion = opol.id_liquidacion
		and opol.baja_fecha is null -- LO PONEMOS PORQUE SI SE DA DE BAJA LA OP ORIGINAL NO APARECEN LAS OPS NUEVAS..
		left outer join
		orden_pago_ospim opo on
		opol.id_orden_pago_ospim = opo.id_orden_pago
		left outer join
		orden_pago_ospim_pagos opos
		on opo.id_orden_pago = opos.id_orden_pago
		where
		($1 = 0 or ($1 != 0 and l.id_liquidacion=$1)) and
		($2 is null or ($2 is not null and l.fecha>=$2))  and
		($3 is null or ($3 is not null and l.fecha<=$3))  and
		($4 is null or ($4 is not null and l.periodo>=$4))  and
		($5 is null or ($5 is not null and l.periodo<=$5))  and
		($7 = 0 or ($7 != 0 and l.id_prestador = $7)) and
		($8 is null or ($8 is not null and l.entidad = $8)) and
		($9 = '' or ($9 != '' and l.compro_a_debitar_tipo=$9)) and
		($10 = '' or ($10 != '' and l.compro_a_debitar_letra=$10)) and
		($11 = 0 or ($11 != 0 and l.sucu=$11)) and
		($12 = '' or ($12 != '' and l.compro_a_debitar_numero like '%'||$12||'%')) and
		($14 = 0 or ($14 != 0 and l.id_orden_compra=$14)) and
		(
		($13 = 0

			and ((opo.id_orden_pago is null or 
					(opo.id_orden_pago is not null and opo.baja_fecha is not null and 
						(
							(SELECT count (*) from orden_pago_ospim_liquidaciones opol2 where id_liquidacion = l.id_liquidacion)
							>= 1
						)
					)
				
			) or (opo.id_orden_pago is not null and opo.baja_fecha is null 
			))
						
			     
		) 


		or
		

			($13 = 1 and not exists (select 1 from orden_pago_ospim_liquidaciones where id_liquidacion = l.id_liquidacion and baja_fecha is null)							
			
			
			--	(opo.id_orden_pago is null or 
				--	(opo.id_orden_pago is not null and opo.baja_fecha is not null and 
					--	(
						--	(SELECT count (*) from orden_pago_ospim_liquidaciones opol2 where id_liquidacion = l.id_liquidacion)
							-->= 1
						--)
					--)
				--)
			) 
			or			
			($13 = 2 and 
				(opo.id_orden_pago is not null and opo.baja_fecha is null)
			)
			
		)
	order by l.fecha
	limit 200;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;