alter table plan_cuentas add modi_usr character varying;
alter table plan_cuentas add modi_fecha date;
alter table plan_cuentas add alta_usr character varying;
alter table plan_cuentas add alta_fecha date;

insert into plan_cuentas(cuenta, imputable, tipo, valido_desde, valido_hasta, id_cuenta_maestro, numero)
select cuenta, imputable, tipo, '18000101', '29990101', id ,numero from plan_cuentas_maestro;

-- Table: plan_cuentas

-- DROP TABLE plan_cuentas;

CREATE TABLE plan_cuentas
(
  cuenta character varying(100),
  imputable boolean,
  tipo character varying(10),
  valido_desde date,
  valido_hasta date,
  id serial NOT NULL,
  id_cuenta_maestro int,
  numero character varying(20),
  CONSTRAINT pk_plan_cuenta PRIMARY KEY (id),
  constraint fk_plan_cuentas_pc_maestro foreign key (id_cuenta_maestro) references plan_cuentas_maestro (id)
)
WITH (
  OIDS=FALSE
);
