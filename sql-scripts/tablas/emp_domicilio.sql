alter table emp_domicilio alter column sucursal type character varying(6)

CREATE TABLE emp_domicilio (
    cuit character varying(13) NOT NULL,
    sucursal character varying(6) NOT NULL,
    id_domicilio integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL,
    baja_fecha timestamp without time zone,
    domi_tipo character varying(1) DEFAULT 'E'::character varying NOT NULL
);


ALTER TABLE public.emp_domicilio OWNER TO postgres;

--
ALTER TABLE ONLY emp_domicilio
    ADD CONSTRAINT pk_emp_domicilio PRIMARY KEY (cuit, sucursal, id_domicilio);


--
ALTER TABLE ONLY emp_domicilio
    ADD CONSTRAINT fk_emp_domicilio_domicilio FOREIGN KEY (id_domicilio) REFERENCES domicilio(id_domicilio) MATCH FULL;


--
ALTER TABLE ONLY emp_domicilio
    ADD CONSTRAINT fk_empresa FOREIGN KEY (cuit, sucursal) REFERENCES empresa(cuit, sucursal);


--
