CREATE TABLE provincias_fox (
    pr_id character varying(10) NOT NULL,
    pr_desc character varying(45) NOT NULL,
    pr_cort character varying(5) NOT NULL,
    pr_codi character varying(2) NOT NULL,
    pr_domi integer DEFAULT 0 NOT NULL,
    pr_esta character varying(1) DEFAULT 'S'::character varying NOT NULL,
    usinicial character varying(15) DEFAULT ''::character varying NOT NULL,
    feinicial date NOT NULL,
    eqinicial character varying(15) DEFAULT ''::character varying NOT NULL,
    usultmodif character varying(15) DEFAULT ''::character varying NOT NULL,
    feultmodif date NOT NULL,
    equltmodif character varying(15) DEFAULT ''::character varying NOT NULL,
    usbaja character varying(15) DEFAULT ''::character varying NOT NULL,
    febaja date NOT NULL,
    eqbaja character varying(15) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.provincias_fox OWNER TO postgres;

--
ALTER TABLE ONLY provincias_fox
    ADD CONSTRAINT provincias_fox_pkey PRIMARY KEY (pr_id);


--
