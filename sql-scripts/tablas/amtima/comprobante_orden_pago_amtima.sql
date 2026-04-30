
CREATE TABLE comprobante_orden_pago_amtima
(
  id_orden_pago_amtima integer NOT NULL,
  id_punto_venta smallint NOT NULL,
  compro_tipo character varying(3) NOT NULL,
  compro_nro character varying(50) NOT NULL,
  cuit character(11) NOT NULL DEFAULT 0,
  compro_letra character varying(1) NOT NULL DEFAULT 'B'::character varying,
  compro_sucu integer NOT NULL DEFAULT 0,
  CONSTRAINT pk_comp_orden_pago_amtima PRIMARY KEY (id_orden_pago_amtima, id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit),
  CONSTRAINT fk_comp_op_amtima FOREIGN KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit)
      REFERENCES comprobante_amtima (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_comprobate_orden_pago_amtima foreign KEY (id_orden_pago_amtima) references orden_pago_amtima(id_orden_pago)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE comprobante_orden_pago_amtima OWNER TO postgres;
