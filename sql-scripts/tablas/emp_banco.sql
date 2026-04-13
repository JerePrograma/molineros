alter table emp_banco alter column sucursal type character varying(6)

CREATE TABLE emp_banco (
    cuit character varying(13) NOT NULL,
    sucursal character varying(6) NOT NULL,
    id_banco integer NOT NULL,
    sucur_banco integer,
    calle character varying(100) NOT NULL,
    numero smallint,
    numero_cuenta character varying(25) NOT NULL
);


ALTER TABLE public.emp_banco OWNER TO postgres;

--
ALTER TABLE ONLY emp_banco
    ADD CONSTRAINT pk_emp_banco PRIMARY KEY (cuit, sucursal, id_banco);


--
ALTER TABLE ONLY emp_banco
    ADD CONSTRAINT fk_emp_banco_emp FOREIGN KEY (cuit, sucursal) REFERENCES empresa(cuit, sucursal) MATCH FULL;


--
