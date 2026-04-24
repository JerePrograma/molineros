-- Table: convenio_no_os

-- DROP TABLE convenio_no_os;

CREATE TABLE convenio_no_os
(
  id serial NOT NULL,
  numero character varying(15),
  cuit character varying(13) NOT NULL,
  sucursal character varying(6) NOT NULL,
  fecha_inicio timestamp without time zone NOT NULL,
  fecha_pago timestamp without time zone,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  interes numeric(10,2),
  ajuste_capital numeric(10,2),
  ex_id character varying(10),
  deuda_actas_asociadas numeric(10,2),
  deuda_convenios_asociados numeric(10,2),
  ajuste_interes numeric(10,2),
  observaciones character varying(200),
  entidad character varying,
  CONSTRAINT pk_convenio_no_os PRIMARY KEY (id ),
  CONSTRAINT fk_convenio_no_os FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE convenio_no_os
  OWNER TO postgres;

