
CREATE TABLE uoma.incidente_unidad_operativa_detalle
(
  id_incidente_detalle serial NOT NULL,
  fecha timestamp without time zone NOT NULL,
  seguimiento_incidente text,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  id_incidente integer,
  CONSTRAINT pk_incidente_detalle_unidad_operativa PRIMARY KEY (id_incidente_detalle)
)
WITH (
  OIDS=FALSE
);
