alter table detalle_discapacidad add column cie_diez character varying

drop table detalle_discapacidad

CREATE TABLE detalle_discapacidad (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
	diagnostico character varying(5000),
	dependencia boolean,
    telefono_contacto character varying(60),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
   	cie_diez character varying
);