CREATE TABLE tipo_telefono (
    id_tipo_telefono character(1) NOT NULL,
    descripcion character varying(20) NOT NULL
);


ALTER TABLE public.tipo_telefono OWNER TO postgres;

--
ALTER TABLE ONLY tipo_telefono
    ADD CONSTRAINT pk_tipo_telefono PRIMARY KEY (id_tipo_telefono);


--
