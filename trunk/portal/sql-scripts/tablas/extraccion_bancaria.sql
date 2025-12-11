create table extraccion_bancaria (
fecha timestamp without time zone,
tipo character varying (3),
codigo_os character varying (4), 
constraint pk_extraccion_bancaria primary key (fecha, tipo, codigo_os)
)