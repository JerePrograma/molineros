CREATE OR REPLACE FUNCTION cerrar_periodo_contable_anterior()
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare v_fecha  date;
declare v_exite_cierre  boolean;
BEGIN
	v_fecha = date (extract('year' from current_date) || '' || lpad(cast(extract('month' from current_date) as character varying),2,'0') || '01' ) - interval '1 day';

	v_exite_cierre = true where exists (select 1 from cierre_periodo_contable where fecha_cierre = v_fecha and baja_fecha is null);
	if v_exite_cierre is null then
		insert into cierre_periodo_contable (fecha_cierre, observacion, alta_fecha, alta_usr, modi_fecha, modi_usr)
		values (v_fecha,'Cierre automático', localtimestamp, 'auto', localtimestamp, 'auto');
	end if;
	
	return 1;
END;
$BODY$;