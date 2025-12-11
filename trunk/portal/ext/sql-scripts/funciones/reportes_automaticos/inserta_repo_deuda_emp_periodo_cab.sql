CREATE OR REPLACE FUNCTION inserta_repo_deuda_emp_periodo(usuario_p character varying, fecha_solicitado_p date, id_repo_autom_p integer, fecha_desde_param_p date,
					fecha_hasta_param_p date, ramo_desde_param_p integer, ramo_hasta_param_p integer, agrupa_x_remun_param_p boolean,
					empresa_sin_deuda_param_p boolean) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
 
INSERT INTO informes.reporte_deuda_empresas_periodo_cab(usuario, fecha_solicitado, id_repo_autom, fecha_desde_param, fecha_hasta_param, 
					ramo_desde_param, ramo_hasta_param, agrupa_x_remun_param, empresa_sin_deuda_param)
    VALUES (usuario_p, fecha_solicitado_p, id_repo_autom_p, fecha_desde_param_p, fecha_hasta_param_p, ramo_desde_param_p, ramo_hasta_param_p, 
    				agrupa_x_remun_param_p, empresa_sin_deuda_param_p);
            
  return currval('reporte_deuda_empresas_periodo_cab_id_seq');
  end;  
$BODY$;
