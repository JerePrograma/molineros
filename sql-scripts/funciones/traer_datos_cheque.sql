-- Function: traer_datos_cheque(character varying, integer)

-- DROP FUNCTION traer_datos_cheque(character varying, integer);

CREATE OR REPLACE FUNCTION traer_datos_cheque(IN p_numero character varying, IN p_id_op integer)
  RETURNS TABLE(importe character, paguesea character, importe_texto character varying, mes character, dia character, anio character) AS
$BODY$

	select  
	to_char(importe, '999G999G999D99'),
	case when a_nombre_de is null or length(trim(a_nombre_de)) =0 then '' else cast(a_nombre_de as character varying) || '  -----' end,
	upper(transf_importe_a_texto(importe))  || '  -----',
	case EXTRACT(MONTH FROM fecha)
	when 1 then 'ENERO'
	when 2 then 'FEBRERO'
	when 3 then 'MARZO'
	when 4 then 'ABRIL'
	when 5 then 'MAYO'
	when 6 then 'JUNIO'
	when 7 then 'JULIO'
	when 8 then 'AGOSTO'
	when 9 then 'SETIEMBRE'
	when 10 then 'OCTUBRE'
	when 11 then 'NOVIEMBRE'
	when 12 then 'DICIEMBRE' end,
	cast(EXTRACT(DAY FROM fecha) as character(2)),
	cast(EXTRACT(YEAR FROM current_date) as character(4)) as anio
	from cheque c
	where (($1 is null or $1 = '') or  ($1 is not null and cast($1 as numeric) = nro_cheque))
	and  ($2 is null or 
			($2 is not null and 
			  exists (select 1 from orden_pago_ospim_pagos opop 
				  where nro_cheque = c.nro_cheque 
				  and id_banco = c.id_banco 
				  and id_orden_pago = $2)
			)
		)
	order by nro_cheque asc;

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION traer_datos_cheque(character varying, integer) OWNER TO postgres;
