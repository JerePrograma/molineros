INSERT INTO motivo(
            compro_tipo, id_motivo, descripcion, observaciones, alta_fecha, 
            alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr)
    VALUES ('NDB', 2, 'Afiliado de baja', 'afiliado de baja', '02-03-2011', 
            'admin', '02-03-2011', 'admin', null, null);

            INSERT INTO motivo(
            compro_tipo, id_motivo, descripcion, observaciones, alta_fecha, 
            alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr)
    VALUES ('NDB', 2, 'Afiliado inexistente', 'Afiliado inexistente', '02-03-2011', 
            'admin', '02-03-2011', 'admin', null, null);

drop table motivo

CREATE TABLE motivo
(
  compro_tipo character varying(3) NOT NULL,
  id_motivo smallint NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_motivo PRIMARY KEY (compro_tipo, id_motivo)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE motivo OWNER TO postgres;
