-- Function: buscar_ultimo_nro_comprobante(character varying, character varying, character varying)

-- DROP FUNCTION buscar_ultimo_nro_comprobante(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION buscar_ultimo_nro_comprobante(p_tipo character varying, p_cuit character varying, p_sucu character varying)
  RETURNS character varying AS
$BODY$

select compro_nro  from comprobante 
where compro_tipo = $1
and compro_nro like $2 || '-' || $3 || '/%' 
and compro_nro <> '30629138567-003/1-'
and id_punto_venta = 1
and cast(substring(compro_nro,position('/' in compro_nro)+1,length(compro_nro))  as integer) = (select max  (cast(substring(compro_nro,position('/' in compro_nro)+1,length(compro_nro))  as integer))  from comprobante 
												where compro_tipo = $1
												and compro_nro like $2 || '-' || $3 || '/%' 
												and compro_nro <> '30629138567-003/1-'
												and id_punto_venta = 1)
$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
ALTER FUNCTION buscar_ultimo_nro_comprobante(character varying, character varying, character varying) OWNER TO postgres;
