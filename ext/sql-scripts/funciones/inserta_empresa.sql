CREATE OR REPLACE FUNCTION inserta_empresa(p_cuit character varying,
 p_sucu character varying,
 p_descr character varying,
 p_ramo integer,
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
 p_provinciafisc integer,
 p_localidadfisc integer,
 p_codpostalfisc character varying,
 p_callefisc character varying,
 p_numerofisc character varying,
 p_pisofisc character varying,
 p_dptofisc character varying,
 p_iva integer,
 p_entidad integer,
 p_observaciones character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN



insert into empresa (
	cuit,
  sucursal,
  razon_soc,
  nombre_fantasia,
  id_ramo_empresa,
  id_seccional,
  contacto,
  id_entidad_cam_empresa,
  observaciones,
  vigen_fecha,
  alta_fecha,
  alta_usr,
  alta_ip,
  modi_fecha,
  modi_usr,
  modi_ip,
  id_posicion_iva
)
values
(p_cuit,p_sucu,p_descr,p_descr,p_ramo,p_seccional,p_contacto,p_entidad,p_observaciones, LOCALTIMESTAMP, LOCALTIMESTAMP,p_usuario, null, LOCALTIMESTAMP, p_usuario, null, p_iva);


resultDom=inserta_domicilio('A',   p_calle,   p_piso,  p_dpto,  null,  p_codPostal,  null,  null, null, 'N',  LOCALTIMESTAMP,  p_usuario,  LOCALTIMESTAMP,  p_usuario,  p_provincia,  p_localidad,  p_numero, null, null);

insert into emp_domicilio (  cuit,
  sucursal,
  id_domicilio,
  vigen_desde,
  domi_tipo)
 values (p_cuit, p_sucu, resultDom, LOCALTIMESTAMP,'A');
 
 if (p_callefisc is not null and p_callefisc != '') then
		resultDom=inserta_domicilio('F',   p_callefisc,   p_pisofisc,  p_dptofisc,  null,  p_codPostalfisc,  null,  null, null, 'N',  LOCALTIMESTAMP,  p_usuario,  LOCALTIMESTAMP,  p_usuario,  p_provinciafisc,  p_localidadfisc,  p_numerofisc, null, null);
		insert into emp_domicilio (  cuit,
		  sucursal,
		  id_domicilio,
		  vigen_desde,
		  domi_tipo)
		 values (p_cuit, p_sucu, resultDom, LOCALTIMESTAMP,'F');
end if;

 if p_fax is not null then
	 resultDom=inserta_contacto_e('F', LOCALTIMESTAMP, p_fax, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	 insert into emp_contacto_e (cuit, sucursal, id_contacto_e, vigen_desde)
	 values (p_cuit, p_sucu, resultDom, LOCALTIMESTAMP);
 end if;

 if p_email is not null then  
	 resultDom=inserta_contacto_e('E', LOCALTIMESTAMP, p_email, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	  insert into emp_contacto_e (cuit, sucursal, id_contacto_e, vigen_desde)
	 values (p_cuit, p_sucu, resultDom, LOCALTIMESTAMP);
 end if;
  
  if p_sitio is not null then
	 resultDom=inserta_contacto_e('S', LOCALTIMESTAMP, p_sitio, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	  insert into emp_contacto_e (cuit, sucursal, id_contacto_e, vigen_desde)
	 values (p_cuit, p_sucu, resultDom, LOCALTIMESTAMP);
 end if;
   
   
  if p_telefono0Numero is not null then
  	  resultDom=inserta_telefono( 'C', LOCALTIMESTAMP,  p_telefono0Pais, p_telefono0Area, p_telefono0Numero, p_telefono0Ext, null , LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario) ;
  	  insert into emp_telefono (cuit, sucursal,  id_telefono,  vigen_desde)
	 		values (p_cuit, p_sucu, resultDom, LOCALTIMESTAMP);
  end if;
  
  if p_telefono1Numero is not null then
  	  resultDom=inserta_telefono( 'C', LOCALTIMESTAMP,  p_telefono1Pais, p_telefono1Area, p_telefono1Numero, p_telefono1Ext, null , LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario) ;
  	  insert into emp_telefono (cuit, sucursal,  id_telefono,  vigen_desde)
	 		values (p_cuit, p_sucu, resultDom, LOCALTIMESTAMP);
  end if;
  
  if p_telefono2Numero is not null then
  	  resultDom=inserta_telefono( 'C', LOCALTIMESTAMP,  p_telefono2Pais, p_telefono2Area, p_telefono2Numero, p_telefono2Ext, null , LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario) ;
  	  insert into emp_telefono (cuit, sucursal,  id_telefono,  vigen_desde)
	 		values (p_cuit, p_sucu, resultDom, LOCALTIMESTAMP);
  end if;
  
  
return 1;
END;
$BODY$;


ALTER FUNCTION public.inserta_empresa(p_cuit character varying, p_sucu character varying, p_descr character varying, p_ramo integer, p_seccional integer, p_provincia integer, p_localidad integer, p_codpostal character varying, p_calle character varying, p_numero character varying, p_piso character varying, p_dpto character varying, p_telefono0pais character varying, p_telefono0area character varying, p_telefono0numero character varying, p_telefono0ext character varying, p_telefono1pais character varying, p_telefono1area character varying, p_telefono1numero character varying, p_telefono1ext character varying, p_telefono2pais character varying, p_telefono2area character varying, p_telefono2numero character varying, p_telefono2ext character varying, p_fax character varying, p_email character varying, p_sitio character varying, p_contacto character varying, p_usuario character varying, p_provinciafisc integer, p_localidadfisc integer, p_codpostalfisc character varying, p_callefisc character varying, p_numerofisc character varying, p_pisofisc character varying, p_dptofisc character varying, p_iva integer, p_entidad integer, p_observaciones character varying) OWNER TO postgres;

--
