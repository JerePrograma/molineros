CREATE OR REPLACE FUNCTION inserta_repo_autom(titulo_p character varying, stored_procedure_p character varying, csv_parameteres_p character varying,
									hora_p integer, diario_p boolean, incluir_fin_de_semana_p boolean, dia_de_la_semana_p integer, dia_del_mes_p integer,
									fecha_unica_vez_p timestamp without time zone, mails_destino_p character varying, ultima_ejecucion_p timestamp without time zone, 
									difusion_p integer, base_p integer, java_p character varying ) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
 
INSERT INTO reportes_automaticos(titulo, stored_procedure, csv_parameteres, hora, diario, incluir_fin_de_semana, dia_de_la_semana, dia_del_mes, 
							fecha_unica_vez, mails_destino, ultima_ejecucion, difusion, base, java)
    VALUES (titulo_p, stored_procedure_p, csv_parameteres_p, hora_p, diario_p, incluir_fin_de_semana_p, dia_de_la_semana_p, dia_del_mes_p,
    		fecha_unica_vez_p, mails_destino_p, ultima_ejecucion_p, difusion_p, base_p, java_p);
            
  return currval('reportes_automaticos_id_seq');
  end;  
$BODY$;
