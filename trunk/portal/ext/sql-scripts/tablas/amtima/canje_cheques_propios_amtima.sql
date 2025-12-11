CREATE TABLE canje_cheques_propios_amtima
(
  id serial NOT NULL,
  id_orden_pago_amtima integer,
  id_orden_pago_amtima_nueva integer,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  id_movimiento integer,
  CONSTRAINT pk_canje_cheques_propios_amtima PRIMARY KEY (id),
  CONSTRAINT fk_canje_mov_bcrio_amtima FOREIGN KEY (id_movimiento)
      REFERENCES movimiento_banco_amtima (id_movimiento) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_canje_op_amtima FOREIGN KEY (id_orden_pago_amtima)
      REFERENCES orden_pago_amtima (id_orden_pago) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_canje_op_nueva FOREIGN KEY (id_orden_pago_amtima_nueva)
      REFERENCES orden_pago_amtima (id_orden_pago) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
