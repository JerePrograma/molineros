create table excepcion_23201_liquidacion (
  id_liquidacion integer NOT NULL,
  orden integer not null,
  cantidad_viajes_mes numeric,
  cantidad_kilometros_dia numeric,
  cantidad_kilometros_mes numeric,
  importe_kilometro_unit numeric(9,2),
  hs_espera_dia numeric,
  hs_espera_mes numeric,
  importe_hs_espera_unit numeric(9,2)
)