CREATE TABLE cierre_periodo_contable_asientos_amtima
(
  fecha_cierre timestamp without time zone NOT NULL,
  observacion character varying(2000) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50)
)
WITH (
  OIDS=FALSE
);
