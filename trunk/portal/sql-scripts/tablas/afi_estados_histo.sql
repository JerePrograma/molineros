CREATE TABLE afi_estados_histo
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_ospim integer,
  id_uoma integer,
  id_amtima integer,
  apellido character varying(100),
  nombre character varying(100),
  documento_tipo character varying(4),
  sexo character varying(2),
  cuil character varying(13),
  naci_fecha date,
  id_estado_civil_sss integer,
  id_parentesco_sss integer,
  ingre_fecha date,
  id_seccional integer,
  anterior_os integer,
  vigen_fecha timestamp without time zone,
  observaciones character varying(250),
  pres_ssalud_fecha date,
  alta_fecha timestamp with time zone NOT NULL,
  alta_usr character varying(50),
  modi_fecha timestamp without time zone,
  modi_usr character varying(50),
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  discapacitado character varying(1),
  docu_numero character varying(15),
  nacionalidad integer,
  aportante_titular integer,
  nro_afiliado integer,
  id_motivo_baja integer,
  id_ospim_baja_fecha timestamp without time zone,
  id_uoma_baja_fecha timestamp without time zone,
  id_amtima_baja_fecha timestamp without time zone,
  descripcion_operacion character(3),
  id serial NOT NULL,
  CONSTRAINT pk_afi_estados_histo PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_estados_histo OWNER TO postgres;

-- Index: cuil_inte_fecha_idx

-- DROP INDEX cuil_inte_fecha_idx;

CREATE INDEX cuil_inte_fecha_idx
  ON afi_estados_histo
  USING btree
  (cuil_titular, inte, alta_fecha);

