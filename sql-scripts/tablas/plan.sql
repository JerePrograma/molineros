CREATE TABLE plan (
    id_plan integer NOT NULL,
    descripcion character varying(100) NOT NULL,
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    peso integer
);


ALTER TABLE public.plan OWNER TO postgres;

--
ALTER TABLE ONLY plan
    ADD CONSTRAINT pk_plan PRIMARY KEY (id_plan);


--
