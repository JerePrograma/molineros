CREATE TABLE movimiento_banco_amtima_items
(
  id serial NOT NULL,
  id_movimiento integer,
  nro_cheque numeric(15,0),
  id_banco integer,
  id_estado_cheque_viejo integer,
  id_estado_cheque_nuevo integer,
  recibo_ingreso_id integer,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone,
  modi_usr character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_movimiento_banco_amtima_items PRIMARY KEY (id),
  CONSTRAINT fk_id_estado_nuevo_amtima FOREIGN KEY (id_estado_cheque_nuevo)
      REFERENCES cheque_amtima_estado (id) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_id_estado_viejo_amtima FOREIGN KEY (id_estado_cheque_viejo)
      REFERENCES cheque_amtima_estado (id) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_mov_cheque_amtima FOREIGN KEY (nro_cheque, id_banco)
      REFERENCES cheque_amtima (nro_cheque, id_banco) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_mov_cheque_rec_ing FOREIGN KEY (recibo_ingreso_id)
      REFERENCES recibo_ingresos (id) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_movimiento_banco_amtima FOREIGN KEY (id_movimiento)
      REFERENCES movimiento_banco_amtima (id_movimiento) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE movimiento_banco_amtima_items
  OWNER TO postgres;
