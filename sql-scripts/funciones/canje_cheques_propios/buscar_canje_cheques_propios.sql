drop  FUNCTION buscar_canje_cheques_propios(id_canje integer);
CREATE OR REPLACE FUNCTION buscar_canje_cheques_propios(id_canje integer) 
RETURNS TABLE( id integer,
	id_orden_pago_ospim integer,
	id_orden_pago_ospim_nueva integer,
	id_movimiento integer,
	alta_fecha timestamp without time zone,
	alta_usr character varying(50),
	modi_fecha timestamp without time zone,
	modi_usr character varying(50),
	baja_fecha timestamp without time zone,
	baja_usr character varying(50))
    LANGUAGE sql
    AS $BODY$
	select 	
	 id ,
	id_orden_pago_ospim ,
	id_orden_pago_ospim_nueva,
	id_movimiento,
	alta_fecha,
	alta_usr,
	modi_fecha,
	modi_usr,
	baja_fecha,
	baja_usr 
	 from canje_cheques_propios 
	where id = $1;
$BODY$;


