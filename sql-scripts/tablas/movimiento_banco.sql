-- Table: movimiento_banco

-- DROP TABLE movimiento_banco;

CREATE TABLE movimiento_banco
(
  id_movimiento integer NOT NULL DEFAULT nextval('mov_bcrio_id_seq'::regclass),
  fecha_movimiento date NOT NULL,
  id_tipo_mov integer NOT NULL,
  id_cuenta_bcria integer NOT NULL,
  deb_cred boolean NOT NULL,
  id_tipo_transaccion integer,
  id_chequera integer,
  nro_compro character varying,
  fecha_comprobante date,
  importe_movimiento double precision NOT NULL,
  descripcion character varying,
  imprime_cheque boolean,
  no_a_la_orden boolean,
  conciliacion_fecha timestamp without time zone,
  conciliacion_usr character varying(15),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone,
  modi_usr character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_mov_bco PRIMARY KEY (id_movimiento),
  CONSTRAINT fk_id_chequera FOREIGN KEY (id_chequera)
      REFERENCES chequera (id_chequera) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_id_cta_bcria FOREIGN KEY (id_cuenta_bcria)
      REFERENCES cuenta_bcria (id_cuenta_bcria) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_id_tipo_trans FOREIGN KEY (id_tipo_transaccion)
      REFERENCES tipo_trans_bcria (id_tipo_transaccion) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_tipo_mov FOREIGN KEY (id_tipo_mov)
      REFERENCES tipo_mov_bcrio_maestro (id) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE movimiento_banco OWNER TO postgres;
