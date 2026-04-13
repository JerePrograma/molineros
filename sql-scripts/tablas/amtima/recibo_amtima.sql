CREATE TABLE recibo_amtima
(
  id serial NOT NULL,
  numero character varying(16),
  tipo character varying(2),
  fecha date,
  cuit character varying(13),
  descripcion character varying(500),
  importe numeric(10,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  ex_id character varying(10),
  sucursal character varying(6),
  id_seccional integer,
  cuil_p character varying,
  inte_p integer,
  CONSTRAINT pk_recibo_amtima PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
