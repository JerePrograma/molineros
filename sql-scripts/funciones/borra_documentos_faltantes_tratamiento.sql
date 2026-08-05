CREATE OR REPLACE FUNCTION borra_documentos_faltantes_tratamiento(id_tratamiento integer)
  RETURNS integer AS
$BODY$
    delete from documento_faltante_tratamiento
    where id_tratamiento=$1;    
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;