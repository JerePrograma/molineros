create type return_empleadores as (cuit character varying,
 sucursal character varying,
 razon_soc character varying,
 nombre_fantasia character varying,
 id_ramo_empresa smallint,
 id_seccional integer,
 contacto character varying,
 id_entidad_cam_empresa smallint,
 observaciones character varying,
 vigen_fecha timestamp without time zone,
 motivo_baja character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 alta_ip character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 modi_ip character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying,
 baja_ip character varying,
 id_posicion_iva smallint);

drop FUNCTION buscar_empleadores(cuit character,
 descripcion character varying,
 sucu character varying) ;

 
 CREATE OR REPLACE FUNCTION buscar_empleadores(p_cuit character, p_descripcion character varying, p_sucu character varying)
  RETURNS SETOF return_empleadores AS
$BODY$
begin
    if p_cuit is not null and p_sucu is null then
	return query select
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
	  motivo_baja,
	  alta_fecha,
	  alta_usr,
	  alta_ip,
	  modi_fecha,
	  modi_usr,
	  modi_ip,
	  baja_fecha,
	  baja_usr,
	  baja_ip,
	  id_posicion_iva
	from empresa
	where cuit=p_cuit;
     end if;

    if p_cuit is not null and p_sucu is not null then
	return query select
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
	  motivo_baja,
	  alta_fecha,
	  alta_usr,
	  alta_ip,
	  modi_fecha,
	  modi_usr,
	  modi_ip,
	  baja_fecha,
	  baja_usr,
	  baja_ip,
	  id_posicion_iva
	from empresa
	where cuit=p_cuit and sucursal=p_sucu;
     end if;
     
     if p_cuit is null then
	return query select 	
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
	  motivo_baja,
	  alta_fecha,
	  alta_usr,
	  alta_ip,
	  modi_fecha,
	  modi_usr,
	  modi_ip,
	  baja_fecha,
	  baja_usr,
	  baja_ip,
	  id_posicion_iva
	from empresa
	where (p_cuit is null or (p_cuit is not null  and cuit=p_cuit))
	and (p_descripcion is null or (p_descripcion is not null and upper(razon_soc) like '%'||upper(p_descripcion)||'%'))	
	and (p_sucu is null or (p_sucu is not null and sucursal=p_sucu));
     end if;

end;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_empleadores(character, character varying, character varying) OWNER TO postgres;
