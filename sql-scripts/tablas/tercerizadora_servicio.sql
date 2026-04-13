alter table tercerizadora_servicio add column convenios boolean default false;
update tercerizadora_servicio set convenios = true where id_tercerizadora = 'OMI';

-- Table: tercerizadora_servicio

-- DROP TABLE tercerizadora_servicio;

CREATE TABLE tercerizadora_servicio
(
  id_tercerizadora character varying(3) NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_tercerizadora_servicio PRIMARY KEY (id_tercerizadora)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tercerizadora_servicio OWNER TO postgres;
GRANT ALL ON TABLE tercerizadora_servicio TO postgres;
GRANT SELECT ON TABLE tercerizadora_servicio TO dschejtman;
