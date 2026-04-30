DROP FUNCTION buscar_movimientos_bcrios(fecha_desde date,
 fecha_hasta date,
 id_cta_bcria integer,
 descripcion character varying);
CREATE OR REPLACE FUNCTION buscar_movimientos_bcrios(fecha_desde date,
 fecha_hasta date,
 id_cta_bcria integer,
 descripcion character varying,
 id_tipo_mov integer) 
RETURNS TABLE(id_movimiento integer,
 fecha_movimiento date,
 tipo_mov character varying,
 cuenta_bcria character varying,
 fecha_comprobante date,
 nro_compro character varying,
 mov_descripcion character varying,
 importe double precision)
    LANGUAGE sql
    AS $BODY$
		select 	id_movimiento,
		cast (fecha_movimiento as date),
		tmb.descripcion tipo_mov,
		cb.descripcion cuenta_bcria,
		fecha_comprobante,
		nro_compro,
		m.descripcion,
		m.importe_movimiento
	from movimiento_banco m,
	     tipo_mov_bcrio tmb,
	     cuenta_bcria cb
	where  ($1 is null or ($1 is not null and fecha_movimiento>=$1))
		and ($2 is null or ($2 is not null and m.fecha_movimiento<=$2))
		and ($3 is null or ($3 is not null and m.id_cuenta_bcria=$3))
		and ($4 is null or ($4 is not null and m.descripcion=$4))
		and tmb.id_tipo_mov_maestro=m.id_tipo_mov
		and cast (tmb.valido_desde as date) <= cast (fecha_movimiento as date)
		and cast (tmb.valido_hasta as date) >= cast (fecha_movimiento as date)
		and cb.id_cuenta_bcria= m.id_cuenta_bcria
		and m.baja_fecha is null
		and ($5 is null or ($5 is not null and tmb.descripcion in (select descripcion from tipo_mov_bcrio where id_tipo_mov_maestro = $5)))

	union all 
	select null, cast (deb.fecha as date),  tmb.descripcion,  (select descripcion from cuenta_bcria where id_cuenta_bcria = 2 ),  cast (deb.fecha as date), null, ceb.descripcion,  deb.importe
	from detalle_extraccion_bancaria deb
	inner join codigo_ext_bcrias_afip  ceb
	on cast (deb.codigo_movimiento as integer) = ceb.codigo
	inner join tipo_mov_bcrio tmb
	on ceb.id_tipo_mov = tmb.id_tipo_mov_maestro
	and cast (tmb.valido_desde as date) <= cast (deb.fecha as date)
	and cast (tmb.valido_hasta as date) >= cast (deb.fecha as date)
	where deb.tipo = 'PRO'
	and ($1 is null or ($1 is not null and  deb.fecha>=$1))
	and ($2 is null or ($2 is not null and  deb.fecha<=$2))
	and ($3 is null or ($3 is not null and 2=$3))
	and ($4 is null or ($4 is not null and ceb.descripcion=$4))
	and ($5 is null or ($5 is not null and tmb.id_tipo_mov_maestro = $5))
$BODY$;


ALTER FUNCTION public.buscar_movimientos_bcrios(fecha_desde date, fecha_hasta date, id_cta_bcria integer, descripcion character varying, id_tipo_mov integer) OWNER TO postgres;

