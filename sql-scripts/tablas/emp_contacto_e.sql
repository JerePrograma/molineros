alter table emp_contacto_e alter column sucursal type character varying(6)

CREATE TABLE emp_contacto_e (
    cuit character varying(13) NOT NULL,
    sucursal character varying(6) NOT NULL,
    id_contacto_e integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL
);


ALTER TABLE public.emp_contacto_e OWNER TO postgres;

--
ALTER TABLE ONLY emp_contacto_e
    ADD CONSTRAINT pk_emp_contacto_e PRIMARY KEY (cuit, sucursal, id_contacto_e);


--
ALTER TABLE ONLY emp_contacto_e
    ADD CONSTRAINT fk_emp_contacto_e_contacto_e FOREIGN KEY (id_contacto_e) REFERENCES contacto_e(id_contacto_e) MATCH FULL;


--
ALTER TABLE ONLY emp_contacto_e
    ADD CONSTRAINT fk_empresa FOREIGN KEY (cuit, sucursal) REFERENCES empresa(cuit, sucursal);


--
