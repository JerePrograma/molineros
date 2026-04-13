drop FUNCTION trae_tipos_movimiento_extracto_bancario();
CREATE OR REPLACE FUNCTION trae_tipos_movimiento_extracto_bancario() 
RETURNS TABLE(codigo_movimiento integer,
descripcion_movimiento character varying,
id_tipo_mov integer,
descripcion_tipo_mov character varying)
    LANGUAGE sql
    AS $BODY$
    
select  codigo , cod.descripcion, tmb.id_tipo_mov_maestro, tmb.descripcion
	from codigo_ext_bcrias_afip cod
	inner join tipo_mov_bcrio tmb
	on cod.id_tipo_mov = tmb.id_tipo_mov_maestro
	and valido_desde <= current_date
	and valido_hasta >= current_date
	order by cod.descripcion asc;

$BODY$;


ALTER FUNCTION public.trae_tipos_movimiento_extracto_bancario() OWNER TO postgres;

--
