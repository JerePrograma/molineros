alter table orden_pago_amtima add column cuit_acreedor  character varying(13) ;
alter table orden_pago_amtima add column sucu_acreedor character varying(4);
alter table orden_pago_amtima add column observaciones character varying(700);
update orden_pago_amtima  set cuit_acreedor = cuitcuil;
update orden_pago_amtima set observaciones = concepto;
alter table orden_pago_amtima  drop cuitcuil;
alter table orden_pago_amtima  drop column concepto;
alter table orden_pago_amtima drop column compro_sucu;
alter table orden_pago_amtima drop column compro_nro;
alter table orden_pago_amtima drop column  cuit;
alter table orden_pago_amtima drop column a_favor_de;
alter table orden_pago_amtima drop COLUMN alta_ip ;
alter table orden_pago_amtima drop COLUMN modi_ip ;
alter table orden_pago_amtima drop COLUMN baja_ip ;

insert into orden_pago_amtima_pagos (id_orden_pago, nro_cheque, id_banco_cheque)
select id_orden_pago, nro_cheque, 1 from orden_pago_amtima  
alter table orden_pago_amtima drop COLUMN nro_cheque ;

drop sequence orden_pago_amtima_id_seq cascade;
    
CREATE TABLE orden_pago_amtima (
    id_orden_pago integer,
    nro_cheque numeric(11,0) NOT NULL,
    importe numeric(10,2) NOT NULL,
    fecha timestamp without time zone NOT NULL,
    fecha_desde timestamp without time zone NOT NULL,
    fecha_hasta timestamp without time zone NOT NULL,
    descuento numeric(10,2) NOT NULL,
    descuento_por_drogueria numeric(10,2) NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    afiliado_razon_social character varying(200),
    id_seccional integer
);


ALTER TABLE public.orden_pago_amtima OWNER TO postgres;

--
ALTER TABLE ONLY orden_pago_amtima
    ADD CONSTRAINT pk_op_amtima PRIMARY KEY (id_orden_pago);

--
