CREATE TABLE boletin_listas
(
  id_boletin integer,
  id_mailing_list integer,
  alta_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  modi_user character varying,
  baja_user character varying,
  alta_user character varying,
  CONSTRAINT fk_boletin_list FOREIGN KEY (id_mailing_list)
      REFERENCES mailing_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE boletin_listas
  OWNER TO postgres
