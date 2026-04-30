-- Table: informes.reporte_deuda_empresas_periodo_cab

-- DROP TABLE informes.reporte_deuda_empresas_periodo_cab;

CREATE TABLE informes.reporte_deuda_empresas_periodo_cab
(
  id serial NOT NULL,
  usuario character varying(50) NOT NULL,
  fecha_solicitado timestamp without time zone,
  fecha_desde_param date,
  fecha_hasta_param date,
  ramo_desde_param integer,
  ramo_hasta_param integer,
  agrupa_x_remun_param boolean,
  empresa_sin_deuda_param boolean,
  CONSTRAINT "PRIMARY KEY" PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE informes.reporte_deuda_empresas_periodo_cab
  OWNER TO postgres;
