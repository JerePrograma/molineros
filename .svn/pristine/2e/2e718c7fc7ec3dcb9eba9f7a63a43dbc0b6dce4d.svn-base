create table caja_conceptos (
id integer not null,
descripcion character varying(100),
codigo character varying(10),
tipo char,
ex_id_cuenta character varying(20),
alta_fecha timestamp without time zone,
alta_usr character varying(50),
modi_fecha timestamp without time zone,
modi_usr character varying(50),
baja_fecha timestamp without time zone,
baja_usr character varying(50));
ALTER TABLE caja_conceptos ALTER COLUMN id SET DEFAULT nextval('caja_conceptos_id_seq'::regclass);
ALTER TABLE caja_conceptos add constraint pk_caja_conceptos  primary key (id);


--migrar desde fox
insert into caja_conceptos  (descripcion,codigo,tipo ,ex_id_cuenta,alta_fecha,alta_usr,modi_fecha,modi_usr )
select  cm_desc, cm_codi , cm_tipo, cm_idct, localtimestamp, 'admin', localtimestamp, 'admin'
from cj_cajasmov where (eqbaja is null or eqbaja = '' ) and cm_Esta = 'S' and feinicial <> '00:00';