CREATE OR REPLACE FUNCTION actualiza_prestador(p_cuit character varying,
 p_descr character varying,
 p_seccional integer,
 p_provincia integer,
 p_localidad integer,
 p_codpostal character varying,
 p_calle character varying,
 p_numero character varying,
 p_piso character varying,
 p_dpto character varying,
 p_telefono0pais character varying,
 p_telefono0area character varying,
 p_telefono0numero character varying,
 p_telefono0ext character varying,
 p_telefono1pais character varying,
 p_telefono1area character varying,
 p_telefono1numero character varying,
 p_telefono1ext character varying,
 p_telefono2pais character varying,
 p_telefono2area character varying,
 p_telefono2numero character varying,
 p_telefono2ext character varying,
 p_fax character varying,
 p_email character varying,
 p_sitio character varying,
 p_contacto character varying,
 p_usuario character varying,
 p_id_prestador integer,
 p_id_domicilio integer,
 p_telefono0_id integer,
 p_telefono1_id integer,
 p_telefono2_id integer,
 p_fax_id integer,
 p_email_id integer,
 p_sitio_id integer,
 p_iva integer,
 p_tipo_prestador integer,
 p_matricula_tipo character,
 p_matricula_nro integer,
 p_matricula_provincia integer,
 p_matricula_categoria character,
 p_observaciones character varying,
 p_cheque_a_nombre_de character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
BEGIN

--PRESTADOR
update prestador set
            id_tipo_prestador = p_tipo_prestador,
            tipo_matricula = p_matricula_tipo, 
            nro_matricula = p_matricula_nro, 
            id_mat_provincia = p_matricula_provincia, 
            id_mat_categoria = p_matricula_categoria, 
            contacto = p_contacto, 
            id_seccional = p_seccional, 
            observaciones = p_observaciones, 
            rein_liqui = 3, 
            id_condicion_de_iva = p_iva, 
            cheque_a_nombre_de = p_cheque_a_nombre_de, 
						modi_fecha = LOCALTIMESTAMP, 
						modi_usr = p_usuario, 
						descripcion = p_descr
		where id_prestador= p_id_prestador;
		
--DOMICILIO
update prestad_lugar_atencion
set baja_fecha = LOCALTIMESTAMP
where id_domicilio = p_id_domicilio;

res=actualiza_domicilio(p_id_domicilio, 'A',   p_calle,   p_piso,  p_dpto,  null,  p_codPostal,  null,  null, null, 'N',  LOCALTIMESTAMP,  p_usuario,  p_provincia,  p_localidad,  p_numero);
insert into prestad_lugar_atencion ( id_prestador,  id_domicilio,  vigen_desde)
 values (p_id_prestador, res, LOCALTIMESTAMP);

 	
 --CONTACTO ELECTRONICO
if p_fax_id is not null then
	res=actualiza_contacto_e('F', p_fax, null, LOCALTIMESTAMP, p_usuario, p_fax_id);
end if;

if p_email_id is not null then
	res=actualiza_contacto_e('E', p_email, null, LOCALTIMESTAMP, p_usuario, p_email_id);
end if;

if p_sitio_id is not null then
	res=actualiza_contacto_e('S', p_sitio, null, LOCALTIMESTAMP, p_usuario, p_sitio_id);
end if;  

--TELEFONOS
 if p_telefono0_id is not null then
  	  res=actualiza_telefono( 'C',  p_telefono0Pais, p_telefono0Area, p_telefono0Numero, p_telefono0Ext, null , LOCALTIMESTAMP, p_usuario, p_telefono0_id) ;
  end if;
  
  if p_telefono1_id is not null then
  	  res=actualiza_telefono( 'C',  p_telefono1Pais, p_telefono1Area, p_telefono1Numero, p_telefono1Ext, null , LOCALTIMESTAMP, p_usuario,p_telefono1_id) ;
  end if;
  
  if p_telefono2_id is not null then
  	  res=actualiza_telefono( 'C',  p_telefono2Pais, p_telefono2Area, p_telefono2Numero, p_telefono2Ext, null , LOCALTIMESTAMP, p_usuario, p_telefono2_id) ;
  end if;
  
return 1;
END;
$BODY$;


ALTER FUNCTION public.actualiza_prestador(p_cuit character varying, p_descr character varying, p_seccional integer, p_provincia integer, p_localidad integer, p_codpostal character varying, p_calle character varying, p_numero character varying, p_piso character varying, p_dpto character varying, p_telefono0pais character varying, p_telefono0area character varying, p_telefono0numero character varying, p_telefono0ext character varying, p_telefono1pais character varying, p_telefono1area character varying, p_telefono1numero character varying, p_telefono1ext character varying, p_telefono2pais character varying, p_telefono2area character varying, p_telefono2numero character varying, p_telefono2ext character varying, p_fax character varying, p_email character varying, p_sitio character varying, p_contacto character varying, p_usuario character varying, p_id_prestador integer, p_id_domicilio integer, p_telefono0_id integer, p_telefono1_id integer, p_telefono2_id integer, p_fax_id integer, p_email_id integer, p_sitio_id integer, p_iva integer, p_tipo_prestador integer, p_matricula_tipo character, p_matricula_nro integer, p_matricula_provincia integer, p_matricula_categoria character, p_observaciones character varying) OWNER TO postgres;

--
