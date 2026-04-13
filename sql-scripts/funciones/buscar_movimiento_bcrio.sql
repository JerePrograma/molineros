DROP FUNCTION buscar_movimiento_bcrio(id_mov integer);
CREATE OR REPLACE FUNCTION buscar_movimiento_bcrio(id_mov integer) 
RETURNS TABLE(id_movimiento integer,
 fecha_movimiento date,
 id_tipo_mov integer,
 id_cuenta_bcria integer,
 deb_cred boolean,
 id_chequera integer,
 nro_compro character varying,
 fecha_comprobante date,
 importe double precision,
 descripcion character varying,
 imprime_cheque boolean,
 no_a_la_orden boolean,
 concilia_fecha timestamp without time zone,
 concilia_usr character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying)
    LANGUAGE sql
    AS $BODY$
	select 	id_movimiento,
		fecha_movimiento,
		id_tipo_mov,
		id_cuenta_bcria,
		deb_cred,
		id_chequera,
		nro_compro,
		fecha_comprobante,
		importe_movimiento,
		descripcion,
		imprime_cheque,
		no_a_la_orden,
		conciliacion_fecha,
		conciliacion_usr,
		alta_fecha,
		alta_usr
	from movimiento_banco m	     
	where id_movimiento=$1;
$BODY$;


ALTER FUNCTION public.buscar_movimiento_bcrio(id_mov integer) OWNER TO postgres;

--
