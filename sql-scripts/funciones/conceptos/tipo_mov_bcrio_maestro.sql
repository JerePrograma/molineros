
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