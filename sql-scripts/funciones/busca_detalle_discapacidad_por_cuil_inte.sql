drop function busca_detalle_discapacidad_por_cuil_inte(cuil_titu character varying, inte integer) 
CREATE OR REPLACE FUNCTION busca_detalle_discapacidad_por_cuil_inte(cuil_titu character varying, intee integer) 
RETURNS TABLE(
    cuil_titular character varying,
    inte integer,
	diagnostico character varying,
	dependencia boolean,
    telefono_contacto character varying,
    cie_diez character varying
)
    LANGUAGE sql
    AS $BODY$

	select
    d.cuil_titular, d.inte, d.diagnostico, d.dependencia, CASE WHEN d.telefono_contacto IS NULL THEN (CASE WHEN af.telefono IS NULL THEN '' ELSE af.telefono END) ELSE d.telefono_contacto END, d.cie_diez
    from afiliado a
    left outer join	
    detalle_discapacidad d
    on a.cuil_titular = d.cuil_titular and a.inte  = d.inte     
    left outer join 
    afi_domicilio af on
    a.cuil_titular = af.cuil_titular
    and a.inte = af.inte
    and af.inte = 0
    and af.modi_fecha in (select max(a1.modi_fecha) from afi_domicilio a1 where a1.cuil_titular = $1 and a1.inte = 0)
             
    where    
    a.cuil_titular = $1 and a.inte = $2         
   
$BODY$;