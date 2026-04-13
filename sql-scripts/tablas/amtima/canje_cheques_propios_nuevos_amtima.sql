CREATE TABLE canje_cheques_propios_nuevos_amtima
(
  canje_id integer NOT NULL,
  nro_cheque numeric(15,0) NOT NULL,
  id_banco integer NOT NULL,
  CONSTRAINT pk_canje_cheques_propios_nuevos_amtima PRIMARY KEY (canje_id, nro_cheque, id_banco),
  CONSTRAINT fk_ccp_n_amtima FOREIGN KEY (canje_id)
      REFERENCES canje_cheques_propios_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ccp_n_ch_amtima FOREIGN KEY (nro_cheque, id_banco)
      REFERENCES cheque_amtima (nro_cheque, id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
