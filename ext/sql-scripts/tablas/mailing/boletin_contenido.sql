CREATE TABLE boletin_contenido
(
  titulo text,
  contenido text,
  alta_user character varying,
  alta_fecha timestamp without time zone,
  modi_user character varying,
  modi_fecha timestamp without time zone,
  baja_user character varying,
  baja_fecha timestamp without time zone,
  id serial NOT NULL,
  id_boletin integer,
  seccion character varying,
  CONSTRAINT pk_contenido PRIMARY KEY (id ),
  CONSTRAINT fk_contenido_boletin FOREIGN KEY (id_boletin)
      REFERENCES boletin (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE boletin_contenido
  OWNER TO postgres;
