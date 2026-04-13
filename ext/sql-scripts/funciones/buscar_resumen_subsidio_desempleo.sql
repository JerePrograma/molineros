drop function buscar_resumen_subsidio_desempleo(p_fecha_ini date, p_fecha_fin date);

CREATE OR REPLACE FUNCTION buscar_resumen_subsidio_desempleo(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(fecha date, total numeric, descripcion character varying, orden integer) AS
$BODY$

select * from (
	select cast(fecha_proceso as date) , sum(subsidio ) as importe,cast( 'Acreditación mensual subsidio' as character varying) as descripcion,1 
	from detalle_subsidio_os 
	group by fecha_proceso
	having cast(fecha_proceso as date) >= (cast ($1 as date)) and cast(fecha_proceso as date) < (cast($2 as date) + interval '1 day')

	union all

	select cast(fecha_movimiento as date)  as fecha_proceso, 
		sum(cast (importe_movimiento as numeric)) as importe,
		 tmb.descripcion , 2
	from  movimiento_banco mb
	inner join tipo_mov_bcrio tmb
	on mb.id_tipo_mov= tmb.id_tipo_mov_maestro
	and cast(tmb.valido_desde as date) <=  cast(fecha_movimiento as date)
	and cast(tmb.valido_hasta as date) >=  cast(fecha_movimiento as date)
	where tmb.id_tipo_mov_maestro = (select id from tipo_mov_bcrio_maestro where descripcion_original = 'ACREDITACION MENSUAL ANSES DESEMPLEO')
		and cast(fecha_movimiento as date) >= (cast($1 as date)) 
		and cast(fecha_movimiento as date) < (cast($2 as date) + interval '1 day')
	group by cast(fecha_movimiento as date), tmb.descripcion
) asd
order by fecha_proceso;

$BODY$
  LANGUAGE 'sql' VOLATILE