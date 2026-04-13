CREATE TABLE novedades_sss.tipo_novedad
(
  codigo character varying(2) NOT NULL,
  grupo character varying(100) NOT NULL,
  descripcion character varying(250) NOT NULL,
  CONSTRAINT tipo_novedad_pkey PRIMARY KEY (codigo)
)
WITH (
  OIDS=FALSE
);

CREATE TYPE novedades_sss.cod_tipo_novedad AS
(
  codigo character varying(2),
  grupo character varying(100),
  descripcion character varying(250)
);
