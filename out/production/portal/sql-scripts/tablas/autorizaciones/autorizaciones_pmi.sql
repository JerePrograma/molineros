CREATE TABLE autorizaciones.autorizaciones_pmi
(
  id_autorizacion_pmi integer,
  tipo_receta character varying NOT NULL,
  fecha date,
  cuil_titular character varying,
  inte integer,
  alta_fecha timestamp without time zone,
  alta_usr character varying,
  modi_fecha timestamp without time zone,
  modi_usr character varying,
  baja_fecha timestamp without time zone,
  baja_usr character varying,
  nro_receta bigint NOT NULL DEFAULT nextval('autorizaciones.autorizacion_pmi_receta_seq'::regclass),
  observaciones character varying,
  CONSTRAINT pk_autorizaciones_pmi PRIMARY KEY (nro_receta )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE autorizaciones.autorizaciones_pmi
  OWNER TO postgres;