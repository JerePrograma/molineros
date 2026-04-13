CREATE OR REPLACE FUNCTION buscar_prestador_externo_by_id(p_id integer) 
RETURNS TABLE(
 prs__id_prestador integer,
 prs__cuit character varying,
 prs__tipo character varying,
 prs__tipo_matricula character,
 prs__nro_matricula integer,
 prs__id_mat_provincia integer,
 prs__id_mat_categoria character,
 prs__contacto character varying,
 prs__id_seccional integer,
 prs__observaciones character varying,
 prs__rein_liqui smallint,
 prs__id_condicion_de_iva smallint,
 prs__cheque_a_nombre_de character varying,
 prs__alta_fecha timestamp without time zone,
 prs__alta_usr character varying,
 prs__modi_fecha timestamp without time zone,
 prs__modi_usr character varying,
 prs__baja_fecha timestamp without time zone,
 prs__baja_usr character varying,
 prs__descripcion character varying
 )
    LANGUAGE sql
    AS $BODY$
    
	select 	
	  prs.id_prestador_externo,
	  prs.cuit,
	  prs.tipo_prestador_externo,
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
	  prs.descripcion
	from prestador_externo prs	
	where  prs.id_prestador_externo = $1;
$BODY$;