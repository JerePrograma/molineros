CREATE TABLE area_comentario
(
  id_area integer,
  alta_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  alta_user character varying,
  baja_user character varying,
  modi_user character varying,
  fecha timestamp without time zone,
  comentario character varying,
  CONSTRAINT fk_area_comentario FOREIGN KEY (id_area)
      REFERENCES area (id_area) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE area_comentario
  OWNER TO postgres;

