CREATE TABLE area_contacto
(
  id_area integer,
  id_contacto integer,
  baja_fecha timestamp without time zone,
  baja_user character varying,
  CONSTRAINT fk_area FOREIGN KEY (id_area)
      REFERENCES area (id_area) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_contacto_area FOREIGN KEY (id_contacto)
      REFERENCES contacto (id_contacto) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE area_contacto
  OWNER TO postgres;
