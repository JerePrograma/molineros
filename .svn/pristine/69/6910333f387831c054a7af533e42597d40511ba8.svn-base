CREATE TABLE amtima_aportes
(
  ente numeric,
  suc_nacion numeric,
  suc_bcra numeric,
  fecha_recauda date NOT NULL,
  fecha_rendicion date,
  cod_movimiento character varying,
  nro_movimiento numeric NOT NULL,
  importe numeric(13,2),
  moneda character varying,
  cod_barras character varying,
  banco_cheque numeric,
  sucursal_cheque numeric,
  nro_cheque numeric,
  estado_cheque character varying,
  cuit character varying,
  periodo_cod_barras date,
  nro_dec_portal_emple integer,
  nro_boleta_portal_emple integer,
  tipo_boleta integer,
  CONSTRAINT "PK_uoma_aportes" PRIMARY KEY (fecha_recauda , nro_movimiento )
)
