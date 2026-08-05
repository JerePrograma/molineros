create table recibo_conceptos_pagos(
	recibo_concepto_id integer,
	recibo_ingreso_id integer,
	importe numeric(12,2), 
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
    CONSTRAINT fk_recibo_conceptos FOREIGN KEY (recibo_concepto_id) 
    references recibo_conceptos (id) MATCH SIMPLE 
    ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_recibo_ingresos FOREIGN KEY (recibo_ingreso_id)
    REFERENCES recibo_ingresos (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

