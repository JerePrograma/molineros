CREATE OR REPLACE FUNCTION buscar_prestadores_domicilio(IN cuit character, IN descripcion character varying, 
IN id_prestador integer, IN provincia integer, IN localidad integer, IN solovigentes boolean)
  RETURNS TABLE(prs__id_prestador integer, prs__cuit character varying, prs__id_tipo_prestador smallint, 
  prs__tipo_matricula character, prs__nro_matricula integer, prs__id_mat_provincia integer, prs__id_mat_categoria character, 
  prs__contacto character varying, prs__id_seccional integer, prs__observaciones character varying, 
  prs__rein_liqui smallint, prs__id_condicion_de_iva smallint, prs__cheque_a_nombre_de character varying, 
  prs__alta_fecha timestamp without time zone, prs__alta_usr character varying, prs__modi_fecha timestamp without time zone, 
  prs__modi_usr character varying, prs__baja_fecha timestamp without time zone, prs__baja_usr character varying, 
  prs__descripcion character varying, dom__id_domicilio integer, dom__domi_tipo character varying, 
  dom__calle character varying, dom__piso character varying, dom__depto character varying, dom__oficina character varying, 
  dom__postal_codi character varying, dom__barrio character varying, dom__telefono character varying,
  dom__cod_area_telefono character varying, dom__cod_area_celular character varying, dom__celular character varying, 
  dom__observaciones character varying, dom__domi_val character varying, dom__alta_fecha timestamp without time zone, 
  dom__alta_usr character varying, dom__modi_fecha timestamp without time zone, dom__modi_usr character varying, 
  dom__baja_fecha timestamp without time zone, dom__baja_usr character varying, dom__provincia integer, dom__localidad integer, 
  dom__numero character varying, dom__localidad_nombre character, dom__provincia_nombre character) AS
$BODY$

	select 	
	  prs.id_prestador,
	  prs.cuit,
	  prs.id_tipo_prestador,
	  prs.tipo_matricula,
	  prs.nro_matricula,
	  prs.id_mat_provincia,
	  prs.id_mat_categoria,
	  prs.contacto ,
	  prs.id_seccional ,
	  prs.observaciones ,
	  prs.rein_liqui ,
	  prs.id_condicion_de_iva,
	  prs.cheque_a_nombre_de ,
	  prs.alta_fecha,
	  prs.alta_usr ,
	  prs.modi_fecha,
	  prs.modi_usr,
	  prs.baja_fecha ,
	  prs.baja_usr,
	  prs.descripcion,
	  dom.id_domicilio,
	  dom.domi_tipo,
		dom.calle,
		dom.piso,
		dom.depto, 
		dom.oficina,
		dom.postal_codi,
		dom.barrio ,
		cast(null as varchar),
		dom.telefono,
		cast(null as varchar),
		cast(null as varchar), 
		dom.observaciones,
		dom.domi_val ,
		dom.alta_fecha, 
		dom.alta_usr ,
		dom.modi_fecha, 
		dom.modi_usr ,
		dom.baja_fecha, 
		dom.baja_usr ,
		dom.provincia ,
		dom.localidad ,
		dom.numero ,
		dom.localidad_nombre ,
		dom.provincia_nombre 
	
	from prestador prs
	left outer join prestad_lugar_atencion prsdom
	on prs.id_prestador = prsdom.id_prestador
	and prsdom.baja_fecha is null
	and prsdom.vigen_desde = (select max(vigen_desde) from prestad_lugar_atencion where id_prestador = prsdom.id_prestador and baja_fecha is null )
	left outer join domicilio dom
	on prsdom.id_domicilio = dom.id_domicilio
	--where  prs.id_prestador = $1;

	where ($1 is null or ($1 is not null  and prs.cuit=$1))
	and ($2 is null or ($2 is not null and upper(prs.descripcion) like '%'||upper($2)||'%'))
	and ($3 = 0 or ($3 != 0 and $3 = prs.id_prestador))
	and ($4 = 0 or ($4 != 0 and $4 = dom.provincia))
	and ($5 = 0 or ($5 != 0 and $5 = dom.localidad))
	and ($6 is false or ($6 is true and prs.baja_fecha is null))

	limit 200;
 
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;