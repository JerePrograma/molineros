CREATE TABLE organismo
(
  id_organismo integer NOT NULL DEFAULT nextval('organismo_id_seq'::regclass),
  denominacion character varying,
  observaciones character varying,
  alta_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  alta_usr character varying,
  modi_usr character varying,
  baja_usr character varying,
  ambito character varying,
  telefono character varying,
  web character varying,
  sigla character varying,
  orbita character varying,
  CONSTRAINT pk_organismo PRIMARY KEY (id_organismo )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organismo
  OWNER TO postgres;

