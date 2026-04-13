 CREATE OR REPLACE FUNCTION insertar_nomenclador_concepto(
 p_codigo character varying, --1
 p_descripcion character varying,--2
 p_id_hon_amb integer,--3
 p_id_hon_int integer,--4
 p_id_gas_amb integer,--5
 p_id_gas_int integer,--6
 p_coef_gastos numeric,--7
 p_coef_honorarios numeric,--8
 p_valido_desde date,--9
 p_valido_hasta date,--10
 p_user character varying, --11
 p_marca_rein_liq integer, --12
 p_tipo_nomenclador integer)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
insert into nomenclador (codigo, descripcion, coef_gastos, coef_honorarios,alta_usr,alta_fecha,modi_usr,modi_fecha, id_tipo_nomenclador, marca_rein_liq)
values ($1,$2,$7,$8,$11,localtimestamp,$11,localtimestamp, $13, $12);

insert into plan_prestacion (id_prestacion, id_plan)
values (currval('nomenclador_id_seq'),1);

insert into nomenclador_conceptos (codigo, descripcion, concepto_id, id_prestacion,valido_desde, valido_hasta, tipo_id, alta_fecha, alta_usr, modi_fecha, modi_usr)
values ($1,$2,$3,currval('nomenclador_id_seq'), $9,$10, 1, current_date, $11, current_date, $11 );

insert into nomenclador_conceptos (codigo, descripcion, concepto_id, id_prestacion,valido_desde, valido_hasta, tipo_id, alta_fecha, alta_usr, modi_fecha, modi_usr)
values ($1,$2,$4,currval('nomenclador_id_seq'), $9,$10, 2, current_date, $11, current_date, $11 );

insert into nomenclador_conceptos (codigo, descripcion, concepto_id, id_prestacion,valido_desde, valido_hasta, tipo_id, alta_fecha, alta_usr, modi_fecha, modi_usr)
values ($1,$2,$5,currval('nomenclador_id_seq'), $9,$10, 3, current_date, $11, current_date, $11 );

insert into nomenclador_conceptos (codigo, descripcion, concepto_id, id_prestacion,valido_desde, valido_hasta, tipo_id, alta_fecha, alta_usr, modi_fecha, modi_usr)
values ($1,$2,$6,currval('nomenclador_id_seq'), $9,$10, 4, current_date, $11, current_date, $11 );

return currval('nomenclador_id_seq');
END;
$BODY$;
