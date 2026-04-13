alter table medicamentos drop column pmoe
alter table medicamentos drop column cober_ospim
alter table medicamentos drop column cober_amtima
alter table medicamentos drop column precio_ospim,
alter table medicamentos drop column vademecum_amtima

CREATE TABLE medicamentos
(
  troquel numeric,
  nombre character varying,
  presentacion character varying,
  monto_ioma numeric,
  norma_ioma character(1),
  cober_ioma character(1),
  laboratorio character varying,
  precio numeric,
  fecha timestamp without time zone NOT NULL,
  controlado character(1),
  importado character(1),
  tipo_venta character(1),
  iva character(1),
  cod_dto_pami character(1),
  cod_lab integer,
  nro_registro numeric NOT NULL,
  baja character(1),
  cod_barra character varying,
  unidades integer,
  tamanio character(1),
  heladera character(1),
  sifar character(1),
  baja_especial character(1),
  accion character varying,  
  droga character varying,
  id_medicamento serial NOT NULL,
  baja_fecha timestamp without time zone,
  alta_fecha timestamp without time zone
  CONSTRAINT pk_medicamento PRIMARY KEY (id_medicamento),
  CONSTRAINT const_unique_fecha_registro UNIQUE (nro_registro, fecha)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE medicamentos OWNER TO postgres;
