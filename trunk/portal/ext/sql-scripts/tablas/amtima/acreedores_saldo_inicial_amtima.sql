CREATE TABLE acreedores_saldo_inicial_amtima
(
  cuit_acreedor character varying(13),
  sucu_acreedor character varying(5),
  seccional integer,
  fecha_inicio_ejercicio date,
  saldo numeric(15,2),
  CONSTRAINT fk_acreedores_saldo_inicial_empre_amtima FOREIGN KEY (cuit_acreedor, sucu_acreedor)
      REFERENCES empresa (cuit, sucursal) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_acreedores_saldo_inicial_secc_amtima FOREIGN KEY (seccional)
      REFERENCES seccional (id_seccional) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE acreedores_saldo_inicial_amtima
  OWNER TO postgres;
