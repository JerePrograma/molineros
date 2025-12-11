create TABLE correo.paquete
(
  id integer NOT NULL DEFAULT nextval('correo.paquete_id_seq'::regclass),    
  alta_fecha timestamp without time zone,
  estado character varying,
  alta_usr character varying,
  modi_fecha timestamp without time zone,
  modi_usr character varying,
  baja_fecha timestamp without time zone,
  baja_usr character varying,  
  CONSTRAINT pk_paquete PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);


