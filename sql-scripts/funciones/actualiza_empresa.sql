CREATE OR REPLACE FUNCTION actualiza_empresa(p_cuit character varying, p_sucu character varying, p_descr character varying, p_ramo integer, p_seccional integer, p_provincia integer, p_localidad integer, p_codpostal character varying, p_calle character varying, p_numero character varying, p_piso character varying, p_dpto character varying, p_telefono0pais character varying, p_telefono0area character varying, p_telefono0numero character varying, p_telefono0ext character varying, p_telefono1pais character varying, p_telefono1area character varying, p_telefono1numero character varying, p_telefono1ext character varying, p_telefono2pais character varying, p_telefono2area character varying, p_telefono2numero character varying, p_telefono2ext character varying, p_fax character varying, p_email character varying, p_sitio character varying, p_contacto character varying, p_usuario character varying, p_id_domicilio integer, p_telefono0_id integer, p_telefono1_id integer, p_telefono2_id integer, p_fax_id integer, p_email_id integer, p_sitio_id integer, p_provinciafisc integer, p_localidadfisc integer, p_codpostalfisc character varying, p_callefisc character varying, p_numerofisc character varying, p_pisofisc character varying, p_dptofisc character varying, p_id_domiciliofisc integer, p_iva integer, p_entidad integer, p_observaciones character varying)
  RETURNS integer AS
$BODY$
declare res integer;
declare resultT int;
BEGIN

--EMPRESA
update empresa set 
  razon_soc = p_descr,
  nombre_fantasia = p_descr,
  id_ramo_empresa = p_ramo,
  id_seccional = p_seccional,
  contacto = p_contacto,
  id_entidad_cam_empresa = p_entidad,
  observaciones = p_observaciones,
  modi_fecha = LOCALTIMESTAMP,
  modi_usr = p_usuario,
  modi_ip = null,
  id_posicion_iva = p_iva
where cuit = p_cuit and sucursal = p_sucu;


--DOMICILIO
update emp_domicilio
set baja_fecha = LOCALTIMESTAMP
where id_domicilio = p_id_domicilio;

res=actualiza_domicilio(p_id_domicilio, 'A',   p_calle,   p_piso,  p_dpto,  null,  p_codPostal,  null,  null, null, 'N',  LOCALTIMESTAMP,  p_usuario,  p_provincia,  p_localidad,  p_numero);
insert into emp_domicilio (  cuit,   sucursal,  id_domicilio,  vigen_desde, domi_tipo)
 values (p_cuit, p_sucu, res, LOCALTIMESTAMP, 'A');

 	
 	--DOMI FISCAL
 	update emp_domicilio
	set baja_fecha = LOCALTIMESTAMP
	where id_domicilio = p_id_domiciliofisc;
	
 if (p_callefisc is not null and p_callefisc != '') then
	res=actualiza_domicilio(p_id_domiciliofisc, 'F',  p_callefisc,   p_pisofisc,  p_dptofisc,  null,  p_codPostalfisc, null,  null, null, 'N',  LOCALTIMESTAMP,  p_usuario,   p_provinciafisc,  p_localidadfisc,  p_numerofisc);
	insert into emp_domicilio (  cuit,   sucursal,  id_domicilio,  vigen_desde, domi_tipo)
	 values (p_cuit, p_sucu, res, LOCALTIMESTAMP, 'F');
else

	res=borra_domicilio (p_id_domiciliofisc,  LOCALTIMESTAMP, p_usuario);
end if;


 --CONTACTO ELECTRONICO
if p_fax_id is not null then
	res=actualiza_contacto_e('F', p_fax, null, LOCALTIMESTAMP, p_usuario, p_fax_id);
end if;
if (p_fax_id is null or p_fax_id=0) and p_fax_id is not null then	
	resultT= inserta_contacto_e('F', CURRENT_DATE, p_fax, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	INSERT INTO emp_contacto_e(cuit, sucursal, id_contacto_e, vigen_desde)
	VALUES (p_cuit, p_sucu, resultT, LOCALTIMESTAMP);	
end if;

if p_email_id is not null then
	res=actualiza_contacto_e('E', p_email, null, LOCALTIMESTAMP, p_usuario, p_email_id);
end if;

if (p_email_id is null or p_email_id=0) and p_email_id is not null then	
	resultT= inserta_contacto_e('E', CURRENT_DATE, p_email, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	INSERT INTO emp_contacto_e(cuit, sucursal, id_contacto_e, vigen_desde)
	VALUES (p_cuit, p_sucu, resultT, LOCALTIMESTAMP);	
end if;


if p_sitio_id is not null then
	res=actualiza_contacto_e('S', p_sitio, null, LOCALTIMESTAMP, p_usuario, p_sitio_id);
end if;  

if (p_sitio_id is null or p_sitio_id=0) and p_sitio_id is not null then	
	resultT= inserta_contacto_e('S', CURRENT_DATE, p_sitio, null, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);
	INSERT INTO emp_contacto_e(cuit, sucursal, id_contacto_e, vigen_desde)
	VALUES (p_cuit, p_sucu, resultT, LOCALTIMESTAMP);	
end if;

--TELEFONOS
  if p_telefono0_id is not null and p_telefono0_id<>0 then
  	  res=actualiza_telefono( 'C',  p_telefono0Pais, p_telefono0Area, p_telefono0Numero, p_telefono0Ext, null , LOCALTIMESTAMP, p_usuario, p_telefono0_id) ;
  end if;
  
  if (p_telefono0_id is null or p_telefono0_id=0) and p_telefono0Numero is not null then	
	resultT= inserta_telefono('C', current_date, p_telefono0Pais, p_telefono0Area, p_telefono0Numero, p_telefono0Ext, null, LOCALTIMESTAMP, p_usuario,
		LOCALTIMESTAMP,  p_usuario);
	insert into emp_telefono (cuit, sucursal,  id_telefono,  vigen_desde)
	 		values (p_cuit, p_sucu, resultT, LOCALTIMESTAMP);	
  end if;
  
  if p_telefono1_id is not null then
  	  res=actualiza_telefono( 'C',  p_telefono1Pais, p_telefono1Area, p_telefono1Numero, p_telefono1Ext, null , LOCALTIMESTAMP, p_usuario,p_telefono1_id) ;
  end if;

  if (p_telefono1_id is null or p_telefono1_id=0) and p_telefono1Numero is not null then	
	resultT= inserta_telefono('C', current_date, p_telefono1Pais, p_telefono1Area, p_telefono1Numero, p_telefono1Ext, null, LOCALTIMESTAMP, p_usuario,
		LOCALTIMESTAMP,  p_usuario);
	insert into emp_telefono (cuit, sucursal,  id_telefono,  vigen_desde)
	 		values (p_cuit, p_sucu, resultT, LOCALTIMESTAMP);	
  end if;
  
  if p_telefono2_id is not null then
  	  res=actualiza_telefono( 'C',  p_telefono2Pais, p_telefono2Area, p_telefono2Numero, p_telefono2Ext, null , LOCALTIMESTAMP, p_usuario, p_telefono2_id) ;
  end if;
  if (p_telefono2_id is null or p_telefono2_id=0) and p_telefono2Numero is not null then	
	resultT= inserta_telefono('C', current_date, p_telefono2Pais, p_telefono2Area, p_telefono2Numero, p_telefono2Ext, null, LOCALTIMESTAMP, p_usuario,
		LOCALTIMESTAMP,  p_usuario);
	insert into emp_telefono (cuit, sucursal,  id_telefono,  vigen_desde)
	 		values (p_cuit, p_sucu, resultT, LOCALTIMESTAMP);	
  end if;
  
  
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

