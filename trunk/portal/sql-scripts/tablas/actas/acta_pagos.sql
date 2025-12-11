-- Martin - 21/03/2011
alter table acta_pagos add column forma char(1);
update acta_pagos set forma = 'C' where nro_cheque is not null;
update acta_pagos set forma = 'E' where nro_cheque is null;

-------------------------------------------------------------
-- Table: acta_pagos

-- DROP TABLE acta_pagos;

CREATE TABLE acta_pagos
(
  acta_id integer NOT NULL,
  tipo character(3) NOT NULL,
  fecha_pago timestamp without time zone,
  importe numeric(10,2),
  interes numeric(10,2),
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
  acta_relacion_id integer,
  convenio_acta_id integer,
  nro_cheque numeric(15,0),
  banco_cheque integer,
  CONSTRAINT pk_acta_pagos PRIMARY KEY (id),
  CONSTRAINT fk_acta_pa_ac_rel FOREIGN KEY (acta_relacion_id)
      REFERENCES acta_relacion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_acta_pa_co_ac FOREIGN KEY (convenio_acta_id)
      REFERENCES convenio_actas (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_acta_pagos FOREIGN KEY (acta_id)
      REFERENCES acta (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_acta_pagos_cheque FOREIGN KEY (nro_cheque, banco_cheque)
      REFERENCES cheque (nro_cheque, id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_acta_pagos_rec FOREIGN KEY (recibo_id)
      REFERENCES recibo (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE acta_pagos OWNER TO postgres;
