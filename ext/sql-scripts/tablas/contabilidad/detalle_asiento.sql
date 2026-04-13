
create table detalle_asiento (
id serial,
asiento_id integer,
pase integer,
id_plan_cuentas integer,
comprobante character varying,
debe numeric(12,2),
haber numeric(12,2),
observaciones character varying,
constraint pk_detalle_asiento primary key (id),
constraint fk_detalle_asiento_a foreign key (asiento_id) references asiento(id) )

alter table detalle_asiento add constraint fk_detalle_asiento_plan_cuentas foreign key (id_plan_cuentas) references plan_cuentas_maestro(id);