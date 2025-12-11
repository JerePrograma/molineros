CREATE OR REPLACE FUNCTION reporte_nota_debito_liquidacion(IN p_id integer, IN terceros character varying, IN importe_terceros numeric)
  RETURNS TABLE(numero_nd character varying, importe numeric, cuit_sucu character varying, razon_soc character varying, domicilio character varying, fecha text, conceptos character varying, observaciones character varying) AS
$BODY$

DECLARE _record  RECORD;
DECLARE _comps varchar;
DECLARE _fact varchar;

begin
_comps = '';

if (terceros = '0') then

	FOR _record IN
	select 'Tipo: ' || ci.compro_tipo  || ' ' ||  ci.compro_nro  || ' Motivo ' || m.descripcion || '  ' || ci.observaciones || '  Importe: $' || 
	ci.saldo 
	 as inf from comprobante_liquidacion cl, compro_items ci, motivo m
		 where cl.id_liquidacion = p_id
		  and ci.id_punto_venta = cl.id_punto_venta
		  and ci.compro_tipo = cl.compro_tipo
		  and ci.compro_letra = cl.compro_letra
		  and ci.compro_sucu = cl.compro_sucu
		  and ci.compro_nro = cl.compro_nro
		  and ci.cuit = cl.cuit
		  and (ci.compro_tipo = 'NDB' or ci.compro_tipo = 'NDI')
		  and ci.compro_tipo = m.compro_tipo
		  and ci.motivo = m.id_motivo
	LOOP
	  _comps = _comps || '
	'|| _record.inf;

	END LOOP;	
	
	  _fact = cl.compro_tipo  || ' ' || cl.compro_letra || cast(cl.compro_sucu as character varying) || ' ' || cl.compro_nro from 
	  comprobante_liquidacion cl
		 where cl.id_liquidacion = p_id
		 	and cl.compro_tipo not in ('NDB', 'NDI');		  	

	return query

	select

	cast (case when c.alta_fecha < '21-09-2011' then '0001-' else '0002-' end 
	|| cast(lpad(cast(ci.compro_nro as character varying), 8, '0') as character varying) as character varying),	
	c.total,
	p.cuit,	
	p.descripcion,
	cast ('' as character varying),	
	to_char(c.fecha,'dd/mm/yyyy'),
	_fact,
	_comps

	from liquidacion l, comprobante_liquidacion cl, comprobante c, compro_items ci, prestador p
	   where l.id_liquidacion = p_id
	   and l.id_liquidacion = cl.id_liquidacion
	   and c.id_punto_venta = cl.id_punto_venta
	   and c.compro_tipo = cl.compro_tipo
	   and c.compro_nro = cl.compro_nro
	   and c.cuit = cl.cuit
	   and c.compro_letra = cl.compro_letra
	   and c.compro_sucu = cl.compro_sucu
	   and (ci.compro_tipo = 'NDB' or ci.compro_tipo = 'NDI')
	   and l.id_prestador = p.id_prestador
	   and ci.id_punto_venta = cl.id_punto_venta
	   and ci.compro_tipo = cl.compro_tipo
	   and ci.compro_letra = cl.compro_letra
	   and ci.compro_sucu = cl.compro_sucu
	   and ci.compro_nro = cl.compro_nro
	   and ci.cuit = cl.cuit 
	   and ci.item = (select min (ci2.item) from compro_items ci2
	   where ci2.compro_tipo = ci.compro_tipo
	   and ci2.compro_letra = ci.compro_letra
	   and ci2.compro_sucu = ci.compro_sucu
	   and ci2.compro_nro = ci.compro_nro
	   and ci2.cuit = ci.cuit);

end if;
if (terceros = '1') then

	return query
	
	select

	cast ('0002-' || cast(lpad(cast(l.numero_ndb as character varying), 8, '0') as character varying) as character varying),	
	importe_terceros,
	cast ('30520634971' as character varying),	
	cast ('CONSOLIDAR SALUD SOCIEDAD ANONIMA' as character varying),
	cast ('' as character varying),
	to_char(l.alta_fecha,'dd/mm/yyyy'),
	cast ('' as character varying),
	l.observaciones	
	from liquidacion_debitos_terceros l

	where l.id_liquidacion = cast(p_id as integer);

end if;

if (terceros = '2') then
	return query
	
	select
	
	cast (case when c.alta_fecha < '23-09-2011' then '0001-' else '0002-' end 
	|| cast(lpad(cast(c.compro_nro as character varying), 8, '0') as character varying) as character varying),	
	c.total,
	c.cuit_acreedor,
	e.razon_soc,
	cast ('' as character varying),
	to_char(c.fecha,'dd/mm/yyyy'),
	cast ('' as character varying),
	c.observaciones

	from comprobante c left outer join empresa e
					 
	on 

	c.cuit_acreedor = e.cuit and 
	c.sucu_acreedor = e.sucursal
  
	 where
	   c.compro_tipo = 'NDB' and
	   c.compro_nro = cast (p_id as character varying) and
	   c.id_punto_venta = 2 AND
	   c.alta_fecha > '23-09-2011';
	   
end if;

end;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION reporte_nota_debito_liquidacion(integer, character varying, numeric) OWNER TO postgres;