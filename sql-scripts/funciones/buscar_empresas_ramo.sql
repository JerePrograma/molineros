CREATE OR REPLACE FUNCTION buscar_empresas_ramo(id_ramo_p integer, vigen_desde date, vigen_hasta date)
  RETURNS SETOF return_empleadores AS
$BODY$
begin
	drop table if exists aux_empresa;
	create temp table aux_empresa as
	select
	  e.cuit,
	  e.sucursal,
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
	from empresa e
	where exists (select 1 from detalle_declaracion_jurada ddj where ddj.cuit=e.cuit and periodo>=vigen_desde and periodo<=vigen_hasta)
	and case when id_ramo_p=999999 then (id_ramo_empresa=id_ramo_empresa or id_ramo_empresa is null) else id_ramo_empresa=id_ramo_p end;

	if id_ramo_p=999999 then	
	  RAISE INFO 'INSERTO!';
	  insert into aux_empresa (cuit, sucursal, razon_soc, nombre_fantasia, id_ramo_empresa, id_seccional, contacto,
				   id_entidad_cam_empresa, observaciones, vigen_fecha, motivo_baja, alta_fecha, alta_usr,
				   alta_ip, modi_fecha, modi_usr, modi_ip, baja_fecha, baja_usr, baja_ip, id_posicion_iva)
	  select cuit, '000', 'NO EMPADRONADO', 'NO EMPADRONADO', 99, null, null,
				   null, null, null, null, null, null,
				   null, null, null, null, null, null, null, null
	  from detalle_declaracion_jurada ddj
	  where ddj.periodo>=vigen_desde
	  and ddj.periodo>=vigen_hasta
	  and not exists (select 1 from aux_empresa a where a.cuit=ddj.cuit);
	end if;
	  

	return query 
	select 	  cuit,
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
	from aux_empresa;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE

