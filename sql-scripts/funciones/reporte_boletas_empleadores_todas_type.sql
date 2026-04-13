CREATE TYPE reporte_boletas_empleadores_todas_type AS
   (empresa_cuit character varying,
    razon_soc character varying,
    camara character varying,
    fecha_ing text,
    categoriasalarial character varying,
    remuneracion numeric,
    aportesocialuoma character varying,
    articulo46 character varying,
    cuotaamtima character varying,
    cuotasocialuoma character varying,
    cuotausufructo character varying,
    adherenteamtima character varying,
    apellido text,
    cuil_titular character varying,
    remuneracion2 numeric,
    importenoremunerativo numeric);
ALTER TYPE reporte_boletas_empleadores_todas_type
  OWNER TO postgres;

