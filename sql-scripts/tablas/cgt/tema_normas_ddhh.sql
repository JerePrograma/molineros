CREATE TABLE tema_normas_ddhh
(
  id integer NOT NULL DEFAULT nextval('tema_normas_id_seq'::regclass),
  descripcion character varying(25) NOT NULL,
  CONSTRAINT pk_tema_normas_ddhh PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);