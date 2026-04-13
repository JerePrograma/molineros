CREATE TABLE recibo_conceptos_amtima
(
  id serial NOT NULL,
  recibo_id integer NOT NULL,
  acta_id integer,
  convenio_id integer,
  nro_cheque_no_depositado numeric,
  id_banco_no_depositado integer,
  nro_cheque_rechazado numeric,
  id_banco_rechazado integer,
  caja_concepto_id integer,
  concepto_importe_por_cheques numeric(12,2),
  concepto_importe_adicional numeric(12,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  importe_remuneracion_total numeric(12,2),
  cantidad_empleados integer,
  periodo date,
  CONSTRAINT pk_recibo_conceptos_amtima PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
