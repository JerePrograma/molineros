CREATE TABLE nomenclador_conceptos_amtima
(
  id_prestacion integer,
  codigo character varying(100),
  descripcion character varying(100),
  valido_desde date,
  valido_hasta date,
  id serial NOT NULL,
  tipo_id integer,
  concepto_id integer,
  modi_usr character varying,
  modi_fecha date,
  alta_usr character varying,
  alta_fecha date,
  CONSTRAINT pk_nomenclador_conceptos_amtima PRIMARY KEY (id),
  CONSTRAINT fk_nc_concepto_amtima FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_nc_tipo_amtima FOREIGN KEY (tipo_id)
      REFERENCES nomenclador_concepto_tipo_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_nomenc_conceptos_prst_amtima FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador_amtima (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
