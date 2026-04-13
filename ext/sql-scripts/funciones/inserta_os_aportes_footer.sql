CREATE OR REPLACE FUNCTION inserta_os_aportes_footer(fecha_proceso_v date,
 hora_proceso_v character varying,
 cant_reg_v integer,
 importe_total_v double precision,
 deb_cred_v character varying,
 cant_reg_tn_v integer,
 cant_trf_nom_v integer,
 importe_trf_nom_v double precision,
 deb_cred2_v character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
begin

INSERT INTO os_aportes_footer(
            fecha_proceso, hora_proceso, cant_reg, importe_total, deb_cred, 
            cant_reg_tn, cant_trf_nom, importe_trf_nom, deb_cred2)
    VALUES (fecha_proceso_v, hora_proceso_v, cant_reg_v, importe_total_v, deb_cred_v, 
            cant_reg_tn_v, cant_trf_nom_v, importe_trf_nom_v, deb_cred2_v);
return 1;
end;
$BODY$;


ALTER FUNCTION public.inserta_os_aportes_footer(fecha_proceso_v date, hora_proceso_v character varying, cant_reg_v integer, importe_total_v double precision, deb_cred_v character varying, cant_reg_tn_v integer, cant_trf_nom_v integer, importe_trf_nom_v double precision, deb_cred2_v character varying) OWNER TO postgres;

--
