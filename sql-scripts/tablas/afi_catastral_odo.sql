DROP table afi_catastral_odo

CREATE SEQUENCE afi_catastral_odo_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

-- Table: afi_catastral_odo

-- DROP TABLE afi_catastral_odo;

CREATE TABLE afi_catastral_odo
(
  id serial NOT NULL,
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  fecha timestamp without time zone,
  id_prestacion integer NOT NULL,
  codigo character varying NOT NULL,
  pieza character varying(2),
  cara character varying(5),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  CONSTRAINT fk_afi_catastral_odo_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_catastral_odo_prest FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_catastral_odo OWNER TO postgres;
GRANT ALL ON TABLE afi_catastral_odo TO postgres;
GRANT SELECT ON TABLE afi_catastral_odo TO dschejtman;

--
ALTER TABLE ONLY afi_catastral_odo
    ADD CONSTRAINT pk_afi_catastral_odo PRIMARY KEY (id);

