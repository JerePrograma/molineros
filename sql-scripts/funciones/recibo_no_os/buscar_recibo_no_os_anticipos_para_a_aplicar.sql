-- Function: buscar_recibo_no_os_anticipos_para_a_aplicar(character varying, character varying, character varying)

-- DROP FUNCTION buscar_recibo_no_os_anticipos_para_a_aplicar(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION buscar_recibo_no_os_anticipos_para_a_aplicar(IN p_cuit character varying, IN p_sucu character varying, IN p_entidad character varying)
  RETURNS TABLE(id integer, recibo_id integer, numero character varying, importe numeric, recibo_fecha date) AS
$BODY$


select rc.id, r.id, r.numero, rc.concepto_importe_adicional, r.fecha
from recibo_no_os_conceptos rc
inner join recibo_no_os r
on rc.recibo_id = r.id
where rc.caja_concepto_id = 122
and r.cuit = $1
and r.sucursal =  $2
and r.entidad= $3
and not exists (select 1 from recibo_no_os_ingresos where id_anticipo_recibo_concepto = rc.id);


$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_recibo_no_os_anticipos_para_a_aplicar(character varying, character varying, character varying)
  OWNER TO postgres;

