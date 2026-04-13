-- Table: tarjeta_acceso

-- DROP TABLE tarjeta_acceso;

CREATE TABLE tarjeta_acceso
(
  id serial NOT NULL,
  id_tarjeta_acceso integer NOT NULL,
  apellido character varying(100) NOT NULL,
  nombre character varying(100) NOT NULL,
  entidad character varying,
  legajo integer,
  horas_jornada numeric DEFAULT 8,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tarjeta_acceso
  OWNER TO postgres;
