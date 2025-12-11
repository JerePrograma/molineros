CREATE TABLE parametros_conceptos_amtima
(
  parametro character varying,
  id_concepto integer,
  valido_desde date,
  valido_hasta date,
  observaciones character varying(700),
  modi_usr character varying,
  modi_fecha date,
  alta_usr character varying,
  alta_fecha date
)
WITH (
  OIDS=FALSE
);
