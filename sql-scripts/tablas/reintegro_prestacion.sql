alter table reintegro_prestacion add fecha_comprobante timestamp without time zone;
alter table reintegro_prestacion add importe_comprobante numeric (9,2);
alter table reintegro_prestacion add motivo_alta_discapacidad integer;

-- Table: reintegro_prestacion

-- DROP TABLE reintegro_prestacion;

CREATE TABLE reintegro_prestacion
(
  id_reintegro integer NOT NULL,
  id_prestacion integer NOT NULL,
  id_plan integer NOT NULL,
  fecha_prestacion timestamp without time zone NOT NULL,
  cantidad numeric(9,2),
  importe numeric(9,2),
  compro_a_debitar_tipo character varying(3),
  compro_a_debitar_numero character varying(15),
  tercerizado character varying(1),
  ex_id numeric,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  cuit character varying(11),
  descripcion character varying(250),
  codigo character varying(10),
  periodo timestamp without time zone,
  cuit_entidad character varying(13),
  sucursal_entidad character varying(6)  
  CONSTRAINT pk_reintegro_prestacion PRIMARY KEY (id_reintegro, id_prestacion, id_plan, alta_fecha),
  CONSTRAINT fk_reint_prestc_reint FOREIGN KEY (id_reintegro)
      REFERENCES reintegro (id_reintegro) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_reinte_prest_prest FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE reintegro_prestacion OWNER TO postgres;
GRANT ALL ON TABLE reintegro_prestacion TO postgres;
GRANT SELECT ON TABLE reintegro_prestacion TO dschejtman;
