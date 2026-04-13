create table excepcion_23201_reintegro (
  id_reintegro integer NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  cantidad_viajes_mes numeric,
  cantidad_kilometros_dia numeric,
  cantidad_kilometros_mes numeric,
  importe_kilometro_unit numeric(9,2),
  hs_espera_dia numeric,
  hs_espera_mes numeric,
  importe_hs_espera_unit numeric(9,2)
)