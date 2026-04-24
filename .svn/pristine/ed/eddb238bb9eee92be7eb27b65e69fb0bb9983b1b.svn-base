-- Function: busca_documentos_faltantes_por_id_tratamiento(integer)

-- DROP FUNCTION busca_documentos_faltantes_por_id_tratamiento(integer);

CREATE OR REPLACE FUNCTION busca_documentos_faltantes_por_id_tratamiento(IN id integer)
  RETURNS TABLE(  
  id_documento integer) AS
$BODY$

select id_documento from documento_faltante_tratamiento
where id_tratamiento = $1 
  
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION busca_documentos_faltantes_por_id_tratamiento(integer) OWNER TO postgres;
