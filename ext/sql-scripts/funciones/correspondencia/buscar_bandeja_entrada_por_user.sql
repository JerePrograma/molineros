
/* version comun, mas abajo la version p paginar */

CREATE OR REPLACE FUNCTION correo.buscar_bandeja_entrada_por_user(p_screen_name character varying, p_edificio character varying, p_esrecepcionista boolean, p_fecha_desde date, p_fecha_hasta date, p_estado character varying, p_cuit character varying, p_pertenece_liquidaciones boolean)
  RETURNS SETOF correo.correspondencia_cab_item AS
$BODY$
BEGIN

return query

select

  cab.id_correspondencia,  
  cab.lugar_recep_emision,
  cab.fecha,
  cab.tipo_registro,
  cab.tipo_envio,
  cab.oblea,
  cab.alta_fecha,
  cab.alta_usr,
  cab.modi_fecha,
  cab.modi_usr,
  cab.baja_fecha,
  cab.baja_usr,
  
  ic.id, 
  ic.id_correspondencia,  
  ic.entrada_salida,
  ic.tipo_remitente_destinatario,    
  ic.edificio,
  ic.sector,
  ic.usuario,
  ic.empresa_remite,
  ic.sector_remite,
  ic.usuario_remite,
  ic.contenido,
  
  ic.estado,  
  
  ic.cuil_titular,
  ic.inte,    
  --datos farmacia
  ic.codigo_farmacia,  
  --datos otros
  ic.descripcion_otro,   
  --datos prestador y proveedor
  ic.id_prestador,
  ic.cuit_proveedor,
  ic.sucu_proveedor,
  ic.id_punto_venta,
  ic.compro_tipo,
  ic.compro_nro,
  ic.cuit,
  ic.compro_letra,
  ic.compro_sucu,
  ic.importe,
  ic.fecha_emision,
  ic.fecha_vencimiento,

  ic.id_seccional,
  
  ic.alta_fecha,
  ic.alta_usr,
  ic.modi_fecha,
  ic.modi_usr,
  ic.baja_fecha,
  ic.baja_usr,
  ic.alta_sector,

  a.cuil_titular,
a.inte,
a.id_ospim ,
--a.id_ospim_baja_fecha,
a.id_uoma,
--a.id_uoma_baja_fecha,
a.id_amtima,
--a.id_amtima_baja_fecha,
a.apellido,
a.nombre,
a.documento_tipo,
a.sexo,
a.cuil, 
a.docu_numero,

  f.id_farmacia,
  f.farmacia,
  f.cuit,
  f.codigo,
  f.cod_farm,
  f.sucursal,
  
  	  prs.id_prestador,
	  prs.cuit,
	  prs.id_tipo_prestador,
	  prs.id_seccional ,
	  prs.descripcion,
	  
 e.cuit,
 e.sucursal,
 e.razon_soc,
 e.nombre_fantasia,
--e.id_ramo_empresa,
 e.id_seccional,
 
 s.id_seccional,
 s.descripcion,
  
 lp.id,
 lp.id_paquete,
 lp.id_item_correspondencia,
 lp.alta_fecha,
 lp.alta_usr,
 /*lp.modi_fecha,
 lp.modi_usr,
 lp.baja_fecha,
 lp.baja_usr,*/
 p.estado,
 cast(0 as integer)

from correo.item_correspondencia ic
inner join correo.cabecera_correspondencia cab
on ic.id_correspondencia=cab.id_correspondencia
left outer join correo.lista_paquete lp
on ic.id = lp.id_item_correspondencia
left outer join correo.paquete p
on lp.id_paquete = p.id
left outer join afiliado a
on ic.cuil_titular = a.cuil_titular and
ic.inte = a.inte
left outer join farmacia f
--on ic.codigo_farmacia = f.codigo
on ic.codigo_farmacia = f.id_farmacia
left outer join prestador prs
on ic.id_prestador = prs.id_prestador
left outer join empresa e
on ic.cuit_proveedor = e.cuit and
ic.sucu_proveedor = e.sucursal
left outer join seccional s
on ic.id_seccional = s.id_seccional

where

--Usuario... item ingresado, con/sin paquete, mismo usuario, mismo edificio destino
ic.baja_fecha is null 
and 
(
(p_pertenece_liquidaciones = false 
  and
-- para cualquier rol que traiga sus mensajes
((cab.tipo_envio='MENSAJERIA' 
  and ic.estado = p_estado
  and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
  and ic.usuario = p_screen_name 
  and ic.edificio = p_edificio ) 
OR
(cab.tipo_envio='CORREOINTERNO' 
 and p.estado='DESEMPAQUETADO'
 and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
 and ic.estado = p_estado
 and ic.usuario = p_screen_name 
 and ic.edificio = p_edificio )) 
)
OR
(p_pertenece_liquidaciones = true 
  and
-- para cualquier rol que traiga sus mensajes
(p_cuit is null 
       or (p_cuit is not null and ic.cuit_proveedor = p_cuit)
       or (p_cuit is not null and prs.cuit = p_cuit)) 
  and
(
((cab.tipo_envio='MENSAJERIA' or (cab.tipo_envio='CORREOINTERNO' and p.estado='DESEMPAQUETADO'))
  and ic.estado = p_estado
  and p_estado='INGRESADO'
  and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
  and ic.usuario = p_screen_name 
  and ic.edificio = p_edificio)
OR
((cab.tipo_envio='MENSAJERIA' or (cab.tipo_envio='CORREOINTERNO' and p.estado='DESEMPAQUETADO'))
  and ic.estado = p_estado
  and p_estado='RECIBIDO'
  and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
  and ic.usuario = p_screen_name 
  and ic.edificio = p_edificio
  and (
        (ic.tipo_remitente_destinatario='PRESTADOR'
	and (p_cuit is null 
	     or (p_cuit is not null and ic.cuit_proveedor = p_cuit)
             or (p_cuit is not null and prs.cuit = p_cuit))
	and exists (select 1 from comprobante_liquidacion cl 
	     where prs.cuit = cl.cuit 
	     and ic.id_punto_venta = cl.id_punto_venta 
	     and ic.compro_tipo = cl.compro_tipo 
	     and lpad(ic.compro_nro,8,'0') = cl.compro_nro 
	     and ic.compro_letra = cl.compro_letra 
	     and ic.compro_sucu = cl.compro_sucu))
	or ic.tipo_remitente_destinatario not in ('PRESTADOR')      
     )    
)        
OR
(p_estado = 'PARA_LIQUIDAR' 
   and (ic.tipo_remitente_destinatario='PRESTADOR')
   and ic.estado in('RECIBIDO')
   and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
   and ic.usuario = p_screen_name
   and ic.sector in ('99214') --liquidaciones
   and lp.baja_fecha is null 
   and not exists (select 1 from comprobante_liquidacion cl 
	     where prs.cuit = cl.cuit 
	     and ic.id_punto_venta = cl.id_punto_venta 
	     and ic.compro_tipo = cl.compro_tipo 
	     and lpad(ic.compro_nro,8,'0') = cl.compro_nro 
	     and ic.compro_letra = cl.compro_letra 
	     and ic.compro_sucu = cl.compro_sucu)))
)	     
OR
--Recepcionista ...item en paquete, mismo edificio
(p_esRecepcionista = true 
 and lp.id_paquete is not null 
 and lp.id_paquete <> 0 
 and lp.baja_fecha is null 
 and (cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta) 
 and p_estado in('INGRESADO','ENVIADO') 
 and ic.estado not in('RECIBIDO','REVISAR') 
 and ic.edificio = p_edificio) 
OR 
(p_esRecepcionista = true 
 and (cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta) 
 and p_estado='REVISAR' 
 and ic.estado = 'REVISAR' 
 and cab.lugar_recep_emision = p_edificio)
)
order by cab.id_correspondencia desc,  ic.id desc, cab.alta_fecha desc;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;

  
/* version p paginar */
  
  
CREATE OR REPLACE FUNCTION correo.buscar_bandeja_entrada_por_user(p_screen_name character varying, p_edificio character varying, p_esrecepcionista boolean, p_fecha_desde date, p_fecha_hasta date, p_estado character varying, p_cuit character varying, p_pertenece_liquidaciones boolean, offset_p integer)
  RETURNS SETOF correo.correspondencia_cab_item AS
$BODY$
declare total_registros_v int;
BEGIN

if(offset_p>0) then
offset_p=offset_p*50;
end if;

total_registros_v=count(*) from correo.buscar_bandeja_entrada_por_user(p_screen_name, p_edificio, p_esrecepcionista, p_fecha_desde, p_fecha_hasta, p_estado, p_cuit, p_pertenece_liquidaciones);

return query

select

  cab.id_correspondencia,  
  cab.lugar_recep_emision,
  cab.fecha,
  cab.tipo_registro,
  cab.tipo_envio,
  cab.oblea,
  cab.alta_fecha,
  cab.alta_usr,
  cab.modi_fecha,
  cab.modi_usr,
  cab.baja_fecha,
  cab.baja_usr,
  
  ic.id, 
  ic.id_correspondencia,  
  ic.entrada_salida,
  ic.tipo_remitente_destinatario,    
  ic.edificio,
  ic.sector,
  ic.usuario,
  ic.empresa_remite,
  ic.sector_remite,
  ic.usuario_remite,
  ic.contenido,
  
  ic.estado,  
  
  ic.cuil_titular,
  ic.inte,    
  --datos farmacia
  ic.codigo_farmacia,  
  --datos otros
  ic.descripcion_otro,   
  --datos prestador y proveedor
  ic.id_prestador,
  ic.cuit_proveedor,
  ic.sucu_proveedor,
  ic.id_punto_venta,
  ic.compro_tipo,
  ic.compro_nro,
  ic.cuit,
  ic.compro_letra,
  ic.compro_sucu,
  ic.importe,
  ic.fecha_emision,
  ic.fecha_vencimiento,

  ic.id_seccional,
  
  ic.alta_fecha,
  ic.alta_usr,
  ic.modi_fecha,
  ic.modi_usr,
  ic.baja_fecha,
  ic.baja_usr,
  ic.alta_sector,

  a.cuil_titular,
a.inte,
a.id_ospim ,
--a.id_ospim_baja_fecha,
a.id_uoma,
--a.id_uoma_baja_fecha,
a.id_amtima,
--a.id_amtima_baja_fecha,
a.apellido,
a.nombre,
a.documento_tipo,
a.sexo,
a.cuil, 
a.docu_numero,

  f.id_farmacia,
  f.farmacia,
  f.cuit,
  f.codigo,
  f.cod_farm,
  f.sucursal,
  
  	  prs.id_prestador,
	  prs.cuit,
	  prs.id_tipo_prestador,
	  prs.id_seccional ,
	  prs.descripcion,
	  
 e.cuit,
 e.sucursal,
 e.razon_soc,
 e.nombre_fantasia,
 --e.id_ramo_empresa,
 e.id_seccional,
 
 s.id_seccional,
 s.descripcion,
  
 lp.id,
 lp.id_paquete,
 lp.id_item_correspondencia,
 lp.alta_fecha,
 lp.alta_usr,
 /*lp.modi_fecha,
 lp.modi_usr,
 lp.baja_fecha,
 lp.baja_usr,*/
 p.estado,
 total_registros_v

from correo.item_correspondencia ic
inner join correo.cabecera_correspondencia cab
on ic.id_correspondencia=cab.id_correspondencia
left outer join correo.lista_paquete lp
on ic.id = lp.id_item_correspondencia
left outer join correo.paquete p
on lp.id_paquete = p.id
left outer join afiliado a
on ic.cuil_titular = a.cuil_titular and
ic.inte = a.inte
left outer join farmacia f
--on ic.codigo_farmacia = f.codigo
on ic.codigo_farmacia = f.id_farmacia
left outer join prestador prs
on ic.id_prestador = prs.id_prestador
left outer join empresa e
on ic.cuit_proveedor = e.cuit and
ic.sucu_proveedor = e.sucursal
left outer join seccional s
on ic.id_seccional = s.id_seccional

where

--Usuario... item ingresado, con/sin paquete, mismo usuario, mismo edificio destino
ic.baja_fecha is null 
and 
(
(p_pertenece_liquidaciones = false 
  and
-- para cualquier rol que traiga sus mensajes
((cab.tipo_envio='MENSAJERIA' 
  and ic.estado = p_estado
  and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
  and ic.usuario = p_screen_name 
  and ic.edificio = p_edificio ) 
OR
(cab.tipo_envio='CORREOINTERNO' 
 and p.estado='DESEMPAQUETADO'
 and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
 and ic.estado = p_estado
 and ic.usuario = p_screen_name 
 and ic.edificio = p_edificio )) 
)
OR
(p_pertenece_liquidaciones = true 
  and
-- para cualquier rol que traiga sus mensajes
(p_cuit is null 
       or (p_cuit is not null and ic.cuit_proveedor = p_cuit)
       or (p_cuit is not null and prs.cuit = p_cuit)) 
  and
(
((cab.tipo_envio='MENSAJERIA' or (cab.tipo_envio='CORREOINTERNO' and p.estado='DESEMPAQUETADO'))
  and ic.estado = p_estado
  and p_estado='INGRESADO'
  and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
  and ic.usuario = p_screen_name 
  and ic.edificio = p_edificio)
OR
((cab.tipo_envio='MENSAJERIA' or (cab.tipo_envio='CORREOINTERNO' and p.estado='DESEMPAQUETADO'))
  and ic.estado = p_estado
  and p_estado='RECIBIDO'
  and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
  and ic.usuario = p_screen_name 
  and ic.edificio = p_edificio
  and (
        (ic.tipo_remitente_destinatario='PRESTADOR'
	and (p_cuit is null 
	     or (p_cuit is not null and ic.cuit_proveedor = p_cuit)
             or (p_cuit is not null and prs.cuit = p_cuit))
	and exists (select 1 from comprobante_liquidacion cl 
	     where prs.cuit = cl.cuit 
	     and ic.id_punto_venta = cl.id_punto_venta 
	     and ic.compro_tipo = cl.compro_tipo 
	     and lpad(ic.compro_nro,8,'0') = cl.compro_nro 
	     and ic.compro_letra = cl.compro_letra 
	     and ic.compro_sucu = cl.compro_sucu))
	or ic.tipo_remitente_destinatario not in ('PRESTADOR')      
     )    
)        
OR
(p_estado = 'PARA_LIQUIDAR' 
   and (ic.tipo_remitente_destinatario='PRESTADOR')
   and ic.estado in('RECIBIDO')
   and cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta
   and ic.usuario = p_screen_name
   and ic.sector in ('99214') --liquidaciones
   and lp.baja_fecha is null 
   and not exists (select 1 from comprobante_liquidacion cl 
	     where prs.cuit = cl.cuit 
	     and ic.id_punto_venta = cl.id_punto_venta 
	     and ic.compro_tipo = cl.compro_tipo 
	     and lpad(ic.compro_nro,8,'0') = cl.compro_nro 
	     and ic.compro_letra = cl.compro_letra 
	     and ic.compro_sucu = cl.compro_sucu)))
)	     
OR
--Recepcionista ...item en paquete, mismo edificio
(p_esRecepcionista = true 
 and lp.id_paquete is not null 
 and lp.id_paquete <> 0 
 and lp.baja_fecha is null 
 and (cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta) 
 and p_estado in('INGRESADO','ENVIADO') 
 and ic.estado not in('RECIBIDO','REVISAR') 
 and ic.edificio = p_edificio) 
OR 
(p_esRecepcionista = true 
 and (cab.fecha >= p_fecha_desde and cab.fecha <= p_fecha_hasta) 
 and p_estado='REVISAR' 
 and ic.estado = 'REVISAR' 
 and cab.lugar_recep_emision = p_edificio)
)
order by cab.id_correspondencia desc,  ic.id desc, cab.alta_fecha desc
--order by 1 desc, 12 desc,  6 desc
limit 50
offset offset_p;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;


