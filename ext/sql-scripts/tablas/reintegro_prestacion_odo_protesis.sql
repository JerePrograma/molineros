-- Table: reintegro_prestacion_odo_protesis

-- DROP TABLE reintegro_prestacion_odo_protesis;

CREATE TABLE reintegro_prestacion_odo_protesis
(
  id integer NOT NULL DEFAULT nextval('reintegro_prestacion_odo_prot_id_seq'::regclass),
  id_reintegro integer NOT NULL,
  id_prestacion integer NOT NULL,
  id_plan integer NOT NULL,
  fecha_prestacion timestamp without time zone NOT NULL,
  pieza character varying(2),
  cara character varying(5),
  honorarios numeric(11,2),
  gastos numeric(11,2),
  tercerizado character varying(1),
  importe numeric(11,2),
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
  CONSTRAINT pk_reintegro_prestacion_odo_protesis PRIMARY KEY (id),
  CONSTRAINT fk_reintegro_prestacion_odo_protesis_prest FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE reintegro_prestacion_odo_protesis OWNER TO postgres;
GRANT ALL ON TABLE reintegro_prestacion_odo_protesis TO postgres;
GRANT SELECT ON TABLE reintegro_prestacion_odo_protesis TO dschejtman;
