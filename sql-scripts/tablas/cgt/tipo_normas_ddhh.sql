CREATE TABLE tipo_normas_ddhh
(
  id integer NOT NULL DEFAULT nextval('tipo_normas_id_seq'::regclass),
  sistema character varying(15) NOT NULL,
  descripcion character varying(25) NOT NULL,
  CONSTRAINT pk_tipo_normas_ddhh PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);