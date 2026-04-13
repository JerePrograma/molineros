CREATE TABLE cheque_amtima
(
  nro_cheque numeric(15,0) NOT NULL,
  cuit character varying(13),
  a_nombre_de character varying(250),
  fecha timestamp without time zone NOT NULL,
  importe numeric(10,2) NOT NULL,
  concepto character varying(700),
  id_cta_bcria integer,
  debito_credito character(1) NOT NULL,
  id_estado integer NOT NULL,
  id_banco integer NOT NULL,
  prestador boolean,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_cheque_amtima PRIMARY KEY (nro_cheque, id_banco),
  CONSTRAINT fk_cheque_banco FOREIGN KEY (id_banco)
      REFERENCES banco (id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cheque_amtima_estado FOREIGN KEY (id_estado)
      REFERENCES cheque_amtima_estado (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cta_bcria FOREIGN KEY (id_cta_bcria)
      REFERENCES cuenta_bcria (id_cuenta_bcria) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cheque OWNER TO postgres;










--////////////////////////////////////////////////////////////////////////////////////
insert into cheque_amtima
select   
  nro_cheque ,
  cuitcuil ,
  a_favor_de ,
  fecha ,
  op.importe - round((op.importe -.05) *op.descuento /100,2 ) -op.descuento_por_drogueria  ,
  concepto ,
  null,
  'D',
  1,
  1, --banco
  false,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  baja_fecha ,
  baja_usr 
from orden_pago_amtima  op 
where nro_cheque not in (select nro_cheque from orden_pago_amtima  op group by nro_cheque having count(*)>1)
order by nro_cheque asc , alta_fecha asc;

insert into cheque_amtima
select   
  nro_cheque ,
  cuitcuil ,
  a_favor_de ,
  fecha ,
  op.importe - round((op.importe -.05) *op.descuento /100,2 ) -op.descuento_por_drogueria  ,
  concepto ,
  null,
  'D',
  1,
  1, --banco
  false,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  baja_fecha ,
  baja_usr 
from orden_pago_amtima  op 
where id_orden_pago in (select max (id_orden_pago) from orden_pago_amtima  op group by nro_cheque having count(*)>1)
order by nro_cheque asc , alta_fecha asc;

insert into orden_pago_amtima_pagos(id_orden_pago,  nro_cheque ,  id_banco_cheque )
select  id_orden_pago,
  nro_cheque ,
  1 --banco
from orden_pago_amtima  op;