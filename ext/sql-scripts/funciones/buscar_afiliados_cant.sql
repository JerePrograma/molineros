CREATE OR REPLACE FUNCTION buscar_afiliados_cant(cuil_v character,
 inte_v integer,
 tipodoc_v character,
 nrodoc_v character,
 seccional_v integer,
 apellido_v character,
 nombre_v character) 
RETURNS bigint
    LANGUAGE sql
    AS $BODY$
	select 	count(*)
	from afiliado a
	where cuil_titular=isNull($1,cuil_titular)
	and inte=isNull($2,inte)
	and documento_tipo=isNull($3,documento_tipo)
	and docu_numero=isNull($4,docu_numero)	
	and a.id_seccional=isNull(isNull($5),a.id_seccional)
	and upper(apellido) like ''||isNull(upper($6),upper(apellido))||'%'
	and upper(nombre) like ''||isNull(upper($7),upper(nombre))||'%';	
$BODY$;


ALTER FUNCTION public.buscar_afiliados_cant(cuil_v character, inte_v integer, tipodoc_v character, nrodoc_v character, seccional_v integer, apellido_v character, nombre_v character) OWNER TO postgres;

--
