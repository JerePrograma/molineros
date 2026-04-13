CREATE TABLE organismo_contacto
(
  id_organismo integer,
  id_contacto integer,
  baja_fecha timestamp without time zone,
  baja_user character varying,
  CONSTRAINT fk_contacto FOREIGN KEY (id_contacto)
      REFERENCES contacto (id_contacto) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_organismo FOREIGN KEY (id_organismo)
      REFERENCES organismo (id_organismo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organismo_contacto
  OWNER TO postgres;

