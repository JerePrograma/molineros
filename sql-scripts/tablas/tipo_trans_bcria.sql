CREATE TABLE tipo_trans_bcria (
    id_tipo_transaccion integer NOT NULL,
    descripcion character varying
);


ALTER TABLE public.tipo_trans_bcria OWNER TO postgres;

--
ALTER TABLE ONLY tipo_trans_bcria
    ADD CONSTRAINT pk_tipo_tran_bcria PRIMARY KEY (id_tipo_transaccion);


--
