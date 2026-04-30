CREATE TABLE bonos
(
  tipo_bono integer NOT NULL,
  nro_bono integer NOT NULL,
  alta_usr character varying,
  alta_fecha date,
  CONSTRAINT pk_bono PRIMARY KEY (tipo_bono, nro_bono)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE bonos OWNER TO postgres;