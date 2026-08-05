CREATE TABLE tipo_prestador (
    id_tipo_prestador smallint NOT NULL,
    descripcion character varying(20) NOT NULL
);


ALTER TABLE public.tipo_prestador OWNER TO postgres;

--
ALTER TABLE ONLY tipo_prestador
    ADD CONSTRAINT pk_tipo_prestador PRIMARY KEY (id_tipo_prestador);


--
