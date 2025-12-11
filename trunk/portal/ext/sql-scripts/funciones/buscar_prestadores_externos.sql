CREATE OR REPLACE FUNCTION buscar_prestadores_externos(
tipo_matricula character varying, 
numero_matricula integer,
descripcion character varying,
id_prestador integer)
 
RETURNS TABLE(id_prestador integer,
 cuit character varying,
 tipo_prestador character varying,
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
	  id_prestador_externo,
	  cuit,
	  tipo_prestador_externo,
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
	from prestador_externo
	where ($1 is null or ($1 is not null  and tipo_matricula=$1))
	and ($2 = 0 or ($2 != 0  and nro_matricula=$2))
	and ($3 is null or ($3 is not null and upper(descripcion) like '%'||upper($3)||'%'))
	and ($4 = 0 or ($4 != 0 and $4 = id_prestador_externo))
 limit 200;
$BODY$;