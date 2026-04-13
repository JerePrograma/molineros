create TABLE correo.lista_paquete
(
  id integer NOT NULL DEFAULT nextval('correo.lista_paquete_id_seq'::regclass)	,
  id_paquete integer,
  id_item_correspondencia integer,
  alta_fecha timestamp without time zone,
  alta_usr character varying,
  modi_fecha timestamp without time zone,
  modi_usr character varying,
  baja_fecha timestamp without time zone,
  baja_usr character varying,  
  CONSTRAINT pk_lista_paquete PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);