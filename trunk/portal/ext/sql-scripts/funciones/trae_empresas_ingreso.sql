create type result_trae_empleadores as (cuit varchar, sucursal varchar, id_ramo_empresa smallint, descripcion varchar)

CREATE OR REPLACE FUNCTION trae_empresas_ingreso(cuit_p character varying, p_sucu character varying, descripcion_p character varying)
  RETURNS SETOF result_trae_empleadores AS
$BODY$
begin
      
           
      return query	
      select distinct 
      r.cuit , r.sucursal,e.id_ramo_empresa, e.razon_soc    
      from recibo r
      left outer join empresa e
      on r.cuit =e.cuit
      and r.sucursal=e.sucursal
      where (cuit_p is null or (cuit_p is not null and r.cuit = cuit_p))
      and (p_sucu is null or (p_sucu is not null and r.sucursal=p_sucu))
      and (descripcion_p is null or (descripcion_p is not null and upper(e.razon_soc) like '%'||upper(descripcion_p)||'%'));
end;
$BODY$
  LANGUAGE plpgsql VOLATILE

