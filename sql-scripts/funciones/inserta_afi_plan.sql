CREATE SEQUENCE afi_plan_id_sequence;
ALTER TABLE afi_plan ALTER COLUMN id SET DEFAULT nextval('afi_plan_id_sequence');

SELECT setval('public.afi_plan_id_sequence', 9999, true); --- 9999 se debe calcular con el " select max(id)+1 from afi_plan; "

----------------------

CREATE OR REPLACE FUNCTION inserta_afi_plan(cuil_titular_p character varying, inte_p integer, vigen_desde_p timestamp without time zone, vigen_hasta_p date, motivo_baja_p integer, id_plan_p integer, id_plan_omint_p integer, usr_p character varying)
  RETURNS bigint AS
$BODY$
declare ya_informo_baja_a_la_sss boolean;
declare plan_anterior_molinero boolean;
declare plan_actual_molinero boolean;
  begin
  ya_informo_baja_a_la_sss = fecha_baja_super is not null from afiliado where cuil_titular=cuil_titular_p and inte=inte_p;

if ya_informo_baja_a_la_sss then
    plan_anterior_molinero = ospim from plan p, afi_plan ap
                      where p.id_plan=ap.id_plan
                      and ap.id = (select max(ap_.id) from afi_plan ap_ where ap_.cuil_titular=cuil_titular_p
                      and ap_.inte=inte_p and ap_.baja_fecha is null)
                      and cuil_titular=cuil_titular_p and inte=inte_p;   

    plan_actual_molinero = ospim from plan p where p.id_plan=id_plan_p;   

    if(NOT plan_anterior_molinero AND plan_actual_molinero) then
    /*Insertamos movimiento historico para la Super*/
        INSERT INTO informes.fechas_informe_super(cuil, fecha_baja, cuil_titular, inte, fecha_proceso) 
            SELECT cuil, fecha_baja_super, cuil_titular, inte, current_date FROM afiliado
                WHERE cuil_titular=cuil_titular_p and inte=inte_p;
    /* Blanqueamos datos para presentar nuevamente a la SSS */
        UPDATE afiliado set fecha_baja_super=null
            WHERE cuil_titular=cuil_titular_p and inte=inte_p;
    end if;       
end if;


  insert into afi_plan(cuil_titular, inte, vigen_desde, vigen_hasta, id_motivo_baja, id_plan, id_plan_omint,
               id_tarifa, alta_fecha, alta_usr, modi_fecha, modi_usr)
  values (cuil_titular_p, inte_p, vigen_desde_p, vigen_hasta_p, motivo_baja_p, id_plan_p, id_plan_omint_p,
            0, localtimestamp, usr_p, localtimestamp, usr_p);
  
  return currval('afi_plan_id_sequence');
  end; 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;