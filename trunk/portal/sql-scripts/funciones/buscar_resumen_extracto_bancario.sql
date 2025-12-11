drop function buscar_resumen_extracto_bancario(p_fecha_ini date, p_fecha_fin date, p_cta_bcria integer);
CREATE OR REPLACE FUNCTION buscar_resumen_extracto_bancario(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(fecha date, codigo_movimiento integer, descripcion_movimiento character varying, id_tipo_mov integer, descripcion_tipo_mov character varying, total numeric) AS
$BODY$

	select cast(fecha as date), cod.codigo, cod.descripcion, tmb.id_tipo_mov_maestro, tmb.descripcion, sum(importe) from detalle_extraccion_bancaria    deb
	inner join codigo_ext_bcrias_afip cod
	on cast (deb.codigo_movimiento as integer) =  cod.codigo
	inner join tipo_mov_bcrio tmb
	on cod.id_tipo_mov = tmb.id_tipo_mov_maestro
	and cast(tmb.valido_desde as date) <= cast(fecha as date)
	and cast(tmb.valido_hasta as date) >=  cast(fecha as date)
	where tipo = 'PRO'
	group by fecha, cod.codigo, cod.descripcion, tmb.id_tipo_mov_maestro, tmb.descripcion
	having  fecha >= (cast($1 as date)) and fecha < (cast($2 as date) + interval '1 day')
	order by fecha asc;

$BODY$
  LANGUAGE 'sql' VOLATILE
