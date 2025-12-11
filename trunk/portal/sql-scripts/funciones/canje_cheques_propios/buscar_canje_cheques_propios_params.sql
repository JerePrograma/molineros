drop FUNCTION buscar_canje_cheques_propios_params(p_date_ini date, p_date_fin date, p_nro_nuevo numeric, p_nro_viejo numeric);
CREATE OR REPLACE FUNCTION buscar_canje_cheques_propios_params(p_date_ini date, p_date_fin date, p_nro_nuevo numeric, p_nro_viejo numeric, p_op_generada integer) 
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
	id_orden_pago_ospim_nueva ,
	id_movimiento,
	alta_fecha,
	alta_usr,
	modi_fecha,
	modi_usr,
	baja_fecha,
	baja_usr 
	 from canje_cheques_propios ccp
	where ($1 is null or ($1 is not null and cast(alta_fecha as date) >= $1))
	and ($2 is null or ($2 is not null and cast(alta_fecha as date) <= $2))
	and ($3 is null or ($3 is not null and exists  (select 1 from canje_cheques_propios_nuevos where canje_id = ccp.id and nro_cheque = $3)))
	and ($4 is null or ($4 is not null and exists  (select 1 from canje_cheques_propios_viejos where canje_id = ccp.id and nro_cheque = $4)))
	and ($5 is null or ($5 is not null and id_orden_pago_ospim_nueva = $5))
$BODY$;



