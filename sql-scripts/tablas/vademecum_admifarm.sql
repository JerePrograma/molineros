-- Vademecum Admifarm: estructura suministrada para vigentes e historicos.
-- No elimina ni modifica registros existentes. Aplicar en la base del portal.
-- No cambia propietarios ni permisos de las tablas existentes.
-- Las tablas nuevas requieren los permisos del usuario de conexion del portal.
BEGIN;

CREATE TABLE IF NOT EXISTS public.vademecum_admifarm_ampliado (
    registro numeric,
    nombre character varying,
    presentacion character varying,
    accion character varying,
    monodroga character varying,
    laboratorio character varying,
    tipo_venta character varying
);

CREATE TABLE IF NOT EXISTS public.vademecum_admifarm_ampliado_historico (
    alta_fecha timestamp without time zone,
    registro numeric,
    nombre character varying,
    presentacion character varying,
    accion character varying,
    monodroga character varying,
    laboratorio character varying,
    tipo_venta character varying
);

CREATE TABLE IF NOT EXISTS public.vademecum_admifarm_pmo (
    registro numeric,
    nombre character varying,
    monodroga character varying,
    presentacion character varying,
    accion character varying,
    laboratorio character varying
);

CREATE TABLE IF NOT EXISTS public.vademecum_admifarm_pmo_historico (
    alta_fecha timestamp without time zone,
    registro numeric,
    nombre character varying,
    monodroga character varying,
    presentacion character varying,
    accion character varying,
    laboratorio character varying
);

COMMIT;
