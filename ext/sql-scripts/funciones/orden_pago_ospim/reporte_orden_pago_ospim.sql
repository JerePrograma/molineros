drop FUNCTION reporte_orden_pago_ospim(p_id integer) ;
drop type return_reporte_orden_pago_ospim;

create type return_reporte_orden_pago_ospim as
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
baja character varying
);


CREATE OR REPLACE FUNCTION reporte_orden_pago_ospim(p_id integer) 
RETURNS SETOF return_reporte_orden_pago_ospim

    LANGUAGE plpgsql
    AS $BODY$
DECLARE _record  RECORD;
DECLARE _comps varchar;
DECLARE _record2  RECORD;
DECLARE _comps2 varchar;
DECLARE _pagos varchar;
DECLARE _conceptos varchar;
declare r record;
declare r_conceptos record;
begin
_comps = '';
_comps2 = '';
_pagos= '';
_conceptos = '';



FOR _record IN

select (to_char(c.fecha_recepcion, 'dd/mm/yyyy') || ' - ' || c.compro_tipo  || ' ' || c.id_punto_venta ||'-'||  c.compro_nro  || '  Importe: $' || (case when c.debito_para_egreso then -1*c.total else c.total end) )
 as inf from comprobante c
inner join comprobante_orden_pago_ospim copo
on copo.id_orden_pago_ospim = $1
and copo.id_punto_venta = c.id_punto_venta
and copo.compro_tipo = c.compro_tipo
and copo.compro_nro = c.compro_nro
and copo.compro_letra = c.compro_letra
and copo.compro_sucu = c.compro_sucu
and copo.cuit = c.cuit
union all
select ('ANT' || ' ' || opop.compro_nro_antic || ' ' ||opop.compro_letra_antic )   ||   '  Importe: $-' || cast(c.total as character varying ) || '
' || ' CUIT: ' || c.cuit || ' OP: ' || opo2.id_orden_pago || ' Fecha: ' ||  to_char(opo2.alta_fecha, 'dd/mm/yyyy')   as inf
from orden_pago_ospim_pagos opop
inner join comprobante c
on opop.id_punto_venta_antic =  c.id_punto_venta
and opop.compro_tipo_antic =c.compro_tipo
and opop.compro_letra_antic  =c.compro_letra
and opop.compro_sucu_antic =c.compro_sucu
and opop.compro_nro_antic =c.compro_nro
and opop.cuit_antic = c.cuit
inner join comprobante_orden_pago_ospim copo
on opop.id_punto_venta_antic =  copo.id_punto_venta
and opop.compro_tipo_antic =copo.compro_tipo
and opop.compro_letra_antic  =copo.compro_letra
and opop.compro_sucu_antic =copo.compro_sucu
and opop.compro_nro_antic =copo.compro_nro
and opop.cuit_antic = copo.cuit
inner join orden_pago_ospim opo2
on copo.id_orden_pago_ospim = opo2.id_orden_pago
and opo2.baja_fecha is null
where opop.id_punto_venta_antic is not null
and opop.id_orden_pago = $1
order by 1
limit 42
LOOP
  _comps = _record.inf || '
' || _comps;

END LOOP;


FOR _record2 IN

select (to_char(c.fecha_recepcion, 'dd/mm/yyyy') || ' - ' || c.compro_tipo  || ' ' || c.id_punto_venta ||'-'||  c.compro_nro  || '  Importe: $' || (case when c.debito_para_egreso then -1*c.total else c.total end) )
 as inf from comprobante c
inner join comprobante_orden_pago_ospim copo
on copo.id_orden_pago_ospim = $1
and copo.id_punto_venta = c.id_punto_venta
and copo.compro_tipo = c.compro_tipo
and copo.compro_nro = c.compro_nro
and copo.compro_letra = c.compro_letra
and copo.compro_sucu = c.compro_sucu
and copo.cuit = c.cuit
union all
select ('ANT' || ' ' || opop.compro_nro_antic || ' ' ||opop.compro_letra_antic )   ||   '  Importe: $-' || cast(c.total as character varying ) || '
' || ' CUIT: ' || c.cuit || ' OP: ' || opo2.id_orden_pago || ' Fecha: ' ||  to_char(opo2.alta_fecha, 'dd/mm/yyyy')   as inf
from orden_pago_ospim_pagos opop
inner join comprobante c
on opop.id_punto_venta_antic =  c.id_punto_venta
and opop.compro_tipo_antic =c.compro_tipo
and opop.compro_letra_antic  =c.compro_letra
and opop.compro_sucu_antic =c.compro_sucu
and opop.compro_nro_antic =c.compro_nro
and opop.cuit_antic = c.cuit
inner join comprobante_orden_pago_ospim copo
on opop.id_punto_venta_antic =  copo.id_punto_venta
and opop.compro_tipo_antic =copo.compro_tipo
and opop.compro_letra_antic  =copo.compro_letra
and opop.compro_sucu_antic =copo.compro_sucu
and opop.compro_nro_antic =copo.compro_nro
and opop.cuit_antic = copo.cuit
inner join orden_pago_ospim opo2
on copo.id_orden_pago_ospim = opo2.id_orden_pago
and opo2.baja_fecha is null
where opop.id_punto_venta_antic is not null
and opop.id_orden_pago = $1
order by 1
offset 42
LOOP
  _comps2 = _record2.inf || '
' || _comps2;

END LOOP;


for r in  
	select inf from (
	select ('Cheque' || ' - Nro: ' || opop.nro_cheque || ' - ' || cb.descripcion || ' ' || cb.nro_cuenta || '/' || cb.sucursal)|| lpad( '$' || cast(c.importe as character varying),13, ' ') 
	 	|| (case when c.a_nombre_de is not null then '
' || 'A Favor De: ' || substring(c.a_nombre_de for 30) else '' end)
|| (case when c.baja_fecha is not null then '
' || 'Anulado: ' || to_char(c.baja_fecha, 'DD-MM-YYYY') else '' end)as inf
	from orden_pago_ospim_pagos opop
	inner join cheque c
	on opop.nro_cheque = c.nro_cheque
	and opop.id_banco_cheque = c.id_banco
	inner join cuenta_bcria cb
	on c.id_cta_bcria = cb.id_cuenta_bcria	
	inner join banco b
	on cb.id_banco = b.id_banco
	where opop.nro_cheque is not null
	and opop.id_orden_pago = $1
	union all
	select ('Retencion'|| ' - ' || cb2.descripcion || ' ' || cb2.nro_cuenta || '/' || cb2.sucursal ) || lpad( '$' || cast(opop.importe_retencion as character varying), 13, ' ') as inf
	from orden_pago_ospim_pagos opop
	inner join cuenta_bcria cb2
	on opop.id_cta_bcria_retencion = cb2.id_cuenta_bcria	
	inner join banco b2
	on cb2.id_banco = b2.id_banco
	where opop.id_cta_bcria_retencion is not null
	and opop.id_orden_pago = $1
	union all
	select (tp.descripcion || ( case when opop.nro_debito_bcrio is not null then ' - Nro:' || opop.nro_debito_bcrio else '' end )|| ' - ' ||  cb4.descripcion || ' ' || cb4.nro_cuenta || '/' || cb4.sucursal) || lpad('$' || cast(opop.importe_debito_bcrio as character varying), 13, ' ') as inf
	from orden_pago_ospim_pagos opop
	inner join cuenta_bcria cb4
	on opop.id_cta_bcria_debito_crio = cb4.id_cuenta_bcria	
	inner join banco b4
	on cb4.id_banco = b4.id_banco
	inner join tipo_pago tp
	on opop.tipo_pago = tp.id_tipo_pago
	where opop.id_cta_bcria_debito_crio is not null
	and opop.id_orden_pago = $1) a 
	where inf is not null
loop
 _pagos = r.inf || '
' || _pagos ;
end loop;

for r_conceptos in 
select (conc.descripcion || '  $' ||  sum(case when c.debito_para_egreso then -1*cc.importe else cc.importe end) ) as inf  from comprobante_orden_pago_ospim copo
inner join comprobante c
on copo.id_orden_pago_ospim = $1
and copo.id_punto_venta = c.id_punto_venta
and copo.compro_tipo = c.compro_tipo
and copo.compro_nro = c.compro_nro
and copo.compro_letra = c.compro_letra
and copo.compro_sucu = c.compro_sucu
and copo.cuit = c.cuit
inner join concepto_comprobante cc
on  c.id_punto_venta = cc.id_punto_venta
and c.compro_tipo = cc.compro_tipo
and c.compro_nro = cc.compro_nro
and c.compro_letra = cc.compro_letra
and c.compro_sucu = cc.compro_sucu
and c.cuit = cc.cuit
inner join conceptos conc
on cc.concepto_id = conc.id_concepto_maestro
and cast(conc.valido_desde as date)  <= cast(c.fecha_recepcion as date)
and cast(conc.valido_hasta as date)  >= cast(c.fecha_recepcion as date)
group by conc.descripcion
having sum(case when c.debito_para_egreso then -1*cc.importe else cc.importe end)  <> 0
loop
_conceptos = r_conceptos.inf || '; ' || _conceptos;
end loop;

for r_conceptos in 
select (conc.descripcion || '  $' || sum(-1*cc.importe)) as inf  
from ( select c.id_punto_venta, c.compro_tipo, c.compro_letra, c.compro_sucu, c.compro_nro, c.cuit, c.fecha_recepcion
	from orden_pago_ospim_pagos opop
	inner join comprobante c
	on opop.id_punto_venta_antic =  c.id_punto_venta
	and opop.compro_tipo_antic =c.compro_tipo
	and opop.compro_letra_antic  =c.compro_letra
	and opop.compro_sucu_antic =c.compro_sucu
	and opop.compro_nro_antic =c.compro_nro
	and opop.cuit_antic = c.cuit
	where opop.id_punto_venta_antic is not null
	and opop.id_orden_pago = $1)  c
inner join concepto_comprobante cc
on  c.id_punto_venta = cc.id_punto_venta
and c.compro_tipo = cc.compro_tipo
and c.compro_nro = cc.compro_nro
and c.compro_letra = cc.compro_letra
and c.compro_sucu = cc.compro_sucu
and c.cuit = cc.cuit
inner join conceptos conc
on cc.concepto_id = conc.id_concepto_maestro
and cast(conc.valido_desde as date)  <= cast(c.fecha_recepcion as date)
and cast(conc.valido_hasta as date)  >= cast(c.fecha_recepcion as date)
group by conc.descripcion
loop
_conceptos = r_conceptos.inf || '; ' || _conceptos;
end loop;


return  query select id_orden_pago, 
		_comps,
		_comps2, 
		_pagos, 
		_conceptos,
		case when op.observaciones is null then '' else op.observaciones end, 
		op.importe, 
		cast ('Cuit: ' || cuit_acreedor || ' - Suc: ' || (case when op.id_seccional is not null and op.id_seccional <> 0 then cast(op.id_seccional as character varying) else sucu_acreedor end) as character varying),
		 to_char(op.alta_fecha, 'dd/mm/yyyy'),
		 cast (e.razon_soc || (case when op.id_seccional is not null then (' - ' || secc.descripcion) else '' end)   as character varying),
		 cast(case when op.baja_fecha is not null then 'Anulada ' || to_char(op.baja_fecha, 'DD-MM-YYYY') else '' end as character varying)
  from orden_pago_ospim op
  left outer join empresa e
  on op.cuit_acreedor = e.cuit
  and op.sucu_acreedor = e.sucursal
  left outer join seccional secc
  on op.id_seccional = secc.id_Seccional
  where id_orden_pago = $1;


end;
$BODY$;

