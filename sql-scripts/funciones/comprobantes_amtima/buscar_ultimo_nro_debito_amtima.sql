create or replace function buscar_ultimo_nro_debito_amtima(p_tipo character varying)
returns character varying 
LANGUAGE sql
AS $BODY$

	select cast(secuencia as character varying) from compro_tipo_amtima  where compro_tipo = 'NDB';
	

$BODY$;	