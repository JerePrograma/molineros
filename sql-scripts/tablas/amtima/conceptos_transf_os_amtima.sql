
CREATE TABLE conceptos_transf_os_amtima
(
  cod_conc character varying NOT NULL,
  descripcion character varying,
  cod_contra_conc character varying,
  deb_cred character varying,
  liquidable boolean,
  CONSTRAINT conceptos_transf_os_pkey_amtima PRIMARY KEY (cod_conc)
)
WITH (
  OIDS=FALSE
);
