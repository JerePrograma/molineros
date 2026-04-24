CREATE TABLE norma_ddhh (
  id integer NOT NULL DEFAULT nextval('norma_ddhh_id_seq'::regclass),
  sistema character varying(15) NOT NULL,
  id_tipo_norma_ddhh integer NOT NULL,
  numero character varying(15),
  fuente_dependencia character varying(25),
  autor character varying(25),
  fecha date,
  lugar text,
  resumen text,
  contenido text,
  id_tema_norma_ddhh integer NOT NULL,
  link character varying(100),
  sigla character varying(100),
  inc_legis_nac character varying(100), 
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  CONSTRAINT pk_norma_ddhh PRIMARY KEY (id ),
  CONSTRAINT fk_tipo_norma_ddhh FOREIGN KEY (id_tipo_norma_ddhh)
      REFERENCES tipo_normas_ddhh (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_tema_norma_ddhh FOREIGN KEY (id_tema_norma_ddhh)
      REFERENCES tema_normas_ddhh (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE);