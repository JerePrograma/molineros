CREATE TABLE recibo_conceptos_pagos_amtima
(
  recibo_concepto_id integer,
  recibo_ingreso_id integer,
  importe numeric(12,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  pendiente_derivar numeric
)
WITH (
  OIDS=FALSE
);
