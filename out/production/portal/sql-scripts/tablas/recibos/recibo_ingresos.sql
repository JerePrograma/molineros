alter table recibo_ingresos add id_recibo_ingreso_tipo_deposito integer;
alter table recibo_ingresos add constraint fk_recibo_ingreso_tipo_deposito foreign key (id_recibo_ingreso_tipo_deposito) references recibo_ingreso_tipo_deposito(id);
-- Table: recibo_ingresos

-- DROP TABLE recibo_ingresos;

CREATE TABLE recibo_ingresos
(
  id serial NOT NULL,
  recibo_id integer NOT NULL,
  nro_cheque numeric,
  id_banco integer,
  numero_deposito character varying(30),
  importe numeric(12,2),
  fecha date,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  id_estado_efectivo integer,
  id_cuenta_bcria_destino_deposito integer,
  id_anticipo_recibo_concepto integer,
  id_recibo_ingreso_tipo_deposito integer,
  CONSTRAINT pk_recibo_ingresos PRIMARY KEY (id),
  CONSTRAINT fk_anticipo_recibo_concepto FOREIGN KEY (id_anticipo_recibo_concepto)
      REFERENCES recibo_conceptos (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ingreso_efectivo_estado FOREIGN KEY (id_estado_efectivo)
      REFERENCES efectivo_estado (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recibo_cheque FOREIGN KEY (nro_cheque, id_banco)
      REFERENCES cheque (nro_cheque, id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recibo_rec FOREIGN KEY (recibo_id)
      REFERENCES recibo (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE recibo_ingresos OWNER TO postgres;
