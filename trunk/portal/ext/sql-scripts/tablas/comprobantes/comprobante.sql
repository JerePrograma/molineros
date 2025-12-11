alter table comprobante add anulado_fecha timestamp without time zone;
alter table comprobante add anulado_usr character varying(15);
-- Table: comprobante

-- DROP TABLE comprobante;

CREATE TABLE comprobante
(
  id_punto_venta smallint NOT NULL,
  compro_tipo character varying(3) NOT NULL,
  compro_nro character varying(50) NOT NULL,
  factu_perio timestamp without time zone,
  fecha timestamp without time zone NOT NULL,
  impre_fecha timestamp without time zone,
  cuil_titular character varying(15),
  inte integer,
  vto timestamp without time zone,
  vto2 timestamp without time zone,
  exen numeric(9,2) NOT NULL,
  grava numeric(9,2) NOT NULL,
  iva_total numeric(9,2) NOT NULL,
  ivan_total numeric(9,2) NOT NULL,
  total numeric(12,2) NOT NULL,
  cance character varying(1),
  anu_moti smallint,
  anu_fecha timestamp without time zone,
  anu_usu character varying(15),
  observaciones character varying(250),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  fecha_recepcion timestamp without time zone,
  fecha_emision timestamp without time zone,
  cuit character(11) NOT NULL DEFAULT 0,
  compro_letra character varying(1) NOT NULL DEFAULT 'B'::character varying,
  compro_sucu integer NOT NULL DEFAULT 0,
  debito_para_egreso boolean,
  seccional integer,
  cuit_acreedor character varying(13),
  sucu_acreedor character varying(6),
  periodo_prestacion date,
  CONSTRAINT pk_comprobante PRIMARY KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit),
  CONSTRAINT fk_compro_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_empre_acreedor FOREIGN KEY (cuit_acreedor, sucu_acreedor)
      REFERENCES empresa (cuit, sucursal) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE comprobante OWNER TO postgres;
