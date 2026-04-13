-- Table: acta_no_os_inspector

-- DROP TABLE acta_no_os_inspector;

CREATE TABLE acta_no_os_inspector
(
  id_acta integer NOT NULL,
  id_inspector integer NOT NULL,
  CONSTRAINT pf_acta_no_os_inspector PRIMARY KEY (id_acta , id_inspector ),
  CONSTRAINT fk_acta_inspector_acta_no_os FOREIGN KEY (id_acta)
      REFERENCES acta_no_os (id) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_acta_no_os_inspector_insp FOREIGN KEY (id_inspector)
      REFERENCES inspector (id) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE acta_no_os_inspector
  OWNER TO postgres;

