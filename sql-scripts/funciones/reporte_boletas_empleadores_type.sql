-- Type: reporte_boletas_empleadores_type

-- DROP TYPE reporte_boletas_empleadores_type;

CREATE TYPE reporte_boletas_empleadores_type AS
   (descripcion character varying,
    cuenta_sucursal text,
    cod_sucursal_nacion integer,
    nombre_suc_nacion character varying,
    fecha_recauda date,
    periodo_cod_barras date,
    cuit character varying,
    nro_boleta_portal_emple integer,
    razon_soc character varying,
    importe numeric,
    nro_cheque numeric,
    estado_cheque text,
    nroacta character varying,
    observacion character varying);
ALTER TYPE reporte_boletas_empleadores_type
  OWNER TO postgres;
