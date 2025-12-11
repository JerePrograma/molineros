create type result_trae_empleadores as (cuit varchar, sucursal varchar, id_ramo_empresa smallint, descripcion varchar)

CREATE OR REPLACE FUNCTION trae_empleadores_op_filtro(cuit_p character varying, descripcion_p character varying, p_sucu character varying, id_prestador_p integer)
  RETURNS SETOF result_trae_empleadores AS
$BODY$
    DECLARE _razon varchar;
    DECLARE cuit_v varchar;
begin
      
      if(cuit_p is null and id_prestador_p is not null) then         
         cuit_v=cuit from prestador where id_prestador=id_prestador_p;
      else
         cuit_v=cuit_p;  
      end if;
            
      return query	
      select distinct op.cuit_acreedor , op.sucu_acreedor, e.id_ramo_empresa, e.razon_soc    
      from comprobante op--orden_pago_ospim op
      left outer join empresa e
      on op.cuit_acreedor=e.cuit
      and op.sucu_acreedor=e.sucursal
      where (cuit_v is null or (cuit_v is not null and op.cuit_acreedor=cuit_v))
      and (p_sucu is null or (p_sucu is not null and op.sucu_acreedor=p_sucu))
      and (descripcion_p is null or (descripcion_p is not null and upper(e.razon_soc) like '%'||upper(descripcion_p)||'%'));

end;
$BODY$
  LANGUAGE plpgsql VOLATILE

