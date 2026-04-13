create table utilizacion_anticipos_ospim (
    id_punto_venta smallint NOT NULL,
    compro_tipo character varying(3) NOT NULL,
    compro_nro character varying(50) NOT NULL,
    cuit character(11) DEFAULT 0 NOT NULL,
    compro_letra character varying(1) not null,
    compro_sucu integer not null,
    importe_utilizado numeric(12,2),
    id_orden_pago_ospim integer,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);

ALTER TABLE ONLY utilizacion_anticipos_ospim
    add CONSTRAINT pk_utilizacion_anticipos_ospim PRIMARY KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit, id_orden_pago_ospim);

alter table only utilizacion_anticipos_ospim
 add CONSTRAINT fk_utilizacion_anticipos_ospim FOREIGN KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit)
      REFERENCES comprobante (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION;


ALTER TABLE ONLY utilizacion_anticipos_ospim
    add CONSTRAINT fk_utilizacion_anticipos_ospim_op foreign KEY (id_orden_pago_ospim) references orden_pago_ospim(id_orden_pago);
