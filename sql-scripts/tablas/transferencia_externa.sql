create table transferencia_externa(
fecha_proceso timestamp without time zone, 
codigo_organismo  character varying(4),
debito_credito character(1),
constraint pk_transf_ext primary key (fecha_proceso, codigo_organismo, debito_credito)
)