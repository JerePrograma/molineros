
CREATE TABLE boletin
(
  nombre character varying,
  asunto character varying,
  observaciones character varying,
  alta_user character varying,
  alta_fecha timestamp without time zone,
  modi_user character varying,
  modi_fecha timestamp without time zone,
  baja_user character varying,
  baja_fecha timestamp without time zone,
  id serial NOT NULL,
  CONSTRAINT pk_boletin PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE boletin
  OWNER TO postgres;
