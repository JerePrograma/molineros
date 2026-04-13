alter table detalle_cuota ALTER plan_tratamiento type character varying (400)
alter table detalle_cuota ALTER informe type character varying (5000)

alter table detalle_cuota drop column fecha
alter table detalle_cuota drop column periodo
alter table detalle_cuota add column fecha timestamp without time zone;
alter table detalle_cuota add column periodo timestamp without time zone;

periodo timestamp without time zone NOT NULL,

 column ci type varchar(25);


drop table detalle_cuota

CREATE TABLE detalle_cuota (
    id_cuota integer DEFAULT nextval('reintegro_ort_id_seq'::regclass) NOT NULL,  --id del reintegro, se actualiza cuando se audita la cuota, es ahí cuando la cuota se convierte en reintegro.
    id_reintegro integer NOT NULL, --id del reintegro al que pertenece la cuota.
    nro_cuota smallint NOT NULL, --1, 2, ó 3
    fecha character varying(15),
    periodo character varying(15),
    porcentaje smallint,
    importe numeric(9,2),
    diagnostico character varying(700),
    plan_tratamiento character varying(700),
    tiempo_estimado character varying(700),
    pronostico character varying(700),
    informe character varying(5000),
    compro_a_debitar_tipo character varying(3),
    compro_a_debitar_numero character varying(15),
    estado integer
);


ALTER TABLE public.detalle_cuota OWNER TO postgres;
--
ALTER TABLE ONLY detalle_cuota
    ADD CONSTRAINT pk_detalle_cuota PRIMARY KEY (id_reintegro, nro_cuota);

ALTER TABLE ONLY detalle_cuota
    ADD CONSTRAINT fk_detalle_cuota_reintegro FOREIGN KEY (id_reintegro) REFERENCES reintegro(id_reintegro) MATCH FULL;
--