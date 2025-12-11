-- Table: acta_no_os

-- DROP TABLE acta_no_os;

CREATE TABLE acta_no_os
(
  id serial NOT NULL,
  numero character varying(8),
  cuit character varying(13),
  sucursal character varying(6),
  fecha_inicio timestamp without time zone NOT NULL,
  fecha_pago timestamp without time zone NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  otros numeric(10,2),
  interes numeric(10,2),
  capital numeric(10,2),
  deuda_actas_asociadas numeric(10,2),
  cierre_fecha timestamp without time zone,
  cierre_usr character varying(15),
  ex_id character varying(10),
  acta_cerrada boolean NOT NULL DEFAULT false,
  molinera boolean,
  estado character varying,
  entidad character varying,
  periodo_ini date,
  periodo_fin date,
  capital_sindicato numeric,
  interes_sindicato numeric,
  capital_solidario numeric,
  interes_solidario numeric,
  capital_usufructo numeric,
  interes_usufructo numeric,
  capital_art46 numeric,
  interes_art46 numeric,
  CONSTRAINT pk_acta_no_os PRIMARY KEY (id ),
  CONSTRAINT fk_acta_no_os_empresa FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE acta_no_os
  OWNER TO postgres;
