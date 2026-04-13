alter table emp_telefono alter column sucursal type character varying(6)

CREATE TABLE emp_telefono (
    cuit character varying(13) NOT NULL,
    sucursal character varying(6) NOT NULL,
    id_telefono integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL
);


ALTER TABLE public.emp_telefono OWNER TO postgres;

--
ALTER TABLE ONLY emp_telefono
    ADD CONSTRAINT pk_emp_telefono PRIMARY KEY (cuit, sucursal, id_telefono);


--
ALTER TABLE ONLY emp_telefono
    ADD CONSTRAINT fk_afi_telefono_telefono FOREIGN KEY (id_telefono) REFERENCES telefono(id_telefono) MATCH FULL;


--
ALTER TABLE ONLY emp_telefono
    ADD CONSTRAINT fk_empresa FOREIGN KEY (cuit, sucursal) REFERENCES empresa(cuit, sucursal);


--
