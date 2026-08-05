-- Table: orden_pago_ospim_pagos

-- DROP TABLE orden_pago_ospim_pagos;
alter table orden_pago_ospim_pagos add constraint fk_opop_op foreign key id_orden_pago on orden_pago_ospim(id_orden_pago);
CREATE TABLE orden_pago_ospim_pagos
(
  id serial NOT NULL,
  id_orden_pago integer,
  id_punto_venta_antic smallint,
  compro_tipo_antic character varying(3),
  compro_nro_antic character varying(50),
  cuit_antic character(11),
  compro_letra_antic character varying(1),
  compro_sucu_antic integer,
  nro_cheque numeric(15,0),
  id_banco_cheque integer,
  id_cta_bcria_cheque integer,
  id_cta_bcria_retencion integer,
  importe_retencion numeric(12,2),
  id_cta_bcria_transf_bcria integer,
  importe_transf_bcria numeric(12,2),
  nro_transf_bcria character varying(50),
  id_cta_bcria_debito_crio integer,
  importe_debito_bcrio numeric(12,2),
  nro_debito_bcrio character varying(50),
  tipo_pago integer,
  CONSTRAINT fk_opo_anticipo FOREIGN KEY (id_punto_venta_antic, compro_tipo_antic, compro_letra_antic, compro_sucu_antic, compro_nro_antic, cuit_antic)
      REFERENCES comprobante (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_opo_cheque FOREIGN KEY (nro_cheque, id_banco_cheque)
      REFERENCES cheque (nro_cheque, id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_opo_cheque_cta_bcria FOREIGN KEY (id_cta_bcria_cheque)
      REFERENCES cuenta_bcria (id_cuenta_bcria) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_opo_debito FOREIGN KEY (id_cta_bcria_debito_crio)
      REFERENCES cuenta_bcria (id_cuenta_bcria) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_opo_reten FOREIGN KEY (id_cta_bcria_retencion)
      REFERENCES cuenta_bcria (id_cuenta_bcria) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_opo_trasf FOREIGN KEY (id_cta_bcria_transf_bcria)
      REFERENCES cuenta_bcria (id_cuenta_bcria) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_tipo_pago FOREIGN KEY (tipo_pago)
      REFERENCES tipo_pago (id_tipo_pago) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE orden_pago_ospim_pagos OWNER TO postgres;
