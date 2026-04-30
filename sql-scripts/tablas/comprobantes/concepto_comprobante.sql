-- Table: concepto_comprobante

-- DROP TABLE concepto_comprobante;

CREATE TABLE concepto_comprobante
(
  id_punto_venta smallint NOT NULL,
  compro_tipo character varying(3) NOT NULL,
  compro_nro character varying(50) NOT NULL,
  cuit character(11) NOT NULL DEFAULT 0,
  compro_letra character varying(1) NOT NULL,
  compro_sucu integer NOT NULL,
  concepto_id integer NOT NULL,
  importe numeric(12,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_concepto_comprobante PRIMARY KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit, concepto_id),
  CONSTRAINT fk_concepto_comprobante FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro (id) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_concepto_comprobante_comprobante FOREIGN KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit)
      REFERENCES comprobante (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE concepto_comprobante OWNER TO postgres;
