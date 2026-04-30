-- Table: convenio_no_os_pagos

-- DROP TABLE convenio_no_os_pagos;

CREATE TABLE convenio_no_os_pagos
(
  convenio_id integer NOT NULL,
  cuota_id integer NOT NULL,
  tipo character(3) NOT NULL,
  fecha_pago timestamp without time zone,
  importe numeric(10,2),
  recibo_id integer,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  id serial NOT NULL,
  convenio_relacion_id integer,
  nro_cheque numeric(15,0),
  banco_cheque integer,
  interes numeric(10,2),
  pendiente_derivar numeric,
  CONSTRAINT pk_convenio_no_os_pagos PRIMARY KEY (id ),
  CONSTRAINT fk_convenio_no_os_pagos FOREIGN KEY (convenio_id)
      REFERENCES convenio_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_convenio_no_os_pagos_cheque FOREIGN KEY (nro_cheque, banco_cheque)
      REFERENCES cheque (nro_cheque, id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_convenio_no_os_pagos_conv_rel FOREIGN KEY (convenio_relacion_id)
      REFERENCES convenio_no_os_relacion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_convenio_no_os_pagos_rec FOREIGN KEY (recibo_id)
      REFERENCES recibo (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE convenio_no_os_pagos
  OWNER TO postgres;

