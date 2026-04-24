-- Table: tratamiento_discapacidad

-- DROP TABLE tratamiento_discapacidad;

CREATE TABLE tratamiento_discapacidad
(
  id_tratamiento integer NOT NULL DEFAULT nextval('tratamiento_discapacidad_id_seq'::regclass),
  id_prestacion integer NOT NULL,
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  cantidad numeric(9,2),
  periodicidad character varying(10),
  periodo_desde timestamp without time zone,
  periodo_hasta timestamp without time zone,
  importe_total numeric(9,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  id_prestador integer NOT NULL,
  estado integer NOT NULL,
  recupera_ape boolean,
  observaciones character varying(5000),
  cuit character varying(13),
  prestador character varying(6),
  id_seccional character varying(6),
  cantidad_viajes_mes numeric(9,2),
  cantidad_kilometros_dia numeric(9,2),
  cantidad_kilometros_mes numeric(9,2),
  importe_kilometro_unit numeric(9,2),
  hs_espera_dia numeric(9,2),
  hs_espera_mes numeric(9,2),
  importe_hs_espera_unit numeric(9,2),
  importe_tercerizado numeric(9,2),
  id_tercerizadora character varying,
  CONSTRAINT pk_tratamiento_discapacidad PRIMARY KEY (id_tratamiento),
  CONSTRAINT fk_tratamiento_discapacidad_prest FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tratamiento_discapacidad
  OWNER TO postgres;
