-- Table: recibo_no_os_ingreso_tipo_deposito

-- DROP TABLE recibo_no_os_ingreso_tipo_deposito;

CREATE TABLE recibo_no_os_ingreso_tipo_deposito
(
  id integer NOT NULL,
  descripcion character varying,
  CONSTRAINT pk_recibo_no_os_ingreso_tipo_deposito PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE recibo_no_os_ingreso_tipo_deposito
  OWNER TO postgres;

