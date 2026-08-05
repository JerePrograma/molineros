create table detalle_extraccion_bancaria (
fecha timestamp without time zone, 
tipo character varying (3),
codigo_os_header character varying(4),
codigo_os character varying (4), 
debito_credito char(1), 
codigo_movimiento character varying (2), 
importe numeric (13,2),
importe_rechazado numeric(13,2),
constraint fk_detalle_Ext_bcria foreign key (fecha, tipo, codigo_os_header) references extraccion_bancaria (fecha, tipo, codigo_os)
)