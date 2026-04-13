-- Function: insertar_opcionsss_viejo(character varying, character varying, integer, integer, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, date, date, character varying, character varying, date, date, integer, character varying, character varying, boolean)

-- DROP FUNCTION insertar_opcionsss_viejo(character varying, character varying, integer, integer, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, date, date, character varying, character varying, date, date, integer, character varying, character varying, boolean);

CREATE OR REPLACE FUNCTION insertar_opcionsss_viejo(tipo_exportacion_p character varying, delegacion_p character varying, libro_p integer, tomo_p integer, nro_formulario_p integer, 
os_elegida_p integer, regimen_p character varying, cuil_p character varying, apellido_p character varying, sexo_p character varying, 
calle_p character varying, numero_p character varying, piso_p integer, departamento_p character varying, localidad_p character varying,
telefono_particular_p character varying, telefono_laboral_p character varying, telefono_celular_p character varying, email_p character varying, 
os_anterior_p integer, cuit_p character varying, unifica_apo_p character varying, fecha_elecc_p date, fecha_certi_p date, cuil_conyuge_p character varying,
ape_nom_conyuge_p character varying, fecha_baja_p date, fecha_entrega_p date, numero_lote_p integer, version_sistema_p character varying, postal_codi_p character varying,
okdesdesss_p boolean)
  RETURNS void AS
$BODY$
declare result integer;
BEGIN
  --el select debe retornar vacio porque si viene el cuil, esta repetido
  result = 1 from afi_opciones_sss a
		where a.cuil = $8;
  if result is null then

	INSERT INTO afi_opciones_sss(tipo_exportacion, delegacion, libro, tomo, nro_formulario, os_elegida,
	   regimen, cuil, apellido, sexo, calle, numero, piso, departamento, localidad, telefono_particular, telefono_laboral,
	   telefono_celular,email, os_anterior, cuit, unifica_apo, fecha_elecc, fecha_certi, cuil_conyuge, ape_nom_conyuge,
	   fecha_baja, fecha_entrega, numero_lote, version_sistema, postal_codi, okdesdesss) 
	VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20, $21, $22, $23, 
		$24, $25, $26, $27, $28, $29, $30, $31, $32);
	
  end if;

--return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
  
ALTER FUNCTION insertar_opcionsss_viejo(character varying, character varying, integer, integer, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, date, date, character varying, character varying, date, date, integer, character varying, character varying, boolean)
  OWNER TO postgres;
