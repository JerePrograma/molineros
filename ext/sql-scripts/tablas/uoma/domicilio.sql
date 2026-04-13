CREATE TABLE uoma.domicilio
(
  id_domicilio integer NOT NULL DEFAULT nextval('uoma.domicilio_uoma_id_seq'::regclass),
  calle character varying(100) NOT NULL,
  piso character varying(5),
  depto character varying(4),
  oficina character varying(10),
  postal_codi character varying(4) NOT NULL,
  barrio character varying(50),
  telefono character varying(100),
  observaciones character varying(250),
  domi_val character varying(1) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  provincia integer,
  localidad integer,
  numero character varying,
  localidad_nombre character(50),
  provincia_nombre character(50),
  CONSTRAINT pk_domicilio_uoma PRIMARY KEY (id_domicilio)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE uoma.domicilio OWNER TO postgres;

