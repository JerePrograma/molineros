CREATE TABLE acta_inspector (
    id_acta integer NOT NULL,
    id_inspector integer NOT NULL
);


ALTER TABLE public.acta_inspector OWNER TO postgres;

--
ALTER TABLE ONLY acta_inspector
    ADD CONSTRAINT pf_acta_inspector PRIMARY KEY (id_acta, id_inspector);


--
ALTER TABLE ONLY acta_inspector
    ADD CONSTRAINT fk_acta_inspector_acta FOREIGN KEY (id_acta) REFERENCES acta(id) MATCH FULL;


--
ALTER TABLE ONLY acta_inspector
    ADD CONSTRAINT fk_acta_inspector_insp FOREIGN KEY (id_inspector) REFERENCES inspector(id) MATCH FULL;


--
