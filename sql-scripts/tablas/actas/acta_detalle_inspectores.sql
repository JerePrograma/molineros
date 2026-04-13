CREATE TABLE acta_detalle_inspectores (
    id integer DEFAULT nextval('acta_detalle_id_seq'::regclass) NOT NULL,
    acta_id integer NOT NULL,
    capital numeric(10,2),
    desde timestamp without time zone NOT NULL,
    hasta timestamp without time zone NOT NULL,
    interes numeric(10,2)
);


ALTER TABLE public.acta_detalle_inspectores OWNER TO postgres;

--
ALTER TABLE ONLY acta_detalle_inspectores
    ADD CONSTRAINT pk_detalle_a_inspectores PRIMARY KEY (id);


--
ALTER TABLE ONLY acta_detalle_inspectores
    ADD CONSTRAINT fk_detalle_a_insp FOREIGN KEY (acta_id) REFERENCES acta(id);


--
