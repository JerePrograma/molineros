alter table seccional add column imaginaria integer default 0 

INSERT INTO seccional values (
            9997, 'MIRAMAR', '', 'I', null, 
            null, null, '2011-10-25', '2011-10-25', 'admin', null, 
            '2011-10-25', 'admin', null, null, null, null, 
            1)

CREATE TABLE seccional (
    id_seccional integer NOT NULL,
    descripcion character varying(150) NOT NULL,
    cheque_a_la_orden character varying(200),
    tipo character varying(1) NOT NULL,
    id_domicilio integer,
    contacto character varying(250),
    observaciones character varying(250),
    vigen_fecha timestamp without time zone NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    alta_ip character varying(15),
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    modi_ip character varying(15),
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    baja_ip character varying(15)
);

ALTER TABLE public.seccional OWNER TO postgres;

--
ALTER TABLE ONLY seccional
    ADD CONSTRAINT pk_seccional PRIMARY KEY (id_seccional);
--