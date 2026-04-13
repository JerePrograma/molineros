drop FUNCTION reporte_orden_pago_amtima_farmacia(p_id integer,  p_id_fin integer) ;
drop type return_reporte_orden_pago_amtima_farmacia;

create type return_reporte_orden_pago_amtima_farmacia as
(id_orden_pago integer,
comprobantes character varying, 
comprobantes2 character varying, 
pagos character varying,
conceptos character varying,
observaciones character varying, 
importe numeric, 
cuit_sucu character varying, 
fecha text,
razon_soc character varying,
baja character varying,
fecha_desde character varying,
fecha_hasta character varying,
descuento numeric,
descuento_valor numeric,
descuento_por_drogueria numeric ,        
importe_sin_dcto numeric
);


CREATE OR REPLACE FUNCTION reporte_orden_pago_amtima_farmacia(p_id integer, p_id_fin integer) 
RETURNS SETOF return_reporte_orden_pago_amtima_farmacia

    LANGUAGE plpgsql
    AS $BODY$
DECLARE _op  RECORD;
DECLARE _record  RECORD;
DECLARE _comps varchar;
DECLARE _comps_importe numeric(12,2);
DECLARE _record2  RECORD;
DECLARE _comps2 varchar;
DECLARE _comps2_importe numeric(12,2);
DECLARE _pagos varchar;
DECLARE _conceptos varchar;
declare r record;
declare r_conceptos record;
begin
_comps = '';
_comps_importe = 0;
_comps2 = '';
_comps2_importe = 0;
_pagos= '';
_conceptos = '';

drop table if exists temp_reporte_op_amtima;
create table temp_reporte_op_amtima(
	id_orden_pago integer,
	comps text,
	comps_importe numeric(12,2),
	comps2 text,
	comps2_importe numeric(12,2),
	pagos text,
	conceptos text
);

 
for _op in select id_orden_pago from orden_pago_amtima where id_orden_pago >= $1 and id_orden_pago <= $2
loop
	_comps = '';
	_comps_importe = 0;
	FOR _record IN
	select (c.compro_tipo  || ' ' || c.id_punto_venta ||'-'||  c.compro_nro  || '  Importe: $' || (case when c.debito_para_egreso then -1*c.total else c.total end) )
	 as inf, (case when c.debito_para_egreso then -1*c.total else c.total end) as comps_importe from comprobante_amtima c
	inner join comprobante_orden_pago_amtima copo
	on copo.id_orden_pago_amtima = _op.id_orden_pago
	and copo.id_punto_venta = c.id_punto_venta
	and copo.compro_tipo = c.compro_tipo
	and copo.compro_nro = c.compro_nro
	and copo.compro_letra = c.compro_letra
	and copo.compro_sucu = c.compro_sucu
	and copo.cuit = c.cuit
	union all
	select ('ANT' || ' ' || opop.compro_nro_antic || ' ' ||opop.compro_letra_antic )   ||   '  Importe: $-' || cast(c.total as character varying)  as inf,
	(case when c.debito_para_egreso then -1*c.total else c.total end) as comps_importe
	from orden_pago_amtima_pagos opop
	inner join comprobante_amtima c
	on opop.id_punto_venta_antic =  c.id_punto_venta
	and opop.compro_tipo_antic =c.compro_tipo
	and opop.compro_letra_antic  =c.compro_letra
	and opop.compro_sucu_antic =c.compro_sucu
	and opop.compro_nro_antic =c.compro_nro
	and opop.cuit_antic = c.cuit
	where opop.id_punto_venta_antic is not null
	and opop.id_orden_pago = _op.id_orden_pago
	order by 1
	limit 42
	LOOP
  _comps = _record.inf || '
' || _comps;
	_comps_importe = _comps_importe + _record.comps_importe;
	END LOOP;
	insert into temp_reporte_op_amtima (id_orden_pago, comps, comps_importe) values (_op.id_orden_pago, _comps, _comps_importe);
end loop;

 
for _op in select id_orden_pago from orden_pago_amtima where id_orden_pago >= $1 and id_orden_pago <= $2
loop
	_comps2 = '';
	_comps2_importe = 0;
	FOR _record2 IN
	select (c.compro_tipo  || ' ' || c.id_punto_venta ||'-'||  c.compro_nro  || '  Importe: $' || (case when c.debito_para_egreso then -1*c.total else c.total end) )
	 as inf, (case when c.debito_para_egreso then -1*c.total else c.total end) as comps2_importe from comprobante_amtima c
	inner join comprobante_orden_pago_amtima copo
	on copo.id_orden_pago_amtima = _op.id_orden_pago
	and copo.id_punto_venta = c.id_punto_venta
	and copo.compro_tipo = c.compro_tipo
	and copo.compro_nro = c.compro_nro
	and copo.compro_letra = c.compro_letra
	and copo.compro_sucu = c.compro_sucu
	and copo.cuit = c.cuit
	union all
	select ('ANT' || ' ' || opop.compro_nro_antic || ' ' ||opop.compro_letra_antic )   ||   '  Importe: $-' || cast(c.total as character varying)  as inf, 
	(case when c.debito_para_egreso then -1*c.total else c.total end) as comps2_importe
	from orden_pago_amtima_pagos opop
	inner join comprobante_amtima c
	on opop.id_punto_venta_antic =  c.id_punto_venta
	and opop.compro_tipo_antic =c.compro_tipo
	and opop.compro_letra_antic  =c.compro_letra
	and opop.compro_sucu_antic =c.compro_sucu
	and opop.compro_nro_antic =c.compro_nro
	and opop.cuit_antic = c.cuit
	where opop.id_punto_venta_antic is not null
	and opop.id_orden_pago = _op.id_orden_pago
	order by 1
	offset 42
LOOP
  _comps2 = _record2.inf || '
' || _comps2;
_comps2_importe = _comps2_importe + _record.comps2_importe;
	END LOOP;
	update temp_reporte_op_amtima set comps2 = _comps2, comps2_importe = _comps2_importe where id_orden_pago = _op.id_orden_pago;
end loop;

for _op in select id_orden_pago from orden_pago_amtima where id_orden_pago >= $1 and id_orden_pago <= $2
loop
	_pagos = '';
	for r in  
		select inf from (
		select ('Cheque' || ' - Nro: ' || opop.nro_cheque || ' - ' || cb.descripcion || ' ' || cb.nro_cuenta || '/' || cb.sucursal)|| lpad( '$' || cast(c.importe as character varying),13, ' ') 
		 	|| (case when c.a_nombre_de is not null then '
	' || 'A Favor De: ' || substring(c.a_nombre_de for 30) else '' end)
	|| (case when c.baja_fecha is not null then '
	' || 'Anulado: ' || to_char(c.baja_fecha, 'DD-MM-YYYY') else '' end)as inf
		from orden_pago_amtima_pagos opop
		inner join cheque_amtima c
		on opop.nro_cheque = c.nro_cheque
		and opop.id_banco_cheque = c.id_banco
		inner join cuenta_bcria cb
		on c.id_cta_bcria = cb.id_cuenta_bcria	
		inner join banco b
		on cb.id_banco = b.id_banco
		where opop.nro_cheque is not null
		and opop.id_orden_pago = _op.id_orden_pago
		union all
		select ('Retencion'|| ' - ' || cb2.descripcion || ' ' || cb2.nro_cuenta || '/' || cb2.sucursal ) || lpad( '$' || cast(opop.importe_retencion as character varying), 13, ' ') as inf
		from orden_pago_amtima_pagos opop
		inner join cuenta_bcria cb2
		on opop.id_cta_bcria_retencion = cb2.id_cuenta_bcria	
		inner join banco b2
		on cb2.id_banco = b2.id_banco
		where opop.id_cta_bcria_retencion is not null
		and opop.id_orden_pago = _op.id_orden_pago
		union all
		select (tp.descripcion || ( case when opop.nro_debito_bcrio is not null then ' - Nro:' || opop.nro_debito_bcrio else '' end )|| ' - ' ||  cb4.descripcion || ' ' || cb4.nro_cuenta || '/' || cb4.sucursal) || lpad('$' || cast(opop.importe_debito_bcrio as character varying), 13, ' ') as inf
		from orden_pago_amtima_pagos opop
		inner join cuenta_bcria cb4
		on opop.id_cta_bcria_debito_crio = cb4.id_cuenta_bcria	
		inner join banco b4
		on cb4.id_banco = b4.id_banco
		inner join tipo_pago tp
		on opop.tipo_pago = tp.id_tipo_pago
		where opop.id_cta_bcria_debito_crio is not null
		and opop.id_orden_pago = _op.id_orden_pago) a 
		where inf is not null
	loop
 _pagos = r.inf || '
' || _pagos ;
	end loop;
	update temp_reporte_op_amtima set pagos = _pagos where id_orden_pago = _op.id_orden_pago;
end loop;

for _op in select id_orden_pago from orden_pago_amtima where id_orden_pago >= $1 and id_orden_pago <= $2
loop
	_conceptos = '';
	for r_conceptos in 
	select (conc.descripcion || '  $' ||  sum(case when c.debito_para_egreso then -1*cc.importe else cc.importe end) ) as inf  from comprobante_orden_pago_amtima copo
	inner join comprobante_amtima c
	on copo.id_orden_pago_amtima = _op.id_orden_pago
	and copo.id_punto_venta = c.id_punto_venta
	and copo.compro_tipo = c.compro_tipo
	and copo.compro_nro = c.compro_nro
	and copo.compro_letra = c.compro_letra
	and copo.compro_sucu = c.compro_sucu
	and copo.cuit = c.cuit
	inner join concepto_comprobante_amtima cc
	on  c.id_punto_venta = cc.id_punto_venta
	and c.compro_tipo = cc.compro_tipo
	and c.compro_nro = cc.compro_nro
	and c.compro_letra = cc.compro_letra
	and c.compro_sucu = cc.compro_sucu
	and c.cuit = cc.cuit
	inner join conceptos_amtima conc
	on cc.concepto_id = conc.id
	group by conc.descripcion
	having sum(case when c.debito_para_egreso then -1*cc.importe else cc.importe end)  <> 0
	loop
	_conceptos = r_conceptos.inf || '; ' || _conceptos;
	end loop;
	update temp_reporte_op_amtima set conceptos = _conceptos where id_orden_pago = _op.id_orden_pago;
end loop;

for _op in select id_orden_pago from orden_pago_amtima where id_orden_pago >= $1 and id_orden_pago <= $2
loop
	_conceptos = '';
	for r_conceptos in 
	select (conc.descripcion || '  $' || sum(-1*cc.importe)) as inf  
	from ( select c.id_punto_venta, c.compro_tipo, c.compro_letra, c.compro_sucu, c.compro_nro, c.cuit
		from orden_pago_amtima_pagos opop
		inner join comprobante_amtima c
		on opop.id_punto_venta_antic =  c.id_punto_venta
		and opop.compro_tipo_antic =c.compro_tipo
		and opop.compro_letra_antic  =c.compro_letra
		and opop.compro_sucu_antic =c.compro_sucu
		and opop.compro_nro_antic =c.compro_nro
		and opop.cuit_antic = c.cuit
		where opop.id_punto_venta_antic is not null
		and opop.id_orden_pago = _op.id_orden_pago)  c
	inner join concepto_comprobante_amtima cc
	on  c.id_punto_venta = cc.id_punto_venta
	and c.compro_tipo = cc.compro_tipo
	and c.compro_nro = cc.compro_nro
	and c.compro_letra = cc.compro_letra
	and c.compro_sucu = cc.compro_sucu
	and c.cuit = cc.cuit
	inner join conceptos_amtima conc
	on cc.concepto_id = conc.id
	group by conc.descripcion
	loop
	_conceptos = r_conceptos.inf || '; ' || _conceptos;
	end loop;
	update temp_reporte_op_amtima set conceptos = conceptos || _conceptos where id_orden_pago = _op.id_orden_pago;
end loop;	


return  query select op.id_orden_pago, 
		cast(troa.comps as character varying),
		cast(troa.comps2 as character varying),
		cast(troa.pagos as character varying),
		cast(troa.conceptos as character varying),
		case when op.observaciones is null then '' else op.observaciones end, 
		op.importe, 
		cast ('Cuit: ' || cuit_acreedor || ' - Suc: ' || (case when op.id_seccional is not null and op.id_seccional <> 0 then cast(op.id_seccional as character varying) else sucu_acreedor end) as character varying),
		 to_char(op.alta_fecha, 'dd/mm/yyyy'),
		 cast (e.razon_soc || (case when op.id_seccional is not null then (' - ' || secc.descripcion) else '' end)   as character varying),
		 cast(case when op.baja_fecha is not null then 'Anulada ' || to_char(op.baja_fecha, 'DD-MM-YYYY') else '' end as character varying),
		 cast(to_char(op.fecha_desde, 'mm/yyyy') as character varying) as fecha_desde,
         cast(to_char(op.fecha_hasta, 'mm/yyyy') as character varying)  as fecha_hasta,
         op.descuento,
         round(((comps_importe + comps2_importe)) *descuento /100,2 ) as descuento_valor ,
         op.descuento_por_drogueria,
         comps_importe + comps2_importe
  from temp_reporte_op_amtima troa 
  inner join orden_pago_amtima op
  on troa.id_orden_pago = op.id_orden_pago
  left outer join empresa e
  on op.cuit_acreedor = e.cuit
  and op.sucu_acreedor = e.sucursal
  left outer join seccional secc
  on op.id_seccional = secc.id_Seccional;


end;
$BODY$;


