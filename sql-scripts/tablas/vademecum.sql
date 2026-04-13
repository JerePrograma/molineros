CREATE TABLE vademecum
(
  droga character varying(100),
  nombre character varying(100),
  presentacion character varying(100),
  laboratorio character varying(100),
  accion character varying(100),
  troquel numeric,
  registro numeric NOT NULL,
  porc_ospim numeric,
  porc_amtima numeric,
  porc_sssalud numeric,
  pmoe_n numeric,
  alta_fecha timestamp without time zone,
  alta_usr character varying,
  modi_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  baja_usr character varying,
  modi_usr character varying,
  fecha timestamp without time zone NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vademecum OWNER TO postgres;