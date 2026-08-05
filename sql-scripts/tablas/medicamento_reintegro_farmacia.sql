-- Table: medicamento_reintegro_farmacia

alter table medicamento_reintegro_farmacia drop constraint pk_medicamento_reintegro_farmacia;
alter table medicamento_reintegro_farmacia add constraint pk_medicamento_reintegro_farmacia PRIMARY KEY (id);
alter table medicamento_reintegro_farmacia add column precio_al_publico numeric(10,2),  
alter table medicamento_reintegro_farmacia add column precio_ospim numeric(10,2),


-- Table: medicamento_reintegro_farmacia

-- DROP TABLE medicamento_reintegro_farmacia;

-- Table: medicamento_reintegro_farmacia

-- DROP TABLE medicamento_reintegro_farmacia;

CREATE TABLE medicamento_reintegro_farmacia
(
  id_reintegro integer NOT NULL,
  fecha timestamp without time zone NOT NULL,
  nro_receta integer NOT NULL,
  profesional character varying NOT NULL,
  cantidad integer NOT NULL,
  troquel integer NOT NULL,  
  cober_sss numeric(10,2) NOT NULL,
  cober_amtima numeric(10,2) NOT NULL,
  cober_ospim numeric(10,2) NOT NULL,  
  monto_ospim numeric(10,2) NOT NULL,
  monto_amtima numeric(10,2) NOT NULL,  
  precio_al_publico numeric(10,2),  
  precio_ospim numeric(10,2),  
  total_med numeric(10,2) NOT NULL,
  total_cobertura numeric(10,2) NOT NULL,
  total numeric(10,2) NOT NULL,  
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying NOT NULL,
  mod_fecha timestamp without time zone NOT NULL,
  modi_usr character varying NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying,
  id_medicamento integer,
  id serial NOT NULL,
  
  
  CONSTRAINT pk_medicamento_reintegro_farmacia PRIMARY KEY (id),
  CONSTRAINT fk_medicamento FOREIGN KEY (id_medicamento)
      REFERENCES medicamentos (id_medicamento) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_reintegro_farmacia FOREIGN KEY (id_reintegro)
      REFERENCES reintegro_farmacia (id_reintegro) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE medicamento_reintegro_farmacia OWNER TO postgres;
