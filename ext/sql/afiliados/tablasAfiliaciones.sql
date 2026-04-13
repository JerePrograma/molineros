-- Table: afiliado

-- DROP TABLE afiliado;

CREATE TABLE afiliado
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_ospim integer,
  id_uoma integer,
  id_amtima integer,
  apellido character varying(100) NOT NULL,
  nombre character varying(100) NOT NULL,
  documento_tipo character varying(4),
  sexo character varying(2) NOT NULL,
  cuil character varying(13),
  naci_fecha date NOT NULL,
  civil_esta character varying(20) NOT NULL,
  parentesco character varying(100),
  ingre_fecha date NOT NULL,
  id_seccional integer,
  anterior_os integer,
  vigen_fecha timestamp without time zone NOT NULL,
  observaciones character varying(250),
  pres_ssalud_fecha date,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  discapacitado character varying(1),
  docu_numero character varying(15),
  nacionalidad integer,
  CONSTRAINT pk_afiliado PRIMARY KEY (cuil_titular, inte),
  CONSTRAINT fk_afiliado_seccional FOREIGN KEY (id_seccional)
      REFERENCES seccional (id_seccional) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afiliado OWNER TO postgres;

-- Index: fki_afiliado_seccional

-- DROP INDEX fki_afiliado_seccional;

CREATE INDEX fki_afiliado_seccional
  ON afiliado
  USING btree
  (id_seccional);

-- Table: actividad

-- DROP TABLE actividad;

CREATE TABLE actividad
(
  id_actividad integer NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_actividad PRIMARY KEY (id_actividad)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE actividad OWNER TO postgres;


-- Table: afi_aportes

-- DROP TABLE afi_aportes;

CREATE TABLE afi_aportes
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_aporte integer NOT NULL,
  fecha_ingre date NOT NULL,
  fecha_baja date,
  id_motivo_baja smallint,
  CONSTRAINT pk_afi_aportes PRIMARY KEY (cuil_titular, inte, id_aporte),
  CONSTRAINT fk_afi_aportes_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_aportes_apor FOREIGN KEY (id_aporte)
      REFERENCES aporte (id_aporte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_aportes_motivo_baja FOREIGN KEY (id_motivo_baja)
      REFERENCES motivo_baja (id_motivo_baja) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_aportes OWNER TO postgres;

-- Table: afi_contacto_e

-- DROP TABLE afi_contacto_e;

CREATE TABLE afi_contacto_e
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_contacto_e integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  CONSTRAINT pk_afi_contacto_e PRIMARY KEY (cuil_titular, inte, id_contacto_e),
  CONSTRAINT fk_afi_contacto_e_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_contacto_e_contacto_e FOREIGN KEY (id_contacto_e)
      REFERENCES contacto_e (id_contacto_e) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_contacto_e OWNER TO postgres;

-- Table: afi_documento

-- DROP TABLE afi_documento;

CREATE TABLE afi_documento
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_documento integer NOT NULL,
  fecha_ini timestamp without time zone,
  fecha_vto timestamp without time zone,
  CONSTRAINT pk_afi_documento PRIMARY KEY (cuil_titular, inte, id_documento),
  CONSTRAINT fk_afi_documento_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_documento_documento FOREIGN KEY (id_documento)
      REFERENCES documento (id_documento) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_documento OWNER TO postgres;

-- Table: afi_domicilio

-- DROP TABLE afi_domicilio;

CREATE TABLE afi_domicilio
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  domi_tipo character varying(1) NOT NULL,
  calle character varying(100) NOT NULL,
  piso character varying(5),
  depto character varying(4),
  oficina character varying(10),
  postal_codi character varying(4) NOT NULL,
  barrio character varying(50),
  telefono character varying(100),
  observaciones character varying(250),
  domi_val character varying(1) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  provincia integer,
  localidad integer,
  numero character varying,
  CONSTRAINT pk_afi_domicilio PRIMARY KEY (cuil_titular, inte, vigen_desde),
  CONSTRAINT fk_afi_domicilio_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_domicilio OWNER TO postgres;

-- Table: afi_plan

-- DROP TABLE afi_plan;

CREATE TABLE afi_plan
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_plan character varying(13) NOT NULL,
  id_tarifa integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  CONSTRAINT pk_afi_plan PRIMARY KEY (cuil_titular, inte, id_plan),
  CONSTRAINT fk_afi_plan_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_plan_plan FOREIGN KEY (id_plan)
      REFERENCES plan (id_plan) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_plan OWNER TO postgres;

-- Table: afi_situ_laboral

-- DROP TABLE afi_situ_laboral;

CREATE TABLE afi_situ_laboral
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  cuit character varying(13) NOT NULL,
  sucursal character varying(3) NOT NULL,
  fecha_ingre date NOT NULL,
  fecha_baja date,
  id_puesto integer,
  id_revista integer,
  CONSTRAINT pk_afi_situ_laboral PRIMARY KEY (cuil_titular, inte, cuit, sucursal),
  CONSTRAINT fk_afi_situ_laboral_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_situ_laboral_emp FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_situ_laboral OWNER TO postgres;

-- Table: afi_telefono

-- DROP TABLE afi_telefono;

CREATE TABLE afi_telefono
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_telefono integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  CONSTRAINT pk_afi_telefono PRIMARY KEY (cuil_titular, inte, id_telefono),
  CONSTRAINT fk_afi_telefono_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_telefono_telefono FOREIGN KEY (id_telefono)
      REFERENCES telefono (id_telefono) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_telefono OWNER TO postgres;

-- Table: afi_tercerizadora_servicio

-- DROP TABLE afi_tercerizadora_servicio;

CREATE TABLE afi_tercerizadora_servicio
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_tercerizadora character varying(3) NOT NULL,
  fecha_inicio_pres timestamp without time zone,
  fecha_fin_pres timestamp without time zone,
  fecha_alta timestamp without time zone,
  fecha_baja timestamp without time zone,
  CONSTRAINT pk_afi_tercerizadora PRIMARY KEY (cuil_titular, inte, id_tercerizadora),
  CONSTRAINT fk_afi_aportes_afi FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_afi_tercerizadora_tercerizadora FOREIGN KEY (id_tercerizadora)
      REFERENCES tercerizadora_servicio (id_tercerizadora) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afi_tercerizadora_servicio OWNER TO postgres;

-- Table: afiliado

-- DROP TABLE afiliado;

CREATE TABLE afiliado
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  id_ospim integer,
  id_uoma integer,
  id_amtima integer,
  apellido character varying(100) NOT NULL,
  nombre character varying(100) NOT NULL,
  documento_tipo character varying(4),
  sexo character varying(2) NOT NULL,
  cuil character varying(13),
  naci_fecha date NOT NULL,
  civil_esta character varying(20) NOT NULL,
  parentesco character varying(100),
  ingre_fecha date NOT NULL,
  id_seccional integer,
  anterior_os integer,
  vigen_fecha timestamp without time zone NOT NULL,
  observaciones character varying(250),
  pres_ssalud_fecha date,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  discapacitado character varying(1),
  docu_numero character varying(15),
  nacionalidad integer,
  CONSTRAINT pk_afiliado PRIMARY KEY (cuil_titular, inte),
  CONSTRAINT fk_afiliado_seccional FOREIGN KEY (id_seccional)
      REFERENCES seccional (id_seccional) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE afiliado OWNER TO postgres;

-- Index: fki_afiliado_seccional

-- DROP INDEX fki_afiliado_seccional;

CREATE INDEX fki_afiliado_seccional
  ON afiliado
  USING btree
  (id_seccional);

-- Table: aporte

-- DROP TABLE aporte;

CREATE TABLE aporte
(
  id_aporte integer NOT NULL,
  tipo_aporte character varying(3) NOT NULL,
  plan character varying(3) NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_aporte PRIMARY KEY (id_aporte)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE aporte OWNER TO postgres;

-- Table: contacto_e

-- DROP TABLE contacto_e;

CREATE TABLE contacto_e
(
  id_contacto_e integer NOT NULL,
  tipo_contacto_e character varying(1) NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  contacto character varying(100),
  observaciones character varying(250),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_contacto_e PRIMARY KEY (id_contacto_e)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE contacto_e OWNER TO postgres;

-- Table: documento

-- DROP TABLE documento;

CREATE TABLE documento
(
  id_documento integer NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_documento PRIMARY KEY (id_documento)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE documento OWNER TO postgres;

-- Table: domicilio

-- DROP TABLE domicilio;

CREATE TABLE domicilio
(
  id_domicilio integer NOT NULL DEFAULT nextval('domicilio_id_seq'::regclass),
  domi_tipo character varying(1) NOT NULL,
  calle character varying(100) NOT NULL,
  piso character varying(5),
  depto character varying(4),
  oficina character varying(10),
  postal_codi character varying(4) NOT NULL,
  barrio character varying(50),
  telefono character varying(100),
  observaciones character varying(250),
  domi_val character varying(1) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  provincia integer,
  localidad integer,
  numero character varying,
  CONSTRAINT pk_domicilio PRIMARY KEY (id_domicilio)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE domicilio OWNER TO postgres;

-- Table: emp_actividad

-- DROP TABLE emp_actividad;

CREATE TABLE emp_actividad
(
  cuit character varying(13) NOT NULL,
  sucursal character varying(3) NOT NULL,
  id_actividad integer NOT NULL,
  tipo_actividad character varying(25) NOT NULL,
  fecha_ini timestamp without time zone,
  CONSTRAINT pk_emp_actividad PRIMARY KEY (cuit, sucursal, id_actividad),
  CONSTRAINT fk_emp_actividad_actividad FOREIGN KEY (id_actividad)
      REFERENCES actividad (id_actividad) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE emp_actividad OWNER TO postgres;

-- Table: emp_banco

-- DROP TABLE emp_banco;

CREATE TABLE emp_banco
(
  cuit character varying(13) NOT NULL,
  sucursal character varying(3) NOT NULL,
  id_banco integer NOT NULL,
  sucur_banco integer,
  calle character varying(100) NOT NULL,
  numero smallint,
  numero_cuenta character varying(25) NOT NULL,
  CONSTRAINT pk_emp_banco PRIMARY KEY (cuit, sucursal, id_banco),
  CONSTRAINT fk_emp_banco_emp FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE emp_banco OWNER TO postgres;

-- Table: emp_contacto_e

-- DROP TABLE emp_contacto_e;

CREATE TABLE emp_contacto_e
(
  cuit character varying(13) NOT NULL,
  sucursal character varying(3) NOT NULL,
  id_contacto_e integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  CONSTRAINT pk_emp_contacto_e PRIMARY KEY (cuit, sucursal, id_contacto_e),
  CONSTRAINT fk_emp_contacto_e_contacto_e FOREIGN KEY (id_contacto_e)
      REFERENCES contacto_e (id_contacto_e) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_emp_contacto_e_emp FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE emp_contacto_e OWNER TO postgres;

-- Table: emp_domicilio

-- DROP TABLE emp_domicilio;

CREATE TABLE emp_domicilio
(
  cuit character varying(13) NOT NULL,
  sucursal character varying(3) NOT NULL,
  id_domicilio integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  CONSTRAINT pk_emp_domicilio PRIMARY KEY (cuit, sucursal, id_domicilio),
  CONSTRAINT fk_emp_domicilio_domicilio FOREIGN KEY (id_domicilio)
      REFERENCES domicilio (id_domicilio) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_emp_domicilio_emp FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE emp_domicilio OWNER TO postgres;

-- Table: emp_telefono

-- DROP TABLE emp_telefono;

CREATE TABLE emp_telefono
(
  cuit character varying(13) NOT NULL,
  sucursal character varying(3) NOT NULL,
  id_telefono integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  CONSTRAINT pk_emp_telefono PRIMARY KEY (cuit, sucursal, id_telefono),
  CONSTRAINT fk_afi_telefono_telefono FOREIGN KEY (id_telefono)
      REFERENCES telefono (id_telefono) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_emp_telefono_emp FOREIGN KEY (cuit, sucursal)
      REFERENCES empresa (cuit, sucursal) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE emp_telefono OWNER TO postgres;

-- Table: empresa

-- DROP TABLE empresa;

CREATE TABLE empresa
(
  cuit character varying(13) NOT NULL,
  sucursal character varying(3) NOT NULL,
  razon_soc character varying(200) NOT NULL,
  nombre_fantasia character varying(200) NOT NULL,
  id_ramo_empresa smallint,
  iva character varying(3),
  id_seccional integer,
  contacto character varying(250),
  id_entidad_cam_empresa smallint,
  observaciones character varying(250),
  vigen_fecha timestamp without time zone NOT NULL,
  motivo_baja character varying(3),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  CONSTRAINT pk_empresa PRIMARY KEY (cuit, sucursal),
  CONSTRAINT fk_emp_entidad_cam_empresa FOREIGN KEY (id_entidad_cam_empresa)
      REFERENCES entidad_cam_empresa (id_entidad_cam_empresa) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_emp_ramo_empresa FOREIGN KEY (id_ramo_empresa)
      REFERENCES ramo_empresa (id_ramo_empresa) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE empresa OWNER TO postgres;

-- Table: entidad_cam_empresa

-- DROP TABLE entidad_cam_empresa;

CREATE TABLE entidad_cam_empresa
(
  id_entidad_cam_empresa smallint NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_entidad_cam_empresa PRIMARY KEY (id_entidad_cam_empresa)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE entidad_cam_empresa OWNER TO postgres;

-- Table: localidad

-- DROP TABLE localidad;

CREATE TABLE localidad
(
  id_localidad serial NOT NULL,
  id_provincia integer,
  detalle character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE localidad OWNER TO postgres;


-- Table: motivo_baja

-- DROP TABLE motivo_baja;

CREATE TABLE motivo_baja
(
  id_motivo_baja smallint NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_motivo_baja PRIMARY KEY (id_motivo_baja)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE motivo_baja OWNER TO postgres;


-- Table: nacionalidad

-- DROP TABLE nacionalidad;

CREATE TABLE nacionalidad
(
  id integer NOT NULL DEFAULT nextval('nacionalidades_id_seq'::regclass),
  detalle character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nacionalidad OWNER TO postgres;

-- Table: plan

-- DROP TABLE plan;

CREATE TABLE plan
(
  id_plan character varying(13) NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_plan PRIMARY KEY (id_plan)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE plan OWNER TO postgres;


-- Table: plan_aporte

-- DROP TABLE plan_aporte;

CREATE TABLE plan_aporte
(
  id_plan character varying(13) NOT NULL,
  id_aporte integer NOT NULL,
  CONSTRAINT pk_plan_aporte PRIMARY KEY (id_plan, id_aporte),
  CONSTRAINT fk_plan_aporte_aporte FOREIGN KEY (id_aporte)
      REFERENCES aporte (id_aporte) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_plan_aporte_plan FOREIGN KEY (id_plan)
      REFERENCES plan (id_plan) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE plan_aporte OWNER TO postgres;

-- Table: provincia

-- DROP TABLE provincia;

CREATE TABLE provincia
(
  id_provincia serial NOT NULL,
  detalle character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE provincia OWNER TO postgres;

-- Table: ramo_empresa

-- DROP TABLE ramo_empresa;

CREATE TABLE ramo_empresa
(
  id_ramo_empresa smallint NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_ramo_empresa PRIMARY KEY (id_ramo_empresa)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ramo_empresa OWNER TO postgres;


-- Table: seccional

-- DROP TABLE seccional;

CREATE TABLE seccional
(
  id_seccional integer NOT NULL,
  descripcion character varying(150) NOT NULL,
  cheque_a_la_orden character varying(200),
  tipo character varying(1) NOT NULL,
  id_domicilio integer NOT NULL,
  contacto character varying(250),
  observaciones character varying(250),
  vigen_fecha timestamp without time zone NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  CONSTRAINT pk_seccional PRIMARY KEY (id_seccional)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE seccional OWNER TO postgres;

-- Table: seccional_contacto_e

-- DROP TABLE seccional_contacto_e;

CREATE TABLE seccional_contacto_e
(
  id_seccional integer NOT NULL,
  id_contacto_e integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  CONSTRAINT pk_seccional_contacto_e PRIMARY KEY (id_seccional, id_contacto_e),
  CONSTRAINT fk_seccional_contacto_cont_e FOREIGN KEY (id_contacto_e)
      REFERENCES contacto_e (id_contacto_e) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_seccional_contacto_secc FOREIGN KEY (id_seccional)
      REFERENCES seccional (id_seccional) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE seccional_contacto_e OWNER TO postgres;

-- Table: seccional_telefono

-- DROP TABLE seccional_telefono;

CREATE TABLE seccional_telefono
(
  id_seccional integer NOT NULL,
  id_telefono integer NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  CONSTRAINT pk_seccional_telefono PRIMARY KEY (id_seccional, id_telefono),
  CONSTRAINT fk_seccional_telefono_secc FOREIGN KEY (id_seccional)
      REFERENCES seccional (id_seccional) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_seccional_telefono_tel FOREIGN KEY (id_telefono)
      REFERENCES telefono (id_telefono) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE seccional_telefono OWNER TO postgres;


-- Table: telefono

-- DROP TABLE telefono;

CREATE TABLE telefono
(
  id_telefono integer NOT NULL,
  tipo_tele character varying(1) NOT NULL,
  vigen_desde timestamp without time zone NOT NULL,
  codigo_pais character varying(4),
  codigo_nacional character varying NOT NULL,
  numero character varying(10) NOT NULL,
  extension character varying(8),
  observaciones character varying(250),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_telefonos PRIMARY KEY (id_telefono)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE telefono OWNER TO postgres;


-- Table: tercerizadora_servicio

-- DROP TABLE tercerizadora_servicio;

CREATE TABLE tercerizadora_servicio
(
  id_tercerizadora character varying(3) NOT NULL,
  descripcion character varying(100) NOT NULL,
  observaciones character varying(250) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_tercerizadora_servicio PRIMARY KEY (id_tercerizadora)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tercerizadora_servicio OWNER TO postgres;