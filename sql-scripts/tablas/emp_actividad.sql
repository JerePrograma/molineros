alter table emp_actividad alter column sucursal type character varying(6) 

CREATE TABLE emp_actividad (
    cuit character varying(13) NOT NULL,
    sucursal character varying(6) NOT NULL,
    id_actividad integer NOT NULL,
    tipo_actividad character varying(25) NOT NULL,
    fecha_ini timestamp without time zone
);


ALTER TABLE public.emp_actividad OWNER TO postgres;

--
ALTER TABLE ONLY emp_actividad
    ADD CONSTRAINT pk_emp_actividad PRIMARY KEY (cuit, sucursal, id_actividad);


--
ALTER TABLE ONLY emp_actividad
    ADD CONSTRAINT fk_emp_actividad_actividad FOREIGN KEY (id_actividad) REFERENCES actividad(id_actividad) MATCH FULL;


--
