create TABLE correo.item_correspondencia
(
  id integer NOT NULL DEFAULT nextval('correo.item_correspondencia_id_seq'::regclass), 
  id_correspondencia integer NOT NULL,
  
  entrada_salida character varying,
  tipo_remitente_destinatario character varying,
    
  edificio character varying,
  sector character varying,
  usuario character varying,
  contenido character varying,
  
  estado character varying,
  --datos afiliado
  cuil_titular character varying,
  inte integer,    
  --datos farmacia
--  codigo_farmacia character varying,
  codigo_farmacia integer,  
  --datos otros
  descripcion_otro character varying,   
  --datos prestador y proveedor
  id_prestador integer,
  cuit_proveedor character varying,
  sucu_proveedor character varying,  
  id_punto_venta smallint,
  compro_tipo character varying,
  compro_nro character varying,
  cuit character varying,
  compro_letra character varying,
  compro_sucu integer,  
  importe numeric, 
  
  fecha_emision timestamp without time zone,
  fecha_vencimiento timestamp without time zone,
  
  id_seccional integer,
  
  alta_fecha timestamp without time zone,
  alta_usr character varying,
  modi_fecha timestamp without time zone,
  modi_usr character varying,
  baja_fecha timestamp without time zone,
  baja_usr character varying,  
  CONSTRAINT pk_item_correspondencia PRIMARY KEY (id)  
)
WITH (
  OIDS=FALSE
);

ALTER TABLE correo.item_correspondencia ADD COLUMN empresa_remite character varying(25);
ALTER TABLE correo.item_correspondencia ADD COLUMN sector_remite character varying(25);
ALTER TABLE correo.item_correspondencia ADD COLUMN usuario_remite character varying(25);
ALTER TABLE correo.item_correspondencia ADD COLUMN alta_sector character varying(10);
ALTER TABLE correo.item_correspondencia ALTER COLUMN contenido TYPE text;