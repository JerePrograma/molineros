
DROP FUNCTION subdiario_ingresos(IN p_fecha_ini date, IN p_fecha_fin date, IN p_cuit character varying, IN p_sucu character varying,
			p_incluir_bcrios boolean, p_incluir_recibos boolean, p_incluir_afip boolean)			;

CREATE OR REPLACE FUNCTION subdiario_ingresos(IN p_fecha_ini date, IN p_fecha_fin date, IN p_cuit character varying, IN p_sucu character varying, IN p_incluir_bcrios boolean, IN p_incluir_recibos boolean, IN p_incluir_afip boolean)
  RETURNS TABLE(numero_comprobante character varying, fecha date, baja_fecha date, cuit character varying, sucursal character varying, razon_soc character varying, cuenta character varying, numero character varying, cuenta_id integer, f_de_pag character varying, cuenta_f_de_pag character varying, cuenta_id_f_de_pag integer, importe numeric) AS
$BODY$



SELECT   numero_comprobante,
	 fecha,
	 cast(subdiario.baja_fecha as date),
         subdiario.cuit,
         subdiario.sucursal,
         e.razon_soc,
         subdiario.cuenta,
         subdiario.numero,
         subdiario.id_cuenta_maestro,
         pc.numero as f_de_pag,
         pc.cuenta as cuenta_f_de_pag,
         pc.id_cuenta_maestro,
         sum(importe)
FROM     (
         --recibos y convenios
         SELECT  'Rec. ' || r.numero  as numero_comprobante,
         		 r.fecha,
         		 cast (r.baja_fecha as date),
                 r.cuit,
                 r.sucursal,
                 pc.cuenta,
                 pc.numero,
                 pc.id_cuenta_maestro,
                 case when ee.id_plan_cuenta is not null 
                 	then ee.id_plan_cuenta
                 	else 
                 		case when ri.nro_cheque is not null
                 			then (select id_plan_cuenta from parametros_contabilidad where parametro = 'valores_depositar' and valido_desde <= cast( r.fecha as date) and valido_hasta >= cast(r.fecha as date))
                 			else cb.id_plan_cuenta
                 		end 
                 end  AS f_de_pag,  
                 rcp.importe
         FROM    recibo r
                 INNER JOIN recibo_conceptos rc
                 ON      r.id = rc.recibo_id
                 INNER JOIN plan_cuentas pc
                 ON   pc.id_cuenta_maestro = (select id_plan_cuenta from parametros_contabilidad where parametro = 'deudores_actas_y_acuerdos' and valido_desde <= cast( r.fecha as date) and valido_hasta >= cast(r.fecha as date))
                 and pc.valido_desde <= cast(r.fecha as date) and pc.valido_hasta >= cast(r.fecha as date)
                 inner join recibo_conceptos_pagos  rcp
                 on rcp.recibo_concepto_id   = rc.id
                 inner join recibo_ingresos ri
                 on rcp.recibo_ingreso_id  = ri.id
                 left outer join efectivo_estado ee
                 on ri.id_estado_efectivo = ee.id
                left outer join cuenta_bcria cb
                on ri.id_cuenta_bcria_destino_deposito = cb.id_cuenta_bcria
         WHERE $6 = true   
         and (rc.acta_id     IS NOT NULL  OR      rc.convenio_id IS NOT NULL)
         AND (   (CAST (r.fecha AS DATE) >= cast($1 as date)   AND    CAST (r.fecha AS DATE)  <= cast($2 as date))
              or   (CAST (r.baja_fecha AS DATE) >= cast($1 as date)   AND    CAST (r.baja_fecha AS DATE)  <= cast($2 as date)))
         and ($3 is null or ($3 is not null and r.cuit = $3))
		and ($4 is null or ($4 is not null and  r.sucursal=$4))
         union all 
         -- canje de cheque no depositado
		 SELECT  'Rec. ' || r.numero  as numero_comprobante,
		 		 r.fecha,
		 		 cast (r.baja_fecha as date),
                 r.cuit,
                 r.sucursal,
                 pc.cuenta,
                 pc.numero,
                 pc.id_cuenta_maestro,
                case when ee.id_plan_cuenta is not null 
                 	then ee.id_plan_cuenta
                 	else 
                 		case when ri.nro_cheque is not null
                 			then  (select id_plan_cuenta from parametros_contabilidad where parametro = 'valores_depositar' and valido_desde <= cast( r.fecha as date) and valido_hasta >= cast(r.fecha as date) )
                 			else cb.id_plan_cuenta
                 		end 
                 end  AS f_de_pag,
                rcp.importe
         FROM   recibo r
                INNER JOIN recibo_conceptos rc
                ON     r.id = rc.recibo_id
                INNER JOIN plan_cuentas pc
                on pc.id_cuenta_maestro = (select id_plan_cuenta from parametros_contabilidad where parametro = 'valores_depositar' and valido_desde <= cast( r.fecha as date) and valido_hasta >= cast(r.fecha as date))
                and pc.valido_desde <= cast(r.fecha as date) and pc.valido_hasta >= cast(r.fecha as date)
                 inner join recibo_conceptos_pagos  rcp
                 on rcp.recibo_concepto_id   = rc.id
                 inner join recibo_ingresos ri
                 on rcp.recibo_ingreso_id  = ri.id
				left outer join efectivo_estado ee
                 on ri.id_estado_efectivo = ee.id
                 left outer join cuenta_bcria cb
                on ri.id_cuenta_bcria_destino_deposito = cb.id_cuenta_bcria
         WHERE  $6 = true
         and (nro_cheque_no_depositado IS NOT NULL )
         AND (   (CAST (r.fecha AS DATE) >= cast($1 as date)   AND    CAST (r.fecha AS DATE)  <= cast($2 as date))
              or   (CAST (r.baja_fecha AS DATE) >= cast($1 as date)   AND    CAST (r.baja_fecha AS DATE)  <= cast($2 as date)))
        and ($3 is null or ($3 is not null and r.cuit = $3))
		and ($4 is null or ($4 is not null and  r.sucursal=$4))
	-------
	union all -- Rechazo del cheque y su reposicion
	-------
	SELECT  'Rec. ' || r.numero as numero_comprobante,
         		 r.fecha,
         	 	 cast (r.baja_fecha as date),
                 r.cuit,
                 r.sucursal,
                 pc.cuenta,
                 pc.numero,
                  pc.id_cuenta_maestro,
                 case when ee.id_plan_cuenta is not null 
                 	then ee.id_plan_cuenta
                 	else 
                 		case when ri.nro_cheque is not null
                 			then  (select id_plan_cuenta from parametros_contabilidad where parametro = 'valores_depositar' and valido_desde <= cast( r.fecha as date) and valido_hasta >= cast(r.fecha as date))
                 			else cb.id_plan_cuenta
                 		end 
                 end  AS f_de_pag,
                 rcp.importe
         FROM   recibo r
                INNER JOIN recibo_conceptos rc
                ON     r.id = rc.recibo_id   
		INNER JOIN plan_cuentas pc
                ON     pc.id_cuenta_maestro = (select id_plan_cuenta from parametros_contabilidad where parametro ='deudores_cheques_rechazados' and valido_desde <= cast( r.fecha as date) and valido_hasta >= cast(r.fecha as date))
                and pc.valido_desde <= cast(r.fecha as date) and pc.valido_hasta >= cast(r.fecha as date)
                 inner join recibo_conceptos_pagos  rcp
                 on rcp.recibo_concepto_id   = rc.id
                 inner join recibo_ingresos ri
                 on rcp.recibo_ingreso_id  = ri.id
		left outer join efectivo_estado ee
                 on ri.id_estado_efectivo = ee.id
                 left outer join cuenta_bcria cb
                on ri.id_cuenta_bcria_destino_deposito = cb.id_cuenta_bcria
         WHERE  $6 = true
         and (nro_cheque_rechazado IS NOT NULL )
         AND (   (CAST (r.fecha AS DATE) >= cast($1 as date)   AND    CAST (r.fecha AS DATE)  <= cast($2 as date))
              or   (CAST (r.baja_fecha AS DATE) >= cast($1 as date)   AND    CAST (r.baja_fecha AS DATE)  <= cast($2 as date)))
          and ($3 is null or ($3 is not null and r.cuit = $3))
		and ($4 is null or ($4 is not null and  r.sucursal=$4))
         union all
         --otros
         SELECT  'Rec. ' || r.numero as numero_comprobante,
         		 r.fecha,
         		 cast (r.baja_fecha as date),
                 r.cuit,
                 r.sucursal,
                 pc.cuenta,
                 pc.numero,
                  pc.id_cuenta_maestro,
                 case when ee.id_plan_cuenta is not null 
                 	then ee.id_plan_cuenta
                 	else 
                 		case when ri.nro_cheque is not null
                 			then  (select id_plan_cuenta from parametros_contabilidad where parametro = 'valores_depositar' and valido_desde <= cast( r.fecha as date) and valido_hasta >= cast(r.fecha as date))
                 			else cb.id_plan_cuenta
                 		end 
                 end  AS f_de_pag,
                 rcp.importe
         FROM   recibo r
                INNER JOIN recibo_conceptos rc
                ON     r.id = rc.recibo_id
                INNER JOIN conceptos c
                ON     rc.caja_concepto_id = c.id_concepto_maestro
                and cast(c.valido_desde as date) <= cast(r.fecha as date)
                and cast(c.valido_hasta as date) >= cast(r.fecha as date)
                INNER JOIN plan_cuentas pc
                ON     c.id_plan_cuenta  = pc.id_cuenta_maestro
                and pc.valido_desde <= cast(r.fecha as date) and pc.valido_hasta >= cast(r.fecha as date)
                 inner join recibo_conceptos_pagos  rcp
                 on rcp.recibo_concepto_id   = rc.id
                 inner join recibo_ingresos ri
                 on rcp.recibo_ingreso_id  = ri.id
                 left outer join efectivo_estado ee
                 on ri.id_estado_efectivo = ee.id
                 left outer join cuenta_bcria cb
                on ri.id_cuenta_bcria_destino_deposito = cb.id_cuenta_bcria
         WHERE  $6 = true
         and (   (CAST (r.fecha AS DATE) >= cast($1 as date)   AND    CAST (r.fecha AS DATE)  <= cast($2 as date))
              or   (CAST (r.baja_fecha AS DATE) >= cast($1 as date)   AND    CAST (r.baja_fecha AS DATE)  <= cast($2 as date)))
          and ($3 is null or ($3 is not null and r.cuit = $3))
		and ($4 is null or ($4 is not null and  r.sucursal=$4))
	 union all -- CONCEPTOS MARCADOS AL SUBDIARIO DE INGRESOS

	 select 'MOV. BCRIO. '|| c.descripcion || ' ' || m.id_movimiento as numero_comprobante,
		cast (m.fecha_movimiento as date) as fecha,
		cast(m.baja_fecha as date),
		cast ('CUENTA '|| cb.nro_cuenta as varchar),
		cast ('SUC: '|| cb.sucursal as varchar),
		case when m.deb_cred is true and cb.nro_cuenta='78802' then 'BCO.NACION SUBSIDIO N°78802/91' else  pc2.cuenta end as cuenta,
		case when m.deb_cred is true and cb.nro_cuenta='78802' then '1.1.1.2030' else  pc2.numero end as numero,
		case when m.deb_cred is true and cb.nro_cuenta='78802' then 57 else  pc2.id_cuenta_maestro end as id_cuenta_maestro,
		case when m.deb_cred is false then cb.id_plan_cuenta else pc2.id_cuenta_maestro end as f_de_pag,
		--pc2.id_cuenta_maestro,
		--cb.id_plan_cuenta AS f_de_pag,
		cast(m.importe_movimiento as numeric)	
		from movimiento_banco m
		left outer join  tipo_mov_bcrio tmb
		on m.id_tipo_mov = tmb.id_tipo_mov_maestro
		and cast(m.fecha_movimiento as date)>= tmb.valido_desde 
		and cast(m.fecha_movimiento as date) <= tmb.valido_hasta
		left outer join  cuenta_bcria cb
		on m.id_cuenta_bcria = cb.id_cuenta_bcria
		left outer join plan_cuentas pc
		on cb.id_plan_cuenta = pc.id_cuenta_maestro
		and pc.valido_desde <= cast(m.fecha_movimiento as date) and pc.valido_hasta >= cast(m.fecha_movimiento as date)
		left outer join conceptos c
		on tmb.concepto_id = c.id_concepto_maestro
		and cast(c.valido_desde as date) <= cast (m.fecha_movimiento as date)
		and cast(c.valido_hasta as date) >= cast (m.fecha_movimiento as date)
		left outer join plan_cuentas pc2
		on c.id_plan_cuenta = pc2.id_cuenta_maestro
		and pc2.valido_desde <= cast(m.fecha_movimiento as date) and pc2.valido_hasta >= cast(m.fecha_movimiento as date)
		left outer join movimiento_banco_items mbi
		on m.id_movimiento = mbi.id_movimiento
		left outer join cheque ch
		on mbi.nro_cheque = ch.nro_cheque
		and mbi.id_banco = ch.id_banco
		and mbi.id_estado_cheque_nuevo = 5
		where $5 = true 
		and ((fecha_movimiento>=cast($1 as date) and fecha_movimiento<=cast($2 as date)) or (cast(m.baja_fecha as date) >= cast($1 as date) and cast(m.baja_fecha as date) <= cast($2 as date)))
		and m.id_tipo_mov  in  (select id_tipo_mov_maestro from tipo_mov_bcrio   where concepto_id is not null )
		and c.sub_ingreso=true
         union all -- UNO CON LA QUERY DE OS_APORTES_DETALLE
         select null, fecha, null as baja_fecha, aux.cuit, aux.sucursal, pc.cuenta, pc.numero,  pc.id_cuenta_maestro,
         (select id_plan_cuenta from parametros_contabilidad where parametro = 'bco_nacion' and valido_desde <= cast( fecha as date) and valido_hasta >= cast(fecha as date)) as f_de_pag, 
         importe from (         
				select fecha_transf as fecha , cuit_contribuyente as cuit , cast('000' as character varying) as sucursal, concepto_transf, sum(importe) as importe
				from os_aportes_detalle oad
				where $7 = true 
				and oad.concepto_transf <> 'COM' and sucur not like 'NO_%'
				and cast (fecha_transf as date)>= cast($1 as date)
				and cast (fecha_transf as date)<= cast($2 as date)
				group by fecha_transf, cuit_contribuyente,  concepto_transf
			 ) aux
		 left outer join concepto_transferencia ct
		 on aux.concepto_transf = ct.concepto_transf
		 and cast (fecha as date) >= ct.valido_desde
		 and cast (fecha as date) <= ct.valido_hasta
		 left outer join conceptos c
		 on ct.concepto_id = c.id_concepto_maestro
		 and cast (fecha as date) >= c.valido_desde
		 and cast (fecha as date) <= c.valido_hasta 
		 left outer join plan_cuentas pc
		 on c.id_plan_cuenta = pc.id_cuenta_maestro
		 and pc.valido_desde <= cast(fecha as date) and pc.valido_hasta >= cast(fecha as date)
		  where($3 is null and $4 is null)
		  and $7 = true
		 union all
		  select null, fecha, null as baja_fecha, aux.cuit, aux.sucursal, pc.cuenta, pc.numero,  pc.id_cuenta_maestro,
		  (select id_plan_cuenta from parametros_contabilidad where parametro = 'descuento_anticipos' and valido_desde <= cast( fecha as date) and valido_hasta >= cast(fecha as date)) as f_de_pag, 
		  importe from (         
				select fecha_transf as fecha , cuit_contribuyente as cuit , cast('000' as character varying) as sucursal, concepto_transf, sum(importe) as importe
				from os_aportes_detalle oad
				where $7 = true 
				and oad.concepto_transf <> 'COM' and sucur like 'NO_%'
				and cast (fecha_transf as date)>= cast($1 as date)
				and cast (fecha_transf as date)<= cast($2 as date)
				group by fecha_transf, cuit_contribuyente,  concepto_transf
			 ) aux
		 left outer join concepto_transferencia ct
		 on aux.concepto_transf = ct.concepto_transf
		 and cast (fecha as date) >= ct.valido_desde
		 and cast (fecha as date) <= ct.valido_hasta
		 left outer join conceptos c
		 on ct.concepto_id = c.id_concepto_maestro
		 and cast (fecha as date) >= c.valido_desde
		 and cast (fecha as date) <= c.valido_hasta
		 left outer join plan_cuentas pc
		 on c.id_plan_cuenta = pc.id_cuenta_maestro
		  and pc.valido_desde <= cast(fecha as date) and pc.valido_hasta >= cast(fecha as date)
		  where($3 is null and $4 is null)
		  and $7 = true
		 union all
		--DEPOSITO DE LAS COSAS INGRESADAS POR RECIBO
			select 'Mov Bcrio ' || mb.id_movimiento, 
				cast(fecha_movimiento as date) , 
				cast(mb.baja_fecha as date), 
				null, 
				null, pc.cuenta, 
				pc.numero,
				pc.id_cuenta_maestro,
				cb.id_plan_cuenta  as f_de_pag,
				cast(sum(c.importe) as numeric)
			from movimiento_banco_items  mbi,
			     movimiento_banco mb,
			     recibo_ingresos ri,
			     cuenta_bcria cb,
			     recibo r, plan_cuentas pc,
			     cheque c
			where $5 = true 
			and mbi.id_movimiento = mb.id_movimiento
			and mbi.nro_cheque = ri.nro_cheque
			and mbi.id_banco = ri.id_banco
			and mbi.nro_cheque = c.nro_cheque
			and mbi.id_banco = c.id_banco
			and mbi.id_estado_cheque_nuevo = 4
			and mb.id_cuenta_bcria = cb.id_cuenta_bcria
			and ri.recibo_id = r.id
			and pc.id_cuenta_maestro = (select id_plan_cuenta from parametros_contabilidad where parametro = 'valores_depositar' and valido_desde <= cast(fecha_movimiento as date) and valido_hasta >= cast(fecha_movimiento as date) )
			 and pc.valido_desde <= cast(fecha_movimiento as date) and pc.valido_hasta >= cast(fecha_movimiento as date)
			AND (   (CAST (fecha_movimiento AS DATE) >= cast($1 as date)   AND    CAST (fecha_movimiento AS DATE)  <= cast($2 as date))
			    or   (CAST (mb.baja_fecha AS DATE) >= cast($1 as date)   AND    CAST (mb.baja_fecha AS DATE)  <= cast($2 as date)))
			and (ri.baja_fecha is null or (ri.baja_fecha is not null and CAST (ri.baja_fecha AS DATE) <= cast($1 as date)))
			and ($3 is null or ($3 is not null and r.cuit = $3))
			and ($4 is null or ($4 is not null and  r.sucursal=$4))
			group by mb.id_movimiento, mb.fecha_movimiento, mb.baja_fecha,pc.cuenta, pc.numero, pc.id_cuenta_maestro, cb.id_plan_cuenta
			union all
			select 'Mov Bcrio ' || mb.id_movimiento, cast(fecha_movimiento as date) , cast(mb.baja_fecha as date),
			null, null,pc.cuenta, pc.numero, pc.id_cuenta_maestro, cb.id_plan_cuenta  as f_de_pag,  
			cast(sum(ri.importe) as numeric)
			from movimiento_banco_items  mbi,
			     movimiento_banco mb,
			     recibo_ingresos ri,
			     cuenta_bcria cb,
			     recibo r,  plan_cuentas pc,
			     efectivo_estado ee
			where $5 = true 
			and mbi.id_movimiento = mb.id_movimiento
			and recibo_ingreso_id= ri.id
			and mb.id_cuenta_bcria = cb.id_cuenta_bcria
			and ri.recibo_id = r.id
			and ri.id_estado_efectivo = ee.id
			and pc.id_cuenta_maestro = ee.id_plan_cuenta
			and pc.valido_desde <= cast(fecha_movimiento as date) and pc.valido_hasta >= cast(fecha_movimiento as date)
			AND (   (CAST (fecha_movimiento AS DATE) >= cast($1 as date)   AND    CAST (fecha_movimiento AS DATE)  <= cast($2 as date))
			    or   (CAST (mb.baja_fecha AS DATE) >= cast($1 as date)   AND    CAST (mb.baja_fecha AS DATE)  <= cast($2 as date)))			
			and ($3 is null or ($3 is not null and r.cuit = $3))
			and ($4 is null or ($4 is not null and  r.sucursal=$4))
			group by mb.id_movimiento, mb.fecha_movimiento, mb.baja_fecha,pc.cuenta, pc.numero, pc.id_cuenta_maestro, cb.id_plan_cuenta
			union all
			select 'Acreditación mensual subsidio' , 
				cast(fecha_proceso as date) , 
				null, 
				null, 
				null, 
				pc.cuenta, 
				pc.numero,
				 pc.id_cuenta_maestro,
				(select id_plan_cuenta from parametros_contabilidad where parametro = 'bco_nacion' and valido_desde <= cast( fecha_proceso as date) and valido_hasta >= cast(fecha_proceso as date)) as f_de_pag,
				sum(subsidio ) as importe
			from detalle_subsidio_os 
			INNER JOIN plan_cuentas pc
		        ON     pc.id_cuenta_maestro = (select id_plan_cuenta from parametros_contabilidad where parametro ='acreditacion_subsidio' and valido_desde <= cast(fecha_proceso as date) and valido_hasta >= cast(fecha_proceso as date))
		        and pc.valido_desde <= cast(fecha_proceso as date) and pc.valido_hasta >= cast(fecha_proceso as date)
		        where $5 = true 
		        and (cast(fecha_proceso as date)>=cast($1 as date) and cast(fecha_proceso as date)<=cast($2 as date)) 
			group by fecha_proceso, pc.cuenta, pc.numero, pc.id_cuenta_maestro
	)  subdiario
         LEFT OUTER JOIN empresa e
         ON       subdiario.cuit     = e.cuit
         AND      subdiario.sucursal = e.sucursal
         left outer join plan_cuentas pc
         on f_de_pag = pc.id_cuenta_maestro
            and pc.valido_desde <= cast(fecha as date) and pc.valido_hasta >= cast(fecha as date)
group by subdiario.numero_comprobante,
	 subdiario.fecha,
	 subdiario.baja_fecha,
         subdiario.cuit,
         subdiario.sucursal,
         e.razon_soc,
         subdiario.cuenta,
         subdiario.numero,
         subdiario.id_cuenta_maestro,
         pc.numero,
         pc.cuenta,
         pc.id_cuenta_maestro
ORDER BY fecha, baja_fecha, numero_comprobante

$BODY$
  LANGUAGE sql VOLATILE

