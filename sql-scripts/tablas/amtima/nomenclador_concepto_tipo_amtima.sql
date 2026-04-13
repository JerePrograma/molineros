CREATE TABLE nomenclador_concepto_tipo_amtima
(
  id serial NOT NULL,
  descripcion character varying(100),
  CONSTRAINT pf_nomenclador_concepto_tipo_amtima PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
