CREATE OR REPLACE FUNCTION inserta_os_aporte_footer_fn(fecha_proceso_v date,
 hora_proceso_v character varying,
 secuencia_reg_v character varying,
 cant_trf_nom_v integer,
 importe_nom_v double precision,
 deb_cred_v character varying,
 cant_trf_fdo_v integer,
 importe_fdo_res_v double precision,
 deb_cred2_v character varying,
 cant_trf_ant_v integer,
 importe_ant_v double precision,
 deb_cred3_v character,
 saldo_ant_sin_nominar_v double precision) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
begin
INSERT INTO os_aportes_footer_fn(fecha_proceso, hora_proceso, secuencia_reg, cant_trf_nom, importe_nom, deb_cred, cant_trf_fdo, importe_fdo_res,
	deb_cred2, cant_trf_ant, importe_ant, deb_cred3, saldo_ant_sin_nominar)
    VALUES (fecha_proceso_v, hora_proceso_v, secuencia_reg_v, cant_trf_nom_v, importe_nom_v, deb_cred_v, cant_trf_fdo_v, importe_fdo_res_v,
	deb_cred2_v, cant_trf_ant_v, importe_ant_v, deb_cred3_v, saldo_ant_sin_nominar_v);
return 1;
end;
$BODY$;


ALTER FUNCTION public.inserta_os_aporte_footer_fn(fecha_proceso_v date, hora_proceso_v character varying, secuencia_reg_v character varying, cant_trf_nom_v integer, importe_nom_v double precision, deb_cred_v character varying, cant_trf_fdo_v integer, importe_fdo_res_v double precision, deb_cred2_v character varying, cant_trf_ant_v integer, importe_ant_v double precision, deb_cred3_v character, saldo_ant_sin_nominar_v double precision) OWNER TO postgres;

--
