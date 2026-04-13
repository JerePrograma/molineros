CREATE OR REPLACE FUNCTION inserta_os_aporte_detalle(fecha_proceso_v date,
 hora_proceso_v character varying,
 concepto_transf_v character varying,
 importe_v double precision,
 deb_cred_v character varying,
 fecha_transf_v date,
 fecha_recauda_v date,
 cuit_contribuyente_v character varying,
 periodo_v date,
 num_oblig_v character varying,
 sec_oblic_v character varying,
 cuil_aportante_v character varying,
 banco_v character varying,
 sucur_v character varying,
 zona_v character varying,
 porc_reducc_v integer,
 porc_reducc2_v integer,
 porc_reducc3_v integer,
 grupo_fliar_v character varying,
 tipo_pago_v character varying,
 marca_apro_v character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
begin

IF concepto_transf_v='REM' THEN
	INSERT INTO os_aportes_rem(
		    fecha_proceso, hora_proceso, concepto_transf, importe, deb_cred, 
		    fecha_transf, fecha_recauda, cuit_contribuyente, periodo, num_oblig, 
		    sec_oblic, cuil_aportante, banco, sucur, zona, porc_reducc, porc_reducc2, 
		    porc_reducc3, grupo_fliar, tipo_pago, marca_apro)
	    VALUES (fecha_proceso_v, hora_proceso_v, concepto_transf_v, importe_v, deb_cred_v, 
		    fecha_transf_v, fecha_recauda_v, cuit_contribuyente_v, periodo_v, num_oblig_v, 
		    sec_oblic_v, cuil_aportante_v, banco_v, sucur_v, zona_v, porc_reducc_v, porc_reducc2_v, 
		    porc_reducc3_v, grupo_fliar_v, tipo_pago_v, marca_apro_v);
ELSE 
	IF deb_cred_v='D' THEN	
		INSERT INTO os_aportes_detalle(
			    fecha_proceso, hora_proceso, concepto_transf, importe, deb_cred, 
			    fecha_transf, fecha_recauda, cuit_contribuyente, periodo, num_oblig, 
			    sec_oblic, cuil_aportante, banco, sucur, zona, porc_reducc, porc_reducc2, 
			    porc_reducc3, grupo_fliar, tipo_pago, marca_apro)
		    VALUES (fecha_proceso_v, hora_proceso_v, concepto_transf_v, importe_v*-1, deb_cred_v, 
			    fecha_transf_v, fecha_recauda_v, cuit_contribuyente_v, periodo_v, num_oblig_v, 
			    sec_oblic_v, cuil_aportante_v, banco_v, sucur_v, zona_v, porc_reducc_v, porc_reducc2_v, 
			    porc_reducc3_v, grupo_fliar_v, tipo_pago_v, marca_apro_v);
	ELSE
		INSERT INTO os_aportes_detalle(
			    fecha_proceso, hora_proceso, concepto_transf, importe, deb_cred, 
			    fecha_transf, fecha_recauda, cuit_contribuyente, periodo, num_oblig, 
			    sec_oblic, cuil_aportante, banco, sucur, zona, porc_reducc, porc_reducc2, 
			    porc_reducc3, grupo_fliar, tipo_pago, marca_apro)
		    VALUES (fecha_proceso_v, hora_proceso_v, concepto_transf_v, importe_v, deb_cred_v, 
			    fecha_transf_v, fecha_recauda_v, cuit_contribuyente_v, periodo_v, num_oblig_v, 
			    sec_oblic_v, cuil_aportante_v, banco_v, sucur_v, zona_v, porc_reducc_v, porc_reducc2_v, 
			    porc_reducc3_v, grupo_fliar_v, tipo_pago_v, marca_apro_v);
	END IF;
END IF;
return 1;
end;
$BODY$;


ALTER FUNCTION public.inserta_os_aporte_detalle(fecha_proceso_v date, hora_proceso_v character varying, concepto_transf_v character varying, importe_v double precision, deb_cred_v character varying, fecha_transf_v date, fecha_recauda_v date, cuit_contribuyente_v character varying, periodo_v date, num_oblig_v character varying, sec_oblic_v character varying, cuil_aportante_v character varying, banco_v character varying, sucur_v character varying, zona_v character varying, porc_reducc_v integer, porc_reducc2_v integer, porc_reducc3_v integer, grupo_fliar_v character varying, tipo_pago_v character varying, marca_apro_v character varying) OWNER TO postgres;

--
