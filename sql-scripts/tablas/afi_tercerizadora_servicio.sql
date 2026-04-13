//---- 10/01/2011

ALTER TABLE ONLY afi_tercerizadora_servicio
    drop CONSTRAINT pk_afi_tercerizadora ; 

ALTER TABLE ONLY afi_tercerizadora_servicio
    ADD CONSTRAINT pk_afi_tercerizadora PRIMARY KEY (cuil_titular, inte, id_tercerizadora, fecha_inicio_pres);
    
    
//---------------------------------------------------------------    
    

CREATE TABLE afi_tercerizadora_servicio (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    id_tercerizadora character varying(3) NOT NULL,
    fecha_inicio_pres date,
    fecha_fin_pres date,
    alta_fecha timestamp with time zone,
    modi_fecha timestamp with time zone,
    baja_fecha timestamp with time zone,
    alta_usr character(50),
    modi_usr character(50),
    baja_usr character(50)
);


ALTER TABLE public.afi_tercerizadora_servicio OWNER TO postgres;

--
ALTER TABLE ONLY afi_tercerizadora_servicio
    ADD CONSTRAINT pk_afi_tercerizadora PRIMARY KEY (cuil_titular, inte, id_tercerizadora, fecha_inicio_pres);


--
ALTER TABLE ONLY afi_tercerizadora_servicio
    ADD CONSTRAINT fk_afi_aportes_afi FOREIGN KEY (cuil_titular, inte) REFERENCES afiliado(cuil_titular, inte) MATCH FULL;


--
ALTER TABLE ONLY afi_tercerizadora_servicio
    ADD CONSTRAINT fk_afi_tercerizadora_tercerizadora FOREIGN KEY (id_tercerizadora) REFERENCES tercerizadora_servicio(id_tercerizadora) MATCH FULL;


--
