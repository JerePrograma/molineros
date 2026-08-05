CREATE TABLE organismo_linea
(
  id_organismo integer,
  alta_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  alta_user character varying,
  baja_user character varying,
  modi_user character varying,
  linea character varying,
  tipo_linea character varying,
  CONSTRAINT fk_organismo_linea FOREIGN KEY (id_organismo)
      REFERENCES organismo (id_organismo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organismo_linea
  OWNER TO postgres;

