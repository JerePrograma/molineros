


create table detalle_transferencia_externa_factura(
fecha_proceso_transf timestamp without time zone, 
codigo_organismo_transf character varying(4), 
debito_credito_transf character(1), 
numero_expediente character varying(9), 
debito_credito character(1), 
numero_factura varchar(10), 
constraint pk_det_transf_ext_fact primary key (fecha_proceso_transf, codigo_organismo_transf, debito_credito_transf, numero_expediente, debito_credito, numero_factura),
constraint fk_transferencia_ext_fact foreign key (fecha_proceso_transf, codigo_organismo_transf, debito_credito_transf, numero_expediente, debito_credito) references detalle_transferencia_externa(fecha_proceso_transf, codigo_organismo_transf, debito_credito_transf, numero_expediente, debito_credito)
)
