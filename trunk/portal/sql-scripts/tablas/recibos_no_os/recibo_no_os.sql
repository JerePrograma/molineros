-- Table: recibo_no_os

-- DROP TABLE recibo_no_os;

CREATE TABLE recibo_no_os
(
  id serial NOT NULL,
  numero character varying(16),
  tipo character varying(2),
  fecha date,
  cuit character varying(13),
  descripcion character varying(500),
  importe numeric(10,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  ex_id character varying(10),
  sucursal character varying(6),
  id_seccional integer,
  entidad character varying,
  CONSTRAINT pk_recibo_no_os PRIMARY KEY (id ),
  CONSTRAINT fk_recibo_no_os_empresa FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE recibo_no_os
  OWNER TO postgres;

