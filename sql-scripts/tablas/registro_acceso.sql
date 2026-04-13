-- DROP TABLE registro_acceso;

CREATE TABLE registro_acceso
(
  id serial NOT NULL,
  id_tarjeta_acceso integer NOT NULL,
  fecha_registro timestamp without time zone NOT NULL,
  tipo_registro character varying(1) NOT NULL,
  punto_acceso integer,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_registro_acceso PRIMARY KEY (id) 
)
WITH (
  OIDS=FALSE
);
ALTER TABLE registro_acceso
  OWNER TO postgres;
