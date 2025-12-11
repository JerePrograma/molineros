CREATE TABLE concepto_maestro_amtima
(
  id serial NOT NULL,
  descripcion_original character varying,
  alta_fecha date,
  alta_usr character varying,
  modi_fecha date,
  modi_usr character varying,
  baja_fecha date,
  baja_usr character varying,
  valido_desde date,
  valido_hasta date,
  CONSTRAINT pk_concepto_maestro_amtima PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
