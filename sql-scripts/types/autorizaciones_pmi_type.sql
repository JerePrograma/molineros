CREATE TYPE autorizaciones.autorizaciones_pmi_type AS
   (id_autorizacion_pmi integer,
    tipo_receta character varying,
    fecha date,
    cuil_titular character varying,
    inte integer,
    alta_fecha timestamp without time zone,
    alta_usr character varying,
    nro_receta bigint,
    observaciones character varying);
ALTER TYPE autorizaciones.autorizaciones_pmi_type
  OWNER TO postgres;
