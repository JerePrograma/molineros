CREATE TABLE inspector (
    id integer DEFAULT nextval('inspector_id_seq'::regclass) NOT NULL,
    nombre character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.inspector OWNER TO postgres;

--
ALTER TABLE ONLY inspector
    ADD CONSTRAINT pk_inspector PRIMARY KEY (id);


--
