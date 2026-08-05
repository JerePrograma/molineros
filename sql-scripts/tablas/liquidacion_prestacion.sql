alter table liquidacion_prestacion alter column cantidad  type numeric(9,2);
alter table liquidacion_prestacion add motivo_alta_discapacidad integer;

-- Table: liquidacion_prestacion

-- DROP TABLE liquidacion_prestacion;

CREATE TABLE liquidacion_prestacion
(
  id_liquidacion integer NOT NULL,
  orden integer NOT NULL,
  cuil_titular character varying(15),
  id_prestacion integer,
  fecha_prestacion timestamp without time zone,
  cantidad smallint,
  importe numeric(11,2),
  servicio character varying(30),
  solicitado numeric(11,2),
  debitado numeric(11,2),
  resultado numeric(11,2),
  tercerizado character varying(1),
  inte integer,
  alta_usr character varying(15) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15),
  modi_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_fecha timestamp without time zone,
  periodo timestamp without time zone,
  CONSTRAINT pk_liquidacion_prestadocion PRIMARY KEY (id_liquidacion, orden),
  CONSTRAINT fk_liq_prest_prest FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_lp_liquidacion FOREIGN KEY (id_liquidacion)
      REFERENCES liquidacion (id_liquidacion) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE liquidacion_prestacion OWNER TO postgres;
GRANT ALL ON TABLE liquidacion_prestacion TO postgres;
GRANT SELECT ON TABLE liquidacion_prestacion TO dschejtman;
