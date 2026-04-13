CREATE OR REPLACE FUNCTION buscar_opciones_sss_para_exportar()
  RETURNS TABLE(opsss_tipo_exportacion character varying, opsss_id_delegacion integer, opsss_delegacion character varying, 
  opsss_libro integer, opsss_tomo integer, opsss_nro_formulario integer, opsss_os_elegida integer, opsss_regimen character varying, 
  opsss_cuil character varying, opsss_apellido character varying, opsss_nombre character varying, opsss_sexo character varying, 
  opsss_calle character varying, opsss_numero character varying, opsss_piso integer, opsss_departamento character varying, 
  opsss_localidad character varying, opsss_telefono_particular character varying, opsss_telefono_laboral character varying, 
  opsss_telefono_celular character varying, opsss_email character varying, opsss_os_anterior integer, opsss_cuit character varying, 
  opsss_unifica_apo character varying, opsss_fecha_elecc date, opsss_fecha_certi date, opsss_cuil_conyuge character varying, 
  opsss_ape_nom_conyuge character varying, opsss_fecha_entrega date, opsss_fecha_exportacion date, opsss_numero_lote integer, 
  opsss_version_sistema character varying, opsss_postal_codi character varying, opsss_id integer, opsss_provincia character varying) AS
$BODY$
declare max_nro_lote integer;	
BEGIN
drop table if exists opciones_a_exportar_sss;

max_nro_lote=max(numero_lote) from afi_opciones_sss;
	  
create temp table opciones_a_exportar_sss as 	
	
select 	
  tipo_exportacion,
  id_delegacion,
  delegacion,
  libro,
  tomo,
  nro_formulario,
  os_elegida,
  regimen,
  cuil,
  apellido,
  nombre,
  sexo,
  calle,
  numero,
  piso,
  departamento,
  localidad,
  telefono_particular,
  telefono_laboral,
  telefono_celular,
  email,
  os_anterior,
  cuit,
  unifica_apo,
  fecha_elecc,
  fecha_certi,
  cuil_conyuge,
  ape_nom_conyuge,
  fecha_entrega,
  fecha_exportacion,
  numero_lote,
  version_sistema,
  postal_codi,
  id serial,
  provincia
	from afi_opciones_sss opsss
	where fecha_exportacion is null 
	and okdesdesss is false
	and baja_fecha is null;

--luego de buscar las opciones a exportar vamos a poner la fecha de exportacion.
--recordar que no se podran volver a exportar x 2da vez 
--y si se necesitara hacer hay que volver atras los registros con la fecha de exportacion que recien se genero
	
update afi_opciones_sss aos set fecha_exportacion=CURRENT_TIMESTAMP, numero_lote=max_nro_lote+1 where exists (select 1 from opciones_a_exportar_sss exp where aos.cuil=exp.cuil);	
	 
 return query select * from opciones_a_exportar_sss;
END; 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;