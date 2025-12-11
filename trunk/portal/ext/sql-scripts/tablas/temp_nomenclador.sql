CREATE TABLE temp_nomenclador (
    tipo character varying(150),
    tipo_nomenclador character varying(2),
    desde character varying(150),
    hasta character varying(150),
    gastos character varying(150),
    honorarios character varying(250)
);


ALTER TABLE public.temp_nomenclador OWNER TO postgres;

--
