CREATE TABLE parametros_contabilidad_amtima
(
  parametro character varying,
  id_plan_cuenta integer,
  observaciones character varying(255),
  valido_hasta date,
  valido_desde date,
  modi_usr character varying,
  modi_fecha date,
  alta_usr character varying,
  alta_fecha date
)
WITH (
  OIDS=FALSE
);
