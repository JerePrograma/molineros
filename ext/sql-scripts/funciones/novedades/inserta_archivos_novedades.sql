CREATE OR REPLACE FUNCTION novedades_sss.inserta_archivos_novedades(fecha_archivo_p date, descripcion_p character varying, cant_registros_p integer, import_usr_p character varying)
  RETURNS integer AS
$BODY$

BEGIN

INSERT INTO novedades_sss.archivos_novedades(fecha_archivo, descripcion, 
				cant_registros, import_usr)
			VALUES (fecha_archivo_p, descripcion_p, 
				cant_registros_p, import_usr_p);

return currval('novedades_sss.archivos_novedades_id_proceso_seq');

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;