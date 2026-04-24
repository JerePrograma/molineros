alter table afi_situ_laboral add constraint afi_situ_laboral_cat foreign key  (id_categoria) references categoria_laboral (id_categoria)
alter table afi_situ_laboral alter column sucursal type character varying(6)

CREATE TABLE afi_situ_laboral
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  cuit character varying(13) NOT NULL,
  sucursal character varying(6) NOT NULL,
  fecha_ingre date NOT NULL,
  id_puesto integer,
  id_revista integer,
  fecha_egre date,
  modi_fecha timestamp with time zone,
  alta_fecha timestamp with time zone,
  baja_fecha timestamp with time zone,
  alta_usr character(50),
  modi_usr character(50),
  baja_usr character(50),
  id_categoria integer,
  id_motivo_baja integer,
  escala_salarial character varying,
  CONSTRAINT pk_afi_situ_laboral PRIMARY KEY (cuil_titular, inte, cuit, sucursal, fecha_ingre),
  CONSTRAINT fk_afi_situ_laboral_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT pk_motivo_baja FOREIGN KEY (id_motivo_baja)
      REFERENCES motivo_baja (id_motivo_baja) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_situ_laboral OWNER TO postgres;
