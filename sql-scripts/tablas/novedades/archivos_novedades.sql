CREATE TABLE novedades_sss.archivos_novedades
(
  id_proceso serial NOT NULL,	
  fecha_archivo date NOT NULL,
  descripcion character varying(50) NOT NULL,
  cant_registros integer NOT NULL DEFAULT 0,
  import_usr character varying(15) NOT NULL,
  import_fecha timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);