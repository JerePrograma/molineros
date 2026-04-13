drop FUNCTION buscar_conceptos_afip(p_desde date, p_hasta date);
CREATE OR REPLACE FUNCTION buscar_conceptos_afip(p_desde date, p_hasta date) 
RETURNS TABLE(
  cod_conc character varying, 
  descripcion character varying, 
  cod_contra_conc character varying, 
  deb_cred character varying, 
  liquidable boolean, 
   valido_desde date,
   valido_hasta date,
   id integer,
    c__id integer,
 c__descripcion character varying,
 c__numero character varying,
 c__cuenta character varying)
    LANGUAGE sql
    AS $BODY$
  
    select cod_conc, cto.descripcion, cod_contra_conc, deb_cred, cto.liquidable, 
    c.valido_desde, c.valido_hasta, c.id, con.id_concepto_maestro, con.descripcion, pc.numero , pc.cuenta
    from conceptos_transf_os  cto 
    left outer join concepto_transferencia c
    on c.concepto_transf = cto.cod_conc
    left outer join conceptos con
    on c.concepto_id = con.id_concepto_maestro
    and cast(con.valido_desde as date)<= cast(c.valido_desde as date)  and cast(con.valido_hasta as date)>=cast (c.valido_desde as date)
    left outer join plan_cuentas pc
    on con.id_plan_cuenta = pc.id_cuenta_maestro
    and (cast(pc.valido_desde as date) <= cast(con.valido_Desde as date) )
    and (cast(pc.valido_hasta as date) >= cast(con.valido_Desde as date)  )
    where  ((cast(c.valido_desde as date) <= cast($2 as date)  and cast(c.valido_hasta as date)>=cast($2 as date)) 
    or (cast(c.valido_desde as date) <= cast($1 as date)  and cast(c.valido_hasta as date)>=cast($1 as date))
    or (cast(c.valido_desde as date) >= cast($1 as date)  and cast(c.valido_hasta as date)<=cast($2 as date)))
    order by cto.descripcion;

$BODY$;

