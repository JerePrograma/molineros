CREATE TYPE emple_domi_result AS
   (emp__cuit character varying,
    emp__sucursal character varying,
    emp__razon_soc character varying,
    emp__nombre_fantasia character varying,
    emp__id_ramo_empresa smallint,
    emp__id_seccional integer,
    emp__contacto character varying,
    emp__id_entidad_cam_empresa smallint,
    emp__observaciones character varying,
    emp__vigen_fecha timestamp without time zone,
    emp__motivo_baja character varying,
    emp__alta_fecha timestamp without time zone,
    emp__alta_usr character varying,
    emp__alta_ip character varying,
    emp__modi_fecha timestamp without time zone,
    emp__modi_usr character varying,
    emp__modi_ip character varying,
    emp__baja_fecha timestamp without time zone,
    emp__baja_usr character varying,
    emp__baja_ip character varying,
    emp__id_posicion_iva smallint,
    dom__id_domicilio integer,
    dom__domi_tipo character varying,
    dom__calle character varying,
    dom__piso character varying,
    dom__depto character varying,
    dom__oficina character varying,
    dom__postal_codi character varying,
    dom__barrio character varying,
    dom__cod_area_telefono character varying,
    dom__telefono character varying,
    dom__cod_area_celular character varying,
    dom__celular character varying,
    dom__observaciones character varying,
    dom__domi_val character varying,
    dom__alta_fecha timestamp without time zone,
    dom__alta_usr character varying,
    dom__modi_fecha timestamp without time zone,
    dom__modi_usr character varying,
    dom__baja_fecha timestamp without time zone,
    dom__baja_usr character varying,
    dom__provincia integer,
    dom__localidad integer,
    dom__numero character varying,
    dom__localidad_nombre character varying,
    dom__provincia_nombre character varying,
    domfisc__id_domicilio integer,
    domfisc__domi_tipo character varying,
    domfisc__calle character varying,
    domfisc__piso character varying,
    domfisc__depto character varying,
    domfisc__oficina character varying,
    domfisc__postal_codi character varying,
    domfisc__barrio character varying,
    domfisc__cod_area_telefono character varying,
    domfisc__telefono character varying,
    domfisc__cod_area_celular character varying,
    domfisc__celular character varying,
    domfisc__observaciones character varying,
    domfisc__domi_val character varying,
    domfisc__alta_fecha timestamp without time zone,
    domfisc__alta_usr character varying,
    domfisc__modi_fecha timestamp without time zone,
    domfisc__modi_usr character varying,
    domfisc__baja_fecha timestamp without time zone,
    domfisc__baja_usr character varying,
    domfisc__provincia integer,
    domfisc__localidad integer,
    domfisc__numero character varying,
    domfisc__localidad_nombre character(50),
    domfisc__provincia_nombre character(50),
    emp__domiafip text,
    emp__domiremo character varying,
    emp__domiestudio character varying,
    emp__molinera boolean,
    ubicacion_carpeta character varying,
    carta_doc character varying);
ALTER TYPE emple_domi_result
  OWNER TO postgres;

  
-- Function: buscar_empleador_y_domi(character, character varying)

-- DROP FUNCTION buscar_empleador_y_domi(character, character varying);

CREATE OR REPLACE FUNCTION buscar_empleador_y_domi(cuit_p character, sucur_p character varying)
  RETURNS SETOF emple_domi_result AS
$BODY$
BEGIN
drop table if exists aux;
create temp table aux as
	select 	
	emp.cuit,
	emp.sucursal,
	emp.razon_soc,
	emp.nombre_fantasia,
	emp.id_ramo_empresa,
	emp.id_seccional,
	emp.contacto,
	emp.id_entidad_cam_empresa,
	emp.observaciones as observaciones_es,
	emp.vigen_fecha,
	emp.motivo_baja,
	emp.alta_fecha as alta_fecha_em,
	emp.alta_usr as alta_usr_em,
	emp.alta_ip as alta_ip_em,
	emp.modi_fecha as modi_fecha_em ,
	emp.modi_usr as modi_usr_em,
	emp.modi_ip as modi_ip_em,
	emp.baja_fecha as baja_fecha_em,
	emp.baja_usr as baja_usr_em,
	emp.baja_ip as baja_ip_em,
	emp.id_posicion_iva,
	dom.id_domicilio as id_domicilio_dom,
	dom.domi_tipo as domi_tipo_dom,
	dom.calle as calle_dom,
	dom.piso as piso_dom,
	dom.depto as depto_dom, 
	dom.oficina as oficina_dom,
	dom.postal_codi as postal_codi_dom,
	dom.barrio as barrio_dom,
	cast(null as varchar) as cod_area_telefono_dom,
	dom.telefono as telefono_dom,
	cast(null as varchar) as cod_area_celular_dom,
	cast(null as varchar) as celular_dom, 
	dom.observaciones as observaciones_dom,
	dom.domi_val as domi_val_dom ,
	dom.alta_fecha as alta_fecha_dom, 
	dom.alta_usr as alta_usr_dom,
	dom.modi_fecha as modi_fecha_dom, 
	dom.modi_usr as modi_usr_dom,
	dom.baja_fecha as baja_fecha_dom, 
	dom.baja_usr as baja_usr_dom,
	dom.provincia as provincia_dom ,
	dom.localidad as localidad_dom,
	dom.numero as numero_dom ,
	loca.detalle as localidad,	
	pro.detalle as provincia,
	domfisc.id_domicilio as id_domicilio_fis,
	domfisc.domi_tipo as domi_tipo_fis,
	domfisc.calle,
	domfisc.piso,
	domfisc.depto, 
	domfisc.oficina,
	domfisc.postal_codi,
	domfisc.barrio ,
	cast(null as varchar) as cod_area_telefono_fis,
	domfisc.telefono,
	cast(null as varchar) as cod_area_celular_fis,
	cast(null as varchar) as celular_fis, 
	domfisc.observaciones,
	domfisc.domi_val ,
	domfisc.alta_fecha as alta_fecha_fis, 
	domfisc.alta_usr as alta_usr_fis ,
	domfisc.modi_fecha as modi_fecha_fis, 
	domfisc.modi_usr as modi_usr_fis,
	domfisc.baja_fecha as baja_fecha_fis, 
	domfisc.baja_usr as baja_usr_fis,
	domfisc.provincia as provincia_fis ,
	domfisc.localidad as localidad_fis ,
	domfisc.numero ,
	domfisc.localidad_nombre,
	domfisc.provincia_nombre,
	dp.calle||' '||dp.numero||', '||dp.localidad||', '||pv2.detalle||'. CP.:'||dp.codigopostal as domi_afip,
	dom.domicilio_remo,
	dom.domicilio_estudio,
	r.molinera,
	cast(null as varchar) as ubicacion_carpeta,
	cast(null as varchar) as carta_doc	
	from empresa emp
	left outer join ramo_empresa r
	on r.id_ramo_empresa=emp.id_ramo_empresa
	left outer join emp_domicilio empdom
	on emp.cuit = empdom.cuit
	and emp.sucursal = empdom.sucursal
	and empdom.domi_tipo = 'A'
	and empdom.baja_fecha is null
	and empdom.vigen_desde = (select max(vigen_desde) from emp_domicilio ed where ed.cuit = empdom.cuit and sucursal = empdom.sucursal and domi_tipo = 'A' and baja_fecha is null )
	left outer join domicilio dom
	on empdom.id_domicilio = dom.id_domicilio
	left outer join provincia pro
	on dom.provincia=pro.id_provincia
	left outer join localidad loca
	on dom.localidad=loca.id_localidad
	left outer join emp_domicilio empdomf
	on emp.cuit = empdomf.cuit
	and emp.sucursal = empdomf.sucursal
	and empdomf.domi_tipo = 'F'
	and empdomf.baja_fecha is null
	and empdomf.vigen_desde = (select max(vigen_desde) from emp_domicilio ed2 where ed2.cuit = empdomf.cuit and ed2.sucursal = empdomf.sucursal and ed2.domi_tipo = 'F' and ed2.baja_fecha is null )
	left outer join domicilio domfisc
	on empdomf.id_domicilio = domfisc.id_domicilio
	left outer join detalle_padron_contribuyentes dp
	on dp.cuit =cast($1 as numeric)
	and dp.fecha_proceso=(select max(fecha_proceso) from detalle_padron_contribuyentes dpp where dp.cuit=dpp.cuit)
	left outer join provincia pv2 
	on dp.provincia=pv2.id_provincia_afip
	where emp.cuit = $1
	and emp.sucursal = $2;

	update aux a
	set ubicacion_carpeta=i.ubicacion_carpeta
	from estudio_empresas_info i
	where i.cuit=a.cuit
	and i.fecha=(select max(fecha) from estudio_empresas_info ii where i.cuit=ii.cuit and ii.ubicacion_carpeta <>'' and ii.ubicacion_carpeta is not null limit 1)
	and i.ubicacion_carpeta is not null and i.ubicacion_carpeta<>'';

	update aux a
	set carta_doc=i.carta_doc
	from estudio_empresas_info i
	where i.cuit=a.cuit
	and i.fecha=(select max(fecha) from estudio_empresas_info ii where i.cuit=ii.cuit and ii.carta_doc is not null and ii.carta_doc<>'' limit 1)
	and i.carta_doc is not null and i.carta_doc<>'';

	return query
	select 	cuit,
	sucursal,
	razon_soc,
	nombre_fantasia,
	id_ramo_empresa,
	id_seccional,
	contacto,
	id_entidad_cam_empresa,
	observaciones_es,
	vigen_fecha,
	motivo_baja,
	alta_fecha_em,
	alta_usr_em,
	alta_ip_em,
	modi_fecha_em,
	modi_usr_em,
	modi_ip_em,
	baja_fecha_em,
	baja_usr_em,
	baja_ip_em,
	id_posicion_iva,
	id_domicilio_dom,
	domi_tipo_dom,
	calle_dom,
	piso_dom,
	depto_dom, 
	oficina_dom,
	postal_codi_dom,
	barrio_dom ,
	cod_area_telefono_dom,
	telefono_dom,
	cod_area_celular_dom,
	celular_dom, 
	observaciones_dom,
	domi_val_dom ,
	alta_fecha_dom, 
	alta_usr_dom ,
	modi_fecha_dom, 
	modi_usr_dom ,
	baja_fecha_dom, 
	baja_usr_dom ,
	provincia_dom ,
	localidad_dom ,
	numero_dom ,
	localidad,	
	provincia,
	id_domicilio_fis,
	domi_tipo_fis,
	calle,
	piso,
	depto, 
	oficina,
	postal_codi,
	barrio ,
	cod_area_telefono_fis, 
	telefono, 
	cod_area_celular_fis, 
	celular_fis, 
	observaciones,
	domi_val ,
	alta_fecha_fis, 
	alta_usr_fis ,
	modi_fecha_fis, 
	modi_usr_fis ,
	baja_fecha_fis, 
	baja_usr_fis ,
	provincia_fis ,
	localidad_fis ,
	numero ,
	localidad_nombre,
	provincia_nombre,
	domi_afip,
	domicilio_remo,
	domicilio_estudio,
	molinera,
	ubicacion_carpeta,
	carta_doc
	from aux;
	
	END;	
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_empleador_y_domi(character, character varying)
  OWNER TO postgres;