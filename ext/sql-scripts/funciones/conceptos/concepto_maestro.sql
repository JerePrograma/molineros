create table concepto_maestro(
   id serial,
   descripcion_original character varying,
   alta_fecha date,
   alta_usr character varying,
   modi_fecha date,
   modi_usr character varying,
   baja_fecha date,
   baja_usr character varying,
   valido_desde date,
   valido_hasta date
);
alter table concepto_maestro add constraint pk_concepto_maestro primary key(id);
alter table conceptos  add alta_usr character varying;
alter table conceptos add alta_fecha date;
alter table nomenclador_conceptos  add alta_usr character varying;
alter table nomenclador_conceptos add alta_fecha date;
alter table  tipo_mov_bcrio drop ex_tipo_mov_id;
alter table tipo_mov_bcrio  add alta_usr character varying;
alter table tipo_mov_bcrio add alta_fecha date;
alter table concepto_transferencia  add modi_usr character varying;
alter table concepto_transferencia add modi_fecha date;
alter table concepto_transferencia  add alta_usr character varying;
alter table concepto_transferencia add alta_fecha date;

insert into concepto_maestro (descripcion_original, alta_usr, alta_fecha)
select descripcion, 'admin', current_date
from conceptos 
group by descripcion;
insert into parametros_conceptos  values ('canje_cheque', (select id from concepto_maestro where descripcion_original = 'CANJE CHEQUES PROPIOS'), '18000101','29990101','Concepto a ser utilizado en la opcion de canje de cheques',null,null,null,null);
update concepto_maestro  set valido_desde = '18000101', valido_hasta = '29990101';
----------------
--conceptos
--------------
alter table conceptos add id_concepto_maestro integer;
alter table conceptos add constraint fk_concepto_maestro foreign key (id_concepto_maestro) references concepto_maestro(id);


update conceptos c set id_concepto_maestro = cm.id
from  concepto_maestro cm
where c.descripcion = cm.descripcion_original;

--------------
-------------
--concepto_comprobante
--------------
alter table concepto_comprobante drop constraint fk_concepto_comprobante;
ALTER TABLE concepto_comprobante drop CONSTRAINT pk_concepto_comprobante;
ALTER TABLE ONLY concepto_comprobante alter column concepto_id drop not null;
alter table concepto_comprobante  add id_concepto_maestro integer;

update concepto_comprobante cm set id_concepto_maestro = c.id_concepto_maestro
from conceptos c
where cm.concepto_id = c.id;

update concepto_comprobante set concepto_id = null;

update concepto_comprobante cm set concepto_id = id_concepto_maestro;

ALTER TABLE ONLY concepto_comprobante  ADD CONSTRAINT fk_concepto_comprobante FOREIGN KEY (concepto_id) REFERENCES concepto_maestro(id) MATCH FULL;
ALTER TABLE concepto_comprobante  add CONSTRAINT pk_concepto_comprobante PRIMARY KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit, concepto_id);
alter table concepto_comprobante drop column id_concepto_maestro;



-----------------
--recibo_conceptos
-----------------

 alter table recibo_conceptos drop CONSTRAINT fk_recibo_conceptos_caja_concepto ;

update recibo_conceptos cm set caja_concepto_id = c.id_concepto_maestro
from conceptos c
where cm.caja_concepto_id = c.id;


alter table recibo_conceptos add CONSTRAINT fk_recibo_conceptos_caja_concepto FOREIGN KEY (caja_concepto_id)
REFERENCES concepto_maestro (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION;
      
-------------------
--tipo_mov_bcrio_maestro
-------------------
create table tipo_mov_bcrio_maestro(
   id serial,
   descripcion_original character varying,
   alta_fecha date,
   alta_usr character varying,
   modi_fecha date,
   modi_usr character varying,
   baja_fecha date,
   baja_usr character varying   ,
   valido_desde date,
   valido_hasta date
);
alter table tipo_mov_bcrio_maestro add constraint pk_tipo_mov_bcrio_maestro primary key(id);

--reacomodo movimiento_banco
update movimiento_banco mb set id_tipo_mov = tipo.id_min
from  (select t.id_tipo_mov, t2.id_tipo_mov as id_min from tipo_mov_bcrio t, (
	select min(id_tipo_mov) as id_tipo_mov,descripcion 
	from tipo_mov_bcrio group by descripcion) t2
	where t.descripcion = t2.descripcion) tipo
where mb.id_tipo_mov = tipo.id_tipo_mov;

delete from tipo_mov_bcrio   where valido_Desde > '18000101';



--inserto en tipo_mov_bcrio_maestro y luego updateo tipo_mov_bcrio
alter table tipo_mov_bcrio add id_tipo_mov_maestro integer;
alter table tipo_mov_bcrio add constraint fk_tipo_mov_maestro foreign key (id_tipo_mov_maestro) references tipo_mov_bcrio_maestro(id);

insert into  tipo_mov_bcrio_maestro (descripcion_original, alta_usr, alta_fecha, valido_desde, valido_hasta)
select descripcion, 'admin', current_date, '18000101','29990101'
from tipo_mov_bcrio 
group by descripcion;

update tipo_mov_bcrio c set id_tipo_mov_maestro = cm.id
from  tipo_mov_bcrio_maestro cm
where c.descripcion = cm.descripcion_original;

update tipo_mov_bcrio set ex_tipo_mov_id = null;

-----------------
----------------------
--movimiento_banco
-------------------
ALTER TABLE ONLY movimiento_banco   drop CONSTRAINT fk_tipo_mov;
    
update movimiento_banco mb set id_tipo_mov = t.id_tipo_mov_maestro
from tipo_mov_bcrio t
where mb.id_tipo_mov = t.id_tipo_mov;

ALTER TABLE ONLY movimiento_banco
    ADD CONSTRAINT fk_tipo_mov FOREIGN KEY (id_tipo_mov) REFERENCES tipo_mov_bcrio_maestro(id) MATCH FULL;
    
-----------------
----------------------
--tipo_mov_bcrio concepto
-------------------   
alter table tipo_mov_bcrio drop CONSTRAINT fk_tipo_mov_concepto ;

update tipo_mov_bcrio nc set concepto_id = c.id_concepto_maestro
from  conceptos c
where nc.concepto_id = c.id;


alter table tipo_mov_bcrio add CONSTRAINT fk_tipo_mov_concepto FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
      
-----------------
----------------------
--codigo_ext_bcrias_afip
-------------------   
alter table codigo_ext_bcrias_afip drop CONSTRAINT fk_codigo_ext_bcrias_afip_tipo_mov ;

update codigo_ext_bcrias_afip nc set id_tipo_mov = c.id_tipo_mov_maestro
from  tipo_mov_bcrio c
where nc.id_tipo_mov = c.id_tipo_mov;

alter table codigo_ext_bcrias_afip add
 CONSTRAINT fk_codigo_ext_bcrias_afip_tipo_mov FOREIGN KEY (id_tipo_mov)
      REFERENCES tipo_mov_bcrio_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

      
-----------------
-------------------
-- nomenclador_conceptos
--------------
    
alter table nomenclador_conceptos drop CONSTRAINT fk_nc_concepto ;

update nomenclador_conceptos nc set concepto_id = c.id_concepto_maestro
from  conceptos c
where nc.concepto_id = c.id;

alter table nomenclador_conceptos add
  CONSTRAINT fk_nc_concepto FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
      
--------------
--------------
--parametros_conceptos
---------------
      
alter table parametros_conceptos drop CONSTRAINT fk_param_conc_conc ;
      

update parametros_conceptos nc set id_concepto = c.id_concepto_maestro
from  conceptos c
where nc.id_concepto = c.id;

alter table parametros_conceptos add CONSTRAINT fk_param_conc_conc FOREIGN KEY (id_concepto)
      REFERENCES concepto_maestro   (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

      
      
--------------
--------------
--concepto_transferencia
--------------- 
      
update concepto_transferencia set valido_Desde = '18000101', valido_hasta = '29990101';

alter table concepto_transferencia drop CONSTRAINT fk_concepto_transferencia ;
      

update concepto_transferencia nc set concepto_id = c.id_concepto_maestro
from  conceptos c
where nc.concepto_id = c.id;

alter table concepto_transferencia add CONSTRAINT fk_concepto_transferencia FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro   (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
      
------------
------------
--  grupo_concepto
-----------
update grupos_concepto nc set id_concepto = c.id_concepto_maestro
from  conceptos c
where nc.id_concepto = c.id;

alter table grupos_concepto add constraint fk_gp_conceptos foreign key (id_concepto) references concepto_maestro (id);
alter table grupos_concepto add id serial;

delete from grupos_concepto where id not in (
select min(id) from grupos_concepto group by id_grupo_concepto, id_grupo, id_concepto)
--------------------
--index
--------------------------
CREATE INDEX idx_conceptos
on conceptos(id_concepto_maestro,valido_desde,valido_hasta);

-----------------
update tipo_mov_bcrio set valido_hasta = '29990101';
---------------
---lista
 listado_de_deudas     
 trae_concepto_ingreso
 trae_concepto_egreso
 trae_concepto_liquidacion
 buscar_concepto_comprobante_por_fecha_subdiario  **creo q bien
 buscar_concepto_comprobante_por_fecha
 buscar_concepto_comprobante_por_orden_pago_ospim
 buscar_concepto_comprobante
 reporte_egresos_por_concepto_cta_46_agrupados  **creo q bien
 reporte_egresos_por_concepto_cta_46**creo q bien
 buscar_recibo_conceptos_por_fechas
 buscar_recibo_conceptos
 buscar_movimientos_bcrios_subdiario_egreso**creo q bien
 reporte_aportes_contrib_empresa_periodo_con_ac_conv
 subdiario_ingresos
 trae_tipos_mov_bcrios_por_fechas
 
 