CREATE TABLE afiliado (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    id_ospim integer,
    id_uoma integer,
    id_amtima integer,
    apellido character varying(100) NOT NULL,
    nombre character varying(100) NOT NULL,
    documento_tipo character varying(4),
    sexo character varying(2) NOT NULL,
    cuil character varying(13),
    naci_fecha date NOT NULL,
    civil_esta character varying(20) NOT NULL,
    parentesco character varying(100),
    ingre_fecha date NOT NULL,
    id_seccional integer,
    anterior_os integer,
    vigen_fecha timestamp without time zone NOT NULL,
    observaciones character varying(250),
    pres_ssalud_fecha date,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
    discapacitado character varying(1),
    docu_numero character varying(15),
    nacionalidad integer,
    aportante_titular integer DEFAULT 0,
    nro_afiliado integer,
    id_motivo_baja integer,
    id_ospim_baja_fecha timestamp without time zone,
    id_uoma_baja_fecha timestamp without time zone,
    id_amtima_baja_fecha timestamp without time zone
);


ALTER TABLE public.afiliado OWNER TO postgres;

--
ALTER TABLE ONLY afiliado
    ADD CONSTRAINT pk_afiliado PRIMARY KEY (cuil_titular, inte);


--
ALTER TABLE ONLY afiliado
    ADD CONSTRAINT fk_afiliado_seccional FOREIGN KEY (id_seccional) REFERENCES seccional(id_seccional);


--
ALTER TABLE ONLY afiliado
    ADD CONSTRAINT fk_id_motivo_baja FOREIGN KEY (id_motivo_baja) REFERENCES motivo_baja(id_motivo_baja);


--
