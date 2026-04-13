-- Type: result_reporte_deuda_empresas_periodo

-- DROP TYPE result_reporte_deuda_empresas_periodo;

CREATE TYPE result_reporte_deuda_empresas_periodo AS
   (periodo date,
    cuit character varying,
    razon_soc character varying,
    ramo smallint,
    total_afi_81 bigint,
    total_afi_765 bigint,
    total_empleados bigint,
    total_rem_81 numeric,
    total_rem_765 numeric,
    total_remuneracion numeric,
    calculado_81 numeric,
    calculado_765 numeric,
    total_calculado numeric,
    pagado numeric,
    pagado_acta_convenio numeric,
    porc_pagado numeric,
    deuda numeric,
    calle character varying,
    numero character varying,
    piso character varying,
    dpto character varying,
    localidad character varying,
    provincia character varying,
    cod_postal character varying);
    
-- Function: informes.reporte_deuda_empresas_periodo_detalle(integer)

-- DROP FUNCTION informes.reporte_deuda_empresas_periodo_detalle(integer);

CREATE OR REPLACE FUNCTION informes.reporte_deuda_empresas_periodo_detalle(id_reporte_p integer)
  RETURNS SETOF result_reporte_deuda_empresas_periodo AS
$BODY$
BEGIN
return query
select cast(periodo as date),
       cuit, 
       razon_soc, 
       ramo, 
       total_afi_81, 
       total_afi_765, 
       total_empleados,       
       total_rem_81, 
       total_rem_765, 
       total_remuneracion, 
       calculado_81, 
       calculado_765, 
       total_calculado, 
       pagado,       
       pagado_acta_convenio,
       porc_pagado,
       deuda,
       calle,
       numero,
       piso,
       dpto,
       localidad,
       provincia,
       cod_postal
from informes.reporte_deuda_empresas_periodo_det
where id_cab=id_reporte_p;




END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION informes.reporte_deuda_empresas_periodo_detalle(integer)
  OWNER TO postgres;
