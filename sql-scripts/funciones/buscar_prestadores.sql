drop function buscar_prestadores(cuit character,
 descripcion character varying,  id_prestador integer);

CREATE OR REPLACE FUNCTION buscar_prestadores(cuit character,
 descripcion character varying,
 id_prestador integer) 
RETURNS TABLE(id_prestador integer,
 cuit character varying,
 id_tipo_prestador smallint,
 tipo_matricula character,
 nro_matricula integer,
 id_mat_provincia integer,
 id_mat_categoria character,
 contacto character varying,
 id_seccional integer,
 observaciones character varying,
 rein_liqui smallint,
 id_condicion_de_iva smallint,
 cheque_a_nombre_de character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
	select 	
	  id_prestador,
	  cuit,
	  id_tipo_prestador,
	  tipo_matricula,
	  nro_matricula,
	  id_mat_provincia,
	  id_mat_categoria,
	  contacto ,
	  id_seccional ,
	  observaciones ,
	  rein_liqui ,
	  id_condicion_de_iva,
	  cheque_a_nombre_de ,
	  alta_fecha,
	  alta_usr ,
	  modi_fecha,
	  modi_usr,
	  baja_fecha ,
	  baja_usr,
	  descripcion
	 from prestador
	where ($1 is null or ($1 is not null  and cuit=$1))
	and ($2 is null or ($2 is not null and upper(descripcion) like '%'||upper($2)||'%'))
	and ($3 = 0 or ($3 != 0 and $3 = id_prestador))
 limit 200;
$BODY$;