alter table lista_reintegro_reporte_detalle add column tipo_reintegro character varying
alter table lista_reintegro_reporte_detalle drop constraint pk_lista_reintegro_reporte_d
alter table lista_reintegro_reporte_detalle drop constraint fk_lista_reintegro_reporte_d_r

CREATE TABLE lista_reintegro_reporte_detalle
(
id_lista_reintegro_reporte integer NOT NULL,
id_reintegro integer NOT NULL,
importe numeric(10,2),
tipo_reintegro character varying

--CONSTRAINT pk_lista_reintegro_reporte_d PRIMARY KEY (id_lista_reintegro_reporte, id_reintegro),
CONSTRAINT fk_lista_reintegro_reporte_d_l FOREIGN KEY (id_lista_reintegro_reporte)
REFERENCES lista_reintegro_reporte (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION,
--CONSTRAINT fk_lista_reintegro_reporte_d_r FOREIGN KEY (id_reintegro)
--REFERENCES reintegro (id_reintegro) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE lista_reintegro_reporte_detalle OWNER TO postgres;
