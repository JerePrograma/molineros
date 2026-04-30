-- Table: informes.reporte_deuda_empresas_periodo_det

-- DROP TABLE informes.reporte_deuda_empresas_periodo_det;

CREATE TABLE informes.reporte_deuda_empresas_periodo_det
(
  id serial NOT NULL,
  id_cab integer,
  periodo date,
  cuit character varying,
  razon_soc character varying,
  ramo smallint,
  total_afi_81 bigint,
  total_afi_765 bigint,
  total_empleados bigint,
  total_rem_81 numeric,
  total_rem_765 numeric,
  total_remuneracion numeric,
  calculado_81 numeric,
  calculado_765 numeric,
  total_calculado numeric,
  pagado numeric,
  pagado_acta_convenio numeric,
  porc_pagado numeric,
  deuda numeric,
  calle character varying,
  numero character varying,
  piso character varying,
  dpto character varying,
  localidad character varying,
  provincia character varying,
  cod_postal character varying,
  CONSTRAINT "PRIMARY KEY DEU_EMP_PERI_DET" PRIMARY KEY (id ),
  CONSTRAINT "FOREING KEY CABECERA" FOREIGN KEY (id_cab)
      REFERENCES informes.reporte_deuda_empresas_periodo_cab (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE informes.reporte_deuda_empresas_periodo_det
  OWNER TO postgres;
