-- Table: grupos_concepto

-- DROP TABLE grupos_concepto;

CREATE TABLE grupos_concepto
(
  id_grupo_concepto integer,
  id_grupo integer,
  id_concepto integer
)
WITH (
  OIDS=FALSE
);
ALTER TABLE grupos_concepto OWNER TO postgres;


alter table grupos_concepto add constraint fk_gp_conceptos foreign key (id_concepto) references concepto_maestro (id);
alter table grupos_concepto add id serial;