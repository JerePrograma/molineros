drop function buscar_resumen_os_aportes_detalle(p_fecha_ini date, p_fecha_fin date);

CREATE OR REPLACE FUNCTION buscar_resumen_os_aportes_detalle(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(fecha date, total numeric, descripcion character varying, orden integer) AS
$BODY$

select * from (
	select cast (fecha as date), sum(importe), descripcion, orden from
	(select fecha_transf as fecha, sum(importe) as importe, 'Distribucion normal' as descripcion, 1 as orden from os_aportes_detalle 
	where fecha_transf >= (cast($1 as date)) and fecha_transf < (cast($2  as date) + interval '1 day')
	and concepto_transf <> 'AN' and concepto_transf <> 'COM'
	and sucur not like 'AN_%'
	group by fecha_transf
	union select cast(fecha as date), sum(importe),'Distribucion normal' as descripcion, 1 as orden 
	from detalle_extraccion_bancaria where codigo_movimiento='13' and tipo='PRO' and fecha>= (cast($1 as date)) and fecha< (cast($2  as date) + interval '1 day')
	group by fecha
	) a group by fecha, descripcion, orden

	union all
	
	select fecha_transf as fecha, sum(importe) as importe, 'Comisiones' as descripcion, 2 as orden from os_aportes_detalle 
	where fecha_transf >= (cast($1 as date)) and fecha_transf < (cast($2  as date) + interval '1 day')
	and concepto_transf = 'COM'
	group by fecha_transf

	union all

	select fecha_transf as fecha, -1*sum(importe) as importe, 'Dto Anticipos' as descripcion, 4 as orden from os_aportes_detalle 
	where fecha_transf >= (cast($1 as date)) and fecha_transf < (cast($2  as date) + interval '1 day')
	and sucur like 'NO_%'
	group by fecha_transf

	union all

	select fecha_transf as fecha, sum(importe) as importe, 'Pago Anticipos' as descripcion, 5 as orden from os_aportes_detalle 
	where fecha_transf >= (cast($1 as date)) and fecha_transf < (cast($2  as date) + interval '1 day')
	and sucur like 'AN_%'
	group by fecha_transf

	union all
	
	select cast(fecha as date) as fecha, sum(importe) as importe, cod.descripcion as descripcion, 3 as orden  from detalle_extraccion_bancaria    deb
	inner join codigo_ext_bcrias_afip cod 
	on cast (deb.codigo_movimiento as integer) =  cod.codigo
	inner join tipo_mov_bcrio tmb
	on cod.id_tipo_mov = tmb.id_tipo_mov_maestro
	and cast(tmb.valido_desde as date) <=  cast(fecha as date)
	and cast(tmb.valido_hasta as date) >=  cast(fecha as date)
	where tipo = 'PRO'
	and cod.codigo = 6
	group by fecha, cod.codigo, cod.descripcion, tmb.id_tipo_mov_maestro, tmb.descripcion
	having  fecha >= (cast($1 as date)) and fecha < (cast($2  as date) + interval '1 day')

) asd
order by fecha;

$BODY$
  LANGUAGE sql VOLATILE

