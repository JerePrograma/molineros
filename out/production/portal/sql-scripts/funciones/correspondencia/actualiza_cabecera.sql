CREATE OR REPLACE FUNCTION correo.actualiza_cabecera(p_lugar_recep_emision character varying, p_fecha timestamp without time zone, 
p_tipo_registro character varying, p_tipo_envio character varying, p_usuario character varying, p_d_correspondencia integer,
p_oblea character varying)
  RETURNS integer AS
$BODY$
 begin 

 UPDATE correo.cabecera_correspondencia
 	SET lugar_recep_emision = p_lugar_recep_emision, 
	    fecha = p_fecha, 
	    tipo_envio = p_tipo_envio, 
	    tipo_registro = p_tipo_registro,
	    oblea = p_oblea,
 	    modi_fecha=LOCALTIMESTAMP, 
 	    modi_usr=p_usuario	 	
 	WHERE id_correspondencia=p_d_correspondencia;

 return 1;
 end;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;