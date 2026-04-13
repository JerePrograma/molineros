drop function buscar_ultimo_nro_debito();
create or replace function buscar_ultimo_nro_debito(p_tipo character varying)
returns character varying 
LANGUAGE sql
AS $BODY$

	select cast(secuencia as character varying) from compro_tipo  where compro_tipo = 'NDB';
	

$BODY$;	