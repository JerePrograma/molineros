alter table orden_pago_ospim alter column sucu_acreedor type character varying(6)

--Martin 26/05/2011

alter table orden_pago_ospim add column observaciones character varying(700);
alter table orden_pago_ospim add column cuit_acreedor  character varying(13) ;
alter table orden_pago_ospim add column sucu_acreedor character varying(4);
alter table orden_pago_ospim add constraint fk_empre_acreedor foreign key (cuit_acreedor,sucu_acreedor) references empresa(cuit,sucursal);

alter table orden_pago_ospim alter a_favor_de drop not null;
alter table orden_pago_ospim alter fecha drop not null;
-- Martin  - 30/03/2011
alter table orden_pago_ospim add forma_pago_total_debitos character varying(20);


--

CREATE TABLE orden_pago_ospim
(
  id_orden_pago integer,
  importe numeric(12,2) NOT NULL,
  prestador boolean,
  cuit_acreedor  character varying(13),
  sucu_acreedor character varying(6),
  id_seccional integer,
  observaciones character varying(700),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),

  a_favor_de character varying(250) NOT NULL,
  fecha timestamp without time zone NOT NULL,
  concepto character varying(700),
  cuitcuil character varying(11),
  afiliado_razon_social character varying(200),


  CONSTRAINT pk_id_orden_pago PRIMARY KEY (id_orden_pago)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE orden_pago_ospim OWNER TO postgres;
