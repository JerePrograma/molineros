CREATE TABLE uoma.incidente_unidad_operativa
(
  id_incidente serial NOT NULL,
  cuil_titular character varying NOT NULL,
  fecha timestamp without time zone NOT NULL,
  id_domicilio integer,
  inte integer NOT NULL,
  detalle_incidente text,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  id_seccional integer,
  fecha_recepcion date DEFAULT ('now'::text)::date,
  CONSTRAINT pk_incidente_unidad_operativa PRIMARY KEY (id_incidente),
  CONSTRAINT fk_incidente_afiliado FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_incidente_domicilio FOREIGN KEY (id_domicilio)
      REFERENCES uoma.domicilio (id_domicilio) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE uoma.incidente_unidad_operativa OWNER TO postgres;

