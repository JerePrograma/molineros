
CREATE TABLE compro_tipo_amtima
(
  compro_tipo character varying(3) NOT NULL,
  observaciones character varying(250),
  secuencia integer NOT NULL,
  CONSTRAINT pk_compro_tipo_amtima PRIMARY KEY (compro_tipo)
)
WITH (
  OIDS=FALSE
);
