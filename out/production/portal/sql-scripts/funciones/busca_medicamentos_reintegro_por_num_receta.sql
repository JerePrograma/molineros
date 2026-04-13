CREATE OR REPLACE FUNCTION busca_medicamentos_reintegro_por_num_receta
(
IN numreceta_p integer
)

  RETURNS TABLE(
  rp_id_reintegro integer, 
  rp_fecha timestamp without time zone, 
  rp_nro_receta integer, rp_profesional character varying, rp_cantidad integer, rp_troquel integer, rp_cober_sss numeric, rp_cober_amtima numeric, 
  rp_cober_ospim numeric, rp_monto_ospim numeric, rp_monto_amtima numeric, rp_precio_al_publico numeric, rp_precio_ospim numeric, rp_total_med numeric, 
  rp_total_cobertura numeric, rp_total numeric, rp_alta_fecha timestamp without time zone, rp_alta_usr character varying, 
  rp_modi_fecha timestamp without time zone, rp_modi_usr character varying, rp_baja_fecha timestamp without time zone, rp_baja_usr character varying, 
  rp_id_medicamento integer, rp_id integer, 
  m_troquel numeric, m_nombre character varying, m_presentacion character varying, m_monto_ioma numeric, m_norma_ioma character, 
  m_cober_ioma character, m_laboratorio character varying, m_precio numeric, m_fecha timestamp without time zone, m_controlado character, 
  m_importado character, m_tipo_venta character, m_iva character, m_cod_dto_pami character, m_cod_lab integer, m_nro_registro numeric, m_baja character, 
  m_cod_barra character varying, m_unidades integer, m_tamanio character, m_heladera character, m_sifar character, m_baja_especial character, 
  m_accion character varying, m_droga character varying, m_id_medicamento integer, m_porc_ospim numeric, m_porc_amtima numeric, m_porc_sssalud numeric, m_pmoe_n numeric) AS
$BODY$

select

--medicamento detalle

  rp.id_reintegro,
  rp.fecha,
  rp.nro_receta,
  rp.profesional,
  rp.cantidad,
  rp.troquel,
  rp.cober_sss,
  rp.cober_amtima,
  rp.cober_ospim,
  rp.monto_ospim,
  rp.monto_amtima,
  rp.precio_al_publico,
  rp.precio_ospim,  
  rp.total_med,
  rp.total_cobertura,
  rp.total,
  rp.alta_fecha as alta_fecha_rp,
  rp.alta_usr as alta_usr_rp,
  rp.mod_fecha as modi_fecha_rp,
  rp.modi_usr as modi_usr_rp,
  rp.baja_fecha as baja_fecha_rp,
  rp.baja_usr as baja_usr_rp,
  rp.id_medicamento as id_medicamento_rp,
  rp.id,  
--medicamento
  
  m.troquel as troquel_m,
  m.nombre,
  m.presentacion,
  m.monto_ioma,
  m.norma_ioma,
  m.cober_ioma,
  m.laboratorio,
  m.precio,
  m.fecha as fecha_m,
  m.controlado,
  m.importado ,
  m.tipo_venta ,
  m.iva ,
  m.cod_dto_pami ,
  m.cod_lab ,
  m.nro_registro ,
  m.baja ,
  m.cod_barra  ,
  m.unidades ,
  m.tamanio ,
  m.heladera ,
  m.sifar ,
  m.baja_especial ,	  
  m.accion  ,  
  m.droga  ,  
  m.id_medicamento , 
  
 trunc(v.porc_ospim,2) as uno, 
 trunc(v.porc_amtima,2) as dos, 
 trunc(v.porc_sssalud,2) as tres,  
 v.pmoe_n as cuatro
 
  from 
 
medicamento_reintegro_farmacia rp
left outer join medicamentos m
on rp.id_medicamento = m.id_medicamento
left outer join vademecum v
on v.registro=m.nro_registro
and m.fecha=(select max(fecha) from medicamentos m2 where m2.nro_registro=m.nro_registro)
where rp.nro_receta=$1;

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;