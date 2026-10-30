# Refactor de Compras: entrega Java del 30/10/2026

Esta entrega implementa los caminos de Compras en Java y prepara la configuracion
para una migracion coordinada. No aplica SQL ni despliega Tomcat. El usuario
confirmo que las altas de catalogo y los cambios de produccion se realizaran en
la proxima migracion. Publicar Git no equivale a activar esta entrega.

## Base y trabajo local

Repositorio https://github.com/JerePrograma/molineros.git, ruta unica
C:\wsmolineros\ext, rama main. HEAD inicial
`e0094bd2d721483d5e552ac5863201e2ae683ee7`.
El avance inicial ff-only aborto para preservar `view_reclamo.jsp`.
Tras autorizacion expresa se conservaron y comprometieron los cambios locales
previos en `1528ccdc`; luego se integro origin/main mediante merge
`152cff1e11a9c5b2bbe4fecb1354a71b00b4022b`, incluyendo el minimo
`f51a0aca94adb60c4273c0b511e666688d229885`. No se usaron stash, reset,
clean, rebase, ramas nuevas ni force-push.

## Implementacion

- `ConfiguracionComprasHelper`: lectura estricta por operacion de system_config,
  validacion tipada y fallo cerrado. No hay cache de configuracion estatica,
  en sesion ni entre operaciones. La conexion transaccional del caller se
  conserva en las operaciones autoritativas. Claves duplicadas o errores JDBC
  no se confunden con configuracion vacia.
- `WebKeysCompras` conserva las claves y las fachadas legacy; delega reglas,
  normalizacion e importes a los Helpers de Compras. Sus callers consumen
  roles, estados, limites y valores funcionales configurados. Los codigos de
  protocolo, claves HTTP/sesion y limites estructurales de columnas se conservan.
- `NomencladorCompraBusquedaHelper` interpreta las filas de
  `compras.sector_nomenclador`: especificas sustituyen generales, AND dentro
  de una fila y OR entre filas. No hay whitelist Java de tipos admitidos.
  Se validan todas las filas efectivas y el catalogo real. El selector y el
  alta comparten catalogo y exclusiones configuradas; errores no habilitan todo.
- Cabecera, detalle, correccion, duplicados, cotizacion, estados, anulacion,
  observacion y baja RP validan en Helper y persisten mediante JDBC bajo la
  misma transaccion. Se conservan bloqueos, orden de locks, CAS, identidad,
  auditoria, idempotencia, snapshots y rollback ante escritura parcial.
- Notificaciones resuelven candidatos, contactos, reservas y finalizacion
  en Java; JDBC mantiene locks, ON CONFLICT y CAS. La normalizacion tecnica
  `lower(btrim(contacto))` conserva LC_CTYPE instalado, sin decidir elegibilidad
  en SQL. No se compensa Document Library si el resultado transaccional es incierto.
- Presupuestos, recetas, comparativas y documentos coordinan validacion,
  persistencia y compensacion DL. Un fallo del lote revierte la transaccion;
  la compensacion solo se ejecuta tras rollback confirmado. Las opciones y
  defaults de Comparativa son configurables y se validan antes de persistir.
- Ordenes de Compra reconstruyen el contexto bloqueado dentro de su transaccion,
  verifican adjudicacion, referencias RP/presupuestos y versiones, y actualizan
  el estado mediante CAS. Los bridges legacy pasan por el mismo Helper.
- La propagacion de fecha de comprobante pre-valida todas las filas activas
  con la regla existente de ValidarReclamo antes de mutar cualquier fila. El
  rechazo identifica la fila destino y conserva todas las filas intactas.
  Consentimientos OTR/numero/fecha conservan sus alcances separados.
- La identidad entre fila temporal RP y detalle de Compras se verifica con
  informacion del servidor; los flags HTTP no reclasifican un origen NOMENCLADOR.

No se modifica la estetica ni se agregan frameworks/dependencias. Se conserva
Java 8, source/target 1.5, Liferay 5.2, JSP y JavaScript ES5. Las nuevas fuentes
y los archivos modificados se verifican como ISO-8859-1 sin BOM y sin mojibake.

## Referencias elegidas

- Liquidaciones: `ext-web/docroot/html/portlet/liquidaciones/reportes/reporte_debitos_terciarizadoras.jsp:11-19`,
  interpretacion Java de configuracion DB por operacion.
- Liquidaciones: `ext-impl/src/ar/com/ospim/liquidaciones/ordenespago/action/EditarOrdenesPagoOspimAction.java:163-168,230-234`,
  validacion antes de persistir y lectura de listas configuradas.
- Liquidaciones: `ext-web/docroot/html/portlet/liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp:66-75`,
  presentacion de errores en la vista existente.
- Liquidaciones: `ext-impl/src/ar/com/ospim/liquidaciones/services/ConceptoServiceUtil.java`,
  conexion transaccional, commit y rollback.
- Autorizaciones: `ext-impl/src/ar/com/ospim/autorizaciones/action/CuentaDocumentoHelper.java:90`,
  consulta de datos y decision de duplicidad en Java.
- Compras: Helpers y contratos existentes, contrastados con las definiciones
  realmente instaladas; no con scripts historicos como fuente de verdad.

## Diagnostico y estado de 1388

Se reutilizo el diagnostico previo de esta PC y se completo evidencia faltante
con transacciones REPEATABLE READ READ ONLY y timeouts. Se relevaron las 103
funciones de Compras, cinco funciones externas directas, cinco triggers activos,
constraints, indices, vistas, reglas y escritores RP. Los cuerpos completos y
sus hashes se conservaron fuera del repositorio, sin credenciales.

El estado actual de 446 difiere del diagnostico previo: canonico 2573/tipo 3,
snapshot codigo 9100045. El usuario confirmo una correccion externa autorizada.
No se restauro 11452 ni se reclasificaron canonicos compartidos. 445 conserva
10415/tipo 3. Las pruebas cubren el historico original 446/11452/tipo 10 y el
actual 446/2573/tipo 3 como no-op, sin UPDATE ni normalizacion de snapshots.
No se utilizo 1388 como dato descartable.

## SQL preparado; no ejecutado

1. `20261030_refactor_java_configuracion.sql`: agrega 67 claves nuevas y una
   version. Verifica la funcion instalada trae_system_config, bloquea la tabla,
   rechaza instalaciones parciales/valores diferentes y admite repeticion
   identica. No pisa claves existentes.
2. `20261030_refactor_java_configuracion_reversion.sql`: retira solo esas
   claves si version y valores siguen exactamente iguales. Detiene la reversion
   ante drift. Aplicar unicamente coordinado con el artefacto correspondiente.
3. `20261030_refactor_java_diabetes_15_condicionado.sql`: cambia solamente
   la asociacion especifica de nuevas selecciones de Diabetes. Permanece
   bloqueado hasta completar la identidad aprobada del catalogo 15 y comprobar
   canonicos dedicados activos. El catalogo real relevado aun no contiene 15.
   No crea identidades ficticias, no modifica 10415/11452 ni detalles historicos.
4. `20261030_refactor_java_efectos_validar.sql`,
   `20261030_refactor_java_notificaciones_validar.sql` y
   `20261030_refactor_java_orden_validar.sql`: verificaciones de definiciones
   instaladas y precondiciones para la ventana de migracion.
5. `20261030_refactor_java_efectos_avance_bloqueado.sql`: bloquea expresamente
   el retiro de triggers. Los DROP son solo comentarios. Falta certificar todos
   los escritores externos y su acceso al camino Java; no basta un flag manual.
6. `20261030_refactor_java_efectos_reversion.sql`: permite reponer solo triggers
   ausentes con funciones exactas del diagnostico; rechaza diferencias.

Las cinco funciones de trigger y los cinco triggers instalados permanecen
intactos. El reemplazo Java se prueba preservando su coexistencia con los valores
actuales. La configuracion funcional nueva no debe activarse contradiciendo
triggers de reglas todavia instalados: por ejemplo, cambiar el concepto
automatico mientras el trigger anterior sigue insertando otro concepto.
La ventana coordinada debe certificar los escritores y retirar esos efectos
antes de habilitar valores incompatibles. El avance permanece bloqueado
mientras esa cobertura no se demuestre; las pruebas aisladas no certifican
la convivencia de cualquier configuracion con triggers antiguos.
Antes de retirar efectos SQL deben certificarse los escritores externos,
las firmas legacy aun accesibles y el orden coordinado de activacion.

La reversion del Java viejo requiere verificar si hubo cargas nuevas tipo 15.
Nunca borrar esas cargas para hacer que una reversion pase. Conservar el
artefacto compatible o preparar una correccion hacia adelante si el Java viejo
no puede representarlas.

## Validacion y limites

Se ejecutaron los tres targets `compile` de la accion configurada Build Java
focalizado, con JDK 1.8.0_251 y Ant 1.10.17; no se ejecuto ant clean.
Los runners persistentes de `test/compras` registran hora, HEAD, contexto,
salida de javac y resultado Java. Prueban fuentes reales y simulan solamente
las fronteras JDBC/DL/HTTP necesarias para no escribir produccion ni enviar
correos reales. Los casos de rollback/SMTP/DL inyectan fallos deliberados y
comprueban que no se informa exito falso.

Los logs finales, conteos, SHA256 por archivo y clase, matriz SQL -> Java,
comandos/codigos de salida y SHA publicado se entregan en el informe externo
consolidado de esta ejecucion. Las advertencias del compilador por source 1.5,
APIs deprecated y logging legacy se preservan en esos logs.

PRUEBA UI MANUAL PENDIENTE. El acceso de automatizacion a la sesion existente
fallo y no se ejecutaron clicks, requests ni persistencia en una navegacion
real. Tampoco se afirmaron smoke tests de produccion ni despliegue. El Tomcat
Windows local ya estaba escuchando en 8080; no se inicio una segunda instancia,
no se detuvo ni se cambio su artefacto. No debe confundirse con el servidor
Linux/Tomcat 8.5.20 de produccion.

La siguiente migracion requiere el catalogo aprobado de Diabetes, certificacion
de escritores externos, acceso/procedimiento real de produccion, ventana
coordinada, artefacto empaquetado y smoke tests reales. La aplicacion no debe
atender con Java y configuracion SQL incompatibles. Este documento describe
codigo implementado y scripts preparados; no acredita una migracion aplicada.
