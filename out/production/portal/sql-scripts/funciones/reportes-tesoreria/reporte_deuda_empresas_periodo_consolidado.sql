CREATE TYPE reporte_deuda_consolidado AS
   (cuit character varying,
    razon_soc character varying,
    ramo smallint,
    total_calculado numeric,
    pagado numeric,
    pagado_acta_convenio numeric,
    deuda numeric,
    calle character varying,
    numero character varying,
    piso character varying,
    dpto character varying,
    localidad character varying,
    provincia character varying,
    cod_postal character varying);

-- Function: informes.reporte_deuda_empresas_periodo_consolidado(integer)

-- DROP FUNCTION informes.reporte_deuda_empresas_periodo_consolidado(integer);

CREATE OR REPLACE FUNCTION informes.reporte_deuda_empresas_periodo_consolidado(id_reporte_p integer)
  RETURNS SETOF reporte_deuda_consolidado AS
$BODY$
BEGIN
return query
select cuit, razon_soc, ramo, sum(total_calculado), sum(pagado), 
sum(pagado_acta_convenio), sum(deuda), cast(max(calle) as varchar), cast(max(numero) as varchar), 
cast(max(piso) as varchar), cast(max(dpto) as varchar), cast(max(localidad) as varchar),
cast(max(provincia) as varchar), cast(max(cod_postal) as varchar)
from informes.reporte_deuda_empresas_periodo_det
where id_cab=id_reporte_p
group by cuit, razon_soc, ramo;



END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION informes.reporte_deuda_empresas_periodo_consolidado(integer)
  OWNER TO postgres;
