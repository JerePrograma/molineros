-- Table: reintegro_prestacion_odo_ort

-- DROP TABLE reintegro_prestacion_odo_ort;

CREATE TABLE reintegro_prestacion_odo_ort
(
  id serial NOT NULL,
  id_reintegro integer NOT NULL,
  id_prestacion integer NOT NULL,
  id_plan integer NOT NULL,
  fecha_prestacion timestamp without time zone NOT NULL,
  pieza character varying(2),
  cara character varying(5),
  honorarios numeric(9,2),
  gastos numeric(9,2),
  tercerizado character varying(1),
  importe numeric(9,2),
  compro_a_debitar_tipo character varying(3),
  compro_a_debitar_numero character varying(15),
  ex_id numeric,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  cuit character varying(11),
  descripcion character varying(250),
  codigo character varying(10),
  cantidad numeric(9,2),
  id_prestador_externo integer,
  nro_cuotas smallint DEFAULT 1,
  CONSTRAINT pk_reintegro_prestacion_odo_ort PRIMARY KEY (id),
  CONSTRAINT fk_reint_prestc_ort_reint FOREIGN KEY (id_reintegro)
      REFERENCES reintegro (id_reintegro) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_reintegro_prestacion_odo_ort_prest FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE reintegro_prestacion_odo_ort OWNER TO postgres;
GRANT ALL ON TABLE reintegro_prestacion_odo_ort TO postgres;
GRANT SELECT ON TABLE reintegro_prestacion_odo_ort TO dschejtman;
