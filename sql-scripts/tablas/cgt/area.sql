CREATE TABLE area
(
  id_area integer NOT NULL DEFAULT nextval('area_id_seq'::regclass),
  denominacion character varying,
  observaciones character varying,
  alta_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  alta_usr character varying,
  modi_usr character varying,
  baja_usr character varying,
  ambito character varying,
  telefono character varying,
  web character varying,
  sigla character varying,
  id_organismo integer,
  CONSTRAINT pk_area PRIMARY KEY (id_area ),
  CONSTRAINT fk_area_organismo FOREIGN KEY (id_organismo)
      REFERENCES organismo (id_organismo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE area
  OWNER TO postgres;
