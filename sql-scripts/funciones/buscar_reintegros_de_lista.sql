CREATE OR REPLACE FUNCTION buscar_reintegros_de_lista(p_idLista integer) 
RETURNS TABLE(id_reintegro integer,
	importe numeric(10,2),
	id_seccional integer)
    LANGUAGE sql
    AS $BODY$
	select 	
	 d.id_reintegro ,
	d.importe ,
	r.id_seccional
	 from lista_reintegro_pago_detalle d inner join reintegro r
	 on d.id_reintegro = r.id_reintegro
	where id_lista_reintegro_pago = $1;
$BODY$;


ALTER FUNCTION public.buscar_reintegros_de_lista(p_idLista integer)  OWNER TO postgres;

--
