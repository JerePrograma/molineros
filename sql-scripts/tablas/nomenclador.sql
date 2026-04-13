alter table nomenclador add column importe numeric(11,2);  

update nomenclador add 

CREATE TABLE nomenclador (
    id_prestacion integer NOT NULL,
    descripcion character varying(150),
    marca_rein_liq smallint,
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    id_tipo_nomenclador integer,
    id_especialidad integer,
    importe numeric(11,2)
);


ALTER TABLE public.nomenclador OWNER TO postgres;

--
ALTER TABLE ONLY nomenclador
    ADD CONSTRAINT pk_nomenclador PRIMARY KEY (id_prestacion);


--
ALTER TABLE ONLY nomenclador
    ADD CONSTRAINT fk_tipo_nomen FOREIGN KEY (id_tipo_nomenclador) REFERENCES tipo_nomenclador(id_tipo_nomenclador) MATCH FULL;


--
alter table nomenclador add column codigo varchar (10);