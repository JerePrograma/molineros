CREATE OR REPLACE FUNCTION buscar_grupo_fliar(IN cuil_v character, IN inte_v integer)
  RETURNS TABLE(cuil character varying, inte integer, id_parentesco_sss integer, parentesco character varying, 
  nombre character varying, apellido character varying, tdoc character varying, documento character varying, seccional character varying, 
  ingreso date, baja_fecha timestamp without time zone, vigen_fecha date) AS
$BODY$
	select 	a.cuil_titular,
		a.inte, 
		a.id_parentesco_sss,
		p.descripcion as parentesco,
		a.nombre,
		a.apellido,
		a.documento_tipo,
		a.docu_numero,
		s.descripcion,
		a.ingre_fecha,
		a.baja_fecha,
		cast(a.vigen_fecha as date) 
	from afiliado a, seccional s, parentesco_sss p
	where cuil_titular=isNull($1,cuil_titular)	
	--and inte!=$2
	and s.id_seccional=a.id_seccional
	and a.id_parentesco_sss = p.codigo
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
