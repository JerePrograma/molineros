CREATE TABLE lista_reintegro_farmacia_reporte_detalle
(
id_lista_reintegro_farmacia_reporte integer NOT NULL,
id_reintegro integer NOT NULL,
importe numeric(10,2),
tipo_reintegro character varying

--CONSTRAINT pk_lista_reintegro_reporte_d PRIMARY KEY (id_lista_reintegro_reporte, id_reintegro),
)
WITH (
OIDS=FALSE
);

