-- MOLINEROS / COMPRAS - Efectos SQL, corte Java del 30/10/2026.
-- ISO-8859-1 sin BOM. Compatible PostgreSQL 9.6.
-- Fuente: catalogo REAL de devmolineros relevado el 30/10/2026.
-- No contiene credenciales, correos ni datos de afiliados.
-- Este paquete NO autoriza operar produccion: entrega para proxima migracion.
-- No ejecutado contra DB por el autor de este archivo.

-- AVANCE BLOQUEADO: NO ES UN INSTALADOR DE RETIRO DE TRIGGERS.
-- El codigo incorpora persistencia/lecturas con la misma Connection y
-- coordinacion Java. Eso no prueba cobertura de todos escritores externos.
-- No se usa un GUC/flag de operador como sustituto de esa evidencia.
-- Los DROP de abajo son comentarios para revision de la proxima migracion;
-- no hay sentencia DROP ejecutable en este archivo.
-- Primero ejecutar el archivo *_efectos_validar.sql y conservar su salida.
-- Las firmas y hashes exactos se preservan en validar y reversion.

BEGIN;
SET LOCAL lock_timeout = '2s';
DO $avance_no_habilitado$
BEGIN
    RAISE EXCEPTION 'Retiro de efectos SQL NO habilitado: faltan pruebas completas de todos escritores y entrega coordinada Java/SQL. Conservar los cinco triggers.';
END;
$avance_no_habilitado$;
ROLLBACK;

-- trg_compras_baja_vinculo_reclamo
-- Pendiente: Todos ingresos de bajaRP deben coordinar RP/cuentas/vinculo en mismaTX; invocaciones externasSQL/manuales no cubiertas no permiten retirar.
-- DROP TRIGGER trg_compras_baja_vinculo_reclamo ON autorizaciones.reclamos_prestacionales;

-- trg_compras_requerimiento_completar_baja
-- Pendiente: Todas rutas estado99 deben persistir baja_fecha/usuario equivalentes en la misma transaccion.
-- DROP TRIGGER trg_compras_requerimiento_completar_baja ON compras.requerimiento;

-- trg_compras_requerimiento_gastos_envio
-- Pendiente: Alta Java con concepto configurado, coexistencia sin doblecargo y TODOS escritores cabecera probados.
-- DROP TRIGGER trg_compras_requerimiento_gastos_envio ON compras.requerimiento;

-- tr_compras_detalle_codigo_prestacion
-- Pendiente: Todos escritores detalle, incl automatico/correccion/cotizacion y externos, deben validar ID/codigo bajo lockcabecera.
-- DROP TRIGGER tr_compras_detalle_codigo_prestacion ON compras.requerimiento_detalle;

-- trg_compras_detalle_calcular_total
-- Pendiente: Todos escritores unitario/cantidad, incl finalizar_cotizacion_requerimiento y correccion, deben recibir total calculado Java; probar total explicito.
-- DROP TRIGGER trg_compras_detalle_calcular_total ON compras.requerimiento_detalle;

-- No retirar funciones validar_* sin demostrar sus callers y reemplazos.
-- Cinco funciones RETURNS trigger sin asociacion no son efectos activos.
-- Las funciones de persistencia se conservan mientras sus contratos sigan
-- vigentes. La configuracion de valores esta en el script separado de root.
-- No cambiar canonicos10415/11452, snapshots, importes ni datos1388.
-- No ejecutar compras_schema.sql ni historicos globales.
