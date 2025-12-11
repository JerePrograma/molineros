UPDATE compro_tipo SET secuencia = 1 where compro_tipo = 'NDB';

insert into compro_tipo (compro_tipo,
    observaciones,
	secuencia)
	values ('NDM', 'NOTAS DEBITO MANUALES', 99)

insert into compro_tipo (compro_tipo,
    observaciones,
	secuencia)
	values ('NDI', 'NOTAS DEBITO INTERNA', 1)
	
CREATE TABLE compro_tipo (    
    compro_tipo character varying(3) NOT NULL,
    observaciones character varying(250),
	secuencia integer not null	
);

ALTER TABLE public.compro_tipo OWNER TO postgres;

--
ALTER TABLE ONLY compro_tipo
    ADD CONSTRAINT pk_compro_tipo PRIMARY KEY (compro_tipo);