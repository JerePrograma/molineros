CREATE OR REPLACE FUNCTION inserta_prestador(p_cuit character varying,
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
declare resultDom integer;
BEGIN

INSERT INTO prestador(
            cuit, id_tipo_prestador, tipo_matricula, nro_matricula, 
            id_mat_provincia, id_mat_categoria, contacto, id_seccional, observaciones, 
            rein_liqui, id_condicion_de_iva, cheque_a_nombre_de, alta_fecha, 
            alta_usr, modi_fecha, modi_usr, descripcion)
    VALUES (p_cuit, p_tipo_prestador, p_matricula_tipo, p_matricula_nro, p_matricula_provincia, 
            p_matricula_categoria, p_contacto, p_seccional, 
            p_observaciones, 3, p_iva, 
            p_cheque_a_nombre_de, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario, p_descr);


resultDom=inserta_domicilio('A',   p_calle,   p_piso,  p_dpto,  null,  p_codPostal,  null,  null, null, 'N',  LOCALTIMESTAMP,  p_usuario,  LOCALTIMESTAMP,  p_usuario,  p_provincia,  p_localidad,  p_numero, null, null);

INSERT INTO prestad_lugar_atencion(
            id_prestador, id_domicilio, vigen_desde)
 values (currval('prestador_id_seq'), resultDom, LOCALTIMESTAMP);
 
 if p_fax is not null then
	 resultDom=inserta_contacto_e('F', LOCALTIMESTAMP, p_fax, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	 insert into prestad_contacto_e (id_prestador, id_contacto_e, vigen_desde)
	 values (currval('prestador_id_seq'), resultDom, LOCALTIMESTAMP);
 end if;

 if p_email is not null then  
	 resultDom=inserta_contacto_e('E', LOCALTIMESTAMP, p_email, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	  insert into prestad_contacto_e ( id_prestador, id_contacto_e, vigen_desde)
	 values (currval('prestador_id_seq'), resultDom, LOCALTIMESTAMP);
 end if;
  
  if p_sitio is not null then
	 resultDom=inserta_contacto_e('S', LOCALTIMESTAMP, p_sitio, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	  insert into prestad_contacto_e (id_prestador , id_contacto_e, vigen_desde)
	 values (currval('prestador_id_seq'), resultDom, LOCALTIMESTAMP);
 end if;
   
   
  if p_telefono0Numero is not null then
  	  resultDom=inserta_telefono( 'C', LOCALTIMESTAMP,  p_telefono0Pais, p_telefono0Area, p_telefono0Numero, p_telefono0Ext, null , LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario) ;
  	  insert into prestad_telefono (id_prestador, id_telefono,  vigen_desde)
	 		values (currval('prestador_id_seq'), resultDom, LOCALTIMESTAMP);
  end if;
  
  if p_telefono1Numero is not null then
  	  resultDom=inserta_telefono( 'C', LOCALTIMESTAMP,  p_telefono1Pais, p_telefono1Area, p_telefono1Numero, p_telefono1Ext, null , LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario) ;
  	  insert into prestad_telefono (id_prestador,  id_telefono,  vigen_desde)
	 		values (currval('prestador_id_seq'), resultDom, LOCALTIMESTAMP);
  end if;
  
  if p_telefono2Numero is not null then
  	  resultDom=inserta_telefono( 'C', LOCALTIMESTAMP,  p_telefono2Pais, p_telefono2Area, p_telefono2Numero, p_telefono2Ext, null , LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario) ;
  	  insert into prestad_telefono (id_prestador,  id_telefono,  vigen_desde)
	 		values (currval('prestador_id_seq'), resultDom, LOCALTIMESTAMP);
  end if;
  
  
return currval('prestador_id_seq');
END;
$BODY$;


ALTER FUNCTION public.inserta_prestador(p_cuit character varying, p_descr character varying, p_seccional integer, p_provincia integer, p_localidad integer, p_codpostal character varying, p_calle character varying, p_numero character varying, p_piso character varying, p_dpto character varying, p_telefono0pais character varying, p_telefono0area character varying, p_telefono0numero character varying, p_telefono0ext character varying, p_telefono1pais character varying, p_telefono1area character varying, p_telefono1numero character varying, p_telefono1ext character varying, p_telefono2pais character varying, p_telefono2area character varying, p_telefono2numero character varying, p_telefono2ext character varying, p_fax character varying, p_email character varying, p_sitio character varying, p_contacto character varying, p_usuario character varying, p_iva integer, p_tipo_prestador integer, p_matricula_tipo character, p_matricula_nro integer, p_matricula_provincia integer, p_matricula_categoria character, p_observaciones character varying) OWNER TO postgres;

--
