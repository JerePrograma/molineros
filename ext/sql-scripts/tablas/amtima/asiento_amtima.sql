CREATE TABLE asiento_amtima
(
  id serial NOT NULL,
  fecha timestamp without time zone,
  descripcion character varying,
  automatico boolean,
  numero integer,
  ejercicio_desde date,
  ejercicio_hasta date,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_asiento_amtima PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE asiento_amtima
  OWNER TO postgres;
