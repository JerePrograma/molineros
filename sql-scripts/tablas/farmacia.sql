alter table farmacia add column cheque_a_favor_de character varying(250)
-- Table: farmacia

-- DROP TABLE farmacia;

CREATE TABLE farmacia
(
  camara character varying,
  farmacia character varying,
  cuit character varying,
  codigo character varying,
  calle character varying,
  telefono character varying,
  cod_farm character varying,
  sucursal character varying(6),
  cheque_a_favor_de character varying(250)
  CONSTRAINT fk_farmacia_empresa FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE farmacia OWNER TO postgres;
