CREATE TABLE documento_faltante_tratamiento (
    id_tratamiento integer NOT NULL,
    id_documento integer NOT NULL,    
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL
);
